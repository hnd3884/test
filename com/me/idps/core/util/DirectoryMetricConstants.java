package com.me.idps.core.util;

import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import java.util.ArrayList;
import java.util.List;

public class DirectoryMetricConstants
{
    private static List<String> trackingKeys;
    public static final String FILE_WRITE_NUM = "FILE_WRITE_NUM";
    public static final String FILE_WRITE_SIZE = "FILE_WRITE_SIZE";
    public static final String FILE_WRITE_TIME_TAKEN = "FILE_WRITE_TIME_TAKEN";
    public static final String FILE_READ_NUM = "FILE_READ_NUM";
    public static final String FILE_READ_SIZE = "FILE_READ_SIZE";
    public static final String FILE_READ_TIME_TAKEN = "FILE_READ_TIME_TAKEN";
    public static final String FILE_DELETE_NUM = "FILE_DELETE_NUM";
    public static final String FILE_DELETE_SIZE = "FILE_DELETE_SIZE";
    public static final String FILE_DELETE_TIME_TAKEN = "FILE_DELETE_TIME_TAKEN";
    public static final String TEMP_OPS = "tempOps";
    public static final String CORE_OPS = "coreOps";
    public static final String PROD_OPS = "prodOps";
    public static final String EVENT_OPS = "eventOps";
    public static final String TIME_TAKEN = "T";
    public static final String DELETED_ROWS = "d";
    public static final String UPDATED_ROWS = "u";
    public static final String INSERTED_ROWS = "i";
    public static final String VERSION = "VERSION";
    public static final String OP_COUNT = "OP_COUNT";
    public static final String OKTA_COUNT = "OKTA_COUNT";
    public static final String AZURE_COUNT = "AZURE_COUNT";
    public static final String HYBRID_COUNT = "HYBRID_COUNT";
    public static final String GSUITE_COUNT = "GSUITE_COUNT";
    public static final String PEOPLE_COUNT = "PEOPLE_COUNT";
    public static final String WEBHOOK_SYNC = "WEBHOOK_SYNC";
    public static final String DIR_USER_RANK = "DIR_USER_RANK";
    public static final String DIR_GROUP_RANK = "DIR_GROUP_RANK";
    public static final String ZOHO_DIR_COUNT = "ZOHO_DIR_COUNT";
    public static final String DIR_USER_SHARE = "DIR_USER_SHARE";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String TOTAL_DIR_COUNT = "TOTAL_DIR_COUNT";
    public static final String GROUP_DIR_SHARE = "GROUP_DIR_SHARE";
    public static final String DIR_ADAPTER_ERR = "DIR_ADAPTER_ERR";
    public static final String SAME_EMAIL_COUNT = "SAME_EMAIL_COUNT";
    public static final String QUEUE_PROC_ERROR = "QUEUE_PROC_ERROR";
    public static final String PK_EXCEEDED_ERROR = "PK_EXCEEDED_ERROR";
    public static final String DIR_OBJ_DEL_COUNT = "DIR_OBJ_DEL_COUNT";
    public static final String DEL_DIR_OBJ_COUNT = "DEL_DIR_OBJ_COUNT";
    public static final String ADDED_EVENT_COUNT = "ADDED_EVENT_COUNT";
    public static final String AZURE_OAUTH_ERROR = "AZURE_OAUTH_ERROR";
    public static final String SUCCESS_SYNC_COUNT = "SUCCESS_SYNC_COUNT";
    public static final String REMOVED_EVENT_COUNT = "REMOVED_EVENT_COUNT";
    public static final String MODIFIED_EVENT_COUNT = "MODIFIED_EVENT_COUNT";
    public static final String DOMAIN_UNREACHABLE_ERROR = "DOMAIN_UNREACHABLE_ERROR";
    public static final String DOMAIN_DUPLICATION_COUNT = "DOMAIN_DUPLICATION_COUNT";
    public static final String USER_DELETED_EVENT_COUNT = "USER_DELETED_EVENT_COUNT";
    public static final String SYNC_ENGINE_OPS_DURATION = "SYNC_ENGINE_OPS_DURATION";
    public static final String DELTA_TOKEN_EXPIRED_ERROR = "DELTA_TOKEN_EXPIRED_ERROR";
    public static final String GROUP_DELETED_EVENT_COUNT = "GROUP_DELETED_EVENT_COUNT";
    public static final String INPUT_USER_NAME_DUPL_ERROR = "INPUT_USER_NAME_DUPL_ERROR";
    public static final String GROUP_MODIFIED_EVENT_COUNT = "GROUP_MODIFIED_EVENT_COUNT";
    public static final String RESOURCE_DUPLICATION_COUNT = "RESOURCE_DUPLICATION_COUNT";
    public static final String USER_ACTIVATED_EVENT_COUNT = "USER_ACTIVATED_EVENT_COUNT";
    public static final String OAUTH_INVALID_CLIENT_ERROR = "OAUTH_INVALID_CLIENT_ERROR";
    public static final String DOMAIN_EMPTYUSERSLIST_ERROR = "DOMAIN_EMPTYUSERSLIST_ERROR";
    public static final String GROUP_ACTIVATED_EVENT_COUNT = "GROUP_ACTIVATED_EVENT_COUNT";
    public static final String QUERY_TIMED_OUT_ERROR_COUNT = "QUERY_TIMED_OUT_ERROR_COUNT";
    public static final String MS_SQL_DEADLOCK_ERROR_COUNT = "MS_SQL_DEADLOCK_ERROR_COUNT";
    public static final String USER_DIR_DISABLED_EVENT_COUNT = "USER_DIR_DISABLED_EVENT_COUNT";
    public static final String USER_SYNC_DISABLED_EVENT_COUNT = "USER_SYNC_DISABLED_EVENT_COUNT";
    public static final String GROUP_DIR_DISABLED_EVENT_COUNT = "GROUP_DIR_DISABLED_EVENT_COUNT";
    public static final String GROUP_SYNC_DISABLED_EVENT_COUNT = "GROUP_SYNC_DISABLED_EVENT_COUNT";
    
    public static List<String> getTrackingKeys() {
        if (DirectoryMetricConstants.trackingKeys == null) {
            List<String> prodTrackingKeys = new ArrayList<String>();
            try {
                final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                dirProdImplRequest.eventType = IdpEventConstants.GET_PROD_SPECIFIC_ME_TRACKING_KEYS;
                prodTrackingKeys = (List)DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
                if (prodTrackingKeys == null) {
                    IDPSlogger.ERR.log(Level.WARNING, "prodTrackingKeys are null!");
                }
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
            final List<String> dirTrackingKeys = new ArrayList<String>(Arrays.asList("OP_COUNT", "OKTA_COUNT", "PEOPLE_COUNT", "AZURE_COUNT", "HYBRID_COUNT", "GSUITE_COUNT", "DIR_USER_RANK", "DIR_GROUP_RANK", "ZOHO_DIR_COUNT", "TOTAL_DIR_COUNT", "SAME_EMAIL_COUNT", "DIR_USER_SHARE", "INTERNAL_ERROR", "DIR_OBJ_DEL_COUNT", "DEL_DIR_OBJ_COUNT", "ADDED_EVENT_COUNT", "OAUTH_INVALID_CLIENT_ERROR", "REMOVED_EVENT_COUNT", "MODIFIED_EVENT_COUNT", "GROUP_MODIFIED_EVENT_COUNT", "USER_DIR_DISABLED_EVENT_COUNT", "AZURE_OAUTH_ERROR", "USER_SYNC_DISABLED_EVENT_COUNT", "USER_DELETED_EVENT_COUNT", "GROUP_DELETED_EVENT_COUNT", "GROUP_DIR_DISABLED_EVENT_COUNT", "GROUP_SYNC_DISABLED_EVENT_COUNT", "DOMAIN_UNREACHABLE_ERROR", "RESOURCE_DUPLICATION_COUNT", "DOMAIN_DUPLICATION_COUNT", "GROUP_DIR_SHARE", "USER_ACTIVATED_EVENT_COUNT", "GROUP_ACTIVATED_EVENT_COUNT", "WEBHOOK_SYNC", "DIR_ADAPTER_ERR", "SUCCESS_SYNC_COUNT", "QUEUE_PROC_ERROR", "PK_EXCEEDED_ERROR", "VERSION", "SYNC_ENGINE_OPS_DURATION", "QUERY_TIMED_OUT_ERROR_COUNT", "MS_SQL_DEADLOCK_ERROR_COUNT", "DELTA_TOKEN_EXPIRED_ERROR", "DOMAIN_EMPTYUSERSLIST_ERROR", "INPUT_USER_NAME_DUPL_ERROR"));
            if (prodTrackingKeys != null) {
                dirTrackingKeys.addAll(prodTrackingKeys);
            }
            DirectoryMetricConstants.trackingKeys = dirTrackingKeys;
        }
        return DirectoryMetricConstants.trackingKeys;
    }
    
    static {
        DirectoryMetricConstants.trackingKeys = null;
    }
}
