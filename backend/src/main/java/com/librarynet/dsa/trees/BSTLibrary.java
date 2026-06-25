package com.librarynet.dsa.trees;

import com.librarynet.dsa.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BSTLibrary {
    private static final class Node {
        Book book;
        Node left;
        Node right;
        Node(Book book) { this.book = book; }
    }

    private Node root;
    private int size;

    public void clear() { root = null; size = 0; }
    public int size() { return size; }
    public int height() { return height(root); }
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
        else node.book = book;
        return node;
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
        return node;
    }

    private Node min(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

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

    private int height(Node node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }

    public boolean isValidBST() { return validate(root, Long.MIN_VALUE, Long.MAX_VALUE); }

    private boolean validate(Node node, long min, long max) {
        if (node == null) return true;
        int id = node.book.getId();
        return id > min && id < max && validate(node.left, min, id) && validate(node.right, id, max);
    }
}
