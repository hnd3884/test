package org.apache.tomcat.jdbc.pool;

import java.util.Properties;

public interface PoolConfiguration
{
    public static final String PKG_PREFIX = "org.apache.tomcat.jdbc.pool.interceptor.";
    
    void setAbandonWhenPercentageFull(final int p0);
    
    int getAbandonWhenPercentageFull();
    
    boolean isFairQueue();
    
    void setFairQueue(final boolean p0);
    
    boolean isAccessToUnderlyingConnectionAllowed();
    
    void setAccessToUnderlyingConnectionAllowed(final boolean p0);
    
    String getConnectionProperties();
    
    void setConnectionProperties(final String p0);
    
    Properties getDbProperties();
    
    void setDbProperties(final Properties p0);
    
    Boolean isDefaultAutoCommit();
    
    Boolean getDefaultAutoCommit();
    
    void setDefaultAutoCommit(final Boolean p0);
    
    String getDefaultCatalog();
    
    void setDefaultCatalog(final String p0);
    
    Boolean isDefaultReadOnly();
    
    Boolean getDefaultReadOnly();
    
    void setDefaultReadOnly(final Boolean p0);
    
    int getDefaultTransactionIsolation();
    
    void setDefaultTransactionIsolation(final int p0);
    
    String getDriverClassName();
    
    void setDriverClassName(final String p0);
    
    int getInitialSize();
    
    void setInitialSize(final int p0);
    
    boolean isLogAbandoned();
    
    void setLogAbandoned(final boolean p0);
    
    int getMaxActive();
    
    void setMaxActive(final int p0);
    
    int getMaxIdle();
    
    void setMaxIdle(final int p0);
    
    int getMaxWait();
    
    void setMaxWait(final int p0);
    
    int getMinEvictableIdleTimeMillis();
    
    void setMinEvictableIdleTimeMillis(final int p0);
    
    int getMinIdle();
    
    void setMinIdle(final int p0);
    
    String getName();
    
    void setName(final String p0);
    
    int getNumTestsPerEvictionRun();
    
    void setNumTestsPerEvictionRun(final int p0);
    
    String getPassword();
    
    void setPassword(final String p0);
    
    String getPoolName();
    
    String getUsername();
    
    void setUsername(final String p0);
    
    boolean isRemoveAbandoned();
    
    void setRemoveAbandoned(final boolean p0);
    
    void setRemoveAbandonedTimeout(final int p0);
    
    int getRemoveAbandonedTimeout();
    
    boolean isTestOnBorrow();
    
    void setTestOnBorrow(final boolean p0);
    
    boolean isTestOnReturn();
    
    void setTestOnReturn(final boolean p0);
    
    boolean isTestWhileIdle();
    
    void setTestWhileIdle(final boolean p0);
    
    int getTimeBetweenEvictionRunsMillis();
    
    void setTimeBetweenEvictionRunsMillis(final int p0);
    
    String getUrl();
    
    void setUrl(final String p0);
    
    String getValidationQuery();
    
    void setValidationQuery(final String p0);
    
    int getValidationQueryTimeout();
    
    void setValidationQueryTimeout(final int p0);
    
    String getValidatorClassName();
    
    void setValidatorClassName(final String p0);
    
    Validator getValidator();
    
    void setValidator(final Validator p0);
    
    long getValidationInterval();
    
    void setValidationInterval(final long p0);
    
    String getInitSQL();
    
    void setInitSQL(final String p0);
    
    boolean isTestOnConnect();
    
    void setTestOnConnect(final boolean p0);
    
    String getJdbcInterceptors();
    
    void setJdbcInterceptors(final String p0);
    
    PoolProperties.InterceptorDefinition[] getJdbcInterceptorsAsArray();
    
    boolean isJmxEnabled();
    
    void setJmxEnabled(final boolean p0);
    
    boolean isPoolSweeperEnabled();
    
    boolean isUseEquals();
    
    void setUseEquals(final boolean p0);
    
    long getMaxAge();
    
    void setMaxAge(final long p0);
    
    boolean getUseLock();
    
    void setUseLock(final boolean p0);
    
    void setSuspectTimeout(final int p0);
    
    int getSuspectTimeout();
    
    void setDataSource(final Object p0);
    
    Object getDataSource();
    
    void setDataSourceJNDI(final String p0);
    
    String getDataSourceJNDI();
    
    boolean isAlternateUsernameAllowed();
    
    void setAlternateUsernameAllowed(final boolean p0);
    
    void setCommitOnReturn(final boolean p0);
    
    boolean getCommitOnReturn();
    
    void setRollbackOnReturn(final boolean p0);
    
    boolean getRollbackOnReturn();
    
    void setUseDisposableConnectionFacade(final boolean p0);
    
    boolean getUseDisposableConnectionFacade();
    
    void setLogValidationErrors(final boolean p0);
    
    boolean getLogValidationErrors();
    
    boolean getPropagateInterruptState();
    
    void setPropagateInterruptState(final boolean p0);
    
    void setIgnoreExceptionOnPreLoad(final boolean p0);
    
    boolean isIgnoreExceptionOnPreLoad();
    
    void setUseStatementFacade(final boolean p0);
    
    boolean getUseStatementFacade();
}
