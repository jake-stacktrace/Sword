package com.jake.example.errors.fields;

import javax.inject.Inject;

import com.jake.sword.Bind;

public class DuplicateBind {
	interface Letter {}
	
	@Bind(from=Letter.class,to=A.class)
	class A implements Letter {
		@Inject
		A() {}
	}
	@Bind(from=Letter.class,to=A.class)
	class B implements Letter {
		@Inject
		B() {}
	}
}
