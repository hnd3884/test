package com.me.mdm.server.windows.profile.payload.content.vpn;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;

public class SoniceWallPluginProfileGenerator extends BasePluginProfileGenerator
{
    public SoniceWallPluginProfileGenerator(final String identifer) {
        super(identifer);
    }
    
    @Override
    public void createRootElement() {
        this.root = OMAbstractFactory.getOMFactory().createOMElement("MobileConnect", (OMNamespace)null);
    }
}
