import java.io.IOException;
// Excepcion chequeada: el archivo externo puede tener un formato
// invalido aunque el programa se use correctamente, esto no podemos evitarlo nosotros
// Quien abre el indice debe manejar o propagar este error de E/S
public class FormatoInvalidoException extends IOException {
 	public FormatoInvalidoException(String message) {
 		super(message);
 	}
 }
