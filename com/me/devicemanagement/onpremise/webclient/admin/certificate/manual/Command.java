package com.me.devicemanagement.onpremise.webclient.admin.certificate.manual;

import java.io.IOException;
import java.util.logging.Level;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

class Command
{
    private static Logger logger;
    private static BatchLogHandler logHandler;
    
    public static ArrayList<String> getCustomCommand(final String customCommand) {
        final ArrayList<String> commands = new ArrayList<String>();
        commands.add(0, "cmd");
        commands.add(1, "/c");
        commands.add(2, customCommand);
        return commands;
    }
    
    public static void executeCommand(final ArrayList<String> commands, final File dir) {
        try {
            ProcessBuilder pBuilder = new ProcessBuilder(commands);
            pBuilder.directory(dir);
            pBuilder = pBuilder.redirectErrorStream(true);
            final Process childProcess = pBuilder.start();
            childProcess.waitFor();
        }
        catch (final IOException ex) {
            Command.logger.log(Level.SEVERE, "Administrative privileges not granted to this tool.. Process creation failed..", ex);
            Command.logHandler.logExceptionTrace(ex);
        }
        catch (final InterruptedException ex2) {
            Command.logger.log(Level.SEVERE, "Process was interrupted.. possible deletion of this running process..", ex2);
            Command.logHandler.logExceptionTrace(ex2);
        }
    }
    
    public static ArrayList<String> getRenameCommand(final String beforeFileName, final String afterFileName) {
        final ArrayList<String> commands = new ArrayList<String>();
        commands.add(0, "cmd");
        commands.add(1, "/c");
        commands.add(2, "rename \"" + beforeFileName + "\" \"" + afterFileName + "\"");
        return commands;
    }
    
    public static ArrayList<String> getCopyCommand(final String fromFile, final String toFile) {
        final ArrayList<String> commands = new ArrayList<String>();
        commands.add(0, "cmd");
        commands.add(1, "/c");
        commands.add(2, "copy /Y \"" + fromFile + "\" \"" + toFile + "\"");
        return commands;
    }
    
    public static ArrayList<String> getDeleteCommand(final String orgFile) {
        final ArrayList<String> commands = new ArrayList<String>();
        commands.add(0, "cmd");
        commands.add(1, "/c");
        commands.add(2, "del " + orgFile);
        return commands;
    }
    
    static {
        Command.logger = BatchCertificateConverter.logger;
        Command.logHandler = BatchCertificateConverter.logHandler;
    }
}
