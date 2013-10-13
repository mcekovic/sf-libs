package org.strangeforest.aspects;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.*;
import org.strangeforest.annotation.*;

@Aspect
public class RetriedAspect extends AnnotationDrivenAspectSupport<Retried, RetriedAspect.RetriedInfo> {

	@Pointcut("execution(* *(..)) && @annotation(org.strangeforest.annotation.Retried)")
	private void executionOfRetriedMethod() {}

	@Around("executionOfRetriedMethod()")
	public Object retry(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		RetriedInfo retriedInfo = getAnnotationInfo(signature, Retried.class);
		for (int i = 0; i < retriedInfo.count; i++) {
			try {
				return joinPoint.proceed();
			}
			catch (Throwable th) {
				if (!retriedInfo.isInstance(th))
					throw th;
			}
		}
		return joinPoint.proceed();
	}

	@Override protected RetriedInfo getAnnotationInfo(Retried retriedAnn, RetriedInfo retriedInfo) {
		if (retriedInfo == null)
			retriedInfo = new RetriedInfo();
		if (retriedAnn != null) {
			Class[] exceptions = retriedAnn.exceptions();
			if (exceptions.length > 0)
				retriedInfo.exceptions = exceptions;
			retriedInfo.count = retriedAnn.count();
		}
		return retriedInfo;
	}

	public static final class RetriedInfo {
		private Class[] exceptions = new Class[] {Throwable.class};
		private int count;

		boolean isInstance(Throwable th) {
			if (exceptions != null) {
				for (Class ex : exceptions) {
					if (ex.isInstance(th))
						return true;
				}
			}
			return false;
		}
	}
}
