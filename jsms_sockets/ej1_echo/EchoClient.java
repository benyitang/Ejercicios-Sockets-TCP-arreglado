import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 8080;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Conectado al servidor de eco en " + host + ":" + puerto);
            System.out.println("Escribe un mensaje (escribe 'CHAO' para salir):");

            String userInput;
            while ((userInput = consoleIn.readLine()) != null) {
                out.println(userInput);
                String response = serverIn.readLine();
                System.out.println("Respuesta del servidor -> " + response);

                if ("CHAO".equalsIgnoreCase(userInput)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el cliente de eco: " + e.getMessage());
        }
    }
}
