public class ProtocolParser {
    public static Command parse(String rawInput) {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            return new Command("ERROR", "Comando vacío");
        }

        String[] partes = rawInput.trim().split("\\s+", 2);
        String comando = partes[0].toUpperCase();
        String argumento = partes.length > 1 ? partes[1].trim() : "";

        switch (comando) {
            case "CONNECT":
            case "JOIN":
            case "MSG":
            case "QUIT":
            case "PING":
                return new Command(comando, argumento);
            default:
                return new Command("ERROR", "Comando desconocido: " + comando);
        }
    }
}
