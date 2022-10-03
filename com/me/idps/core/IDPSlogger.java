package com.me.idps.core;

import java.util.logging.Logger;

public class IDPSlogger
{
    public static final String SOM_LOG_NAME = "SoMLogger";
    public static final String OAUTH_LOG_NAME = "OauthLogger";
    public static final String SYNC_LOG_NAME = "MDMADSyncLogger";
    public static final String DBO_LOG_NAME = "DirectoryDbOpsLogger";
    public static final String UPGRADE_LOG_NAME = "IDPSupgradeLogger";
    public static final String ERROR_LOG_NAME = "DirectoryErrorLogger";
    public static final String AUDIT_LOG_NAME = "DirectoryAuditLogger";
    public static final String QUEUE_LOG_NAME = "DirectoryQueueLogger";
    public static final String EVENT_LOG_NAME = "DirectoryEventLogger";
    public static final String IDPS_TXN_LOG_NAME = "TransactionLogger";
    public static final String ASYNCH_LOG_NAME = "DirectoryAsyncLogger";
    public static final Logger SOM;
    public static final Logger DBO;
    public static final Logger SYNC;
    public static final Logger ERR;
    public static final Logger OAUTH;
    public static final Logger QUEUE;
    public static final Logger AUDIT;
    public static final Logger EVENT;
    public static final Logger TXN;
    public static final Logger ASYNCH;
    public static final Logger UPGRADE;
    
    static {
        SOM = Logger.getLogger("SoMLogger");
        DBO = Logger.getLogger("DirectoryDbOpsLogger");
        SYNC = Logger.getLogger("MDMADSyncLogger");
        ERR = Logger.getLogger("DirectoryErrorLogger");
        OAUTH = Logger.getLogger("OauthLogger");
        QUEUE = Logger.getLogger("DirectoryQueueLogger");
        AUDIT = Logger.getLogger("DirectoryAuditLogger");
        EVENT = Logger.getLogger("DirectoryEventLogger");
        TXN = Logger.getLogger("TransactionLogger");
        ASYNCH = Logger.getLogger("DirectoryAsyncLogger");
        UPGRADE = Logger.getLogger("IDPSupgradeLogger");
    }
}
