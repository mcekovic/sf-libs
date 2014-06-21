package org.strangeforest.aspects;

import java.lang.annotation.*;
import java.time.*;
import java.util.*;

import org.aspectj.lang.reflect.*;
import org.slf4j.*;
import org.strangeforest.annotation.*;
import org.strangeforest.util.*;

import static org.strangeforest.annotation.AnnotationUtil.*;

public abstract class MethodLoggingInfo<A extends Annotation> extends AnnotationInfo<A> {

	private Class type;
	Logger logger;
	List<Integer> skipParams;
	List<Integer> maskParams;

	@Override public void withType(Class type) {
		this.type = type;
	}

	protected void withLogger(String logger) {
		if (logger.length() > 0) {
			if (logger.equals(DEFAULT_LOGGER_NAME))
				this.logger = LoggerFactory.getLogger(type);
			else
				this.logger = LoggerFactory.getLogger(logger);
		}
	}

	@Override public void withMethodParamsAnnotations(Annotation[][] paramsAnns) {
		for (int i = 0; i < paramsAnns.length; i++) {
			for (Annotation paramAnn : paramsAnns[i]) {
				if (paramAnn instanceof Skip)
					skip(i);
				if (paramAnn instanceof Mask)
					mask(i);
			}
		}
	}

	private void skip(int i) {
		if (skipParams == null)
			skipParams = new ArrayList<>();
		skipParams.add(i);
	}

	private void mask(int i) {
		if (maskParams == null)
			maskParams = new ArrayList<>();
		maskParams.add(i);
	}

	public String beforeMessage(MethodSignature signature, Object[] paramValues) {
		StringBuilder sb = new StringBuilder();
		sb.append(signature.getName()).append('(');
		String[] paramNames = signature.getParameterNames();
		boolean first = true;
		for (int i = 0, len = paramValues.length; i < len ; i++) {
			if (skipParams != null && skipParams.contains(i))
				continue;
			if (first)
				first = false;
			else
				sb.append(", ");
			String paramValue = StringUtil.toString(paramValues[i]);
			sb.append(String.valueOf(paramNames[i])).append('=');
			if (maskParams != null && maskParams.contains(i))
				sb.append(StringUtil.copy('*', paramValue.length()));
			else
				sb.append(paramValue);
		}
		return sb.append(')').toString();
	}

	public String afterMessage(MethodSignature signature, Object returnValue, long dt) {
		StringBuilder sb = new StringBuilder();
		sb.append('~').append(signature.getName());
		if (!signature.getReturnType().equals(void.class))
			sb.append('=').append(returnValue);
		if (dt != -1L)
			sb.append(" [").append(Duration.ofMillis(dt)).append(']');
		return sb.toString();
	}
}
