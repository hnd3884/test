package io.netty.handler.ssl;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public final class IdentityCipherSuiteFilter implements CipherSuiteFilter
{
    public static final IdentityCipherSuiteFilter INSTANCE;
    public static final IdentityCipherSuiteFilter INSTANCE_DEFAULTING_TO_SUPPORTED_CIPHERS;
    private final boolean defaultToDefaultCiphers;
    
    private IdentityCipherSuiteFilter(final boolean defaultToDefaultCiphers) {
        this.defaultToDefaultCiphers = defaultToDefaultCiphers;
    }
    
    @Override
    public String[] filterCipherSuites(final Iterable<String> ciphers, final List<String> defaultCiphers, final Set<String> supportedCiphers) {
        if (ciphers == null) {
            return this.defaultToDefaultCiphers ? defaultCiphers.toArray(new String[0]) : supportedCiphers.toArray(new String[0]);
        }
        final List<String> newCiphers = new ArrayList<String>(supportedCiphers.size());
        for (final String c : ciphers) {
            if (c == null) {
                break;
            }
            newCiphers.add(c);
        }
        return newCiphers.toArray(new String[0]);
    }
    
    static {
        INSTANCE = new IdentityCipherSuiteFilter(true);
        INSTANCE_DEFAULTING_TO_SUPPORTED_CIPHERS = new IdentityCipherSuiteFilter(false);
    }
}
