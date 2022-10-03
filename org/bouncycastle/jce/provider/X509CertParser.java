package org.bouncycastle.jce.provider;

import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.x509.util.StreamParsingException;
import java.io.BufferedInputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.cert.CertificateParsingException;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import java.security.cert.Certificate;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.x509.X509StreamParserSpi;

public class X509CertParser extends X509StreamParserSpi
{
    private static final PEMUtil PEM_PARSER;
    private ASN1Set sData;
    private int sDataObjectCount;
    private InputStream currentStream;
    
    public X509CertParser() {
        this.sData = null;
        this.sDataObjectCount = 0;
        this.currentStream = null;
    }
    
    private Certificate readDERCertificate(final InputStream inputStream) throws IOException, CertificateParsingException {
        final ASN1Sequence asn1Sequence = (ASN1Sequence)new ASN1InputStream(inputStream).readObject();
        if (asn1Sequence.size() > 1 && asn1Sequence.getObjectAt(0) instanceof ASN1ObjectIdentifier && asn1Sequence.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
            this.sData = new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), true)).getCertificates();
            return this.getCertificate();
        }
        return new X509CertificateObject(org.bouncycastle.asn1.x509.Certificate.getInstance(asn1Sequence));
    }
    
    private Certificate getCertificate() throws CertificateParsingException {
        if (this.sData != null) {
            while (this.sDataObjectCount < this.sData.size()) {
                final ASN1Encodable object = this.sData.getObjectAt(this.sDataObjectCount++);
                if (object instanceof ASN1Sequence) {
                    return new X509CertificateObject(org.bouncycastle.asn1.x509.Certificate.getInstance(object));
                }
            }
        }
        return null;
    }
    
    private Certificate readPEMCertificate(final InputStream inputStream) throws IOException, CertificateParsingException {
        final ASN1Sequence pemObject = X509CertParser.PEM_PARSER.readPEMObject(inputStream);
        if (pemObject != null) {
            return new X509CertificateObject(org.bouncycastle.asn1.x509.Certificate.getInstance(pemObject));
        }
        return null;
    }
    
    @Override
    public void engineInit(final InputStream currentStream) {
        this.currentStream = currentStream;
        this.sData = null;
        this.sDataObjectCount = 0;
        if (!this.currentStream.markSupported()) {
            this.currentStream = new BufferedInputStream(this.currentStream);
        }
    }
    
    @Override
    public Object engineRead() throws StreamParsingException {
        try {
            if (this.sData != null) {
                if (this.sDataObjectCount != this.sData.size()) {
                    return this.getCertificate();
                }
                this.sData = null;
                this.sDataObjectCount = 0;
                return null;
            }
            else {
                this.currentStream.mark(10);
                final int read = this.currentStream.read();
                if (read == -1) {
                    return null;
                }
                if (read != 48) {
                    this.currentStream.reset();
                    return this.readPEMCertificate(this.currentStream);
                }
                this.currentStream.reset();
                return this.readDERCertificate(this.currentStream);
            }
        }
        catch (final Exception ex) {
            throw new StreamParsingException(ex.toString(), ex);
        }
    }
    
    @Override
    public Collection engineReadAll() throws StreamParsingException {
        final ArrayList list = new ArrayList();
        Certificate certificate;
        while ((certificate = (Certificate)this.engineRead()) != null) {
            list.add(certificate);
        }
        return list;
    }
    
    static {
        PEM_PARSER = new PEMUtil("CERTIFICATE");
    }
}
