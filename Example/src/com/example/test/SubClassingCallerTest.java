package com.example.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jake.sword.SwordInjector;

public class SubClassingCallerTest extends AbstractCallerTestCase {
	@Inject
	protected Caller caller;
	@Mock
	protected Receiver receiver;

	@Before
	public void setUp() {
		super.setupInjections();
	}

	@Test
	public void testCaller() {
		MockitoAnnotations.initMocks(this);
		SwordInjector.inject(this);
		caller.callReceiver();
		verify(receiver).receive();
		assertTrue(caller.receiver.getClass().toString().contains("Mock"));
		// injections only get replaced 1 level deep not 2
		assertFalse(caller.other.receiver.getClass().toString().contains("Mock"));
	}
}
