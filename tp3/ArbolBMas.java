import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
/*
P es el tamaño de la página en bytes.
M es el número maximo de hijos por nodo interno.
L es la cantidad maxima de elementos por nodo nodo.
header es 12 bytes, son  datos comunes asi que se resta a P y se divide entre 16 que serian el resto de datos de un nodo nodo para calcular L = (P - 12) / 16\

Para calcular M :
i es cantidad de claves por nodo interno, entonces i = M - 1
Cada nodo interno tiene 12 de header y 4 bytes por cada hijo, entonces el tamaño de un nodo interno es
12 + 4M + 8(M - 1) <= P
12 + 4M + 8M - 8 <= P
4 + 12M <= P
M = (P - 4) / 12
*/


//Dato curioso que pille haciendo: leer y eso es como el acceso a un arreglo en forma de puntero

public class ArbolBMas implements AutoCloseable{
	//header/cabecera
	private static final int MAGIC = 0x41454433;
	private static final int HEADER_SIZE = 12;
	private static final int VERSION = 1;

	private int P; //tamano pagina en bytes
	private int M; //maximo de hijos por nodo
	private int L; //maximo de elementos por nodo nodo

	private int raiz; //pagina raiz
	private int numPaginas; //numero de paginas
	private int niveles;
	private long cantClaves;

	private int cantPagEscritas;
	private int cantPagLeidas;

	private RandomAccessFile archivo;

	//Representacion de un nodo en memoria
	private static class Nodo {
		int pagina;       // nro de pagina en el archivo
		int tipo;         // 0  = nodo
		int n;            // claves
		int siguiente;    // sgte nodo.

		long[] claves;
		long[] posiciones; // Solo para nodos.
		int[] hijos;       // Solo para internos.

		private Nodo(int pagina, int tipo, int M, int L) {
			this.pagina = pagina;
			this.tipo = tipo;
			this.n = 0;
			this.siguiente = -1;
			// Se da espacio extra para el desborde temporal en memoria, antes splitear
			if (tipo == 0) { // nodo
				// ese +1 es para podes desbordar el nodo y hacer split, no se puede hacer overflow en un nodo, por eso se hace +1
				this.claves = new long[L + 1];
				this.posiciones = new long[L + 1];
			} else { // interno
				this.claves = new long[M];
				this.hijos = new int[M + 1];
			}
		}
	}

    private ArbolBMas(RandomAccessFile archivo, int tamPagina){
        this.archivo = archivo;
        this.P = tamPagina;
        this.L = calcularL(tamPagina);
        this.M = calcularM(tamPagina);
    }

	private static int calcularL(int p) {
		return (p - HEADER_SIZE) / 16;
	}

	private static int calcularM(int p) {
		return (p - 4) / 12;
	}

	public long paginasLeidas() {
    	return cantPagLeidas;
	}
	public void reiniciarContadores() {
    	cantPagLeidas = 0;
    	cantPagEscritas = 0;
	}

	public static ArbolBMas crear(String ruta, int tamPagina) throws IOException, TamPaginaInvalidoException {
		if (tamPagina < 40 || calcularL(tamPagina) < 2 || calcularM(tamPagina) < 3) {
			throw new TamPaginaInvalidoException("El tamaño ingresado es invalido.");
		}
		//crear un archivo binario para el arbol b+ con el tamano de pagina especificado
		RandomAccessFile archivo = new RandomAccessFile(ruta, "rw");
		try {
			//ressetear el archivo
			archivo.setLength(0);
			ArbolBMas arbol = new ArbolBMas(archivo, tamPagina);
			arbol.raiz = 1;
			arbol.numPaginas = 2;
			arbol.niveles = 1;
			arbol.cantClaves = 0;
			arbol.cantPagEscritas = 0;
			arbol.cantPagLeidas = 0;

			arbol.escribirHeader();
			arbol.escribirRaizVacia();
			return arbol;

		} catch (IOException e) { //si falla se tira a la basurota
			archivo.close();
			throw e;
		}
	}

	private void escribirHeader() throws IOException {
		//crear un buffer de bytes para escribir el header, el buffer es como un arreglo de bytes que se puede escribir en el archivo
		ByteBuffer buffer = ByteBuffer.allocate(P);
		buffer.putInt(MAGIC);
		buffer.putInt(VERSION);
		buffer.putInt(P);
		buffer.putInt(M);
		buffer.putInt(L);
		buffer.putInt(raiz);
		buffer.putInt(numPaginas);
		buffer.putInt(niveles);
		buffer.putLong(cantClaves);

		//escribir el buffer en el archivo
		archivo.seek(0);
		archivo.write(buffer.array());
	}
	//direccion de una pagina es: (long) numeroPagina * P
	private void escribirRaizVacia() throws IOException {
    // 1. Crear un ByteBuffer de P bytes.
	ByteBuffer buffer = ByteBuffer.allocate(P);
	buffer.putInt(0);
	buffer.putInt(0);
	buffer.putInt(-1);
	// Mover el cursor del archivo al comienzo de la pagina 1
	archivo.seek(P);
	archivo.write(buffer.array());
    // 4. Escribir el buffer completo.
	}
	//IMPORTANTE PARA MI: cada lectura avanza el cursor del archivo, por lo que no es necesario moverlo manualmente
	public static ArbolBMas abrir(String ruta) throws IOException {

		//verificar que el archivo existe
		if (!new java.io.File(ruta).isFile()) {
    		throw new FormatoInvalidoException("No existe el archivo del indice.");
		}
		//abrir el archivo binario para el arbol b+
		RandomAccessFile archivo = new RandomAccessFile(ruta, "rw");

		try {
			//verificar que el archivo tiene al menos 40 bytes (del header)
			if (archivo.length() < 40) {
				throw new FormatoInvalidoException("El archivo no tiene una cabezera valida");
			}
			int magic = archivo.readInt();
			int version = archivo.readInt();

			//verificar que el archivo tiene el formato correcto, si no, lanzar una excepcion
			if (magic != MAGIC || version != VERSION) {
				throw new FormatoInvalidoException("El formato del archivo es invalido.");
			}

			//verificar que el tamano de pagina es valido
			int newP = archivo.readInt();
			if (newP < 40 || calcularL(newP) < 2 || calcularM(newP) < 3) {
				throw new FormatoInvalidoException("El tamaño de pagina es invalido.");
			}
			int newM = archivo.readInt();
			int newL = archivo.readInt();

			//Verificar que M y L correspondan a la P leida
			if (newM != calcularM(newP) || newL != calcularL(newP)) {
				throw new FormatoInvalidoException("M y L no corresponden a P.");
			}

			int newRaiz = archivo.readInt();
			int newNumPaginas = archivo.readInt();
			int newNiveles = archivo.readInt();
			long newCantClaves = archivo.readLong();

			//revisar longitud del archivo
			if (archivo.length() != (long) newNumPaginas * newP) {
				throw new FormatoInvalidoException("El tamano del archivo no corresponde a la cantidad de paginas y el tamano de pagina leido");
			}
			ArbolBMas arbol = new ArbolBMas(archivo, newP);
			arbol.raiz = newRaiz;
			arbol.numPaginas = newNumPaginas;
			arbol.niveles = newNiveles;
			arbol.cantClaves = newCantClaves;
			return arbol;

		} catch (IOException e) {
			archivo.close();
			throw e;
		}
	}
	private Nodo leerNodo(int pagina) throws IOException {
		return leerNodo(pagina, true);
	}
	private Nodo leerNodo(int pagina, boolean contar) throws IOException {
		//creamos un arr que basicamente va a tener todos los datos de la pag
		byte[] datos = new byte[P];
		archivo.seek((long) pagina * P);
		//basicamente copia los datos de la pagina en el arr de bytes
		archivo.readFully(datos);
		if (contar) {
        	cantPagLeidas++;
    	}
		//creamos un buffer de bytes para leer los datos del arr de bytes, el buffer es como un arreglo de bytes que se puede leer
		ByteBuffer buffer = ByteBuffer.wrap(datos);

		// Leer el tipo de nodo y el numero de claves -- datos comunes a todos los nodos
		int tipo = buffer.getInt();
		int n = buffer.getInt();
		int sgte = buffer.getInt();

		Nodo nodo = new Nodo(pagina, tipo, M, L);
		nodo.n = n;
		nodo.siguiente = sgte;

		if (tipo == 0) { // nodo
			for (int i = 0; i < n; i++) {
				nodo.claves[i] = buffer.getLong();
				nodo.posiciones[i] = buffer.getLong();
			}
		} else { // interno
			for (int i = 0; i <= n; i++) {
				nodo.hijos[i] = buffer.getInt();
			}

			buffer.position(12 + 4 * M);

			for (int i = 0; i < n; i++) {
				nodo.claves[i] = buffer.getLong();
			}
		}
		return nodo;
	}
	//casi misma logica que el de arriba xd
	private void escribirNodo(Nodo nodo) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(P);
		buffer.putInt(nodo.tipo);
		buffer.putInt(nodo.n);
		buffer.putInt(nodo.siguiente);
		if (nodo.tipo == 0) { // nodo
			for (int i = 0; i < nodo.n; i++) {
				buffer.putLong(nodo.claves[i]);
				buffer.putLong(nodo.posiciones[i]);
			}
		} else { // interno
			for (int i = 0; i <= nodo.n; i++) {
				buffer.putInt(nodo.hijos[i]);
			}
			buffer.position(12 + 4 * M);
			for (int i = 0; i < nodo.n; i++) {
				buffer.putLong(nodo.claves[i]);
			}
		}
		archivo.seek((long) nodo.pagina * P);
		archivo.write(buffer.array());
		cantPagEscritas++;
	}

	private int indiceHijo(Nodo nodo, long clave) {
		//classic binary search, pero en vez de buscar un valor, busca el indice del hijo donde estaria la clave
		int izq = 0;
		int der = nodo.n;
		while (izq < der) {
			int medio = izq + (der - izq) / 2;
			if (clave < nodo.claves[medio]) {
				der = medio;
			} else {
				izq = medio + 1;
			}
		}
		return izq;
	}

	public long buscar(long clave) throws IOException {

		Nodo nodo = leerNodo(raiz);
		while (nodo.tipo != 0) { // mientras no sea nodo
			int i = indiceHijo(nodo, clave);
			nodo = leerNodo(nodo.hijos[i]);
		}

		//aplicamos bynary search para buscar la clave en el nodo nodo, si no se encuentra, devuelve -1
		int izq = 0;
		int der = nodo.n - 1;
		while (izq <= der) {
			int medio = izq + (der - izq) / 2;
			if (clave < nodo.claves[medio]) {
				der = medio - 1;
			} else if (clave > nodo.claves[medio]) {
				izq = medio + 1;
			} else {
				return nodo.posiciones[medio];
			}
		}
		return -1;
	}

	//top funciones mas terroristas
	public boolean insertar(long clave, long posicion) throws IOException {
		//Bajamos hasta la nodo correspondiente, guardando los nodos padres y los indices de los hijos en arreglos para poder splitear si se necesita

		Nodo[] padres = new Nodo[niveles];
		int[] indices = new int[niveles];
		int profundidad = 0;

		Nodo nodo = leerNodo(raiz);
		while (nodo.tipo != 0) { //bajamos a la nodo correspondiente
			int i = indiceHijo(nodo, clave);
			padres[profundidad] = nodo;
			indices[profundidad] = i;
			profundidad++;
			nodo = leerNodo(nodo.hijos[i]);
		}
		//al salir, ya estamos donde tenemos q insertar

		//Los pasos a seguir son:
		int pos = posicionDeInsercion(nodo, clave);
		if (pos < nodo.n && nodo.claves[pos] == clave) {
			// La clave ya existe, no se inserta
			return false;
		}
		//caso milagroso: No hay que splitear nada y lo insertamos al toque en memorria
		for (int i = nodo.n; i > pos; i--) {
			nodo.claves[i] = nodo.claves[i - 1];
			nodo.posiciones[i] = nodo.posiciones[i - 1];
		}
		nodo.claves[pos] = clave;
		nodo.posiciones[pos] = posicion;
		nodo.n++;

		// Si esto no incumple la cantidad maxima de elementos por nodo, escribimos el nodo y el header y listo
		if (nodo.n <= L) {
			escribirNodo(nodo);
			cantClaves++;
			escribirHeader();

			return true;

		}
		int nuevaPag = numPaginas;
		numPaginas++;
		Nodo nuevoNodo = new Nodo(nuevaPag, 0, M, L);
		int total = nodo.n;
		int cantIzq = (total + 1) / 2; // cantidad de elementos que se quedan en el nodo original
		//copiar
		for (int i = cantIzq; i < total; i++) {
			nuevoNodo.claves[i - cantIzq] = nodo.claves[i];
			nuevoNodo.posiciones[i - cantIzq] = nodo.posiciones[i];
		}
		//reasignar el nodo original para que tenga solo la mitad de los elementos
		nodo.n = cantIzq;
		nuevoNodo.n = total - cantIzq;

		nuevoNodo.siguiente = nodo.siguiente;
		nodo.siguiente = nuevoNodo.pagina;

		long separador = nuevoNodo.claves[0];

		escribirNodo(nodo);
		escribirNodo(nuevoNodo);

		//ahora hay que insertar la clave en el nodo correspondiente, que puede ser el original o el nuevo
		while (profundidad > 0) {
			profundidad--;
			Nodo padre = padres[profundidad];
			int indice = indices[profundidad];

			for (int i = padre.n; i > indice; i--) {
				padre.claves[i] = padre.claves[i - 1];
				padre.hijos[i + 1] = padre.hijos[i];
			}
			padre.claves[indice] = separador;
			padre.hijos[indice + 1] = nuevoNodo.pagina;
			padre.n++;
			//si el padre no se desborda, escribimos y listo
			if (padre.n < M) {
				// Insertar la clave y el hijo en el nodo padre
				escribirNodo(padre);
				cantClaves++;
				escribirHeader();
				return true;
			} else {
				// Split del nodo padre
				int nuevaPagPadre = numPaginas;
				numPaginas++;
				Nodo nuevoPadre = new Nodo(nuevaPagPadre, 1, M, L);

				int totalPadre = padre.n;
				// Cantidad de elementos que se quedan en el nodo padre original
				int cantIzqPadre = (totalPadre + 2) / 2;
				int iSeparador = cantIzqPadre - 1;

				//este nodo quedaba fuera xd
				nuevoPadre.hijos[0] = padre.hijos[cantIzqPadre];

				for (int i = cantIzqPadre; i < totalPadre; i++) {
					nuevoPadre.claves[i - cantIzqPadre] = padre.claves[i];
					nuevoPadre.hijos[i - cantIzqPadre + 1] = padre.hijos[i + 1];
				}

				nuevoPadre.n = totalPadre - iSeparador - 1;

				separador = padre.claves[iSeparador];

				padre.n = iSeparador;

				escribirNodo(padre);
				escribirNodo(nuevoPadre);

				nuevoNodo = nuevoPadre;
			}
		}
		// Si llegamos hasta aca,se desbordo la raiz
		int nuevaPagRaiz = numPaginas;
		numPaginas++;

		Nodo nuevaRaiz = new Nodo(nuevaPagRaiz, 1, M, L);
		nuevaRaiz.claves[0] = separador;
		nuevaRaiz.hijos[0] = raiz;
		nuevaRaiz.hijos[1] = nuevoNodo.pagina;

		nuevaRaiz.n = 1;
		raiz = nuevaRaiz.pagina;
		niveles++;

		escribirNodo(nuevaRaiz);
		cantClaves++;
		escribirHeader();
		return true;


	}

	private int posicionDeInsercion(Nodo nodo, long clave) {
		int izq = 0;
		int der = nodo.n;
		while (izq < der) {
			int medio = izq + (der - izq) / 2;
			if ( nodo.claves[medio] < clave) {
				izq = medio + 1;
			} else {
				der = medio;
			}
		}
		return izq;
	}

	public int buscarRango(long desde, long hasta, ConsumidorEntrada c)throws IOException, RangoInvalidoException {
		if (desde > hasta) {
        	throw new RangoInvalidoException("Rango invalido");
    	}
		int cantidad=0;
		Nodo nodo=leerNodo(raiz);
		//ir desde la raiz hasta la hoja
		while (nodo.tipo!=0) {
			int i=indiceHijo(nodo, desde);
			nodo=leerNodo(nodo.hijos[i]);
    	}
		int i;
		// Recorrer las hojas mientras no se termine el rango
		while (nodo!=null) {
			for (i=0; i< nodo.n ; i++) {
				if (nodo.claves[i] > hasta) {
					return cantidad;
				} else if (nodo.claves[i]>=desde) {
					c.aceptar(nodo.claves[i], nodo.posiciones[i]);
					cantidad++;
				}
			}
			//parar si el ultimo elemento de la hoja es mayor o igual al limite superior del rango
			if (nodo.n > 0 && nodo.claves[nodo.n - 1] >= hasta) {
    			return cantidad;
			}

			if (nodo.siguiente==-1) {
				nodo=null;
			} else {
				nodo=leerNodo(nodo.siguiente);
			}

		}
		return cantidad;
	}
	public int niveles(){
		return this.niveles;
	}
	public int raiz(){
		return this.raiz;
	}
	public int cantidadPaginas(){
		return this.numPaginas;
	}
	public long cantidadClavez(){
		return this.cantClaves;
	}
	public int tamPagina(){
		return this.P;
	}
	public int ordenInterno(){
		return this.M;
	}
	public int capacidadHoja(){
		return this.L;
	}
	public long paginasEscritas(){
		return cantPagEscritas;
	}
	public String volcarPagina(int nro) throws IOException {
		Nodo nodo=leerNodo(nro);
		String resultado="#"+nro;
		int i;
		if (nodo.tipo == 0) {
			// Hoja
			resultado+=" H n="+nodo.n;
			resultado+=" sig="+nodo.siguiente;
			resultado+=" [";
			for (i=0; i<nodo.n; i++) {
				resultado+=" ";
				resultado+=nodo.claves[i]+":"+nodo.posiciones[i];
			}
			resultado+="]";
		} else {
			// Interno
			resultado+=" I n="+nodo.n;
			resultado+=" [";
			for (i=0; i < nodo.n; i++) {
				resultado+="#"+nodo.hijos[i];
				resultado+=" "+nodo.claves[i]+" ";
			}
			resultado += "#" + nodo.hijos[nodo.n] + "]";
			}

		return resultado;
	}

	public String volcarArbol() throws IOException {
		String resultado = "";
		int[] cola=new int[numPaginas];
		int frente=0;
		int fin=0;
		int nivel=1;
		int i,j;
		cola[fin]=raiz;
		fin++;
		while (frente<fin) {
			int cantidad=fin-frente;
			resultado+=nivel+": ";
			for (i=0; i<cantidad; i++) {
				int pagina=cola[frente];
				frente++;
				Nodo nodo=leerNodo(pagina, false);
				if (i > 0) {
					resultado += " | ";
				}
				resultado+="#"+pagina;
				if (nodo.tipo==0) {
					resultado+="[";
					for (j=0; j<nodo.n; j++) {
						if (j>0) {
							resultado+=",";
						}
						resultado+=nodo.claves[j];
					}
					resultado+="]";
				} else {
					resultado += "(";
					for (j=0; j<nodo.n; j++) {
						if (j>0) {
							resultado+=",";
						}
						resultado+=nodo.claves[j];
					}
					resultado+=")";
					for (j=0; j<=nodo.n; j++) {
						cola[fin]=nodo.hijos[j];
						fin++;
					}
				}
			}
			if (frente<fin) {
				resultado += "\n";
			}
			nivel++;
		}
		return resultado;
	}
	public class EstadisticasBMas {

		public int hojas;
		public int internos;

		public double ocupacionHojas;
		public double ocupacionInternos;
	}
	
	public EstadisticasBMas estadisticas() throws IOException {
		EstadisticasBMas e = new EstadisticasBMas();
		int sumaHojas = 0;
		int sumaInternos = 0;
		int[] cola = new int[numPaginas];
		int frente = 0;
		int fin = 0;
		cola[fin] = raiz;
		fin++;
		while (frente < fin) {
			int pagina = cola[frente];
			frente++;
			Nodo nodo = leerNodo(pagina, false);
			if (nodo.tipo == 0) {
				e.hojas++;
				sumaHojas += nodo.n;
			} else {
				e.internos++;
				sumaInternos += nodo.n + 1;
				for (int i = 0; i <= nodo.n; i++) {
					cola[fin] = nodo.hijos[i];
					fin++;
				}
			}
		}
		if (e.hojas > 0) {
			e.ocupacionHojas =(double) sumaHojas / (e.hojas * L);
		}
		if (e.internos > 0) {
			e.ocupacionInternos =(double) sumaInternos / (e.internos * M);
		}
		return e;
	}
	private static class EstadoVerificacion {
		boolean[] visitadas;
		int paginasVisitadas;
		long clavesContadas;
		int ultimaHoja;

		EstadoVerificacion(int numPaginas) {
			visitadas = new boolean[numPaginas];
			paginasVisitadas = 0;
			clavesContadas = 0;
			ultimaHoja = -1;
		}
	}

	public String verificarInvariantes() throws IOException {
		if (numPaginas < 2) {
			return "Cantidad de paginas invalida";
		}
		if (archivo.length() != (long) numPaginas * P) {
			return "Longitud del archivo incorrecta";
		}
		if (raiz < 1 || raiz >= numPaginas) {
			return "Pagina raiz invalida";
		}
		if (niveles < 1) {
			return "Cantidad de niveles invalida";
		}
		EstadoVerificacion estado =new EstadoVerificacion(numPaginas);
		String error = verificarNodo(raiz, 1, null, null, estado);
		if (!error.equals("OK")) {
			return error;
		}
		if (estado.ultimaHoja != -1) {
			Nodo ultima = leerNodo(estado.ultimaHoja, false);
			if (ultima.siguiente != -1) {
				return "La ultima hoja no termina en -1";
			}
		}
		if (estado.clavesContadas != cantClaves) {
			return "Cantidad de claves diferente de la cabecera";
		}
		for (int i = 1; i < numPaginas; i++) {
			if (!estado.visitadas[i]) {
				return "Pagina " + i + " no alcanzable";
			}
		}
		return "OK";
	}
	private String verificarNodo(
			int pagina,
			int nivelActual,
			Long minimo,
			Long maximo,
			EstadoVerificacion estado) throws IOException {
		if (pagina < 1 || pagina >= numPaginas) {
			return "Referencia a pagina invalida: " + pagina;
		}
		if (estado.visitadas[pagina]) {
			return "Pagina " + pagina + " visitada mas de una vez";
		}
		estado.visitadas[pagina] = true;
		estado.paginasVisitadas++;
		archivo.seek((long) pagina * P);
		int tipo = archivo.readInt();
		int n = archivo.readInt();
		if (tipo != 0 && tipo != 1) {
			return "Tipo invalido en pagina " + pagina;
		}
		if (tipo == 0) {
			if (n < 0 || n > L) {
				return "Ocupacion invalida en hoja " + pagina;
			}
		} else {
			if (n < 0 || n >= M) {
				return "Ocupacion invalida en interno " + pagina;
			}
		}
		Nodo nodo = leerNodo(pagina, false);
		if (pagina != raiz) {
			if (tipo == 0) {
				if (n < (L + 1) / 2) {
					return "Hoja " + pagina + " con pocas claves";
				}
			} else {
				if (n + 1 < (M + 1) / 2) {
					return "Interno " + pagina + " con pocos hijos";
				}
			}
		} else if (tipo == 1 && n < 1) {
			return "Raiz interna sin suficientes hijos";
		}
		//los claves tienen que ser mayor si o si
		for (int i = 1; i < n; i++) {
			if (nodo.claves[i - 1] >= nodo.claves[i]) {
				return "Claves desordenadas en pagina " + pagina;
			}
		}
		for (int i = 0; i < n; i++) {
			if (minimo != null && nodo.claves[i] < minimo) {
				return "Clave menor que la cota en pagina " + pagina;
			}
			if (maximo != null && nodo.claves[i] >= maximo) {
				return "Clave mayor o igual que la cota en pagina " + pagina;
			}
		}
		if (tipo == 0) {
			if (nivelActual != niveles) {
				return "Hoja " + pagina + " en nivel incorrecto";
			}
			if (estado.ultimaHoja != -1) {
				Nodo anterior =leerNodo(estado.ultimaHoja, false);
				if (anterior.siguiente != pagina) {
					return "Cadena de hojas incorrecta entre "
							+ estado.ultimaHoja + " y " + pagina;
				}
			}
			estado.ultimaHoja = pagina;
			estado.clavesContadas += n;
		} else {
			if (nivelActual >= niveles) {
				return "Interno " + pagina + " en nivel incorrecto";
			}
			// Recorrer los hijos de izquierda a derecha
			for (int i = 0; i <= n; i++) {
				Long cotaInferior = minimo;
				Long cotaSuperior = maximo;
				if (i > 0) {
					cotaInferior = nodo.claves[i - 1];
				}
				if (i < n) {
					cotaSuperior = nodo.claves[i];
				}
				String error = verificarNodo(
						nodo.hijos[i],
						nivelActual + 1,
						cotaInferior,
						cotaSuperior,
						estado);
				if (!error.equals("OK")) {
					return error;
				}
			}
		}
		return "OK";
	}
	public int[] caminoBusqueda(long clave) throws IOException {
		int[] camino = new int[niveles];
		Nodo nodo = leerNodo(raiz);
		int i = 0;
		while (nodo.tipo != 0) {
			camino[i] = nodo.pagina;
			i++;
			int indice = indiceHijo(nodo, clave);
			nodo = leerNodo(nodo.hijos[indice]);
		}
		camino[i] = nodo.pagina;	
		return camino;
	}
	@Override
	public void close() throws IOException {
		try {
			escribirHeader();
		} finally {
			archivo.close();
		}
	}

}
