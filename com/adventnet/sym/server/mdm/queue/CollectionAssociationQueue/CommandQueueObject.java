package com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue;

import java.util.Properties;
import java.io.Serializable;

public class CommandQueueObject implements Serializable
{
    private String commandName;
    private String customerId;
    private Properties propsFile;
    private Integer commandType;
    private static final long serialVersionUID = 1L;
    
    public String getCommandName() {
        return this.commandName;
    }
    
    public void setCommandName(final String commandName) {
        this.commandName = commandName;
    }
    
    public String getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }
    
    public Properties getPropsFile() {
        return this.propsFile;
    }
    
    public void setPropsFile(final Properties taskProps) {
        this.propsFile = taskProps;
    }
    
    public Integer getCommandType() {
        return this.commandType;
    }
    
    public void setCommandType(final Integer commandType) {
        this.commandType = commandType;
    }
}
