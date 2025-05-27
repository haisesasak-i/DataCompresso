package com.example.datacompresso.Huffman;

import java.io.Serializable;

public class Node implements Serializable {
    private static final long serialVersionUID = 1L;
    private  int frequency;
    private  Character data;
    private  Node left;
    private  Node right;
    public Node(int frequency, Character data) {
        this.frequency = frequency;
        this.data = data;
    }
    public  Node (int frequency, Node left, Node right){
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Character getData() {
        return data;
    }

    public void setData(Character data) {
        this.data = data;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "Node{" +
                "frequency=" + frequency +
                ", data=" + data +
                ", left=" + left +
                ", right=" + right +
                '}';
    }

}
