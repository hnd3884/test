package sun.net.spi.nameservice.dns;

import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.NameServiceDescriptor;

public final class DNSNameServiceDescriptor implements NameServiceDescriptor
{
    @Override
    public NameService createNameService() throws Exception {
        return new DNSNameService();
    }
    
    @Override
    public String getProviderName() {
        return "sun";
    }
    
    @Override
    public String getType() {
        return "dns";
    }
}
