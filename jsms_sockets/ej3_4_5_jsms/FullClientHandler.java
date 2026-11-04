import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class FullClientHandler implements Runnable {
    private static final int TIMEOUT_MS = 30000;

    private final Socket socket;
    private final RoomManager roomManager;
    private PrintWriter out;
    private BufferedReader in;
    private volatile String username;
    private volatile String currentRoom = "#general";

    public FullClientHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(TIMEOUT_MS);
            socket.setKeepAlive(true);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            roomManager.registrarConexion(this);

            out.println("200 OK - Bienvenido al JSMS. Usa CONNECT <user>");

            String rawMessage;
            while ((rawMessage = in.readLine()) != null) {
                Command cmd = ProtocolParser.parse(rawMessage);

                switch (cmd.getTipo()) {
                    case "CONNECT":
                        manejarConnect(cmd.getPayload());
                        break;
                    case "JOIN":
                        manejarJoin(cmd.getPayload());
                        break;
                    case "MSG":
                        manejarMsg(cmd.getPayload());
                        break;
                    case "PING":
                        out.println("PONG");
                        break;
                    case "QUIT":
                        out.println("BYE");
                        return;
                    default:
                        out.println("401 ERROR: " + cmd.getPayload());
                        break;
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Timeout: cliente inactivo (" + nombre() + ").");
            enviarMensajeDirecto("408 TIMEOUT - Cerrando por inactividad");
        } catch (SocketException e) {
            System.err.println("Conexión interrumpida con " + nombre() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de I/O en la sesión: " + e.getMessage());
        } finally {
            roomManager.removerDeSalas(this);
            roomManager.removerUsuario(username);
            roomManager.removerConexion(this);
            cerrar();
            System.out.println("Sesión finalizada: " + nombre());
        }
    }

    private void manejarConnect(String nombre) {
        if (username != null) {
            out.println("401 ERROR: Ya estás conectado como " + username);
        } else if (nombre.isEmpty() || nombre.contains(" ")) {
            out.println("401 ERROR: Uso: CONNECT <usuario> (sin espacios)");
        } else if (!roomManager.registrarUsuario(nombre, this)) {
            out.println("401 ERROR: El usuario '" + nombre + "' ya está en uso");
        } else {
            username = nombre;
            roomManager.unirseASala(currentRoom, this);
            out.println("200 OK CONNECTED as " + username);
        }
    }

    private void manejarJoin(String sala) {
        if (username == null) {
            out.println("401 ERROR: Primero usa CONNECT <usuario>");
        } else if (sala.isEmpty() || sala.contains(" ")) {
            out.println("401 ERROR: Uso: JOIN <sala> (sin espacios)");
        } else {
            roomManager.salirDeSala(currentRoom, this);
            currentRoom = sala;
            roomManager.unirseASala(currentRoom, this);
            out.println("JOINED " + currentRoom);
        }
    }

    private void manejarMsg(String texto) {
        if (username == null) {
            out.println("401 ERROR: Primero usa CONNECT <usuario>");
        } else if (texto.isEmpty()) {
            out.println("401 ERROR: Uso: MSG <texto>");
        } else {
            String linea = "MSG " + username + ": " + texto;
            roomManager.broadcast(currentRoom, linea, this);
            out.println(linea);
        }
    }

    public void enviarMensajeDirecto(String mensaje) {
        PrintWriter w = out;
        if (w != null) {
            w.println(mensaje);
        }
    }

    public void cerrar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar socket: " + e.getMessage());
        }
    }

    private String nombre() {
        return username != null ? username : String.valueOf(socket.getRemoteSocketAddress());
    }
}
