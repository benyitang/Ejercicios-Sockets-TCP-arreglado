public class Command {
    private final String tipo;
    private final String payload;

    public Command(String tipo, String payload) {
        this.tipo = tipo;
        this.payload = payload;
    }

    public String getTipo() { return tipo; }
    public String getPayload() { return payload; }

    @Override
    public String toString() { return tipo + "[" + payload + "]"; }
}
