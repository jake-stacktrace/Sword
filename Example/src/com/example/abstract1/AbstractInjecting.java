package com.example.abstract1;

import javax.inject.Inject;

import com.jake.sword.SwordInjector;

public abstract class AbstractInjecting {
	@Inject
	AbstractInjecting() {
		SwordInjector.inject(this);
	}
}
