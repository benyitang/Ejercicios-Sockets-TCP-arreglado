import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("Cliente conectado desde: " + socket.getRemoteSocketAddress());
            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                if ("CHAO".equalsIgnoreCase(mensaje)) {
                    out.println("Conexión cerrada por el servidor.");
                    break;
                }
                out.println("ECO MULTIHILO: " + mensaje);
            }
        } catch (IOException e) {
            System.err.println("Error manejando al cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket: " + e.getMessage());
            }
            System.out.println("Cliente desconectado: " + socket.getRemoteSocketAddress());
        }
    }
}
