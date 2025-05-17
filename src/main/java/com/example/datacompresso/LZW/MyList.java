package  com.example.datacompresso.LZW;
public class MyList {
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
}