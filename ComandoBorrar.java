public class ComandoBorrar implements Comando {
	//variable
    private BufferGap<Character> buffer;
    private char caracterBorrado;
	
    public ComandoBorrar(BufferGap<Character> buffer) {
        this.buffer = buffer;
    }

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