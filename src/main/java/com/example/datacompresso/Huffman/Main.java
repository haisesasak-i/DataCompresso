package com.example.datacompresso.Huffman;

public class Main {
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        System.out.println(huffman.encodedMessage("abbcccdddd","lol"));
        huffman.displayHeap();
    }
}
