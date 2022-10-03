package com.unboundid.util.ssl.cert;

import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IssuerAlternativeNameExtension extends GeneralAlternativeNameExtension
{
    public static final OID ISSUER_ALTERNATIVE_NAME_OID;
    private static final long serialVersionUID = -1448132657790331913L;
    
    IssuerAlternativeNameExtension(final boolean isCritical, final GeneralNames generalNames) throws CertException {
        super(IssuerAlternativeNameExtension.ISSUER_ALTERNATIVE_NAME_OID, isCritical, generalNames);
    }
    
    IssuerAlternativeNameExtension(final X509CertificateExtension extension) throws CertException {
        super(extension);
    }
    
    @Override
    public String getExtensionName() {
        return CertMessages.INFO_ISSUER_ALT_NAME_EXTENSION_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        this.toString("IssuerAlternativeNameExtension", buffer);
    }
    
    static {
        ISSUER_ALTERNATIVE_NAME_OID = new OID("2.5.29.18");
    }
}
