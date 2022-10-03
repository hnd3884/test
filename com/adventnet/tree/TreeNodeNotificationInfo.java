package com.adventnet.tree;

import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.persistence.Row;
import java.io.Serializable;

public class TreeNodeNotificationInfo implements Serializable, Cloneable
{
    public static final int ADD_NODE = 1000;
    public static final int DELETE_NODE = 1001;
    public static final int MOVE_NODE = 1002;
    public static final int UPDATE_NODE = 1003;
    int operation;
    long[] newTreePath;
    long[] oldTreePath;
    HierarchyNode hierarchyNode;
    Row treeIdentifier;
    String treeType;
    int nodeIndex;
    long oldNodeID;
    
    public TreeNodeNotificationInfo(final int operation, final long[] newTreePath, final long[] oldTreePath, final HierarchyNode hierarchyNode, final Row treeIdentifier, final String treeType) {
        this.oldNodeID = -10L;
        this.operation = operation;
        this.oldTreePath = oldTreePath;
        this.newTreePath = newTreePath;
        this.hierarchyNode = hierarchyNode;
        this.treeIdentifier = treeIdentifier;
        this.treeType = treeType;
    }
    
    public void setNode(final HierarchyNode node) {
        this.hierarchyNode = node;
    }
    
    public void setOldNodeID(final long oldNodeID) {
        this.oldNodeID = oldNodeID;
    }
    
    public long getOldNodeID() {
        return this.oldNodeID;
    }
    
    public int getOperation() {
        return this.operation;
    }
    
    public void setOperation(final int operation) {
        this.operation = operation;
    }
    
    public long[] getOldTreePath() {
        return this.oldTreePath;
    }
    
    public void setOldTreePath(final long[] oldTreePath) {
        this.oldTreePath = oldTreePath;
    }
    
    public long[] getNewTreePath() {
        return this.newTreePath;
    }
    
    public void setNewTreePath(final long[] newTreePath) {
        this.newTreePath = newTreePath;
    }
    
    public Row getTreeIdentifier() {
        return this.treeIdentifier;
    }
    
    public String getTreeType() {
        return this.treeType;
    }
    
    public int getNodeIndex() {
        return this.nodeIndex;
    }
    
    static String getPkColumns(final Row key) throws Exception {
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<KEY> <" + key.getTableName() + ">");
        for (int i = 0; i < key.getPKColumns().size(); ++i) {
            final String column = key.getPKColumns().get(i);
            final Object value = key.get(column);
            stringBuffer.append(column + "=" + value + " ");
        }
        stringBuffer.append("</" + key.getTableName() + "> </KEY>");
        return stringBuffer.toString();
    }
    
    static String getColumns(final Row key, final List columns) throws Exception {
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<" + key.getTableName() + ">");
        for (int i = 0; i < columns.size(); ++i) {
            final String column = columns.get(i);
            final Object value = key.get(column);
            stringBuffer.append(column + "=" + value + " ");
        }
        stringBuffer.append("</" + key.getTableName() + ">");
        return stringBuffer.toString();
    }
    
    String getDOString(final DataObject doo) throws Exception {
        if (doo == null) {
            return "NULL";
        }
        final StringBuffer buf = new StringBuffer();
        final List tableNames = doo.getTableNames();
        for (int i = 0; i < tableNames.size(); ++i) {
            final String tableName = tableNames.get(i);
            final Iterator iter = doo.getRows(tableName);
            while (iter.hasNext()) {
                final Row row = iter.next();
                buf.append("\n        " + getColumns(row, row.getColumns()));
            }
        }
        return buf.toString();
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        try {
            buf.append("\n<TREENODE_NOTIFICATION_INFO>");
            buf.append("\n  <HIERARCHY_NODE>");
            buf.append("\n     <KEY> ");
            buf.append(getPkColumns(this.hierarchyNode.getKey()));
            buf.append(" </KEY>");
            buf.append("\n     <NODE_ID = " + this.hierarchyNode.getNodeID() + ">");
            buf.append("\n     <DATA_OBJECT>");
            buf.append("\n     " + this.getDOString(this.hierarchyNode.getDataObject()));
            buf.append("\n     </DATA_OBJECT>");
            buf.append("\n  </HIERARCHY_NODE>");
            switch (this.operation) {
                case 1000: {
                    buf.append("\n\t<OPERATION = ADD_NODE>");
                    buf.append("\n\t<PATH =" + this.getPath(this.newTreePath) + ">");
                    break;
                }
                case 1001: {
                    buf.append("\n\t<OPERATION = DELETE_NODE>");
                    buf.append("\n\t<PATH =" + this.getPath(this.oldTreePath) + ">");
                    buf.append("\n\t<OLD_NODE_ID =" + this.getOldNodeID() + ">");
                    break;
                }
                case 1002: {
                    buf.append("\n\t<OPERATION = MOVE_NODE>");
                    buf.append("\n\t<OLDPATH =" + this.getPath(this.oldTreePath) + ">");
                    buf.append("\n\t<NEWPATH =" + this.getPath(this.newTreePath) + ">");
                    buf.append("\n\t<OLD_NODE_ID =" + this.getOldNodeID() + ">");
                    break;
                }
                case 1003: {
                    buf.append("\n\t<OPERATION = UPDATE_NODE>");
                    buf.append("\n\t<PATH =" + this.getPath(this.oldTreePath) + ">");
                    buf.append("\n\t<OLD_NODE_ID =" + this.getOldNodeID() + ">");
                    break;
                }
            }
            buf.append("\n\t<TREETYPE =" + this.treeType + ">");
            buf.append("\n</TREENODE_NOTIFICATION_INFO>");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
    
    List getPath(final long[] path) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < path.length; ++i) {
            list.add(new Long(path[i]));
        }
        return list;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final TreeNodeNotificationInfo info = (TreeNodeNotificationInfo)super.clone();
        info.operation = this.operation;
        info.newTreePath = this.newTreePath;
        info.oldTreePath = this.oldTreePath;
        info.hierarchyNode = (HierarchyNode)this.hierarchyNode.clone();
        info.treeIdentifier = this.treeIdentifier;
        info.treeType = this.treeType;
        info.nodeIndex = this.nodeIndex;
        return info;
    }
    
    public HierarchyNode getNode() {
        return this.hierarchyNode;
    }
}
