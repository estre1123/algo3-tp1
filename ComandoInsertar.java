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

public class ComandoInsertar implements Comando {

    private BufferGap<Character> buffer;
    private char caracter;

	//lo mismo, agarra su propia referencia al buffer
    public ComandoInsertar(BufferGap<Character> buffer, char caracter) {
        this.buffer = buffer;
        this.caracter = caracter;
    }


    @Override
    public void ejecutar() {
        buffer.insertar(caracter);
    }

    //su deshacer es quitar el caracter recien insertado, es masomenos lo contrario al de borrar (bastante similar la vd)
	@Override
    public void deshacer() {
        try {
            buffer.borrar();
        } catch (BufferVacioException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String descripcion() {
        return "Insertar '" + caracter + "'";
    }
}
