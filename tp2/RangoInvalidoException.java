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
public class RangoInvalidoException extends RuntimeException {
	public RangoInvalidoException(String mensaje) {
		super(mensaje);
	}
}
/**Excepción no chequeada porque un rango inválido representa un error de 
programación al construir los límites de la consulta. El código que llama 
a consultarRango debería asegurarse de que los límites sean válidos.**/