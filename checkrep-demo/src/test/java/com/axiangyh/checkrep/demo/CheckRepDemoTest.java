package com.axiangyh.checkrep.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class CheckRepDemoTest {

    @Test
    void checkRepClassIsCheckedInTestState() {
        DemoStateRecord record = new DemoStateRecord();
        assertEquals(0, record.value());
        record.setValue(1);
        assertThrows(IllegalStateException.class, () -> record.setValue(-1));

        try {
            Field f = record.getClass().getDeclaredField("value");
            f.setAccessible(true);
            f.set(record, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThrows(IllegalStateException.class, record::empty);
        assertThrows(IllegalStateException.class, record::value);
    }

    @Test
    void checkRepClassIsCheckedAtConstructorEnd() {
        assertThrows(IllegalStateException.class, () -> new ConstructorStateRecord(-1));
    }

    /*
    @Test
    void verifyRepMethodIsCheckedInReleaseState() {
        ReleaseStateRecord record = new ReleaseStateRecord();
        assertThrows(IllegalStateException.class, () -> record.setValue(-1));
    }

     */
}
