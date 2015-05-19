package com.jake.sword;

import java.util.List;

import javax.lang.model.element.Element;

public class Provider {
	private final Element classElement;
	private final String name;
	private final List<Element> qualifiers;

	public Provider(Element classElement, String name, List<Element> qualifiers) {
		this.classElement = classElement;
		this.name = name;
		this.qualifiers = qualifiers;
	}
	
	public Element getClassElement() {
		return classElement;
	}

	public String getName() {
		return name;
	}

	public List<Element> getQualifiers() {
		return qualifiers;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classElement == null) ? 0 : classElement.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((qualifiers == null) ? 0 : qualifiers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Provider other = (Provider) obj;
		if (classElement == null) {
			if (other.classElement != null)
				return false;
		} else if (!classElement.equals(other.classElement))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (qualifiers == null) {
			if (other.qualifiers != null)
				return false;
		} else if (!qualifiers.equals(other.qualifiers))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return classElement + " " + name + " " + qualifiers;
	}
}
