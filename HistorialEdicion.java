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

    public boolean deshacer() {
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

    public boolean rehacer() {
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