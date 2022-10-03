package com.unboundid.util.ssl;

import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedHashSet;
import com.unboundid.util.Validator;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.Set;
import java.security.cert.X509Certificate;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import javax.net.ssl.X509TrustManager;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class HostNameTrustManager implements X509TrustManager
{
    private static final X509Certificate[] NO_CERTIFICATES;
    private final boolean allowWildcards;
    private final Set<String> acceptableHostNames;
    
    public HostNameTrustManager(final boolean allowWildcards, final String... acceptableHostNames) {
        this(allowWildcards, StaticUtils.toList(acceptableHostNames));
    }
    
    public HostNameTrustManager(final boolean allowWildcards, final Collection<String> acceptableHostNames) {
        Validator.ensureNotNull(acceptableHostNames);
        Validator.ensureFalse(acceptableHostNames.isEmpty(), "The set of acceptable host names must not be empty.");
        this.allowWildcards = allowWildcards;
        final LinkedHashSet<String> nameSet = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(acceptableHostNames.size()));
        for (final String s : acceptableHostNames) {
            nameSet.add(StaticUtils.toLowerCase(s));
        }
        this.acceptableHostNames = Collections.unmodifiableSet((Set<? extends String>)nameSet);
    }
    
    public boolean allowWildcards() {
        return this.allowWildcards;
    }
    
    public Set<String> getAcceptableHostNames() {
        return this.acceptableHostNames;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        final StringBuilder buffer = new StringBuilder();
        for (final String s : this.acceptableHostNames) {
            buffer.setLength(0);
            if (HostNameSSLSocketVerifier.certificateIncludesHostname(s, chain[0], this.allowWildcards, buffer)) {
                return;
            }
        }
        throw new CertificateException(SSLMessages.ERR_HOSTNAME_NOT_FOUND.get(buffer.toString()));
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        final StringBuilder buffer = new StringBuilder();
        for (final String s : this.acceptableHostNames) {
            buffer.setLength(0);
            if (HostNameSSLSocketVerifier.certificateIncludesHostname(s, chain[0], this.allowWildcards, buffer)) {
                return;
            }
        }
        throw new CertificateException(SSLMessages.ERR_HOSTNAME_NOT_FOUND.get(buffer.toString()));
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return HostNameTrustManager.NO_CERTIFICATES;
    }
    
    static {
        NO_CERTIFICATES = new X509Certificate[0];
    }
}
