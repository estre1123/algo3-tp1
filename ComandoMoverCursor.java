public class ComandoMoverCursor implements Comando {
	//variable
    private BufferGap<Character> buffer;
	//para saber cuanto movio
    private int delta;

    public ComandoMoverCursor(BufferGap<Character> buffer, int delta) {
        this.buffer = buffer;
        this.delta = delta;
    }

    @Override
    public void ejecutar() {
		//llama al moverCursor del BufferGap
        buffer.moverCursor(delta);
    }
	//si se movio hacia la derecha 3 lugares entonces tiene que volver 3 lugares
    @Override
    public void deshacer() {
        buffer.moverCursor(-delta);
    }

    @Override
    public String descripcion() {
        return "Mover cursor " + delta;
    }
}