// [Cebolinha] Teste para validar splits em cascata.
public final class BPlusTreeSplitTest {
    private BPlusTreeSplitTest() {
    }

    public static void main(String[] args) {
        BPlusTree tree = new BPlusTree();
        tree.insert(10, "dez");
        tree.insert(20, "vinte");
        tree.insert(30, "trinta");
        tree.insert(40, "quarenta");
        tree.insert(50, "cinquenta");
        tree.insert(60, "sessenta");

        BPlusTree.BPTNode root = tree.getRoot();
        if (root == null || root.isLeaf) {
            System.out.println("FAIL root should be internal");
        }
        if (root.keyCount != 2) {
            System.out.println("FAIL root should have 2 keys");
        }
        if (root.children[0] == null || root.children[1] == null || root.children[2] == null) {
            System.out.println("FAIL root children missing");
        }
        if (root.children[0].keys[0] != 10 || root.children[0].keys[1] != 20) {
            System.out.println("FAIL left leaf content");
        }
        if (root.children[1].keys[0] != 30 || root.children[1].keys[1] != 40) {
            System.out.println("FAIL middle leaf content");
        }
        if (root.children[2].keys[0] != 50 || root.children[2].keys[1] != 60) {
            System.out.println("FAIL right leaf content");
        }
        if (root.children[0].next != root.children[1] || root.children[1].next != root.children[2]) {
            System.out.println("FAIL leaf linkage");
        }
        System.out.println("OK");
    }
}
