package com.librarynet.dsa.indexing;

import com.librarynet.dsa.model.Publication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * B+ Tree with linked leaves and a compound (publication year, publication ID) key.
 *
 * <p>The compound key avoids assumptions about the numerical range of PublicationID.
 * Deletion performs normal redistribution/merge operations instead of rebuilding the
 * entire index.</p>
 */
public class BPlusTree {
    private static final class PublicationKey implements Comparable<PublicationKey> {
        private final int year;
        private final int publicationId;

        private PublicationKey(int year, int publicationId) {
            this.year = year;
            this.publicationId = publicationId;
        }

        @Override
        public int compareTo(PublicationKey other) {
            int byYear = Integer.compare(year, other.year);
            return byYear != 0 ? byYear : Integer.compare(publicationId, other.publicationId);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof PublicationKey)) return false;
            PublicationKey key = (PublicationKey) other;
            return year == key.year && publicationId == key.publicationId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(year, publicationId);
        }
    }

    private abstract static class Node {
        final List<PublicationKey> keys = new ArrayList<>();
        InternalNode parent;
        abstract boolean isLeaf();
    }

    private static final class LeafNode extends Node {
        final List<Publication> values = new ArrayList<>();
        LeafNode next;
        LeafNode previous;
        @Override boolean isLeaf() { return true; }
    }

    private static final class InternalNode extends Node {
        final List<Node> children = new ArrayList<>();
        @Override boolean isLeaf() { return false; }
    }

    private static final class Split {
        final PublicationKey promotedKey;
        final Node right;

        Split(PublicationKey promotedKey, Node right) {
            this.promotedKey = promotedKey;
            this.right = right;
        }
    }

    private final int order;
    private final Map<Integer, Publication> publicationById = new HashMap<>();
    private Node root;
    private int size;

    public BPlusTree() { this(4); }

    /**
     * @param order maximum number of children in an internal node. Must be at least 3.
     */
    public BPlusTree(int order) {
        if (order < 3) throw new IllegalArgumentException("B+ Tree order must be at least 3");
        this.order = order;
        this.root = new LeafNode();
    }

    private PublicationKey key(Publication publication) {
        return new PublicationKey(publication.getYear(), publication.getId());
    }

    private PublicationKey firstKeyForYear(int year) {
        return new PublicationKey(year, Integer.MIN_VALUE);
    }

    private PublicationKey lastKeyForYear(int year) {
        return new PublicationKey(year, Integer.MAX_VALUE);
    }

    private int maxLeafKeys() { return order - 1; }
    private int minLeafKeys() { return (int) Math.ceil(maxLeafKeys() / 2.0); }
    private int minInternalChildren() { return (int) Math.ceil(order / 2.0); }

    public void clear() {
        root = new LeafNode();
        publicationById.clear();
        size = 0;
    }

    public int size() { return size; }

    public boolean insert(Publication publication) {
        if (publication == null) throw new IllegalArgumentException("Publication cannot be null");
        if (publicationById.containsKey(publication.getId())) return false;

        Split split = insert(root, publication);
        if (split != null) {
            InternalNode newRoot = new InternalNode();
            newRoot.children.add(root);
            newRoot.children.add(split.right);
            root.parent = newRoot;
            split.right.parent = newRoot;
            rebuildKeys(newRoot);
            root = newRoot;
        }
        publicationById.put(publication.getId(), publication);
        size++;
        refreshAllSeparators(root);
        return true;
    }

    private Split insert(Node node, Publication publication) {
        PublicationKey newKey = key(publication);
        if (node.isLeaf()) {
            LeafNode leaf = (LeafNode) node;
            int index = Collections.binarySearch(leaf.keys, newKey);
            if (index < 0) index = -index - 1;
            leaf.keys.add(index, newKey);
            leaf.values.add(index, publication);
            return leaf.keys.size() > maxLeafKeys() ? splitLeaf(leaf) : null;
        }

        InternalNode internal = (InternalNode) node;
        int childIndex = childIndex(internal, newKey);
        Split childSplit = insert(internal.children.get(childIndex), publication);
        if (childSplit != null) {
            internal.children.add(childIndex + 1, childSplit.right);
            childSplit.right.parent = internal;
            rebuildKeys(internal);
        }
        return internal.children.size() > order ? splitInternal(internal) : null;
    }

    private Split splitLeaf(LeafNode leaf) {
        int splitIndex = (leaf.keys.size() + 1) / 2;
        LeafNode right = new LeafNode();
        right.keys.addAll(new ArrayList<>(leaf.keys.subList(splitIndex, leaf.keys.size())));
        right.values.addAll(new ArrayList<>(leaf.values.subList(splitIndex, leaf.values.size())));
        leaf.keys.subList(splitIndex, leaf.keys.size()).clear();
        leaf.values.subList(splitIndex, leaf.values.size()).clear();

        right.next = leaf.next;
        if (right.next != null) right.next.previous = right;
        leaf.next = right;
        right.previous = leaf;
        right.parent = leaf.parent;
        return new Split(right.keys.get(0), right);
    }

    private Split splitInternal(InternalNode node) {
        int splitChildIndex = (node.children.size() + 1) / 2;
        InternalNode right = new InternalNode();
        right.children.addAll(new ArrayList<>(node.children.subList(splitChildIndex, node.children.size())));
        node.children.subList(splitChildIndex, node.children.size()).clear();
        for (Node child : right.children) child.parent = right;
        right.parent = node.parent;
        rebuildKeys(node);
        rebuildKeys(right);
        return new Split(firstKey(right), right);
    }

    private int childIndex(InternalNode node, PublicationKey searchKey) {
        int index = 0;
        while (index < node.keys.size() && searchKey.compareTo(node.keys.get(index)) >= 0) index++;
        return index;
    }

    private LeafNode findLeaf(PublicationKey searchKey) {
        Node node = root;
        while (!node.isLeaf()) {
            InternalNode internal = (InternalNode) node;
            node = internal.children.get(childIndex(internal, searchKey));
        }
        return (LeafNode) node;
    }

    public List<Publication> rangeSearch(int startYear, int endYear) {
        if (startYear > endYear) {
            int temp = startYear;
            startYear = endYear;
            endYear = temp;
        }
        PublicationKey start = firstKeyForYear(startYear);
        PublicationKey end = lastKeyForYear(endYear);
        LeafNode leaf = findLeaf(start);
        List<Publication> result = new ArrayList<>();
        while (leaf != null) {
            for (int i = 0; i < leaf.keys.size(); i++) {
                PublicationKey current = leaf.keys.get(i);
                if (current.compareTo(end) > 0) return result;
                if (current.compareTo(start) >= 0) result.add(leaf.values.get(i));
            }
            leaf = leaf.next;
        }
        return result;
    }

    /** Exact PublicationID lookup is O(1) through a synchronized ID directory. */
    public Publication searchById(int id) {
        return publicationById.get(id);
    }

    /**
     * Removes a publication using B+ Tree redistribution and merge operations.
     */
    public boolean delete(int id) {
        Publication publication = publicationById.get(id);
        if (publication == null) return false;

        PublicationKey target = key(publication);
        LeafNode leaf = findLeaf(target);
        int index = Collections.binarySearch(leaf.keys, target);
        if (index < 0) return false;

        leaf.keys.remove(index);
        leaf.values.remove(index);
        publicationById.remove(id);
        size--;

        if (leaf != root && leaf.keys.size() < minLeafKeys()) rebalanceLeaf(leaf);
        collapseRootIfNeeded();
        refreshAllSeparators(root);
        return true;
    }

    private void rebalanceLeaf(LeafNode leaf) {
        InternalNode parent = leaf.parent;
        int index = parent.children.indexOf(leaf);
        LeafNode left = index > 0 ? (LeafNode) parent.children.get(index - 1) : null;
        LeafNode right = index + 1 < parent.children.size() ? (LeafNode) parent.children.get(index + 1) : null;

        if (left != null && left.keys.size() > minLeafKeys()) {
            int last = left.keys.size() - 1;
            leaf.keys.add(0, left.keys.remove(last));
            leaf.values.add(0, left.values.remove(last));
            rebuildKeys(parent);
            return;
        }
        if (right != null && right.keys.size() > minLeafKeys()) {
            leaf.keys.add(right.keys.remove(0));
            leaf.values.add(right.values.remove(0));
            rebuildKeys(parent);
            return;
        }

        if (left != null) {
            left.keys.addAll(leaf.keys);
            left.values.addAll(leaf.values);
            left.next = leaf.next;
            if (leaf.next != null) leaf.next.previous = left;
            parent.children.remove(index);
            rebuildKeys(parent);
            rebalanceInternal(parent);
        } else if (right != null) {
            leaf.keys.addAll(right.keys);
            leaf.values.addAll(right.values);
            leaf.next = right.next;
            if (right.next != null) right.next.previous = leaf;
            parent.children.remove(index + 1);
            rebuildKeys(parent);
            rebalanceInternal(parent);
        }
    }

    private void rebalanceInternal(InternalNode node) {
        if (node == root) {
            collapseRootIfNeeded();
            return;
        }
        if (node.children.size() >= minInternalChildren()) return;

        InternalNode parent = node.parent;
        int index = parent.children.indexOf(node);
        InternalNode left = index > 0 ? (InternalNode) parent.children.get(index - 1) : null;
        InternalNode right = index + 1 < parent.children.size()
                ? (InternalNode) parent.children.get(index + 1) : null;

        if (left != null && left.children.size() > minInternalChildren()) {
            Node borrowed = left.children.remove(left.children.size() - 1);
            node.children.add(0, borrowed);
            borrowed.parent = node;
            rebuildKeys(left);
            rebuildKeys(node);
            rebuildKeys(parent);
            return;
        }
        if (right != null && right.children.size() > minInternalChildren()) {
            Node borrowed = right.children.remove(0);
            node.children.add(borrowed);
            borrowed.parent = node;
            rebuildKeys(right);
            rebuildKeys(node);
            rebuildKeys(parent);
            return;
        }

        if (left != null) {
            for (Node child : node.children) {
                left.children.add(child);
                child.parent = left;
            }
            parent.children.remove(index);
            rebuildKeys(left);
            rebuildKeys(parent);
            rebalanceInternal(parent);
        } else if (right != null) {
            for (Node child : right.children) {
                node.children.add(child);
                child.parent = node;
            }
            parent.children.remove(index + 1);
            rebuildKeys(node);
            rebuildKeys(parent);
            rebalanceInternal(parent);
        }
    }

    private void collapseRootIfNeeded() {
        if (!root.isLeaf()) {
            InternalNode internal = (InternalNode) root;
            if (internal.children.size() == 1) {
                root = internal.children.get(0);
                root.parent = null;
            } else if (internal.children.isEmpty()) {
                root = new LeafNode();
            }
        }
    }

    public List<Publication> allPublications() {
        List<Publication> result = new ArrayList<>();
        LeafNode leaf = leftmostLeaf();
        while (leaf != null) {
            result.addAll(leaf.values);
            leaf = leaf.next;
        }
        result.sort(Comparator.comparingInt(Publication::getYear).thenComparingInt(Publication::getId));
        return result;
    }

    private LeafNode leftmostLeaf() {
        Node node = root;
        while (!node.isLeaf()) node = ((InternalNode) node).children.get(0);
        return (LeafNode) node;
    }

    private PublicationKey firstKey(Node node) {
        Node current = node;
        while (!current.isLeaf()) current = ((InternalNode) current).children.get(0);
        LeafNode leaf = (LeafNode) current;
        if (leaf.keys.isEmpty()) throw new IllegalStateException("Cannot obtain a separator from an empty subtree");
        return leaf.keys.get(0);
    }

    private void rebuildKeys(InternalNode node) {
        node.keys.clear();
        for (int i = 1; i < node.children.size(); i++) node.keys.add(firstKey(node.children.get(i)));
    }

    private void refreshAllSeparators(Node node) {
        if (node.isLeaf()) return;
        InternalNode internal = (InternalNode) node;
        for (Node child : internal.children) refreshAllSeparators(child);
        rebuildKeys(internal);
    }

    public boolean isValid() {
        if (root == null || root.parent != null) return false;
        List<Publication> all = allPublications();
        if (all.size() != size || publicationById.size() != size) return false;
        for (int i = 1; i < all.size(); i++) {
            if (key(all.get(i - 1)).compareTo(key(all.get(i))) >= 0) return false;
        }
        for (Publication publication : all) {
            if (publicationById.get(publication.getId()) != publication) return false;
        }

        List<LeafNode> leaves = new ArrayList<>();
        int[] expectedLeafDepth = {-1};
        if (!validateNode(root, null, 0, expectedLeafDepth, leaves)) return false;
        for (int i = 0; i < leaves.size(); i++) {
            LeafNode expectedPrevious = i == 0 ? null : leaves.get(i - 1);
            LeafNode expectedNext = i + 1 == leaves.size() ? null : leaves.get(i + 1);
            if (leaves.get(i).previous != expectedPrevious || leaves.get(i).next != expectedNext) return false;
        }
        return true;
    }

    private boolean validateNode(Node node, InternalNode expectedParent, int depth,
                                 int[] expectedLeafDepth, List<LeafNode> leaves) {
        if (node.parent != expectedParent) return false;
        for (int i = 1; i < node.keys.size(); i++) {
            if (node.keys.get(i - 1).compareTo(node.keys.get(i)) >= 0) return false;
        }

        if (node.isLeaf()) {
            LeafNode leaf = (LeafNode) node;
            if (leaf.keys.size() != leaf.values.size()) return false;
            if (node != root && (leaf.keys.size() < minLeafKeys() || leaf.keys.size() > maxLeafKeys())) return false;
            if (node == root && leaf.keys.size() > maxLeafKeys()) return false;
            for (int i = 0; i < leaf.values.size(); i++) {
                if (!leaf.keys.get(i).equals(key(leaf.values.get(i)))) return false;
            }
            if (expectedLeafDepth[0] < 0) expectedLeafDepth[0] = depth;
            if (expectedLeafDepth[0] != depth) return false;
            leaves.add(leaf);
            return true;
        }

        InternalNode internal = (InternalNode) node;
        if (internal.children.size() != internal.keys.size() + 1) return false;
        if (node == root) {
            if (internal.children.size() < 2 || internal.children.size() > order) return false;
        } else if (internal.children.size() < minInternalChildren() || internal.children.size() > order) {
            return false;
        }
        for (int i = 1; i < internal.children.size(); i++) {
            if (!internal.keys.get(i - 1).equals(firstKey(internal.children.get(i)))) return false;
        }
        for (Node child : internal.children) {
            if (!validateNode(child, internal, depth + 1, expectedLeafDepth, leaves)) return false;
        }
        return true;
    }
}
