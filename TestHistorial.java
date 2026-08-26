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

public class TestHistorial {
	public static void main(String[] args) {
		BufferGap<Character> buffer = new BufferGap<Character>();
		HistorialEdicion historial = new HistorialEdicion();

		System.out.printf("%-20s%-20s%-20s%-20s%-20s\n", "Nro Operacion", "Operacion", "Contenido", "desh", "reh");
		System.out.println("-----------------------------------------------------------------------------------");

		//basicamente dejar el buffer donde pide pero usando el historial
		buffer.insertar('H');
		buffer.insertar('o');
		buffer.insertar('l');
		buffer.insertar('a');

		buffer.moverCursor(-2);
		buffer.insertar('X');
		buffer.get(4);

		//comienza la prueba
		historial.ejecutar(new ComandoInsertar(buffer, '!'));
		tabla1("1", "Insertar '!'", buffer, historial);
		historial.ejecutar(new ComandoInsertar(buffer, '?'));
		tabla1("2", "Insertar '?'", buffer, historial);
		historial.deshacer();
		tabla1("3", "Deshacer -> true", buffer, historial);
		historial.deshacer();
		tabla1("4", "Deshacer -> true", buffer, historial);
		historial.rehacer();
		tabla1("5", "Rehacer -> true", buffer, historial);
		historial.ejecutar(new ComandoMoverCursor(buffer, -4));
		tabla1("6", "MoverCursor(-4)", buffer, historial);
		historial.rehacer();
		tabla1("7", "Rehacer -> false", buffer, historial);
		historial.deshacer();
		tabla1("8", "Deshacer -> true", buffer, historial);
		historial.ejecutar(new ComandoBorrar(buffer));
		tabla1("9", "Borrar()", buffer, historial);
		historial.deshacer();
		tabla1("10", "Deshacer -> true", buffer, historial);
		historial.deshacer();
		tabla1("11", "Deshacer -> true", buffer, historial);
		historial.deshacer();
		tabla1("12", "Deshacer -> false", buffer, historial);


	}

	private static void tabla1(String nroOp,String op, BufferGap<Character> bf, HistorialEdicion hs) {
		String bfs = bf.toString();
		System.out.printf("%-20s%-20s%-20s%-20s%-20s\n",nroOp, op, bfs, hs.sizeDeshacer(), hs.sizeRehacer());
	}
}
