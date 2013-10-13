package org.strangeforest.aspects;

import org.aspectj.lang.annotation.*;

@Aspect @DeclarePrecedence("TraceAspect,HandleExceptionAspect,Retried,*,TransactionAspect")
public class AspectsPrecedence {}
