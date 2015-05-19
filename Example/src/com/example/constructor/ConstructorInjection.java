package com.example.constructor;

import javax.inject.Inject;

import com.example.level1.Example;

public class ConstructorInjection {
    private final Example example;

    @Inject
    public ConstructorInjection(Example example) {
        this.example = example;
    }
    
    public Example getExample() {
		return example;
	}
}
