package com.example.datacompresso.Huffman;

import java.io.Serializable;

public class StateForAdaptiveEncoding  implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
