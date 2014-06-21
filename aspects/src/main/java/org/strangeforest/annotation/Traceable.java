package org.strangeforest.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Traceable {
	String logger() default AnnotationUtil.DEFAULT_LOGGER_NAME;
	boolean trackTime() default false;
	boolean trackPerformance() default false;
}
