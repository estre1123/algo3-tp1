import java.io.IOException;

public interface ConsumidorEntrada {
    void aceptar(long clave,long posicion)throws IOException;
}
