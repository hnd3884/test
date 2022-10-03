package com.adventnet.sym.server.mdm.group;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class MDMGroupSelectListViewDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        return this.createChildTreeNodeList(requestMap);
    }
    
    private List createChildTreeNodeList(final Map requestMap) {
        final List treeNodeList = new ArrayList();
        return treeNodeList;
    }
}
