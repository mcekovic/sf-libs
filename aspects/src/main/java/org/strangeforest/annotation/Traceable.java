package org.strangeforest.annotation;

import java.lang.annotation.*;

import org.strangeforest.util.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Traceable {
	String logger() default StringUtil.EMPTY;
	boolean trackTime() default false;
	boolean trackPerformance() default false;
}
