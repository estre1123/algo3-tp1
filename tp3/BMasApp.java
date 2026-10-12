
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class BMasApp {
    public static void main(String[] args) throws Exception {
        //asegurar de que si se puso algo
        if (args.length == 0) {
            System.out.println("Debe ingresar un comando.");
            return;
        }
        String comando = args[0];

        if (comando.equals("construir")) {
            // por si solo pone construir
            if (args.length < 2) {
                System.out.println("Debe ingresar el archivo TSV.");
                return;
            }
            //indica de donde leer
            String ruta = args[1];
            //cant filas
            int filas = 300000;
            int tamPagina = 4096;
            boolean aleatorio = false;

            int numeros = 0;
            int i;
            //inicia desde 2 porque 0 es "construir" y 1 es la ruta
            for (i = 2; i < args.length; i++) {
                if (args[i].equals("--aleatorio")) {
                    aleatorio = true;
                } else if (numeros == 0) {
                    filas = Integer.parseInt(args[i]);
                    numeros++;
                } else if (numeros == 1) {
                    tamPagina = Integer.parseInt(args[i]);
                    numeros++;
                }
            }
            // ahi obtenemos todos los datos y le llamamos a construir
            construir(ruta, filas, tamPagina, aleatorio);
        }else if (comando.equals("buscar")) {
            if (args.length < 2) {
                System.out.println("Debe ingresar una clave.");
                return;
            }
            buscar(args[1]);
        } else if (comando.equals("rango")) {
            if (args.length < 3) {
                System.out.println("Debe ingresar desde y hasta.");
                return;
            }
            long desde = Long.parseLong(args[1]);
            long hasta = Long.parseLong(args[2]);
            int mostrar = 10;
            if (args.length >= 4) {
                String valor = args[3];
                if (valor.startsWith("mostrar=")) {
                    mostrar = Integer.parseInt(valor.substring(8));
                }
            }
            rango(desde, hasta, mostrar);
        } else if (comando.equals("secuencial")) {
            if (args.length < 2) {
                System.out.println("Debe ingresar una clave.");
                return;
            }
            secuencial(Long.parseLong(args[1]));

        } else if (comando.equals("info")) {
            info();

        } else if (comando.equals("verificar")) {
            verificar();

        } else if (comando.equals("pagina")) {
            if (args.length < 2) {
                System.out.println("Debe ingresar un numero de pagina.");
                return;
            }

            pagina(Integer.parseInt(args[1]));

        } else {
            System.out.println("Comando no valido.");
        }
    }

    public static void construir(String ruta, int filas,int tamPagina, boolean aleatorio)throws Exception {
        //abre la ruta, cambiar de bytes a letra, la ultima permite leer por linea
        BufferedReader lector = new BufferedReader(new InputStreamReader(new FileInputStream(ruta), "UTF-8"));
        //crear los dos archivos
        ArchivoDatos datos = ArchivoDatos.crear("datos.dat");
        ArbolBMas arbol = ArbolBMas.crear("indice.dat", tamPagina);

        long[] claves = null;
        long[] posiciones = null;
        //solo se crea cuando introduce aleatoriamente
        //porque cuando no es aleatorio al leer ya inserta
        //pero si es aleatorio lee y tiene que guardar para insertar aleatoriamente despues
        if (aleatorio) {
            claves = new long[filas];
            posiciones = new long[filas];
        }

        lector.readLine();

        String linea;
        int n = 0;
        //mientras que exista mas filas y que no sea nula la fila 
        while (n < filas && (linea = lector.readLine()) != null) {
            //busca la posicion del primer \t
            int fin = linea.indexOf('\t');
            //saca el string desde posicion 0 hasta primer\t que es el fin（sin \t）
            String tconst = linea.substring(0, fin);
            //funcion substring saca el string desde posicion 2, luego con la funcion parseLong convierte a long
            long clave = Long.parseLong(tconst.substring(2));
            //llama a agregar y lo agrega al archivo y retorna su posicion inicial de bytes
            long pos = datos.agregar(clave, linea);

            if (aleatorio) {
                //si es aleatorio guarda primero a los arreglos para insertar despues
                claves[n] = clave;
                posiciones[n] = pos;
            } else {
                //si no es aleatorio inserta directamente al arbol
                arbol.insertar(clave, pos);
            }

            n++;
        }

        if (aleatorio) {

            Random rng = new Random(2026);
            int i;
            for (i = n - 1; i > 0; i--) {
                //genera un numero de 0 a i
                int j = rng.nextInt(i + 1); 

                long aux = claves[i];
                claves[i] = claves[j];
                claves[j] = aux;
                //al cambiar los claves posiciones tambien debe cambiar
                aux = posiciones[i];
                posiciones[i] = posiciones[j];
                posiciones[j] = aux;
            }
            //insertamos en el arbol
            for (i = 0; i < n; i++) {
                arbol.insertar(claves[i], posiciones[i]);
            }
        }
        lector.close();
        datos.close();
        arbol.close();
    }
    
    public static void buscar(String entrada) throws Exception {
        //si empieza con tt le saca
        if (entrada.startsWith("tt")) {
            entrada = entrada.substring(2);
        }
        //convierte de stirng a long 
        long clave = Long.parseLong(entrada);

        ArbolBMas arbol = ArbolBMas.abrir("indice.dat");
        ArchivoDatos datos = ArchivoDatos.abrir("datos.dat");

        arbol.reiniciarContadores();
        //busca en el arbol
        long pos = arbol.buscar(clave);
        //cant pag leida
        long paginas = arbol.paginasLeidas();
        
        int[] camino = arbol.caminoBusqueda(clave);
        //imprimimos el camino
        System.out.print("paginas leidas: " + paginas + " (");
        for (int i = 0; i < camino.length; i++) {
            if (i > 0) {
                System.out.print(" -> ");
            }
            System.out.print("#" + camino[i]);
        }
        System.out.println(")");

        if (pos == -1) {
            System.out.println("No encontrado.");
        } else {
            System.out.println("pos=" + pos + " " + datos.leer(pos));
        }

        datos.close();
        arbol.close();
    }
    
    public static void rango(long desde, long hasta, int mostrar)throws Exception {

        ArbolBMas arbol = ArbolBMas.abrir("indice.dat");
        ArchivoDatos datos = ArchivoDatos.abrir("datos.dat");

        arbol.reiniciarContadores();
        datos.reiniciarContadores();

        final int[] mostrados = {0};

        int cantidad = arbol.buscarRango(desde, hasta, (clave, pos) -> {

            if (mostrados[0] < mostrar) {
                System.out.println(datos.leer(pos));
                mostrados[0]++;
            }

        });

        System.out.println("Cantidad encontrada: " + cantidad);
        System.out.println("Paginas leidas: " + arbol.paginasLeidas());
        System.out.println("Registros leidos: " + datos.registrosLeidos());

        datos.close();
        arbol.close();
    }
    public static void secuencial(long clave) throws Exception {

        ArchivoDatos datos = ArchivoDatos.abrir("datos.dat");
        //llama a la funcion del archivoDatos
        long pos = datos.busquedaSecuencial(clave);

        if (pos == -1) {
            System.out.println("No encontrado.");
        } else {
            System.out.println("pos=" + pos + " " + datos.leer(pos));
        }

        datos.close();
    }
    public static void info() throws Exception {

        ArbolBMas arbol = ArbolBMas.abrir("indice.dat");

        System.out.println("P: " + arbol.tamPagina());
        System.out.println("M: " + arbol.ordenInterno());
        System.out.println("L: " + arbol.capacidadHoja());
        System.out.println("Raiz: " + arbol.raiz());
        System.out.println("Niveles: " + arbol.niveles());
        System.out.println("Paginas: " + arbol.cantidadPaginas());
        System.out.println("Claves: " + arbol.cantidadClaves());

        EstadisticasBMas e = arbol.estadisticas();

        System.out.println("Ocupacion hojas: " + e.ocupacionHojas*100+"%");
        System.out.println("Ocupacion internos: " + e.ocupacionInternos*100+"%");

        arbol.close();
    }
    
    public static void verificar() throws Exception {

        ArbolBMas arbol = ArbolBMas.abrir("indice.dat");

        System.out.println(arbol.verificarInvariantes());

        arbol.close();
    }

    
    public static void pagina(int nro) throws Exception {

        ArbolBMas arbol = ArbolBMas.abrir("indice.dat");

        System.out.println(arbol.volcarPagina(nro));

        arbol.close();
    }

}
