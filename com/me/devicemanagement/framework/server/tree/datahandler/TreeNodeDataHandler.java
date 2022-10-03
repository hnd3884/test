package com.me.devicemanagement.framework.server.tree.datahandler;

import com.me.devicemanagement.framework.server.tree.NodeSettings;
import org.json.JSONObject;
import java.util.Map;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.tree.TreeMetaData;
import java.util.HashMap;
import java.util.logging.Logger;

public class TreeNodeDataHandler
{
    protected static Logger logger;
    private static String sourceClass;
    private final HashMap mTreeMetaDataHash;
    public static TreeNodeDataHandler dataHandler;
    
    public TreeNodeDataHandler() {
        this.mTreeMetaDataHash = new HashMap();
    }
    
    public static TreeNodeDataHandler getInstance(final int treeId) {
        (TreeNodeDataHandler.dataHandler = new TreeNodeDataHandler()).loadStaticDataToInMemory(treeId);
        return TreeNodeDataHandler.dataHandler;
    }
    
    private void loadStaticDataToInMemory(final int treeId) {
        final TreeMetaData metaData = this.createTreeMetaData(treeId);
        this.mTreeMetaDataHash.put(metaData.treeId, metaData);
    }
    
    private TreeMetaData createTreeMetaData(final int treeId) {
        final TreeMetaData metaData = new TreeMetaData();
        switch (treeId) {
            case 501: {
                metaData.treeId = 501;
                metaData.treeName = "LocationSettingsGroup";
                metaData.treeDataHandlerName = "com.me.mdm.server.tree.datahandler.GroupListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 502: {
                metaData.treeId = 502;
                metaData.treeName = "LocationSettingsDevice";
                metaData.treeDataHandlerName = "com.me.mdm.server.tree.datahandler.DeviceListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 504: {
                metaData.treeId = 504;
                metaData.treeName = "staticUniqueMDMGroups";
                metaData.treeDataHandlerName = "com.me.mdm.server.tree.datahandler.MDMStaticUniqueGroupListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 505: {
                metaData.treeId = 505;
                metaData.treeName = "knoxLicenseDistributeGroups";
                metaData.treeDataHandlerName = "com.me.mdm.server.tree.datahandler.MDMAndroidGroupsListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 801: {
                metaData.treeId = 801;
                metaData.treeName = "dcStaticUniqueGroups";
                metaData.treeDataHandlerName = "com.me.dc.server.tree.datahandler.DCGroupListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 802: {
                metaData.treeId = 802;
                metaData.treeName = "dcRemoteOfficeGroups";
                metaData.treeDataHandlerName = "com.me.dc.server.tree.datahandler.RemoteOfficeListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 803: {
                metaData.treeId = 803;
                metaData.treeName = "dcDSLogGroups";
                metaData.treeDataHandlerName = "com.me.dc.server.tree.datahandler.RemoteOfficeListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 901: {
                metaData.treeId = 901;
                metaData.treeName = "adUsers";
                metaData.treeDataHandlerName = "com.me.dc.server.tree.datahandler.ADUserListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 902: {
                metaData.treeId = 902;
                metaData.treeName = "easMailBoxes";
                metaData.treeDataHandlerName = "com.me.mdm.server.easmanagement.EASMailListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 2001: {
                metaData.treeId = 2001;
                metaData.treeName = "ADGroups";
                metaData.treeDataHandlerName = "com.me.mdm.server.enrollment.ADGroupsTreeNodeDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 2002: {
                metaData.treeId = 2002;
                metaData.treeName = "ADGroupsSelect";
                metaData.treeDataHandlerName = "com.me.mdm.server.enrollment.SelectedADGroupsTreeNodeDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 1001: {
                metaData.treeId = 1001;
                metaData.treeName = "mdmGroupMembers";
                metaData.treeDataHandlerName = "com.adventnet.sym.server.mdm.group.MDMGroupListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 1002: {
                metaData.treeId = 1002;
                metaData.treeName = "mdmGroupMemberSelect";
                metaData.treeDataHandlerName = "com.adventnet.sym.server.mdm.group.MDMGroupSelectListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 1003: {
                metaData.treeId = 1003;
                metaData.treeName = "mdmProfiles";
                metaData.treeDataHandlerName = "com.adventnet.sym.server.mdm.config.MDMProfileListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 1004: {
                metaData.treeId = 1004;
                metaData.treeName = "mdmApps";
                metaData.treeDataHandlerName = "com.adventnet.sym.server.mdm.config.MDMAppListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            case 1005: {
                metaData.treeId = 1005;
                metaData.treeName = "mdmGroupList";
                metaData.treeDataHandlerName = "com.me.mdm.server.tree.datahandler.GroupMoveListViewDataHandler";
                metaData.loggerName = "default";
                break;
            }
            default: {
                try {
                    final DataObject formatDO = SyMUtil.getPersistence().get("TreeDataNodeHandler", new Criteria(new Column("TreeDataNodeHandler", "TREE_ID"), (Object)treeId, 0));
                    if (formatDO != null && !formatDO.isEmpty()) {
                        final Row formatRow = formatDO.getRow("TreeDataNodeHandler");
                        metaData.treeId = treeId;
                        metaData.treeName = (String)formatRow.get("TREE_NAME");
                        metaData.treeDataHandlerName = (String)formatRow.get("TREE_HANDLER");
                        metaData.loggerName = (String)formatRow.get("TREE_LOGGER");
                        return metaData;
                    }
                }
                catch (final Exception ex) {
                    TreeNodeDataHandler.logger.logp(Level.INFO, TreeNodeDataHandler.sourceClass, "createTreeMetaData", null, ex);
                }
                break;
            }
        }
        return metaData;
    }
    
    public JSONObject getTreeJSONObject(final Map requestMap) throws Exception {
        final String sourceMethod = "getJSONObject";
        TreeMetaData tMetaData = null;
        JSONObject jsonObject = null;
        TreeNodeDataObject treeData = null;
        final NodeSettings nodeSettings = this.getNodeSettings(requestMap);
        tMetaData = this.mTreeMetaDataHash.get(nodeSettings.treeId);
        TreeNodeDataHandler.logger.logp(Level.INFO, TreeNodeDataHandler.sourceClass, sourceMethod, tMetaData.toString());
        treeData = (TreeNodeDataObject)Class.forName(tMetaData.treeDataHandlerName).newInstance();
        jsonObject = treeData.getJSONObject(nodeSettings);
        return jsonObject;
    }
    
    public JSONObject getToolTipJSONObject(final Map requestMap) throws Exception {
        final String sourceMethod = "getJSONObject";
        TreeMetaData tMetaData = null;
        JSONObject jsonObject = null;
        TreeNodeDataObject treeData = null;
        final NodeSettings nodeSettings = this.getNodeSettings(requestMap);
        tMetaData = this.mTreeMetaDataHash.get(nodeSettings.treeId);
        TreeNodeDataHandler.logger.logp(Level.INFO, TreeNodeDataHandler.sourceClass, sourceMethod, tMetaData.toString());
        treeData = (TreeNodeDataObject)Class.forName(tMetaData.treeDataHandlerName).newInstance();
        jsonObject = treeData.getJSONObject(nodeSettings);
        return jsonObject;
    }
    
    private NodeSettings getNodeSettings(final Map hMap) throws Exception {
        NodeSettings nodeSettings = null;
        int treeId = -1;
        if (hMap.isEmpty()) {
            return nodeSettings;
        }
        nodeSettings = new NodeSettings();
        final String temp_treeTypeID = hMap.get("treeId");
        if (temp_treeTypeID != null && !temp_treeTypeID.equalsIgnoreCase("")) {
            treeId = Integer.parseInt(temp_treeTypeID);
        }
        nodeSettings.treeId = treeId;
        nodeSettings.userData = hMap;
        nodeSettings.isTreeView = true;
        nodeSettings.isTraverse = ((hMap.get("is_traverse") == null) ? "" : hMap.get("is_traverse"));
        nodeSettings.parent_id = ((hMap.get("parent_id") == null) ? "" : hMap.get("parent_id"));
        return nodeSettings;
    }
    
    static {
        TreeNodeDataHandler.logger = Logger.getLogger(TreeNodeDataHandler.class.getName());
        TreeNodeDataHandler.sourceClass = "TreeNodeDataHandler";
        TreeNodeDataHandler.dataHandler = null;
    }
}
