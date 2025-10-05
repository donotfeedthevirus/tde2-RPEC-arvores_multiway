// [Tism-man] Escrita via console padrão sem depender de StringBuilder.
public final class ConsoleLineWriter implements LineWriter {
    private final java.io.Console console;

    public ConsoleLineWriter(java.io.Console console) {
        this.console = console;
    }

    @Override
    public void print(String text) {
        console.printf("%s", text);
    }

    @Override
    public void println(String text) {
        console.printf("%s%n", text);
    }
}
