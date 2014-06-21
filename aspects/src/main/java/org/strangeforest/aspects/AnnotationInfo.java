package org.strangeforest.aspects;

import java.lang.annotation.*;

public abstract class AnnotationInfo<A extends Annotation> {

	protected abstract void updateWithAnnotation(A ann);
	protected void updateWithMethodParamsAnnotations(Annotation[][] paramsAnns) {}
}
