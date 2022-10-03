package com.adventnet.cli.config;

import java.util.Properties;

public class TaskData
{
    private String taskName;
    private String[] scriptArgs;
    private Properties cmdParams;
    
    public TaskData() {
        this.taskName = null;
        this.scriptArgs = null;
        this.cmdParams = null;
    }
    
    public String getTaskName() {
        return this.taskName;
    }
    
    public void setTaskName(final String taskName) {
        this.taskName = taskName;
    }
    
    public String[] getScriptArgs() {
        return this.scriptArgs;
    }
    
    public void setScriptArgs(final String[] scriptArgs) {
        this.scriptArgs = scriptArgs;
    }
    
    public Properties getCmdParams() {
        return this.cmdParams;
    }
    
    public void setCmdParams(final Properties cmdParams) {
        this.cmdParams = cmdParams;
    }
}
