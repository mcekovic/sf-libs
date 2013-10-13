package org.strangeforest.annotation;

import java.lang.annotation.*;

import org.strangeforest.util.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandleException {
	Class[] exceptions() default {};
	String logger() default StringUtil.EMPTY;
	Class<? extends Throwable> wrapInto() default AnnotationUtil.$DummyException.class;
	boolean strictWrapping() default false;
}
