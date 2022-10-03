package com.adventnet.mfw;

import com.zoho.conf.Configuration;
import com.adventnet.mfw.logging.LoggerUtil;

public class StartDB
{
    public static void main(final String[] args) throws Exception {
        LoggerUtil.initLog("startDB");
        startDBServer();
    }
    
    public static void startDBServer() throws Exception {
        try {
            Configuration.setString("startscript", "true");
            Starter.loadSystemProperties();
            Starter.LoadJars();
            Starter.getNewServerClassInstance().startDB();
            System.exit(0);
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception occured while starting server. Kindly refer logs.");
            e.printStackTrace();
            throw e;
        }
    }
}
