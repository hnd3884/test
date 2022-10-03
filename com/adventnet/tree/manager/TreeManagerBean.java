package com.adventnet.tree.manager;

import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import java.util.List;
import com.adventnet.tree.query.TreeQuery;
import com.adventnet.tree.TreeException;
import com.adventnet.tree.HierarchyNode;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.tree.TreeManagerUtility;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.bean.Initializable;

public class TreeManagerBean implements TreeManager, Initializable
{
    String treeType;
    TreeManagerImpl localTreeManager;
    Logger logger;
    
    public TreeManagerBean() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public void initialize(final DataObject dobj) throws Exception {
        System.out.println("Test Print=Inside Intialize==" + dobj);
        final Iterator properties = dobj.get("BeanProperties", "PROPERTY");
        final Iterator values = dobj.get("BeanProperties", "VALUE");
        final HashMap propertyMap = new HashMap();
        while (properties.hasNext() && values.hasNext()) {
            propertyMap.put(properties.next(), values.next());
        }
        System.out.println("Test Print=property Map==" + propertyMap);
        this.treeType = propertyMap.get("TreeType");
        final DataObject tdef = TreeManagerUtility.getTreeDefinition(this.treeType);
        this.localTreeManager = new TreeManagerImpl(tdef);
    }
    
    public void setTreeType(final String treetype) {
        this.treeType = treetype;
    }
    
    public String getTreeType() {
        return this.treeType;
    }
    
    @Override
    public HierarchyNode addNode(final Row treeIdentifier, final HierarchyNode hierarchyNode) throws TreeException {
        try {
            return this.localTreeManager.addNode(treeIdentifier, hierarchyNode);
        }
        catch (final TreeException te) {
            throw te;
        }
    }
    
    @Override
    public HierarchyNode deleteNode(final Row treeIdentifier, final Row startingParentKey) throws TreeException {
        try {
            return this.localTreeManager.deleteNode(treeIdentifier, startingParentKey);
        }
        catch (final TreeException te) {
            throw te;
        }
    }
    
    @Override
    public HierarchyNode moveNode(final Row treeIdentifier, final Row key, final Row newParentKey) throws TreeException {
        try {
            return this.localTreeManager.moveNode(treeIdentifier, key, newParentKey);
        }
        catch (final TreeException te) {
            throw te;
        }
    }
    
    @Override
    public HierarchyNode getNode(final Row treeIdentifier, final Row key, final int numberOfLevels) throws TreeException {
        try {
            return this.localTreeManager.getNode(treeIdentifier, key, numberOfLevels);
        }
        catch (final TreeException te) {
            throw te;
        }
    }
    
    @Override
    public HierarchyNode getNode(final Row treeIdentifier, final Row key) throws TreeException {
        try {
            return this.localTreeManager.getNode(treeIdentifier, key);
        }
        catch (final TreeException te) {
            throw te;
        }
    }
    
    @Override
    public HierarchyNode getNode(final TreeQuery treeQuery) throws TreeException {
        try {
            return this.localTreeManager.getNode(treeQuery);
        }
        catch (final TreeException te) {
            throw te;
        }
    }
    
    @Override
    public HierarchyNode getAncestors(final Row treeIdentifier, final Row key) throws TreeException {
        try {
            return this.localTreeManager.getAncestors(treeIdentifier, key);
        }
        catch (final TreeException te) {
            throw te;
        }
    }
    
    @Override
    public HierarchyNode getAncestors(final Row treeIdentifier, final Row key, final int level) throws TreeException {
        try {
            return this.localTreeManager.getAncestors(treeIdentifier, key, level);
        }
        catch (final TreeException te) {
            throw te;
        }
    }
    
    @Override
    public long[] getTreePath(final Row treeIdentifier, final Row key) throws TreeException {
        try {
            return this.localTreeManager.getTreePath(treeIdentifier, key);
        }
        catch (final TreeException te) {
            throw te;
        }
    }
    
    @Override
    public int levelOfElement(final Row treeIdentifier, final Row key) throws TreeException {
        return this.localTreeManager.levelOfElement(treeIdentifier, key);
    }
    
    @Override
    public Map getAncestors(final Row treeIdentifier, final List keyList) throws TreeException {
        return this.localTreeManager.getAncestors(treeIdentifier, keyList);
    }
    
    @Override
    public SelectQuery getAncestorQuery(final Row treeIdentifier, final Row key) throws TreeException {
        return this.localTreeManager.getAncestorQuery(treeIdentifier, key);
    }
}
