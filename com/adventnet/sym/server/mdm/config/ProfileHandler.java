package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.config.MDMCollectionUtil;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.Map;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.core.management.ManagementUtil;
import com.me.mdm.core.management.ManagementConstants;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.tracker.mics.MICSProfileFeatureController;
import java.util.UUID;
import com.adventnet.persistence.Row;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ProfileHandler
{
    public static Logger logger;
    public static final int MDM_PROFILE_TYPE = 1;
    public static final int MDM_APP_PROFILE_TYPE = 2;
    public static final int MDM_OS_UPDATE_PROFILE_TYPE = 3;
    public static final int MDM_APP_BLACKLIST_PROFILE = 4;
    public static final int MDM_COMPLIANCE_POLICY_TYPE = 5;
    public static final int MDM_PRE_ACTIVATION_PROFILE_TYPE = 6;
    public static final int MDM_DATA_USAGE_PROFILE_TYPE = 8;
    public static final int MDM_HIDDEN_APP_TYPE = 7;
    public static final int MDM_HIDDEN_CONFIG_TYPE = 9;
    public static final int MDM_APP_CONFIG_TYPE = 10;
    public static final int MDM_APP_UPDATE_POLICY = 12;
    public static final int STANDALONE_PROFILE = 0;
    public static final int PROFILE_SHARED_AMONG_CUSTOMERS = 1;
    
    public static Long addOrUpdateProfile(final JSONObject profileMap) throws SyMException {
        Long profileID = null;
        try {
            if (profileMap != null) {
                ProfileHandler.logger.log(Level.FINE, "ProfileHandler addOrUpdateProfile().. profileMap json = {0}", profileMap.toString());
                DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
                String profileName = profileMap.optString("PROFILE_NAME", (String)null);
                profileName = MDMStringUtils.trimStringLenght(profileName, 100);
                final String profileDescription = profileMap.optString("PROFILE_DESCRIPTION", (String)null);
                final Integer profileType = profileMap.optInt("PROFILE_TYPE", -1);
                if (profileType == 2) {
                    profileName = AppsUtil.getInstance().replaceSplCharsInAppName(profileName);
                }
                final Integer platformType = profileMap.optInt("PLATFORM_TYPE", -1);
                final Long createdBy = JSONUtil.optLong(profileMap, "CREATED_BY", -1L);
                Long modifiedBy = JSONUtil.optLong(profileMap, "LAST_MODIFIED_BY", -1L);
                final Integer scope = profileMap.optInt("SCOPE", 0);
                final Long customerID = JSONUtil.optLong(profileMap, "CUSTOMER_ID", -1L);
                final Boolean isMovedToTrash = profileMap.optBoolean("IS_MOVED_TO_TRASH", false);
                final Integer isForAllCustomers = profileMap.optInt("PROFILE_SHARED_SCOPE", 0);
                Long createdTime;
                final Long currentTime = createdTime = System.currentTimeMillis();
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget") && profileMap.has("CREATION_TIME")) {
                    createdTime = profileMap.getLong("CREATION_TIME");
                }
                Long updatedTime = currentTime;
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget") && profileMap.has("LAST_MODIFIED_TIME")) {
                    updatedTime = profileMap.getLong("LAST_MODIFIED_TIME");
                }
                String profileIdentifier = null;
                profileID = JSONUtil.optLong(profileMap, "PROFILE_ID", -1L);
                if (modifiedBy == null || modifiedBy == -1L) {
                    modifiedBy = createdBy;
                }
                if (profileID == null || profileID == -1L) {
                    Row profileRow = new Row("Profile");
                    profileRow.set("PROFILE_NAME", (Object)profileName);
                    profileRow.set("CREATED_BY", (Object)createdBy);
                    profileRow.set("PROFILE_TYPE", (Object)profileType);
                    profileRow.set("PLATFORM_TYPE", (Object)platformType);
                    profileRow.set("LAST_MODIFIED_BY", (Object)modifiedBy);
                    profileRow.set("PROFILE_DESCRIPTION", (Object)profileDescription);
                    profileRow.set("CREATION_TIME", (Object)createdTime);
                    profileRow.set("LAST_MODIFIED_TIME", (Object)updatedTime);
                    profileRow.set("PROFILE_SHARED_SCOPE", (Object)isForAllCustomers);
                    if (profileType == 1 || profileType == 9 || profileType == 12) {
                        if ((MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget") || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) && profileMap.has("PROFILE_PAYLOAD_IDENTIFIER")) {
                            profileIdentifier = profileMap.getString("PROFILE_PAYLOAD_IDENTIFIER");
                        }
                        else {
                            profileIdentifier = "com.mdm." + UUID.randomUUID().toString() + "." + profileName.replaceAll("[^a-zA-Z0-9]", "");
                        }
                        profileRow.set("PROFILE_PAYLOAD_IDENTIFIER", (Object)profileIdentifier);
                        profileRow.set("PROFILE_IDENTIFIER", (Object)"--");
                        profileRow.set("SCOPE", (Object)scope);
                    }
                    else {
                        profileRow.set("PROFILE_IDENTIFIER", (Object)profileName);
                        profileRow.set("PROFILE_PAYLOAD_IDENTIFIER", (Object)profileMap.optString("PROFILE_PAYLOAD_IDENTIFIER", (String)null));
                        profileRow.set("SCOPE", (Object)scope);
                    }
                    dataObject.addRow(profileRow);
                    final Row profileToCustomerRel = new Row("ProfileToCustomerRel");
                    profileToCustomerRel.set("PROFILE_ID", profileRow.get("PROFILE_ID"));
                    profileToCustomerRel.set("CUSTOMER_ID", (Object)customerID);
                    dataObject.addRow(profileToCustomerRel);
                    ProfileHandler.logger.log(Level.FINE, "DataObject  before insert...{0}", dataObject);
                    dataObject = MDMUtil.getPersistence().add(dataObject);
                    profileRow = dataObject.getRow("Profile");
                    if (profileRow != null) {
                        profileID = (Long)profileRow.get("PROFILE_ID");
                        if (profileType == 1) {
                            MICSProfileFeatureController.addTrackingData(platformType, MICSProfileFeatureController.ProfileOperation.CREATE);
                        }
                    }
                }
                else {
                    final Criteria criteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileID, 0);
                    dataObject = MDMUtil.getPersistence().get("Profile", criteria);
                    final Row profileRow2 = dataObject.getRow("Profile");
                    if (profileRow2 != null) {
                        if (profileName != null) {
                            profileRow2.set("PROFILE_NAME", (Object)profileName);
                        }
                        if (profileDescription != null) {
                            profileRow2.set("PROFILE_DESCRIPTION", (Object)profileDescription);
                        }
                        if (modifiedBy != null && modifiedBy != -1L) {
                            profileRow2.set("LAST_MODIFIED_BY", (Object)modifiedBy);
                        }
                        if (isMovedToTrash != null) {
                            profileRow2.set("IS_MOVED_TO_TRASH", (Object)isMovedToTrash);
                        }
                        profileRow2.set("LAST_MODIFIED_TIME", (Object)updatedTime);
                    }
                    dataObject.updateRow(profileRow2);
                    MDMUtil.getPersistence().update(dataObject);
                    if (profileType == 1) {
                        MICSProfileFeatureController.addTrackingData(platformType, MICSProfileFeatureController.ProfileOperation.EDIT);
                    }
                }
                if (profileID != null) {
                    ManagementUtil.addManagementType(1, profileID, profileMap.optInt("management_type", (int)ManagementConstants.Types.MOBILE_MGMT));
                }
            }
        }
        catch (final DataAccessException exp) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateProfile", (Throwable)exp);
            throw new SyMException(1002, exp.getCause());
        }
        catch (final Exception exp2) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateProfile", exp2);
            throw new SyMException(1002, exp2.getCause());
        }
        return profileID;
    }
    
    public static Long addOrUpdateProfileCollectionDO(final JSONObject profileMap) throws SyMException, Exception {
        Long collectionID = null;
        try {
            DataObject collectionDO = MDMUtil.getPersistence().constructDataObject();
            String profileName = (String)profileMap.get("PROFILE_NAME");
            profileName = MDMStringUtils.trimStringLenght(profileName, 100);
            final String profileDescription = (String)profileMap.get("PROFILE_DESCRIPTION");
            final Long customerID = (Long)profileMap.get("CUSTOMER_ID");
            final Long profileID = (Long)profileMap.get("PROFILE_ID");
            final Integer securityType = (Integer)profileMap.get("SECURITY_TYPE");
            final long time = System.currentTimeMillis();
            Row collectionRow = new Row("Collection");
            collectionRow.set("COLLECTION_NAME", (Object)profileName);
            collectionRow.set("DESCRIPTION", (Object)profileDescription);
            collectionRow.set("CREATION_TIME", (Object)time);
            collectionRow.set("MODIFIED_TIME", (Object)time);
            collectionRow.set("IS_SINGLE_CONFIG", (Object)Boolean.FALSE);
            collectionRow.set("COLLECTION_TYPE", (Object)new Integer(3));
            collectionRow.set("IS_CONFIG_COLLECTION", (Object)Boolean.FALSE);
            collectionRow.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
            final Row collectionStatus = new Row("CollectionStatus");
            collectionStatus.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
            collectionStatus.set("STATUS", (Object)new Integer(1));
            collectionStatus.set("PROFILE_COLLECTION_STATUS", (Object)new Integer(1));
            collectionStatus.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
            final Row iOSCollectionPayload = new Row("IOSCollectionPayload");
            iOSCollectionPayload.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
            iOSCollectionPayload.set("SECURITY_TYPE", (Object)securityType);
            iOSCollectionPayload.set("PAYLOAD_UUID", (Object)profileName);
            final Row collnToCustomer = new Row("CollnToCustomerRel");
            if (customerID != null) {
                collnToCustomer.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
                collnToCustomer.set("CUSTOMER_ID", (Object)new Long(customerID));
                collnToCustomer.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
            }
            final Row profileToCollectionRel = new Row("ProfileToCollection");
            profileToCollectionRel.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
            profileToCollectionRel.set("PROFILE_ID", (Object)profileID);
            final Criteria criteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileID, 0);
            final Integer maxValue = (Integer)DBUtil.getMaxOfValue("ProfileToCollection", "PROFILE_VERSION", criteria);
            if (maxValue == null) {
                profileToCollectionRel.set("PROFILE_VERSION", (Object)new Integer(1));
            }
            else {
                profileToCollectionRel.set("PROFILE_VERSION", (Object)(maxValue + 1));
            }
            collectionDO.addRow(collectionRow);
            collectionDO.addRow(collectionStatus);
            collectionDO.addRow(collnToCustomer);
            collectionDO.addRow(profileToCollectionRel);
            collectionDO.addRow(iOSCollectionPayload);
            collectionDO = MDMUtil.getPersistence().add(collectionDO);
            collectionRow = collectionDO.getRow("Collection");
            if (collectionRow != null) {
                collectionID = (Long)collectionRow.get("COLLECTION_ID");
            }
            if (profileMap.optInt("PROFILE_TYPE", -1) != 2) {
                addOrUpdateRecentProfileToCollection(profileID, collectionID);
            }
        }
        catch (final DataAccessException exp) {
            throw new SyMException(1002, exp.getCause());
        }
        return collectionID;
    }
    
    public static void addOrUpdateRecentProfileToCollection(final Long profileID, final Long collectionID) throws DataAccessException {
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileToColln", "PROFILE_ID"), (Object)profileID, 0);
        final DataObject profileDO = MDMUtil.getPersistence().get("RecentProfileToColln", profileCriteria);
        if (!profileDO.isEmpty()) {
            final Row recentProfileToCollectionRel = profileDO.getFirstRow("RecentProfileToColln");
            recentProfileToCollectionRel.set("COLLECTION_ID", (Object)collectionID);
            profileDO.updateRow(recentProfileToCollectionRel);
            MDMUtil.getPersistence().update(profileDO);
        }
        else {
            final Row recentProfileToCollectionRel = new Row("RecentProfileToColln");
            recentProfileToCollectionRel.set("COLLECTION_ID", (Object)collectionID);
            recentProfileToCollectionRel.set("PROFILE_ID", (Object)profileID);
            profileDO.addRow(recentProfileToCollectionRel);
            MDMUtil.getPersistence().add(profileDO);
        }
    }
    
    public static DataObject createConfigDataDO(final JSONObject configDataMap) throws SyMException {
        DataObject configDataDO = null;
        try {
            if (configDataMap != null) {
                configDataDO = MDMUtil.getPersistence().constructDataObject();
                final String configName = (String)configDataMap.get("CONFIG_NAME");
                final Integer configID = (Integer)configDataMap.get("CONFIG_ID");
                final Long collectionID = (Long)configDataMap.get("COLLECTION_ID");
                final Integer configType = new Integer(3);
                final long time = System.currentTimeMillis();
                final Row configDataRow = new Row("ConfigData");
                configDataRow.set("CONFIG_ID", (Object)configID);
                configDataRow.set("LABEL", (Object)configName);
                configDataRow.set("CREATION_TIME", (Object)new Long(time));
                configDataRow.set("MODIFIED_TIME", (Object)new Long(time));
                configDataRow.set("DESCRIPTION", (Object)configName);
                configDataRow.set("CONFIG_TYPE", (Object)configType);
                configDataRow.set("DB_UPDATED_TIME", (Object)new Long(time));
                final Row configDataStatusRow = new Row("ConfigDataStatus");
                configDataStatusRow.set("CONFIG_DATA_ID", configDataRow.get("CONFIG_DATA_ID"));
                configDataStatusRow.set("CONFIG_STATUS", (Object)new Integer(1));
                final Row cfgdatatocollectionRow = new Row("CfgDataToCollection");
                cfgdatatocollectionRow.set("COLLECTION_ID", (Object)collectionID);
                cfgdatatocollectionRow.set("CONFIG_DATA_ID", configDataRow.get("CONFIG_DATA_ID"));
                cfgdatatocollectionRow.set("ORDER_OF_EXECUTION", (Object)new Integer(1));
                cfgdatatocollectionRow.set("DB_UPDATED_TIME", (Object)new Long(System.currentTimeMillis()));
                configDataDO.addRow(configDataRow);
                configDataDO.addRow(configDataStatusRow);
                configDataDO.addRow(cfgdatatocollectionRow);
            }
        }
        catch (final Exception exp) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in createConfigDataDO", exp);
            throw new SyMException(1002, exp.getCause());
        }
        return configDataDO;
    }
    
    public static DataObject createConfigDataItemDO(final JSONObject cfgDataItemMap) throws SyMException {
        DataObject configDataItemDO = null;
        try {
            if (cfgDataItemMap != null) {
                configDataItemDO = MDMUtil.getPersistence().constructDataObject();
                final Integer configDataID = (Integer)cfgDataItemMap.get("CONFIG_DATA_ID");
                final Integer configID = (Integer)cfgDataItemMap.get("CONFIG_ID");
                final Row cfgDataItemRow = new Row("ConfigDataItem");
                cfgDataItemRow.set("CONFIG_DATA_ID", (Object)configDataID);
                configDataItemDO.addRow(cfgDataItemRow);
                final Row iosConfigPayloadRow = new Row("IOSConfigPayload");
                iosConfigPayloadRow.set("CONFIG_DATA_ITEM_ID", cfgDataItemRow.get("CONFIG_DATA_ITEM_ID"));
                iosConfigPayloadRow.set("PAYLOAD_DISPLAY_NAME", (Object)configID.toString());
                iosConfigPayloadRow.set("PAYLOAD_DESCRIPTION", (Object)"description");
                iosConfigPayloadRow.set("PAYLOAD_IDENTIFIER", (Object)"identifier");
                iosConfigPayloadRow.set("PAYLOAD_TYPE", (Object)"type");
                iosConfigPayloadRow.set("PAYLOAD_UUID", (Object)"uuid");
                iosConfigPayloadRow.set("PAYLOAD_VERSION", (Object)"1");
                configDataItemDO.addRow(iosConfigPayloadRow);
            }
        }
        catch (final Exception exp) {
            throw new SyMException(1002, exp.getCause());
        }
        return configDataItemDO;
    }
    
    public static Long addOrUpdateProfile(final Map profileMap) throws SyMException {
        Long profileID = null;
        try {
            if (profileMap != null) {
                DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
                String profileName = profileMap.get("PROFILE_NAME");
                profileName = MDMStringUtils.trimStringLenght(profileName, 100);
                final String profileDescription = profileMap.get("PROFILE_DESCRIPTION");
                final Integer profileType = profileMap.get("PROFILE_TYPE");
                final Integer platformType = profileMap.get("PLATFORM_TYPE");
                final Long createdBy = profileMap.get("CREATED_BY");
                Long modifiedBy = profileMap.get("LAST_MODIFIED_BY");
                final Long customerID = profileMap.get("CUSTOMER_ID");
                profileID = profileMap.get("PROFILE_ID");
                if (modifiedBy == null) {
                    modifiedBy = createdBy;
                }
                if (profileID == null || profileID == -1L) {
                    Row profileRow = new Row("Profile");
                    profileRow.set("PROFILE_NAME", (Object)profileName);
                    profileRow.set("CREATED_BY", (Object)createdBy);
                    profileRow.set("PROFILE_TYPE", (Object)profileType);
                    profileRow.set("PLATFORM_TYPE", (Object)platformType);
                    profileRow.set("LAST_MODIFIED_BY", (Object)modifiedBy);
                    profileRow.set("PROFILE_DESCRIPTION", (Object)profileDescription);
                    profileRow.set("CREATION_TIME", (Object)System.currentTimeMillis());
                    profileRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    if (platformType == 1 && profileType == 1) {
                        final String profileIdentifier = "com.mdm." + UUID.randomUUID().toString() + "." + profileName;
                        profileRow.set("PROFILE_PAYLOAD_IDENTIFIER", (Object)profileIdentifier);
                        profileRow.set("PROFILE_IDENTIFIER", (Object)"--");
                    }
                    else {
                        profileRow.set("PROFILE_IDENTIFIER", (Object)profileName);
                    }
                    dataObject.addRow(profileRow);
                    final Row profileToCustomerRel = new Row("ProfileToCustomerRel");
                    profileToCustomerRel.set("PROFILE_ID", profileRow.get("PROFILE_ID"));
                    profileToCustomerRel.set("CUSTOMER_ID", (Object)customerID);
                    dataObject.addRow(profileToCustomerRel);
                    ProfileHandler.logger.log(Level.INFO, "DataObject  before insert...{0}", dataObject);
                    dataObject = MDMUtil.getPersistence().add(dataObject);
                    profileRow = dataObject.getRow("Profile");
                    if (profileRow != null) {
                        profileID = (Long)profileRow.get("PROFILE_ID");
                    }
                }
                else {
                    final Criteria criteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileID, 0);
                    dataObject = MDMUtil.getPersistence().get("Profile", criteria);
                    final Row profileRow2 = dataObject.getRow("Profile");
                    if (profileRow2 != null) {
                        if (profileName != null) {
                            profileRow2.set("PROFILE_NAME", (Object)profileName);
                        }
                        if (profileDescription != null) {
                            profileRow2.set("PROFILE_DESCRIPTION", (Object)profileDescription);
                        }
                        if (modifiedBy != null) {
                            profileRow2.set("LAST_MODIFIED_BY", (Object)modifiedBy);
                        }
                        profileRow2.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    }
                    dataObject.updateRow(profileRow2);
                    MDMUtil.getPersistence().update(dataObject);
                }
                if (profileID != null) {
                    ManagementUtil.addManagementType(1, profileID, (profileMap.get("management_type") == null) ? ManagementConstants.Types.MOBILE_MGMT : profileMap.get("management_type"));
                }
            }
        }
        catch (final DataAccessException exp) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateProfile", (Throwable)exp);
            throw new SyMException(1002, exp.getCause());
        }
        catch (final Exception exp2) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateProfile", exp2);
            throw new SyMException(1002, exp2.getCause());
        }
        return profileID;
    }
    
    public static Long addOrUpdateProfileCollectionDO(final Map profileMap) throws SyMException, Exception {
        Long collectionID = null;
        try {
            DataObject collectionDO = MDMUtil.getPersistence().constructDataObject();
            String profileName = profileMap.get("PROFILE_NAME");
            profileName = MDMStringUtils.trimStringLenght(profileName, 100);
            final String profileDescription = profileMap.get("PROFILE_DESCRIPTION");
            final Integer profileType = profileMap.get("PROFILE_TYPE");
            final Long createdBy = profileMap.get("CREATED_BY");
            final Long modifiedBy = profileMap.get("LAST_MODIFIED_BY");
            final Long customerID = profileMap.get("CUSTOMER_ID");
            final Long profileID = profileMap.get("PROFILE_ID");
            final Integer securityType = profileMap.get("SECURITY_TYPE");
            final long time = System.currentTimeMillis();
            Row collectionRow = new Row("Collection");
            collectionRow.set("COLLECTION_NAME", (Object)profileName);
            collectionRow.set("DESCRIPTION", (Object)profileDescription);
            collectionRow.set("CREATION_TIME", (Object)time);
            collectionRow.set("MODIFIED_TIME", (Object)time);
            collectionRow.set("IS_SINGLE_CONFIG", (Object)Boolean.FALSE);
            collectionRow.set("COLLECTION_TYPE", (Object)new Integer(3));
            collectionRow.set("IS_CONFIG_COLLECTION", (Object)Boolean.FALSE);
            collectionRow.set("DB_UPDATED_TIME", (Object)time);
            final Row collectionStatus = new Row("CollectionStatus");
            collectionStatus.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
            collectionStatus.set("STATUS", (Object)new Integer(1));
            collectionStatus.set("PROFILE_COLLECTION_STATUS", (Object)new Integer(1));
            collectionStatus.set("DB_UPDATED_TIME", (Object)time);
            final Row iOSCollectionPayload = new Row("IOSCollectionPayload");
            iOSCollectionPayload.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
            iOSCollectionPayload.set("SECURITY_TYPE", (Object)securityType);
            iOSCollectionPayload.set("PAYLOAD_UUID", (Object)profileName);
            final Row collnToCustomer = new Row("CollnToCustomerRel");
            if (customerID != null) {
                collnToCustomer.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
                collnToCustomer.set("CUSTOMER_ID", (Object)new Long(customerID));
                collnToCustomer.set("DB_UPDATED_TIME", (Object)time);
            }
            final Row profileToCollectionRel = new Row("ProfileToCollection");
            profileToCollectionRel.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
            profileToCollectionRel.set("PROFILE_ID", (Object)profileID);
            final Criteria criteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileID, 0);
            final Integer maxValue = (Integer)DBUtil.getMaxOfValue("ProfileToCollection", "PROFILE_VERSION", criteria);
            if (maxValue == null) {
                profileToCollectionRel.set("PROFILE_VERSION", (Object)new Integer(1));
            }
            else {
                profileToCollectionRel.set("PROFILE_VERSION", (Object)(maxValue + 1));
            }
            collectionDO.addRow(collectionRow);
            collectionDO.addRow(collectionStatus);
            collectionDO.addRow(collnToCustomer);
            collectionDO.addRow(profileToCollectionRel);
            collectionDO.addRow(iOSCollectionPayload);
            collectionDO = MDMUtil.getPersistence().add(collectionDO);
            collectionRow = collectionDO.getRow("Collection");
            if (collectionRow != null) {
                collectionID = (Long)collectionRow.get("COLLECTION_ID");
            }
            if (profileType != 2) {
                final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileToColln", "PROFILE_ID"), (Object)profileID, 0);
                final DataObject profileDO = MDMUtil.getPersistence().get("RecentProfileToColln", profileCriteria);
                if (!profileDO.isEmpty()) {
                    final Row recentProfileToCollectionRel = profileDO.getFirstRow("RecentProfileToColln");
                    recentProfileToCollectionRel.set("COLLECTION_ID", (Object)collectionID);
                    profileDO.updateRow(recentProfileToCollectionRel);
                    MDMUtil.getPersistence().update(profileDO);
                }
                else {
                    final Row recentProfileToCollectionRel = new Row("RecentProfileToColln");
                    recentProfileToCollectionRel.set("COLLECTION_ID", (Object)collectionID);
                    recentProfileToCollectionRel.set("PROFILE_ID", (Object)profileID);
                    profileDO.addRow(recentProfileToCollectionRel);
                    MDMUtil.getPersistence().add(profileDO);
                }
            }
        }
        catch (final DataAccessException exp) {
            throw new SyMException(1002, exp.getCause());
        }
        return collectionID;
    }
    
    public static DataObject createConfigDataDO(final Map configDataMap) throws SyMException {
        DataObject configDataDO = null;
        try {
            if (configDataMap != null) {
                configDataDO = MDMUtil.getPersistence().constructDataObject();
                final String configName = configDataMap.get("CONFIG_NAME");
                final Integer configID = configDataMap.get("CONFIG_ID");
                final Long collectionID = configDataMap.get("COLLECTION_ID");
                final Integer configType = new Integer(3);
                final long time = System.currentTimeMillis();
                final Row configDataRow = new Row("ConfigData");
                configDataRow.set("CONFIG_ID", (Object)configID);
                configDataRow.set("LABEL", (Object)configName);
                configDataRow.set("CREATION_TIME", (Object)new Long(time));
                configDataRow.set("MODIFIED_TIME", (Object)new Long(time));
                configDataRow.set("DESCRIPTION", (Object)configName);
                configDataRow.set("CONFIG_TYPE", (Object)configType);
                configDataRow.set("DB_UPDATED_TIME", (Object)new Long(time));
                final Row configDataStatusRow = new Row("ConfigDataStatus");
                configDataStatusRow.set("CONFIG_DATA_ID", configDataRow.get("CONFIG_DATA_ID"));
                configDataStatusRow.set("CONFIG_STATUS", (Object)new Integer(1));
                final Row cfgdatatocollectionRow = new Row("CfgDataToCollection");
                cfgdatatocollectionRow.set("COLLECTION_ID", (Object)collectionID);
                cfgdatatocollectionRow.set("CONFIG_DATA_ID", configDataRow.get("CONFIG_DATA_ID"));
                cfgdatatocollectionRow.set("ORDER_OF_EXECUTION", (Object)new Integer(1));
                cfgdatatocollectionRow.set("DB_UPDATED_TIME", (Object)new Long(System.currentTimeMillis()));
                configDataDO.addRow(configDataRow);
                configDataDO.addRow(configDataStatusRow);
                configDataDO.addRow(cfgdatatocollectionRow);
            }
        }
        catch (final DataAccessException exp) {
            throw new SyMException(1002, exp.getCause());
        }
        return configDataDO;
    }
    
    public static DataObject createConfigDataItemDO(final Map cfgDataItemMap) throws SyMException {
        DataObject configDataItemDO = null;
        try {
            if (cfgDataItemMap != null) {
                configDataItemDO = MDMUtil.getPersistence().constructDataObject();
                final Integer configDataID = cfgDataItemMap.get("CONFIG_DATA_ID");
                final Integer configID = cfgDataItemMap.get("CONFIG_ID");
                final Row cfgDataItemRow = new Row("ConfigDataItem");
                cfgDataItemRow.set("CONFIG_DATA_ID", (Object)configDataID);
                configDataItemDO.addRow(cfgDataItemRow);
                final Row iosConfigPayloadRow = new Row("IOSConfigPayload");
                iosConfigPayloadRow.set("CONFIG_DATA_ITEM_ID", cfgDataItemRow.get("CONFIG_DATA_ITEM_ID"));
                iosConfigPayloadRow.set("PAYLOAD_DISPLAY_NAME", (Object)configID.toString());
                iosConfigPayloadRow.set("PAYLOAD_DESCRIPTION", (Object)"description");
                iosConfigPayloadRow.set("PAYLOAD_IDENTIFIER", (Object)"identifier");
                iosConfigPayloadRow.set("PAYLOAD_TYPE", (Object)"type");
                iosConfigPayloadRow.set("PAYLOAD_UUID", (Object)"uuid");
                iosConfigPayloadRow.set("PAYLOAD_VERSION", (Object)"1");
                configDataItemDO.addRow(iosConfigPayloadRow);
            }
        }
        catch (final DataAccessException exp) {
            throw new SyMException(1002, exp.getCause());
        }
        return configDataItemDO;
    }
    
    public static HashMap getProfileBeanMap(final Long profileID, final Long customerId) {
        final HashMap profileMap = new HashMap();
        try {
            DataObject profileDO = null;
            Long collectionID = null;
            if (profileID != null) {
                Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
                final Join profileCollectionJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                selectQuery.addJoin(profileCollectionJoin);
                final Join recentCollectionJoin = new Join("ProfileToCollection", "RecentProfileToColln", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1);
                selectQuery.addJoin(recentCollectionJoin);
                final Join iOSCollectionPayloadJoin = new Join("ProfileToCollection", "IOSCollectionPayload", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
                selectQuery.addJoin(iOSCollectionPayloadJoin);
                final Join appGroupToCollectionJoin = new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
                selectQuery.addJoin(appGroupToCollectionJoin);
                selectQuery.addJoin(AppVersionDBUtil.getInstance().getAppReleaseLabelJoin(1));
                final Criteria appReleaseLabelNullOrProd = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)null, 0).or(AppVersionDBUtil.getInstance().getAppReleaseLabelCriteria(AppVersionDBUtil.RELEASE_LABEL_PRODUCTION, customerId));
                criteria = criteria.and(appReleaseLabelNullOrProd);
                if (customerId != null) {
                    final Join customerJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                    selectQuery.addJoin(customerJoin);
                    criteria = criteria.and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
                }
                selectQuery.setCriteria(criteria);
                selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
                ProfileHandler.logger.log(Level.INFO, "SelectQuery before execute...{0}", RelationalAPI.getInstance().getSelectSQL((Query)selectQuery));
                profileDO = MDMUtil.getPersistence().get(selectQuery);
                if (!profileDO.isEmpty()) {
                    final Row profileRow = profileDO.getFirstRow("Profile");
                    profileMap.put("PROFILE_ID", profileRow.get("PROFILE_ID"));
                    profileMap.put("PROFILE_NAME", profileRow.get("PROFILE_NAME"));
                    profileMap.put("PROFILE_DESCRIPTION", profileRow.get("PROFILE_DESCRIPTION"));
                    profileMap.put("PROFILE_TYPE", profileRow.get("PROFILE_TYPE"));
                    profileMap.put("SCOPE", profileRow.get("SCOPE"));
                    final Row iOSCOLLECTIONPAYLOADRow = profileDO.getFirstRow("IOSCollectionPayload");
                    profileMap.put("SECURITY_TYPE", iOSCOLLECTIONPAYLOADRow.get("SECURITY_TYPE"));
                    collectionID = getRecentProfileCollectionID(profileID);
                    if (collectionID == null && profileDO.containsTable("AppGroupToCollection")) {
                        final Row appGrpToCollection = profileDO.getFirstRow("AppGroupToCollection");
                        if (appGrpToCollection != null && appGrpToCollection.get("COLLECTION_ID") != null) {
                            collectionID = (Long)appGrpToCollection.get("COLLECTION_ID");
                        }
                    }
                    if (collectionID != null) {
                        profileMap.put("COLLECTION_ID", collectionID);
                    }
                }
            }
        }
        catch (final Exception exp) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in getProfileBeanMap", exp);
        }
        return profileMap;
    }
    
    public static Long getRecentProfileCollectionID(final Long profileID) {
        Long collectionID = null;
        try {
            if (profileID != null) {
                final DataObject profileCollectionStatusDO = getRecentProfileCollectionDO(profileID);
                if (!profileCollectionStatusDO.isEmpty()) {
                    final Row collectionStatusRow = profileCollectionStatusDO.getFirstRow("CollectionStatus");
                    if (collectionStatusRow != null) {
                        collectionID = (Long)collectionStatusRow.get("COLLECTION_ID");
                    }
                }
            }
        }
        catch (final Exception exp) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in getRecentProfileCollectionID", exp);
        }
        return collectionID;
    }
    
    public static int getRecentProfileCollectionStatus(final Long profileID) {
        int collectionStatus = 0;
        try {
            if (profileID != null) {
                final DataObject profileCollectionStatusDO = getRecentProfileCollectionDO(profileID);
                if (!profileCollectionStatusDO.isEmpty()) {
                    final Row collectionStatusRow = profileCollectionStatusDO.getFirstRow("CollectionStatus");
                    if (collectionStatusRow != null) {
                        collectionStatus = (int)collectionStatusRow.get("PROFILE_COLLECTION_STATUS");
                    }
                }
            }
        }
        catch (final Exception exp) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in getRecentProfileCollectionStatus", exp);
        }
        return collectionStatus;
    }
    
    public static void addOrUpdateRecentPublishedProfileToCollection(final Long profileID, final Long collectionID) throws DataAccessException {
        if (profileID != null && collectionID != null) {
            final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0);
            final DataObject recentPubProfileToCollnDO = MDMUtil.getPersistence().get("RecentPubProfileToColln", criteria);
            if (recentPubProfileToCollnDO.isEmpty()) {
                final Row recentPubProfileToColln = new Row("RecentPubProfileToColln");
                recentPubProfileToColln.set("COLLECTION_ID", (Object)collectionID);
                recentPubProfileToColln.set("PROFILE_ID", (Object)profileID);
                recentPubProfileToCollnDO.addRow(recentPubProfileToColln);
                MDMUtil.getPersistence().add(recentPubProfileToCollnDO);
            }
            else {
                final Row recentPubProfileToColln = recentPubProfileToCollnDO.getFirstRow("RecentPubProfileToColln");
                recentPubProfileToColln.set("COLLECTION_ID", (Object)collectionID);
                recentPubProfileToCollnDO.updateRow(recentPubProfileToColln);
                MDMUtil.getPersistence().update(recentPubProfileToCollnDO);
            }
        }
    }
    
    public static void addOrUpdateProfileCollectionStatus(final Long collectionID, final int profileStatus) throws DataAccessException {
        if (collectionID != null) {
            final Criteria criteria = new Criteria(Column.getColumn("CollectionStatus", "COLLECTION_ID"), (Object)collectionID, 0);
            final DataObject recentPubProfileToCollnDO = MDMUtil.getPersistence().get("CollectionStatus", criteria);
            if (recentPubProfileToCollnDO.isEmpty()) {
                final Row recentPubProfileToColln = new Row("CollectionStatus");
                recentPubProfileToColln.set("COLLECTION_ID", (Object)collectionID);
                recentPubProfileToColln.set("PROFILE_COLLECTION_STATUS", (Object)new Integer(profileStatus));
                recentPubProfileToColln.set("IS_STATUS_COMPUTABLE", (Object)Boolean.FALSE);
                recentPubProfileToColln.set("DB_UPDATED_TIME", (Object)new Long(System.currentTimeMillis()));
                recentPubProfileToCollnDO.addRow(recentPubProfileToColln);
                MDMUtil.getPersistence().add(recentPubProfileToCollnDO);
            }
            else {
                final Row recentPubProfileToColln = recentPubProfileToCollnDO.getFirstRow("CollectionStatus");
                recentPubProfileToColln.set("PROFILE_COLLECTION_STATUS", (Object)new Integer(profileStatus));
                recentPubProfileToColln.set("DB_UPDATED_TIME", (Object)new Long(System.currentTimeMillis()));
                recentPubProfileToCollnDO.updateRow(recentPubProfileToColln);
                MDMUtil.getPersistence().update(recentPubProfileToCollnDO);
            }
        }
    }
    
    private static DataObject getRecentProfileCollectionDO(final Long profileID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileToColln"));
        final Join collectionJoin = new Join("RecentProfileToColln", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        selectQuery.addJoin(collectionJoin);
        final Criteria criteria = new Criteria(Column.getColumn("RecentProfileToColln", "PROFILE_ID"), (Object)profileID, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
        final DataObject profileCollectionStatusDO = MDMUtil.getPersistence().get(selectQuery);
        return profileCollectionStatusDO;
    }
    
    public static Properties getCollectionProperties(final Long collectionID) throws SyMException {
        final Properties properties = new Properties();
        final DataObject collectionDO = MDMCollectionUtil.getCollection(collectionID);
        final List configDOList = MDMConfigUtil.getConfigurationDataItems(collectionID);
        ((Hashtable<String, Long>)properties).put("collectionId", collectionID);
        ((Hashtable<String, DataObject>)properties).put("collectionDO", collectionDO);
        ((Hashtable<String, List>)properties).put("configDOList", configDOList);
        ((Hashtable<String, Boolean>)properties).put("isMobileConfig", Boolean.TRUE);
        ((Hashtable<String, String>)properties).put("userName", "admin");
        return properties;
    }
    
    public static String getProfileIdentifierFromCollectionID(final Long collectionID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        final Join profileToCollectionJoin = new Join("Collection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join profileJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        selectQuery.addJoin(profileToCollectionJoin);
        selectQuery.addJoin(profileJoin);
        selectQuery.setCriteria(new Criteria(Column.getColumn("Collection", "COLLECTION_ID"), (Object)collectionID, 0));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "*"));
        final DataObject profileDO = MDMUtil.getPersistence().get(selectQuery);
        String profileIdentifier = null;
        if (!profileDO.isEmpty()) {
            profileIdentifier = (String)profileDO.getFirstValue("Profile", "PROFILE_PAYLOAD_IDENTIFIER");
        }
        return profileIdentifier;
    }
    
    public static String getProfileIdentifierFromProfileID(final Long profileID) throws DataAccessException {
        String profileIdentifier = null;
        final DataObject profileDO = MDMUtil.getPersistence().get("Profile", new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0));
        if (!profileDO.isEmpty()) {
            profileIdentifier = (String)profileDO.getFirstValue("Profile", "PROFILE_PAYLOAD_IDENTIFIER");
        }
        return profileIdentifier;
    }
    
    public static Map<Long, List<Long>> getProfileCollections(final Set<Long> profileIDSet) {
        Map<Long, List<Long>> profileCollectionMap = null;
        try {
            final DataObject profileDO = MDMUtil.getPersistence().get("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileIDSet.toArray(), 8));
            if (!profileDO.isEmpty()) {
                profileCollectionMap = new HashMap<Long, List<Long>>();
                for (final Long profileID : profileIDSet) {
                    final Iterator profileIterator = profileDO.getRows("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileID, 0));
                    final List<Long> collectionIDList = new ArrayList<Long>();
                    while (profileIterator.hasNext()) {
                        final Row row = profileIterator.next();
                        collectionIDList.add((Long)row.get("COLLECTION_ID"));
                    }
                    profileCollectionMap.put(profileID, collectionIDList);
                }
            }
        }
        catch (final Exception ex) {
            ProfileHandler.logger.log(Level.WARNING, "Exception occurred while getCollectionsForProfile", ex);
        }
        return profileCollectionMap;
    }
    
    public Map<String, Object> getProfileInfoFromCollectionID(final Long collectionID) {
        final Map<String, Object> profileInfo = new HashMap<String, Object>();
        try {
            final Criteria collecionIDCrit = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final Join profToCollnJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addSelectColumn(Column.getColumn("Profile", "*"));
            selectQuery.addJoin(profToCollnJoin);
            selectQuery.setCriteria(collecionIDCrit);
            final DataObject profileDO = MDMUtil.getPersistence().get(selectQuery);
            if (!profileDO.isEmpty()) {
                final Row row = profileDO.getFirstRow("Profile");
                profileInfo.put("PROFILE_ID", row.get("PROFILE_ID"));
                profileInfo.put("PROFILE_NAME", row.get("PROFILE_NAME"));
                profileInfo.put("PLATFORM_TYPE", row.get("PLATFORM_TYPE"));
            }
        }
        catch (final Exception ex) {
            ProfileHandler.logger.log(Level.WARNING, "Exception occurred while getting profile information from colleciton ID", ex);
        }
        return profileInfo;
    }
    
    public Long getRecentlyAppliedCollectionIdForGroup(final Long groupId, final Long profileId) throws Exception {
        Long collectionId = null;
        final Criteria profileIdCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria groupIdCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0);
        final DataObject groupCollnDO = MDMUtil.getPersistence().get("RecentProfileForGroup", profileIdCriteria.and(groupIdCriteria));
        if (!groupCollnDO.isEmpty()) {
            final Row recentProfGroupRow = groupCollnDO.getFirstRow("RecentProfileForGroup");
            collectionId = (Long)recentProfGroupRow.get("COLLECTION_ID");
        }
        return collectionId;
    }
    
    public boolean checkProfileNameExist(final Long customerId, final String profileName, final Integer profileType, final Criteria profileCriteria) {
        return this.checkProfileNameExist(customerId, profileName, profileType, profileCriteria, null);
    }
    
    public boolean checkProfileNameExist(final Long customerId, final String profileName, final Integer profileType, final Criteria profileCriteria, final Boolean isForAllCustomers) {
        boolean isExist = false;
        ProfileHandler.logger.log(Level.INFO, "*** checkProfileNameExist inputs: CustomerId:{0}; Profilename:{1}", new Object[] { customerId, profileName });
        if (customerId != null && profileName != null && !profileName.isEmpty()) {
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria profilenameCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)profileName.trim(), 0, false);
            final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            Criteria criteria;
            if (isForAllCustomers != null && isForAllCustomers == Boolean.TRUE) {
                criteria = profilenameCriteria.and(profileTypeCriteria);
            }
            else {
                criteria = customerCriteria.and(profilenameCriteria).and(profileTypeCriteria);
            }
            if (profileCriteria != null) {
                criteria = criteria.and(profileCriteria);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final Join customerToProfJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(customerToProfJoin);
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
            selectQuery.setCriteria(criteria);
            try {
                final DataObject profNameDO = MDMUtil.getPersistence().get(selectQuery);
                if (profNameDO != null && !profNameDO.isEmpty()) {
                    isExist = true;
                }
            }
            catch (final Exception ex) {
                ProfileHandler.logger.log(Level.WARNING, "Exception occurred while checkProfileNameExist {0}", ex);
            }
            ProfileHandler.logger.log(Level.INFO, "**checkProfileNameExist ** IS PROFILE NAME EXIST: {0}", isExist);
        }
        return isExist;
    }
    
    public boolean checkIfEmptyProfile(final Long collectionID) {
        try {
            final List configDOList = MDMConfigUtil.getConfigurations(collectionID);
            return configDOList.size() > 0;
        }
        catch (final SyMException e) {
            e.printStackTrace();
            return true;
        }
    }
    
    public HashMap getPlatformBasedProfileIds(final List profileIdsList) {
        final HashMap map = new HashMap();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIdsList.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.setCriteria(criteria);
            final Set ios = new HashSet();
            final Set android = new HashSet();
            final Set windows = new HashSet();
            final Set chrome = new HashSet();
            final Set mac = new HashSet();
            final Set tvOS = new HashSet();
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("Profile");
                while (iterator.hasNext()) {
                    final Row profileRow = iterator.next();
                    final int platform = (int)profileRow.get("PLATFORM_TYPE");
                    final Long resourceId = (Long)profileRow.get("PROFILE_ID");
                    switch (platform) {
                        case 1: {
                            ios.add(resourceId);
                            continue;
                        }
                        case 2: {
                            android.add(resourceId);
                            continue;
                        }
                        case 3: {
                            windows.add(resourceId);
                            continue;
                        }
                        case 4: {
                            chrome.add(resourceId);
                            continue;
                        }
                        case 6: {
                            mac.add(resourceId);
                            continue;
                        }
                        case 7: {
                            tvOS.add(resourceId);
                            continue;
                        }
                    }
                }
            }
            map.put(1, ios);
            map.put(2, android);
            map.put(3, windows);
            map.put(4, chrome);
            map.put(6, mac);
            map.put(7, tvOS);
        }
        catch (final Exception ex) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in getPlatformBasedProfileIds", ex);
        }
        return map;
    }
    
    public List getProfileIDsFromCollectionIDs(final List collectionIds) {
        List profileIds = null;
        try {
            profileIds = DBUtil.getDistinctColumnValue("ProfileToCollection", "PROFILE_ID", new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8));
        }
        catch (final Exception e) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception while getting profileId from collection", e);
        }
        return profileIds;
    }
    
    public static Long getProfileIDFromConfigDataItemID(final Long configDataItemID) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ConfigDataItem"));
            query.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addJoin(new Join("CfgDataToCollection", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("RecentProfileToColln", "PROFILE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Row row = dataObject.getFirstRow("RecentProfileToColln");
            return (Long)row.get("PROFILE_ID");
        }
        catch (final Exception e) {
            ProfileHandler.logger.log(Level.SEVERE, "Failed to fetch profileID from collectionID", e);
            return null;
        }
    }
    
    public Long getProfileIDFromCollectionID(final Long collectionID) {
        try {
            final List<Long> collectionList = new ArrayList<Long>();
            collectionList.add(collectionID);
            final List<Long> profileList = this.getProfileIDsFromCollectionIDs(collectionList);
            return Long.parseLong(String.valueOf(profileList.get(0)));
        }
        catch (final Exception e) {
            ProfileHandler.logger.log(Level.SEVERE, "Exception in getting Profile ID From collection ID", e);
            return null;
        }
    }
    
    static {
        ProfileHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
