package com.adventnet.client.tree.util;

import com.adventnet.tree.TreeManagerUtility;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.List;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.tree.DefaultOrderedHierarchyNode;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.model.tree.TreeModelData;
import com.adventnet.tree.HierarchyNode;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

public class TreeCreator
{
    private HashMap tdef_map;
    private HashMap table_map;
    private HashMap mapper_list;
    private ArrayList standAlone_list;
    private Collection tableNames;
    private String treeType;
    private String baseTreeNodeTable;
    private ArrayList tree_id_list;
    private HashMap pointerMap;
    private static Logger logger;
    
    public TreeCreator(final String treeType, final HashMap mapper_list, final ArrayList standAlone_list) {
        this.tdef_map = null;
        this.table_map = null;
        this.mapper_list = null;
        this.standAlone_list = null;
        this.tableNames = null;
        this.treeType = null;
        this.baseTreeNodeTable = null;
        this.tree_id_list = null;
        this.pointerMap = new HashMap();
        TreeCreator.logger.log(Level.FINER, " Tree Creation Starting <-> {0}", treeType);
        this.treeType = treeType;
        this.mapper_list = mapper_list;
        this.standAlone_list = standAlone_list;
    }
    
    public void init() {
        final long intime = System.currentTimeMillis();
        this.initTableDefinitions(this.treeType);
        this.tableNames = this.tdef_map.keySet();
        this.table_map = this.getRelatedKeys(this.tdef_map, this.tableNames);
        this.populateTableDefinition(this.mapper_list.keySet(), this.tdef_map);
        final long outtime = System.currentTimeMillis();
        TreeCreator.logger.log(Level.FINER, "TreeCreator init<->{0}ms", new Long(outtime - intime));
    }
    
    public void reinit() {
        this.pointerMap = new HashMap();
    }
    
    public ArrayList getRootNodes(final DataObject dobj) {
        if (dobj == null) {
            return null;
        }
        final ArrayList list = this.getRootNodeList(dobj);
        final long intime = System.currentTimeMillis();
        final ArrayList newList = new ArrayList();
        for (HierarchyNode node : list) {
            node = (HierarchyNode)node.clone();
            newList.add(node);
            TreeCreator.logger.log(Level.FINEST, "TreeCreator<-?dumping<->{0}<->{1}", new Object[] { TreeModelData.getString(node), node.getParentKey() });
        }
        final long outtime = System.currentTimeMillis();
        TreeCreator.logger.log(Level.FINER, "TreeCreator getRootNode<->{0}ms", new Long(outtime - intime));
        return newList;
    }
    
    public String getBaseTreeNodeTable() {
        return this.baseTreeNodeTable;
    }
    
    public ArrayList getTreeIdList() {
        return this.tree_id_list;
    }
    
    private ArrayList getRootNodeList(final DataObject dobj) {
        final long intime = System.currentTimeMillis();
        Iterator doIt = null;
        final ArrayList root = new ArrayList();
        try {
            doIt = dobj.getRows((String)null);
        }
        catch (final DataAccessException e) {
            TreeCreator.logger.log(Level.WARNING, "DAE<->error populating xml file{0}", (Throwable)e);
            e.printStackTrace();
            return root;
        }
        while (doIt.hasNext()) {
            final Row currentRow = doIt.next();
            final String currentTName = currentRow.getTableName();
            final TableDefinition tdef = this.tdef_map.get(currentTName);
            if (tdef == null) {
                continue;
            }
            Row keyRow = null;
            final HierarchyNode hNode = (HierarchyNode)new DefaultOrderedHierarchyNode();
            if (this.mapper_list.containsKey(currentTName)) {
                final TableMapper tableMapper = this.mapper_list.get(currentTName);
                final String childTable = tableMapper.getChildTableName();
                keyRow = new Row(childTable);
                this.updateRowWithFK(tdef, tableMapper.getChildTableFKs(), currentRow, keyRow);
                hNode.setKey(keyRow);
                TreeCreator.logger.log(Level.FINEST, "TreeCreator My KeyRow 1<->{0}", keyRow);
                final String parentTable = tableMapper.getParentTableName();
                final Row parentRow = new Row(parentTable);
                final boolean isRowValid = this.updateRowWithFK(tdef, tableMapper.getParentTableFKs(), currentRow, parentRow);
                TreeCreator.logger.log(Level.FINEST, "TreeCreator My Row<->{0}<->{1}<->{2}", new Object[] { currentRow, parentRow, new Boolean(isRowValid) });
                if (!isRowValid) {
                    continue;
                }
                final boolean added = this.addToParent(parentRow, hNode);
                if (added) {
                    continue;
                }
                TreeCreator.logger.log(Level.FINEST, "TreeCreator <-> adding<->{0}<->{1}<->{2}", new Object[] { hNode, parentRow, TreeModelData.getString(hNode) });
                root.add(hNode);
                hNode.setParentKey(parentRow);
                final Pointer pointer = this.getPointer(keyRow);
                pointer.addNode(hNode);
            }
            else {
                if (!this.standAlone_list.contains(currentTName)) {
                    continue;
                }
                keyRow = new Row(currentTName);
                this.updateRowWithPK(tdef, currentRow, keyRow);
                hNode.setKey(keyRow);
                TreeCreator.logger.log(Level.FINEST, "TreeCreator My KeyRow2  <->{0}", keyRow);
                final ArrayList fk_list = this.table_map.get(currentTName);
                boolean nodeAdded = false;
                final Iterator iterator = fk_list.iterator();
                TreeCreator.logger.log(Level.FINEST, "TreeCreator CurrentRow<->{0}", currentRow);
                while (iterator.hasNext()) {
                    final HierarchyNode currentNode = (HierarchyNode)hNode.clone();
                    final ForeignKeyDefinition fkey = iterator.next();
                    final String parentTName = fkey.getMasterTableName();
                    if (!this.standAlone_list.contains(parentTName)) {
                        continue;
                    }
                    if ("PreferenceNode_Profile_FK".equals(fkey.getName()) && currentRow.get("PARENTNODEID") != null) {
                        continue;
                    }
                    TreeCreator.logger.log(Level.FINEST, "TreeCreator parentTName<->{0}<->{1}<->{2}", new Object[] { parentTName, fkey.getName(), fkey.getSlaveTableName() });
                    final Row parentRow2 = new Row(parentTName);
                    final List lit = fkey.getForeignKeyColumns();
                    final List pk_columns = parentRow2.getPKColumns();
                    TreeCreator.logger.log(Level.FINEST, "TreeCreator size(){0}<->{1}", new Object[] { new Integer(lit.size()), new Integer(pk_columns.size()) });
                    boolean isNull = false;
                    for (int k = 0; k < lit.size(); ++k) {
                        final ForeignKeyColumnDefinition fckd = lit.get(k);
                        final String currentColumnName = fckd.getLocalColumnDefinition().getColumnName();
                        final String parentColumnName = fckd.getReferencedColumnDefinition().getColumnName();
                        final Object newValue = currentRow.get(currentColumnName);
                        parentRow2.set(parentColumnName, newValue);
                        TreeCreator.logger.log(Level.FINEST, "TreeCreator DEBUG 1<->{0}<->{1}<->{2}", new Object[] { new Boolean(pk_columns.contains(parentColumnName)), fkey.getName(), currentColumnName });
                        TreeCreator.logger.log(Level.FINEST, "TreeCreator DEBUG 2<->{0}", newValue);
                        if (newValue == null && pk_columns.contains(parentColumnName)) {
                            isNull = true;
                            TreeCreator.logger.log(Level.FINEST, "TreeCreator DEBUG 3<->{0}", new Boolean(isNull));
                        }
                        TreeCreator.logger.log(Level.FINEST, "TreeCreator<->{0}<->{1}<->{2}<->{3}<->{4}<->{5}<->{6}<->{7}", new Object[] { parentColumnName, currentColumnName, newValue, new Boolean(isNull), currentRow, new Integer(k), parentTName, fkey.getName() });
                    }
                    if (!isNull) {
                        final boolean added2 = this.addToParent(parentRow2, currentNode);
                        TreeCreator.logger.log(Level.FINEST, "TreeCreator Add to parent status<->{0}", new Boolean(added2));
                        if (added2) {
                            nodeAdded = added2;
                        }
                        if (added2) {
                            continue;
                        }
                        TreeCreator.logger.log(Level.FINEST, "TreeCreator Adding latter referencing child nodes<->{0}<->{1}", new Object[] { currentNode, parentRow2 });
                        nodeAdded = true;
                        root.add(currentNode);
                        currentNode.setParentKey(parentRow2);
                        final Pointer pointer2 = this.getPointer(keyRow);
                        pointer2.addNode(currentNode);
                    }
                    else {
                        TreeCreator.logger.finest("TreeCreator PK isNull<->");
                    }
                }
                if (nodeAdded) {
                    continue;
                }
                root.add(hNode);
                hNode.setParentKey((Row)null);
                final Pointer pointer3 = this.getPointer(keyRow);
                pointer3.addNode(hNode);
            }
        }
        final long outtime = System.currentTimeMillis();
        TreeCreator.logger.log(Level.FINER, "TreeCreator MyGetRootNode<->{0}ms", new Long(outtime - intime));
        return root;
    }
    
    private boolean addToParent(final Row parentRow, final HierarchyNode hNode) {
        final Pointer parentPointer = this.pointerMap.get(parentRow);
        final Pointer pointer = this.getPointer(hNode.getKey());
        TreeCreator.logger.log(Level.FINEST, "TreeCreator DEBUGGG 1 addToParent<->{0}<->{1}<->{2}<->{3}", new Object[] { parentRow, TreeModelData.getString(hNode), parentPointer, hNode.getParentKey() });
        if (parentPointer != null) {
            final HierarchyNode currentNode = hNode;
            TreeCreator.logger.log(Level.FINEST, "TreeCreator DEBUGGGG 2 addToParent<->{0}<->", new Integer(parentPointer.getList().size()));
            currentNode.setParentKey(parentRow);
            parentPointer.addChild(currentNode, pointer);
            return true;
        }
        return false;
    }
    
    public Pointer getPointer(final Row keyRow) {
        Pointer pointer = this.pointerMap.get(keyRow);
        if (pointer == null) {
            pointer = new Pointer(this);
            this.pointerMap.put(keyRow, pointer);
        }
        return pointer;
    }
    
    private void populateTableDefinition(final Collection tables, final HashMap tdef_map) {
        try {
            for (final String tName : tables) {
                final TableDefinition tdef = MetaDataUtil.getTableDefinitionByName(tName);
                tdef_map.put(tName, tdef);
            }
        }
        catch (final MetaDataException e) {
            e.printStackTrace();
        }
    }
    
    private HashMap getRelatedKeys(final HashMap tdef_map, final Collection tableNames) {
        final long intime = System.currentTimeMillis();
        final HashMap table_map = new HashMap();
        final Iterator it = tableNames.iterator();
        while (it.hasNext()) {
            final ArrayList key_list = new ArrayList();
            final String tName = it.next();
            final TableDefinition tdef = tdef_map.get(tName);
            final List list = tdef.getForeignKeyList();
            if (list == null) {
                table_map.put(tName, key_list);
            }
            else {
                for (final ForeignKeyDefinition fkey : list) {
                    final String master = fkey.getMasterTableName();
                    if (tableNames.contains(master)) {
                        TreeCreator.logger.log(Level.FINEST, "TreeCreator tableName,fkey<->{0}<->{1}<->{2}", new Object[] { tName, master, fkey });
                        key_list.add(fkey);
                    }
                }
                table_map.put(tName, key_list);
            }
        }
        final long outtime = System.currentTimeMillis();
        TreeCreator.logger.log(Level.FINER, "TreeCreator getRelatedKeys<->{0}ms", new Long(outtime - intime));
        return table_map;
    }
    
    private void initTableDefinitions(final String treeType) {
        final long intime = System.currentTimeMillis();
        this.tdef_map = new HashMap();
        this.tree_id_list = new ArrayList();
        try {
            final DataObject treeDefinition = TreeManagerUtility.getTreeDefinition(treeType);
            TreeCreator.logger.log(Level.FINER, "initTableDefinitions 0<->{0}ms", new Long(System.currentTimeMillis() - intime));
            this.baseTreeNodeTable = (String)treeDefinition.getFirstValue("TreeDefinition", "BASETREENODETABLE");
            Iterator it = treeDefinition.getRows("TablesInTree");
            while (it.hasNext()) {
                final Row row = it.next();
                final String tName = (String)row.get("TABLENAME");
                final TableDefinition tdef = MetaDataUtil.getTableDefinitionByName(tName);
                this.tdef_map.put(tName, tdef);
            }
            final long inter = System.currentTimeMillis();
            TreeCreator.logger.log(Level.FINER, "TreeCreator initTableDefinitions 1<->{0}ms", new Long(inter - intime));
            it = treeDefinition.getRows("TreeIdentifierColumns");
            while (it.hasNext()) {
                final Row row2 = it.next();
                final String columnName = (String)row2.get("COLUMNNAME");
                this.tree_id_list.add(columnName);
            }
            TreeCreator.logger.log(Level.FINER, "TreeCreator initTableDefinitions 2<->{0}ms", new Long(System.currentTimeMillis() - inter));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        final long outtime = System.currentTimeMillis();
        TreeCreator.logger.log(Level.FINER, "TreeCreator initTableDefinitions<->{0}ms", new Long(outtime - intime));
    }
    
    private void updateRowWithPK(final TableDefinition tdef, final Row currentRow, final Row keyRow) {
        final List pk_list = tdef.getPrimaryKey().getColumnList();
        for (final String columnName : pk_list) {
            final Object value = currentRow.get(columnName);
            TreeCreator.logger.log(Level.FINEST, "TreeCreator PK<->{0}<->{1}<->{2}", new Object[] { columnName, value, currentRow });
            keyRow.set(columnName, value);
        }
    }
    
    private boolean updateRowWithFK(final TableDefinition currentTdef, final ArrayList FKs, final Row currentRow, final Row keyRow) {
        final List jl = currentTdef.getForeignKeyList();
        boolean parentValue = true;
        final Iterator il = jl.iterator();
        for (int i = 0; i < FKs.size(); ++i) {
            final String currentFK = FKs.get(i);
            final ForeignKeyDefinition fk_defn = currentTdef.getForeignKeyDefinitionByName(currentFK);
            if (fk_defn != null) {
                final List fk_columns = fk_defn.getForeignKeyColumns();
                for (final ForeignKeyColumnDefinition fkcd : fk_columns) {
                    final String sourceName = fkcd.getLocalColumnDefinition().getColumnName();
                    final String targetName = fkcd.getReferencedColumnDefinition().getColumnName();
                    final Object value = currentRow.get(sourceName);
                    keyRow.set(targetName, value);
                    if (value == null) {
                        parentValue = false;
                    }
                }
            }
        }
        return parentValue;
    }
    
    static {
        TreeCreator.logger = Logger.getLogger(TreeCreator.class.getName());
    }
}
