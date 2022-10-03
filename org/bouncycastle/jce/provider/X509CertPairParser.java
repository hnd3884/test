package org.bouncycastle.jce.provider;

import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.x509.util.StreamParsingException;
import java.io.BufferedInputStream;
import java.security.cert.CertificateParsingException;
import java.io.IOException;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.x509.X509CertificatePair;
import java.io.InputStream;
import org.bouncycastle.x509.X509StreamParserSpi;

public class X509CertPairParser extends X509StreamParserSpi
{
    private InputStream currentStream;
    
    public X509CertPairParser() {
        this.currentStream = null;
    }
    
    private X509CertificatePair readDERCrossCertificatePair(final InputStream inputStream) throws IOException, CertificateParsingException {
        return new X509CertificatePair(CertificatePair.getInstance(new ASN1InputStream(inputStream).readObject()));
    }
    
    @Override
    public void engineInit(final InputStream currentStream) {
        this.currentStream = currentStream;
        if (!this.currentStream.markSupported()) {
            this.currentStream = new BufferedInputStream(this.currentStream);
        }
    }
    
    @Override
    public Object engineRead() throws StreamParsingException {
        try {
            this.currentStream.mark(10);
            if (this.currentStream.read() == -1) {
                return null;
            }
            this.currentStream.reset();
            return this.readDERCrossCertificatePair(this.currentStream);
        }
        catch (final Exception ex) {
            throw new StreamParsingException(ex.toString(), ex);
        }
    }
    
    @Override
    public Collection engineReadAll() throws StreamParsingException {
        final ArrayList list = new ArrayList();
        X509CertificatePair x509CertificatePair;
        while ((x509CertificatePair = (X509CertificatePair)this.engineRead()) != null) {
            list.add(x509CertificatePair);
        }
        return list;
    }
}
