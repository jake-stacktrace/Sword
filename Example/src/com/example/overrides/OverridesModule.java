package com.example.overrides;

import javax.inject.Inject;

import com.jake.sword.Provides;

public class OverridesModule {
	@Inject
	public OverridesModule() {
	}
	@Provides IProvidedThing getOriginalProductionValue() {
		return new OriginalProvidedThing();
	}
	
	@Provides(overrides=true) IProvidedThing getOverridenValueInTest() {
		return new OverridenProvidedThing();
	}
}