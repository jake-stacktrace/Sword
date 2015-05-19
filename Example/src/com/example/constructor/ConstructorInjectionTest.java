package com.example.constructor;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class ConstructorInjectionTest {
	@Test
	public void testConstructorInjection() {
		assertNotNull(SwordInjector.get(ContainingClass.class).constructorInjection.getExample());
		assertNotNull(SwordInjector.get(ConstructorInjection.class).getExample());
	}
}
