package org.strangeforest.db.gateway;

import java.io.*;
import java.net.*;
import java.util.*;

import org.strangeforest.util.*;
import org.strangeforest.xml.helpers.*;
import org.strangeforest.xml.util.*;

public class SQLs {

	private String sqlsFileName;
	private boolean hotSwap;
	private List<String> profiles;
	private File sqlsFile;
	private long lastModified;
	private ElementHelper sqlsRoot;
	private final Map<String, String> sqlCache;
	private final Map<String, ElementHelper> sqlElementCache;

	private static final String ELEMENT_SQL     = "sql";
	private static final String ELEMENT_INCLUDE = "include";
	private static final String ELEMENT_PROFILE = "profile";
	private static final String ATTR_NAME       = "name";

	public SQLs(String sqlsFileName, boolean hotSwap) {
		this();
		this.sqlsFileName = sqlsFileName;
		this.hotSwap = hotSwap;
		loadFromFile();
	}

	public SQLs(URI sqlsURI) {
		this();
		hotSwap = false;
		loadFromURI(sqlsURI);
	}

	public SQLs(InputStream inputStream) {
		this();
		hotSwap = false;
		loadFromStream(inputStream);
	}

	public SQLs(Class cls, String sqlsName) {
		this();
		hotSwap = false;
		InputStream in = cls.getResourceAsStream(sqlsName);
		if (in != null) {
			try {
				loadFromStream(in);
			}
			finally {
				try {
					in.close();
				}
				catch (IOException ignored) {}
			}
		}
		else
			throw new DBException("Cannot find SQLs: " + sqlsName);
	}

	public SQLs(String sqls) {
		this(new ByteArrayInputStream(sqls.getBytes()));
	}

	public SQLs(Map<String, String> sqls) {
		super();
		sqlCache = sqls;
		sqlElementCache = Collections.emptyMap();
		sqlsRoot = ParserUtil.parse(new ByteArrayInputStream("<sqls/>".getBytes()));
	}

	private SQLs() {
		super();
		sqlCache = new HashMap<>();
		sqlElementCache = new HashMap<>();
	}

	public List<String> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<String> profiles) {
		this.profiles = profiles;
	}

	public boolean hasProfile(String profile) {
		return profiles != null && profiles.contains(profile);
	}

	public void setProfiles(String profiles) {
		this.profiles =  CSVUtil.toStringList(profiles);
	}

	public String getSQL(String sqlName) {
		checkForNew();
		synchronized (sqlCache) {
			String sql = sqlCache.get(sqlName);
			if (sql == null) {
				ElementHelper sqlHelper = sqlsRoot.findElementByAttrValue(ELEMENT_SQL, ATTR_NAME, sqlName);
				processProfiles(sqlHelper);
				processIncludes(sqlHelper);
				sql = sqlHelper.getTextContent().trim();
				sqlCache.put(sqlName, sql);
			}
			return sql;
		}
	}

	public String getSQL(String sqlName, SQLTransformer transformer) {
		if (transformer == null)
			return getSQL(sqlName);
		else if (transformer instanceof CachedSQLTransformer)
			return getCachedTransformedSQL(sqlName, (CachedSQLTransformer)transformer);
		else
			return getTransformedSQL(sqlName, transformer);
	}

	private String getTransformedSQL(String sqlName, SQLTransformer transformer) {
		ElementHelper sqlElement = getSQLElement(sqlName);
		transformer.transform(sqlElement);
		return sqlElement.getTextContent().trim();
	}

	private String getCachedTransformedSQL(String sqlName, CachedSQLTransformer transformer) {
		String sqklKey = sqlName + '$' + transformer.cacheKey();
		synchronized (sqlCache) {
			String sql = sqlCache.get(sqklKey);
			if (sql == null) {
				sql = getTransformedSQL(sqlName, transformer);
				sqlCache.put(sqklKey, sql);
			}
			return sql;
		}
	}

	public ElementHelper getSQLElement(String sqlName) {
		checkForNew();
		ElementHelper sqlHelper;
		synchronized (sqlElementCache) {
			sqlHelper = sqlElementCache.get(sqlName);
			if (sqlHelper == null) {
				sqlHelper = sqlsRoot.findElementByAttrValue(ELEMENT_SQL, ATTR_NAME, sqlName);
				processProfiles(sqlHelper);
				processIncludes(sqlHelper);
				sqlElementCache.put(sqlName, sqlHelper);
			}
			return (ElementHelper)sqlHelper.cloneNode(true);
		}
	}

	private void processIncludes(ElementHelper sqlHelper) {
		for (NodeHelper include : sqlHelper.findElements(ELEMENT_INCLUDE)) {
			String includeName = ((ElementHelper)include).getAttribute(ATTR_NAME);
			include.replace(getSQLElement(includeName));
		}
	}

	private void processProfiles(ElementHelper sqlHelper) {
		for (NodeHelper profileNode : sqlHelper.findElements(ELEMENT_PROFILE)) {
			String profileName = ((ElementHelper)profileNode).getAttribute(ATTR_NAME);
			if (!hasProfile(profileName))
				profileNode.remove();
		}
	}

	private void loadFromFile() {
		sqlsRoot = ParserUtil.parse(sqlsFileName);
		sqlsFile = new File(sqlsFileName);
		lastModified = sqlsFile.lastModified();
	}

	private void loadFromURI(URI sqlsURI) {
		sqlsRoot = ParserUtil.parse(sqlsURI);
	}

	private void loadFromStream(InputStream inputStream) {
		sqlsRoot = ParserUtil.parse(inputStream);
	}

	private synchronized void checkForNew() {
		if (hotSwap && sqlsFile.lastModified() > lastModified) {
			clearCache();
			reload();
		}
	}

	private void clearCache() {
		synchronized (sqlCache) {
			sqlCache.clear();
		}
		synchronized (sqlElementCache) {
			sqlElementCache.clear();
		}
	}

	private void reload() {
		loadFromFile();
	}
}
