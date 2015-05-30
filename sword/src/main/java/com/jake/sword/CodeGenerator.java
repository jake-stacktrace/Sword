package com.jake.sword;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
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
		Map<String, List<Element>> superClasses = elementModel.getSuperClasses();
		for(String superClassName : superClasses.keySet()) {
			List<Element> classElements = superClasses.get(superClassName);
			String variableName = getVariableName(superClassName);
			code += "  public static void inject(" + superClassName + " " + variableName + ") {\n";
			for(Element classElement : classElements) {
				code += "        if(" + variableName + " instanceof " + classElement.toString() + ") {\n";
				code += "          " + elementHelper.getPackageName(classElement) + "." + PACKAGE_INJECTOR_NAME + 
						".inject((" + classElement + ")" + variableName + ");\n";
				code += "        }\n";
			}
			code += "    }\n";
		}
		code += "  @SuppressWarnings(\"unchecked\")\n";
		code += "  public static <T> T get(Class<T> clazz) {\n";
		for (Element classElement : elementModel.getClassElements()) {
			if (!elementHelper.isAbstract(classElement)) {
				String className = classElement.getSimpleName().toString();
				code += "      if(clazz == " + classElement + ".class) {\n";
				code += "        return (T)" + elementHelper.getPackageName(classElement) + "." + PACKAGE_INJECTOR_NAME + ".get"
						+ className + "();\n";
				code += "      }\n";
			}
		}
		code += "    throw new IllegalArgumentException(\"Class not found for injection:\" + clazz);\n";
		code += "    }\n";
		code += "}\n";
		return code;
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
		for (Element classElement : elementModel.getClassElements(packageElement)) {
			String className = elementHelper.getClassName(classElement);
			String variableName = getVariableName(classElement);
			boolean isSingleton = false;
			if (classElement.getAnnotation(Singleton.class) != null) {
				variableName += "Instance";
				code += "  private static " + classElement + " " + getVariableName(classElement) + "Instance = "
						+ construct(classElement, elementModel.getProvidedMethod(classElement)) + ";\n";
				code += "static { " + PACKAGE_INJECTOR_NAME + ".inject(" + variableName + "); }\n";
				isSingleton = true;
			}
			if (!elementHelper.isAbstract(classElement)) {
				code += "  public static " + className + " get" + classElement.getSimpleName() + "() {\n";
				if (!isSingleton) {
					code += "      " + className + " " + variableName + " = " + construct(classElement, elementModel.getProvidedMethod(classElement))
							+ ";\n";
					code += "      " + PACKAGE_INJECTOR_NAME + ".inject(" + variableName + ");\n";
				}
				code += "      return " + variableName + ";\n";
				code += "    }\n";
				code += "\n";
			}
			code += "  public static void inject(" + className + " " + variableName + ") {\n";
			code += populateMemberFields(packageElement, classElement, variableName);
			for (Element subClassElement : elementModel.getClassElements()) {
				if (elementHelper.isSubtype(classElement, subClassElement) && !subClassElement.equals(classElement)) {
					PackageElement subClassPackageElement = elementHelper.getPackageElement(subClassElement);
					code += populateMemberFields(subClassPackageElement, subClassElement, variableName);
				}
			}
			for (Element superClassElement : elementModel.getClassElements()) {
				if (elementHelper.isSubtype(superClassElement, classElement) && !superClassElement.equals(classElement)) {
					PackageElement superClassPackageElement = elementHelper.getPackageElement(superClassElement);
					code += "    if(" + variableName + " instanceof " + superClassElement + ") {\n";
					String superVariableName = getVariableName(superClassElement);
					code += "      " + superClassElement + " " + superVariableName + " = (" + superClassElement + ")" + variableName
							+ ";\n";
					code += populateMemberFields(superClassPackageElement, superClassElement, superVariableName);
					code += "    }\n";
				}
			}
			code += "    }\n";
			code += "\n";
		}
		code += "}";
		return code;
	}

	private String populateMemberFields(PackageElement packageElement, Element classElement, String variableName) {
		String code = "";
		List<Element> fieldElements = elementModel.getFieldElements(packageElement, classElement);
		if (elementModel.isTestClass(classElement)) {
			for (Element fieldElement : fieldElements) {
				String fieldVariable = getVariableName(fieldElement);
				code += "      " + variableName + "." + fieldVariable + " = ";
				Element fieldTypeElement = elementHelper.asElement(fieldElement.asType());
				code += construct(fieldTypeElement, fieldElement) + ";\n";
				List<Element> subFieldElements = elementModel.getFieldElements(elementHelper.getPackageElement(fieldTypeElement), fieldTypeElement);
				for(Element subFieldElement : subFieldElements) {
					String subFieldVariableName = getVariableName(subFieldElement);
					code += "      " + variableName + "." + fieldVariable + "." + subFieldVariableName + " = ";
					Element mock = elementModel.getMock(fieldTypeElement, subFieldElement);
					if (mock != null) {
						code += variableName + "." + mock.getSimpleName() + ";\n";
					} else {
						code += createObject(subFieldElement.asType(), subFieldElement) + ";\n";
					}
				}
			}
		} else {
			for (Element fieldElement : fieldElements) {
				String fieldVariable = getVariableName(fieldElement);
				code += "      " + variableName + "." + fieldVariable + " = ";
				code += createObject(fieldElement.asType(), fieldElement) + ";\n";
			}
		}
		return code;
	}

	private String createObject(TypeMirror type, Element referringElement) {
		if (referringElement.asType().getKind().isPrimitive()) {
			ExecutableElement providedMethod = elementModel.getProvidedMethod(referringElement);
			if (providedMethod != null) {
				Element moduleClassElement = providedMethod.getEnclosingElement();
				return construct(moduleClassElement, referringElement) + "." + providedMethod.getSimpleName().toString() + "()";
			}
			elementHelper.error(referringElement, "Primitive types must be provided");
			return "??";
		}
		Element classElement = elementHelper.asElement(type);
		PackageElement packageElement = elementHelper.getPackageElement(classElement);
		ExecutableElement providedMethod = elementModel.getProvidedMethod(referringElement);
		if (elementModel.containsClassElement(packageElement, classElement)) {
			return createFromGetterOnInjector(classElement);
		} else if (providedMethod != null) {
			Element moduleClassElement = providedMethod.getEnclosingElement();
			return construct(moduleClassElement, referringElement) + "." + providedMethod.getSimpleName().toString() + "()";
		} else {
			return construct(classElement, referringElement);
		}
	}

	private String construct(Element classElement, Element referringElement) {
		classElement = elementModel.rebind(new Provider(classElement, referringElement, elementModel));
		ExecutableElement constructorElement = elementModel.getInjectedConstructor(classElement);
		if (constructorElement == null) {
			PackageElement packageElement = elementHelper.getPackageElement(classElement);
			List<Element> fieldElements = elementModel.getFieldElements(packageElement, classElement);
			if (fieldElements.isEmpty()) {
				if (referringElement == null) {
					elementHelper.error(classElement, "Missing @Inject tags on target class " + classElement);
				} else if (elementModel.getProvidedMethod(referringElement) == null) {
					elementHelper.error(referringElement, "Missing @Inject tags on target class " + classElement);
				}
			}
			return "new " + classElement + "()";
		}
		String params = "";
		for (VariableElement param : constructorElement.getParameters()) {
			params += createObject(param.asType(), param) + ",";
		}
		if (params.length() > 0) {
			params = params.substring(0, params.length() - 1);
		}
		return "new " + classElement + "(" + params + ")";
	}

	private String createFromGetterOnInjector(Element classElement) {
		String className = classElement.getSimpleName().toString();
		String packageName = elementHelper.getPackageName(classElement);
		return packageName + "." + PACKAGE_INJECTOR_NAME + ".get" + className + "()";
	}

	private String getVariableName(Element classElement) {
		String name = classElement.getSimpleName().toString();
		return getVariableName(name);
	}

	private String getVariableName(String name) {
		if(name.contains(".")) {
			name = name.substring(name.lastIndexOf(".")+1);
		}
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}
}
