package com.example.datacompresso.Huffman;

public class Main {
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        huffman.encodedMessage("abbcccdddd");
        huffman.displayHeap();
    }
}
