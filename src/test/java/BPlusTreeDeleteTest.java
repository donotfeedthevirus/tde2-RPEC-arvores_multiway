// [Du] Testes para remoção com redistribuição e merge.
// public final class BPlusTreeDeleteTest {
//     private BPlusTreeDeleteTest() {
//     }
//
//     public static void main(String[] args) {
//         BPlusTreeTestPrinter.banner("Remoção");
//         BPlusTree tree = new BPlusTree();
//         int[] keys = {10, 20, 30, 40, 50, 60, 70, 80};
//         BPlusTreeTestPrinter.step("inserir conjunto inicial de chaves");
//         int index = 0;
//         while (index < keys.length) {
//             int key = keys[index];
//             boolean inserido = tree.insert(key, "v" + key);
//             BPlusTreeTestPrinter.result("inserir(" + key + ") -> " + boolToText(inserido));
//             index++;
//         }
//         BPlusTreeTestPrinter.printTree(tree);
//         BPlusTreeTestPrinter.printLeafChain(tree);
//
//         BPlusTreeTestPrinter.step("range(25,65) para inspecionar segmento do meio");
//         tree.range(25, 65);
//
//         boolean ok = true;
//         ok &= logRemove(tree, 40, true);
//         ok &= logRemove(tree, 50, true);
//         ok &= logRemove(tree, 40, false);
//         ok &= logRemove(tree, 10, true);
//         ok &= logRemove(tree, 20, true);
//
//         if (tree.search(40) != null || tree.search(20) != null) {
//             System.out.println("[ERRO] buscas deveriam retornar nulo após remoção");
//             ok = false;
//         } else {
//             BPlusTreeTestPrinter.step("buscas confirmam ausência das chaves removidas");
//             BPlusTreeTestPrinter.result("buscar(40) -> nulo, buscar(20) -> nulo");
//         }
//
//         BPlusTreeTestPrinter.step("estrutura após remoções");
//         BPlusTreeTestPrinter.printTree(tree);
//         BPlusTreeTestPrinter.printLeafChain(tree);
//
//         int remaining = countKeys(tree);
//         if (remaining != 4) {
//             System.out.println("[ERRO] esperado 4 chaves restantes, encontrado " + remaining);
//             ok = false;
//         } else {
//             BPlusTreeTestPrinter.step("total de chaves remanescentes -> " + remaining);
//         }
//
//         BPlusTree.BPTNode root = tree.getRoot();
//         if (root == null) {
//             System.out.println("[ERRO] árvore não deveria estar vazia");
//             return;
//         }
//         if (!root.isLeaf && root.childCount == 1) {
//             System.out.println("[ERRO] raiz deveria ter colapsado para folha");
//             ok = false;
//         }
//
//         if (ok) {
//             System.out.println("[SUCESSO] remoções preservaram os invariantes");
//         }
//     }
//
//     private static boolean logRemove(BPlusTree tree, int key, boolean expected) {
//         BPlusTreeTestPrinter.step("remover(" + key + ") -> esperado " + boolToText(expected));
//         boolean result = tree.remove(key);
//         BPlusTreeTestPrinter.result("retornou " + boolToText(result));
//         BPlusTreeTestPrinter.printTree(tree);
//         BPlusTreeTestPrinter.printLeafChain(tree);
//         if (result != expected) {
//             System.out.println("[ERRO] remoção retornou valor inesperado para chave " + key);
//             return false;
//         }
//         return true;
//     }
//
//     private static int countKeys(BPlusTree tree) {
//         BPlusTree.BPTNode node = tree.getRoot();
//         if (node == null) {
//             return 0;
//         }
//         while (node != null && !node.isLeaf) {
//             node = node.children[0];
//         }
//         int total = 0;
//         while (node != null) {
//             total += node.keyCount;
//             node = node.next;
//         }
//         return total;
//     }
//
//     private static String boolToText(boolean value) {
//         return value ? "verdadeiro" : "falso";
//     }
// }
