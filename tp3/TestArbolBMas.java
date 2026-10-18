import java.io.IOException;

public class TestArbolBMas {
    public static void main(String[] args) throws IOException, RangoInvalidoException, TamPaginaInvalidoException {
		ArbolBMas abm =  ArbolBMas.crear("indice.dat", 60);
		faseA(abm);

    }
    public static void faseA(ArbolBMas abm ) throws IOException, RangoInvalidoException, TamPaginaInvalidoException {
		printFaseA("Arbol B+ vacio", abm);
		abm.insertar(50, 0500);
		abm.insertar(20, 200);
		abm.insertar(80, 800);
		printFaseA("Insertar 50, 20, 80", abm);
		abm.insertar(10, 100);
		abm.insertar(40, 400);
		printFaseA("Insertar 10 (raiz nueva), 40 (reenlace)", abm);


		//System.out.printf();
    }

	public static void printFaseA(String op, ArbolBMas abm) throws IOException {
		String formato = "%-30s %10d %10d %10d %s\n";
		System.out.printf(formato, op, abm.niveles(), abm.cantidadPaginas(), abm.buscar(0), abm.volcarArbol());
	}
}
