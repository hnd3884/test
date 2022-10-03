package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.me.devicemanagement.onpremise.server.util.ScheduleDBBackupUtil;
import java.util.Properties;
import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class DMBackupPasswordHandler
{
    private static final Logger LOGGER;
    private static DMBackupPasswordHandler instance;
    private DMBackupPasswordProvider passwordProvider;
    private String passwordHintFile;
    private int encryptionType;
    
    private DMBackupPasswordHandler() {
        this.passwordProvider = null;
        this.passwordHintFile = System.getProperty("server.home") + File.separator + "DB_Password_Hint.txt";
        this.encryptionType = 1;
    }
    
    public static DMBackupPasswordHandler getInstance() {
        if (DMBackupPasswordHandler.instance == null) {
            DMBackupPasswordHandler.instance = new DMBackupPasswordHandler();
        }
        return DMBackupPasswordHandler.instance;
    }
    
    public void setPasswordProvider(final DMBackupPasswordProvider pwdProvider) {
        if (pwdProvider != null) {
            this.passwordProvider = pwdProvider;
            DMBackupPasswordHandler.LOGGER.log(Level.INFO, pwdProvider.getClass().getName() + " is set as password provider");
        }
        else {
            DMBackupPasswordHandler.LOGGER.log(Level.WARNING, "DMBackupPasswordProvider  is null ");
        }
    }
    
    public String getPassword(final Boolean isBackup) {
        if (this.passwordProvider == null) {
            this.setEncryptionType(3);
            this.setPasswordProvider(new DMBackupPasswordProvider() {
                @Override
                public String getPassword() {
                    return null;
                }
                
                @Override
                public String getPasswordHint() {
                    return null;
                }
            });
        }
        final String password = this.passwordProvider.getPassword();
        if (isBackup) {
            this.savePasswordHint();
        }
        return password;
    }
    
    private void savePasswordHint() {
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        String passwordHint = this.passwordProvider.getPasswordHint();
        if (passwordHint == null) {
            DMBackupPasswordHandler.LOGGER.log(Level.INFO, "password hint not provided");
            passwordHint = "";
        }
        try {
            final Properties pwdProps = new Properties();
            if (this.getEncryptionType() == 2) {
                ((Hashtable<String, String>)pwdProps).put("encryptionType", String.valueOf(2));
                ((Hashtable<String, String>)pwdProps).put("pwdHint", passwordHint);
            }
            else if (this.getEncryptionType() == 1) {
                ((Hashtable<String, String>)pwdProps).put("encryptionType", String.valueOf(1));
                ((Hashtable<String, String>)pwdProps).put("pwd", ScheduleDBBackupUtil.getEncryptedDBBackupPassword(this.passwordProvider.getPassword()));
            }
            else if (this.getEncryptionType() == 3) {
                ((Hashtable<String, String>)pwdProps).put("encryptionType", String.valueOf(3));
            }
            writer = new FileWriter(this.passwordHintFile);
            bufferedWriter = new BufferedWriter(writer);
            pwdProps.store(bufferedWriter, null);
        }
        catch (final Exception e) {
            DMBackupPasswordHandler.LOGGER.log(Level.WARNING, "Caught exception while saving password hint", e);
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception e) {
                DMBackupPasswordHandler.LOGGER.log(Level.WARNING, "Caught exception while closing file writer", e);
            }
        }
        finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception e2) {
                DMBackupPasswordHandler.LOGGER.log(Level.WARNING, "Caught exception while closing file writer", e2);
            }
        }
    }
    
    public int getEncryptionType() {
        return this.encryptionType;
    }
    
    public void setEncryptionType(final int encryptionType) {
        this.encryptionType = encryptionType;
    }
    
    public static String getEncryptedDBBackupPassword(final String pwd) {
        try {
            return new EnDecryptAES256Impl().encrypt(pwd, WinAccessProvider.getInstance().nativeGetDefaultDBBackupPassword());
        }
        catch (final Exception ex) {
            DMBackupPasswordHandler.LOGGER.log(Level.INFO, "Cannot get default db backup password from native.", ex);
            return null;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DMBackupPasswordHandler.class.getName());
        DMBackupPasswordHandler.instance = null;
    }
}
