package com.example.level1;

import javax.inject.Inject;

import com.jake.sword.Provides;

public class FooModule {
	@Inject
	public FooModule() {
	}
	
    @Provides
    public IFoo getFoo() {
        return new Foo(new Bar());
    }
}
