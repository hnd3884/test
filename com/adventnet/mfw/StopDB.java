package com.adventnet.mfw;

import com.zoho.conf.Configuration;
import com.adventnet.mfw.logging.LoggerUtil;

public class StopDB
{
    public static void main(final String[] args) throws Exception {
        LoggerUtil.initLog("stopDB");
        stopDBServer();
    }
    
    public static void stopDBServer() throws Exception {
        try {
            Configuration.setString("stopscript", "true");
            Starter.loadSystemProperties();
            Starter.LoadJars();
            Starter.getNewServerClassInstance().stopDB();
            System.exit(0);
        }
        catch (final Exception e) {
            e.printStackTrace();
            ConsoleOut.println(e.getMessage());
            throw e;
        }
    }
}
