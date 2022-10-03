package com.adventnet.mfw;

import com.zoho.conf.Configuration;
import java.io.Console;
import java.util.Locale;
import com.adventnet.mfw.logging.LoggerUtil;
import com.zoho.net.handshake.HandShakeUtil;
import java.util.Properties;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackupDB
{
    private static String server_home;
    private static final String BACKUP_DIR;
    private static final Logger out;
    public static boolean SHOW_STATUS;
    public static boolean BACKUP_DB_USING_SCRIPTS;
    
    public static void backupDB(final String backupDir, final String backupFile, final int backupContentType, final String archivePassword) throws Exception {
        checkAndCreateBackupDir(backupDir);
        BackupDB.out.log(Level.INFO, "BackingUp DB Started");
        getServerClass().backupDB(backupDir, backupFile, backupContentType, archivePassword);
        BackupDB.out.log(Level.INFO, "BackingUp DB Completed");
    }
    
    private static void checkAndCreateBackupDir(final String backupDir) {
        if (backupDir != null) {
            final File file = new File(backupDir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }
    
    private static ServerInterface getServerClass() throws Exception {
        return Starter.getNewServerClassInstance();
    }
    
    public static void fileBackUp(final String backupDir, final String backupFileName) throws Exception {
        checkAndCreateBackupDir(backupDir);
        BackupDB.out.log(Level.INFO, "BackingUp DB Started");
        getServerClass().fileBackup(backupDir, backupFileName);
    }
    
    public static void backup(final Properties backupProps) {
        String backupDir = null;
        String backupFile = null;
        int backupContentType = 1;
        boolean isFileBackUp = false;
        Starter.loadSystemProperties();
        if (HandShakeUtil.isServerListening()) {
            ConsoleOut.println("Server seems to be running. Please shutdown server and try invoking the script.");
            throw new RuntimeException("Server seems to be running. Please shutdown server and try invoking the script.");
        }
        backupFile = backupProps.getProperty("-f");
        if (backupFile != null && backupFile.isEmpty()) {
            throw new IllegalArgumentException("Backup file cannot be empty");
        }
        if (backupFile != null && backupFile.contains(".")) {
            ConsoleOut.println("FileName should not contain extension.");
            throw new IllegalArgumentException("FileName should not contain extension.");
        }
        backupDir = backupProps.getProperty("-d");
        if (backupDir != null && backupDir.isEmpty()) {
            throw new IllegalArgumentException("Backup directory file cannot be empty");
        }
        if (backupDir == null) {
            backupDir = BackupDB.BACKUP_DIR;
        }
        final String type = backupProps.getProperty("-t");
        if (type == null) {
            backupContentType = 1;
        }
        else {
            if (type.isEmpty()) {
                throw new IllegalArgumentException("Backup type cannot be empty");
            }
            if (type.equals("fb")) {
                isFileBackUp = true;
            }
            else if (type.equals("b")) {
                backupContentType = 1;
            }
            else if (type.equals("d")) {
                backupContentType = 2;
            }
            else {
                backupContentType = 1;
            }
        }
        final String password = backupProps.getProperty("-p");
        if (password != null && password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        try {
            Starter.LoadJars();
            if (isFileBackUp) {
                fileBackUp(backupDir, backupFile);
                System.exit(0);
            }
            else {
                ConsoleOut.println("Backup Started");
                backupDB(backupDir, backupFile, backupContentType, password);
                ConsoleOut.println("Backup Completed Successfully");
                System.exit(0);
            }
        }
        catch (final Exception e) {
            BackupDB.out.log(Level.SEVERE, "Problem while BackingUp DB.");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void main(final String[] args) {
        try {
            LoggerUtil.initLog("backup");
        }
        catch (final Exception e) {
            BackupDB.out.log(Level.SEVERE, "Logger not initialized");
            e.printStackTrace();
            System.exit(1);
        }
        if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
            showHelpMessage();
        }
        else {
            final Properties p = new Properties();
            for (int i = 0; i < args.length; ++i) {
                final String currentArg = args[i];
                if (!currentArg.equalsIgnoreCase("-p") && !currentArg.equalsIgnoreCase("-d") && !currentArg.equalsIgnoreCase("-t") && !currentArg.equalsIgnoreCase("-f")) {
                    ConsoleOut.println("Unknown argument :: [" + currentArg + "]");
                    showHelpMessage();
                }
                String nextArg;
                if (currentArg.equalsIgnoreCase("-p")) {
                    if (i + 1 <= args.length - 1) {
                        nextArg = args[i + 1];
                        if (nextArg.equalsIgnoreCase("-d") || nextArg.equalsIgnoreCase("-f") || nextArg.equalsIgnoreCase("-t")) {
                            nextArg = readPasswordFromConsole();
                        }
                        else {
                            ++i;
                        }
                    }
                    else {
                        nextArg = readPasswordFromConsole();
                    }
                }
                else {
                    nextArg = args[++i];
                }
                p.setProperty(currentArg.toLowerCase(Locale.ENGLISH), nextArg);
            }
            backup(p);
        }
    }
    
    private static String readPasswordFromConsole() {
        final Console console = System.console();
        if (console == null) {
            ConsoleOut.println("Password for the file to be backed-up is not specified");
            System.exit(1);
            return null;
        }
        char[] passwordArray;
        while (true) {
            passwordArray = console.readPassword("Enter Password : ", new Object[0]);
            if (passwordArray.length > 0) {
                break;
            }
            console.printf("Password cannot be empty\n", new Object[0]);
        }
        return new String(passwordArray);
    }
    
    public static void showHelpMessage() {
        ConsoleOut.println("Usage :");
        ConsoleOut.println("backupDB [OPTION]...");
        ConsoleOut.println("Options : ");
        ConsoleOut.println("-t (d|b|fb)     Backup content type (Dump, Binary (default), file backup)");
        ConsoleOut.println("-f              Name of the backup-file without extension");
        ConsoleOut.println("-d              Directory in which backup-file will be created. By Default, " + BackupDB.BACKUP_DIR + " will be used");
        ConsoleOut.println("-p              If flag -p is given it can be followed by the password or can be entered through console prompt");
        System.exit(1);
    }
    
    static {
        BackupDB.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        BACKUP_DIR = BackupDB.server_home + File.separator + "Backup";
        out = Logger.getLogger(BackupDB.class.getName());
        BackupDB.SHOW_STATUS = false;
        BackupDB.BACKUP_DB_USING_SCRIPTS = false;
    }
}
