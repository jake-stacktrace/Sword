package com.jake.sword;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

@Qualifier
@Documented
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface Provides {
	boolean overrides() default false;
}