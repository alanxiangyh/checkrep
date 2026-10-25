package com.axiangyh.checkrep.demo.queue;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FasterQueueTest {

    @Test
    void threeStudentsAreServedInTheOrderTheyJoined() {
        FasterQueue queue = new FasterQueue();
        queue.join("ada");
        queue.join("bob");
        queue.join("eve");
        assertEquals("ada", queue.next());
        assertEquals(List.of("bob", "eve"), queue.waiting());
        assertEquals("bob", queue.next());
        assertEquals("eve", queue.next());

        FasterQueue queue2 = new FasterQueue();
        queue2.join("ada");
        queue2.join("bob");
        queue2.joinBuggy("eve");
        assertEquals("ada", queue2.next());
        assertEquals("bob", queue2.next());
        assertEquals("eve", queue2.next());
    }
}