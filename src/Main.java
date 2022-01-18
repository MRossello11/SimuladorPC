import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        //creacion objeto Ordenador
        Ordenador c1 = new Ordenador("PC", 4000, 100000);
        //se enciende el ordenador
        c1.encender();
    }
}