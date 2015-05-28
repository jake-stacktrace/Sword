package com.jake.example.errors.fields;

import javax.inject.Inject;

public class MissingInjection {
	@Inject
	protected A missingInjectTag;
	@Inject
	protected int primitiveTypeNotProvided;
	@Inject
	protected int primitiveTypeNotProvided2;
}

class A {
}
