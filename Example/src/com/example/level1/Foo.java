package com.example.level1;

import javax.inject.Inject;

public class Foo implements IFoo {
	private final Bar bar;

	@Inject
	public Foo(Bar bar) {
		this.bar = bar;
	}
	@Override
	public Bar getBar() {
		return bar;
	}
}
