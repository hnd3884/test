package com.adventnet.cli.messageset;

import java.io.Serializable;

public class CmdObject implements Serializable
{
    private CmdObject[] childObjectList;
    private CmdParams[] parameterList;
    private CmdOptions optionsList;
    private String objectName;
    private String objectValue;
    private boolean sendParam;
    private String delimiter;
    private String description;
    
    public CmdObject() {
        this.childObjectList = null;
        this.parameterList = null;
        this.optionsList = null;
        this.objectName = null;
        this.objectValue = null;
        this.sendParam = false;
        this.delimiter = " ";
        this.description = null;
    }
    
    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }
    
    public String getObjectName() {
        return this.objectName;
    }
    
    public void setObjectValue(final String objectValue) {
        this.objectValue = objectValue;
    }
    
    public String getObjectValue() {
        return this.objectValue;
    }
    
    public void setChildCmdObjectList(final CmdObject[] childObjectList) {
        this.childObjectList = childObjectList;
    }
    
    public CmdObject[] getChildCmdObjectList() {
        return this.childObjectList;
    }
    
    public void setParameterList(final CmdParams[] parameterList) {
        this.parameterList = parameterList;
    }
    
    public CmdParams[] getParameterList() {
        return this.parameterList;
    }
    
    public void setOptionsList(final CmdOptions optionsList) {
        this.optionsList = optionsList;
    }
    
    public CmdOptions getOptionsList() {
        return this.optionsList;
    }
    
    public String getObjectDelimiter() {
        return this.delimiter;
    }
    
    public void setObjectDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String toString() {
        return this.getObjectName();
    }
}
