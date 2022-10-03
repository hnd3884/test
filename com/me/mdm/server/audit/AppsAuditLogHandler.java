package com.me.mdm.server.audit;

import java.util.logging.Level;
import java.util.Map;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class AppsAuditLogHandler implements AuditLogHandlerInterface
{
    private static AppsAuditLogHandler appsAuditLogHandler;
    private static final String GROUP = "Group";
    private static final String RESOURCE = "Resource";
    private Logger profileLogger;
    
    public AppsAuditLogHandler() {
        this.profileLogger = Logger.getLogger("MDMProfileConfigLogger");
    }
    
    public static AppsAuditLogHandler getInstance() {
        if (AppsAuditLogHandler.appsAuditLogHandler == null) {
            AppsAuditLogHandler.appsAuditLogHandler = new AppsAuditLogHandler();
        }
        return AppsAuditLogHandler.appsAuditLogHandler;
    }
    
    private String getEvenLogRemarksKey(final Boolean isGroup, final Boolean isApplicableApp, final Boolean isBusinessStoreApp) {
        if (isApplicableApp) {
            if (isGroup) {
                if (isBusinessStoreApp) {
                    return "mdm.actionlog.businessapp.dist_group_suc";
                }
                return "dc.mdm.actionlog.appmgmt.dist_group_suc";
            }
            else {
                if (isBusinessStoreApp) {
                    return "mdm.actionlog.businessapp.dist_device_suc";
                }
                return "dc.mdm.actionlog.appmgmt.dist_device_suc";
            }
        }
        else if (isGroup) {
            if (isBusinessStoreApp) {
                return "mdm.actionlog.businessapp.dist_group_na";
            }
            return "dc.mdm.actionlog.appmgmt.dist_group_na";
        }
        else {
            if (isBusinessStoreApp) {
                return "mdm.actionlog.businessapp.dist_device_na";
            }
            return "dc.mdm.actionlog.appmgmgt.dist_device_na";
        }
    }
    
    private HashMap groupResourceToAppIDMapToAppToResourceList(final HashMap resourceToAppID) {
        final HashMap<Long, List<Long>> appToResourceList = new HashMap<Long, List<Long>>();
        for (final Object resourceID : resourceToAppID.keySet()) {
            if (appToResourceList.containsKey(resourceToAppID.get(resourceID))) {
                final List resourceList = appToResourceList.get(resourceToAppID.get(resourceID));
                resourceList.add(resourceID);
            }
            else {
                final List resourceList = new ArrayList();
                resourceList.add(resourceID);
                appToResourceList.put(resourceToAppID.get(resourceID), resourceList);
            }
        }
        return appToResourceList;
    }
    
    @Override
    public void addEventLogEntry(final DCQueueData qData) {
        try {
            final JSONObject eventDetails = new JSONObject(qData.queueData.toString());
            final JSONObject profileCollectionMap = (JSONObject)eventDetails.get("ProfileToCollectionMap");
            final JSONObject profileToBusinessStore = eventDetails.optJSONObject("profileToBusinessStore");
            final JSONObject collnToApplicableResList = (JSONObject)eventDetails.get("CollectionApplicableResource");
            final JSONObject profileProperties = eventDetails.has("ProfileProperties") ? ((JSONObject)eventDetails.get("ProfileProperties")) : null;
            final Boolean isGroup = (Boolean)eventDetails.get("isGroup");
            String associatedUserName = (String)eventDetails.opt("AssociatedUserName");
            final String commandName = eventDetails.has("CommandName") ? ((String)eventDetails.get("CommandName")) : null;
            final Long customerID = eventDetails.getLong("CustomerID");
            final Long eventTimeStamp = eventDetails.optLong("EventTimeStamp", (long)new Long(System.currentTimeMillis()));
            if (commandName == null || (commandName != null && !commandName.equals("BlacklistAppInDevice"))) {
                final Map<Long, String> collectionToReleaseLabelText = AppVersionDBUtil.getInstance().getCollectionToReleaseLabelNameMap(JSONUtil.getValuesOfJSONObject(profileCollectionMap));
                final Iterator profileIDs = profileCollectionMap.keys();
                String sEventLogRemarksKey = null;
                final int eventConstant = 2033;
                final String inputType = isGroup ? "Group" : "Resource";
                while (profileIDs.hasNext()) {
                    final Long profileID = Long.parseLong(profileIDs.next());
                    final Long collectionID = profileCollectionMap.getLong(String.valueOf(profileID));
                    final Long businessStoreID = (profileToBusinessStore != null) ? profileToBusinessStore.optLong(String.valueOf(profileID), -1L) : -1L;
                    final List validResList = JSONUtil.convertJSONArrayToList((JSONArray)collnToApplicableResList.get(String.valueOf(collectionID)));
                    final Long appGroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionID);
                    if (profileProperties != null) {
                        final JSONObject props = (JSONObject)profileProperties.get(String.valueOf(profileID));
                        if (props != null) {
                            final String profileAssociatedUser = (String)props.get("associatedByUserName");
                            if (profileAssociatedUser != null) {
                                associatedUserName = profileAssociatedUser;
                            }
                        }
                    }
                    final HashMap customArgs = new HashMap();
                    customArgs.put("releaseLabelName", collectionToReleaseLabelText.get(collectionID));
                    Boolean isBusinessStoreApp = Boolean.FALSE;
                    if (businessStoreID != -1L) {
                        customArgs.put("businessstore_name", MDBusinessStoreUtil.getBusinessStoreName(businessStoreID));
                        isBusinessStoreApp = true;
                    }
                    if (!isGroup) {
                        final HashMap<Long, Long> resourceToAppID = new AppLicenseMgmtHandler().getAppIDsForResource(validResList, appGroupID, collectionID);
                        final HashMap appToResourceList = this.groupResourceToAppIDMapToAppToResourceList(resourceToAppID);
                        for (final Object appID : appToResourceList.keySet()) {
                            final String appVersion = AppsUtil.getInstance().getAppVersionFromAppID((Long)appID);
                            final List resourceListForGivenAppID = appToResourceList.get(appID);
                            if ((long)appID != -1L) {
                                customArgs.put("appVersion", AppsUtil.getValidVersion(appVersion));
                                sEventLogRemarksKey = this.getEvenLogRemarksKey(isGroup, Boolean.TRUE, isBusinessStoreApp);
                            }
                            else {
                                sEventLogRemarksKey = this.getEvenLogRemarksKey(isGroup, Boolean.FALSE, isBusinessStoreApp);
                            }
                            ProfileAssociateHandler.getInstance().addProfileActionEventLogEntry(customerID, resourceListForGivenAppID, profileID, sEventLogRemarksKey, eventConstant, associatedUserName, inputType, eventTimeStamp, customArgs);
                            this.profileLogger.log(Level.INFO, "ProfileID :{0}\t\tCollectionID :{1}\t\tAppID :{2}\t\tProfile Type :{3}\t\tAction :{4}\t\tResourceID: {5}", new Object[] { profileID, collectionID, appID, "App", "APP_ASSOCIATION", resourceListForGivenAppID });
                        }
                    }
                    else {
                        final String appVersion = AppsUtil.getInstance().getAppVersionFromCollectionID(collectionID);
                        final List resourceList = validResList;
                        customArgs.put("appVersion", AppsUtil.getValidVersion(appVersion));
                        sEventLogRemarksKey = this.getEvenLogRemarksKey(isGroup, Boolean.TRUE, isBusinessStoreApp);
                        ProfileAssociateHandler.getInstance().addProfileActionEventLogEntry(customerID, resourceList, profileID, sEventLogRemarksKey, eventConstant, associatedUserName, inputType, eventTimeStamp, customArgs);
                        this.profileLogger.log(Level.INFO, "ProfileID :{0}\t\tCollectionID :{1}\t\tProfile Type :{2}\t\tAction :{3}\t\tResourceID: {4}", new Object[] { profileID, collectionID, "App", "APP_ASSOCIATION", resourceList });
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.profileLogger.log(Level.SEVERE, "Exception in addEventLogEntry() of apps", ex);
        }
    }
    
    static {
        AppsAuditLogHandler.appsAuditLogHandler = null;
    }
}
