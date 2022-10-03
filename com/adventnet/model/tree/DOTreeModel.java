package com.adventnet.model.tree;

import com.adventnet.beans.rangenavigator.events.NavigationListener;
import com.adventnet.beans.xtable.ModelException;
import com.adventnet.beans.treetable.TreeTableModel;
import java.util.Iterator;
import java.util.Vector;
import com.adventnet.tree.TreeException;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.ArrayList;
import com.adventnet.tree.TreeManagerUtility;
import javax.swing.tree.TreeNode;
import com.adventnet.tree.HierarchyNode;
import com.adventnet.beans.xtable.SortColumn;
import com.adventnet.persistence.DataObject;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.customview.CustomViewManager;
import com.adventnet.ds.query.Column;
import com.adventnet.tree.query.TreeQuery;
import com.adventnet.idioms.treetablenavigator.TreeTableNavigatorModel;
import com.adventnet.customview.CustomViewManagerUser;
import com.adventnet.model.Model;
import javax.swing.tree.DefaultTreeModel;

public class DOTreeModel extends DefaultTreeModel implements Model, Cloneable, CustomViewManagerUser, TreeTableNavigatorModel
{
    protected TreeQuery query;
    private int columnCount;
    private Column[] columns;
    CustomViewManager customViewManager;
    CustomViewRequest customViewRequest;
    private transient DataObject fullDataObject;
    SortColumn[] viewSortedColumns;
    SortColumn[] modelSortedColumns;
    long endIndex;
    long pageLength;
    long startIndex;
    long totalRecordCount;
    
    public DOTreeModel(final HierarchyNode rootNode, final TreeQuery treeQuery, final DataObject fullDataObject) throws Exception {
        super(rootNode);
        this.startIndex = 1L;
        this.totalRecordCount = 1L;
        this.query = treeQuery;
        if (this.root != null) {
            final long n = this.root.getChildCount();
            this.pageLength = n;
            this.endIndex = n;
            if (this.pageLength == 0L) {
                this.pageLength = 5L;
            }
        }
        this.fullDataObject = fullDataObject;
    }
    
    public DataObject getDataObjectForAllNodes() {
        return this.fullDataObject;
    }
    
    public void setColumns(final Column[] columns) {
        this.columns = columns;
        this.columnCount = columns.length;
    }
    
    public Column[] getColumns() {
        return this.columns;
    }
    
    private void init() throws Exception {
        final String[] tableNames = TreeManagerUtility.getTablesInTree(this.query.getTreeDefinition());
        final ArrayList columnNameList = new ArrayList();
        columnNameList.add(new Column("DUMMY", ""));
        for (int i = 0; i < tableNames.length; ++i) {
            final SelectQuery sq = this.query.getSelectQuery(tableNames[i]);
            for (int j = 0; j < sq.getSelectColumns().size(); ++j) {
                final Column column = sq.getSelectColumns().get(j);
                if (column.getColumnAlias().equals("*")) {
                    final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(this.getTableName(column.getTableAlias(), sq));
                    final List columnList = tableDef.getColumnList();
                    for (int k = 0; k < columnList.size(); ++k) {
                        final ColumnDefinition clDef = columnList.get(k);
                        columnNameList.add(Column.getColumn(clDef.getTableName(), clDef.getColumnName()));
                    }
                }
                else {
                    columnNameList.add(sq.getSelectColumns().get(j));
                }
            }
        }
        this.columns = new Column[columnNameList.size()];
        this.columns = columnNameList.toArray(this.columns);
        this.columnCount = this.columns.length;
    }
    
    private String getTableName(final String tableAlias, final SelectQuery sq) throws TreeException {
        for (int i = 0; i < sq.getTableList().size(); ++i) {
            final Table table = sq.getTableList().get(i);
            if (table.getTableAlias().equals(tableAlias)) {
                return table.getTableName();
            }
        }
        throw new TreeException("Invalid Table Alias " + tableAlias);
    }
    
    public void fireTreeStructureChanged(final Object source, final Object[] path, final int[] childIndices, final Object[] children) {
        super.fireTreeStructureChanged(source, path, childIndices, children);
    }
    
    public void fireTreeNodesChanged(final Object source, final Object[] path, final int[] childIndices, final Object[] children) {
        super.fireTreeNodesChanged(source, path, childIndices, children);
    }
    
    public void fireTreeNodesInserted(final Object source, final Object[] path, final int[] childIndices, final Object[] children) {
        super.fireTreeNodesInserted(source, path, childIndices, children);
    }
    
    public void fireTreeNodesRemoved(final Object source, final Object[] path, final int[] childIndices, final Object[] children) {
        super.fireTreeNodesRemoved(source, path, childIndices, children);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final DOTreeModel obj = (DOTreeModel)super.clone();
        obj.query = (TreeQuery)this.query.clone();
        return obj;
    }
    
    public String toString() {
        return " " + super.toString() + "\n" + TreeModelData.getString((HierarchyNode)this.getRoot());
    }
    
    public Object getValueAt(final Object node, final int column) {
        try {
            final DataObject doo = ((HierarchyNode)node).getDataObject();
            final Vector values = new Vector();
            final Column columnObject = this.columns[column];
            final Iterator iter = doo.get(columnObject.getTableAlias(), columnObject.getColumnName());
            while (iter.hasNext()) {
                values.add(iter.next());
            }
            if (values.size() == 0) {
                return null;
            }
            if (values.size() == 1) {
                return values.get(0);
            }
            return values;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getColumnName(final int column) {
        if (this.columns[column].getColumnAlias() != null) {
            return this.columns[column].getColumnAlias();
        }
        return this.columns[column].getColumnName();
    }
    
    public int getColumnCount() {
        return this.columnCount;
    }
    
    public Class getColumnClass(final int column) {
        if (column == 0) {
            return TreeTableModel.class;
        }
        return String.class;
    }
    
    public boolean isCellEditable(final Object node, final int column) {
        return column == 0;
    }
    
    public void setValueAt(final Object aValue, final Object node, final int column) {
        throw new UnsupportedOperationException("Modification is not supported");
    }
    
    public void sortView(final Object object, final SortColumn[] sortColumns) throws ModelException {
        this.viewSortedColumns = sortColumns;
    }
    
    public SortColumn[] getViewSortedColumns() {
        return this.viewSortedColumns;
    }
    
    public void sortModel(final Object object, final SortColumn[] sortColumns) throws ModelException {
        this.modelSortedColumns = sortColumns;
    }
    
    public SortColumn[] getModelSortedColumns() {
        return this.modelSortedColumns;
    }
    
    public void addNavigationListener(final NavigationListener listener) {
    }
    
    public long getEndIndex() {
        return this.endIndex;
    }
    
    public long getPageLength() {
        return this.pageLength;
    }
    
    public long getStartIndex() {
        return this.startIndex;
    }
    
    public long getTotalRecordsCount() {
        return this.totalRecordCount;
    }
    
    public void removeNavigationListener(final NavigationListener listener) {
    }
    
    public void setPageLength(final long length) {
        this.pageLength = length;
    }
    
    public void showRange(final long from, final long to) {
    }
    
    @Override
    public void setCustomViewManager(final CustomViewManager customViewManager) {
        this.customViewManager = customViewManager;
    }
    
    @Override
    public void setCustomViewRequest(final CustomViewRequest customViewRequest) {
        this.customViewRequest = customViewRequest;
    }
}
