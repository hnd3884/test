package com.me.mdm.server.updates.osupdates;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.api.paging.PagingUtil;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.me.mdm.server.customgroup.GroupFacade;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.me.mdm.http.HttpException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.profiles.ProfileFacade;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class OSUpdateProfileFacade
{
    protected static Logger logger;
    
    public JSONObject getOSUpdatePolicy(final JSONObject message) throws JSONException, DataAccessException {
        final Long profileId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        new ProfileFacade().validateIfProfileExists(profileId, APIUtil.getCustomerID(message));
        return new OSUpdatePolicyHandler().getOSUpdatePolicyDetail(profileId);
    }
    
    private JSONObject facadeRequestJSON(final JSONObject json) throws JSONException {
        final JSONObject newJSON = new JSONObject();
        final Iterator<String> keyIterator = json.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            String newKey;
            if (key.equalsIgnoreCase("Profile")) {
                newKey = "Profile";
            }
            else if (key.equalsIgnoreCase("OSUpdatePolicy")) {
                newKey = "OSUpdatePolicy";
            }
            else if (key.equalsIgnoreCase("DeploymentNotifTemplate")) {
                newKey = "DeploymentNotifTemplate";
            }
            else if (key.equalsIgnoreCase("DeploymentWindowTemplate")) {
                newKey = "DeploymentWindowTemplate";
            }
            else if (key.equalsIgnoreCase("DeploymentPolicySettings")) {
                newKey = "DeploymentPolicySettings";
            }
            else {
                if (!key.equalsIgnoreCase("DeploymentPolicyFiles")) {
                    OSUpdateProfileFacade.logger.log(Level.INFO, "Additional key sent for osupdate policy");
                    throw new HttpException(500, "Either new key was not handled by server or invalid key :: " + key);
                }
                newKey = "DeploymentPolicyFiles";
            }
            newJSON.put(newKey, json.get(key));
        }
        return newJSON;
    }
    
    public JSONObject addOSUpdatePolicy(final JSONObject message) throws Exception {
        final JSONObject requestJSON = message.getJSONObject("msg_body");
        final JSONObject convertedRequestJSON = JSONUtil.getInstance().changeJSONKeyCase(requestJSON, 1);
        final JSONObject msgHeaderJSON = this.getMessageJSON(message);
        final JSONObject modifiedJSON = this.facadeRequestJSON(convertedRequestJSON);
        final String profileName = String.valueOf(modifiedJSON.getJSONObject("Profile").get("PROFILE_NAME"));
        final Long customerID = JSONUtil.optLongForUVH(msgHeaderJSON, "CUSTOMER_ID", Long.valueOf(-1L));
        final Boolean checkTrashedName = APIUtil.getBooleanFilter(message, "checktrashedpolicyname", true);
        Criteria trashedNameCriteria = null;
        if (!checkTrashedName) {
            trashedNameCriteria = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)checkTrashedName, 0);
        }
        if (new ProfileHandler().checkProfileNameExist(customerID, profileName, 3, trashedNameCriteria)) {
            throw new APIHTTPException("PAY0008", new Object[0]);
        }
        OSUpdateProfileValidator.getInstance(modifiedJSON.getJSONObject("Profile").getInt("PLATFORM_TYPE")).validatePostData(modifiedJSON);
        final JSONObject responseJSON = OSUpdatePolicyHandler.getInstance().addOrUpdateOSPolicy(msgHeaderJSON, modifiedJSON);
        final JSONObject idJSON = new JSONObject();
        idJSON.put("osupdatepolicie_id", responseJSON.get("PROFILE_ID"));
        final JSONObject messageHeader = message.getJSONObject("msg_header");
        messageHeader.put("resource_identifier", (Object)idJSON);
        return this.getOSUpdatePolicy(message);
    }
    
    public void deleteOSUpdateProfile(final JSONObject message) throws Exception {
        Long profileID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        if (profileID == 0L) {
            profileID = null;
        }
        HashSet<Long> profileSet;
        if (profileID != null) {
            profileSet = new HashSet<Long>(Arrays.asList(profileID));
        }
        else {
            profileSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("osupdatepolicie_ids")));
        }
        new ProfileFacade().validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
        final JSONObject msgHeaderJSON = this.getMessageJSON(message);
        final JSONObject removePolicyJSON = new JSONObject();
        removePolicyJSON.put("PROFILE_IDS", (Object)new JSONArray((Collection)profileSet));
        OSUpdatePolicyHandler.getInstance().deleteOSUpdatePolicy(msgHeaderJSON, removePolicyJSON);
    }
    
    public JSONObject modifyProfile(final JSONObject message) throws Exception {
        final Long profileId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        final JSONObject requestJSON = message.getJSONObject("msg_body");
        final JSONObject modifiedRequestJSON = JSONUtil.getInstance().changeJSONKeyCase(requestJSON, 1);
        final JSONObject msgHeaderJSON = this.getMessageJSON(message);
        final JSONObject modifiedJSON = this.facadeRequestJSON(modifiedRequestJSON);
        modifiedJSON.getJSONObject("Profile").put("PROFILE_ID", (Object)profileId);
        OSUpdateProfileValidator.getInstance(modifiedJSON.getJSONObject("Profile").getInt("PLATFORM_TYPE")).validatePostData(modifiedJSON);
        final JSONObject responseJSON = OSUpdatePolicyHandler.getInstance().addOrUpdateOSPolicy(msgHeaderJSON, modifiedJSON);
        final JSONObject idJSON = new JSONObject();
        idJSON.put("osupdatepolicie_id", responseJSON.get("PROFILE_ID"));
        final JSONObject messageHeader = message.getJSONObject("msg_header");
        messageHeader.put("resource_identifier", (Object)idJSON);
        return this.getOSUpdatePolicy(message);
    }
    
    public JSONObject associateOSUpdateProfileToDevices(final JSONObject message) throws HttpException, JSONException, Exception {
        Long profileID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        if (profileID == 0L) {
            profileID = null;
        }
        HashSet<Long> profileIdSet = null;
        if (profileID != null) {
            profileIdSet = new HashSet<Long>(Arrays.asList(profileID));
        }
        else {
            profileIdSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("osupdatepolicie_ids")));
        }
        final HashSet<Long> resourceSet = new MDMUtil().parseDevicesFromJSON(message.getJSONObject("msg_body"));
        final JSONObject messageHeaderJSON = this.getMessageJSON(message);
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("PROFILE_IDS", (Object)new JSONArray((Collection)profileIdSet));
        requestJSON.put("DEVICE_IDS", (Object)new JSONArray((Collection)resourceSet));
        new ProfileFacade().validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
        new DeviceFacade().validateIfDevicesExists(resourceSet, APIUtil.getCustomerID(message));
        new OSUpdatePolicyHandler().distributeOSUpdatePolicy(messageHeaderJSON, requestJSON);
        return null;
    }
    
    public JSONObject disassociateOSUpdateProfileToDevices(final JSONObject message) throws Exception {
        Long profileID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        if (profileID == 0L) {
            profileID = null;
        }
        HashSet<Long> profileIdSet = null;
        if (profileID != null) {
            profileIdSet = new HashSet<Long>(Arrays.asList(profileID));
        }
        else {
            profileIdSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("osupdatepolicie_ids")));
        }
        final HashSet<Long> resourceSet = new MDMUtil().parseDevicesFromJSON(message.getJSONObject("msg_body"));
        new ProfileFacade().validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
        new DeviceFacade().validateIfDevicesExists(resourceSet, APIUtil.getCustomerID(message));
        final JSONObject messageHeaderJSON = this.getMessageJSON(message);
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("PROFILE_IDS", (Object)new JSONArray((Collection)profileIdSet));
        requestJSON.put("DEVICE_IDS", (Object)new JSONArray((Collection)resourceSet));
        new OSUpdatePolicyHandler().removeDistributedOSUpdatePolicy(messageHeaderJSON, requestJSON);
        return null;
    }
    
    public JSONObject associateOSUpdateProfileToGroups(final JSONObject message) throws Exception {
        Long profileID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        if (profileID == 0L) {
            profileID = null;
        }
        HashSet<Long> profileIdSet = null;
        if (profileID != null) {
            profileIdSet = new HashSet<Long>(Arrays.asList(profileID));
        }
        else {
            profileIdSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("osupdatepolicie_ids")));
        }
        final HashSet<Long> resourceSet = new MDMUtil().parseGroupsFromJSON(message.getJSONObject("msg_body"));
        final JSONObject messageHeaderJSON = this.getMessageJSON(message);
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("PROFILE_IDS", (Object)new JSONArray((Collection)profileIdSet));
        requestJSON.put("GROUP_IDS", (Object)new JSONArray((Collection)resourceSet));
        new ProfileFacade().validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
        new GroupFacade().validateIfGroupsExists(resourceSet, APIUtil.getCustomerID(message));
        new OSUpdatePolicyHandler().distributeOSUpdatePolicy(messageHeaderJSON, requestJSON);
        return null;
    }
    
    public JSONObject disassociateOSUpdateProfileToGroups(final JSONObject message) throws Exception {
        Long profileID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        if (profileID == 0L) {
            profileID = null;
        }
        HashSet<Long> profileIdSet = null;
        if (profileID != null) {
            profileIdSet = new HashSet<Long>(Arrays.asList(profileID));
        }
        else {
            profileIdSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("osupdatepolicie_ids")));
        }
        final HashSet<Long> resourceSet = new MDMUtil().parseGroupsFromJSON(message.getJSONObject("msg_body"));
        new ProfileFacade().validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
        new GroupFacade().validateIfGroupsExists(resourceSet, APIUtil.getCustomerID(message));
        final JSONObject messageHeaderJSON = this.getMessageJSON(message);
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("PROFILE_IDS", (Object)new JSONArray((Collection)profileIdSet));
        requestJSON.put("GROUP_IDS", (Object)new JSONArray((Collection)resourceSet));
        new OSUpdatePolicyHandler().removeDistributedOSUpdatePolicy(messageHeaderJSON, requestJSON);
        return null;
    }
    
    public Object getAllOSUpdateProfiles(final JSONObject request) throws Exception {
        JSONObject responseJSON = new JSONObject();
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final OSUpdatePolicyHandler osupdateHandler = new OSUpdatePolicyHandler();
        final String search = APIUtil.optStringFilter(request, "search", null);
        final PagingUtil pagingUtil = apiUtil.getPagingParams(request);
        final DeltaTokenUtil deltaTokenUtil = apiUtil.getDeltaTokenForAPIRequest(request);
        final Boolean trashed = APIUtil.getBooleanFilter(request, "trashed");
        final Long customerId = APIUtil.getCustomerID(request);
        final int count = osupdateHandler.getAllOSUpdatePolicyCount(new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)trashed, 0), customerId, search);
        final JSONObject meta = new JSONObject();
        meta.put("total_record_count", count);
        Long timeStamp = null;
        if (deltaTokenUtil != null) {
            timeStamp = deltaTokenUtil.getRequestTimestamp();
        }
        responseJSON = osupdateHandler.getAllOSUpdatePolicy(pagingUtil.getStartIndex(), pagingUtil.getLimit(), trashed, timeStamp, customerId, search);
        responseJSON.put("metadata", (Object)meta);
        final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(String.valueOf(request.getJSONObject("msg_header").get("request_url")));
        if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
            responseJSON.put("delta-token", (Object)newDeltaTokenUtil.getDeltaToken());
        }
        if (count != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                responseJSON.put("paging", (Object)pagingJSON);
            }
        }
        return responseJSON;
    }
    
    public JSONObject retryOSUpdateProfiles(final JSONObject message) throws Exception {
        Long profileID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        if (profileID == 0L) {
            profileID = null;
        }
        HashSet<Long> profileIdSet = null;
        if (profileID != null) {
            profileIdSet = new HashSet<Long>(Arrays.asList(profileID));
            new ProfileFacade().validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            HashSet<Long> resourceSet = new HashSet<Long>();
            try {
                resourceSet = new MDMUtil().parseDevicesFromJSON(message.getJSONObject("msg_body"));
            }
            catch (final SyMException e) {
                OSUpdateProfileFacade.logger.log(Level.WARNING, "No resource sent");
            }
            final HashSet<Long> groupSet = new MDMUtil().parseGroupsFromJSON(message.getJSONObject("msg_body"));
            final JSONObject messageHeaderJSON = this.getMessageJSON(message);
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("PROFILE_ID", (Object)profileID);
            if (!resourceSet.isEmpty()) {
                requestJSON.put("RESOURCE_IDS", (Object)new JSONArray((Collection)resourceSet));
            }
            if (!groupSet.isEmpty()) {
                requestJSON.put("GROUP_IDS", (Object)new JSONArray((Collection)groupSet));
            }
            new OSUpdatePolicyHandler().retryOSupdatePolicy(messageHeaderJSON, requestJSON);
            return null;
        }
        throw new APIHTTPException("COM0005", new Object[0]);
    }
    
    public JSONObject trashOSUpdateProfile(final JSONObject message) throws Exception {
        Long profileID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        if (profileID == 0L) {
            profileID = null;
        }
        HashSet<Long> profileIdSet = null;
        if (profileID != null) {
            profileIdSet = new HashSet<Long>(Arrays.asList(profileID));
        }
        else {
            profileIdSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("osupdatepolicie_ids")));
        }
        new ProfileFacade().validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
        final JSONObject messageHeaderJSON = this.getMessageJSON(message);
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("PROFILE_IDS", (Object)new JSONArray((Collection)profileIdSet));
        return new OSUpdatePolicyHandler().trashOSUpdatePolicy(messageHeaderJSON, requestJSON);
    }
    
    public JSONObject restoreOSUpdatePolicy(final JSONObject message) throws Exception {
        Long profileID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "osupdatepolicie_id", (Long)null);
        if (profileID == 0L) {
            profileID = null;
        }
        HashSet<Long> profileIdSet = null;
        if (profileID != null) {
            profileIdSet = new HashSet<Long>(Arrays.asList(profileID));
        }
        else {
            profileIdSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("osupdatepolicie_ids")));
        }
        new ProfileFacade().validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
        final JSONObject messageHeaderJSON = this.getMessageJSON(message);
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("PROFILE_IDS", (Object)new JSONArray((Collection)profileIdSet));
        new OSUpdatePolicyHandler().restoreOSUpdatePolicy(messageHeaderJSON, requestJSON);
        return null;
    }
    
    private JSONObject getMessageJSON(final JSONObject message) throws Exception {
        JSONObject messageHeaderJSON = new JSONObject();
        try {
            messageHeaderJSON = message.getJSONObject("msg_header");
            messageHeaderJSON.put("USER_ID", (Object)MDMUtil.getInstance().getCurrentlyLoggedOnUserID());
            messageHeaderJSON.put("loggedOnUserName", (Object)MDMUtil.getInstance().getCurrentlyLoggedOnUserName());
            messageHeaderJSON.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(message));
        }
        catch (final Exception e) {
            OSUpdateProfileFacade.logger.log(Level.SEVERE, "Exception in messageHeaderJSON", e);
            throw e;
        }
        return messageHeaderJSON;
    }
    
    static {
        OSUpdateProfileFacade.logger = Logger.getLogger("MDMAPILogger");
    }
}
