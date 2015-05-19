package com.example.test;

import javax.inject.Inject;

public class Blah {
	@Inject
	protected Receiver receiver;
	@Inject
	protected Other other;

	public void callReceiver() {
		receiver.receive();
	}
}
