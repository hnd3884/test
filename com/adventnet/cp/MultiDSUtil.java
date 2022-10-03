package com.adventnet.cp;

import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.logging.Logger;

public class MultiDSUtil
{
    public static int readIndex;
    static Logger OUT;
    private static boolean isEnabled;
    private static ThreadLocal<Integer> readDB;
    
    public static void setThreadLocal() {
        MultiDSUtil.readDB.set(MultiDSUtil.readIndex);
    }
    
    public static Integer getThreadLocal() {
        return MultiDSUtil.readDB.get();
    }
    
    public static void removeThreadLocal() {
        MultiDSUtil.readDB.remove();
    }
    
    public static boolean isMultiDataSourceEnabled() {
        return MultiDSUtil.isEnabled;
    }
    
    public static void enableMWSR() {
        MultiDSUtil.isEnabled = true;
    }
    
    public static void disableMWSR() {
        MultiDSUtil.isEnabled = false;
    }
    
    public static void setDefaultDB(final String defaultDatabase) {
        if (PersistenceInitializer.getDatabases().contains(defaultDatabase)) {
            MultiDSUtil.readIndex = PersistenceInitializer.getDatabases().indexOf(defaultDatabase);
        }
        else {
            MultiDSUtil.OUT.log(Level.SEVERE, defaultDatabase + " is not configured");
        }
    }
    
    public static String getDefaultDB() {
        return PersistenceInitializer.getDatabases().get(MultiDSUtil.readDB.get());
    }
    
    static {
        MultiDSUtil.readIndex = -1;
        MultiDSUtil.OUT = Logger.getLogger(MultiDSUtil.class.getName());
        MultiDSUtil.isEnabled = false;
        MultiDSUtil.readDB = new ThreadLocal<Integer>();
    }
}
