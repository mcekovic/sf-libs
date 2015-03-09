package org.strangeforest.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Transactional {
	Propagation propagation() default Propagation.REQUIRED;
	boolean rollbackOnly() default false;
}
