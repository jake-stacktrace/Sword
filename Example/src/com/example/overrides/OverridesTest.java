package com.example.overrides;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jake.sword.Provides;
import com.jake.sword.SwordInjector;

class OverridesModule {
	@Provides IProvidedThing getOriginalProductionValue() {
		return new OriginalProvidedThing();
	}
	
	@Provides(overrides=true) IProvidedThing getOverridenValueInTest() {
		return new OverridenProvidedThing();
	}
}
public class OverridesTest {
	@Test
	public void testOverrideProvides() throws Exception {
		IProvidedThing providedThing = SwordInjector.get(IProvidedThing.class);
		assertTrue(providedThing instanceof OverridenProvidedThing);
	}
}

