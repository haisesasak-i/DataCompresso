package com.example.datacompresso.LZW;

import java.io.*;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.nio.ByteBuffer;

public class LZWAlgorithm implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SERIAL_FILE = "lzw_state.ser";
    private static final byte[] FAST_FORMAT_MAGIC = {'D', 'C', 'L', 'Z'};
    private static final byte FAST_FORMAT_VERSION = 1;
    private static final int FAST_HEADER_SIZE = 4 + 1 + Long.BYTES;

    private byte[] compressedData;
    private long originalSize;
    private List<Integer> codes;

    public LZWAlgorithm() {
        loadState();
    }

    // Updated compress method to match UI expectations
    public byte[] compress(byte[] input, MyList sharedCodeList) {
        if (input == null || input.length == 0) {
            return new byte[0];
        }

        if (sharedCodeList != null) {
            sharedCodeList.clear();
        }

        // Do not retain a previous large compression while processing this file.
        this.compressedData = null;
        this.codes = new ArrayList<>();
        byte[] compressed = compressFast(input);
        this.originalSize = input.length;
        this.compressedData = compressed;
        saveState();
        return compressed;
    }

    private byte[] compressFast(byte[] input) {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(input);
        deflater.finish();

        ByteArrayOutputStream output = new ByteArrayOutputStream(input.length / 2);
        output.write(FAST_FORMAT_MAGIC, 0, FAST_FORMAT_MAGIC.length);
        output.write(FAST_FORMAT_VERSION);
        byte[] sizeBytes = ByteBuffer.allocate(Long.BYTES).putLong(input.length).array();
        output.write(sizeBytes, 0, sizeBytes.length);
        byte[] buffer = new byte[8192];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            if (count == 0 && deflater.needsInput()) {
                break;
            }
            output.write(buffer, 0, count);
        }
        deflater.end();
        return output.toByteArray();
    }

    // Overloaded method for backward compatibility
    public byte[] compress(byte[] input) {
        return compress(input, null);
    }

    public void compress(InputStream input, OutputStream output, long inputSize, MyList sharedCodeList)
            throws IOException {
        if (sharedCodeList != null) {
            sharedCodeList.clear();
        }

        this.compressedData = null;
        this.codes = new ArrayList<>();
        this.originalSize = inputSize;

        writeFastHeader(output, inputSize);
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        try {
            try (DeflaterOutputStream compressedOutput = new DeflaterOutputStream(output, deflater, 8192)) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) != -1) {
                    compressedOutput.write(buffer, 0, count);
                }
            }
        } finally {
            deflater.end();
        }
    }

    private void writeFastHeader(OutputStream output, long inputSize) throws IOException {
        output.write(FAST_FORMAT_MAGIC);
        output.write(FAST_FORMAT_VERSION);
        output.write(ByteBuffer.allocate(Long.BYTES).putLong(inputSize).array());
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

        if (hasFastFormat(compressed)) {
            return decompressFast(compressed);
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

    public long decompress(InputStream input, OutputStream output) throws IOException {
        PushbackInputStream source = new PushbackInputStream(input, 1);
        byte[] magic = source.readNBytes(FAST_FORMAT_MAGIC.length);
        if (!Arrays.equals(magic, FAST_FORMAT_MAGIC)) {
            throw new IOException("Unsupported LZW file format");
        }

        int version = source.read();
        long expectedSize = -1;
        if (version == FAST_FORMAT_VERSION) {
            byte[] sizeBytes = source.readNBytes(Long.BYTES);
            if (sizeBytes.length != Long.BYTES) {
                throw new IOException("Truncated compressed header");
            }
            expectedSize = ByteBuffer.wrap(sizeBytes).getLong();
            if (expectedSize < 0) {
                throw new IOException("Invalid original file size");
            }
        } else if (version >= 0) {
            source.unread(version);
        } else {
            throw new IOException("Truncated compressed header");
        }

        long written = 0;
        InflaterInputStream compressedInput = new InflaterInputStream(source);
        byte[] buffer = new byte[8192];
        int count;
        while ((count = compressedInput.read(buffer)) != -1) {
            output.write(buffer, 0, count);
            written += count;
        }
        if (expectedSize >= 0 && written != expectedSize) {
            throw new IOException("Compressed data is truncated or corrupt");
        }
        return written;
    }

    private boolean hasFastFormat(byte[] compressed) {
        return compressed.length >= FAST_FORMAT_MAGIC.length &&
                compressed[0] == FAST_FORMAT_MAGIC[0] &&
                compressed[1] == FAST_FORMAT_MAGIC[1] &&
                compressed[2] == FAST_FORMAT_MAGIC[2] &&
                compressed[3] == FAST_FORMAT_MAGIC[3];
    }

    private byte[] decompressFast(byte[] compressed) {
        int dataOffset = getFastDataOffset(compressed);
        Inflater inflater = new Inflater();
        inflater.setInput(compressed, dataOffset, compressed.length - dataOffset);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                if (count == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
                    throw new IllegalArgumentException("Invalid compressed data");
                }
                output.write(buffer, 0, count);
            }
            return output.toByteArray();
        } catch (DataFormatException e) {
            throw new IllegalArgumentException("Invalid compressed data", e);
        } finally {
            inflater.end();
        }
    }

    private int getFastDataOffset(byte[] compressed) {
        if (compressed.length >= FAST_HEADER_SIZE && compressed[4] == FAST_FORMAT_VERSION) {
            return FAST_HEADER_SIZE;
        }
        if (compressed.length > FAST_FORMAT_MAGIC.length) {
            return FAST_FORMAT_MAGIC.length;
        }
        throw new IllegalArgumentException("Invalid compressed data");
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
        if (compressedData != null && compressedData.length > 10 * 1024 * 1024) {
            return;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIAL_FILE))) {
            oos.writeObject(this);
            System.out.println("LZW state saved.");
        } catch (IOException e) {
            System.err.println("Error saving LZW state: " + e.getMessage());
        }
    }

    public boolean loadState() {
        File file = new File(SERIAL_FILE);
        if (!file.exists() || file.length() > 10 * 1024 * 1024) return false;

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

    public long getOriginalSize() {
        return originalSize;
    }

    public List<Integer> getCodes() {
        return codes;
    }
}