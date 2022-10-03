package com.me.mdm.server.easmanagement.pss;

import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.server.easmanagement.EASMgmt;
import java.io.File;
import com.me.mdm.server.easmanagement.EASMgmtConstants;

public class PSSScriptGenerator
{
    private static PSSScriptGenerator pssCommandGen;
    
    public static PSSScriptGenerator getInstance() {
        if (PSSScriptGenerator.pssCommandGen == null) {
            PSSScriptGenerator.pssCommandGen = new PSSScriptGenerator();
        }
        return PSSScriptGenerator.pssCommandGen;
    }
    
    private void waitForExecutionOfPreviousScript(final Long serverID, final boolean force) {
        final File sourceFile = new File(EASMgmtConstants.getTaskScriptFileAddress(serverID));
        if (force) {
            sourceFile.delete();
        }
        for (int i = 0; i < 5; ++i) {
            if (sourceFile.exists()) {
                try {
                    EASMgmt.logger.log(Level.WARNING, "waiting for previous source script file to be executed");
                    Thread.sleep(1000L);
                }
                catch (final InterruptedException ex) {
                    EASMgmt.logger.log(Level.SEVERE, null, ex);
                }
            }
        }
        sourceFile.delete();
    }
    
    public void writeCommandsToScript(final Long easServerID, final String script, final String fileName, final boolean force) {
        this.waitForExecutionOfPreviousScript(easServerID, force);
        final String dedicatedServerFolderPath = EASMgmtConstants.getDedicatedFolderPath(easServerID);
        try {
            if (!ApiFactoryProvider.getFileAccessAPI().isDirectory(dedicatedServerFolderPath)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(dedicatedServerFolderPath);
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        File file = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            file = new File(fileName);
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            String taskPrefix = "";
            taskPrefix = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.SCRIPT_PREFIX);
            taskPrefix = taskPrefix.replace("{0}", fileName);
            bufferedWriter.write(taskPrefix);
            bufferedWriter.write(System.getProperty("line.separator"));
            bufferedWriter.write(script);
        }
        catch (final IOException ex2) {
            EASMgmt.logger.log(Level.SEVERE, null, ex2);
            try {
                bufferedWriter.close();
            }
            catch (final IOException ex2) {
                EASMgmt.logger.log(Level.SEVERE, null, ex2);
            }
            try {
                fileWriter.close();
            }
            catch (final IOException ex2) {
                EASMgmt.logger.log(Level.SEVERE, null, ex2);
            }
        }
        finally {
            try {
                bufferedWriter.close();
            }
            catch (final IOException ex3) {
                EASMgmt.logger.log(Level.SEVERE, null, ex3);
            }
            try {
                fileWriter.close();
            }
            catch (final IOException ex3) {
                EASMgmt.logger.log(Level.SEVERE, null, ex3);
            }
        }
    }
    
    public void writeCommandsToScript(final Long easServerID, final String script, final boolean force) {
        final String taskScriptFileAddress = EASMgmtConstants.getTaskScriptFileAddress(easServerID);
        this.writeCommandsToScript(easServerID, script, taskScriptFileAddress, force);
    }
    
    public void writeCommandsToScript(final Long easServerID, final String script) {
        this.writeCommandsToScript(easServerID, script, false);
    }
    
    static {
        PSSScriptGenerator.pssCommandGen = null;
    }
}
