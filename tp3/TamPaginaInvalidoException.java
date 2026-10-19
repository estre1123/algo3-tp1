// Excepcion no chequeada: el tamano de pagina es un argumento
// invalido si no permite alojar la cabecera y las capacidades
// minimas exigidas, es culpa de quien llama, no de la estructura de datos

public class TamPaginaInvalidoException extends IllegalArgumentException {
	public TamPaginaInvalidoException(String message) {
		super(message);
	}

}
