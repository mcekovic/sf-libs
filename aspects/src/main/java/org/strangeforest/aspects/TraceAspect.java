package org.strangeforest.aspects;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.*;
import org.slf4j.*;
import org.strangeforest.annotation.*;
import org.strangeforest.performance.*;

@Aspect
public class TraceAspect extends AnnotationDrivenAspectSupport<Traceable, TraceAspect.TraceableInfo> {

	private final PerformanceMonitor performanceMonitor = PerformanceMonitor.instance();

	@Pointcut("execution(* *(..)) && @annotation(org.strangeforest.annotation.Traceable)")
	private void executionOfTraceableMethod() {}

	@Around("executionOfTraceableMethod()")
	public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		TraceableInfo trcInfo = getAnnotationInfo(signature, Traceable.class);
		Logger logger = trcInfo.logger;
		if (logger.isTraceEnabled()) {
			logger.trace(trcInfo.beforeMessage(signature, joinPoint.getArgs()));
			PerformanceInfo perfInfo = trcInfo.trackPerformance ? trackPerformance(signature) : null;
			long t0 = trcInfo.trackTime || trcInfo.trackPerformance ? System.currentTimeMillis() : 0L;
			Object retVal = joinPoint.proceed();
			long dt = trcInfo.trackTime || trcInfo.trackPerformance ? System.currentTimeMillis() - t0 : -1L;
			if (trcInfo.trackPerformance)
				perfInfo.after(dt);
			logger.trace(trcInfo.afterMessage(signature, retVal, dt));
			return retVal;
		}
		else if (trcInfo.trackPerformance) {
			PerformanceInfo perfInfo = trackPerformance(signature);
			long t0 = System.currentTimeMillis();
			Object retVal = joinPoint.proceed();
			perfInfo.after(System.currentTimeMillis() - t0);
			return retVal;
		}
		else
			return joinPoint.proceed();
	}

	private PerformanceInfo trackPerformance(MethodSignature signature) {
		PerformanceInfo perfInfo = performanceMonitor.getPerformanceInfo(signature.getDeclaringTypeName() + '.' + signature.getName());
		perfInfo.before();
		return perfInfo;
	}

	@Override protected TraceableInfo createAnnotationInfo() {
		return new TraceableInfo();
	}

	public static final class TraceableInfo extends MethodLoggingInfo<Traceable> {

		private Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		private boolean trackTime = false;
		private boolean trackPerformance = false;

		@Override protected void updateWithAnnotation(Traceable trcAnn) {
			String loggerName = trcAnn.logger();
			if (loggerName.length() > 0)
				logger = LoggerFactory.getLogger(loggerName);
			boolean trackTime = trcAnn.trackTime();
			if (trackTime)
				this.trackTime = true;
			boolean trackPerformance = trcAnn.trackPerformance();
			if (trackPerformance)
				this.trackPerformance = true;
		}
	}
}
