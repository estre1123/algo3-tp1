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
public class ClaveInexistenteException extends Exception {
	 public ClaveInexistenteException(String mensaje) {
		 super(mensaje);
	 }
 }
/**Excepción chequeada porque representa una situación que puede ocurrir 
normalmente durante el uso del programa y que el código que llama puede 
prever y manejar razonablemente. Por ejemplo, se puede intentar buscar, 
eliminar o consultar una clave que no se encuentra en la estructura.**/