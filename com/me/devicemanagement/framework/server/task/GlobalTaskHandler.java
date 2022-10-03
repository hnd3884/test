package com.me.devicemanagement.framework.server.task;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class GlobalTaskHandler
{
    private static List<GlobalTask> globalTaskClassList;
    private static GlobalTaskHandler globalTaskHandler;
    private Logger logger;
    
    public GlobalTaskHandler() {
        this.logger = Logger.getLogger(GlobalTaskHandler.class.getName());
    }
    
    public static GlobalTaskHandler getInstance() {
        if (GlobalTaskHandler.globalTaskHandler == null) {
            GlobalTaskHandler.globalTaskHandler = new GlobalTaskHandler();
        }
        return GlobalTaskHandler.globalTaskHandler;
    }
    
    public void addGlobalTaskListener(final GlobalTask taskListener) {
        this.logger.log(Level.INFO, "addGlobalTaskListener() called : {0}", taskListener.getClass().getName());
        GlobalTaskHandler.globalTaskClassList.add(taskListener);
    }
    
    public void removeGlobalTaskListener(final GlobalTask taskListener) {
        this.logger.log(Level.INFO, "removeGlobalTaskListener() called : {0}", taskListener.getClass().getName());
        GlobalTaskHandler.globalTaskClassList.remove(taskListener);
    }
    
    public void invokeGlobalTaskListeners() {
        final int l = GlobalTaskHandler.globalTaskClassList.size();
        this.logger.log(Level.INFO, "invokeGlobalTaskListeners() called : {0}", GlobalTaskHandler.globalTaskClassList.toString());
        for (int s = 0; s < l; ++s) {
            final GlobalTask listener = GlobalTaskHandler.globalTaskClassList.get(s);
            listener.executeTask();
        }
    }
    
    static {
        GlobalTaskHandler.globalTaskClassList = new ArrayList<GlobalTask>();
        GlobalTaskHandler.globalTaskHandler = null;
    }
}
