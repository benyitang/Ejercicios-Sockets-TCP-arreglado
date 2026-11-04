import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClientServer {
    private static final int PUERTO = 8080;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println("Servidor Multihilo listo en puerto " + PUERTO + "...");
            while (true) {
                Socket socket = server.accept();
                pool.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
