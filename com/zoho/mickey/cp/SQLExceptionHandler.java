package com.zoho.mickey.cp;

import java.sql.SQLException;

public interface SQLExceptionHandler
{
    void handleSQLException(final SQLException p0);
    
    void onConnectException(final SQLException p0);
}
