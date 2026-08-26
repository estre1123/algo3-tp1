public class ComandoInsertar implements Comando {

    private BufferGap<Character> buffer;
    private char caracter;

	//lo mismo, agarra su propia referencia al buffer
    public ComandoInsertar(BufferGap<Character> buffer, char caracter) {
        this.buffer = buffer;
        this.caracter = caracter;
    }


    @Override
    public void ejecutar() {
        buffer.insertar(caracter);
    }

    //su deshacer es quitar el caracter recien insertado, es masomenos lo contrario al de borrar (bastante similar la vd)
	@Override
    public void deshacer() {
        try {
            buffer.borrar();
        } catch (BufferVacioException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String descripcion() {
        return "Insertar '" + caracter + "'";
    }
}
