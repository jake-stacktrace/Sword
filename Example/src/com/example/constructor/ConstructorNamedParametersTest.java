package com.example.constructor;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class ConstructorNamedParametersTest {
	@Test
	public void testConstructorNamedParameters() {
		assertEquals(Arrays.asList("Apple", "Alligator"), SwordInjector.get(ContainingClass.class).constructorNamedParameters.getaNames());
	}
}
