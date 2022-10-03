package com.me.ems.framework.common.api.v1.service;

import com.me.ems.framework.common.core.TreeNodeGroup;
import com.me.ems.framework.common.core.TreeMetaData;
import com.me.ems.framework.common.core.handlers.TreeNodeData;
import com.me.ems.framework.common.core.handlers.TreeDataHandler;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ResourceBrowserService
{
    private Logger logger;
    
    public ResourceBrowserService() {
        this.logger = Logger.getLogger(ResourceBrowserService.class.getName());
    }
    
    public Map getResourceTrees() throws APIException {
        final Map response = new HashMap();
        final List treeDatas = new ArrayList();
        try {
            final DataObject treeDO = SyMUtil.getPersistence().get("TreeDataNodeHandler", (Criteria)null);
            if (treeDO != null && !treeDO.isEmpty()) {
                final Iterator itr = treeDO.getRows("TreeDataNodeHandler");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    final Map treeData = new HashMap();
                    treeData.put("id", row.get("TREE_ID"));
                    treeData.put("name", row.get("TREE_NAME"));
                    treeDatas.add(treeData);
                }
            }
            response.put("data", treeDatas);
            response.put("count", treeDatas.size());
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in constructing Resource Type Data ", ex);
            throw new APIException("GENERIC0005");
        }
        return response;
    }
    
    public Map getResourceInfoDetails(final Long treeID, final Map userParams, final MultivaluedMap<String, String> resourceInfoMap) throws APIException {
        final Map resourceTypeInfo = new HashMap();
        try {
            final TreeDataHandler treeDataHandler = TreeDataHandler.getInstance();
            final TreeMetaData treeMetaData = treeDataHandler.getTreeMetaData(treeID);
            resourceTypeInfo.put("resources", new ArrayList());
            resourceTypeInfo.put("count", 0);
            if (treeMetaData != null) {
                this.logger.log(Level.INFO, "Resource Tree Meta Data [" + treeID + "] : " + treeMetaData.toString());
                final TreeNodeData treeNodeData = (TreeNodeData)Class.forName(treeMetaData.getDataHandlerName()).newInstance();
                final TreeNodeGroup treeNodeGroup = treeNodeData.getTreeNodeObject(treeID, userParams, resourceInfoMap);
                resourceTypeInfo.put("treeName", treeMetaData.getTreeName());
                resourceTypeInfo.put("resources", treeNodeGroup.getResources());
                resourceTypeInfo.put("count", treeNodeGroup.getTotalCount());
                resourceTypeInfo.put("properties", treeNodeGroup.getProperties());
                if (treeNodeGroup.getParent() != null) {
                    resourceTypeInfo.put("parent", treeNodeGroup.getParent());
                }
            }
            resourceTypeInfo.put("treeID", treeID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in constructing Resource Tree Data ", ex);
            throw new APIException("GENERIC0005");
        }
        return resourceTypeInfo;
    }
}
