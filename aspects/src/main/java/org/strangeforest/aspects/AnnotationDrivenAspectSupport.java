package org.strangeforest.aspects;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import org.aspectj.lang.reflect.*;

public abstract class AnnotationDrivenAspectSupport<A extends Annotation, AI> {

	private final Map<String, AI> annInfoCache = new ConcurrentHashMap<>();

	public AI getAnnotationInfo(MethodSignature signature, Class<A> annClass) {
		String infoKey = signature.toLongString();
		AI annInfo = annInfoCache.get(infoKey);
		if (annInfo == null) {
			annInfo = getAnnotationInfo((A)signature.getDeclaringType().getAnnotation(annClass), annInfo);
			Method method = signature.getMethod();
			annInfo = getAnnotationInfo(method.getAnnotation(annClass), annInfo);
			updateAnnotationInfo(annInfo, method.getParameterAnnotations());
			annInfoCache.put(infoKey, annInfo);
		}
		return annInfo;
	}

	protected abstract AI getAnnotationInfo(A ann, AI annInfo);

	protected void updateAnnotationInfo(AI annInfo, Annotation[][] paramAnns) {}
}
