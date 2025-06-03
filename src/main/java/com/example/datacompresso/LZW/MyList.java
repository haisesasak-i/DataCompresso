package com.example.datacompresso.LZW;

import java.io.Serializable;

// Custom dynamic array implementation
public class MyList implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[] data = new int[100];
    private int size = 0;

    public void add(int value) {
        if (size == data.length) {
            int[] newData = new int[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
        data[size++] = value;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return data[index];
    }

    public int size() {
        return size;
    }

    public int[] toArray() {
        int[] result = new int[size];
        System.arraycopy(data, 0, result, 0, size);
        return result;
    }

    public void clear() {
        size = 0;
    }
}

// Custom hash map implementation
class MyMap implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int SIZE = 4096;
    private String[] keys = new String[SIZE];
    private int[] values = new int[SIZE];
    private int count = 0;

    public void put(String key, int value) {
        if (count >= SIZE) return;
        for (int i = 0; i < count; i++) {
            if (keys[i].equals(key)) {
                values[i] = value;
                return;
            }
        }
        keys[count] = key;
        values[count] = value;
        count++;
    }

    public Integer get(String key) {
        for (int i = 0; i < count; i++) {
            if (keys[i] != null && keys[i].equals(key)) {
                return values[i];
            }
        }
        return null;
    }

    public boolean containsKey(String key) {
        for (int i = 0; i < count; i++) {
            if (keys[i] != null && keys[i].equals(key)) {
                return true;
            }
        }
        return false;
    }
}