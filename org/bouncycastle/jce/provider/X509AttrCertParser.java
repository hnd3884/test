package org.bouncycastle.jce.provider;

import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.x509.util.StreamParsingException;
import java.io.BufferedInputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import java.io.IOException;
import org.bouncycastle.x509.X509V2AttributeCertificate;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.x509.X509AttributeCertificate;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.x509.X509StreamParserSpi;

public class X509AttrCertParser extends X509StreamParserSpi
{
    private static final PEMUtil PEM_PARSER;
    private ASN1Set sData;
    private int sDataObjectCount;
    private InputStream currentStream;
    
    public X509AttrCertParser() {
        this.sData = null;
        this.sDataObjectCount = 0;
        this.currentStream = null;
    }
    
    private X509AttributeCertificate readDERCertificate(final InputStream inputStream) throws IOException {
        final ASN1Sequence asn1Sequence = (ASN1Sequence)new ASN1InputStream(inputStream).readObject();
        if (asn1Sequence.size() > 1 && asn1Sequence.getObjectAt(0) instanceof ASN1ObjectIdentifier && asn1Sequence.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
            this.sData = new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), true)).getCertificates();
            return this.getCertificate();
        }
        return new X509V2AttributeCertificate(asn1Sequence.getEncoded());
    }
    
    private X509AttributeCertificate getCertificate() throws IOException {
        if (this.sData != null) {
            while (this.sDataObjectCount < this.sData.size()) {
                final ASN1Encodable object = this.sData.getObjectAt(this.sDataObjectCount++);
                if (object instanceof ASN1TaggedObject && ((ASN1TaggedObject)object).getTagNo() == 2) {
                    return new X509V2AttributeCertificate(ASN1Sequence.getInstance((ASN1TaggedObject)object, false).getEncoded());
                }
            }
        }
        return null;
    }
    
    private X509AttributeCertificate readPEMCertificate(final InputStream inputStream) throws IOException {
        final ASN1Sequence pemObject = X509AttrCertParser.PEM_PARSER.readPEMObject(inputStream);
        if (pemObject != null) {
            return new X509V2AttributeCertificate(pemObject.getEncoded());
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
        X509AttributeCertificate x509AttributeCertificate;
        while ((x509AttributeCertificate = (X509AttributeCertificate)this.engineRead()) != null) {
            list.add(x509AttributeCertificate);
        }
        return list;
    }
    
    static {
        PEM_PARSER = new PEMUtil("ATTRIBUTE CERTIFICATE");
    }
}
