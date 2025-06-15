# DataCompresso

**DataCompresso** is a Java-based file compression tool that supports two powerful algorithms: **Huffman Encoding** and **LZW**.  
It includes optional encryption and run-length encoding (RLE) for better compression.  
Built using JavaFX with a simple and custom UI — no FXML used.

---

## ✨ Features

- 📦 Compress & Decompress using:
    - Huffman Encoding (with RLE + XOR encryption)
    - LZW Compression
- 🔐 Secure your files with key-based XOR encryption
- 📁 Support for file and text input
- 📊 Shows compression stats
- 💾 Serialization support to save internal state
- 🎨 JavaFX-based GUI (custom scenes and effects)

---

## 🧠 Algorithms Used

### Huffman Encoding
- Classic tree-based encoding
- Supports RLE if repeated characters are found
- Output is XOR-encrypted using user’s key

### LZW Algorithm
- Dictionary-based compression
- Uses a custom list structure (`MyList.java`)

---

## 🧰 How to Use

1. **Launch the app** via `Main` or `HelloApplication`
2. Choose your algorithm on the **Algorithm Selection** screen
3. Select one of the following:
    - 📥 **Compress**: choose file or enter text, enter key, save compressed output
    - 📤 **Decompress**: select `.huff` or `.lzw` file, enter key, view or save result
4. Visual stats & progress bar are shown during compression/decompression


---

## 🔍 What I Learned

This project helped me understand how real compression works — from tree structures in Huffman to dictionary management in LZW.

I also learned about:
- Serialization in Java
- File I/O and custom encryption
- Building GUIs using **JavaFX (without FXML)**

It’s not just theory — this actually works on real files and gives decent results.  
I feel proud to have turned an idea into a working tool.

---

## 🚧 To Do

- [ ] Improve encryption (AES or more secure)
- [ ] Add drag-and-drop file support
- [ ] Polish LZW UI and testing
- [ ] Export stats to text or CSV
- [ ] Add CLI support (optional)

---

## 📜 License

Free to use and modify. Made for learning, testing, and fun.
