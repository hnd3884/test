package io.netty.handler.ssl;

import java.util.Iterator;
import java.util.ArrayList;
import io.netty.util.internal.ObjectUtil;
import java.util.Set;
import java.util.List;

public final class SupportedCipherSuiteFilter implements CipherSuiteFilter
{
    public static final SupportedCipherSuiteFilter INSTANCE;
    
    private SupportedCipherSuiteFilter() {
    }
    
    @Override
    public String[] filterCipherSuites(Iterable<String> ciphers, final List<String> defaultCiphers, final Set<String> supportedCiphers) {
        ObjectUtil.checkNotNull(defaultCiphers, "defaultCiphers");
        ObjectUtil.checkNotNull(supportedCiphers, "supportedCiphers");
        List<String> newCiphers;
        if (ciphers == null) {
            newCiphers = new ArrayList<String>(defaultCiphers.size());
            ciphers = defaultCiphers;
        }
        else {
            newCiphers = new ArrayList<String>(supportedCiphers.size());
        }
        for (final String c : ciphers) {
            if (c == null) {
                break;
            }
            if (!supportedCiphers.contains(c)) {
                continue;
            }
            newCiphers.add(c);
        }
        return newCiphers.toArray(new String[0]);
    }
    
    static {
        INSTANCE = new SupportedCipherSuiteFilter();
    }
}
