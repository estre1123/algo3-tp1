import java.io.IOException;

public class TestArbolBMas {
	public static void main(String[] args) throws IOException {
		try (ArbolBMas abm = ArbolBMas.crear("pruebas.dat", 60)) {
			printCumple("Vacio: un nivel", abm.niveles() == 1);
			printCumple("Vacio: dos paginas", abm.cantidadPaginas() == 2);
			printCumple("Vacio: raiz en pagina 1", abm.raiz() == 1);
			printCumple("Vacio: cero claves", abm.cantidadClaves() == 0);
			printCumple("Vacio: volcado",
					abm.volcarArbol().equals("1: #1[]"));

			abm.reiniciarContadores();

			printCumple("Vacio: buscar devuelve -1", abm.buscar(50) == -1);
			printCumple("Vacio: buscar lee una pagina",
					abm.paginasLeidas() == 1);
			printCumple("Vacio: buscar no escribe",
					abm.paginasEscritas() == 0);

			printCumple("Vacio: invariantes",
					abm.verificarInvariantes().equals("OK"));

			probarInserciones(abm);
			probarBusquedas(abm);
			probarRangos(abm);
		}

		probarPersistencia();
		probarArchivoInvalido();
	}

	public static void faseA(ArbolBMas abm) throws IOException, RangoInvalidoException, TamPaginaInvalidoException {
		printFaseA("Arbol B+ vacio", abm);
		abm.insertar(50, 0500);
		abm.insertar(20, 200);
		abm.insertar(80, 800);
		printFaseA("Insertar 50, 20, 80", abm);
		abm.insertar(10, 100);
		abm.insertar(40, 400);
		printFaseA("Insertar 10 (raiz nueva), 40 (reenlace)", abm);

		// System.out.printf();
	}

	public static void printFaseA(String op, ArbolBMas abm) throws IOException {
		String formato = "%-30s %10d %10d %10d %s\n";
		System.out.printf(formato, op, abm.niveles(), abm.cantidadPaginas(), abm.buscar(0), abm.volcarArbol());
	}

	private static void printCumple(String caso, boolean cumple) {
		System.out.println((cumple ? "OK: " : "NO CUMPLE: ") + caso);

	}

	private static void probarInserciones(ArbolBMas abm) throws IOException {

		int[] claves = { 50, 20, 80, 10, 30, 60, 90, 40, 70, 25, 35, 45, 15 };

		int[] lecturasEsperadas = { 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3 };

		int[] escriturasEsperadas = { 1, 1, 1, 3, 1, 1, 3, 3, 1, 1, 1, 5, 3 };

		int[] paginasEsperadas = { 2, 2, 2, 4, 4, 4, 5, 6, 6, 6, 6, 9, 10 };

		for (int i = 0; i < claves.length; i++) {
			int clave = claves[i];
			abm.reiniciarContadores();

			boolean insertada = abm.insertar(clave, 10L * clave);

			// Capturamos los contadores de esta insercion
			long lecturas = abm.paginasLeidas();
			long escrituras = abm.paginasEscritas();

			System.out.printf(
					"%nInsertar %d: R=%d W=%d paginas=%d%n",
					clave, lecturas, escrituras, abm.cantidadPaginas());
			System.out.println(abm.volcarArbol());

			printCumple("Se inserto " + clave, insertada);
			printCumple("Lecturas esperadas",
					lecturas == lecturasEsperadas[i]);
			printCumple("Escrituras esperadas",
					escrituras == escriturasEsperadas[i]);
			printCumple("Cantidad de paginas",
					abm.cantidadPaginas() == paginasEsperadas[i]);
			printCumple("Cantidad de claves",
					abm.cantidadClaves() == i + 1);

			String estado = abm.verificarInvariantes();
			printCumple("Invariantes: " + estado, estado.equals("OK"));

			printCumple("Diagnosticos no alteran contadores",
					abm.paginasLeidas() == lecturas
							&& abm.paginasEscritas() == escrituras);
		}
	}

	private static void probarBusquedas(ArbolBMas abm) throws IOException {

		System.out.println("\n--- BUSQUEDAS ---");

		long[] claves = { 10, 90, 33 };
		long[] posiciones = { 100, 900, -1 };

		for (int i = 0; i < claves.length; i++) {
			abm.reiniciarContadores();

			long resultado = abm.buscar(claves[i]);

			printCumple(
					"buscar(" + claves[i] + ") = " + resultado,
					resultado == posiciones[i]);
			printCumple("Busqueda: tres lecturas",
					abm.paginasLeidas() == 3);
			printCumple("Busqueda: ninguna escritura",
					abm.paginasEscritas() == 0);
		}

		int[] camino = abm.caminoBusqueda(35);

		printCumple("Camino de 35: [8, 3, 5]",
				camino.length == 3
						&& camino[0] == 8
						&& camino[1] == 3
						&& camino[2] == 5);

		System.out.println("\n--- DUPLICADO ---");

		String antes = abm.volcarArbol();
		int paginasAntes = abm.cantidadPaginas();
		long clavesAntes = abm.cantidadClaves();

		abm.reiniciarContadores();

		boolean insertada = abm.insertar(35, 1);

		printCumple("Duplicado: retorna false", !insertada);
		printCumple("Duplicado: tres lecturas",
				abm.paginasLeidas() == 3);
		printCumple("Duplicado: ninguna escritura",
				abm.paginasEscritas() == 0);
		printCumple("Duplicado: conserva cantidad de paginas",
				abm.cantidadPaginas() == paginasAntes);
		printCumple("Duplicado: conserva cantidad de claves",
				abm.cantidadClaves() == clavesAntes);
		printCumple("Duplicado: conserva estructura",
				abm.volcarArbol().equals(antes));

		printCumple("Duplicado: conserva posicion original 350",
				abm.buscar(35) == 350);
	}

	private static void printCumpleRango(ArbolBMas abm, long desde, long hasta, long[] esperadas,
			long lecturasEsperadas) throws IOException {

		final int[] entregadas = { 0 };
		final boolean[] paresCorrectos = { true };

		abm.reiniciarContadores();

		int cantidad = abm.buscarRango(desde, hasta, (clave, posicion) -> {
			int i = entregadas[0];

			if (i >= esperadas.length || clave != esperadas[i] || posicion != clave * 10L) {
				paresCorrectos[0] = false;
			}

			entregadas[0]++;
		});

		System.out.println("\nRango [" + desde + ", " + hasta + "]");

		printCumple("Claves, orden y posiciones correctos", paresCorrectos[0] && entregadas[0] == esperadas.length);

		printCumple("Cantidad retornada: " + cantidad,
				cantidad == esperadas.length);

		printCumple("Paginas leidas: " + abm.paginasLeidas(),
				abm.paginasLeidas() == lecturasEsperadas);

		printCumple("Ninguna escritura",
				abm.paginasEscritas() == 0);
	}

	private static void probarRangos(ArbolBMas abm) throws IOException {

		System.out.println("\n--- RANGOS ---");

		printCumpleRango(abm, 31, 35, new long[] { 35 }, 3);

		printCumpleRango(abm, 22, 62, new long[] { 25, 30, 35, 40, 45, 50, 60 }, 6);

		printCumpleRango(abm, 20, 45, new long[] { 20, 25, 30, 35, 40, 45 }, 5);

		printCumpleRango(abm, 31, 34, new long[] {}, 3);

		boolean lanzoExcepcion = false;

		try {
			abm.buscarRango(9, 5, (clave, posicion) -> {
			});
		} catch (RangoInvalidoException e) {
			lanzoExcepcion = true;
		}

		printCumple("Desde > hasta lanza RangoInvalidoException", lanzoExcepcion);
	}

	private static void probarPersistencia() throws IOException {
		System.out.println("\n--- PERSISTENCIA ---");

		printCumple("Archivo de 600 bytes",
				new java.io.File("pruebas.dat").length() == 600);

		try (ArbolBMas abm = ArbolBMas.abrir("pruebas.dat")) {
			printCumple("Pagina de 60 bytes", abm.tamPagina() == 60);
			printCumple("M = 4", abm.ordenInterno() == 4);
			printCumple("L = 3", abm.capacidadHoja() == 3);
			printCumple("Raiz en pagina 8", abm.raiz() == 8);
			printCumple("Tres niveles", abm.niveles() == 3);
			printCumple("Diez paginas", abm.cantidadPaginas() == 10);
			printCumple("Trece claves", abm.cantidadClaves() == 13);

			printCumple("Contadores en cero al abrir", abm.paginasLeidas() == 0 && abm.paginasEscritas() == 0);

			printCumple("Tras reabrir: buscar(45) = 450", abm.buscar(45) == 450);
			printCumple("Tras reabrir: buscar(15) = 150", abm.buscar(15) == 150);

			printCumple("Dos busquedas: seis lecturas", abm.paginasLeidas() == 6);
			printCumple("Dos busquedas: ninguna escritura", abm.paginasEscritas() == 0);

			String estado = abm.verificarInvariantes();
			printCumple("Invariantes tras reabrir: " + estado,
					estado.equals("OK"));

			printCumple("Verificar no altera contadores",
					abm.paginasLeidas() == 6
							&& abm.paginasEscritas() == 0);
		}
	}

	private static void probarArchivoInvalido() throws IOException {
		System.out.println("\n--- ARCHIVO INVALIDO ---");

		// Archivo separado: no modifica el indice de las pruebas.
		try (java.io.RandomAccessFile archivo =
				new java.io.RandomAccessFile("prueba-invalida.dat", "rw")) {

			archivo.setLength(0);
			archivo.write(new byte[60]);
		}

		boolean rechazada = false;

		try (ArbolBMas abm = ArbolBMas.abrir("prueba-invalida.dat")) {
			// Si entra aqui, abrir acepto un archivo que debia rechazar.
		} catch (FormatoInvalidoException e) {
			rechazada = true;
			System.out.println("Motivo del rechazo: " + e.getMessage());
		}

		printCumple("Rechaza una cabecera invalida", rechazada);
	}
}
