package com.jake.example.errors.fields;

import com.jake.sword.Provides;

public class OverridesWithoutOriginalProvides {
	@Provides(overrides=true)
	public Foo2 generateFoo() {
		return new Foo2();
	}
}
