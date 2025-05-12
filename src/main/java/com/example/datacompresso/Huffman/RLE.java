package com.example.datacompresso.Huffman;

public class RLE {
    private StringBuilder builder;
    public RLE(){
        this.builder = new StringBuilder();
    }
    public String encode(String message) {
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
}
