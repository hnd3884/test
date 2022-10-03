package com.me.mdm.server.windows.profile.payload;

import java.util.Iterator;
import java.util.List;

public class WindowsNetworkIsolationPayload extends WindowsPayload
{
    String baseURI;
    
    public WindowsNetworkIsolationPayload() {
        this.baseURI = "./Device/Vendor/MSFT/Policy/Config/NetworkIsolation/";
    }
    
    public void setEnterpriseNetworkDomainNames(final List<String> domainList) {
        final Iterator iterator = domainList.iterator();
        final StringBuilder domainString = new StringBuilder();
        while (iterator.hasNext()) {
            domainString.append(iterator.next());
            if (iterator.hasNext()) {
                domainString.append(",");
            }
        }
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "EnterpriseNetworkDomainNames", domainString.toString(), "chr"));
    }
    
    public void setEnterpriseIPRange(final List<String> ipList) {
        final Iterator iterator = ipList.iterator();
        final StringBuilder domainString = new StringBuilder();
        while (iterator.hasNext()) {
            domainString.append(iterator.next());
            if (iterator.hasNext()) {
                domainString.append(",");
            }
        }
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "EnterpriseIPRange", domainString.toString(), "chr"));
    }
    
    public void setEnterpriseCloudResources(final List<String> resList) {
        final Iterator iterator = resList.iterator();
        final StringBuilder domainString = new StringBuilder();
        while (iterator.hasNext()) {
            domainString.append(iterator.next());
            if (iterator.hasNext()) {
                domainString.append(",");
            }
        }
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "EnterpriseCloudResources", domainString.toString(), "chr"));
    }
    
    public void setNeutralResources(final List<String> resList) {
        final Iterator iterator = resList.iterator();
        final StringBuilder domainString = new StringBuilder();
        while (iterator.hasNext()) {
            domainString.append(iterator.next());
            if (iterator.hasNext()) {
                domainString.append(",");
            }
        }
        if (!resList.isEmpty()) {
            this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "NeutralResources", domainString.toString(), "chr"));
        }
    }
    
    public void setInternalProxyResources(final List<String> resList) {
        final Iterator iterator = resList.iterator();
        final StringBuilder domainString = new StringBuilder();
        while (iterator.hasNext()) {
            domainString.append(iterator.next());
            if (iterator.hasNext()) {
                domainString.append(",");
            }
        }
        if (!resList.isEmpty()) {
            this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "EnterpriseInternalProxyServers", domainString.toString(), "chr"));
        }
    }
    
    public void setProxyServer(final List<String> resList) {
        final Iterator iterator = resList.iterator();
        final StringBuilder domainString = new StringBuilder();
        while (iterator.hasNext()) {
            domainString.append(iterator.next());
            if (iterator.hasNext()) {
                domainString.append(",");
            }
        }
        if (!resList.isEmpty()) {
            this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "EnterpriseProxyServers", domainString.toString(), "chr"));
        }
    }
}
