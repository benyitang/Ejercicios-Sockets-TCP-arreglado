import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public static void main(String[] args) {
        int puerto = 8080;
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor de eco iniciado en puerto " + puerto);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Cliente conectado: " + clientSocket.getRemoteSocketAddress());
                    String mensaje;
                    while ((mensaje = in.readLine()) != null) {
                        if ("CHAO".equalsIgnoreCase(mensaje)) {
                            out.println("Hasta luego!");
                            break;
                        }
                        out.println("ECO: " + mensaje);
                    }
                    System.out.println("Cliente desconectado.");
                } catch (IOException e) {
                    System.err.println("Error con el cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}
