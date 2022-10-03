package com.adventnet.cli.messageset;

import java.io.Serializable;

public class CLICommandTemplate implements Serializable
{
    CmdObject[] cmdObjectList;
    CmdParams[] cmdParamsList;
    CmdOptions cmdOptions;
    CmdHelp cmdHelp;
    String cmdName;
    String delimiter;
    
    public CLICommandTemplate() {
        this.cmdObjectList = null;
        this.cmdParamsList = null;
        this.cmdOptions = null;
        this.cmdHelp = null;
        this.cmdName = null;
        this.delimiter = " ";
    }
    
    public void setCmdObjectList(final CmdObject[] cmdObjectList) {
        this.cmdObjectList = cmdObjectList;
    }
    
    public CmdObject[] getCmdObjectList() {
        return this.cmdObjectList;
    }
    
    public void setCmdParamsList(final CmdParams[] cmdParamsList) {
        this.cmdParamsList = cmdParamsList;
    }
    
    public CmdParams[] getCmdParamsList() {
        return this.cmdParamsList;
    }
    
    public void setCmdOptionsList(final CmdOptions cmdOptions) {
        this.cmdOptions = cmdOptions;
    }
    
    public CmdOptions getCmdOptionsList() {
        return this.cmdOptions;
    }
    
    public void setCommandName(final String cmdName) {
        this.cmdName = cmdName;
    }
    
    public String getCommandName() {
        return this.cmdName;
    }
    
    public void setCommandDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }
    
    public String getCommandDelimiter() {
        return this.delimiter;
    }
    
    public CmdHelp getCmdHelp() {
        return this.cmdHelp;
    }
    
    public void setCmdHelp(final CmdHelp cmdHelp) {
        this.cmdHelp = cmdHelp;
    }
    
    public String toString() {
        return this.getCommandName();
    }
}
