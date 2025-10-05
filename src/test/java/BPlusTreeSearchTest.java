// [Tism-man] Teste simples para validar findLeaf e search.
// public final class BPlusTreeSearchTest {
//     private BPlusTreeSearchTest() {
//     }
//
//     public static void main(String[] args) {
//         BPlusTree tree = new BPlusTree();
//         BPlusTree.BPTNode leaf = new BPlusTree.BPTNode(true);
//         leaf.keyCount = 2;
//         leaf.valueCount = 2;
//         leaf.keys[0] = 10;
//         leaf.keys[1] = 20;
//         leaf.values[0] = "dez";
//         leaf.values[1] = "vinte";
//         tree.setRoot(leaf);
//
//         String hit = tree.search(10);
//         String miss = tree.search(15);
//         String secondHit = tree.search(20);
//
//         if (!"dez".equals(hit)) {
//             System.out.println("FAIL search 10");
//         }
//         if (miss != null) {
//             System.out.println("FAIL search 15 should be null");
//         }
//         if (!"vinte".equals(secondHit)) {
//             System.out.println("FAIL search 20");
//         }
//         System.out.println("OK");
//     }
// }
