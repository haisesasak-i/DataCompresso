package com.example.datacompresso.Huffman;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

public class Huffman implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String SERIAL_FILE = "huffman_state.ser";

    private RLE rle;
    private HashMap<Character, Integer> frequencyMap;
    private HashMap<Character, String> codeMap;
    private StringBuilder builder;
    private MinHeap minHeap;
    private StateForAdaptiveEncoding state;

    public Huffman() {

        if (!loadState()) {

            rle = new RLE();
            this.frequencyMap = new HashMap<>();
            this.minHeap = new MinHeap();
            this.builder = new StringBuilder();
            this.codeMap = new HashMap<>();
            state = new StateForAdaptiveEncoding();
        }
    }


    public void saveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIAL_FILE))) {
            oos.writeObject(this);
            System.out.println("Huffman state saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load Huffman state from file
    private boolean loadState() {
        File file = new File(SERIAL_FILE);
        if (!file.exists()) return false;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Huffman loaded = (Huffman) ois.readObject();

            // Copy loaded fields into this
            this.rle = loaded.rle;
            this.frequencyMap = loaded.frequencyMap;
            this.codeMap = loaded.codeMap;
            this.builder = new StringBuilder();
            this.minHeap = loaded.minHeap;
            this.state = loaded.state;

            System.out.println("Loaded Huffman state from file.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error loading Huffman state from file.");
            return false;
        }
    }

    public String encodedMessage(String message, String key) {
        if (message == null || message.isEmpty()) {
            System.out.println("invalid Message");
            return null;
        }

        // Clear previous state before encoding new message
        frequencyMap.clear();
        codeMap.clear();
        minHeap.clear();

        String oldMessage = message;
        message = rle.adaptiveEncode(message);
        if (message.equals(oldMessage)) {
            state.setState(false);
        }

        for (char current : message.toCharArray()) {
            frequencyMap.put(current, frequencyMap.getOrDefault(current, 0) + 1);
        }

        createNodes();
        mergeNodes();
        getCodes();

        builder.setLength(0);
        for (char current : message.toCharArray()) {
            builder.append(codeMap.get(current));
        }
        String encodedMessage = builder.toString();


        // Save the current state to file after encoding
        saveState();

        return encodeAndDecodeWithKey(key, encodedMessage);
    }

    private void createNodes() {
        for (char current : frequencyMap.keySet()) {
            minHeap.insert(new Node(frequencyMap.get(current), current));
        }
    }

    private void mergeNodes() {
        while (minHeap.getSize() > 1) {
            Node left = minHeap.poll();
            Node right = minHeap.poll();
            Node mergedNode = new Node(left.getFrequency() + right.getFrequency(), left, right);
            minHeap.insert(mergedNode);
        }

    }
    public void displayHeap(){
        System.out.println(this.frequencyMap);
        minHeap.displayHeap();
        System.out.println(this.codeMap);
    }
    private void getCodes(){
        Node root = minHeap.peek();
        builder.setLength(0);
        getCodesHelper(root,builder);
    }
    private void getCodesHelper(Node node, StringBuilder builder) {
        if (node == null) return;

        // Leaf node: has character data
        if (node.getLeft() == null && node.getRight() == null) {
            codeMap.put(node.getData(), builder.toString());
            return;
        }

        // Traverse left
        builder.append('0');
        getCodesHelper(node.getLeft(), builder);
        builder.deleteCharAt(builder.length() - 1); // backtrack

        // Traverse right
        builder.append('1');
        getCodesHelper(node.getRight(), builder);
        builder.deleteCharAt(builder.length() - 1); // backtrack
    }
    private String encodeAndDecodeWithKey(String key, String message) {
        StringBuilder result = new StringBuilder(message.length());
        Random prng = new Random(key.hashCode());


        for (int i = 0; i < message.length(); i++) {
            char bit = message.charAt(i);
            int originalBit = bit - '0';
            int keyBit = prng.nextBoolean() ? 1 : 0;

            int xorResult = originalBit ^ keyBit;
            result.append((char) (xorResult + '0'));
        }

        return result.toString();

    }

    public String decoder(String message, String key) {
        String decryptedMessage = encodeAndDecodeWithKey(key, message);
        Node root = minHeap.peek();
        Node currentNode = root;
        StringBuilder builder = new StringBuilder();

        for (char current : decryptedMessage.toCharArray()) {
            if (current == '0') {
                currentNode = currentNode.getLeft();
            } else if (current == '1') {
                currentNode = currentNode.getRight();
            }

            // When we reach a leaf node
            if (currentNode.getLeft() == null && currentNode.getRight() == null) {
                builder.append(currentNode.getData());
                currentNode = root; // Reset for next character
            }
        }

        if (state.isState())
            return rle.decode(builder.toString());
        else
            return builder.toString();
    }

}
