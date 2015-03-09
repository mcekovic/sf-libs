package org.strangeforest.annotation;

import java.lang.annotation.*;

import org.strangeforest.util.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retried {
	Class[] exceptions() default {};
	int count() default 1;
	String logger() default StringUtil.EMPTY;
}
