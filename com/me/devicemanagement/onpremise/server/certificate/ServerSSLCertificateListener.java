package com.me.devicemanagement.onpremise.server.certificate;

import org.json.JSONObject;

public interface ServerSSLCertificateListener
{
    public static final int SELF_SIGNED_CERT = 1;
    public static final int THIRD_PARTY_CERT = 2;
    public static final int ENTERPRISE_CA_CERT = 3;
    
    void certificateChanged();
    
    JSONObject canUploadCertificate(final ServerSSLCertificateChangeEvent p0);
}
