package com.example.inner;

import javax.inject.Inject;

import com.example.level1.IFoo;

public class OuterClass {
	@Inject
	InnerClass innerClass;

	public static class InnerClass {
		InnerInnerClass innerInnerClass;

		@Inject
		InnerClass(InnerInnerClass innerInnerClass) {
			this.innerInnerClass = innerInnerClass;
		}
		
		public static class InnerInnerClass {
			@Inject
			IFoo foo;
		}
	}
}
