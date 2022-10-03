package com.me.mdm.server.windows.profile.payload.content.vpn;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;

public class PulseSecureProfileGenerator extends BasePluginProfileGenerator
{
    public PulseSecureProfileGenerator(final String identifer) {
        super(identifer);
    }
    
    @Override
    public void createRootElement() {
        this.root = OMAbstractFactory.getOMFactory().createOMElement("pulse-schema", (OMNamespace)null);
    }
}
