package com.jake.sword;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public class Provider {
	private final Element classElement;
	private final List<Element> qualifiers = new ArrayList<Element>();
	private String name;
	private boolean overrides = false;

	public Provider(Element classElement, Element fieldElement, ElementModel elementModel, boolean overrides) {
		this.classElement = classElement;
		this.overrides = overrides;
		if (fieldElement != null) {
			Provides provides = fieldElement.getAnnotation(Provides.class);
			if(provides != null) {
				this.overrides = provides.overrides();
			}
			Named namedAnnotation = fieldElement.getAnnotation(Named.class);
			this.name = namedAnnotation == null ? null : namedAnnotation.value();
			for (Element qualifier : elementModel.getQualifiers()) {
				List<? extends AnnotationMirror> annotationMirrors = fieldElement.getAnnotationMirrors();
				for (AnnotationMirror annotationMirror : annotationMirrors) {
					Element annotationElement = annotationMirror.getAnnotationType().asElement();
					if (qualifier.equals(annotationElement)) {
						qualifiers.add(annotationElement);
					}
				}
			}
		}
	}
	
	public Provider(Provider provider) {
		this.classElement = provider.classElement;
		this.name = provider.name;
		
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
		result = prime * result + (overrides ? 1231 : 1237);
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
		if (overrides != other.overrides)
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
		return classElement + " " + getName() + " " + getQualifiers();
	}

	public boolean isOverrides() {
		return overrides;
	}

	public void setOverrides(boolean overrides) {
		this.overrides = overrides;
	}
}
