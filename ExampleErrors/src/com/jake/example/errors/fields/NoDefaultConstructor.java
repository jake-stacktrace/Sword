package com.jake.example.errors.fields;

import javax.inject.Inject;


public class NoDefaultConstructor {
	
	public NoDefaultConstructor(String s) {
	}
}

class ReferringClass {
	@Inject
	NoDefaultConstructor reference;
	
}