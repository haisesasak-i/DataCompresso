package com.example.datacompresso.Huffman;

import java.util.HashMap;

public class Huffman {
   private RLE rle;
    private HashMap<Character, Integer> frequencyMap;
    private HashMap<Character, String> codeMap;
    private StringBuilder builder;
    private MinHeap minHeap;
    private StateForAdaptiveEncoding state;

    public Huffman() {
        rle = new RLE();
        this.frequencyMap = new HashMap<>();
        this.minHeap = new MinHeap();
        this.builder = new StringBuilder();
        this.codeMap = new HashMap<>();
        state = new StateForAdaptiveEncoding();
    }

    public String encodedMessage(String message,String key) {

        if (message == null || message.isEmpty()) {
            System.out.println("invalid Message");
            return null;
        }
        String oldMessage = message;
        message = rle.adaptiveEncode(message);
        if(message.equals(oldMessage)){
            state.setState(false);
        }
        for (char current : message.toCharArray()) {
            if (frequencyMap.containsKey(current)) {
                frequencyMap.replace(current, frequencyMap.get(current) + 1);
            } else {
                frequencyMap.put(current, 1);
            }
        }
        createNodes();
        mergeNodes();
        getCodes();
        builder.setLength(0);
        for (char current : message.toCharArray()) {
            builder.append(codeMap.get(current));
        }
        String encodedMessage = builder.toString();

        return encodeAndDecodeWithKey(key,encodedMessage);
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
    private String encodeAndDecodeWithKey(String key, String message){
        builder.setLength(0);
        for(int i =0;i<message.length();i++){
            char messageChar = message.charAt(i);
            char keyChar = key.charAt(i%key.length());
            char encodedChar = (char) (messageChar ^ keyChar);
            builder.append(encodedChar);
        }
        return builder.toString();
    }
    public String decoder(String message, String key) {
        String decryptedMessage = encodeAndDecodeWithKey(key, message);
        Node root = minHeap.peek();
        StringBuilder builder = new StringBuilder();

        for (char current : decryptedMessage.toCharArray()) {
            if (root.getData() != null) {
                builder.append(root.getData());
                root = minHeap.peek();
            }

            if (current == '0') {
                root = root.getLeft();
            } else if (current == '1') {
                root = root.getRight();
            }
        }

        if (root.getData() != null) {
            builder.append(root.getData());
        }
        if(state.isState())
            return rle.decode(builder.toString());
        else
            return builder.toString();
    }

}
