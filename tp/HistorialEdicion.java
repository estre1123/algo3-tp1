package tp;
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

import excepciones.PilaVaciaException;

public class HistorialEdicion {
	//para guardar lo que saco
    private PilaES<Comando> deshacer;
	//para guardar para volver
    private PilaES<Comando> rehacer;

    public HistorialEdicion() {
        deshacer = new PilaES<>();
        rehacer = new PilaES<>();
    }

    public void ejecutar(Comando c) {
		//ejecuta
        c.ejecutar();
		//guarda en deshacer
        deshacer.apilar(c);
		//hay que crear una lista nueva para rehacer
        rehacer = new PilaES<>();
    }

    public boolean deshacer() throws PilaVaciaException {
		//si la lista de deshacer esta vacia entonces retorna falso
        if (deshacer.estaVacia()) {
            return false;
        }
		//saca el ultimo de la pila
        Comando c = deshacer.desapilar();
		//hay que hacer lo contrario
		//como insertar A 	 borrar A
		// borrar A        insertar A
		//mover +2        mover-2
        c.deshacer();
		//y lo guardamos en la pila de rehacer
        rehacer.apilar(c);

        return true;
    }

    public boolean rehacer() throws PilaVaciaException {
		//si esta vacio no se puede hacer nada
        if (rehacer.estaVacia()) {
            return false;
        }
		//sacar lo ultimo que se guardo
        Comando c = rehacer.desapilar();
		// ejecutar de nuevo
        c.ejecutar();
		//y guardan en la pila de deshacer
        deshacer.apilar(c);

        return true;
    }

    public int sizeDeshacer() {
		//retorna cuantas veces se puede deshacer
        return deshacer.size();
    }

    public int sizeRehacer() {
		//retorna cuantas veces se puede rehacer
        return rehacer.size();
    }
}
