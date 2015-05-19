package com.example.subclass;

import javax.inject.Inject;

import com.example.level1.IFoo;
import com.jake.sword.SwordInjector;


public class InjectingSuperClass {
	@Inject
	protected IFoo foo;
	
	InjectingSuperClass() {
		SwordInjector.inject(this);
	}
}
