package com.example.overrides;

import javax.inject.Inject;

import com.example.level1.Foo;

public class OverridenProvidedThing implements IProvidedThing {
	@Inject
	Foo foo;
}