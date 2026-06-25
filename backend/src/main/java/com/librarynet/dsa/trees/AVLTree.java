package com.librarynet.dsa.trees;

import com.librarynet.dsa.model.Book;
import java.util.ArrayList;
import java.util.List;

public class AVLTree {
    private static final class Node {
        Book book;
        int height = 1;
        Node left;
        Node right;
        Node(Book book) { this.book = book; }
    }

    private Node root;
    private int size;
    private int rotationCount;

    public void clear() { root = null; size = 0; rotationCount = 0; }
    public int size() { return size; }
    public int height() { return height(root); }
    public int getRotationCount() { return rotationCount; }
    public boolean isEmpty() { return root == null; }

    public boolean addResource(Book book) {
        if (book == null) throw new IllegalArgumentException("Book cannot be null");
        int before = size;
        root = insert(root, book);
        return size > before;
    }

    private Node insert(Node node, Book book) {
        if (node == null) {
            size++;
            return new Node(book);
        }
        if (book.getId() < node.book.getId()) node.left = insert(node.left, book);
        else if (book.getId() > node.book.getId()) node.right = insert(node.right, book);
        else {
            node.book = book;
            return node;
        }
        updateHeight(node);
        return rebalance(node);
    }

    public Book searchResource(int id) { return searchWithStats(id).getBook(); }

    public TreeSearchResult searchWithStats(int id) {
        Node current = root;
        int comparisons = 0;
        while (current != null) {
            comparisons++;
            if (id == current.book.getId()) return new TreeSearchResult(current.book, comparisons);
            current = id < current.book.getId() ? current.left : current.right;
        }
        return new TreeSearchResult(null, comparisons);
    }

    public boolean removeResource(int id) {
        if (searchResource(id) == null) return false;
        root = delete(root, id);
        size--;
        return true;
    }

    private Node delete(Node node, int id) {
        if (node == null) return null;
        if (id < node.book.getId()) node.left = delete(node.left, id);
        else if (id > node.book.getId()) node.right = delete(node.right, id);
        else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            Node successor = min(node.right);
            node.book = successor.book;
            node.right = delete(node.right, successor.book.getId());
        }
        updateHeight(node);
        return rebalance(node);
    }

    private Node min(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private Node rebalance(Node node) {
        int balance = balance(node);
        if (balance > 1) {
            if (balance(node.left) < 0) node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1) {
            if (balance(node.right) > 0) node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t = x.right;
        x.right = y;
        y.left = t;
        updateHeight(y);
        updateHeight(x);
        rotationCount++;
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t = y.left;
        y.left = x;
        x.right = t;
        updateHeight(x);
        updateHeight(y);
        rotationCount++;
        return y;
    }

    private int height(Node node) { return node == null ? 0 : node.height; }
    private int balance(Node node) { return node == null ? 0 : height(node.left) - height(node.right); }
    private void updateHeight(Node node) { node.height = 1 + Math.max(height(node.left), height(node.right)); }

    public List<Book> inorder() {
        List<Book> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    public List<Book> preorder() {
        List<Book> result = new ArrayList<>();
        preorder(root, result);
        return result;
    }

    public List<Book> postorder() {
        List<Book> result = new ArrayList<>();
        postorder(root, result);
        return result;
    }

    private void inorder(Node node, List<Book> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.book);
        inorder(node.right, out);
    }

    private void preorder(Node node, List<Book> out) {
        if (node == null) return;
        out.add(node.book);
        preorder(node.left, out);
        preorder(node.right, out);
    }

    private void postorder(Node node, List<Book> out) {
        if (node == null) return;
        postorder(node.left, out);
        postorder(node.right, out);
        out.add(node.book);
    }

    public boolean isBalanced() { return check(root).balanced; }

    private BalanceCheck check(Node node) {
        if (node == null) return new BalanceCheck(true, 0);
        BalanceCheck left = check(node.left);
        BalanceCheck right = check(node.right);
        boolean ok = left.balanced && right.balanced && Math.abs(left.height - right.height) <= 1
                && node.height == 1 + Math.max(left.height, right.height);
        return new BalanceCheck(ok, 1 + Math.max(left.height, right.height));
    }

    private static final class BalanceCheck {
        final boolean balanced;
        final int height;
        BalanceCheck(boolean balanced, int height) { this.balanced = balanced; this.height = height; }
    }
}
