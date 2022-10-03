package com.unboundid.util.ssl;

import java.util.Iterator;
import java.security.cert.CertificateException;
import com.unboundid.util.Debug;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.List;
import java.security.cert.X509Certificate;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import javax.net.ssl.X509TrustManager;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AggregateTrustManager implements X509TrustManager
{
    private static final X509Certificate[] NO_CERTIFICATES;
    private final boolean requireAllAccepted;
    private final List<X509TrustManager> trustManagers;
    
    public AggregateTrustManager(final boolean requireAllAccepted, final X509TrustManager... trustManagers) {
        this(requireAllAccepted, StaticUtils.toList(trustManagers));
    }
    
    public AggregateTrustManager(final boolean requireAllAccepted, final Collection<X509TrustManager> trustManagers) {
        Validator.ensureNotNull(trustManagers);
        Validator.ensureFalse(trustManagers.isEmpty(), "The set of associated trust managers must not be empty.");
        this.requireAllAccepted = requireAllAccepted;
        this.trustManagers = Collections.unmodifiableList((List<? extends X509TrustManager>)new ArrayList<X509TrustManager>(trustManagers));
    }
    
    public boolean requireAllAccepted() {
        return this.requireAllAccepted;
    }
    
    public List<X509TrustManager> getAssociatedTrustManagers() {
        return this.trustManagers;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        ArrayList<String> exceptionMessages = null;
        for (final X509TrustManager m : this.trustManagers) {
            try {
                m.checkClientTrusted(chain, authType);
                if (!this.requireAllAccepted) {
                    return;
                }
                continue;
            }
            catch (final CertificateException ce) {
                Debug.debugException(ce);
                if (this.requireAllAccepted) {
                    throw ce;
                }
                if (exceptionMessages == null) {
                    exceptionMessages = new ArrayList<String>(this.trustManagers.size());
                }
                exceptionMessages.add(ce.getMessage());
            }
        }
        if (exceptionMessages == null || exceptionMessages.isEmpty()) {
            return;
        }
        if (exceptionMessages.size() == 1) {
            throw new CertificateException(exceptionMessages.get(0));
        }
        throw new CertificateException(SSLMessages.ERR_AGGREGATE_TRUST_MANAGER_NONE_TRUSTED.get(SSLUtil.certificateToString(chain[0]), StaticUtils.concatenateStrings(exceptionMessages)));
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        ArrayList<String> exceptionMessages = null;
        for (final X509TrustManager m : this.trustManagers) {
            try {
                m.checkServerTrusted(chain, authType);
                if (!this.requireAllAccepted) {
                    return;
                }
                continue;
            }
            catch (final CertificateException ce) {
                Debug.debugException(ce);
                if (this.requireAllAccepted) {
                    throw ce;
                }
                if (exceptionMessages == null) {
                    exceptionMessages = new ArrayList<String>(this.trustManagers.size());
                }
                exceptionMessages.add(ce.getMessage());
            }
        }
        if (exceptionMessages == null || exceptionMessages.isEmpty()) {
            return;
        }
        if (exceptionMessages.size() == 1) {
            throw new CertificateException(exceptionMessages.get(0));
        }
        throw new CertificateException(SSLMessages.ERR_AGGREGATE_TRUST_MANAGER_NONE_TRUSTED.get(SSLUtil.certificateToString(chain[0]), StaticUtils.concatenateStrings(exceptionMessages)));
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return AggregateTrustManager.NO_CERTIFICATES;
    }
    
    static {
        NO_CERTIFICATES = new X509Certificate[0];
    }
}
