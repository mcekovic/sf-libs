package org.strangeforest.aspects;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import org.aspectj.lang.reflect.*;

public abstract class AnnotationDrivenAspectSupport<A extends Annotation, AI extends AnnotationInfo<A>> {

	private final Map<String, AI> annInfoCache = new ConcurrentHashMap<>();

	protected AI getAnnotationInfo(MethodSignature signature, Class<A> annClass) {
		String infoKey = signature.toLongString();
		AI annInfo = annInfoCache.get(infoKey);
		if (annInfo == null) {
			annInfo = createAnnotationInfo();
			Class declaringType = signature.getDeclaringType();
			annInfo.withType(declaringType);
			A classAnn = (A)declaringType.getAnnotation(annClass);
			if (classAnn != null)
				annInfo.withAnnotation(classAnn);
			Method method = signature.getMethod();
			A methodAnn = method.getAnnotation(annClass);
			if (methodAnn != null)
				annInfo.withAnnotation(methodAnn);
			annInfo.withMethodParamsAnnotations(method.getParameterAnnotations());
			annInfoCache.put(infoKey, annInfo);
		}
		return annInfo;
	}

	protected abstract AI createAnnotationInfo();
}
