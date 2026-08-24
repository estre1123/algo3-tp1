public class PilaES<E> {

    private Nodo tope;
    private int cantidad;

    private class Nodo {
        E dato;
        Nodo siguiente;

        Nodo(E dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    public void apilar(E dato) {
        Nodo nuevo = new Nodo(dato);
        nuevo.siguiente = tope;
        tope = nuevo;
        cantidad++;
    }

    public E desapilar() {
        if (estaVacia()) {
            throw new RuntimeException("La pila esta vacia");
        }

        E dato = tope.dato;
        tope = tope.siguiente;
        cantidad--;

        return dato;
    }

    public E tope() {
        if (estaVacia()) {
            throw new RuntimeException("La pila esta vacia");
        }

        return tope.dato;
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public int size() {
        return cantidad;
    }
}