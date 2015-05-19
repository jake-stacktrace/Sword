package com.example.qualifier;

import javax.inject.Inject;

import com.jake.sword.Provides;

public class QualifierExample {
	@Inject
	@Regular
	protected String regular;
	
	@Inject
	@Decaf
	protected String decaf;
	
	@Provides
	@Regular
	public String getRegular() { return "regular"; }
	
	@Provides
	@Decaf
	public String getDecaf() { return "decaf"; }
}
