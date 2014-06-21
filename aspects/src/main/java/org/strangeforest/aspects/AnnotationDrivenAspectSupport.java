package org.strangeforest.aspects;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import org.aspectj.lang.reflect.*;

public abstract class AnnotationDrivenAspectSupport<A extends Annotation, AI extends AnnotationInfo<A>> {

	private final Map<String, AI> annInfoCache = new ConcurrentHashMap<>();

	public AI getAnnotationInfo(MethodSignature signature, Class<A> annClass) {
		String infoKey = signature.toLongString();
		AI annInfo = annInfoCache.get(infoKey);
		if (annInfo == null) {
			annInfo = createAnnotationInfo();
			A classAnn = (A)signature.getDeclaringType().getAnnotation(annClass);
			if (classAnn != null)
				annInfo.updateWithAnnotation(classAnn);
			Method method = signature.getMethod();
			A methodAnn = method.getAnnotation(annClass);
			if (methodAnn != null)
				annInfo.updateWithAnnotation(methodAnn);
			annInfo.updateWithMethodParamsAnnotations(method.getParameterAnnotations());
			annInfoCache.put(infoKey, annInfo);
		}
		return annInfo;
	}

	protected abstract AI createAnnotationInfo();
}
