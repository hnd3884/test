package com.adventnet.tree;

import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.List;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import com.adventnet.ds.query.DataSet;
import java.util.logging.Logger;

public class TreeManagerUtility
{
    static Logger logger;
    
    public static void safeClose(final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void safeClose(final ResultSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void safeClose(final Statement ds) {
        try {
            if (ds != null) {
                ds.close();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void safeClose(final Connection conn) {
        try {
            conn.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Criteria getTreeIdentifierAsCriteria(final DataObject tdef, final Row treeIdentifier) throws QueryConstructionException, DataAccessException {
        Criteria criteria = null;
        final String[] treeIdentifierColumnNames = getTreeIdentifierColumns(tdef);
        final String baseTreeNodeTable = getBaseTreeNodeTable(tdef);
        for (final String columnName : treeIdentifierColumnNames) {
            final Object value = treeIdentifier.get(columnName);
            if (criteria == null) {
                criteria = new Criteria(Column.getColumn(baseTreeNodeTable, columnName), value, 0);
            }
            else {
                criteria = criteria.and(Column.getColumn(baseTreeNodeTable, columnName), value, 0);
            }
        }
        return criteria;
    }
    
    public static Criteria getStartingParentAsCriteria(final String parentTableName, final String parentTreeNodeTableName, final Row startingParentKey) throws QueryConstructionException, MetaDataException {
        final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(parentTableName);
        final PrimaryKeyDefinition pk = tableDefinition.getPrimaryKey();
        final List columnNames = startingParentKey.getPKColumns();
        Criteria criteria = null;
        for (int i = 0; i < columnNames.size(); ++i) {
            final String columnName = columnNames.get(i);
            final Object obj = startingParentKey.get(columnName);
            if (criteria == null) {
                criteria = new Criteria(Column.getColumn(parentTreeNodeTableName, columnName), obj, 0);
            }
            else {
                criteria = criteria.and(Column.getColumn(parentTreeNodeTableName, columnName), obj, 0);
            }
        }
        return criteria;
    }
    
    public static Criteria getStartingParentAsCriteria(final Row startingParentKey, final DataObject tdef) throws QueryConstructionException, DataAccessException {
        final String parentTableName = startingParentKey.getTableName();
        final String parentTreeNodeTableName = getTreeNodeTable(tdef, parentTableName);
        final List columnNames = startingParentKey.getPKColumns();
        Criteria criteria = null;
        for (int i = 0; i < columnNames.size(); ++i) {
            final String columnName = columnNames.get(i);
            final Object obj = startingParentKey.get(columnName);
            if (criteria == null) {
                criteria = new Criteria(Column.getColumn(parentTreeNodeTableName, columnName), obj, 0);
            }
            else {
                criteria = criteria.and(Column.getColumn(parentTreeNodeTableName, columnName), obj, 0);
            }
        }
        return criteria;
    }
    
    public static Criteria getPreparedStatementCriteria(final String treeNodeTable, final String associatedTable) throws MetaDataException {
        final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(associatedTable);
        final PrimaryKeyDefinition pk = tableDefinition.getPrimaryKey();
        final List columnNames = pk.getColumnList();
        Criteria criteria = null;
        for (int i = 0; i < columnNames.size(); ++i) {
            final String columnName = columnNames.get(i);
            if (criteria == null) {
                criteria = new Criteria(Column.getColumn(treeNodeTable, columnName), (Object)QueryConstants.PREPARED_STMT_CONST, 0);
            }
            else {
                criteria = criteria.and(Column.getColumn(treeNodeTable, columnName), (Object)QueryConstants.PREPARED_STMT_CONST, 0);
            }
        }
        return criteria;
    }
    
    public static String[] getKeyColumns(final String tableName) throws QueryConstructionException, MetaDataException {
        final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
        final PrimaryKeyDefinition pkd = tableDefinition.getPrimaryKey();
        final List columnNames = pkd.getColumnList();
        final String[] keyColumns = new String[columnNames.size()];
        return columnNames.toArray(keyColumns);
    }
    
    public static boolean compare(final Row startingParent1, final Row startingParent2) throws QueryConstructionException, MetaDataException {
        if (!startingParent2.getTableName().equals(startingParent1.getTableName())) {
            return false;
        }
        final String parentTableName = startingParent1.getTableName();
        final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(parentTableName);
        final PrimaryKeyDefinition pk = tableDefinition.getPrimaryKey();
        final List columnNames = pk.getColumnList();
        for (int i = 0; i < columnNames.size(); ++i) {
            final String columnName = columnNames.get(i);
            final Object obj1 = startingParent1.get(columnName);
            final Object obj2 = startingParent2.get(columnName);
            if (!obj1.equals(obj2)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean compare(final DataObject tdef, final Row treeId1, final Row treeId2) throws QueryConstructionException, DataAccessException {
        if (!treeId2.getTableName().equals(treeId1.getTableName())) {
            TreeManagerUtility.logger.severe("######## TreeidentifierClass did not match this should not occur");
            return false;
        }
        final String[] treeIdentifierColumnNames = getTreeIdentifierColumns(tdef);
        final String baseTreeNodeTable = getBaseTreeNodeTable(tdef);
        for (final String columnName : treeIdentifierColumnNames) {
            final Object value1 = treeId1.get(columnName);
            final Object value2 = treeId2.get(columnName);
            if (!value2.equals(value1)) {
                return false;
            }
        }
        return true;
    }
    
    public static String[] getTreeIdentifierColumns(final DataObject tdef) throws DataAccessException {
        final ArrayList v = new ArrayList();
        final Iterator iter = tdef.getRows("TreeIdentifierColumns");
        while (iter.hasNext()) {
            final Row row = iter.next();
            v.add(row.get(2));
        }
        final String[] treeIdentifierColumnNames = new String[v.size()];
        return v.toArray(treeIdentifierColumnNames);
    }
    
    static Object getFromBase(final DataObject tdef, final String columnName) throws DataAccessException {
        final Row row = tdef.getFirstRow("TreeDefinition");
        return row.get(columnName);
    }
    
    public static String getBaseTreeNodeTable(final DataObject tdef) throws DataAccessException {
        return (String)getFromBase(tdef, "BASETREENODETABLE");
    }
    
    public static String getTreeInfoTable(final DataObject tdef) throws DataAccessException {
        return (String)getFromBase(tdef, "TREEINFOTABLE");
    }
    
    public static String getTreeType(final DataObject tdef) throws DataAccessException {
        return (String)getFromBase(tdef, "TREETYPE");
    }
    
    public static String getTreeNodeTable(final DataObject tdef, final String tableName) throws DataAccessException {
        final Row condition = new Row("TablesInTree");
        condition.set(3, (Object)tableName);
        final Iterator iter = tdef.getRows("TablesInTree");
        while (iter.hasNext()) {
            final Row row = iter.next();
            if (row.get(3).equals(tableName)) {
                return (String)row.get(2);
            }
        }
        return null;
    }
    
    public static boolean getSiblingOrdered(final DataObject tdef) throws DataAccessException {
        return (boolean)getFromBase(tdef, "SIBLINGORDERED");
    }
    
    public static DataObject getTreeDefinition(final String treeType) throws TreeException {
        try {
            final Persistence persistence = (Persistence)BeanUtil.lookup("Persistence");
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("TreeDefinition"));
            sq.addJoin(new Join(Table.getTable("TreeDefinition"), Table.getTable("TablesInTree"), new String[] { "TREEID" }, new String[] { "TREEID" }, 2));
            sq.addJoin(new Join(Table.getTable("TreeDefinition"), Table.getTable("TreeIdentifierColumns"), new String[] { "TREEID" }, new String[] { "TREEID" }, 2));
            final Criteria condition = new Criteria(Column.getColumn("TreeDefinition", "TREETYPE"), (Object)treeType, 0);
            sq.addSelectColumn(Column.getColumn("TreeDefinition", "*"));
            sq.addSelectColumn(Column.getColumn("TablesInTree", "*"));
            sq.addSelectColumn(Column.getColumn("TreeIdentifierColumns", "*"));
            sq.setCriteria(condition);
            final DataObject tdef = persistence.get(sq);
            TreeManagerUtility.logger.log(Level.FINEST, "Tree definition got is {0}", tdef);
            return tdef;
        }
        catch (final Exception e) {
            throw new TreeException(e);
        }
    }
    
    public static String[] getTablesInTree(final DataObject tdef) throws DataAccessException {
        final Row condition = new Row("TablesInTree");
        final ArrayList list = new ArrayList();
        final Iterator iter = tdef.getRows("TablesInTree");
        while (iter.hasNext()) {
            final Row row = iter.next();
            list.add(row.get(3));
        }
        String[] tableNames = new String[list.size()];
        tableNames = list.toArray(tableNames);
        return tableNames;
    }
    
    static {
        TreeManagerUtility.logger = Logger.getLogger(TreeManagerUtility.class.getName());
    }
}
