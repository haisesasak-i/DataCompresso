package com.example.datacompresso.Huffman;

public class Main {
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        String key = "secure123";
        String original = "Congratulations!!! Congratulations!!!";

        String encoded = huffman.encodedMessage(original, key);
        System.out.println("Encoded Message: " + encoded);

        String decoded = huffman.decoder(encoded, key);
        System.out.println("Decoded Message: " + decoded);

        System.out.println("Match: " + original.equals(decoded));
    }
}
