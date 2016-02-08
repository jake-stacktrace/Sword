package com.jake.sword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class ElementModel {
	private final List<InjectedClass> injectedClasses = new ArrayList<InjectedClass>();
	private final Map<Provider, ExecutableElement> providers = new HashMap<Provider,ExecutableElement>();
	private final ElementHelper elementHelper;
	private Map<Element, ExecutableElement> constructors = new HashMap<Element,ExecutableElement>();
	private List<Element> qualifiers = new ArrayList<Element>();
	private List<Element> mocks = new ArrayList<Element>();

	public ElementModel(ElementHelper elementHelper) {
		this.elementHelper = elementHelper;
	}

	public void addField(PackageElement packageElement, Element fieldElement) {
		injectedClasses.add(new InjectedClass(packageElement, (TypeElement) fieldElement.getEnclosingElement(), fieldElement));
	}

	public Set<PackageElement> getPackageElements() {
		Set<PackageElement> packageElements = new HashSet<PackageElement>();
		for (InjectedClass injectedClass : injectedClasses) {
			packageElements.add(injectedClass.getPackageElement());
		}
		return packageElements;
	}

	public Set<TypeElement> getClassElements(PackageElement packageElement) {
		Set<TypeElement> classElements = new HashSet<TypeElement>();
		for (InjectedClass injectedClass : injectedClasses) {
			if (injectedClass.getPackageElement().equals(packageElement)) {
				classElements.add(injectedClass.getClassElement());
			}
		}
		return classElements;
	}

	public List<Element> getFieldElements(PackageElement packageElement, Element classElement) {
		List<Element> fieldElements = new ArrayList<Element>();
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
		TypeElement classElement = (TypeElement) constructorElement.getEnclosingElement();
		if (constructors.containsKey(classElement)) {
			elementHelper.error(constructorElement, "Only one constructor can have @Inject tag");
			return;
		}
		constructors.put(classElement, constructorElement);
		addClassWithZeroFields(classElement);
	}

	public void addProvides(Element returnElement, ExecutableElement providesMethodElement) {
		Provides providesAnnotation = providesMethodElement.getAnnotation(Provides.class);
		boolean overrides = providesAnnotation.overrides();
		Provider provider = new Provider(returnElement, providesMethodElement, this, overrides);
		if (providers.containsKey(provider)) {
			elementHelper.error(providesMethodElement,
					"Duplicate Provides, cannot figure out which Injection to match. Use @Named or a custom Qualifier");
		}
		providers.put(provider, providesMethodElement);
	}

	public void addSingleton(TypeElement singletonElement) {
		addClassWithZeroFields(singletonElement);
	}

	private void addClassWithZeroFields(TypeElement classElement) {
		PackageElement packageElement = elementHelper.getPackageElement(classElement);
		injectedClasses.add(new InjectedClass(packageElement, classElement, null));
	}

	public ExecutableElement getProvidesMethod(Element classElement, Element fieldElement) {
		ExecutableElement overrideMethod = providers.get(new Provider(classElement, fieldElement, this, true));
		if (overrideMethod != null) {
			return overrideMethod;
		}
		return providers.get(new Provider(classElement, fieldElement, this, false));
	}

	public Set<TypeElement> getClassElements() {
		Set<TypeElement> classElements = new HashSet<TypeElement>();
		for (PackageElement packageElement : getPackageElements()) {
			classElements.addAll(getClassElements(packageElement));
		}
		return classElements;
	}

	public Map<String, Set<TypeElement>> getSuperClasses() {
		Set<TypeElement> classElements = new HashSet<TypeElement>();
		for (Element mockElement : mocks) {
			TypeElement classElement = (TypeElement) mockElement.getEnclosingElement();
			classElements.add(classElement);
		}
		classElements.addAll(getClassElements());
		Map<String, Set<TypeElement>> superClasses = new HashMap<String,Set<TypeElement>>();
		gatherSuperClasses(superClasses, classElements);
		return superClasses;
	}

	private void gatherSuperClasses(Map<String, Set<TypeElement>> superClasses, Set<TypeElement> elements) {
		for (TypeElement classElement : elements) {
			TypeMirror superClassType = classElement.getSuperclass();
			gatherSubClassesFromType(superClasses, elements, classElement, superClassType);
			for (TypeMirror interfaceType : classElement.getInterfaces()) {
				Element interfaceElement = elementHelper.asElement(interfaceType);
				if (interfaceElement.getModifiers().contains(Modifier.PUBLIC)) {
					gatherSubClassesFromType(superClasses, elements, classElement, interfaceType);
				}
			}
		}
	}

	private void gatherSubClassesFromType(Map<String, Set<TypeElement>> superClasses, Set<TypeElement> elements, TypeElement classElement,
			TypeMirror superClassType) {
		Element superClassElement = elementHelper.asElement(superClassType);
		if (!elements.contains(superClassElement)) {
			String strSuperClass = superClassType.toString();
			if (!strSuperClass.equals("java.lang.Object")) {
				ExecutableElement providedMethod = getProvidesMethod(superClassElement, superClassElement);
				if (providedMethod != null) {
					classElement = (TypeElement) superClassElement;
				}
				Set<TypeElement> classElements = superClasses.get(strSuperClass);
				if (classElements == null) {
					classElements = new HashSet<TypeElement>();
					superClasses.put(strSuperClass, classElements);
				}
				classElements.add(classElement);
			}
		}
	}

	public ExecutableElement getInjectedConstructor(Element classElement) {
		return constructors.get(classElement);
	}

	public void addQualifier(Element element) {
		qualifiers.add(element);
	}

	public List<Element> getQualifiers() {
		return qualifiers;
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

	public boolean isTestClass(Element classElement) {
		for (Element mock : mocks) {
			if (mock.getEnclosingElement().equals(classElement)) {
				return true;
			}
		}
		return false;
	}

	public void checkOverridesProvidesErrors() {
		for(Provider provider : providers.keySet()) {
			if(provider.isOverrides()) {
				Provider originalProvider = new Provider(provider);
				originalProvider.setOverrides(false);
				if(!providers.containsKey(originalProvider)) {
					elementHelper.error(providers.get(provider), "Overriding @Provides, but no original @Provides found");
				}
			}
		}
	}
}
