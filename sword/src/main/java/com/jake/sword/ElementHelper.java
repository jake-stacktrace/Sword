package com.jake.sword;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public class ElementHelper {
	private final ProcessingEnvironment processingEnv;

	public ElementHelper(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	public PackageElement getPackageElement(Element element) {
		return processingEnv.getElementUtils().getPackageOf(element);
	}

	public void error(Element e, String msg, Object... args) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
	}

	public Element asElement(TypeMirror type) {
		return processingEnv.getTypeUtils().asElement(type);
	}

	public PackageElement getPackageElement(String packageName) {
		return processingEnv.getElementUtils().getPackageElement(packageName);
	}

	public String getPackageName(Element classElement) {
		return getPackageElement(classElement).getQualifiedName().toString();
	}

	public boolean isSubtype(Element superClassElement, Element subClassElement) {
		return processingEnv.getTypeUtils().isSubtype(superClassElement.asType(), subClassElement.asType());
	}

	public boolean isAbstract(Element classElement) {
		return classElement.getModifiers().contains(Modifier.ABSTRACT);
	}

	public String getClassName(Element classElement) {
		Element curElement = classElement.getEnclosingElement();
		String outerClasses = "";
		while (curElement.getKind() == ElementKind.CLASS) {
			outerClasses = curElement.getSimpleName().toString() + "." + outerClasses;
			curElement = curElement.getEnclosingElement();
		}
		return outerClasses + classElement.getSimpleName().toString();
	}

	public TypeElement getNonStaticOuterClass(Element classElement) {
		Element maybeOuterClass = classElement.getEnclosingElement();
		if (maybeOuterClass.getKind() == ElementKind.CLASS && !classElement.getModifiers().contains(Modifier.STATIC)) {
			return (TypeElement) maybeOuterClass;
		}
		return null;
	}
}
