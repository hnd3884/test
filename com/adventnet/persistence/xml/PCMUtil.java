package com.adventnet.persistence.xml;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;

class PCMUtil
{
    private String fileName;
    private HashMap tNamePcmMap;
    private HashMap bdfkChildVsParent;
    private ArrayList overLapParents;
    private ArrayList disjointHandledTables;
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    
    PCMUtil(final String fileName, final HashMap tNamePcmMap) {
        this.bdfkChildVsParent = new HashMap();
        this.overLapParents = new ArrayList();
        this.disjointHandledTables = new ArrayList();
        this.fileName = fileName;
        this.tNamePcmMap = tNamePcmMap;
    }
    
    void addToRoot(final ParentChildrenMap pcm) {
        PCMUtil.LOGGER.entering(PCMUtil.CLASS_NAME, "addToRoot", pcm);
        final ParentChildrenMap rootPcm = this.tNamePcmMap.get(this.fileName);
        pcm.addParentElementName(this.fileName);
        rootPcm.addChildPCM(pcm);
        this.tNamePcmMap.put(pcm.getElementName(), pcm);
        if (pcm.isGroupingTag()) {
            final ParentChildrenMap childPcm = pcm.getChildPCMs().get(0);
            this.tNamePcmMap.put(childPcm.getElementName(), childPcm);
        }
        PCMUtil.LOGGER.exiting(PCMUtil.CLASS_NAME, "addToRoot", this.tNamePcmMap);
    }
    
    void addToParent(final ParentChildrenMap pcm) {
        PCMUtil.LOGGER.entering(PCMUtil.CLASS_NAME, "addToParent", pcm);
        final List list = pcm.getParentElementNames();
        final String parentElementName = list.get(list.size() - 1);
        final ParentChildrenMap parentPcm = this.tNamePcmMap.get(parentElementName);
        PCMUtil.LOGGER.log(Level.FINEST, "ParentPcm:{0}", parentPcm);
        parentPcm.addChildPCM(pcm);
        this.tNamePcmMap.put(pcm.getElementName(), pcm);
        if (pcm.isGroupingTag()) {
            final ParentChildrenMap childPcm = pcm.getChildPCMs().get(0);
            this.tNamePcmMap.put(childPcm.getElementName(), childPcm);
        }
        PCMUtil.LOGGER.exiting(PCMUtil.CLASS_NAME, "addToParent", this.tNamePcmMap);
    }
    
    void swapParentChild(final ParentChildrenMap pcm) throws DataAccessException {
        PCMUtil.LOGGER.entering(PCMUtil.CLASS_NAME, "swapParentChild", pcm);
        List list = pcm.getParentElementNames();
        final String parentElementName = list.get(list.size() - 1);
        if (this.overLapParents.contains(parentElementName)) {
            pcm.setUseCaseType(2);
            this.addToParent(pcm);
            PCMUtil.LOGGER.exiting(PCMUtil.CLASS_NAME, "swapParentChild", pcm);
            return;
        }
        final ParentChildrenMap parentPcm = this.tNamePcmMap.get(parentElementName);
        PCMUtil.LOGGER.log(Level.FINEST, "ParentPCM:{0}", parentPcm);
        if (this.bdfkChildVsParent.containsValue(parentElementName)) {
            PCMUtil.LOGGER.log(Level.FINEST, "Already a slave table with bdfk exists for the master table {0}.", parentElementName);
            final HashMap tables = new HashMap();
            tables.put("parentTableName", parentElementName);
            tables.put("tableName", pcm.getElementName());
            final Iterator keyIterator = this.bdfkChildVsParent.keySet().iterator();
            String bdfkTableName = null;
            while (keyIterator.hasNext()) {
                final String childTableName = keyIterator.next();
                if (this.bdfkChildVsParent.get(childTableName).equals(parentElementName)) {
                    bdfkTableName = childTableName;
                    PCMUtil.LOGGER.log(Level.FINEST, "{0} refers to the master table {1} with bdfk set to true.", new Object[] { childTableName, parentElementName });
                    tables.put("childTableName", childTableName);
                }
            }
            this.overLapParents.add(parentElementName);
            this.handleOverLap(tables);
            pcm.setUseCaseType(2);
            this.addToParent(pcm);
            PCMUtil.LOGGER.exiting(PCMUtil.CLASS_NAME, "swapParentChild", this.tNamePcmMap);
            return;
        }
        this.bdfkChildVsParent.put(pcm.getElementName(), parentElementName);
        list = parentPcm.getParentElementNames();
        final String pparentElementName = list.get(list.size() - 1);
        final ParentChildrenMap pparentPcm = this.tNamePcmMap.get(pparentElementName);
        pparentPcm.removeChildPCM(parentPcm);
        parentPcm.removeParentElementName(pparentPcm.getElementName());
        parentPcm.addParentElementName(pcm.getElementName());
        pcm.addChildPCM(parentPcm);
        pcm.removeParentElementName(parentPcm.getElementName());
        pcm.addParentElementName(pparentPcm.getElementName());
        if (pparentPcm.getElementName().equals(this.fileName)) {
            pcm.setUseCaseType(1);
        }
        parentPcm.setUseCaseType(2);
        pparentPcm.addChildPCM(pcm);
        this.tNamePcmMap.put(pcm.getElementName(), pcm);
        PCMUtil.LOGGER.exiting(PCMUtil.CLASS_NAME, "swapParentChild", this.tNamePcmMap);
    }
    
    void handleOverLap(final HashMap tables) {
        PCMUtil.LOGGER.entering(PCMUtil.CLASS_NAME, "handleOverLap", tables);
        final String childTableName = tables.get("childTableName");
        final String parentTableName = tables.get("parentTableName");
        final ParentChildrenMap parentPcm = this.tNamePcmMap.get(parentTableName);
        final ParentChildrenMap childPcm = this.tNamePcmMap.get(childTableName);
        PCMUtil.LOGGER.log(Level.FINEST, "ParentPcm:{0}", parentPcm);
        PCMUtil.LOGGER.log(Level.FINEST, "ChildPcm:{0}", childPcm);
        parentPcm.removeParentElementName(childTableName);
        childPcm.removeChildPCM(parentPcm);
        final String superTableName = childPcm.getParentElementNames().get(0);
        childPcm.removeParentElementName(superTableName);
        childPcm.addParentElementName(parentTableName);
        childPcm.setUseCaseType(2);
        parentPcm.addChildPCM(childPcm);
        final ParentChildrenMap superPcm = this.tNamePcmMap.get(superTableName);
        superPcm.removeChildPCM(childPcm);
        superPcm.addChildPCM(parentPcm);
        parentPcm.addParentElementName(superTableName);
        if (superTableName.equals(this.fileName)) {
            parentPcm.setUseCaseType(1);
        }
        PCMUtil.LOGGER.exiting(PCMUtil.CLASS_NAME, "handleOverLap");
    }
    
    static {
        CLASS_NAME = PCMUtil.class.getName();
        LOGGER = Logger.getLogger(PCMUtil.CLASS_NAME);
    }
}
