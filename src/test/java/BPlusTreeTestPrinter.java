public final class BPlusTreeTestPrinter {
    private BPlusTreeTestPrinter() {
    }

    static void banner(String title) {
        System.out.println();
        System.out.println("================================");
        System.out.println(">>> Cenário: " + title);
        System.out.println("================================");
    }

    static void step(String message) {
        System.out.println("   → " + message);
    }

    static void result(String message) {
        System.out.println("     ↳ " + message);
    }

    static void printTree(BPlusTree tree) {
        BPlusTree.BPTNode root = tree.getRoot();
        if (root == null) {
            System.out.println("   [Árvore] vazia");
            System.out.println();
            return;
        }
        if (root.isLeaf) {
            System.out.println("   [Árvore] raiz folha " + formatNode(root));
            System.out.println();
            return;
        }
        System.out.println("   [Árvore] raiz " + formatNode(root));
        int index = 0;
        while (index < root.childCount) {
            BPlusTree.BPTNode child = root.children[index];
            System.out.println("      filho " + index + " " + formatNode(child));
            index++;
        }
        System.out.println();
    }

    static void printLeafChain(BPlusTree tree) {
        BPlusTree.BPTNode node = tree.getRoot();
        if (node == null) {
            System.out.println("   [Folhas] vazio");
            System.out.println();
            return;
        }
        while (node != null && !node.isLeaf) {
            node = node.children[0];
        }
        if (node == null) {
            System.out.println("   [Folhas] nenhuma folha");
            System.out.println();
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("   [Folhas]");
        while (node != null) {
            builder.append(" → ").append(formatNode(node));
            node = node.next;
        }
        System.out.println(builder);
        System.out.println();
    }

    private static String formatNode(BPlusTree.BPTNode node) {
        if (node == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(node.isLeaf ? "Folha" : "Interno");
        builder.append(" chaves=");
        builder.append('[');
        int index = 0;
        while (index < node.keyCount) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(node.keys[index]);
            index++;
        }
        builder.append(']');
        if (node.isLeaf) {
            builder.append(" valores=");
            builder.append('[');
            index = 0;
            while (index < node.valueCount) {
                if (index > 0) {
                    builder.append(',');
                }
                builder.append(node.values[index]);
                index++;
            }
            builder.append(']');
            builder.append(" prox=");
            builder.append(node.next == null ? "não" : "sim");
        } else {
            builder.append(" filhos=");
            builder.append('[');
            index = 0;
            while (index < node.childCount) {
                if (index > 0) {
                    builder.append(',');
                }
                if (node.children[index] == null) {
                    builder.append("null");
                } else {
                    builder.append(node.children[index].isLeaf ? "Folha" : "Interno");
                }
                index++;
            }
            builder.append(']');
        }
        return builder.toString();
    }
}
