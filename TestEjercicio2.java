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
