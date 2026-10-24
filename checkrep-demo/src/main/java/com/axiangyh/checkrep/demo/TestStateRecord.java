package com.axiangyh.checkrep.demo;

import com.axiangyh.checkrep.annotation.CheckRep;

@CheckRep
public class TestStateRecord {

    private int value;

    public void setValue(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    private void checkRep() {
        if (value < 0) {
            throw new IllegalStateException("value must be non-negative");
        }
    }
}
