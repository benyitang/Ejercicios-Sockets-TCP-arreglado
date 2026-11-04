import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomManager {
    private final Map<String, List<FullClientHandler>> salas = new ConcurrentHashMap<>();
    private final Map<String, FullClientHandler> usuarios = new ConcurrentHashMap<>();
    private final Set<FullClientHandler> conexiones = ConcurrentHashMap.newKeySet();

    public void unirseASala(String nombreSala, FullClientHandler cliente) {
        List<FullClientHandler> miembros =
                salas.computeIfAbsent(nombreSala, k -> new CopyOnWriteArrayList<>());
        if (!miembros.contains(cliente)) {
            miembros.add(cliente);
        }
    }

    public void salirDeSala(String nombreSala, FullClientHandler cliente) {
        List<FullClientHandler> miembros = salas.get(nombreSala);
        if (miembros != null) {
            miembros.remove(cliente);
        }
    }

    public void removerDeSalas(FullClientHandler cliente) {
        for (List<FullClientHandler> miembros : salas.values()) {
            miembros.remove(cliente);
        }
    }

    public void broadcast(String nombreSala, String mensaje, FullClientHandler emisor) {
        List<FullClientHandler> miembros = salas.get(nombreSala);
        if (miembros != null) {
            for (FullClientHandler miembro : miembros) {
                if (miembro != emisor) {
                    miembro.enviarMensajeDirecto(mensaje);
                }
            }
        }
    }

    public boolean registrarUsuario(String nombre, FullClientHandler cliente) {
        return usuarios.putIfAbsent(nombre, cliente) == null;
    }

    public void removerUsuario(String nombre) {
        if (nombre != null) {
            usuarios.remove(nombre);
        }
    }

    public void registrarConexion(FullClientHandler cliente) { conexiones.add(cliente); }
    public void removerConexion(FullClientHandler cliente) { conexiones.remove(cliente); }

    public void notificarATodos(String mensaje) {
        for (FullClientHandler c : conexiones) {
            c.enviarMensajeDirecto(mensaje);
        }
    }

    public void cerrarTodas() {
        for (FullClientHandler c : conexiones) {
            c.cerrar();
        }
    }
}
