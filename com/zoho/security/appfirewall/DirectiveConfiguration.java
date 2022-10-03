package com.zoho.security.appfirewall;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class DirectiveConfiguration
{
    private String directiveName;
    private List<AppFirewallComponent> componentList;
    private String id;
    
    public DirectiveConfiguration() {
        this.directiveName = null;
        this.id = null;
    }
    
    public void setName(final String dirName) {
        this.directiveName = dirName;
    }
    
    public String getDirectiveName() {
        return this.directiveName;
    }
    
    public List<AppFirewallComponent> getComponentList() {
        return this.componentList;
    }
    
    public void setId(final String autoID) {
        this.id = autoID;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void initalizeComponentList() {
        this.componentList = new ArrayList<AppFirewallComponent>();
    }
    
    public enum Directive
    {
        URL("url", Arrays.asList("path", "querystring")), 
        METHOD("method", Arrays.asList("name")), 
        HEADERS("headers", Arrays.asList("name", "value")), 
        PARAMS("params", Arrays.asList("name", "value")), 
        FILES("files", Arrays.asList("name", "content")), 
        INPUTSTREAM("inputstream", Arrays.asList("content")), 
        IP("ip", Arrays.asList("value")), 
        SERVER("server", Arrays.asList("name", "port")), 
        USERS("users", Arrays.asList("zuid", "zaaid", "zsoid", "email"));
        
        private String value;
        private List<String> component;
        
        private Directive(final String value, final List<String> componentList) {
            this.value = value;
            this.component = componentList;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public List<String> getComponentList() {
            return this.component;
        }
    }
}
