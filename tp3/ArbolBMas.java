import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
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
M = (P - 4) / 12
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
	private long cantClaves;

	private int cantPagEscritas;
	private int cantPagLeidas;

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

	public static ArbolBMas crear(String ruta, int tamPagina) throws IOException, TamPaginaInvalidoException {
		if (tamPagina < 40 || calcularL(tamPagina) < 2 || calcularM(tamPagina) < 3) {
			throw new TamPaginaInvalidoException("El tamaño ingresado es invalido.");
		}
		//crear un archivo binario para el arbol b+ con el tamano de pagina especificado
		RandomAccessFile archivo = new RandomAccessFile(ruta, "rw");
		try {
			//ressetear el archivo
			archivo.setLength(0);
			ArbolBMas arbol = new ArbolBMas(archivo, tamPagina);
			arbol.raiz = 1;
			arbol.numPaginas = 2;
			arbol.niveles = 1;
			arbol.cantClaves = 0;
			arbol.cantPagEscritas = 0;
			arbol.cantPagLeidas = 0;

			arbol.escribirHeader();
			arbol.escribirRaizVacia();
			return arbol;

		} catch (IOException e) { //si falla se tira a la basurota
			archivo.close();
			throw e;
		}
	}

	private void escribirHeader() throws IOException {
		//crear un buffer de bytes para escribir el header, el buffer es como un arreglo de bytes que se puede escribir en el archivo
		ByteBuffer buffer = ByteBuffer.allocate(P);
		buffer.putInt(MAGIC);
		buffer.putInt(VERSION);
		buffer.putInt(P);
		buffer.putInt(M);
		buffer.putInt(L);
		buffer.putInt(raiz);
		buffer.putInt(numPaginas);
		buffer.putInt(niveles);
		buffer.putLong(cantClaves);

		//escribir el buffer en el archivo
		archivo.seek(0);
		archivo.write(buffer.array());
	}
	//direccion de una pagina es: (long) numeroPagina * P
	private void escribirRaizVacia() throws IOException {
    // 1. Crear un ByteBuffer de P bytes.
	ByteBuffer buffer = ByteBuffer.allocate(P);
	buffer.putInt(0);
	buffer.putInt(0);
	buffer.putInt(-1);
	// Mover el cursor del archivo al comienzo de la pagina 1
	archivo.seek(P);
	archivo.write(buffer.array());
    // 4. Escribir el buffer completo.
	}
	//IMPORTANTE PARA MI: cada lectura avanza el cursor del archivo, por lo que no es necesario moverlo manualmente
	public static ArbolBMas abrir(String ruta) throws IOException {

		//verificar que el archivo existe
		if (!new java.io.File(ruta).isFile()) {
    		throw new FormatoInvalidoException("No existe el archivo del indice.");
		}
		//abrir el archivo binario para el arbol b+
		RandomAccessFile archivo = new RandomAccessFile(ruta, "rw");

		try {
			//verificar que el archivo tiene al menos 40 bytes (del header)
			if (archivo.length() < 40) {
				throw new FormatoInvalidoException("El archivo no tiene una cabezera valida");
			}
			int magic = archivo.readInt();
			int version = archivo.readInt();

			//verificar que el archivo tiene el formato correcto, si no, lanzar una excepcion
			if (magic != MAGIC || version != VERSION) {
				throw new FormatoInvalidoException("El formato del archivo es invalido.");
			}

			//verificar que el tamano de pagina es valido
			int newP = archivo.readInt();
			if (newP < 40 || calcularL(newP) < 2 || calcularM(newP) < 3) {
				throw new FormatoInvalidoException("El tamaño de pagina es invalido.");
			}
			int newM = archivo.readInt();
			int newL = archivo.readInt();

			//Verificar que M y L correspondan a la P leida
			if (newM != calcularM(newP) || newL != calcularL(newP)) {
				throw new FormatoInvalidoException("M y L no corresponden a P.");
			}

			int newRaiz = archivo.readInt();
			int newNumPaginas = archivo.readInt();
			int newNiveles = archivo.readInt();
			long newCantClaves = archivo.readLong();

			//revisar longitud del archivo
			if (archivo.length() != (long) newNumPaginas * newP) {
				throw new FormatoInvalidoException("El tamano del archivo no corresponde a la cantidad de paginas y el tamano de pagina leido");
			}
			ArbolBMas arbol = new ArbolBMas(archivo, newP);
			arbol.raiz = newRaiz;
			arbol.numPaginas = newNumPaginas;
			arbol.niveles = newNiveles;
			arbol.cantClaves = newCantClaves;
			return arbol;

		} catch (IOException e) {
			archivo.close();
			throw e;
		}
	}


	@Override
	public void close() throws IOException {
		try {
			escribirHeader();
		} finally {
			archivo.close();
		}
	}
}
