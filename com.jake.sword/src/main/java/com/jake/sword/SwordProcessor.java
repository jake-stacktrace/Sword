package com.jake.sword;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes(value = {"javax.inject.Inject","com.jake.Provides","javax.inject.Singleton","javax.inject.Named"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SwordProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        ElementHelper elementHelper = new ElementHelper(processingEnv);
        ElementModel elementModel = new ElementModel(elementHelper);
        new ElementProcessor(elementHelper, elementModel).process(env);
        new CodeGenerator(processingEnv, elementModel).generateCode();
        return true;
    }
}
