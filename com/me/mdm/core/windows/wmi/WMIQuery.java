package com.me.mdm.core.windows.wmi;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import java.util.List;

public abstract class WMIQuery
{
    protected String wmiNamespace;
    protected String wmiClassName;
    protected List<String> wmiClassProperties;
    protected String wmiCommandName;
    
    public WMIQuery() {
        this.wmiNamespace = null;
        this.wmiClassName = null;
        this.wmiClassProperties = null;
        this.wmiCommandName = null;
    }
    
    public String getWmiNamespace() {
        return this.wmiNamespace;
    }
    
    public String getWmiClassName() {
        return this.wmiClassName;
    }
    
    public String getFullyQualifiedWmiClassName() {
        return this.wmiNamespace + "/" + this.wmiClassName;
    }
    
    public List<String> getWmiClassProperties() {
        return this.wmiClassProperties;
    }
    
    public String getWmiCommandName() {
        return this.wmiCommandName;
    }
    
    public GetRequestCommand modifyChildPropertyQuery(final GetRequestCommand baseGetRequestCommand, final String[] wmiInstances) throws Exception {
        return baseGetRequestCommand;
    }
    
    protected Item createCommandItemTagElement(final String sLocationURI) {
        return this.createCommandItemTagElement(sLocationURI, null);
    }
    
    protected Item createCommandItemTagElement(final String sLocationURI, final String sValue) {
        final Item commandItem = new Item();
        commandItem.setTarget(new Location(sLocationURI));
        if (sValue != null) {
            commandItem.setData(sValue);
        }
        return commandItem;
    }
}
