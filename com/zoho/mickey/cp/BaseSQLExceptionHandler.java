package com.zoho.mickey.cp;

import java.sql.SQLException;

public class BaseSQLExceptionHandler implements SQLExceptionHandler
{
    @Override
    public void handleSQLException(final SQLException sqlException) {
    }
    
    @Override
    public void onConnectException(final SQLException sqlException) {
        final String message = sqlException.getMessage();
        if (message != null && message.contains("No ManagedConnections")) {
            ConnectionInfoFactory.dumpInUseConnections();
        }
    }
}
