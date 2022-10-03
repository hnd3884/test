package org.apache.commons.text.lookup;

import java.net.UnknownHostException;
import java.net.InetAddress;

final class LocalHostStringLookup extends AbstractStringLookup
{
    static final LocalHostStringLookup INSTANCE;
    
    private LocalHostStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        try {
            switch (key) {
                case "name": {
                    return InetAddress.getLocalHost().getHostName();
                }
                case "canonical-name": {
                    return InetAddress.getLocalHost().getCanonicalHostName();
                }
                case "address": {
                    return InetAddress.getLocalHost().getHostAddress();
                }
                default: {
                    throw new IllegalArgumentException(key);
                }
            }
        }
        catch (final UnknownHostException e) {
            return null;
        }
    }
    
    static {
        INSTANCE = new LocalHostStringLookup();
    }
}
