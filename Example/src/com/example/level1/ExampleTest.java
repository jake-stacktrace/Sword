package com.example.level1;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class ExampleTest {
    @Test
    public void testExample() {
        Example example = new Example();
        SwordInjector.inject(example);
        assertNotNull(example.fooImpl);
        assertNotNull(example.foo);
    }
}
