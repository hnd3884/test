package com.microsoft.sqlserver.jdbc;

enum SQLServerDriverIntProperty
{
    PACKET_SIZE("packetSize", 8000), 
    LOCK_TIMEOUT("lockTimeout", -1), 
    LOGIN_TIMEOUT("loginTimeout", 15), 
    QUERY_TIMEOUT("queryTimeout", -1), 
    PORT_NUMBER("portNumber", 1433), 
    SOCKET_TIMEOUT("socketTimeout", 0), 
    SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD("serverPreparedStatementDiscardThreshold", 10), 
    STATEMENT_POOLING_CACHE_SIZE("statementPoolingCacheSize", 0), 
    CANCEL_QUERY_TIMEOUT("cancelQueryTimeout", -1);
    
    private final String name;
    private final int defaultValue;
    
    private SQLServerDriverIntProperty(final String name, final int defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }
    
    int getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
