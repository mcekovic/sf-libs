package org.strangeforest.util;

import java.beans.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;

/**
 * <p>Utility class for accessing JavaBean properties by name.</p>
 */
public abstract class BeanUtil {

	private static final Map<String, Method> readMethodCache = new HashMap<>();
	private static final Map<String, Method> writeMethodCache = new HashMap<>();

	public static Object getProperty(Object bean, String name) {
		try {
			String key = getKey(bean, name);
			Method method;
			synchronized (readMethodCache) {
				method = readMethodCache.get(key);
				if (method == null) {
					method = getPropertyDescriptor(bean.getClass(), name).getReadMethod();
					method.setAccessible(true);
					readMethodCache.put(key, method);
				}
			}
			return method.invoke(bean);
		}
		catch (Exception ex) {
			throw new BeanException("Can not access property ''{0}'' in class {1}.", ex, name, bean.getClass().getName());
		}
	}

	public static void setProperty(Object bean, String name, Object value) {
		try {
			getWriteMethod(bean, name).invoke(bean, value);
		}
		catch (Exception ex) {
			throw new BeanException("Can not set property ''{0}'' in class {1}.", ex, name, bean.getClass().getName());
		}
	}

	private static Method getWriteMethod(Object bean, String name) {
		String key = getKey(bean, name);
		Method method;
		synchronized (writeMethodCache) {
			method = writeMethodCache.get(key);
			if (method == null) {
				method = getPropertyDescriptor(bean.getClass(), name).getWriteMethod();
				method.setAccessible(true);
				writeMethodCache.put(key, method);
			}
		}
		return method;
	}

	public static Object evalExpression(Object bean, String expression) {
		Object obj = bean;
		for (StringTokenizer st = new StringTokenizer(expression, "."); st.hasMoreTokens() && obj != null; ) {
			if (obj instanceof Map)
				obj = ((Map)obj).get(st.nextToken());
			else
				obj = getProperty(obj, st.nextToken());
		}
		return obj;
	}

	public static void copyProperties(Object source, Object target, String... ignoreProperties) throws BeanException {
		copyProperties(source, target, null, ignoreProperties);
	}

	public static void copyProperties(Object source, Object target, Function propertyFunction, String... ignoreProperties) throws BeanException {
		Class sourceClass = source.getClass();
		Class targetClass = target.getClass();
		PropertyDescriptor[] targetDescs = getPropertyDescriptors(targetClass);
		List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;
		for (PropertyDescriptor targetDesc : targetDescs) {
			Method writeMethod = targetDesc.getWriteMethod();
			String propName = targetDesc.getName();
			if (writeMethod != null && (ignoreProperties == null || !ignoreList.contains(propName))) {
				PropertyDescriptor sourceDesc = getPropertyDescriptor(sourceClass, propName);
				if (sourceDesc != null) {
					Method readMethod = sourceDesc.getReadMethod();
					if (sourceDesc.getReadMethod() != null) {
						try {
							Object value = readMethod.invoke(source, null);
							if (propertyFunction != null)
								value = propertyFunction.apply(value);
							writeMethod.invoke(target, value);
						}
						catch (Throwable ex) {
							throw new BeanException("Could not copy property ''{0}'' from class ''{1}'' to class ''{2}''.", ex, propName, sourceClass.getName(), targetClass.getName());
						}
					}
				}
			}
		}
	}

	private static final Set<String> DEFAULT_SKIP_PROPERTIES = Collections.singleton("class");

	public static Map<String, Object> toMap(Object bean) {
		return toMap(bean, null);
	}

	public static Map<String, Object> toMap(Object bean, String... ignoreProperties) {
		Map<String, Object> map = new HashMap<>();
		List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;
		for (PropertyDescriptor desc : getPropertyDescriptors(bean.getClass())) {
			String name = desc.getName();
			if (DEFAULT_SKIP_PROPERTIES.contains(name))
				continue;
			if (ignoreProperties != null && ignoreList.contains(name))
				continue;
			map.put(name, getProperty(bean, name));
		}
		return map;
	}

	public static void setProperties(Object bean, Map<String, Object> properties) {
		for (Map.Entry<String, Object> property : properties.entrySet())
			setProperty(bean, property.getKey(), property.getValue());
	}

	private static PropertyDescriptor getPropertyDescriptor(Class cls, String name) {
		for (PropertyDescriptor desc : getPropertyDescriptors(cls))
			if (desc.getName().equals(name))
				return desc;
		String msg = MessageFormat.format("Can not find property {0} in {1}", name, cls.getName());
		throw new IllegalArgumentException(msg);
	}

	//TODO Cache property descriptors 
	private static PropertyDescriptor[] getPropertyDescriptors(Class cls) {
		try {
			return Introspector.getBeanInfo(cls).getPropertyDescriptors();
		}
		catch (IntrospectionException ex) {
			throw new BeanException("Can not introspect bean class {0}.", ex, cls.getName());
		}
	}

	private static String getKey(Object bean, String name) {
		String className = bean.getClass().getName();
		StringBuilder key = new StringBuilder(className.length() + name.length() + 1);
		key.append(className);
		key.append('.');
		key.append(name);
		return key.toString();
	}
}
