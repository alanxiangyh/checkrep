package com.axiangyh.checkrep.demo;

import com.axiangyh.checkrep.annotation.CheckRep;

import java.util.function.Supplier;

@CheckRep
public class DemoStateRecord {

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

    public void empty() {

    }

    public void test() {
        int a = 10;

        Supplier<String> supplier = () -> "test";
        if (a == 10) {
            System.out.println(supplier.get());
            return;
        }
        System.out.println(1);
    }
}
