package com.example.overrides;

import javax.inject.Inject;

import com.example.level1.Foo;

public class OriginalProvidedThing implements IProvidedThing {
	@Inject
	Foo foo;
}