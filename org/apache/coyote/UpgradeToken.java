package org.apache.coyote;

import org.apache.tomcat.InstanceManager;
import javax.servlet.http.HttpUpgradeHandler;
import org.apache.tomcat.ContextBind;

public final class UpgradeToken
{
    private final ContextBind contextBind;
    private final HttpUpgradeHandler httpUpgradeHandler;
    private final InstanceManager instanceManager;
    private final String protocol;
    
    public UpgradeToken(final HttpUpgradeHandler httpUpgradeHandler, final ContextBind contextBind, final InstanceManager instanceManager, final String protocol) {
        this.contextBind = contextBind;
        this.httpUpgradeHandler = httpUpgradeHandler;
        this.instanceManager = instanceManager;
        this.protocol = protocol;
    }
    
    public final ContextBind getContextBind() {
        return this.contextBind;
    }
    
    public final HttpUpgradeHandler getHttpUpgradeHandler() {
        return this.httpUpgradeHandler;
    }
    
    public final InstanceManager getInstanceManager() {
        return this.instanceManager;
    }
    
    public final String getProtocol() {
        return this.protocol;
    }
}
