// [Tism-man] Estrutura base da B+ Tree sem dependências externas.
public final class BPlusTree {
    // [Tism-man] Constantes de ordem da árvore.
    static final int ORDER = 4;
    static final int MAX_KEYS = ORDER - 1;
    static final int MAX_CHILDREN = ORDER;
    static final int MIN_KEYS_LEAF = ORDER / 2;
    static final int MIN_CHILDREN = (ORDER + 1) / 2;
    static final boolean DEBUG = false;

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
