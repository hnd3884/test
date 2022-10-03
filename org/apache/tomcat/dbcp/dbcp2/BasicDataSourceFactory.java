package org.apache.tomcat.dbcp.dbcp2;

import java.util.LinkedHashMap;
import org.apache.juli.logging.LogFactory;
import java.util.StringTokenizer;
import java.util.Collection;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Arrays;
import javax.naming.RefAddr;
import java.util.Iterator;
import java.util.Properties;
import java.util.ArrayList;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.util.List;
import java.util.Map;
import org.apache.juli.logging.Log;
import javax.naming.spi.ObjectFactory;

public class BasicDataSourceFactory implements ObjectFactory
{
    private static final Log log;
    private static final String PROP_DEFAULT_AUTO_COMMIT = "defaultAutoCommit";
    private static final String PROP_DEFAULT_READ_ONLY = "defaultReadOnly";
    private static final String PROP_DEFAULT_TRANSACTION_ISOLATION = "defaultTransactionIsolation";
    private static final String PROP_DEFAULT_CATALOG = "defaultCatalog";
    private static final String PROP_DEFAULT_SCHEMA = "defaultSchema";
    private static final String PROP_CACHE_STATE = "cacheState";
    private static final String PROP_DRIVER_CLASS_NAME = "driverClassName";
    private static final String PROP_LIFO = "lifo";
    private static final String PROP_MAX_TOTAL = "maxTotal";
    private static final String PROP_MAX_IDLE = "maxIdle";
    private static final String PROP_MIN_IDLE = "minIdle";
    private static final String PROP_INITIAL_SIZE = "initialSize";
    private static final String PROP_MAX_WAIT_MILLIS = "maxWaitMillis";
    private static final String PROP_TEST_ON_CREATE = "testOnCreate";
    private static final String PROP_TEST_ON_BORROW = "testOnBorrow";
    private static final String PROP_TEST_ON_RETURN = "testOnReturn";
    private static final String PROP_TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
    private static final String PROP_NUM_TESTS_PER_EVICTION_RUN = "numTestsPerEvictionRun";
    private static final String PROP_MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
    private static final String PROP_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = "softMinEvictableIdleTimeMillis";
    private static final String PROP_EVICTION_POLICY_CLASS_NAME = "evictionPolicyClassName";
    private static final String PROP_TEST_WHILE_IDLE = "testWhileIdle";
    private static final String PROP_PASSWORD = "password";
    private static final String PROP_URL = "url";
    private static final String PROP_USER_NAME = "username";
    private static final String PROP_VALIDATION_QUERY = "validationQuery";
    private static final String PROP_VALIDATION_QUERY_TIMEOUT = "validationQueryTimeout";
    private static final String PROP_JMX_NAME = "jmxName";
    private static final String PROP_CONNECTION_FACTORY_CLASS_NAME = "connectionFactoryClassName";
    private static final String PROP_CONNECTION_INIT_SQLS = "connectionInitSqls";
    private static final String PROP_ACCESS_TO_UNDERLYING_CONNECTION_ALLOWED = "accessToUnderlyingConnectionAllowed";
    private static final String PROP_REMOVE_ABANDONED_ON_BORROW = "removeAbandonedOnBorrow";
    private static final String PROP_REMOVE_ABANDONED_ON_MAINTENANCE = "removeAbandonedOnMaintenance";
    private static final String PROP_REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";
    private static final String PROP_LOG_ABANDONED = "logAbandoned";
    private static final String PROP_ABANDONED_USAGE_TRACKING = "abandonedUsageTracking";
    private static final String PROP_POOL_PREPARED_STATEMENTS = "poolPreparedStatements";
    private static final String PROP_CLEAR_STATEMENT_POOL_ON_RETURN = "clearStatementPoolOnReturn";
    private static final String PROP_MAX_OPEN_PREPARED_STATEMENTS = "maxOpenPreparedStatements";
    private static final String PROP_CONNECTION_PROPERTIES = "connectionProperties";
    private static final String PROP_MAX_CONN_LIFETIME_MILLIS = "maxConnLifetimeMillis";
    private static final String PROP_LOG_EXPIRED_CONNECTIONS = "logExpiredConnections";
    private static final String PROP_ROLLBACK_ON_RETURN = "rollbackOnReturn";
    private static final String PROP_ENABLE_AUTO_COMMIT_ON_RETURN = "enableAutoCommitOnReturn";
    private static final String PROP_DEFAULT_QUERY_TIMEOUT = "defaultQueryTimeout";
    private static final String PROP_FAST_FAIL_VALIDATION = "fastFailValidation";
    private static final String PROP_DISCONNECTION_SQL_CODES = "disconnectionSqlCodes";
    private static final String NUPROP_MAX_ACTIVE = "maxActive";
    private static final String NUPROP_REMOVE_ABANDONED = "removeAbandoned";
    private static final String NUPROP_MAXWAIT = "maxWait";
    private static final String SILENT_PROP_FACTORY = "factory";
    private static final String SILENT_PROP_SCOPE = "scope";
    private static final String SILENT_PROP_SINGLETON = "singleton";
    private static final String SILENT_PROP_AUTH = "auth";
    private static final String[] ALL_PROPERTIES;
    private static final Map<String, String> NUPROP_WARNTEXT;
    private static final List<String> SILENT_PROPERTIES;
    
    @Override
    public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx, final Hashtable<?, ?> environment) throws Exception {
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        final Reference ref = (Reference)obj;
        if (!"javax.sql.DataSource".equals(ref.getClassName())) {
            return null;
        }
        final List<String> warnings = new ArrayList<String>();
        final List<String> infoMessages = new ArrayList<String>();
        this.validatePropertyNames(ref, name, warnings, infoMessages);
        for (final String warning : warnings) {
            BasicDataSourceFactory.log.warn((Object)warning);
        }
        for (final String infoMessage : infoMessages) {
            BasicDataSourceFactory.log.info((Object)infoMessage);
        }
        final Properties properties = new Properties();
        for (final String propertyName : BasicDataSourceFactory.ALL_PROPERTIES) {
            final RefAddr ra = ref.get(propertyName);
            if (ra != null) {
                final String propertyValue = ra.getContent().toString();
                properties.setProperty(propertyName, propertyValue);
            }
        }
        return createDataSource(properties);
    }
    
    private void validatePropertyNames(final Reference ref, final Name name, final List<String> warnings, final List<String> infoMessages) {
        final List<String> allPropsAsList = Arrays.asList(BasicDataSourceFactory.ALL_PROPERTIES);
        final String nameString = (name != null) ? ("Name = " + name.toString() + " ") : "";
        if (BasicDataSourceFactory.NUPROP_WARNTEXT != null && !BasicDataSourceFactory.NUPROP_WARNTEXT.isEmpty()) {
            for (final String propertyName : BasicDataSourceFactory.NUPROP_WARNTEXT.keySet()) {
                final RefAddr ra = ref.get(propertyName);
                if (ra != null && !allPropsAsList.contains(ra.getType())) {
                    final StringBuilder stringBuilder = new StringBuilder(nameString);
                    final String propertyValue = ra.getContent().toString();
                    stringBuilder.append(BasicDataSourceFactory.NUPROP_WARNTEXT.get(propertyName)).append(" You have set value of \"").append(propertyValue).append("\" for \"").append(propertyName).append("\" property, which is being ignored.");
                    warnings.add(stringBuilder.toString());
                }
            }
        }
        final Enumeration<RefAddr> allRefAddrs = ref.getAll();
        while (allRefAddrs.hasMoreElements()) {
            final RefAddr ra2 = allRefAddrs.nextElement();
            final String propertyName2 = ra2.getType();
            if (!allPropsAsList.contains(propertyName2) && !BasicDataSourceFactory.NUPROP_WARNTEXT.containsKey(propertyName2) && !BasicDataSourceFactory.SILENT_PROPERTIES.contains(propertyName2)) {
                final String propertyValue2 = ra2.getContent().toString();
                final StringBuilder stringBuilder2 = new StringBuilder(nameString);
                stringBuilder2.append("Ignoring unknown property: ").append("value of \"").append(propertyValue2).append("\" for \"").append(propertyName2).append("\" property");
                infoMessages.add(stringBuilder2.toString());
            }
        }
    }
    
    public static BasicDataSource createDataSource(final Properties properties) throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();
        String value = properties.getProperty("defaultAutoCommit");
        if (value != null) {
            dataSource.setDefaultAutoCommit(Boolean.valueOf(value));
        }
        value = properties.getProperty("defaultReadOnly");
        if (value != null) {
            dataSource.setDefaultReadOnly(Boolean.valueOf(value));
        }
        value = properties.getProperty("defaultTransactionIsolation");
        if (value != null) {
            int level = -1;
            if ("NONE".equalsIgnoreCase(value)) {
                level = 0;
            }
            else if ("READ_COMMITTED".equalsIgnoreCase(value)) {
                level = 2;
            }
            else if ("READ_UNCOMMITTED".equalsIgnoreCase(value)) {
                level = 1;
            }
            else if ("REPEATABLE_READ".equalsIgnoreCase(value)) {
                level = 4;
            }
            else if ("SERIALIZABLE".equalsIgnoreCase(value)) {
                level = 8;
            }
            else {
                try {
                    level = Integer.parseInt(value);
                }
                catch (final NumberFormatException e) {
                    System.err.println("Could not parse defaultTransactionIsolation: " + value);
                    System.err.println("WARNING: defaultTransactionIsolation not set");
                    System.err.println("using default value of database driver");
                    level = -1;
                }
            }
            dataSource.setDefaultTransactionIsolation(level);
        }
        value = properties.getProperty("defaultCatalog");
        if (value != null) {
            dataSource.setDefaultCatalog(value);
        }
        value = properties.getProperty("defaultSchema");
        if (value != null) {
            dataSource.setDefaultSchema(value);
        }
        value = properties.getProperty("cacheState");
        if (value != null) {
            dataSource.setCacheState(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("driverClassName");
        if (value != null) {
            dataSource.setDriverClassName(value);
        }
        value = properties.getProperty("lifo");
        if (value != null) {
            dataSource.setLifo(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("maxTotal");
        if (value != null) {
            dataSource.setMaxTotal(Integer.parseInt(value));
        }
        value = properties.getProperty("maxIdle");
        if (value != null) {
            dataSource.setMaxIdle(Integer.parseInt(value));
        }
        value = properties.getProperty("minIdle");
        if (value != null) {
            dataSource.setMinIdle(Integer.parseInt(value));
        }
        value = properties.getProperty("initialSize");
        if (value != null) {
            dataSource.setInitialSize(Integer.parseInt(value));
        }
        value = properties.getProperty("maxWaitMillis");
        if (value != null) {
            dataSource.setMaxWaitMillis(Long.parseLong(value));
        }
        value = properties.getProperty("testOnCreate");
        if (value != null) {
            dataSource.setTestOnCreate(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("testOnBorrow");
        if (value != null) {
            dataSource.setTestOnBorrow(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("testOnReturn");
        if (value != null) {
            dataSource.setTestOnReturn(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("timeBetweenEvictionRunsMillis");
        if (value != null) {
            dataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(value));
        }
        value = properties.getProperty("numTestsPerEvictionRun");
        if (value != null) {
            dataSource.setNumTestsPerEvictionRun(Integer.parseInt(value));
        }
        value = properties.getProperty("minEvictableIdleTimeMillis");
        if (value != null) {
            dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(value));
        }
        value = properties.getProperty("softMinEvictableIdleTimeMillis");
        if (value != null) {
            dataSource.setSoftMinEvictableIdleTimeMillis(Long.parseLong(value));
        }
        value = properties.getProperty("evictionPolicyClassName");
        if (value != null) {
            dataSource.setEvictionPolicyClassName(value);
        }
        value = properties.getProperty("testWhileIdle");
        if (value != null) {
            dataSource.setTestWhileIdle(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("password");
        if (value != null) {
            dataSource.setPassword(value);
        }
        value = properties.getProperty("url");
        if (value != null) {
            dataSource.setUrl(value);
        }
        value = properties.getProperty("username");
        if (value != null) {
            dataSource.setUsername(value);
        }
        value = properties.getProperty("validationQuery");
        if (value != null) {
            dataSource.setValidationQuery(value);
        }
        value = properties.getProperty("validationQueryTimeout");
        if (value != null) {
            dataSource.setValidationQueryTimeout(Integer.parseInt(value));
        }
        value = properties.getProperty("accessToUnderlyingConnectionAllowed");
        if (value != null) {
            dataSource.setAccessToUnderlyingConnectionAllowed(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("removeAbandonedOnBorrow");
        if (value != null) {
            dataSource.setRemoveAbandonedOnBorrow(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("removeAbandonedOnMaintenance");
        if (value != null) {
            dataSource.setRemoveAbandonedOnMaintenance(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("removeAbandonedTimeout");
        if (value != null) {
            dataSource.setRemoveAbandonedTimeout(Integer.parseInt(value));
        }
        value = properties.getProperty("logAbandoned");
        if (value != null) {
            dataSource.setLogAbandoned(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("abandonedUsageTracking");
        if (value != null) {
            dataSource.setAbandonedUsageTracking(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("poolPreparedStatements");
        if (value != null) {
            dataSource.setPoolPreparedStatements(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("clearStatementPoolOnReturn");
        if (value != null) {
            dataSource.setClearStatementPoolOnReturn(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("maxOpenPreparedStatements");
        if (value != null) {
            dataSource.setMaxOpenPreparedStatements(Integer.parseInt(value));
        }
        value = properties.getProperty("connectionInitSqls");
        if (value != null) {
            dataSource.setConnectionInitSqls(parseList(value, ';'));
        }
        value = properties.getProperty("connectionProperties");
        if (value != null) {
            final Properties p = getProperties(value);
            final Enumeration<?> e2 = p.propertyNames();
            while (e2.hasMoreElements()) {
                final String propertyName = (String)e2.nextElement();
                dataSource.addConnectionProperty(propertyName, p.getProperty(propertyName));
            }
        }
        value = properties.getProperty("maxConnLifetimeMillis");
        if (value != null) {
            dataSource.setMaxConnLifetimeMillis(Long.parseLong(value));
        }
        value = properties.getProperty("logExpiredConnections");
        if (value != null) {
            dataSource.setLogExpiredConnections(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("jmxName");
        if (value != null) {
            dataSource.setJmxName(value);
        }
        value = properties.getProperty("enableAutoCommitOnReturn");
        if (value != null) {
            dataSource.setAutoCommitOnReturn(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("rollbackOnReturn");
        if (value != null) {
            dataSource.setRollbackOnReturn(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("defaultQueryTimeout");
        if (value != null) {
            dataSource.setDefaultQueryTimeout(Integer.valueOf(value));
        }
        value = properties.getProperty("fastFailValidation");
        if (value != null) {
            dataSource.setFastFailValidation(Boolean.parseBoolean(value));
        }
        value = properties.getProperty("disconnectionSqlCodes");
        if (value != null) {
            dataSource.setDisconnectionSqlCodes(parseList(value, ','));
        }
        value = properties.getProperty("connectionFactoryClassName");
        if (value != null) {
            dataSource.setConnectionFactoryClassName(value);
        }
        if (dataSource.getInitialSize() > 0) {
            dataSource.getLogWriter();
        }
        return dataSource;
    }
    
    private static Properties getProperties(final String propText) throws Exception {
        final Properties p = new Properties();
        if (propText != null) {
            p.load(new ByteArrayInputStream(propText.replace(';', '\n').getBytes(StandardCharsets.ISO_8859_1)));
        }
        return p;
    }
    
    private static Collection<String> parseList(final String value, final char delimiter) {
        final StringTokenizer tokenizer = new StringTokenizer(value, Character.toString(delimiter));
        final Collection<String> tokens = new ArrayList<String>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }
    
    static {
        log = LogFactory.getLog((Class)BasicDataSourceFactory.class);
        ALL_PROPERTIES = new String[] { "defaultAutoCommit", "defaultReadOnly", "defaultTransactionIsolation", "defaultCatalog", "defaultSchema", "cacheState", "driverClassName", "lifo", "maxTotal", "maxIdle", "minIdle", "initialSize", "maxWaitMillis", "testOnCreate", "testOnBorrow", "testOnReturn", "timeBetweenEvictionRunsMillis", "numTestsPerEvictionRun", "minEvictableIdleTimeMillis", "softMinEvictableIdleTimeMillis", "evictionPolicyClassName", "testWhileIdle", "password", "url", "username", "validationQuery", "validationQueryTimeout", "connectionInitSqls", "accessToUnderlyingConnectionAllowed", "removeAbandonedOnBorrow", "removeAbandonedOnMaintenance", "removeAbandonedTimeout", "logAbandoned", "abandonedUsageTracking", "poolPreparedStatements", "clearStatementPoolOnReturn", "maxOpenPreparedStatements", "connectionProperties", "maxConnLifetimeMillis", "logExpiredConnections", "rollbackOnReturn", "enableAutoCommitOnReturn", "defaultQueryTimeout", "fastFailValidation", "disconnectionSqlCodes", "jmxName", "connectionFactoryClassName" };
        (NUPROP_WARNTEXT = new LinkedHashMap<String, String>()).put("maxActive", "Property maxActive is not used in DBCP2, use maxTotal instead. maxTotal default value is 8.");
        BasicDataSourceFactory.NUPROP_WARNTEXT.put("removeAbandoned", "Property removeAbandoned is not used in DBCP2, use one or both of removeAbandonedOnBorrow or removeAbandonedOnMaintenance instead. Both have default value set to false.");
        BasicDataSourceFactory.NUPROP_WARNTEXT.put("maxWait", "Property maxWait is not used in DBCP2 , use maxWaitMillis instead. maxWaitMillis default value is -1.");
        (SILENT_PROPERTIES = new ArrayList<String>()).add("factory");
        BasicDataSourceFactory.SILENT_PROPERTIES.add("scope");
        BasicDataSourceFactory.SILENT_PROPERTIES.add("singleton");
        BasicDataSourceFactory.SILENT_PROPERTIES.add("auth");
    }
}
