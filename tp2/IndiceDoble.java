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
He utilizado herramientas de Inteligencia Artificial únicamente para comprender el enunciado y aclarar conceptos, sin utilizar código generado por estas herramientas en nuestra solución.
*/
public class IndiceDoble<K extends Comparable<? super K>, V> {

    private ABBAumentado<K, V> arbol;
    private TablaEncadenada<K, ABBAumentado.Nodo<K, V>> tabla;

    public IndiceDoble() {
        arbol = new ABBAumentado<>();
        tabla = new TablaEncadenada<>();
    }

    public void agregar(K clave, V valor) throws ClaveNulaException {
        if (clave == null) {
            throw new ClaveNulaException("La clave no puede ser nula.");
        }
        arbol.agregar(clave, valor);
        ABBAumentado.Nodo<K, V> nodo =arbol.nodoDe(clave);
        tabla.insertar(clave, nodo);
    }
    public V obtener(K clave) throws ClaveInexistenteException, ClaveNulaException {
        if (clave == null) {
            throw new ClaveNulaException("La clave no puede ser nula.");
        }
        ABBAumentado.Nodo<K,V> nodo = tabla.obtener(clave);
        if (nodo == null) {
            throw new ClaveInexistenteException("La clave " + clave + " no existe.");
        }
        return nodo.getValor();
    }
    public V eliminar(K clave) throws ClaveInexistenteException,ClaveNulaException {
        if (clave == null) {
            throw new ClaveNulaException("La clave no puede ser nula.");
        }
        ABBAumentado.Nodo<K, V> nodo = tabla.obtener(clave);
        if (nodo == null) {
            throw new ClaveInexistenteException("La clave " + clave + " no existe en el árbol.");
        }
        V valor = nodo.getValor();
        tabla.eliminar(clave);
        arbol.eliminar(clave);
        return valor;
    }
    public K kEsimo(int k) throws IndiceFueraDeRangoException {
        return arbol.kEsimo(k);
    }
    public int consultarRango(K a, K b) throws RangoInvalidoException, ClaveNulaException {
        return arbol.consultarRango(a, b);
    }
    public int size() {
        return arbol.size();
    }
    public ABBAumentado<K, V> getArbol() {
        return arbol;
    }

    public TablaEncadenada<K, ABBAumentado.Nodo<K, V>> getTabla() {
        return tabla;
    }
}
