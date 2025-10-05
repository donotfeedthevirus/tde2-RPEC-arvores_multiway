// [Tism-man] CLI mínima controlando a B+ Tree.
public final class ApplicationCLI {
    private static final String PROMPT = "> ";
    private final BPlusTree tree;
    private final DatasetLoader loader;
    private final LineWriter writer;

    public ApplicationCLI(BPlusTree tree, DatasetLoader loader, LineWriter writer) {
        this.tree = tree;
        this.loader = loader;
        this.writer = writer;
    }

    public void run(LineReader reader) {
        boolean running = true;
        while (running) {
            writer.print(PROMPT);
            String line = reader.readLine();
            running = execute(line);
        }
    }

    public boolean execute(String rawLine) {
        if (rawLine == null) {
            return false;
        }
        String line = trim(rawLine);
        if (line.length() == 0) {
            return true;
        }

        if (matchesCommand(line, "exit")) {
            writer.println("BYE");
            return false;
        }
        if (matchesCommand(line, "help")) {
            showHelp();
            return true;
        }
        if (matchesCommand(line, "clear")) {
            tree.clear();
            writer.println("CLEAR -> OK");
            return true;
        }
        if (matchesCommand(line, "status")) {
            showStatus();
            return true;
        }
        if (matchesCommand(line, "load")) {
            handleLoad(line);
            return true;
        }
        if (matchesCommand(line, "insert")) {
            handleInsert(line);
            return true;
        }
        if (matchesCommand(line, "search")) {
            handleSearch(line);
            return true;
        }
        if (matchesCommand(line, "remove")) {
            handleRemove(line);
            return true;
        }
        if (matchesCommand(line, "range")) {
            handleRange(line);
            return true;
        }

        writer.println("ERROR comando desconhecido");
        return true;
    }

    private void handleLoad(String line) {
        String remainder = afterKeyword(line, "load");
        if (remainder.length() == 0) {
            writer.println("ERROR load requer caminho");
            return;
        }
        String path = trim(remainder);
        boolean ok = loader.load(path, tree, writer);
        if (!ok) {
            writer.println("LOAD " + path + " -> FAIL");
        }
    }

    private void handleInsert(String line) {
        String remainder = afterKeyword(line, "insert");
        if (remainder.length() == 0) {
            writer.println("ERROR insert requer argumentos");
            return;
        }
        IntParseResult first = new IntParseResult();
        if (!parseIntForward(remainder, 0, first)) {
            writer.println("ERROR insert chave invalida");
            return;
        }
        int next = skipSpaces(remainder, first.nextIndex);
        if (next >= remainder.length()) {
            writer.println("ERROR insert valor ausente");
            return;
        }
        if (remainder.charAt(next) != '"') {
            writer.println("ERROR insert valor deve estar entre aspas");
            return;
        }
        int closing = next + 1;
        int limit = remainder.length();
        while (closing < limit && remainder.charAt(closing) != '"') {
            closing++;
        }
        if (closing >= limit) {
            writer.println("ERROR insert aspas de fechamento ausentes");
            return;
        }
        String value = remainder.substring(next + 1, closing);
        int tail = skipSpaces(remainder, closing + 1);
        if (tail < remainder.length()) {
            writer.println("ERROR insert possui sufixo inesperado");
            return;
        }
        boolean inserted = tree.insert(first.value, value);
        if (inserted) {
            writer.println("INSERT " + first.value + " -> OK");
        } else {
            writer.println("INSERT " + first.value + " -> UPDATED");
        }
    }

    private void handleSearch(String line) {
        String remainder = afterKeyword(line, "search");
        if (remainder.length() == 0) {
            writer.println("ERROR search requer chave");
            return;
        }
        IntParseResult parsed = new IntParseResult();
        if (!parseIntForward(remainder, 0, parsed)) {
            writer.println("ERROR search chave invalida");
            return;
        }
        int tail = skipSpaces(remainder, parsed.nextIndex);
        if (tail < remainder.length()) {
            writer.println("ERROR search possui sufixo inesperado");
            return;
        }
        String value = tree.search(parsed.value);
        if (value == null) {
            writer.println("FOUND " + parsed.value + " -> null");
        } else {
            writer.println("FOUND " + parsed.value + " -> " + value);
        }
    }

    private void handleRemove(String line) {
        String remainder = afterKeyword(line, "remove");
        if (remainder.length() == 0) {
            writer.println("ERROR remove requer chave");
            return;
        }
        IntParseResult parsed = new IntParseResult();
        if (!parseIntForward(remainder, 0, parsed)) {
            writer.println("ERROR remove chave invalida");
            return;
        }
        int tail = skipSpaces(remainder, parsed.nextIndex);
        if (tail < remainder.length()) {
            writer.println("ERROR remove possui sufixo inesperado");
            return;
        }
        boolean removed = tree.remove(parsed.value);
        if (removed) {
            writer.println("REMOVE " + parsed.value + " -> OK");
        } else {
            writer.println("REMOVE " + parsed.value + " -> NOT FOUND");
        }
    }

    private void handleRange(String line) {
        String remainder = afterKeyword(line, "range");
        if (remainder.length() == 0) {
            writer.println("ERROR range requer limites");
            return;
        }
        IntParseResult first = new IntParseResult();
        if (!parseIntForward(remainder, 0, first)) {
            writer.println("ERROR range limite inicial invalido");
            return;
        }
        int secondIndex = skipSpaces(remainder, first.nextIndex);
        IntParseResult second = new IntParseResult();
        if (!parseIntForward(remainder, secondIndex, second)) {
            writer.println("ERROR range limite final invalido");
            return;
        }
        int tail = skipSpaces(remainder, second.nextIndex);
        if (tail < remainder.length()) {
            writer.println("ERROR range possui sufixo inesperado");
            return;
        }
        writer.println("RANGE " + first.value + ".." + second.value + ":");
        tree.range(first.value, second.value);
    }

    private void showHelp() {
        writer.println("Comandos:");
        writer.println("  load <arquivo>");
        writer.println("  insert <id> \"<valor>\"");
        writer.println("  search <id>");
        writer.println("  remove <id>");
        writer.println("  range <k1> <k2>");
        writer.println("  clear");
        writer.println("  status");
        writer.println("  exit");
    }

    private void showStatus() {
        BPlusTree.BPTNode root = tree.getRoot();
        if (root == null) {
            writer.println("STATUS: vazia");
            return;
        }
        writer.println("STATUS: raiz " + (root.isLeaf ? "folha" : "interna") + ", chaves=" + root.keyCount);
    }

    private boolean matchesCommand(String line, String keyword) {
        int commandLen = keyword.length();
        if (line.length() < commandLen) {
            return false;
        }
        int index = 0;
        while (index < commandLen) {
            if (line.charAt(index) != keyword.charAt(index)) {
                return false;
            }
            index++;
        }
        if (line.length() == commandLen) {
            return true;
        }
        char separator = line.charAt(commandLen);
        return separator == ' ' || separator == '\t';
    }

    private String afterKeyword(String line, String keyword) {
        int start = keyword.length();
        int trimmed = skipSpaces(line, start);
        if (trimmed >= line.length()) {
            return "";
        }
        return line.substring(trimmed);
    }

    private int skipSpaces(String text, int index) {
        int length = text.length();
        int cursor = index;
        while (cursor < length) {
            char ch = text.charAt(cursor);
            if (ch != ' ' && ch != '\t') {
                break;
            }
            cursor++;
        }
        return cursor;
    }

    private String trim(String text) {
        int length = text.length();
        int start = 0;
        while (start < length && text.charAt(start) <= ' ') {
            start++;
        }
        int end = length - 1;
        while (end >= start && text.charAt(end) <= ' ') {
            end--;
        }
        if (start > end) {
            return "";
        }
        return text.substring(start, end + 1);
    }

    private boolean parseIntForward(String text, int index, IntParseResult result) {
        int length = text.length();
        int cursor = skipSpaces(text, index);
        if (cursor >= length) {
            return false;
        }
        boolean negative = false;
        if (text.charAt(cursor) == '-') {
            negative = true;
            cursor++;
        }
        int startDigits = cursor;
        int value = 0;
        while (cursor < length) {
            char ch = text.charAt(cursor);
            if (ch < '0' || ch > '9') {
                break;
            }
            int digit = ch - '0';
            value = (value * 10) + digit;
            cursor++;
        }
        if (cursor == startDigits) {
            return false;
        }
        result.value = negative ? -value : value;
        result.nextIndex = cursor;
        return true;
    }

    private static final class IntParseResult {
        int value;
        int nextIndex;
    }

    public static void main(String[] args) {
        java.io.Console console = System.console();
        if (console == null) {
            System.out.println("Console indisponível. Use ApplicationCLI#execute em testes.");
            return;
        }
        BPlusTree tree = new BPlusTree();
        DatasetLoader loader = new PendingDatasetLoader();
        LineWriter writer = new ConsoleLineWriter(console);
        LineReader reader = new ConsoleLineReader(console);
        ApplicationCLI cli = new ApplicationCLI(tree, loader, writer);
        cli.run(reader);
    }
}
