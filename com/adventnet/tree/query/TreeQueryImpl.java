package com.adventnet.tree.query;

import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Properties;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.tree.TreeManagerUtility;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SortColumn;
import java.util.Collection;
import java.util.List;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;

public class TreeQueryImpl extends SelectQueryImpl implements TreeQuery
{
    private String treeType;
    private Row treeIdentifier;
    private int depth;
    private Row startingParentKey;
    private HashMap selectQueries;
    private ArrayList sortColumns;
    private DataObject tdef;
    private boolean compiled;
    
    public TreeQueryImpl(final String treeType, final Row treeIdentifier, final Row startingParentKey, final int depth) {
        this.selectQueries = new HashMap();
        this.sortColumns = new ArrayList();
        this.compiled = false;
        this.treeType = treeType;
        this.treeIdentifier = treeIdentifier;
        this.depth = depth;
        this.startingParentKey = startingParentKey;
    }
    
    public void addSelectColumn(final Column column) {
        throw new IllegalArgumentException("Criteria are not alowed ");
    }
    
    public void addSelectColumns(final List columns) {
        throw new IllegalArgumentException("Criteria are not alowed ");
    }
    
    public void addSortColumns(final List columns) {
        this.sortColumns.addAll(columns);
    }
    
    public void addSortColumn(final SortColumn column) {
        this.sortColumns.add(column);
    }
    
    public void addJoin(final Join join) {
        throw new IllegalArgumentException("Criteria are not alowed ");
    }
    
    public void setSelectQuery(final SelectQuery selectQuery) {
        final Table table = selectQuery.getTableList().get(0);
        this.selectQueries.put(table.getTableName(), selectQuery);
    }
    
    public SelectQuery getSelectQuery(final String tableName) {
        SelectQuery sq = this.selectQueries.get(tableName);
        if (sq == null) {
            sq = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
            sq.addSelectColumn(Column.getColumn(tableName, "*"));
        }
        return sq;
    }
    
    public void compile(final DataObject tdef) throws QueryConstructionException, MetaDataException, DataAccessException {
        if (this.compiled) {
            return;
        }
        this.compiled = true;
        this.tdef = tdef;
        final List filteredTables = this.formFilteredTables(tdef);
        final String baseTreeNodeTable = TreeManagerUtility.getBaseTreeNodeTable(tdef);
        this.setBaseTable(Table.getTable(baseTreeNodeTable));
        try {
            String parentTableName = null;
            String parentTreeNodeTableName = null;
            if (this.startingParentKey != null) {
                parentTableName = this.startingParentKey.getTableName();
                parentTreeNodeTableName = TreeManagerUtility.getTreeNodeTable(tdef, parentTableName);
            }
            super.addJoin(new Join(Table.getTable(baseTreeNodeTable), Table.getTable(TreeManagerUtility.getTreeInfoTable(tdef), "A1"), new String[] { "NODEID" }, new String[] { "DESCENDANTID" }, 2));
            super.addJoin(new Join(Table.getTable(TreeManagerUtility.getTreeInfoTable(tdef), "A1"), Table.getTable(TreeManagerUtility.getTreeInfoTable(tdef), "A2"), new String[] { "DESCENDANTID" }, new String[] { "DESCENDANTID" }, 2));
            if (this.startingParentKey != null) {
                super.addJoin(new Join(Table.getTable(TreeManagerUtility.getTreeInfoTable(tdef), "A1"), Table.getTable(parentTreeNodeTableName, "STP"), new String[] { "ANCESTORID" }, new String[] { "NODEID" }, 1));
            }
        }
        catch (final Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Exception while compiling TreeQuery: ", e);
            throw new QueryConstructionException("Exception while compiling TreeQuery: ", (Throwable)e);
        }
        int size = 0;
        if (filteredTables != null) {
            size = filteredTables.size();
        }
        for (int i = 0; i < size; ++i) {
            final String tableName = filteredTables.get(i);
            final String treeNodeTable = TreeManagerUtility.getTreeNodeTable(tdef, tableName);
            final Join baseVsTreeNodeJoin = new Join(Table.getTable(baseTreeNodeTable), Table.getTable(treeNodeTable), new String[] { "NODEID" }, new String[] { "NODEID" }, 1);
            super.addJoin(baseVsTreeNodeJoin);
            super.addSelectColumn(Column.getColumn(treeNodeTable, "*"));
            final String[] keyColumns = TreeManagerUtility.getKeyColumns(tableName);
            final Join treeNodeVsTable = new Join(Table.getTable(treeNodeTable), Table.getTable(tableName), keyColumns, keyColumns, 1);
            super.addJoin(treeNodeVsTable);
        }
        super.addSelectColumn(Column.getColumn(baseTreeNodeTable, "NODEID"));
        super.addSelectColumn(Column.getColumn("A2", "*"));
        super.addSelectColumn(Column.getColumn("A1", "*"));
        super.addSortColumn(new SortColumn("A1", "NODELEVEL", true));
        if (this.sortColumns.size() > 0) {
            super.addSortColumns((List)this.sortColumns);
        }
        else if (TreeManagerUtility.getSiblingOrdered(tdef)) {
            super.addSortColumn(new SortColumn(TreeManagerUtility.getBaseTreeNodeTable(tdef), "NODEINDEX", true));
        }
        Criteria actualCriteria = null;
        int j = 0;
        final Iterator iter = tdef.getRows("TablesInTree");
        while (iter.hasNext()) {
            final Row tableInTree = iter.next();
            final String tableName2 = (String)tableInTree.get(3);
            final SelectQuery sq = this.getSelectQuery(tableName2);
            final SelectQuery modifiedSelectQuery = this.modifySelectQuery((SelectQuery)sq.clone(), tableName2, ++j);
            final Criteria sqCriteria = modifiedSelectQuery.getCriteria();
            super.addSelectColumns(modifiedSelectQuery.getSelectColumns());
            for (int k = 0; k < modifiedSelectQuery.getJoins().size(); ++k) {
                super.addJoin((Join)modifiedSelectQuery.getJoins().get(k));
            }
            if (actualCriteria == null) {
                actualCriteria = sqCriteria;
            }
            else {
                actualCriteria = actualCriteria.or(sqCriteria);
            }
        }
        if (actualCriteria == null) {
            actualCriteria = this.getTreeCriteria();
        }
        else {
            actualCriteria = actualCriteria.and(this.getTreeCriteria());
        }
        super.setCriteria(actualCriteria);
    }
    
    private Criteria getTreeCriteria() throws QueryConstructionException, MetaDataException, DataAccessException {
        Criteria criteria = TreeManagerUtility.getTreeIdentifierAsCriteria(this.tdef, this.treeIdentifier);
        criteria = criteria.and(Column.getColumn("A2", "NODELEVEL"), (Object)new Integer(1), 0);
        final TableDefinition tableDefinition = null;
        if (this.startingParentKey != null) {
            final String tableName = this.startingParentKey.getTableName();
            Criteria first = TreeManagerUtility.getStartingParentAsCriteria(tableName, "STP", this.startingParentKey);
            final String[] keyColumns = TreeManagerUtility.getKeyColumns(tableName);
            Criteria second = new Criteria(Column.getColumn("STP", keyColumns[0]), (Object)null, 0);
            for (int i = 0; i < keyColumns.length; ++i) {
                second = second.and(new Criteria(Column.getColumn(TreeManagerUtility.getTreeNodeTable(this.tdef, tableName), keyColumns[i]), this.startingParentKey.get(keyColumns[i]), 0));
            }
            first = first.or(second);
            criteria = criteria.and(first);
        }
        else {
            criteria = criteria.and(Column.getColumn("A1", "ANCESTORID"), (Object)new Integer(-1), 0);
        }
        return criteria;
    }
    
    private SelectQuery modifySelectQuery(final SelectQuery sq, final String tableName, final int i) throws QueryConstructionException, MetaDataException {
        SelectQuery modifiedSelectQuery = null;
        final List tableList = sq.getTableList();
        final Table baseTable = tableList.get(0);
        modifiedSelectQuery = (SelectQuery)new SelectQueryImpl(baseTable);
        final Properties prop = new Properties();
        for (int j = 0; j < sq.getJoins().size(); ++j) {
            final Join join = sq.getJoins().get(j);
            final int num = join.getNumberOfColumns();
            final String[] baseColumns = new String[num];
            final String[] refColumns = new String[num];
            for (int k = 0; k < num; ++k) {
                baseColumns[k] = join.getBaseTableColumn(k);
                refColumns[k] = join.getReferencedTableColumn(k);
            }
            final String baseTableAlias = join.getBaseTableAlias();
            final String referencedTableAlias = join.getReferencedTableAlias();
            modifiedSelectQuery.addJoin(new Join(Table.getTable(join.getBaseTableName(), baseTableAlias), Table.getTable(join.getReferencedTableName(), referencedTableAlias), baseColumns, refColumns, 1));
            if (join.getJoinType() == 2) {
                final String pkColumn1 = TreeManagerUtility.getKeyColumns(join.getReferencedTableName())[0];
                Criteria pkColumn1Criteria = new Criteria(Column.getColumn(referencedTableAlias, pkColumn1), (Object)null, 1);
                if (sq.getCriteria() == null) {
                    sq.setCriteria(pkColumn1Criteria);
                }
                else {
                    pkColumn1Criteria = pkColumn1Criteria.and(sq.getCriteria());
                    sq.setCriteria(pkColumn1Criteria);
                }
            }
        }
        Criteria cr = sq.getCriteria();
        if (cr == null) {
            cr = this.getDummyCriteria(tableName);
        }
        modifiedSelectQuery.setCriteria(cr);
        if (sq.getSelectColumns().contains(Column.getColumn((String)null, "*"))) {
            for (int l = 0; l < sq.getTableList().size(); ++l) {
                final String tableAlias = sq.getTableList().get(l).getTableAlias();
                modifiedSelectQuery.addSelectColumn(Column.getColumn(tableAlias, "*"));
            }
        }
        else {
            modifiedSelectQuery.addSelectColumns(sq.getSelectColumns());
        }
        return modifiedSelectQuery;
    }
    
    private Criteria getDummyCriteria(final String tableName) throws QueryConstructionException, MetaDataException {
        Criteria cr = null;
        final String pkColumn1 = TreeManagerUtility.getKeyColumns(tableName)[0];
        final ColumnDefinition cdef = MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(pkColumn1);
        if (cdef.getDataType().equals("INTEGER") || cdef.getDataType().equals("BIGINT")) {
            cr = new Criteria(Column.getColumn(tableName, pkColumn1), (Object)new Integer(-10), 5);
        }
        else {
            cr = new Criteria(Column.getColumn(tableName, pkColumn1), (Object)"*", 2);
        }
        return cr;
    }
    
    public boolean isCompiled() {
        return this.compiled;
    }
    
    public Row getTreeIdentifier() {
        return this.treeIdentifier;
    }
    
    public DataObject getTreeDefinition() {
        return this.tdef;
    }
    
    public void setTreeDefinition(final DataObject tdef) {
        this.tdef = tdef;
    }
    
    public Row getStartingParentKey() {
        return this.startingParentKey;
    }
    
    public TreeQuery getTreeQuery(final Row startingParentKey1) {
        final TreeQueryImpl tq = new TreeQueryImpl(this.treeType, this.treeIdentifier, startingParentKey1, this.depth);
        tq.selectQueries = cloneHashMap(this.selectQueries);
        tq.sortColumns = (ArrayList)this.sortColumns.clone();
        tq.compiled = false;
        return tq;
    }
    
    public synchronized Object clone() {
        final TreeQueryImpl tqi = (TreeQueryImpl)super.clone();
        tqi.treeType = this.treeType;
        tqi.treeIdentifier = this.treeIdentifier;
        tqi.startingParentKey = this.startingParentKey;
        tqi.selectQueries = cloneHashMap(this.selectQueries);
        tqi.sortColumns = (ArrayList)this.sortColumns.clone();
        return tqi;
    }
    
    private static HashMap cloneHashMap(final HashMap selectQueries) {
        final HashMap map = new HashMap(selectQueries.size());
        for (final String key : selectQueries.keySet()) {
            final SelectQuery sq = (SelectQuery)selectQueries.get(key).clone();
            map.put(key, sq);
        }
        return map;
    }
    
    ArrayList formFilteredTables(final DataObject tdef) throws DataAccessException {
        final ArrayList ff = new ArrayList();
        final Iterator iter = tdef.getRows("TablesInTree");
        while (iter.hasNext()) {
            final Row tableInTree = iter.next();
            final String tableName = (String)tableInTree.get("TABLENAME");
            ff.add(tableName);
        }
        return ff;
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        if (this.isCompiled()) {
            buf.append("\n<TREEQUERY>");
            buf.append("\n  <COMPILED>" + this.isCompiled() + "</COMPILED>");
            buf.append("\n  " + super.toString().replaceAll("\n", "\n    "));
            buf.append("\n  " + this.selectQueries.toString().replaceAll("\n", "\n    "));
            buf.append("\n</TREEQUERY>");
            return buf.toString();
        }
        buf.append("\n<TREEQUERY>");
        buf.append("\n  <COMPILED>" + this.isCompiled() + "</COMPILED>");
        if (this.startingParentKey != null) {
            buf.append("\n  <STARTING PARENT>");
            buf.append("\n    " + this.startingParentKey.toString().replaceAll("\n", "\n    "));
            buf.append("\n  </STARTING PARENT>");
        }
        else {
            buf.append("\n  <STARTING PARENT>null</STARTING PARENT>");
        }
        buf.append("\n  <TREE IDENTIFIER>");
        buf.append("\n    " + this.treeIdentifier.toString().replaceAll("\n", "\n    "));
        buf.append("  </TREE IDENTIFIER>");
        buf.append("\n  " + this.selectQueries.toString().replaceAll("\n", "\n    "));
        buf.append("\n</TREEQUERY>");
        return buf.toString();
    }
    
    public String getTreeType() {
        return this.treeType;
    }
    
    public HashMap getSelectQueries() {
        return this.selectQueries;
    }
    
    public int getDepth() {
        return this.depth;
    }
    
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!(object instanceof TreeQueryImpl)) {
            return false;
        }
        final TreeQueryImpl passedSQ = (TreeQueryImpl)object;
        return super.equals((Object)passedSQ) && (this.treeType.equals(passedSQ.treeType) && this.equals(this.treeIdentifier, passedSQ.treeIdentifier) && this.depth == passedSQ.depth && this.equals(this.startingParentKey, passedSQ.startingParentKey) && this.selectQueries.equals(passedSQ.selectQueries) && this.sortColumns.equals(passedSQ.sortColumns) && this.equals(this.tdef, passedSQ.tdef) && this.compiled == passedSQ.compiled);
    }
    
    private boolean equals(final Object o1, final Object o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }
}
