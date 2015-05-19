package com.example.constructor;

import javax.inject.Inject;

public class ContainingClass {
    @Inject
    protected ConstructorInjection constructorInjection;
    @Inject
    protected ConstructorNamedParameters constructorNamedParameters;

}
