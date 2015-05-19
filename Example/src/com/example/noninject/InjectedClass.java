package com.example.noninject;

import javax.inject.Inject;

import com.example.level1.IFoo;

public class InjectedClass {
	@Inject
	protected IFoo foo;
}
