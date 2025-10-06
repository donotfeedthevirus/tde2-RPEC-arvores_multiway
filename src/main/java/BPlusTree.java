public final class BPlusTree {
    static final int ORDER = 4;
    static final int MAX_KEYS = ORDER - 1;
    static final int MAX_CHILDREN = ORDER;
    static final int MIN_KEYS_LEAF = ORDER / 2;
    static final int MIN_CHILDREN = (ORDER + 1) / 2;
    static final boolean DEBUG = false;

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
        LeafSplitResult result = splitLeaf(leaf, key, value);
        BPTNode right = result.rightNode;
        int promotedKey = result.promotedKey;

        propagateSplit(leaf, promotedKey, right);
        return true;
    }

    private LeafSplitResult splitLeaf(BPTNode leaf, int key, String value) {
        int[] tempKeys = new int[MAX_CHILDREN];
        String[] tempValues = new String[MAX_CHILDREN];

        int total = leaf.keyCount;
        int index = 0;
        while (index < total) {
            tempKeys[index] = leaf.keys[index];
            tempValues[index] = leaf.values[index];
            index++;
        }

        int position = findLeafPosition(leaf, key);
        int mover = total;
        while (mover > position) {
            tempKeys[mover] = tempKeys[mover - 1];
            tempValues[mover] = tempValues[mover - 1];
            mover--;
        }
        tempKeys[position] = key;
        tempValues[position] = value;
        total++;

        int splitPoint = total / 2;
        BPTNode right = createLeaf();
        right.parent = leaf.parent;

        int clearIndex = 0;
        while (clearIndex < MAX_KEYS) {
            leaf.keys[clearIndex] = 0;
            leaf.values[clearIndex] = null;
            clearIndex++;
        }

        int leftCount = splitPoint;
        int leftIndex = 0;
        while (leftIndex < leftCount) {
            leaf.keys[leftIndex] = tempKeys[leftIndex];
            leaf.values[leftIndex] = tempValues[leftIndex];
            leftIndex++;
        }
        leaf.keyCount = leftCount;
        leaf.valueCount = leftCount;

        int rightIndex = 0;
        int tempIndex = splitPoint;
        int rightCount = total - splitPoint;
        while (rightIndex < rightCount) {
            right.keys[rightIndex] = tempKeys[tempIndex];
            right.values[rightIndex] = tempValues[tempIndex];
            rightIndex++;
            tempIndex++;
        }
        right.keyCount = rightCount;
        right.valueCount = rightCount;

        right.next = leaf.next;
        leaf.next = right;

        LeafSplitResult result = new LeafSplitResult();
        result.rightNode = right;
        result.promotedKey = right.keys[0];
        return result;
    }

    private void insertIntoParent(BPTNode left, int promotedKey, BPTNode right) {
        BPTNode parent = left.parent;
        if (parent == null) {
            return;
        }

        int childIndex = findChildIndex(parent, left);
        if (childIndex < 0) {
            return;
        }

        if (parent.keyCount < MAX_KEYS) {
            shiftRightChildren(parent, childIndex + 1);
            parent.children[childIndex + 1] = right;
            parent.childCount++;

            shiftRightKeysInternal(parent, childIndex);
            parent.keys[childIndex] = promotedKey;
            parent.keyCount++;

            right.parent = parent;
            return;
        }

        insertIntoFullInternal(parent, childIndex, promotedKey, right);
    }

    private int findChildIndex(BPTNode parent, BPTNode child) {
        int index = 0;
        while (index < parent.childCount) {
            if (parent.children[index] == child) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private void shiftRightChildren(BPTNode parent, int index) {
        int cursor = parent.childCount;
        while (cursor > index) {
            parent.children[cursor] = parent.children[cursor - 1];
            cursor--;
        }
    }

    private void shiftRightKeysInternal(BPTNode parent, int index) {
        int cursor = parent.keyCount;
        while (cursor > index) {
            parent.keys[cursor] = parent.keys[cursor - 1];
            cursor--;
        }
    }

    private void insertIntoFullInternal(BPTNode parent, int childIndex, int promotedKey, BPTNode rightChild) {
        InternalSplitResult result = splitInternal(parent, childIndex, promotedKey, rightChild);
        BPTNode right = result.rightNode;
        int upKey = result.promotedKey;

        propagateSplit(parent, upKey, right);
    }

    private void propagateSplit(BPTNode left, int promotedKey, BPTNode right) {
        if (left.parent == null) {
            BPTNode newRoot = createInternal();
            newRoot.keys[0] = promotedKey;
            newRoot.keyCount = 1;
            newRoot.children[0] = left;
            newRoot.children[1] = right;
            newRoot.childCount = 2;
            left.parent = newRoot;
            right.parent = newRoot;
            setRoot(newRoot);
            return;
        }

        right.parent = left.parent;
        insertIntoParent(left, promotedKey, right);
    }

    private InternalSplitResult splitInternal(BPTNode parent, int childIndex, int promotedKey, BPTNode rightChild) {
        int existingKeys = parent.keyCount;
        int existingChildren = parent.childCount;

        int[] tempKeys = new int[MAX_CHILDREN];
        BPTNode[] tempChildren = new BPTNode[MAX_CHILDREN + 1];

        int index = 0;
        while (index < existingKeys) {
            tempKeys[index] = parent.keys[index];
            index++;
        }

        index = 0;
        while (index < existingChildren) {
            tempChildren[index] = parent.children[index];
            index++;
        }

        int childMover = existingChildren;
        while (childMover > childIndex + 1) {
            tempChildren[childMover] = tempChildren[childMover - 1];
            childMover--;
        }
        tempChildren[childIndex + 1] = rightChild;

        int keyMover = existingKeys;
        while (keyMover > childIndex) {
            tempKeys[keyMover] = tempKeys[keyMover - 1];
            keyMover--;
        }
        tempKeys[childIndex] = promotedKey;

        int totalKeys = existingKeys + 1;
        int totalChildren = existingChildren + 1;
        int promoteIndex = totalKeys / 2;

        int promoted = tempKeys[promoteIndex];
        BPTNode right = createInternal();
        right.parent = parent.parent;

        int clearKey = promoteIndex;
        while (clearKey < MAX_KEYS) {
            parent.keys[clearKey] = 0;
            clearKey++;
        }

        int clearChild = promoteIndex + 1;
        while (clearChild < MAX_CHILDREN) {
            parent.children[clearChild] = null;
            clearChild++;
        }

        parent.keyCount = 0;
        parent.childCount = 0;

        int leftKeyIndex = 0;
        while (leftKeyIndex < promoteIndex) {
            parent.keys[leftKeyIndex] = tempKeys[leftKeyIndex];
            leftKeyIndex++;
        }
        parent.keyCount = promoteIndex;

        int leftChildIndex = 0;
        while (leftChildIndex <= promoteIndex) {
            BPTNode child = tempChildren[leftChildIndex];
            parent.children[leftChildIndex] = child;
            if (child != null) {
                child.parent = parent;
            }
            leftChildIndex++;
        }
        parent.childCount = promoteIndex + 1;

        int rightKeyIndex = 0;
        int tempKeyIndex = promoteIndex + 1;
        while (tempKeyIndex < totalKeys) {
            right.keys[rightKeyIndex] = tempKeys[tempKeyIndex];
            rightKeyIndex++;
            tempKeyIndex++;
        }
        right.keyCount = rightKeyIndex;

        int rightChildIndex = 0;
        int tempChildIndex = promoteIndex + 1;
        while (tempChildIndex < totalChildren) {
            BPTNode child = tempChildren[tempChildIndex];
            right.children[rightChildIndex] = child;
            if (child != null) {
                child.parent = right;
            }
            rightChildIndex++;
            tempChildIndex++;
        }
        right.childCount = rightChildIndex;

        InternalSplitResult result = new InternalSplitResult();
        result.promotedKey = promoted;
        result.rightNode = right;
        return result;
    }

    // [NeoVini] Inserção básica em folha sem tratar overflow.
    // public boolean insert(int key, String value) {
    //     if (root == null) {
    //         BPTNode leaf = createLeaf();
    //         writeLeafEntry(leaf, 0, key, value);
    //         leaf.keyCount = 1;
    //         leaf.valueCount = 1;
    //         setRoot(leaf);
    //         return true;
    //     }
    //
    //     BPTNode leaf = findLeaf(key);
    //     int position = findLeafPosition(leaf, key);
    //
    //     if (position < leaf.keyCount && leaf.keys[position] == key) {
    //         leaf.values[position] = value;
    //         return false;
    //     }
    //
    //     if (leaf.keyCount < MAX_KEYS) {
    //         shiftRightLeaf(leaf, position);
    //         writeLeafEntry(leaf, position, key, value);
    //         leaf.keyCount++;
    //         leaf.valueCount++;
    //         return true;
    //     }
    //
    //     return insertIntoFullLeaf(leaf, key, value);
    // }

    // [Du] Range simples percorrendo folhas encadeadas.
    // public void range(int startKey, int endKey) {
    //     if (root == null || startKey > endKey) {
    //         return;
    //     }
    //
    //     BPTNode current = findLeaf(startKey);
    //     while (current != null) {
    //         int index = 0;
    //         while (index < current.keyCount) {
    //             int key = current.keys[index];
    //             if (key > endKey) {
    //                 return;
    //             }
    //             if (key >= startKey) {
    //                 System.out.println(key + " -> " + current.values[index]);
    //             }
    //             index++;
    //         }
    //         current = current.next;
    //     }
    // }

    // [Du] Remoção básica com chamada para rebalanceamento.
    // public boolean remove(int key) {
    //     if (root == null) {
    //         return false;
    //     }
    //
    //     BPTNode leaf = findLeaf(key);
    //     if (leaf == null) {
    //         return false;
    //     }
    //
    //     int index = findKeyIndex(leaf, key);
    //     if (index < 0) {
    //         return false;
    //     }
    //
    //     shiftLeftLeaf(leaf, index);
    //     leaf.keyCount--;
    //     leaf.valueCount--;
    //
    //     if (leaf == root) {
    //         if (leaf.keyCount == 0) {
    //             clear();
    //         }
    //         return true;
    //     }
    //
    //     refreshParentKey(leaf);
    //
    //     if (leaf.keyCount < MIN_KEYS_LEAF) {
    //         rebalanceAfterDelete(leaf);
    //     }
    //     return true;
    // }

    private int findKeyIndex(BPTNode node, int key) {
        int index = 0;
        while (index < node.keyCount) {
            if (node.keys[index] == key) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private void refreshParentKey(BPTNode node) {
        if (node.parent == null || node.keyCount == 0) {
            return;
        }
        int parentIndex = findChildIndex(node.parent, node);
        if (parentIndex > 0) {
            node.parent.keys[parentIndex - 1] = node.keys[0];
        }
    }

    // [Cebolinha] Rebalanceamento de nós.
    private void rebalanceAfterDelete(BPTNode node) {
        if (node == null) {
            return;
        }

        if (node.parent == null) {
            if (!node.isLeaf && node.childCount == 1) {
                BPTNode newRoot = node.children[0];
                setRoot(newRoot);
            } else if (node.isLeaf && node.keyCount == 0) {
                clear();
            }
            return;
        }

        int minKeys = node.isLeaf ? MIN_KEYS_LEAF : (MIN_CHILDREN - 1);
        if (node.keyCount >= minKeys) {
            if (node.isLeaf) {
                refreshParentKey(node);
            }
            return;
        }

        BPTNode parent = node.parent;
        int index = findChildIndex(parent, node);
        BPTNode leftSibling = index > 0 ? parent.children[index - 1] : null;
        BPTNode rightSibling = index + 1 < parent.childCount ? parent.children[index + 1] : null;

        if (node.isLeaf) {
            if (leftSibling != null && leftSibling.keyCount > MIN_KEYS_LEAF) {
                borrowFromLeftLeaf(node, leftSibling, parent, index);
                return;
            }
            if (rightSibling != null && rightSibling.keyCount > MIN_KEYS_LEAF) {
                borrowFromRightLeaf(node, rightSibling, parent, index);
                return;
            }
            if (leftSibling != null) {
                mergeLeaves(leftSibling, node, parent, index - 1);
                rebalanceAfterDelete(parent);
                return;
            }
            if (rightSibling != null) {
                mergeLeaves(node, rightSibling, parent, index);
                rebalanceAfterDelete(parent);
            }
            return;
        }

        int internalMin = MIN_CHILDREN - 1;
        if (leftSibling != null && leftSibling.keyCount > internalMin) {
            borrowFromLeftInternal(node, leftSibling, parent, index);
            return;
        }
        if (rightSibling != null && rightSibling.keyCount > internalMin) {
            borrowFromRightInternal(node, rightSibling, parent, index);
            return;
        }
        if (leftSibling != null) {
            mergeInternals(leftSibling, node, parent, index - 1);
            rebalanceAfterDelete(parent);
            return;
        }
        if (rightSibling != null) {
            mergeInternals(node, rightSibling, parent, index);
            rebalanceAfterDelete(parent);
        }
    }

    private void borrowFromLeftLeaf(BPTNode node, BPTNode leftSibling, BPTNode parent, int index) {
        shiftRightLeaf(node, 0);
        int donor = leftSibling.keyCount - 1;
        node.keys[0] = leftSibling.keys[donor];
        node.values[0] = leftSibling.values[donor];
        node.keyCount++;
        node.valueCount++;

        leftSibling.keys[donor] = 0;
        leftSibling.values[donor] = null;
        leftSibling.keyCount--;
        leftSibling.valueCount--;

        parent.keys[index - 1] = node.keys[0];
    }

    private void borrowFromRightLeaf(BPTNode node, BPTNode rightSibling, BPTNode parent, int index) {
        int insert = node.keyCount;
        node.keys[insert] = rightSibling.keys[0];
        node.values[insert] = rightSibling.values[0];
        node.keyCount++;
        node.valueCount++;

        shiftLeftLeaf(rightSibling, 0);
        rightSibling.keyCount--;
        rightSibling.valueCount--;

        if (rightSibling.keyCount > 0) {
            parent.keys[index] = rightSibling.keys[0];
        }
    }

    private void mergeLeaves(BPTNode left, BPTNode right, BPTNode parent, int parentKeyIndex) {
        int write = left.keyCount;
        int read = 0;
        while (read < right.keyCount) {
            left.keys[write] = right.keys[read];
            left.values[write] = right.values[read];
            write++;
            read++;
        }
        left.keyCount = write;
        left.valueCount = write;
        left.next = right.next;

        deleteParentEntry(parent, parentKeyIndex);
        refreshParentKey(left);
    }

    private void borrowFromLeftInternal(BPTNode node, BPTNode leftSibling, BPTNode parent, int index) {
        shiftRightKeysInternal(node, 0);
        shiftRightChildren(node, 0);

        int donorKey = leftSibling.keyCount - 1;
        int donorChild = leftSibling.childCount - 1;

        node.keys[0] = parent.keys[index - 1];
        node.keyCount++;

        node.children[0] = leftSibling.children[donorChild];
        if (node.children[0] != null) {
            node.children[0].parent = node;
        }
        node.childCount++;

        parent.keys[index - 1] = leftSibling.keys[donorKey];

        leftSibling.keys[donorKey] = 0;
        leftSibling.keyCount--;
        leftSibling.children[donorChild] = null;
        leftSibling.childCount--;
    }

    private void borrowFromRightInternal(BPTNode node, BPTNode rightSibling, BPTNode parent, int index) {
        int insertKey = node.keyCount;
        node.keys[insertKey] = parent.keys[index];
        node.keyCount++;

        int insertChild = node.childCount;
        node.children[insertChild] = rightSibling.children[0];
        if (node.children[insertChild] != null) {
            node.children[insertChild].parent = node;
        }
        node.childCount++;

        shiftLeftChildren(rightSibling, 0);
        shiftLeftKeysInternal(rightSibling, 0);
        rightSibling.childCount--;
        rightSibling.keyCount--;

        if (rightSibling.keyCount > 0) {
            parent.keys[index] = rightSibling.keys[0];
        } else {
            parent.keys[index] = node.keys[node.keyCount - 1];
        }
    }

    private void mergeInternals(BPTNode left, BPTNode right, BPTNode parent, int parentKeyIndex) {
        int writeKey = left.keyCount;
        left.keys[writeKey] = parent.keys[parentKeyIndex];
        writeKey++;

        int readKey = 0;
        while (readKey < right.keyCount) {
            left.keys[writeKey] = right.keys[readKey];
            writeKey++;
            readKey++;
        }
        left.keyCount = writeKey;

        int writeChild = left.childCount;
        int readChild = 0;
        while (readChild < right.childCount) {
            left.children[writeChild] = right.children[readChild];
            if (right.children[readChild] != null) {
                right.children[readChild].parent = left;
            }
            writeChild++;
            readChild++;
        }
        left.childCount = writeChild;

        deleteParentEntry(parent, parentKeyIndex);
    }

    private void deleteParentEntry(BPTNode parent, int keyIndex) {
        int mover = keyIndex;
        while (mover + 1 < parent.keyCount) {
            parent.keys[mover] = parent.keys[mover + 1];
            mover++;
        }
        if (parent.keyCount > 0) {
            parent.keys[parent.keyCount - 1] = 0;
        }
        parent.keyCount--;

        int childMover = keyIndex + 1;
        while (childMover + 1 < parent.childCount) {
            parent.children[childMover] = parent.children[childMover + 1];
            childMover++;
        }
        if (parent.childCount > 0) {
            parent.children[parent.childCount - 1] = null;
        }
        parent.childCount--;

        if (parent.parent == null && parent.keyCount == 0 && parent.childCount == 1) {
            BPTNode newRoot = parent.children[0];
            setRoot(newRoot);
        }
    }

    private void shiftLeftKeysInternal(BPTNode node, int index) {
        int cursor = index;
        while (cursor + 1 < node.keyCount) {
            node.keys[cursor] = node.keys[cursor + 1];
            cursor++;
        }
        if (node.keyCount > 0) {
            node.keys[node.keyCount - 1] = 0;
        }
    }

    private void shiftLeftChildren(BPTNode node, int index) {
        int cursor = index;
        while (cursor + 1 < node.childCount) {
            node.children[cursor] = node.children[cursor + 1];
            cursor++;
        }
        if (node.childCount > 0) {
            node.children[node.childCount - 1] = null;
        }
    }

    private static final class LeafSplitResult {
        int promotedKey;
        BPTNode rightNode;
    }

    private static final class InternalSplitResult {
        int promotedKey;
        BPTNode rightNode;
    }

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
