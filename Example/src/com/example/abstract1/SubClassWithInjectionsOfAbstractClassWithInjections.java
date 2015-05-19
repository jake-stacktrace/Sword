package com.example.abstract1;

import javax.inject.Inject;

import com.example.level1.IFoo;

public class SubClassWithInjectionsOfAbstractClassWithInjections extends AbstractWithInjections {
	@Inject
	IFoo foo2;
}
