public class ParserDemo {
    public static void main(String[] args) {
        String[] pruebas = {
            "CONNECT ana", "join #java", "MSG hola a todos", "QUIT",
            "", "   ", "VOLAR lejos", null
        };
        for (String p : pruebas) {
            System.out.printf("%-20s -> %s%n", "\"" + p + "\"", ProtocolParser.parse(p));
        }
    }
}
