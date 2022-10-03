package org.bouncycastle.jce.provider;

import java.net.UnknownHostException;
import java.net.InetAddress;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.SignatureException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.Signature;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.misc.VerisignCzagExtension;
import org.bouncycastle.asn1.misc.NetscapeRevocationURL;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.Arrays;
import java.security.PublicKey;
import java.util.Enumeration;
import org.bouncycastle.asn1.x509.Extensions;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.x509.Extension;
import java.util.Collection;
import java.util.Collections;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import java.util.List;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.ASN1Encodable;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OutputStream;
import java.io.ByteArrayOutputStream;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.Principal;
import java.math.BigInteger;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.util.Date;
import org.bouncycastle.asn1.DERBitString;
import java.security.cert.CertificateParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import java.security.cert.X509Certificate;

public class X509CertificateObject extends X509Certificate implements PKCS12BagAttributeCarrier
{
    private org.bouncycastle.asn1.x509.Certificate c;
    private BasicConstraints basicConstraints;
    private boolean[] keyUsage;
    private boolean hashValueSet;
    private int hashValue;
    private PKCS12BagAttributeCarrier attrCarrier;
    
    public X509CertificateObject(final org.bouncycastle.asn1.x509.Certificate c) throws CertificateParsingException {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.c = c;
        try {
            final byte[] extensionBytes = this.getExtensionBytes("2.5.29.19");
            if (extensionBytes != null) {
                this.basicConstraints = BasicConstraints.getInstance(ASN1Primitive.fromByteArray(extensionBytes));
            }
        }
        catch (final Exception ex) {
            throw new CertificateParsingException("cannot construct BasicConstraints: " + ex);
        }
        try {
            final byte[] extensionBytes2 = this.getExtensionBytes("2.5.29.15");
            if (extensionBytes2 != null) {
                final DERBitString instance = DERBitString.getInstance(ASN1Primitive.fromByteArray(extensionBytes2));
                final byte[] bytes = instance.getBytes();
                final int n = bytes.length * 8 - instance.getPadBits();
                this.keyUsage = new boolean[(n < 9) ? 9 : n];
                for (int i = 0; i != n; ++i) {
                    this.keyUsage[i] = ((bytes[i / 8] & 128 >>> i % 8) != 0x0);
                }
            }
            else {
                this.keyUsage = null;
            }
        }
        catch (final Exception ex2) {
            throw new CertificateParsingException("cannot construct KeyUsage: " + ex2);
        }
    }
    
    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        this.checkValidity(new Date());
    }
    
    @Override
    public void checkValidity(final Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        if (date.getTime() > this.getNotAfter().getTime()) {
            throw new CertificateExpiredException("certificate expired on " + this.c.getEndDate().getTime());
        }
        if (date.getTime() < this.getNotBefore().getTime()) {
            throw new CertificateNotYetValidException("certificate not valid till " + this.c.getStartDate().getTime());
        }
    }
    
    @Override
    public int getVersion() {
        return this.c.getVersionNumber();
    }
    
    @Override
    public BigInteger getSerialNumber() {
        return this.c.getSerialNumber().getValue();
    }
    
    @Override
    public Principal getIssuerDN() {
        try {
            return new X509Principal(X500Name.getInstance(this.c.getIssuer().getEncoded()));
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    @Override
    public X500Principal getIssuerX500Principal() {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new ASN1OutputStream(byteArrayOutputStream).writeObject(this.c.getIssuer());
            return new X500Principal(byteArrayOutputStream.toByteArray());
        }
        catch (final IOException ex) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }
    
    @Override
    public Principal getSubjectDN() {
        return new X509Principal(X500Name.getInstance(this.c.getSubject().toASN1Primitive()));
    }
    
    @Override
    public X500Principal getSubjectX500Principal() {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new ASN1OutputStream(byteArrayOutputStream).writeObject(this.c.getSubject());
            return new X500Principal(byteArrayOutputStream.toByteArray());
        }
        catch (final IOException ex) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }
    
    @Override
    public Date getNotBefore() {
        return this.c.getStartDate().getDate();
    }
    
    @Override
    public Date getNotAfter() {
        return this.c.getEndDate().getDate();
    }
    
    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        try {
            return this.c.getTBSCertificate().getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException(ex.toString());
        }
    }
    
    @Override
    public byte[] getSignature() {
        return this.c.getSignature().getOctets();
    }
    
    @Override
    public String getSigAlgName() {
        final Provider provider = Security.getProvider("BC");
        if (provider != null) {
            final String property = provider.getProperty("Alg.Alias.Signature." + this.getSigAlgOID());
            if (property != null) {
                return property;
            }
        }
        final Provider[] providers = Security.getProviders();
        for (int i = 0; i != providers.length; ++i) {
            final String property2 = providers[i].getProperty("Alg.Alias.Signature." + this.getSigAlgOID());
            if (property2 != null) {
                return property2;
            }
        }
        return this.getSigAlgOID();
    }
    
    @Override
    public String getSigAlgOID() {
        return this.c.getSignatureAlgorithm().getAlgorithm().getId();
    }
    
    @Override
    public byte[] getSigAlgParams() {
        if (this.c.getSignatureAlgorithm().getParameters() != null) {
            try {
                return this.c.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded("DER");
            }
            catch (final IOException ex) {
                return null;
            }
        }
        return null;
    }
    
    @Override
    public boolean[] getIssuerUniqueID() {
        final DERBitString issuerUniqueId = this.c.getTBSCertificate().getIssuerUniqueId();
        if (issuerUniqueId != null) {
            final byte[] bytes = issuerUniqueId.getBytes();
            final boolean[] array = new boolean[bytes.length * 8 - issuerUniqueId.getPadBits()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = ((bytes[i / 8] & 128 >>> i % 8) != 0x0);
            }
            return array;
        }
        return null;
    }
    
    @Override
    public boolean[] getSubjectUniqueID() {
        final DERBitString subjectUniqueId = this.c.getTBSCertificate().getSubjectUniqueId();
        if (subjectUniqueId != null) {
            final byte[] bytes = subjectUniqueId.getBytes();
            final boolean[] array = new boolean[bytes.length * 8 - subjectUniqueId.getPadBits()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = ((bytes[i / 8] & 128 >>> i % 8) != 0x0);
            }
            return array;
        }
        return null;
    }
    
    @Override
    public boolean[] getKeyUsage() {
        return this.keyUsage;
    }
    
    @Override
    public List getExtendedKeyUsage() throws CertificateParsingException {
        final byte[] extensionBytes = this.getExtensionBytes("2.5.29.37");
        if (extensionBytes != null) {
            try {
                final ASN1Sequence asn1Sequence = (ASN1Sequence)new ASN1InputStream(extensionBytes).readObject();
                final ArrayList list = new ArrayList();
                for (int i = 0; i != asn1Sequence.size(); ++i) {
                    list.add(((ASN1ObjectIdentifier)asn1Sequence.getObjectAt(i)).getId());
                }
                return Collections.unmodifiableList((List<?>)list);
            }
            catch (final Exception ex) {
                throw new CertificateParsingException("error processing extended key usage extension");
            }
        }
        return null;
    }
    
    @Override
    public int getBasicConstraints() {
        if (this.basicConstraints == null) {
            return -1;
        }
        if (!this.basicConstraints.isCA()) {
            return -1;
        }
        if (this.basicConstraints.getPathLenConstraint() == null) {
            return Integer.MAX_VALUE;
        }
        return this.basicConstraints.getPathLenConstraint().intValue();
    }
    
    @Override
    public Collection getSubjectAlternativeNames() throws CertificateParsingException {
        return getAlternativeNames(this.getExtensionBytes(Extension.subjectAlternativeName.getId()));
    }
    
    @Override
    public Collection getIssuerAlternativeNames() throws CertificateParsingException {
        return getAlternativeNames(this.getExtensionBytes(Extension.issuerAlternativeName.getId()));
    }
    
    public Set getCriticalExtensionOIDs() {
        if (this.getVersion() == 3) {
            final HashSet set = new HashSet();
            final Extensions extensions = this.c.getTBSCertificate().getExtensions();
            if (extensions != null) {
                final Enumeration oids = extensions.oids();
                while (oids.hasMoreElements()) {
                    final ASN1ObjectIdentifier asn1ObjectIdentifier = oids.nextElement();
                    if (extensions.getExtension(asn1ObjectIdentifier).isCritical()) {
                        set.add(asn1ObjectIdentifier.getId());
                    }
                }
                return set;
            }
        }
        return null;
    }
    
    private byte[] getExtensionBytes(final String s) {
        final Extensions extensions = this.c.getTBSCertificate().getExtensions();
        if (extensions != null) {
            final Extension extension = extensions.getExtension(new ASN1ObjectIdentifier(s));
            if (extension != null) {
                return extension.getExtnValue().getOctets();
            }
        }
        return null;
    }
    
    public byte[] getExtensionValue(final String s) {
        final Extensions extensions = this.c.getTBSCertificate().getExtensions();
        if (extensions != null) {
            final Extension extension = extensions.getExtension(new ASN1ObjectIdentifier(s));
            if (extension != null) {
                try {
                    return extension.getExtnValue().getEncoded();
                }
                catch (final Exception ex) {
                    throw new IllegalStateException("error parsing " + ex.toString());
                }
            }
        }
        return null;
    }
    
    public Set getNonCriticalExtensionOIDs() {
        if (this.getVersion() == 3) {
            final HashSet set = new HashSet();
            final Extensions extensions = this.c.getTBSCertificate().getExtensions();
            if (extensions != null) {
                final Enumeration oids = extensions.oids();
                while (oids.hasMoreElements()) {
                    final ASN1ObjectIdentifier asn1ObjectIdentifier = oids.nextElement();
                    if (!extensions.getExtension(asn1ObjectIdentifier).isCritical()) {
                        set.add(asn1ObjectIdentifier.getId());
                    }
                }
                return set;
            }
        }
        return null;
    }
    
    public boolean hasUnsupportedCriticalExtension() {
        if (this.getVersion() == 3) {
            final Extensions extensions = this.c.getTBSCertificate().getExtensions();
            if (extensions != null) {
                final Enumeration oids = extensions.oids();
                while (oids.hasMoreElements()) {
                    final ASN1ObjectIdentifier asn1ObjectIdentifier = oids.nextElement();
                    final String id = asn1ObjectIdentifier.getId();
                    if (!id.equals(RFC3280CertPathUtilities.KEY_USAGE) && !id.equals(RFC3280CertPathUtilities.CERTIFICATE_POLICIES) && !id.equals(RFC3280CertPathUtilities.POLICY_MAPPINGS) && !id.equals(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY) && !id.equals(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS) && !id.equals(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT) && !id.equals(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR) && !id.equals(RFC3280CertPathUtilities.POLICY_CONSTRAINTS) && !id.equals(RFC3280CertPathUtilities.BASIC_CONSTRAINTS) && !id.equals(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME)) {
                        if (id.equals(RFC3280CertPathUtilities.NAME_CONSTRAINTS)) {
                            continue;
                        }
                        if (extensions.getExtension(asn1ObjectIdentifier).isCritical()) {
                            return true;
                        }
                        continue;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public PublicKey getPublicKey() {
        try {
            return BouncyCastleProvider.getPublicKey(this.c.getSubjectPublicKeyInfo());
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        try {
            return this.c.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException(ex.toString());
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Certificate)) {
            return false;
        }
        final Certificate certificate = (Certificate)o;
        try {
            return Arrays.areEqual(this.getEncoded(), certificate.getEncoded());
        }
        catch (final CertificateEncodingException ex) {
            return false;
        }
    }
    
    @Override
    public synchronized int hashCode() {
        if (!this.hashValueSet) {
            this.hashValue = this.calculateHashCode();
            this.hashValueSet = true;
        }
        return this.hashValue;
    }
    
    private int calculateHashCode() {
        try {
            int n = 0;
            final byte[] encoded = this.getEncoded();
            for (byte b = 1; b < encoded.length; ++b) {
                n += encoded[b] * b;
            }
            return n;
        }
        catch (final CertificateEncodingException ex) {
            return 0;
        }
    }
    
    public void setBagAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        this.attrCarrier.setBagAttribute(asn1ObjectIdentifier, asn1Encodable);
    }
    
    public ASN1Encodable getBagAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return this.attrCarrier.getBagAttribute(asn1ObjectIdentifier);
    }
    
    public Enumeration getBagAttributeKeys() {
        return this.attrCarrier.getBagAttributeKeys();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append("  [0]         Version: ").append(this.getVersion()).append(lineSeparator);
        sb.append("         SerialNumber: ").append(this.getSerialNumber()).append(lineSeparator);
        sb.append("             IssuerDN: ").append(this.getIssuerDN()).append(lineSeparator);
        sb.append("           Start Date: ").append(this.getNotBefore()).append(lineSeparator);
        sb.append("           Final Date: ").append(this.getNotAfter()).append(lineSeparator);
        sb.append("            SubjectDN: ").append(this.getSubjectDN()).append(lineSeparator);
        sb.append("           Public Key: ").append(this.getPublicKey()).append(lineSeparator);
        sb.append("  Signature Algorithm: ").append(this.getSigAlgName()).append(lineSeparator);
        final byte[] signature = this.getSignature();
        sb.append("            Signature: ").append(new String(Hex.encode(signature, 0, 20))).append(lineSeparator);
        for (int i = 20; i < signature.length; i += 20) {
            if (i < signature.length - 20) {
                sb.append("                       ").append(new String(Hex.encode(signature, i, 20))).append(lineSeparator);
            }
            else {
                sb.append("                       ").append(new String(Hex.encode(signature, i, signature.length - i))).append(lineSeparator);
            }
        }
        final Extensions extensions = this.c.getTBSCertificate().getExtensions();
        if (extensions != null) {
            final Enumeration oids = extensions.oids();
            if (oids.hasMoreElements()) {
                sb.append("       Extensions: \n");
            }
            while (oids.hasMoreElements()) {
                final ASN1ObjectIdentifier asn1ObjectIdentifier = oids.nextElement();
                final Extension extension = extensions.getExtension(asn1ObjectIdentifier);
                if (extension.getExtnValue() != null) {
                    final ASN1InputStream asn1InputStream = new ASN1InputStream(extension.getExtnValue().getOctets());
                    sb.append("                       critical(").append(extension.isCritical()).append(") ");
                    try {
                        if (asn1ObjectIdentifier.equals(Extension.basicConstraints)) {
                            sb.append(BasicConstraints.getInstance(asn1InputStream.readObject())).append(lineSeparator);
                        }
                        else if (asn1ObjectIdentifier.equals(Extension.keyUsage)) {
                            sb.append(KeyUsage.getInstance(asn1InputStream.readObject())).append(lineSeparator);
                        }
                        else if (asn1ObjectIdentifier.equals(MiscObjectIdentifiers.netscapeCertType)) {
                            sb.append(new NetscapeCertType((DERBitString)asn1InputStream.readObject())).append(lineSeparator);
                        }
                        else if (asn1ObjectIdentifier.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
                            sb.append(new NetscapeRevocationURL((DERIA5String)asn1InputStream.readObject())).append(lineSeparator);
                        }
                        else if (asn1ObjectIdentifier.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
                            sb.append(new VerisignCzagExtension((DERIA5String)asn1InputStream.readObject())).append(lineSeparator);
                        }
                        else {
                            sb.append(asn1ObjectIdentifier.getId());
                            sb.append(" value = ").append(ASN1Dump.dumpAsString(asn1InputStream.readObject())).append(lineSeparator);
                        }
                    }
                    catch (final Exception ex) {
                        sb.append(asn1ObjectIdentifier.getId());
                        sb.append(" value = ").append("*****").append(lineSeparator);
                    }
                }
                else {
                    sb.append(lineSeparator);
                }
            }
        }
        return sb.toString();
    }
    
    @Override
    public final void verify(final PublicKey publicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        final String signatureName = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
        Signature signature;
        try {
            signature = Signature.getInstance(signatureName, "BC");
        }
        catch (final Exception ex) {
            signature = Signature.getInstance(signatureName);
        }
        this.checkSignature(publicKey, signature);
    }
    
    @Override
    public final void verify(final PublicKey publicKey, final String s) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        final String signatureName = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
        Signature signature;
        if (s != null) {
            signature = Signature.getInstance(signatureName, s);
        }
        else {
            signature = Signature.getInstance(signatureName);
        }
        this.checkSignature(publicKey, signature);
    }
    
    @Override
    public final void verify(final PublicKey publicKey, final Provider provider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        final String signatureName = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
        Signature signature;
        if (provider != null) {
            signature = Signature.getInstance(signatureName, provider);
        }
        else {
            signature = Signature.getInstance(signatureName);
        }
        this.checkSignature(publicKey, signature);
    }
    
    private void checkSignature(final PublicKey publicKey, final Signature signature) throws CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (!this.isAlgIdEqual(this.c.getSignatureAlgorithm(), this.c.getTBSCertificate().getSignature())) {
            throw new CertificateException("signature algorithm in TBS cert not same as outer cert");
        }
        X509SignatureUtil.setSignatureParameters(signature, this.c.getSignatureAlgorithm().getParameters());
        signature.initVerify(publicKey);
        signature.update(this.getTBSCertificate());
        if (!signature.verify(this.getSignature())) {
            throw new SignatureException("certificate does not verify with supplied key");
        }
    }
    
    private boolean isAlgIdEqual(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2) {
        if (!algorithmIdentifier.getAlgorithm().equals(algorithmIdentifier2.getAlgorithm())) {
            return false;
        }
        if (algorithmIdentifier.getParameters() == null) {
            return algorithmIdentifier2.getParameters() == null || algorithmIdentifier2.getParameters().equals(DERNull.INSTANCE);
        }
        if (algorithmIdentifier2.getParameters() == null) {
            return algorithmIdentifier.getParameters() == null || algorithmIdentifier.getParameters().equals(DERNull.INSTANCE);
        }
        return algorithmIdentifier.getParameters().equals(algorithmIdentifier2.getParameters());
    }
    
    private static Collection getAlternativeNames(final byte[] array) throws CertificateParsingException {
        if (array == null) {
            return null;
        }
        try {
            final ArrayList list = new ArrayList();
            final Enumeration objects = ASN1Sequence.getInstance(array).getObjects();
            while (objects.hasMoreElements()) {
                final GeneralName instance = GeneralName.getInstance(objects.nextElement());
                final ArrayList list2 = new ArrayList();
                list2.add(Integers.valueOf(instance.getTagNo()));
                switch (instance.getTagNo()) {
                    case 0:
                    case 3:
                    case 5: {
                        list2.add(instance.getEncoded());
                        break;
                    }
                    case 4: {
                        list2.add(X500Name.getInstance(RFC4519Style.INSTANCE, instance.getName()).toString());
                        break;
                    }
                    case 1:
                    case 2:
                    case 6: {
                        list2.add(((ASN1String)instance.getName()).getString());
                        break;
                    }
                    case 8: {
                        list2.add(ASN1ObjectIdentifier.getInstance(instance.getName()).getId());
                        break;
                    }
                    case 7: {
                        final byte[] octets = ASN1OctetString.getInstance(instance.getName()).getOctets();
                        String hostAddress;
                        try {
                            hostAddress = InetAddress.getByAddress(octets).getHostAddress();
                        }
                        catch (final UnknownHostException ex) {
                            continue;
                        }
                        list2.add(hostAddress);
                        break;
                    }
                    default: {
                        throw new IOException("Bad tag number: " + instance.getTagNo());
                    }
                }
                list.add(Collections.unmodifiableList((List<?>)list2));
            }
            if (list.size() == 0) {
                return null;
            }
            return Collections.unmodifiableCollection((Collection<?>)list);
        }
        catch (final Exception ex2) {
            throw new CertificateParsingException(ex2.getMessage());
        }
    }
}
