package com.jake.sword;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

@Qualifier
@Documented
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Bind {
	Class<?> from();
	Class<?> to();	
}
