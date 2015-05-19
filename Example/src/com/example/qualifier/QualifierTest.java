package com.example.qualifier;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class QualifierTest {
	@Test
	public void testQualifiers() {
		QualifierExample qualifierExample = SwordInjector.get(QualifierExample.class);
		assertEquals("regular", qualifierExample.regular);
		assertEquals("decaf", qualifierExample.decaf);
	}
}
