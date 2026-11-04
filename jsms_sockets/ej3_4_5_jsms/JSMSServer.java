import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JSMSServer {
    private static final int PUERTO = 8080;
    private static final int MAX_HILOS = 20;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_HILOS);
        RoomManager roomManager = new RoomManager();

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nIniciando apagado controlado del servidor...");
                roomManager.notificarATodos("500 SERVER SHUTDOWN - El servidor se está apagando");
                try {
                    serverSocket.close();
                } catch (IOException ignored) { }
                roomManager.cerrarTodas();
                pool.shutdown();
                try {
                    if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                        pool.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    pool.shutdownNow();
                }
                System.out.println("Servidor apagado.");
            }));

            System.out.println("Servidor JSMS escuchando en el puerto " + PUERTO + " (Ctrl+C para apagar)");

            while (!serverSocket.isClosed()) {
                try {
                    Socket cliente = serverSocket.accept();
                    pool.execute(new FullClientHandler(cliente, roomManager));
                } catch (IOException e) {
                    if (serverSocket.isClosed()) break;
                    System.err.println("Error aceptando cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor en el puerto " + PUERTO + ": " + e.getMessage());
            pool.shutdownNow();
        }
    }
}
