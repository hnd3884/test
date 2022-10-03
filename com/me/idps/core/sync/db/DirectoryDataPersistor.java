package com.me.idps.core.sync.db;

import java.util.Hashtable;
import com.me.idps.core.util.DirectoryAttributeConstants;
import java.util.Properties;
import com.me.idps.core.sync.asynch.DirectoryDataReceiver;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.me.idps.core.sync.events.IdpEventConstants;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.MessageFormat;
import com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl;
import java.util.HashMap;
import org.json.simple.JSONArray;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.simple.JSONObject;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.adventnet.i18n.I18N;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.idps.core.util.DirectoryUtil;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import java.sql.Connection;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;

class DirectoryDataPersistor
{
    private static final String BLOCK = "coreSyncEngine";
    private static final String TEMP_BLOCK = "DirectoryTempDataHandler";
    private static DirectoryDataPersistor directoryDataPersistor;
    
    static DirectoryDataPersistor getInstance() {
        if (DirectoryDataPersistor.directoryDataPersistor == null) {
            DirectoryDataPersistor.directoryDataPersistor = new DirectoryDataPersistor();
        }
        return DirectoryDataPersistor.directoryDataPersistor;
    }
    
    Criteria getTempValidCri() {
        return new Criteria(Column.getColumn("DirObjTmp", "IS_INVALID"), (Object)Boolean.TRUE, 1);
    }
    
    Criteria getTempDomCri(final Long dmDomainID) {
        return new Criteria(Column.getColumn("DirObjTmp", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
    }
    
    Criteria getTempCollCri(final Long collationID) {
        return new Criteria(Column.getColumn("DirObjTmp", "COLLATION_ID"), (Object)collationID, 0);
    }
    
    private String getTempIDcol(final String tableName) {
        return DirectoryQueryutil.getTempIDcol(tableName);
    }
    
    private String getObjIDcol(final String tableName) {
        return DirectoryQueryutil.getObjIDcol(tableName);
    }
    
    private String getCollIDcol(final String tableName) {
        return DirectoryQueryutil.getCollIDcol(tableName);
    }
    
    private String getDomainIDcol(final String tableName) {
        return DirectoryQueryutil.getDomainIDcol(tableName);
    }
    
    private String getResIDcol(final String tableName) {
        return DirectoryQueryutil.getResIDcol(tableName);
    }
    
    private String getDirResTypecol(final String tableName) {
        return DirectoryQueryutil.getDirResTypecol(tableName);
    }
    
    Criteria getTempValValiCri(final String tableName) {
        return new Criteria(Column.getColumn(tableName, DirectoryQueryutil.getInvalidcol(tableName)), (Object)Boolean.TRUE, 1);
    }
    
    Criteria getTempBaseCri(final Long dmDomainID, final Long collationID) {
        return this.getTempDomCri(dmDomainID).and(this.getTempCollCri(collationID)).and(this.getTempValidCri());
    }
    
    Criteria getTempValDomCri(final String tableName, final Long dmDomainID) {
        return new Criteria(Column.getColumn(tableName, this.getDomainIDcol(tableName)), (Object)dmDomainID, 0);
    }
    
    Criteria getTempValCollCri(final String tableName, final Long collationID) {
        return new Criteria(Column.getColumn(tableName, this.getCollIDcol(tableName)), (Object)collationID, 0);
    }
    
    Criteria getTempValBaseCri(final String tableName, final Long dmDomainID, final Long collationID) {
        return this.getTempValDomCri(tableName, dmDomainID).and(this.getTempValCollCri(tableName, collationID)).and(this.getTempValValiCri(tableName));
    }
    
    private void preValidatePropogation(final Connection connection, final Join tempTokenJoin, final Criteria cri, final Long dmDomainID, final Long collationID) throws Exception {
        final SelectQuery dirObjTempCollValidatorQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjTmp"));
        dirObjTempCollValidatorQuery.addJoin(tempTokenJoin);
        dirObjTempCollValidatorQuery.setCriteria(cri.and(new Criteria(Column.getColumn("DirObjTmp", "OBJ_ID"), (Object)null, 1).or(new Criteria(Column.getColumn("DirObjTmp", "DM_DOMAIN_ID"), (Object)null, 1)).or(new Criteria(Column.getColumn("DirObjTmp", "COLLATION_ID"), (Object)null, 1)).or(new Criteria(Column.getColumn("DirObjTmp", "MAX_ADDED_AT"), (Object)null, 1)).or(new Criteria(Column.getColumn("DirObjTmp", "DUPLICATED_MAX_TEMP_ID"), (Object)null, 1))));
        dirObjTempCollValidatorQuery.addSelectColumn(IdpsUtil.getCountOfColumn("DirObjTmp", "TEMP_ID", "count"));
        if (!DirectoryUtil.getInstance().canExecQuery(dmDomainID, collationID)) {
            return;
        }
        final int invalidTempobjCount = DBUtil.getRecordCount(connection, RelationalAPI.getInstance().getSelectSQL((Query)dirObjTempCollValidatorQuery));
        if (invalidTempobjCount == 0) {
            return;
        }
        throw new Exception("somebody else has bit our apple");
    }
    
    private void propogateCollation(final Connection connection, final String tableName, final Long dmDomainID, final Long collationID, final Criteria tempCri) throws Exception {
        final String tempIDcol = this.getTempIDcol(tableName);
        final String collIDcol = this.getCollIDcol(tableName);
        final String domainIDcol = this.getDomainIDcol(tableName);
        final Join tempValJoin = new Join(tableName, "DirObjTmp", new String[] { tempIDcol }, new String[] { "TEMP_ID" }, 2);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(tableName);
        updateQuery.addJoin(tempValJoin);
        updateQuery.setCriteria(tempCri.and(this.getTempValValiCri(tableName)));
        updateQuery.setUpdateColumn(collIDcol, (Object)collationID);
        updateQuery.setUpdateColumn(domainIDcol, (Object)dmDomainID);
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", null, false);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
        deleteQuery.addJoin(tempValJoin);
        deleteQuery.setCriteria(tempCri.and(new Criteria(Column.getColumn(tableName, collIDcol), (Object)null, 0).or(new Criteria(Column.getColumn(tableName, domainIDcol), (Object)null, 0))));
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "DirectoryTempDataHandler", null, false);
    }
    
    private void propogateCollation(final Connection connection, final Long dmDomainID, final Long collationID, final Criteria tempCri) throws Exception {
        DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", I18N.getMsg("mdm.ad.collation", new Object[0]));
        final Criteria cri = this.getTempValidCri().and(new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)collationID, 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)931, 0));
        final Join tempTokenJoin = new Join("DirObjTmp", "DirectorySyncDetails", new String[] { "SYNC_TOKEN_ID" }, new String[] { "SYNC_TOKEN_ID" }, 2);
        this.preValidatePropogation(connection, tempTokenJoin, cri, dmDomainID, collationID);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjTmp");
        updateQuery.addJoin(tempTokenJoin);
        updateQuery.setCriteria(cri);
        updateQuery.setUpdateColumn("DM_DOMAIN_ID", (Object)dmDomainID);
        updateQuery.setUpdateColumn("COLLATION_ID", (Object)collationID);
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", null, false);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmp");
        deleteQuery.addJoin(tempTokenJoin);
        deleteQuery.setCriteria(cri.and(new Criteria(Column.getColumn("DirObjTmp", "DM_DOMAIN_ID"), (Object)null, 0).or(new Criteria(Column.getColumn("DirObjTmp", "COLLATION_ID"), (Object)null, 0))));
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "DirectoryTempDataHandler", null, false);
        this.propogateCollation(connection, "DirObjTmpRegIntVal", dmDomainID, collationID, tempCri);
        this.propogateCollation(connection, "DirObjTmpRegStrVal", dmDomainID, collationID, tempCri);
        this.propogateCollation(connection, "DirObjTmpArrStrVal", dmDomainID, collationID, tempCri);
    }
    
    private void bridgeTempResWithDirResRel(final Connection connection, final Criteria tempCri, final Long dmDomainID, final Integer dmDomainClientID, final Long collationID) throws Exception {
        final Criteria dmDomainResCri = new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
        final Criteria cri = tempCri.and(dmDomainResCri);
        UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjTmp");
        if (IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).isGUIDresTypeunique()) {
            updateQuery.addJoin(new Join("DirObjTmp", "DirResRel", new String[] { "GUID", "OBJECT_TYPE" }, new String[] { "GUID", "DIR_RESOURCE_TYPE" }, 2));
        }
        else {
            updateQuery.addJoin(new Join("DirObjTmp", "DirResRel", new String[] { "GUID" }, new String[] { "GUID" }, 2));
        }
        updateQuery.setCriteria(cri.and(new Criteria(Column.getColumn("DirObjTmp", "OBJ_ID"), (Object)null, 0)));
        updateQuery.setUpdateColumn("OBJ_ID", (Object)Column.getColumn("DirResRel", "OBJ_ID"));
        String logMsg = "bridging temp data to identify resource id of guid in temp tables";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
        updateQuery = (UpdateQuery)new UpdateQueryImpl("DirResRel");
        updateQuery.addJoin(new Join("DirResRel", "DirObjTmp", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
        updateQuery.setCriteria(cri.and(new Criteria(Column.getColumn("DirObjTmp", "OBJ_ID"), (Object)null, 1)));
        updateQuery.setUpdateColumn("MODIFIED_AT", (Object)Column.getColumn("DirObjTmp", "MAX_ADDED_AT"));
        logMsg = "updating modified guid entries";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "coreSyncEngine", logMsg, false);
    }
    
    private int detectNumOfNewDirObj(final Connection connection, final Criteria tempCri) throws Exception {
        int newDirObj = 0;
        final Column countCol = IdpsUtil.getCountOfColumn("DirObjTmp", "TEMP_ID", "COUNT_DIROBJTMP.TEMP_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjTmp"));
        selectQuery.setCriteria(tempCri.and(new Criteria(Column.getColumn("DirObjTmp", "OBJ_ID"), (Object)null, 0)).and(new Criteria(Column.getColumn("DirObjTmp", "ADDED_AT"), (Object)Column.getColumn("DirObjTmp", "MAX_ADDED_AT"), 0)));
        selectQuery.addSelectColumn(countCol);
        final JSONArray jsonArray = IdpsUtil.executeSelectQuery(connection, selectQuery);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            final JSONObject jsonObject = (JSONObject)jsonArray.get(0);
            newDirObj = Integer.valueOf(String.valueOf(jsonObject.get((Object)countCol.getColumnAlias())));
            if (newDirObj > 0) {
                IDPSlogger.DBO.log(Level.INFO, "detected {0} new objects to be inserted", new Object[] { String.valueOf(newDirObj) });
            }
        }
        return newDirObj;
    }
    
    private int prepareData(final Connection connection, final Long dmDomainID, final Integer dmDomainClientID, final Long collationID, final Criteria tempCri) throws Exception {
        DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", I18N.getMsg("mdm.ad.prep.data", new Object[0]));
        this.bridgeTempResWithDirResRel(connection, tempCri, dmDomainID, dmDomainClientID, collationID);
        final int newObjCount = this.detectNumOfNewDirObj(connection, tempCri);
        return newObjCount;
    }
    
    void validatePKallocation(final Connection connection, final Long dmDomainID, final Long pkEnd) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DirResRel", "OBJ_ID"), (Object)pkEnd, 5).and(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)));
        final Column objCountCol = IdpsUtil.getCountOfColumn("DirResRel", "OBJ_ID", "OBJ_ID_COUNT");
        selectQuery.addSelectColumn(objCountCol);
        final JSONArray jsArray = IdpsUtil.executeSelectQuery(connection, selectQuery);
        if (jsArray != null && !jsArray.isEmpty()) {
            final JSONObject jsonObject = (JSONObject)jsArray.get(0);
            final int pkEndCount = Integer.valueOf(String.valueOf(jsonObject.get((Object)objCountCol.getColumnAlias())));
            if (pkEndCount > 0) {
                throw new Exception("PK_EXCEEDED_ERROR");
            }
        }
    }
    
    private void transmitDirObjIDtoVal(final Connection connection, final String tableName, final Long dmDomainID, final Long collationID, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap) throws Exception {
        final String objIDcol = this.getObjIDcol(tableName);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(tableName);
        updateQuery.addJoin(new Join(tableName, "DirObjTmp", new String[] { this.getTempIDcol(tableName) }, new String[] { "TEMP_ID" }, 2));
        updateQuery.setCriteria(tempCri.and((Criteria)tempValCriMap.get(tableName)).and(new Criteria(Column.getColumn(tableName, objIDcol), (Object)null, 0)).and(new Criteria(Column.getColumn("DirObjTmp", "OBJ_ID"), (Object)null, 1)));
        updateQuery.setUpdateColumn(objIDcol, (Object)Column.getColumn("DirObjTmp", "OBJ_ID"));
        final String logMsg = "transmitting resource_id to tempattrval table";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    private void insertIntoDirResRel(final Connection connection, final JSONObject taskDetails, final Long dmDomainID, final Long collationID, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap, final Criteria resCri) throws Exception {
        if (taskDetails.containsKey((Object)"PK_START") && taskDetails.containsKey((Object)"PK_END")) {
            final Long pkStart = Long.valueOf(String.valueOf(taskDetails.get((Object)"PK_START")));
            final Long pkEnd = Long.valueOf(String.valueOf(taskDetails.get((Object)"PK_END")));
            final HashMap<String, Column> colMap = new HashMap<String, Column>();
            colMap.put("GUID", Column.getColumn("DirObjTmp", "GUID"));
            colMap.put("OBJ_ID", Column.getColumn("DirObjTmp", "OBJ_ID"));
            colMap.put("ADDED_AT", Column.getColumn("DirObjTmp", "MAX_ADDED_AT"));
            colMap.put("MODIFIED_AT", Column.getColumn("DirObjTmp", "MAX_ADDED_AT"));
            colMap.put("DM_DOMAIN_ID", Column.getColumn("DirObjTmp", "DM_DOMAIN_ID"));
            colMap.put("DIR_RESOURCE_TYPE", Column.getColumn("DirObjTmp", "OBJECT_TYPE"));
            final HashMap<String, String> replaceMap = new HashMap<String, String>();
            replaceMap.put("DirObjTmp.OBJ_ID,", String.valueOf(pkStart - 1L) + " + row_number() over(order by MAX_ADDED_AT),");
            replaceMap.put("\"DirObjTmp\".\"OBJ_ID\",", String.valueOf(pkStart - 1L) + " + row_number() over(order by MAX_ADDED_AT),");
            final Criteria cri = tempCri.and(new Criteria(Column.getColumn("DirObjTmp", "OBJ_ID"), (Object)null, 0)).and(new Criteria(Column.getColumn("DirObjTmp", "ADDED_AT"), (Object)Column.getColumn("DirObjTmp", "MAX_ADDED_AT"), 0));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjTmp"));
            selectQuery.setCriteria(cri);
            DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, selectQuery, "DirResRel", colMap, replaceMap, "coreSyncEngine", null, false);
            this.validatePKallocation(connection, dmDomainID, pkEnd);
        }
        final Criteria dmDomainResCri = new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
        final Criteria cri2 = tempCri.and(dmDomainResCri);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjTmp");
        updateQuery.addJoin(new Join("DirObjTmp", "DirResRel", new String[] { "MAX_ADDED_AT" }, new String[] { "ADDED_AT" }, 2));
        updateQuery.setCriteria(cri2.and(new Criteria(Column.getColumn("DirObjTmp", "OBJ_ID"), (Object)null, 0)));
        updateQuery.setUpdateColumn("OBJ_ID", (Object)Column.getColumn("DirResRel", "OBJ_ID"));
        String logMsg = "bridging temp data to identify obj_id of guid in temp tables";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "coreSyncEngine", logMsg, false);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmp");
        deleteQuery.setCriteria(tempCri.and(new Criteria(Column.getColumn("DirObjTmp", "OBJ_ID"), (Object)null, 0)));
        logMsg = "deleting rows from temp table where resource id could not be mapped";
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "coreSyncEngine", logMsg, false);
        this.transmitDirObjIDtoVal(connection, "DirObjTmpRegIntVal", dmDomainID, collationID, tempCri, tempValCriMap);
        this.transmitDirObjIDtoVal(connection, "DirObjTmpRegStrVal", dmDomainID, collationID, tempCri, tempValCriMap);
        this.transmitDirObjIDtoVal(connection, "DirObjTmpArrStrVal", dmDomainID, collationID, tempCri, tempValCriMap);
    }
    
    private void reviseObjectsStatus(final Connection connection, final JSONObject taskDetails, final Long collationID) throws Exception {
        final Long dmDomainID = Long.valueOf(String.valueOf(taskDetails.get((Object)"DOMAIN_ID")));
        final Integer syncType = Integer.valueOf(String.valueOf(taskDetails.get((Object)"SYNC_TYPE")));
        final Long minSyncTokenAddedAt = Long.valueOf(String.valueOf(taskDetails.get((Object)"ADDED_AT")));
        DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", I18N.getMsg("mdm.ad.del.revise", new Object[0]));
        if (syncType != null && syncType == 1) {
            final Criteria deleteDetectCri = new Criteria(Column.getColumn("DirResRel", "MODIFIED_AT"), (Object)minSyncTokenAddedAt, 7);
            DirectoryObjDeleter.getInstance().detectDeleted(connection, taskDetails, collationID, deleteDetectCri);
        }
    }
    
    private int prepareTempData(final Connection connection, final Integer syncType, final String domainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClientID, final Long collationID, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap) throws Exception {
        DirectoryQueryutil.getInstance().prePlan(connection);
        this.propogateCollation(connection, dmDomainID, collationID, tempCri);
        DirectoryTempDataValidator.getInstance().validateTempDataBeforePersisting(connection, syncType, domainName, customerID, dmDomainID, dmDomainClientID, collationID, tempCri, tempValCriMap);
        final int newObjCount = this.prepareData(connection, dmDomainID, dmDomainClientID, collationID, tempCri);
        return newObjCount;
    }
    
    private void suspendSyncToken(final Connection connection, final JSONObject taskDetails, final Long dmDomainID, final Long collationID) throws Exception {
        IDPSlogger.DBO.log(Level.INFO, "suspending sync tokens for collID {0},{1}...", new Object[] { String.valueOf(dmDomainID), String.valueOf(collationID) });
        final JSONObject tokeDelTaskDetails = DirectoryUtil.getInstance().getNewTaskDetails(taskDetails);
        tokeDelTaskDetails.put((Object)"COLLATION_ID", (Object)collationID);
        DirectorySequenceAsynchImpl.getInstance().suspendSyncTokens(connection, tokeDelTaskDetails, dmDomainID, collationID);
    }
    
    private void transmitToVal(final Connection connection, final String tableName, final String updateColName, final String baseTableUpdateColName, final Long dmDomainID, final Long collationID) throws Exception {
        final Column updateCol = Column.getColumn("DirResRel", baseTableUpdateColName);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(tableName);
        updateQuery.addJoin(new Join(tableName, "DirResRel", new String[] { getInstance().getObjIDcol(tableName) }, new String[] { "OBJ_ID" }, 2));
        updateQuery.setCriteria(new Criteria(updateCol, (Object)null, 0).and(new Criteria(Column.getColumn(tableName, updateColName), (Object)null, 1)).or(new Criteria(updateCol, (Object)null, 1).and(new Criteria(Column.getColumn(tableName, updateColName), (Object)null, 0))).or(new Criteria(Column.getColumn(tableName, updateColName), (Object)updateCol, 1)));
        updateQuery.setUpdateColumn(updateColName, (Object)updateCol);
        final String logMsg = MessageFormat.format("transmitting {0} to attrval table", updateColName);
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "postSyncEngine", logMsg, false);
    }
    
    void transmitToVal(final Connection connection, final Long dmDomainID, final Long collationID, final List<String> columns) throws Exception {
        final List<String> tableNames = new ArrayList<String>(Arrays.asList("DirObjRegIntVal", "DirObjRegStrVal", "DirObjArrLngVal"));
        for (final String tableName : tableNames) {
            for (final String colName : columns) {
                String updateCol = null;
                final String s = colName;
                switch (s) {
                    case "RESOURCE_ID": {
                        updateCol = this.getResIDcol(tableName);
                        break;
                    }
                    case "DM_DOMAIN_ID": {
                        updateCol = this.getDomainIDcol(tableName);
                        break;
                    }
                    case "DIR_RESOURCE_TYPE": {
                        updateCol = this.getDirResTypecol(tableName);
                        break;
                    }
                }
                this.transmitToVal(connection, tableName, updateCol, colName, dmDomainID, collationID);
            }
        }
    }
    
    private void performCoreSyncDataOps(final Connection connection, final JSONObject taskDetails, final Integer dmDomainClientID, final Long dmDomainID, final Long collationID, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap, final Criteria resCri) throws Exception {
        DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", I18N.getMsg("mdm.ad.core.ops", new Object[0]));
        this.insertIntoDirResRel(connection, taskDetails, dmDomainID, collationID, tempCri, tempValCriMap, resCri);
        DirObjAttrValDataPersistor.getInstance().doCoreSyncOps(connection, dmDomainClientID, dmDomainID, collationID, tempValCriMap);
        this.suspendSyncToken(connection, taskDetails, dmDomainID, collationID);
        this.transmitToVal(connection, dmDomainID, collationID, new ArrayList<String>(Arrays.asList("DM_DOMAIN_ID", "DIR_RESOURCE_TYPE")));
        this.reviseObjectsStatus(connection, taskDetails, collationID);
    }
    
    int performCoreSyncOps(final Connection connection, final Long collationID, final JSONObject taskDetails, final boolean preliminary) throws Exception {
        final String dmDomainName = String.valueOf(taskDetails.get((Object)"NAME"));
        final Long dmDomainID = Long.valueOf(String.valueOf(taskDetails.get((Object)"DOMAIN_ID")));
        final Long customerID = Long.valueOf(String.valueOf(taskDetails.get((Object)"CUSTOMER_ID")));
        final Integer dmDomainClientID = Integer.valueOf(String.valueOf(taskDetails.get((Object)"CLIENT_ID")));
        final Integer syncType = Integer.valueOf(String.valueOf(taskDetails.get((Object)"SYNC_TYPE")));
        final Criteria tempCri = this.getTempBaseCri(dmDomainID, collationID);
        final HashMap<String, Criteria> tempValCriMap = new HashMap<String, Criteria>();
        tempValCriMap.put("DirObjTmpRegStrVal", this.getTempValBaseCri("DirObjTmpRegStrVal", dmDomainID, collationID));
        tempValCriMap.put("DirObjTmpRegIntVal", this.getTempValBaseCri("DirObjTmpRegIntVal", dmDomainID, collationID));
        tempValCriMap.put("DirObjTmpArrStrVal", this.getTempValBaseCri("DirObjTmpArrStrVal", dmDomainID, collationID));
        final Criteria resCri = DirectoryQueryutil.getInstance().getResCri(dmDomainName, customerID);
        IDPSlogger.DBO.log(Level.INFO, "performing {0} sync ops..", new Object[] { preliminary ? "preliminary" : "core" });
        int numOfPKrequired = 0;
        if (preliminary) {
            numOfPKrequired = this.prepareTempData(connection, syncType, dmDomainName, customerID, dmDomainID, dmDomainClientID, collationID, tempCri, tempValCriMap);
        }
        else {
            this.performCoreSyncDataOps(connection, taskDetails, dmDomainClientID, dmDomainID, collationID, tempCri, tempValCriMap, resCri);
            DirResMapper.getInstance().mapOrAddResource(connection, taskDetails, customerID, dmDomainName, dmDomainID, dmDomainClientID, collationID, resCri);
        }
        return numOfPKrequired;
    }
    
    void handleObjTypeSyncModify(final String dmDomainName, final Long customerID, final Long dmDomainID, final JSONObject qData) throws Exception {
        final boolean enable = Boolean.valueOf(String.valueOf(qData.get((Object)IdpEventConstants.STATUS_CHANGE_EVENT)));
        if (!enable) {
            final List<Integer> resTypes = new ArrayList<Integer>();
            final JSONArray jsonArray = (JSONArray)qData.get((Object)"RESOURCE_TYPE");
            for (int i = 0; i < jsonArray.size(); ++i) {
                final int curResType = Integer.valueOf(String.valueOf(jsonArray.get(i)));
                resTypes.add(curResType);
            }
            if (!resTypes.isEmpty()) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
                selectQuery.addJoin(new Join("DirResRel", "DirObjRegIntVal", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
                selectQuery.setCriteria(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)2, 1)).and(new Criteria(Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"), (Object)resTypes.toArray(new Integer[resTypes.size()]), 8)));
                selectQuery.addSortColumn(new SortColumn(Column.getColumn("DirResRel", "OBJ_ID"), true));
                selectQuery.addSelectColumn(Column.getColumn("DirResRel", "GUID"));
                selectQuery.addSelectColumn(Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"));
                selectQuery.setRange(new Range(0, 10000));
                Connection connection = null;
                try {
                    final DirectoryDataReceiver dtr = new DirectoryDataReceiver();
                    DirectorySequenceAsynchImpl.getInstance().getNewSyncToken(customerID, dmDomainID, dmDomainName, 2, null, "DISABLE_RES_TYPE_SYNC");
                    dtr.proccessFetchedADData(null, new ArrayList<Properties>(), dmDomainName, customerID, -1, 0, 0, 0, true, false);
                    connection = RelationalAPI.getInstance().getConnection();
                    JSONArray toBeSynDisabledObj = IdpsUtil.executeSelectQuery(connection, selectQuery);
                    if (toBeSynDisabledObj != null && !toBeSynDisabledObj.isEmpty()) {
                        final String statusAttrKey = DirectoryAttributeConstants.getAttrKey(118L);
                        while (toBeSynDisabledObj != null && !toBeSynDisabledObj.isEmpty()) {
                            final ArrayList<Properties> updatedObjList = new ArrayList<Properties>();
                            for (int j = 0; j < toBeSynDisabledObj.size(); ++j) {
                                final JSONObject jsonObject = (JSONObject)toBeSynDisabledObj.get(j);
                                final String guid = (String)jsonObject.get((Object)"GUID");
                                final Properties props = new Properties();
                                ((Hashtable<String, String>)props).put("objectGUID", guid);
                                ((Hashtable<String, Object>)props).put("RESOURCE_TYPE", jsonObject.get((Object)"DIR_RESOURCE_TYPE"));
                                ((Hashtable<String, Integer>)props).put(statusAttrKey, 2);
                                updatedObjList.add(props);
                            }
                            dtr.proccessFetchedADData(null, updatedObjList, dmDomainName, customerID, -1, updatedObjList.size(), 0, updatedObjList.size(), false, false);
                            final Range curRange = selectQuery.getRange();
                            selectQuery.setRange(new Range(curRange.getStartIndex() + curRange.getNumberOfObjects(), 10000));
                            toBeSynDisabledObj = IdpsUtil.executeSelectQuery(connection, selectQuery);
                        }
                    }
                    dtr.proccessFetchedADData(null, new ArrayList<Properties>(), dmDomainName, customerID, -1, 0, 0, 0, false, true);
                }
                finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    }
                    catch (final Exception ex) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
    static {
        DirectoryDataPersistor.directoryDataPersistor = null;
    }
}
