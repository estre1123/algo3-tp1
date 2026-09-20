/*
grupo:g_tq6
Nro tarea: TP1
Integrantes:
	Espinola Aguero,Anibal Julian  CIC=6362752  SECCION=TQ
	Hsu Yang, Estrella  CIC=5837842  SECCION=TQ
Nosotros, Anibal Julian Espinola Aguero y Estrella Hsu Yang:
No hemos discutido el código fuente de nuestra tarea con ningún otro grupo, solo con el Profesor o el AER.
No hemos usado código obtenido de otro estudiante o de cualquier otra fuente no autorizada, modificada o no modificada.
Cualquier código o documentación utilizada en nuestro programa obtenido de fuentes, tales como libros o notas de curso, ha sido claramente indicada en nuestra tarea.
*/
public class TablaEncadenada<K,E> {

    private class Entrada<K,E>{
        K clave;
        E datos;
        Entrada<K,E> siguiente;
    }
    private Entrada<K,E>[] tabla;
    private static final int TAM_INICIAL=11;
    private double amax=1;
    private int m=TAM_INICIAL;
    private int n=0;
    private int sondas=0;

    @SuppressWarnings("unchecked")
    public TablaEncadenada() {
        tabla = (Entrada<K,E>[]) new Entrada[m];
        n = 0;
    }
    @SuppressWarnings("unchecked")
    public TablaEncadenada(int m, double alfaMax) {
        this.m = m;
        this.amax = alfaMax;
        this.tabla = (Entrada<K,E>[]) new Entrada[m];
        this.n = 0;
    }


    private int h(K clave){
        return (clave.hashCode() & 0x7fffffff) % this.m;
    }

    public void insertar(K clave,E datos){
        int posicion=h(clave);
        Entrada<K,E> actual=tabla[posicion];
        while(actual!=null){
            this.sondas++;
            if (actual.clave.equals(clave)) {
                actual.datos = datos;
                return;
            }
        actual = actual.siguiente;
        }

        Entrada<K,E> nueva = new Entrada<>();
        nueva.clave = clave;
        nueva.datos = datos;
        nueva.siguiente = tabla[posicion];
        tabla[posicion] = nueva;

		this.n++;
        if ((double) n / m > this.amax) {
            rehash();
        }
    }
    @SuppressWarnings("unchecked")
    private void rehash() {
        Entrada<K,E>[] tablaAnterior = tabla;
        this.m = this.m * 2;
        tabla = (Entrada<K,E>[]) new Entrada[m];
        for (int i=0;i<tablaAnterior.length; i++) {
            Entrada<K,E> actual=tablaAnterior[i];
            while (actual!=null) {
                Entrada<K,E> siguiente=actual.siguiente;
                int posicion=h(actual.clave);
                actual.siguiente=tabla[posicion];
                tabla[posicion]=actual;
                actual=siguiente;
            }
        }
    }
    public E obtener (K clave){
        int posicion=h(clave);
        Entrada<K,E> actual=tabla[posicion];
        while(actual!=null){
            this.sondas++;
            if(actual.clave.equals(clave)){
                return actual.datos;
            }
            actual=actual.siguiente;
        }
        return null;
    }
    public E eliminar(K clave) {
        int posicion = h(clave);
        Entrada<K,E> actual = tabla[posicion];
        Entrada<K,E> anterior = null;
        while (actual != null) {
            this.sondas++;
            if (actual.clave.equals(clave)) {
				//si el nodo a eliminar es el primero de la lista, actual.siguiente es el siguiente nodo, y lo asignamos a tabla[posicion], si no es el primero, entonces anterior.siguiente apunta al siguiente nodo de actual, eliminando actual de la lista
                if (anterior == null) {
                    tabla[posicion] = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                n--;
                return actual.datos;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return null;
    }
    public int capacidad(){
        return m;
    }
    public int size(){
        return n;
    }
    public double factorCarga(){
        return (double) n/m;
    }
    public int sondas(){
        return this.sondas;
    }
    public void reiniciarSondas(){
        this.sondas=0;
    }
    public void dump() {
        for (int i = 0; i < tabla.length; i++) {
            System.out.print(i + ": ");
            Entrada<K,E> actual = tabla[i];
            while (actual != null) {
                System.out.print(actual.clave + " ");
                actual = actual.siguiente;
            }
            System.out.println();
        }
    }

}
