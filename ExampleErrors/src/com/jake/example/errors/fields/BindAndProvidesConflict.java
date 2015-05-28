package com.jake.example.errors.fields;

import javax.inject.Inject;

import com.jake.sword.Bind;
import com.jake.sword.Provides;

@Bind(from=IFoo.class,to=Foo.class)
public class BindAndProvidesConflict {
	@Inject
	IFoo foo;
	
	@Provides
	public IFoo getFoo() {
		return new Foo();
	}
}
