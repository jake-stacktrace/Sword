package com.example.noninject;

import javax.inject.Inject;

import com.example.level1.IFoo;

public class ConstructorClass {
	protected final IFoo foo;

	@Inject
	public ConstructorClass(IFoo foo) {
		this.foo = foo;
	}
}
