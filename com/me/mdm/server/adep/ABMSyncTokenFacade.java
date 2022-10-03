package com.me.mdm.server.adep;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.ArrayList;
import org.bouncycastle.cms.CMSException;
import org.json.JSONException;
import java.io.IOException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONArray;
import java.util.logging.Logger;

public class ABMSyncTokenFacade
{
    private static ABMSyncTokenFacade facadeObj;
    public static Logger logger;
    
    public static ABMSyncTokenFacade getInstance() {
        if (ABMSyncTokenFacade.facadeObj == null) {
            ABMSyncTokenFacade.facadeObj = new ABMSyncTokenFacade();
        }
        return ABMSyncTokenFacade.facadeObj;
    }
    
    public JSONArray syncAllTokensForCustomer(final Long customerId) throws Exception {
        try {
            ABMSyncTokenFacade.logger.log(Level.INFO, "Going to Sync All ABM/ASM Tokens for the customer:", customerId);
            DEPEnrollmentUtil.syncDEPTokensForCustomer(customerId);
            return this.getSyncAllDetails(customerId);
        }
        catch (final IOException e) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception while sync ABM or ASM token.. IO exp..", e);
            throw new APIHTTPException("ABM002", new Object[0]);
        }
        catch (final JSONException e2) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception while sync ABM or ASM token.. Json exp..", (Throwable)e2);
            throw new APIHTTPException("ABM004", new Object[0]);
        }
        catch (final CMSException e3) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception while sync ABM or ASM token.. CMS exp..", (Throwable)e3);
            throw new APIHTTPException("ABM003", new Object[0]);
        }
        catch (final APIHTTPException e4) {
            throw e4;
        }
        catch (final Exception e5) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray getSyncAllDetails(final Long customerId) throws Exception {
        try {
            ArrayList<Long> abmTokenIds = new ArrayList<Long>();
            final JSONArray getTokenDetails = new JSONArray();
            abmTokenIds = DEPEnrollmentUtil.getAllDepTokenIds(customerId);
            for (final Long eachTokenId : abmTokenIds) {
                getTokenDetails.put((Object)this.getSyncDetails(customerId, eachTokenId));
            }
            return getTokenDetails;
        }
        catch (final Exception e) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception in Get Sync All ABM Details", e);
            throw e;
        }
    }
    
    public JSONObject syncToken(final JSONObject apiJsonObj) throws Exception {
        try {
            final Long customerID = APIUtil.getCustomerID(apiJsonObj);
            final Long tokenId = Long.valueOf(String.valueOf(apiJsonObj.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
            ABMAuthTokenFacade.validateIfDepTokenExists(tokenId, customerID);
            ABMSyncTokenFacade.logger.log(Level.INFO, "Going to Sync ABM/ASM Token: {0}", new Object[] { tokenId });
            DEPEnrollmentUtil.syncParticularDEPToken(tokenId);
            return this.getSyncDetails(apiJsonObj);
        }
        catch (final IOException e) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception while sync ABM or ASM token.. IO exp..", e);
            throw new APIHTTPException("ABM002", new Object[0]);
        }
        catch (final JSONException e2) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception while sync ABM or ASM token.. Json exp..", (Throwable)e2);
            throw new APIHTTPException("ABM004", new Object[0]);
        }
        catch (final CMSException e3) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception while sync ABM or ASM token.. CMS exp..", (Throwable)e3);
            throw new APIHTTPException("ABM003", new Object[0]);
        }
        catch (final APIHTTPException e4) {
            throw e4;
        }
        catch (final Exception e5) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getSyncDetails(final JSONObject apiJsonObj) throws Exception {
        try {
            final Long customerID = APIUtil.getCustomerID(apiJsonObj);
            final Long tokenId = Long.valueOf(String.valueOf(apiJsonObj.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
            ABMAuthTokenFacade.validateIfDepTokenExists(tokenId, customerID);
            return this.getSyncDetails(customerID, tokenId);
        }
        catch (final Exception e) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception in Get Sync Details", e);
            throw e;
        }
    }
    
    public JSONObject getTokenAccountDetails(final JSONObject apiJsonObj) throws Exception {
        final Long customerID = APIUtil.getCustomerID(apiJsonObj);
        final Long tokenID = Long.valueOf(String.valueOf(apiJsonObj.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
        ABMAuthTokenFacade.validateIfDepTokenExists(tokenID, customerID);
        return this.getAccountDetails(customerID, tokenID).getJSONObject(0);
    }
    
    public JSONArray getAllTokenAccountDetails(final Long customerID) throws Exception {
        return this.getAccountDetails(customerID, null);
    }
    
    private JSONArray getAccountDetails(final Long customerID, final Long tokenID) throws Exception {
        try {
            final JSONArray accountDetailsJA = new JSONArray();
            if (tokenID != null) {
                accountDetailsJA.put((Object)this.getABMAccountDetails(customerID, tokenID));
                return accountDetailsJA;
            }
            final ArrayList<Long> abmTokenIds = DEPEnrollmentUtil.getAllDepTokenIds(customerID);
            for (final Long eachTokenId : abmTokenIds) {
                accountDetailsJA.put((Object)this.getABMAccountDetails(customerID, eachTokenId));
            }
            return accountDetailsJA;
        }
        catch (final Exception ex) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception in get ABM Account Details: ", ex);
            throw ex;
        }
    }
    
    private JSONObject getSyncDetails(final Long custId, final Long tokenID) throws Exception {
        JSONObject temp = new JSONObject();
        final DEPAdminEnrollmentHandler handler = new DEPAdminEnrollmentHandler();
        int unAssignedCount = 0;
        int enrolledDEPDeviceCount = 0;
        int devicesWithoutDEPCount = 0;
        int waitingForUserAssignCount = 0;
        boolean isNewTechnicianAssignmentNeededForAbmServer = false;
        JSONObject depSyncDetails = new JSONObject();
        try {
            unAssignedCount = handler.getUnenrolledDeviceCount(custId, tokenID);
            enrolledDEPDeviceCount = handler.getAdminEnrolledDeviceCount(custId, tokenID);
            devicesWithoutDEPCount = handler.getDevicesWithoutDepCount(custId, tokenID);
            waitingForUserAssignCount = handler.getDevicesEnrolledAndNotAssignedUserCount(custId, tokenID);
            depSyncDetails = ADEPServerSyncHandler.getInstance(tokenID, custId).getDEPServerSyncDetails();
            isNewTechnicianAssignmentNeededForAbmServer = handler.isNewTechnicianAssignmentNeededForAbmServer(tokenID);
        }
        catch (final Exception ex) {
            ABMSyncTokenFacade.logger.log(Level.SEVERE, "Exception in gettingDepDeviceDetails", ex);
        }
        final JSONObject syncDetails = new JSONObject();
        temp = new JSONObject();
        temp.put("managed_DEP", enrolledDEPDeviceCount);
        temp.put("staged_DEP", unAssignedCount);
        temp.put("without_DEP", devicesWithoutDEPCount);
        temp.put("waiting_user_assign_DEP", waitingForUserAssignCount);
        syncDetails.put("device_count", (Object)temp);
        syncDetails.put("success_Sync_Time", (Object)depSyncDetails.optString("successSyncTime"));
        syncDetails.put("success_Sync_Time_string", (Object)depSyncDetails.optString("successSyncTimeString"));
        syncDetails.put("last_Sync_Time", (Object)depSyncDetails.optString("lastSyncTime"));
        syncDetails.put("last_Sync_Time_string", (Object)depSyncDetails.optString("lastSyncTimeString"));
        if (depSyncDetails.getBoolean("expired")) {
            syncDetails.put("Status", (Object)"Failed");
            final JSONObject errorJson = new APIHTTPException("ABM014", new Object[0]).toJSONObject();
            final JSONObject errorRemarks = ADEPServerSyncHandler.getInstance(tokenID).getTokenExpiryRemarkArgs();
            if (errorRemarks != null) {
                JSONUtil.putAll(errorJson, errorRemarks);
            }
            syncDetails.put("error", (Object)errorJson);
        }
        else {
            final int syncStatus = depSyncDetails.optInt("serverStatus");
            if (syncStatus == 1) {
                syncDetails.put("Status", (Object)"Success");
            }
            else if (syncStatus == 3) {
                syncDetails.put("Status", (Object)"In Progress");
            }
            else if (syncStatus == 2) {
                syncDetails.put("Status", (Object)"Failed");
                final JSONObject errorDetails = ADEPServerSyncHandler.getInstance(tokenID, custId).getErrorDetails();
                if (errorDetails != null) {
                    syncDetails.put("error", (Object)errorDetails);
                }
            }
        }
        syncDetails.put("server_name", (Object)DEPEnrollmentUtil.getDEPServerName(tokenID));
        syncDetails.put("server_id", (Object)tokenID);
        syncDetails.put("is_new_technician_assignment_needed", isNewTechnicianAssignmentNeededForAbmServer);
        return syncDetails;
    }
    
    private JSONObject getABMAccountDetails(final Long custId, final Long tokenID) throws Exception {
        JSONObject temp = new JSONObject();
        JSONObject accountDetails = new JSONObject();
        accountDetails = AppleDEPAccountDetailsHandler.getInstance().getAccountJSON(custId, tokenID);
        final JSONObject formattedAccDetails = new JSONObject();
        temp = new JSONObject();
        formattedAccDetails.put("server_name", accountDetails.get("SERVER_NAME"));
        formattedAccDetails.put("admin_email", accountDetails.get("ADMIN_EMAIL_ID"));
        formattedAccDetails.put("server_expiry", accountDetails.get("ACCESS_TOKEN_EXPIRY_DATE"));
        formattedAccDetails.put("server_udid", accountDetails.get("SERVER_UDID"));
        temp.put("id", accountDetails.get("ORG_ID"));
        temp.put("email", accountDetails.get("ORG_EMAIL"));
        temp.put("name", accountDetails.get("ORG_NAME"));
        temp.put("hash", accountDetails.get("ORG_ID_HASH"));
        temp.put("phone", accountDetails.get("ORG_PHONE"));
        temp.put("type", accountDetails.get("ORG_TYPE"));
        temp.put("version", accountDetails.get("ORG_VERSION"));
        temp.put("address", accountDetails.get("ORG_ADDRESS"));
        formattedAccDetails.put("org_details", (Object)temp);
        formattedAccDetails.put("server_id", (Object)tokenID);
        return formattedAccDetails;
    }
    
    static {
        ABMSyncTokenFacade.facadeObj = null;
        ABMSyncTokenFacade.logger = Logger.getLogger("MDMEnrollment");
    }
}
