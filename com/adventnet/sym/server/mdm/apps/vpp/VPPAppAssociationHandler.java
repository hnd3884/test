package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import org.json.JSONObject;
import com.me.mdm.server.apps.ios.vpp.VPPAppAPIRequestHandler;
import com.me.mdm.server.apps.ios.vpp.VPPAPIRequestGenerator;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.Properties;
import java.util.List;
import java.util.logging.Logger;

public class VPPAppAssociationHandler
{
    public Logger logger;
    private static VPPAppAssociationHandler vppHandler;
    String className;
    
    public VPPAppAssociationHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
        this.className = VPPAppAssociationHandler.class.getName();
    }
    
    public static VPPAppAssociationHandler getInstance() {
        if (VPPAppAssociationHandler.vppHandler == null) {
            VPPAppAssociationHandler.vppHandler = new VPPAppAssociationHandler();
        }
        return VPPAppAssociationHandler.vppHandler;
    }
    
    public Properties associateVppApps(final List deviceList, final Long appGroupId, final int appStoreId, final Long collectionId, final Long customerId, final Long businessStoreID) throws Exception {
        final Properties finalDeviceProp = new Properties();
        Properties failedProp = new Properties();
        final Integer typeOfAssignment = VPPAppMgmtHandler.getInstance().getVppGlobalAssignmentType(customerId, businessStoreID);
        try {
            if (typeOfAssignment == 2) {
                this.logger.log(Level.INFO, "assignAppForDevices(): VPP: deviceBasedAppsResourceList {0} AppGroupId={1} AppStoreId={2} collectionId={3} customerId={4}", new Object[] { deviceList, appGroupId, appStoreId, collectionId, customerId });
                failedProp = this.associateAppsToDevice(deviceList, appGroupId, appStoreId, collectionId, customerId, businessStoreID);
            }
            else if (typeOfAssignment == 1) {
                this.logger.log(Level.INFO, "assignAppForDevices(): VPP: userBasedAppsResourceList {0}", deviceList);
                final Properties failedUserProp = this.convertUserIdToDeviceIdInMap(deviceList, this.associateAppsToUser(deviceList, appGroupId, appStoreId, collectionId, customerId, businessStoreID));
                final List failedUserLicenseList = new ArrayList();
                final Set resIdList = failedUserProp.keySet();
                for (final Long deviceId : resIdList) {
                    failedUserLicenseList.add(deviceId);
                }
                failedProp.putAll(failedUserProp);
                deviceList.removeAll(failedUserLicenseList);
                ((Hashtable<String, List>)finalDeviceProp).put("userLicensedDeviceList", deviceList);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in associateVppApps() ", ex);
        }
        ((Hashtable<String, Properties>)finalDeviceProp).put("FailedProp", failedProp);
        return finalDeviceProp;
    }
    
    public Properties associateAppsToDevice(final List<Long> deviceIdList, final Long appGroupId, final int appStoreId, final Long collectionId, final Long customerId, final Long businessStoreID) {
        Properties failedMap = new Properties();
        this.logger.log(Level.INFO, "Going to evaluate if license can be distributed to:{0} ,StoreID:{1},collectionId:{2} , resourceIDs:{3}", new Object[] { appGroupId, appStoreId, collectionId, deviceIdList });
        final List toBeAssociatedResList = VPPManagedDeviceHandler.getInstance().getlicenseNotAssociatedResList(deviceIdList, appGroupId, businessStoreID);
        if (!toBeAssociatedResList.isEmpty()) {
            failedMap = this.associateDevicesToApp(toBeAssociatedResList, appGroupId, appStoreId, customerId, businessStoreID);
        }
        else {
            this.logger.log(Level.INFO, "Not Proceeding to call VPP Associate license as list is empty fro AppGroupID:{0} ,StoreID:{1},collectionId:{2}", new Object[] { appGroupId, appStoreId, collectionId });
        }
        if (!failedMap.isEmpty() && this.checkIfAppHasInsufficientLicense(failedMap)) {
            this.logger.log(Level.INFO, "Insufficient licenses: {0}", new Object[] { failedMap });
            new VPPAssetsHandler().addOrUpdateAssetErrorDetails(businessStoreID, String.valueOf(appStoreId), null, null, 888804);
        }
        return failedMap;
    }
    
    private boolean checkIfAppHasInsufficientLicense(final Properties failedProp) {
        boolean ifLicenseNotAvailable = false;
        for (final Long resID : ((Hashtable<Object, V>)failedProp).keySet()) {
            final Properties errorProp = ((Hashtable<K, Properties>)failedProp).get(resID);
            if (errorProp.containsKey("ERROR_CODE") && ((Hashtable<K, Integer>)errorProp).get("ERROR_CODE") == 9610) {
                ifLicenseNotAvailable = true;
                break;
            }
        }
        return ifLicenseNotAvailable;
    }
    
    public Properties associateAppsToUser(final List<Long> deviceIdList, final Long appGroupId, final int appStoreId, final Long collectionId, final Long customerId, final Long businessStoreId) throws Exception {
        final List userResourceId = this.getUserResIdFromDeviceId(deviceIdList);
        this.logger.log(Level.INFO, " Associate user Id {0} to app store id {1} for businessStore {2}", new Object[] { userResourceId, appStoreId, businessStoreId });
        final List notRegisteredUserList = VPPManagedUserHandler.getInstance().registerUserList(userResourceId, customerId, businessStoreId);
        if (!notRegisteredUserList.isEmpty()) {
            this.sendInivitationCode(deviceIdList, notRegisteredUserList, businessStoreId);
        }
        final Properties failedMap = this.associateUsersToApp(userResourceId, appGroupId, appStoreId, customerId, businessStoreId);
        final ArrayList<Long> failedDevicelist = this.updateUserBasedAsgnmentFailedCollnStatus(deviceIdList, failedMap, collectionId);
        return failedMap;
    }
    
    private List<Long> getUserResIdFromDeviceId(final List<Long> deviceIdList) {
        final List<Long> userIdList = new ArrayList<Long>();
        try {
            final Criteria criteria = new Criteria(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceIdList.toArray(), 8);
            final List userIdListStr = DBUtil.getDistinctColumnValue("ManagedUserToDevice", "MANAGED_USER_ID", criteria);
            for (int i = 0; i < userIdListStr.size(); ++i) {
                userIdList.add(Long.parseLong(userIdListStr.get(i)));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getUserIdFromDeviceId", e);
        }
        return userIdList;
    }
    
    public void sendInivitationCode(final List<Long> deviceIdList, final List<Long> userResourceId, final Long businessStoreID) {
        final String methodName = "sendInivitationCode";
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedBusinessStore"));
            sQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreToVppRel", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            sQuery.addJoin(new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            sQuery.addJoin(new Join("MdVPPTokenDetails", "MdVppUser", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            sQuery.addJoin(new Join("MdVppUser", "MdManagedUserToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            sQuery.addJoin(new Join("MdManagedUserToVppUserRel", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            sQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            sQuery.addJoin(new Join("ManagedUserToDevice", "MdDeviceInfo", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria cManagedStatus = new Criteria(new Column("MdVppUser", "MANAGED_STATUS"), (Object)1, 0);
            final Criteria cUserid = new Criteria(new Column("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)userResourceId.toArray(), 8);
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
            Criteria criteria = cUserid.and(cManagedStatus).and(userNotInTrashCriteria).and(businessStoreCriteria);
            if (!deviceIdList.isEmpty()) {
                final Criteria cDevice = new Criteria(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceIdList.toArray(), 8);
                criteria = criteria.and(cDevice);
            }
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "*"));
            sQuery.addSelectColumn(Column.getColumn("ManagedUser", "*"));
            sQuery.addSelectColumn(Column.getColumn("Resource", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdVppUser", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final List needtoInviteList = new ArrayList();
                final Criteria os4CategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
                final Criteria os5CategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
                final Criteria os6CategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
                final Criteria os7criteria = os4CategoryCriteria.and(os5CategoryCriteria).and(os6CategoryCriteria);
                final Iterator os7item = DO.getRows("ManagedUserToDevice", os7criteria);
                while (os7item.hasNext()) {
                    final Row relRow = os7item.next();
                    needtoInviteList.add(relRow.get("MANAGED_DEVICE_ID"));
                }
                if (!needtoInviteList.isEmpty()) {
                    SyMLogger.info(this.logger, this.className, methodName, " Adding invite to program command to following device " + needtoInviteList);
                    final DeviceCommandRepository deviceCommandRepository = new DeviceCommandRepository();
                    final String commandUUID = "InviteToProgram;BusinessStore=" + businessStoreID;
                    final String commandType = "InviteToProgram";
                    final Long commandID = deviceCommandRepository.addCommand(commandUUID, commandType);
                    final Iterator resItr = needtoInviteList.iterator();
                    final HashMap<Long, ArrayList<Long>> deviceToCommandMap = new HashMap<Long, ArrayList<Long>>();
                    while (resItr.hasNext()) {
                        final Long resID = resItr.next();
                        final ArrayList<Long> tempCmdList = new ArrayList<Long>();
                        tempCmdList.add(commandID);
                        deviceToCommandMap.put(resID, tempCmdList);
                    }
                    deviceCommandRepository.assignCommandToDevices(deviceToCommandMap, 1);
                    NotificationHandler.getInstance().SendNotification(needtoInviteList, 1);
                }
                final List sendMailList = new ArrayList();
                final Criteria os4Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 2);
                final Criteria os5Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 2);
                final Criteria os6Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 2);
                final Criteria oscriteria = os4Criteria.or(os5Criteria).or(os6Criteria);
                final Iterator item = DO.getRows("ManagedUserToDevice", oscriteria);
                while (item.hasNext()) {
                    final Row relRow2 = item.next();
                    sendMailList.add(relRow2.get("MANAGED_DEVICE_ID"));
                }
                if (!sendMailList.isEmpty()) {
                    SyMLogger.info(this.logger, this.className, methodName, " Adding email to following device " + sendMailList);
                    for (final Long deviceId : sendMailList) {
                        final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.logger);
                        final Properties appDistributionProperties = new Properties();
                        final Criteria cDeviceId = new Criteria(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceId, 0);
                        final Long managedUserId = (Long)DO.getValue("ManagedUserToDevice", "MANAGED_USER_ID", cDeviceId);
                        ((Hashtable<String, Object>)appDistributionProperties).put("$user_emailid$", DO.getValue("ManagedUser", "EMAIL_ADDRESS", new Criteria(new Column("ManagedUser", "MANAGED_USER_ID"), (Object)managedUserId, 0)));
                        ((Hashtable<String, Object>)appDistributionProperties).put("$user_name$", DO.getValue("Resource", "NAME", new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)managedUserId, 0)));
                        final String invitationUrl = (String)DO.getValue("MdVppUser", "INVITATION_URL", new Criteria(new Column("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)managedUserId, 0));
                        ((Hashtable<String, String>)appDistributionProperties).put("$invitation_url$", invitationUrl);
                        final Long customerId = (Long)DO.getValue("Resource", "CUSTOMER_ID", new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)managedUserId, 0));
                        mailGenerator.sendMail(MDMAlertConstants.VPP_INVITATION_MAIL, "MDM", customerId, appDistributionProperties);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while sending invitation code ", e);
        }
    }
    
    private Properties associateUsersToApp(final List<Long> userResourceId, final Long appGroupId, final int appStoreId, final Long customerId, final Long businessStoreID) {
        final String methodName = "associateUsersToApp";
        final List removedUserList = this.removeAlreadyAssociatedUser(userResourceId, appStoreId, businessStoreID);
        if (!removedUserList.isEmpty()) {
            this.logger.log(Level.INFO, "User resources {0} are already associated with the app(AppGroupId) : {1}", new Object[] { removedUserList, appGroupId });
        }
        final HashMap resultHash = this.associateUsersToLicense(removedUserList, appStoreId, customerId, businessStoreID);
        SyMLogger.info(this.logger, this.className, methodName, "Associate VPP User to license result " + resultHash);
        final ArrayList successUserIDList = resultHash.get("SUCCESS_LIST");
        final Properties failedProp = resultHash.get("FAILED_LIST");
        if (!successUserIDList.isEmpty()) {
            new VPPAppLicenseHandler().addOrUpdateVPPLicenseToUser(successUserIDList, appStoreId, businessStoreID);
        }
        return failedProp;
    }
    
    private Properties associateDevicesToApp(final List<Long> deviceResourceId, final Long appGroupId, final int appStoreId, final Long customerId, final Long businessStoreID) {
        final String methodName = "associateDevicesToApp";
        final HashMap resultHash = this.associateDevicesToLicense(deviceResourceId, appStoreId, customerId, businessStoreID);
        SyMLogger.info(this.logger, this.className, methodName, "Associate VPP Device to license result " + resultHash);
        final ArrayList<String> successProp = resultHash.get("SUCCESS_LIST");
        final Properties failedProp = resultHash.get("FAILED_LIST");
        new VPPAppLicenseHandler().addOrUpdateVPPLicenseToDevice(successProp, appStoreId, businessStoreID, customerId);
        return failedProp;
    }
    
    private List removeAlreadyAssociatedUser(final List<Long> userResourceId, final int appStoreId, final Long businessStoreID) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedBusinessStore"));
            sQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreToVppRel", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            sQuery.addJoin(new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            sQuery.addJoin(new Join("MdVPPTokenDetails", "MdVppAsset", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            sQuery.addJoin(new Join("MdVppAsset", "MdVppAssetToVppUserRel", new String[] { "VPP_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            sQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdManagedUserToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("MdManagedUserToVppUserRel", "*"));
            final Criteria cUserId = new Criteria(new Column("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)userResourceId.toArray(), 8);
            final Criteria cAppStoreId = new Criteria(new Column("MdVppAsset", "ADAM_ID"), (Object)appStoreId, 0);
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            sQuery.setCriteria(cUserId.and(cAppStoreId).and(businessStoreCriteria));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("MdManagedUserToVppUserRel");
                while (item.hasNext()) {
                    final Row userRow = item.next();
                    userResourceId.remove(userRow.get("MANAGED_USER_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while removing already associated user", e);
        }
        return userResourceId;
    }
    
    private HashMap associateUsersToLicense(final List removedUserList, final int appStoreId, final Long customerId, final Long businessStoreID) {
        HashMap associateMap = new HashMap();
        final Properties associatedProp = new Properties();
        final Properties failedProp = new Properties();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdManagedUserToVppUserRel"));
            selectQuery.addJoin(new Join("MdManagedUserToVppUserRel", "MdVppUser", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppUser", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            final Criteria businessStoreCrit = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria managedUsersCrit = new Criteria(Column.getColumn("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)removedUserList.toArray(), 8);
            selectQuery.setCriteria(businessStoreCrit.and(managedUsersCrit));
            selectQuery.addSelectColumn(Column.getColumn("MdManagedUserToVppUserRel", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_CLIENT_USER_ID"));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            final HashMap clientIdToUserIdMap = new HashMap();
            final Iterator vppUserInfoIter = DO.getRows("MdVppUser");
            final Iterator userInfoIter = DO.getRows("MdManagedUserToVppUserRel");
            while (userInfoIter.hasNext()) {
                final Row vppUserRow = vppUserInfoIter.next();
                final Row userInfoRow = userInfoIter.next();
                final Long userId = (Long)userInfoRow.get("MANAGED_USER_ID");
                final String clientUserId = (String)vppUserRow.get("VPP_CLIENT_USER_ID");
                clientIdToUserIdMap.put(clientUserId, userId);
            }
            final List clientUserIdsList = DBUtil.getColumnValuesAsList(DO.getRows("MdVppUser"), "VPP_CLIENT_USER_ID");
            associateMap = this.associateLicense(clientUserIdsList, appStoreId, customerId, clientIdToUserIdMap, 1, businessStoreID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting associate user to license ", e);
        }
        return associateMap;
    }
    
    private Properties associateFailedLicensesWithErrorCode(final Properties prop, final Long id) {
        Integer errorNumber = -1;
        String errorMessage = null;
        final Properties failedProp = new Properties();
        final Properties failedProperty = new Properties();
        try {
            errorNumber = Integer.parseInt(((Hashtable<K, Object>)prop).get("errorNumber").toString());
            errorMessage = ((Hashtable<K, Object>)prop).get("errorMessage").toString();
            if (errorNumber == 9602) {
                errorMessage = "mdm.vpp.sync.failureCommonMessage";
            }
            if (errorNumber == 9610) {
                final String helpUrl = "/help/app_management/ios_app_management.html?$(traceurl)&pgSrc=$(pageSource)#Purchasing_apps_in_vpp";
                errorMessage = "dc.mdm.vpp.app_with_no_license@@@<a target='blank' href=\"$(mdmUrl)" + helpUrl + "\">@@@</a>";
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in associateFailedLicensesWithErrorCode", ex);
        }
        if (errorNumber != 9616) {
            ((Hashtable<String, String>)failedProperty).put("REMARKS", errorMessage);
            ((Hashtable<String, Integer>)failedProperty).put("ERROR_CODE", errorNumber);
        }
        return failedProperty;
    }
    
    private Properties associateUser(final List userIdList, final int appStoreId, final Long customerId, final Long businessStoreID) {
        Properties prop = new Properties();
        try {
            final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
            final String command = new VPPAPIRequestGenerator(sToken).getAssociateAdamIdToUsersCommand(customerId, appStoreId, "STDQ", userIdList);
            final String dummyCommand = command.replace(sToken, "*****");
            this.logger.log(Level.INFO, "Request for manageVPPLicensesByAdamIdSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "manageVPPLicensesByAdamIdSrvUrl", sToken, businessStoreID);
            this.logger.log(Level.INFO, "Response for manageVPPLicensesByAdamIdSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
            prop = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "manageVPPLicensesByAdamIdSrvUrl");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while sending associate user srv", e);
        }
        return prop;
    }
    
    private HashMap associateLicense(final List entitiesList, final int appStoreId, final Long customerId, final HashMap entitiesMap, final int typeOfAssignment, final Long businessStoreID) {
        final HashMap associateMap = new HashMap();
        final ArrayList<Long> associatedList = new ArrayList<Long>();
        final Properties failedProp = new Properties();
        try {
            for (int lastIndex = 0, firstIndex = 0; firstIndex < entitiesList.size(); firstIndex = lastIndex) {
                VPPServiceConfigHandler.getInstance().checkAndFetchServiceUrl();
                final String maxLicenseAssCountStr = VPPAppAPIRequestHandler.getInstance().getServiceUrl("maxBatchAssociateLicenseCount");
                final Integer maxLicenseAssociateCount = (maxLicenseAssCountStr != null) ? Integer.parseInt(maxLicenseAssCountStr) : 1;
                lastIndex = firstIndex + maxLicenseAssociateCount;
                final List subEntitiesList = (lastIndex > entitiesList.size()) ? entitiesList.subList(firstIndex, entitiesList.size()) : entitiesList.subList(firstIndex, lastIndex);
                Properties prop = new Properties();
                String entiryUsedForLicenseAssignment = "";
                if (typeOfAssignment == 2) {
                    prop = this.associateDevice(subEntitiesList, appStoreId, customerId, businessStoreID);
                    entiryUsedForLicenseAssignment = "serialNumber";
                }
                else {
                    prop = this.associateUser(subEntitiesList, appStoreId, customerId, businessStoreID);
                    entiryUsedForLicenseAssignment = "clientUserIdStr";
                }
                if (!prop.containsKey("associations")) {
                    for (int failLicIndex = firstIndex; failLicIndex < entitiesList.size(); ++failLicIndex) {
                        final String valueOfTheEntity = entitiesList.get(failLicIndex);
                        final Long resourceId = entitiesMap.get(valueOfTheEntity);
                        final Properties licErrorProp = this.associateFailedLicensesWithErrorCode(prop, resourceId);
                        if (!licErrorProp.isEmpty()) {
                            ((Hashtable<Long, Properties>)failedProp).put(resourceId, licErrorProp);
                        }
                    }
                    break;
                }
                for (int index = 0; index < ((Hashtable<K, List>)prop).get("associations").size(); ++index) {
                    final Properties associtedDevicePropList = ((Hashtable<K, List<Properties>>)prop).get("associations").get(index);
                    final String valueOfTheEntity2 = ((Hashtable<K, String>)associtedDevicePropList).get(entiryUsedForLicenseAssignment);
                    final String vpplicenseId = ((Hashtable<K, String>)associtedDevicePropList).get("licenseIdStr");
                    final Long resourceId2 = entitiesMap.get(valueOfTheEntity2);
                    if (vpplicenseId != null) {
                        associatedList.add(resourceId2);
                    }
                    else {
                        final Properties licErrorProp2 = this.associateFailedLicensesWithErrorCode(associtedDevicePropList, resourceId2);
                        if (!licErrorProp2.isEmpty()) {
                            ((Hashtable<Long, Properties>)failedProp).put(resourceId2, licErrorProp2);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting associate user to license ", e);
        }
        associateMap.put("SUCCESS_LIST", associatedList);
        associateMap.put("FAILED_LIST", failedProp);
        return associateMap;
    }
    
    private HashMap associateDevicesToLicense(final List managedResourceList, final int appStoreId, final Long customerId, final Long businessStoreID) {
        HashMap associateMap = new HashMap();
        final Properties associatedProp = new Properties();
        final Properties failedProp = new Properties();
        try {
            final Criteria deviceIdCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)managedResourceList.toArray(), 8);
            final DataObject DO = MDMUtil.getPersistence().get("MdDeviceInfo", deviceIdCriteria);
            final HashMap serialNumberToResMap = new HashMap();
            final Iterator deviceInfoIter = DO.getRows("MdDeviceInfo");
            while (deviceInfoIter.hasNext()) {
                final Row deviceInfoRow = deviceInfoIter.next();
                final Long resourceId = (Long)deviceInfoRow.get("RESOURCE_ID");
                final String serialNumber = (String)deviceInfoRow.get("SERIAL_NUMBER");
                serialNumberToResMap.put(serialNumber, resourceId);
            }
            final List entireDeviceSerialNumberList = DBUtil.getColumnValuesAsList(DO.getRows("MdDeviceInfo"), "SERIAL_NUMBER");
            associateMap = this.associateLicense(entireDeviceSerialNumberList, appStoreId, customerId, serialNumberToResMap, 2, businessStoreID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting associate user to license ", e);
        }
        return associateMap;
    }
    
    private Properties associateDevice(final List deviceSerialList, final int appStoreId, final Long customerId, final Long businessStoreID) {
        Properties prop = new Properties();
        try {
            final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
            final String command = new VPPAPIRequestGenerator(sToken).getAssociateAdamIdToDevicesCommand(customerId, appStoreId, "STDQ", deviceSerialList);
            final String dummyCommand = command.replace(sToken, "*****");
            this.logger.log(Level.INFO, "Request for manageVPPLicensesByAdamIdSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "manageVPPLicensesByAdamIdSrvUrl", sToken, businessStoreID);
            this.logger.log(Level.INFO, "Response for manageVPPLicensesByAdamIdSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
            prop = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "manageVPPLicensesByAdamIdSrvUrl");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while sending associate device srv", e);
        }
        return prop;
    }
    
    private Properties convertUserIdToDeviceIdInMap(final List deviceIdList, final Properties failedMap) {
        final Properties failedProp = new Properties();
        try {
            if (!failedMap.isEmpty()) {
                final Set userIdList = failedMap.keySet();
                final Criteria cUser = new Criteria(new Column("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userIdList.toArray(), 8);
                final Criteria cDevice = new Criteria(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceIdList.toArray(), 8);
                final DataObject DO = MDMUtil.getPersistence().get("ManagedUserToDevice", cUser.and(cDevice));
                final Iterator userRows = DO.getRows("ManagedUserToDevice");
                while (userRows.hasNext()) {
                    final Row relRow = userRows.next();
                    final Long userId = (Long)relRow.get("MANAGED_USER_ID");
                    final Long deviceId = (Long)relRow.get("MANAGED_DEVICE_ID");
                    final Properties prop = ((Hashtable<K, Properties>)failedMap).get(userId);
                    ((Hashtable<Long, Properties>)failedProp).put(deviceId, prop);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in convertUserIdToDeviceIdInMad {0}", e);
        }
        return failedProp;
    }
    
    private ArrayList<Long> updateUserBasedAsgnmentFailedCollnStatus(final List deviceIdList, final Properties failedMap, final Long collectionId) {
        final ArrayList<Long> failedDeviceid = new ArrayList<Long>();
        try {
            if (!failedMap.isEmpty()) {
                final Set userIdList = failedMap.keySet();
                final Criteria cUser = new Criteria(new Column("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userIdList.toArray(), 8);
                final Criteria cDevice = new Criteria(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceIdList.toArray(), 8);
                final DataObject DO = MDMUtil.getPersistence().get("ManagedUserToDevice", cUser.and(cDevice));
                final Iterator userRows = DO.getRows("ManagedUserToDevice");
                final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
                while (userRows.hasNext()) {
                    final Row relRow = userRows.next();
                    final Long userId = (Long)relRow.get("MANAGED_USER_ID");
                    final Long deviceId = (Long)relRow.get("MANAGED_DEVICE_ID");
                    failedDeviceid.add(deviceId);
                    final Properties prop = ((Hashtable<K, Properties>)failedMap).get(userId);
                    MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(deviceId, collectionId + "", 7, ((Hashtable<K, String>)prop).get("REMARKS"));
                    MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(deviceId, collectionId, null);
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return failedDeviceid;
    }
    
    public ArrayList<Long> updateDeviceBasedAsgnmentFailedCollnStatus(final Properties failedMap, final Long collectionId) {
        final ArrayList<Long> failedDeviceid = new ArrayList<Long>();
        try {
            if (!failedMap.isEmpty()) {
                final Set resIdList = failedMap.keySet();
                final Iterator resIdIterator = resIdList.iterator();
                final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
                while (resIdIterator.hasNext()) {
                    final Long deviceId = resIdIterator.next();
                    failedDeviceid.add(deviceId);
                    final Properties prop = ((Hashtable<K, Properties>)failedMap).get(deviceId);
                    MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(deviceId, collectionId + "", 7, ((Hashtable<K, String>)prop).get("REMARKS"));
                    final String errorCode = ((Hashtable<K, Object>)prop).get("ERROR_CODE").toString();
                    if (errorCode != null) {
                        MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(deviceId, collectionId, Integer.parseInt(errorCode));
                    }
                    else {
                        MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(deviceId, collectionId, null);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updateFailedCollectionStatus", e);
        }
        return failedDeviceid;
    }
    
    public JSONObject validatePreassociation(final List packageIDList, final List targetResourceList, final Long businessStoreID) {
        final JSONObject responseJSON = new JSONObject();
        boolean validateresult = true;
        this.logger.log(Level.INFO, "Going to check if PackageIDs [{0} ] is distributed to resourceIDs [ {1} ] with businessStoreID {2}", new Object[] { packageIDList, targetResourceList, businessStoreID });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppData"));
        selectQuery.addJoin(new Join("MdPackageToAppData", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria joinCriteria1 = new Criteria(new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 1);
        final Criteria joinCriteria2 = new Criteria(new Column("ProfileToCollection", "PROFILE_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), 0);
        final Criteria joinCriteria3 = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)targetResourceList.toArray(), 8);
        selectQuery.addJoin(new Join("ProfileToCollection", "MDMResourceToDeploymentConfigs", joinCriteria1.and(joinCriteria2).and(joinCriteria3), 2));
        selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppData", "PACKAGE_ID"), (Object)packageIDList.toArray(), 8));
        Column col = new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID");
        col = col.distinct();
        col = col.count();
        col.setColumnAlias("BUSINESSSTORE_ID");
        selectQuery.addSelectColumn(col);
        final Column deviceInstanceCol = Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID");
        final List<Column> gropByColumnList = new ArrayList<Column>();
        gropByColumnList.add(deviceInstanceCol);
        selectQuery.setGroupByClause(new GroupByClause((List)gropByColumnList));
        try {
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (ds.next()) {
                validateresult = false;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in validatePreassociation", ex);
        }
        responseJSON.put("validate_result", validateresult);
        this.logger.log(Level.INFO, "Validation Result for PackageIDs [{0} ] is distributed to resourceIDs [ {1} ] with businessStoreID {2} is {3}", new Object[] { packageIDList, targetResourceList, businessStoreID, responseJSON });
        return responseJSON;
    }
    
    public List getPreferedLocationListForResource(final List<Long> resourceIDs, final Boolean isGroup) {
        final List<Long> bsIDList = new ArrayList<Long>();
        final Criteria joinCriteria2 = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
        final Criteria notNullBSID = new Criteria(new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)null, 1);
        SelectQuery selectQuery;
        Criteria redIDCriteria;
        if (isGroup) {
            selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
            final Criteria joinCriteria3 = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), 0);
            redIDCriteria = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)resourceIDs.toArray(), 8);
            selectQuery.addJoin(new Join("RecentProfileForGroup", "MDMResourceToDeploymentConfigs", joinCriteria3.and(joinCriteria2).and(notNullBSID), 2));
        }
        else {
            selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            redIDCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            final Join latestDepJoin = MDBusinessStoreUtil.getLatestDeploymentConfigJoinForRecentProfileForResource();
            Criteria latDepJoinCri = latestDepJoin.getCriteria();
            latDepJoinCri = latDepJoinCri.and(joinCriteria2).and(notNullBSID);
            latestDepJoin.setCriteria(latDepJoinCri);
            selectQuery.addJoin(latestDepJoin);
        }
        Column selCol = new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID");
        selCol = selCol.distinct();
        selCol.setColumnAlias("BUSINESSSTORE_ID");
        selectQuery.setCriteria(redIDCriteria);
        selectQuery.addSelectColumn(selCol);
        try {
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final Long value = (Long)ds.getValue(1);
                if (value != null) {
                    bsIDList.add(value);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getPreferedLocationListForResource", ex);
        }
        this.logger.log(Level.INFO, "Prefered LocationIDs for resource  [{0} ] are {1}", new Object[] { resourceIDs, bsIDList });
        return bsIDList;
    }
    
    static {
        VPPAppAssociationHandler.vppHandler = null;
    }
}
