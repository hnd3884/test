package org.bouncycastle.cert.ocsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.bouncycastle.asn1.x509.Extensions;
import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import java.util.List;
import java.util.Set;
import org.bouncycastle.cert.X509CertificateHolder;

class OCSPUtils
{
    static final X509CertificateHolder[] EMPTY_CERTS;
    static Set EMPTY_SET;
    static List EMPTY_LIST;
    
    static Date extractDate(final ASN1GeneralizedTime asn1GeneralizedTime) {
        try {
            return asn1GeneralizedTime.getDate();
        }
        catch (final Exception ex) {
            throw new IllegalStateException("exception processing GeneralizedTime: " + ex.getMessage());
        }
    }
    
    static Set getCriticalExtensionOIDs(final Extensions extensions) {
        if (extensions == null) {
            return OCSPUtils.EMPTY_SET;
        }
        return Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList(extensions.getCriticalExtensionOIDs())));
    }
    
    static Set getNonCriticalExtensionOIDs(final Extensions extensions) {
        if (extensions == null) {
            return OCSPUtils.EMPTY_SET;
        }
        return Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList(extensions.getNonCriticalExtensionOIDs())));
    }
    
    static List getExtensionOIDs(final Extensions extensions) {
        if (extensions == null) {
            return OCSPUtils.EMPTY_LIST;
        }
        return Collections.unmodifiableList((List<?>)Arrays.asList((T[])extensions.getExtensionOIDs()));
    }
    
    static {
        EMPTY_CERTS = new X509CertificateHolder[0];
        OCSPUtils.EMPTY_SET = Collections.unmodifiableSet((Set<?>)new HashSet<Object>());
        OCSPUtils.EMPTY_LIST = Collections.unmodifiableList((List<?>)new ArrayList<Object>());
    }
}
