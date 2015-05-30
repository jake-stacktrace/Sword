package com.example.named;

import javax.inject.Inject;
import javax.inject.Named;

import com.jake.sword.Bind;

public class NamedBindExample {
	interface INamedBind {
	}
	
	@Named("namedBind1")
	@Bind(from=INamedBind.class,to=NamedBind1.class)
	public static class NamedBind1 implements INamedBind {
		@Inject
		public NamedBind1() {
		}
	}
	@Named("namedBind2")
	@Bind(from=INamedBind.class,to=NamedBind2.class)
	public static class NamedBind2 implements INamedBind {
		@Inject
		public NamedBind2() {
		}
	}
	
	@Named("namedBind1")
	@Inject
	INamedBind namedBind1;
	
	@Named("namedBind2")
	@Inject
	INamedBind namedBind2;
}
