package com.example.primitive;

import javax.inject.Inject;
import javax.inject.Named;

import com.jake.sword.Provides;

public class Primitive {
	@Inject
	@Named("integer1")
	int i;
	
	@Inject
	@Named("integer2")
	Integer e;
	
	@Provides
	@Named("integer1")
	public int getInteger1() { return 3; }
	
	@Provides
	@Named("integer2")
	public Integer getInteger2() { return 4; }
	
	@Inject
	@Named("byte1")
	byte b;
	
	@Inject
	@Named("byte2")
	Byte b2;

	@Provides
	@Named("byte1")
	public byte getByte() { return (byte)1; }
	
	@Provides
	@Named("byte2")
	public Byte getByte2() { return (byte)2; }
	
	@Inject
	@Named("char1")
	char c;
	
	@Inject
	@Named("char2")
	Character c2;

	@Provides
	@Named("char1")
	public char getChar1() { return 'a'; }
	
	@Provides
	@Named("char2")
	public Character getChar2() { return 'b'; }
}
