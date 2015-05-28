package com.jake.example.errors.fields;

import javax.inject.Inject;

public class Fields {
	@Inject
	private Foo privateField;
	@Inject
	protected final Foo finalField = null;
}
