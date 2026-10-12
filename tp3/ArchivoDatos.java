import java.io.IOException;
import java.io.RandomAccessFile;

public class ArchivoDatos {

    private RandomAccessFile archivo;
    private long registrosLeidos = 0;
    private ArchivoDatos(RandomAccessFile archivo) {
        this.archivo = archivo;
    }

    public static ArchivoDatos crear(String ruta) throws IOException {
        //crea el archivo，rw es leer y escribir
        RandomAccessFile archivo = new RandomAccessFile(ruta, "rw");
        //vacia los datos del archivo y crea desde 0
        archivo.setLength(0);
        return new ArchivoDatos(archivo);
    }
    public static ArchivoDatos abrir(String ruta) throws IOException {
        //r es solo leer no se puede escribir 
        RandomAccessFile archivo = new RandomAccessFile(ruta, "r");
        return new ArchivoDatos(archivo);
    }
    public long agregar(long clave, String linea) throws IOException {
        //cambia el string a bytes y guarda en un arreglo
        byte[] bytes = linea.getBytes("UTF-8");
        //para encontrar y dps ir al final del archivo
        long pos = archivo.length();
        archivo.seek(pos);
        //escribir en el archivo
        archivo.writeLong(clave);
        archivo.writeInt(bytes.length);
        archivo.write(bytes);

        return pos;
    }
    public String leer(long pos) throws IOException {
        //ir a la posicion 
        archivo.seek(pos);
        //leer clave
        long clave = archivo.readLong();
        //leer la cantidad de bytes
        int largo = archivo.readInt();
        //crear el arreglo para guardar despues 
        byte[] bytes = new byte[largo];
        //esta funcion lee toda la cantidad de bytes del tamanio del arreglo y llena
        archivo.readFully(bytes);
        registrosLeidos++;
        //retornamos el string
        return new String(bytes, "UTF-8");
    }

    public long busquedaSecuencial(long clave) throws IOException {
        //nos vamos al inicio del archivo
        archivo.seek(0);
        //la primera funcion lee la cantidad de bytes que esta apuntando ahora
        while (archivo.getFilePointer() < archivo.length()) {
            //la cantidad de bytes que esta apuntando ahora
            long pos = archivo.getFilePointer();
            //leemos el clave de ahora
            long claveActual = archivo.readLong();
            //cantidad de bytes
            int largo = archivo.readInt();
            //se va a proximo clave
            archivo.seek(archivo.getFilePointer() + largo);
            registrosLeidos++;
            if (claveActual == clave) {
                return pos;
            }
        }

        return -1;
    }
    public long registrosLeidos() {
        return registrosLeidos;
    }

    public void reiniciarContadores() {
        registrosLeidos = 0;
    }

    public long tamano() throws IOException {
        return archivo.length();
    }

    public void close() throws IOException {
        archivo.close();
    }
}