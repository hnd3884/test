package com.adventnet.client.util;

import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Join;
import java.util.HashMap;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.persistence.PersistenceUtil;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.util.QueryUtil;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;

public class DataUtils
{
    public static DataObject getDataObject(final String customViewName, final DataObject criteriaDO) throws Exception {
        if (customViewName == null) {
            throw new Exception("customViewName is null");
        }
        final Row row = new Row("CustomViewConfiguration");
        row.set("CVNAME", (Object)customViewName);
        final DataObject doObj = LookUpUtil.getPersistence().getForPersonalities((List)StaticLists.CUSTOMVIEW, (List)StaticLists.CUSTOMVIEW, row);
        final SelectQuery[] selectArray = QueryUtil.getSelectQueryFromDO(doObj);
        final SelectQuery selectQuery = selectArray[0];
        final Criteria criteria = selectQuery.getCriteria();
        if (criteria != null) {
            final Criteria newCriteria = QueryUtil.getTemplateReplacedCriteria(criteria, criteriaDO);
            selectQuery.setCriteria(newCriteria);
        }
        final DataObject dataObject = LookUpUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    public static List<Row> getSortedList(final DataObject configDO, final String tableName, final String indexColName) {
        try {
            final int size = configDO.size(tableName);
            if (size == -1) {
                return new ArrayList<Row>(0);
            }
            final ArrayList<Row> sortedList = new ArrayList<Row>(size);
            final Iterator<Row> ite = configDO.getRows(tableName);
            while (ite.hasNext()) {
                sortedList.add(ite.next());
            }
            final int colIndex = sortedList.get(0).findColumn(indexColName);
            Collections.sort(sortedList, new Comparator<Object>() {
                @Override
                public int compare(final Object row1, final Object row2) {
                    final Object val = ((Row)row1).get(colIndex);
                    final Object val2 = ((Row)row2).get(colIndex);
                    if (val instanceof Integer) {
                        return ((Integer)val).compareTo((Integer)val2);
                    }
                    if (val instanceof Long) {
                        return ((Long)val).compareTo((Long)val2);
                    }
                    if (val instanceof Double) {
                        return ((Double)val).compareTo((Double)val2);
                    }
                    throw new RuntimeException("Unhandled Type");
                }
            });
            return sortedList;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void cascadeChangePKColumn(final DataObject dao, final String rootTableName, final String rootPKColName, final Object newPKValue) throws DataAccessException {
        List<String> tables = dao.getTableNames();
        tables = PersistenceUtil.sortTables((List)tables);
        final List<Join> jList = QueryConstructor.getJoins((List)tables);
        final HashMap<String, String> tableNameToCol = new HashMap<String, String>();
        tableNameToCol.put(rootTableName, rootPKColName);
        updateColMapping(tableNameToCol, jList, rootTableName);
        for (int i = 0, j = tables.size(); i < j; ++i) {
            final String tableName = tables.get(i);
            final String colName = tableNameToCol.get(tableName);
            final Iterator<Row> ite = dao.getRows(tableName);
            while (ite.hasNext()) {
                final Row rowToBeUpdated = ite.next();
                rowToBeUpdated.set(colName, newPKValue);
                dao.updateRow(rowToBeUpdated);
            }
        }
    }
    
    private static void updateColMapping(final HashMap<String, String> tableNameToCol, final List<Join> joinsList, final String curTableName) {
        final String colName = tableNameToCol.get(curTableName);
        for (int i = 0, j = joinsList.size(); i < j; ++i) {
            final Join jn = joinsList.get(i);
            if (jn != null) {
                if (jn.getBaseTableName().equals(curTableName)) {
                    joinsList.set(i, null);
                    for (int k = 0, l = jn.getNumberOfColumns(); k < l; ++k) {
                        if (jn.getBaseTableColumn(k).equals(colName)) {
                            tableNameToCol.put(jn.getReferencedTableName(), jn.getReferencedTableColumn(k));
                        }
                    }
                    updateColMapping(tableNameToCol, joinsList, jn.getReferencedTableName());
                }
            }
        }
    }
    
    public static DataObject getFromCache(final String persName, final String dominantTableName, final String pkColName, final Object pkValue) throws Exception {
        return getFromCache(persName, null, dominantTableName, pkColName, pkValue);
    }
    
    public static DataObject getFromCache(final List deepFetchedPersList, final String dominantTableName, final String pkColName, final Object pkValue) throws Exception {
        return getFromCache(null, deepFetchedPersList, dominantTableName, pkColName, pkValue);
    }
    
    private static DataObject getFromCache(final String persName, final List deepFetchedPersList, final String dominantTableName, final String pkColName, final Object pkValue) throws Exception {
        DataObject configDO = null;
        final String key = persName + ":" + pkValue;
        configDO = (DataObject)StaticCache.getFromCache(key);
        if (configDO == null) {
            final Criteria criteria = new Criteria(Column.getColumn(dominantTableName, pkColName), pkValue, 0);
            if (persName != null) {
                configDO = LookUpUtil.getPersistence().getForPersonality(persName, criteria);
            }
            else {
                configDO = LookUpUtil.getPersistence().getForPersonalities(deepFetchedPersList, deepFetchedPersList, criteria);
            }
            if (persName != null) {
                StaticCache.addToCache(key, configDO, PersonalityConfigurationUtil.getConstituentTables(persName));
            }
            else {
                StaticCache.addToCache(key, configDO, PersonalityConfigurationUtil.getConstituentTables((String)deepFetchedPersList.get(0)));
            }
            ((WritableDataObject)configDO).makeImmutable();
        }
        return configDO;
    }
    
    public static int getMaxIndex(final DataObject data, final String tableName, final int colIndex) throws DataAccessException {
        final Iterator<Row> iterator = data.getRows(tableName);
        int index = -1;
        while (iterator.hasNext()) {
            final Row currentRow = iterator.next();
            final int currentIndex = (int)currentRow.get(colIndex);
            if (currentIndex > index) {
                index = currentIndex;
            }
        }
        return index;
    }
    
    public static List<Row> getRowsAsList(final String tableName, final DataObject origDO) throws Exception {
        final ArrayList<Row> tblList = new ArrayList<Row>();
        final Iterator<Row> ite = origDO.getRows(tableName);
        while (ite.hasNext()) {
            tblList.add(ite.next());
        }
        return tblList;
    }
    
    public static void addValuesToList(final String tableName, final String colName, final DataObject origDO, final List<Object> listToAdd) throws Exception {
        int index = -1;
        final Iterator<Row> ite = origDO.getRows(tableName);
        while (ite.hasNext()) {
            final Row r = ite.next();
            if (index < -1) {
                index = r.findColumn(colName);
            }
            listToAdd.add(r.get(index));
        }
    }
}
