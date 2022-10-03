package com.me.mdm.onpremise.server.agent;

import com.me.mdm.core.auth.APIKey;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.agent.DiscoveryServiceHandler;

public class DiscoveryServiceHandlerOnpremiseImpl extends DiscoveryServiceHandler
{
    public NSDictionary setIOSAgentCommDetails(final NSDictionary dict, final APIKey key) {
        final NSDictionary servicesDict = super.setIOSAgentCommDetails(dict, key);
        if (dict.containsKey("Configuration")) {
            this.putIfKeyIsPresentInConfig(dict, "Services", (Object)servicesDict);
            this.putIfKeyIsPresentInConfig(dict, "authtoken", (Object)"");
            this.putIfKeyIsPresentInConfig(dict, "SCOPE", (Object)"");
        }
        return dict;
    }
}
