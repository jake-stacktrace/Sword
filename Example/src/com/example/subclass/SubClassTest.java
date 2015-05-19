package com.example.subclass;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class SubClassTest {
	@Test
	public void testInjectingSubClassWithBothClassesHavingInjections() throws Exception {
		SubClass subClass = new SubClass();
		assertNotNull(subClass.foo);
		assertNotNull(subClass.bar);
	}
	
	@Test
	public void testGettingSubClassWithBothClassesHavingInjections() throws Exception {
		SubClass subClass = SwordInjector.get(SubClass.class);
		assertNotNull(subClass.foo);
		assertNotNull(subClass.bar);
	}
}
