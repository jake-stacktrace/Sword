package com.jake.example.errors.fields;

import javax.inject.Inject;

import com.jake.sword.Bind;

@Bind(from=IFoo.class,to=Foo2.class)
class BindToClassWithMissingInjectConstructor {
	@Inject
	IFoo foo;
}