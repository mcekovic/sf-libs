package org.strangeforest.aspects;

import java.lang.annotation.*;

public abstract class AnnotationInfo<A extends Annotation> {

	public void withType(Class type) {}
	public abstract void withAnnotation(A ann);
	public void withMethodParamsAnnotations(Annotation[][] paramsAnns) {}
}
