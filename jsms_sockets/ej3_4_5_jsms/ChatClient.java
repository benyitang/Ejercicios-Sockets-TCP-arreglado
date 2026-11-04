import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {

            Thread receptor = new Thread(() -> {
                try {
                    String linea;
                    while ((linea = serverIn.readLine()) != null) {
                        System.out.println("<< " + linea);
                    }
                } catch (IOException e) {

                }
                System.out.println("[Conexión cerrada por el servidor]");
            }, "receptor");
            receptor.setDaemon(true);
            receptor.start();

            Thread ping = new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(15000);
                        out.println("PING");
                    }
                } catch (InterruptedException ignored) { }
            }, "ping");
            ping.setDaemon(true);
            ping.start();

            System.out.println("Comandos: CONNECT <user> | JOIN <sala> | MSG <texto> | QUIT");

            String entrada;
            while ((entrada = consoleIn.readLine()) != null) {
                out.println(entrada);
                if ("QUIT".equalsIgnoreCase(entrada.trim())) {
                    receptor.join(2000);
                    break;
                }
                if (!receptor.isAlive()) break;
            }
        } catch (IOException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
