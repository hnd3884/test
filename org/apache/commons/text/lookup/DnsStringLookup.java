package org.apache.commons.text.lookup;

import java.net.UnknownHostException;
import java.net.InetAddress;

final class DnsStringLookup extends AbstractStringLookup
{
    static final DnsStringLookup INSTANCE;
    
    private DnsStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String[] keys = key.trim().split("\\|");
        final int keyLen = keys.length;
        final String subKey = keys[0].trim();
        final String subValue = (keyLen < 2) ? key : keys[1].trim();
        try {
            final InetAddress inetAddress = InetAddress.getByName(subValue);
            final String s = subKey;
            switch (s) {
                case "name": {
                    return inetAddress.getHostName();
                }
                case "canonical-name": {
                    return inetAddress.getCanonicalHostName();
                }
                case "address": {
                    return inetAddress.getHostAddress();
                }
                default: {
                    return inetAddress.getHostAddress();
                }
            }
        }
        catch (final UnknownHostException e) {
            return null;
        }
    }
    
    static {
        INSTANCE = new DnsStringLookup();
    }
}
