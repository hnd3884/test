package com.unboundid.util.ssl.cert;

import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SubjectAlternativeNameExtension extends GeneralAlternativeNameExtension
{
    public static final OID SUBJECT_ALTERNATIVE_NAME_OID;
    private static final long serialVersionUID = 4194307412985686108L;
    
    SubjectAlternativeNameExtension(final boolean isCritical, final GeneralNames generalNames) throws CertException {
        super(SubjectAlternativeNameExtension.SUBJECT_ALTERNATIVE_NAME_OID, isCritical, generalNames);
    }
    
    SubjectAlternativeNameExtension(final X509CertificateExtension extension) throws CertException {
        super(extension);
    }
    
    @Override
    public String getExtensionName() {
        return CertMessages.INFO_SUBJECT_ALT_NAME_EXTENSION_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        this.toString("SubjectAlternativeNameExtension", buffer);
    }
    
    static {
        SUBJECT_ALTERNATIVE_NAME_OID = new OID("2.5.29.17");
    }
}
