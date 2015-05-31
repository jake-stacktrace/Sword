package com.jake.example.errors.fields;

import javax.inject.Inject;

public class InnerClasses {
	interface NonPublicInnerInterface {
	}
	class NonPublicInnerClass implements NonPublicInnerInterface {
		@Inject
		Foo foo;
	}
	public class PublicInnerClass implements NonPublicInnerInterface {
		@Inject
		Foo foo;
	}
}
