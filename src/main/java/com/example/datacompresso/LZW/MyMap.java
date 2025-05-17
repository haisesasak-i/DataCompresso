package com.example.datacompresso.LZW;
public class MyMap {
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
            if (keys[i].equals(key)) {
                return values[i];
            }
        }
        return null;
    }

    public boolean containsKey(String key) {
        for (int i = 0; i < count; i++) {
            if (keys[i].equals(key)) {
                return true;
            }
        }
        return false;
    }
}