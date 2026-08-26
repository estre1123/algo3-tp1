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
	//esto hay que mirar que los desplazamientos los restaure a como estaban previamente, devolver el hueco a donde estaban implica sumar desplazamientos (cosa que tecnicamente NO esta pasando porque estamos volviendo sobre nuestros pasos)

	//actualizacion decidi dejar nomas el tema de los desplazamientos porque en verdad eso no es parte del funcionamiento del buffer, es solo una estadistica que se puede ver en el test, no es parte de la logica del buffer, asi que no hay que preocuparse por eso chill nomas

	//ah y se usa la cantidad de desplazamiento porque ya hay una funcion que mueve en base a eso, y asi puedo usar simplemente su negativo, reutilizo codigo y no gasto mas memoria en guardar indices y demas, ademas de que es mas facil de entender, si moviste 2 a la derecha, al deshacer, te moves dos a la izq
    @Override
    public void deshacer() {
        buffer.moverCursor(-delta);
    }

    @Override
    public String descripcion() {
        return "Mover cursor " + delta;
    }
}
