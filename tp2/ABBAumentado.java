//K extiende comparable porque necesitamos que pueda ser comparado
//FALTAN ELIMINAR, TAMANOSCONSISTENTES, EL ITERATOR, Y EL TOSTRING, EL RESTO VERIFICAR
public class ABBAumentado <K extends Comparable<? super K>, V> {
	private Nodo<K, V> raiz;
	private long visitas;

	//clase estatica del nodo, basicamente es la estructura de datos que vamos a usar para almacenar la informacion, cada nodo tiene un hijo izquierdo y derecho, ademas de un padre, y un tamaño que es el tamaño del subarbol mas el mismo
	//Es estatica para que no se puedan crear nodos fuera de la clase ABBAumentado, y para que no se pueda acceder a los miembros de la clase ABBAumentado desde la clase Nodo
	public static class Nodo <K, V>{
		//Info del nodo, K es de key, la clave usada para comparar, la V es de value, es la info asociada al nodo
		K clave;
		V valor;
		//Padre, el pdf no menciona asi que de momento no se usa
		Nodo<K, V> padre;
		//Hijos
		Nodo<K, V> izq;
		Nodo<K, V> der;

		//Tamaño del subarbol mas el mismo
		int tamano;

		public Nodo(K clave, V valor) {
			this.clave = clave;
			this.valor = valor;
			this.izq = null;
			this.der = null;
			this.padre = null;
			//el tamano indica cuantos nodos hay en el subarbol que tiene como raiz este nodo, y como es un nodo hoja, el inicial es 1
			this.tamano = 1;
		}
		//getters mencionados
		public K getClave() {
			return clave;
		}
		public V getValor() {
			return valor;
		}
		public int getTamano() {
			return tamano;
		}
	}

	public ABBAumentado() {
		this.raiz = null;
		this.visitas = 0;
	}
	//Metodos de agregar, se compone de uno sencillo (para el user), y de otro recursivo, que es el que realmente hace el trabajo
	public void agregar(K clave, V valor) throws ClaveNulaException {
		if (clave == null) {
			throw new ClaveNulaException("La clave no puede ser nula.");
		}
		this.raiz = agregarRec(this.raiz, clave, valor);
	}

	public Nodo<K, V> agregarRec(Nodo<K, V> nodo, K clave, V valor) {
		if (nodo == null) {
			return new Nodo<>(clave, valor); //si estamos en una hoja VACIA, creamos un nodo y lo retornamos
		}
		this.visitas++; //incrementamos las visitas, ya que estamos bajando en el arbol
		//sino, comparamos...
		if (clave.compareTo(nodo.clave) < 0) { // si la clave es menor, bajamos a la izquierda
			nodo.izq = agregarRec(nodo.izq, clave, valor);
		} else if (clave.compareTo(nodo.clave) > 0) { // si es mayor, bajamos a la derecha
			nodo.der = agregarRec(nodo.der, clave, valor);
		} else {
			//si la clave ya existe, actualizamos el valor, no se permiten claves duplicadas
			nodo.valor = valor;
		}
		nodo.tamano = 1 + obtenerTamano(nodo.izq) + obtenerTamano(nodo.der); //actualizamos el tamaño del nodo
		return nodo;

	}

	//falta eliminar, pero eso despues hago


	//metodo de obtener la clave, se compone de uno sencillo (para el user), y de otro recursivo, que es el que realmente hace el trabajo, lo mismisimo que el metodo de agregar
	public V obtener(K clave) throws ClaveNulaException {
		if (clave == null) {
			throw new ClaveNulaException("La clave no puede ser nula.");
		}
		return obtenerRec(this.raiz, clave);
	}


	private V obtenerRec(Nodo<K, V> nodo, K clave) {
		if (nodo == null) {
			return null;
		}
		this.visitas++;
		if (clave.compareTo(nodo.clave) < 0) {
			return obtenerRec(nodo.izq, clave);
		} else if (clave.compareTo(nodo.clave) > 0) {
			return obtenerRec(nodo.der, clave);
		} else {
			return nodo.valor;
		}
	}

	private int obtenerTamano(Nodo<K, V> nodo) {
		if (nodo == null) {
			return 0;
		}
		return nodo.tamano;
	}

	public boolean contiene(K clave) throws ClaveNulaException {
		if (clave == null) {
			throw new ClaveNulaException("La clave no puede ser nula.");
		}
		return contieneRec(this.raiz, clave);
	}

	private boolean contieneRec(Nodo<K, V> nodo, K clave) {
		if (nodo == null) {
			return false;
		}
		//aumentamos las visitas cada vez que bajamos
		this.visitas++;
		if (clave.compareTo(nodo.clave) < 0) {
			return contieneRec(nodo.izq, clave);
		} else if (clave.compareTo(nodo.clave) > 0) {
			return contieneRec(nodo.der, clave);
		} else {
			return true;
		}
	}

	//metodo kEsimo que devuelve la kEsima clave del arbol en orden ascendente y con los rangos de 1 a size, si k es menor que 1 o mayor que size, lanza una excepcion de indice fuera de rango
	public K kEsimo(int k) throws IndiceFueraDeRangoException {
		if (k < 1 || k > this.size()) {
			throw new IndiceFueraDeRangoException("El indice " + k + " esta fuera de rango [1, " + this.size() + "]");
		}
		if (k == 1) return obtenerMinimo(raiz).clave;
		if (k == this.size()) return obtenerMaximo(raiz).clave;
		else {
			return kEsimoRec(this.raiz, k);
		}
	}

	private K kEsimoRec(Nodo<K, V> nodo, int k) {
		int l = obtenerTamano(nodo.izq);
		this.visitas++;
		if (k == l + 1) {
			return nodo.clave;
		}
		else if (k <= l ) {
			return kEsimoRec(nodo.izq, k);
		}
		else {
			return kEsimoRec(nodo.der, k - l - 1);
		}
	}

	//metodo que devuelve la cantidad de claves menores a una dada que no tiene por que existir en el arbol
	public int cuantosMenores(K clave) {
		return cuantosMenoresRec(this.raiz, clave);
	}

	private int cuantosMenoresRec(Nodo<K, V> nodo, K clave) {
		//caso base, si el nodo es nulo, no hay menores, retornamos 0
		if (nodo == null) {
			return 0;
		}
		this.visitas++;
		//si la clave en cuestion es menor que la del nodo, bajamos por la izq, ya que no cuenta
		if (clave.compareTo(nodo.clave) <= 0) {
			return cuantosMenoresRec(nodo.izq, clave);
		} else { //si la clave es mayor, contamos el nodo, el subarbol izquierdo del nodo porque ahi son todas menores, y seguimos el recorrido por la derecha
			return 1 + obtenerTamano(nodo.izq) + cuantosMenoresRec(nodo.der, clave);
		}
	}

	public int consultarRango(K a, K b) throws RangoInvalidoException, ClaveNulaException {
		if (a == null || b == null) {
			throw new ClaveNulaException("Las claves no pueden ser nulas.");
		}
		if (a.compareTo(b) > 0) {
			throw new RangoInvalidoException("El rango es invalido, " + a + " es mayor que " + b);
		}
		// si b esta en el arbol, lo contamos tambien (cuantosMenores() solamente devuleve la cantidad de nodos estrictamente menores, por eso sumamos 1), sino simplemente contamos cuantos menores que b hay, y restamos cuantos menores que a hay, y eso nos da la cantidad de nodos en el rango [a, b]
		int menoresB = menoresOIguales(b);

		return menoresB - cuantosMenores(a);
	}

	private int menoresOIguales(K clave) {
		return menoresOIgualesRec(this.raiz, clave);
	}

	private int menoresOIgualesRec(Nodo<K, V> nodo, K clave) {
		if (nodo == null) {
			return 0;
		}
		this.visitas++;
		if (clave.compareTo(nodo.clave) < 0) {
			return menoresOIgualesRec(nodo.izq, clave);
		} else if (clave.compareTo(nodo.clave) > 0) {
			return 1 + obtenerTamano(nodo.izq) + menoresOIgualesRec(nodo.der, clave);
		} else {
			return 1 + obtenerTamano(nodo.izq);
		}
	}

	//tiempo O(n) porque recorre todo el arbol, y no O(h) como el metodo anterior, pero es mas facil de entender y sirve para demostrar las tablas y comparar con el metodo anterior, que es mas eficiente
	public int consultarRangoIngenuo(K a, K b) throws RangoInvalidoException {
		if (a.compareTo(b) > 0) {
			throw new RangoInvalidoException("El rango es invalido, " + a + " es mayor que " + b);
		}
		return rangoIngenuoRec(this.raiz, a, b);
	}

	private int rangoIngenuoRec(Nodo<K, V> nodo, K a, K b) {
		if (nodo == null) {
			return 0;
		}
		this.visitas++;
		int cantidad = rangoIngenuoRec(nodo.izq, a, b);
		if (nodo.clave.compareTo(a) >= 0 && nodo.clave.compareTo(b) <= 0) {
			cantidad++;
		}
		cantidad += rangoIngenuoRec(nodo.der, a, b);
		return cantidad;
	}

	public int rango(K clave) throws ClaveInexistenteException, ClaveNulaException {
		if (!this.contiene(clave)) {
			throw new ClaveInexistenteException("La clave " + clave + " no existe en el arbol.");
		}
		return cuantosMenores(clave) + 1;

	}


	//metodos que buscan el maximo y el minimo, agregado por practicidad y aparte son O(h) y no O(n)
	private Nodo<K, V> obtenerMinimo(Nodo<K, V> nodo) {
		if (nodo == null) {
			return null;
		}
		if (nodo.izq == null) {
			return nodo;
		}
		return obtenerMinimo(nodo.izq);
	}

	private Nodo<K, V> obtenerMaximo(Nodo<K, V> nodo) {
		if (nodo == null) {
			return null;
		}
		if (nodo.der == null) {
			return nodo;
		}
		return obtenerMaximo(nodo.der);
	}

	public int size() {
		return obtenerTamano(raiz);
	}

	public int altura() {
		return alturaRec(this.raiz);
	}
	private int alturaRec(Nodo<K, V> nodo) {
		if (nodo == null) {
			return -1; //altura de un arbol vacio es -1, altura de un arbol con un solo nodo es 0
		}
		this.visitas++;
		int alturaIzq = alturaRec(nodo.izq);
		int alturaDer = alturaRec(nodo.der);
		return Math.max(alturaIzq, alturaDer) + 1;
	}

	public long visitas() {
		return this.visitas;
	}

	public void reiniciarVisitas() {
		this.visitas = 0;
	}








}
