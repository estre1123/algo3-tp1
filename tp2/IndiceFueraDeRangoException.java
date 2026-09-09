//extiende de runtimeExc porque es una excepcion que se da en tiempo de ejecucion, y no es necesario que el usuario la maneje
public class IndiceFueraDeRangoException extends RuntimeException {
	 public IndiceFueraDeRangoException(String mensaje) {
		 super(mensaje);
	 }
 }
