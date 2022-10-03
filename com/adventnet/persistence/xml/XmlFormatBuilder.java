package com.adventnet.persistence.xml;

import com.adventnet.persistence.PersistenceUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

class XmlFormatBuilder
{
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    private HashMap tNamePcmMap;
    private boolean swap_parent_child;
    
    XmlFormatBuilder(final boolean swap_parent) {
        this.tNamePcmMap = new HashMap();
        this.swap_parent_child = swap_parent;
    }
    
    ParentChildrenMap createFormat(final List tableNames, final String fileName) throws Exception {
        XmlFormatBuilder.LOGGER.entering(XmlFormatBuilder.CLASS_NAME, "createFormat", new Object[] { tableNames, fileName });
        final ParentChildrenMap rootPcm = new ParentChildrenMap();
        rootPcm.setElementName(fileName);
        this.tNamePcmMap.put(fileName, rootPcm);
        final XmlUseCaseResolver resolver = new XmlUseCaseResolver(tableNames, this.swap_parent_child);
        final PCMUtil pcmutil = new PCMUtil(fileName, this.tNamePcmMap);
        for (final String tableName : tableNames) {
            ParentChildrenMap pcm = new ParentChildrenMap();
            pcm.setElementName(tableName);
            pcm = resolver.resolveUseCase(pcm);
            XmlFormatBuilder.LOGGER.log(Level.FINEST, "ParentChildrenMap after resolving the UseCaseType:{0}", pcm);
            switch (pcm.getUseCaseType()) {
                case 1: {
                    pcmutil.addToRoot(pcm);
                    break;
                }
                case 2: {
                    pcmutil.addToParent(pcm);
                    break;
                }
                case 3: {
                    pcmutil.swapParentChild(pcm);
                    break;
                }
            }
            XmlFormatBuilder.LOGGER.log(Level.FINEST, "TableName Vs ParentChildrenMap:{0}", this.tNamePcmMap);
        }
        this.handleOrderOfChilds(rootPcm);
        XmlFormatBuilder.LOGGER.exiting(XmlFormatBuilder.CLASS_NAME, "createFormat", rootPcm);
        return rootPcm;
    }
    
    private void handleOrderOfChilds(final ParentChildrenMap rootPcm) throws Exception {
        final List firstLevelPcms = rootPcm.getChildPCMs();
        if (firstLevelPcms == null) {
            return;
        }
        for (final ParentChildrenMap pcm : firstLevelPcms) {
            final List childPcms = pcm.getChildPCMs();
            this.handleOrderOfChilds(pcm, childPcms);
        }
    }
    
    private void handleOrderOfChilds(final ParentChildrenMap parentPcm, final List childPcms) throws Exception {
        if (childPcms == null) {
            return;
        }
        final HashMap groupingTagPcms = new HashMap();
        final Iterator childIterator = childPcms.iterator();
        ArrayList tableNames = new ArrayList();
        while (childIterator.hasNext()) {
            final ParentChildrenMap pcm = childIterator.next();
            if (pcm.isGroupingTag()) {
                final ParentChildrenMap childPcm = pcm.getChildPCMs().get(0);
                tableNames.add(childPcm.getElementName());
                groupingTagPcms.put(childPcm.getElementName(), pcm);
            }
            else {
                tableNames.add(pcm.getElementName());
            }
        }
        tableNames = (ArrayList)PersistenceUtil.sortTables(tableNames);
        parentPcm.removeAllChildPCMs();
        for (final String tableName : tableNames) {
            final ParentChildrenMap pcm2 = this.tNamePcmMap.get(tableName);
            if (groupingTagPcms.containsKey(tableName)) {
                parentPcm.addChildPCM(groupingTagPcms.get(tableName));
            }
            else {
                parentPcm.addChildPCM(pcm2);
            }
        }
        for (final String tableName : tableNames) {
            final ParentChildrenMap pcm2 = this.tNamePcmMap.get(tableName);
            this.handleOrderOfChilds(pcm2, pcm2.getChildPCMs());
        }
    }
    
    static {
        CLASS_NAME = XmlFormatBuilder.class.getName();
        LOGGER = Logger.getLogger(XmlFormatBuilder.CLASS_NAME);
    }
}
