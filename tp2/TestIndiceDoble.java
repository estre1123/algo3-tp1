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
        System.out.println("FASE B");

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
}
