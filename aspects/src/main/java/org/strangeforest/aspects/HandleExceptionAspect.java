package org.strangeforest.aspects;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.*;
import org.slf4j.*;
import org.strangeforest.annotation.*;
import org.strangeforest.util.*;

@Aspect
public class HandleExceptionAspect extends AnnotationDrivenAspectSupport<HandleException, HandleExceptionAspect.HandleExInfo> {

	@Pointcut("execution(* *(..)) && @annotation(org.strangeforest.annotation.HandleException)")
	private void executionOfHandleExceptionMethod() {}

	@Around("executionOfHandleExceptionMethod()")
	public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		HandleExInfo handleExInfo = getAnnotationInfo(signature, HandleException.class);
		try {
			return joinPoint.proceed();
		}
		catch (Throwable th) {
			if (handleExInfo.isInstance(th)) {
				Logger logger = handleExInfo.logger;
				if (logger != null && logger.isErrorEnabled())
					logger.error(handleExInfo.beforeMessage(signature, joinPoint.getArgs()), th);
				Class<? extends Throwable> wrapInto = handleExInfo.wrapInto;
				if (wrapInto != null) {
					if (handleExInfo.strictWrapping)
						throw ExceptionUtil.strictlyWrap(handleExInfo.wrapInto, th);
					else
						throw ExceptionUtil.wrap(handleExInfo.wrapInto, th);
				}
			}
			throw th;
		}
	}

	@Override protected HandleExInfo createAnnotationInfo() {
		return new HandleExInfo();
	}

	public static final class HandleExInfo extends MethodLoggingInfo<HandleException> {

		private Class[] exceptions = new Class[] {Throwable.class};
		private Logger logger;
		private Class<? extends Throwable> wrapInto;
		private boolean strictWrapping = false;

		@Override protected void updateWithAnnotation(HandleException handleExAnn) {
			Class[] exceptions = handleExAnn.exceptions();
			if (exceptions.length > 0)
				this.exceptions = exceptions;
			String loggerName = handleExAnn.logger();
			if (loggerName.length() > 0)
				logger = LoggerFactory.getLogger(loggerName);
			Class<? extends Throwable> wrapInto = handleExAnn.wrapInto();
			if (!wrapInto.equals(AnnotationUtil.NO_EXCEPTION))
				this.wrapInto = wrapInto;
			boolean strictWrapping = handleExAnn.strictWrapping();
			if (strictWrapping)
				this.strictWrapping = true;
		}

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
