package com.example.abstract1;

import javax.inject.Inject;

import com.example.level1.IFoo;

public class SubClassofAbstractInjecting extends AbstractInjecting {
	@Inject
	IFoo foo;
}
