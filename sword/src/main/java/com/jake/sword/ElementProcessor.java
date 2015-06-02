package com.jake.sword;

import java.lang.annotation.Annotation;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class ElementProcessor {
	private ElementHelper elementHelper;
	private ElementModel elementModel;

	public ElementProcessor(ElementHelper elementHelper, ElementModel elementModel) {
		this.elementHelper = elementHelper;
		this.elementModel = elementModel;
	}

	@SuppressWarnings({ "unchecked" })
	public void process(RoundEnvironment env) {
		for (Element element : env.getElementsAnnotatedWith(Inject.class)) {
			if (element.getKind() == ElementKind.FIELD) {
				if (element.getModifiers().contains(Modifier.FINAL)) {
					elementHelper.error(element, "Injected members must not be final");
				}
				if (element.getModifiers().contains(Modifier.PRIVATE)) {
					elementHelper.error(element, "Injected members must not be private");
				}
				PackageElement packageElement = elementHelper.getPackageElement(element);
				elementModel.addField(packageElement, element);
			} else if (element.getKind() == ElementKind.CONSTRUCTOR) {
				PackageElement packageElement = elementHelper.getPackageElement(element);
				elementModel.addConstructor(packageElement, (ExecutableElement) element);
			}
		}
		try {
			for (Element element : env.getElementsAnnotatedWith((Class<? extends Annotation>) Class.forName("org.mockito.Mock"))) {
				elementModel.addMock(element);
			}
		} catch (ClassNotFoundException e) {
		}
		try {
			for (Element element : env.getElementsAnnotatedWith((Class<? extends Annotation>) Class
					.forName("org.mockito.MockitoAnnotations.Mock"))) {
				elementModel.addMock(element);
			}
		} catch (ClassNotFoundException e) {
		}
		for (Element element : env.getElementsAnnotatedWith(Qualifier.class)) {
			elementModel.addQualifier(element);
		}
		for (Element element : env.getElementsAnnotatedWith(Provides.class)) {
			addProvidesElement(element);
		}
		try {
			for (Element element : env.getElementsAnnotatedWith((Class<? extends Annotation>) Class.forName("dagger.Provides"))) {
				addProvidesElement(element);
			}
		} catch (ClassNotFoundException e) {
		}
		for (Element element : env.getElementsAnnotatedWith(Singleton.class)) {
			elementModel.addSingleton((TypeElement)element);
		}
		elementModel.checkOverridesProvidesErrors();
	}

	private void addProvidesElement(Element element) {
		ExecutableElement methodElement = (ExecutableElement) element;
		Element returnElement = elementHelper.asElement(methodElement.getReturnType());
		elementModel.addProvides(returnElement, methodElement);
	}
}
