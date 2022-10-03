package com.adventnet.cli.config;

import java.util.ArrayList;

public class ConfigObject
{
    private String configID;
    private String loginLevel;
    private String description;
    private DataInterface dataIfc;
    private ArrayList taskList;
    
    public ConfigObject() {
        this.configID = null;
        this.loginLevel = null;
        this.description = null;
        this.dataIfc = null;
        this.taskList = null;
    }
    
    public String getConfigID() {
        return this.configID;
    }
    
    public void setConfigID(final String configID) {
        this.configID = configID;
    }
    
    public String getLoginLevel() {
        return this.loginLevel;
    }
    
    public void setLoginLevel(final String loginLevel) {
        this.loginLevel = loginLevel;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public DataInterface getDataInterface() {
        return this.dataIfc;
    }
    
    public void setDataInterface(final DataInterface dataIfc) {
        this.dataIfc = dataIfc;
    }
    
    public ArrayList getTaskList() {
        return this.taskList;
    }
    
    public void setTaskList(final ArrayList taskList) {
        this.taskList = taskList;
    }
    
    public String toString() {
        return this.configID;
    }
}
