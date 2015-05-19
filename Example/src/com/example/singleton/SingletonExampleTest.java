package com.example.singleton;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class SingletonExampleTest {
    @Test
    public void testSingletonExample() {
        SingletonExample singleton1 = SwordInjector.get(SingletonExample.class);
        SingletonExample singleton2 = SwordInjector.get(SingletonExample.class);
        assertSame(singleton1, singleton2);
    }
}
