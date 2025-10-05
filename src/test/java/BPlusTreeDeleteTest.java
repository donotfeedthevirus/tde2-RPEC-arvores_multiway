// [Du] Testes para remoção com redistribuição e merge.
// public final class BPlusTreeDeleteTest {
//     private BPlusTreeDeleteTest() {
//     }
//
//     public static void main(String[] args) {
//         BPlusTree tree = new BPlusTree();
//         int[] keys = {10, 20, 30, 40, 50, 60, 70, 80};
//         int total = 8;
//         int index = 0;
//         while (index < total) {
//             int key = keys[index];
//             tree.insert(key, "v" + key);
//             index++;
//         }
//
//         tree.range(25, 65);
//
//         boolean removed40 = tree.remove(40);
//         boolean removed50 = tree.remove(50);
//         boolean removed40Again = tree.remove(40);
//         boolean removed10 = tree.remove(10);
//         boolean removed20 = tree.remove(20);
//
//         if (!removed40 || !removed50 || !removed10 || !removed20) {
//             System.out.println("FAIL remove existing keys");
//         }
//         if (removed40Again) {
//             System.out.println("FAIL removing missing key should return false");
//         }
//         if (tree.search(40) != null || tree.search(20) != null) {
//             System.out.println("FAIL search should return null after delete");
//         }
//
//         BPlusTree.BPTNode root = tree.getRoot();
//         if (root == null) {
//             System.out.println("FAIL tree should not be empty");
//             return;
//         }
//
//         while (!root.isLeaf) {
//             root = root.children[0];
//         }
//
//         int totalKeys = 0;
//         BPlusTree.BPTNode cursor = root;
//         while (cursor != null) {
//             totalKeys += cursor.keyCount;
//             cursor = cursor.next;
//         }
//
//         if (totalKeys != 4) {
//             System.out.println("FAIL expected 4 remaining keys, found " + totalKeys);
//         }
//
//         BPlusTree.BPTNode parent = tree.getRoot();
//         if (!parent.isLeaf && parent.childCount == 1) {
//             System.out.println("FAIL root should have colapsado para folha");
//         }
//         System.out.println("OK");
//     }
// }
