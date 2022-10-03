package com.me.ems.framework.common.core.handlers;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.framework.common.core.TreeMetaData;
import java.util.logging.Logger;

public class TreeDataHandler
{
    public static TreeDataHandler dataHandler;
    private static Logger logger;
    
    private TreeDataHandler() {
    }
    
    public static TreeDataHandler getInstance() {
        if (TreeDataHandler.dataHandler == null) {
            TreeDataHandler.dataHandler = new TreeDataHandler();
        }
        return TreeDataHandler.dataHandler;
    }
    
    public TreeMetaData getTreeMetaData(final Long treeID) throws Exception {
        try {
            final DataObject formatDO = SyMUtil.getPersistence().get("TreeDataNodeHandler", new Criteria(new Column("TreeDataNodeHandler", "TREE_ID"), (Object)treeID, 0));
            if (formatDO != null && !formatDO.isEmpty()) {
                final Row formatRow = formatDO.getRow("TreeDataNodeHandler");
                final String treeName = (String)formatRow.get("TREE_NAME");
                final String treeHandlerName = (String)formatRow.get("TREE_HANDLER");
                final String loggerName = (String)formatRow.get("TREE_LOGGER");
                final TreeMetaData metaData = new TreeMetaData(treeID, treeName, treeHandlerName, loggerName);
                return metaData;
            }
        }
        catch (final Exception ex) {
            TreeDataHandler.logger.log(Level.SEVERE, "Exception in getting Tree/List Data for treed ID " + treeID + " : ", ex);
            throw ex;
        }
        return null;
    }
    
    static {
        TreeDataHandler.dataHandler = null;
        TreeDataHandler.logger = Logger.getLogger(TreeDataHandler.class.getName());
    }
}
