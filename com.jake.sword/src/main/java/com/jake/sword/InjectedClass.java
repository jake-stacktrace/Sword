package com.jake.sword;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

public class InjectedClass {
	private final Element fieldElement;
	private final PackageElement packageElement;
	private final Element classElement;

	public InjectedClass(PackageElement packageElement, Element classElement, Element fieldElement) {
		this.packageElement = packageElement;
		this.classElement = classElement;
		this.fieldElement = fieldElement;
	}

	public Element getFieldElement() {
		return fieldElement;
	}

	public PackageElement getPackageElement() {
		return packageElement;
	}

	public Element getClassElement() {
		return classElement;
	}
}
