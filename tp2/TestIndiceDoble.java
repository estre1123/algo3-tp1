import java.util.Random;

public class TestIndiceDoble {
    public static void main(String[] args) throws Exception{
        IndiceDoble<Integer, String> indice = new IndiceDoble<>();
        ABBAumentado<Integer, String> arbol = indice.getArbol();
        TablaEncadenada<Integer, ABBAumentado.Nodo<Integer, String>> tabla=indice.getTabla();
        System.out.println("A");
        indice.agregar(50, "P50");
        indice.agregar(30, "P30");
        indice.agregar(70, "P70");
        indice.agregar(20, "P20");
        indice.agregar(40, "P40");
        indice.agregar(60, "P60");
        indice.agregar(80, "P80");
        indice.agregar(35, "P35");
        indice.agregar(65, "P65");
        tabla.dump();
        System.out.println("ARBOL:");
        System.out.println(arbol.toString());
        System.out.println("indice.size() = " + indice.size());
        System.out.println("arbol.size() = " + arbol.size());
        System.out.println("arbol.visitas() = " + arbol.visitas());
        System.out.println("tabla.sondas() = " + tabla.sondas());
        System.out.println();
        System.out.println("B");
        indice.agregar(61, "P61");
        indice.agregar(41, "P41");
        tabla.dump();
        System.out.println("ARBOL:");
        System.out.println(arbol.toString());
        System.out.println("indice.size() = " + indice.size());
        System.out.println("arbol.size() = " + arbol.size());
        System.out.println("arbol.visitas() = " + arbol.visitas());
        System.out.println("tabla.sondas() = " + tabla.sondas());
        System.out.println();
        System.out.println("C");
        arbol.reiniciarVisitas();
        tabla.reiniciarSondas();
        String resultadoIndice = indice.obtener(70);
        System.out.println("indice.obtener(70) = " + resultadoIndice);
        System.out.println("arbol.visitas() = " + arbol.visitas());
        System.out.println("tabla.sondas() = " + tabla.sondas());
        arbol.reiniciarVisitas();
        String resultadoArbol = arbol.obtener(70);
        System.out.println("arbol.obtener(70) = " + resultadoArbol);
        System.out.println("arbol.visitas() = " + arbol.visitas());
        System.out.println();
        System.out.println("D");
        indice.eliminar(30);
        tabla.dump();
        System.out.println("ARBOL:");
        System.out.println(arbol.toString());
        System.out.println("indice.size() = " + indice.size());
        System.out.println("arbol.size() = " + arbol.size());
        System.out.println("arbol.visitas() = " + arbol.visitas());
        System.out.println("tabla.sondas() = " + tabla.sondas());
        System.out.println();
        System.out.println("indice.contiene(30) = " + contieneIndice(indice, 30));
        System.out.println("arbol.contiene(30) = " + arbol.contiene(30));
        System.out.println("indice.kEsimo(2) = " + indice.kEsimo(2));
        System.out.println("indice.obtener(35) = " + indice.obtener(35));
        System.out.println("indice.size() = " + indice.size());
        System.out.println("arbol.size() = " + arbol.size());
        System.out.println();
        experimento1();
        experimento2(); 
    }
    public static boolean contieneIndice(IndiceDoble<Integer, String> indice,Integer clave) {
        try {
            indice.obtener(clave);
            return true;
        } catch (ClaveInexistenteException e) {
            return false;
        } catch (ClaveNulaException e) {
            return false;
        }
    }
    public static int[] generar(int N) {
        int[] claves=new int[N];
        int i;
        for (i=0;i<N;i++) {
            claves[i]=i+1;
        }
        Random random=new Random(2026);
        for (i=N-1;i>0;i--) {
            int j=random.nextInt(i+1);
            int aux=claves[i];
            claves[i]=claves[j];
            claves[j]=aux;
        }
        return claves;
    }

    public static void experimento1() throws Exception {
        int[] valoresN={2000, 4000, 6000, 8000, 10000};
        System.out.println();     
        System.out.println("N       vis_ABB_get       sondas_hash_get            alpha                         m");
        for (int N : valoresN) {
            int[] claves=generar(N);
            IndiceDoble<Integer, Integer> indice=new IndiceDoble<>();
            for (int i=0; i<N; i++) {
                indice.agregar(claves[i], claves[i]);
            }
            ABBAumentado<Integer, Integer> arbol=indice.getArbol();
            TablaEncadenada<Integer, ABBAumentado.Nodo<Integer, Integer>> tabla=indice.getTabla();
            int m = tabla.capacidad();
            arbol.reiniciarVisitas();
            for (int i=0; i<N; i++) {
                arbol.obtener(claves[i]);
            }
            long visABB=arbol.visitas();
            tabla.reiniciarSondas();
            for (int i=0; i<N; i++) {
                indice.obtener(claves[i]);
            }
            long sondasHash=tabla.sondas();
            double alpha=tabla.factorCarga();
            System.out.println(N + "          " +visABB + "             " +sondasHash + "            " +alpha+"                "+m);
        }
    }

    public static void experimento2() throws Exception {
        int[] valoresN={2000,4000,6000,8000,10000};
        int i;
        System.out.println();
        System.out.println("N                 alpha                  sondas/N");
        for (int N : valoresN) {
            TablaEncadenada<Integer, Integer> tabla=new TablaEncadenada<>(97, Double.POSITIVE_INFINITY);
            for (i=1; i<=N;i++) {
                tabla.insertar(i, i);
            }
            tabla.reiniciarSondas();
            for (i=1;i<=N;i++) {
                tabla.obtener(i);
            }
            long sondas=tabla.sondas();
            double alpha=tabla.factorCarga();
            double sondasPorN=(double)sondas/N;
            System.out.println(N + "        " +alpha + "           " +sondasPorN);
        }
    }
}