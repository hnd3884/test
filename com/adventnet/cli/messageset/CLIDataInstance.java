package com.adventnet.cli.messageset;

import java.io.Serializable;

public class CLIDataInstance implements Serializable
{
    CmdObject[] cmdObjectList;
    CmdParams[] cmdParamList;
    CmdOptions cmdOption;
    String dataName;
    
    CLIDataInstance() {
        this.cmdObjectList = null;
        this.cmdParamList = null;
        this.cmdOption = null;
        this.dataName = null;
    }
    
    public void setCmdObjectList(final CmdObject[] cmdObjectList) {
        this.cmdObjectList = cmdObjectList;
    }
    
    public CmdObject[] getCmdObjectList() {
        return this.cmdObjectList;
    }
    
    public void setCmdParamList(final CmdParams[] cmdParamList) {
        this.cmdParamList = cmdParamList;
    }
    
    public CmdParams[] getCmdParamList() {
        return this.cmdParamList;
    }
    
    public void setCmdOption(final CmdOptions cmdOption) {
        this.cmdOption = cmdOption;
    }
    
    public CmdOptions getCmdOption() {
        return this.cmdOption;
    }
    
    public void setDataName(final String dataName) {
        this.dataName = dataName;
    }
    
    public String getDataName() {
        return this.dataName;
    }
    
    private void printValues() {
        System.out.println("Name: " + this.dataName);
        final CmdParams[] parameterList = this.cmdObjectList[0].getParameterList();
        for (int i = 0; i < parameterList.length; ++i) {
            System.out.println("ParamName: " + parameterList[i].getParamName());
        }
    }
    
    public CmdObject getCmdObjectByName(final String s) {
        if (this.cmdObjectList != null) {
            for (int i = 0; i < this.cmdObjectList.length; ++i) {
                final CmdObject cmdObject = this.findCmdObject(this.cmdObjectList[i], s);
                if (cmdObject != null) {
                    return cmdObject;
                }
            }
        }
        return null;
    }
    
    private CmdObject findCmdObject(final CmdObject cmdObject, final String s) {
        if (cmdObject.getObjectName().equals(s)) {
            return cmdObject;
        }
        final CmdObject[] childCmdObjectList = cmdObject.getChildCmdObjectList();
        if (childCmdObjectList == null) {
            return null;
        }
        for (int i = 0; i < childCmdObjectList.length; ++i) {
            final CmdObject cmdObject2 = this.findCmdObject(childCmdObjectList[i], s);
            if (cmdObject2 != null) {
                return cmdObject2;
            }
        }
        return null;
    }
    
    public String getParameterValue(final CmdObject cmdObject, final String s) {
        final CmdParams[] parameterList = cmdObject.getParameterList();
        if (parameterList == null) {
            return null;
        }
        for (int i = 0; i < parameterList.length; ++i) {
            final CmdParams cmdParams = parameterList[i];
            if (cmdParams.getParamName().equals(s)) {
                return cmdParams.getParamValue();
            }
        }
        return null;
    }
}
