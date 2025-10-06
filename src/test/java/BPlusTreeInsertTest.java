// Testes Inserts atualizados (Vini)
public final class BPlusTreeInsertTest {
    private BPlusTreeInsertTest() {
    }

    public static void main(String[] args) {
        BPlusTreeTestPrinter.banner("Inserção");
        BPlusTreeTestPrinter.step("criar árvore vazia");
        BPlusTree tree = new BPlusTree();
        BPlusTreeTestPrinter.result("árvore criada");

        logInsert(tree, 10, "dez", true);
        logInsert(tree, 20, "vinte", true);
        logInsert(tree, 15, "quinze", true);
        logInsert(tree, 20, "vinte novo", false);

        BPlusTreeTestPrinter.step("estrutura após inserções");
        BPlusTreeTestPrinter.printTree(tree);
        BPlusTreeTestPrinter.printLeafChain(tree);

        BPlusTree.BPTNode root = tree.getRoot();
        boolean ok = true;
        if (root == null || !root.isLeaf) {
            System.out.println("[ERRO] raiz deveria ser uma folha");
            ok = false;
        }
        if (!hasExpectedOrder(root)) {
            ok = false;
        }
        if (ok) {
            System.out.println("[SUCESSO] inserções comportaram-se como esperado");
        }
    }

    private static void logInsert(BPlusTree tree, int key, String value, boolean expected) {
        BPlusTreeTestPrinter.step("inserir(" + key + ",\"" + value + "\") -> esperado " + boolToText(expected));
        boolean result = tree.insert(key, value);
        BPlusTreeTestPrinter.result("retornou " + boolToText(result));
        if (result != expected) {
            System.out.println("[ERRO] retorno inesperado para chave " + key);
        }
        BPlusTreeTestPrinter.printTree(tree);
        BPlusTreeTestPrinter.printLeafChain(tree);
    }

    private static boolean hasExpectedOrder(BPlusTree.BPTNode root) {
        if (root == null) {
            System.out.println("[ERRO] raiz ausente");
            return false;
        }
        if (root.keyCount != 3) {
            System.out.println("[ERRO] esperado 3 chaves, encontrado " + root.keyCount);
            return false;
        }
        if (root.keys[0] != 10 || root.keys[1] != 15 || root.keys[2] != 20) {
            System.out.println("[ERRO] chaves fora da ordem indicada");
            return false;
        }
        if (!"vinte novo".equals(root.values[2])) {
            System.out.println("[ERRO] valor duplicado não foi sobrescrito");
            return false;
        }
        return true;
    }

    private static String boolToText(boolean value) {
        return value ? "verdadeiro" : "falso";
    }
}
