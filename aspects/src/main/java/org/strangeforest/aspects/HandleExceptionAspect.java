package org.strangeforest.aspects;

import java.lang.annotation.*;
import java.util.*;

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
				if (handleExInfo.logger != null && logger.isErrorEnabled())
					logger.error(MethodLoggingUtil.beforeMessage(signature, joinPoint.getArgs(), handleExInfo.skipParams, handleExInfo.maskParams), th);
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

	@Override protected HandleExInfo getAnnotationInfo(HandleException handleExAnn, HandleExInfo handleExInfo) {
		if (handleExInfo == null)
			handleExInfo = new HandleExInfo();
		if (handleExAnn != null) {
			Class[] exceptions = handleExAnn.exceptions();
			if (exceptions.length > 0)
				handleExInfo.exceptions = exceptions;
			String loggerName = handleExAnn.logger();
			if (loggerName.length() > 0)
				handleExInfo.logger = LoggerFactory.getLogger(loggerName);
			Class<? extends Throwable> wrapInto = handleExAnn.wrapInto();
			if (!wrapInto.equals(AnnotationUtil.NO_EXCEPTION))
				handleExInfo.wrapInto = wrapInto;
			boolean strictWrapping = handleExAnn.strictWrapping();
			if (strictWrapping)
				handleExInfo.strictWrapping = true;
		}
		return handleExInfo;
	}

	@Override protected void updateAnnotationInfo(HandleExInfo annInfo, Annotation[][] paramsAnns) {
		for (int i = 0; i < paramsAnns.length; i++) {
			for (Annotation paramAnn : paramsAnns[i]) {
				if (paramAnn instanceof Skip)
					annInfo.skip(i);
				if (paramAnn instanceof Mask)
					annInfo.mask(i);
			}
		}
	}

	public static final class HandleExInfo {

		private Class[] exceptions = new Class[] {Throwable.class};
		private Logger logger;
		private Class<? extends Throwable> wrapInto;
		private boolean strictWrapping = false;
		private List<Integer> skipParams;
		private List<Integer> maskParams;

		boolean isInstance(Throwable th) {
			if (exceptions != null) {
				for (Class ex : exceptions) {
					if (ex.isInstance(th))
						return true;
				}
			}
			return false;
		}

		void skip(int i) {
			if (skipParams == null)
				skipParams = new ArrayList<>();
			skipParams.add(i);
		}

		void mask(int i) {
			if (maskParams == null)
				maskParams = new ArrayList<>();
			maskParams.add(i);
		}
	}
}
