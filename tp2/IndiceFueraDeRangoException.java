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
He utilizado herramientas de Inteligencia Artificial únicamente para comprender el enunciado y aclarar conceptos, sin utilizar código generado por estas herramientas en nuestra solución.
*/

//extiende de runtimeExc porque es una excepcion que se da en tiempo de ejecucion, y no es necesario que el usuario la maneje
public class IndiceFueraDeRangoException extends RuntimeException {
	 public IndiceFueraDeRangoException(String mensaje) {
		 super(mensaje);
	 }
 }
/**Excepción no chequeada porque indica un error de programación del código 
que realiza la llamada. El valor de k debería haber sido calculado o 
validado correctamente antes de llamar a kEsimo.**/