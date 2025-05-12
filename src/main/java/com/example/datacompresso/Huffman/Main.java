package com.example.datacompresso.Huffman;

public class Main {
    public static void main(String[] args) {
           Huffman huffman = new Huffman();
      String message =      huffman.encodedMessage("the quick brown fox jumps over the lazy dog and the rain in spain falls mainly on the plain while the mountains are covered with snow and the birds are chirping in the trees and the wind blows gently through the leaves\n","1234567890");
        System.out.println(message);
        System.out.println(huffman.decoder(message,"1234567890"));
    }
}
