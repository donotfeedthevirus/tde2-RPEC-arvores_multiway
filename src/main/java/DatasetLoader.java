// [Tism-man] Contrato para carregamento de dados externos.
public interface DatasetLoader {
    boolean load(String path, BPlusTree tree, LineWriter writer);
}
