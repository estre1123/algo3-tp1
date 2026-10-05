import java.io.RandomAccessFile;
import java.io.RandomAccessFile;
/*
P es el tamaño de la página en bytes.
M es el número maximo de hijos por nodo interno.
L es la cantidad maxima de elementos por nodo hoja.
header es 12 bytes, son  datos comunes asi que se resta a P y se divide entre 16 que serian el resto de datos de un nodo hoja para calcular L = (P - 12) / 16\

Para calcular M :
i es cantidad de claves por nodo interno, entonces i = M - 1
Cada nodo interno tiene 12 de header y 4 bytes por cada hijo, entonces el tamaño de un nodo interno es
12 + 4M + 8(M - 1) <= P
12 + 4M + 8M - 8 <= P
4 + 12M <= P
M = (P - 12) / 12
*/


public class ArbolBMas implements AutoCloseable{
	//header/cabecera
	private static final int MAGIC = 0x41454433;
	private static final int HEADER_SIZE = 12;
	private static final int VERSION = 1;

	private int P; //tamano pagina en bytes
	private int M; //maximo de hijos por nodo
	private int L; //maximo de elementos por nodo hoja

	private int raiz; //pagina raiz
	private int numPaginas; //numero de paginas
	private int niveles;

	private RandomAccessFile archivo;

    private ArbolBMas(RandomAccessFile archivo, int tamPagina){
        this.archivo = archivo;
        this.P = tamPagina;
        this.L = calcularL(tamPagina);
        this.M = calcularM(tamPagina);
    }

	private static int calcularL(int p) {
		return (p - HEADER_SIZE) / 16;
	}

	private static int calcularM(int p) {
		return (p - 4) / 12;
	}

	@Override
	public void close() throws Exception {
		archivo.close();
	}

}
