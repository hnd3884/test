package com.adventnet.tree.manager;

import java.util.Collections;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.internal.SequenceGeneratorRepository;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.logging.Level;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Query;
import java.util.Map;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import javax.swing.tree.MutableTreeNode;
import com.adventnet.tree.DefaultHierarchyNode;
import java.util.Iterator;
import com.adventnet.model.tree.DOTreeModel;
import com.adventnet.tree.query.TreeQueryImpl;
import com.adventnet.model.tree.TreeModelManager;
import com.adventnet.tree.query.TreeQuery;
import com.adventnet.ds.query.DeleteQuery;
import java.sql.ResultSet;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.tree.OrderedHierarchyNode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import com.adventnet.persistence.DataAccessException;
import java.sql.SQLException;
import com.adventnet.persistence.DataAccess;
import java.util.ArrayList;
import com.adventnet.tree.HierarchyNode;
import com.adventnet.persistence.Row;
import com.adventnet.tree.TreeException;
import com.adventnet.tree.TreeManagerUtility;
import com.adventnet.db.persistence.SequenceGenerator;
import java.util.logging.Logger;
import java.util.HashMap;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.DataObject;

public class TreeManagerImpl
{
    private DataObject tdef;
    private RelationalAPI relAPI;
    private long nodeID;
    private HashMap allAncestores;
    private HashMap findIndexes;
    private HashMap deleteSelectQueries;
    private Logger logger;
    String treeType;
    TreeNotificationHandler treeNotificationHandler;
    private SequenceGenerator seqgen;
    
    public TreeManagerImpl(final DataObject tdef) throws TreeException {
        this.allAncestores = new HashMap();
        this.findIndexes = new HashMap();
        this.deleteSelectQueries = new HashMap();
        this.logger = Logger.getLogger(this.getClass().getName());
        this.treeNotificationHandler = null;
        this.seqgen = null;
        this.relAPI = RelationalAPI.getInstance();
        this.tdef = tdef;
        System.out.println("Tdef" + tdef);
        try {
            this.formQueries();
            this.treeNotificationHandler = new TreeNotificationHandler();
            this.treeType = TreeManagerUtility.getTreeType(tdef);
        }
        catch (final Exception e) {
            throw new TreeException("Exception while initializing Queries and Method mappings for tree :" + this.treeType, e);
        }
    }
    
    public HierarchyNode addNode(final Row treeIdentifier, final HierarchyNode hierarchyNode) throws TreeException {
        return this.addNode(treeIdentifier, hierarchyNode, true);
    }
    
    private HierarchyNode addNode(final Row treeIdentifier, final HierarchyNode hierarchyNode, final boolean sendNotification) throws TreeException {
        if (hierarchyNode.getKey() == null || treeIdentifier == null) {
            throw new TreeException("treeIdentifier or key is null", new IllegalArgumentException("TreeIdentifier or key is null"));
        }
        ArrayList pathV = new ArrayList();
        final long[] path = null;
        long parentNodeID = -1L;
        PreparedStatement ansprep = null;
        Connection connection = null;
        try {
            final OrderedHierarchyNode orderedHierarchyNode = null;
            connection = this.relAPI.getConnection();
            ansprep = this.getAllAncestorsPrep(treeIdentifier, hierarchyNode, connection);
            final ArrayList ancestorids = new ArrayList();
            if (hierarchyNode.getParentKey() != null) {
                parentNodeID = this.fillTreeInfo(ancestorids, ansprep);
                pathV = (ArrayList)ancestorids.clone();
                if (parentNodeID == -1L) {
                    throw new TreeException("No parent node exist for the node " + hierarchyNode + " specified parent is : " + hierarchyNode.getParentKey());
                }
            }
            int dbindex = -1;
            if (TreeManagerUtility.getSiblingOrdered(this.tdef)) {
                dbindex = this.findIndex(hierarchyNode.getParentKey(), treeIdentifier, connection);
            }
            final DataObject fullDataObject = DataAccess.constructDataObject();
            this.addCachedNode(parentNodeID, ancestorids, this.generateNodeID(), hierarchyNode, treeIdentifier, dbindex, fullDataObject);
            DataAccess.add(fullDataObject);
        }
        catch (final SQLException sqle) {
            throw new TreeException("EXception while getting conection", sqle);
        }
        catch (final DataAccessException dae) {
            throw new TreeException("EXception while getting conection", (Exception)dae);
        }
        finally {
            TreeManagerUtility.safeClose(ansprep);
            TreeManagerUtility.safeClose(connection);
        }
        if (sendNotification) {
            this.treeNotificationHandler.sendTreeNodeNotification(1000, hierarchyNode.getRootPath(), null, hierarchyNode, treeIdentifier, this.tdef);
        }
        return hierarchyNode;
    }
    
    public HierarchyNode deleteNode(final Row treeIdentifier, final Row startingParentKey) throws TreeException {
        return this.deleteNode(treeIdentifier, startingParentKey, true);
    }
    
    public HierarchyNode deleteNode(final Row treeIdentifier, final Row startingParentKey, final boolean sendNotification) throws TreeException {
        if (startingParentKey == null) {
            throw new TreeException("Starting parent key cannot be null", new IllegalArgumentException());
        }
        final ArrayList ancestorids = new ArrayList();
        long[] path = null;
        long treeNodeID = -1L;
        ResultSet ds = null;
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement prep = null;
        PreparedStatement ansprep = null;
        HierarchyNode oldNode = null;
        try {
            conn = this.relAPI.getConnection();
            stmt = conn.createStatement();
            oldNode = this.getNode(treeIdentifier, startingParentKey);
            if (oldNode == null) {
                return null;
            }
            oldNode.setDataObject(null);
            ansprep = this.getAllAncestorsPrep(treeIdentifier, startingParentKey, conn);
            treeNodeID = this.fillTreeInfo(ancestorids, ansprep);
            if (treeNodeID == -1L) {
                throw new TreeException("Delete operation: TreeNode does not exist for : " + startingParentKey);
            }
            prep = this.getPreparedStatementForSelectDescendantIDS(treeIdentifier, startingParentKey, conn);
            ds = prep.executeQuery();
            final ArrayList descendantIDs = new ArrayList();
            boolean start = true;
            while (ds.next()) {
                if (start) {
                    descendantIDs.add(new Long(ds.getLong(2)));
                    start = false;
                }
                descendantIDs.add(new Long(ds.getLong(1)));
            }
            if (descendantIDs.size() == 0) {
                this.logger.info("Nodes size is zero : this node might be leaf node");
                final HierarchyNode hn = this.getNode(treeIdentifier, startingParentKey);
                if (hn == null) {
                    throw new TreeException("Descedant does not exist for the specofied node");
                }
                descendantIDs.add(new Long(hn.getNodeID()));
            }
            final String tableName = TreeManagerUtility.getBaseTreeNodeTable(this.tdef);
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
            deleteQuery.setCriteria(new Criteria(Column.getColumn(tableName, "NODEID"), (Object)descendantIDs.toArray(), 8));
            DataAccess.delete(deleteQuery);
        }
        catch (final Exception qe) {
            throw new TreeException("Exception while delete node", qe);
        }
        finally {
            TreeManagerUtility.safeClose(ds);
            TreeManagerUtility.safeClose(stmt);
            TreeManagerUtility.safeClose(prep);
            TreeManagerUtility.safeClose(ansprep);
            TreeManagerUtility.safeClose(conn);
        }
        path = this.convert(ancestorids, -1L);
        oldNode.setRootPath(this.getRootPath(ancestorids, -1L, oldNode.getNodeID()));
        if (sendNotification) {
            this.treeNotificationHandler.sendTreeNodeNotification(1001, null, oldNode.getRootPath(), oldNode, treeIdentifier, this.tdef, oldNode.getNodeID());
        }
        return oldNode;
    }
    
    public void deleteNode(final TreeQuery treeQueryObject) throws TreeException {
        throw new TreeException("NOT supported ");
    }
    
    public HierarchyNode moveNode(final Row treeIdentifier, final Row key, final Row newParentKey) throws TreeException {
        final HierarchyNode oldNode = this.deleteNode(treeIdentifier, key, false);
        if (oldNode == null) {
            return null;
        }
        HierarchyNode newNode = (HierarchyNode)oldNode.clone();
        newNode.setParentKey(newParentKey);
        newNode = this.addNode(treeIdentifier, newNode, false);
        this.treeNotificationHandler.sendTreeNodeNotification(1002, newNode.getRootPath(), oldNode.getRootPath(), newNode, treeIdentifier, this.tdef, oldNode.getNodeID());
        return newNode;
    }
    
    public HierarchyNode getNode(final Row treeIdentifier, final Row key, final int numberOfLevels) throws TreeException {
        try {
            final TreeModelManager manager = TreeModelManager.getInstance();
            final TreeQuery query = new TreeQueryImpl(this.treeType, treeIdentifier, key, numberOfLevels);
            query.compile(this.tdef);
            final DOTreeModel data = manager.getModel(query);
            return (HierarchyNode)data.getRoot();
        }
        catch (final Exception e) {
            throw new TreeException(e.getMessage(), e);
        }
    }
    
    public HierarchyNode getNode(final Row treeIdentifier, final Row key) throws TreeException {
        try {
            final TreeModelManager manager = TreeModelManager.getInstance();
            final TreeQuery query = new TreeQueryImpl(this.treeType, treeIdentifier, key, -1);
            query.compile(this.tdef);
            final DOTreeModel data = manager.getModel(query);
            return (HierarchyNode)data.getRoot();
        }
        catch (final Exception e) {
            throw new TreeException(e.getMessage(), e);
        }
    }
    
    public HierarchyNode getNode(final TreeQuery treeQuery) throws TreeException {
        try {
            final TreeModelManager manager = TreeModelManager.getInstance();
            if (!this.treeType.equals(treeQuery.getTreeType())) {
                throw new TreeException("Given TreeType : " + treeQuery.getTreeType() + " does not match with : " + this.treeType);
            }
            treeQuery.compile(this.tdef);
            final DOTreeModel data = manager.getModel(treeQuery);
            return (HierarchyNode)data.getRoot();
        }
        catch (final Exception e) {
            throw new TreeException(e.getMessage(), e);
        }
    }
    
    private HierarchyNode formAncestorsNode(final Iterator rows, final DataObject doo, final Row key) throws Exception {
        HierarchyNode root = null;
        HierarchyNode pr = null;
        while (rows.hasNext()) {
            final Row ansRow = rows.next();
            final DataObject inner = formDataObject(doo, ansRow, key.getTableName());
            final HierarchyNode hn = new DefaultHierarchyNode();
            hn.setKey(inner.getFirstRow(key.getTableName()));
            hn.setDataObject(inner);
            if (root == null) {
                root = hn;
            }
            else {
                hn.setParentKey(pr.getKey());
                pr.add(hn);
            }
            pr = hn;
        }
        return root;
    }
    
    public HierarchyNode getAncestors(final Row treeIdentifier, final Row key) throws TreeException {
        try {
            final SelectQuery ancestorsSQ = this.getAncestorQuery(treeIdentifier, key);
            final DataObject doo = DataAccess.get(ancestorsSQ);
            return this.formAncestorsNode(doo.getRows("ANS"), doo, key);
        }
        catch (final Exception e) {
            throw new TreeException(e);
        }
    }
    
    public Map getAncestors(final Row treeIdentifier, final List keyList) throws TreeException {
        if (keyList == null || keyList.size() == 0) {
            throw new TreeException("Keylist cannot be null or empty" + keyList);
        }
        final HashMap map = new HashMap();
        DataSet ds = null;
        Connection conn = null;
        try {
            final Row kk = keyList.get(0);
            final String associatedTable = kk.getTableName();
            final String treeNodeTable = TreeManagerUtility.getTreeNodeTable(this.tdef, associatedTable);
            final List pkColumns = kk.getPKColumns();
            final List columns = kk.getColumns();
            final int pkCount = pkColumns.size();
            final SelectQuery ancestorsSQ = this.getBulkAncestorQuery(treeIdentifier, keyList);
            final Criteria cr = TreeManagerUtility.getTreeIdentifierAsCriteria(this.tdef, treeIdentifier);
            conn = this.relAPI.getConnection();
            ds = this.relAPI.executeQuery((Query)ancestorsSQ, conn);
            while (ds.next()) {
                final Row keyRow = this.formKeyRow(ds, pkColumns, associatedTable);
                final Row ancestorRow = this.formAncestorRow(ds, pkCount, columns, associatedTable);
                final Long nodeID = (Long)ds.getValue(pkCount + 1);
                final HierarchyNode hn = new DefaultHierarchyNode();
                final DataObject doo = DataAccess.constructDataObject();
                doo.addRow(ancestorRow);
                hn.setNodeID(nodeID);
                hn.setDataObject(doo);
                hn.setKey(ancestorRow);
                final HierarchyNode parentNode = map.get(keyRow);
                if (parentNode != null) {
                    parentNode.add(hn);
                    hn.setParentKey(parentNode.getKey());
                }
                map.put(keyRow, hn);
            }
            for (final Object key : map.keySet()) {
                final DefaultHierarchyNode dhn = map.get(key);
                map.put(key, dhn.getRoot());
            }
            return map;
        }
        catch (final Exception e) {
            throw new TreeException(e);
        }
        finally {
            TreeManagerUtility.safeClose(ds);
            TreeManagerUtility.safeClose(conn);
        }
    }
    
    Row formKeyRow(final DataSet ds, final List pkColumns, final String associatedTable) throws Exception {
        final int pkCount = pkColumns.size();
        final Row row = new Row(associatedTable);
        for (int i = 1; i <= pkCount; ++i) {
            row.set((String)pkColumns.get(i - 1), ds.getValue(i));
        }
        return row;
    }
    
    Row formAncestorRow(final DataSet ds, final int pkCount, final List columns, final String associatedTable) throws Exception {
        final Row row = new Row(associatedTable);
        final int columnCount = ds.getColumnCount();
        int columnIncrementer = 0;
        for (int i = pkCount + 2; i <= columnCount; ++i) {
            row.set((String)columns.get(columnIncrementer), ds.getValue(i));
            ++columnIncrementer;
        }
        return row;
    }
    
    Criteria getCriteria(final Row key, final DataObject doo, final String treeNodeTable) throws Exception {
        final Iterator rows = doo.getRows(treeNodeTable, key);
        Criteria criteria = null;
        if (rows.hasNext()) {
            final Row firstRow = rows.next();
            final Object nodeID = firstRow.get("NODEID");
            criteria = new Criteria(Column.getColumn(TreeManagerUtility.getTreeInfoTable(this.tdef), "DESCENDANTID"), nodeID, 0);
        }
        return criteria;
    }
    
    public SelectQuery getBulkAncestorQuery(final Row treeIdentifier, final List keyList) throws TreeException {
        try {
            final String associatedTable = keyList.get(0).getTableName();
            final String treeNodeTable = TreeManagerUtility.getTreeNodeTable(this.tdef, associatedTable);
            final String baseTreeNode = TreeManagerUtility.getBaseTreeNodeTable(this.tdef);
            final String infoTable = TreeManagerUtility.getTreeInfoTable(this.tdef);
            final String[] pks = TreeManagerUtility.getKeyColumns(associatedTable);
            final SelectQuery ancestorsSQ = (SelectQuery)new SelectQueryImpl(Table.getTable(infoTable));
            ancestorsSQ.addJoin(new Join(Table.getTable(infoTable), Table.getTable(baseTreeNode), new String[] { "DESCENDANTID" }, new String[] { "NODEID" }, 2));
            ancestorsSQ.addJoin(new Join(Table.getTable(baseTreeNode), Table.getTable(treeNodeTable), new String[] { "NODEID" }, new String[] { "NODEID" }, 2));
            ancestorsSQ.addJoin(new Join(Table.getTable(infoTable), Table.getTable(treeNodeTable, "ANS"), new String[] { "ANCESTORID" }, new String[] { "NODEID" }, 2));
            ancestorsSQ.addJoin(new Join(Table.getTable(treeNodeTable, "ANS"), Table.getTable(associatedTable), pks, pks, 2));
            for (int i = 0; i < pks.length; ++i) {
                ancestorsSQ.addSelectColumn(Column.getColumn(treeNodeTable, pks[i]));
            }
            ancestorsSQ.addSelectColumn(Column.getColumn("ANS", "NODEID"));
            ancestorsSQ.addSelectColumn(Column.getColumn(associatedTable, "*"));
            ancestorsSQ.addSortColumn(new SortColumn(infoTable, "NODELEVEL", false));
            Criteria cr = TreeManagerUtility.getTreeIdentifierAsCriteria(this.tdef, treeIdentifier);
            Criteria keysCriteria = null;
            for (int j = 0; j < keyList.size(); ++j) {
                final Row key = keyList.get(j);
                final Criteria local = TreeManagerUtility.getStartingParentAsCriteria(associatedTable, treeNodeTable, key);
                if (keysCriteria == null) {
                    keysCriteria = local;
                }
                else {
                    keysCriteria = keysCriteria.or(local);
                }
            }
            cr = cr.and(keysCriteria);
            ancestorsSQ.setCriteria(cr);
            return ancestorsSQ;
        }
        catch (final Exception e) {
            throw new TreeException(e);
        }
    }
    
    public SelectQuery getAncestorQuery(final Row treeIdentifier, final Row key) throws TreeException {
        try {
            final String associatedTable = key.getTableName();
            final String treeNodeTable = TreeManagerUtility.getTreeNodeTable(this.tdef, associatedTable);
            final String baseTreeNode = TreeManagerUtility.getBaseTreeNodeTable(this.tdef);
            final String infoTable = TreeManagerUtility.getTreeInfoTable(this.tdef);
            final String[] pks = TreeManagerUtility.getKeyColumns(associatedTable);
            final SelectQuery ancestorsSQ = (SelectQuery)new SelectQueryImpl(Table.getTable(infoTable));
            ancestorsSQ.addJoin(new Join(Table.getTable(infoTable), Table.getTable(baseTreeNode), new String[] { "DESCENDANTID" }, new String[] { "NODEID" }, 2));
            ancestorsSQ.addJoin(new Join(Table.getTable(baseTreeNode), Table.getTable(treeNodeTable), new String[] { "NODEID" }, new String[] { "NODEID" }, 2));
            ancestorsSQ.addJoin(new Join(Table.getTable(infoTable), Table.getTable(treeNodeTable, "ANS"), new String[] { "ANCESTORID" }, new String[] { "NODEID" }, 2));
            ancestorsSQ.addJoin(new Join(Table.getTable(treeNodeTable, "ANS"), Table.getTable(associatedTable), pks, pks, 2));
            ancestorsSQ.addSelectColumn(Column.getColumn(infoTable, "*"));
            ancestorsSQ.addSelectColumn(Column.getColumn(baseTreeNode, "*"));
            ancestorsSQ.addSelectColumn(Column.getColumn(treeNodeTable, "*"));
            ancestorsSQ.addSelectColumn(Column.getColumn("ANS", "*"));
            ancestorsSQ.addSelectColumn(Column.getColumn(associatedTable, "*"));
            ancestorsSQ.addSortColumn(new SortColumn(infoTable, "NODELEVEL", false));
            Criteria cr = TreeManagerUtility.getTreeIdentifierAsCriteria(this.tdef, treeIdentifier);
            cr = cr.and(TreeManagerUtility.getStartingParentAsCriteria(associatedTable, treeNodeTable, key));
            ancestorsSQ.setCriteria(cr);
            return ancestorsSQ;
        }
        catch (final Exception e) {
            throw new TreeException(e);
        }
    }
    
    public HierarchyNode getAncestors(final Row treeIdentifier, final Row key, final int level) throws TreeException {
        return null;
    }
    
    private int findIndex(final Row key, final Row treeIdentifier, final Connection connection) throws TreeException, SQLException, DataAccessException {
        if (key == null) {
            return -1;
        }
        final String findIndexSQL = this.getIndexQuery(key.getTableName());
        this.check(findIndexSQL, key);
        final PreparedStatement prepFindIndex = connection.prepareStatement(findIndexSQL);
        this.setValues(prepFindIndex, key, treeIdentifier);
        ResultSet ds = null;
        try {
            ds = prepFindIndex.executeQuery();
            ds.next();
            return ds.getInt(1);
        }
        finally {
            TreeManagerUtility.safeClose(ds);
            TreeManagerUtility.safeClose(prepFindIndex);
        }
    }
    
    private PreparedStatement getAllAncestorsPrep(final Row treeIdentifier, final HierarchyNode hierarchyNode, final Connection connection) throws SQLException, TreeException, DataAccessException {
        if (hierarchyNode.getParentKey() == null) {
            return null;
        }
        final String ancestorsSQL = this.getAncestorQuery(hierarchyNode.getParentKey().getTableName());
        this.check(ancestorsSQL, hierarchyNode.getKey());
        final PreparedStatement prepFindAllAncestors = connection.prepareStatement(ancestorsSQL);
        this.setValues(prepFindAllAncestors, hierarchyNode.getParentKey(), treeIdentifier);
        return prepFindAllAncestors;
    }
    
    private PreparedStatement setValuesForPrep(final String sql, final Row treeIdentifier, final Row key, final Connection connection) throws SQLException, TreeException, DataAccessException {
        final PreparedStatement prepFindAllAncestors = connection.prepareStatement(sql);
        this.setValues(prepFindAllAncestors, key, treeIdentifier);
        return prepFindAllAncestors;
    }
    
    private PreparedStatement getAllAncestorsPrep(final Row treeIdentifier, final Row key, final Connection connection) throws SQLException, TreeException, DataAccessException {
        if (key == null) {
            return null;
        }
        final String ancestorsSQL = this.getAncestorQuery(key.getTableName());
        this.check(ancestorsSQL, key);
        final PreparedStatement prepFindAllAncestors = connection.prepareStatement(ancestorsSQL);
        this.setValues(prepFindAllAncestors, key, treeIdentifier);
        return prepFindAllAncestors;
    }
    
    private String getArrayAsCommaSeparatedValues(final ArrayList descendantIDs) throws TreeException {
        String string = descendantIDs.get(0).toString();
        for (int i = 1; i < descendantIDs.size(); ++i) {
            string = string + "," + descendantIDs.get(i).toString();
        }
        return string;
    }
    
    private long fillTreeInfo(final ArrayList ancestorids, final PreparedStatement ansprep) throws TreeException {
        long parentID = -1L;
        ResultSet ds = null;
        int levelIncrementor = 1;
        try {
            ds = ansprep.executeQuery();
            while (ds.next()) {
                if (levelIncrementor == 1) {
                    parentID = ds.getLong(2);
                }
                final long ans = ds.getLong(1);
                ancestorids.add(new Long(ans));
                ++levelIncrementor;
            }
        }
        catch (final Exception sqle) {
            this.logger.log(Level.SEVERE, "Exception while processing ancestorid's ", sqle);
            throw new TreeException(sqle.getMessage(), sqle);
        }
        finally {
            TreeManagerUtility.safeClose(ds);
        }
        return parentID;
    }
    
    private void addCachedNode(final long parentNodeID, final ArrayList ancestorids, final long uniqueID, final HierarchyNode hierarchyNode, final Row treeIdentifier, final int nodeIndex, final DataObject fullDataObject) throws TreeException {
        try {
            hierarchyNode.setRootPath(this.getRootPath(ancestorids, parentNodeID, uniqueID));
            this.addTreeNodeObject(hierarchyNode, treeIdentifier, nodeIndex, fullDataObject);
            final Row row = new Row(TreeManagerUtility.getTreeInfoTable(this.tdef));
            row.set("NODELEVEL", (Object)new Integer(1));
            row.set("ANCESTORID", (Object)new Long(parentNodeID));
            row.set("DESCENDANTID", (Object)new Long(uniqueID));
            fullDataObject.addRow(row);
            for (int i = 0; i < ancestorids.size(); ++i) {
                final long ans = ancestorids.get(i);
                final Row localRow = new Row(TreeManagerUtility.getTreeInfoTable(this.tdef));
                localRow.set("NODELEVEL", (Object)new Integer(i + 2));
                localRow.set("ANCESTORID", (Object)new Long(ans));
                localRow.set("DESCENDANTID", (Object)new Long(uniqueID));
                fullDataObject.addRow(localRow);
            }
        }
        catch (final Exception sqle) {
            throw new TreeException(sqle.getMessage(), sqle);
        }
        for (int childCount = hierarchyNode.getChildCount(), i = 0; i < childCount; ++i) {
            final ArrayList localAncestorids = (ArrayList)ancestorids.clone();
            localAncestorids.add(0, new Long(parentNodeID));
            final HierarchyNode childNode = (HierarchyNode)hierarchyNode.getChildAt(i);
            this.addCachedNode(hierarchyNode.getNodeID(), localAncestorids, this.generateNodeID(), childNode, treeIdentifier, i, fullDataObject);
        }
    }
    
    private long generateNodeID() throws TreeException {
        try {
            if (this.seqgen == null) {
                final String columnName = "NODEID";
                final String tableName = TreeManagerUtility.getBaseTreeNodeTable(this.tdef);
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
                final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
                final String seqGenName = uvg.getGeneratorName();
                this.seqgen = SequenceGeneratorRepository.get(seqGenName);
                if (this.seqgen == null) {
                    SequenceGeneratorRepository.initGeneratorValues(td);
                    this.seqgen = SequenceGeneratorRepository.get(seqGenName);
                    this.logger.log(Level.INFO, "SequenceGenerator for the table :: {0} has been initialized and the seqgen is :: {1}", new Object[] { tableName, this.seqgen });
                }
            }
            return (long)this.seqgen.nextValue();
        }
        catch (final Exception e) {
            throw new TreeException(e);
        }
    }
    
    private Criteria getTreeIdentifierCriteria() throws Exception {
        Criteria criteria = null;
        final String[] treeIdentifierColumnNames = TreeManagerUtility.getTreeIdentifierColumns(this.tdef);
        final String baseTreeNodeTable = TreeManagerUtility.getBaseTreeNodeTable(this.tdef);
        for (final String columnName : treeIdentifierColumnNames) {
            if (criteria == null) {
                criteria = new Criteria(Column.getColumn(baseTreeNodeTable, columnName), (Object)QueryConstants.PREPARED_STMT_CONST, 0);
            }
            else {
                criteria = criteria.and(Column.getColumn(baseTreeNodeTable, columnName), (Object)QueryConstants.PREPARED_STMT_CONST, 0);
            }
        }
        return criteria;
    }
    
    private PreparedStatement getPreparedStatementForSelectDescendantIDS(final Row treeIdentifier, final Row startingParentKey, final Connection conn) throws TreeException, SQLException, DataAccessException {
        final String selectDescendantIDSQL = this.getDeleteSelectQuery(startingParentKey.getTableName());
        this.check(selectDescendantIDSQL, startingParentKey);
        final PreparedStatement prepForDelete = conn.prepareStatement(selectDescendantIDSQL);
        this.setValues(prepForDelete, startingParentKey, treeIdentifier);
        return prepForDelete;
    }
    
    private void setValues(final PreparedStatement pstmt, final Row key, final Row treeIdentifier) throws SQLException, TreeException, DataAccessException {
        final List pkColumns = key.getPKColumns();
        final int keySize = pkColumns.size();
        for (int i = 0; i < keySize; ++i) {
            final String columnName = pkColumns.get(i);
            final Object value = key.get(columnName);
            pstmt.setObject(i + 1, value);
        }
        for (int len = TreeManagerUtility.getTreeIdentifierColumns(this.tdef).length, j = 0; j < len; ++j) {
            final String columnName2 = TreeManagerUtility.getTreeIdentifierColumns(this.tdef)[j];
            final Object value2 = treeIdentifier.get(columnName2);
            pstmt.setObject(j + keySize + 1, value2);
        }
    }
    
    private void formQueries() throws Exception {
        final SelectQuery allAncestor = this.getBaseAncestorQuery();
        final SelectQuery findIndex = this.getBaseFindIndexQuery();
        final SelectQuery selectIDForDelete = this.getBaseDeleteQuery();
        final Criteria treeIdentifierCriteria = this.getTreeIdentifierCriteria();
        final Iterator iter = this.tdef.getRows("TablesInTree");
        while (iter.hasNext()) {
            final Row tableInTree = iter.next();
            final String associatedTable = (String)tableInTree.get("TABLENAME");
            final String treeNodeTable = (String)tableInTree.get("TREENODETABLE");
            Criteria tableCriteria = TreeManagerUtility.getPreparedStatementCriteria(treeNodeTable, associatedTable);
            tableCriteria = tableCriteria.and(treeIdentifierCriteria);
            final SelectQuery tableAncestorQuery = (SelectQuery)allAncestor.clone();
            tableAncestorQuery.addJoin(new Join(Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)), Table.getTable(treeNodeTable), new String[] { "NODEID" }, new String[] { "NODEID" }, 2));
            tableAncestorQuery.setCriteria(tableCriteria);
            final String tableAncestorQuerySQL = this.relAPI.getSelectSQL((Query)tableAncestorQuery);
            this.allAncestores.put(associatedTable, tableAncestorQuerySQL);
            if (TreeManagerUtility.getSiblingOrdered(this.tdef)) {
                final SelectQuery prepFindIndexSQL = (SelectQuery)findIndex.clone();
                final Join jj = new Join(Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)), Table.getTable(treeNodeTable), new String[] { "NODEID" }, new String[] { "NODEID" }, 2);
                prepFindIndexSQL.addJoin(jj);
                Criteria criteria = (Criteria)tableCriteria.clone();
                criteria = criteria.and(Column.getColumn("A1", "NODELEVEL"), (Object)new Integer(1), 0);
                criteria = criteria.and(Column.getColumn("A2", "NODELEVEL"), (Object)new Integer(1), 0);
                prepFindIndexSQL.setCriteria(criteria);
                final String prepFindIndexSQLString = this.relAPI.getSelectSQL((Query)prepFindIndexSQL);
                this.findIndexes.put(associatedTable, prepFindIndexSQLString);
            }
            final SelectQuery tableDelete = (SelectQuery)selectIDForDelete.clone();
            final Join jj2 = new Join(Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)), Table.getTable(treeNodeTable), new String[] { "NODEID" }, new String[] { "NODEID" }, 2);
            tableDelete.addJoin(jj2);
            tableDelete.setCriteria(tableCriteria);
            final String tableDeleteQuerySQL = this.relAPI.getSelectSQL((Query)tableDelete);
            this.deleteSelectQueries.put(associatedTable, tableDeleteQuerySQL);
        }
    }
    
    String getIndexQuery(final String tableName) throws TreeException {
        try {
            final SelectQuery prepFindIndexSQL = this.getBaseFindIndexQuery();
            final Criteria treeIdentifierCriteria = this.getTreeIdentifierCriteria();
            final Iterator iter = this.tdef.getRows("TablesInTree");
            while (iter.hasNext()) {
                final Row tableInTree = iter.next();
                final String associatedTable = (String)tableInTree.get("TABLENAME");
                if (!tableName.equals(associatedTable)) {
                    continue;
                }
                final String treeNodeTable = (String)tableInTree.get("TREENODETABLE");
                Criteria tableCriteria = TreeManagerUtility.getPreparedStatementCriteria(treeNodeTable, associatedTable);
                tableCriteria = tableCriteria.and(treeIdentifierCriteria);
                if (TreeManagerUtility.getSiblingOrdered(this.tdef)) {
                    final Join jj = new Join(Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)), Table.getTable(treeNodeTable), new String[] { "NODEID" }, new String[] { "NODEID" }, 2);
                    prepFindIndexSQL.addJoin(jj);
                    Criteria criteria = (Criteria)tableCriteria.clone();
                    criteria = criteria.and(Column.getColumn("A1", "NODELEVEL"), (Object)new Integer(1), 0);
                    criteria = criteria.and(Column.getColumn("A2", "NODELEVEL"), (Object)new Integer(1), 0);
                    prepFindIndexSQL.setCriteria(criteria);
                    final String prepFindIndexSQLString = this.relAPI.getSelectSQL((Query)prepFindIndexSQL);
                    return prepFindIndexSQLString;
                }
            }
        }
        catch (final Exception e) {
            throw new TreeException("Exception while fetching the index Query for the table : " + tableName, e);
        }
        return null;
    }
    
    String getAncestorQuery(final String tableName) throws TreeException {
        try {
            final SelectQuery tableAncestorQuery = this.getBaseAncestorQuery();
            final Criteria treeIdentifierCriteria = this.getTreeIdentifierCriteria();
            final Iterator iter = this.tdef.getRows("TablesInTree");
            while (iter.hasNext()) {
                final Row tableInTree = iter.next();
                final String associatedTable = (String)tableInTree.get("TABLENAME");
                if (!tableName.equals(associatedTable)) {
                    continue;
                }
                final String treeNodeTable = (String)tableInTree.get("TREENODETABLE");
                Criteria tableCriteria = TreeManagerUtility.getPreparedStatementCriteria(treeNodeTable, associatedTable);
                tableCriteria = tableCriteria.and(treeIdentifierCriteria);
                tableAncestorQuery.addJoin(new Join(Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)), Table.getTable(treeNodeTable), new String[] { "NODEID" }, new String[] { "NODEID" }, 2));
                tableAncestorQuery.setCriteria(tableCriteria);
                final String tableAncestorQuerySQL = this.relAPI.getSelectSQL((Query)tableAncestorQuery);
                return tableAncestorQuerySQL;
            }
        }
        catch (final Exception e) {
            throw new TreeException("Exception while fetching the ancestor Query for the table : " + tableName, e);
        }
        return null;
    }
    
    String getDeleteSelectQuery(final String startingParentTableName) throws TreeException {
        try {
            final SelectQuery tableDelete = this.getBaseDeleteQuery();
            final Criteria treeIdentifierCriteria = this.getTreeIdentifierCriteria();
            final Iterator iter = this.tdef.getRows("TablesInTree");
            while (iter.hasNext()) {
                final Row tableInTree = iter.next();
                final String associatedTable = (String)tableInTree.get("TABLENAME");
                if (!startingParentTableName.equals(associatedTable)) {
                    continue;
                }
                final String treeNodeTable = (String)tableInTree.get("TREENODETABLE");
                Criteria tableCriteria = TreeManagerUtility.getPreparedStatementCriteria(treeNodeTable, associatedTable);
                tableCriteria = tableCriteria.and(treeIdentifierCriteria);
                final Join jj2 = new Join(Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)), Table.getTable(treeNodeTable), new String[] { "NODEID" }, new String[] { "NODEID" }, 2);
                tableDelete.addJoin(jj2);
                tableDelete.setCriteria(tableCriteria);
                final String tableDeleteQuerySQL = this.relAPI.getSelectSQL((Query)tableDelete);
                return tableDeleteQuerySQL;
            }
        }
        catch (final Exception e) {
            throw new TreeException("Exception while fetching the delete Query for the table : " + startingParentTableName, e);
        }
        return null;
    }
    
    private SelectQuery getBaseAncestorQuery() throws Exception {
        final SelectQuery allAncestor = (SelectQuery)new SelectQueryImpl(Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)));
        allAncestor.addJoin(new Join(Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)), Table.getTable(TreeManagerUtility.getTreeInfoTable(this.tdef)), new String[] { "NODEID" }, new String[] { "DESCENDANTID" }, 2));
        allAncestor.addSelectColumn(Column.getColumn(TreeManagerUtility.getTreeInfoTable(this.tdef), "ANCESTORID"));
        allAncestor.addSelectColumn(Column.getColumn(TreeManagerUtility.getBaseTreeNodeTable(this.tdef), "NODEID"));
        allAncestor.addSortColumn(new SortColumn(TreeManagerUtility.getTreeInfoTable(this.tdef), "NODELEVEL", true));
        return allAncestor;
    }
    
    private SelectQuery getBaseFindIndexQuery() throws Exception {
        final SelectQuery findIndex = (SelectQuery)new SelectQueryImpl(Table.getTable(TreeManagerUtility.getTreeInfoTable(this.tdef), "A1"));
        final Column column = Column.getColumn("A1", "DESCENDANTID");
        findIndex.addSelectColumn(column.count());
        findIndex.addJoin(new Join(Table.getTable(TreeManagerUtility.getTreeInfoTable(this.tdef), "A1"), Table.getTable(TreeManagerUtility.getTreeInfoTable(this.tdef), "A2"), new String[] { "DESCENDANTID" }, new String[] { "DESCENDANTID" }, 2));
        findIndex.addJoin(new Join(Table.getTable(TreeManagerUtility.getTreeInfoTable(this.tdef), "A2"), Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)), new String[] { "ANCESTORID" }, new String[] { "NODEID" }, 2));
        return findIndex;
    }
    
    private SelectQuery getBaseDeleteQuery() throws Exception {
        final SelectQuery selectIDForDelete = (SelectQuery)new SelectQueryImpl(Table.getTable(TreeManagerUtility.getTreeInfoTable(this.tdef)));
        selectIDForDelete.addSelectColumn(Column.getColumn(TreeManagerUtility.getTreeInfoTable(this.tdef), "DESCENDANTID"));
        selectIDForDelete.addSelectColumn(Column.getColumn(TreeManagerUtility.getTreeInfoTable(this.tdef), "ANCESTORID"));
        final Join jj1 = new Join(Table.getTable(TreeManagerUtility.getTreeInfoTable(this.tdef)), Table.getTable(TreeManagerUtility.getBaseTreeNodeTable(this.tdef)), new String[] { "ANCESTORID" }, new String[] { "NODEID" }, 2);
        selectIDForDelete.addJoin(jj1);
        return selectIDForDelete;
    }
    
    private void check(final String sql, final Row key) throws TreeException {
        if (sql == null) {
            final String tableName = key.getTableName();
            throw new TreeException("Table " + tableName + " does not defined as part of the hierarchy for tree definition " + this.treeType);
        }
    }
    
    private void addTreeNodeObject(final HierarchyNode hierarchyNode, final Row treeIdentifier, final int nodeIndex, final DataObject fullDataObject) throws TreeException {
        try {
            final Row key = hierarchyNode.getKey();
            final String treeNodeTable = TreeManagerUtility.getTreeNodeTable(this.tdef, key.getTableName());
            if (treeNodeTable == null) {
                throw new TreeException("Table " + key.getTableName() + " does not participate in the tree :" + this.treeType);
            }
            final DataObject treeNodeInfo = DataAccess.constructDataObject();
            final Row baseRow = new Row(TreeManagerUtility.getBaseTreeNodeTable(this.tdef));
            final Row subRow = new Row(treeNodeTable);
            subRow.set("NODEID", (Object)new Long(hierarchyNode.getNodeID()));
            baseRow.set("NODEID", (Object)new Long(hierarchyNode.getNodeID()));
            for (int i = 0; i < TreeManagerUtility.getTreeIdentifierColumns(this.tdef).length; ++i) {
                final String columnName = TreeManagerUtility.getTreeIdentifierColumns(this.tdef)[i];
                baseRow.set(columnName, treeIdentifier.get(columnName));
            }
            final List pkColumns = key.getPKColumns();
            for (int j = 0; j < pkColumns.size(); ++j) {
                final String pkColumn = pkColumns.get(j);
                subRow.set(pkColumn, key.get(pkColumn));
            }
            if (TreeManagerUtility.getSiblingOrdered(this.tdef)) {
                baseRow.set("NODEINDEX", (Object)new Integer(nodeIndex));
            }
            fullDataObject.addRow(baseRow);
            fullDataObject.addRow(subRow);
        }
        catch (final Exception e) {
            throw new TreeException(e.getMessage(), e);
        }
    }
    
    private long[] convert(final ArrayList ancestorsparam, final long parentNodeID) {
        final ArrayList ancestors = (ArrayList)ancestorsparam.clone();
        if (ancestors.contains(new Long(-1L))) {
            final int ansindex = ancestors.indexOf(new Long(-1L));
            ancestors.remove(ansindex);
        }
        if (parentNodeID != -1L) {
            ancestors.add(0, new Long(parentNodeID));
        }
        Collections.reverse(ancestors);
        final int size = ancestors.size();
        final long[] path = new long[size];
        final int j = 0;
        for (int i = 0; i < size; ++i) {
            final Long ans = ancestors.get(i);
            path[i] = ans;
        }
        return path;
    }
    
    public long[] getTreePath(final Row treeIdentifier, final Row key) throws TreeException {
        Connection conn = null;
        PreparedStatement ansprep = null;
        try {
            if (key == null) {
                return null;
            }
            conn = this.relAPI.getConnection();
            ansprep = this.getAllAncestorsPrep(treeIdentifier, key, conn);
            final ArrayList ancestorids = new ArrayList();
            final long parentNodeID = this.fillTreeInfo(ancestorids, ansprep);
            return this.convert(ancestorids, parentNodeID);
        }
        catch (final Exception e) {
            throw new TreeException(e.getMessage(), e);
        }
        finally {
            TreeManagerUtility.safeClose(ansprep);
            TreeManagerUtility.safeClose(conn);
        }
    }
    
    public void cleanup() throws TreeException {
        try {
            this.treeNotificationHandler.cleanup();
        }
        catch (final Exception e) {
            throw new TreeException("Exception while cleanup of treeManager for treetype :" + this.treeType, e);
        }
    }
    
    private long[] getRootPath(final ArrayList ancestorids, final long parentNodeID, final long uniqueID) {
        final ArrayList ancestors = (ArrayList)ancestorids.clone();
        if (ancestors.contains(new Long(-1L))) {
            final int ansindex = ancestors.indexOf(new Long(-1L));
            ancestors.remove(ansindex);
        }
        if (parentNodeID != -1L) {
            ancestors.add(0, new Long(parentNodeID));
        }
        ancestors.add(0, new Long(uniqueID));
        Collections.reverse(ancestors);
        final int size = ancestors.size();
        final long[] path = new long[size];
        final int j = 0;
        for (int i = 0; i < size; ++i) {
            final Long ans = ancestors.get(i);
            path[i] = ans;
        }
        return path;
    }
    
    public static DataObject formDataObject(final DataObject doo, final Row ansRow, final String associatedTable) throws Exception {
        final DataObject rdo = DataAccess.constructDataObject();
        final Row asRow = doo.getFirstRow(associatedTable, ansRow);
        rdo.addRow(ansRow);
        rdo.addRow(asRow);
        return rdo;
    }
    
    public int levelOfElement(final Row treeIdentifier, final Row key) throws TreeException {
        try {
            final String associatedTable = key.getTableName();
            final String treeNodeTable = TreeManagerUtility.getTreeNodeTable(this.tdef, associatedTable);
            final String baseTreeNode = TreeManagerUtility.getBaseTreeNodeTable(this.tdef);
            final String infoTable = TreeManagerUtility.getTreeInfoTable(this.tdef);
            final String[] pks = TreeManagerUtility.getKeyColumns(associatedTable);
            final SelectQuery ancestorsSQ = (SelectQuery)new SelectQueryImpl(Table.getTable(infoTable));
            ancestorsSQ.addSelectColumn(Column.getColumn(infoTable, "*"));
            ancestorsSQ.addJoin(new Join(Table.getTable(infoTable), Table.getTable(treeNodeTable), new String[] { "DESCENDANTID" }, new String[] { "NODEID" }, 2));
            ancestorsSQ.addJoin(new Join(Table.getTable(treeNodeTable), Table.getTable(baseTreeNode), new String[] { "NODEID" }, new String[] { "NODEID" }, 2));
            Criteria cr = TreeManagerUtility.getTreeIdentifierAsCriteria(this.tdef, treeIdentifier);
            cr = cr.and(TreeManagerUtility.getStartingParentAsCriteria(associatedTable, treeNodeTable, key));
            cr = cr.and(new Criteria(Column.getColumn(infoTable, "ANCESTORID"), (Object)new Long(-1L), 0));
            ancestorsSQ.setCriteria(cr);
            final DataObject doo = DataAccess.get(ancestorsSQ);
            final Integer nodeLevel = (Integer)doo.getFirstRow(infoTable).get("NODELEVEL");
            return nodeLevel;
        }
        catch (final Exception e) {
            throw new TreeException(e);
        }
    }
}
