package com.jake.sword;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

public class CodeGenerator {
	private final static String PACKAGE_INJECTOR_NAME = "PackageSwordInjector";
	private final ProcessingEnvironment processingEnv;
	private final ElementModel elementModel;
	private final ElementHelper elementHelper;

	public CodeGenerator(ProcessingEnvironment processingEnv, ElementModel elementModel) {
		this.processingEnv = processingEnv;
		this.elementModel = elementModel;
		this.elementHelper = new ElementHelper(processingEnv);
	}

	public void generateCode() {
		for (PackageElement packageElement : elementModel.getPackageElements()) {
			String packageName = packageElement.getQualifiedName().toString();
			String className = packageName + "." + PACKAGE_INJECTOR_NAME;
			String code = generatePackageInjector(packageElement);
			writeJavaFile(packageElement, className, code);
		}
		String packageName = "com.jake.sword";
		writeJavaFile(elementHelper.getPackageElement(packageName), packageName + ".SwordInjector", generateSwordInjectorCode());
	}

	private String generateSwordInjectorCode() {
		String code = "";
		code += "package com.jake.sword;\n\n";
		code += "public class SwordInjector {\n";
		for (Element classElement : elementModel.getClassElements()) {
			String variableName = getVariableName(classElement);
			code += "  public static void inject(" + classElement + " " + variableName + ") {\n";
			code += "        " + elementHelper.getPackageName(classElement) + "." + PACKAGE_INJECTOR_NAME + ".inject(" + variableName
					+ ");\n";
			code += "    }\n";
		}
		Map<String, Set<TypeElement>> superClasses = elementModel.getSuperClasses();
		for (String superClassName : superClasses.keySet()) {
			Set<TypeElement> classElements = superClasses.get(superClassName);
			String variableName = getVariableName(superClassName);
			code += "  public static void inject(" + superClassName + " " + variableName + ") {\n";
			for (Element classElement : classElements) {
				code += "        if(" + variableName + " instanceof " + classElement.toString() + ") {\n";
				code += "          " + elementHelper.getPackageName(classElement) + "." + PACKAGE_INJECTOR_NAME + ".inject(("
						+ classElement + ")" + variableName + ");\n";
				code += "        }\n";
			}
			code += "    }\n";
		}
		code += "  @SuppressWarnings(\"unchecked\")\n";
		code += "  public static <T> T get(Class<T> clazz) {\n";
		for (Element classElement : elementModel.getClassElements()) {
			if (!elementHelper.isAbstract(classElement)) {
				String className = getClassName(classElement);
				code += "      if(clazz == " + classElement + ".class) {\n";
				if (elementHelper.getNonStaticOuterClass(classElement) != null) {
					code += "throw new IllegalArgumentException(\"Cannot create instance of inner non-static class " + classElement
							+ ".\");\n";
				} else {
					code += "        return (T)" + elementHelper.getPackageName(classElement) + "." + PACKAGE_INJECTOR_NAME + ".get"
							+ className + "();\n";
				}
				code += "      }\n";
			}
		}
		for (String superClassName : superClasses.keySet()) {
			Set<TypeElement> classElements = superClasses.get(superClassName);
			code += "      if(clazz == " + superClassName + ".class) {\n";
			if (classElements.size() == 1) {
				Element classElement = classElements.iterator().next();
				code += "        return (T)" + elementHelper.getPackageName(classElement) + "." + PACKAGE_INJECTOR_NAME + ".get"
						+ getClassName(classElement) + "();\n";
			} else {
				code += "        throw new IllegalArgumentException(\"Multiple implementations found for " + superClassName + ": "
						+ joinAndSort(classElements, ",") + "\");";
			}
			code += "      }\n";
		}
		code += "    throw new IllegalArgumentException(\"Class not found for injection:\" + clazz);\n";
		code += "    }\n";
		code += "}\n";
		return code;
	}

	private String joinAndSort(Set<TypeElement> classElements, String delimeter) {
		String str = "";
		List<String> strClasses = new ArrayList<String>();
		for (Element element : classElements) {
			strClasses.add(element.toString());
		}
		Collections.sort(strClasses);
		for (String className : strClasses) {
			str += className + ",";
		}
		// chop off last comma
		if (str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	private void writeJavaFile(PackageElement packageElement, String fullyQualifiedClassName, String code) {
		Writer writer = null;
		try {
			Filer filer = processingEnv.getFiler();
			JavaFileObject file = filer.createSourceFile(fullyQualifiedClassName, packageElement);
			writer = file.openWriter();
			writer.write(code);
		} catch (IOException e) {
			System.out.println("Failed to Write Java File" + e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				System.out.println("Failed to Write Java File" + e);
			}
		}
	}

	private String generatePackageInjector(PackageElement packageElement) {
		String code = "";
		String packageName = packageElement.getQualifiedName().toString();
		code += "package " + packageName + ";\n";
		code += "public class " + PACKAGE_INJECTOR_NAME + " {\n";
		Set<TypeElement> allElements = new HashSet<TypeElement>();
		for (TypeElement classElement : elementModel.getClassElements(packageElement)) {
			allElements.add(classElement);
		}
		Map<String, Set<TypeElement>> superClasses = elementModel.getSuperClasses();
		for (String strSuperClass : superClasses.keySet()) {
			Set<TypeElement> elements = superClasses.get(strSuperClass);
			for (TypeElement element : elements) {
				if (elementHelper.getPackageElement(element).equals(packageElement)) {
					allElements.add(element);
				}
			}
		}
		for (TypeElement element : allElements) {
			code += generatePackInjectorForClassElement(packageElement, element);
		}
		code += "}";
		return code;
	}

	private String generatePackInjectorForClassElement(PackageElement packageElement, TypeElement classElement) {
		String className = elementHelper.getClassName(classElement);
		String variableName = getVariableName(classElement);
		boolean isSingleton = classElement.getAnnotation(Singleton.class) != null;
		String code = generatePackageInjectorGetter(classElement, className, variableName, isSingleton);

		code += "  public static void inject(" + className + " " + variableName + ") {\n";
		code += populateMemberFields(packageElement, classElement, variableName);
		for (TypeElement subClassElement : elementModel.getClassElements()) {
			if (elementHelper.isSubtype(classElement, subClassElement) && !subClassElement.equals(classElement)) {
				PackageElement subClassPackageElement = elementHelper.getPackageElement(subClassElement);
				code += populateMemberFields(subClassPackageElement, subClassElement, variableName);
			}
		}
		for (TypeElement superClassElement : elementModel.getClassElements()) {
			if (elementHelper.isSubtype(superClassElement, classElement) && !superClassElement.equals(classElement)) {
				PackageElement superClassPackageElement = elementHelper.getPackageElement(superClassElement);
				code += "    if(" + variableName + " instanceof " + superClassElement + ") {\n";
				String superVariableName = getVariableName(superClassElement);
				code += "      " + superClassElement + " " + superVariableName + " = (" + superClassElement + ")" + variableName + ";\n";
				code += populateMemberFields(superClassPackageElement, superClassElement, superVariableName);
				code += "    }\n";
			}
		}
		code += "    }\n";
		code += "\n";
		return code;
	}

	private String generatePackageInjectorGetter(TypeElement classElement, String className, String variableName, boolean isSingleton) {
		String code = "";
		if (elementHelper.isAbstract(classElement) && elementModel.getProvidesMethod(classElement, classElement) == null) {
			return code;
		}
		if (isSingleton) {
			variableName += "Instance";
			code += "  private static " + classElement + " " + getVariableName(classElement) + "Instance = "
					+ construct(classElement, elementModel.getProvidesMethod(classElement, classElement), variableName) + ";\n";
			code += "static { " + PACKAGE_INJECTOR_NAME + ".inject(" + variableName + "); }\n";
		}
		code += "  public static " + className + " get" + getClassName(classElement) + "(";
		TypeElement outerClass = elementHelper.getNonStaticOuterClass(classElement);
		if (outerClass != null) {
			code += outerClass + " outer";
		}
		code += ") {\n";
		if (!isSingleton) {
			code += "      " + classElement + " " + variableName + " = ";
			if (outerClass != null) {
				code += "outer.";
			}
			code += construct(classElement, elementModel.getProvidesMethod(classElement, classElement), "outer") + ";\n";
			code += "      " + PACKAGE_INJECTOR_NAME + ".inject(" + variableName + ");\n";
		}
		code += "      return " + variableName + ";\n";
		code += "    }\n";
		code += "\n";
		return code;
	}

	private String populateMemberFields(PackageElement packageElement, TypeElement classElement, String variableName) {
		String code = "";
		List<Element> fieldElements = elementModel.getFieldElements(packageElement, classElement);
		if (elementModel.isTestClass(classElement)) {
			for (Element fieldElement : fieldElements) {
				String fieldVariable = getVariableName(fieldElement);
				code += "      " + variableName + "." + fieldVariable + " = ";
				TypeElement fieldTypeElement = (TypeElement) elementHelper.asElement(fieldElement.asType());
				code += construct(fieldTypeElement, fieldElement, variableName) + ";\n";
				List<Element> subFieldElements = elementModel.getFieldElements(elementHelper.getPackageElement(fieldTypeElement),
						fieldTypeElement);
				for (Element subFieldElement : subFieldElements) {
					String subFieldVariableName = getVariableName(subFieldElement);
					code += "      " + variableName + "." + fieldVariable + "." + subFieldVariableName + " = ";
					Element mock = elementModel.getMock(fieldTypeElement, subFieldElement);
					if (mock != null) {
						code += variableName + "." + mock.getSimpleName() + ";\n";
					} else {
						code += createObject(subFieldElement.asType(), subFieldElement, variableName) + ";\n";
					}
				}
			}
		} else {
			for (Element fieldElement : fieldElements) {
				String fieldVariable = getVariableName(fieldElement);
				code += "      " + variableName + "." + fieldVariable + " = ";
				code += createObject(fieldElement.asType(), fieldElement, variableName) + ";\n";
			}
		}
		return code;
	}

	private String createObject(TypeMirror type, Element referringElement, String variableName) {
		if (referringElement.asType().getKind().isPrimitive()) {
			ExecutableElement providedMethod = elementModel.getProvidesMethod(elementHelper.asElement(type), referringElement);
			if (providedMethod != null) {
				TypeElement moduleClassElement = (TypeElement) providedMethod.getEnclosingElement();
				return construct(moduleClassElement, referringElement, variableName) + "." + providedMethod.getSimpleName().toString()
						+ "()";
			}
			elementHelper.error(referringElement, "Primitive types must be provided");
			return "0";
		}
		TypeElement classElement = (TypeElement) elementHelper.asElement(type);
		PackageElement packageElement = elementHelper.getPackageElement(classElement);
		if (elementModel.containsClassElement(packageElement, classElement)) {
			return createFromGetterOnInjector(classElement, variableName);
		} else {
			return construct(classElement, referringElement, variableName);
		}
	}

	private String construct(TypeElement classElement, Element referringElement, String variableName) {
		ExecutableElement providedMethod = elementModel.getProvidesMethod(classElement, referringElement);
		if (providedMethod != null) {
			TypeElement moduleClassElement = (TypeElement) providedMethod.getEnclosingElement();
			return construct(moduleClassElement, referringElement, variableName) + "." + providedMethod.getSimpleName().toString() + "()";
		}
		ExecutableElement constructorElement = elementModel.getInjectedConstructor(classElement);
		String params = "";
		if (constructorElement != null) {
			for (VariableElement param : constructorElement.getParameters()) {
				params += createObject(param.asType(), param, variableName) + ",";
			}
			if (params.length() > 0) {
				params = params.substring(0, params.length() - 1);
			}
		}
		TypeElement outerClass = elementHelper.getNonStaticOuterClass(classElement);
		if(outerClass != null) {
			return variableName + "." + makeNewClass(classElement, params);
		}
		return makeNewClass(classElement, params);
	}

	private String makeNewClass(TypeElement classElement, String params) {
		String className = classElement.toString();
		if (elementHelper.getNonStaticOuterClass(classElement) != null) {
			className = classElement.getSimpleName().toString();
		}
		boolean foundNoArgConstructor = false;
		for (Element element : classElement.getEnclosedElements()) {
			if (element.getKind() == ElementKind.CONSTRUCTOR) {
				ExecutableElement executableElement = (ExecutableElement) element;
				if (executableElement.getParameters().size() == 0) {
					foundNoArgConstructor = true;
				}
				if (executableElement.getAnnotation(Inject.class) != null) {
					foundNoArgConstructor = true;
				}
			}
		}
		if (!foundNoArgConstructor) {
			elementHelper.error(classElement, "No default no-argument or @Inject constructor found for " + className);
		}
		return "new " + className + "(" + params + ")";
	}

	private String createFromGetterOnInjector(Element classElement, String variableName) {
		String className = getClassName(classElement);
		String packageName = elementHelper.getPackageName(classElement);
		String code = packageName + "." + PACKAGE_INJECTOR_NAME + ".get" + className + "(";
		if (elementHelper.getNonStaticOuterClass(classElement) != null) {
			code += variableName;
		}
		code += ")";
		return code;
	}

	private String getClassName(Element classElement) {
		String outerClass = "";
		Element maybeOuterClass = classElement.getEnclosingElement();
		if (maybeOuterClass.getKind() == ElementKind.CLASS && maybeOuterClass.getModifiers().contains(Modifier.STATIC)) {
			outerClass = maybeOuterClass.getSimpleName().toString();
		}
		return outerClass + classElement.getSimpleName();
	}

	private String getVariableName(Element classElement) {
		return getVariableName(classElement.getSimpleName().toString());
	}

	private String getVariableName(String name) {
		if (name.contains(".")) {
			name = name.substring(name.lastIndexOf(".") + 1);
		}
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}
}
