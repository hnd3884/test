package com.me.idps.mdm.sync;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.idps.core.crud.DomainDataProvider;
import java.util.concurrent.TimeUnit;
import org.json.simple.parser.JSONParser;
import com.adventnet.ds.query.DeleteQuery;
import java.text.MessageFormat;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.simple.JSONObject;
import com.me.idps.core.util.IdpsUtil;
import org.json.simple.JSONArray;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import java.util.Properties;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Iterator;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.idps.core.sync.events.DirectoryEventsUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.mdm.directory.sync.mdm.MDMDirectoryProductImpl;
import com.me.mdm.server.tracker.mics.MICSGroupFeatureController;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import java.util.HashMap;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import java.sql.Connection;

class MDMDirectoryDataPersistor
{
    private static final String BLOCK = "MDMDirectoryDataPersistor";
    private static MDMDirectoryDataPersistor mdmDirectoryDataPersistor;
    
    static MDMDirectoryDataPersistor getInstance() {
        if (MDMDirectoryDataPersistor.mdmDirectoryDataPersistor == null) {
            MDMDirectoryDataPersistor.mdmDirectoryDataPersistor = new MDMDirectoryDataPersistor();
        }
        return MDMDirectoryDataPersistor.mdmDirectoryDataPersistor;
    }
    
    private void insertDirResIntoMDMResource(final Connection connection, final Long dmDomainID, final Long collationID) throws Exception {
        final Column resIDcol = Column.getColumn("Resource", "RESOURCE_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
        selectQuery.addJoin(new Join("DirResRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MDMResource", "RESOURCE_ID"), (Object)null, 0).and(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)));
        selectQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(resIDcol))));
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        colMap.put("RESOURCE_ID", resIDcol);
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, selectQuery, "MDMResource", colMap, null, "MDMDirectoryDataPersistor", null, false);
    }
    
    private void insertCustomGroup(final Connection connection, final Long dmDomainID, final Long collationID) throws Exception {
        IDPSlogger.DBO.log(Level.INFO, "inserting into customgroup...");
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        colMap.put("RESOURCE_ID", Column.getColumn("DirResRel", "RESOURCE_ID"));
        colMap.put("GROUP_TYPE", Column.getColumn("CustomGroup", "GROUP_TYPE"));
        colMap.put("IS_EDITABLE", Column.getColumn("CustomGroup", "IS_EDITABLE"));
        colMap.put("GROUP_CATEGORY", Column.getColumn("CustomGroup", "GROUP_CATEGORY"));
        final MDMDirectoryProductImpl mdmIDPSimpl = MDMIdpsUtil.getMDMdirProdImpl();
        final int cgType = mdmIDPSimpl.getDirectoryCGType();
        final int cgCategory = mdmIDPSimpl.getDirectoryCGCategory();
        final HashMap<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.put("CustomGroup.IS_EDITABLE", String.valueOf(Boolean.FALSE));
        replaceMap.put("\"CustomGroup\".\"IS_EDITABLE\"", "'" + String.valueOf(Boolean.FALSE) + "'");
        replaceMap.put("CustomGroup.GROUP_TYPE", String.valueOf(cgType));
        replaceMap.put("\"CustomGroup\".\"GROUP_TYPE\"", String.valueOf(cgType));
        replaceMap.put("CustomGroup.GROUP_CATEGORY", String.valueOf(cgCategory));
        replaceMap.put("\"CustomGroup\".\"GROUP_CATEGORY\"", String.valueOf(cgCategory));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
        selectQuery.addJoin(new Join("DirResRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)null, 0).and(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0)).and(new Criteria(Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"), (Object)new Integer[] { 7, 1003 }, 8)));
        final int groupCreatedCount = DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, selectQuery, "CustomGroup", colMap, replaceMap, "MDMDirectoryDataPersistor", null, false);
        if (groupCreatedCount > 0) {
            MICSGroupFeatureController.addTrackingData(7, MICSGroupFeatureController.GroupOperation.CREATE, false);
        }
    }
    
    private void insertCustomGroupExtn(final Connection connection, final Long dmDomainID, final Long collationID, final Long syncInitiatedBy) throws Exception {
        IDPSlogger.DBO.log(Level.INFO, "inserting into customgroupext...");
        final Column resIDcol = Column.getColumn("CustomGroup", "RESOURCE_ID");
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        colMap.put("RESOURCE_ID", resIDcol);
        colMap.put("CREATED_BY", Column.getColumn("CustomGroupExtn", "CREATED_BY"));
        colMap.put("LAST_MODIFIED_BY", Column.getColumn("CustomGroupExtn", "LAST_MODIFIED_BY"));
        final HashMap<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.put("CustomGroupExtn.CREATED_BY", String.valueOf(syncInitiatedBy));
        replaceMap.put("\"CustomGroupExtn\".\"CREATED_BY\"", String.valueOf(syncInitiatedBy));
        replaceMap.put("CustomGroupExtn.LAST_MODIFIED_BY", String.valueOf(syncInitiatedBy));
        replaceMap.put("\"CustomGroupExtn\".\"LAST_MODIFIED_BY\"", String.valueOf(syncInitiatedBy));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
        selectQuery.addJoin(new Join("DirResRel", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.setCriteria(DirectoryGrouper.getInstance().getCGcri().and(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("CustomGroupExtn", "RESOURCE_ID"), (Object)null, 0)));
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, selectQuery, "CustomGroupExtn", colMap, replaceMap, "MDMDirectoryDataPersistor", null, false);
    }
    
    private void updateResourceToProfileSummary(final Connection connection) throws Exception {
        IDPSlogger.DBO.log(Level.INFO, "updating res summary");
        final MDMDirectoryProductImpl mdmIDPSimpl = MDMIdpsUtil.getMDMdirProdImpl();
        mdmIDPSimpl.updateResSummary(connection);
        IDPSlogger.DBO.log(Level.INFO, "updated res summary");
    }
    
    private void getModifiedObjDetailsBasedOnMDMtables(final Connection connection, final Long dmDomainID, final Long collationID, final int resType) throws Exception {
        final HashMap<Long, Column> attrColMap = MDMIdpsUtil.getObjAttrColMap(resType);
        if (attrColMap != null) {
            IdpEventConstants eventType = null;
            if (resType == 2) {
                eventType = IdpEventConstants.USER_MODIFIED_EVENT;
            }
            else if (resType == 101) {
                eventType = IdpEventConstants.GROUP_MODIFIED_EVENT;
            }
            final Iterator itr = attrColMap.entrySet().iterator();
            while (itr != null && itr.hasNext()) {
                final Map.Entry<Long, Column> entry = itr.next();
                final Long curAttrID = entry.getKey();
                final List<Column> productValCols = new ArrayList<Column>(Arrays.asList(entry.getValue()));
                if (curAttrID.equals(103L) && resType == 101) {
                    productValCols.add(Column.getColumn("CustomGroup", "DESCRIPTION"));
                }
                for (int i = 0; i < productValCols.size(); ++i) {
                    final Column productValCol = productValCols.get(i);
                    String baseTableJoinCol = null;
                    final String baseTableName = productValCol.getTableAlias();
                    if (baseTableName.equalsIgnoreCase("ManagedUser")) {
                        baseTableJoinCol = "MANAGED_USER_ID";
                    }
                    else if (baseTableName.equalsIgnoreCase("CustomGroup")) {
                        baseTableJoinCol = "RESOURCE_ID";
                    }
                    else if (baseTableName.equalsIgnoreCase("CustomGroupExtn")) {
                        baseTableJoinCol = "RESOURCE_ID";
                    }
                    if (!SyMUtil.isStringEmpty(baseTableJoinCol)) {
                        final Join statusJoin = new Join(baseTableName, "DirObjRegIntVal", new String[] { baseTableJoinCol }, new String[] { "RESOURCE_ID" }, 2);
                        final Join dirObjAttrValJoin = new Join(baseTableName, "DirObjRegStrVal", new String[] { baseTableJoinCol }, new String[] { "RESOURCE_ID" }, 2);
                        final Criteria criteria = new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)curAttrID, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(productValCol, (Object)null, 0).or(new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)productValCol, 1, true))).and(new Criteria(Column.getColumn("DirObjRegIntVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)1, 0)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0)));
                        Long dirEventID = null;
                        if (i == 0) {
                            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
                            selectQuery.addJoin(statusJoin);
                            selectQuery.addJoin(dirObjAttrValJoin);
                            selectQuery.setCriteria(criteria);
                            final Column resIDcol = Column.getColumn(baseTableName, baseTableJoinCol);
                            final Column eventDetailCol = Column.getColumn("DirObjRegStrVal", "ATTR_ID");
                            final Column eventTimeStampValCol = Column.getColumn("DirObjRegStrVal", "MODIFIED_AT");
                            dirEventID = DirectoryEventsUtil.getInstance().populateEvents(connection, dmDomainID, collationID, eventType, selectQuery, resIDcol, eventTimeStampValCol, eventDetailCol);
                        }
                        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(baseTableName);
                        updateQuery.addJoin(statusJoin);
                        updateQuery.addJoin(dirObjAttrValJoin);
                        updateQuery.setCriteria(criteria);
                        updateQuery.setUpdateColumn(productValCol.getColumnName(), (Object)Column.getColumn("DirObjRegStrVal", "VALUE"));
                        final int groupUpdatedCount = DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "MDMDirectoryDataPersistor", null, false);
                        if (groupUpdatedCount > 0) {
                            MICSGroupFeatureController.addTrackingData(7, MICSGroupFeatureController.GroupOperation.EDIT, false);
                        }
                        if (i == 0 && dirEventID != null) {
                            DirectoryEventsUtil.getInstance().markEventSucceeded(connection, dmDomainID, dirEventID);
                        }
                    }
                }
            }
        }
    }
    
    private void performMDMcoreSyncOps(final Connection connection, final Long collationID, final Properties dmDomainProps, final Long aaaUserID, final List<Integer> objectsToBeSynced) throws Exception {
        final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
        final Integer dmDomainClientID = ((Hashtable<K, Integer>)dmDomainProps).get("CLIENT_ID");
        DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", MDMI18N.getI18Nmsg("mdm.ad.post.msg"));
        this.insertDirResIntoMDMResource(connection, dmDomainID, collationID);
        if (objectsToBeSynced != null && !objectsToBeSynced.isEmpty()) {
            if (objectsToBeSynced.contains(2)) {
                this.getModifiedObjDetailsBasedOnMDMtables(connection, dmDomainID, collationID, 2);
            }
            if (objectsToBeSynced.contains(7) || objectsToBeSynced.contains(1003)) {
                this.insertCustomGroup(connection, dmDomainID, collationID);
                this.insertCustomGroupExtn(connection, dmDomainID, collationID, aaaUserID);
                DirectoryGrouper.getInstance().insertAndDelCGrel(connection, dmDomainID, collationID, dmDomainClientID, objectsToBeSynced);
                this.updateResourceToProfileSummary(connection);
                this.getModifiedObjDetailsBasedOnMDMtables(connection, dmDomainID, collationID, 101);
            }
        }
    }
    
    void handleProductOps(final Connection connection, final Long collationID, final Properties dmDomainProps, final Long aaaUserID) throws Exception {
        try {
            final List<Integer> objectTypesToBeSynced = DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced(((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID"));
            this.performMDMcoreSyncOps(connection, collationID, dmDomainProps, aaaUserID, objectTypesToBeSynced);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in handling product specific ops", ex);
            throw ex;
        }
    }
    
    void checkForCGcleanUp(final Connection connection) throws Exception {
        final JSONArray jsonArray = MDMIdpsUtil.getDuplicatedGroupDomains(connection, "DirectoryResourceRel", "RESOURCE_ID");
        final JSONArray domainsToCleanUpCG = new JSONArray();
        if (jsonArray != null && !jsonArray.isEmpty()) {
            IDPSlogger.AUDIT.log(Level.INFO, "cg duplicated in {0}", new Object[] { IdpsUtil.getPrettyJSON(jsonArray) });
            for (int i = 0; i < jsonArray.size(); ++i) {
                final JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                if (jsonObject.containsKey((Object)"DOMAIN_NETBIOS_NAME")) {
                    final String curDomainName = String.valueOf(jsonObject.get((Object)"DOMAIN_NETBIOS_NAME"));
                    if (!SyMUtil.isStringEmpty(curDomainName) && !domainsToCleanUpCG.contains((Object)curDomainName)) {
                        domainsToCleanUpCG.add((Object)curDomainName);
                    }
                }
            }
            IDPSlogger.AUDIT.log(Level.INFO, "have to cleanup cg in {0}", new Object[] { IdpsUtil.getPrettyJSON(domainsToCleanUpCG) });
            if (!domainsToCleanUpCG.isEmpty()) {
                ApiFactoryProvider.getCacheAccessAPI().putCache("HARD_RESET_POST_CG_CLEANUP", (Object)domainsToCleanUpCG.toString(), 2);
            }
        }
    }
    
    private boolean doDomainCGcleanUp(final Long customerID, final String dmDomainName, final Long dmDomainID) {
        try {
            IDPSlogger.DBO.log(Level.INFO, "to be sure:updating group summary");
            MDMIdpsUtil.getMDMdirProdImpl().updateResSummary(101, true);
            IDPSlogger.DBO.log(Level.INFO, "updated group summary");
            final Column domainNameCol = Column.getColumn("DMDomain", "NAME");
            final Column resNetbiosNameCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
            final Criteria domainNameCri = new Criteria(domainNameCol, (Object)dmDomainName, 0, false).and(DirectoryQueryutil.getInstance().getResCri(dmDomainName, customerID)).and(new Criteria(resNetbiosNameCol, (Object)domainNameCol, 0, false)).and(new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Column.getColumn("DMDomain", "CUSTOMER_ID"), 0));
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("Resource");
            deleteQuery.addJoin(new Join("Resource", "DMDomain", domainNameCri, 2));
            deleteQuery.addJoin(new Join("Resource", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("CustomGroup", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("CustomGroup", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            deleteQuery.setCriteria(domainNameCri.and(DirectoryGrouper.getInstance().getCGcri()).and(new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)null, 0)).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0)).and(new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)1, 1)).and(new Criteria(Column.getColumn("ResourceToProfileSummary", "APP_COUNT"), (Object)0, 0)).and(new Criteria(Column.getColumn("ResourceToProfileSummary", "DOC_COUNT"), (Object)0, 0)).and(new Criteria(Column.getColumn("ResourceToProfileSummary", "PROFILE_COUNT"), (Object)0, 0)));
            final int delCount = DirectoryQueryutil.getInstance().executeDeleteQuery(deleteQuery, false);
            IDPSlogger.AUDIT.log(Level.INFO, "deleted {0} duplicated dir synced cg for {1}", new Object[] { delCount, dmDomainName });
            return true;
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, MessageFormat.format("exception in clearing duplicates for dir synced CG for {0}", dmDomainName), ex);
            return false;
        }
    }
    
    void cgCleanUp(final Long customerID, final String dmDomainName, final Long dmDomainID) {
        try {
            final String domainsWithDuplCG = String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache("HARD_RESET_POST_CG_CLEANUP", 2));
            if (!SyMUtil.isStringEmpty(domainsWithDuplCG)) {
                JSONArray domainCGcleanup = null;
                try {
                    domainCGcleanup = (JSONArray)new JSONParser().parse(domainsWithDuplCG);
                }
                catch (final Exception ex) {
                    IDPSlogger.ERR.log(Level.SEVERE, MessageFormat.format("couldn't parse {0}", domainsWithDuplCG), ex);
                }
                if (domainCGcleanup != null && !domainCGcleanup.isEmpty()) {
                    IDPSlogger.AUDIT.log(Level.INFO, "cache has cleanup cg domains as {0}", new Object[] { IdpsUtil.getPrettyJSON(domainCGcleanup) });
                    final JSONArray newDomainCGcleanup = new JSONArray();
                    for (int i = 0; i < domainCGcleanup.size(); ++i) {
                        final String domainName = (String)domainCGcleanup.get(i);
                        if (!SyMUtil.isStringEmpty(domainName)) {
                            boolean domainCleanedUp = false;
                            if (domainName.equalsIgnoreCase(dmDomainName)) {
                                domainCleanedUp = this.doDomainCGcleanUp(customerID, domainName, dmDomainID);
                            }
                            if (!domainCleanedUp) {
                                newDomainCGcleanup.add((Object)domainName);
                            }
                        }
                    }
                    IDPSlogger.AUDIT.log(Level.INFO, "now cache cleanup cg domains are {0}", new Object[] { IdpsUtil.getPrettyJSON(newDomainCGcleanup) });
                    ApiFactoryProvider.getCacheAccessAPI().putCache("HARD_RESET_POST_CG_CLEANUP", (Object)newDomainCGcleanup, 2);
                }
                else {
                    ApiFactoryProvider.getCacheAccessAPI().removeCache("HARD_RESET_POST_CG_CLEANUP", 2);
                }
            }
            else {
                ApiFactoryProvider.getCacheAccessAPI().removeCache("HARD_RESET_POST_CG_CLEANUP", 2);
            }
        }
        catch (final Exception ex2) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
        }
    }
    
    UpdateQuery dirToManagedDeviceRel(final Long dmdomainId, final Criteria nullCheck) {
        final Criteria deviceMapCriteria = new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmdomainId, 0).and(new Criteria(Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"), (Object)201, 0)).and(new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)121L, 0)).and(new Criteria(Column.getColumn("MdCertificateInfo", "CERTIFICATE_ISSUER_DN"), (Object)"*MS-Organization-Access*", 2));
        final UpdateQuery dirResRelToManagedDevice = (UpdateQuery)new UpdateQueryImpl("DirResRel");
        dirResRelToManagedDevice.addJoin(new Join("DirResRel", "DirObjRegStrVal", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 1));
        dirResRelToManagedDevice.addJoin(new Join("DirObjRegStrVal", "MdCertificateInfo", new String[] { "VALUE" }, new String[] { "CERTIFICATE_NAME" }, 1));
        dirResRelToManagedDevice.addJoin(new Join("MdCertificateInfo", "MdCertificateResourceRel", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 1));
        dirResRelToManagedDevice.addJoin(new Join("MdCertificateResourceRel", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        if (nullCheck != null) {
            dirResRelToManagedDevice.setCriteria(deviceMapCriteria.and(nullCheck));
        }
        else {
            dirResRelToManagedDevice.setCriteria(deviceMapCriteria);
        }
        dirResRelToManagedDevice.setUpdateColumn("RESOURCE_ID", (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        return dirResRelToManagedDevice;
    }
    
    void handleAzureUsersPostedAsGroups(final Connection connection, final String dmDomainName, final Long customerID, final Long dmDomainID, final Criteria tempCri) throws Exception {
        final String domainsWithUsersAsCGchecked = String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache("HANDLE_AZURE_USERS_POSTED_AS_CG", 2));
        JSONArray azureDomainCGcleanup = new JSONArray();
        if (!SyMUtil.isStringEmpty(domainsWithUsersAsCGchecked)) {
            try {
                azureDomainCGcleanup = (JSONArray)new JSONParser().parse(domainsWithUsersAsCGchecked);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, MessageFormat.format("couldn't parse {0}", domainsWithUsersAsCGchecked), ex);
                return;
            }
        }
        if (!azureDomainCGcleanup.contains((Object)dmDomainName)) {
            MDMIdpsUtil.getMDMdirProdImpl().updateResSummary(101, true);
            final Column domainNameCol = Column.getColumn("DMDomain", "NAME");
            final Column resNetbiosNameCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
            final Criteria domainNameCri = new Criteria(domainNameCol, (Object)dmDomainName, 0, false).and(DirectoryQueryutil.getInstance().getResCri(dmDomainName, customerID)).and(new Criteria(resNetbiosNameCol, (Object)domainNameCol, 0, false)).and(new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0)).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0)).and(new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)3, 0)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Column.getColumn("DMDomain", "CUSTOMER_ID"), 0));
            final Criteria dirTempCri = tempCri.and(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirObjTmp", "OBJECT_TYPE"), (Object)2, 0));
            final Join tempJoin = new Join("DirResRel", "DirObjTmp", new String[] { "GUID" }, new String[] { "GUID" }, 2);
            final Join dmDomainJoin = new Join("DirResRel", "DMDomain", new String[] { "DM_DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2);
            DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("Resource");
            deleteQuery.addJoin(new Join("Resource", "DMDomain", domainNameCri, 2));
            deleteQuery.addJoin(new Join("Resource", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("CustomGroup", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("CustomGroup", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(tempJoin);
            deleteQuery.setCriteria(domainNameCri.and(DirectoryGrouper.getInstance().getCGcri()).and(new Criteria(Column.getColumn("ResourceToProfileSummary", "APP_COUNT"), (Object)0, 0)).and(new Criteria(Column.getColumn("ResourceToProfileSummary", "DOC_COUNT"), (Object)0, 0)).and(new Criteria(Column.getColumn("ResourceToProfileSummary", "PROFILE_COUNT"), (Object)0, 0)).and(dirTempCri));
            int delCount = DirectoryQueryutil.getInstance().executeDeleteQuery(deleteQuery, false);
            IDPSlogger.AUDIT.log(Level.INFO, "deleted {0} users posted as groups for {1}", new Object[] { delCount, dmDomainName });
            deleteQuery = (DeleteQuery)new DeleteQueryImpl("Resource");
            deleteQuery.addJoin(new Join("Resource", "DMDomain", domainNameCri, 2));
            deleteQuery.addJoin(new Join("Resource", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("Resource", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(tempJoin);
            deleteQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            deleteQuery.setCriteria(dirTempCri.and(domainNameCri).and(new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)null, 0)).and(new Criteria(Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"), (Object)7, 0)));
            delCount = DirectoryQueryutil.getInstance().executeDeleteQuery(deleteQuery, false);
            IDPSlogger.AUDIT.log(Level.INFO, "deleted {0} users posted as cg for {1}", new Object[] { delCount, dmDomainName });
            deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjArrLngVal");
            deleteQuery.addJoin(new Join("DirObjArrLngVal", "DirResRel", new String[] { "DM_DOMAIN_ID", "VALUE" }, new String[] { "DM_DOMAIN_ID", "OBJ_ID" }, 2));
            deleteQuery.addJoin(tempJoin);
            deleteQuery.addJoin(dmDomainJoin);
            deleteQuery.addJoin(new Join("DirObjArrLngVal", "Resource", new String[] { "VALUE_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            deleteQuery.setCriteria(dirTempCri.and(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)null, 0)).and(new Criteria(Column.getColumn("DirObjArrLngVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)));
            delCount = DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
            IDPSlogger.AUDIT.log(Level.INFO, "deleted {0} attributes posted as groups for {1}", new Object[] { delCount, dmDomainName });
            deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirResRel");
            deleteQuery.addJoin(tempJoin);
            deleteQuery.addJoin(dmDomainJoin);
            deleteQuery.addJoin(new Join("DirResRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            deleteQuery.setCriteria(dirTempCri.and(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)null, 0)));
            delCount = DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
            IDPSlogger.AUDIT.log(Level.INFO, "deleted {0} dir user objects posted as groups for {1}", new Object[] { delCount, dmDomainName });
            azureDomainCGcleanup.add((Object)dmDomainName);
            final int dayInSeconds = Integer.valueOf(String.valueOf(TimeUnit.DAYS.toSeconds(3L)));
            ApiFactoryProvider.getCacheAccessAPI().putCache("HANDLE_AZURE_USERS_POSTED_AS_CG", (Object)azureDomainCGcleanup, 2, dayInSeconds);
            final SelectQuery azureDomainsQuery = DomainDataProvider.getDMManagedDomainQuery(customerID, null, null, 3);
            final List<Properties> dmDomainProps = DMDomainDataHandler.getInstance().getDomains(azureDomainsQuery);
            if (dmDomainProps != null && dmDomainProps.size() == azureDomainCGcleanup.size()) {
                MDMIdpsUtil.getMDMdirProdImpl().updateFeatureAvailability("HANDLE_AZURE_USERS_POSTED_AS_CG", String.valueOf(false));
            }
        }
    }
    
    void remDefaultAzureDevice(final Connection connection) throws Exception {
        final Criteria cri = new Criteria(Column.getColumn("UserParams", "PARAM_VALUE"), (Object)String.valueOf(true), 0, false).and(new Criteria(Column.getColumn("UserParams", "PARAM_NAME"), (Object)"skipazurecagettingstarted", 0, false));
        final DataObject dobj = SyMUtil.getPersistenceLite().get("UserParams", cri);
        if (dobj != null && dobj.isEmpty()) {
            final SelectQuery query = DomainDataProvider.getDMManagedDomainQuery(null, null, null, 3);
            final List<Properties> dmDomainProps = DMDomainDataHandler.getInstance().getDomains(query);
            if (dmDomainProps != null && !dmDomainProps.isEmpty()) {
                for (int i = 0; i < dmDomainProps.size(); ++i) {
                    final Properties dmDomainProp = dmDomainProps.get(i);
                    final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProp).get("DOMAIN_ID");
                    final Integer dmDomainClientID = ((Hashtable<K, Integer>)dmDomainProp).get("CLIENT_ID");
                    if (dmDomainClientID == 3) {
                        final List<Integer> objectsToBeSynced = DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced(dmDomainID);
                        if (!objectsToBeSynced.contains(205)) {
                            final Criteria criteria = new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"), (Object)201, 0));
                            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirResRel");
                            deleteQuery.setCriteria(criteria);
                            DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
                            DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateDirectorySyncSettings(dmDomainProp, 201, false);
                        }
                    }
                }
            }
        }
        final DeleteQuery deleteQuery2 = (DeleteQuery)new DeleteQueryImpl("Resource");
        deleteQuery2.setCriteria(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)201, 0));
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery2, false);
    }
    
    static {
        MDMDirectoryDataPersistor.mdmDirectoryDataPersistor = null;
    }
}
