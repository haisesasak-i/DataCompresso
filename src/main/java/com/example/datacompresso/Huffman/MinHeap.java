package com.example.datacompresso.Huffman;

import java.util.ArrayList;

public class MinHeap {
    private ArrayList<Node> heap;
    public MinHeap(){
        heap = new ArrayList<>();
    }
    private int getParent(int index){
        return (index-1)/2;
    }
    private int getLeftChild(int index){
        return 2*index+1;
    }
    private int getRightChild(int index){
        return 2*index+2;
    }
    private boolean isEmpty(){
        return heap.isEmpty();
    }
    public void insert(Node node){
        this.heap.add(node);
        heapUP(heap.size()-1);

    }
    private void heapUP(int index){
        while (index > 0){
            int parentIndex = getParent(index);
            Node parent = heap.get(parentIndex);
            Node child = heap.get(index);
            if (parent.getFrequency() > child.getFrequency()){
                heap.set(parentIndex, child);
                heap.set(index, parent);
                index = parentIndex;
            }else{
                break;
            }
        }
    }
    public Node poll(){
        if(isEmpty()){
            System.out.println("heap is empty");
            return null;
        }
        Node first = heap.getFirst();
        heap.set(0,heap.getLast());
        heap.removeLast();
        heapDown(0);
        return first;
    }
    private  void heapDown(int index){
        while (getLeftChild(index) < heap.size() ){
            int smallerChildIndex = getSmallerChildIndex(index);
            Node smallerChild = heap.get(smallerChildIndex);
            if(smallerChild.getFrequency() < heap.get(index).getFrequency()){
                heap.set(smallerChildIndex,heap.get(index));
                heap.set(index,smallerChild);
                index = smallerChildIndex;
            }
            else{
                break;
            }
        }
    }
    private int getSmallerChildIndex(int index){
        int leftChildIndex = getLeftChild(index);
        if(getRightChild(index)>heap.size()-1){
            return  leftChildIndex;
        }
        int rightChildIndex = getRightChild(index);
        if (heap.get(leftChildIndex).getFrequency() < heap.get(rightChildIndex).getFrequency()){
            return leftChildIndex;
        }
        return rightChildIndex;

    }
    public Node peek(){
        if (!isEmpty()){
           return  heap.getFirst();
        }else{
          return null;
        }
    }
}
