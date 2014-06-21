package org.strangeforest.aspects;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.*;
import org.slf4j.*;
import org.strangeforest.annotation.*;

@Aspect
public class RetriedAspect extends AnnotationDrivenAspectSupport<Retried, RetriedAspect.RetriedInfo> {

	@Pointcut("execution(* *(..)) && @annotation(org.strangeforest.annotation.Retried)")
	private void executionOfRetriedMethod() {}

	@Around("executionOfRetriedMethod()")
	public Object retry(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		RetriedInfo retriedInfo = getAnnotationInfo(signature, Retried.class);
		for (int retry = 1; retry <= retriedInfo.count; retry++) {
			try {
				return joinPoint.proceed();
			}
			catch (Throwable th) {
				if (retriedInfo.isInstance(th)) {
					Logger logger = retriedInfo.logger;
					if (logger != null && logger.isTraceEnabled())
						logger.trace("Retry " + retry + ": " + retriedInfo.beforeMessage(signature, joinPoint.getArgs()));
				}
				else
					throw th;
			}
		}
		return joinPoint.proceed();
	}

	@Override protected RetriedInfo createAnnotationInfo() {
		return new RetriedInfo();
	}

	public static final class RetriedInfo extends MethodLoggingInfo<Retried> {

		private Logger logger;
		private Class[] exceptions = new Class[] {Throwable.class};
		private int count;

		@Override public void updateWithAnnotation(Retried retriedAnn) {
			Class[] exceptions = retriedAnn.exceptions();
			if (exceptions.length > 0)
				this.exceptions = exceptions;
			count = retriedAnn.count();
			String loggerName = retriedAnn.logger();
			if (loggerName.length() > 0)
				logger = LoggerFactory.getLogger(loggerName);
		}

		public boolean isInstance(Throwable th) {
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
