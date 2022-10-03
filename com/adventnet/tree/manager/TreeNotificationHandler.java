package com.adventnet.tree.manager;

import java.util.logging.Level;
import com.adventnet.mfw.message.Messenger;
import com.adventnet.tree.TreeNodeNotificationInfo;
import com.adventnet.tree.TreeManagerUtility;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.tree.HierarchyNode;
import com.adventnet.tree.TreeException;
import java.util.logging.Logger;

public class TreeNotificationHandler
{
    private String treeNotificationTopicName;
    Logger logger;
    
    public TreeNotificationHandler() throws TreeException {
        this.treeNotificationTopicName = "topic/TreeNotificationTopic";
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public void sendTreeNodeNotification(final int operation, final long[] newPath, final long[] oldPath, final HierarchyNode hierarchyNode, final Row treeIdentifier, final DataObject tdef) {
        this.sendTreeNodeNotification(operation, newPath, oldPath, hierarchyNode, treeIdentifier, tdef, -5L);
    }
    
    public void sendTreeNodeNotification(final int operation, final long[] newPath, final long[] oldPath, final HierarchyNode hierarchyNode, final Row treeIdentifier, final DataObject tdef, final long oldNodeID) {
        try {
            TreeNodeNotificationInfo treeNodeNotificationInfo = null;
            treeNodeNotificationInfo = new TreeNodeNotificationInfo(operation, newPath, oldPath, hierarchyNode, treeIdentifier, TreeManagerUtility.getTreeType(tdef));
            treeNodeNotificationInfo.setOldNodeID(oldNodeID);
            Messenger.publish(this.treeNotificationTopicName, (Object)treeNodeNotificationInfo);
        }
        catch (final Exception ne) {
            this.logger.log(Level.SEVERE, "cannot send notifications", ne);
        }
    }
    
    public void cleanup() throws Exception {
        this.logger.info(" treeNotificationTopicPublisher has been closed");
    }
}
