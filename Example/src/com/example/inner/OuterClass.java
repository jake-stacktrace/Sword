package com.example.inner;

import javax.inject.Inject;

import com.example.level1.IFoo;

public class OuterClass {
	@Inject
	StaticInnerClass staticInnerClass;
	@Inject
	NonStaticInnerClass nonStaticInnerClass;

	public static class StaticInnerClass {
		InnerInnerClass innerInnerClass;

		@Inject
		StaticInnerClass(InnerInnerClass innerInnerClass) {
			this.innerInnerClass = innerInnerClass;
		}
		
		public static class InnerInnerClass {
			@Inject
			IFoo foo;
		}
	}
	public class NonStaticInnerClass {
		InnerInnerClass innerInnerClass;

		@Inject
		NonStaticInnerClass(InnerInnerClass innerInnerClass) {
			this.innerInnerClass = innerInnerClass;
		}
		
		public class InnerInnerClass {
			@Inject
			IFoo foo;
		}
	}
}
