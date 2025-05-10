package com.example.datacompresso.Huffman;

import java.util.HashMap;

public class Huffman {
    private HashMap<Character, Integer> frequencyMap;
    private MinHeap minHeap;
    public Huffman(){
        this.frequencyMap = new HashMap<>();
        this.minHeap = new MinHeap();
    }
    public String encodedMessage(String message){
        if(message==null||message.isEmpty()){
            System.out.println("invalid Message");
            return  null;
        }
        for(char current:message.toCharArray()){
            if(frequencyMap.containsKey(current)){
                frequencyMap.replace(current,frequencyMap.get(current)+1);
            }
            else {
                frequencyMap.put(current,1);
            }
        }
    }
}
