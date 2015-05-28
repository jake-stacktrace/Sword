package com.jake.example.errors.fields;

import javax.inject.Inject;

public class MultipleInjectConstructors {
	@Inject
	public MultipleInjectConstructors() {
	}
	@Inject
	public MultipleInjectConstructors(IFoo foo) {
	}
}
