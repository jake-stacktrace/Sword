package com.jake.sword;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;

import org.mockito.Mock;

public class ElementProcessor {

	private ElementHelper elementHelper;
	private ElementModel elementModel;

	public ElementProcessor(ElementHelper elementHelper, ElementModel elementModel) {
		this.elementHelper = elementHelper;
		this.elementModel = elementModel;
	}

	@SuppressWarnings("deprecation")
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
		for (Element element : env.getElementsAnnotatedWith(Mock.class)) {
			elementModel.addMock(element);
		}
		for (Element element : env.getElementsAnnotatedWith(org.mockito.MockitoAnnotations.Mock.class)) {
			elementModel.addMock(element);
		}
		for (Element element : env.getElementsAnnotatedWith(Qualifier.class)) {
			elementModel.addQualifier(element);
		}
		for (Element element : env.getElementsAnnotatedWith(Provides.class)) {
			ExecutableElement methodElement = (ExecutableElement) element;
			Element returnElement = elementHelper.asElement(methodElement.getReturnType());
			elementModel.addProvides(returnElement, methodElement, elementModel.getQualifiers(methodElement));
		}
		for (Element element : env.getElementsAnnotatedWith(Singleton.class)) {
			elementModel.addSingleton(element);
		}
		for (Element element : env.getElementsAnnotatedWith(Bind.class)) {
			elementModel.addBind(element);
		}
	}
}
