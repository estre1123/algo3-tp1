public class ABBAumentado <K extends Comparable<? super K>, V> {
	private Nodo<K, V> raiz;
	private long visitas;

	private class Nodo<K, V> {
		private K clave;
		private V valor;
		private Nodo<K, V> izq;
		private Nodo<K, V> der;
		private int tamano;
		public Nodo(K clave, V valor) {
			this.clave = clave;
			this.valor = valor;
			this.izq = null;
			this.der = null;
			this.tamano = 0;
		}
	}


}
