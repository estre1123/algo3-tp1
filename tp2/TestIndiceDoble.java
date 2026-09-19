public class TestIndiceDoble {
    public static void main(String[] args) throws Exception{
        IndiceDoble<Integer, String> indice = new IndiceDoble<>();
        ABBAumentado<Integer, String> arbol = indice.getArbol();
        TablaEncadenada<Integer, ABBAumentado.Nodo<Integer, String>> tabla=indice.getTabla();
        System.out.println("FASE A");
        indice.agregar(50, "P50");
        indice.agregar(30, "P30");
        indice.agregar(70, "P70");
        indice.agregar(20, "P20");
        indice.agregar(40, "P40");
        indice.agregar(60, "P60");
        indice.agregar(80, "P80");
        indice.agregar(35, "P35");
        indice.agregar(65, "P65");
        imprimirEstado(indice, arbol, tabla);
        System.out.println();
        System.out.println("FASE B");
        indice.agregar(61, "P61");
        indice.agregar(41, "P41");
        imprimirEstado(indice, arbol, tabla);
        System.out.println();
        System.out.println("FASE C");
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
        System.out.println("FASE D");
        indice.eliminar(30);
        imprimirEstado(indice, arbol, tabla);
        System.out.println();
        System.out.println("indice.obtener(35) = " + indice.obtener(35));
        System.out.println("indice.kEsimo(2) = " + indice.kEsimo(2));
        System.out.println();
        System.out.println("indice.contiene(30) = " + contieneIndice(indice, 30));
        System.out.println("arbol.contiene(30) = " + arbol.contiene(30));
        System.out.println();
    }

    public static void imprimirEstado(
            IndiceDoble<Integer, String> indice,
            ABBAumentado<Integer, String> arbol,
            TablaEncadenada<Integer, ABBAumentado.Nodo<Integer, String>> tabla) {
        System.out.println();
        System.out.println("TABLA:");
        tabla.dump();
        System.out.println();
        System.out.println("ARBOL:");
        System.out.println(arbol.toString());
        System.out.println("size arbol = " + arbol.size());
        System.out.println("size tabla = " + tabla.size());
        System.out.println("visitas arbol = " + arbol.visitas());
        System.out.println("sondas tabla = " + tabla.sondas());
    }
    public static boolean contieneIndice(
            IndiceDoble<Integer, String> indice,
            Integer clave) {
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