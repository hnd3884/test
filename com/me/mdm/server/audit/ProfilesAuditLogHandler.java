package com.me.mdm.server.audit;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;

public class ProfilesAuditLogHandler implements AuditLogHandlerInterface
{
    private static ProfilesAuditLogHandler profilesAuditLogHandler;
    private static final String GROUP = "Group";
    private static final String RESOURCE = "Resource";
    private Logger profileLogger;
    
    public ProfilesAuditLogHandler() {
        this.profileLogger = Logger.getLogger("MDMProfileConfigLogger");
    }
    
    public static ProfilesAuditLogHandler getInstance() {
        if (ProfilesAuditLogHandler.profilesAuditLogHandler == null) {
            ProfilesAuditLogHandler.profilesAuditLogHandler = new ProfilesAuditLogHandler();
        }
        return ProfilesAuditLogHandler.profilesAuditLogHandler;
    }
    
    private String getEvenLogRemarksKey(final Boolean isGroup) {
        final String sEventLogRemarksKey = isGroup ? "dc.mdm.actionlog.profilemgmt.association_groups_success" : "dc.mdm.actionlog.profilemgmt.association_device_success";
        return sEventLogRemarksKey;
    }
    
    @Override
    public void addEventLogEntry(final DCQueueData qData) {
        try {
            final JSONObject eventDetails = new JSONObject(qData.queueData.toString());
            final JSONObject profileCollectionMap = (JSONObject)eventDetails.get("ProfileToCollectionMap");
            final JSONObject CollectionApplicableResource = eventDetails.optJSONObject("CollectionApplicableResource");
            final JSONObject profileProperties = eventDetails.has("ProfileProperties") ? ((JSONObject)eventDetails.get("ProfileProperties")) : null;
            final Boolean isGroup = (Boolean)eventDetails.get("isGroup");
            String associatedUserName = (String)eventDetails.get("AssociatedUserName");
            final String commandName = eventDetails.has("CommandName") ? ((String)eventDetails.get("CommandName")) : null;
            final Long customerID = eventDetails.getLong("CustomerID");
            final List resourceOrGroupList = JSONUtil.convertJSONArrayToList((JSONArray)eventDetails.get("ResourceList"));
            final Long eventTimeStamp = eventDetails.optLong("EventTimeStamp", (long)new Long(System.currentTimeMillis()));
            if (commandName == null || (commandName != null && !commandName.equals("BlacklistAppInDevice"))) {
                final Iterator profileIDs = profileCollectionMap.keys();
                final String sEventLogRemarksKey = this.getEvenLogRemarksKey(isGroup);
                final int eventConstant = 2021;
                final String inputType = isGroup ? "Group" : "Resource";
                while (profileIDs.hasNext()) {
                    final Long profileID = Long.parseLong(profileIDs.next());
                    final Long collectionID = profileCollectionMap.getLong(String.valueOf(profileID));
                    final int profileType = ProfileUtil.getProfileType(profileID);
                    if (profileType == 12) {
                        return;
                    }
                    List validResourceList = new ArrayList(resourceOrGroupList);
                    if (profileType == 10) {
                        if (CollectionApplicableResource == null) {
                            continue;
                        }
                        if (CollectionApplicableResource.length() == 0) {
                            continue;
                        }
                        final JSONArray applicableResources = (JSONArray)CollectionApplicableResource.get(String.valueOf(collectionID));
                        if (applicableResources == null) {
                            continue;
                        }
                        if (applicableResources.length() == 0) {
                            continue;
                        }
                        validResourceList = JSONUtil.convertJSONArrayToList(applicableResources);
                    }
                    if (profileProperties != null) {
                        final JSONObject props = (JSONObject)profileProperties.get(String.valueOf(profileID));
                        if (props != null) {
                            final String profileAssociatedUser = (String)props.get("associatedByUserName");
                            if (profileAssociatedUser != null) {
                                associatedUserName = profileAssociatedUser;
                            }
                        }
                    }
                    ProfileAssociateHandler.getInstance().addProfileActionEventLogEntry(customerID, validResourceList, profileID, sEventLogRemarksKey, eventConstant, associatedUserName, inputType, eventTimeStamp);
                    this.profileLogger.log(Level.INFO, "ProfileID :{0}\t\tCollectionID :{1}\t\tProfile Type :{2}\t\tAction :{3}\t\tResourceID: {4}", new Object[] { profileID, collectionID, "Profile", "PROFILE_ASSOCIATION", resourceOrGroupList });
                }
            }
        }
        catch (final Exception ex) {
            this.profileLogger.log(Level.SEVERE, "Exception while addEventLogEntry() of profiles", ex);
        }
    }
    
    static {
        ProfilesAuditLogHandler.profilesAuditLogHandler = null;
    }
}
