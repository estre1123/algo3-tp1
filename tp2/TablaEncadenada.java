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
        return (clave.hashCode()&0x7fffffff)%this.m;
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
        // Si la clave no existe, se crea una nueva entrada.
        Entrada<K,E> nueva = new Entrada();
        nueva.clave = clave;
        nueva.datos = datos;
        // La nueva entrada se agrega al comienzo de la lista.
        nueva.siguiente = tabla[posicion];
        tabla[posicion] = nueva;
        this.n++;
        // Si se supera el factor de carga maximo, se duplica la tabla y se vuelven a distribuir los elementos
        if ((double) n / m > this.amax) {
            rehash();
        }
    }
    // Duplica el tamaño de la tabla
    @SuppressWarnings("unchecked")
    private void rehash() {
        Entrada<K,E>[] tablaAnterior = tabla;
        m=m*2;
        tabla = (Entrada<K,E>[]) new Entrada[m];
        for (int i=0;i<tablaAnterior.length; i++) {
            Entrada<K,E> actual=tablaAnterior[i];
            while (actual!=null) {
                Entrada<K,E> siguiente=actual.siguiente;
                // Se calcula nuevamente la posicion porque se cambio el tamanio 
                int posicion=h(actual.clave);
                actual.siguiente=tabla[posicion];
                tabla[posicion]=actual;
                actual=siguiente;
            }
        }
    }
    //busca clave si no existe retorna null
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
                // Si es el primer elemento de la lista, se actualiza
                if (anterior == null) {
                    tabla[posicion] = actual.siguiente;
                } else {
                    // si no es el primer elemento se enlazan el anterior y el siguiente
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
    // Muestra por pantalla el contenido de cada posicion de la tabla en cada posicion se imprimen las claves de la lista enlazada.
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
