package com.example.abstract1;

import javax.inject.Inject;

import com.jake.sword.SwordInjector;

public abstract class AbstractInjecting {
	AbstractInjecting() {
		SwordInjector.inject(this);
	}
}
