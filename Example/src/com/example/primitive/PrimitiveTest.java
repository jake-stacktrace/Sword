package com.example.primitive;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class PrimitiveTest {
	@Test
	public void testPrimitives() {
		Primitive primitive = SwordInjector.get(Primitive.class);
		assertEquals(3, primitive.getInteger1());
		assertEquals(4, primitive.getInteger2().intValue());
		assertEquals((byte)1, primitive.getByte());
		assertEquals((byte)2, primitive.getByte2().byteValue());
		assertEquals('a', primitive.getChar1());
		assertEquals('b', primitive.getChar2().charValue());
	}
}
