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