package com.example.circular;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class CircularTest {
	@Test(expected=StackOverflowError.class)
	public void testCircularInjection() throws Exception {
		Circular circular = new Circular();
		SwordInjector.inject(circular);
	}
}
