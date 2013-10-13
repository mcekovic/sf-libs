package org.strangeforest.util;

import java.util.*;
import java.text.*;

/**
 * <p>Format for <tt>boolean</tt> values.</p>
 */
public class BooleanFormat extends Format {

	private static final String DEFAULT_FORMAT = "true|false|";
	private static final FieldPosition START_POSITION = new FieldPosition(0);

	public static final BooleanFormat DEFAULT = new BooleanFormat();

	private String format;
	private String[] strings;

	private BooleanFormat() {
		this(DEFAULT_FORMAT);
	}

	public BooleanFormat(String format) {
		super();
		setFormat(format);
	}

	public String getFormat() {
		return format;
	}

	private void setFormat(String format) {
		this.format = format;
		strings = new String[3];
		StringTokenizer t = new StringTokenizer(format, "|");
		for (int i = 0; i < 3; i++)
			strings[i] = t.hasMoreTokens() ? t.nextToken() : StringUtil.EMPTY;
	}

	public String format(boolean value) {
		return format(Boolean.valueOf(value));
	}

	public String format(Boolean value) {
		return format(value, new StringBuffer(), START_POSITION).toString();
	}

	@Override public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		if (obj == null || obj instanceof Boolean)
			return format((Boolean)obj, toAppendTo, pos);
		else
			 throw new IllegalArgumentException("Cannot format given Object as a Boolean: " + obj.getClass().getName());
	}

	public StringBuffer format(Boolean value, StringBuffer toAppendTo, FieldPosition pos) {
		String text = value != null ? strings[value ? 0 : 1] : strings[2];
		toAppendTo.append(text);
		return toAppendTo;
	}

	public boolean parse(String text) throws ParseException {
		Boolean value = parseBoolean(text);
		if (value == null)
			throw new ParseException("Invalid value: " + text, 0);
		return value;
	}

	public Boolean parseBoolean(String text) throws ParseException {
		return (Boolean)parseObject(text);
	}

	@Override public Object parseObject(String source) throws ParseException {
		 ParsePosition pos = new ParsePosition(0);
		 Object result = parseObject(source, pos);
		 int errorIndex = pos.getErrorIndex();
		 if (errorIndex >= 0)
			  throw new ParseException("Cannot parse Boolean: " + source, errorIndex);
		 return result;
	}

	@Override public Object parseObject(String source, ParsePosition pos) {
		if (source.equalsIgnoreCase(strings[0]))
			return Boolean.TRUE;
		else if (source.equalsIgnoreCase(strings[1]))
			return Boolean.FALSE;
		else if (source.equalsIgnoreCase(strings[2]))
			return null;
		else {
			pos.setErrorIndex(0);
			return null;
		}
	}
}
