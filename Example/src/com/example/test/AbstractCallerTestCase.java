package com.example.test;

import org.mockito.MockitoAnnotations;

import com.jake.sword.SwordInjector;

public abstract class AbstractCallerTestCase {
	protected void setupInjections() {
		MockitoAnnotations.initMocks(this);
		SwordInjector.inject(this);
	}
}
