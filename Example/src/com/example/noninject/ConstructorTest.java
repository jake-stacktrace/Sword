package com.example.noninject;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class ConstructorTest {
	@Test
	public void testConstructor() {
		assertNotNull(SwordInjector.get(ConstructorClass.class).foo);
	}
}
