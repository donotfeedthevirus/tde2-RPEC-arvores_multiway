// [Tism-man] Implementação baseada em java.io.Console para evitar uso de exceções.
public final class ConsoleLineReader implements LineReader {
    private final java.io.Console console;

    public ConsoleLineReader(java.io.Console console) {
        this.console = console;
    }

    @Override
    public String readLine() {
        return console.readLine();
    }
}
