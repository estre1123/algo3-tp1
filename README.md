grupo:g_tq6  Nro tarea: TP1
Integrantes:
Espinola Aguero,Anibal Julian  CIC=6362752  SECCION=TQ
Hsu Yang, Estrella  CIC=5837842  SECCION=TQ
Nosotros, Anibal Julian Espinola Aguero y Estrella Hsu Yang:
No hemos discutido el código fuente de nuestra tarea con ningún otro grupo, solo con el Profesor o el AER.
No hemos usado código obtenido de otro estudiante o de cualquier otra fuente no autorizada, modificada o no modificada.
Cualquier código o documentación utilizada en nuestro programa obtenido de fuentes, tales como libros o notas de curso, ha sido claramente indicada en nuestra tarea.

DECISIONES DE DISEÑO:
BufferGap se instancia SIN parametros, y con un tamaño inicial de 16 caracteres. El tamaño del buffer se duplica cuando se necesita más espacio.
Al duplicar el tamaño del buffer, el contenido se copia al nuevo buffer y el cursor se mantiene en la misma posición relativa al contenido, es decir, el inicio del hueco se mantiene en la misma posición relativa al contenido, y el fin del hueco se ajusta para reflejar el nuevo tamaño del buffer, mandando todos los datos del anterior buffer al final del nuevo buffer: ESTO LO HICIMOS porque nos permite mantener el flujo de insercion de datos sin tener que moverse constantemente entre buffers, inspirado en como funciona un bufferGap en un editor de texto normal y corriente.

En MOVERCURSOR, primero se desplazan los datos a sus nuevas posiciones, y al final se actualizan las referencias a las que apuntan tanto inicioHueco y finHueco, esto para no ir haciendolo en el bucle (supongase un incremento de recursos despreciable pero nunca innecesario)

Con respecto al COMANDOMOVERCURSOR, decidimos almacenar el valor de DELTA (cantidad de desplazamiento), esto para aprovechar la funcion de moverCursor que ya es propia del codigo, simplifica el cambio y no requiere gastar mas recursos en un ciclo o mas variables (porque ya tenemos una funcion que se encarga de eso)

Tabla 1

Operación	Contenido	inicioHueco	finHueco	Capacidad	Desplaz.
Inicio			""			0			16			16			0
Insertar 'H'	H			1			16			16			0
Insertar 'o'	Ho			2			16			16			0
Insertar 'l'	Hol			3			16			16			0
Insertar 'a'	Hola		4			16			16			0
moverCursor(-2)	Hola		2			14			16			2
insertar('X')	HoXla		3			14			16			2
get(4)			HoXla		3			14			16			2
borrar()		Hola		2			14			16			2


Tabla 1.2`

N        Desplazamientos BufferGap       Desplazamientos arreglo simple
--       -------------------------       ------------------------------
100000                          0                               500000000
200000                          0                               1000000000
300000                          0                               1500000000
400000                          0                               2000000000
500000                          0                               2500000000
600000                          0                               3000000000
700000                          0                               3500000000
800000                          0                               4000000000
900000                          0                               4500000000
1000000                         0                               5000000000
