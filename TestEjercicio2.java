public class TestEjercicio2 {

    public static void main(String[] args) {
        BufferGap<Character> buffer = new BufferGap<>();
        HistorialEdicion historial = new HistorialEdicion();

        System.out.println("=== PRUEBA 1: Operaciones de insercion ===");
        historial.ejecutar(new ComandoInsertar(buffer, 'H'));
        historial.ejecutar(new ComandoInsertar(buffer, 'o'));
        historial.ejecutar(new ComandoInsertar(buffer, 'l'));
        historial.ejecutar(new ComandoInsertar(buffer, 'a'));
        System.out.println("Contenido actual: " + buffer);
        System.out.println("Acciones deshacer disponibles: " + historial.sizeDeshacer());

        System.out.println("\n=== PRUEBA 2: Mover cursor y borrar ===");
        historial.ejecutar(new ComandoMoverCursor(buffer, -1));
        historial.ejecutar(new ComandoBorrar(buffer));
        System.out.println("Contenido actual: " + buffer);

        System.out.println("\n=== PRUEBA 3: Prueba de Deshacer (Undo) ===");
        historial.deshacer();
        System.out.println("Tras deshacer 1 vez (restaurar caracter): " + buffer);

        historial.deshacer();
        historial.deshacer();
        System.out.println("Tras deshacer 3 veces: " + buffer);

        System.out.println("\n=== PRUEBA 4: Prueba de Rehacer (Redo) ===");
        historial.rehacer();
        System.out.println("Tras rehacer 1 vez: " + buffer);
    }
}