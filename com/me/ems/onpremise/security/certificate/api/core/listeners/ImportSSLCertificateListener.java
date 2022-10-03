package com.me.ems.onpremise.security.certificate.api.core.listeners;

import org.json.JSONObject;
import com.me.ems.onpremise.security.certificate.api.core.events.ImportSSLCertificateChangeEvent;

public interface ImportSSLCertificateListener
{
    public static final int SELF_SIGNED_CERT = 1;
    public static final int THIRD_PARTY_CERT = 2;
    public static final int ENTERPRISE_CA_CERT = 3;
    
    void certificateChanged();
    
    JSONObject canUploadCertificate(final ImportSSLCertificateChangeEvent p0);
}
