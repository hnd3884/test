package com.me.idps.core.sync.db;

import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.events.DirectoryEventsUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.text.MessageFormat;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import java.sql.Connection;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;

class DirObjAttrValDataPersistor
{
    private static final String TEMP_BLOCK = "DirectoryTempDataHandler";
    private static final String BLOCK = "coreSyncEngine";
    private static DirObjAttrValDataPersistor dirObjAttrValDataPersistor;
    
    static DirObjAttrValDataPersistor getInstance() {
        if (DirObjAttrValDataPersistor.dirObjAttrValDataPersistor == null) {
            DirObjAttrValDataPersistor.dirObjAttrValDataPersistor = new DirObjAttrValDataPersistor();
        }
        return DirObjAttrValDataPersistor.dirObjAttrValDataPersistor;
    }
    
    private SelectQuery getUniqueObjAttrValueQuery(final String tableName, final HashMap<String, Criteria> tempValCriMap, final Criteria additionalCri) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        Criteria criteria = tempValCriMap.get(tableName);
        if (additionalCri != null) {
            criteria = criteria.and(additionalCri);
        }
        query.setCriteria(criteria);
        return query;
    }
    
    private String getCorrespondingMainTable(final String tempTableName) {
        return DirectoryQueryutil.getCorrespondingMainTable(tempTableName);
    }
    
    private String getAddedAtCol(final String mainTableName) {
        return DirectoryQueryutil.getAddedAtCol(mainTableName);
    }
    
    private String getModifiedAtCol(final String mainTableName) {
        return DirectoryQueryutil.getModifiedAtCol(mainTableName);
    }
    
    private String getAttrIDCol(final String mainTableName) {
        return DirectoryQueryutil.getAttrIDcol(mainTableName);
    }
    
    private String getValCol(final String mainTableName) {
        return DirectoryQueryutil.getValCol(mainTableName);
    }
    
    private String getObjIDcol(final String mainTableName) {
        return DirectoryQueryutil.getObjIDcol(mainTableName);
    }
    
    private void insertRegDirObjVal(final Connection connection, final String tempTableName, final Long dmDomainID, final Long collationID, final HashMap<String, Criteria> tempValCriMap) throws Exception {
        final String tempObjIDcol = DirectoryQueryutil.getObjIDcol(tempTableName);
        final String tempAttrIDcol = DirectoryQueryutil.getAttrIDcol(tempTableName);
        final String tempAddedAtcol = DirectoryQueryutil.getAttrAddedAtCol(tempTableName);
        final String mainTableName = this.getCorrespondingMainTable(tempTableName);
        final String valCol = this.getValCol(mainTableName);
        final String mainObjIDcol = this.getObjIDcol(mainTableName);
        final String mainAttrIDcol = this.getAttrIDCol(mainTableName);
        final String mainAddedAtcol = this.getAddedAtCol(mainTableName);
        final String modifiedAtcol = this.getModifiedAtCol(mainTableName);
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        colMap.put(mainAddedAtcol, new Column(tempTableName, tempAddedAtcol));
        colMap.put(modifiedAtcol, colMap.get(mainAddedAtcol));
        colMap.put(mainAttrIDcol, new Column(tempTableName, tempAttrIDcol));
        colMap.put(mainObjIDcol, new Column(tempTableName, tempObjIDcol));
        colMap.put(valCol, new Column(tempTableName, DirectoryQueryutil.getTempValCol(tempTableName)));
        final SelectQuery selectQuery = this.getUniqueObjAttrValueQuery(tempTableName, tempValCriMap, null);
        selectQuery.addJoin(new Join(tempTableName, mainTableName, new String[] { tempObjIDcol, tempAttrIDcol }, new String[] { mainObjIDcol, mainAttrIDcol }, 1));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn(mainTableName, mainObjIDcol), (Object)null, 0)).and(new Criteria(Column.getColumn(mainTableName, mainAttrIDcol), (Object)null, 0)));
        String logMsg = MessageFormat.format("inserting into {0} from {1}", mainTableName, tempTableName);
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, selectQuery, mainTableName, colMap, null, "coreSyncEngine", logMsg, false);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tempTableName);
        deleteQuery.addJoin(new Join(tempTableName, mainTableName, new String[] { tempObjIDcol, tempAttrIDcol, tempAddedAtcol }, new String[] { mainObjIDcol, mainAttrIDcol, mainAddedAtcol }, 2));
        deleteQuery.setCriteria((Criteria)tempValCriMap.get(tempTableName));
        logMsg = MessageFormat.format("deleting values inserted in {0} from {1}", mainTableName, tempTableName);
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    private void insertRegDirObjVal(final Connection connection, final Long dmDomainID, final Long collationID, final HashMap<String, Criteria> tempValCriMap) throws Exception {
        this.insertRegDirObjVal(connection, "DirObjTmpRegIntVal", dmDomainID, collationID, tempValCriMap);
        this.insertRegDirObjVal(connection, "DirObjTmpRegStrVal", dmDomainID, collationID, tempValCriMap);
    }
    
    private Criteria getUpdateCri(final HashMap<String, Criteria> tempValCriMap, final String tempTableName) {
        final String tempValcol = DirectoryQueryutil.getTempValCol(tempTableName);
        final String tempObjIDcol = DirectoryQueryutil.getObjIDcol(tempTableName);
        final String tempAttrIDcol = DirectoryQueryutil.getAttrIDcol(tempTableName);
        final String mainTableName = this.getCorrespondingMainTable(tempTableName);
        final String mainValcol = this.getValCol(mainTableName);
        final String mainObjIDcol = this.getObjIDcol(mainTableName);
        final String mainAttrIDcol = this.getAttrIDCol(mainTableName);
        return tempValCriMap.get(tempTableName).and(new Criteria(Column.getColumn(mainTableName, mainObjIDcol), (Object)Column.getColumn(tempTableName, tempObjIDcol), 0)).and(new Criteria(Column.getColumn(mainTableName, mainAttrIDcol), (Object)Column.getColumn(tempTableName, tempAttrIDcol), 0)).and(new Criteria(Column.getColumn(mainTableName, mainValcol), (Object)Column.getColumn(tempTableName, tempValcol), 1, true));
    }
    
    private Long detectStatusChangedObj(final Connection connection, final HashMap<String, Criteria> tempValCriMap, final Long dmDomainID, final Long collationID) throws Exception {
        final Criteria joinCri = this.getUpdateCri(tempValCriMap, "DirObjTmpRegIntVal").and(new Criteria(Column.getColumn("DirObjRegIntVal", "RESOURCE_ID"), (Object)null, 1)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0)).and(new Criteria(Column.getColumn("DirObjTmpRegIntVal", "ATTR_ID"), (Object)118L, 0));
        final SelectQuery statusChangedQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjRegIntVal"));
        statusChangedQuery.addJoin(new Join("DirObjRegIntVal", "DirObjTmpRegIntVal", joinCri, 2));
        statusChangedQuery.setCriteria(joinCri);
        final Column resIDcol = Column.getColumn("DirObjRegIntVal", "RESOURCE_ID");
        final Column valCol = Column.getColumn("DirObjTmpRegIntVal", "TEMP_VALUE");
        final Column eventTimeStampValCol = Column.getColumn("DirObjTmpRegIntVal", "ADDED_AT");
        return DirectoryEventsUtil.getInstance().populateEvents(connection, dmDomainID, collationID, IdpEventConstants.STATUS_CHANGE_EVENT, statusChangedQuery, resIDcol, eventTimeStampValCol, valCol);
    }
    
    private void updateDirObjVal(final Connection connection, final String tempTableName, final Long dmDomainID, final Long collationID, final HashMap<String, Criteria> tempValCriMap) throws Exception {
        final Criteria joinCri = this.getUpdateCri(tempValCriMap, tempTableName);
        final String tempValCol = DirectoryQueryutil.getTempValCol(tempTableName);
        final String tempAddedAtCol = DirectoryQueryutil.getAttrAddedAtCol(tempTableName);
        final String mainTableName = this.getCorrespondingMainTable(tempTableName);
        final String mainValCol = this.getValCol(mainTableName);
        final String mainModifiedAtCol = this.getModifiedAtCol(mainTableName);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(mainTableName);
        updateQuery.addJoin(new Join(mainTableName, tempTableName, joinCri, 2));
        updateQuery.setCriteria(joinCri);
        updateQuery.setUpdateColumn(mainValCol, (Object)Column.getColumn(tempTableName, tempValCol));
        updateQuery.setUpdateColumn(mainModifiedAtCol, (Object)Column.getColumn(tempTableName, tempAddedAtCol));
        final String logMsg = MessageFormat.format("updating values in {0} from {1}", mainTableName, tempTableName);
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "coreSyncEngine", logMsg, false);
    }
    
    private void updateDirObjVal(final Connection connection, final Long dmDomainID, final Long collationID, final HashMap<String, Criteria> tempValCriMap, final Long dirEventID) throws Exception {
        this.updateDirObjVal(connection, "DirObjTmpRegIntVal", dmDomainID, collationID, tempValCriMap);
        IDPSlogger.DBO.log(Level.INFO, "marking events obtained for {0} as succeeded", new Object[] { String.valueOf(dirEventID) });
        DirectoryEventsUtil.getInstance().markEventSucceeded(connection, dmDomainID, dirEventID);
        this.updateDirObjVal(connection, "DirObjTmpRegStrVal", dmDomainID, collationID, tempValCriMap);
    }
    
    private void validateTempArrData(final Connection connection, final Integer dmDomainClientID, final Long dmDomainID, final Long collationID, final HashMap<String, Criteria> tempValCriMap) throws Exception {
        DirectoryTempDataValidator.getInstance().prepareArrVal(connection, tempValCriMap, dmDomainClientID, dmDomainID, collationID);
        DirectoryTempDataValidator.getInstance().identifyMaxEarrAttrAmongDuplGUID(connection, tempValCriMap, dmDomainID, collationID);
        DirectoryTempDataValidator.getInstance().invalidLTmaxNEattr(connection, "DirObjTmpArrStrVal", tempValCriMap, dmDomainID, collationID, false);
        DirectoryTempDataValidator.getInstance().deleteInvalidEntriesFromTempVal(connection, "DirObjTmpArrStrVal", dmDomainID, collationID);
    }
    
    private void insertArayTypeDirObjVal(final Connection connection, final Long dmDomainID, final Long collationID, final HashMap<String, Criteria> tempValCriMap, final boolean isExtended) throws Exception {
        final Integer extnType = isExtended ? Integer.valueOf(1) : null;
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        colMap.put("ADDED_AT", new Column("DirObjTmpArrStrVal", "VAL_ADDED_AT"));
        colMap.put("MODIFIED_AT", colMap.get("ADDED_AT"));
        colMap.put("ATTR_ID", new Column("DirObjTmpArrStrVal", "ATTR_ID"));
        colMap.put("VALUE", new Column("DirObjTmpArrStrVal", "DERIVED_LONG_HINT"));
        colMap.put("OBJ_ID", new Column("DirObjTmpArrStrVal", "OBJ_ID"));
        final SelectQuery selectQuery = this.getUniqueObjAttrValueQuery("DirObjTmpArrStrVal", tempValCriMap, new Criteria(Column.getColumn("DirObjTmpArrStrVal", "KEY_DETAIL"), (Object)extnType, 0));
        selectQuery.addJoin(new Join("DirObjTmpArrStrVal", "DirObjArrLngVal", new String[] { "OBJ_ID", "ATTR_ID", "DERIVED_LONG_HINT" }, new String[] { "OBJ_ID", "ATTR_ID", "VALUE" }, 1));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("DirObjArrLngVal", "VALUE"), (Object)null, 0)).and(new Criteria(Column.getColumn("DirObjTmpArrStrVal", "DERIVED_LONG_HINT"), (Object)null, 1)));
        final String logMsg = MessageFormat.format("inserting {0} array object type into {1}", isExtended ? "extended" : "non-extended", "DirObjArrLngVal");
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, selectQuery, "DirObjArrLngVal", colMap, null, "coreSyncEngine", logMsg, false);
    }
    
    private void removeArayTypeDirObjVal(final Connection connection, final Long dmDomainID, final Long collationID, final Criteria tempValCri, final boolean isExtended) throws Exception {
        final Criteria domainCri = new Criteria(Column.getColumn("DirObjArrLngVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
        final Criteria extnCri = new Criteria(Column.getColumn("DirObjTmpArrStrVal", "KEY_DETAIL"), (Object)(isExtended ? Integer.valueOf(2) : null), 0);
        final Criteria tempValJoinCri = tempValCri.and(extnCri).and(new Criteria(Column.getColumn("DirObjArrLngVal", "OBJ_ID"), (Object)Column.getColumn("DirObjTmpArrStrVal", "OBJ_ID"), 0)).and(new Criteria(Column.getColumn("DirObjArrLngVal", "ATTR_ID"), (Object)Column.getColumn("DirObjTmpArrStrVal", "ATTR_ID"), 0)).and(new Criteria(Column.getColumn("DirObjArrLngVal", "VALUE"), (Object)Column.getColumn("DirObjTmpArrStrVal", "DERIVED_LONG_HINT"), 0));
        final Criteria tempNullValCri = new Criteria(Column.getColumn("DirObjTmpArrStrVal", "DERIVED_LONG_HINT"), (Object)null, 0);
        final Criteria joinCri = domainCri.and(tempValJoinCri);
        DeleteQuery delQuery;
        if (isExtended) {
            delQuery = (DeleteQuery)new DeleteQueryImpl("DirObjArrLngVal");
            delQuery.addJoin(new Join("DirObjArrLngVal", "DirObjTmpArrStrVal", joinCri, 2));
            delQuery.setCriteria(joinCri);
        }
        else {
            final HashMap<String, Column> tmpSelColMap = new HashMap<String, Column>();
            tmpSelColMap.put("OBJ_ID", Column.getColumn("DirObjTmpArrStrVal", "OBJ_ID", "inner_DIROBJTMPARRSTRVAL.OBJ_ID"));
            SelectQuery tmpArrSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjTmpArrStrVal"));
            tmpArrSelectQuery.setCriteria(tempValCri.and(extnCri));
            tmpArrSelectQuery = DirectoryQueryutil.getInstance().getSelectQuery(tmpArrSelectQuery, tmpSelColMap);
            tmpArrSelectQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(tmpSelColMap.get("OBJ_ID")))));
            final DerivedTable innerDT = new DerivedTable("innerDT", (Query)tmpArrSelectQuery);
            delQuery = (DeleteQuery)new DeleteQueryImpl("DirObjArrLngVal");
            delQuery.addJoin(new Join(Table.getTable("DirObjArrLngVal"), (Table)innerDT, new String[] { "OBJ_ID" }, new String[] { tmpSelColMap.get("OBJ_ID").getColumnAlias() }, 2));
            delQuery.addJoin(new Join("DirObjArrLngVal", "DirObjTmpArrStrVal", joinCri, 1));
            delQuery.setCriteria(domainCri.and(tempNullValCri));
        }
        String logMsg = MessageFormat.format("deleting {0} array type from dirObjAttrVal", isExtended ? "remove extn" : "non extended");
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, delQuery, "coreSyncEngine", logMsg, false);
        delQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmpArrStrVal");
        delQuery.setCriteria(tempValCri.and(extnCri));
        logMsg = MessageFormat.format("deleting all {0}extended attr from DIROBJTEMPARRSTRVAL", isExtended ? "" : "non");
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, delQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    void doCoreSyncOps(final Connection connection, final Integer dmDomainClientID, final Long dmDomainID, final Long collationID, final HashMap<String, Criteria> tempValCriMap) throws Exception {
        this.insertRegDirObjVal(connection, dmDomainID, collationID, tempValCriMap);
        final Long dirEventID = this.detectStatusChangedObj(connection, tempValCriMap, dmDomainID, collationID);
        this.updateDirObjVal(connection, dmDomainID, collationID, tempValCriMap, dirEventID);
        this.validateTempArrData(connection, dmDomainClientID, dmDomainID, collationID, tempValCriMap);
        this.insertArayTypeDirObjVal(connection, dmDomainID, collationID, tempValCriMap, false);
        this.removeArayTypeDirObjVal(connection, dmDomainID, collationID, tempValCriMap.get("DirObjTmpArrStrVal"), false);
        this.insertArayTypeDirObjVal(connection, dmDomainID, collationID, tempValCriMap, true);
        this.removeArayTypeDirObjVal(connection, dmDomainID, collationID, tempValCriMap.get("DirObjTmpArrStrVal"), true);
    }
    
    static {
        DirObjAttrValDataPersistor.dirObjAttrValDataPersistor = null;
    }
}
