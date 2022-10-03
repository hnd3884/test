package com.adventnet.client.util;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import com.adventnet.mfw.bean.BeanUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.tree.manager.TreeManager;
import com.adventnet.customview.CustomViewManager;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.persistence.Persistence;

public class LookUpUtil
{
    private static Persistence pers;
    private static ReadOnlyPersistence cachePers;
    private static CustomViewManager cvTreeMgr;
    private static CustomViewManager cvTableMgr;
    private static CustomViewManager mdsCvMgr;
    private static TreeManager treeMgr;
    private static String clientConfigNameSpace;
    private static Logger out;
    
    public static Persistence getPersistence() {
        try {
            if (LookUpUtil.clientConfigNameSpace != null) {
                LookUpUtil.out.log(Level.FINE, "Inside if in getPersistence method of {0}", "user specific for SAS");
                return (Persistence)BeanUtil.lookup("Persistence", (Object)LookUpUtil.clientConfigNameSpace);
            }
            LookUpUtil.out.log(Level.FINE, "Inside else in getPersistence method of {0}", "user specific for SAS");
            if (LookUpUtil.pers == null) {
                LookUpUtil.pers = (Persistence)BeanUtil.lookup("Persistence");
            }
            return LookUpUtil.pers;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static ReadOnlyPersistence getCachedPersistence() {
        try {
            if (LookUpUtil.clientConfigNameSpace != null) {
                LookUpUtil.out.log(Level.FINE, "Inside if in getCachedPersistence method of {0}", "user specific for SAS");
                return (ReadOnlyPersistence)BeanUtil.lookup("CachedPersistence", (Object)LookUpUtil.clientConfigNameSpace);
            }
            LookUpUtil.out.log(Level.FINE, "Inside else in getCachedPersistence method of {0}", "user specific for SAS");
            if (LookUpUtil.cachePers == null) {
                LookUpUtil.cachePers = (ReadOnlyPersistence)BeanUtil.lookup("CachedPersistence");
            }
            return LookUpUtil.cachePers;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static CustomViewManager getCVManagerForTree() {
        try {
            if (LookUpUtil.cvTreeMgr == null) {
                LookUpUtil.cvTreeMgr = (CustomViewManager)BeanUtil.lookup("TreeViewManager");
            }
            return LookUpUtil.cvTreeMgr;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static CustomViewManager getCVManagerForTable() {
        try {
            if (LookUpUtil.clientConfigNameSpace != null) {
                LookUpUtil.out.log(Level.FINE, "Inside if in getCVManagerForTable method of {0}", "user specific for SAS");
                return (CustomViewManager)BeanUtil.lookup("TableViewManager", (Object)LookUpUtil.clientConfigNameSpace);
            }
            LookUpUtil.out.log(Level.FINE, "Inside else in getCVManagerForTable method of {0}", "user specific for SAS");
            if (LookUpUtil.cvTableMgr == null) {
                LookUpUtil.cvTableMgr = (CustomViewManager)BeanUtil.lookup("TableViewManager");
            }
            return LookUpUtil.cvTableMgr;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static CustomViewManager getCVManagerForMDS() {
        try {
            if (LookUpUtil.mdsCvMgr == null) {
                LookUpUtil.mdsCvMgr = (CustomViewManager)BeanUtil.lookup("MDSTableViewManager");
            }
            return LookUpUtil.mdsCvMgr;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static TreeManager getMenuTreeManager() {
        try {
            if (LookUpUtil.treeMgr == null) {
                LookUpUtil.treeMgr = (TreeManager)BeanUtil.lookup("MenuTreeManager");
            }
            return LookUpUtil.treeMgr;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static DataObject getRecord(final List personalities, final List deepRetrievedPersonalities, final Criteria condition) throws Exception {
        return getPersistence().getForPersonalities(personalities, deepRetrievedPersonalities, condition);
    }
    
    public static DataObject addRecord(final DataObject recordsDO) throws Exception {
        return getPersistence().add(recordsDO);
    }
    
    public static DataObject updateRecord(final DataObject recordsDO) throws Exception {
        return getPersistence().update(recordsDO);
    }
    
    public static void deleteRecord(final Row confRecord) throws Exception {
    }
    
    public static Persistence getUserPersistence() {
        try {
            if (LookUpUtil.pers == null) {
                LookUpUtil.pers = (Persistence)BeanUtil.lookup("Persistence");
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return LookUpUtil.pers;
    }
    
    public static ReadOnlyPersistence getCachedUserPersistence() {
        try {
            if (LookUpUtil.cachePers == null) {
                LookUpUtil.cachePers = (ReadOnlyPersistence)BeanUtil.lookup("CachedPersistence");
            }
            return LookUpUtil.cachePers;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    static {
        LookUpUtil.mdsCvMgr = null;
        LookUpUtil.clientConfigNameSpace = System.getProperty("client.config.namespace");
        LookUpUtil.out = Logger.getLogger(LookUpUtil.class.getName());
    }
}
