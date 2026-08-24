public class TestPila {

    public static void main(String[] args) {

        PilaES<String> pila = new PilaES<>();

        pila.apilar("A");
        pila.apilar("B");
        pila.apilar("C");

        System.out.println("Tope: " + pila.tope());
        System.out.println("Size: " + pila.size());

        System.out.println("Desapilar: " + pila.desapilar());
        System.out.println("Desapilar: " + pila.desapilar());

        System.out.println("Tope: " + pila.tope());
        System.out.println("Size: " + pila.size());
    }
}