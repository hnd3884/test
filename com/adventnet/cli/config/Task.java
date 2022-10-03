package com.adventnet.cli.config;

public class Task
{
    public static final int SCRIPT = 1;
    public static final int COMMAND = 2;
    private int taskType;
    private String taskName;
    private String command;
    private String scriptName;
    private String scriptType;
    private String loginLevel;
    private boolean mandatory;
    private boolean dataRequired;
    private String description;
    private int executeCount;
    
    public Task() {
        this.taskType = 2;
        this.taskName = null;
        this.command = null;
        this.scriptName = null;
        this.scriptType = null;
        this.loginLevel = null;
        this.mandatory = true;
        this.dataRequired = true;
        this.description = null;
        this.executeCount = 1;
    }
    
    public int getTaskType() {
        return this.taskType;
    }
    
    public void setTaskType(final int taskType) {
        this.taskType = taskType;
    }
    
    public String getTaskName() {
        return this.taskName;
    }
    
    public void setTaskName(final String taskName) {
        this.taskName = taskName;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public String getScriptName() {
        return this.scriptName;
    }
    
    public void setScriptName(final String scriptName) {
        this.scriptName = scriptName;
    }
    
    public String getScriptType() {
        return this.scriptType;
    }
    
    public void setScriptType(final String scriptType) {
        this.scriptType = scriptType;
    }
    
    public String getLoginLevel() {
        return this.loginLevel;
    }
    
    public void setLoginLevel(final String loginLevel) {
        this.loginLevel = loginLevel;
    }
    
    public boolean getMandatory() {
        return this.mandatory;
    }
    
    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }
    
    public boolean getDataRequired() {
        return this.dataRequired;
    }
    
    public void setDataRequired(final boolean dataRequired) {
        this.dataRequired = dataRequired;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public int getExecutionCount() {
        return this.executeCount;
    }
    
    public void setExecutionCount(final int executeCount) {
        this.executeCount = executeCount;
    }
    
    public String toString() {
        return this.taskName;
    }
}
