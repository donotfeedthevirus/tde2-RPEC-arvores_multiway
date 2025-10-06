[Cebolinha] Teste para validar splits em cascata.
public final class BPlusTreeSplitTest {
    private BPlusTreeSplitTest() {
    }

    public static void main(String[] args) {
        BPlusTreeTestPrinter.banner("Divisão");
        BPlusTree tree = new BPlusTree();
        logInsert(tree, 10, "dez");
        logInsert(tree, 20, "vinte");
        logInsert(tree, 30, "trinta");
        logInsert(tree, 40, "quarenta");
        logInsert(tree, 50, "cinquenta");
        logInsert(tree, 60, "sessenta");

        BPlusTreeTestPrinter.step("estrutura final");
        BPlusTreeTestPrinter.printTree(tree);
        BPlusTreeTestPrinter.printLeafChain(tree);

        boolean ok = true;
        BPlusTree.BPTNode root = tree.getRoot();
        if (root == null || root.isLeaf) {
            System.out.println("[ERRO] raiz deveria ser interna após divisões em cascata");
            ok = false;
        }
        if (root != null && root.keyCount != 2) {
            System.out.println("[ERRO] esperado raiz com 2 chaves, encontrado " + (root == null ? 0 : root.keyCount));
            ok = false;
        }
        if (root != null) {
            ok &= checkLeafContent(root.children[0], 10, 20, "esquerda");
            ok &= checkLeafContent(root.children[1], 30, 40, "central");
            ok &= checkLeafContent(root.children[2], 50, 60, "direita");
            if (root.children[0] == null || root.children[1] == null || root.children[2] == null) {
                System.out.println("[ERRO] algum filho da raiz está ausente");
                ok = false;
            }
            if (root.children[0] != null && root.children[1] != null && root.children[2] != null) {
                if (root.children[0].next != root.children[1] || root.children[1].next != root.children[2]) {
                    System.out.println("[ERRO] encadeamento das folhas quebrado");
                    ok = false;
                }
            }
        }

        if (ok) {
            System.out.println("[SUCESSO] divisões produziram a estrutura esperada");
        }
    }

    private static void logInsert(BPlusTree tree, int key, String value) {
        BPlusTreeTestPrinter.step("inserir(" + key + ",\"" + value + "\")");
        boolean result = tree.insert(key, value);
        BPlusTreeTestPrinter.result("retornou " + boolToText(result));
        BPlusTreeTestPrinter.printTree(tree);
        BPlusTreeTestPrinter.printLeafChain(tree);
        if (!result) {
            System.out.println("[ERRO] duplicata inesperada para chave " + key);
        }
    }

    private static boolean checkLeafContent(BPlusTree.BPTNode leaf, int expectedFirst, int expectedSecond, String label) {
        if (leaf == null) {
            System.out.println("[ERRO] folha " + label + " ausente");
            return false;
        }
        if (!leaf.isLeaf) {
            System.out.println("[ERRO] nó " + label + " deveria ser folha");
            return false;
        }
        if (leaf.keyCount != 2) {
            System.out.println("[ERRO] folha " + label + " deveria ter 2 chaves, encontrou " + leaf.keyCount);
            return false;
        }
        if (leaf.keys[0] != expectedFirst || leaf.keys[1] != expectedSecond) {
            System.out.println("[ERRO] chaves da folha " + label + " não conferem");
            return false;
        }
        return true;
    }

    private static String boolToText(boolean value) {
        return value ? "verdadeiro" : "falso";
    }
}
