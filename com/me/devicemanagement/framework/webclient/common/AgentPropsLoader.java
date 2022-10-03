package com.me.devicemanagement.framework.webclient.common;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;

public class AgentPropsLoader
{
    protected static AgentPropsLoader agentPropsLoader;
    private Properties agentProperties;
    
    protected AgentPropsLoader() {
        this.agentProperties = new Properties();
        try {
            final String fname = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "agent_properties.conf";
            this.agentProperties = FileAccessUtil.readProperties(fname);
            Logger.getLogger(ProductUrlLoader.class.getName()).log(Level.WARNING, "agentProperties values {0}", this.agentProperties);
        }
        catch (final Exception ex) {
            Logger.getLogger(ProductUrlLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static AgentPropsLoader getInstance() {
        if (AgentPropsLoader.agentPropsLoader == null) {
            AgentPropsLoader.agentPropsLoader = new AgentPropsLoader();
        }
        return AgentPropsLoader.agentPropsLoader;
    }
    
    public Properties getGeneralProperites() {
        return this.agentProperties;
    }
    
    public String getValue(final String key) {
        final String value = ((Hashtable<K, String>)this.agentProperties).get(key);
        return value;
    }
    
    static {
        AgentPropsLoader.agentPropsLoader = null;
    }
}
