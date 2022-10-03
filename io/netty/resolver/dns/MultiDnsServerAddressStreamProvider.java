package io.netty.resolver.dns;

import java.util.List;

public final class MultiDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider
{
    private final DnsServerAddressStreamProvider[] providers;
    
    public MultiDnsServerAddressStreamProvider(final List<DnsServerAddressStreamProvider> providers) {
        this.providers = providers.toArray(new DnsServerAddressStreamProvider[0]);
    }
    
    public MultiDnsServerAddressStreamProvider(final DnsServerAddressStreamProvider... providers) {
        this.providers = providers.clone();
    }
    
    @Override
    public DnsServerAddressStream nameServerAddressStream(final String hostname) {
        for (final DnsServerAddressStreamProvider provider : this.providers) {
            final DnsServerAddressStream stream = provider.nameServerAddressStream(hostname);
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }
}
