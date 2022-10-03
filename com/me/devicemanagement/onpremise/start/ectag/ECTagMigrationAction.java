package com.me.devicemanagement.onpremise.start.ectag;

import java.io.IOException;
import java.util.logging.Level;
import java.io.File;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import java.security.SecureRandom;
import java.util.logging.Logger;

public class ECTagMigrationAction
{
    public static Logger log;
    
    public static void main(final String[] arg) {
        final ECTagMigrationAction ecTagMigrationAction = new ECTagMigrationAction();
        ecTagMigrationAction.changeEncryptionKey();
    }
    
    public void changeEncryptionKey() {
        final String key = RandomStringUtils.random(15, 0, 0, true, true, (char[])null, (Random)new SecureRandom());
        this.runChangeEncryptionKeyBat(key);
    }
    
    public void changeEncryptionKey(final String key) {
        this.runChangeEncryptionKeyBat(key);
    }
    
    private void runChangeEncryptionKeyBat(final String key) {
        final String serverHome = System.getProperty("server.home");
        final String changeKeyBatchFilePath = serverHome + File.separator + "bin";
        final String changeKeyBatchFile = "changeKey.bat";
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "cmd.exe", "/c", "cd " + changeKeyBatchFilePath + " && " + changeKeyBatchFile + " " + key });
        try {
            ECTagMigrationAction.log.log(Level.INFO, "Changing encryption key process started");
            final Process process = processBuilder.start();
            process.waitFor();
            ECTagMigrationAction.log.log(Level.INFO, "Changing encryption key process ended");
        }
        catch (final IOException io) {
            ECTagMigrationAction.log.log(Level.INFO, "Exception while running change key bat file" + io);
        }
        catch (final InterruptedException ioe) {
            ECTagMigrationAction.log.log(Level.INFO, "Exception occurred while changing encryption key " + ioe);
        }
    }
    
    static {
        ECTagMigrationAction.log = Logger.getLogger(ECTagMigrationAction.class.getName());
    }
}
