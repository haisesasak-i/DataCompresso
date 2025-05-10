package com.example.datacompresso.Huffman;

import java.util.HashMap;

public class Huffman {
    private HashMap<Character, Integer> frequencyMap;
    private StringBuilder builder;
    private MinHeap minHeap;

    public Huffman() {
        this.frequencyMap = new HashMap<>();
        this.minHeap = new MinHeap();
        this.builder = new StringBuilder();
    }

    public String encodedMessage(String message) {

        if (message == null || message.isEmpty()) {
            System.out.println("invalid Message");
            return null;
        }
        for (char current : message.toCharArray()) {
            if (frequencyMap.containsKey(current)) {
                frequencyMap.replace(current, frequencyMap.get(current) + 1);
            } else {
                frequencyMap.put(current, 1);
            }
        }
        createNodes();
        return this.builder.toString();
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
    }
}
