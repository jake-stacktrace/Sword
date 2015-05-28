package com.jake.example.errors.fields;

import com.jake.sword.Provides;

public class DuplicateProvides {
	@Provides
	public String getString1() {
		return null;
	}
	@Provides
	public String getString2() {
		return null;
	}
}
