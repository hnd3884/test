package com.adventnet.persistence;

import java.util.concurrent.ConcurrentHashMap;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.ds.query.ArchiveTable;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.Collections;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import java.util.Collection;
import java.util.Map;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.internal.GetUtil;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Iterator;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Arrays;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.logging.Logger;

public class QueryConstructor
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    private static List nullList;
    private static MultiKeyMap personalityCache;
    
    private QueryConstructor() {
    }
    
    public static SelectQuery get(final String tableName, final Row rowInstance) throws DataAccessException {
        final Criteria criteria = formCriteria(rowInstance);
        return get(tableName, criteria);
    }
    
    public static SelectQuery get(final String tableName, final List rowInstances) throws DataAccessException {
        final Criteria criteria = formCriteria(rowInstances);
        return get(tableName, criteria);
    }
    
    public static SelectQuery get(final String tableName, final Criteria condition) throws DataAccessException {
        final boolean[] isLeftJoins = { false };
        final List tablesList = new ArrayList(1);
        tablesList.add(tableName);
        return get(tablesList, isLeftJoins, condition);
    }
    
    public static SelectQuery get(final List tableNames, final Row rowInstance) throws DataAccessException {
        final Criteria criteria = formCriteria(rowInstance);
        return get(tableNames, criteria);
    }
    
    public static SelectQuery get(final List tableNames, final List rowInstances) throws DataAccessException {
        final Criteria criteria = formCriteria(rowInstances);
        return get(tableNames, criteria);
    }
    
    public static SelectQuery get(final List tableNames, final Criteria condition) throws DataAccessException {
        final boolean[] isLeftJoins = new boolean[tableNames.size()];
        Arrays.fill(isLeftJoins, false);
        return get(tableNames, isLeftJoins, condition);
    }
    
    public static SelectQuery get(final String tableName, final Row rowInstance, final String... columnNames) throws DataAccessException {
        final Criteria criteria = formCriteria(rowInstance);
        final SelectQuery sq = get(tableName, columnNames);
        sq.setCriteria(criteria);
        return sq;
    }
    
    public static SelectQuery get(final String tableName, final Criteria criteria, final String... columnNames) {
        final SelectQuery sq = get(tableName, columnNames);
        sq.setCriteria(criteria);
        return sq;
    }
    
    public static SelectQuery get(final String tableName, final String... columnNames) {
        final SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
        if (columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException("Atleast one column should be selected");
        }
        for (final String columnName : columnNames) {
            sq.addSelectColumn(Column.getColumn(tableName, columnName));
        }
        return sq;
    }
    
    public static SelectQuery getForPersonality(final String personalityName, final Row instance) throws DataAccessException {
        final Criteria criteria = formCriteria(instance);
        return getForPersonality(personalityName, criteria);
    }
    
    public static SelectQuery getForPersonality(final String personalityName, final List instances) throws DataAccessException {
        final Criteria criteria = formCriteria(instances);
        return getForPersonality(personalityName, criteria);
    }
    
    public static SelectQuery getForPersonality(final String personalityName, final Criteria criteria) throws DataAccessException {
        if (personalityName == null || personalityName.trim().equals("")) {
            throw new DataAccessException("Personality cannot be null");
        }
        final List personalities = new ArrayList();
        personalities.add(personalityName);
        final SelectQuery sq = getForPersonalities(personalities, criteria);
        return sq;
    }
    
    public static SelectQuery getForPersonalities(final List personalities, final Row instance) throws DataAccessException {
        final Criteria criteria = formCriteria(instance);
        return getForPersonalities(personalities, criteria);
    }
    
    public static SelectQuery getForPersonalities(final List personalities, final List instances) throws DataAccessException {
        final Criteria criteria = formCriteria(instances);
        return getForPersonalities(personalities, criteria);
    }
    
    public static SelectQuery getForPersonalities(final List<String> personalities, final Criteria criteria) throws DataAccessException {
        return getForPersonalities(personalities, null, criteria);
    }
    
    private static List<String> getTableNames(final SelectQuery sq) {
        List<String> tableNames = null;
        if (sq != null) {
            tableNames = new ArrayList<String>();
            for (final Table table : sq.getTableList()) {
                tableNames.add(table.getTableName());
            }
        }
        return tableNames;
    }
    
    private static void CheckForFKConsistency(final List<String> tableNames, final List<String> fkNames) throws MetaDataException {
        ForeignKeyDefinition fkDef = null;
        for (int i = 0; i < fkNames.size(); ++i) {
            fkDef = MetaDataUtil.getForeignKeyDefinitionByName(fkNames.get(i));
            if (!tableNames.contains(fkDef.getMasterTableName())) {
                throw new MetaDataException("No rows found for the table:: " + fkDef.getMasterTableName());
            }
        }
    }
    
    public static SelectQuery getCompleteQuery(final Row domTbRow) throws DataAccessException {
        try {
            final Column allColumns = Column.getColumn(null, "*");
            final String tableName = domTbRow.getTableName();
            final String domTbName = PersonalityConfigurationUtil.getDominantTable(tableName);
            final SelectQuery sq = new SelectQueryImpl(Table.getTable(domTbName));
            sq.addSelectColumn(allColumns);
            final List<String> consTabs = new ArrayList<String>();
            List<String> tableNameList = new ArrayList<String>();
            List<String> persNames = null;
            final List<String> fkNames = new ArrayList<String>();
            final String idxTableName = domTbName + "_PIDX";
            final TableDefinition idxTD = MetaDataUtil.getTableDefinitionByName(idxTableName);
            if (idxTD != null) {
                final SelectQuery idxQry = new SelectQueryImpl(Table.getTable(idxTableName));
                idxQry.addSelectColumn(allColumns);
                final List idxPkColNames = idxTD.getPrimaryKey().getColumnList();
                Criteria criteria = null;
                for (int i = 0; i < idxPkColNames.size(); ++i) {
                    final String colName = idxPkColNames.get(i);
                    if (!colName.equals("TABLE_NAME")) {
                        if (criteria == null) {
                            criteria = new Criteria(Column.getColumn(idxTableName, colName), domTbRow.get(colName), 0);
                        }
                        else {
                            criteria = criteria.and(new Criteria(Column.getColumn(idxTableName, colName), domTbRow.get(colName), 0));
                        }
                    }
                }
                idxQry.setCriteria(criteria);
                final DataObject idxDO = GetUtil.get(idxQry);
                final List<String> persList = PersonalityConfigurationUtil.getContainedPersonalities(domTbName);
                final Iterator iterator = idxDO.get(idxTableName, "TABLE_NAME");
                while (iterator.hasNext()) {
                    final String tbName = iterator.next();
                    if (tbName.equals(domTbName)) {
                        continue;
                    }
                    consTabs.add(tbName);
                    String fkName = null;
                    for (final String persName : persList) {
                        fkName = PersonalityConfigurationUtil.getFKsForConstituentTables(persName).get(tbName);
                        if (fkName != null) {
                            fkNames.add(fkName);
                            break;
                        }
                    }
                    if (fkName == null) {
                        throw new DataAccessException("Cannot able to resolve the ForeignKey Constraint for the tableName :: [" + tbName + "]. For more reference, dominantTableName [" + domTbName + "] and persList ::" + persList);
                    }
                }
                tableNameList = consTabs;
                tableNameList.add(domTbName);
            }
            else {
                persNames = PersonalityConfigurationUtil.getContainedPersonalities(domTbName);
                final Iterator<String> persIterator = persNames.iterator();
                consTabs.add(domTbName);
                while (persIterator.hasNext()) {
                    final String persName2 = persIterator.next();
                    final List<String> consTbNames = PersonalityConfigurationUtil.getConstituentTables(persName2);
                    final Map<String, String> fkNameMap = PersonalityConfigurationUtil.getFKsForConstituentTables(persName2);
                    for (final String consTbName : consTbNames) {
                        final String fkName2 = fkNameMap.get(consTbName);
                        if (fkName2 == null) {
                            throw new DataAccessException("No ForeignKey found for the table :: [" + consTbName + "]");
                        }
                        if (consTabs.contains(consTbName)) {
                            continue;
                        }
                        consTabs.add(consTbName);
                        fkNames.add(fkName2);
                    }
                }
                tableNameList = consTabs;
            }
            CheckForFKConsistency(tableNameList, fkNames);
            addJoinsIntoQuery(sq, fkNames);
            sq.setCriteria(formCriteria(domTbRow));
            return sq;
        }
        catch (final MetaDataException mde) {
            QueryConstructor.OUT.severe("Exception occurred while getting complete query:: " + mde.getMessage());
            mde.printStackTrace();
            throw new DataAccessException(mde);
        }
    }
    
    public static void addJoinsIntoQuery(final SelectQuery sq, final List<String> fkNames) throws MetaDataException, DataAccessException {
        if (sq != null) {
            ForeignKeyDefinition fkDef = null;
            final List<String> clonedFKNames = new ArrayList<String>(fkNames);
            while (clonedFKNames.size() > 0) {
                final String fn = clonedFKNames.get(0);
                clonedFKNames.remove(0);
                try {
                    fkDef = MetaDataUtil.getForeignKeyDefinitionByName(fn);
                    if (!containsTableName(sq.getTableList(), fkDef.getMasterTableName())) {
                        clonedFKNames.add(fn);
                        continue;
                    }
                }
                catch (final MetaDataException mde) {
                    throw new DataAccessException(mde.getMessage(), mde);
                }
                final List<String> slaveCols = fkDef.getFkColumns();
                final List<String> masterCols = fkDef.getFkRefColumns();
                final String[] masterColNames = new String[masterCols.size()];
                final String[] slaveColNames = new String[slaveCols.size()];
                for (int j = 0; j < masterCols.size(); ++j) {
                    masterColNames[j] = masterCols.get(j);
                    slaveColNames[j] = slaveCols.get(j);
                }
                sq.addJoin(new Join(fkDef.getMasterTableName(), fkDef.getSlaveTableName(), masterColNames, slaveColNames, 1));
            }
        }
    }
    
    private static boolean containsTableName(final List<Table> tables, final String tableName) {
        for (final Table t : tables) {
            if (t.getTableName().equals(tableName)) {
                return true;
            }
        }
        return false;
    }
    
    private static SelectQuery addConstituentTablesIntoSelectQuery(SelectQuery sq, final List<String> personalityNames, final boolean deepRetrPersList) throws DataAccessException {
        try {
            if (personalityNames.size() == 0) {
                return sq;
            }
            List<String> processedTableNames = null;
            if (sq == null) {
                processedTableNames = new ArrayList<String>();
                final String domTbName = PersonalityConfigurationUtil.getDominantTableForPersonality(personalityNames.get(0));
                sq = new SelectQueryImpl(Table.getTable(domTbName));
                sq.addSelectColumn(Column.getColumn(domTbName, "*"));
                processedTableNames.add(domTbName);
            }
            else {
                processedTableNames = getTableNames(sq);
            }
            final List<String> unprocessedTableNames = new ArrayList<String>();
            final HashMap<String, ForeignKeyDefinition> fkDefns = new HashMap<String, ForeignKeyDefinition>();
            final HashMap<String, Boolean> tableNameVsMandatory = new HashMap<String, Boolean>();
            for (final String persName : personalityNames) {
                final List<String> consTbNames = PersonalityConfigurationUtil.getConstituentTables(persName);
                final List<String> mandatoryTbNames = PersonalityConfigurationUtil.getMandatoryConstituentTables(persName);
                final Map<String, String> fkNameMap = PersonalityConfigurationUtil.getFKsForConstituentTables(persName);
                for (final String consTbName : consTbNames) {
                    if (!processedTableNames.contains(consTbName)) {
                        if (unprocessedTableNames.contains(consTbName)) {
                            continue;
                        }
                        unprocessedTableNames.add(consTbName);
                        fkDefns.put(consTbName, MetaDataUtil.getForeignKeyDefinitionByName(fkNameMap.get(consTbName)));
                        if (mandatoryTbNames.contains(consTbName)) {
                            tableNameVsMandatory.put(consTbName, Boolean.TRUE);
                        }
                        else {
                            tableNameVsMandatory.put(consTbName, Boolean.FALSE);
                        }
                    }
                }
                final int unprocessedSize = unprocessedTableNames.size();
                int previousSize = unprocessedSize + 1;
                while (unprocessedTableNames.size() > 0 && previousSize > unprocessedTableNames.size()) {
                    previousSize = unprocessedTableNames.size();
                    for (final String tableName : unprocessedTableNames) {
                        ForeignKeyDefinition fkDefn = fkDefns.get(tableName);
                        if (fkDefn == null) {
                            fkDefn = getSuitableFK(processedTableNames, tableName);
                        }
                        if (fkDefn == null) {
                            throw new DataAccessException("No Join found between the tableList :: " + processedTableNames + "and the tableName :: [" + tableName + "]");
                        }
                        final boolean mandatory = tableNameVsMandatory.get(tableName);
                        addJoin(sq, tableName, fkDefn, deepRetrPersList || !mandatory);
                        sq.addSelectColumn(Column.getColumn(tableName, "*"));
                        processedTableNames.add(tableName);
                    }
                    unprocessedTableNames.removeAll(processedTableNames);
                }
            }
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException(mde.getMessage(), mde);
        }
        return sq;
    }
    
    public static SelectQuery getForPersonalities(final List personalities, final List nonIndexedDeepRetrievedPersonalities, final Criteria criteria) throws DataAccessException {
        QueryConstructor.OUT.entering(QueryConstructor.CLASS_NAME, "getForPersonalities", new Object[] { personalities, nonIndexedDeepRetrievedPersonalities, criteria });
        if (personalities == null || personalities.contains(null)) {
            throw new DataAccessException("Personality cannot be null or contain null");
        }
        Set key2 = null;
        if (nonIndexedDeepRetrievedPersonalities == null) {
            key2 = Collections.EMPTY_SET;
        }
        else {
            if (nonIndexedDeepRetrievedPersonalities.contains(null)) {
                throw new DataAccessException("Deep Retrieved Personalities cannot contain null");
            }
            if (!personalities.containsAll(nonIndexedDeepRetrievedPersonalities)) {
                QueryConstructor.OUT.log(Level.FINER, "Not all the personalities requested for deep retrieval {0} are listed in the personalities {1}", new Object[] { nonIndexedDeepRetrievedPersonalities, personalities });
                throw new DataAccessException("Not all the personalities requested for deep retrieval are listed in the personalities");
            }
            key2 = new HashSet(nonIndexedDeepRetrievedPersonalities);
        }
        if (personalities.size() == 0) {
            throw new DataAccessException("No Such Personality Exists. The list of tables specified to form SelectQuery can not be null/empty");
        }
        SelectQuery selqry = null;
        if (personalities.size() == 0) {
            throw new DataAccessException("No Such Personality Exists. The list of tables specified to form SelectQuery can not be null/empty");
        }
        final Set key3 = new HashSet(personalities);
        if (QueryConstructor.personalityCache.containsKey(key3, key2)) {
            final SelectQuery query = (SelectQuery)QueryConstructor.personalityCache.get(key3, key2);
            selqry = (SelectQuery)query.clone();
        }
        else {
            selqry = addConstituentTablesIntoSelectQuery(selqry, personalities, false);
            if (nonIndexedDeepRetrievedPersonalities != null && nonIndexedDeepRetrievedPersonalities.size() > 0) {
                final List domTables = getDominantTables(nonIndexedDeepRetrievedPersonalities);
                final List deepRetrPers = getContainedPersonalities(domTables);
                selqry = addConstituentTablesIntoSelectQuery(selqry, deepRetrPers, true);
            }
            if (selqry == null) {
                throw new DataAccessException("No Such Personality Exists :: " + personalities);
            }
            QueryConstructor.personalityCache.put(key3, key2, selqry.clone());
        }
        addTables(selqry, criteria);
        selqry.setCriteria(criteria);
        return selqry;
    }
    
    private static List getDominantTables(final List persList) throws DataAccessException {
        if (persList == null || persList.contains(null)) {
            throw new DataAccessException("Personality cannot be null or contain null");
        }
        final int persListSize = persList.size();
        final List domTables = new ArrayList(persListSize);
        for (int i = 0; i < persListSize; ++i) {
            final String persName = persList.get(i);
            final String dominantTable = PersonalityConfigurationUtil.getDominantTableForPersonality(persName);
            if (!domTables.contains(dominantTable)) {
                domTables.add(dominantTable);
            }
        }
        return domTables;
    }
    
    private static List getContainedPersonalities(final List domTableList) throws DataAccessException {
        QueryConstructor.OUT.log(Level.FINEST, "getContainedPersonalities for domTableList : {0}", domTableList);
        final List containedPersonalitiesToReturn = new ArrayList();
        for (int domTablesSize = domTableList.size(), i = 0; i < domTablesSize; ++i) {
            final String domTable = domTableList.get(i);
            final List containedPers = PersonalityConfigurationUtil.getContainedPersonalities(domTable);
            QueryConstructor.OUT.log(Level.FINEST, "containedPers[{0}] : {1}", new Object[] { domTable, containedPers });
            for (int containedPersSize = containedPers.size(), j = 0; j < containedPersSize; ++j) {
                final String containedPersJ = containedPers.get(j);
                if (!containedPersonalitiesToReturn.contains(containedPersJ)) {
                    containedPersonalitiesToReturn.add(containedPersJ);
                }
            }
        }
        QueryConstructor.OUT.log(Level.FINEST, "getContainedPersonalities returning : {0}", containedPersonalitiesToReturn);
        return containedPersonalitiesToReturn;
    }
    
    public static SelectQuery get(final List tableNames, final List optionalTableNames, final Criteria condition) throws DataAccessException {
        if (tableNames == null) {
            throw new DataAccessException("List of tableNames cannot be null");
        }
        tableNames.removeAll(QueryConstructor.nullList);
        final boolean[] isLeftJoins = new boolean[tableNames.size()];
        if (tableNames.size() == 0) {
            throw new DataAccessException("List of tableNames doesnot contain any names");
        }
        if (optionalTableNames != null) {
            optionalTableNames.removeAll(QueryConstructor.nullList);
            if (!tableNames.containsAll(optionalTableNames)) {
                throw new DataAccessException("All OptionalTableNames are not found in the tableNames List");
            }
        }
        for (int i = 0; i < isLeftJoins.length; ++i) {
            isLeftJoins[i] = (optionalTableNames != null && optionalTableNames.contains(tableNames.get(i)));
        }
        return get(tableNames, isLeftJoins, condition);
    }
    
    public static SelectQuery get(final List tableNames, final Criteria condition, final Map<String, ArchiveTable> archiveTabMap) throws DataAccessException {
        final boolean[] isLeftJoins = new boolean[tableNames.size()];
        Arrays.fill(isLeftJoins, false);
        return get(tableNames, isLeftJoins, condition, archiveTabMap);
    }
    
    public static SelectQuery get(final List tableNames, final boolean[] isLeftJoins, final Criteria condition) throws DataAccessException {
        return get(tableNames, isLeftJoins, condition, new HashMap<String, ArchiveTable>());
    }
    
    public static SelectQuery get(final List tableNames, final boolean[] isLeftJoins, final Criteria condition, final Map<String, ArchiveTable> archiveTabMap) throws DataAccessException {
        QueryConstructor.OUT.entering(QueryConstructor.CLASS_NAME, "get", new Object[] { tableNames, isLeftJoins, condition });
        int size = 0;
        if (tableNames == null || (size = tableNames.size()) == 0) {
            QueryConstructor.OUT.log(Level.FINER, "The list of tables specified to form SelectQuery can not be null/empty: {0}", tableNames);
            throw new DataAccessException("The list of tables specified to form SelectQuery can not be null/empty");
        }
        if (isLeftJoins == null) {
            QueryConstructor.OUT.log(Level.FINER, "The list specifying whether to left join each table can't be null: {0}", tableNames);
            throw new DataAccessException("The list specifying whether to left join each table can't be null");
        }
        if (isLeftJoins.length != size) {
            QueryConstructor.OUT.log(Level.FINER, "The list specifying whether to left join each table should contain the same number of elements as the list of tables. Tables length: {0}. IsLeftJoin length: {1}", new Object[] { new Integer(tableNames.size()), new Integer(isLeftJoins.length) });
            final String mess = "The list specifying whether to left join each table should contain the same number of elements as the list of tables";
            throw new DataAccessException(mess);
        }
        final List clonedTableNames = new ArrayList(tableNames);
        final List clonedLeftJoins = new ArrayList();
        for (int i = 0; i < isLeftJoins.length; ++i) {
            clonedLeftJoins.add(new Boolean(isLeftJoins[i]));
        }
        String tableName = clonedTableNames.get(0);
        final ArchiveTable arcTable = archiveTabMap.get(tableName);
        final Table firstTable = (arcTable != null) ? arcTable : Table.getTable(tableName);
        final SelectQuery sq = new SelectQueryImpl(firstTable);
        addSelectColumn(sq, firstTable, "*");
        int initialSize = clonedTableNames.size();
        clonedTableNames.remove(0);
        clonedLeftJoins.remove(0);
        int changedSize;
        for (changedSize = clonedTableNames.size(); changedSize < initialSize && changedSize != 0; changedSize = clonedTableNames.size()) {
            initialSize = changedSize;
            final Iterator tableNameIterator = clonedTableNames.iterator();
            final Iterator leftJoinsIterator = clonedLeftJoins.iterator();
            while (tableNameIterator.hasNext()) {
                tableName = tableNameIterator.next();
                final boolean isLeftJoin = leftJoinsIterator.next();
                if (addTable(sq, tableName, isLeftJoin, archiveTabMap)) {
                    tableNameIterator.remove();
                    leftJoinsIterator.remove();
                }
                Table joinTable = archiveTabMap.get(tableName);
                if (joinTable == null) {
                    joinTable = Table.getTable(tableName);
                }
                addSelectColumn(sq, joinTable, "*");
            }
        }
        if (changedSize > 0) {
            QueryConstructor.OUT.log(Level.FINE, "No foreign key is found between the table {0} and other tables specified prior to that: {1}", new Object[] { clonedTableNames, sq.getTableList() });
            throw new DataAccessException("No foreign key is found between the table " + clonedTableNames + " and other tables specified in the List");
        }
        addTables(sq, condition);
        sq.setCriteria(condition);
        QueryConstructor.OUT.exiting(QueryConstructor.CLASS_NAME, "get", sq);
        return sq;
    }
    
    private static void addSelectColumn(final SelectQuery sq, final Table table, final String columnName) {
        if (table instanceof ArchiveTable) {
            sq.addSelectColumn(Column.getColumn(((ArchiveTable)table).getArchiveTableAlias(), "*"));
        }
        else {
            sq.addSelectColumn(Column.getColumn(table.getTableAlias(), "*"));
        }
    }
    
    public static SelectQuery reArrangeJoins(final SelectQuery sq) {
        final SelectQuery clonedSQ = (SelectQuery)sq.clone();
        final Table t = sq.getTableList().get(0);
        final SelectQuery retSQ = new SelectQueryImpl(t);
        final List joins = clonedSQ.getJoins();
        final int size = joins.size();
        for (int i = 0; i < size - 1; ++i) {
            final Join fjoin = joins.get(i);
            for (int j = i + 1; j < size; ++j) {
                final Join sjoin = joins.get(j);
                if (fjoin.getReferencedTableAlias().equals(sjoin.getBaseTableAlias())) {
                    joins.remove(sjoin);
                    joins.add(i + 1, sjoin);
                    break;
                }
            }
        }
        for (int i = 0; i < size; ++i) {
            retSQ.addJoin(joins.get(i));
        }
        retSQ.addSelectColumns(clonedSQ.getSelectColumns());
        if (clonedSQ.getSortColumns() != null && clonedSQ.getSortColumns().size() > 0) {
            retSQ.addSortColumns(clonedSQ.getSortColumns());
        }
        if (clonedSQ.getGroupByClause() != null) {
            retSQ.setGroupByClause(clonedSQ.getGroupByClause());
        }
        if (clonedSQ.getCriteria() != null) {
            retSQ.setCriteria(clonedSQ.getCriteria());
        }
        return retSQ;
    }
    
    private static void addTables(final SelectQuery sq, final Criteria criteria) throws DataAccessException {
        addTables(sq, criteria, null);
    }
    
    private static void addTables(final SelectQuery sq, final Criteria criteria, final Map<String, ArchiveTable> archiveTabMap) throws DataAccessException {
        if (criteria == null) {
            QueryConstructor.OUT.exiting(QueryConstructor.CLASS_NAME, "addTables", sq);
            return;
        }
        final Criteria leftCriteria = criteria.getLeftCriteria();
        final Criteria rightCriteria = criteria.getRightCriteria();
        if (leftCriteria == null || rightCriteria == null) {
            final Column column = criteria.getColumn();
            final String tableName = column.getTableAlias();
            final List tableList = sq.getTableList();
            boolean found = false;
            for (int size = tableList.size(), i = 0; i < size; ++i) {
                final Table table = tableList.get(i);
                if (tableName.equals(table.getTableName())) {
                    found = true;
                    break;
                }
                if (table instanceof ArchiveTable) {
                    found = (((ArchiveTable)table).getArchiveTableAlias().equals(tableName) || ((ArchiveTable)table).getInvisibleTableAlias().equals(tableName));
                    if (found) {
                        break;
                    }
                }
            }
            if (!found && !addTable(sq, tableName, false, null)) {
                QueryConstructor.OUT.log(Level.WARNING, "No foreign key is found between the table {0} and other tables specified prior to that: {1}", new Object[] { tableName, sq.getTableList() });
                throw new DataAccessException("No foreign key is found between the table " + tableName + " and other tables specified in the List :: " + sq.getTableList());
            }
        }
        else if (leftCriteria != null) {
            addTables(sq, leftCriteria);
        }
        if (rightCriteria != null) {
            addTables(sq, rightCriteria);
        }
        QueryConstructor.OUT.exiting(QueryConstructor.CLASS_NAME, "addTables", sq);
    }
    
    private static boolean addTable(final SelectQuery sq, final String tableName, final boolean isLeftJoin, final Map<String, ArchiveTable> arcTabMap) throws DataAccessException {
        QueryConstructor.OUT.entering(QueryConstructor.CLASS_NAME, "addTable", new Object[] { sq, tableName });
        final List processedTables = sq.getTableList();
        final int size = processedTables.size();
        final List processedTableNames = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            final Table table = processedTables.get(i);
            processedTableNames.add(table.getTableName());
        }
        boolean added = addJoin(processedTableNames, tableName, sq, isLeftJoin, true, arcTabMap);
        if (!added) {
            added = addJoin(processedTableNames, tableName, sq, isLeftJoin, false, arcTabMap);
        }
        return added;
    }
    
    private static boolean addJoin(final List processedTables, final String tableName, final SelectQuery sq, final boolean isLeftJoin, final boolean pkBased, final Map<String, ArchiveTable> arcTabMap) throws DataAccessException {
        QueryConstructor.OUT.entering(QueryConstructor.CLASS_NAME, "addJoin", new Object[] { processedTables, tableName, sq, isLeftJoin, pkBased });
        ForeignKeyDefinition fkDefn = null;
        for (int size = processedTables.size(), i = 0; i < size; ++i) {
            final String processedTableName = processedTables.get(i);
            fkDefn = getSuitableFK(processedTableName, tableName, pkBased);
            if (fkDefn != null) {
                sq.addJoin(getSuitableJoin(processedTableName, tableName, fkDefn, isLeftJoin, arcTabMap));
                return true;
            }
        }
        return false;
    }
    
    private static Join getSuitableJoin(final String processedTableName, final String tableName, final ForeignKeyDefinition fkDefn, final boolean isLeftJoin, final Map<String, ArchiveTable> arcTabMap) throws DataAccessException {
        final TableDefinition td1 = getTableDefinition(processedTableName);
        final String[] cols1 = new String[fkDefn.getFkRefColumns().size()];
        final String[] cols2 = new String[fkDefn.getFkColumns().size()];
        for (int j = 0; j < fkDefn.getFkColumns().size(); ++j) {
            cols1[j] = fkDefn.getFkRefColumns().get(j);
            cols2[j] = fkDefn.getFkColumns().get(j);
        }
        Table masterTable;
        Table slaveTable;
        if (arcTabMap != null) {
            if (arcTabMap.containsKey(processedTableName)) {
                masterTable = arcTabMap.get(processedTableName);
            }
            else {
                masterTable = Table.getTable(processedTableName);
            }
            if (arcTabMap.containsKey(tableName)) {
                slaveTable = arcTabMap.get(tableName);
            }
            else {
                slaveTable = Table.getTable(tableName);
            }
        }
        else {
            masterTable = Table.getTable(processedTableName);
            slaveTable = Table.getTable(tableName);
        }
        if (td1.getTableName().equals(fkDefn.getMasterTableName())) {
            return new Join(masterTable, slaveTable, cols1, cols2, isLeftJoin ? 1 : 2);
        }
        if (td1.getTableName().equals(fkDefn.getSlaveTableName())) {
            return new Join(masterTable, slaveTable, cols2, cols1, isLeftJoin ? 1 : 2);
        }
        return null;
    }
    
    public static ForeignKeyDefinition getSuitableFK(final List processedTables, final String tableName) throws DataAccessException {
        ForeignKeyDefinition fkDefn = null;
        fkDefn = getSuitableFK(processedTables, tableName, true);
        if (fkDefn == null) {
            fkDefn = getSuitableFK(processedTables, tableName, false);
        }
        return fkDefn;
    }
    
    public static ForeignKeyDefinition getSuitableFK(final List processedTables, final String tableName, final boolean pkBased) throws DataAccessException {
        ForeignKeyDefinition fkDefn = null;
        for (int size = processedTables.size(), i = 0; i < size; ++i) {
            final String processedTableName = processedTables.get(i);
            fkDefn = getSuitableFK(processedTableName, tableName, pkBased);
            if (fkDefn != null) {
                break;
            }
        }
        return fkDefn;
    }
    
    public static ForeignKeyDefinition getSuitableFK(final String tableName1, final String tableName2, final String colName) throws DataAccessException {
        final ForeignKeyDefinition retFkDefn = null;
        List fks = null;
        try {
            fks = MetaDataUtil.getForeignKeys(tableName1, tableName2);
            if (fks == null) {
                return null;
            }
            for (final ForeignKeyDefinition fkDef : fks) {
                if (fkDef.getSlaveTableName().equals(tableName2)) {
                    if (fkDef.getFkColumns().contains(colName)) {
                        return fkDef;
                    }
                    if (fkDef.getFkRefColumns().contains(colName)) {
                        return fkDef;
                    }
                    continue;
                }
            }
            return null;
        }
        catch (final MetaDataException mde) {
            final String mess = "Exception occurred while fetching relationship between the tables " + tableName1 + " and " + tableName2 + " colName " + colName;
            QueryConstructor.OUT.log(Level.FINER, mess, mde);
            throw new DataAccessException(mess, mde);
        }
    }
    
    public static ForeignKeyDefinition getSuitableFK(final String tableName1, final String tableName2) throws DataAccessException {
        ForeignKeyDefinition fkDefn = getSuitableFK(tableName1, tableName2, true);
        if (fkDefn == null) {
            fkDefn = getSuitableFK(tableName1, tableName2, false);
        }
        return fkDefn;
    }
    
    public static ForeignKeyDefinition getSuitableFK(final String tableName1, final String tableName2, final boolean pkBased) throws DataAccessException {
        ForeignKeyDefinition fkDefn = null;
        List fks = null;
        try {
            fks = MetaDataUtil.getForeignKeys(tableName1, tableName2);
            QueryConstructor.OUT.log(Level.FINEST, "Relationships between tables {0} and {1} are {2}", new Object[] { tableName1, tableName2, fks });
        }
        catch (final MetaDataException mde) {
            final String mess = "Exception occurred while fetching relationship between the tables " + tableName1 + " and " + tableName2;
            QueryConstructor.OUT.log(Level.FINER, mess, mde);
            throw new DataAccessException(mess, mde);
        }
        if (pkBased) {
            fkDefn = getPKLinkedFK(fks);
        }
        else if (fks != null && fks.size() != 0) {
            if (fks.size() > 1) {
                QueryConstructor.OUT.log(Level.WARNING, "More than one foreign key found between tables {0} and {1}. SelectQuery can not be formed in such cases", new Object[] { tableName1, tableName2 });
            }
            fkDefn = fks.get(0);
        }
        return fkDefn;
    }
    
    public static void addJoin(final SelectQuery sq, final String tableName, final ForeignKeyDefinition fkDefn, final boolean isLeftJoin, final Map<String, ArchiveTable> arcTabMap) throws DataAccessException {
        sq.addJoin(getJoin(tableName, fkDefn, isLeftJoin, arcTabMap));
    }
    
    public static void addJoin(final SelectQuery sq, final String tableName, final ForeignKeyDefinition fkDefn, final boolean isLeftJoin) throws DataAccessException {
        sq.addJoin(getJoin(tableName, fkDefn, isLeftJoin, null));
    }
    
    private static Join getJoin(final String tableName, final ForeignKeyDefinition fkDefn, final boolean isLeftJoin, final Map<String, ArchiveTable> arcTabMap) throws DataAccessException {
        final boolean isMaster = fkDefn.getMasterTableName().equals(tableName);
        final String processedTabName = isMaster ? fkDefn.getSlaveTableName() : fkDefn.getMasterTableName();
        final List fkColDefns = fkDefn.getForeignKeyColumns();
        final int size = fkColDefns.size();
        final String[] fkColumns = new String[size];
        final String[] processedTableCols = new String[size];
        for (int i = 0; i < size; ++i) {
            final ForeignKeyColumnDefinition fkColumnDefn = fkColDefns.get(i);
            final String localCol = fkColumnDefn.getLocalColumnDefinition().getColumnName();
            final String refCol = fkColumnDefn.getReferencedColumnDefinition().getColumnName();
            if (isMaster) {
                fkColumns[i] = refCol;
                processedTableCols[i] = localCol;
            }
            else {
                fkColumns[i] = localCol;
                processedTableCols[i] = refCol;
            }
            QueryConstructor.OUT.log(Level.FINEST, "FK cols: ", fkColumns);
            QueryConstructor.OUT.log(Level.FINEST, "Processedtablecols: ", processedTableCols);
        }
        final int joinType = isLeftJoin ? 1 : 2;
        Table masterTable;
        Table slaveTable;
        if (arcTabMap != null) {
            if (arcTabMap.containsKey(processedTabName)) {
                masterTable = arcTabMap.get(processedTabName);
            }
            else {
                masterTable = Table.getTable(processedTabName);
            }
            if (arcTabMap.containsKey(tableName)) {
                slaveTable = arcTabMap.get(tableName);
            }
            else {
                slaveTable = Table.getTable(tableName);
            }
        }
        else {
            masterTable = Table.getTable(processedTabName);
            slaveTable = Table.getTable(tableName);
        }
        final Join join = new Join(masterTable, slaveTable, processedTableCols, fkColumns, joinType);
        return join;
    }
    
    public static ForeignKeyDefinition getPKLinkedFK(final List fkDefns) throws DataAccessException {
        if (fkDefns == null) {
            return null;
        }
        final int size = fkDefns.size();
        final ForeignKeyDefinition retFK = null;
        for (int i = 0; i < size; ++i) {
            final ForeignKeyDefinition fk = fkDefns.get(i);
            final boolean found = isPKLinkedFK(fk);
            if (found) {
                if (retFK != null) {}
                return fk;
            }
        }
        return retFK;
    }
    
    public static boolean isPKLinkedFK(final ForeignKeyDefinition fk) throws DataAccessException {
        boolean found = true;
        final String slave = fk.getSlaveTableName();
        final TableDefinition td = getTableDefinition(slave);
        final PrimaryKeyDefinition pkDef = td.getPrimaryKey();
        final List pkCols = pkDef.getColumnList();
        final List fkColDefns = fk.getForeignKeyColumns();
        for (int j = 0; j < fkColDefns.size(); ++j) {
            final ForeignKeyColumnDefinition fkColDefn = fkColDefns.get(j);
            final ColumnDefinition cd = fkColDefn.getLocalColumnDefinition();
            final String colName = cd.getColumnName();
            if (!pkCols.contains(colName)) {
                found = false;
                break;
            }
        }
        return found;
    }
    
    public static Criteria formCriteria(final List instances) throws DataAccessException {
        Criteria criteria = null;
        QueryConstructor.OUT.log(Level.FINEST, "Entering into formCriteria: {0}", instances);
        if (instances != null) {
            for (int size = instances.size(), i = 0; i < size; ++i) {
                final Row instance = instances.get(i);
                final Criteria thisCriteria = formCriteria(instance);
                if (criteria == null) {
                    criteria = thisCriteria;
                }
                else if (thisCriteria != null) {
                    criteria = criteria.or(thisCriteria);
                }
            }
            QueryConstructor.OUT.log(Level.FINEST, "Exiting formCriteria: {0}", criteria);
        }
        return criteria;
    }
    
    public static Criteria formCriteria(final Row instance) throws DataAccessException {
        if (instance == null) {
            return null;
        }
        final String tableName = instance.getOriginalTableName();
        final List columns = getPKColumns(tableName);
        Criteria criteria = null;
        boolean addDirtyColumnsInCriteria = false;
        for (int i = 0; i < columns.size(); ++i) {
            final String columnName = columns.get(i);
            final Object value = instance.get(columnName);
            if (value instanceof UniqueValueHolder || value == null) {
                addDirtyColumnsInCriteria = true;
            }
            else {
                final Criteria newCriteria = new Criteria(Column.getColumn(tableName, columnName), instance.get(columnName), 0);
                if (criteria == null) {
                    criteria = newCriteria;
                }
                else {
                    criteria = criteria.and(newCriteria);
                }
            }
        }
        if (addDirtyColumnsInCriteria) {
            final int[] dirtyCols = instance.getChangedColumnIndex();
            final int[] pkColIdx = instance.getKeyIndices();
            if (dirtyCols != null) {
                for (int dirtyColsLen = dirtyCols.length, j = 0; j < dirtyColsLen; ++j) {
                    if (Arrays.binarySearch(pkColIdx, dirtyCols[j]) < 0) {
                        final String columnName2 = instance.getColumns().get(dirtyCols[j] - 1);
                        final Criteria newCriteria2 = new Criteria(Column.getColumn(tableName, columnName2), instance.get(dirtyCols[j]), 0);
                        if (criteria == null) {
                            criteria = newCriteria2;
                        }
                        else {
                            criteria = criteria.and(newCriteria2);
                        }
                    }
                }
            }
        }
        if (criteria == null) {
            QueryConstructor.OUT.log(Level.SEVERE, "No values set in given Row instance :: {0}", instance);
            throw new DataAccessException("No values set in given Row instance");
        }
        return criteria;
    }
    
    public static Join getJoin(final ForeignKeyDefinition fkDef) {
        if (fkDef == null) {
            return null;
        }
        final String masterTable = fkDef.getMasterTableName();
        final String slaveTable = fkDef.getSlaveTableName();
        final List fkCols = fkDef.getForeignKeyColumns();
        final int size = fkCols.size();
        final String[] masterColumns = new String[size];
        final String[] slaveColumns = new String[size];
        for (int i = 0; i < size; ++i) {
            final ForeignKeyColumnDefinition fkColDef = fkCols.get(i);
            slaveColumns[i] = fkColDef.getLocalColumnDefinition().getColumnName();
            masterColumns[i] = fkColDef.getReferencedColumnDefinition().getColumnName();
        }
        return new Join(masterTable, slaveTable, masterColumns, slaveColumns, 2);
    }
    
    private static List getPKColumns(final String tableName) throws DataAccessException {
        List pkColumns = null;
        final TableDefinition td = getTableDefinition(tableName);
        final PrimaryKeyDefinition pkDef = td.getPrimaryKey();
        if (pkDef == null) {
            throw new DataAccessException("Primary Key not defined for " + tableName);
        }
        pkColumns = pkDef.getColumnList();
        return pkColumns;
    }
    
    public static TableDefinition getTableDefinition(final String tableName) throws DataAccessException {
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException mde) {
            final String mess = "Exception occurred while getting the definition for the table " + tableName;
            QueryConstructor.OUT.log(Level.FINEST, mess, mde);
            throw new DataAccessException(mde.getMessage(), mde);
        }
        if (td == null) {
            throw new DataAccessException("Unknown table specified " + tableName);
        }
        return td;
    }
    
    public static List getJoins(final List tableNames) throws DataAccessException {
        if (tableNames == null) {
            return null;
        }
        int initialSize = tableNames.size();
        final List joinsList = new ArrayList(initialSize - 1);
        final List clonedTableNames = new ArrayList(tableNames);
        final List processedTableNames = new ArrayList(initialSize);
        if (initialSize > 1) {
            processedTableNames.add(clonedTableNames.get(0));
            clonedTableNames.remove(0);
            for (int changedSize = clonedTableNames.size(); changedSize < initialSize && changedSize != 0; changedSize = clonedTableNames.size()) {
                initialSize = changedSize;
                final Iterator tableNameIterator = clonedTableNames.iterator();
                while (tableNameIterator.hasNext()) {
                    final String tableName = tableNameIterator.next();
                    ForeignKeyDefinition fkDefinition = getSuitableFK(processedTableNames, tableName, true);
                    if (fkDefinition == null) {
                        fkDefinition = getSuitableFK(processedTableNames, tableName, true);
                    }
                    if (fkDefinition == null) {
                        fkDefinition = getSuitableFK(processedTableNames, tableName, false);
                    }
                    if (fkDefinition != null) {
                        tableNameIterator.remove();
                        processedTableNames.add(tableName);
                        final Join join = getJoin(tableName, fkDefinition, false, null);
                        if (join == null) {
                            throw new DataAccessException("Exception thrown while creating a join between the table :: " + tableName + " using the ForeignKey Definition :: " + fkDefinition);
                        }
                        joinsList.add(join);
                    }
                }
            }
            if (clonedTableNames.size() > 0) {
                throw new DataAccessException("These tables " + clonedTableNames + " doesn't have any relation with the other tables " + processedTableNames);
            }
        }
        return joinsList;
    }
    
    static {
        CLASS_NAME = QueryConstructor.class.getName();
        OUT = Logger.getLogger(QueryConstructor.CLASS_NAME);
        (QueryConstructor.nullList = new ArrayList()).add(null);
        QueryConstructor.personalityCache = new MultiKeyMap();
    }
    
    static class MultiKeyMap
    {
        private ConcurrentHashMap<List, Object> map;
        
        MultiKeyMap() {
            this.map = new ConcurrentHashMap<List, Object>();
        }
        
        public void put(final Object value, final Object... key) {
            this.map.put(Arrays.asList(key), value);
        }
        
        public Object get(final Object... key) {
            return this.map.get(Arrays.asList(key));
        }
        
        public boolean containsKey(final Object... key) {
            return this.map.containsKey(Arrays.asList(key));
        }
    }
}
