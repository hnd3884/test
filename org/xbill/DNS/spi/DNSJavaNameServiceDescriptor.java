package org.xbill.DNS.spi;

import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.NameServiceDescriptor;

public class DNSJavaNameServiceDescriptor implements NameServiceDescriptor
{
    private static NameService nameService;
    
    public NameService createNameService() {
        return DNSJavaNameServiceDescriptor.nameService;
    }
    
    public String getType() {
        return "dns";
    }
    
    public String getProviderName() {
        return "dnsjava";
    }
    
    static {
        DNSJavaNameServiceDescriptor.nameService = new DNSJavaNameService();
    }
}
