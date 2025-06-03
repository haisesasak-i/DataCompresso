package com.example.datacompresso.LZW;

import java.io.*;
import java.util.*;

public class LZWAlgorithm implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SERIAL_FILE = "lzw_state.ser";

    private byte[] compressedData;
    private int originalSize;
    private List<Integer> codes;

    public LZWAlgorithm() {
        loadState();
    }

    // Updated compress method to match UI expectations
    public byte[] compress(byte[] input, MyList sharedCodeList) {
        if (input == null || input.length == 0) {
            return new byte[0];
        }

        // Clear the shared code list for fresh compression
        if (sharedCodeList != null) {
            sharedCodeList.clear();
        }

        Map<String, Integer> dictionary = new HashMap<>();
        int dictSize = 256;

        // Initialize dictionary with single characters (0-255)
        for (int i = 0; i < 256; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }

        List<Integer> result = new ArrayList<>();
        String current = "";

        // Main LZW compression loop
        for (byte b : input) {
            char ch = (char) (b & 0xFF);
            String combined = current + ch;

            if (dictionary.containsKey(combined)) {
                current = combined;
            } else {
                // Output the code for current string
                int code = dictionary.get(current);
                result.add(code);

                // Add to shared code list if provided
                if (sharedCodeList != null) {
                    sharedCodeList.add(code);
                }

                // Add new pattern to dictionary if not full
                if (dictSize < 65536) { // 16-bit limit
                    dictionary.put(combined, dictSize++);
                }
                current = String.valueOf(ch);
            }
        }

        // Don't forget the last string
        if (!current.isEmpty()) {
            int code = dictionary.get(current);
            result.add(code);
            if (sharedCodeList != null) {
                sharedCodeList.add(code);
            }
        }

        this.codes = result;
        this.originalSize = input.length;

        // Use variable bit width encoding
        byte[] compressed = encodeWithVariableBits(result);

        this.compressedData = compressed;
        saveState();
        return compressed;
    }

    // Overloaded method for backward compatibility
    public byte[] compress(byte[] input) {
        return compress(input, null);
    }

    private byte[] encodeWithVariableBits(List<Integer> codes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitOutputStream bos = new BitOutputStream(baos);

        // Find maximum code value to determine bits needed
        int maxCode = 255; // Start with minimum (8 bits for initial dictionary)
        for (int code : codes) {
            maxCode = Math.max(maxCode, code);
        }

        // Determine bits needed - start with 9 bits minimum for LZW
        int bitsNeeded = Math.max(9, Integer.SIZE - Integer.numberOfLeadingZeros(maxCode));
        bitsNeeded = Math.min(bitsNeeded, 16); // Cap at 16 bits

        try {
            // Write header info
            bos.writeBits(bitsNeeded, 5); // 5 bits for bit width (9-16)
            bos.writeBits(codes.size(), 24); // 24 bits for code count

            // Write all codes using the determined bit width
            for (int code : codes) {
                bos.writeBits(code, bitsNeeded);
            }

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] decompress(byte[] compressed) {
        if (compressed == null || compressed.length == 0) {
            return new byte[0];
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
            BitInputStream bis = new BitInputStream(bais);

            // Read header
            int bitsPerCode = bis.readBits(5);
            int codeCount = bis.readBits(24);

            // Read codes
            List<Integer> codes = new ArrayList<>();
            for (int i = 0; i < codeCount; i++) {
                codes.add(bis.readBits(bitsPerCode));
            }

            bis.close();

            return decompressCodes(codes);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] decompressCodes(List<Integer> codes) {
        if (codes.isEmpty()) {
            return new byte[0];
        }

        Map<Integer, String> dictionary = new HashMap<>();
        int dictSize = 256;

        // Initialize dictionary with single characters
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        // Get first code
        int firstCode = codes.get(0);
        String previous = dictionary.get(firstCode);
        if (previous == null) {
            throw new IllegalArgumentException("Invalid first code: " + firstCode);
        }

        result.write(previous.getBytes(), 0, previous.length());

        // Process remaining codes
        for (int i = 1; i < codes.size(); i++) {
            int code = codes.get(i);
            String entry;

            if (dictionary.containsKey(code)) {
                entry = dictionary.get(code);
            } else if (code == dictSize) {
                // Special case: code not in dictionary yet
                entry = previous + previous.charAt(0);
            } else {
                throw new IllegalArgumentException("Invalid code: " + code + " at position " + i);
            }

            result.write(entry.getBytes(), 0, entry.length());

            // Add new pattern to dictionary if not full
            if (dictSize < 65536 && previous.length() > 0) {
                dictionary.put(dictSize++, previous + entry.charAt(0));
            }

            previous = entry;
        }

        return result.toByteArray();
    }

    // Bit-level I/O helper classes
    private static class BitOutputStream {
        private OutputStream out;
        private int buffer = 0;
        private int bitsInBuffer = 0;

        public BitOutputStream(OutputStream out) {
            this.out = out;
        }

        public void writeBits(int value, int bits) throws IOException {
            while (bits > 0) {
                int bitsToWrite = Math.min(bits, 8 - bitsInBuffer);
                int mask = (1 << bitsToWrite) - 1;
                int shiftedValue = (value >> (bits - bitsToWrite)) & mask;
                buffer |= shiftedValue << (8 - bitsInBuffer - bitsToWrite);
                bitsInBuffer += bitsToWrite;
                bits -= bitsToWrite;

                if (bitsInBuffer == 8) {
                    out.write(buffer);
                    buffer = 0;
                    bitsInBuffer = 0;
                }
            }
        }

        public void close() throws IOException {
            if (bitsInBuffer > 0) {
                out.write(buffer);
            }
            out.close();
        }
    }

    private static class BitInputStream {
        private InputStream in;
        private int buffer = 0;
        private int bitsInBuffer = 0;

        public BitInputStream(InputStream in) {
            this.in = in;
        }

        public int readBits(int bits) throws IOException {
            int result = 0;
            while (bits > 0) {
                if (bitsInBuffer == 0) {
                    buffer = in.read();
                    if (buffer == -1) throw new IOException("Unexpected end of stream");
                    bitsInBuffer = 8;
                }

                int bitsToRead = Math.min(bits, bitsInBuffer);
                int mask = (1 << bitsToRead) - 1;
                int shiftedBits = (buffer >> (bitsInBuffer - bitsToRead)) & mask;
                result = (result << bitsToRead) | shiftedBits;
                bitsInBuffer -= bitsToRead;
                bits -= bitsToRead;
            }
            return result;
        }

        public void close() throws IOException {
            in.close();
        }
    }

    public void saveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIAL_FILE))) {
            oos.writeObject(this);
            System.out.println("LZW state saved.");
        } catch (IOException e) {
            System.err.println("Error saving LZW state: " + e.getMessage());
        }
    }

    public boolean loadState() {
        File file = new File(SERIAL_FILE);
        if (!file.exists()) return false;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            LZWAlgorithm loaded = (LZWAlgorithm) ois.readObject();
            this.compressedData = loaded.compressedData;
            this.originalSize = loaded.originalSize;
            this.codes = loaded.codes;
            System.out.println("LZW state loaded.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading LZW state: " + e.getMessage());
            return false;
        }
    }

    // Calculate compression ratio
    public double getCompressionRatio() {
        if (originalSize == 0 || compressedData == null) return 0;
        return (double) compressedData.length / originalSize;
    }

    // Calculate space saved in bytes
    public long getSpaceSaved() {
        if (originalSize == 0 || compressedData == null) return 0;
        return originalSize - compressedData.length;
    }

    // Get compression percentage
    public double getCompressionPercentage() {
        if (originalSize == 0 || compressedData == null) return 0;
        return ((double) (originalSize - compressedData.length) / originalSize) * 100;
    }

    // Getters
    public byte[] getCompressedData() {
        return compressedData;
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public List<Integer> getCodes() {
        return codes;
    }
}