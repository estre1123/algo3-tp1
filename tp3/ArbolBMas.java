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
		//creamos un arr que basicamente va a tener todos los datos de la pag
		byte[] datos = new byte[P];
		archivo.seek((long) pagina * P);
		//basicamente copia los datos de la pagina en el arr de bytes
		archivo.readFully(datos);
		cantPagLeidas++;

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


	@Override
	public void close() throws IOException {
		try {
			escribirHeader();
		} finally {
			archivo.close();
		}
	}
}
