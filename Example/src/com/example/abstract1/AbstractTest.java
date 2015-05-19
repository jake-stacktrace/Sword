package com.example.abstract1;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class AbstractTest {
	@Test(expected=IllegalArgumentException.class)
	public void testAbstractSuperClassWithoutInjections() {
		SwordInjector.get(SubClassWithoutInjectionsOfAbstractClassWithInjections.class);
	}
	
	@Test
	public void testAbstractSuperClassWithInjections() {
		SubClassWithInjectionsOfAbstractClassWithInjections obj = SwordInjector.get(SubClassWithInjectionsOfAbstractClassWithInjections.class);
		assertNotNull(obj.foo);
		assertNotNull(obj.foo2);
	}
	
	public void testSubClassTwiceAnAbstractClassWithInjections() throws Exception {
		SubClassTwiceWithInjectionsOfAbstractClassWithInjections obj = SwordInjector.get(SubClassTwiceWithInjectionsOfAbstractClassWithInjections.class);
		assertNotNull(obj.foo);
		assertNotNull(obj.foo2);
		assertNotNull(obj.foo3);
	}
	
	@Test
	public void testSubClassofAbstractInjecting() throws Exception {
		assertNotNull(new SubClassofAbstractInjecting().foo);
	}
}
