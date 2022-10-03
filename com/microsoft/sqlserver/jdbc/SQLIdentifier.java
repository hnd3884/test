package com.microsoft.sqlserver.jdbc;

final class SQLIdentifier
{
    private String serverName;
    private String databaseName;
    private String schemaName;
    private String objectName;
    
    SQLIdentifier() {
        this.serverName = "";
        this.databaseName = "";
        this.schemaName = "";
        this.objectName = "";
    }
    
    final String getServerName() {
        return this.serverName;
    }
    
    final void setServerName(final String name) {
        this.serverName = name;
    }
    
    final String getDatabaseName() {
        return this.databaseName;
    }
    
    final void setDatabaseName(final String name) {
        this.databaseName = name;
    }
    
    final String getSchemaName() {
        return this.schemaName;
    }
    
    final void setSchemaName(final String name) {
        this.schemaName = name;
    }
    
    final String getObjectName() {
        return this.objectName;
    }
    
    final void setObjectName(final String name) {
        this.objectName = name;
    }
    
    final String asEscapedString() {
        final StringBuilder fullName = new StringBuilder(256);
        if (this.serverName.length() > 0) {
            fullName.append("[").append(this.serverName).append("].");
        }
        if (this.databaseName.length() > 0) {
            fullName.append("[").append(this.databaseName).append("].");
        }
        else {
            assert 0 == this.serverName.length();
        }
        if (this.schemaName.length() > 0) {
            fullName.append("[").append(this.schemaName).append("].");
        }
        else if (this.databaseName.length() > 0) {
            fullName.append('.');
        }
        fullName.append("[").append(this.objectName).append("]");
        return fullName.toString();
    }
}
