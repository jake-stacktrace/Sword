package com.example.overrides;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class OverridesTest {
	@Test
	public void testOverrideProvides() throws Exception {
		IProvidedThing providedThing = SwordInjector.get(IProvidedThing.class);
		assertTrue(providedThing instanceof OverridenProvidedThing);
	}
}

