package com.me.mdm.directory.sync.mdm;

import java.util.Hashtable;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.config.ResourceSummaryHandler;
import java.sql.Connection;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.factory.MDMAuthTokenUtilAPI;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.conditionalaccess.AzureWinCEA;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.idps.core.util.DirectoryUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONException;
import java.util.List;
import com.me.idps.core.api.IdpsAPIException;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.HashMap;
import com.adventnet.ds.query.Column;
import com.me.idps.mdm.sync.MDMIdpsUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.Properties;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.me.idps.mdm.sync.MdmIdpsProdImpl;

public abstract class MDMDirectoryProductImpl extends MdmIdpsProdImpl
{
    private void handleMemberRelChange(final DirProdImplRequest dirProdImplRequest, final int eventType) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final Long groupResID = (Long)dirProdImplRequest.args[0];
        final Long[] membersResIDs = (Long[])dirProdImplRequest.args[1];
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final Properties props = new Properties();
        ((Hashtable<String, Boolean>)props).put("noGroupCountUpdate", Boolean.TRUE);
        final MDMGroupMemberEvent groupEvent = new MDMGroupMemberEvent(groupResID, membersResIDs);
        groupEvent.groupProp = props;
        groupEvent.userId = aaaUserID;
        groupEvent.groupType = 7;
        groupEvent.customerId = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
        MDMGroupHandler.getInstance().invokeGroupMemberListener(groupEvent, eventType);
    }
    
    public void memberAdded(final DirProdImplRequest dirProdImplRequest) {
        this.handleMemberRelChange(dirProdImplRequest, 1);
    }
    
    public void memberRemoved(final DirProdImplRequest dirProdImplRequest) {
        this.handleMemberRelChange(dirProdImplRequest, 2);
    }
    
    private JSONObject formatModifiedFields(final int resType, final JSONObject modifiedUserFields) {
        final JSONArray modifiedCol = new JSONArray();
        final HashMap<Long, Column> attrColMap = MDMIdpsUtil.getObjAttrColMap(resType);
        final JSONArray attrIDmodifiedFields = (JSONArray)modifiedUserFields.get((Object)"MODIFIED_FIELDS");
        for (int i = 0; i < attrIDmodifiedFields.size(); ++i) {
            Column modifiedColObj = null;
            final Long attrID = Long.valueOf(String.valueOf(attrIDmodifiedFields.get(i)));
            if (attrID == 2L) {
                modifiedColObj = Column.getColumn("Resource", "NAME");
            }
            else {
                modifiedColObj = attrColMap.get(attrID);
            }
            if (modifiedColObj != null) {
                final String modifiedColumnName = modifiedColObj.getColumnName();
                modifiedCol.add((Object)modifiedColumnName);
            }
        }
        modifiedUserFields.put((Object)"MODIFIED_FIELDS", (Object)modifiedCol);
        return modifiedUserFields;
    }
    
    public void userModified(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Long resID = (Long)dirProdImplRequest.args[0];
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final JSONObject modifiedUserFields = (JSONObject)dirProdImplRequest.args[1];
        final String dmDomainName = dmDomainProps.getProperty("NAME");
        final Long customerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
        ManagedUserHandler.getInstance().invokeUserListeners(dmDomainName, resID, this.formatModifiedFields(2, modifiedUserFields), customerID, aaaUserID, userName, 3);
    }
    
    public void groupModified(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Long resID = (Long)dirProdImplRequest.args[0];
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final JSONObject modifiedGroupFields = (JSONObject)dirProdImplRequest.args[1];
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} resource id of {1} : group modified", new Object[] { resID, dmDomainProps });
    }
    
    public void userDeleted(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final ArrayList<Long> resIDs = (ArrayList<Long>)dirProdImplRequest.args[0];
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} resource id of {1} : user Deleted", new Object[] { resIDs.toString(), dmDomainProps });
    }
    
    public void userActivated(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final ArrayList<Long> resIDs = (ArrayList<Long>)dirProdImplRequest.args[0];
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} resource id of {1} : user Activated", new Object[] { resIDs.toString(), dmDomainProps });
    }
    
    public void userDirDisabled(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final ArrayList<Long> resIDs = (ArrayList<Long>)dirProdImplRequest.args[0];
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} resource id of {1} : user Dir Disabled", new Object[] { resIDs.toString(), dmDomainProps });
    }
    
    public void userSyncDisabled(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final ArrayList<Long> resIDs = (ArrayList<Long>)dirProdImplRequest.args[0];
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} resource id of {1} : user sync disabled", new Object[] { resIDs.toString(), dmDomainProps });
    }
    
    public void groupDeleted(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final ArrayList<Long> resIDs = (ArrayList<Long>)dirProdImplRequest.args[0];
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} resource id of {1} : group deleted", new Object[] { resIDs.toString(), dmDomainProps });
    }
    
    public void groupActivated(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final ArrayList<Long> resIDs = (ArrayList<Long>)dirProdImplRequest.args[0];
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} resource id of {1} : group activated", new Object[] { resIDs.toString(), dmDomainProps });
    }
    
    public void groupDirDisabled(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final ArrayList<Long> resIDs = (ArrayList<Long>)dirProdImplRequest.args[0];
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} resource id of {1} : group dir disabled", new Object[] { resIDs.toString(), dmDomainProps });
    }
    
    public void groupSyncDisabled(final DirProdImplRequest dirProdImplRequest) {
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final String userName = dirProdImplRequest.userName;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final ArrayList<Long> resIDs = (ArrayList<Long>)dirProdImplRequest.args[0];
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} resource id of {1} : group sync disabled", new Object[] { resIDs.toString(), dmDomainProps });
    }
    
    private org.json.JSONObject checkDomainDelete(final Long dmDomainID, final String domainName, final Long customerId) {
        try {
            final org.json.JSONObject json = new org.json.JSONObject();
            if (domainName != null) {
                json.put("hasManagedUser", (Object)ManagedUserHandler.getInstance().hasManagedUserInDomain(dmDomainID));
                json.put("isSelfEnrollEnabled", EnrollmentSettingsHandler.getInstance().isSelfEnrollmentEnabled(customerId));
                json.put("isDEPSelfEnrollEnabled", DEPEnrollmentUtil.isADAuthenticationEnabledForAnyDEPServerForCustomer(customerId));
                json.put("isAzureCAEnabled", DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced(dmDomainID).contains(205));
                final int authMode = EnrollmentSettingsHandler.getInstance().getInvitationEnrollmentSettings(customerId).getInt("AUTH_MODE");
                if (authMode != 1) {
                    json.put("ADAuthUsed", true);
                }
                else {
                    json.put("ADAuthUsed", false);
                }
                final List ADDomainList = MDMEnrollmentUtil.getInstance().getDomainNames(customerId);
                final int count = ADDomainList.size();
                json.put("isLastManagedADDomain", count == 1);
                json.put("domName", (Object)domainName);
            }
            else {
                json.put("isADDomain", false);
            }
            return json;
        }
        catch (final Exception ex) {
            throw new IdpsAPIException("COM0004");
        }
    }
    
    private void isDomainCheckDelete(final org.json.JSONObject json) throws JSONException {
        if (json.getBoolean("hasManagedUser")) {
            throw new IdpsAPIException("AD001");
        }
        if (json.getBoolean("isSelfEnrollEnabled") && json.getBoolean("ADAuthUsed") && json.getBoolean("isLastManagedADDomain")) {
            throw new IdpsAPIException("AD002");
        }
        if (json.getBoolean("ADAuthUsed") && json.getBoolean("isLastManagedADDomain")) {
            throw new IdpsAPIException("AD003");
        }
        if (json.getBoolean("isDEPSelfEnrollEnabled") && json.getBoolean("isSelfEnrollEnabled") && json.getBoolean("isLastManagedADDomain")) {
            throw new IdpsAPIException("AD004");
        }
        if (json.getBoolean("isSelfEnrollEnabled") && json.getBoolean("isLastManagedADDomain")) {
            throw new IdpsAPIException("AD005");
        }
        if (json.getBoolean("isDEPSelfEnrollEnabled") && json.getBoolean("isLastManagedADDomain")) {
            throw new IdpsAPIException("AD006");
        }
        if (json.getBoolean("isAzureCAEnabled")) {
            throw new IdpsAPIException("AD018");
        }
    }
    
    private void deleteDomainDetails(final Properties domainProperties) {
        final String domainName = domainProperties.getProperty("NAME");
        final int domainType = ((Hashtable<K, Integer>)domainProperties).get("CLIENT_ID");
        final Long dmDomainID = ((Hashtable<K, Long>)domainProperties).get("DOMAIN_ID");
        final Long customerID = ((Hashtable<K, Long>)domainProperties).get("CUSTOMER_ID");
        if (domainType == 4) {
            throw new IdpsAPIException("AD007");
        }
        if (!SyMUtil.isStringValid(domainName)) {
            throw new IdpsAPIException("COM0014");
        }
        this.isDomainCheckDelete(this.checkDomainDelete(dmDomainID, domainName, customerID));
    }
    
    public void approveDomainDeletion(final DirProdImplRequest dirProdImplRequest) {
        this.deleteDomainDetails(dirProdImplRequest.dmDomainProps);
    }
    
    private JSONArray getAzuredomainUsersList(final JSONObject dmDomainProperties, final JSONArray domainUsersList) throws DataAccessException {
        final ArrayList<String> azureUPN = new ArrayList<String>();
        final Long dmDomainID = (Long)dmDomainProperties.get((Object)"DOMAIN_ID");
        final JSONArray newDomainUsersList = new JSONArray();
        for (int i = 0; i < domainUsersList.size(); ++i) {
            azureUPN.add((String)domainUsersList.get(i));
        }
        final SelectQuery validUPNquery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
        validUPNquery.addJoin(new Join("DirResRel", "DirObjRegStrVal", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
        validUPNquery.setCriteria(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)azureUPN.toArray(new String[azureUPN.size()]), 8, false)).and(new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)112L, 0)));
        validUPNquery.addSelectColumn(Column.getColumn("DirObjRegStrVal", "VALUE"));
        validUPNquery.addSelectColumn(Column.getColumn("DirObjRegStrVal", "OBJ_ID"));
        validUPNquery.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ATTR_ID"));
        validUPNquery.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ADDED_AT"));
        validUPNquery.addSelectColumn(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"));
        final DataObject dobj = IdpsUtil.getPersistenceLite().get(validUPNquery);
        if (dobj != null && dobj.containsTable("DirObjRegStrVal")) {
            final Iterator itr = dobj.getRows("DirObjRegStrVal");
            while (itr != null && itr.hasNext()) {
                final Row row = itr.next();
                newDomainUsersList.add(row.get("VALUE"));
            }
        }
        return newDomainUsersList;
    }
    
    private void handleAzurePostSyncForMDM(final JSONObject dmDomainProperties, final Boolean isFullSync, final List<String> postSyncOPDetails) throws DataAccessException {
        if (postSyncOPDetails != null && postSyncOPDetails.size() > 0) {
            for (int i = 0; i < postSyncOPDetails.size(); ++i) {
                try {
                    final String postSyncOPdetailFilePath = postSyncOPDetails.get(i);
                    final JSONObject postSyncOPdetail = (JSONObject)DirectoryUtil.getInstance().readAndDeleteFile(postSyncOPdetailFilePath);
                    if (postSyncOPdetail != null) {
                        String taskType = null;
                        if (postSyncOPdetail.containsKey((Object)"TASK_TYPE")) {
                            taskType = String.valueOf(postSyncOPdetail.get((Object)"TASK_TYPE"));
                            if (!SyMUtil.isStringEmpty(taskType) && taskType.equals("BulkEnrollmentImportInfo")) {
                                JSONArray domainUsersList = null;
                                if (postSyncOPdetail.containsKey((Object)"EMAIL_ADDRESS")) {
                                    domainUsersList = (JSONArray)postSyncOPdetail.get((Object)"EMAIL_ADDRESS");
                                    if (domainUsersList != null && !domainUsersList.isEmpty()) {
                                        final JSONArray validAzureUPN = this.getAzuredomainUsersList(dmDomainProperties, domainUsersList);
                                        MDMApiFactoryProvider.getInvitationEnrollmentRequestListener().handleBulkImport(validAzureUPN);
                                    }
                                }
                            }
                        }
                    }
                }
                catch (final Exception ex) {
                    IDPSlogger.ERR.log(Level.SEVERE, "product specific post sync call back failed : ", ex);
                }
            }
        }
        final List<Integer> syncObjects = DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced((Long)dmDomainProperties.get((Object)"DOMAIN_ID"));
        if (syncObjects.contains(205)) {
            AzureWinCEA.getInstance();
            AzureWinCEA.updateMangedDeviceRel((Long)dmDomainProperties.get((Object)"DOMAIN_ID"));
        }
    }
    
    private void handleGsuitePostSyncForMDM(final JSONObject dmDomainProperties) throws Exception {
        final int count = DBUtil.getRecordCount("MdDeviceRecentUsersInfo", "USER_ID", (Criteria)null);
        if (count == 0) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)new Integer[] { 4 }, 8));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            final DataObject dobj = IdpsUtil.getPersistenceLite().get(selectQuery);
            DeviceInvCommandHandler.getInstance().scanDevice(DBUtil.getColumnValuesAsList(dobj.getRows("ManagedDevice"), "RESOURCE_ID"), DMDomainSyncDetailsDataHandler.getInstance().getSyncIntiatedByUserID((Long)dmDomainProperties.get((Object)"DOMAIN_ID")));
        }
    }
    
    private void handlePostSyncOps(final JSONObject dmDomainProperties, final Boolean isFullSync, final List<String> postSyncOPdetails) throws Exception {
        final int clientID = Integer.valueOf(String.valueOf(dmDomainProperties.get((Object)"CLIENT_ID")));
        switch (clientID) {
            case 3: {
                this.handleAzurePostSyncForMDM(dmDomainProperties, isFullSync, postSyncOPdetails);
                break;
            }
            case 101: {
                this.handleGsuitePostSyncForMDM(dmDomainProperties);
                break;
            }
        }
    }
    
    public void postSyncOPS(final DirProdImplRequest dirProdImplRequest) throws Exception {
        final Boolean isFullSync = (Boolean)dirProdImplRequest.args[1];
        final List<String> postSyncOPdetails = (List<String>)dirProdImplRequest.args[2];
        final JSONObject dmDomainProperties = (JSONObject)dirProdImplRequest.args[0];
        this.handlePostSyncOps(dmDomainProperties, isFullSync, postSyncOPdetails);
    }
    
    public String getDefulatCustPhoneNumber(final DirProdImplRequest dirProdImplRequest) throws SyMException {
        String customerPhoneNumber = null;
        final List customerInfoList = CustomerInfoUtil.getInstance().getCustomerInfoList();
        final Properties customerProperties = customerInfoList.get(0);
        final Long customerID = ((Hashtable<K, Long>)customerProperties).get("CUSTOMER_ID");
        final String customerEmail = customerProperties.getProperty("CUSTOMER_EMAIL");
        try {
            customerPhoneNumber = String.valueOf(ManagedUserHandler.getInstance().getManagedUserDetailsForEmailAddress(customerEmail, customerID).get("PHONE_NUMBER"));
        }
        catch (final JSONException ex) {
            SyMLogger.log("MDMLogger", Level.WARNING, "Phone number for the customer is not available in ManagedUser table", (Throwable)ex);
        }
        if (customerPhoneNumber == null) {
            final MDMAuthTokenUtilAPI mdmAuthTokenUtilAPI = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI();
            customerPhoneNumber = mdmAuthTokenUtilAPI.getCustomerPhoneNumber(customerEmail);
        }
        return customerPhoneNumber;
    }
    
    public JSONObject getUserIDFdetails(final Long resID) throws Exception {
        JSONObject userIDFdetails = null;
        final Row managedUserRow = DBUtil.getRowFromDB("ManagedUser", "MANAGED_USER_ID", (Object)resID);
        if (managedUserRow != null) {
            userIDFdetails = new JSONObject();
            userIDFdetails.put((Object)"PHONE_NUMBER", managedUserRow.get("PHONE_NUMBER"));
            userIDFdetails.put((Object)"EMAIL_ADDRESS", managedUserRow.get("EMAIL_ADDRESS"));
        }
        return userIDFdetails;
    }
    
    public Boolean isFeatureAvailable(final String featureKey) {
        return MDMFeatureParamsHandler.getInstance().isFeatureEnabled(featureKey);
    }
    
    public void updateFeatureAvailability(final String featureKey, final String value) {
        MDMFeatureParamsHandler.updateMDMFeatureParameter(featureKey, value);
    }
    
    public int getDirectoryCGType() {
        return 7;
    }
    
    public int getDirectoryCGCategory() {
        return 1;
    }
    
    public void updateResSummary(final Connection connection) throws Exception {
        ResourceSummaryHandler.getInstance().updateResSummary(connection);
    }
    
    public void updateResSummary(final int resType, final boolean force) throws Exception {
        ResourceSummaryHandler.getInstance().updateResSummary(resType, force);
    }
    
    public void throwExcepForErrResp(final DirProdImplRequest dirProdImplRequest) {
        final String response = (String)dirProdImplRequest.args[0];
        if (response == null || response.equals("false")) {
            throw new IdpsAPIException("COM0004");
        }
        if (response.equalsIgnoreCase("Domain name already exists!")) {
            throw new IdpsAPIException("AD009");
        }
        if (response.equalsIgnoreCase("Invalid username/password")) {
            throw new IdpsAPIException("AD008");
        }
        if (response.equalsIgnoreCase("Unexpected error! Try adding the domain again. If issue persists, contact Support.")) {
            throw new IdpsAPIException("COM0014");
        }
        if (response.equalsIgnoreCase("Unable to connect to AD! Ensure MDM has proper network connectivity and retry. If issue persists, contact Support.")) {
            throw new IdpsAPIException("AD010");
        }
    }
    
    public ArrayList<String> getAutoVAdisabledTables(final DirProdImplRequest dirProdImplRequest) {
        return new ArrayList<String>(Arrays.asList("ResourceToProfileSummary".toUpperCase()));
    }
    
    public void handleUpgrade(final DirProdImplRequest dirProdImplRequest) throws Exception {
        super.handleUpgrade(dirProdImplRequest);
    }
}
