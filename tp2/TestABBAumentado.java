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
import java.util.Random;
//esta clase le robamos epicamente a los chavales pero sera rehecha
public class TestABBAumentado {

    // anchos de columna reutilizados en toda la clase, para que las tres impriman el mismo formato
    private static final String FORMATO_FILA_ESTADO = "%-16s %-60s %8s %6s %13s%n";
    private static final String FORMATO_FILA_CONSULTA = "%-32s %-10s %8s%n";

    public static void main(String[] args) throws ClaveInexistenteException, ClaveNulaException {
        pruebaTraza();
        pruebaFaseB();
        pruebaFaseC();
        pruebaForEach();
        pruebaExperimento();
    }


    private static ABBAumentado<Integer, String> arbolTraza;

    private static void pruebaTraza() throws ClaveInexistenteException, ClaveNulaException {
        System.out.println("A:");
        arbolTraza = new ABBAumentado<>();

        encabezadoEstado();
        imprimirEstado(arbolTraza, "(inicial)");

        int[] claves = {50, 30, 70, 20, 40, 60, 80, 35, 65};
        for (int clave : claves) {
            arbolTraza.agregar(clave, "P" + clave);
            imprimirEstado(arbolTraza, "agregar(" + clave + ")");
        }
        System.out.println();
    }

    private static void encabezadoEstado() {
        System.out.printf(FORMATO_FILA_ESTADO, "Operacion", "toString()", "altura", "size", "consistente");
        System.out.println("-".repeat(107));
    }

    private static void imprimirEstado(ABBAumentado<Integer, String> arbol, String operacion) {
        String texto = arbol.size() == 0 ? "(vacio)" : arbol.toString();
        System.out.printf(FORMATO_FILA_ESTADO,
                operacion, texto, arbol.altura(), arbol.size(), arbol.tamanosConsistentes());
    }

    private static void pruebaFaseB() throws ClaveInexistenteException, ClaveNulaException {
        System.out.println("B:");
        encabezadoConsulta();

        arbolTraza.reiniciarVisitas();
        imprimirConsulta("kEsimo(6)", arbolTraza.kEsimo(6));
        imprimirConsulta("cuantosMenores(65)", arbolTraza.cuantosMenores(65));
        imprimirConsulta("cuantosMenores(35)", arbolTraza.cuantosMenores(35));
        imprimirConsulta("consultarRango(35,65)", arbolTraza.consultarRango(35, 65));
        imprimirConsulta("consultarRangoIngenuo(35,65)", arbolTraza.consultarRangoIngenuo(35, 65));
        imprimirConsulta("sucesor(40)", arbolTraza.sucesor(40));
        imprimirConsulta("sucesor(80)", arbolTraza.sucesor(80));
        imprimirConsulta("predecesor(35)", arbolTraza.predecesor(35));
        imprimirConsulta("rango(50)", arbolTraza.rango(50));

        System.out.println();
    }

    private static void encabezadoConsulta() {
        System.out.printf(FORMATO_FILA_CONSULTA, "Operacion", "Resultado", "Visitas");
        System.out.println("-".repeat(52));
    }

    private static <T> void imprimirConsulta(String operacion, T resultado) {
        System.out.printf(FORMATO_FILA_CONSULTA, operacion, String.valueOf(resultado), arbolTraza.visitas());
        arbolTraza.reiniciarVisitas();
    }



    private static void pruebaFaseC() throws ClaveInexistenteException, ClaveNulaException {
        System.out.println("C:");

        encabezadoEstado();
        String valorEliminado = arbolTraza.eliminar(30);
        imprimirEstado(arbolTraza, "eliminar(30)=\"" + valorEliminado + "\"");
        System.out.println();

        encabezadoConsulta();
        imprimirConsulta("kEsimo(2)", arbolTraza.kEsimo(2));
        imprimirConsulta("consultarRango(35,65)", arbolTraza.consultarRango(35, 65));
        imprimirConsulta("sucesor(20)", arbolTraza.sucesor(20));
        imprimirConsulta("contiene(30)", arbolTraza.contiene(30));
        imprimirConsulta("tamanosConsistentes()", arbolTraza.tamanosConsistentes());

        System.out.println();
    }


    private static void pruebaForEach() {
        System.out.println("3:");

        StringBuilder sb = new StringBuilder();
        Integer anterior = null;
        boolean enOrdenCreciente = true;

        for (Integer clave : arbolTraza) {
            sb.append(clave).append(" ");
            if (anterior != null && anterior.compareTo(clave) >= 0) {
                enOrdenCreciente = false;
            }
            anterior = clave;
        }

        System.out.printf("%-12s %s%n", "Recorrido:", sb.toString().trim());
        System.out.printf("%-12s %s%n", "Esperado:", "20 35 40 50 60 65 70 80");
        System.out.printf("%-12s %s%n", "En orden?:", enOrdenCreciente ? "si (creciente)" : "no");
        System.out.println();
    }


    // tendencias
    // h_ord es n-1 porque crece en 1 por cada n agregada, vis_kEsimo_ord es n/2 porque necesita recorrer el arbol con costo n para llegar hasta la posicion n/2
    // la variacion entre ambos contadores de visitas al hallar el rango se debe a que una utiliza el invariante mientras la otra no,
    // la version ingenua fuerza su camino visitando todos los nodos existentes, mientras que la aumentada solo baja dos veces hasta los extremos del intervalo requerido
    // En un arbol chico no se nota tanta diferencia debido a la poca diferencia de magnitud en la cantidad de datos, tendiendo la version aumentada a un numero
    // cercano a n y la ingenua siendo n. Aun asi no cambia el hecho de que se van notando diferencias a medida que n va creciendo
    // Explicacion a detalle en README.md
    private static void pruebaExperimento() throws ClaveInexistenteException, ClaveNulaException {
        System.out.println("4:");
        System.out.printf("%-7s %-9s %-8s %-16s %-15s %-14s %-14s%n",
                "N", "h_aleat", "h_ord", "vis_kEsimo_aleat", "vis_kEsimo_ord",
                "vis_rango_aum", "vis_rango_ing");
        System.out.println("-".repeat(90));

        int[] valoresDeN = {2000, 4000, 6000, 8000, 10000};

        for (int n : valoresDeN) {
            // arbol aleatorio
            int[] permutacion = generarPermutacion(n);
            ABBAumentado<Integer, String> arbolAleatorio = new ABBAumentado<>();
            for (int clave : permutacion) {
                arbolAleatorio.agregar(clave, "V" + clave);
            }

            // arbol degenerado
            ABBAumentado<Integer, String> arbolOrdenado = new ABBAumentado<>();
            for (int clave = 1; clave <= n; clave++) {
                arbolOrdenado.agregar(clave, "V" + clave);
            }

            int hAleat = arbolAleatorio.altura();
            int hOrd = arbolOrdenado.altura();

            arbolAleatorio.reiniciarVisitas();
            arbolAleatorio.kEsimo(n / 2);
            long visKEsimoAleat = arbolAleatorio.visitas();

            arbolOrdenado.reiniciarVisitas();
            arbolOrdenado.kEsimo(n / 2);
            long visKEsimoOrd = arbolOrdenado.visitas();

            // rango sobre el arbol aleatorio: [kEsimo(N/4), kEsimo(3N/4)]
            int limiteA = arbolAleatorio.kEsimo(Math.max(1, n / 4));
            int limiteB = arbolAleatorio.kEsimo(Math.min(n, 3 * n / 4));

            arbolAleatorio.reiniciarVisitas();
            arbolAleatorio.consultarRango(limiteA, limiteB);
            long visRangoAum = arbolAleatorio.visitas();

            arbolAleatorio.reiniciarVisitas();
            arbolAleatorio.consultarRangoIngenuo(limiteA, limiteB);
            long visRangoIng = arbolAleatorio.visitas();

            System.out.printf("%-7d %-9d %-8d %-16d %-15d %-14d %-14d%n",
                    n, hAleat, hOrd, visKEsimoAleat, visKEsimoOrd, visRangoAum, visRangoIng);
        }

        System.out.println();
    }


    private static int[] generarPermutacion(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i + 1;
        }

        Random rng = new Random(2026);
        for (int i = n - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        return a;
    }
}
