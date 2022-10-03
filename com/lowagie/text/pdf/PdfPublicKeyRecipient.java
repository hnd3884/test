package com.lowagie.text.pdf;

import java.security.cert.Certificate;

public class PdfPublicKeyRecipient
{
    private Certificate certificate;
    private int permission;
    protected byte[] cms;
    
    public PdfPublicKeyRecipient(final Certificate certificate, final int permission) {
        this.certificate = null;
        this.permission = 0;
        this.cms = null;
        this.certificate = certificate;
        this.permission = permission;
    }
    
    public Certificate getCertificate() {
        return this.certificate;
    }
    
    public int getPermission() {
        return this.permission;
    }
    
    protected void setCms(final byte[] cms) {
        this.cms = cms;
    }
    
    protected byte[] getCms() {
        return this.cms;
    }
}
