package org.strangeforest.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retried {
	Class[] exceptions() default {};
	int count() default 1;
}
