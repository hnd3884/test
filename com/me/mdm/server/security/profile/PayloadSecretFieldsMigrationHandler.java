package com.me.mdm.server.security.profile;

import java.util.Hashtable;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;
import java.util.Properties;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import java.util.Map;
import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.security.passcode.MDMManagedPasswordHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

public class PayloadSecretFieldsMigrationHandler
{
    public static Logger logger;
    
    public Set<Long> migrateSecretFieldsDOAndADBinding() throws Exception {
        final Set<Long> collectionList = this.migrateSecretFieldsDO();
        collectionList.addAll(PayloadSecretFieldsMigrationUtil.getCollIDsForOtherSecretFieldPolicies());
        PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "List of collectionIds to be republished : {0}", collectionList);
        return collectionList;
    }
    
    private Set<Long> migrateSecretFieldsDO() throws Exception {
        final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        final List<PayloadSecretFieldsTableMapping> secretFieldsMappedTableList = PayloadSecretFieldsMigrationUtil.getInstance().getSecretFieldsMappedTableList();
        final Set<Long> collectionList = new HashSet<Long>();
        if (customerIds != null && secretFieldsMappedTableList != null && !secretFieldsMappedTableList.isEmpty()) {
            for (int i = 0; i < customerIds.length; ++i) {
                final Long customerId = customerIds[i];
                final Long userId = MDMUtil.getAdminUserId();
                PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "Payload secret fields Migration started for customer {0}", customerId);
                final Map<String, Long> managedPasswordsForCustomer = MDMManagedPasswordHandler.getMDMManagedPasswordsOfCustomer(customerId);
                final Set<Long> tempCollectionList = new HashSet<Long>(collectionList);
                for (final PayloadSecretFieldsTableMapping payloadSecretFieldsTableMapping : secretFieldsMappedTableList) {
                    final SelectQuery selectQuery = PayloadSecretFieldsMigrationUtil.getCommonProfileConfigurationSelectQuery(customerId);
                    payloadSecretFieldsTableMapping.addCfgDataItemJoin(selectQuery, 1);
                    payloadSecretFieldsTableMapping.addSelectColumns(selectQuery);
                    if (payloadSecretFieldsTableMapping.getTableName().equals("WpExchangeActiveSyncPolicy")) {
                        selectQuery.addSelectColumn(new Column("WpExchangeActiveSyncPolicy", "GUID"));
                    }
                    else if (payloadSecretFieldsTableMapping.getTableName().equals("WpEmailPolicy")) {
                        selectQuery.addSelectColumn(new Column("WpEmailPolicy", "GUID"));
                    }
                    final DataObject secretFieldsDO = MDMUtil.getPersistence().get(selectQuery);
                    if (secretFieldsDO != null && !secretFieldsDO.isEmpty()) {
                        if (payloadSecretFieldsTableMapping.checkIfTableHasSecretField()) {
                            final HashMap<String, List<String>> tableToSecretFieldsMap = payloadSecretFieldsTableMapping.getTableToSecretFieldsMap();
                            final HashMap<String, String> secretColumnsMap = payloadSecretFieldsTableMapping.getSecretColumnsMap();
                            for (final String tableName : tableToSecretFieldsMap.keySet()) {
                                if (secretFieldsDO.containsTable(tableName)) {
                                    PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "[PAYLOAD_SECRET_FIELD_MIGRATION] Migrating table having secret field {0}", tableName);
                                    final Iterator rowsIterator = secretFieldsDO.getRows(tableName);
                                    final List<String> secretFields = tableToSecretFieldsMap.get(tableName);
                                    while (rowsIterator.hasNext()) {
                                        final Row row = rowsIterator.next();
                                        PayloadSecretFieldsMigrationUtil.migrateSecretFieldColumns(row, secretFields, secretColumnsMap, secretFieldsDO, managedPasswordsForCustomer, customerId, userId, payloadSecretFieldsTableMapping, collectionList, tableName);
                                    }
                                }
                            }
                        }
                        else if (payloadSecretFieldsTableMapping.checkIfTableHasCertificateColumn()) {
                            final HashMap<String, List<String>> tableToCertificateColumnsMap = payloadSecretFieldsTableMapping.getTableToCertificateColumnsMap();
                            for (final String tableName2 : tableToCertificateColumnsMap.keySet()) {
                                final List<String> certificateColumns = tableToCertificateColumnsMap.get(tableName2);
                                if (secretFieldsDO.containsTable(tableName2)) {
                                    PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "[PAYLOAD_SECRET_FIELD_MIGRATION] Migrating table having certificate {0}", tableName2);
                                    final Iterator rowsIterator = secretFieldsDO.getRows(tableName2);
                                    while (rowsIterator.hasNext()) {
                                        final Row row2 = rowsIterator.next();
                                        PayloadSecretFieldsMigrationUtil.getCollnIdForCertificate(row2, certificateColumns, payloadSecretFieldsTableMapping, collectionList, secretFieldsDO);
                                    }
                                }
                            }
                        }
                        MDMUtil.getPersistence().update(secretFieldsDO);
                    }
                }
                final Set<Long> customerCollectionIds = new HashSet<Long>(collectionList);
                customerCollectionIds.removeAll(tempCollectionList);
                PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "Payload secret fields Migration completed for customer {0} for collectionIDs:{1}", new Object[] { customerId, customerCollectionIds });
            }
        }
        return collectionList;
    }
    
    public void resetSecretFieldColumns(final Set<Long> collectionIds) throws Exception {
        final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        final List<PayloadSecretFieldsTableMapping> secretFieldsMappedTableList = PayloadSecretFieldsMigrationUtil.getInstance().getSecretFieldsMappedTableList();
        if (customerIds != null && secretFieldsMappedTableList != null && !secretFieldsMappedTableList.isEmpty()) {
            for (int i = 0; i < customerIds.length; ++i) {
                final Long customerId = customerIds[i];
                PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "Resetting Payload secret field columns started for customer {0}", customerId);
                for (final PayloadSecretFieldsTableMapping payloadSecretFieldsTableMapping : secretFieldsMappedTableList) {
                    if (payloadSecretFieldsTableMapping.checkIfTableHasSecretField()) {
                        final SelectQuery selectQuery = PayloadSecretFieldsMigrationUtil.getCommonProfileConfigurationSelectQuery(customerId);
                        if (!collectionIds.isEmpty()) {
                            final Criteria collectionIdsCri = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
                            selectQuery.setCriteria(selectQuery.getCriteria().and(collectionIdsCri));
                        }
                        payloadSecretFieldsTableMapping.addCfgDataItemJoin(selectQuery, 1);
                        payloadSecretFieldsTableMapping.addSelectColumns(selectQuery);
                        final DataObject secretFieldsDO = MDMUtil.getPersistence().get(selectQuery);
                        if (secretFieldsDO == null || secretFieldsDO.isEmpty()) {
                            continue;
                        }
                        final HashMap<String, List<String>> tableToSecretFieldsMap = payloadSecretFieldsTableMapping.getTableToSecretFieldsMap();
                        for (final String tableName : tableToSecretFieldsMap.keySet()) {
                            if (secretFieldsDO.containsTable(tableName)) {
                                final Iterator rowsIterator = secretFieldsDO.getRows(tableName);
                                final List<String> secretFields = tableToSecretFieldsMap.get(tableName);
                                while (rowsIterator.hasNext()) {
                                    final Row row = rowsIterator.next();
                                    PayloadSecretFieldsMigrationUtil.resetSecretFieldColumn(row, secretFields, secretFieldsDO);
                                }
                            }
                        }
                        MDMUtil.getPersistence().update(secretFieldsDO);
                    }
                }
                PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "Resetting Payload secret field columns ended for customer {0}", customerId);
            }
        }
    }
    
    public void rePublishProfilesWithSecretFields(final Set<Long> collectionIds) throws Exception {
        final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        if (customerIds != null) {
            for (int i = 0; i < customerIds.length; ++i) {
                final Long customerId = customerIds[i];
                final Set<Long> tempCollectionIdsList = new HashSet<Long>();
                PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "Republishing Profiles with secret fields started for customer {0}", customerId);
                final SelectQuery selectQuery = PayloadSecretFieldsMigrationUtil.getCommonProfileConfigurationSelectQuery(customerId);
                selectQuery.addJoin(new Join("ProfileToCollection", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery.addSelectColumn(new Column("Profile", "PROFILE_PAYLOAD_IDENTIFIER"));
                selectQuery.addSelectColumn(new Column("CollectionStatus", "COLLECTION_ID"));
                selectQuery.addSelectColumn(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
                final Criteria configCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)PayloadSecretFieldsMigrationUtil.getSecretFieldConfigIds().toArray(), 8);
                final Criteria publishedCollection = new Criteria(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"), (Object)110, 0);
                selectQuery.setCriteria(selectQuery.getCriteria().and(configCriteria.and(publishedCollection)));
                if (!collectionIds.isEmpty()) {
                    final Criteria collectionIdsCri = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
                    selectQuery.setCriteria(selectQuery.getCriteria().and(collectionIdsCri));
                }
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                final JSONObject publishLog = new JSONObject();
                if (dataObject != null && !dataObject.isEmpty()) {
                    final Iterator profileCollectionIterator = dataObject.getRows("ProfileToCollection");
                    while (profileCollectionIterator.hasNext()) {
                        final Row profileCollectionRow = profileCollectionIterator.next();
                        if (profileCollectionRow != null) {
                            final Long profileId = (Long)profileCollectionRow.get("PROFILE_ID");
                            publishLog.put((Object)"PROFILE_ID", (Object)profileId);
                            final Long collectionId = (Long)profileCollectionRow.get("COLLECTION_ID");
                            final Row profileRow = dataObject.getRow("Profile", new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0));
                            final String profilePayloadIdentifier = (String)profileRow.get("PROFILE_PAYLOAD_IDENTIFIER");
                            final int platformType = (int)profileRow.get("PLATFORM_TYPE");
                            final Properties publishProperties = new Properties();
                            ((Hashtable<String, Long>)publishProperties).put("collectionId", collectionId);
                            ((Hashtable<String, Boolean>)publishProperties).put("APP_CONFIG", false);
                            ((Hashtable<String, Long>)publishProperties).put("CUSTOMER_ID", customerId);
                            ((Hashtable<String, String>)publishProperties).put("PROFILE_PAYLOAD_IDENTIFIER", profilePayloadIdentifier);
                            ((Hashtable<String, Long>)publishProperties).put("PROFILE_ID", profileId);
                            ((Hashtable<String, Boolean>)publishProperties).put("installprofileneeded", true);
                            ((Hashtable<String, Boolean>)publishProperties).put("removeprofileneeded", false);
                            if (platformType == 1) {
                                ((Hashtable<String, Boolean>)publishProperties).put("seqNeeded", false);
                            }
                            PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "[PAYLOAD_SECRET_FIELD_MIGRATION] Initiating profile-republish {0}", profileId);
                            MDMConfigHandler.getInstance().republishProfileBasedOnPlatform(platformType, publishProperties);
                            tempCollectionIdsList.add(collectionId);
                            final String remarks = "publish-success";
                            publishLog.put((Object)"REMARKS", (Object)remarks);
                        }
                        MDMOneLineLogger.log(Level.INFO, "PUBLISH_PROFILE", publishLog);
                    }
                    PayloadSecretFieldsMigrationUtil.updateDynamicVariableForCommand(tempCollectionIdsList);
                    PayloadSecretFieldsMigrationHandler.logger.log(Level.INFO, "Republishing Profiles with secret fields ended for customer {0}", customerId);
                }
            }
        }
    }
    
    static {
        PayloadSecretFieldsMigrationHandler.logger = Logger.getLogger("MDMDeviceSecurityLogger");
    }
}
