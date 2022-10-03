package com.me.mdm.server.announcement.facade;

import com.me.uem.announcement.AnnouncementImageFileHandler;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.HashSet;
import org.json.JSONArray;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.me.uem.announcement.AnnouncementHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.persistence.DataAccessException;
import com.me.uem.announcement.AnnouncementException;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.announcement.handler.AnnouncementDBHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AnnouncementFacade
{
    private final Logger logger;
    
    public AnnouncementFacade() {
        this.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
    
    public static AnnouncementFacade getNewInstance() {
        return new AnnouncementFacade();
    }
    
    public JSONObject getAnnouncementInfo(final JSONObject apiRequestJSON) throws Exception, APIException {
        final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
        final Long customerID = APIUtil.getCustomerID(apiRequestJSON);
        final Long announcementId = APIUtil.getResourceID(apiRequestJSON, "announcement_id");
        if (announcementId != -1L && !announcementHandler.isCustomerEligible(customerID, announcementId)) {
            throw new APIException("COM0028");
        }
        JSONObject annJSON = announcementHandler.getAnnouncementInfo(announcementId);
        annJSON = announcementHandler.getAnnouncementDistributedInfo(announcementId, annJSON);
        return annJSON;
    }
    
    public JSONObject addAnnouncement(final JSONObject apiRequestJson) throws Exception {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        boolean operationSuccess = false;
        final JSONObject responseJSON = new JSONObject();
        boolean isAdd = false;
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequestJson);
            final JSONObject announcementJSON = apiRequestJson.getJSONObject("msg_body");
            long announcementId = APIUtil.getResourceID(apiRequestJson, "announcement_id");
            if (announcementId == -1L) {
                isAdd = true;
            }
            else {
                announcementJSON.put("announcement_id", announcementId);
            }
            final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
            if (announcementId != -1L && !announcementHandler.isCustomerEligible(customerID, announcementId)) {
                throw new APIException("COM0028");
            }
            final String announcementName = announcementJSON.getString("announcement_name");
            final Long userID = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            announcementId = announcementHandler.addorUpdateAnnouncement(announcementJSON);
            final JSONObject resourceJSON = apiRequestJson.getJSONObject("msg_header").getJSONObject("resource_identifier");
            resourceJSON.put("announcement_id", announcementId);
            final JSONObject messageHeader = apiRequestJson.getJSONObject("msg_header");
            messageHeader.put("resource_identifier", (Object)resourceJSON);
            apiRequestJson.put("msg_header", (Object)messageHeader);
            secLog.put((Object)"ANNOUNCEMENT_ID", (Object)announcementId);
            String sEventLogRemarks = "mdm.actionlog.announcement.modify_success";
            JSONObject profileJson;
            if (isAdd) {
                profileJson = this.addProfileInformation(announcementId, announcementName, customerID, userID);
                announcementJSON.put("announcement_id", announcementId);
                sEventLogRemarks = "mdm.actionlog.announcement.create_success";
            }
            else {
                profileJson = announcementHandler.getCollectionIdForAnnouncement(announcementId);
                profileJson.put("CUSTOMER_ID", (Object)customerID);
                profileJson.put("LAST_MODIFIED_BY", (Object)userID);
                profileJson.put("LAST_MODIFIED_TIME", System.currentTimeMillis());
                ProfileHandler.addOrUpdateProfile(profileJson);
            }
            announcementJSON.put("announcement_distributed_time", (Object)"%last_distributred_time%");
            this.publishAnnouncement(profileJson, announcementJSON);
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            MDMEventLogHandler.getInstance().MDMEventLogEntry(75000, null, sUserName, sEventLogRemarks, announcementName, customerID);
            this.logger.log(Level.INFO, "Successfully created the Anouncement : {0}", announcementId);
            this.logger.log(Level.INFO, "Redistributing the modified announcement : {0}", announcementId);
            if (!isAdd) {
                final boolean isRedistributed = AnnouncementDistributionFacade.getNewInstance().redistributeAnnouncement(customerID, announcementId, (Long)profileJson.get("PROFILE_ID"), (Long)profileJson.get("COLLECTION_ID"));
                secLog.put((Object)"REDISTRIBUTED", (Object)(isRedistributed ? "true" : "false"));
            }
            operationSuccess = true;
            this.logger.log(Level.INFO, "Completed redistributing the modified announcement : {0}", announcementId);
            return this.getAnnouncementInfo(apiRequestJson);
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Invalid post params", (Throwable)e);
            throw e;
        }
        catch (final AnnouncementException e2) {
            this.logger.log(Level.SEVERE, "Invalid post params", e2);
            throw e2;
        }
        catch (final DataAccessException e3) {
            this.logger.log(Level.SEVERE, "Can't perform data operation", (Throwable)e3);
            throw e3;
        }
        catch (final Exception e4) {
            this.logger.log(Level.SEVERE, "Exception adding announcement", e4);
            throw e4;
        }
        finally {
            final String operation = isAdd ? "ADD_ANNOUNCEMENT" : "MODIFY_ANNOUNCEMENT";
            final String remarks = isAdd ? (operationSuccess ? "create-success" : "create-failed") : (operationSuccess ? "update-success" : "update-failed");
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, operation, secLog);
        }
        return this.getAnnouncementInfo(apiRequestJson);
    }
    
    private void doRollback() {
        try {
            MDMUtil.getUserTransaction().rollback();
        }
        catch (final Exception e1) {
            this.logger.log(Level.SEVERE, "Exception in rollback", e1);
        }
    }
    
    private void publishAnnouncement(final JSONObject collectionJson, final JSONObject announcementJSON) throws Exception {
        new AnnouncementHandler().publishAnnouncement(collectionJson, announcementJSON);
    }
    
    private JSONObject addProfileInformation(final long announcementId, final String announcementName, final long customerID, final long userID) throws Exception {
        final JSONObject profileJSON = new JSONObject();
        profileJSON.put("PROFILE_TYPE", 9);
        profileJSON.put("PROFILE_NAME", (Object)announcementName);
        profileJSON.put("PLATFORM_TYPE", 0);
        profileJSON.put("CREATED_BY", userID);
        profileJSON.put("PROFILE_DESCRIPTION", (Object)"Annoncement Profile");
        profileJSON.put("CUSTOMER_ID", customerID);
        profileJSON.put("SECURITY_TYPE", 3);
        profileJSON.put("CONFIG_NAME", (Object)"Announcement");
        profileJSON.put("CONFIG_ID", 10000);
        ProfileConfigHandler.addOrModifyProfileCollection(profileJSON);
        final DataObject configDO = ProfileHandler.createConfigDataDO(profileJSON);
        final Row configData = configDO.getFirstRow("ConfigData");
        final Row cfgDataItemRow = new Row("ConfigDataItem");
        cfgDataItemRow.set("CONFIG_DATA_ID", configData.get("CONFIG_DATA_ID"));
        cfgDataItemRow.set("EXECUTION_ORDER", (Object)0);
        final Row annoncConfig = new Row("AnnouncementConfigData");
        annoncConfig.set("ANNOUNCEMENT_ID", (Object)announcementId);
        annoncConfig.set("CONFIG_DATA_ITEM_ID", cfgDataItemRow.get("CONFIG_DATA_ITEM_ID"));
        configDO.addRow(cfgDataItemRow);
        configDO.addRow(annoncConfig);
        MDMUtil.getPersistence().update(configDO);
        return profileJSON;
    }
    
    public JSONObject getAnnoucementNBarIcon(final JSONObject apiRequest) throws Exception {
        return new JSONObject().put("icons", (Object)new AnnouncementDBHandler().getNBarIcons());
    }
    
    public JSONObject getAnnouncementResponse(final JSONObject apiRequest) throws Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        final PagingUtil pagingUtil = apiUtil.getPagingParams(apiRequest);
        final int startIndex = pagingUtil.getStartIndex();
        final int limit = pagingUtil.getLimit();
        long timeStamp = -1L;
        final DeltaTokenUtil deltaTokenUtil = apiUtil.getDeltaTokenForAPIRequest(apiRequest);
        if (deltaTokenUtil != null) {
            timeStamp = deltaTokenUtil.getRequestTimestamp();
        }
        final JSONObject responseJSON = new AnnouncementDBHandler().getAllAnnouncementInfo(customerId, startIndex, limit, timeStamp, apiRequest);
        this.setMetaInfo(responseJSON, pagingUtil, apiRequest, customerId);
        return responseJSON;
    }
    
    private void setMetaInfo(final JSONObject responseJSON, final PagingUtil pagingUtil, final JSONObject apiRequest, final long customerId) throws Exception {
        int count = 0;
        try {
            count = Integer.parseInt(responseJSON.getJSONObject("metadata").getString("total_record_count"));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting total count", e);
        }
        final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(String.valueOf(apiRequest.getJSONObject("msg_header").get("request_url")));
        if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
            responseJSON.put("delta-token", (Object)newDeltaTokenUtil.getDeltaToken());
        }
        if (count != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                responseJSON.put("paging", (Object)pagingJSON);
            }
        }
    }
    
    public void deleteAnnouncement(final JSONObject apiRequestJson) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final boolean isAdd = false;
        try {
            MDMUtil.getUserTransaction().begin();
            final Long customerID = APIUtil.getCustomerID(apiRequestJson);
            final long announcementId = APIUtil.getResourceID(apiRequestJson, "announcement_id");
            if (announcementId == -1L) {
                throw new APIHTTPException("COM0024", new Object[0]);
            }
            final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
            if (!announcementHandler.isCustomerEligible(customerID, announcementId)) {
                throw new APIHTTPException("COM0028", new Object[0]);
            }
            final ArrayList announcementList = new ArrayList();
            announcementList.add(announcementId);
            AnnouncementDistributionFacade.getNewInstance().moveProfilesToTrash(customerID, announcementList);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "API Exception", e);
            this.doRollback();
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Invalid post params", (Throwable)e2);
            throw e2;
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception deleting announcement", e3);
            throw e3;
        }
    }
    
    private void deleteProfileInformation(final long announcementId) throws DataAccessException {
        final Criteria cAn = new Criteria(new Column("AnnouncementConfigData", "ANNOUNCEMENT_ID"), (Object)announcementId, 0);
        this.deleteProfileInformation(cAn);
    }
    
    public void deleteAnnouncementList(final JSONObject apiRequestJson) throws Exception {
        final JSONObject messageBody = apiRequestJson.getJSONObject("msg_body");
        final JSONArray annArray = messageBody.getJSONArray("announcement_id");
        final ArrayList annLIst = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(annArray);
        final Long customerID = APIUtil.getCustomerID(apiRequestJson);
        final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
        if (!announcementHandler.isCustomerEligible(customerID, annLIst)) {
            throw new APIHTTPException("COM0028", new Object[0]);
        }
        final HashSet<Long> trashedProfiles = announcementHandler.getTrashedAnnouncement(annLIst);
        if (trashedProfiles.size() >= 1) {
            throw new APIHTTPException("COM0015", new Object[] { "Announcements [" + APIUtil.getCommaSeperatedString(trashedProfiles) + "] are already trashed" });
        }
        AnnouncementDistributionFacade.getNewInstance().moveProfilesToTrash(customerID, annLIst);
    }
    
    private void deleteProfileInformation(final long[] announcementId) throws DataAccessException {
        final Criteria cAn = new Criteria(new Column("AnnouncementConfigData", "ANNOUNCEMENT_ID"), (Object)announcementId, 8);
        this.deleteProfileInformation(cAn);
    }
    
    private void deleteProfileInformation(final Criteria criteria) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("Profile");
        deleteQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        deleteQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        deleteQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        deleteQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        deleteQuery.addJoin(new Join("ConfigDataItem", "AnnouncementConfigData", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        deleteQuery.setCriteria(criteria);
        try {
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception deleting announcement", (Throwable)e);
            throw e;
        }
    }
    
    public JSONObject addAnnouncementImgs(final JSONObject apiRequestJson) throws Exception {
        final JSONObject responseJson = new JSONObject();
        final Long customerId = APIUtil.getCustomerID(apiRequestJson);
        final JSONArray announcementJSON = apiRequestJson.getJSONObject("msg_body").getJSONArray("file_id");
        final JSONArray resArray = new JSONArray();
        for (int i = 0; i < announcementJSON.length(); ++i) {
            final long fileId = announcementJSON.getLong(i);
            final String dispFilePathDM = FileUploadManager.getFilePath(fileId);
            final String filepath = AnnouncementImageFileHandler.getInstance().saveAnnouncementImg(dispFilePathDM, customerId);
            final JSONObject fileJson = new JSONObject();
            fileJson.put(String.valueOf(fileId), (Object)filepath);
            resArray.put((Object)fileJson);
        }
        responseJson.put("files", (Object)resArray);
        return responseJson;
    }
}
