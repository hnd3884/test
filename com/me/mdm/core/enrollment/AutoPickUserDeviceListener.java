package com.me.mdm.core.enrollment;

import java.util.Hashtable;
import java.util.Iterator;
import com.me.mdm.server.compliance.ComplianceDistributionHandler;
import java.util.Collection;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import com.me.mdm.server.apps.blacklist.DeviceBlacklistHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Properties;
import java.util.HashMap;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import org.json.JSONArray;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class AutoPickUserDeviceListener extends ManagedDeviceListener
{
    public static Logger logger;
    
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
        AutoPickUserDeviceListener.logger.log(Level.INFO, "[Enroll] [Listener] : Going to call AutoPickUserDeviceListener for {0}", deviceEvent);
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotReassignOldDeviceUser")) {
            final JSONObject reassignJSON = deviceEvent.resourceJSON.optJSONObject("DeviceReassignJson");
            try {
                if (reassignJSON != null) {
                    final Long userID = reassignJSON.optLong("TechUserID");
                    final Integer managedStaus = reassignJSON.optInt("ManagedStatus", -1);
                    if (userID != null && userID != 0L && managedStaus != null && (managedStaus == 2 || managedStaus == 4)) {
                        final JSONObject details = new JSONObject();
                        details.put("DomainName", (Object)reassignJSON.optString("Domain", "MDM"));
                        details.put("primary_key", (Object)"1");
                        details.put("EmailAddr", (Object)reassignJSON.getString("Email"));
                        details.put("UserName", (Object)reassignJSON.getString("UserName"));
                        final JSONArray grpArr = reassignJSON.optJSONArray("GroupIDs");
                        if (grpArr != null && grpArr.length() > 0) {
                            details.put("GroupId", (Object)grpArr);
                        }
                        details.put("CustomerId", deviceEvent.resourceJSON.getLong("CUSTOMER_ID"));
                        details.put("SerialNumber", (Object)deviceEvent.resourceJSON.optString("SERIAL_NUMBER"));
                        details.put("IMEI", (Object)deviceEvent.resourceJSON.optString("IMEI"));
                        details.put("EASID", (Object)deviceEvent.resourceJSON.optString("EAS_DEVICE_IDENTIFIER"));
                        details.put("DeviceName", (Object)reassignJSON.optString("DeviceName"));
                        details.put("UDID", (Object)deviceEvent.resourceJSON.optString("UDID"));
                        final List assignUserList = new ArrayList();
                        assignUserList.add(details);
                        final Long DFEId = new DeviceForEnrollmentHandler().getDeviceForEnrollmentId(deviceEvent.resourceJSON.optString("SERIAL_NUMBER"), deviceEvent.resourceJSON.optString("IMEI"), deviceEvent.resourceJSON.optString("EAS_DEVICE_IDENTIFIER"), deviceEvent.resourceJSON.optString("UDID"));
                        if (DFEId != null) {
                            final String primaryKeyLabel = "primary_key";
                            final JSONObject DFEdetails = new EnrollmentTemplateHandler().getDeviceForEnrollmentIDDetails(DFEId, deviceEvent.resourceJSON.getLong("CUSTOMER_ID"));
                            final JSONObject result = AdminEnrollmentHandler.assignUser(assignUserList, userID, DFEdetails.getInt("TEMPLATE_TYPE"), primaryKeyLabel, DFEdetails.getInt("PLATFORM_TYPE"));
                            if (result.getJSONArray("SuccessList").length() > 0) {
                                final List remarksList = new ArrayList();
                                remarksList.add(details.optString("DeviceName"));
                                MDMEventLogHandler.getInstance().addEvent(2001, DMUserHandler.getUserNameFromUserID(userID), "mdm.enroll.automatic_re_enroll", remarksList, details.optLong("CustomerId"), System.currentTimeMillis());
                                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                                logJSON.put((Object)"REMARKS", (Object)"assign-success");
                                logJSON.put((Object)"NAME", (Object)remarksList);
                                logJSON.put((Object)"MANAGED_DEVICE_ID", (Object)deviceEvent.resourceID);
                                logJSON.put((Object)"DISPLAY_NAME", (Object)reassignJSON.getString("UserName"));
                                MDMOneLineLogger.log(Level.INFO, "DEVICE_USER_ASSIGNED", logJSON);
                                this.completePostEnrollmentAssociations(deviceEvent);
                            }
                        }
                    }
                }
            }
            catch (final Exception e) {
                AutoPickUserDeviceListener.logger.log(Level.INFO, "[Enroll] [Listener] : AutoPickUserDeviceListener for failed and might be partially complete : ", e);
            }
        }
        AutoPickUserDeviceListener.logger.log(Level.INFO, "[Enroll] [Listener] : AutoPickUserDeviceListener for {0}", deviceEvent);
    }
    
    @Deprecated
    private void completePostEnrollmentAssociations(final DeviceEvent deviceEvent) throws Exception {
        final JSONObject oldProps = deviceEvent.resourceJSON.getJSONObject("DeviceReassignJson");
        if (oldProps.has("LostModeDetails")) {
            final JSONObject lostModeDetails = oldProps.getJSONObject("LostModeDetails");
            final JSONObject lostModeData = new JSONObject();
            lostModeData.put("RESOURCE_ID", (Object)deviceEvent.resourceID);
            lostModeData.put("CONTACT_NUMBER", (Object)lostModeDetails.optString("PHONE_NUMBER"));
            lostModeData.put("LOCK_SCREEN_MESSAGE", (Object)lostModeDetails.optString("LOCK_MESSAGE"));
            lostModeData.put("PLATFORM_TYPE", deviceEvent.platformType);
            lostModeData.put("ADDED_BY", -1L);
            final Long userID = oldProps.optLong("TechUserID");
            if (!userID.equals(0L)) {
                lostModeData.put("ADDED_BY", (Object)userID);
            }
            new LostModeDataHandler().activateLostMode(lostModeData);
        }
        final JSONArray docList = (JSONArray)oldProps.get("DocList");
        if (docList != null && docList.length() > 0) {
            final JSONObject docDeviceAssociation = new JSONObject();
            docDeviceAssociation.put("DOC_ID", (Object)docList);
            docDeviceAssociation.put("ASSOCIATE", (Object)Boolean.TRUE);
            docDeviceAssociation.put("MANAGEDDEVICE_ID", (Object)deviceEvent.resourceID.toString());
            DocMgmtDataHandler.getInstance().saveDocDeviceAssociation(new Long[] { deviceEvent.customerID }, docDeviceAssociation);
        }
        if (oldProps.optJSONObject("ProfileIDs").length() == 0) {
            return;
        }
        final JSONObject profileProps = oldProps.getJSONObject("ProfileProps");
        final JSONObject profiles = oldProps.getJSONObject("ProfileIDs");
        final List appProfileIds = new ArrayList();
        final List profileIds = new ArrayList();
        final List blacklistProfileIds = new ArrayList();
        final List osupdateProfileIds = new ArrayList();
        final List dataUsageProfileIds = new ArrayList();
        final List complianceProfileIds = new ArrayList();
        Iterator iterator = profileProps.keys();
        final HashMap profilePropsMap = new HashMap();
        while (iterator.hasNext()) {
            final String profileId = iterator.next();
            final Integer profileType = profileProps.getJSONObject(profileId).getInt("profileType");
            if (profileType == 1) {
                profileIds.add(profileId);
            }
            else if (profileType == 2) {
                appProfileIds.add(profileId);
            }
            else if (profileType == 3) {
                osupdateProfileIds.add(profileId);
            }
            else if (profileType == 4) {
                blacklistProfileIds.add(profileId);
            }
            else if (profileType == 8) {
                dataUsageProfileIds.add(profileId);
            }
            else if (profileType == 5) {
                complianceProfileIds.add(profileId);
            }
            final HashMap curProfileProps = new HashMap();
            curProfileProps.put("associatedByUser", profileProps.getJSONObject(profileId).get("associatedByUser"));
            curProfileProps.put("associatedByUserName", profileProps.getJSONObject(profileId).get("associatedByUserName"));
            profilePropsMap.put(Long.parseLong(profileId), curProfileProps);
        }
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("isSilentInstall", true);
        ((Hashtable<String, Boolean>)properties).put("isNotify", false);
        ((Hashtable<String, Long>)properties).put("customerId", deviceEvent.customerID);
        ((Hashtable<String, HashMap>)properties).put("profileProperties", profilePropsMap);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        final List resList = new ArrayList();
        resList.add(deviceEvent.resourceID);
        ((Hashtable<String, List>)properties).put("resourceList", resList);
        if (appProfileIds.size() > 0) {
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", this.convertToHashMap(profiles, appProfileIds));
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, String>)properties).put("commandName", "InstallApplication");
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
        if (profileIds.size() > 0) {
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", this.convertToHashMap(profiles, profileIds));
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", "InstallProfile");
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
        if (dataUsageProfileIds.size() > 0) {
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", this.convertToHashMap(profiles, dataUsageProfileIds));
            ((Hashtable<String, String>)properties).put("commandName", "InstallDataProfile");
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
        if (blacklistProfileIds.size() > 0) {
            final HashMap params = new HashMap();
            params.put("profileCollectionMap", this.convertToHashMap(profiles, blacklistProfileIds));
            params.put("profileProperties", profilePropsMap);
            params.put("resourceIDs", resList);
            params.put("CUSTOMER_ID", deviceEvent.customerID);
            new DeviceBlacklistHandler().blacklistAppInResource(params);
        }
        if (osupdateProfileIds.size() > 0) {
            final JSONObject distributePoliciesJSON = new JSONObject();
            iterator = osupdateProfileIds.iterator();
            final JSONArray osJSONArray = new JSONArray();
            while (iterator.hasNext()) {
                final String osID = iterator.next();
                Long.parseLong(osID);
                osJSONArray.put((Object)osID);
            }
            final JSONArray deviceArray = new JSONArray();
            deviceArray.put((Object)deviceEvent.resourceID);
            distributePoliciesJSON.put("PROFILE_IDS", (Object)osJSONArray);
            distributePoliciesJSON.put("DEVICE_IDS", (Object)deviceArray);
            final JSONObject msgHeaderJSON = new JSONObject();
            msgHeaderJSON.put("CUSTOMER_ID", (Object)deviceEvent.customerID);
            msgHeaderJSON.put("USER_ID", profilePropsMap.get(osupdateProfileIds.get(0)).get("associatedByUser"));
            msgHeaderJSON.put("loggedOnUserName", profilePropsMap.get(osupdateProfileIds.get(0)).get("associatedByUserName"));
            OSUpdatePolicyHandler.getInstance().distributeOSUpdatePolicy(msgHeaderJSON, distributePoliciesJSON);
        }
        if (complianceProfileIds.size() > 0) {
            final JSONObject complianceJSON = new JSONObject();
            complianceJSON.put("customer_id", (Object)deviceEvent.customerID);
            iterator = complianceProfileIds.iterator();
            final HashMap complianceMap = this.convertToHashMap(profiles, appProfileIds);
            while (iterator.hasNext()) {
                final String profID = iterator.next();
                complianceJSON.put("resource_list", (Collection)resList);
                complianceJSON.put("user_id", profileProps.getJSONObject(profID).get("associatedByUser"));
                complianceJSON.put("user_name", profileProps.getJSONObject(profID).get("associatedByUser"));
                complianceJSON.put("compliance_id", Long.parseLong(profID));
                final Long collectionId = complianceMap.get(profID);
                complianceJSON.put("collection_id", (Object)collectionId);
                complianceJSON.put("profile_id", Long.parseLong(profID));
                ComplianceDistributionHandler.getInstance().associateComplianceToDevices(complianceJSON);
            }
        }
    }
    
    private HashMap convertToHashMap(final JSONObject jsonObject, final List keys) throws Exception {
        final HashMap hashMap = new HashMap();
        Iterator iterator = null;
        if (keys == null) {
            iterator = jsonObject.keys();
        }
        else {
            iterator = keys.iterator();
        }
        while (iterator.hasNext()) {
            final String key = iterator.next();
            if (jsonObject.get(key) != null) {
                hashMap.put(Long.parseLong(key), jsonObject.get(key));
            }
        }
        return hashMap;
    }
    
    static {
        AutoPickUserDeviceListener.logger = Logger.getLogger("MDMEnrollment");
    }
}
