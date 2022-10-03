package com.adventnet.cli.config;

public class LoginLevel
{
    private String loginLevel;
    private String loginCommand;
    private String loginName;
    private String loginPassword;
    private String passwordPrompt;
    private String loginPrompt;
    private String commandPrompt;
    private String[] subLevels;
    private String parentLevel;
    private String levelExitCmd;
    boolean passwordRequired;
    boolean userNameRequired;
    
    LoginLevel() {
        this.loginLevel = null;
        this.loginCommand = null;
        this.loginName = null;
        this.loginPassword = null;
        this.passwordPrompt = null;
        this.loginPrompt = null;
        this.commandPrompt = null;
        this.subLevels = null;
        this.parentLevel = null;
        this.levelExitCmd = null;
        this.passwordRequired = false;
        this.userNameRequired = false;
    }
    
    public String getLoginLevel() {
        return this.loginLevel;
    }
    
    public void setLoginLevel(final String loginLevel) {
        this.loginLevel = loginLevel;
    }
    
    public String getLoginCommand() {
        return this.loginCommand;
    }
    
    public void setLoginCommand(final String loginCommand) {
        this.loginCommand = loginCommand;
    }
    
    public String getLoginName() {
        return this.loginName;
    }
    
    public void setLoginName(final String loginName) {
        if (loginName != null && loginName.equals("")) {
            this.userNameRequired = true;
        }
        this.loginName = loginName;
    }
    
    public String getLoginPassword() {
        return this.loginPassword;
    }
    
    public void setLoginPassword(final String loginPassword) {
        if (loginPassword != null && loginPassword.equals("")) {
            this.passwordRequired = true;
        }
        this.loginPassword = loginPassword;
    }
    
    public String getPasswordPrompt() {
        return this.passwordPrompt;
    }
    
    public void setPasswordPrompt(final String passwordPrompt) {
        this.passwordPrompt = passwordPrompt;
    }
    
    public String getLoginPrompt() {
        return this.loginPrompt;
    }
    
    public void setLoginPrompt(final String loginPrompt) {
        this.loginPrompt = loginPrompt;
    }
    
    public String getCommandPrompt() {
        return this.commandPrompt;
    }
    
    public void setCommandPrompt(final String commandPrompt) {
        this.commandPrompt = commandPrompt;
    }
    
    public String[] getSubLevels() {
        return this.subLevels;
    }
    
    public void setSubLevels(final String[] subLevels) {
        this.subLevels = subLevels;
    }
    
    public String getParentLevel() {
        return this.parentLevel;
    }
    
    public void setParentLevel(final String parentLevel) {
        this.parentLevel = parentLevel;
    }
    
    public String getLevelExitCmd() {
        return this.levelExitCmd;
    }
    
    public void setLevelExitCmd(final String levelExitCmd) {
        this.levelExitCmd = levelExitCmd;
    }
    
    public boolean isPasswordRequired() {
        return this.passwordPrompt != null;
    }
    
    public boolean isUserNameRequired() {
        return this.loginPrompt != null;
    }
}
