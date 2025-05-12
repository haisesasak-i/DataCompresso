package com.example.datacompresso.Huffman;

public class Main {
    public static void main(String[] args) {
            RLE rle = new RLE();
        String message = "aaabbccccc";
        System.out.println(rle.decode(rle.encode(message)));
    }
}
