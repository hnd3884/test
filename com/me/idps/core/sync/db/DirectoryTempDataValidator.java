package com.me.idps.core.sync.db;

import com.adventnet.i18n.I18N;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import java.text.MessageFormat;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.sql.Connection;
import java.util.Set;
import com.me.idps.core.util.DirectoryUtil;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.util.DirectoryAttributeConstants;
import org.json.simple.JSONArray;
import com.me.idps.core.util.IdpsJSONutil;
import org.json.simple.JSONObject;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.util.IdpsUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.persistence.DataObject;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.persistence.Row;

public class DirectoryTempDataValidator
{
    private static DirectoryTempDataValidator directoryTempDataValidator;
    private static final String TEMP_BLOCK = "DirectoryTempDataHandler";
    
    public static DirectoryTempDataValidator getInstance() {
        if (DirectoryTempDataValidator.directoryTempDataValidator == null) {
            DirectoryTempDataValidator.directoryTempDataValidator = new DirectoryTempDataValidator();
        }
        return DirectoryTempDataValidator.directoryTempDataValidator;
    }
    
    private Boolean isRowInValid(final Row row, final int maxPermissibleLength) throws MetaDataException {
        final String tableName = row.getTableName();
        final TableDefinition td = DirectoryQueryutil.getInstance().getTableDefinition(tableName);
        Boolean invalid = Boolean.FALSE;
        final List<String> columnNames = row.getColumns();
        for (int i = 0; i < columnNames.size(); ++i) {
            final String columnName = columnNames.get(i);
            final ColumnDefinition columnDefinition = td.getColumnDefinitionByName(columnName);
            final String columnDataType = columnDefinition.getDataType();
            int colMaxPermissibleLength = maxPermissibleLength;
            if (columnDataType.equalsIgnoreCase("NCHAR") || columnDataType.equalsIgnoreCase("CHAR") || columnDataType.contains("CHAR")) {
                final String value = String.valueOf(row.get(columnName));
                if (colMaxPermissibleLength != -1) {
                    colMaxPermissibleLength = Math.min(columnDefinition.getMaxLength(), colMaxPermissibleLength);
                }
                else {
                    colMaxPermissibleLength = columnDefinition.getMaxLength();
                }
                if (!SyMUtil.isStringEmpty(value) && value.length() > colMaxPermissibleLength) {
                    invalid = Boolean.TRUE;
                    row.set(columnName, (Object)value.substring(0, colMaxPermissibleLength));
                }
                else if (SyMUtil.isStringEmpty(value) && tableName.equalsIgnoreCase("DirObjTmp") && columnName.equalsIgnoreCase("GUID")) {
                    invalid = Boolean.TRUE;
                    row.set("GUID", (Object)("nonExistingGUID-" + String.valueOf(System.currentTimeMillis())));
                }
            }
            else if (columnDataType.equalsIgnoreCase("INTEGER") && tableName.equalsIgnoreCase("DirObjTmp") && columnName.equalsIgnoreCase("OBJECT_TYPE")) {
                Integer value2 = -1;
                try {
                    value2 = Integer.valueOf(String.valueOf(row.get(columnName)));
                }
                catch (final Exception ex) {
                    IDPSlogger.ERR.log(Level.FINE, "harmless exception", ex);
                }
                if (value2 == null || value2 == -1) {
                    invalid = true;
                }
            }
        }
        if (invalid) {
            IDPSlogger.ERR.log(Level.INFO, "invalid data {0}", new Object[] { row.toString() });
        }
        return invalid;
    }
    
    private int getMaxPermissibleLengthForKey(final Long attrID) {
        if (attrID == 114L) {
            return 30;
        }
        if (attrID == 103L) {
            return 250;
        }
        if (attrID == 107L) {
            return 100;
        }
        if (attrID == 112L) {
            return 100;
        }
        if (attrID == 2L) {
            return 100;
        }
        if (attrID == 104L) {
            return 100;
        }
        return -1;
    }
    
    int validateData(final DataObject dobj) throws MetaDataException, DataAccessException {
        int invalidCount = 0;
        final List<String> tempValTables = new ArrayList<String>(Arrays.asList("DirObjTmp", "DirObjTmpRegIntVal", "DirObjTmpRegStrVal", "DirObjTmpArrStrVal"));
        for (int i = 0; i < tempValTables.size(); ++i) {
            final String tableName = tempValTables.get(i);
            final Iterator iterator = dobj.getRows(tableName);
            while (iterator != null && iterator.hasNext()) {
                boolean invalid = Boolean.FALSE;
                final Row row = iterator.next();
                int maxPermissibleLengthForKey = -1;
                Long attrID = null;
                String attrIDcol = null;
                if (!tableName.equalsIgnoreCase("DirObjTmp")) {
                    attrIDcol = DirectoryQueryutil.getAttrIDcol(tableName);
                    attrID = (Long)row.get(attrIDcol);
                    maxPermissibleLengthForKey = this.getMaxPermissibleLengthForKey(attrID);
                }
                invalid |= this.isRowInValid(row, maxPermissibleLengthForKey);
                if (!tableName.equalsIgnoreCase("DirObjTmp")) {
                    final String valCol = DirectoryQueryutil.getTempValCol(tableName);
                    final String value = String.valueOf(row.get(valCol));
                    if (attrID == 106L || attrID == 112L) {
                        if (SyMUtil.isStringEmpty(value) || !IdpsUtil.getInstance().isValidEmail(value)) {
                            invalid = true;
                        }
                    }
                    else if (SyMUtil.isStringEmpty(value)) {
                        if (tableName.equalsIgnoreCase("DirObjTmpArrStrVal")) {
                            final Integer extnType = (Integer)row.get("KEY_DETAIL");
                            if (extnType != null) {
                                invalid = true;
                            }
                        }
                        else {
                            invalid = true;
                        }
                    }
                }
                row.set(DirectoryQueryutil.getInvalidcol(tableName), (Object)invalid);
                if (invalid) {
                    ++invalidCount;
                }
            }
        }
        for (int i = 0; i < tempValTables.size(); ++i) {
            final String curTableName = tempValTables.get(i);
            if (!curTableName.equalsIgnoreCase("DirObjTmp")) {
                dobj.deleteRows(curTableName, new Criteria(Column.getColumn(curTableName, DirectoryQueryutil.getInvalidcol(curTableName)), (Object)Boolean.TRUE, 0));
            }
        }
        return invalidCount;
    }
    
    private String getObjResourceName(final JSONObject adObjProps, final int resType) {
        switch (resType) {
            case 2: {
                final String email = (String)IdpsJSONutil.opt(adObjProps, "mail", null);
                final String upn = (String)IdpsJSONutil.opt(adObjProps, "userPrincipalName", null);
                final String samAccountName = (String)IdpsJSONutil.opt(adObjProps, "sAMAccountName", null);
                String validName = SyMUtil.isStringEmpty(samAccountName) ? (SyMUtil.isStringEmpty(upn) ? samAccountName : upn) : samAccountName;
                validName = (SyMUtil.isStringEmpty(validName) ? (SyMUtil.isStringEmpty(email) ? validName : email) : validName);
                return validName;
            }
            case 101: {
                return (String)IdpsJSONutil.opt(adObjProps, "name", null);
            }
            default: {
                if (adObjProps.containsKey((Object)"name")) {
                    return (String)adObjProps.get((Object)"name");
                }
                return null;
            }
        }
    }
    
    private JSONArray makeValueUnique(final JSONArray jsArray) {
        JSONArray resJSarray = null;
        if (jsArray != null) {
            resJSarray = new JSONArray();
            for (int j = 0; j < jsArray.size(); ++j) {
                final Object curValue = jsArray.get(j);
                if (curValue != null && !resJSarray.contains(curValue)) {
                    resJSarray.add(curValue);
                }
            }
        }
        return resJSarray;
    }
    
    public JSONObject filterInvalidData(final JSONArray directoryData, final Integer dmDomainClient, final Integer chunkResType) throws DataAccessException {
        Long timeStampRange = new Long(0L);
        int baseSize = directoryData.size();
        final JSONArray filteredDirData = new JSONArray();
        final JSONObject timeStampAllocationObj = new JSONObject();
        final String statusAttrKey = DirectoryAttributeConstants.getAttrKey(118L);
        final String resNameAttrKey = DirectoryAttributeConstants.getAttrKey(2L);
        for (int i = 0; i < directoryData.size(); ++i) {
            final JSONObject newJSobject = new JSONObject();
            final JSONObject jsObject = (JSONObject)directoryData.get(i);
            if (i != 0) {
                ++timeStampRange;
            }
            if (!jsObject.containsKey((Object)statusAttrKey)) {
                jsObject.put((Object)statusAttrKey, (Object)1);
            }
            final String objGUID = String.valueOf(jsObject.get((Object)"objectGUID"));
            newJSobject.put((Object)"objectGUID", (Object)objGUID);
            timeStampAllocationObj.put((Object)String.valueOf(timeStampRange), (Object)objGUID);
            if (jsObject.containsKey((Object)"RESOURCE_TYPE")) {
                newJSobject.put((Object)"RESOURCE_TYPE", jsObject.get((Object)"RESOURCE_TYPE"));
            }
            final int dirResType = Integer.valueOf(String.valueOf(IdpsJSONutil.opt(jsObject, "RESOURCE_TYPE", chunkResType)));
            final int actualResType = IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClient).getResourceType(dirResType);
            final String resourceName = this.getObjResourceName(jsObject, actualResType);
            if (!SyMUtil.isStringEmpty(resourceName)) {
                jsObject.put((Object)resNameAttrKey, (Object)resourceName);
            }
            final Set keys = jsObject.keySet();
            final Iterator itr = keys.iterator();
            while (itr != null && itr.hasNext()) {
                final String key = itr.next();
                final Long attrID = DirectoryUtil.getInstance().getAttrID(key);
                final Integer attrType = DirectoryAttributeConstants.getAttrType(attrID);
                if (attrID != null) {
                    Object value = jsObject.get((Object)key);
                    if (value == null) {
                        continue;
                    }
                    if (attrID == 106L || attrID == 112L) {
                        final String strVal = String.valueOf(value);
                        if (!IdpsUtil.getInstance().isValidEmail(strVal)) {
                            continue;
                        }
                    }
                    else if (attrID == 103L) {
                        final String strVal = String.valueOf(value);
                        if (SyMUtil.isStringEmpty(strVal)) {
                            continue;
                        }
                    }
                    long valueTimeStampAllocation = 0L;
                    if (attrType == 2) {
                        if (!(value instanceof JSONArray)) {
                            continue;
                        }
                        value = this.makeValueUnique((JSONArray)value);
                        valueTimeStampAllocation = ((JSONArray)value).size();
                        valueTimeStampAllocation = Math.max(1L, valueTimeStampAllocation);
                    }
                    else if (SyMUtil.isStringEmpty(String.valueOf(value))) {
                        continue;
                    }
                    final long keyTimeStampAllocation = 1L;
                    timeStampRange += keyTimeStampAllocation;
                    timeStampAllocationObj.put((Object)String.valueOf(timeStampRange), (Object)key);
                    timeStampRange += valueTimeStampAllocation;
                    timeStampAllocationObj.put((Object)String.valueOf(timeStampRange), value);
                    newJSobject.put((Object)key, value);
                }
            }
            if (newJSobject != null && !newJSobject.isEmpty()) {
                filteredDirData.add((Object)newJSobject);
            }
        }
        baseSize -= filteredDirData.size();
        if (baseSize > 0) {
            IDPSlogger.ERR.log(Level.SEVERE, "dropped data count : {0}", new Object[] { String.valueOf(baseSize) });
        }
        final JSONObject filteredDataDetails = new JSONObject();
        filteredDataDetails.put((Object)"DirResRel", (Object)filteredDirData);
        filteredDataDetails.put((Object)"PREVIOUS_BATCH_COMPLETED_AT", (Object)String.valueOf(timeStampRange));
        return filteredDataDetails;
    }
    
    private void invalidateNullExtArVal(final Connection connection, final Criteria tempValCri, final Long dmDomainID, final Long collationID) throws Exception {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmpArrStrVal");
        deleteQuery.setCriteria(tempValCri.and(new Criteria(Column.getColumn("DirObjTmpArrStrVal", "TEMP_VALUE"), (Object)null, 0)).and(new Criteria(Column.getColumn("DirObjTmpArrStrVal", "KEY_DETAIL"), (Object)null, 1)));
        final String logMsg = "deleting null value for extended array attributes";
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    private void identifyDuplGUID(final Connection connection, final Criteria tempCri, final Long dmDomainID, final Integer dmDomainClientID, final Long collationID) throws Exception {
        final Column duplGuidCol = new Column("DirObjTmp", "GUID", "base_DIROBJTMP.OBJECT_GUID");
        final Column duplGuidCountCol = IdpsUtil.getCountOfColumn(duplGuidCol, "base_count_DIROBJTMP.OBJECT_GUID");
        final Column maxTempIDcol = IdpsUtil.getMaxOfColumn("DirObjTmp", "TEMP_ID", "base_max_DIROBJTMP.TEMP_ID", -5);
        final Column maxAddedAtcol = IdpsUtil.getMaxOfColumn("DirObjTmp", "ADDED_AT", "base_max_DIROBJTMP.ADDED_AT", -5);
        final Column dirObjTypeCol = new Column("DirObjTmp", "OBJECT_TYPE", "base_DIROBJTMP.OBJECT_TYPE");
        final HashMap<String, Column> insertColMap = new HashMap<String, Column>();
        insertColMap.put("OBJECT_GUID", duplGuidCol);
        insertColMap.put("MAX_ADDED_AT", maxAddedAtcol);
        insertColMap.put("DUPLICATED_MAX_TEMP_ID", maxTempIDcol);
        final List<Column> groupBycols = new ArrayList<Column>(Arrays.asList(duplGuidCol));
        if (IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).isGUIDresTypeunique()) {
            groupBycols.add(dirObjTypeCol);
            insertColMap.put("OBJECT_TYPE", dirObjTypeCol);
        }
        final SelectQuery duplGuidDetectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjTmp"));
        duplGuidDetectQuery.setCriteria(tempCri);
        duplGuidDetectQuery.setGroupByClause(new GroupByClause((List)groupBycols, new Criteria(duplGuidCountCol, (Object)1, 5)));
        String logMsg = "inserting dupl guid into temp dupl table";
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, duplGuidDetectQuery, "DirObjTmpDupl", insertColMap, null, "DirectoryTempDataHandler", logMsg, false);
        UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjTmpDupl");
        updateQuery.addJoin(new Join("DirObjTmpDupl", "DirObjTmp", new String[] { "DUPLICATED_MAX_TEMP_ID" }, new String[] { "TEMP_ID" }, 2));
        updateQuery.setCriteria(tempCri.and(new Criteria(Column.getColumn("DirObjTmpDupl", "DM_DOMAIN_ID"), (Object)null, 0).or(new Criteria(Column.getColumn("DirObjTmpDupl", "COLLATION_ID"), (Object)null, 0))));
        updateQuery.setUpdateColumn("DM_DOMAIN_ID", (Object)dmDomainID);
        updateQuery.setUpdateColumn("COLLATION_ID", (Object)collationID);
        logMsg = "configuring dirobjtempdupl";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
        Join join = new Join("DirObjTmp", "DirObjTmpDupl", new String[] { "GUID" }, new String[] { "OBJECT_GUID" }, 2);
        if (IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).isGUIDresTypeunique()) {
            join = new Join("DirObjTmp", "DirObjTmpDupl", new String[] { "GUID", "OBJECT_TYPE" }, new String[] { "OBJECT_GUID", "OBJECT_TYPE" }, 2);
        }
        updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjTmp");
        updateQuery.addJoin(join);
        updateQuery.setCriteria(tempCri.and(new Criteria(Column.getColumn("DirObjTmpDupl", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirObjTmpDupl", "COLLATION_ID"), (Object)collationID, 0)));
        updateQuery.setUpdateColumn("MAX_ADDED_AT", (Object)Column.getColumn("DirObjTmpDupl", "MAX_ADDED_AT"));
        updateQuery.setUpdateColumn("DUPLICATED_MAX_TEMP_ID", (Object)Column.getColumn("DirObjTmpDupl", "DUPLICATED_MAX_TEMP_ID"));
        logMsg = "identifying dupl guid";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    private void transmitDuplIntoVal(final Connection connection, final String tableName, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap, final Long dmDomainID, final Long collationID) throws Exception {
        final String tempIDcol = DirectoryQueryutil.getTempIDcol(tableName);
        final String duplMaxTempIDcol = DirectoryQueryutil.getDuplMaxTempIDcol(tableName);
        final Criteria tempDuplCri = new Criteria(Column.getColumn("DirObjTmp", "DUPLICATED_MAX_TEMP_ID"), (Object)null, 1);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(tableName);
        updateQuery.addJoin(new Join(tableName, "DirObjTmp", new String[] { tempIDcol }, new String[] { "TEMP_ID" }, 2));
        updateQuery.setCriteria(tempCri.and((Criteria)tempValCriMap.get(tableName)).and(tempDuplCri));
        updateQuery.setUpdateColumn(duplMaxTempIDcol, (Object)Column.getColumn("DirObjTmp", "DUPLICATED_MAX_TEMP_ID"));
        final String logMsg = MessageFormat.format("transmitting dupl guid marking to {0}", tableName);
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    private void transmitDuplIntoVal(final Connection connection, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap, final Long dmDomainID, final Long collationID) throws Exception {
        this.transmitDuplIntoVal(connection, "DirObjTmpRegIntVal", tempCri, tempValCriMap, dmDomainID, collationID);
        this.transmitDuplIntoVal(connection, "DirObjTmpRegStrVal", tempCri, tempValCriMap, dmDomainID, collationID);
        this.transmitDuplIntoVal(connection, "DirObjTmpArrStrVal", tempCri, tempValCriMap, dmDomainID, collationID);
    }
    
    private void identifyMaxNEattrAmongDuplGUID(final Connection connection, final String tableName, final HashMap<String, Criteria> tempValCriMap, final Long dmDomainID, final Long collationID) throws Exception {
        final String duplAttrIDcolName = "ATTR_ID";
        final String duplMaxAddedAtcolName = "MAX_ADDED_AT";
        final String duplTempDuplValMaxTempIDcolName = "DUPLICATED_MAX_TEMP_ID";
        final String attrIDcolName = DirectoryQueryutil.getAttrIDcol(tableName);
        final String addedAtColName = DirectoryQueryutil.getAttrAddedAtCol(tableName);
        final String maxAddedAtcolName = DirectoryQueryutil.getMaxAddedAtCol(tableName);
        final String duplTempValMaxTempIDcolName = DirectoryQueryutil.getDuplMaxTempIDcol(tableName);
        final Column attrIDcol = Column.getColumn(tableName, attrIDcolName);
        final Column duplMaxTempIDcol = Column.getColumn(tableName, duplTempValMaxTempIDcolName);
        final Column duplAttrNEmaxAddedAtCol = IdpsUtil.getMaxOfColumn(tableName, addedAtColName, "MAX_" + tableName + ".ATTR_ADDED_AT", -5);
        final Column attrIDcountCol = IdpsUtil.getCountOfColumn(tableName, attrIDcolName, "attrIDcount");
        final Criteria duplAttrBaseCri = new Criteria(duplMaxTempIDcol, (Object)null, 1);
        Criteria criteria = tempValCriMap.get(tableName).and(duplAttrBaseCri);
        final SelectQuery duplAttrGuidQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        if (tableName.equalsIgnoreCase("DirObjTmpArrStrVal")) {
            criteria = criteria.and(new Criteria(Column.getColumn("DirObjTmpArrStrVal", "KEY_DETAIL"), (Object)null, 0));
        }
        duplAttrGuidQuery.setCriteria(criteria);
        duplAttrGuidQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(attrIDcol, duplMaxTempIDcol)), new Criteria(attrIDcountCol, (Object)1, 5)));
        final HashMap<String, Column> insertColMap = new HashMap<String, Column>();
        insertColMap.put(duplAttrIDcolName, attrIDcol);
        insertColMap.put(duplMaxAddedAtcolName, duplAttrNEmaxAddedAtCol);
        insertColMap.put(duplTempDuplValMaxTempIDcolName, duplMaxTempIDcol);
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, duplAttrGuidQuery, "DirObjTmpDuplAttr", insertColMap, null, "DirectoryTempDataHandler", null, false);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(tableName);
        updateQuery.addJoin(new Join(tableName, "DirObjTmpDuplAttr", new String[] { duplTempValMaxTempIDcolName, attrIDcolName }, new String[] { duplTempDuplValMaxTempIDcolName, duplAttrIDcolName }, 2));
        updateQuery.setCriteria(tempValCriMap.get(tableName).and(duplAttrBaseCri));
        updateQuery.setUpdateColumn(maxAddedAtcolName, (Object)Column.getColumn("DirObjTmpDuplAttr", duplMaxAddedAtcolName));
        final String logMsg = "identifying latest non-extended attribute among duplicated attributes of duplicated guids";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
        final Criteria maxAddedAtNotNullCri = new Criteria(Column.getColumn(tableName, maxAddedAtcolName), (Object)null, 1);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmpDuplAttr");
        deleteQuery.addJoin(new Join("DirObjTmpDuplAttr", tableName, new String[] { duplTempDuplValMaxTempIDcolName, duplAttrIDcolName, duplMaxAddedAtcolName }, new String[] { duplTempValMaxTempIDcolName, attrIDcolName, maxAddedAtcolName }, 2));
        deleteQuery.setCriteria(updateQuery.getCriteria().and(maxAddedAtNotNullCri));
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "DirectoryTempDataHandler", null, false);
    }
    
    private void identifyMaxNEattrAmongDuplGUID(final Connection connection, final HashMap<String, Criteria> tempValCriMap, final Long dmDomainID, final Long collationID) throws Exception {
        this.identifyMaxNEattrAmongDuplGUID(connection, "DirObjTmpRegIntVal", tempValCriMap, dmDomainID, collationID);
        this.identifyMaxNEattrAmongDuplGUID(connection, "DirObjTmpRegStrVal", tempValCriMap, dmDomainID, collationID);
        this.identifyMaxNEattrAmongDuplGUID(connection, "DirObjTmpArrStrVal", tempValCriMap, dmDomainID, collationID);
    }
    
    void identifyMaxEarrAttrAmongDuplGUID(final Connection connection, final HashMap<String, Criteria> tempValCriMap, final Long dmDomainID, final Long collationID) throws Exception {
        final Column attrIDcol = Column.getColumn("DirObjTmpArrStrVal", "ATTR_ID");
        final Column dlHintcol = Column.getColumn("DirObjTmpArrStrVal", "DERIVED_LONG_HINT");
        final Column duplMaxTempIDcol = Column.getColumn("DirObjTmpArrStrVal", "DUPLICATED_MAX_TEMP_ID");
        final Column duplAttrNEmaxAddedAtCol = IdpsUtil.getMaxOfColumn("DirObjTmpArrStrVal", "VAL_ADDED_AT", "MAX_DirObjTmpArrStrVal.VAL_ADDED_AT", -5);
        final Criteria duplAttrBaseCri = new Criteria(duplMaxTempIDcol, (Object)null, 1);
        final Criteria extCri = new Criteria(Column.getColumn("DirObjTmpArrStrVal", "KEY_DETAIL"), (Object)null, 1).and(new Criteria(Column.getColumn("DirObjTmpArrStrVal", "DERIVED_LONG_HINT"), (Object)null, 1));
        final Criteria criteria = tempValCriMap.get("DirObjTmpArrStrVal").and(duplAttrBaseCri).and(extCri);
        final SelectQuery duplAttrGuidQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjTmpArrStrVal"));
        duplAttrGuidQuery.setCriteria(criteria);
        duplAttrGuidQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(attrIDcol, duplMaxTempIDcol, dlHintcol)), new Criteria(IdpsUtil.getCountOfColumn(attrIDcol), (Object)1, 5)));
        final HashMap<String, Column> insertColMap = new HashMap<String, Column>();
        insertColMap.put("ATTR_ID", attrIDcol);
        insertColMap.put("DERIVED_LONG_HINT", dlHintcol);
        insertColMap.put("MAX_ADDED_AT", duplAttrNEmaxAddedAtCol);
        insertColMap.put("DUPLICATED_MAX_TEMP_ID", duplMaxTempIDcol);
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, duplAttrGuidQuery, "DirObjTmpDuplVal", insertColMap, null, "DirectoryTempDataHandler", null, false);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjTmpArrStrVal");
        updateQuery.addJoin(new Join("DirObjTmpArrStrVal", "DirObjTmpDuplVal", new String[] { "DUPLICATED_MAX_TEMP_ID", "ATTR_ID", "DERIVED_LONG_HINT" }, new String[] { "DUPLICATED_MAX_TEMP_ID", "ATTR_ID", "DERIVED_LONG_HINT" }, 2));
        updateQuery.setCriteria(duplAttrGuidQuery.getCriteria());
        updateQuery.setUpdateColumn("MAX_ADDED_AT", (Object)Column.getColumn("DirObjTmpDuplVal", "MAX_ADDED_AT"));
        final String logMsg = "identifying latest extended attribute value among duplicated attributes of duplicated guids";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    void invalidLTmaxNEattr(final Connection connection, final String tableName, final HashMap<String, Criteria> tempValCriMap, final Long dmDomainID, final Long collationID, final boolean valTimestampCheck) throws Exception {
        Column timeStampCol = Column.getColumn(tableName, DirectoryQueryutil.getAttrAddedAtCol(tableName));
        final Column maxAttrAddedAtcol = Column.getColumn(tableName, DirectoryQueryutil.getMaxAddedAtCol(tableName));
        if (tableName.equalsIgnoreCase("DirObjTmpArrStrVal") && valTimestampCheck) {
            timeStampCol = Column.getColumn(tableName, "VAL_ADDED_AT");
        }
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(tableName);
        updateQuery.setCriteria(tempValCriMap.get(tableName).and(new Criteria(maxAttrAddedAtcol, (Object)null, 1)).and(new Criteria(timeStampCol, (Object)maxAttrAddedAtcol, 7)).and(new Criteria(Column.getColumn(tableName, DirectoryQueryutil.getDuplMaxTempIDcol(tableName)), (Object)null, 1)));
        updateQuery.setUpdateColumn(DirectoryQueryutil.getInvalidcol(tableName), (Object)Boolean.TRUE);
        final String logMsg = "invalidating attributes added before max added time among duplicated attributes of duplicated guids";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    private void invalidLTmaxNEattr(final Connection connection, final HashMap<String, Criteria> tempValCriMap, final Long dmDomainID, final Long collationID) throws Exception {
        this.invalidLTmaxNEattr(connection, "DirObjTmpRegIntVal", tempValCriMap, dmDomainID, collationID, false);
        this.invalidLTmaxNEattr(connection, "DirObjTmpRegStrVal", tempValCriMap, dmDomainID, collationID, false);
        this.invalidLTmaxNEattr(connection, "DirObjTmpArrStrVal", tempValCriMap, dmDomainID, collationID, false);
    }
    
    void prepareArrVal(final Connection connection, final HashMap<String, Criteria> tempValCriMap, final Integer dmDomainClientID, final Long dmDomainID, final Long collationID) throws Exception {
        final Criteria tempValCri = tempValCriMap.get("DirObjTmpArrStrVal");
        Join join = new Join("DirObjTmpArrStrVal", "DirResRel", new String[] { "TEMP_VALUE" }, new String[] { "GUID" }, 2);
        if (IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).isGUIDresTypeunique()) {
            join = new Join("DirObjTmpArrStrVal", "DirResRel", new String[] { "TEMP_VALUE", "DIR_RESOURCE_TYPE" }, new String[] { "GUID", "DIR_RESOURCE_TYPE" }, 2);
        }
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjTmpArrStrVal");
        updateQuery.addJoin(join);
        updateQuery.setCriteria(tempValCri.and(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirObjTmpArrStrVal", "TEMP_VALUE"), (Object)null, 1)));
        updateQuery.setUpdateColumn("DERIVED_LONG_HINT", (Object)Column.getColumn("DirResRel", "OBJ_ID"));
        final String logMsg = "resolving long hint for array type temp values";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    private void invalidateDuplicateEntries(final Connection connection, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap, final Long dmDomainID, final Integer dmDomainClientID, final Long collationID) throws Exception {
        this.invalidateNullExtArVal(connection, tempValCriMap.get("DirObjTmpArrStrVal"), dmDomainID, collationID);
        this.identifyDuplGUID(connection, tempCri, dmDomainID, dmDomainClientID, collationID);
        this.transmitDuplIntoVal(connection, tempCri, tempValCriMap, dmDomainID, collationID);
        this.identifyMaxNEattrAmongDuplGUID(connection, tempValCriMap, dmDomainID, collationID);
        this.invalidLTmaxNEattr(connection, tempValCriMap, dmDomainID, collationID);
    }
    
    void deleteInvalidEntriesFromTempVal(final Connection connection, final String tableName, final Long dmDomainID, final Long collationID) throws Exception {
        final Criteria tempValDomCollCri = DirectoryDataPersistor.getInstance().getTempValDomCri(tableName, dmDomainID).and(DirectoryDataPersistor.getInstance().getTempValCollCri(tableName, collationID));
        final Criteria objAttrInvalidCri = new Criteria(Column.getColumn(tableName, DirectoryQueryutil.getInvalidcol(tableName)), (Object)Boolean.TRUE, 0);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
        deleteQuery.setCriteria(tempValDomCollCri.and(objAttrInvalidCri));
        final String logMsg = MessageFormat.format("deleting invalid entries from {0}", tableName);
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    private void deleteInvalidEntriesFromTemp(final Connection connection, final Long dmDomainID, final Long collationID) throws Exception {
        final Criteria objInvalidCri = new Criteria(Column.getColumn("DirObjTmp", "IS_INVALID"), (Object)Boolean.TRUE, 0);
        final Criteria tempDomCollCri = DirectoryDataPersistor.getInstance().getTempDomCri(dmDomainID).and(DirectoryDataPersistor.getInstance().getTempCollCri(collationID));
        this.deleteInvalidEntriesFromTempVal(connection, "DirObjTmpRegIntVal", dmDomainID, collationID);
        this.deleteInvalidEntriesFromTempVal(connection, "DirObjTmpRegStrVal", dmDomainID, collationID);
        this.deleteInvalidEntriesFromTempVal(connection, "DirObjTmpArrStrVal", dmDomainID, collationID);
        DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmp");
        deleteQuery.setCriteria(tempDomCollCri.and(objInvalidCri));
        String logMsg = "deleting invalid obj";
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "DirectoryTempDataHandler", logMsg, false);
        deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmp");
        deleteQuery.addJoin(new Join("DirObjTmp", "DirectorySyncDetails", new String[] { "SYNC_TOKEN_ID" }, new String[] { "SYNC_TOKEN_ID" }, 2));
        deleteQuery.setCriteria(new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)null, 1).and(new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)collationID, 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)931, 0)).and(new Criteria(Column.getColumn("DirObjTmp", "COLLATION_ID"), (Object)null, 0).or(new Criteria(Column.getColumn("DirObjTmp", "DM_DOMAIN_ID"), (Object)null, 0))));
        logMsg = "deleting obj which were not collated";
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, dmDomainID, collationID, deleteQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    private void markObjMaxAddedAtForNonDuplObj(final Connection connection, final Criteria tempCri, final Long dmDomainID, final Long collationID) throws Exception {
        final Criteria nonDuplCri = new Criteria(Column.getColumn("DirObjTmp", "MAX_ADDED_AT"), (Object)null, 0).and(new Criteria(Column.getColumn("DirObjTmp", "DUPLICATED_MAX_TEMP_ID"), (Object)null, 0));
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjTmp");
        updateQuery.setCriteria(tempCri.and(nonDuplCri));
        updateQuery.setUpdateColumn("MAX_ADDED_AT", (Object)Column.getColumn("DirObjTmp", "ADDED_AT"));
        final String logMsg = "marking max added time of non duplicated obj";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "DirectoryTempDataHandler", logMsg, false);
    }
    
    void validateTempDataBeforePersisting(final Connection connection, final Integer syncType, final String domainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClientID, final Long collationID, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap) throws Exception {
        DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", I18N.getMsg("mdm.ad.invalidate.dupl", new Object[0]));
        this.invalidateDuplicateEntries(connection, tempCri, tempValCriMap, dmDomainID, dmDomainClientID, collationID);
        DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", I18N.getMsg("mdm.ad.del.invalid", new Object[0]));
        this.deleteInvalidEntriesFromTemp(connection, dmDomainID, collationID);
        this.markObjMaxAddedAtForNonDuplObj(connection, tempCri, dmDomainID, collationID);
        this.validateTempMaxAddedAt(connection, dmDomainID, collationID, tempCri);
        IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).validateData(connection, tempCri, tempValCriMap, syncType, domainName, customerID, dmDomainID, dmDomainClientID);
    }
    
    private void checkQueryResponse(final Connection connection, final SelectQuery selectQuery, final Long dmDomainID, final Long collationID, final String tempIDalias, final String exMsg, final String logMsg) throws Exception {
        if (DirectoryUtil.getInstance().canExecQuery(dmDomainID, collationID)) {
            IDPSlogger.DBO.log(Level.INFO, logMsg);
            final JSONArray jsonArray = IdpsUtil.executeSelectQuery(connection, selectQuery);
            if (jsonArray != null && !jsonArray.isEmpty()) {
                for (int i = 0; i < jsonArray.size(); ++i) {
                    final JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    if (jsonObject != null && jsonObject.containsKey((Object)tempIDalias)) {
                        final Integer count = Integer.valueOf(String.valueOf(jsonObject.get((Object)tempIDalias)));
                        if (count > 0) {
                            throw new Exception(exMsg);
                        }
                    }
                }
            }
        }
    }
    
    private void validateTempMaxAddedAt(final Connection connection, final Long dmDomainID, final Long collationID, final Criteria tempCri) throws Exception {
        final Column tempIDcountCol = IdpsUtil.getCountOfColumn("DirObjTmp", "TEMP_ID", "DIROBJTEMP_TEMP_ID_COUNT");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjTmp"));
        selectQuery.setCriteria(tempCri.and(new Criteria(Column.getColumn("DirObjTmp", "MAX_ADDED_AT"), (Object)null, 0).or(new Criteria(Column.getColumn("DirObjTmp", "MAX_ADDED_AT"), (Object)0L, 6))));
        selectQuery.addSelectColumn(tempIDcountCol);
        final String logMsg = "checking if any obj don't have max added time value";
        this.checkQueryResponse(connection, selectQuery, dmDomainID, collationID, tempIDcountCol.getColumnAlias(), "INTERNAL_ERROR", logMsg);
    }
    
    static {
        DirectoryTempDataValidator.directoryTempDataValidator = null;
    }
}
