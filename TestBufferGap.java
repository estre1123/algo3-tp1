
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
	}
}
