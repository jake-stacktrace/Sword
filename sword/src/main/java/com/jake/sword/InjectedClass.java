package com.jake.sword;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class InjectedClass {
	private final Element fieldElement;
	private final PackageElement packageElement;
	private final TypeElement classElement;

	public InjectedClass(PackageElement packageElement, TypeElement classElement, Element fieldElement) {
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

	public TypeElement getClassElement() {
		return classElement;
	}
}
