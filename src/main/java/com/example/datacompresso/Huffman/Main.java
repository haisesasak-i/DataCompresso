package com.example.datacompresso.Huffman;

public class Main {
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        String endcodedMessage = huffman.encodedMessage("Hello my name is aqib","lol");
        huffman.displayHeap();
        System.out.println(huffman.decoder(endcodedMessage,"lol"));
    }
}
