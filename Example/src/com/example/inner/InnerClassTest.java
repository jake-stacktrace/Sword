package com.example.inner;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class InnerClassTest {
	@Test
	public void testInnerClass() throws Exception {
		OuterClass innerClass = SwordInjector.get(OuterClass.class);
		assertNotNull(innerClass.staticInnerClass.innerInnerClass.foo);
		assertNotNull(innerClass.nonStaticInnerClass.innerInnerClass.foo);
	}
}
