public class ComandoInsertar implements Comando {

    private BufferGap<Character> buffer;
    private char caracter;

    public ComandoInsertar(BufferGap<Character> buffer, char caracter) {
        this.buffer = buffer;
        this.caracter = caracter;
    }

    @Override
    public void ejecutar() {
        buffer.insertar(caracter);
    }

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