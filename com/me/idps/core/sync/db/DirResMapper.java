package com.me.idps.core.sync.db;

import com.me.idps.core.util.IdpsJSONutil;
import com.me.idps.core.sync.events.DirectoryEventsUtil;
import java.util.HashMap;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.DataObject;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.factory.IdpsAccessAPI;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import org.json.simple.JSONObject;
import com.adventnet.persistence.WritableDataObject;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.Properties;
import com.adventnet.ds.query.UpdateQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.sql.Connection;

class DirResMapper
{
    private static DirResMapper dirResMapper;
    private static final String BLOCK = "postSyncEngine";
    
    static DirResMapper getInstance() {
        if (DirResMapper.dirResMapper == null) {
            DirResMapper.dirResMapper = new DirResMapper();
        }
        return DirResMapper.dirResMapper;
    }
    
    private void prepareResource(final Connection connection, final Long dmDomainID, final Long collationID, final Criteria resCri) throws Exception {
        final Criteria timeNotMatchCri = new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)0L, 6).or(new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)Column.getColumn("DirObjRegStrVal", "MODIFIED_AT"), 7));
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Resource");
        updateQuery.addJoin(new Join("Resource", "DirObjRegStrVal", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        updateQuery.setCriteria(resCri.and(timeNotMatchCri).and(new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)null, 1)).and(new Criteria(Column.getColumn("DirObjRegStrVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)2L, 0)));
        updateQuery.setUpdateColumn("DB_UPDATED_TIME", (Object)Column.getColumn("DirObjRegStrVal", "MODIFIED_AT"));
        final String logMsg = "picking resource based on entries from DIRRESREL";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "postSyncEngine", logMsg, false);
    }
    
    private void insertIntoResource(final Connection connection, final Properties dmDomainProps, final Long customerID, final String dmDomainName, final Long dmDomainID, final Integer dmDomainClientID, final Long collationID, final Criteria resCri, final Criteria dirObjRegCri) throws Exception {
        this.mapResToDirObj(connection, dmDomainProps, dmDomainID, collationID, resCri);
        final IdpsAccessAPI idpsAccessAPI = IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID);
        final Column resNameCol = Column.getColumn("DirObjRegStrVal", "VALUE", "inner1_DIROBJTMP.RESOURCE_NAME");
        final Column resMaxAddedAtCol = Column.getColumn("DirObjRegStrVal", "MODIFIED_AT", "inner1_DIROBJTMP.MAX_ADDED_AT");
        final Column resTypeCol = Column.getColumn("DirObjRegStrVal", "DIR_RESOURCE_TYPE", "inner1_DIROBJTMP.RESOURCE_TYPE");
        int totallyInsertedRowsSofar = 0;
        final Table baseTable = Table.getTable("DirObjRegStrVal");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(baseTable);
        selectQuery.setCriteria(dirObjRegCri.and(new Criteria(Column.getColumn("DirObjRegStrVal", "DIR_RESOURCE_TYPE"), (Object)201, 1)));
        selectQuery.addSelectColumns((List)new ArrayList(Arrays.asList(resNameCol, resTypeCol, resMaxAddedAtCol)));
        selectQuery.addSortColumn(new SortColumn(resMaxAddedAtCol, true));
        selectQuery.setRange(new Range(0, 10000));
        for (JSONArray jsArray = IdpsUtil.executeSelectQuery(connection, selectQuery); jsArray != null && !jsArray.isEmpty(); jsArray = IdpsUtil.executeSelectQuery(connection, selectQuery)) {
            final DataObject toBeAddedRes = (DataObject)new WritableDataObject();
            for (int i = 0; i < jsArray.size(); ++i) {
                final JSONObject jsObject = (JSONObject)jsArray.get(i);
                final Row resourceRow = new Row("Resource");
                resourceRow.set("CUSTOMER_ID", (Object)customerID);
                resourceRow.set("DOMAIN_NETBIOS_NAME", (Object)dmDomainName);
                resourceRow.set("NAME", jsObject.get((Object)resNameCol.getColumnAlias()));
                final int dirObjType = Integer.valueOf(String.valueOf(jsObject.get((Object)resTypeCol.getColumnAlias())));
                resourceRow.set("RESOURCE_TYPE", (Object)idpsAccessAPI.getResourceType(dirObjType));
                final Object addedAt = jsObject.get((Object)resMaxAddedAtCol.getColumnAlias());
                resourceRow.set("DB_ADDED_TIME", addedAt);
                resourceRow.set("DB_UPDATED_TIME", addedAt);
                toBeAddedRes.addRow(resourceRow);
            }
            final int insertedRows = toBeAddedRes.size("Resource");
            totallyInsertedRowsSofar += insertedRows;
            SyMUtil.getPersistenceLite().add(toBeAddedRes);
            IDPSlogger.DBO.log(Level.INFO, "inserted {0} rows into resource, totally inserted={1}", new Object[] { insertedRows, totallyInsertedRowsSofar });
            DirectoryQueryutil.getInstance().incrementDbOpsMetric(dmDomainID, collationID, "postSyncEngine", 1, insertedRows);
            this.mapResToDirObj(connection, dmDomainProps, dmDomainID, collationID, resCri);
            if (jsArray.size() < selectQuery.getRange().getNumberOfObjects() - 1) {
                break;
            }
        }
    }
    
    private void unbindObjVal(final Connection connection, final Long dmDomainID) throws Exception {
        final Criteria joinCri = new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirObjArrLngVal", "VALUE"), (Object)Column.getColumn("DirResRel", "OBJ_ID"), 0));
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjArrLngVal");
        deleteQuery.addJoin(new Join("DirObjArrLngVal", "DirResRel", joinCri, 1));
        deleteQuery.setCriteria(new Criteria(Column.getColumn("DirResRel", "OBJ_ID"), (Object)null, 0).and(new Criteria(Column.getColumn("DirObjArrLngVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirObjArrLngVal", "ATTR_ID"), (Object)new Long[] { 102L, 101L }, 8)));
        IDPSlogger.DBO.log(Level.INFO, "unbinding stale membership values");
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
        IDPSlogger.DBO.log(Level.INFO, "stale membership values are un-bound");
    }
    
    private void unBindDeletedRes(final Connection connection, final Properties dmDomainProps, final Long dmDomainID, final Long collationID) throws Exception {
        final String logMsg = "unbinding deleted res from dir obj mapping";
        final Criteria domainCri = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)null, 0).and(new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)null, 1)).and(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0));
        final Join dmDomainJoin = new Join("DirResRel", "DMDomain", new String[] { "DM_DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2);
        final Join resourceJoin = new Join("DirResRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirResRel");
        deleteQuery.addJoin(dmDomainJoin);
        deleteQuery.addJoin(new Join("DirResRel", "DirObjRegIntVal", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
        deleteQuery.addJoin(resourceJoin);
        deleteQuery.setCriteria(domainCri.and(new Criteria(Column.getColumn("DirObjRegIntVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)1, 1)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0)));
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirResRel");
        updateQuery.addJoin(dmDomainJoin);
        updateQuery.addJoin(resourceJoin);
        updateQuery.setCriteria(domainCri);
        updateQuery.setUpdateColumn("RESOURCE_ID", (Object)null);
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "postSyncEngine", logMsg, false);
        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
        dirProdImplRequest.dmDomainProps = dmDomainProps;
        dirProdImplRequest.eventType = IdpEventConstants.UNBIND_DELETED_RES;
        dirProdImplRequest.args = new Object[] { connection, dmDomainID, collationID, "postSyncEngine" };
        DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
        DirectoryDataPersistor.getInstance().transmitToVal(connection, dmDomainID, collationID, new ArrayList<String>(Arrays.asList("RESOURCE_ID")));
        this.unbindObjVal(connection, dmDomainID);
    }
    
    private void mapResToDirObj(final Connection connection, final Properties dmDomainProps, final Long dmDomainID, final Long collationID, final Criteria resCri) throws Exception {
        String logMsg = "calculating availble resources which are not yet mapped to any dir obj";
        final Column resIDcol = Column.getColumn("Resource", "RESOURCE_ID");
        final Column dirResIDcol = Column.getColumn("DirResRel", "RESOURCE_ID");
        final Column syncTokenIDcol = Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID");
        final Criteria unMappObjCri = new Criteria(dirResIDcol, (Object)null, 0);
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        colMap.put("RESOURCE_ID", resIDcol);
        colMap.put("COLLATION_ID", syncTokenIDcol);
        final SelectQuery innerQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        innerQuery.addJoin(new Join("Resource", "DirectorySyncDetails", new Criteria(syncTokenIDcol, (Object)collationID, 0), 2));
        innerQuery.addJoin(new Join("Resource", "DirTmpAvailableRes", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        innerQuery.addJoin(new Join("Resource", "DirResRel", resCri.and(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)dirResIDcol, 0)), 1));
        innerQuery.setCriteria(resCri.and(unMappObjCri).and(new Criteria(Column.getColumn("DirTmpAvailableRes", "RESOURCE_ID"), (Object)null, 0)).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer[] { 2, 101 }, 8)));
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, innerQuery, "DirTmpAvailableRes", colMap, null, "DirectoryTempDataHandler", logMsg, false);
        final Criteria joinCri = new Criteria(Column.getColumn("DirObjRegStrVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)2L, 0)).and(new Criteria(Column.getColumn("DirResRel", "OBJ_ID"), (Object)Column.getColumn("DirObjRegStrVal", "OBJ_ID"), 0));
        final Criteria resJoinCri = resCri.and(new Criteria(Column.getColumn("Resource", "NAME"), (Object)Column.getColumn("DirObjRegStrVal", "VALUE"), 0, false)).and(new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)Column.getColumn("DirObjRegStrVal", "MODIFIED_AT"), 0));
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirResRel");
        updateQuery.addJoin(new Join("DirResRel", "DirObjRegStrVal", joinCri, 2));
        updateQuery.addJoin(new Join("DirObjRegStrVal", "Resource", resJoinCri, 2));
        updateQuery.addJoin(new Join("Resource", "DirTmpAvailableRes", new Criteria(Column.getColumn("DirTmpAvailableRes", "COLLATION_ID"), (Object)collationID, 0).and(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("DirTmpAvailableRes", "RESOURCE_ID"), 0)), 2));
        updateQuery.setCriteria(joinCri.and(resJoinCri).and(unMappObjCri).and(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)));
        updateQuery.setUpdateColumn("RESOURCE_ID", (Object)Column.getColumn("Resource", "RESOURCE_ID"));
        logMsg = "mapping new resource entries";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "postSyncEngine", logMsg, false);
        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
        dirProdImplRequest.dmDomainProps = dmDomainProps;
        dirProdImplRequest.eventType = IdpEventConstants.BIND_RES;
        dirProdImplRequest.args = new Object[] { connection, dmDomainID, collationID, "postSyncEngine" };
        DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirTmpAvailableRes");
        deleteQuery.addJoin(new Join("DirTmpAvailableRes", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        deleteQuery.setCriteria(new Criteria(Column.getColumn("DirTmpAvailableRes", "COLLATION_ID"), (Object)collationID, 0));
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "DirectoryTempDataHandler", "deleting mapped res from DirTmpAvailableRes", false);
        DirectoryDataPersistor.getInstance().transmitToVal(connection, dmDomainID, collationID, new ArrayList<String>(Arrays.asList("RESOURCE_ID")));
    }
    
    private void setResTimestamp(final Connection connection, final Long dmDomainID, final Long collationID) throws Exception {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Resource");
        updateQuery.addJoin(new Join("Resource", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        updateQuery.setCriteria(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)null, 1).or(new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)0L, 4))).and(new Criteria(Column.getColumn("Resource", "DB_ADDED_TIME"), (Object)null, 0).or(new Criteria(Column.getColumn("Resource", "DB_ADDED_TIME"), (Object)0L, 6))));
        updateQuery.setUpdateColumn("DB_ADDED_TIME", (Object)Column.getColumn("Resource", "DB_UPDATED_TIME"));
        final String logMsg = "updating res timestamp";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "postSyncEngine", logMsg, false);
    }
    
    private void transmitIntoArrDval(final Connection connection, final Long dmDomainID, final Long collationID) throws Exception {
        final Criteria cri = new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirObjArrLngVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirObjArrLngVal", "VALUE"), (Object)Column.getColumn("DirResRel", "OBJ_ID"), 0)).and(new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)null, 1).and(new Criteria(Column.getColumn("DirObjArrLngVal", "VALUE_RESOURCE_ID"), (Object)null, 0).or(new Criteria(Column.getColumn("DirObjArrLngVal", "VALUE_DIR_RESOURCE_TYPE"), (Object)null, 0)).or(new Criteria(Column.getColumn("DirObjArrLngVal", "VALUE_RESOURCE_ID"), (Object)Column.getColumn("DirResRel", "RESOURCE_ID"), 1)).or(new Criteria(Column.getColumn("DirObjArrLngVal", "VALUE_DIR_RESOURCE_TYPE"), (Object)Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"), 1))));
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjArrLngVal");
        updateQuery.addJoin(new Join("DirObjArrLngVal", "DirResRel", cri, 2));
        updateQuery.setCriteria(cri);
        updateQuery.setUpdateColumn("VALUE_RESOURCE_ID", (Object)Column.getColumn("DirResRel", "RESOURCE_ID"));
        updateQuery.setUpdateColumn("VALUE_DIR_RESOURCE_TYPE", (Object)Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"));
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "postSyncEngine", "updating value resourceid and restype if relevant", false);
    }
    
    private void setResName(final Connection connection, final Long dmDomainID, final Long collationID) throws Exception {
        final Long curAttrID = 2L;
        final Column productValCol = Column.getColumn("Resource", "NAME");
        final Join statusJoin = new Join("Resource", "DirObjRegIntVal", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join dirObjAttrValJoin = new Join("Resource", "DirObjRegStrVal", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Criteria criteria = new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)curAttrID, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(productValCol, (Object)null, 0).or(new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)productValCol, 1, true))).and(new Criteria(Column.getColumn("DirObjRegIntVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)1, 0)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "DIR_RESOURCE_TYPE"), (Object)201, 1)));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(statusJoin);
        selectQuery.addJoin(dirObjAttrValJoin);
        selectQuery.setCriteria(criteria);
        final Column resIDcol = Column.getColumn("Resource", "RESOURCE_ID");
        final Column eventDetailCol = Column.getColumn("DirObjRegStrVal", "ATTR_ID");
        final Column eventTimeStampValCol = Column.getColumn("DirObjRegStrVal", "MODIFIED_AT");
        final Long dirEventID = DirectoryEventsUtil.getInstance().populateEvents(connection, dmDomainID, collationID, IdpEventConstants.MODIFIED_EVENT, selectQuery, resIDcol, eventTimeStampValCol, eventDetailCol);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Resource");
        updateQuery.addJoin(statusJoin);
        updateQuery.addJoin(dirObjAttrValJoin);
        updateQuery.setCriteria(criteria);
        updateQuery.setUpdateColumn(productValCol.getColumnName(), (Object)Column.getColumn("DirObjRegStrVal", "VALUE"));
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "postSyncEngine", null, false);
        DirectoryEventsUtil.getInstance().markEventSucceeded(connection, dmDomainID, dirEventID);
    }
    
    void mapOrAddResource(final Connection connection, final JSONObject taskDetails, final Long customerID, final String dmDomainName, final Long dmDomainID, final Integer dmDomainClientID, final Long collationID, final Criteria resCri) throws Exception {
        Criteria dirObjRegCri = new Criteria(Column.getColumn("DirObjRegStrVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)2L, 0));
        final Properties dmDomainProps = IdpsJSONutil.convertJSONObjectToProperties(taskDetails);
        this.prepareResource(connection, dmDomainID, collationID, resCri);
        DirectoryQueryutil.getInstance().validateUserNameUniqueness(connection, dirObjRegCri);
        this.unBindDeletedRes(connection, dmDomainProps, dmDomainID, collationID);
        dirObjRegCri = dirObjRegCri.and(new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)null, 0));
        DirectoryDuplicateHandler.getInstance().handleDuplicates(connection, dmDomainProps, dmDomainID, collationID, resCri, dirObjRegCri);
        this.insertIntoResource(connection, dmDomainProps, customerID, dmDomainName, dmDomainID, dmDomainClientID, collationID, resCri, dirObjRegCri);
        this.transmitIntoArrDval(connection, dmDomainID, collationID);
        this.setResTimestamp(connection, dmDomainID, collationID);
        this.setResName(connection, dmDomainID, collationID);
    }
    
    static {
        DirResMapper.dirResMapper = null;
    }
}
