package com.example.interface1;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;

import com.example.level1.Foo;
import com.jake.sword.SwordInjector;

public class InterfaceExampleTest {
	public interface I {
	}

	public static class A implements I {
		@Inject
		protected Foo f;
	}

	public static class B implements I {
		@Inject
		protected Foo f2;
	}

	@Test
	public void testInterfaceInject() throws Exception {
		I iA = new A();
		SwordInjector.inject(iA);
		assertNotNull(((A) iA).f);

		I iB = new B();
		SwordInjector.inject(iB);
		assertNotNull(((B) iB).f2);
	}

	@Test
	public void testInterfaceGet() throws Exception {
		try {
			SwordInjector.get(I.class);
		} catch (IllegalArgumentException exception) {
			assertEquals("Multiple implementations found for com.example.interface1.InterfaceExampleTest.I: com.example.interface1.InterfaceExampleTest.A,com.example.interface1.InterfaceExampleTest.B", exception.getMessage());
		}
	}
}
