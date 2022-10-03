package com.adventnet.mfw;

import java.net.URLClassLoader;
import com.adventnet.mfw.logging.LoggerUtil;

public class ReinitializeDB
{
    public static void main(final String[] args) throws Exception {
        String forceful = "false";
        LoggerUtil.initLog("reinitialize");
        if (args.length > 0) {
            forceful = args[0];
        }
        reinitDB(forceful);
    }
    
    public static void reinitDB(final String forceful) throws Exception {
        final boolean allTables = forceful.equalsIgnoreCase("true");
        Starter.loadSystemProperties();
        Starter.LoadJars();
        final URLClassLoader ucl = (URLClassLoader)Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(ucl);
        final String serverClassStr = System.getProperty("server.class", "com.zoho.mickey.startup.MEServer");
        final Class serverClass = ucl.loadClass(serverClassStr);
        final ServerInterface server = serverClass.newInstance();
        if (server.reinitialize(allTables) == 0) {
            System.exit(0);
        }
        else {
            System.exit(1);
        }
    }
}
