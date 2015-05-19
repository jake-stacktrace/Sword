package com.example.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jake.sword.SwordInjector;

public class BlahTest {
	@Inject
	protected Blah blah;
	@Mock
	protected Receiver receiver;
	
	@Test
	public void testBlah() {
		MockitoAnnotations.initMocks(this);
		SwordInjector.inject(this);
		blah.callReceiver();
		verify(receiver).receive();
		assertTrue(blah.receiver.getClass().toString().contains("Mock"));
		// injections only get replaced 1 level deep not 2
		assertFalse(blah.other.receiver.getClass().toString().contains("Mock"));
	}
}
