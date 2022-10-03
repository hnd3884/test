package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.util.Collections;
import java.util.ListIterator;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemObject;
import java.io.Writer;
import org.bouncycastle.util.io.pem.PemWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.security.cert.CertificateEncodingException;
import java.util.Iterator;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.NoSuchProviderException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.cert.CertificateException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.InputStream;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import javax.security.auth.x500.X500Principal;
import java.util.Collection;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.util.List;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.cert.CertPath;

public class PKIXCertPath extends CertPath
{
    private final JcaJceHelper helper;
    static final List certPathEncodings;
    private List certificates;
    
    private List sortCerts(final List list) {
        if (list.size() < 2) {
            return list;
        }
        X500Principal x500Principal = list.get(0).getIssuerX500Principal();
        boolean b = true;
        for (int i = 1; i != list.size(); ++i) {
            if (!x500Principal.equals(((X509Certificate)list.get(i)).getSubjectX500Principal())) {
                b = false;
                break;
            }
            x500Principal = ((X509Certificate)list.get(i)).getIssuerX500Principal();
        }
        if (b) {
            return list;
        }
        final ArrayList list2 = new ArrayList(list.size());
        final ArrayList list3 = new ArrayList(list);
        for (int j = 0; j < list.size(); ++j) {
            final X509Certificate x509Certificate = list.get(j);
            boolean b2 = false;
            final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
            for (int k = 0; k != list.size(); ++k) {
                if (((X509Certificate)list.get(k)).getIssuerX500Principal().equals(subjectX500Principal)) {
                    b2 = true;
                    break;
                }
            }
            if (!b2) {
                list2.add(x509Certificate);
                list.remove(j);
            }
        }
        if (list2.size() > 1) {
            return list3;
        }
        for (int l = 0; l != list2.size(); ++l) {
            final X500Principal issuerX500Principal = ((X509Certificate)list2.get(l)).getIssuerX500Principal();
            for (int n = 0; n < list.size(); ++n) {
                final X509Certificate x509Certificate2 = list.get(n);
                if (issuerX500Principal.equals(x509Certificate2.getSubjectX500Principal())) {
                    list2.add(x509Certificate2);
                    list.remove(n);
                    break;
                }
            }
        }
        if (list.size() > 0) {
            return list3;
        }
        return list2;
    }
    
    PKIXCertPath(final List list) {
        super("X.509");
        this.helper = new BCJcaJceHelper();
        this.certificates = this.sortCerts(new ArrayList(list));
    }
    
    PKIXCertPath(final InputStream inputStream, final String s) throws CertificateException {
        super("X.509");
        this.helper = new BCJcaJceHelper();
        try {
            if (s.equalsIgnoreCase("PkiPath")) {
                final ASN1Primitive object = new ASN1InputStream(inputStream).readObject();
                if (!(object instanceof ASN1Sequence)) {
                    throw new CertificateException("input stream does not contain a ASN1 SEQUENCE while reading PkiPath encoded data to load CertPath");
                }
                final Enumeration objects = ((ASN1Sequence)object).getObjects();
                this.certificates = new ArrayList();
                final CertificateFactory certificateFactory = this.helper.createCertificateFactory("X.509");
                while (objects.hasMoreElements()) {
                    this.certificates.add(0, certificateFactory.generateCertificate(new ByteArrayInputStream(objects.nextElement().toASN1Primitive().getEncoded("DER"))));
                }
            }
            else {
                if (!s.equalsIgnoreCase("PKCS7") && !s.equalsIgnoreCase("PEM")) {
                    throw new CertificateException("unsupported encoding: " + s);
                }
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                this.certificates = new ArrayList();
                Certificate generateCertificate;
                while ((generateCertificate = this.helper.createCertificateFactory("X.509").generateCertificate(bufferedInputStream)) != null) {
                    this.certificates.add(generateCertificate);
                }
            }
        }
        catch (final IOException ex) {
            throw new CertificateException("IOException throw while decoding CertPath:\n" + ex.toString());
        }
        catch (final NoSuchProviderException ex2) {
            throw new CertificateException("BouncyCastle provider not found while trying to get a CertificateFactory:\n" + ex2.toString());
        }
        this.certificates = this.sortCerts(this.certificates);
    }
    
    @Override
    public Iterator getEncodings() {
        return PKIXCertPath.certPathEncodings.iterator();
    }
    
    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        final Iterator encodings = this.getEncodings();
        if (encodings.hasNext()) {
            final Object next = encodings.next();
            if (next instanceof String) {
                return this.getEncoded((String)next);
            }
        }
        return null;
    }
    
    @Override
    public byte[] getEncoded(final String s) throws CertificateEncodingException {
        if (s.equalsIgnoreCase("PkiPath")) {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            final ListIterator listIterator = this.certificates.listIterator(this.certificates.size());
            while (listIterator.hasPrevious()) {
                asn1EncodableVector.add(this.toASN1Object((X509Certificate)listIterator.previous()));
            }
            return this.toDEREncoded(new DERSequence(asn1EncodableVector));
        }
        if (s.equalsIgnoreCase("PKCS7")) {
            final ContentInfo contentInfo = new ContentInfo(PKCSObjectIdentifiers.data, null);
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            for (int i = 0; i != this.certificates.size(); ++i) {
                asn1EncodableVector2.add(this.toASN1Object((X509Certificate)this.certificates.get(i)));
            }
            return this.toDEREncoded(new ContentInfo(PKCSObjectIdentifiers.signedData, new SignedData(new ASN1Integer(1L), new DERSet(), contentInfo, new DERSet(asn1EncodableVector2), null, new DERSet())));
        }
        if (s.equalsIgnoreCase("PEM")) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final PemWriter pemWriter = new PemWriter(new OutputStreamWriter(byteArrayOutputStream));
            try {
                for (int j = 0; j != this.certificates.size(); ++j) {
                    pemWriter.writeObject(new PemObject("CERTIFICATE", ((X509Certificate)this.certificates.get(j)).getEncoded()));
                }
                pemWriter.close();
            }
            catch (final Exception ex) {
                throw new CertificateEncodingException("can't encode certificate for PEM encoded path");
            }
            return byteArrayOutputStream.toByteArray();
        }
        throw new CertificateEncodingException("unsupported encoding: " + s);
    }
    
    @Override
    public List getCertificates() {
        return Collections.unmodifiableList((List<?>)new ArrayList<Object>(this.certificates));
    }
    
    private ASN1Primitive toASN1Object(final X509Certificate x509Certificate) throws CertificateEncodingException {
        try {
            return new ASN1InputStream(x509Certificate.getEncoded()).readObject();
        }
        catch (final Exception ex) {
            throw new CertificateEncodingException("Exception while encoding certificate: " + ex.toString());
        }
    }
    
    private byte[] toDEREncoded(final ASN1Encodable asn1Encodable) throws CertificateEncodingException {
        try {
            return asn1Encodable.toASN1Primitive().getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException("Exception thrown: " + ex);
        }
    }
    
    static {
        final ArrayList list = new ArrayList();
        list.add("PkiPath");
        list.add("PEM");
        list.add("PKCS7");
        certPathEncodings = Collections.unmodifiableList((List<?>)list);
    }
}
