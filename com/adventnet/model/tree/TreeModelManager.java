package com.adventnet.model.tree;

import com.adventnet.ds.query.Column;
import com.adventnet.tree.TreeManagerUtility;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import com.adventnet.persistence.Row;
import com.adventnet.tree.DefaultHierarchyNode;
import java.util.logging.Level;
import com.adventnet.tree.HierarchyNode;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.tree.query.TreeQuery;
import com.adventnet.tree.TreeException;
import java.util.logging.Logger;

public class TreeModelManager
{
    Logger logger;
    static TreeModelManager manager;
    
    protected TreeModelManager() throws Exception {
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public static TreeModelManager getInstance() throws TreeException {
        if (TreeModelManager.manager == null) {
            try {
                TreeModelManager.manager = new TreeModelManager();
            }
            catch (final Exception e) {
                throw new TreeException("cannot construct model manager ", e);
            }
        }
        return TreeModelManager.manager;
    }
    
    public DOTreeModel getModel(TreeQuery treeQuery) throws TreeException {
        if (!treeQuery.isCompiled()) {
            throw new TreeException("TreeQuery has not been compiled");
        }
        try {
            treeQuery = (TreeQuery)RelationalAPI.getInstance().getModifiedQuery((Query)treeQuery);
            Integer rootLevel = new Integer(-1);
            final String baseTableName = treeQuery.getTableList().get(0).getTableName();
            final WritableDataObject treeData = (WritableDataObject)DataAccess.get((SelectQuery)treeQuery);
            final List dataObjects = treeData.getDataObjects();
            HierarchyNode rootNode = null;
            final ArrayList descendantIDs = this.getDescendantIDs((DataObject)treeData, treeQuery);
            final ArrayList anscestorIDs = this.getAncestorIDs((DataObject)treeData, treeQuery);
            final HashMap nodeVsID = new HashMap();
            if (descendantIDs.size() == 0) {
                final DOTreeModel dom = new DOTreeModel(null, treeQuery, (DataObject)treeData);
                this.setColumns(dom, treeQuery);
                return dom;
            }
            if (!anscestorIDs.get(0).equals(new Long(-1L))) {
                final int index = descendantIDs.indexOf(anscestorIDs.get(0));
                if (index != -1) {
                    final Object desID = descendantIDs.remove(index);
                    final Object ansID = anscestorIDs.remove(index);
                    descendantIDs.add(0, desID);
                    anscestorIDs.add(0, ansID);
                }
            }
            for (int size = descendantIDs.size(), i = 0; i < size; ++i) {
                final Long descendantID = descendantIDs.get(i);
                final Long anscestorID = anscestorIDs.get(i);
                final DataObject doo = this.getDataObject(dataObjects, descendantID, baseTableName);
                this.logger.log(Level.FINER, "DataObject formed is  :{0}", doo);
                final HierarchyNode des = new DefaultHierarchyNode();
                des.setNodeID(descendantID);
                des.setDataObject(doo);
                nodeVsID.put(descendantID, des);
                Row keyRow = null;
                final Iterator iter = treeQuery.getTreeDefinition().getRows("TablesInTree");
                while (iter.hasNext()) {
                    final Row tableInTree = iter.next();
                    final String tableName = (String)tableInTree.get(3);
                    if (doo.containsTable(tableName)) {
                        keyRow = doo.getFirstRow(tableName);
                        break;
                    }
                }
                if (keyRow == null) {
                    throw new TreeException("KEY ROW CANNOT BE FOUND.... ");
                }
                des.setKey(keyRow);
                if (i == 0) {
                    rootNode = des;
                    rootLevel = (Integer)rootNode.getDataObject().getFirstRow("A1").get("NODELEVEL");
                }
                else {
                    final HierarchyNode parent = nodeVsID.get(anscestorID);
                    if (parent != null) {
                        parent.add(des);
                        des.setParentKey(parent.getKey());
                    }
                    else {
                        this.logger.log(Level.FINER, " parent node for child node : {0} does not exists", des);
                    }
                }
            }
            if (treeQuery.getStartingParentKey() == null) {
                if (rootLevel == 1) {
                    final DOTreeModel dom2 = new DOTreeModel(rootNode, treeQuery, (DataObject)treeData);
                    this.setColumns(dom2, treeQuery);
                    return dom2;
                }
                final DOTreeModel dom2 = new DOTreeModel(null, treeQuery, (DataObject)treeData);
                this.setColumns(dom2, treeQuery);
                return dom2;
            }
            else {
                if (this.comparePK(rootNode.getKey(), treeQuery.getStartingParentKey())) {
                    final DOTreeModel dom2 = new DOTreeModel(rootNode, treeQuery, (DataObject)treeData);
                    this.setColumns(dom2, treeQuery);
                    return dom2;
                }
                final DOTreeModel dom2 = new DOTreeModel(null, treeQuery, (DataObject)treeData);
                this.setColumns(dom2, treeQuery);
                return dom2;
            }
        }
        catch (final Exception e) {
            throw new TreeException(e);
        }
    }
    
    private void setColumns(final DOTreeModel dom, final TreeQuery treeQuery) throws Exception {
        final String[] tableNames = TreeManagerUtility.getTablesInTree(treeQuery.getTreeDefinition());
        final ArrayList columnNameList = new ArrayList();
        for (int i = 0; i < tableNames.length; ++i) {
            SelectQuery sq = treeQuery.getSelectQuery(tableNames[i]);
            sq = (SelectQuery)RelationalAPI.getInstance().getModifiedQuery((Query)sq);
            for (int j = 0; j < sq.getSelectColumns().size(); ++j) {
                final Column column = sq.getSelectColumns().get(j);
                columnNameList.add(sq.getSelectColumns().get(j));
            }
        }
        Column[] columns = new Column[columnNameList.size()];
        columns = columnNameList.toArray(columns);
        dom.setColumns(columns);
    }
    
    private ArrayList getDescendantIDs(final DataObject treeData, final TreeQuery tq) throws Exception {
        final ArrayList descendantIDs = new ArrayList();
        final Iterator iter = treeData.get(TreeManagerUtility.getBaseTreeNodeTable(tq.getTreeDefinition()), "NODEID");
        while (iter.hasNext()) {
            descendantIDs.add(iter.next());
        }
        return descendantIDs;
    }
    
    private ArrayList getAncestorIDs(final DataObject treeData, final TreeQuery tq) throws Exception {
        final ArrayList anscestorIDs = new ArrayList();
        final Iterator iter = treeData.get("A2", "ANCESTORID");
        while (iter.hasNext()) {
            anscestorIDs.add(iter.next());
        }
        return anscestorIDs;
    }
    
    DataObject getDataObject(final List dataObjects, final Long descendantID, final String baseTableName) throws Exception {
        for (int i = 0; i < dataObjects.size(); ++i) {
            final DataObject doo = dataObjects.get(i);
            if (doo.getFirstRow(baseTableName).get("NODEID").equals(descendantID)) {
                return doo;
            }
        }
        return null;
    }
    
    boolean comparePK(final Row key1, final Row key2) {
        if (!key1.getTableName().equals(key2.getTableName())) {
            return false;
        }
        for (int i = 0; i < key1.getPKColumns().size(); ++i) {
            final String columnName = key1.getPKColumns().get(i);
            if (!key1.get(columnName).equals(key2.get(columnName))) {
                return false;
            }
        }
        return true;
    }
}
