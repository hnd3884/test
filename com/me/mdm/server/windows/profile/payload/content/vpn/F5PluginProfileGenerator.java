package com.me.mdm.server.windows.profile.payload.content.vpn;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;

public class F5PluginProfileGenerator extends BasePluginProfileGenerator
{
    public F5PluginProfileGenerator(final String identifer) {
        super(identifer);
    }
    
    @Override
    public void createRootElement() {
        this.root = OMAbstractFactory.getOMFactory().createOMElement("f5-vpn-conf", (OMNamespace)null);
    }
}
