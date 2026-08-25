public class ComandoBorrar implements Comando {
	//variable
    private BufferGap<Character> buffer;
    private char caracterBorrado;

	//inicializa el comando con el buffer, es como que holdea su propia referencia al buffer
	//en vez de hacer buffer.borrar, le decimos al comando que se ejecute y ya lo que pasa detras no nos importa
    public ComandoBorrar(BufferGap<Character> buffer) {
        this.buffer = buffer;
    }

	//ejecuta el comando, obvio
    @Override
    public void ejecutar() {
        try {
			//elimina el caracter y lo guarda
            caracterBorrado = buffer.borrar();
        } catch (BufferVacioException e) {
            throw new RuntimeException(e);
        }
    }

	// si fue borrado algo, guarda en esta funcion
    @Override
    public void deshacer() {
        buffer.insertar(caracterBorrado);
    }
	// para obtener el caracter borrado
    @Override
    public String descripcion() {
        return "Borrar '" + caracterBorrado + "'";
    }
}
