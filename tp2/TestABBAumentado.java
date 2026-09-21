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
import java.util.Random;
public class TestABBAumentado {


    public static void main(String[] args) throws ClaveInexistenteException, ClaveNulaException, Exception {
		System.out.println("Test ABBAumentado");
		ABBAumentado<Integer, String> abb = new ABBAumentado<>();
		FaseA(abb);
		FaseB(abb);
		FaseC(abb);
		test3(abb);
		test4();
    }

	public static void FaseA(ABBAumentado<Integer, String> abb) throws ClaveInexistenteException, ClaveNulaException {
		System.out.printf("%-20s |%-80s |%-10s |%-10s |%-10s \n", "Operacion", "Arbol toString", "Altura", "Tamano", "Tamanos consistentes");
		System.out.println("--------------------------------------------------------------------------------------------------------------------");

		printFaseA("(inicial)", abb);
		abb.agregar(50, "P" + 50);
		printFaseA("agregar(50)", abb);
		abb.agregar(30, "P" + 30);
		printFaseA("agregar(30)", abb);
		abb.agregar(70, "P" + 70);
		printFaseA("agregar(70)", abb);
		abb.agregar(20, "P" + 20);
		printFaseA("agregar(20)", abb);
		abb.agregar(40, "P" + 40);
		printFaseA("agregar(40)", abb);
		abb.agregar(60, "P" + 60);
		printFaseA("agregar(60)", abb);
		abb.agregar(80, "P" + 80);
		printFaseA("agregar(80)", abb);
		abb.agregar(35, "P" + 35);
		printFaseA("agregar(35)", abb);
		abb.agregar(65, "P" + 65);
		printFaseA("agregar(65)", abb);
	}

	private static void printFaseA(String operacion, ABBAumentado<Integer, String> abb) {
		String formato = "%-20s |%-80s |%-10s |%-10s |%-10s \n";
		System.out.printf(formato, operacion, abb.toString(),abb.altura(),abb.size(), abb.tamanosConsistentes());

	}

	private static void FaseB(ABBAumentado<Integer, String> abb) throws ClaveInexistenteException, ClaveNulaException {
		System.out.println("\nFASE B");
		System.out.printf("%-50s %-10s \n", "Operacion y resultado", "Visitas");
		System.out.println("----------------------------------------------------------------");

		abb.reiniciarVisitas();
		printFaseB("kEsimo(6) ->" + abb.kEsimo(6), abb);
		abb.reiniciarVisitas();
		printFaseB("cuantasMenores(65) -> " + abb.cuantosMenores(65), abb);
		abb.reiniciarVisitas();
		printFaseB("cuantasMenores(35) -> " + abb.cuantosMenores(35), abb);
		abb.reiniciarVisitas();
		printFaseB("consultarRango(35, 65) -> " + abb.consultarRango(35, 65), abb);
		abb.reiniciarVisitas();
		printFaseB("consultarRangoIngenuo(35, 65) -> " + abb.consultarRangoIngenuo(35, 65), abb);
		abb.reiniciarVisitas();
		printFaseB("sucesor(40) -> " + abb.sucesor(40), abb);
		abb.reiniciarVisitas();
		printFaseB("sucesor(80) -> " + abb.sucesor(80), abb);
		abb.reiniciarVisitas();
		printFaseB("predecesor(35) -> " + abb.predecesor(35), abb);
		abb.reiniciarVisitas();
		printFaseB("rango(50) -> " + abb.rango(50), abb);
		abb.reiniciarVisitas();

	}

	private static void printFaseB(String operacion, ABBAumentado<Integer, String> abb) {

		String formato = "%-50s %-10s\n";
		System.out.printf(formato, operacion, abb.visitas());
	}

	private static void FaseC(ABBAumentado<Integer, String> abb) throws ClaveInexistenteException, ClaveNulaException {
		System.out.println("\nFASE C");
		System.out.printf("%-50s %-80s %-10s\n", "Operacion", "Arbol toString", "Altura");
		System.out.println("--------------------------------------------------------------------------------------------------------------------");
		printFaseC("(inicial)", abb);
		printFaseC("eliminar(30) -> " + abb.eliminar(30), abb);
		printFaseC("kEsimo(2) -> " + abb.kEsimo(2), abb);
		printFaseC("consultarRango(35, 65) -> " + abb.consultarRango(35, 65), abb);
		printFaseC("sucesor(20) -> " + abb.sucesor(20), abb);
		printFaseC("contiene(30) -> " + abb.contiene(30), abb);
		printFaseC("Tamanos consistentes ->" + abb.tamanosConsistentes(), abb);

	}

	private static void printFaseC(String operacion, ABBAumentado<Integer, String> abb) {
		String formato = "%-50s %-80s %-10s\n";
		System.out.printf(formato, operacion, abb, abb.altura());
	}

	private static void test3(ABBAumentado<Integer, String> abb){
		System.out.println("\nTest 3: Recorrido in-order del arbol en cuestion usando for-each. ");
		for(Integer i : abb){
			System.out.printf("%d ", i);
		}
		System.out.println();
	}

private static void test4() throws Exception {
	System.out.println("\nTest 4: Tendencias de los arboles");
	int[] ns = {2000, 4000, 6000, 8000, 10000};
	String formato = "%-10s\t%-10s\t%-10s\t%-30s\t%-10s\t%-10s\t%-10s\n";
	System.out.printf(formato, "N", "h_aleat", "h_ord", "vis_kEsimo_aleat", "vis_kEsimo_ord", "vis_rango_aum", "vis_rango_ing");

	for (int n : ns) {
		int[] a = new int[n];
		//rellenar a con valores del 1 a n
		for (int i = 0; i < n; i++) {
			a[i] = i + 1;
		}
		//mezclar a aleatoriamente con la semilla esa
		Random rng = new Random(2026);
		for (int i = n - 1; i > 0; i--) {
			int j = rng.nextInt(i + 1);
			int tmp = a[i];
			a[i] = a[j];
			a[j] = tmp;
		}

		ABBAumentado<Integer, String> aleat = new ABBAumentado<>();
		ABBAumentado<Integer, String> ord = new ABBAumentado<>();

		for (int i = 0; i < n; i++) {
			aleat.agregar(a[i], "");
			ord.agregar(i + 1, "");
		}

		int hAleat = aleat.altura();
		int hOrd = ord.altura();



		// kEsimo(N/2) en cada abb
		aleat.reiniciarVisitas();
		aleat.kEsimo(n / 2);
		long visKAleat = aleat.visitas();

		ord.reiniciarVisitas();
		ord.kEsimo(n / 2);
		long visKOrd = ord.visitas();


		//lo de las tendencias del ab aleaotorio, o sea limites de rango, que serian a y b
		int limiteX0 = aleat.kEsimo(n / 4);
		int limiteX = aleat.kEsimo(3 * n / 4);


		aleat.reiniciarVisitas();
		aleat.consultarRango(limiteX0, limiteX);
		long visRangoAum = aleat.visitas();


		aleat.reiniciarVisitas();
		aleat.consultarRangoIngenuo(limiteX0, limiteX);
		long visRangoIng = aleat.visitas();

		System.out.printf(formato, n, hAleat, hOrd, visKAleat, visKOrd, visRangoAum, visRangoIng);

		//debug: si alguna de las columnas no es correcta
		if (hOrd != n - 1 || visKOrd != n / 2 || visRangoIng != n) {
			System.out.println("  !! columnas incorrectas para N:" + n);
		}
	}
}
}
//punto 1
//hOrd es N - 1 porque es un arbol degenerado, y eso quiere decir que la altura es N - 1, porque hay N nodos y la altura es la cantidad de hijos que tiene la raiz, o sea n - 1
//kEsimoOrd es N/2 porque es un arbol degenerado, y para llegar al nodo N/2 hay que visitar N/2 nodos, porque cada nodo tiene un solo hijo, y hay que ir visitando cada uno hasta llegar al N/2

//punto 2
//visRangoAum queda en algunas decenas porque el arbol esta mayormente balanceado, entonces para llegar a los nodos del rango hay que visitar pocos nodos y aprovechamos su atributo tamano de cada hijo para determinar el resto, asi no visitamos tantos nodos.
//visRangoIng es N porque el algoritmo ingenuo recorre todo el arbol y cuenta los nodos que estan en el rango, entonces visita los N nodos

//punto 3
//en la traza B, consultarRango y rangoIngenuo tienen una diferencia de 1 (muy poca diferencia)pero conforme crece N, el tiempo de ejecucion de rangoIngenuo comenzara a crecer mucho mas rapido que el tiempo de ejecucion de rango (test 4), similar a como insersort parece ser mas rapido que mergesort para N pequenos, pero luego mergesort es mucho mas rapido que insersort para N grandes
