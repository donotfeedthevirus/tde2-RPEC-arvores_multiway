// [Tism-man] Loader temporário até a implementação do CSV.
public final class PendingDatasetLoader implements DatasetLoader {
    @Override
    public boolean load(String path, BPlusTree tree, LineWriter writer) {
        writer.println("LOAD " + path + " -> TODO (implementar loader)");
        return false;
    }
}
