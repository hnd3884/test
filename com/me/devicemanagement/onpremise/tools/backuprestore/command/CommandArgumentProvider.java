package com.me.devicemanagement.onpremise.tools.backuprestore.command;

public class CommandArgumentProvider
{
    static CommandArgumentProvider instance;
    private String operation;
    private String backupPath;
    private String restorePath;
    private String password;
    private String passwordhint;
    private int encryptionType;
    
    private CommandArgumentProvider() {
        this.encryptionType = 1;
    }
    
    public static CommandArgumentProvider getInstance() {
        if (CommandArgumentProvider.instance == null) {
            CommandArgumentProvider.instance = new CommandArgumentProvider();
        }
        return CommandArgumentProvider.instance;
    }
    
    public String getOperation() {
        return this.operation;
    }
    
    public void setOperation(final String operation) {
        this.operation = operation;
    }
    
    public String getBackupPath() {
        return this.backupPath;
    }
    
    public void setBackupPath(final String backupPath) {
        this.backupPath = backupPath;
    }
    
    public String getRestorePath() {
        return this.restorePath;
    }
    
    public void setRestorePath(final String restorePath) {
        this.restorePath = restorePath;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getPasswordhint() {
        return this.passwordhint;
    }
    
    public void setPasswordhint(final String passwordhint) {
        this.passwordhint = passwordhint;
    }
    
    public int getEncryptionType() {
        return this.encryptionType;
    }
    
    public void setEncryptionType(final int encryptionType) {
        this.encryptionType = encryptionType;
    }
    
    static {
        CommandArgumentProvider.instance = null;
    }
}
