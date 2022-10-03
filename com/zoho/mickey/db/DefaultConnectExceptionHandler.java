package com.zoho.mickey.db;

import java.sql.SQLException;
import com.zoho.mickey.server.DefaultServerInfoDump;
import com.zoho.mickey.server.ServerInfoDump;
import java.util.logging.Logger;
import com.zoho.mickey.cp.BaseSQLExceptionHandler;

public class DefaultConnectExceptionHandler extends BaseSQLExceptionHandler
{
    protected static final Logger LOGGER;
    protected ServerInfoDump infoDump;
    
    public DefaultConnectExceptionHandler() {
        this.infoDump = (ServerInfoDump)new DefaultServerInfoDump();
    }
    
    public void onConnectException(final SQLException sqlException) {
        final String message = sqlException.getMessage();
        if (message != null && message.contains("No ManagedConnections")) {
            this.operationForNoManagedConnection();
        }
    }
    
    protected synchronized void operationForNoManagedConnection() {
        this.dumpInfo();
    }
    
    protected void dumpInfo() {
        DefaultConnectExceptionHandler.LOGGER.info("Dumping info");
        this.infoDump.dump(new String[0]);
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultConnectExceptionHandler.class.getName());
    }
}
