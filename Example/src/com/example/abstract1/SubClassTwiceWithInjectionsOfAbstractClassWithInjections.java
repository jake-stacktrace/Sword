package com.example.abstract1;

import javax.inject.Inject;

import com.example.level1.IFoo;

public class SubClassTwiceWithInjectionsOfAbstractClassWithInjections extends SubClassWithInjectionsOfAbstractClassWithInjections {
	@Inject
	IFoo foo3;
}
