package com.example.subclass;

import javax.inject.Inject;

import com.example.level1.Bar;

public class SubClass extends InjectingSuperClass {
	@Inject
	Bar bar;
}
