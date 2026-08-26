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

public class TestBufferGap {


	public static void main(String[] args) throws BufferVacioException {
		System.out.println("Test 1:");
		test1();
		System.out.println("Test 2:");
		System.out.println("N \t Desplazamientos BufferGap \t Desplazamientos arreglo simple");
		System.out.println("-- \t ------------------------- \t ------------------------------");
    	for (int n = 100000; n <= 1000000; n += 100000) {
        	test2(n);
    	}
	}


	public static void test1() throws BufferVacioException {
		BufferGap<Character> buffer = new BufferGap<Character>();

		System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s\n", "Operacion", "Contenido logico", "inicioHueco", "finHueco", "Capacidad", "desplazamientos");

		tabla1("Inicio", buffer);
		buffer.insertar('H');
		tabla1("Insertar 'H'", buffer);
		buffer.insertar('o');
		tabla1("Insertar 'o'", buffer);
		buffer.insertar('l');
		tabla1("Insertar 'l'", buffer);
		buffer.insertar('a');
		tabla1("Insertar 'a'", buffer);

		buffer.moverCursor(-2);
		tabla1("moverCursor(-2)", buffer);
		buffer.insertar('X');
		tabla1("insertar('X')", buffer);
		buffer.get(4);
		tabla1("get(4)", buffer);

		buffer.borrar();
		tabla1("borrar()", buffer);
	}

	public static void tabla1(String op, BufferGap<Character> bf) {
		int capacidad = bf.capacidad();
		long desplazamientos = bf.desplazamientos();
		String bfs = bf.toString();
		System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s\n", op, bfs, bf.posicionCursor(), bf.getFinHueco(), capacidad, desplazamientos);

	}

	public static void test2(int N) {
		String abc = "abcdefghijklmnopqrstuvwxyz";
		//elementos q d vd importan digamos
		int cantidadAInsertar = 10000;

		BufferGap<Character> bf = new BufferGap<>();

		char[] arrSimple = new char[N + cantidadAInsertar];
		int cantElementos = 0;
		//vamos rellenando ambos "buffers" con elementos
		for (int i = 0; i < N; i++) {
			char c = abc.charAt((int) (Math.random() * abc.length()));
			bf.insertar(c);
			cantElementos++;
			arrSimple[cantElementos] = c;
		}

		// Ubicar cursor / posición en N/2
		int medio = N / 2;
		bf.moverCursor(medio - bf.posicionCursor()); // reposiciona siempre a la mitad
		int posArr = medio;

		// reiniciar solo el buffer pq el otro se cuenta aparte
		bf.reiniciarDesplazamientos();
		//para contar los desplazamientos de arr
		long desplazamientosIng = 0;
		for (int i = 0; i < cantidadAInsertar; i++) {
			char c = abc.charAt((int) (Math.random() * abc.length()));

			// insertar normal
			bf.insertar(c);

			// arr correr a la derecha todo lo que está después de posArr
			for (int j = cantElementos; j > posArr; j--) {
				arrSimple[j] = arrSimple[j - 1];
				desplazamientosIng++;
			}
			arrSimple[posArr] = c;
			posArr++;
			cantElementos++;
		}
		System.out.printf("%-28d\t%-24d\t%d\n", N, bf.desplazamientos(), desplazamientosIng);
		/*
		la columna del bufferGap es constantemente 0, ya que insertar caracteres no implica MOVER ningun elemento, ya que solo se esta ocupando el espacio YA disponible del hueco del buffer, y como este es ya tiene el espacio necesario, no se redimensiona, y como reiniciamos el desplazamiento en n/2, no hay desplazamientos adicionales.
		En cambio el arreglo ingenuo, al insertar un char o lo que sea, debe mover los n/2 elementos que estan a la derecha del cursor para hacer el espacio que se necesita, ej:
		si tengo
		a b c d e f
		y quiero insertar una X en la posicion 3...
		a b c _ e f
		dentre que mover la d, la e y la f a la derecha para que quede espacio
		a b c _ d e f
		esto implica un desplazamiento por cada item movido (3), pero a medida que voy insertando mas caracteres, el numero de desplazamientos va aumentando ya que hay MAS elementos a mover, es por eso que el arreglo ingenuo tiene un nro de desplazamientos totalmente superior y que aumenta a medida que n aumenta, el calculo seria algo como (n/2) * cantidad a insertar, ya que por cada item a insertar debe mover n/2 elementos
		totalmente insano

		*/
	}
}
