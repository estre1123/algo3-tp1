import java.util.Iterator;
import java.util.NoSuchElementException;

public class BufferGap<T> implements Iterable<T> {

	// constantes
	// tamaño inicial del buffer
	private final int TAM_INICIAL = 16;

	// atributos indispensables
	private int inicioHueco;
	private int finHueco;
	private long desplazamientos = 0;
	private T datos[];

	public int posicionCursor() {
		return inicioHueco;
	}

	// getters y setters
	public int getInicioHueco() {
		return inicioHueco;
	}

	public void setInicioHueco(int inicioHueco) {
		this.inicioHueco = inicioHueco;
	}

	public int getFinHueco() {
		return finHueco;
	}

	public void setFinHueco(int finHueco) {
		this.finHueco = finHueco;
	}

	public Long desplazamientos() {
		return desplazamientos;
	}

	public void reiniciarDesplazamientos() {
		this.desplazamientos = 0;
	}

	/*
	 * tamañoHueco = finHueco - inicioHueco
	 *
	 */

	// Ignorar la advertencia que arroja castear una instancia de un array tal y
	// como se hace y te prende todo el ide en amarillo pero chill es
	@SuppressWarnings("unchecked")
	public BufferGap() {
		this.datos = (T[]) new Object[TAM_INICIAL];
		this.inicioHueco = 0;
		this.finHueco = TAM_INICIAL;
		this.desplazamientos = 0;
	}

	//inserta el elemento en la posicion del cursor y lo desplaza hacia adelante, si el hueco se llena redobla el array
	public void insertar(T obj) {
		this.datos[inicioHueco] = obj;
		this.inicioHueco++;
		if (this.inicioHueco == this.finHueco) {
			redoblarArr();
		}

	}


	//borra el elemento anterior al cursor (como en la vida real) y lo devuelve por si no se queres guardar aparte
	public T borrar() throws BufferVacioException { // agregar el throw buffervacio
		if (inicioHueco == 0) {
			throw new BufferVacioException("El buffer esta vacio!");
		}
		inicioHueco--;
		T obj = this.datos[inicioHueco];
		return obj;
	}


	//getters y setters para el arr lgm pude haber externalizado la verificacion del index pero asi nm nb
	public T get(int index) {
		if (index < 0 || index > this.size()) {
			throw new PosicionInvalidaException("Posicion invalida para el indice dado");
		}
		return this.datos[parseToIndiceFisico(index)];
	}

	public T set(T obj, int index) {
		if (index < 0 || index > this.size()) {
			throw new PosicionInvalidaException("Posicion invalida para el indice dado");
		}
		int indexFisico = parseToIndiceFisico(index);
		T data = this.datos[indexFisico];
		this.datos[indexFisico] = obj;
		return data;
	}


	//la funcion q mueve el cursor, mas bien yo le diria cantidadAMover pq el nombre me hace pensar q lo de le paso es el indice deseado o que pero bueno tp dice que si y yo hago caso
	public void moverCursor(int delta) {
		// Verificar que el cursor no se salga de array por ninguno de los dos lados
		if ((this.inicioHueco + delta) < 0 || (delta + this.finHueco) > this.capacidad()) {
			// arrojar el error pedido
			throw new PosicionInvalidaException("Posicion invalida para el indice, el cursor no puede moverse a esa posicion");
		}
		// mover todos los elementos como corresponde
		if (delta > 0) {
			for (int i = 0; i < delta; i++) {

				this.datos[this.inicioHueco + i] = this.datos[this.finHueco + i];
			}
		}
		if (delta < 0) {
			for (int i = 0; i > delta; i--) {
				// es -1 porque el pdf lo explica, los indices del hueco serian algo como [ini,
				// fin)
				this.datos[this.finHueco + i - 1] = this.datos[this.inicioHueco + i - 1];

			}
		}
		this.desplazamientos += Math.abs(delta);
		// al final cambiar los valores del inicio y fin del cursor
		this.inicioHueco += delta;
		this.finHueco += delta;
	}

	//retorna la capacidad del buffer de verdad, yo le llamaria len pq me confunde pero el tp dice q debe llamarse asi
	public int capacidad() {
		return this.datos.length;
	}


	//retorna el tamaño logico del buffer o sea cantidad de elementos que hay en el buffer
	public int size() {
		return this.capacidad() - (this.finHueco - this.inicioHueco);
	}

	//redobla el array copiando los elementos del anterior hasta la posicion cursor y luego el resto  de elementos los manda al final del arraay asi el usuario PUEDE seguir escribiendo dentro del buffer sin tener que volver a mover el cursor !importante para la defensa
	public void redoblarArr() {
		T[] newArr = (T[]) new Object[this.datos.length * 2];
		for (int i = 0; i < this.inicioHueco; i++) {
			if (i < this.inicioHueco) {
				newArr[i] = this.datos[i];
			}
		}
		int elementosSobrantes = this.datos.length - this.finHueco;
		int newFinHueco = newArr.length - elementosSobrantes;
		for (int i = 0; i < elementosSobrantes; i++) {
			newArr[i + newFinHueco] = this.datos[this.finHueco + i];
		}
		this.finHueco = newFinHueco;
		this.datos = newArr;
		//System.out.println("Capacidad redoblada!");

	}

	// util
	private int parseToIndiceFisico(int n) {
		if (n < this.inicioHueco) {
			return this.finHueco;
		}
		return n + (this.finHueco - this.inicioHueco);
	}

	// implementacion del iterable
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private int pos = sgtePosicion(0);

			//sobreescribir la funcion que verifica si hay elemento siguiente en una lista para que funcione segun el buffer este
			@Override
			public boolean hasNext() {
				return pos < datos.length;
			}

			// convierte el indice fisico que le pasamos como parametro a un indice logico
			private int sgtePosicion(int n) {
				if (n >= inicioHueco && n < finHueco) {
					return finHueco;
				}
				return n;

			}

			//sobrescribe la funcion que basicamente agarra el "siguiente" elemento de una lista
			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException("Ya no hay elementos en la lista");
				}
				T elemento = datos[pos];
				pos = sgtePosicion(pos + 1);
				return elemento;

			}

		};

	}

	@Override
	public String toString() {
		//el string builder este es para que no se genere un nuevo string cada vez que se concatena, sino que se va agregando al mismo objeto o sino el rendimiento cae en picada y mi pc implosiona
		StringBuilder s = new StringBuilder();
		// System.out.println(s.toString());
		for (T t : this) {
			s.append(t);
		}
		// System.out.println(s.toString());
		return s.toString();
	}

}
