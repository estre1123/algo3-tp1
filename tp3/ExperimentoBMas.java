import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class ExperimentoBMas {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso:");
            System.out.println("java ExperimentoBMas sintetico [N=1000000]");
            System.out.println("java ExperimentoBMas imdb title.basics.tsv [filas=300000] [P=4096]");
            System.out.println("java ExperimentoBMas busquedas");
            return;
        }
        if (args[0].equals("sintetico")) {
            int n = 1000000;
            if (args.length > 1) n = Integer.parseInt(args[1]);
            sintetico(n);
        } else if (args[0].equals("imdb")) {
            if (args.length < 2) {
                System.out.println("Debe ingresar el TSV.");
                return;
            }
            int filas = 300000;
            int p = 4096;
            if (args.length > 2) filas = Integer.parseInt(args[2]);
            if (args.length > 3) p = Integer.parseInt(args[3]);
            imdb(args[1], filas, p);
        } else if (args[0].equals("busquedas")) {
            busquedas();
        } else {
            System.out.println("Comando no valido.");
        }
    }

    // Tabla A: para cada P se crean dos arboles con las mismas claves.
    public static void sintetico(int n) throws Exception {
        if (n <= 0) throw new IllegalArgumentException("N debe ser positivo.");
        int[] tamanos = {128, 256, 512, 1024, 2048, 4096};
        long[] claves = new long[n];
        for (int i = 0; i < n; i++) claves[i] = i + 1;

        System.out.println("TABLA A - ESTRUCTURA SINTETICA (N=" + n + ")");
        System.out.println("P\tM\tL\tNivOrd\tHojOrd\tOcupHOrd%\tOcupIOrd%\tNivAle\tHojAle\tOcupHAle%\tOcupIAle%");

        for (int p : tamanos) {
            ArbolBMas orden = ArbolBMas.crear("sintetico_orden_" + p + ".dat", p);
            for (int i = 0; i < n; i++) orden.insertar(claves[i], claves[i]);
            comprobar(orden);
            EstadisticasBMas eOrden = orden.estadisticas();
            int m = orden.ordenInterno();
            int l = orden.capacidadHoja();
            int nivelesOrden = orden.niveles();
            orden.close();

            // Se restaura el orden original antes de cada permutacion.
            for (int i = 0; i < n; i++) claves[i] = i + 1;
            Random rng = new Random(2026);
            for (int i = n - 1; i > 0; i--) {
                int j = rng.nextInt(i + 1);
                long aux = claves[i];
                claves[i] = claves[j];
                claves[j] = aux;
            }

            ArbolBMas aleatorio = ArbolBMas.crear("sintetico_aleatorio_" + p + ".dat", p);
            for (int i = 0; i < n; i++) aleatorio.insertar(claves[i], claves[i]);
            comprobar(aleatorio);
            EstadisticasBMas eAleatorio = aleatorio.estadisticas();
            int nivelesAleatorio = aleatorio.niveles();
            aleatorio.close();

            System.out.printf(java.util.Locale.US,
                    "%d\t%d\t%d\t%d\t%d\t%.2f\t%.2f\t%d\t%d\t%.2f\t%.2f%n",
                    p, m, l, nivelesOrden, eOrden.hojas,
                    eOrden.ocupacionHojas * 100, eOrden.ocupacionInternos * 100,
                    nivelesAleatorio, eAleatorio.hojas,
                    eAleatorio.ocupacionHojas * 100, eAleatorio.ocupacionInternos * 100);
        }
    }

    public static void comprobar(ArbolBMas arbol) throws Exception {
        String resultado = arbol.verificarInvariantes();
        if (!resultado.equals("OK")) {
            throw new IllegalStateException("Invariantes: " + resultado);
        }
    }

    // Tabla B. Se construye primero aleatorio y luego ordenado,
    // de modo que datos.dat e indice.dat queden listos para C y D.
    public static void imdb(String ruta, int filas, int p) throws Exception {
        if (filas <= 0) throw new IllegalArgumentException("filas debe ser positivo.");
        ResultadoConstruccion aleatorio = construirImdb(ruta, filas, p, true);
        ResultadoConstruccion ordenado = construirImdb(ruta, filas, p, false);

        System.out.println("TABLA B - CONSTRUCCION IMDb");
        System.out.println("Orden\tRegistros\tDatosBytes\tIndiceBytes\tP\tM\tL\tNiveles\tHojas\tInternos\tOcupH%\tOcupI%\tPagEscritas\tSegundos");
        imprimirConstruccion("Archivo", ordenado);
        imprimirConstruccion("Aleatorio", aleatorio);
        System.out.println("indice.dat y datos.dat quedaron construidos en orden del archivo.");
    }

    static class ResultadoConstruccion {
        int registros, p, m, l, niveles, hojas, internos;
        long bytesDatos, bytesIndice, paginasEscritas;
        double ocupacionHojas, ocupacionInternos, segundos;
    }

    public static ResultadoConstruccion construirImdb(String ruta, int filas, int p,
                                                       boolean aleatorio) throws Exception {
        long inicio = System.nanoTime();
        BufferedReader lector = new BufferedReader(
                new InputStreamReader(new FileInputStream(ruta), "UTF-8"));
        ArchivoDatos datos = ArchivoDatos.crear("datos.dat");
        ArbolBMas arbol = ArbolBMas.crear("indice.dat", p);
        long[] claves = null;
        long[] posiciones = null;
        if (aleatorio) {
            claves = new long[filas];
            posiciones = new long[filas];
        }

        lector.readLine(); // encabezado
        int n = 0;
        String linea;
        while (n < filas && (linea = lector.readLine()) != null) {
            int fin = linea.indexOf('\t');
            if (fin < 0) throw new IllegalArgumentException("Fila TSV sin tabulacion.");
            long clave = Long.parseLong(linea.substring(2, fin));
            long pos = datos.agregar(clave, linea);
            if (aleatorio) {
                claves[n] = clave;
                posiciones[n] = pos;
            } else {
                arbol.insertar(clave, pos);
            }
            n++;
        }
        lector.close();

        if (aleatorio) {
            Random rng = new Random(2026);
            for (int i = n - 1; i > 0; i--) {
                int j = rng.nextInt(i + 1);
                long aux = claves[i];
                claves[i] = claves[j];
                claves[j] = aux;
                aux = posiciones[i];
                posiciones[i] = posiciones[j];
                posiciones[j] = aux;
            }
            for (int i = 0; i < n; i++) arbol.insertar(claves[i], posiciones[i]);
        }

        ResultadoConstruccion r = new ResultadoConstruccion();
        r.registros = n;
        r.bytesDatos = datos.tamano();
        r.p = arbol.tamPagina();
        r.m = arbol.ordenInterno();
        r.l = arbol.capacidadHoja();
        r.niveles = arbol.niveles();
        r.paginasEscritas = arbol.paginasEscritas();
        EstadisticasBMas e = arbol.estadisticas();
        r.hojas = e.hojas;
        r.internos = e.internos;
        r.ocupacionHojas = e.ocupacionHojas;
        r.ocupacionInternos = e.ocupacionInternos;
        datos.close();
        arbol.close();
        r.bytesIndice = new File("indice.dat").length();
        r.segundos = (System.nanoTime() - inicio) / 1000000000.0;
        return r;
    }

    public static void imprimirConstruccion(String nombre, ResultadoConstruccion r) {
        System.out.printf(java.util.Locale.US,
                "%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%.2f\t%.2f\t%d\t%.3f%n",
                nombre, r.registros, r.bytesDatos, r.bytesIndice,
                r.p, r.m, r.l, r.niveles, r.hojas, r.internos,
                r.ocupacionHojas * 100, r.ocupacionInternos * 100,
                r.paginasEscritas, r.segundos);
    }

    // Tablas C y D: usar el indice IMDb construido en orden del archivo.
    public static void busquedas() throws Exception {
        ArbolBMas arbol = ArbolBMas.abrir("indice.dat");
        ArchivoDatos datos = ArchivoDatos.abrir("datos.dat");
        int n = (int) arbol.cantidadClaves();
        if (n == 0) throw new IllegalStateException("Indice vacio.");
        long[] claves = new long[n];
        final int[] indice = {0};
        arbol.buscarRango(Long.MIN_VALUE, Long.MAX_VALUE, (clave, pos) -> {
            claves[indice[0]] = clave;
            indice[0]++;
        });
        if (indice[0] != n) throw new IllegalStateException("Cantidad de claves inconsistente.");
        // buscarRango entrega las claves en orden creciente.
        long min = claves[0];
        long max = claves[n - 1];
        Random rng = new Random(2026);
        long[] existentes = new long[1000];
        long[] inexistentes = new long[1000];
        for (int i = 0; i < 1000; i++) {
            existentes[i] = claves[rng.nextInt(n)];
        }
        if (max - min + 1L <= n) {
            throw new IllegalStateException("No hay suficientes claves ausentes en el intervalo.");
        }
        int i = 0;
        while (i < 1000) {
            long x = min + (long) (rng.nextDouble() * (max - min + 1.0));
            if (!contiene(claves, x)) {
                inexistentes[i] = x;
                i++;
            }
        }

        System.out.println("TABLA C - BUSQUEDAS");
        System.out.println("Metodo\tConsultas\tPagIndice/consulta\tRegLeidos/consulta\tMicroseg/consulta");
        medirBusqueda("B+ existentes", arbol, datos, existentes, 1000, true);
        medirBusqueda("B+ inexistentes", arbol, datos, inexistentes, 1000, true);
        medirBusqueda("Secuencial existentes", arbol, datos, existentes, 50, false);
        medirBusqueda("Secuencial inexistentes", arbol, datos, inexistentes, 50, false);

        System.out.println("TABLA D - RANGOS");
        System.out.println("w\tRegEntregados/consulta\tPagLeidas/consulta\tHojasLeidas/consulta\tMicroseg/consulta");
        int[] anchos = {10, 100, 1000, 10000};
        for (int w : anchos) {
            long totalRegistros = 0;
            long totalPaginas = 0;
            long totalHojas = 0;
            long totalNanos = 0;
            for (int j = 0; j < 100; j++) {
                long x = claves[rng.nextInt(n)];
                long hasta = x + w - 1L;
                arbol.reiniciarContadores();
                long inicio = System.nanoTime();
                int encontrados = arbol.buscarRango(x, hasta, (clave, pos) -> {
                    // Solo contamos las entradas entregadas; no leemos datos.dat.
                });
                long tiempo = System.nanoTime() - inicio;
                long paginas = arbol.paginasLeidas();
                long hojas = paginas - (arbol.niveles() - 1);
                totalRegistros += encontrados;
                totalPaginas += paginas;
                totalHojas += hojas;
                totalNanos += tiempo;
            }
            System.out.printf(java.util.Locale.US, "%d\t%.3f\t%.3f\t%.3f\t%.3f%n",
                    w, totalRegistros / 100.0, totalPaginas / 100.0,
                    totalHojas / 100.0, totalNanos / 100000.0);
        }
        datos.close();
        arbol.close();
    }

    // Busqueda binaria propia, sin Arrays.binarySearch.
    public static boolean contiene(long[] claves, long buscada) {
        int izquierda = 0;
        int derecha = claves.length - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            if (claves[medio] == buscada) return true;
            if (claves[medio] < buscada) izquierda = medio + 1;
            else derecha = medio - 1;
        }
        return false;
    }

    public static void medirBusqueda(String nombre, ArbolBMas arbol, ArchivoDatos datos,
                                     long[] consultas, int cantidad, boolean usarIndice) throws Exception {
        long paginasTotal = 0;
        long registrosTotal = 0;
        long nanosTotal = 0;
        for (int i = 0; i < cantidad; i++) {
            arbol.reiniciarContadores();
            datos.reiniciarContadores();
            long inicio = System.nanoTime();
            long pos;
            if (usarIndice) pos = arbol.buscar(consultas[i]);
            else pos = datos.busquedaSecuencial(consultas[i]);
            if (pos != -1) datos.leer(pos);
            nanosTotal += System.nanoTime() - inicio;
            paginasTotal += arbol.paginasLeidas();
            registrosTotal += datos.registrosLeidos();
        }
        System.out.printf(java.util.Locale.US, "%s\t%d\t%.3f\t%.3f\t%.3f%n",
                nombre, cantidad, paginasTotal / (double) cantidad,
                registrosTotal / (double) cantidad, nanosTotal / (cantidad * 1000.0));
    }
}
