package com.jake.sword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.MirroredTypeException;

public class ElementModel {
	private final List<InjectedClass> injectedClasses = new ArrayList<>();
	private final Map<Provider, ExecutableElement> provides = new HashMap<>();
	private final ElementHelper elementHelper;
	private Map<Element, ExecutableElement> constructors = new HashMap<>();
	private Map<Element, Element> bindings = new HashMap<>();
	private List<Element> qualifiers = new ArrayList<>();
	private List<Element> mocks = new ArrayList<>();

	public ElementModel(ElementHelper elementHelper) {
		this.elementHelper = elementHelper;
	}

	public void addField(PackageElement packageElement, Element fieldElement) {
		injectedClasses.add(new InjectedClass(packageElement, fieldElement.getEnclosingElement(), fieldElement));
	}

	public Set<PackageElement> getPackageElements() {
		Set<PackageElement> packageElements = new HashSet<>();
		for (InjectedClass injectedClass : injectedClasses) {
			packageElements.add(injectedClass.getPackageElement());
		}
		return packageElements;
	}

	public Set<Element> getClassElements(PackageElement packageElement) {
		Set<Element> classElements = new HashSet<>();
		for (InjectedClass injectedClass : injectedClasses) {
			if (injectedClass.getPackageElement().equals(packageElement)) {
				classElements.add(injectedClass.getClassElement());
			}
		}
		return classElements;
	}

	public List<Element> getFieldElements(PackageElement packageElement, Element classElement) {
		List<Element> fieldElements = new ArrayList<>();
		for (InjectedClass injectedClass : injectedClasses) {
			if (injectedClass.getPackageElement().equals(packageElement) && injectedClass.getClassElement().equals(classElement)) {
				Element fieldElement = injectedClass.getFieldElement();
				if (fieldElement != null) {
					fieldElements.add(fieldElement);
				}
			}
		}
		return fieldElements;
	}

	public boolean containsClassElement(PackageElement packageElement, Element classElement) {
		for (InjectedClass injectedClass : injectedClasses) {
			if (injectedClass.getPackageElement().equals(packageElement) && injectedClass.getClassElement().equals(classElement)) {
				return true;
			}
		}
		return false;
	}

	public void addConstructor(PackageElement packageElement, ExecutableElement constructorElement) {
		Element classElement = constructorElement.getEnclosingElement();
		if (constructors.containsKey(classElement)) {
			elementHelper.error(constructorElement, "Only one constructor can have @Inject tag");
			return;
		}
		constructors.put(classElement, constructorElement);
		addClassWithZeroFields(classElement);
	}

	public void addProvides(Element returnElement, ExecutableElement providesMethodElement) {
		Provider classAndName = new Provider(returnElement, providesMethodElement, this);
		if (provides.containsKey(classAndName)) {
			elementHelper.error(providesMethodElement,
					"Duplicate Provides, cannot figure out which Injection to match. Use @Named or a custom Qualifier");
		}
		provides.put(classAndName, providesMethodElement);
	}

	public boolean isProvidingClass(Element classElement, String name) {
		for (Provider classAndName : provides.keySet()) {
			if (classAndName.getClassElement().equals(classElement) && classAndName.getName().equals(name)) {
				return true;
			}
		}
		for (ExecutableElement providerMethodElement : provides.values()) {
			if (providerMethodElement.getEnclosingElement().equals(classElement)) {
				return true;
			}
		}
		return false;
	}

	public void addSingleton(Element singletonElement) {
		addClassWithZeroFields(singletonElement);
	}

	private void addClassWithZeroFields(Element classElement) {
		PackageElement packageElement = elementHelper.getPackageElement(classElement);
		injectedClasses.add(new InjectedClass(packageElement, classElement, null));
	}

	private ExecutableElement getProvidedClass(Element classElement, Element fieldElement) {
		return provides.get(new Provider(classElement, fieldElement, this));
	}

	public Set<Element> getClassElements() {
		Set<Element> classElements = new HashSet<>();
		for (PackageElement packageElement : getPackageElements()) {
			classElements.addAll(getClassElements(packageElement));
		}
		return classElements;
	}

	public ExecutableElement getInjectedConstructor(Element classElement) {
		return constructors.get(classElement);
	}

	public void addBind(Element element) {
		Bind bind = element.getAnnotation(Bind.class);
		try {
			bind.from();
		} catch (MirroredTypeException e) {
			Element fromElement = elementHelper.asElement(e.getTypeMirror());
			if (provides.containsKey(new Provider(fromElement, element, this))) {
				elementHelper.error(element,
						"Bind conflicts with Provides, cannot figure out which Injection to match. Use @Named or a custom Qualifier");
			}
			try {
				bind.to();
			} catch (MirroredTypeException e2) {
				Element toElement = elementHelper.asElement(e2.getTypeMirror());
				if(!constructors.containsKey(toElement) && !provides.containsKey(new Provider(toElement, element, this))) {
					elementHelper.error(element, "Bind to class with missing @Provides or @Inject constructor");
				}
				bindings.put(fromElement, toElement);
			}
		}
	}

	public void addQualifier(Element element) {
		qualifiers.add(element);
	}
	
	public List<Element> getQualifiers() {
		return qualifiers;
	}

	public Element rebind(Element classElement) {
		Element boundElement = bindings.get(classElement);
		if (boundElement != null) {
			return boundElement;
		}
		return classElement;
	}

	public void addMock(Element element) {
		mocks.add(element);
	}

	public Element getMock(Element classElement, Element fieldElement) {
		for (Element mockElement : mocks) {
			// if the current field's type matches the mock type
			if (fieldElement.asType().equals(mockElement.asType()) && fieldElement.getSimpleName().equals(mockElement.getSimpleName())) {
				for (InjectedClass injectedClass : injectedClasses) {
					if (injectedClass.getFieldElement() != null) {
						Element fieldTypeElement = elementHelper.asElement(injectedClass.getFieldElement().asType());
						// if test class (BlahTest) matches where the real
						// injected object was
						if (injectedClass.getClassElement().equals(mockElement.getEnclosingElement())) {
							// if the current class (Blah) matches the class
							// where the injection that is getting
							// replace with a mock is
							if (classElement.equals(fieldTypeElement)) {
								return mockElement;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public boolean isMockClass(Element classElement) {
		for (Element mock : mocks) {
			if (mock.getEnclosingElement().equals(classElement)) {
				return true;
			}
		}
		return false;
	}

	public ExecutableElement getProvidedMethod(Element fieldElement) {
		return getProvidedClass(elementHelper.asElement(fieldElement.asType()), fieldElement);
	}

}
