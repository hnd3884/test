package com.adventnet.mfw;

import java.util.logging.Level;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.Console;
import com.adventnet.mfw.logging.LoggerUtil;
import java.util.logging.Logger;

public class RestoreDB
{
    private static final Logger LOGGER;
    public static boolean RESTORING_DB_USING_SCRIPTS;
    
    public static void main(final String[] args) throws Exception {
        LoggerUtil.initLog("restore");
        if (args.length == 0) {
            ConsoleOut.println("File to be restored is not specified");
            System.exit(1);
        }
        final String src = args[0];
        String password = null;
        if (args.length == 3) {
            if (args[1].equals("-p")) {
                password = args[2];
            }
        }
        else if (args.length == 2 && args[1].equals("-p")) {
            final Console console = System.console();
            if (console == null) {
                ConsoleOut.println("Password for the file to be restored is not specified");
                System.exit(1);
            }
            else {
                char[] passwordArray;
                while (true) {
                    passwordArray = console.readPassword("Enter Password : ", new Object[0]);
                    if (passwordArray.length > 0) {
                        break;
                    }
                    console.printf("Password cannot be empty\n", new Object[0]);
                }
                password = new String(passwordArray);
            }
        }
        restoreDB(src, password);
    }
    
    public static void restoreDB(final String srcFile, final String password) {
        if (srcFile == null || srcFile.equals("")) {
            ConsoleOut.println("File to be restored is not specified");
            System.exit(1);
        }
        final String src = srcFile;
        boolean exitStatus = true;
        try {
            if (!new File(src).exists()) {
                ConsoleOut.println("Please enter a valid File Name [" + src + "].");
                throw new FileNotFoundException("Please enter a valid File Name [" + src + "].");
            }
            Starter.loadSystemProperties();
            if (!Starter.checkShutdownListenerPort()) {
                ConsoleOut.println("\nServer seems to be running. Please shutdown server.\n");
                throw new RuntimeException("Server seems to be running. Please shutdown server.");
            }
            RestoreDB.LOGGER.log(Level.INFO, "Restore DB Started");
            final ServerInterface server = Starter.getNewServerClassInstance();
            Starter.initializeDiskSpaceMonitor();
            server.restoreDB(src, password);
        }
        catch (final Exception e) {
            RestoreDB.LOGGER.log(Level.INFO, "Problem while Restoring DB. ", e);
            e.printStackTrace();
            exitStatus = false;
        }
        finally {
            Starter.shutDownDiskSpaceMonitor();
            if (exitStatus) {
                System.exit(0);
            }
            else {
                System.exit(1);
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(RestoreDB.class.getName());
        RestoreDB.RESTORING_DB_USING_SCRIPTS = false;
    }
}
