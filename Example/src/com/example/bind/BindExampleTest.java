package com.example.bind;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jake.sword.SwordInjector;

public class BindExampleTest {
	@Test
	public void testBind() {
		assertNotNull(SwordInjector.get(BindExample.class).widget);
	}
}
