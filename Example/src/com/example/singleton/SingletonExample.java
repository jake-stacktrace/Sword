package com.example.singleton;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SingletonExample {
	@Inject
    protected SingletonExample() {
    }
}
