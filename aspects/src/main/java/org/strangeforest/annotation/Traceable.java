package org.strangeforest.annotation;

import java.lang.annotation.*;

import org.slf4j.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Traceable {
	String logger() default Logger.ROOT_LOGGER_NAME;
	boolean trackTime() default false;
	boolean trackPerformance() default false;
}
