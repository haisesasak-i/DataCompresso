package com.example.datacompresso.Huffman;

import java.io.Serial;
import java.io.Serializable;

public class RLE  implements Serializable {
    private static final long serialVersionUID = 1L;
    private StringBuilder builder;
    public RLE(){
        this.builder = new StringBuilder();
    }
    public String adaptiveEncode(String message) {
        int repeats = 0;
        for (int i = 1; i < message.length(); i++)
            if (message.charAt(i) == message.charAt(i - 1)) repeats++;
        return (repeats >= message.length() * 0.2) ? encode(message) : message;
    }
    public String encode(String message) {
        builder.setLength(0);
        int count = 1;
        for(int i =0;i<message.length()-1;i++){
            if(message.charAt(i) == message.charAt(i+1)){
                count++;
            }else{
                builder.append(message.charAt(i));
                builder.append(count);
                count = 1;
            }
        }
        builder.append(message.charAt(message.length()-1));
        builder.append(count);

        return builder.toString();
    }
    public String decode(String message){
        int index = 0; // Keeps track of the character to repeat
        builder.setLength(0); // Reset StringBuilder to store the decoded message

        // Iterate through the encoded message
        for(int i = 0; i < message.length(); i++) {
            // Check if current character is a digit (part of the run-length count)
            if (Character.isDigit(message.charAt(i))) {
                StringBuilder digit = new StringBuilder("" + message.charAt(i));

                // Handle multiple digits in the count (e.g., '12' for 12 repetitions)
                while (i + 1 < message.length() && Character.isDigit(message.charAt(i + 1))) {
                    digit.append(message.charAt(++i));
                }

                // Repeat the character the number of times specified by the count
                for (int j = 0; j < Integer.parseInt(digit.toString()); j++) {
                    builder.append(message.charAt(index)); // Append the character at 'index'
                }
            } else {
                // When we encounter a character (not a digit), update the index to this character
                index = i;
            }
        }

        return builder.toString();
    }


}
