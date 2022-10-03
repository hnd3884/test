package com.adventnet.cli;

import java.util.Properties;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.io.Serializable;

public class CLIMessage implements Serializable
{
    String cliData;
    static final int READING = 0;
    static final int COMPLETE = 1;
    private int state;
    int msgID;
    CLIProtocolOptions cliProtocolOptions;
    String cliPrompt;
    String[] cliPrompts;
    boolean cmdEcho;
    boolean promptEcho;
    int requestTimeout;
    String msgSuffix;
    private Properties cliPromptAction;
    private boolean partialResponse;
    
    int getState() {
        return this.state;
    }
    
    void setState(final int state) {
        this.state = state;
    }
    
    public CLIMessage(final String cliData) {
        this.cliData = null;
        this.state = 0;
        this.msgID = 0;
        this.cliPrompt = null;
        this.cliPrompts = null;
        this.cmdEcho = true;
        this.promptEcho = true;
        this.requestTimeout = 5000;
        this.msgSuffix = "\n";
        this.cliPromptAction = null;
        this.partialResponse = false;
        this.cliData = cliData;
    }
    
    public CLIMessage(final byte[] array) {
        this.cliData = null;
        this.state = 0;
        this.msgID = 0;
        this.cliPrompt = null;
        this.cliPrompts = null;
        this.cmdEcho = true;
        this.promptEcho = true;
        this.requestTimeout = 5000;
        this.msgSuffix = "\n";
        this.cliPromptAction = null;
        this.partialResponse = false;
        this.cliData = new String(array);
    }
    
    public void setData(final byte[] array) {
        this.cliData = new String(array);
    }
    
    public void setData(final String cliData) {
        this.cliData = cliData;
    }
    
    public String getData() {
        return this.cliData;
    }
    
    public void setMsgID(final int msgID) {
        this.msgID = msgID;
    }
    
    public int getMsgID() {
        return this.msgID;
    }
    
    public void setCLIProtocolOptions(final CLIProtocolOptions cliProtocolOptions) {
        this.cliProtocolOptions = (CLIProtocolOptions)cliProtocolOptions.clone();
    }
    
    public CLIProtocolOptions getCLIProtocolOptions() {
        return this.cliProtocolOptions;
    }
    
    public void setCLIPrompt(final String cliPrompt) {
        this.cliPrompt = cliPrompt;
    }
    
    public void setCLIPromptList(final String[] cliPrompts) {
        this.cliPrompts = cliPrompts;
    }
    
    public String getCLIPrompt() {
        return this.cliPrompt;
    }
    
    public String[] getCLIPromptList() {
        return this.cliPrompts;
    }
    
    public void setCommandEcho(final boolean cmdEcho) {
        this.cmdEcho = cmdEcho;
    }
    
    public boolean isSetCommandEcho() {
        return this.cmdEcho;
    }
    
    public void setPromptEcho(final boolean promptEcho) {
        this.promptEcho = promptEcho;
    }
    
    public boolean isSetPromptEcho() {
        return this.promptEcho;
    }
    
    public void setRequestTimeout(final int requestTimeout) {
        if (requestTimeout >= 0) {
            this.requestTimeout = requestTimeout;
        }
    }
    
    public int getRequestTimeout() {
        return this.requestTimeout;
    }
    
    public void setMessageSuffix(final String msgSuffix) {
        this.msgSuffix = msgSuffix;
    }
    
    public String getMessageSuffix() {
        return this.msgSuffix;
    }
    
    public Properties getCLIPromptAction() {
        return this.cliPromptAction;
    }
    
    public void setCLIPromptAction(final Properties cliPromptAction) {
        this.cliPromptAction = cliPromptAction;
    }
    
    public boolean getPartialResponse() {
        return this.partialResponse;
    }
    
    public void setPartialResponse(final boolean partialResponse) {
        this.partialResponse = partialResponse;
    }
    
    public Object clone() {
        final CLIMessage cliMessage = new CLIMessage(this.cliData);
        cliMessage.msgID = this.msgID;
        if (this.getCLIProtocolOptions() != null) {
            cliMessage.setCLIProtocolOptions((CLIProtocolOptions)this.getCLIProtocolOptions().clone());
        }
        if (this.getCLIPromptList() == null) {
            cliMessage.setCLIPrompt(this.getCLIPrompt());
        }
        else {
            cliMessage.setCLIPromptList(this.getCLIPromptList());
        }
        cliMessage.setCommandEcho(this.isSetCommandEcho());
        cliMessage.setPromptEcho(this.isSetPromptEcho());
        cliMessage.setRequestTimeout(this.getRequestTimeout());
        cliMessage.setMessageSuffix(this.getMessageSuffix());
        cliMessage.setCLIPromptAction(this.getCLIPromptAction());
        cliMessage.setPartialResponse(this.getPartialResponse());
        return cliMessage;
    }
}
