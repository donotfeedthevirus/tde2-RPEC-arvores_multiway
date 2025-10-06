// [Tism-man] Teste simples para validar findLeaf e search.
public final class BPlusTreeSearchTest {
    private BPlusTreeSearchTest() {
    }

    public static void main(String[] args) {
        BPlusTreeTestPrinter.banner("Busca");
        BPlusTreeTestPrinter.step("preparar folha fixa com duas entradas");
        BPlusTree tree = new BPlusTree();
        BPlusTree.BPTNode leaf = new BPlusTree.BPTNode(true);
        leaf.keyCount = 2;
        leaf.valueCount = 2;
        leaf.keys[0] = 10;
        leaf.keys[1] = 20;
        leaf.values[0] = "dez";
        leaf.values[1] = "vinte";
        tree.setRoot(leaf);
        BPlusTreeTestPrinter.result("raiz substituída pela folha preparada");
        BPlusTreeTestPrinter.printTree(tree);

        boolean ok = true;
        ok &= logSearch(tree, 10, "dez");
        ok &= logSearch(tree, 15, null);
        ok &= logSearch(tree, 20, "vinte");

        if (ok) {
            System.out.println("[SUCESSO] buscas retornaram os resultados esperados");
        }
    }

    private static boolean logSearch(BPlusTree tree, int key, String expected) {
        BPlusTreeTestPrinter.step("buscar(" + key + ") -> esperado " + formatExpected(expected));
        String value = tree.search(key);
        BPlusTreeTestPrinter.result("retornou " + formatExpected(value));
        if (expected == null) {
            if (value != null) {
                System.out.println("[ERRO] esperado nulo para a chave " + key);
                return false;
            }
            return true;
        }
        if (!expected.equals(value)) {
            System.out.println("[ERRO] esperado " + expected + " mas encontrado " + value);
            return false;
        }
        return true;
    }

    private static String formatExpected(String value) {
        return value == null ? "nulo" : '"' + value + '"';
    }
}
