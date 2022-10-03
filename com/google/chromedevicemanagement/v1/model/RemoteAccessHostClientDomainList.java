package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class RemoteAccessHostClientDomainList extends GenericJson
{
    @Key
    private List<String> domains;
    @Key
    private PolicyOptions policyOptions;
    
    public List<String> getDomains() {
        return this.domains;
    }
    
    public RemoteAccessHostClientDomainList setDomains(final List<String> domains) {
        this.domains = domains;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public RemoteAccessHostClientDomainList setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public RemoteAccessHostClientDomainList set(final String s, final Object o) {
        return (RemoteAccessHostClientDomainList)super.set(s, o);
    }
    
    public RemoteAccessHostClientDomainList clone() {
        return (RemoteAccessHostClientDomainList)super.clone();
    }
}
