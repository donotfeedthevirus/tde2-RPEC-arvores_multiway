// [Tism-man] Estrutura base da B+ Tree sem dependências externas.
public final class BPlusTree {
    // [Tism-man] Constantes e configuração principal.
    static final int ORDER = 4;
    static final int MAX_KEYS = ORDER - 1;
    static final int MAX_CHILDREN = ORDER;
    static final int MIN_KEYS_LEAF = ORDER / 2;
    static final int MIN_CHILDREN = (ORDER + 1) / 2;
    static final boolean DEBUG = false;

    // [Tism-man] Estado principal da árvore.
    private BPTNode root;

    public BPlusTree() {
        root = null;
    }

    public void clear() {
        root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public BPTNode getRoot() {
        return root;
    }

    // [Tism-man] Busca pelo nó folha que deve conter a chave.
    public BPTNode findLeaf(int key) {
        BPTNode current = root;
        if (current == null) {
            return null;
        }
        while (!current.isLeaf) {
            int childIndex = 0;
            while (childIndex < current.keyCount && key >= current.keys[childIndex]) {
                childIndex++;
            }
            current = current.children[childIndex];
        }
        return current;
    }

    // [Tism-man] Consulta linear dentro da folha encontrada.
    public String search(int key) {
        BPTNode leaf = findLeaf(key);
        if (leaf == null) {
            return null;
        }
        int index = 0;
        while (index < leaf.keyCount) {
            int probe = leaf.keys[index];
            if (probe == key) {
                return leaf.values[index];
            }
            if (probe > key) {
                return null;
            }
            index++;
        }
        return null;
    }

    private BPTNode createLeaf() {
        return new BPTNode(true);
    }

    private BPTNode createInternal() {
        return new BPTNode(false);
    }

    void setRoot(BPTNode node) {
        root = node;
        if (node != null) {
            node.parent = null;
        }
    }

    void printNode(BPTNode node) {
        if (!DEBUG || node == null) {
            return;
        }
        int index = 0;
        System.out.print(node.isLeaf ? "Leaf:" : "Internal:");
        while (index < node.keyCount) {
            System.out.print(" " + node.keys[index]);
            index++;
        }
        System.out.println();
    }

    private int findLeafPosition(BPTNode leaf, int key) {
        int index = 0;
        while (index < leaf.keyCount && leaf.keys[index] < key) {
            index++;
        }
        return index;
    }

    private void shiftRightLeaf(BPTNode leaf, int index) {
        int cursor = leaf.keyCount;
        while (cursor > index) {
            leaf.keys[cursor] = leaf.keys[cursor - 1];
            leaf.values[cursor] = leaf.values[cursor - 1];
            cursor--;
        }
    }

    private void shiftLeftLeaf(BPTNode leaf, int index) {
        int cursor = index;
        while (cursor + 1 < leaf.keyCount) {
            leaf.keys[cursor] = leaf.keys[cursor + 1];
            leaf.values[cursor] = leaf.values[cursor + 1];
            cursor++;
        }
        if (leaf.keyCount > 0) {
            int eraseIndex = leaf.keyCount - 1;
            leaf.keys[eraseIndex] = 0;
            leaf.values[eraseIndex] = null;
        }
    }

    private void writeLeafEntry(BPTNode leaf, int index, int key, String value) {
        leaf.keys[index] = key;
        leaf.values[index] = value;
    }

    private boolean insertIntoFullLeaf(BPTNode leaf, int key, String value) {
        // TODO [Cebolinha] dividir folha e promover nova chave.
        if (DEBUG) {
            System.out.println("WARN overflow pendente em folha");
        }
        return false;
    }

    // [NeoVini] Inserção básica em folha sem tratar overflow.
    public boolean insert(int key, String value) {
        if (root == null) {
            BPTNode leaf = createLeaf();
            writeLeafEntry(leaf, 0, key, value);
            leaf.keyCount = 1;
            leaf.valueCount = 1;
            setRoot(leaf);
            return true;
        }

        BPTNode leaf = findLeaf(key);
        int position = findLeafPosition(leaf, key);

        if (position < leaf.keyCount && leaf.keys[position] == key) {
            leaf.values[position] = value;
            return false;
        }

        if (leaf.keyCount < MAX_KEYS) {
            shiftRightLeaf(leaf, position);
            writeLeafEntry(leaf, position, key, value);
            leaf.keyCount++;
            leaf.valueCount++;
            return true;
        }

        return insertIntoFullLeaf(leaf, key, value);
    }

    // [Tism-man] Nó básico com contadores manuais e ponteiros necessários.
    static final class BPTNode {
        boolean isLeaf;
        int[] keys = new int[MAX_KEYS];
        int keyCount;

        BPTNode[] children = new BPTNode[MAX_CHILDREN];
        int childCount;

        String[] values = new String[MAX_KEYS];
        int valueCount;

        BPTNode parent;
        BPTNode next;

        BPTNode(boolean leaf) {
            isLeaf = leaf;
            keyCount = 0;
            valueCount = 0;
            childCount = 0;
            parent = null;
            next = null;
        }
    }
}
