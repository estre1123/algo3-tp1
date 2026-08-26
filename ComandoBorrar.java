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

public class ComandoBorrar implements Comando {
	//variable
    private BufferGap<Character> buffer;
    private char caracterBorrado;

	//inicializa el comando con el buffer, es como que holdea su propia referencia al buffer
	//en vez de hacer buffer.borrar, le decimos al comando que se ejecute y ya lo que pasa detras no nos importa
    public ComandoBorrar(BufferGap<Character> buffer) {
        this.buffer = buffer;
    }

	//ejecuta el comando, obvio
    @Override
    public void ejecutar() {
        try {
			//elimina el caracter y lo guarda
            caracterBorrado = buffer.borrar();
        } catch (BufferVacioException e) {
            throw new RuntimeException(e);
        }
    }

	// si fue borrado algo, guarda en esta funcion
    @Override
    public void deshacer() {
        buffer.insertar(caracterBorrado);
    }
	// para obtener el caracter borrado
    @Override
    public String descripcion() {
        return "Borrar '" + caracterBorrado + "'";
    }
}
