
//Inserindo teste para validar o insert - (Vini)

public final class BPlusTreeInsertTest {
    private BPlusTreeInsertTest() {
    }

    public static void main(String[] args) {
        BPlusTree tree = new BPlusTree();
        boolean first = tree.insert(10, "dez");
        boolean second = tree.insert(20, "vinte");
        boolean third = tree.insert(15, "quinze");
        boolean duplicate = tree.insert(20, "vinte novo");

        BPlusTree.BPTNode root = tree.getRoot();
        if (root == null || !root.isLeaf) {
            System.out.println("FAIL root should be leaf");
        }
        if (!first || !second || !third) {
            System.out.println("FAIL insert return true for new keys");
        }
        if (duplicate) {
            System.out.println("FAIL duplicate should return false");
        }
        if (root.keyCount != 3) {
            System.out.println("FAIL expected 3 keys");
        }
        if (root.keys[0] != 10 || root.keys[1] != 15 || root.keys[2] != 20) {
            System.out.println("FAIL order mismatch");
        }
        if (!"vinte novo".equals(root.values[2])) {
            System.out.println("FAIL duplicate override");
        }
        System.out.println("OK");
    }
}
