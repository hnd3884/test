package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.X509Certificate;
import java.util.List;
import java.security.cert.CertPath;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.BufferedInputStream;
import java.util.Collection;
import java.security.cert.CertificateException;
import java.io.ByteArrayInputStream;
import org.bouncycastle.util.io.Streams;
import java.security.cert.CRLException;
import java.security.cert.CRL;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.cert.CertificateParsingException;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Sequence;
import java.security.cert.Certificate;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.cert.CertificateFactorySpi;

public class CertificateFactory extends CertificateFactorySpi
{
    private final JcaJceHelper bcHelper;
    private static final PEMUtil PEM_CERT_PARSER;
    private static final PEMUtil PEM_CRL_PARSER;
    private static final PEMUtil PEM_PKCS7_PARSER;
    private ASN1Set sData;
    private int sDataObjectCount;
    private InputStream currentStream;
    private ASN1Set sCrlData;
    private int sCrlDataObjectCount;
    private InputStream currentCrlStream;
    
    public CertificateFactory() {
        this.bcHelper = new BCJcaJceHelper();
        this.sData = null;
        this.sDataObjectCount = 0;
        this.currentStream = null;
        this.sCrlData = null;
        this.sCrlDataObjectCount = 0;
        this.currentCrlStream = null;
    }
    
    private Certificate readDERCertificate(final ASN1InputStream asn1InputStream) throws IOException, CertificateParsingException {
        return this.getCertificate(ASN1Sequence.getInstance(asn1InputStream.readObject()));
    }
    
    private Certificate readPEMCertificate(final InputStream inputStream) throws IOException, CertificateParsingException {
        return this.getCertificate(CertificateFactory.PEM_CERT_PARSER.readPEMObject(inputStream));
    }
    
    private Certificate getCertificate(final ASN1Sequence asn1Sequence) throws CertificateParsingException {
        if (asn1Sequence == null) {
            return null;
        }
        if (asn1Sequence.size() > 1 && asn1Sequence.getObjectAt(0) instanceof ASN1ObjectIdentifier && asn1Sequence.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
            this.sData = SignedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), true)).getCertificates();
            return this.getCertificate();
        }
        return new X509CertificateObject(this.bcHelper, org.bouncycastle.asn1.x509.Certificate.getInstance(asn1Sequence));
    }
    
    private Certificate getCertificate() throws CertificateParsingException {
        if (this.sData != null) {
            while (this.sDataObjectCount < this.sData.size()) {
                final ASN1Encodable object = this.sData.getObjectAt(this.sDataObjectCount++);
                if (object instanceof ASN1Sequence) {
                    return new X509CertificateObject(this.bcHelper, org.bouncycastle.asn1.x509.Certificate.getInstance(object));
                }
            }
        }
        return null;
    }
    
    protected CRL createCRL(final CertificateList list) throws CRLException {
        return new X509CRLObject(this.bcHelper, list);
    }
    
    private CRL readPEMCRL(final InputStream inputStream) throws IOException, CRLException {
        return this.getCRL(CertificateFactory.PEM_CRL_PARSER.readPEMObject(inputStream));
    }
    
    private CRL readDERCRL(final ASN1InputStream asn1InputStream) throws IOException, CRLException {
        return this.getCRL(ASN1Sequence.getInstance(asn1InputStream.readObject()));
    }
    
    private CRL getCRL(final ASN1Sequence asn1Sequence) throws CRLException {
        if (asn1Sequence == null) {
            return null;
        }
        if (asn1Sequence.size() > 1 && asn1Sequence.getObjectAt(0) instanceof ASN1ObjectIdentifier && asn1Sequence.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
            this.sCrlData = SignedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), true)).getCRLs();
            return this.getCRL();
        }
        return this.createCRL(CertificateList.getInstance(asn1Sequence));
    }
    
    private CRL getCRL() throws CRLException {
        if (this.sCrlData == null || this.sCrlDataObjectCount >= this.sCrlData.size()) {
            return null;
        }
        return this.createCRL(CertificateList.getInstance(this.sCrlData.getObjectAt(this.sCrlDataObjectCount++)));
    }
    
    @Override
    public Certificate engineGenerateCertificate(final InputStream inputStream) throws CertificateException {
        if (this.currentStream == null) {
            this.currentStream = inputStream;
            this.sData = null;
            this.sDataObjectCount = 0;
        }
        else if (this.currentStream != inputStream) {
            this.currentStream = inputStream;
            this.sData = null;
            this.sDataObjectCount = 0;
        }
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
                InputStream inputStream2;
                if (inputStream.markSupported()) {
                    inputStream2 = inputStream;
                }
                else {
                    inputStream2 = new ByteArrayInputStream(Streams.readAll(inputStream));
                }
                inputStream2.mark(1);
                final int read = inputStream2.read();
                if (read == -1) {
                    return null;
                }
                inputStream2.reset();
                if (read != 48) {
                    return this.readPEMCertificate(inputStream2);
                }
                return this.readDERCertificate(new ASN1InputStream(inputStream2));
            }
        }
        catch (final Exception ex) {
            throw new ExCertificateException("parsing issue: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public Collection engineGenerateCertificates(final InputStream inputStream) throws CertificateException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        final ArrayList list = new ArrayList();
        Certificate engineGenerateCertificate;
        while ((engineGenerateCertificate = this.engineGenerateCertificate(bufferedInputStream)) != null) {
            list.add(engineGenerateCertificate);
        }
        return list;
    }
    
    @Override
    public CRL engineGenerateCRL(final InputStream inputStream) throws CRLException {
        if (this.currentCrlStream == null) {
            this.currentCrlStream = inputStream;
            this.sCrlData = null;
            this.sCrlDataObjectCount = 0;
        }
        else if (this.currentCrlStream != inputStream) {
            this.currentCrlStream = inputStream;
            this.sCrlData = null;
            this.sCrlDataObjectCount = 0;
        }
        try {
            if (this.sCrlData != null) {
                if (this.sCrlDataObjectCount != this.sCrlData.size()) {
                    return this.getCRL();
                }
                this.sCrlData = null;
                this.sCrlDataObjectCount = 0;
                return null;
            }
            else {
                InputStream inputStream2;
                if (inputStream.markSupported()) {
                    inputStream2 = inputStream;
                }
                else {
                    inputStream2 = new ByteArrayInputStream(Streams.readAll(inputStream));
                }
                inputStream2.mark(1);
                final int read = inputStream2.read();
                if (read == -1) {
                    return null;
                }
                inputStream2.reset();
                if (read != 48) {
                    return this.readPEMCRL(inputStream2);
                }
                return this.readDERCRL(new ASN1InputStream(inputStream2, true));
            }
        }
        catch (final CRLException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new CRLException(ex2.toString());
        }
    }
    
    @Override
    public Collection engineGenerateCRLs(final InputStream inputStream) throws CRLException {
        final ArrayList list = new ArrayList();
        CRL engineGenerateCRL;
        while ((engineGenerateCRL = this.engineGenerateCRL(new BufferedInputStream(inputStream))) != null) {
            list.add(engineGenerateCRL);
        }
        return list;
    }
    
    @Override
    public Iterator engineGetCertPathEncodings() {
        return PKIXCertPath.certPathEncodings.iterator();
    }
    
    @Override
    public CertPath engineGenerateCertPath(final InputStream inputStream) throws CertificateException {
        return this.engineGenerateCertPath(inputStream, "PkiPath");
    }
    
    @Override
    public CertPath engineGenerateCertPath(final InputStream inputStream, final String s) throws CertificateException {
        return new PKIXCertPath(inputStream, s);
    }
    
    @Override
    public CertPath engineGenerateCertPath(final List list) throws CertificateException {
        for (final Object next : list) {
            if (next != null && !(next instanceof X509Certificate)) {
                throw new CertificateException("list contains non X509Certificate object while creating CertPath\n" + next.toString());
            }
        }
        return new PKIXCertPath(list);
    }
    
    static {
        PEM_CERT_PARSER = new PEMUtil("CERTIFICATE");
        PEM_CRL_PARSER = new PEMUtil("CRL");
        PEM_PKCS7_PARSER = new PEMUtil("PKCS7");
    }
    
    private class ExCertificateException extends CertificateException
    {
        private Throwable cause;
        
        public ExCertificateException(final Throwable cause) {
            this.cause = cause;
        }
        
        public ExCertificateException(final String s, final Throwable cause) {
            super(s);
            this.cause = cause;
        }
        
        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}
