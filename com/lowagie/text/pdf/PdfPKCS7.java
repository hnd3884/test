package com.lowagie.text.pdf;

import org.bouncycastle.asn1.ASN1String;
import java.security.cert.X509CRL;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.asn1.x509.X509Extensions;
import java.io.FileInputStream;
import java.io.File;
import java.security.KeyStore;
import org.bouncycastle.asn1.tsp.MessageImprint;
import java.util.Arrays;
import java.security.SignatureException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import org.bouncycastle.asn1.cms.Attribute;
import java.util.Iterator;
import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.asn1.ASN1Set;
import java.util.HashSet;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERTaggedObject;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.IOException;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import com.lowagie.text.ExceptionConverter;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1InputStream;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.jce.provider.X509CertParser;
import java.util.Date;
import java.util.GregorianCalendar;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import java.util.HashMap;
import org.bouncycastle.tsp.TimeStampToken;
import java.util.Calendar;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Set;

public class PdfPKCS7
{
    private byte[] sigAttr;
    private byte[] digestAttr;
    private int version;
    private int signerversion;
    private Set digestalgos;
    private Collection certs;
    private Collection crls;
    private Collection signCerts;
    private X509Certificate signCert;
    private byte[] digest;
    private MessageDigest messageDigest;
    private String digestAlgorithm;
    private String digestEncryptionAlgorithm;
    private Signature sig;
    private transient PrivateKey privKey;
    private byte[] RSAdata;
    private boolean verified;
    private boolean verifyResult;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String provider;
    private static final String ID_PKCS7_DATA = "1.2.840.113549.1.7.1";
    private static final String ID_PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
    private static final String ID_RSA = "1.2.840.113549.1.1.1";
    private static final String ID_DSA = "1.2.840.10040.4.1";
    private static final String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
    private static final String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    private static final String ID_SIGNING_TIME = "1.2.840.113549.1.9.5";
    private static final String ID_ADBE_REVOCATION = "1.2.840.113583.1.1.8";
    private String reason;
    private String location;
    private Calendar signDate;
    private String signName;
    private TimeStampToken timeStampToken;
    private static final HashMap digestNames;
    private static final HashMap algorithmNames;
    private static final HashMap allowedDigests;
    private BasicOCSPResp basicResp;
    
    public static String getDigest(final String oid) {
        final String ret = PdfPKCS7.digestNames.get(oid);
        if (ret == null) {
            return oid;
        }
        return ret;
    }
    
    public static String getAlgorithm(final String oid) {
        final String ret = PdfPKCS7.algorithmNames.get(oid);
        if (ret == null) {
            return oid;
        }
        return ret;
    }
    
    public TimeStampToken getTimeStampToken() {
        return this.timeStampToken;
    }
    
    public Calendar getTimeStampDate() {
        if (this.timeStampToken == null) {
            return null;
        }
        final Calendar cal = new GregorianCalendar();
        final Date date = this.timeStampToken.getTimeStampInfo().getGenTime();
        cal.setTime(date);
        return cal;
    }
    
    public PdfPKCS7(final byte[] contentsKey, final byte[] certsKey, final String provider) {
        try {
            this.provider = provider;
            final X509CertParser cr = new X509CertParser();
            cr.engineInit((InputStream)new ByteArrayInputStream(certsKey));
            this.certs = cr.engineReadAll();
            this.signCerts = this.certs;
            this.signCert = this.certs.iterator().next();
            this.crls = new ArrayList();
            final ASN1InputStream in = new ASN1InputStream((InputStream)new ByteArrayInputStream(contentsKey));
            this.digest = ((DEROctetString)in.readObject()).getOctets();
            if (provider == null) {
                this.sig = Signature.getInstance("SHA1withRSA");
            }
            else {
                this.sig = Signature.getInstance("SHA1withRSA", provider);
            }
            this.sig.initVerify(this.signCert.getPublicKey());
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public BasicOCSPResp getOcsp() {
        return this.basicResp;
    }
    
    private void findOcsp(ASN1Sequence seq) throws IOException {
        this.basicResp = null;
        boolean ret = false;
        while (!(seq.getObjectAt(0) instanceof ASN1ObjectIdentifier) || !((ASN1ObjectIdentifier)seq.getObjectAt(0)).getId().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic.getId())) {
            ret = true;
            int k = 0;
            while (k < seq.size()) {
                if (seq.getObjectAt(k) instanceof ASN1Sequence) {
                    seq = (ASN1Sequence)seq.getObjectAt(0);
                    ret = false;
                    break;
                }
                if (seq.getObjectAt(k) instanceof ASN1TaggedObject) {
                    final ASN1TaggedObject tag = (ASN1TaggedObject)seq.getObjectAt(k);
                    if (tag.getObject() instanceof ASN1Sequence) {
                        seq = (ASN1Sequence)tag.getObject();
                        ret = false;
                        break;
                    }
                    return;
                }
                else {
                    ++k;
                }
            }
            if (ret) {
                return;
            }
        }
        final DEROctetString os = (DEROctetString)seq.getObjectAt(1);
        final ASN1InputStream inp = new ASN1InputStream(os.getOctets());
        final BasicOCSPResponse resp = BasicOCSPResponse.getInstance((Object)inp.readObject());
        this.basicResp = new BasicOCSPResp(resp);
    }
    
    public PdfPKCS7(final byte[] contentsKey, final String provider) {
        try {
            this.provider = provider;
            final ASN1InputStream din = new ASN1InputStream((InputStream)new ByteArrayInputStream(contentsKey));
            ASN1Primitive pkcs;
            try {
                pkcs = din.readObject();
            }
            catch (final IOException e) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("can.t.decode.pkcs7signeddata.object"));
            }
            if (!(pkcs instanceof ASN1Sequence)) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("not.a.valid.pkcs.7.object.not.a.sequence"));
            }
            final ASN1Sequence signedData = (ASN1Sequence)pkcs;
            final ASN1ObjectIdentifier objId = (ASN1ObjectIdentifier)signedData.getObjectAt(0);
            if (!objId.getId().equals("1.2.840.113549.1.7.2")) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("not.a.valid.pkcs.7.object.not.signed.data"));
            }
            final ASN1Sequence content = (ASN1Sequence)((DERTaggedObject)signedData.getObjectAt(1)).getObject();
            this.version = ((ASN1Integer)content.getObjectAt(0)).getValue().intValue();
            this.digestalgos = new HashSet();
            final Enumeration e2 = ((ASN1Set)content.getObjectAt(1)).getObjects();
            while (e2.hasMoreElements()) {
                final ASN1Sequence s = e2.nextElement();
                final ASN1ObjectIdentifier o = (ASN1ObjectIdentifier)s.getObjectAt(0);
                this.digestalgos.add(o.getId());
            }
            final X509CertParser cr = new X509CertParser();
            cr.engineInit((InputStream)new ByteArrayInputStream(contentsKey));
            this.certs = cr.engineReadAll();
            final X509CRLParser cl = new X509CRLParser();
            cl.engineInit((InputStream)new ByteArrayInputStream(contentsKey));
            this.crls = cl.engineReadAll();
            final ASN1Sequence rsaData = (ASN1Sequence)content.getObjectAt(2);
            if (rsaData.size() > 1) {
                final DEROctetString rsaDataContent = (DEROctetString)((DERTaggedObject)rsaData.getObjectAt(1)).getObject();
                this.RSAdata = rsaDataContent.getOctets();
            }
            int next;
            for (next = 3; content.getObjectAt(next) instanceof DERTaggedObject; ++next) {}
            final ASN1Set signerInfos = (ASN1Set)content.getObjectAt(next);
            if (signerInfos.size() != 1) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("this.pkcs.7.object.has.multiple.signerinfos.only.one.is.supported.at.this.time"));
            }
            final ASN1Sequence signerInfo = (ASN1Sequence)signerInfos.getObjectAt(0);
            this.signerversion = ((ASN1Integer)signerInfo.getObjectAt(0)).getValue().intValue();
            final ASN1Sequence issuerAndSerialNumber = (ASN1Sequence)signerInfo.getObjectAt(1);
            final BigInteger serialNumber = ((ASN1Integer)issuerAndSerialNumber.getObjectAt(1)).getValue();
            for (final X509Certificate cert : this.certs) {
                if (serialNumber.equals(cert.getSerialNumber())) {
                    this.signCert = cert;
                    break;
                }
            }
            if (this.signCert == null) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("can.t.find.signing.certificate.with.serial.1", serialNumber.toString(16)));
            }
            this.signCertificateChain();
            this.digestAlgorithm = ((ASN1ObjectIdentifier)((ASN1Sequence)signerInfo.getObjectAt(2)).getObjectAt(0)).getId();
            next = 3;
            if (signerInfo.getObjectAt(next) instanceof ASN1TaggedObject) {
                final ASN1TaggedObject tagsig = (ASN1TaggedObject)signerInfo.getObjectAt(next);
                final ASN1Set sseq = ASN1Set.getInstance(tagsig, false);
                this.sigAttr = sseq.getEncoded("DER");
                for (int k = 0; k < sseq.size(); ++k) {
                    final ASN1Sequence seq2 = (ASN1Sequence)sseq.getObjectAt(k);
                    if (((ASN1ObjectIdentifier)seq2.getObjectAt(0)).getId().equals("1.2.840.113549.1.9.4")) {
                        final ASN1Set set = (ASN1Set)seq2.getObjectAt(1);
                        this.digestAttr = ((DEROctetString)set.getObjectAt(0)).getOctets();
                    }
                    else if (((ASN1ObjectIdentifier)seq2.getObjectAt(0)).getId().equals("1.2.840.113583.1.1.8")) {
                        final ASN1Set setout = (ASN1Set)seq2.getObjectAt(1);
                        final ASN1Sequence seqout = (ASN1Sequence)setout.getObjectAt(0);
                        for (int j = 0; j < seqout.size(); ++j) {
                            final ASN1TaggedObject tg = (ASN1TaggedObject)seqout.getObjectAt(j);
                            if (tg.getTagNo() == 1) {
                                final ASN1Sequence seqin = (ASN1Sequence)tg.getObject();
                                this.findOcsp(seqin);
                            }
                        }
                    }
                }
                if (this.digestAttr == null) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("authenticated.attribute.is.missing.the.digest"));
                }
                ++next;
            }
            this.digestEncryptionAlgorithm = ((ASN1ObjectIdentifier)((ASN1Sequence)signerInfo.getObjectAt(next++)).getObjectAt(0)).getId();
            this.digest = ((DEROctetString)signerInfo.getObjectAt(next++)).getOctets();
            if (next < signerInfo.size() && signerInfo.getObjectAt(next) instanceof DERTaggedObject) {
                final DERTaggedObject taggedObject = (DERTaggedObject)signerInfo.getObjectAt(next);
                final ASN1Set unat = ASN1Set.getInstance((ASN1TaggedObject)taggedObject, false);
                final AttributeTable attble = new AttributeTable(unat);
                final Attribute ts = attble.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
                if (ts != null && ts.getAttrValues().size() > 0) {
                    final ASN1Set attributeValues = ts.getAttrValues();
                    final ASN1Sequence tokenSequence = ASN1Sequence.getInstance((Object)attributeValues.getObjectAt(0));
                    final ContentInfo contentInfo = new ContentInfo(tokenSequence);
                    this.timeStampToken = new TimeStampToken(contentInfo);
                }
            }
            if (this.RSAdata != null || this.digestAttr != null) {
                if (provider == null || provider.startsWith("SunPKCS11")) {
                    this.messageDigest = MessageDigest.getInstance(this.getHashAlgorithm());
                }
                else {
                    this.messageDigest = MessageDigest.getInstance(this.getHashAlgorithm(), provider);
                }
            }
            if (provider == null) {
                this.sig = Signature.getInstance(this.getDigestAlgorithm());
            }
            else {
                this.sig = Signature.getInstance(this.getDigestAlgorithm(), provider);
            }
            this.sig.initVerify(this.signCert.getPublicKey());
        }
        catch (final Exception e3) {
            throw new ExceptionConverter(e3);
        }
    }
    
    public PdfPKCS7(final PrivateKey privKey, final Certificate[] certChain, final CRL[] crlList, final String hashAlgorithm, final String provider, final boolean hasRSAdata) throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {
        this.privKey = privKey;
        this.provider = provider;
        this.digestAlgorithm = PdfPKCS7.allowedDigests.get(hashAlgorithm.toUpperCase());
        if (this.digestAlgorithm == null) {
            throw new NoSuchAlgorithmException(MessageLocalization.getComposedMessage("unknown.hash.algorithm.1", hashAlgorithm));
        }
        final int n = 1;
        this.signerversion = n;
        this.version = n;
        this.certs = new ArrayList();
        this.crls = new ArrayList();
        (this.digestalgos = new HashSet()).add(this.digestAlgorithm);
        this.signCert = (X509Certificate)certChain[0];
        for (int i = 0; i < certChain.length; ++i) {
            this.certs.add(certChain[i]);
        }
        if (crlList != null) {
            for (int i = 0; i < crlList.length; ++i) {
                this.crls.add(crlList[i]);
            }
        }
        if (privKey != null) {
            this.digestEncryptionAlgorithm = privKey.getAlgorithm();
            if (this.digestEncryptionAlgorithm.equals("RSA")) {
                this.digestEncryptionAlgorithm = "1.2.840.113549.1.1.1";
            }
            else {
                if (!this.digestEncryptionAlgorithm.equals("DSA")) {
                    throw new NoSuchAlgorithmException(MessageLocalization.getComposedMessage("unknown.key.algorithm.1", this.digestEncryptionAlgorithm));
                }
                this.digestEncryptionAlgorithm = "1.2.840.10040.4.1";
            }
        }
        if (hasRSAdata) {
            this.RSAdata = new byte[0];
            if (provider == null || provider.startsWith("SunPKCS11")) {
                this.messageDigest = MessageDigest.getInstance(this.getHashAlgorithm());
            }
            else {
                this.messageDigest = MessageDigest.getInstance(this.getHashAlgorithm(), provider);
            }
        }
        if (privKey != null) {
            if (provider == null) {
                this.sig = Signature.getInstance(this.getDigestAlgorithm());
            }
            else {
                this.sig = Signature.getInstance(this.getDigestAlgorithm(), provider);
            }
            this.sig.initSign(privKey);
        }
    }
    
    public void update(final byte[] buf, final int off, final int len) throws SignatureException {
        if (this.RSAdata != null || this.digestAttr != null) {
            this.messageDigest.update(buf, off, len);
        }
        else {
            this.sig.update(buf, off, len);
        }
    }
    
    public boolean verify() throws SignatureException {
        if (this.verified) {
            return this.verifyResult;
        }
        if (this.sigAttr != null) {
            this.sig.update(this.sigAttr);
            if (this.RSAdata != null) {
                final byte[] msd = this.messageDigest.digest();
                this.messageDigest.update(msd);
            }
            this.verifyResult = (Arrays.equals(this.messageDigest.digest(), this.digestAttr) && this.sig.verify(this.digest));
        }
        else {
            if (this.RSAdata != null) {
                this.sig.update(this.messageDigest.digest());
            }
            this.verifyResult = this.sig.verify(this.digest);
        }
        this.verified = true;
        return this.verifyResult;
    }
    
    public boolean verifyTimestampImprint() throws NoSuchAlgorithmException {
        if (this.timeStampToken == null) {
            return false;
        }
        final MessageImprint imprint = this.timeStampToken.getTimeStampInfo().toTSTInfo().getMessageImprint();
        final byte[] md = MessageDigest.getInstance("SHA-1").digest(this.digest);
        final byte[] imphashed = imprint.getHashedMessage();
        final boolean res = Arrays.equals(md, imphashed);
        return res;
    }
    
    public Certificate[] getCertificates() {
        return this.certs.toArray(new X509Certificate[this.certs.size()]);
    }
    
    public Certificate[] getSignCertificateChain() {
        return this.signCerts.toArray(new X509Certificate[this.signCerts.size()]);
    }
    
    private void signCertificateChain() {
        final ArrayList cc = new ArrayList();
        cc.add(this.signCert);
        final ArrayList oc = new ArrayList(this.certs);
        for (int k = 0; k < oc.size(); ++k) {
            if (this.signCert.getSerialNumber().equals(oc.get(k).getSerialNumber())) {
                oc.remove(k);
                --k;
            }
        }
        boolean found = true;
        while (found) {
            final X509Certificate v = cc.get(cc.size() - 1);
            found = false;
            int i = 0;
            while (i < oc.size()) {
                try {
                    if (this.provider == null) {
                        v.verify(oc.get(i).getPublicKey());
                    }
                    else {
                        v.verify(oc.get(i).getPublicKey(), this.provider);
                    }
                    found = true;
                    cc.add(oc.get(i));
                    oc.remove(i);
                }
                catch (final Exception ex) {
                    ++i;
                    continue;
                }
                break;
            }
        }
        this.signCerts = cc;
    }
    
    public Collection getCRLs() {
        return this.crls;
    }
    
    public X509Certificate getSigningCertificate() {
        return this.signCert;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public int getSigningInfoVersion() {
        return this.signerversion;
    }
    
    public String getDigestAlgorithm() {
        String dea = getAlgorithm(this.digestEncryptionAlgorithm);
        if (dea == null) {
            dea = this.digestEncryptionAlgorithm;
        }
        return this.getHashAlgorithm() + "with" + dea;
    }
    
    public String getHashAlgorithm() {
        return getDigest(this.digestAlgorithm);
    }
    
    public static KeyStore loadCacertsKeyStore() {
        return loadCacertsKeyStore(null);
    }
    
    public static KeyStore loadCacertsKeyStore(final String provider) {
        File file = new File(System.getProperty("java.home"), "lib");
        file = new File(file, "security");
        file = new File(file, "cacerts");
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            KeyStore k;
            if (provider == null) {
                k = KeyStore.getInstance("JKS");
            }
            else {
                k = KeyStore.getInstance("JKS", provider);
            }
            k.load(fin, null);
            return k;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    public static String verifyCertificate(final X509Certificate cert, final Collection crls, Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        if (cert.hasUnsupportedCriticalExtension()) {
            return "Has unsupported critical extension";
        }
        try {
            cert.checkValidity(calendar.getTime());
        }
        catch (final Exception e) {
            return e.getMessage();
        }
        if (crls != null) {
            final Iterator it = crls.iterator();
            while (it.hasNext()) {
                if (it.next().isRevoked(cert)) {
                    return "Certificate revoked";
                }
            }
        }
        return null;
    }
    
    public static Object[] verifyCertificates(final Certificate[] certs, final KeyStore keystore, final Collection crls, Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        for (int k = 0; k < certs.length; ++k) {
            final X509Certificate cert = (X509Certificate)certs[k];
            final String err = verifyCertificate(cert, crls, calendar);
            if (err != null) {
                return new Object[] { cert, err };
            }
            try {
                final Enumeration aliases = keystore.aliases();
                while (aliases.hasMoreElements()) {
                    try {
                        final String alias = aliases.nextElement();
                        if (!keystore.isCertificateEntry(alias)) {
                            continue;
                        }
                        final X509Certificate certStoreX509 = (X509Certificate)keystore.getCertificate(alias);
                        if (verifyCertificate(certStoreX509, crls, calendar) != null) {
                            continue;
                        }
                        try {
                            cert.verify(certStoreX509.getPublicKey());
                            return null;
                        }
                        catch (final Exception e) {}
                    }
                    catch (final Exception ex) {
                        continue;
                    }
                    break;
                }
            }
            catch (final Exception ex2) {}
            int j;
            for (j = 0; j < certs.length; ++j) {
                if (j != k) {
                    final X509Certificate certNext = (X509Certificate)certs[j];
                    try {
                        cert.verify(certNext.getPublicKey());
                        break;
                    }
                    catch (final Exception ex3) {}
                }
            }
            if (j == certs.length) {
                return new Object[] { cert, "Cannot be verified against the KeyStore or the certificate chain" };
            }
        }
        return new Object[] { null, "Invalid state. Possible circular certificate chain" };
    }
    
    public static String getOCSPURL(final X509Certificate certificate) {
        try {
            final ASN1Primitive obj = getExtensionValue(certificate, X509Extensions.AuthorityInfoAccess.getId());
            if (obj == null) {
                return null;
            }
            final ASN1Sequence AccessDescriptions = (ASN1Sequence)obj;
            int i = 0;
            while (i < AccessDescriptions.size()) {
                final ASN1Sequence AccessDescription = (ASN1Sequence)AccessDescriptions.getObjectAt(i);
                if (AccessDescription.size() == 2 && AccessDescription.getObjectAt(0) instanceof ASN1ObjectIdentifier && ((ASN1ObjectIdentifier)AccessDescription.getObjectAt(0)).getId().equals("1.3.6.1.5.5.7.48.1")) {
                    final String AccessLocation = getStringFromGeneralName((ASN1Primitive)AccessDescription.getObjectAt(1));
                    if (AccessLocation == null) {
                        return "";
                    }
                    return AccessLocation;
                }
                else {
                    ++i;
                }
            }
        }
        catch (final Exception ex) {}
        return null;
    }
    
    public boolean isRevocationValid() {
        if (this.basicResp == null) {
            return false;
        }
        if (this.signCerts.size() < 2) {
            return false;
        }
        try {
            final X509Certificate[] cs = (X509Certificate[])this.getSignCertificateChain();
            final SingleResp sr = this.basicResp.getResponses()[0];
            final CertificateID cid = sr.getCertID();
            final X509Certificate sigcer = this.getSigningCertificate();
            final X509Certificate isscer = cs[1];
            final DigestCalculatorProvider digCalcProv = new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build();
            final CertificateID id = new CertificateID(digCalcProv.get(CertificateID.HASH_SHA1), (X509CertificateHolder)new JcaX509CertificateHolder(isscer), sigcer.getSerialNumber());
            return id.equals((Object)cid);
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    private static ASN1Primitive getExtensionValue(final X509Certificate cert, final String oid) throws IOException {
        final byte[] bytes = cert.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        ASN1InputStream aIn = new ASN1InputStream((InputStream)new ByteArrayInputStream(bytes));
        final ASN1OctetString octs = (ASN1OctetString)aIn.readObject();
        aIn = new ASN1InputStream((InputStream)new ByteArrayInputStream(octs.getOctets()));
        return aIn.readObject();
    }
    
    private static String getStringFromGeneralName(final ASN1Primitive names) throws IOException {
        final DERTaggedObject taggedObject = (DERTaggedObject)names;
        return new String(ASN1OctetString.getInstance((ASN1TaggedObject)taggedObject, false).getOctets(), StandardCharsets.ISO_8859_1);
    }
    
    private static ASN1Primitive getIssuer(final byte[] enc) {
        try {
            final ASN1InputStream in = new ASN1InputStream((InputStream)new ByteArrayInputStream(enc));
            final ASN1Sequence seq = (ASN1Sequence)in.readObject();
            return (ASN1Primitive)seq.getObjectAt((seq.getObjectAt(0) instanceof DERTaggedObject) ? 3 : 2);
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    private static ASN1Primitive getSubject(final byte[] enc) {
        try {
            final ASN1InputStream in = new ASN1InputStream((InputStream)new ByteArrayInputStream(enc));
            final ASN1Sequence seq = (ASN1Sequence)in.readObject();
            return (ASN1Primitive)seq.getObjectAt((seq.getObjectAt(0) instanceof DERTaggedObject) ? 5 : 4);
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static X509Name getIssuerFields(final X509Certificate cert) {
        try {
            return new X509Name((ASN1Sequence)getIssuer(cert.getTBSCertificate()));
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static X509Name getSubjectFields(final X509Certificate cert) {
        try {
            return new X509Name((ASN1Sequence)getSubject(cert.getTBSCertificate()));
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public byte[] getEncodedPKCS1() {
        try {
            if (this.externalDigest != null) {
                this.digest = this.externalDigest;
            }
            else {
                this.digest = this.sig.sign();
            }
            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            final ASN1OutputStream dout = new ASN1OutputStream((OutputStream)bOut);
            dout.writeObject((ASN1Encodable)new DEROctetString(this.digest));
            dout.close();
            return bOut.toByteArray();
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public void setExternalDigest(final byte[] digest, final byte[] RSAdata, final String digestEncryptionAlgorithm) {
        this.externalDigest = digest;
        this.externalRSAdata = RSAdata;
        if (digestEncryptionAlgorithm != null) {
            if (digestEncryptionAlgorithm.equals("RSA")) {
                this.digestEncryptionAlgorithm = "1.2.840.113549.1.1.1";
            }
            else {
                if (!digestEncryptionAlgorithm.equals("DSA")) {
                    throw new ExceptionConverter(new NoSuchAlgorithmException(MessageLocalization.getComposedMessage("unknown.key.algorithm.1", digestEncryptionAlgorithm)));
                }
                this.digestEncryptionAlgorithm = "1.2.840.10040.4.1";
            }
        }
    }
    
    public byte[] getEncodedPKCS7() {
        return this.getEncodedPKCS7(null, null, null, null);
    }
    
    public byte[] getEncodedPKCS7(final byte[] secondDigest, final Calendar signingTime) {
        return this.getEncodedPKCS7(secondDigest, signingTime, null, null);
    }
    
    public byte[] getEncodedPKCS7(final byte[] secondDigest, final Calendar signingTime, final TSAClient tsaClient, final byte[] ocsp) {
        try {
            if (this.externalDigest != null) {
                this.digest = this.externalDigest;
                if (this.RSAdata != null) {
                    this.RSAdata = this.externalRSAdata;
                }
            }
            else if (this.externalRSAdata != null && this.RSAdata != null) {
                this.RSAdata = this.externalRSAdata;
                this.sig.update(this.RSAdata);
                this.digest = this.sig.sign();
            }
            else {
                if (this.RSAdata != null) {
                    this.RSAdata = this.messageDigest.digest();
                    this.sig.update(this.RSAdata);
                }
                this.digest = this.sig.sign();
            }
            final ASN1EncodableVector digestAlgorithms = new ASN1EncodableVector();
            final Iterator it = this.digestalgos.iterator();
            while (it.hasNext()) {
                final ASN1EncodableVector algos = new ASN1EncodableVector();
                algos.add((ASN1Encodable)new ASN1ObjectIdentifier((String)it.next()));
                algos.add((ASN1Encodable)DERNull.INSTANCE);
                digestAlgorithms.add((ASN1Encodable)new DERSequence(algos));
            }
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier("1.2.840.113549.1.7.1"));
            if (this.RSAdata != null) {
                v.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)new DEROctetString(this.RSAdata)));
            }
            final DERSequence contentinfo = new DERSequence(v);
            v = new ASN1EncodableVector();
            final Iterator i = this.certs.iterator();
            while (i.hasNext()) {
                final ASN1InputStream tempstream = new ASN1InputStream((InputStream)new ByteArrayInputStream(i.next().getEncoded()));
                v.add((ASN1Encodable)tempstream.readObject());
            }
            final DERSet dercertificates = new DERSet(v);
            final ASN1EncodableVector signerinfo = new ASN1EncodableVector();
            signerinfo.add((ASN1Encodable)new ASN1Integer((long)this.signerversion));
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)getIssuer(this.signCert.getTBSCertificate()));
            v.add((ASN1Encodable)new ASN1Integer(this.signCert.getSerialNumber()));
            signerinfo.add((ASN1Encodable)new DERSequence(v));
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier(this.digestAlgorithm));
            v.add((ASN1Encodable)new DERNull());
            signerinfo.add((ASN1Encodable)new DERSequence(v));
            if (secondDigest != null && signingTime != null) {
                signerinfo.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.getAuthenticatedAttributeSet(secondDigest, signingTime, ocsp)));
            }
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier(this.digestEncryptionAlgorithm));
            v.add((ASN1Encodable)new DERNull());
            signerinfo.add((ASN1Encodable)new DERSequence(v));
            signerinfo.add((ASN1Encodable)new DEROctetString(this.digest));
            if (tsaClient != null) {
                final byte[] tsImprint = MessageDigest.getInstance("SHA-1").digest(this.digest);
                final byte[] tsToken = tsaClient.getTimeStampToken(this, tsImprint);
                if (tsToken != null) {
                    final ASN1EncodableVector unauthAttributes = this.buildUnauthenticatedAttributes(tsToken);
                    if (unauthAttributes != null) {
                        signerinfo.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)new DERSet(unauthAttributes)));
                    }
                }
            }
            final ASN1EncodableVector body = new ASN1EncodableVector();
            body.add((ASN1Encodable)new ASN1Integer((long)this.version));
            body.add((ASN1Encodable)new DERSet(digestAlgorithms));
            body.add((ASN1Encodable)contentinfo);
            body.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)dercertificates));
            body.add((ASN1Encodable)new DERSet((ASN1Encodable)new DERSequence(signerinfo)));
            final ASN1EncodableVector whole = new ASN1EncodableVector();
            whole.add((ASN1Encodable)new ASN1ObjectIdentifier("1.2.840.113549.1.7.2"));
            whole.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)new DERSequence(body)));
            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            final ASN1OutputStream dout = new ASN1OutputStream((OutputStream)bOut);
            dout.writeObject((ASN1Encodable)new DERSequence(whole));
            dout.close();
            return bOut.toByteArray();
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    private ASN1EncodableVector buildUnauthenticatedAttributes(final byte[] timeStampToken) throws IOException {
        if (timeStampToken == null) {
            return null;
        }
        final String ID_TIME_STAMP_TOKEN = "1.2.840.113549.1.9.16.2.14";
        final ASN1InputStream tempstream = new ASN1InputStream((InputStream)new ByteArrayInputStream(timeStampToken));
        final ASN1EncodableVector unauthAttributes = new ASN1EncodableVector();
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)new ASN1ObjectIdentifier(ID_TIME_STAMP_TOKEN));
        final ASN1Sequence seq = (ASN1Sequence)tempstream.readObject();
        v.add((ASN1Encodable)new DERSet((ASN1Encodable)seq));
        unauthAttributes.add((ASN1Encodable)new DERSequence(v));
        return unauthAttributes;
    }
    
    public byte[] getAuthenticatedAttributeBytes(final byte[] secondDigest, final Calendar signingTime, final byte[] ocsp) {
        try {
            return this.getAuthenticatedAttributeSet(secondDigest, signingTime, ocsp).getEncoded("DER");
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    private DERSet getAuthenticatedAttributeSet(final byte[] secondDigest, final Calendar signingTime, final byte[] ocsp) {
        try {
            final ASN1EncodableVector attribute = new ASN1EncodableVector();
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier("1.2.840.113549.1.9.3"));
            v.add((ASN1Encodable)new DERSet((ASN1Encodable)new ASN1ObjectIdentifier("1.2.840.113549.1.7.1")));
            attribute.add((ASN1Encodable)new DERSequence(v));
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier("1.2.840.113549.1.9.5"));
            v.add((ASN1Encodable)new DERSet((ASN1Encodable)new DERUTCTime(signingTime.getTime())));
            attribute.add((ASN1Encodable)new DERSequence(v));
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier("1.2.840.113549.1.9.4"));
            v.add((ASN1Encodable)new DERSet((ASN1Encodable)new DEROctetString(secondDigest)));
            attribute.add((ASN1Encodable)new DERSequence(v));
            if (ocsp != null) {
                v = new ASN1EncodableVector();
                v.add((ASN1Encodable)new ASN1ObjectIdentifier("1.2.840.113583.1.1.8"));
                final DEROctetString doctet = new DEROctetString(ocsp);
                final ASN1EncodableVector vo1 = new ASN1EncodableVector();
                final ASN1EncodableVector v2 = new ASN1EncodableVector();
                v2.add((ASN1Encodable)OCSPObjectIdentifiers.id_pkix_ocsp_basic);
                v2.add((ASN1Encodable)doctet);
                final ASN1Enumerated den = new ASN1Enumerated(0);
                final ASN1EncodableVector v3 = new ASN1EncodableVector();
                v3.add((ASN1Encodable)den);
                v3.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DERSequence(v2)));
                vo1.add((ASN1Encodable)new DERSequence(v3));
                v.add((ASN1Encodable)new DERSet((ASN1Encodable)new DERSequence((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)new DERSequence(vo1)))));
                attribute.add((ASN1Encodable)new DERSequence(v));
            }
            else if (!this.crls.isEmpty()) {
                v = new ASN1EncodableVector();
                v.add((ASN1Encodable)new ASN1ObjectIdentifier("1.2.840.113583.1.1.8"));
                final ASN1EncodableVector v4 = new ASN1EncodableVector();
                final Iterator i = this.crls.iterator();
                while (i.hasNext()) {
                    final ASN1InputStream t = new ASN1InputStream((InputStream)new ByteArrayInputStream(i.next().getEncoded()));
                    v4.add((ASN1Encodable)t.readObject());
                }
                v.add((ASN1Encodable)new DERSet((ASN1Encodable)new DERSequence((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DERSequence(v4)))));
                attribute.add((ASN1Encodable)new DERSequence(v));
            }
            return new DERSet(attribute);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public Calendar getSignDate() {
        return this.signDate;
    }
    
    public void setSignDate(final Calendar signDate) {
        this.signDate = signDate;
    }
    
    public String getSignName() {
        return this.signName;
    }
    
    public void setSignName(final String signName) {
        this.signName = signName;
    }
    
    static {
        digestNames = new HashMap();
        algorithmNames = new HashMap();
        allowedDigests = new HashMap();
        PdfPKCS7.digestNames.put("1.2.840.113549.2.5", "MD5");
        PdfPKCS7.digestNames.put("1.2.840.113549.2.2", "MD2");
        PdfPKCS7.digestNames.put("1.3.14.3.2.26", "SHA1");
        PdfPKCS7.digestNames.put("2.16.840.1.101.3.4.2.4", "SHA224");
        PdfPKCS7.digestNames.put("2.16.840.1.101.3.4.2.1", "SHA256");
        PdfPKCS7.digestNames.put("2.16.840.1.101.3.4.2.2", "SHA384");
        PdfPKCS7.digestNames.put("2.16.840.1.101.3.4.2.3", "SHA512");
        PdfPKCS7.digestNames.put("1.3.36.3.2.2", "RIPEMD128");
        PdfPKCS7.digestNames.put("1.3.36.3.2.1", "RIPEMD160");
        PdfPKCS7.digestNames.put("1.3.36.3.2.3", "RIPEMD256");
        PdfPKCS7.digestNames.put("1.2.840.113549.1.1.4", "MD5");
        PdfPKCS7.digestNames.put("1.2.840.113549.1.1.2", "MD2");
        PdfPKCS7.digestNames.put("1.2.840.113549.1.1.5", "SHA1");
        PdfPKCS7.digestNames.put("1.2.840.113549.1.1.14", "SHA224");
        PdfPKCS7.digestNames.put("1.2.840.113549.1.1.11", "SHA256");
        PdfPKCS7.digestNames.put("1.2.840.113549.1.1.12", "SHA384");
        PdfPKCS7.digestNames.put("1.2.840.113549.1.1.13", "SHA512");
        PdfPKCS7.digestNames.put("1.2.840.113549.2.5", "MD5");
        PdfPKCS7.digestNames.put("1.2.840.113549.2.2", "MD2");
        PdfPKCS7.digestNames.put("1.2.840.10040.4.3", "SHA1");
        PdfPKCS7.digestNames.put("2.16.840.1.101.3.4.3.1", "SHA224");
        PdfPKCS7.digestNames.put("2.16.840.1.101.3.4.3.2", "SHA256");
        PdfPKCS7.digestNames.put("2.16.840.1.101.3.4.3.3", "SHA384");
        PdfPKCS7.digestNames.put("2.16.840.1.101.3.4.3.4", "SHA512");
        PdfPKCS7.digestNames.put("1.3.36.3.3.1.3", "RIPEMD128");
        PdfPKCS7.digestNames.put("1.3.36.3.3.1.2", "RIPEMD160");
        PdfPKCS7.digestNames.put("1.3.36.3.3.1.4", "RIPEMD256");
        PdfPKCS7.algorithmNames.put("1.2.840.113549.1.1.1", "RSA");
        PdfPKCS7.algorithmNames.put("1.2.840.10040.4.1", "DSA");
        PdfPKCS7.algorithmNames.put("1.2.840.113549.1.1.2", "RSA");
        PdfPKCS7.algorithmNames.put("1.2.840.113549.1.1.4", "RSA");
        PdfPKCS7.algorithmNames.put("1.2.840.113549.1.1.5", "RSA");
        PdfPKCS7.algorithmNames.put("1.2.840.113549.1.1.14", "RSA");
        PdfPKCS7.algorithmNames.put("1.2.840.113549.1.1.11", "RSA");
        PdfPKCS7.algorithmNames.put("1.2.840.113549.1.1.12", "RSA");
        PdfPKCS7.algorithmNames.put("1.2.840.113549.1.1.13", "RSA");
        PdfPKCS7.algorithmNames.put("1.2.840.10040.4.3", "DSA");
        PdfPKCS7.algorithmNames.put("2.16.840.1.101.3.4.3.1", "DSA");
        PdfPKCS7.algorithmNames.put("2.16.840.1.101.3.4.3.2", "DSA");
        PdfPKCS7.algorithmNames.put("1.3.36.3.3.1.3", "RSA");
        PdfPKCS7.algorithmNames.put("1.3.36.3.3.1.2", "RSA");
        PdfPKCS7.algorithmNames.put("1.3.36.3.3.1.4", "RSA");
        PdfPKCS7.allowedDigests.put("MD5", "1.2.840.113549.2.5");
        PdfPKCS7.allowedDigests.put("MD2", "1.2.840.113549.2.2");
        PdfPKCS7.allowedDigests.put("SHA1", "1.3.14.3.2.26");
        PdfPKCS7.allowedDigests.put("SHA224", "2.16.840.1.101.3.4.2.4");
        PdfPKCS7.allowedDigests.put("SHA256", "2.16.840.1.101.3.4.2.1");
        PdfPKCS7.allowedDigests.put("SHA384", "2.16.840.1.101.3.4.2.2");
        PdfPKCS7.allowedDigests.put("SHA512", "2.16.840.1.101.3.4.2.3");
        PdfPKCS7.allowedDigests.put("MD-5", "1.2.840.113549.2.5");
        PdfPKCS7.allowedDigests.put("MD-2", "1.2.840.113549.2.2");
        PdfPKCS7.allowedDigests.put("SHA-1", "1.3.14.3.2.26");
        PdfPKCS7.allowedDigests.put("SHA-224", "2.16.840.1.101.3.4.2.4");
        PdfPKCS7.allowedDigests.put("SHA-256", "2.16.840.1.101.3.4.2.1");
        PdfPKCS7.allowedDigests.put("SHA-384", "2.16.840.1.101.3.4.2.2");
        PdfPKCS7.allowedDigests.put("SHA-512", "2.16.840.1.101.3.4.2.3");
        PdfPKCS7.allowedDigests.put("RIPEMD128", "1.3.36.3.2.2");
        PdfPKCS7.allowedDigests.put("RIPEMD-128", "1.3.36.3.2.2");
        PdfPKCS7.allowedDigests.put("RIPEMD160", "1.3.36.3.2.1");
        PdfPKCS7.allowedDigests.put("RIPEMD-160", "1.3.36.3.2.1");
        PdfPKCS7.allowedDigests.put("RIPEMD256", "1.3.36.3.2.3");
        PdfPKCS7.allowedDigests.put("RIPEMD-256", "1.3.36.3.2.3");
    }
    
    public static class X509Name
    {
        public static final ASN1ObjectIdentifier C;
        public static final ASN1ObjectIdentifier O;
        public static final ASN1ObjectIdentifier OU;
        public static final ASN1ObjectIdentifier T;
        public static final ASN1ObjectIdentifier CN;
        public static final ASN1ObjectIdentifier SN;
        public static final ASN1ObjectIdentifier L;
        public static final ASN1ObjectIdentifier ST;
        public static final ASN1ObjectIdentifier SURNAME;
        public static final ASN1ObjectIdentifier GIVENNAME;
        public static final ASN1ObjectIdentifier INITIALS;
        public static final ASN1ObjectIdentifier GENERATION;
        public static final ASN1ObjectIdentifier UNIQUE_IDENTIFIER;
        public static final ASN1ObjectIdentifier EmailAddress;
        public static final ASN1ObjectIdentifier E;
        public static final ASN1ObjectIdentifier DC;
        public static final ASN1ObjectIdentifier UID;
        public static HashMap DefaultSymbols;
        public HashMap values;
        
        public X509Name(final ASN1Sequence seq) {
            this.values = new HashMap();
            final Enumeration e = seq.getObjects();
            while (e.hasMoreElements()) {
                final ASN1Set set = e.nextElement();
                for (int i = 0; i < set.size(); ++i) {
                    final ASN1Sequence s = (ASN1Sequence)set.getObjectAt(i);
                    final String id = X509Name.DefaultSymbols.get(s.getObjectAt(0));
                    if (id != null) {
                        ArrayList vs = this.values.get(id);
                        if (vs == null) {
                            vs = new ArrayList();
                            this.values.put(id, vs);
                        }
                        vs.add(((ASN1String)s.getObjectAt(1)).getString());
                    }
                }
            }
        }
        
        public X509Name(final String dirName) {
            this.values = new HashMap();
            final X509NameTokenizer nTok = new X509NameTokenizer(dirName);
            while (nTok.hasMoreTokens()) {
                final String token = nTok.nextToken();
                final int index = token.indexOf(61);
                if (index == -1) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("badly.formated.directory.string"));
                }
                final String id = token.substring(0, index).toUpperCase();
                final String value = token.substring(index + 1);
                ArrayList vs = this.values.get(id);
                if (vs == null) {
                    vs = new ArrayList();
                    this.values.put(id, vs);
                }
                vs.add(value);
            }
        }
        
        public String getField(final String name) {
            final ArrayList vs = this.values.get(name);
            return (vs == null) ? null : vs.get(0);
        }
        
        public ArrayList getFieldArray(final String name) {
            final ArrayList vs = this.values.get(name);
            return vs;
        }
        
        public HashMap getFields() {
            return this.values;
        }
        
        @Override
        public String toString() {
            return this.values.toString();
        }
        
        static {
            C = new ASN1ObjectIdentifier("2.5.4.6");
            O = new ASN1ObjectIdentifier("2.5.4.10");
            OU = new ASN1ObjectIdentifier("2.5.4.11");
            T = new ASN1ObjectIdentifier("2.5.4.12");
            CN = new ASN1ObjectIdentifier("2.5.4.3");
            SN = new ASN1ObjectIdentifier("2.5.4.5");
            L = new ASN1ObjectIdentifier("2.5.4.7");
            ST = new ASN1ObjectIdentifier("2.5.4.8");
            SURNAME = new ASN1ObjectIdentifier("2.5.4.4");
            GIVENNAME = new ASN1ObjectIdentifier("2.5.4.42");
            INITIALS = new ASN1ObjectIdentifier("2.5.4.43");
            GENERATION = new ASN1ObjectIdentifier("2.5.4.44");
            UNIQUE_IDENTIFIER = new ASN1ObjectIdentifier("2.5.4.45");
            EmailAddress = new ASN1ObjectIdentifier("1.2.840.113549.1.9.1");
            E = X509Name.EmailAddress;
            DC = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");
            UID = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");
            (X509Name.DefaultSymbols = new HashMap()).put(X509Name.C, "C");
            X509Name.DefaultSymbols.put(X509Name.O, "O");
            X509Name.DefaultSymbols.put(X509Name.T, "T");
            X509Name.DefaultSymbols.put(X509Name.OU, "OU");
            X509Name.DefaultSymbols.put(X509Name.CN, "CN");
            X509Name.DefaultSymbols.put(X509Name.L, "L");
            X509Name.DefaultSymbols.put(X509Name.ST, "ST");
            X509Name.DefaultSymbols.put(X509Name.SN, "SN");
            X509Name.DefaultSymbols.put(X509Name.EmailAddress, "E");
            X509Name.DefaultSymbols.put(X509Name.DC, "DC");
            X509Name.DefaultSymbols.put(X509Name.UID, "UID");
            X509Name.DefaultSymbols.put(X509Name.SURNAME, "SURNAME");
            X509Name.DefaultSymbols.put(X509Name.GIVENNAME, "GIVENNAME");
            X509Name.DefaultSymbols.put(X509Name.INITIALS, "INITIALS");
            X509Name.DefaultSymbols.put(X509Name.GENERATION, "GENERATION");
        }
    }
    
    public static class X509NameTokenizer
    {
        private final String oid;
        private int index;
        private final StringBuffer buf;
        
        public X509NameTokenizer(final String oid) {
            this.buf = new StringBuffer();
            this.oid = oid;
            this.index = -1;
        }
        
        public boolean hasMoreTokens() {
            return this.index != this.oid.length();
        }
        
        public String nextToken() {
            if (this.index == this.oid.length()) {
                return null;
            }
            int end = this.index + 1;
            boolean quoted = false;
            boolean escaped = false;
            this.buf.setLength(0);
            while (end != this.oid.length()) {
                final char c = this.oid.charAt(end);
                if (c == '\"') {
                    if (!escaped) {
                        quoted = !quoted;
                    }
                    else {
                        this.buf.append(c);
                    }
                    escaped = false;
                }
                else if (escaped || quoted) {
                    this.buf.append(c);
                    escaped = false;
                }
                else if (c == '\\') {
                    escaped = true;
                }
                else {
                    if (c == ',') {
                        break;
                    }
                    this.buf.append(c);
                }
                ++end;
            }
            this.index = end;
            return this.buf.toString().trim();
        }
    }
}
