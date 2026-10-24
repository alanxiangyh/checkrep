package com.axiangyh.checkrep.demo;

import com.axiangyh.checkrep.annotation.CheckRep;

@CheckRep
public class ConstructorStateRecord {

    private final int value;

    public ConstructorStateRecord(int value) {
        this.value = value;
    }

    private void checkRep() {
        if (value < 0) {
            throw new IllegalStateException("value must be non-negative");
        }
    }

    public void test() {
        int a = 10;

        if (a == 10) {
            return;
        }
        System.out.println(1);
    }
}
