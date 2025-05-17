// MyMap.java

package com.example.datacompresso.LZW;
// MyList.java


// LZWAlgorithm.java
import java.io.ByteArrayOutputStream;

public class LZWAlgorithm {
    public static byte[] compress(byte[] input, MyList codeList) {
        int dictSize = 256;
        MyMap dictionary = new MyMap();
        for (int i = 0; i < 256; i++) {
            dictionary.put("" + (char) i, i);
        }

        String current = "";
        for (byte b : input) {
            char ch = (char) (b & 0xFF);
            String combined = current + ch;
            if (dictionary.containsKey(combined)) {
                current = combined;
            } else {
                codeList.add(dictionary.get(current));
                dictionary.put(combined, dictSize++);
                current = "" + ch;
            }
        }

        if (!current.equals("")) {
            codeList.add(dictionary.get(current));
        }

        int totalBits = codeList.size() * 12;
        byte[] output = new byte[(totalBits + 7) / 8];
        int bitIndex = 0;

        for (int i = 0; i < codeList.size(); i++) {
            int code = codeList.get(i);
            for (int j = 11; j >= 0; j--) {
                int bit = (code >> j) & 1;
                int byteIndex = bitIndex / 8;
                int bitPos = 7 - (bitIndex % 8);
                if (bit == 1) {
                    output[byteIndex] |= (1 << bitPos);
                }
                bitIndex++;
            }
        }

        return output;
    }

    public static byte[] decompress(byte[] compressed, int codeCount) {
        int dictSize = 256;
        String[] dictionary = new String[4096];
        for (int i = 0; i < 256; i++) {
            dictionary[i] = "" + (char) i;
        }

        int[] codes = new int[codeCount];
        int bitIndex = 0;

        for (int i = 0; i < codeCount; i++) {
            int code = 0;
            for (int j = 0; j < 12; j++) {
                int byteIndex = bitIndex / 8;
                int bitPos = 7 - (bitIndex % 8);
                int bit = (compressed[byteIndex] >> bitPos) & 1;
                code = (code << 1) | bit;
                bitIndex++;
            }
            codes[i] = code;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String w = dictionary[codes[0]];
        for (char c : w.toCharArray()) out.write((byte) c);

        for (int i = 1; i < codeCount; i++) {
            String entry;
            int k = codes[i];

            if (dictionary[k] != null) {
                entry = dictionary[k];
            } else if (k == dictSize) {
                entry = w + w.charAt(0);
            } else {
                throw new IllegalArgumentException("Bad compressed k: " + k);
            }

            for (char c : entry.toCharArray()) out.write((byte) c);

            if (dictSize < 4096) {
                dictionary[dictSize++] = w + entry.charAt(0);
            }
            w = entry;
        }

        return out.toByteArray();
    }
}

