package com.example.level1;

import javax.inject.Inject;

public class Example implements IExample {
    @Inject
    protected Foo fooImpl;
    @Inject
    protected IFoo foo;

    public Foo getFooImpl() {
        return fooImpl;
    }

	public IFoo getFoo() {
		return foo;
	}
}
