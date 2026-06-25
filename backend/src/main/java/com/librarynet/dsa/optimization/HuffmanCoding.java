package com.librarynet.dsa.optimization;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public final class HuffmanCoding {
    private static final class Node implements Comparable<Node> {
        final char character;
        final int frequency;
        final Node left;
        final Node right;

        Node(char character, int frequency) { this(character, frequency, null, null); }
        Node(char character, int frequency, Node left, Node right) {
            this.character = character;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
        boolean isLeaf() { return left == null && right == null; }
        @Override public int compareTo(Node other) { return Integer.compare(frequency, other.frequency); }
    }

    public static final class Result {
        private final String encoded;
        private final Map<Character, String> codes;
        private final Node root;

        Result(String encoded, Map<Character, String> codes, Node root) {
            this.encoded = encoded;
            this.codes = Map.copyOf(codes);
            this.root = root;
        }

        public String getEncoded() { return encoded; }
        public Map<Character, String> getCodes() { return codes; }
        public String decode() { return decodeBits(encoded, root); }
    }

    private HuffmanCoding() { }

    public static Result encode(String text) {
        if (text == null || text.isEmpty()) throw new IllegalArgumentException("Text cannot be empty");
        Map<Character, Integer> frequencies = new HashMap<>();
        for (char c : text.toCharArray()) frequencies.merge(c, 1, Integer::sum);
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            queue.add(new Node(entry.getKey(), entry.getValue()));
        }
        while (queue.size() > 1) {
            Node left = queue.remove();
            Node right = queue.remove();
            queue.add(new Node('\0', left.frequency + right.frequency, left, right));
        }
        Node root = queue.remove();
        Map<Character, String> codes = new HashMap<>();
        buildCodes(root, "", codes);
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) encoded.append(codes.get(c));
        return new Result(encoded.toString(), codes, root);
    }

    private static void buildCodes(Node node, String prefix, Map<Character, String> codes) {
        if (node.isLeaf()) {
            codes.put(node.character, prefix.isEmpty() ? "0" : prefix);
            return;
        }
        buildCodes(node.left, prefix + '0', codes);
        buildCodes(node.right, prefix + '1', codes);
    }

    private static String decodeBits(String bits, Node root) {
        if (root.isLeaf()) return String.valueOf(root.character).repeat(bits.length());
        StringBuilder decoded = new StringBuilder();
        Node current = root;
        for (int i = 0; i < bits.length(); i++) {
            char bit = bits.charAt(i);
            if (bit != '0' && bit != '1') throw new IllegalArgumentException("Encoded data must contain only 0 and 1");
            current = bit == '0' ? current.left : current.right;
            if (current == null) throw new IllegalArgumentException("Invalid Huffman bit sequence");
            if (current.isLeaf()) {
                decoded.append(current.character);
                current = root;
            }
        }
        if (current != root) throw new IllegalArgumentException("Incomplete Huffman bit sequence");
        return decoded.toString();
    }
}
