package org.strangeforest.aspects;

import java.util.*;

import org.aspectj.lang.reflect.*;
import org.joda.time.*;
import org.joda.time.format.*;
import org.strangeforest.util.*;

public abstract class MethodLoggingUtil {

	private static final PeriodFormatter PERIOD_FORMATTER = PeriodFormat.wordBased();

	public static String beforeMessage(MethodSignature signature, Object[] paramValues, List<Integer> skipParams, List<Integer> maskParams) {
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

	public static String afterMessage(MethodSignature signature, Object returnValue, long dt) {
		StringBuilder sb = new StringBuilder();
		sb.append('~').append(signature.getName());
		if (!signature.getReturnType().equals(void.class))
			sb.append('=').append(returnValue);
		if (dt != -1L)
			sb.append(" [").append(new Period(dt).toString(PERIOD_FORMATTER)).append(']');
		return sb.toString();
	}
}
