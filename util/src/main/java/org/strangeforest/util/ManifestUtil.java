package org.strangeforest.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.regex.*;

public abstract class ManifestUtil {

	private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

	public static String getManifestAttribute(String classpathItem, String attrName) throws IOException {
		return getManifestAttribute(null, classpathItem, null, attrName);
	}

	public static String getManifestAttribute(String classpathItem, String implementationTitle, String attrName) throws IOException {
		return getManifestAttribute(null, classpathItem, implementationTitle, attrName);
	}

	public static String getManifestAttribute(ClassLoader classLoader, String classpathItem, String implementationTitle, String attrName) throws IOException {
		Pattern classpathPattern = classpathItem != null ? Pattern.compile(classpathItem) : null;
		for (Enumeration<URL> urls = classLoader != null ? classLoader.getResources(MANIFEST_PATH) : ClassLoader.getSystemResources(MANIFEST_PATH); urls.hasMoreElements(); ) {
			URL url = urls.nextElement();
			if (classpathPattern == null || matchesClasspath(classpathPattern, url)) {
				try (InputStream in = new BufferedInputStream(url.openStream())) {
					Attributes attributes = new Manifest(in).getMainAttributes();
					if (implementationTitle == null || implementationTitle.equals(attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE)))
						return attributes.getValue(attrName);
				}
			}
		}
		return "Unknown";
	}

	private static boolean matchesClasspath(Pattern pattern, URL url) {
		String urlPath = url.toString();
		int itemEnd = urlPath.length() - MANIFEST_PATH.length() - 1;
		boolean isJar = urlPath.charAt(itemEnd - 1) == '!';
		if (isJar)
			itemEnd--;
		else
			return true;
		int lastDash = urlPath.lastIndexOf('/', itemEnd - 1);
		int itemStart = lastDash >= 0 ? lastDash + 1 : 0;
		String urlItem = urlPath.substring(itemStart, itemEnd);
		return pattern.matcher(urlItem).find();
	}
}
