package com.adventnet.client.components.cv.web.cvcreation;

import com.adventnet.persistence.cache.CacheManager;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.util.StaticLists;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.List;

public class CVUtility
{
    public static String getComparatorString(final String value, final int comparator, final String datatype) {
        String comparatorString = "";
        if (datatype.equals("T") || datatype.equals("B")) {
            if (comparator == 0) {
                comparatorString = "is";
            }
            else if (comparator == 1) {
                comparatorString = "isn't";
            }
            if (comparator == 3) {
                comparatorString = "doesn't contain";
            }
            if (comparator == 2) {
                if (value.startsWith("*") && value.endsWith("*")) {
                    comparatorString = "contains";
                }
                else if (value.endsWith("*")) {
                    comparatorString = "starts with";
                }
                else if (value.startsWith("*")) {
                    comparatorString = "ends with";
                }
                else {
                    comparatorString = "contains";
                }
            }
        }
        else if (datatype.equals("D")) {
            if (comparator == 0) {
                comparatorString = "is";
            }
            else if (comparator == 1) {
                comparatorString = "isn't";
            }
            else if (comparator == 6) {
                comparatorString = "is before";
            }
            else if (comparator == 4) {
                comparatorString = "is after";
            }
        }
        else if (datatype.equals("N")) {
            if (comparator == 0) {
                comparatorString = "=";
            }
            else if (comparator == 1) {
                comparatorString = "<>";
            }
            else if (comparator == 4) {
                comparatorString = ">=";
            }
            else if (comparator == 5) {
                comparatorString = ">";
            }
            else if (comparator == 6) {
                comparatorString = "<=";
            }
            else if (comparator == 7) {
                comparatorString = "<";
            }
        }
        return comparatorString;
    }
    
    public static String getMappedDataType(final String dataType) {
        final String charectorType = "T";
        final String numericType = "N";
        final String dateType = "D";
        final String booleanType = "B";
        final String notSupportedType = "NOT_SUPPORTED_DATATYPE";
        if (dataType.equals("CHAR")) {
            return charectorType;
        }
        if (dataType.equals("INTEGER")) {
            return numericType;
        }
        if (dataType.equals("BIGINT")) {
            return numericType;
        }
        if (dataType.equals("DOUBLE")) {
            return numericType;
        }
        if (dataType.equals("FLOAT")) {
            return numericType;
        }
        if (dataType.equals("BOOLEAN")) {
            return booleanType;
        }
        if (dataType.equals("DATE")) {
            return dateType;
        }
        if (dataType.equals("DATETIME")) {
            return dateType;
        }
        if (dataType.equals("TIME")) {
            return dateType;
        }
        if (dataType.equals("TIMESTAMP")) {
            return dateType;
        }
        return notSupportedType;
    }
    
    public static List getPKColList(final String tableName) throws MetaDataException {
        final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
        final PrimaryKeyDefinition pkdef = tableDef.getPrimaryKey();
        if (pkdef != null) {
            final List pkList = pkdef.getColumnList();
            return (pkList == null || pkList.size() == 0) ? null : pkList;
        }
        return null;
    }
    
    public static void checkAndAddCols(final HashMap tableAndCols, final String tableName, final Column selCol) throws MetaDataException {
        if (!tableAndCols.containsKey(tableName)) {
            final ArrayList cols = new ArrayList();
            boolean exists = false;
            final List pkList = getPKColList(tableName);
            for (int k = 0; pkList != null && k < pkList.size(); ++k) {
                final String pkColumnName = pkList.get(k);
                final Column pkColumn = new Column(tableName, pkColumnName);
                cols.add(pkColumn);
                if (pkColumnName.equalsIgnoreCase(selCol.getColumnName())) {
                    exists = true;
                }
            }
            if (!exists) {
                cols.add(selCol);
            }
            tableAndCols.put(tableName, cols);
        }
        else {
            final ArrayList cols = tableAndCols.get(tableName);
            boolean exists = false;
            final List pkList = getPKColList(tableName);
            for (int k = 0; pkList != null && k < pkList.size(); ++k) {
                final String pkColumnName = pkList.get(k);
                if (pkColumnName.equalsIgnoreCase(selCol.getColumnName())) {
                    exists = true;
                }
            }
            if (!exists) {
                cols.add(selCol);
            }
            tableAndCols.put(tableName, cols);
        }
    }
    
    public static Integer getQueryConstants(final String comparator) {
        Integer constant = null;
        if (comparator.equals("is") || comparator.equals("=")) {
            constant = new Integer(0);
        }
        else if (comparator.equals("isn't") || comparator.equals("<>")) {
            constant = new Integer(1);
        }
        else if (comparator.equals("contains") || comparator.equals("starts with") || comparator.equals("ends with")) {
            constant = new Integer(2);
        }
        else if (comparator.equals("doesn't contain")) {
            constant = new Integer(3);
        }
        else if (comparator.equals("<")) {
            constant = new Integer(7);
        }
        else if (comparator.equals(">")) {
            constant = new Integer(5);
        }
        else if (comparator.equals("<=") || comparator.equals("is before")) {
            constant = new Integer(6);
        }
        else if (comparator.equals(">=") || comparator.equals("is after")) {
            constant = new Integer(4);
        }
        return constant;
    }
    
    public static void addOrUpdateRow(final DataObject dob, final Row rw, final boolean isAdd) throws DataAccessException {
        final int[] pkIndexes = rw.getKeyIndices();
        final Row existingRow = new Row(rw.getOriginalTableName());
        for (int i = 0; i < pkIndexes.length; ++i) {
            existingRow.set(pkIndexes[i], rw.get(pkIndexes[i]));
        }
        final Row foundRow = dob.findRow(existingRow);
        if (isAdd || foundRow == null) {
            dob.addRow(rw);
        }
        else {
            dob.updateRow(rw);
        }
    }
    
    public static DataObject getViewDetails(final String viewName) {
        try {
            final Row viewConfigRow = new Row("ViewConfiguration");
            viewConfigRow.set(2, (Object)viewName);
            final DataObject viewConfig = LookUpUtil.getPersistence().getForPersonalities(StaticLists.VIEWCONFIGURATIONPERS, StaticLists.VIEWCONFIGURATIONPERS, viewConfigRow);
            if (!viewConfig.containsTable("ACTableViewConfig") && !viewConfig.containsTable("ACFormConfig")) {
                return viewConfig;
            }
            Long colConfList = null;
            Long psConfList = null;
            String navigConfigName = null;
            Object cvId = null;
            if (viewConfig.containsTable("ACTableViewConfig")) {
                colConfList = (Long)viewConfig.getFirstValue("ACTableViewConfig", 4);
                psConfList = (Long)viewConfig.getFirstValue("ACTableViewConfig", 5);
                navigConfigName = WebViewAPI.getViewName((Object)viewConfig.getFirstValue("ACTableViewConfig", 1));
                cvId = viewConfig.getFirstValue("ACTableViewConfig", 2);
            }
            else {
                colConfList = (Long)viewConfig.getFirstValue("ACFormConfig", 3);
                psConfList = (Long)viewConfig.getFirstValue("ACFormConfig", 4);
                cvId = viewConfig.getFirstValue("ACFormConfig", 9);
            }
            final Row accList = new Row("ACColumnConfigurationList");
            accList.set(1, (Object)colConfList);
            final DataObject colConfig = LookUpUtil.getPersistence().getForPersonalities(StaticLists.COLUMNCONFIGURATIONPERS, StaticLists.COLUMNCONFIGURATIONPERS, accList);
            viewConfig.merge(colConfig);
            if (psConfList != null) {
                final Row customViewRow = new Row("CustomViewConfiguration");
                customViewRow.set("CVNAME", (Object)"PSCV");
                final DataObject customViewDO = LookUpUtil.getPersistence().get("CustomViewConfiguration", customViewRow);
                final long queryID = (long)customViewDO.getFirstValue("CustomViewConfiguration", "QUERYID");
                final SelectQuery query = QueryUtil.getSelectQuery(queryID);
                final Criteria crit = new Criteria(new Column("ACPSConfiguration", "CONFIGNAME"), (Object)psConfList, 0);
                query.setCriteria(crit);
                final DataObject psdo = LookUpUtil.getPersistence().get(query);
                viewConfig.merge(psdo);
            }
            if (navigConfigName != null) {
                final Row navigRow = new Row("ACNavigationConfiguration");
                navigRow.set("NAME", (Object)navigConfigName);
                final DataObject navigDO = LookUpUtil.getPersistence().getForPersonality("NavigationConfig", navigRow);
                viewConfig.merge(navigDO);
            }
            if (cvId != null) {
                final Row cvRow = new Row("CustomViewConfiguration");
                cvRow.set(1, cvId);
                final DataObject cvDO = LookUpUtil.getPersistence().getForPersonality("CustomViewConfiguration", cvRow);
                viewConfig.merge(cvDO);
                final Long queryId = (Long)cvDO.getFirstValue("CustomViewConfiguration", 3);
                final Row sqRow = new Row("SelectQuery");
                sqRow.set(1, (Object)queryId);
                final DataObject sqDO = LookUpUtil.getPersistence().getForPersonality("SelectQuery", sqRow);
                viewConfig.merge(sqDO);
            }
            return viewConfig;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void createACTableViewFromTemplate(final String tmpview, final String newview, final String[] viewcols, final String[] displayNames, final String transformer) throws Exception {
        DataObject dobj = getViewDetails(tmpview);
        DataObject columlistdobj = LookUpUtil.getPersistence().get("ACColumnConfigurationList", new Criteria(new Column("ACColumnConfigurationList", "NAME"), (Object)tmpview, 0));
        dobj = PersistenceUtil.constructDO(dobj);
        final Row viewrow = dobj.getRow("ViewConfiguration");
        viewrow.set("VIEWNAME", (Object)newview);
        columlistdobj = PersistenceUtil.constructDO(columlistdobj);
        columlistdobj.set("ACColumnConfigurationList", "NAME", (Object)newview);
        final Row colList = columlistdobj.getRow("ACColumnConfigurationList");
        dobj.set("ACTableViewConfig", 4, colList.get(1));
        for (int i = 0; i < viewcols.length; ++i) {
            final Row accolumnConfig = new Row("ACColumnConfiguration");
            accolumnConfig.set(1, colList.get(1));
            accolumnConfig.set("COLUMNINDEX", (Object)(i + 1));
            accolumnConfig.set(3, (Object)viewcols[i]);
            accolumnConfig.set("DISPLAYNAME", (Object)displayNames[i]);
            accolumnConfig.set(5, (Object)true);
            accolumnConfig.set(6, (Object)true);
            accolumnConfig.set(9, (Object)true);
            if (transformer != null) {
                accolumnConfig.set("TRANSFORMER", (Object)transformer);
            }
            columlistdobj.addRow(accolumnConfig);
        }
        LookUpUtil.getPersistence().add(columlistdobj);
        LookUpUtil.getPersistence().add(dobj);
    }
    
    public static void updateACTableView(final String existingview, final String[] viewcols, final String[] displayNames, final String transformer) throws Exception {
        final ArrayList list = new ArrayList();
        list.add("ACColumnConfigurationList");
        list.add("ACColumnConfiguration");
        final DataObject existingcolumlistdobj = LookUpUtil.getPersistence().get((List)list, new Criteria(new Column("ACColumnConfigurationList", "NAME"), (Object)existingview, 0));
        final DataObject columnlistdobj = (DataObject)existingcolumlistdobj.clone();
        if (columnlistdobj.containsTable("ACColumnConfiguration")) {
            columnlistdobj.deleteRows("ACColumnConfiguration", (Criteria)null);
        }
        final Row colList = columnlistdobj.getRow("ACColumnConfigurationList");
        for (int i = 0; i < viewcols.length; ++i) {
            final Row accolumnConfig = new Row("ACColumnConfiguration");
            accolumnConfig.set(1, colList.get(1));
            accolumnConfig.set("COLUMNINDEX", (Object)(i + 1));
            accolumnConfig.set(3, (Object)viewcols[i]);
            accolumnConfig.set("DISPLAYNAME", (Object)displayNames[i]);
            accolumnConfig.set(5, (Object)true);
            accolumnConfig.set(6, (Object)true);
            accolumnConfig.set(9, (Object)true);
            if (transformer != null) {
                accolumnConfig.set("TRANSFORMER", (Object)transformer);
            }
            columnlistdobj.addRow(accolumnConfig);
        }
        final DataObject changesDO = existingcolumlistdobj.diff(columnlistdobj);
        LookUpUtil.getPersistence().update(changesDO);
        CacheManager.getCacheRepository().clearCachedData();
    }
}
