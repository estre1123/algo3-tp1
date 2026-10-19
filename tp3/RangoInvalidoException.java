// Excepcion no chequeada: desde > hasta: es un error que quien lo llama debe proporcionar limites validos, no se puede evitar que el programa se use incorrectamente

public class RangoInvalidoException extends RuntimeException {

 	public RangoInvalidoException(String message) {
 		super(message);
 	}
 }
