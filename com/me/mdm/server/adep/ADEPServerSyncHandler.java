package com.me.mdm.server.adep;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.ds.query.SortColumn;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

public class ADEPServerSyncHandler extends AppleDEPHandler
{
    static HashMap<Integer, String> errCodeI18KeyMap;
    public static final int DEP_SCAN_SUCCESS = 1;
    public static final int DEP_SCAN_FAILED = 2;
    public static final int DEP_SCAN_INITIATED = 3;
    public static final int TERMS_CONDITIONS_NOT_SIGNED = 1011;
    public static final int TOKEN_REJECTED = 1012;
    public static final int OTHER_ERROR = 1013;
    public static final int TOKEN_EXPIRED = 1014;
    public static final int OAUTH_ERROR = 1015;
    public static final int FORBIDDEN = 1016;
    public static final int MESSAGE_FORMAT_ERROR = 1017;
    public static final int INTERNAL_SERVER_ERROR = 1018;
    public static final int SERVER_TIME_MISMATCH = 1019;
    public static Logger logger;
    
    public static ADEPServerSyncHandler getInstance(final Long tokenId) {
        return new ADEPServerSyncHandler(tokenId);
    }
    
    public static ADEPServerSyncHandler getInstance(final Long tokenId, final Long custoemrID) {
        return new ADEPServerSyncHandler(tokenId, custoemrID);
    }
    
    private ADEPServerSyncHandler(final Long tokenID) {
        super(tokenID);
    }
    
    private ADEPServerSyncHandler(final Long tokenId, final Long customerID) {
        super(tokenId, customerID);
    }
    
    public void addOrUpdateServerSyncDetails(final int status, final int error, final String remarks) {
        ADEPServerSyncHandler.logger.log(Level.INFO, "Inside addOrUpdateServerSyncDetails");
        final Long tokenID = this.tokenId;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppleDEPServerSyncStatus"));
        sQuery.addJoin(new Join("AppleDEPServerSyncStatus", "DEPTokenDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn("AppleDEPServerSyncStatus", "*"));
        final Criteria criteria = new Criteria(new Column("AppleDEPServerSyncStatus", "DEP_TOKEN_ID"), (Object)tokenID, 0, false);
        sQuery.setCriteria(criteria);
        try {
            DataObject DObj = MDMUtil.getPersistence().get(sQuery);
            if (DObj.isEmpty()) {
                DObj = (DataObject)new WritableDataObject();
                final Row row = new Row("AppleDEPServerSyncStatus");
                row.set("LAST_SYNC_TIME", (Object)MDMUtil.getCurrentTime());
                row.set("DEP_TOKEN_ID", (Object)tokenID);
                row.set("DEP_SYNC_STATUS", (Object)status);
                row.set("ERROR_CODE", (Object)error);
                row.set("ERROR_REMARKS", (Object)remarks);
                DObj.addRow(row);
                MDMUtil.getPersistence().add(DObj);
            }
            else {
                final Row row = DObj.getFirstRow("AppleDEPServerSyncStatus");
                row.set("DEP_SYNC_STATUS", (Object)status);
                row.set("ERROR_CODE", (Object)error);
                row.set("ERROR_REMARKS", (Object)remarks);
                if (status == 3) {
                    row.set("LAST_SYNC_TIME", (Object)MDMUtil.getCurrentTime());
                }
                else if (status == 1) {
                    row.set("LAST_SUCCESSFUL_SYNC_TIME", (Object)row.get("LAST_SYNC_TIME"));
                }
                DObj.updateRow(row);
                MDMUtil.getPersistence().update(DObj);
            }
        }
        catch (final Exception ex) {
            ADEPServerSyncHandler.logger.log(Level.WARNING, "Exception occured while updating DEP server details - ", ex);
        }
    }
    
    public JSONObject getDEPServerSyncDetails() {
        final JSONObject depSyncDetails = new JSONObject();
        String successSyncTime = "--";
        String lastSyncTime = "--";
        Long tokenid = null;
        int serverStatus = 1;
        boolean errorFlag = false;
        String remarks = null;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppleDEPServerSyncStatus"));
        sQuery.addSelectColumn(Column.getColumn("AppleDEPServerSyncStatus", "*"));
        sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
        sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "ACCESS_TOKEN_EXPIRY_DATE"));
        final SortColumn sortColumn = new SortColumn("AppleDEPServerSyncStatus", "ERROR_CODE", false);
        sQuery.addSortColumn(sortColumn);
        sQuery.addJoin(new Join("AppleDEPServerSyncStatus", "DEPTokenDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "*"));
        Criteria criteria;
        final Criteria criteriaCustomer = criteria = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)this.customerId, 0);
        if (this.tokenId != null) {
            criteria = criteria.and(new Criteria(new Column("AppleDEPServerSyncStatus", "DEP_TOKEN_ID"), (Object)this.tokenId, 0));
        }
        sQuery.setCriteria(criteria);
        try {
            final DataObject DObj = MDMUtil.getPersistence().get(sQuery);
            if (!DObj.isEmpty()) {
                final Long expiryTime = (Long)DObj.getFirstValue("DEPTokenDetails", "ACCESS_TOKEN_EXPIRY_DATE");
                final Long syncTimeSecs = (Long)DObj.getFirstValue("AppleDEPServerSyncStatus", "LAST_SUCCESSFUL_SYNC_TIME");
                successSyncTime = Utils.getEventTime(syncTimeSecs);
                final Long lastSyncTimeSecs = (Long)DObj.getFirstValue("AppleDEPServerSyncStatus", "LAST_SYNC_TIME");
                lastSyncTime = Utils.getEventTime(lastSyncTimeSecs);
                serverStatus = (int)DObj.getFirstValue("AppleDEPServerSyncStatus", "DEP_SYNC_STATUS");
                final int error = (int)DObj.getFirstValue("AppleDEPServerSyncStatus", "ERROR_CODE");
                tokenid = (Long)DObj.getFirstValue("AppleDEPServerSyncStatus", "DEP_TOKEN_ID");
                if (error != -1) {
                    errorFlag = true;
                    remarks = (String)DObj.getFirstValue("AppleDEPServerSyncStatus", "ERROR_REMARKS");
                }
                depSyncDetails.put("depServerName", (Object)DEPEnrollmentUtil.getDEPServerName(tokenid));
                depSyncDetails.put("lastSyncTimeString", (Object)lastSyncTime);
                depSyncDetails.put("lastSyncTime", (Object)lastSyncTimeSecs);
                depSyncDetails.put("successSyncTimeString", (Object)successSyncTime);
                depSyncDetails.put("successSyncTime", (Object)syncTimeSecs);
                depSyncDetails.put("serverStatus", serverStatus);
                depSyncDetails.put("error", (Object)remarks);
                depSyncDetails.put("tokenID", (Object)tokenid);
                depSyncDetails.put("error_flag", errorFlag);
                depSyncDetails.put("error_code", error);
                if (expiryTime <= MDMUtil.getCurrentTimeInMillis()) {
                    depSyncDetails.put("expired", true);
                }
                else {
                    depSyncDetails.put("expired", false);
                }
            }
        }
        catch (final Exception ex) {
            ADEPServerSyncHandler.logger.log(Level.SEVERE, "Exception in getting DEP server Sync Time", ex);
        }
        return depSyncDetails;
    }
    
    public void handleDEPServerHandshakeExceptions(final SyMException e) throws Exception {
        ADEPServerSyncHandler.logger.log(Level.SEVERE, "--------- Exception in ABM ---------", (Throwable)e);
        final String errorMsg = e.getMessage().toLowerCase();
        int internalErrorCode = 1013;
        String remarks = "OtherError";
        final DepErrorsAPI depErrorsAPI = MDMApiFactoryProvider.getDepErrorsAPI();
        if (errorMsg.contains("oauth_problem_advice")) {
            internalErrorCode = 1015;
            if (depErrorsAPI != null) {
                internalErrorCode = depErrorsAPI.getErrorCause(1015);
            }
            remarks = ((internalErrorCode == 1019) ? "ServerTimeMismatch" : "OauthError");
        }
        else if (errorMsg.contains("t_c_not_signed")) {
            internalErrorCode = 1011;
            remarks = "TCError";
        }
        else if (errorMsg.contains("token_rejected")) {
            internalErrorCode = 1012;
            remarks = "TokenRejected";
        }
        else if (errorMsg.contains("token_expired")) {
            internalErrorCode = 1014;
            remarks = "TokenRejected";
        }
        else if (errorMsg.contains("message_format_error")) {
            internalErrorCode = 1017;
            remarks = "FormatError";
        }
        else if (errorMsg.contains("internal_server_error")) {
            internalErrorCode = 1018;
            remarks = "ServiceDown";
        }
        else if (errorMsg.contains("forbidden")) {
            internalErrorCode = 1016;
            remarks = "Forbidden";
        }
        else {
            final int symErrorCode = e.getErrorCode();
            if (symErrorCode == 401) {
                internalErrorCode = 1015;
                remarks = "OauthError";
            }
            else if (symErrorCode == 500 || symErrorCode == 503) {
                internalErrorCode = 1018;
                remarks = "ServiceDown";
            }
            else {
                internalErrorCode = 1013;
                remarks = "OtherError";
            }
        }
        ADEPServerSyncHandler.logger.log(Level.SEVERE, "ABM error code is : {0} and remark is : {1}", new Object[] { internalErrorCode, remarks });
        this.addOrUpdateServerSyncDetails(2, internalErrorCode, remarks);
    }
    
    private static void initDEPErrorMessageMap() {
        (ADEPServerSyncHandler.errCodeI18KeyMap = new HashMap<Integer, String>()).put(1011, "ABM011");
        ADEPServerSyncHandler.errCodeI18KeyMap.put(1012, "ABM024");
        ADEPServerSyncHandler.errCodeI18KeyMap.put(1013, "ABM013");
        ADEPServerSyncHandler.errCodeI18KeyMap.put(1014, "ABM014");
        ADEPServerSyncHandler.errCodeI18KeyMap.put(1018, "ABM018");
        ADEPServerSyncHandler.errCodeI18KeyMap.put(1015, "ABM015");
        ADEPServerSyncHandler.errCodeI18KeyMap.put(1016, "ABM016");
        ADEPServerSyncHandler.errCodeI18KeyMap.put(1017, "ABM013");
        ADEPServerSyncHandler.errCodeI18KeyMap.put(1019, "ABM023");
    }
    
    private JSONObject getErrorDetails(final int errorCode) throws Exception {
        if (ADEPServerSyncHandler.errCodeI18KeyMap == null) {
            initDEPErrorMessageMap();
        }
        JSONObject errorJson;
        try {
            JSONObject errorRemarkJson = null;
            if (errorCode == 1019) {
                errorRemarkJson = MDMApiFactoryProvider.getDepErrorsAPI().getErrorRemarkArgs(errorCode);
            }
            else if (errorCode == 1014) {
                errorRemarkJson = this.getTokenExpiryRemarkArgs();
            }
            errorJson = new APIHTTPException(ADEPServerSyncHandler.errCodeI18KeyMap.get(errorCode), new Object[0]).toJSONObject();
            if (errorRemarkJson != null) {
                JSONUtil.putAll(errorJson, errorRemarkJson);
            }
        }
        catch (final Exception e) {
            ADEPServerSyncHandler.logger.log(Level.SEVERE, "Exception while getting error details", e);
            errorJson = new APIHTTPException(ADEPServerSyncHandler.errCodeI18KeyMap.get(1013), new Object[0]).toJSONObject();
        }
        return errorJson;
    }
    
    public JSONObject getErrorDetails() throws Exception {
        final int errorCode = (int)DBUtil.getValueFromDB("AppleDEPServerSyncStatus", "DEP_TOKEN_ID", (Object)this.tokenId, "ERROR_CODE");
        if (errorCode == -1) {
            return null;
        }
        return this.getErrorDetails(errorCode);
    }
    
    public JSONObject getTokenExpiryRemarkArgs() throws Exception {
        JSONObject tokenExpiryRemarkArgs = null;
        final Long accessTokenExpiryDateMs = (Long)DBUtil.getValueFromDB("DEPTokenDetails", "DEP_TOKEN_ID", (Object)this.tokenId, "ACCESS_TOKEN_EXPIRY_DATE");
        if (accessTokenExpiryDateMs != null) {
            tokenExpiryRemarkArgs = new JSONObject();
            tokenExpiryRemarkArgs.put("EXPIRED_TIME", (Object)accessTokenExpiryDateMs);
        }
        return tokenExpiryRemarkArgs;
    }
    
    static {
        ADEPServerSyncHandler.errCodeI18KeyMap = null;
        ADEPServerSyncHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
