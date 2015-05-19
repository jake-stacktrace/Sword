package com.example.constructor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class ConstructorNamedParameters {
	private final List<String> aNames;
	
	@Inject
	public ConstructorNamedParameters(@Named("A words") List<String> aNames) {
		this.aNames = aNames;
	}

	public List<String> getaNames() {
		return aNames;
	}
}
