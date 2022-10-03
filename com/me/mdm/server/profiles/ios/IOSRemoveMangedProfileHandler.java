package com.me.mdm.server.profiles.ios;

import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class IOSRemoveMangedProfileHandler
{
    private static final Logger LOGGER;
    private static final Integer DAY;
    
    public void checkAndRemoveProfile(final JSONObject params) {
        try {
            final Long customerId = (Long)params.get("customerId");
            final Long resourceId = (Long)params.get("resourceId");
            if (customerId != null && resourceId != null) {
                final String paramValue = CustomerParamsHandler.getInstance().getParameterValue("DELETE_NOTMANAGED_PROFILELIST", (long)customerId);
                if (!MDMStringUtils.isEmpty(paramValue) && Boolean.valueOf(paramValue)) {
                    IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "Checking the managed device for profile removal. For customer:{0} & resource:{1}", new Object[] { customerId, resourceId });
                    final List<String> profileList = this.getProfileList(customerId);
                    final Criteria defaultProfile = new Criteria(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"), (Object)new String[] { "com.mdm.DCAppCatalog", "com.zohocorp.mdm", "com.mdm.osupdate.restriction", "com.mdm.kiosk_default_mdm_app", "com.mdm.kiosk_install_profile", "com.mdm.MDMBlacklistApps" }, 9);
                    final Criteria resourceCriteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Criteria installedSource = new Criteria(new Column("ResourceToConfigProfiles", "INSTALLED_SOURCE"), (Object)IOSConfigPayloadDataHandler.MANAGED_CONFIG_PROFILE, 0);
                    Criteria finalCriteria = defaultProfile.and(resourceCriteria).and(installedSource);
                    if (profileList.size() > 0) {
                        finalCriteria = finalCriteria.and(new Criteria(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"), (Object)profileList.toArray(), 9));
                    }
                    final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
                    selectQuery.addJoin(new Join("Resource", "ResourceToConfigProfiles", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                    selectQuery.addJoin(new Join("ResourceToConfigProfiles", "IOSConfigPayload", new String[] { "PROFILE_PAYLOAD_ID" }, new String[] { "PAYLOAD_ID" }, 2));
                    final Table subIOSConfigPayload = new Table("IOSConfigPayload", "subIOSConfigPayload");
                    selectQuery.addJoin(new Join("ResourceToConfigProfiles", "IOSConfigProfilePayloads", new String[] { "PROFILE_PAYLOAD_ID" }, new String[] { "PROFILE_PAYLOAD_ID" }, 2));
                    selectQuery.addJoin(new Join(new Table("IOSConfigProfilePayloads"), subIOSConfigPayload, new String[] { "PAYLOAD_PAYLOAD_ID" }, new String[] { "PAYLOAD_ID" }, 2));
                    selectQuery.setCriteria(finalCriteria);
                    selectQuery.addSelectColumn(new Column("ResourceToConfigProfiles", "RESOURCE_ID"));
                    selectQuery.addSelectColumn(new Column("ResourceToConfigProfiles", "PROFILE_PAYLOAD_ID"));
                    selectQuery.addSelectColumn(new Column("IOSConfigPayload", "PAYLOAD_ID"));
                    selectQuery.addSelectColumn(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"));
                    selectQuery.addSelectColumn(new Column("IOSConfigProfilePayloads", "*"));
                    selectQuery.addSelectColumn(new Column(subIOSConfigPayload.getTableAlias(), "PAYLOAD_ID"));
                    selectQuery.addSelectColumn(new Column(subIOSConfigPayload.getTableAlias(), "PAYLOAD_IDENTIFIER"));
                    selectQuery.addSelectColumn(new Column(subIOSConfigPayload.getTableAlias(), "PAYLOAD_TYPE"));
                    final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                    if (!dataObject.isEmpty()) {
                        IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "Resource having the managed deleted profile");
                        final JSONObject removalJSON = new JSONObject();
                        final List<String> payloadList = new ArrayList<String>();
                        final JSONArray resourceArray = new JSONArray();
                        resourceArray.put((Object)resourceId);
                        final Iterator iterator = dataObject.getRows("IOSConfigPayload");
                        while (iterator.hasNext()) {
                            final Row payloadRow = iterator.next();
                            final String payloadIdentifier = (String)payloadRow.get("PAYLOAD_IDENTIFIER");
                            final Long payloadId = (Long)payloadRow.get("PAYLOAD_ID");
                            if (this.checkPayloadIdentifier(payloadIdentifier)) {
                                final Iterator iterator2 = dataObject.getRows(subIOSConfigPayload.getTableAlias(), new Criteria(new Column("IOSConfigProfilePayloads", "PROFILE_PAYLOAD_ID"), (Object)payloadId, 0));
                                final List<String> additionalPayloadIdentifier = this.checkAnyOtherPayloadIdentifierNeeded(iterator2);
                                for (final String additionalPayload : additionalPayloadIdentifier) {
                                    removalJSON.put(additionalPayload, (Object)resourceArray);
                                }
                                if (this.isPayloadMatches(profileList, payloadIdentifier)) {
                                    continue;
                                }
                                payloadList.add(payloadIdentifier);
                                final JSONObject payloadObject = new JSONObject();
                                payloadObject.put("RESOURCE_ID", (Object)resourceArray);
                                payloadObject.put("PAYLOAD_ID", (Object)payloadId);
                                removalJSON.put(payloadIdentifier, (Object)payloadObject);
                            }
                        }
                        IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "Going to remove the managed profile in the device");
                        if (removalJSON.length() > 0) {
                            final List notRemovalList = this.removeValidProfile(payloadList, customerId);
                            for (final String payloadString : notRemovalList) {
                                IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "Removing some valid profiles.{0}", new Object[] { payloadString });
                                removalJSON.remove(payloadString);
                            }
                            new ConfigProfileRemoveHandler().removeConfigProfileAndRefresh(removalJSON);
                        }
                    }
                    else {
                        IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "No managed deleted profile in the device");
                    }
                }
            }
        }
        catch (final Exception e) {
            IOSRemoveMangedProfileHandler.LOGGER.log(Level.SEVERE, "Exception in executing the profile task");
        }
    }
    
    private List<String> removeValidProfile(final List profilepayloadList, final Long customerId) {
        final List<String> availableList = new ArrayList<String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCustomerRel"));
            selectQuery.addJoin(new Join("ProfileToCustomerRel", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Criteria payloadCriteria = new Criteria(new Column("Profile", "PROFILE_PAYLOAD_IDENTIFIER"), (Object)profilepayloadList.toArray(), 8);
            final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(payloadCriteria.and(customerCriteria));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("Profile");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final String payloadIdentifier = (String)row.get("PROFILE_PAYLOAD_IDENTIFIER");
                    availableList.add(payloadIdentifier);
                }
            }
        }
        catch (final DataAccessException e) {
            IOSRemoveMangedProfileHandler.LOGGER.log(Level.SEVERE, "Exception while checking remove valid profile", (Throwable)e);
        }
        return availableList;
    }
    
    private List<String> getProfileList(final Long customerId) throws DataAccessException {
        final List<String> profileList = new ArrayList<String>();
        try {
            final SelectQuery profileQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCustomerRel"));
            profileQuery.addJoin(new Join("ProfileToCustomerRel", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria notnull = new Criteria(new Column("Profile", "PROFILE_PAYLOAD_IDENTIFIER"), (Object)null, 1);
            final Criteria profileType = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)1, 0);
            Criteria platformType = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)1, 0);
            platformType = platformType.or(new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)new Integer[] { 6, 7 }, 8));
            profileQuery.setCriteria(customerCriteria.and(notnull).and(platformType).and(profileType));
            profileQuery.addSelectColumn(new Column("ProfileToCustomerRel", "*"));
            profileQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            profileQuery.addSelectColumn(new Column("Profile", "PROFILE_PAYLOAD_IDENTIFIER"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(profileQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("Profile");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final String profileIdentifier = (String)row.get("PROFILE_PAYLOAD_IDENTIFIER");
                    if (!MDMStringUtils.isEmpty(profileIdentifier)) {
                        profileList.add(profileIdentifier);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            IOSRemoveMangedProfileHandler.LOGGER.log(Level.SEVERE, "Exception in getting the payload identifier for profile list", (Throwable)e);
            throw e;
        }
        return profileList;
    }
    
    private boolean checkPayloadIdentifier(final String payloadIdentifier) {
        final String regex = "com.mdm.[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}.*";
        final Pattern pattern = Pattern.compile(regex, 2);
        final Matcher matcher = pattern.matcher(payloadIdentifier);
        return matcher.matches();
    }
    
    private void updateCustomerParam(final List<String> profileList, final Long customerId) {
        try {
            IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "Going to update the customer params for managed profile list. ProfileId:{0}&customer:{1}", new Object[] { profileList, customerId });
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
            selectQuery.addJoin(new Join("Resource", "ResourceToConfigProfiles", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ResourceToConfigProfiles", "IOSConfigPayload", new String[] { "PROFILE_PAYLOAD_ID" }, new String[] { "PAYLOAD_ID" }, 2));
            selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("Resource", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria defaultProfile = new Criteria(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"), (Object)new String[] { "com.mdm.DCAppCatalog", "com.zohocorp.mdm", "com.mdm.osupdate.restriction", "com.mdm.kiosk_default_mdm_app", "com.mdm.kiosk_install_profile", "com.mdm.MDMBlacklistApps" }, 9);
            final Criteria installedSource = new Criteria(new Column("ResourceToConfigProfiles", "INSTALLED_SOURCE"), (Object)IOSConfigPayloadDataHandler.MANAGED_CONFIG_PROFILE, 0);
            final Criteria customerIdCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria managedDevice = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            Hashtable ht = new Hashtable();
            ht = DateTimeUtil.determine_From_To_Times("today");
            final Long today = ht.get("date1");
            final Criteria contactTime = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)(today - IOSRemoveMangedProfileHandler.DAY * 24 * 60 * 60 * 1000L), 5);
            Criteria finalCriteria = defaultProfile.and(installedSource).and(customerIdCriteria).and(managedDevice).and(contactTime);
            if (profileList.size() > 0) {
                finalCriteria = finalCriteria.and(new Criteria(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"), (Object)profileList.toArray(), 9));
            }
            selectQuery.setCriteria(finalCriteria);
            selectQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("ResourceToConfigProfiles", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("ResourceToConfigProfiles", "PROFILE_PAYLOAD_ID"));
            selectQuery.addSelectColumn(new Column("IOSConfigPayload", "PAYLOAD_ID"));
            selectQuery.addSelectColumn(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject.isEmpty()) {
                IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "No managed profile is installed and not removed");
                final String addedTime = CustomerParamsHandler.getInstance().getParameterValue("DELETE_NOTMANAGED_PROFILELIST_TIME", (long)customerId);
                final SelectQuery profileListAddedQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
                profileListAddedQuery.addJoin(new Join("Resource", "MdCommandsToDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                profileListAddedQuery.addJoin(new Join("MdCommandsToDevice", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
                profileListAddedQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                profileListAddedQuery.addJoin(new Join("Resource", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                final Criteria addedTimeCriteria = new Criteria(new Column("MdCommandsToDevice", "ADDED_AT"), (Object)addedTime, 6);
                final Criteria commandNameCriteria = new Criteria(new Column("MdCommands", "COMMAND_TYPE"), (Object)"ProfileList", 0);
                final Criteria finalCommandCriteria = addedTimeCriteria.and(commandNameCriteria).and(customerIdCriteria).and(contactTime).and(managedDevice);
                profileListAddedQuery.setCriteria(finalCommandCriteria);
                profileListAddedQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
                final DataObject dataObject2 = MDMUtil.getPersistenceLite().get(profileListAddedQuery);
                if (dataObject2.isEmpty()) {
                    IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "No profilelist command for the active devices");
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("DELETE_NOTMANAGED_PROFILELIST", "false", (long)customerId);
                }
                else {
                    IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "Some resources are there to delete the managed profile.ProfileList not sent.");
                }
            }
            else {
                IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "There are some resource with other payloads.");
                final Iterator iterator = dataObject.getRows("IOSConfigPayload");
                boolean isNotManagedProfileAvailable = false;
                while (iterator.hasNext()) {
                    final Row configRow = iterator.next();
                    final String payloadIdentifier = (String)configRow.get("PAYLOAD_IDENTIFIER");
                    IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "Checking for payloadIdentifier:{0}", new Object[] { payloadIdentifier });
                    if (this.checkPayloadIdentifier(payloadIdentifier) && !this.isPayloadMatches(profileList, payloadIdentifier)) {
                        isNotManagedProfileAvailable = true;
                        break;
                    }
                }
                if (!isNotManagedProfileAvailable) {
                    IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "NO unmanaged profile available so turning off");
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("DELETE_NOTMANAGED_PROFILELIST", "false", (long)customerId);
                }
            }
        }
        catch (final DataAccessException e) {
            IOSRemoveMangedProfileHandler.LOGGER.log(Level.SEVERE, "Exception while getting result in profileList", (Throwable)e);
        }
        catch (final Exception e2) {
            IOSRemoveMangedProfileHandler.LOGGER.log(Level.SEVERE, "Exception while updating the params for managed profile list", e2);
        }
    }
    
    private List<String> checkAnyOtherPayloadIdentifierNeeded(final Iterator iterator) {
        final List<String> payloadIdentifier = new ArrayList<String>();
        while (iterator.hasNext()) {
            final Row payloadRow = iterator.next();
            final String payloadType = (String)payloadRow.get("PAYLOAD_TYPE");
            if (payloadType.equalsIgnoreCase("com.apple.app.lock")) {
                payloadIdentifier.add("com.mdm.kiosk_install_profile");
            }
        }
        return payloadIdentifier;
    }
    
    public void updateRemovePayloadParams() {
        try {
            final Long[] customerDetails = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            IOSRemoveMangedProfileHandler.LOGGER.log(Level.INFO, "Checking for each customer to turn off remove profile.CustomerId:{0}", new Object[] { Arrays.asList(customerDetails) });
            if (customerDetails != null) {
                for (int i = 0; i < customerDetails.length; ++i) {
                    final Long customerId = customerDetails[i];
                    final String params = CustomerParamsHandler.getInstance().getParameterValue("DELETE_NOTMANAGED_PROFILELIST", (long)customerId);
                    if (!MDMStringUtils.isEmpty(params) && Boolean.valueOf(params)) {
                        final List<String> profileList = this.getProfileList(customerId);
                        this.updateCustomerParam(profileList, customerId);
                    }
                }
            }
        }
        catch (final Exception e) {
            IOSRemoveMangedProfileHandler.LOGGER.log(Level.SEVERE, "Exception in handling the remove payload params", e);
        }
    }
    
    private boolean isPayloadMatches(final List<String> profileList, final String payloadIdentifier) {
        boolean isPayloadMatches = false;
        for (final String profileIdentifier : profileList) {
            if (payloadIdentifier.contains(profileIdentifier)) {
                isPayloadMatches = true;
                break;
            }
        }
        return isPayloadMatches;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
        DAY = 90;
    }
}
