package com.librarynet.dsa.indexing;

import com.librarynet.dsa.model.Publication;
import java.util.ArrayList;
import java.util.List;

/**
 * A genuine B-Tree keyed by publication ID.
 * minimumDegree=3 gives at most 5 keys per node, matching the common
 * classroom interpretation of an "order-5" B-Tree.
 */
public class BTree {
    private static final class Node {
        int keyCount;
        final Publication[] keys;
        final Node[] children;
        boolean leaf;

        Node(int minimumDegree, boolean leaf) {
            this.leaf = leaf;
            this.keys = new Publication[2 * minimumDegree - 1];
            this.children = new Node[2 * minimumDegree];
        }
    }

    private final int t;
    private Node root;
    private int size;

    public BTree() { this(3); }

    public BTree(int minimumDegree) {
        if (minimumDegree < 2) throw new IllegalArgumentException("Minimum degree must be at least 2");
        this.t = minimumDegree;
        this.root = new Node(t, true);
    }

    public void clear() { root = new Node(t, true); size = 0; }
    public int size() { return size; }
    public int height() {
        int height = 0;
        Node current = root;
        while (!current.leaf) { height++; current = current.children[0]; }
        return height;
    }

    public Publication search(int id) { return search(root, id); }

    private Publication search(Node node, int id) {
        int i = 0;
        while (i < node.keyCount && id > node.keys[i].getId()) i++;
        if (i < node.keyCount && id == node.keys[i].getId()) return node.keys[i];
        return node.leaf ? null : search(node.children[i], id);
    }

    public boolean insert(Publication publication) {
        if (publication == null) throw new IllegalArgumentException("Publication cannot be null");
        if (search(publication.getId()) != null) return false;
        Node currentRoot = root;
        if (currentRoot.keyCount == 2 * t - 1) {
            Node newRoot = new Node(t, false);
            newRoot.children[0] = currentRoot;
            splitChild(newRoot, 0);
            root = newRoot;
            insertNonFull(newRoot, publication);
        } else {
            insertNonFull(currentRoot, publication);
        }
        size++;
        return true;
    }

    private void insertNonFull(Node node, Publication publication) {
        int i = node.keyCount - 1;
        if (node.leaf) {
            while (i >= 0 && publication.getId() < node.keys[i].getId()) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            node.keys[i + 1] = publication;
            node.keyCount++;
        } else {
            while (i >= 0 && publication.getId() < node.keys[i].getId()) i--;
            i++;
            if (node.children[i].keyCount == 2 * t - 1) {
                splitChild(node, i);
                if (publication.getId() > node.keys[i].getId()) i++;
            }
            insertNonFull(node.children[i], publication);
        }
    }

    private void splitChild(Node parent, int index) {
        Node full = parent.children[index];
        Node right = new Node(t, full.leaf);
        right.keyCount = t - 1;

        for (int j = 0; j < t - 1; j++) right.keys[j] = full.keys[j + t];
        if (!full.leaf) {
            for (int j = 0; j < t; j++) right.children[j] = full.children[j + t];
        }
        Publication median = full.keys[t - 1];
        full.keyCount = t - 1;

        for (int j = parent.keyCount; j >= index + 1; j--) parent.children[j + 1] = parent.children[j];
        parent.children[index + 1] = right;
        for (int j = parent.keyCount - 1; j >= index; j--) parent.keys[j + 1] = parent.keys[j];
        parent.keys[index] = median;
        parent.keyCount++;
    }

    public boolean delete(int id) {
        if (search(id) == null) return false;
        delete(root, id);
        if (root.keyCount == 0 && !root.leaf) root = root.children[0];
        size--;
        return true;
    }

    private void delete(Node node, int id) {
        int index = findKey(node, id);
        if (index < node.keyCount && node.keys[index].getId() == id) {
            if (node.leaf) removeFromLeaf(node, index);
            else removeFromNonLeaf(node, index);
        } else {
            if (node.leaf) return;
            boolean atLastChild = index == node.keyCount;
            if (node.children[index].keyCount < t) fill(node, index);
            if (atLastChild && index > node.keyCount) delete(node.children[index - 1], id);
            else delete(node.children[index], id);
        }
    }

    private int findKey(Node node, int id) {
        int index = 0;
        while (index < node.keyCount && node.keys[index].getId() < id) index++;
        return index;
    }

    private void removeFromLeaf(Node node, int index) {
        for (int i = index + 1; i < node.keyCount; i++) node.keys[i - 1] = node.keys[i];
        node.keys[node.keyCount - 1] = null;
        node.keyCount--;
    }

    private void removeFromNonLeaf(Node node, int index) {
        Publication key = node.keys[index];
        if (node.children[index].keyCount >= t) {
            Publication predecessor = predecessor(node, index);
            node.keys[index] = predecessor;
            delete(node.children[index], predecessor.getId());
        } else if (node.children[index + 1].keyCount >= t) {
            Publication successor = successor(node, index);
            node.keys[index] = successor;
            delete(node.children[index + 1], successor.getId());
        } else {
            merge(node, index);
            delete(node.children[index], key.getId());
        }
    }

    private Publication predecessor(Node node, int index) {
        Node current = node.children[index];
        while (!current.leaf) current = current.children[current.keyCount];
        return current.keys[current.keyCount - 1];
    }

    private Publication successor(Node node, int index) {
        Node current = node.children[index + 1];
        while (!current.leaf) current = current.children[0];
        return current.keys[0];
    }

    private void fill(Node node, int index) {
        if (index != 0 && node.children[index - 1].keyCount >= t) borrowFromPrevious(node, index);
        else if (index != node.keyCount && node.children[index + 1].keyCount >= t) borrowFromNext(node, index);
        else if (index != node.keyCount) merge(node, index);
        else merge(node, index - 1);
    }

    private void borrowFromPrevious(Node node, int index) {
        Node child = node.children[index];
        Node sibling = node.children[index - 1];
        for (int i = child.keyCount - 1; i >= 0; i--) child.keys[i + 1] = child.keys[i];
        if (!child.leaf) {
            for (int i = child.keyCount; i >= 0; i--) child.children[i + 1] = child.children[i];
        }
        child.keys[0] = node.keys[index - 1];
        if (!child.leaf) child.children[0] = sibling.children[sibling.keyCount];
        node.keys[index - 1] = sibling.keys[sibling.keyCount - 1];
        sibling.keys[sibling.keyCount - 1] = null;
        child.keyCount++;
        sibling.keyCount--;
    }

    private void borrowFromNext(Node node, int index) {
        Node child = node.children[index];
        Node sibling = node.children[index + 1];
        child.keys[child.keyCount] = node.keys[index];
        if (!child.leaf) child.children[child.keyCount + 1] = sibling.children[0];
        node.keys[index] = sibling.keys[0];
        for (int i = 1; i < sibling.keyCount; i++) sibling.keys[i - 1] = sibling.keys[i];
        if (!sibling.leaf) {
            for (int i = 1; i <= sibling.keyCount; i++) sibling.children[i - 1] = sibling.children[i];
        }
        sibling.keys[sibling.keyCount - 1] = null;
        child.keyCount++;
        sibling.keyCount--;
    }

    private void merge(Node node, int index) {
        Node child = node.children[index];
        Node sibling = node.children[index + 1];
        child.keys[t - 1] = node.keys[index];
        for (int i = 0; i < sibling.keyCount; i++) child.keys[i + t] = sibling.keys[i];
        if (!child.leaf) {
            for (int i = 0; i <= sibling.keyCount; i++) child.children[i + t] = sibling.children[i];
        }
        for (int i = index + 1; i < node.keyCount; i++) node.keys[i - 1] = node.keys[i];
        for (int i = index + 2; i <= node.keyCount; i++) node.children[i - 1] = node.children[i];
        child.keyCount += sibling.keyCount + 1;
        node.keys[node.keyCount - 1] = null;
        node.children[node.keyCount] = null;
        node.keyCount--;
    }

    public List<Publication> traverse() {
        List<Publication> result = new ArrayList<>();
        traverse(root, result);
        return result;
    }

    private void traverse(Node node, List<Publication> result) {
        for (int i = 0; i < node.keyCount; i++) {
            if (!node.leaf) traverse(node.children[i], result);
            result.add(node.keys[i]);
        }
        if (!node.leaf) traverse(node.children[node.keyCount], result);
    }

    public boolean isValid() {
        if (root == null) return false;
        int leafDepth = firstLeafDepth(root);
        return validate(root, true, Long.MIN_VALUE, Long.MAX_VALUE, 0, leafDepth);
    }

    private int firstLeafDepth(Node node) {
        int depth = 0;
        while (!node.leaf) { node = node.children[0]; depth++; }
        return depth;
    }

    private boolean validate(Node node, boolean isRoot, long min, long max, int depth, int leafDepth) {
        int minKeys = isRoot ? (node.leaf ? 0 : 1) : t - 1;
        if (node.keyCount < minKeys || node.keyCount > 2 * t - 1) return false;
        for (int i = 0; i < node.keyCount; i++) {
            int id = node.keys[i].getId();
            if (id <= min || id >= max) return false;
            if (i > 0 && node.keys[i - 1].getId() >= id) return false;
        }
        if (node.leaf) return depth == leafDepth;
        for (int i = 0; i <= node.keyCount; i++) {
            if (node.children[i] == null) return false;
            long childMin = i == 0 ? min : node.keys[i - 1].getId();
            long childMax = i == node.keyCount ? max : node.keys[i].getId();
            if (!validate(node.children[i], false, childMin, childMax, depth + 1, leafDepth)) return false;
        }
        return true;
    }
}
