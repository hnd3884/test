package com.adventnet.persistence;

import com.zoho.conf.Configuration;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Collections;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Properties;
import com.zoho.mickey.crypto.DefaultDBPasswordProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.adventnet.persistence.json.JsonUtil;
import java.util.HashSet;
import com.adventnet.ds.query.QueryToJsonConverter;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.ds.query.AlterTableQuery;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.SecureRandom;
import com.adventnet.db.persistence.metadata.parser.DataDictionaryParser;
import java.net.URL;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.DataDictionary;
import java.io.FileNotFoundException;
import com.adventnet.persistence.xml.DataAccessPopulationHandler;
import com.adventnet.persistence.xml.ConfUrlInfo;
import java.io.File;
import com.adventnet.mfw.ConfPopulator;
import com.adventnet.persistence.internal.UniqueValueHolder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.InetAddress;
import java.net.ServerSocket;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import java.sql.SQLException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.internal.GetUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.ArrayList;
import com.adventnet.ds.query.DataSet;
import java.sql.Statement;
import java.sql.Connection;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.List;
import java.util.logging.Logger;
import com.zoho.mickey.crypto.DBPasswordProvider;
import java.util.Map;

public class PersistenceUtil
{
    private static final String CLASS_NAME;
    private static Map<String, DBPasswordProvider> dbPasswordProviderMap;
    private static final Logger OUT;
    private static String server_home;
    private static final String TABLE_NAME = "TABLE_NAME";
    private static List<String> sqlServerKeys;
    private static List<String> configurations;
    
    private PersistenceUtil() {
    }
    
    public static boolean match(final Row instance, final Row condition) throws DataAccessException {
        if (instance == null || condition == null) {
            return false;
        }
        final String insTableName = instance.getTableName();
        final String condTableName = condition.getTableName();
        if (insTableName.equals(condTableName)) {
            return matchRows(instance, instance.getKeyIndices(), condition, condition.getKeyIndices());
        }
        final ForeignKeyDefinition fkDef = getSuitableFK(insTableName, condTableName);
        final Join join = QueryConstructor.getJoin(fkDef);
        return matchRows(instance, condition, join);
    }
    
    public static boolean matchRows(final Row instance, final Row condition, final Join join) throws DataAccessException {
        if (instance == null || condition == null || join == null) {
            return false;
        }
        if (join.getCriteria() != null) {
            join.setCriteria(QueryUtil.syncForDataType(join.getCriteria()));
            final Map hmap = new HashMap();
            if (instance.getOriginalTableName().equals(join.getBaseTableName())) {
                fillMapWithColumnAndValues(join, instance, hmap, true);
            }
            else if (instance.getOriginalTableName().equals(join.getReferencedTableName())) {
                fillMapWithColumnAndValues(join, instance, hmap, false);
            }
            if (condition.getOriginalTableName().equals(join.getBaseTableName())) {
                fillMapWithColumnAndValues(join, condition, hmap, true);
            }
            else if (condition.getOriginalTableName().equals(join.getReferencedTableName())) {
                fillMapWithColumnAndValues(join, condition, hmap, false);
            }
            final boolean result = join.getCriteria().matches(hmap);
            return result;
        }
        if (join.getBaseTableColumnIndices() == null) {
            populateColumnIndicesInformation(join);
        }
        final int[] baseColumnIndices = join.getBaseTableColumnIndices();
        final int[] refColumnIndices = join.getReferencedTableColumnIndices();
        if (instance.getOriginalTableName().equals(join.getBaseTableName())) {
            return matchRows(instance, baseColumnIndices, condition, refColumnIndices);
        }
        return condition.getOriginalTableName().equals(join.getBaseTableName()) && matchRows(condition, baseColumnIndices, instance, refColumnIndices);
    }
    
    private static void fillMapWithColumnAndValues(final Join join, final Row row, final Map hMap, final boolean isBase) throws DataAccessException {
        final String tableAlias = isBase ? join.getBaseTableAlias() : join.getReferencedTableAlias();
        final List colNames = row.getColumns();
        for (int i = 0; i < colNames.size(); ++i) {
            final String columnName = colNames.get(i);
            final Column col = new Column(tableAlias, i + 1);
            col.setColumnName(columnName);
            hMap.put(col, row.get(i + 1));
        }
    }
    
    public static void populateColumnIndicesInformation(final Join join) throws DataAccessException {
        final int size = join.getNumberOfColumns();
        final int[] insColumnIndices = new int[size];
        final int[] condColumnIndices = new int[size];
        for (int i = 0; i < size; ++i) {
            insColumnIndices[i] = getColumnIndex(join.getBaseTableName(), join.getBaseTableColumn(i));
            condColumnIndices[i] = getColumnIndex(join.getReferencedTableName(), join.getReferencedTableColumn(i));
        }
        join.setBaseTableColumnIndices(insColumnIndices);
        join.setReferencedTableColumnIndices(condColumnIndices);
    }
    
    public static int getColumnIndex(final String tableName, final String columnName) throws DataAccessException {
        try {
            final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
            return tableDefinition.getColumnIndex(columnName, true);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Exception occured while fetching table definition for the table " + tableName, mde);
        }
    }
    
    private static String toString(final int[] vals) {
        if (vals == null || vals.length == 0) {
            return "";
        }
        final StringBuffer buff = new StringBuffer();
        buff.append("[");
        for (int i = 0; i < vals.length; ++i) {
            buff.append(vals[i]);
            if (i < vals.length - 1) {
                buff.append(",");
            }
        }
        buff.append("]");
        return buff.toString();
    }
    
    public static boolean matchRows(final Row row1, final int[] row1ColIndices, final Row row2, final int[] row2ColIndices) throws DataAccessException {
        boolean matches = true;
        if (row1ColIndices.length != row2ColIndices.length) {
            throw new DataAccessException("The given column lists are not of same size");
        }
        for (int size = row1ColIndices.length, i = 0; i < size; ++i) {
            matches = (matches && matchValues(row1.get(row1ColIndices[i]), row2.get(row2ColIndices[i])));
        }
        return matches;
    }
    
    public static boolean matchRows(final Row row1, final List columns1, final Row row2, final List columns2) throws DataAccessException {
        boolean matches = true;
        if (columns1.size() != columns2.size()) {
            throw new DataAccessException("The given column lists are not of same size");
        }
        for (int size = columns1.size(), i = 0; i < size; ++i) {
            matches = (matches && matchValues(row1.get(columns1.get(i)), row2.get(columns2.get(i))));
        }
        if (PersistenceUtil.OUT.isLoggable(Level.FINEST)) {
            PersistenceUtil.OUT.log(Level.FINEST, "Matching result for Row1 {0} Row2 {1} is : {2}", new Object[] { row1, row2, matches });
        }
        return matches;
    }
    
    private static boolean matchValues(final Object value1, final Object value2) {
        try {
            final boolean matches = (value1 == null) ? (value2 == null) : value1.equals(value2);
            return matches;
        }
        catch (final RuntimeException exp) {
            PersistenceUtil.OUT.log(Level.WARNING, "Error: [{0}], value1:[{1}],value2:[{2}]", new Object[] { exp.getMessage(), value1, value2 });
            throw exp;
        }
    }
    
    public static ForeignKeyDefinition getSuitableFK(final String tableName1, final String tableName2) throws DataAccessException {
        ForeignKeyDefinition fkDefn = null;
        List fks = null;
        try {
            fks = MetaDataUtil.getForeignKeys(tableName1, tableName2);
            PersistenceUtil.OUT.log(Level.FINEST, "Relationships between tables {0} and {1} are {2}", new Object[] { tableName1, tableName2, fks });
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Exception occured while fetching relationship between the tables " + tableName1 + " and " + tableName2, mde);
        }
        fkDefn = QueryConstructor.getPKLinkedFK(fks);
        if (fkDefn == null && fks != null && fks.size() != 0) {
            if (fks.size() > 1) {
                PersistenceUtil.OUT.log(Level.FINE, "More than one foreign key found between tables {0} and {1}. Rows can't be matched in such cases", new Object[] { tableName1, tableName2 });
                throw new DataAccessException("More than one foreign key found between tables " + tableName1 + " and " + tableName2 + ". Rows can't be matched in such cases");
            }
            fkDefn = fks.get(0);
        }
        return fkDefn;
    }
    
    static String getIndexTableName(final String tableName) throws DataAccessException {
        String idxTableName = null;
        final String dominantTable = getDominantTable(tableName);
        if (dominantTable == null) {
            return null;
        }
        final boolean isIndexed = PersonalityConfigurationUtil.isIndexed(dominantTable);
        if (dominantTable != null && isIndexed) {
            idxTableName = dominantTable + "_PIDX";
        }
        return idxTableName;
    }
    
    static String getDominantTable(final String tableName) throws DataAccessException {
        String dominantTable = null;
        final List personalityNames = PersonalityConfigurationUtil.getContainedPersonalities(tableName);
        if (personalityNames == null || personalityNames.size() == 0) {
            PersistenceUtil.OUT.log(Level.FINEST, "The table {0} does not participate in any personality", tableName);
            dominantTable = null;
        }
        else {
            final String personalityName = personalityNames.get(0);
            dominantTable = PersonalityConfigurationUtil.getDominantTableForPersonality(personalityName);
        }
        return dominantTable;
    }
    
    static Join getJoinWithDominantTable(final String tableName) throws DataAccessException {
        PersistenceUtil.OUT.entering(PersistenceUtil.CLASS_NAME, "getJoinWithDominantTable", tableName);
        Join retJoin = null;
        final List personalityNames = PersonalityConfigurationUtil.getContainedPersonalities(tableName);
        if (personalityNames == null || personalityNames.size() == 0) {
            PersistenceUtil.OUT.exiting(PersistenceUtil.CLASS_NAME, "getJoinWithDominantTable", null);
            return null;
        }
        final String personalityName = personalityNames.get(0);
        final List constituentTables = PersonalityConfigurationUtil.getConstituentTables(personalityName);
        retJoin = getJoinWithDominantTable(constituentTables, tableName);
        PersistenceUtil.OUT.exiting(PersistenceUtil.CLASS_NAME, "getJoinWithDominantTable", retJoin);
        return retJoin;
    }
    
    private static Join getJoinWithDominantTable(final List constituentTables, final String tableName) throws DataAccessException {
        PersistenceUtil.OUT.entering(PersistenceUtil.CLASS_NAME, "getJoinWithDominantTable", new Object[] { constituentTables, tableName });
        final int index = constituentTables.indexOf(tableName);
        if (index == 0) {
            PersistenceUtil.OUT.exiting(PersistenceUtil.CLASS_NAME, "getJoinWithDominantTable", null);
            return null;
        }
        for (int i = 0; i < index; ++i) {
            final String parentTable = constituentTables.get(i);
            List fks = null;
            try {
                fks = MetaDataUtil.getForeignKeys(parentTable, tableName);
            }
            catch (final MetaDataException mde) {
                PersistenceUtil.OUT.log(Level.FINER, "MetaDataException occured while getting foreign key definitions connecting the tables {0} and {1}", new Object[] { parentTable, tableName });
                PersistenceUtil.OUT.log(Level.FINER, "Exception thrown", mde);
                throw new DataAccessException(mde);
            }
            if (fks != null) {
                for (int j = 0; j < fks.size(); ++j) {
                    final ForeignKeyDefinition fk = fks.get(j);
                    if (QueryConstructor.isPKLinkedFK(fk)) {
                        final Join join = QueryConstructor.getJoin(fk);
                        PersistenceUtil.OUT.log(Level.FINEST, "Join found between parentTable {0} and given table {1} is {2}", new Object[] { parentTable, tableName, join });
                        Join retJoin = join;
                        final Join parentJoin = getJoinWithDominantTable(constituentTables, parentTable);
                        if (parentJoin != null) {
                            retJoin = mergeJoins(parentJoin, join);
                        }
                        PersistenceUtil.OUT.exiting(PersistenceUtil.CLASS_NAME, "getJoinWithDominantTable", retJoin);
                        return retJoin;
                    }
                }
            }
        }
        PersistenceUtil.OUT.log(Level.WARNING, "No join found between the table {0} and the tables listed prior to that in the list {1}", new Object[] { tableName, constituentTables });
        throw new DataAccessException("No join found between the table " + tableName + " and the tables listed prior to that in the list " + constituentTables);
    }
    
    private static Join mergeJoins(final Join parentJoin, final Join childJoin) throws DataAccessException {
        PersistenceUtil.OUT.entering(PersistenceUtil.CLASS_NAME, "mergeJoins", new Object[] { parentJoin, childJoin });
        final String parentTableName = parentJoin.getBaseTableName();
        final String childTableName = childJoin.getReferencedTableName();
        final String[] parentColumns = getColumns(parentJoin, true);
        final String[] intermediateColumns = getColumns(parentJoin, false);
        final String[] childColumns = new String[parentColumns.length];
        final String[] intermediateColumnsInChild = getColumns(childJoin, true);
        final String[] childColumnsInChild = getColumns(childJoin, false);
        for (int i = 0; i < parentColumns.length; ++i) {
            final String intermediateCol = intermediateColumns[i];
            final int index = indexOf(intermediateColumnsInChild, intermediateCol);
            if (index == -1) {
                PersistenceUtil.OUT.log(Level.FINER, "No matching column found in the join {0} for the column {1} in the join {2}", new Object[] { childJoin, intermediateCol, parentJoin });
                throw new DataAccessException("No matching column found in the join " + childJoin + " for the column " + intermediateCol + " in the join " + parentJoin);
            }
            childColumns[i] = childColumnsInChild[index];
        }
        final Join retJoin = new Join(parentTableName, childTableName, parentColumns, childColumns, 2);
        return retJoin;
    }
    
    static int indexOf(final String[] arr, final String value) {
        for (int i = 0; i < arr.length; ++i) {
            if (value.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }
    
    static String[] getColumns(final Join join, final boolean isBase) {
        final int count = join.getNumberOfColumns();
        final String[] retArr = new String[count];
        for (int i = 0; i < count; ++i) {
            retArr[i] = (isBase ? join.getBaseTableColumn(i) : join.getReferencedTableColumn(i));
        }
        return retArr;
    }
    
    public static void safeClose(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception exc) {
            PersistenceUtil.OUT.log(Level.SEVERE, "Exception while closing connection", exc);
        }
    }
    
    public static void safeClose(final Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (final Exception exc) {
            PersistenceUtil.OUT.log(Level.SEVERE, "Exception while closing statement", exc);
        }
    }
    
    private static void safeClose(final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
        }
        catch (final Exception exc) {
            PersistenceUtil.OUT.log(Level.FINEST, "Exception occured while closing DataSet {0}", ds);
            PersistenceUtil.OUT.log(Level.FINEST, "Exception Stack trace:", exc);
        }
    }
    
    public static List getContainingTables(final Row instance) throws DataAccessException {
        PersistenceUtil.OUT.entering(PersistenceUtil.CLASS_NAME, "getContainingTables", instance);
        final String tableName = instance.getTableName();
        final String dominantTabName = getDominantTable(tableName);
        if (!PersonalityConfigurationUtil.isIndexed(dominantTabName)) {
            return getContainingTablesForNonIndexedPersonality(dominantTabName, instance);
        }
        final List containingTables = new ArrayList();
        final String idxTableName = getIndexTableName(tableName);
        if (idxTableName == null) {
            PersistenceUtil.OUT.log(Level.FINEST, "Table {0} is not indexed", tableName);
            return containingTables;
        }
        Criteria criteria = null;
        final Join joinWithDominantTable = getJoinWithDominantTable(tableName);
        String[] dominantColumns = null;
        String[] slaveColumns = null;
        if (joinWithDominantTable != null) {
            dominantColumns = getColumns(joinWithDominantTable, true);
            slaveColumns = getColumns(joinWithDominantTable, false);
        }
        TableDefinition idxTD = null;
        try {
            idxTD = MetaDataUtil.getTableDefinitionByName(idxTableName);
            if (idxTD == null) {
                throw new DataAccessException("Table definition not found for the index table " + idxTableName);
            }
            final List columnDefns = idxTD.getColumnList();
            for (int size = columnDefns.size(), i = 0; i < size - 1; ++i) {
                final ColumnDefinition cd = columnDefns.get(i);
                final String pkColumnName = cd.getColumnName();
                String columnName = null;
                if (joinWithDominantTable == null) {
                    columnName = pkColumnName;
                }
                else {
                    final int index = indexOf(dominantColumns, pkColumnName);
                    columnName = joinWithDominantTable.getReferencedTableColumn(index);
                }
                final Column col = Column.getColumn(idxTableName, pkColumnName);
                final Object value = instance.get(columnName);
                final Criteria oneCriteria = new Criteria(col, value, 0);
                if (criteria == null) {
                    criteria = oneCriteria;
                }
                else {
                    criteria = criteria.and(oneCriteria);
                }
            }
            final SelectQuery sq = QueryConstructor.get(idxTableName, criteria);
            final DataObject dObj = GetUtil.get(sq);
            PersistenceUtil.OUT.log(Level.FINEST, "The DataObject holding the list of tables, which have the data is {0}", dObj);
            final Iterator itr = dObj.getRows(idxTableName);
            while (itr.hasNext()) {
                final Row row = itr.next();
                final String containingTableName = (String)row.get("TABLE_NAME");
                containingTables.add(containingTableName);
            }
        }
        catch (final MetaDataException mde) {
            final String mess = "Exception occured while getting the definition for the index table " + idxTableName;
            PersistenceUtil.OUT.log(Level.FINER, mess, mde);
            throw new DataAccessException(mess, mde);
        }
        PersistenceUtil.OUT.exiting(PersistenceUtil.CLASS_NAME, "getContainingTables", containingTables);
        return containingTables;
    }
    
    private static List getContainingTablesForNonIndexedPersonality(final String dominantTableName, final Row row) throws DataAccessException {
        PersistenceUtil.OUT.log(Level.FINE, "getContainingTables called for NonIndexedPersonality with dominant table {0}", new Object[] { dominantTableName });
        final List personalites = PersonalityConfigurationUtil.getContainedPersonalities(dominantTableName);
        PersistenceUtil.OUT.log(Level.FINE, "Personalities with dominant table {0} are {1}", new Object[] { dominantTableName, personalites });
        if (personalites == null || personalites.size() == 0) {
            throw new DataAccessException("No Personality exists with constitutent table as " + dominantTableName);
        }
        final List tableNames = PersonalityConfigurationUtil.getConstituentTables(personalites);
        final List sortedContainingTables = sortTables(tableNames);
        PersistenceUtil.OUT.log(Level.FINEST, "Sorted tables are {0}", sortedContainingTables);
        final boolean[] isLeftJoins = new boolean[sortedContainingTables.size()];
        Arrays.fill(isLeftJoins, true);
        final Criteria criteria = QueryConstructor.formCriteria(row);
        final SelectQuery query = QueryConstructor.get(sortedContainingTables, isLeftJoins, criteria);
        final DataObject deepRetDObj = GetUtil.get(query);
        PersistenceUtil.OUT.log(Level.FINEST, "DataObject in getContainingTablesForNonIndexedPersonality {0}", deepRetDObj);
        final List tableNamesToReturn = deepRetDObj.getTableNames();
        PersistenceUtil.OUT.log(Level.FINE, "Containing Tables for the personality {0} are {1}", new Object[] { personalites, tableNamesToReturn });
        return tableNamesToReturn;
    }
    
    public static List sortTables(final WritableDataObject writDataObject, final List involvedTables) throws DataAccessException {
        PersistenceUtil.OUT.entering(PersistenceUtil.CLASS_NAME, "sortTables", new Object[] { writDataObject, involvedTables });
        final int size = involvedTables.size();
        List toSortList = new ArrayList(size);
        final HashMap realTablesMap = new HashMap(size);
        for (int i = 0; i < size; ++i) {
            final String aliasTableName = involvedTables.get(i);
            final String origTableName = writDataObject.getOrigTableName(aliasTableName);
            PersistenceUtil.OUT.log(Level.FINER, "Alias Table {0} Orig Table {1}", new Object[] { aliasTableName, origTableName });
            if (origTableName == null) {
                throw new DataAccessException("Unknown table encounted " + aliasTableName);
            }
            toSortList.add(origTableName);
            List aliasList = realTablesMap.get(origTableName);
            if (aliasList == null) {
                aliasList = new ArrayList();
                realTablesMap.put(origTableName, aliasList);
            }
            PersistenceUtil.OUT.log(Level.FINER, "Alias {0} added to the List {1}", new Object[] { aliasTableName, aliasList });
            aliasList.add(aliasTableName);
        }
        PersistenceUtil.OUT.log(Level.FINER, "toSortList {0}", toSortList);
        toSortList = sortTables(toSortList);
        PersistenceUtil.OUT.log(Level.FINER, "Sorted List {0}", toSortList);
        final List retTableList = new ArrayList(size);
        for (int compSize = toSortList.size(), j = 0; j < compSize; ++j) {
            final String realTableName = toSortList.get(j);
            final List aliasList2 = realTablesMap.get(realTableName);
            retTableList.addAll(aliasList2);
        }
        PersistenceUtil.OUT.exiting(PersistenceUtil.CLASS_NAME, "sortTables", retTableList);
        return retTableList;
    }
    
    public static List sortTables(List tableNames) throws DataAccessException {
        PersistenceUtil.OUT.entering(PersistenceUtil.CLASS_NAME, "sortTables", tableNames);
        tableNames = new ArrayList(tableNames);
        final List<String> nonTemplateInstanceTableNames = new ArrayList<String>();
        final List sortedTableNames = new ArrayList();
        final Map<String, List<String>> templateNameVsInstanceName = new HashMap<String, List<String>>();
        for (final Object o : tableNames) {
            final String tableName = (String)o;
            try {
                final String templateName = MetaDataUtil.getTableDefinitionByName(tableName).getTableName();
                if (!templateName.equals(tableName)) {
                    if (templateNameVsInstanceName.containsKey(templateName)) {
                        final List<String> instances = templateNameVsInstanceName.get(templateName);
                        instances.add(tableName);
                    }
                    else {
                        final List<String> instances = new ArrayList<String>();
                        instances.add(tableName);
                        templateNameVsInstanceName.put(templateName, instances);
                    }
                }
                else {
                    nonTemplateInstanceTableNames.add(tableName);
                }
            }
            catch (final MetaDataException mde) {
                throw new DataAccessException(mde);
            }
        }
        String tableName2 = null;
        while (nonTemplateInstanceTableNames.size() != 0) {
            tableName2 = nonTemplateInstanceTableNames.remove(0);
            sortTable(tableName2, nonTemplateInstanceTableNames, sortedTableNames);
        }
        final List<String> templateNames = new ArrayList<String>();
        templateNames.addAll(templateNameVsInstanceName.keySet());
        final List<String> sortedTemplateTableNames = new ArrayList<String>();
        while (templateNames.size() != 0) {
            tableName2 = templateNames.remove(0);
            sortTable(tableName2, templateNames, sortedTemplateTableNames);
        }
        for (final String templateName2 : sortedTemplateTableNames) {
            sortedTableNames.addAll(templateNameVsInstanceName.get(templateName2));
        }
        PersistenceUtil.OUT.exiting(PersistenceUtil.CLASS_NAME, "sortTables", sortedTableNames);
        return sortedTableNames;
    }
    
    public static List<String> getTableNamesInTableOrder() throws DataAccessException, SQLException {
        DataSet ds = null;
        Connection con = null;
        final List<String> list = new ArrayList<String>();
        final SelectQuery selQuery = new SelectQueryImpl(new Table("TableDetails"));
        selQuery.addSelectColumn(new Column("TableDetails", "TABLE_NAME"));
        final SortColumn sortCol = new SortColumn(new Column("TableDetails", "TABLE_ORDER"), true);
        selQuery.addSortColumn(sortCol);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        try {
            con = relapi.getConnection();
            con.setAutoCommit(false);
            ds = relapi.executeQuery(selQuery, con, 0);
            while (ds.next()) {
                list.add(ds.getAsString("TABLE_NAME"));
            }
        }
        catch (final Exception e) {
            throw new DataAccessException("Exception occured while executing Query. " + e.getMessage());
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
        return list;
    }
    
    private static void sortTable(final String tableName, final List tableNames, final List sortedTableNames) throws DataAccessException {
        PersistenceUtil.OUT.entering(PersistenceUtil.CLASS_NAME, "sortTable", new Object[] { tableName, tableNames, sortedTableNames });
        try {
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            if (td == null) {
                throw new DataAccessException("Unknown table " + tableName + " specified for insertion");
            }
            final List fkDefns = td.getForeignKeyList();
            if (fkDefns != null) {
                for (int size = fkDefns.size(), i = 0; i < size; ++i) {
                    final ForeignKeyDefinition fkDefn = fkDefns.get(i);
                    final String masterTable = fkDefn.getMasterTableName();
                    if (tableNames.remove(masterTable)) {
                        sortTable(masterTable, tableNames, sortedTableNames);
                    }
                }
            }
            sortedTableNames.add(tableName);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Exception occured while identifying the order of tables for insertion", mde);
        }
        PersistenceUtil.OUT.exiting(PersistenceUtil.CLASS_NAME, "sortTable", sortedTableNames);
    }
    
    public static List diff(final DataObject dObjX, final DataObject dObjY) throws DataAccessException {
        PersistenceUtil.OUT.entering(PersistenceUtil.CLASS_NAME, "diff", new Object[] { dObjX, dObjY });
        final List actionInfos = new ArrayList();
        final List tableNamesX = new ArrayList(dObjX.getTableNames());
        final List tableNamesY = new ArrayList(dObjY.getTableNames());
        final List commonTables = new ArrayList();
        final List deletedTables = new ArrayList();
        for (int tableNamesLenX = tableNamesX.size(), m = 0; m < tableNamesLenX; ++m) {
            final String tableName = tableNamesX.get(m);
            if (tableNamesY.remove(tableName)) {
                commonTables.add(tableName);
                final Iterator i1 = dObjX.getRows(tableName);
                final List rowList1 = getRowsAsList(i1);
                final Iterator i2 = dObjY.getRows(tableName);
                final List rowList2 = getRowsAsList(i2);
                final int len1 = rowList1.size();
                TableDefinition td;
                try {
                    td = MetaDataUtil.getTableDefinitionByName(tableName);
                }
                catch (final MetaDataException mde) {
                    throw new DataAccessException(mde);
                }
                final List<String> colNames = td.getColumnNames();
                final PrimaryKeyDefinition pkDef = td.getPrimaryKey();
                final List pkCols = pkDef.getColumnList();
                final int pkColsLen = pkCols.size();
                final int totolCols = colNames.size();
                final int[] colIndices = new int[pkColsLen];
                for (int j = 0; j < pkColsLen; ++j) {
                    colIndices[j] = colNames.indexOf(pkCols.get(j)) + 1;
                }
                final Map pkListVsRowsX = new HashMap();
                final List unchangedRows = new ArrayList();
                for (int k = 0; k < len1; ++k) {
                    final Row row = rowList1.get(k);
                    if (rowList2.contains(row)) {
                        unchangedRows.add(row);
                    }
                    else {
                        final List pkList = new ArrayList();
                        for (int l = 0; l < pkColsLen; ++l) {
                            pkList.add(row.get(colIndices[l]));
                        }
                        pkListVsRowsX.put(pkList, row);
                    }
                }
                rowList1.removeAll(unchangedRows);
                rowList2.removeAll(unchangedRows);
                for (int rowList2Len = rowList2.size(), i3 = 0; i3 < rowList2Len; ++i3) {
                    final Row rowY = rowList2.get(i3);
                    final List pkList2 = new ArrayList();
                    for (int j2 = 0; j2 < pkColsLen; ++j2) {
                        pkList2.add(rowY.get(colIndices[j2]));
                    }
                    if (pkListVsRowsX.containsKey(pkList2)) {
                        final Row rowXToY = (Row)pkListVsRowsX.get(pkList2).clone();
                        rowList1.remove(rowXToY);
                        for (int j3 = totolCols; j3 > 0; --j3) {
                            rowXToY.set(j3, rowY.get(j3));
                        }
                        final ActionInfo actionInfo = new ActionInfo(2, rowXToY);
                        actionInfos.add(actionInfo);
                    }
                    else {
                        final ActionInfo actionInfo2 = new ActionInfo(1, rowY);
                        actionInfos.add(actionInfo2);
                    }
                }
                for (int rowList1Len = rowList1.size(), i4 = 0; i4 < rowList1Len; ++i4) {
                    final Row rowX = rowList1.get(i4);
                    final ActionInfo actionInfo2 = new ActionInfo(3, rowX);
                    actionInfos.add(actionInfo2);
                }
            }
            else {
                deletedTables.add(tableName);
            }
        }
        addActionInfos(actionInfos, dObjX, deletedTables, 3);
        addActionInfos(actionInfos, dObjY, tableNamesY, 1);
        PersistenceUtil.OUT.exiting(PersistenceUtil.CLASS_NAME, "diff", actionInfos);
        return actionInfos;
    }
    
    private static void addActionInfos(final List actionInfos, final DataObject dObj, final List tableNames, final int actionType) throws DataAccessException {
        for (int tableNamesLen = tableNames.size(), i = 0; i < tableNamesLen; ++i) {
            final String tableName = tableNames.get(i);
            final Iterator iter = dObj.getRows(tableName);
            while (iter.hasNext()) {
                final Row rowForActionInfo = iter.next();
                final ActionInfo actionInfo = new ActionInfo(actionType, rowForActionInfo);
                actionInfos.add(actionInfo);
            }
        }
    }
    
    private static List getRowsAsList(final Iterator iter) throws DataAccessException {
        final List rowList = new ArrayList();
        while (iter.hasNext()) {
            final Row rowInIter = iter.next();
            final Row rowToAdd = (Row)rowInIter.clone();
            rowList.add(rowToAdd);
        }
        return rowList;
    }
    
    public static String convertToString(final int[] o) {
        final StringBuffer sb = new StringBuffer(100);
        final int size = o.length;
        sb.append("[");
        for (int i = 0; i < size; ++i) {
            sb.append(o[i] + ((i < size - 1) ? "," : ""));
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static String convertToString(final String[] o) {
        final StringBuffer sb = new StringBuffer(100);
        final int size = o.length;
        sb.append("[");
        for (int i = 0; i < size; ++i) {
            sb.append(o[i] + ((i < size - 1) ? "," : ""));
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static String convertToString(final Object[] o) {
        final StringBuffer sb = new StringBuffer(100);
        final int size = o.length;
        sb.append("[");
        for (int i = 0; i < size; ++i) {
            sb.append(o[i] + ((i < size - 1) ? "," : ""));
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static void addChildRowsIntoDO(final DataObject dataObject, final Iterator iterator) throws DataAccessException {
        if (iterator != null) {
            while (iterator.hasNext()) {
                final Row childRow = iterator.next();
                addChildRowIntoDO(dataObject, childRow);
            }
        }
    }
    
    public static void addChildRowIntoDO(final DataObject dataObject, final Row childRow) throws DataAccessException {
        try {
            final String actualTableName = childRow.getOriginalTableName();
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(actualTableName);
            final List fks = td.getForeignKeyList();
            if (fks != null) {
                for (final ForeignKeyDefinition fkd : fks) {
                    final String masterTbName = fkd.getMasterTableName();
                    final Row masterRow = dataObject.getRow(masterTbName);
                    if (masterRow != null) {
                        final List refColNames = fkd.getFkRefColumns();
                        final List curColNames = fkd.getFkColumns();
                        for (int i = 0; i < curColNames.size(); ++i) {
                            final Object parentValue = masterRow.get(refColNames.get(i));
                            childRow.set(curColNames.get(i), parentValue);
                        }
                    }
                }
            }
            dataObject.addRow(childRow);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Exception occured while processing the ForeignKeys.");
        }
    }
    
    public static DataObject getDOForPersonality(final String personalityName) throws DataAccessException {
        final DataObject dataObject = new WritableDataObject();
        final List unSortedtableNames = PersonalityConfigurationUtil.getConstituentTables(personalityName);
        final List tableNames = sortTables(unSortedtableNames);
        for (final String tableName : tableNames) {
            final Row r = new Row(tableName);
            addChildRowIntoDO(dataObject, r);
        }
        return dataObject;
    }
    
    public static boolean matches(final DataObject dataObject, final Criteria criteria) throws Exception {
        return matches(dataObject, criteria, false, false);
    }
    
    public static boolean matches(final DataObject dataObject, final Criteria criteria, final boolean considerTablesNotPresentInDO) throws Exception {
        return matches(dataObject, criteria, considerTablesNotPresentInDO, false);
    }
    
    public static boolean matches(final DataObject dataObject, final Criteria criteria, final boolean considerTablesNotPresentInDO, final boolean treatNullAsValue) throws Exception {
        boolean found = false;
        final Criteria lc = criteria.getLeftCriteria();
        final Criteria rc = criteria.getRightCriteria();
        if (lc == null && rc == null) {
            final boolean initialValue = criteria.isNullTreatedAsValue();
            try {
                criteria.treatNullAsValue(treatNullAsValue);
                final String tableName = criteria.getColumn().getTableAlias();
                if (dataObject.containsTable(tableName)) {
                    found = (dataObject.getRow(tableName, criteria) != null);
                }
                else if (considerTablesNotPresentInDO && criteria.getValue() == null) {
                    final int comparator = criteria.getComparator();
                    final boolean eql_comparator = comparator == 0 || comparator == 2;
                    final boolean neq_comparator = comparator == 1 || comparator == 3;
                    if (eql_comparator || neq_comparator) {
                        found = ((eql_comparator && !criteria.isNegate()) || (neq_comparator && criteria.isNegate()));
                    }
                    else {
                        PersistenceUtil.OUT.log(Level.WARNING, "PersistenceUtil.matches() is returning false since the comparator in the Criteria can be anyone of the following (EQUAL, NOT_EQUAL, LIKE, NOT_LIKE) when the criteria-value is NULL but the given criteria is {0}", criteria);
                    }
                }
            }
            finally {
                criteria.treatNullAsValue(initialValue);
            }
        }
        else {
            final String operator = criteria.getOperator();
            if (operator.equals(" AND ")) {
                found = (matches(dataObject, lc, considerTablesNotPresentInDO, treatNullAsValue) && matches(dataObject, rc, considerTablesNotPresentInDO, treatNullAsValue));
            }
            else {
                found = (matches(dataObject, lc, considerTablesNotPresentInDO, treatNullAsValue) || matches(dataObject, rc, considerTablesNotPresentInDO, treatNullAsValue));
            }
            found = (criteria.isNegate() ? (!found) : found);
        }
        return found;
    }
    
    @Deprecated
    public static boolean isPortFree(final int port) {
        return isPortFree(port, null);
    }
    
    public static boolean isPortFree(final int port, final String hostName) {
        if (port <= 0) {
            return false;
        }
        ServerSocket sock = null;
        try {
            sock = ((hostName == null) ? new ServerSocket(port) : new ServerSocket(port, 0, InetAddress.getByName(hostName)));
        }
        catch (final Exception ex) {
            return false;
        }
        finally {
            if (sock != null) {
                try {
                    sock.close();
                }
                catch (final Exception e) {
                    PersistenceUtil.OUT.log(Level.SEVERE, "Exception while closing socket", e);
                }
            }
        }
        return true;
    }
    
    public static int getPort(final String urlstr) {
        return getPort(urlstr, Pattern.compile("(.*url=.*)(jdbc.*):([0-9]*)(.*)"));
    }
    
    public static int getPort(final String urlstr, final Pattern pat) {
        int toReturn = 0;
        final String patstr = "url=" + urlstr;
        final Matcher mat = pat.matcher(patstr);
        if (mat.matches()) {
            try {
                toReturn = Integer.parseInt(mat.group(3));
            }
            catch (final Exception e) {
                PersistenceUtil.OUT.log(Level.INFO, "Exception occurred while getting port number from jdbc url - {0}", e);
            }
        }
        return toReturn;
    }
    
    public static DataObject constructDO(final DataObject templateDO) throws Exception {
        if (templateDO == null) {
            return null;
        }
        final DataObject newDataObject = new WritableDataObject();
        Row newRow = null;
        final List subDOs = ((WritableDataObject)templateDO).getDataObjects();
        final Iterator subDOIterator = subDOs.iterator();
        while (subDOIterator.hasNext()) {
            final HashMap keyVsValue = new HashMap();
            final DataObject subDO = subDOIterator.next();
            final DataObject newSubDO = new WritableDataObject();
            List tableNames = subDO.getTableNames();
            tableNames = sortTables(tableNames);
            final Iterator iterator = tableNames.iterator();
            Row row = null;
            while (iterator.hasNext()) {
                final Iterator rowIterator = subDO.getRows(iterator.next());
                while (rowIterator.hasNext()) {
                    row = rowIterator.next();
                    newRow = new Row(row.getTableName());
                    for (int i = 1; i <= row.getColumns().size(); ++i) {
                        if (!(newRow.get(i) instanceof UniqueValueHolder)) {
                            newRow.set(i, row.get(i));
                        }
                        else {
                            final String key = newRow.getOriginalTableName() + ":" + newRow.getColumns().get(i - 1) + ":" + row.get(i);
                            final Object value = newRow.get(i);
                            keyVsValue.put(key, value);
                        }
                    }
                    addChildRowIntoDO(newSubDO, newRow, keyVsValue);
                    for (int i = 1; i <= row.getColumns().size(); ++i) {
                        if (newRow.get(i) instanceof UniqueValueHolder) {
                            final String key = newRow.getOriginalTableName() + ":" + newRow.getColumns().get(i - 1) + ":" + row.get(i);
                            if (!keyVsValue.containsKey(key)) {
                                final Object value = newRow.get(i);
                                keyVsValue.put(key, value);
                            }
                        }
                    }
                }
            }
            newDataObject.append(newSubDO);
        }
        return newDataObject;
    }
    
    private static void addChildRowIntoDO(final DataObject dataObject, final Row childRow, final HashMap keyVsValue) throws DataAccessException {
        try {
            final String actualTableName = childRow.getOriginalTableName();
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(actualTableName);
            final List fks = td.getForeignKeyList();
            if (fks != null) {
                for (final ForeignKeyDefinition fkd : fks) {
                    final String masterTbName = fkd.getMasterTableName();
                    final List refColNames = fkd.getFkRefColumns();
                    final List curColNames = fkd.getFkColumns();
                    for (int i = 0; i < curColNames.size(); ++i) {
                        final String key = masterTbName + ":" + refColNames.get(i) + ":" + childRow.get(curColNames.get(i));
                        final Object value = keyVsValue.get(key);
                        if (value != null) {
                            childRow.set(curColNames.get(i), value);
                        }
                    }
                }
            }
            dataObject.addRow(childRow);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Exception occured while processing the ForeignKeys.");
        }
    }
    
    public static void populate(final String moduleName, final String fileName) throws Exception {
        final Long moduleId = ConfPopulator.getModuleId(moduleName);
        final ConfUrlInfo info = new ConfUrlInfo(moduleName, moduleId, new File(PersistenceUtil.server_home).toURL());
        final Row confFileRow = new Row("ConfFile");
        confFileRow.set("URL", fileName);
        final DataObject confFileDO = new WritableDataObject();
        confFileDO.addRow(confFileRow);
        new DataAccessPopulationHandler().populate(info, confFileDO);
    }
    
    public static TableDefinition changeTableName(final String ddFileName, final String tableNameToBeChanged, final String newTableName) throws Exception {
        if (tableNameToBeChanged == null) {
            throw new IllegalArgumentException("TableName to be Changed Cannot be Null");
        }
        if (newTableName == null) {
            throw new IllegalArgumentException("New TableName Cannot be Null");
        }
        if (newTableName.length() > 26) {
            throw new IllegalArgumentException("New TableName Cannot be greator Than 26");
        }
        final File file = new File(ddFileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File [" + file.getCanonicalPath() + "] Not Found");
        }
        final DataDictionary dd = getDataDictionary(file.toURL());
        final TableDefinition tabDef = dd.getTableDefinitionByName(tableNameToBeChanged);
        return changeTableName(tabDef, newTableName);
    }
    
    public static TableDefinition changeTableName(final TableDefinition tabDef, final String newTableName) throws Exception {
        if (newTableName == null) {
            throw new IllegalArgumentException("New TableName Cannot be Null");
        }
        if (newTableName.length() > 26) {
            throw new IllegalArgumentException("New TableName Cannot be greator Than 26");
        }
        final TableDefinition newTabDef = new TableDefinition();
        newTabDef.setTableName(newTableName);
        newTabDef.setDescription(tabDef.getDescription());
        newTabDef.setDisplayName(tabDef.getDisplayName());
        final List columnList = tabDef.getColumnList();
        for (final ColumnDefinition columnDef : columnList) {
            final UniqueValueGeneration uvg = columnDef.getUniqueValueGeneration();
            if (uvg != null) {
                String uvgName = uvg.getGeneratorName();
                final int index = (uvgName.lastIndexOf(46) == -1) ? uvgName.lastIndexOf(95) : uvgName.lastIndexOf(46);
                if (index == -1) {
                    throw new IllegalArgumentException("Given TableDefintion's UniqueValueGeneration generateName [" + uvgName + "] is InValid for column [" + columnDef.getColumnName() + "]");
                }
                uvgName = newTableName + uvgName.substring(index);
                uvg.setGeneratorName(uvgName);
                columnDef.setUniqueValueGeneration(uvg);
            }
            newTabDef.addColumnDefinition(columnDef);
        }
        final PrimaryKeyDefinition pkDef = tabDef.getPrimaryKey();
        String pkCon = pkDef.getName();
        pkCon = newTableName + pkCon.substring(pkCon.lastIndexOf(95));
        pkDef.setName(pkCon);
        newTabDef.setPrimaryKey(pkDef);
        final List fkList = tabDef.getForeignKeyList();
        if (fkList != null) {
            for (final ForeignKeyDefinition fkDef : fkList) {
                String fkCon = fkDef.getName();
                if (fkCon.lastIndexOf(95) == -1) {
                    throw new IllegalArgumentException("Cannot able to find TableName from FKConstarintName [" + fkCon + "]");
                }
                fkCon = newTableName + fkCon.substring(fkCon.lastIndexOf(95));
                fkDef.setName(fkCon);
                newTabDef.addForeignKey(fkDef);
            }
        }
        final List ukList = tabDef.getUniqueKeys();
        if (ukList != null) {
            for (final UniqueKeyDefinition ukDef : ukList) {
                String ukCon = ukDef.getName();
                if (ukCon.lastIndexOf(95) == -1) {
                    throw new IllegalArgumentException("Cannot able to find TableName from UKConstarintName [" + ukCon + "]");
                }
                ukCon = newTableName + ukCon.substring(ukCon.lastIndexOf(95));
                ukDef.setName(ukCon);
                newTabDef.addUniqueKey(ukDef);
            }
        }
        final List idxList = tabDef.getIndexes();
        if (idxList != null) {
            for (final IndexDefinition idxDef : idxList) {
                String idxCon = idxDef.getName();
                if (idxCon.lastIndexOf(95) == -1) {
                    throw new IllegalArgumentException("Cannot able to find TableName from INDEX ConstarintName [" + idxCon + "]");
                }
                idxCon = newTableName + idxCon.substring(idxCon.lastIndexOf(95));
                idxDef.setName(idxCon);
                newTabDef.addIndex(idxDef);
            }
        }
        return newTabDef;
    }
    
    public static DataDictionary getDataDictionary(final URL ddURL) throws Exception {
        return DataDictionaryParser.getDataDictionary(ddURL);
    }
    
    public static String generateRandomPassword() {
        final String matrix = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final int count = 10;
        String passwd = "";
        for (int i = 0; i < count; ++i) {
            final SecureRandom generator = new SecureRandom();
            final long range = 62L;
            final int rnd = (int)(range * generator.nextDouble());
            passwd += matrix.substring(rnd, rnd + 1);
        }
        return passwd;
    }
    
    public static boolean removeKeyInDBConf(final String key) throws IOException {
        final String fileNameWithAbsolutePath = PersistenceInitializer.getDBParamsFilePath();
        final File dbparamsPath = new File(fileNameWithAbsolutePath);
        if (!dbparamsPath.exists()) {
            throw new FileNotFoundException("Specified File Not Found :: [" + fileNameWithAbsolutePath + "]");
        }
        final StringBuffer buffer = new StringBuffer();
        try (final BufferedReader br = new BufferedReader(new FileReader(dbparamsPath))) {
            String str;
            while ((str = br.readLine()) != null) {
                str = str.trim();
                if (!str.matches("(#*)(" + key + ")(=| ).*")) {
                    buffer.append(str + "\n");
                }
            }
        }
        catch (final IOException ex) {
            ex.printStackTrace();
            return false;
        }
        try (final BufferedWriter bw = new BufferedWriter(new FileWriter(dbparamsPath))) {
            bw.write(buffer.toString());
        }
        return true;
    }
    
    public static boolean updateUserNameInDBConf(final String userName) throws IOException {
        final String fileNameWithAbsolutePath = PersistenceInitializer.getDBParamsFilePath();
        final File dbparamsPath = new File(fileNameWithAbsolutePath);
        if (!dbparamsPath.exists()) {
            throw new FileNotFoundException("Specified File Not Found :: [" + fileNameWithAbsolutePath + "]");
        }
        final StringBuffer buffer = new StringBuffer();
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(dbparamsPath));
            String str = br.readLine();
            boolean addedNewUserName = false;
            final Pattern pat = Pattern.compile("(#*)(username)(=| ).*");
            while (str != null) {
                str = str.trim();
                if (str.matches("(#*)(username)(=| ).*")) {
                    if (!addedNewUserName) {
                        buffer.append("username=" + userName + "\n");
                        addedNewUserName = true;
                    }
                }
                else {
                    buffer.append(str + "\n");
                }
                str = br.readLine();
            }
            if (!addedNewUserName) {
                buffer.append("username=" + userName + "\n");
                addedNewUserName = true;
            }
            bw = new BufferedWriter(new FileWriter(dbparamsPath));
            bw.write(buffer.toString());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
        }
        return true;
    }
    
    public static boolean updatePasswordInDBConf(final String password) throws IOException {
        final String fileNameWithAbsolutePath = PersistenceInitializer.getDBParamsFilePath();
        final File dbparamsPath = new File(fileNameWithAbsolutePath);
        if (!dbparamsPath.exists()) {
            throw new FileNotFoundException("Specified File Not Found :: [" + fileNameWithAbsolutePath + "]");
        }
        final StringBuffer buffer = new StringBuffer();
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(dbparamsPath));
            String str = br.readLine();
            boolean addedNewPassword = false;
            final Pattern pat = Pattern.compile("(#*)(password)(=| ).*");
            while (str != null) {
                str = str.trim();
                if (str.matches("(#*)(password)(=| ).*")) {
                    if (!addedNewPassword) {
                        buffer.append("password=" + password + "\n");
                        addedNewPassword = true;
                    }
                }
                else {
                    buffer.append(str + "\n");
                }
                str = br.readLine();
            }
            if (!addedNewPassword) {
                buffer.append("password=" + password + "\n");
                addedNewPassword = true;
            }
            bw = new BufferedWriter(new FileWriter(dbparamsPath));
            bw.write(buffer.toString());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
        }
        return true;
    }
    
    public static boolean addKeyInDBConf(final String key, final String value) throws IOException {
        final String fileNameWithAbsolutePath = PersistenceInitializer.getDBParamsFilePath();
        final File dbparamsPath = new File(fileNameWithAbsolutePath);
        if (!dbparamsPath.exists()) {
            throw new FileNotFoundException("Specified File Not Found :: [" + fileNameWithAbsolutePath + "]");
        }
        final StringBuffer buffer = new StringBuffer();
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(dbparamsPath));
            for (String str = br.readLine(); str != null; str = br.readLine()) {
                buffer.append(str + "\n");
            }
            buffer.append(key + "=" + value + "\n");
            bw = new BufferedWriter(new FileWriter(dbparamsPath));
            bw.write(buffer.toString());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
        }
        return true;
    }
    
    @Deprecated
    public static boolean updateDBConf(final String password) throws IOException {
        updatePasswordInDBConf(password);
        return true;
    }
    
    public static String generateRandomValue(int length) {
        final String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ-_1234567890abcdefghijklmnopqrstuvwxyz";
        length = ((length < 10) ? 10 : length);
        final SecureRandom secureRnd = new SecureRandom();
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            sb.append(source.charAt(secureRnd.nextInt(source.length())));
        }
        return sb.toString();
    }
    
    public static AlterTableQuery getAlterTableQuery(final String ddFileName, final String tableName, final String columnName) throws Exception {
        if (tableName == null) {
            throw new IllegalArgumentException("Table Name Cannot be Null");
        }
        if (columnName == null) {
            throw new IllegalArgumentException("Column Name Cannot be Null");
        }
        final File file = new File(ddFileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File [" + file.getCanonicalPath() + "] Not Found");
        }
        final DataDictionary dd = getDataDictionary(file.toURL());
        final TableDefinition tabDef = dd.getTableDefinitionByName(tableName);
        if (tabDef == null) {
            throw new IllegalArgumentException("Table [" + tableName + "] not found in given dataDictionary.");
        }
        final ColumnDefinition colDef = tabDef.getColumnDefinitionByName(columnName);
        if (colDef == null) {
            throw new IllegalArgumentException("Column [" + tableName + "] not found in given Table [" + tableName + "].");
        }
        final TableDefinition oldTabDef = MetaDataUtil.getTableDefinitionByName(tableName);
        if (oldTabDef == null) {
            throw new IllegalArgumentException("Table [" + tableName + "] not exists in DB (MetaDataCache).");
        }
        final ColumnDefinition oldColDef = oldTabDef.getColumnDefinitionByName(columnName);
        AlterTableQuery atq = null;
        if (oldColDef == null && colDef != null) {
            atq = new AlterTableQueryImpl(tableName, 1);
        }
        else if (oldColDef != null && colDef != null) {
            atq = new AlterTableQueryImpl(tableName, 2);
        }
        if (atq == null) {
            return atq;
        }
        if (colDef.getAllowedValues() != null) {
            atq.setAllowedValues(colDef.getAllowedValues());
        }
        atq.setColumnName(columnName);
        atq.setDataType(colDef.getDataType());
        if (colDef.getDefaultValue() != null) {
            atq.setDefaultValue(colDef.getDefaultValue());
        }
        if (colDef.getDescription() != null) {
            atq.setDescription(colDef.getDescription());
        }
        if (colDef.getDisplayName() != null) {
            atq.setDisplayName(colDef.getDisplayName());
        }
        if (colDef.getMaxLength() > 0) {
            atq.setMaxLength(colDef.getMaxLength());
        }
        atq.setNullable(colDef.isNullable());
        if (colDef.getUniqueValueGeneration() != null) {
            atq.setUniqueValueGeneration(colDef.getUniqueValueGeneration());
        }
        return atq;
    }
    
    public static void initLog(final String logFileNameWithAbsolutePath) throws FileNotFoundException {
        final File file = new File(logFileNameWithAbsolutePath);
        final String logDir = (logFileNameWithAbsolutePath.lastIndexOf(47) > 0) ? logFileNameWithAbsolutePath.substring(0, logFileNameWithAbsolutePath.lastIndexOf(47)) : null;
        if (logDir != null) {
            final File dir = new File(logDir);
            dir.mkdirs();
        }
        final PrintStream logOut = new PrintStream(new FileOutputStream(file, true), true);
        System.setOut(logOut);
        System.setErr(logOut);
        PersistenceUtil.OUT.fine("\n\n");
        PersistenceUtil.OUT.fine("===========================================================================");
    }
    
    private static List<String> getEncryptedColumnNames(final List<String> tableNames, final DataObject data) throws DataAccessException {
        final List<String> encrpytColList = new ArrayList<String>();
        for (int size = tableNames.size(), i = 0; i < size; ++i) {
            final String tableName = tableNames.get(i);
            try {
                TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                if (td == null && data != null) {
                    td = MetaDataUtil.getTableDefinitionByName(((WritableDataObject)data).getOrigTableName(tableName));
                }
                if (td == null) {
                    PersistenceUtil.OUT.log(Level.SEVERE, "getEncryptedColumnNames :: Unknown tableName found :: [{0}] in tableNames :: [{1}] and dataObject :: [{2}]", new Object[] { tableName, tableNames, data });
                    throw new DataAccessException("getEncryptedColumnNames :: Unknown tableName found :: " + tableName);
                }
                final List<String> encryptCols = td.getEncryptedColumnNames();
                if (encryptCols != null && encryptCols.size() > 0) {
                    encrpytColList.addAll(encryptCols);
                }
            }
            catch (final MetaDataException mde) {
                PersistenceUtil.OUT.log(Level.SEVERE, "Exception during finding the defintion for table {0} {1}", new Object[] { tableName, mde.getMessage() });
                throw new DataAccessException(mde);
            }
        }
        return encrpytColList;
    }
    
    public static void handlePreExec(final List sortedTableList, final Connection conn, final DataObject data) throws DataAccessException {
        try {
            final List<String> encryptColList = getEncryptedColumnNames(sortedTableList, data);
            RelationalAPI.getInstance().getDBAdapter().handlePreExecute(conn, sortedTableList, encryptColList);
        }
        catch (final Exception exc) {
            PersistenceUtil.OUT.log(Level.SEVERE, "Exception during preexecution", exc);
            throw new DataAccessException(exc);
        }
    }
    
    public static void handlePostExec(final List sortedTableList, final Connection conn, final DataObject data) throws DataAccessException {
        try {
            final List<String> encryptColList = getEncryptedColumnNames(sortedTableList, data);
            RelationalAPI.getInstance().getDBAdapter().handlePostExecute(conn, sortedTableList, encryptColList);
        }
        catch (final Exception exc) {
            PersistenceUtil.OUT.log(Level.SEVERE, "Exception during postexecution", exc);
            throw new DataAccessException(exc);
        }
    }
    
    public static String escapeSpecialCharacters(final String valueStr) {
        return RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().escapeSpecialCharacters(valueStr, 12);
    }
    
    public static JSONObject serializeAsJson(final DataObject dobj) throws JSONException, DataAccessException {
        final JSONObject mainjson = new JSONObject();
        final Map rowIdVsOpr = new HashMap();
        final List<ActionInfo> actions = dobj.getOperations();
        for (final ActionInfo acinfo : actions) {
            final Row.RowIdentifier rowid = acinfo.getValue().getPKValues();
            rowIdVsOpr.put(rowid, acinfo.getOperation());
        }
        final JSONObject dataJsonObj = new JSONObject();
        final JSONObject metadataJsonObj = new JSONObject();
        final List<String> tablelist = dobj.getTableNames();
        for (final String tableAlias : tablelist) {
            try {
                final String tableName = ((WritableDataObject)dobj).getOrigTableName(tableAlias);
                final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
                if (tabDef == null) {
                    throw new DataAccessException("TableDefinition for table " + tableName + " is not found in metadata on serializing DO");
                }
                if (metadataJsonObj.isNull(tableName)) {
                    metadataJsonObj.put(tableName, (Object)getMetaDataJSON(tabDef));
                }
                if (dataJsonObj.isNull(tableName)) {
                    dataJsonObj.put(tableName, (Object)new JSONArray());
                }
                final JSONArray tableJA = dataJsonObj.getJSONArray(tableName);
                final Iterator<Row> rows = dobj.getRows(tableAlias);
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final JSONArray rowJA = getRowJA(row, tabDef);
                    if (rowIdVsOpr.containsKey(row.getPKValues())) {
                        rowJA.put(1, rowIdVsOpr.get(row.getPKValues()));
                    }
                    else {
                        rowJA.put(1, 0);
                    }
                    tableJA.put((Object)rowJA);
                }
                dataJsonObj.put(tableName, (Object)tableJA);
            }
            catch (final MetaDataException mde) {
                PersistenceUtil.OUT.log(Level.INFO, "metadata exception on serializing DO: {0} ", mde);
            }
        }
        final Map operTypeVsOperTables = ((WritableDataObject)dobj).getOperationTables();
        final WritableDataObject.OperationTables tAction = operTypeVsOperTables.get("delete");
        if (tAction != null) {
            final Map tableVsDelActionInfo = tAction.getActions();
            final Iterator it = tableVsDelActionInfo.keySet().iterator();
            while (it.hasNext()) {
                try {
                    final String tableAlias2 = it.next();
                    final String tableName2 = ((WritableDataObject)dobj).getOrigTableName(tableAlias2);
                    final TableDefinition tabDef2 = MetaDataUtil.getTableDefinitionByName(tableName2);
                    if (tabDef2 != null) {
                        final List<String> columnnames = tabDef2.getColumnNames();
                        if (metadataJsonObj.isNull(tableName2)) {
                            metadataJsonObj.put(tableName2, (Object)getMetaDataJSON(tabDef2));
                        }
                        if (dataJsonObj.isNull(tableName2)) {
                            dataJsonObj.put(tableName2, (Object)new JSONArray());
                        }
                        final JSONArray tableJA2 = dataJsonObj.getJSONArray(tableName2);
                        final List<ActionInfo> delactions = tableVsDelActionInfo.get(tableName2);
                        for (final ActionInfo delinfo : delactions) {
                            final Row row2 = delinfo.getValue();
                            final JSONArray rowJA2 = getRowJA(row2, tabDef2);
                            rowJA2.put(1, delinfo.getOperation());
                            tableJA2.put((Object)rowJA2);
                        }
                        dataJsonObj.put(tableName2, (Object)tableJA2);
                        continue;
                    }
                    throw new DataAccessException("TableDefinition for table " + tableName2 + " is not found in metadata on serializing DO");
                }
                catch (final MetaDataException mde2) {
                    PersistenceUtil.OUT.log(Level.INFO, "metadata exception on serializing DO: {0} ", mde2);
                    throw new DataAccessException(mde2.getMessage());
                }
                break;
            }
        }
        mainjson.put("metadata", (Object)metadataJsonObj);
        mainjson.put("data", (Object)dataJsonObj);
        PersistenceUtil.OUT.log(Level.FINEST, "Serilized JSONObject {0} for DataObject {1}  ", new Object[] { mainjson, dobj });
        return mainjson;
    }
    
    public static JSONObject serializeAsJsonWithJoin(final DataObject dobj) throws JSONException, DataAccessException {
        final JSONObject jsonObject = serializeAsJson(dobj);
        final List<Join> joins = ((WritableDataObject)dobj).getAllJoins();
        if (joins != null & !joins.isEmpty()) {
            final JSONArray joinJsonArray = QueryToJsonConverter.createNewQueryToJsonConverter().fromJoins(joins);
            jsonObject.put("joins", (Object)joinJsonArray);
        }
        PersistenceUtil.OUT.log(Level.FINEST, "Serilized JSONObject {0} for DataObject {1}  ", new Object[] { jsonObject, dobj });
        return jsonObject;
    }
    
    public static DataObject deserializeJson(final JSONObject jsonObject, final boolean validateSchema) throws JSONException, DataAccessException {
        final JSONObject metadataJson = jsonObject.getJSONObject("metadata");
        final JSONObject datajsonObject = jsonObject.getJSONObject("data");
        final WritableDataObject dobj = new WritableDataObject();
        final List<Row> deleterows = new ArrayList<Row>();
        final Iterator<String> keys = datajsonObject.keys();
        while (keys.hasNext()) {
            final String tableName = keys.next();
            try {
                final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
                if (tabDef == null) {
                    throw new DataAccessException("TableDefinition for table " + tableName + " is not found in metadata on de-serializing json into DO");
                }
                final Long metadigest = tabDef.metaDigest();
                final JSONObject metatable = metadataJson.getJSONObject(tableName);
                final JSONArray colJA = metatable.getJSONArray("columns");
                final JSONArray datatypeJA = metatable.getJSONArray("datatype");
                final List<String> columnsinJson = new ArrayList<String>();
                final List<String> datatypesInJson = new ArrayList<String>();
                for (int j = 0; j < colJA.length(); ++j) {
                    final String columnName = (String)colJA.get(j);
                    columnsinJson.add(columnName);
                    datatypesInJson.add((String)datatypeJA.get(j));
                }
                final List<String> columns = tabDef.getColumnNames();
                if (validateSchema) {
                    if (!new HashSet(columnsinJson).equals(new HashSet(columns))) {
                        PersistenceUtil.OUT.log(Level.INFO, "columns in Json {0} differs with table definition columns {1} of table [{2}]", new Object[] { columnsinJson, columns, tableName });
                        throw new DataAccessException("Columns in serialized JsonObject differs for the table " + tableName + ".. Check whether the Tabledefinition during serialization and de-serailization is same");
                    }
                    for (final String columnName2 : columns) {
                        final int columnIndex = columnsinJson.indexOf(columnName2);
                        final String colDataTypeInJson = datatypesInJson.get(columnIndex);
                        final String colDataType = tabDef.getColumnType(columnName2);
                        if (!colDataType.equalsIgnoreCase(colDataTypeInJson)) {
                            PersistenceUtil.OUT.log(Level.INFO, "datatype in Json [{0}] differs with table definition datatype [{1}] for column [{2}] of table [{3}]", new Object[] { colDataTypeInJson, colDataType, columnName2, tableName });
                            throw new DataAccessException("Datatype differs for the column [" + columnName2 + "] of table [" + tableName + "].. Check whether the Tabledefinition during serialization and de-serailization is same");
                        }
                    }
                }
                final JSONArray rowsArr = datajsonObject.getJSONArray(tableName);
                for (int i = 0; i < rowsArr.length(); ++i) {
                    final JSONArray rowJA = rowsArr.getJSONArray(i);
                    final List rowValues = new ArrayList();
                    final JSONArray valJA = rowJA.getJSONArray(0);
                    for (int k = 0; k < valJA.length(); ++k) {
                        if (!valJA.isNull(k)) {
                            rowValues.add(valJA.get(k));
                        }
                        else {
                            rowValues.add(null);
                        }
                    }
                    String tableAlias = tableName;
                    if (!rowJA.isNull(4)) {
                        tableAlias = rowJA.getString(4);
                    }
                    final Row row = new Row(tableName, tableAlias);
                    for (final String columnName3 : columns) {
                        final int columnIndex2 = columnsinJson.indexOf(columnName3);
                        if (columnIndex2 != -1) {
                            final Object value = rowValues.get(columnIndex2);
                            final String colDataType2 = tabDef.getColumnDefinitionByName(columnName3).getDataType();
                            if (value != null) {
                                String valueStr = value.toString();
                                Label_0724: {
                                    if (!"SCHAR".equals(colDataType2)) {
                                        if (!"SBLOB".equals(colDataType2)) {
                                            break Label_0724;
                                        }
                                    }
                                    try {
                                        valueStr = JsonUtil.decryptValue((String)value);
                                    }
                                    catch (final Exception e) {
                                        PersistenceUtil.OUT.log(Level.FINE, "Exception when handling SCHAR object", e.getMessage());
                                    }
                                    try {
                                        row.set(columnName3, (value instanceof UniqueValueHolder) ? value : MetaDataUtil.convert(valueStr, colDataType2));
                                        row.setAppropOrigValue(columnName3, valueStr);
                                    }
                                    catch (final Exception e) {
                                        if (valueStr.startsWith("UVH@")) {
                                            continue;
                                        }
                                        PersistenceUtil.OUT.log(Level.FINE, "Exception in setting Row value ::{0}", e.getMessage());
                                        row.set(columnName3, value);
                                    }
                                }
                            }
                            else {
                                row.set(columnName3, value);
                            }
                        }
                    }
                    final JSONArray dirtyArrIdx = rowJA.getJSONArray(2);
                    final JSONArray origValuesJA = rowJA.getJSONArray(3);
                    for (int index = 0; index < dirtyArrIdx.length(); ++index) {
                        final int dirtyIndex = dirtyArrIdx.getInt(index);
                        final String columnName4 = columnsinJson.get(dirtyIndex - 1);
                        if (tabDef.getColumnIndex(columnName4) != -1) {
                            row.markAsDirty(columnName4);
                            if (!origValuesJA.isNull(index)) {
                                final Object origValue = origValuesJA.get(index);
                                String valueStr2 = origValue.toString();
                                try {
                                    final String colDataType3 = tabDef.getColumnType(columnName4);
                                    Label_0978: {
                                        if (!"SCHAR".equals(colDataType3)) {
                                            if (!"SBLOB".equals(colDataType3)) {
                                                break Label_0978;
                                            }
                                        }
                                        try {
                                            valueStr2 = JsonUtil.decryptValue(valueStr2);
                                        }
                                        catch (final Exception e2) {
                                            PersistenceUtil.OUT.log(Level.FINE, "Exception when handling SCHAR object", e2.getMessage());
                                        }
                                    }
                                    row.setAppropOrigValue(columnName4, valueStr2);
                                }
                                catch (final Exception e3) {
                                    if (!valueStr2.startsWith("UVH@")) {
                                        PersistenceUtil.OUT.log(Level.FINE, "Exception in setting Row Original value ::{0}", e3.getMessage());
                                    }
                                }
                            }
                            else {
                                row.setAppropOrigValue(columnName4, null);
                            }
                        }
                    }
                    PersistenceUtil.OUT.log(Level.FINEST, "constructed row :: " + row);
                    final int operation = rowJA.getInt(1);
                    if (operation == 1) {
                        dobj.addRow(row);
                    }
                    else if (operation == 2) {
                        dobj.updateBlindly(row);
                    }
                    else if (operation == 3) {
                        dobj.add(row);
                        dobj.deleteRowIgnoreFK(row);
                    }
                    else {
                        dobj.add(row);
                    }
                }
            }
            catch (final MetaDataException mde) {
                PersistenceUtil.OUT.log(Level.INFO, "metadata exception on de-serializing JSON into DO: {0} ", mde);
            }
        }
        if (jsonObject.has("joins")) {
            final JSONArray joinJson = jsonObject.getJSONArray("joins");
            final List<Join> joins = QueryToJsonConverter.createNewQueryToJsonConverter().toJoins(joinJson);
            for (final Join join : joins) {
                dobj.addJoin(join);
            }
        }
        PersistenceUtil.OUT.log(Level.FINEST, "Deserialized DataObject {0} for JSONObject {1} ", new Object[] { dobj, jsonObject });
        return dobj;
    }
    
    private static JSONObject getMetaDataJSON(final TableDefinition tabDef) throws JSONException {
        final JSONObject tableMetaData = new JSONObject();
        final List<String> columnnames = tabDef.getColumnNames();
        final JSONArray columnNamesJA = new JSONArray();
        final JSONArray colDataTypeJA = new JSONArray();
        for (final String columnName : columnnames) {
            columnNamesJA.put((Object)columnName);
            final String datatype = tabDef.getColumnType(columnName);
            colDataTypeJA.put((Object)datatype);
        }
        final long metaDigest = tabDef.metaDigest();
        tableMetaData.put("columns", (Object)columnNamesJA);
        tableMetaData.put("datatype", (Object)colDataTypeJA);
        return tableMetaData;
    }
    
    private static JSONArray getRowJA(final Row row, final TableDefinition tabDef) throws JSONException {
        final String tableName = row.getOriginalTableName();
        final String tableAlias = row.getTableName();
        final List<String> columnnames = tabDef.getColumnNames();
        final JSONArray rowJA = new JSONArray();
        final List values = row.getValues();
        final JSONArray rowValues = new JSONArray();
        for (int i = 0; i < values.size(); ++i) {
            final String columnName = columnnames.get(i);
            final Object value = values.get(i);
            final ColumnDefinition colDef = tabDef.getColumnDefinitionByName(columnName);
            final String colDataType = colDef.getDataType();
            rowValues.put(getValue(value, colDataType));
        }
        rowJA.put(0, (Object)rowValues);
        final List<Integer> dirtyArr = row.getChangedColumnIndices();
        final JSONArray dirtycols = new JSONArray();
        final JSONArray origValuesJA = new JSONArray();
        if (dirtyArr != null) {
            for (final int dirtyIndex : dirtyArr) {
                dirtycols.put(dirtyIndex);
                final Object origValue = row.getOriginalValue(dirtyIndex);
                final String dirtyColDataType = tabDef.getColumnType(dirtyIndex);
                origValuesJA.put(getValue(origValue, dirtyColDataType));
            }
        }
        rowJA.put(2, (Object)dirtycols);
        rowJA.put(3, (Object)origValuesJA);
        if (!tableAlias.equals(tableName)) {
            rowJA.put(4, (Object)tableAlias);
        }
        return rowJA;
    }
    
    private static Object getValue(Object value, final String colDataType) {
        if (value instanceof ByteArrayInputStream) {
            try {
                value = IOUtils.toString((InputStream)value, (String)null);
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
        Label_0072: {
            if (!"SCHAR".equals(colDataType)) {
                if (!"SBLOB".equals(colDataType)) {
                    break Label_0072;
                }
            }
            try {
                value = JsonUtil.encryptValue((String)value);
            }
            catch (final Exception e2) {
                PersistenceUtil.OUT.log(Level.FINE, "Exception when handling SCHAR object", e2.getMessage());
            }
        }
        if (value instanceof Date) {
            if ("DATE".equals(colDataType)) {
                final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                value = formatter.format((Date)value);
            }
            if ("DATETIME".equals(colDataType) || "TIMESTAMP".equals(colDataType)) {
                final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                value = formatter.format((Date)value);
            }
        }
        return value;
    }
    
    public static DBPasswordProvider getDBPasswordProvider() throws PersistenceException {
        return getDBPasswordProvider(PersistenceInitializer.getConfigurationValue("DBName"));
    }
    
    public static DBPasswordProvider getDBPasswordProvider(final String dbType) throws PersistenceException {
        if (PersistenceUtil.dbPasswordProviderMap.get(dbType) == null) {
            String className = null;
            try {
                final Properties props = PersistenceInitializer.getConfigurationProps(dbType);
                className = ((props.getProperty("dbpasswordprovider") != null) ? props.getProperty("dbpasswordprovider") : DefaultDBPasswordProvider.class.getName());
                final DBPasswordProvider dbPasswordProvider = (DBPasswordProvider)Class.forName(className).newInstance();
                PersistenceUtil.dbPasswordProviderMap.put(dbType, dbPasswordProvider);
            }
            catch (final Exception e) {
                throw new PersistenceException("Exception while instantiating the class: " + className, e);
            }
        }
        return PersistenceUtil.dbPasswordProviderMap.get(dbType);
    }
    
    public static int removeOrphanRows(final ForeignKeyDefinition fkDef) throws MetaDataException, QueryConstructionException, SQLException {
        try (final Connection con = RelationalAPI.getInstance().getConnection()) {
            return removeOrphanRows(RelationalAPI.getInstance().getDBAdapter(), con, fkDef);
        }
    }
    
    public static int removeOrphanRows(final DBAdapter dbAdapter, final Connection con, final ForeignKeyDefinition fkDef) throws MetaDataException, QueryConstructionException, SQLException {
        final List<String> tableNames = new ArrayList<String>();
        tableNames.add(fkDef.getSlaveTableName());
        tableNames.add(fkDef.getMasterTableName());
        final SelectQuery sq = new SelectQueryImpl(Table.getTable(fkDef.getSlaveTableName()));
        final List<String> pkColumns = MetaDataUtil.getTableDefinitionByName(fkDef.getSlaveTableName()).getPrimaryKey().getColumnList();
        for (final String columnName : pkColumns) {
            final Column column = Column.getColumn(fkDef.getSlaveTableName(), columnName);
            sq.addSelectColumn(column);
        }
        final Join join = new Join(fkDef.getSlaveTableName(), fkDef.getMasterTableName(), fkDef.getFkColumns().toArray(new String[fkDef.getFkColumns().size()]), fkDef.getFkRefColumns().toArray(new String[fkDef.getFkRefColumns().size()]), 1);
        sq.addJoin(join);
        Criteria criteria = null;
        for (final String columnName2 : fkDef.getFkRefColumns()) {
            final Column column2 = Column.getColumn(fkDef.getMasterTableName(), columnName2);
            criteria = ((criteria == null) ? new Criteria(column2, null, 0) : criteria.and(column2, null, 0));
        }
        if (criteria != null) {
            for (final String columnName2 : fkDef.getFkColumns()) {
                if (MetaDataUtil.getTableDefinitionByName(fkDef.getSlaveTableName()).getColumnDefinitionByName(columnName2).isNullable()) {
                    final Column column2 = Column.getColumn(fkDef.getSlaveTableName(), columnName2);
                    criteria = criteria.and(column2, null, 1);
                }
            }
        }
        final String sql = dbAdapter.getSQLGenerator().getSQLForDelete(fkDef.getSlaveTableName(), join, criteria);
        Statement statement = null;
        try {
            statement = con.createStatement();
            PersistenceUtil.OUT.fine("Query to be executed :: " + sql);
            final int affectedRows = dbAdapter.executeUpdate(statement, sql);
            PersistenceUtil.OUT.fine("No of rows affected ::: " + affectedRows);
            return affectedRows;
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
    
    public static List<String> getEncryptedSqlServerProps() {
        if (PersistenceUtil.sqlServerKeys == null) {
            (PersistenceUtil.sqlServerKeys = new ArrayList<String>()).add("certificate.name");
            PersistenceUtil.sqlServerKeys.add("certificate.subject");
            PersistenceUtil.sqlServerKeys.add("symmetrickey.name");
            PersistenceUtil.sqlServerKeys.add("masterkey.password");
            PersistenceUtil.sqlServerKeys = Collections.unmodifiableList((List<? extends String>)PersistenceUtil.sqlServerKeys);
        }
        return PersistenceUtil.sqlServerKeys;
    }
    
    public static List<String> getEncryptedConfigurations() {
        if (PersistenceUtil.configurations == null) {
            (PersistenceUtil.configurations = new ArrayList<String>()).add("ECTag");
            PersistenceUtil.configurations.add("LVInstance");
            PersistenceUtil.configurations = Collections.unmodifiableList((List<? extends String>)PersistenceUtil.configurations);
        }
        return PersistenceUtil.configurations;
    }
    
    static {
        CLASS_NAME = PersistenceUtil.class.getName();
        PersistenceUtil.dbPasswordProviderMap = new TreeMap<String, DBPasswordProvider>(String.CASE_INSENSITIVE_ORDER);
        OUT = Logger.getLogger(PersistenceUtil.class.getName());
        PersistenceUtil.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
