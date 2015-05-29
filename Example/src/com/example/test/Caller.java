package com.example.test;

import javax.inject.Inject;

public class Caller {
	@Inject
	protected Receiver receiver;
	@Inject
	protected Other other;

	public void callReceiver() {
		receiver.receive();
	}
}
