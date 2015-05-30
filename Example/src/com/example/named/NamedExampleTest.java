package com.example.named;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class NamedExampleTest {
    @Test
    public void testNamed() {
        NamedExample namedExample = SwordInjector.get(NamedExample.class);
        assertEquals(Arrays.asList("Apple", "Alligator"), namedExample.aWords);
        assertEquals(Arrays.asList("Bread", "Blue"), namedExample.bWords);
    }
    
    @Test
	public void testNamedBind() throws Exception {
		NamedBindExample namedBindExample = SwordInjector.get(NamedBindExample.class);
		assertTrue(namedBindExample.namedBind1 instanceof NamedBindExample.NamedBind1);
		assertTrue(namedBindExample.namedBind2 instanceof NamedBindExample.NamedBind2);
	}
}
