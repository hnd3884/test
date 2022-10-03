package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.util.Iterator;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.Strings;
import java.util.Collections;
import java.security.cert.X509CRLEntry;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TBSCertList;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.Principal;
import java.security.Provider;
import java.security.SignatureException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.PublicKey;
import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.HashSet;
import java.util.Set;
import java.security.cert.CRLException;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.cert.X509CRL;

class X509CRLObject extends X509CRL
{
    private JcaJceHelper bcHelper;
    private CertificateList c;
    private String sigAlgName;
    private byte[] sigAlgParams;
    private boolean isIndirect;
    private boolean isHashCodeSet;
    private int hashCodeValue;
    
    static boolean isIndirectCRL(final X509CRL x509CRL) throws CRLException {
        try {
            final byte[] extensionValue = x509CRL.getExtensionValue(Extension.issuingDistributionPoint.getId());
            return extensionValue != null && IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets()).isIndirectCRL();
        }
        catch (final Exception ex) {
            throw new ExtCRLException("Exception reading IssuingDistributionPoint", ex);
        }
    }
    
    protected X509CRLObject(final JcaJceHelper bcHelper, final CertificateList c) throws CRLException {
        this.isHashCodeSet = false;
        this.bcHelper = bcHelper;
        this.c = c;
        try {
            this.sigAlgName = X509SignatureUtil.getSignatureName(c.getSignatureAlgorithm());
            if (c.getSignatureAlgorithm().getParameters() != null) {
                this.sigAlgParams = c.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded("DER");
            }
            else {
                this.sigAlgParams = null;
            }
            this.isIndirect = isIndirectCRL(this);
        }
        catch (final Exception ex) {
            throw new CRLException("CRL contents invalid: " + ex);
        }
    }
    
    public boolean hasUnsupportedCriticalExtension() {
        final Set criticalExtensionOIDs = this.getCriticalExtensionOIDs();
        if (criticalExtensionOIDs == null) {
            return false;
        }
        criticalExtensionOIDs.remove(Extension.issuingDistributionPoint.getId());
        criticalExtensionOIDs.remove(Extension.deltaCRLIndicator.getId());
        return !criticalExtensionOIDs.isEmpty();
    }
    
    private Set getExtensionOIDs(final boolean b) {
        if (this.getVersion() == 2) {
            final Extensions extensions = this.c.getTBSCertList().getExtensions();
            if (extensions != null) {
                final HashSet set = new HashSet();
                final Enumeration oids = extensions.oids();
                while (oids.hasMoreElements()) {
                    final ASN1ObjectIdentifier asn1ObjectIdentifier = oids.nextElement();
                    if (b == extensions.getExtension(asn1ObjectIdentifier).isCritical()) {
                        set.add(asn1ObjectIdentifier.getId());
                    }
                }
                return set;
            }
        }
        return null;
    }
    
    public Set getCriticalExtensionOIDs() {
        return this.getExtensionOIDs(true);
    }
    
    public Set getNonCriticalExtensionOIDs() {
        return this.getExtensionOIDs(false);
    }
    
    public byte[] getExtensionValue(final String s) {
        final Extensions extensions = this.c.getTBSCertList().getExtensions();
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
    
    @Override
    public byte[] getEncoded() throws CRLException {
        try {
            return this.c.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new CRLException(ex.toString());
        }
    }
    
    @Override
    public void verify(final PublicKey publicKey) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature;
        try {
            signature = this.bcHelper.createSignature(this.getSigAlgName());
        }
        catch (final Exception ex) {
            signature = Signature.getInstance(this.getSigAlgName());
        }
        this.doVerify(publicKey, signature);
    }
    
    @Override
    public void verify(final PublicKey publicKey, final String s) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature;
        if (s != null) {
            signature = Signature.getInstance(this.getSigAlgName(), s);
        }
        else {
            signature = Signature.getInstance(this.getSigAlgName());
        }
        this.doVerify(publicKey, signature);
    }
    
    @Override
    public void verify(final PublicKey publicKey, final Provider provider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature;
        if (provider != null) {
            signature = Signature.getInstance(this.getSigAlgName(), provider);
        }
        else {
            signature = Signature.getInstance(this.getSigAlgName());
        }
        this.doVerify(publicKey, signature);
    }
    
    private void doVerify(final PublicKey publicKey, final Signature signature) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
        }
        signature.initVerify(publicKey);
        signature.update(this.getTBSCertList());
        if (!signature.verify(this.getSignature())) {
            throw new SignatureException("CRL does not verify with supplied public key.");
        }
    }
    
    @Override
    public int getVersion() {
        return this.c.getVersionNumber();
    }
    
    @Override
    public Principal getIssuerDN() {
        return new X509Principal(X500Name.getInstance(this.c.getIssuer().toASN1Primitive()));
    }
    
    @Override
    public X500Principal getIssuerX500Principal() {
        try {
            return new X500Principal(this.c.getIssuer().getEncoded());
        }
        catch (final IOException ex) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }
    
    @Override
    public Date getThisUpdate() {
        return this.c.getThisUpdate().getDate();
    }
    
    @Override
    public Date getNextUpdate() {
        if (this.c.getNextUpdate() != null) {
            return this.c.getNextUpdate().getDate();
        }
        return null;
    }
    
    private Set loadCRLEntries() {
        final HashSet set = new HashSet();
        final Enumeration revokedCertificateEnumeration = this.c.getRevokedCertificateEnumeration();
        X500Name instance = null;
        while (revokedCertificateEnumeration.hasMoreElements()) {
            final TBSCertList.CRLEntry crlEntry = revokedCertificateEnumeration.nextElement();
            set.add(new X509CRLEntryObject(crlEntry, this.isIndirect, instance));
            if (this.isIndirect && crlEntry.hasExtensions()) {
                final Extension extension = crlEntry.getExtensions().getExtension(Extension.certificateIssuer);
                if (extension == null) {
                    continue;
                }
                instance = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName());
            }
        }
        return set;
    }
    
    @Override
    public X509CRLEntry getRevokedCertificate(final BigInteger bigInteger) {
        final Enumeration revokedCertificateEnumeration = this.c.getRevokedCertificateEnumeration();
        X500Name instance = null;
        while (revokedCertificateEnumeration.hasMoreElements()) {
            final TBSCertList.CRLEntry crlEntry = revokedCertificateEnumeration.nextElement();
            if (bigInteger.equals(crlEntry.getUserCertificate().getValue())) {
                return new X509CRLEntryObject(crlEntry, this.isIndirect, instance);
            }
            if (!this.isIndirect || !crlEntry.hasExtensions()) {
                continue;
            }
            final Extension extension = crlEntry.getExtensions().getExtension(Extension.certificateIssuer);
            if (extension == null) {
                continue;
            }
            instance = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName());
        }
        return null;
    }
    
    @Override
    public Set getRevokedCertificates() {
        final Set loadCRLEntries = this.loadCRLEntries();
        if (!loadCRLEntries.isEmpty()) {
            return Collections.unmodifiableSet((Set<?>)loadCRLEntries);
        }
        return null;
    }
    
    @Override
    public byte[] getTBSCertList() throws CRLException {
        try {
            return this.c.getTBSCertList().getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new CRLException(ex.toString());
        }
    }
    
    @Override
    public byte[] getSignature() {
        return this.c.getSignature().getOctets();
    }
    
    @Override
    public String getSigAlgName() {
        return this.sigAlgName;
    }
    
    @Override
    public String getSigAlgOID() {
        return this.c.getSignatureAlgorithm().getAlgorithm().getId();
    }
    
    @Override
    public byte[] getSigAlgParams() {
        if (this.sigAlgParams != null) {
            final byte[] array = new byte[this.sigAlgParams.length];
            System.arraycopy(this.sigAlgParams, 0, array, 0, array.length);
            return array;
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append("              Version: ").append(this.getVersion()).append(lineSeparator);
        sb.append("             IssuerDN: ").append(this.getIssuerDN()).append(lineSeparator);
        sb.append("          This update: ").append(this.getThisUpdate()).append(lineSeparator);
        sb.append("          Next update: ").append(this.getNextUpdate()).append(lineSeparator);
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
        final Extensions extensions = this.c.getTBSCertList().getExtensions();
        if (extensions != null) {
            final Enumeration oids = extensions.oids();
            if (oids.hasMoreElements()) {
                sb.append("           Extensions: ").append(lineSeparator);
            }
            while (oids.hasMoreElements()) {
                final ASN1ObjectIdentifier asn1ObjectIdentifier = oids.nextElement();
                final Extension extension = extensions.getExtension(asn1ObjectIdentifier);
                if (extension.getExtnValue() != null) {
                    final ASN1InputStream asn1InputStream = new ASN1InputStream(extension.getExtnValue().getOctets());
                    sb.append("                       critical(").append(extension.isCritical()).append(") ");
                    try {
                        if (asn1ObjectIdentifier.equals(Extension.cRLNumber)) {
                            sb.append(new CRLNumber(ASN1Integer.getInstance(asn1InputStream.readObject()).getPositiveValue())).append(lineSeparator);
                        }
                        else if (asn1ObjectIdentifier.equals(Extension.deltaCRLIndicator)) {
                            sb.append("Base CRL: " + new CRLNumber(ASN1Integer.getInstance(asn1InputStream.readObject()).getPositiveValue())).append(lineSeparator);
                        }
                        else if (asn1ObjectIdentifier.equals(Extension.issuingDistributionPoint)) {
                            sb.append(IssuingDistributionPoint.getInstance(asn1InputStream.readObject())).append(lineSeparator);
                        }
                        else if (asn1ObjectIdentifier.equals(Extension.cRLDistributionPoints)) {
                            sb.append(CRLDistPoint.getInstance(asn1InputStream.readObject())).append(lineSeparator);
                        }
                        else if (asn1ObjectIdentifier.equals(Extension.freshestCRL)) {
                            sb.append(CRLDistPoint.getInstance(asn1InputStream.readObject())).append(lineSeparator);
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
        final Set revokedCertificates = this.getRevokedCertificates();
        if (revokedCertificates != null) {
            final Iterator iterator = revokedCertificates.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next());
                sb.append(lineSeparator);
            }
        }
        return sb.toString();
    }
    
    @Override
    public boolean isRevoked(final Certificate certificate) {
        if (!certificate.getType().equals("X.509")) {
            throw new IllegalArgumentException("X.509 CRL used with non X.509 Cert");
        }
        final Enumeration revokedCertificateEnumeration = this.c.getRevokedCertificateEnumeration();
        X500Name x500Name = this.c.getIssuer();
        if (revokedCertificateEnumeration.hasMoreElements()) {
            final BigInteger serialNumber = ((X509Certificate)certificate).getSerialNumber();
            while (revokedCertificateEnumeration.hasMoreElements()) {
                final TBSCertList.CRLEntry instance = TBSCertList.CRLEntry.getInstance(revokedCertificateEnumeration.nextElement());
                if (this.isIndirect && instance.hasExtensions()) {
                    final Extension extension = instance.getExtensions().getExtension(Extension.certificateIssuer);
                    if (extension != null) {
                        x500Name = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName());
                    }
                }
                if (instance.getUserCertificate().getValue().equals(serialNumber)) {
                    X500Name x500Name2;
                    if (certificate instanceof X509Certificate) {
                        x500Name2 = X500Name.getInstance(((X509Certificate)certificate).getIssuerX500Principal().getEncoded());
                    }
                    else {
                        try {
                            x500Name2 = org.bouncycastle.asn1.x509.Certificate.getInstance(certificate.getEncoded()).getIssuer();
                        }
                        catch (final CertificateEncodingException ex) {
                            throw new IllegalArgumentException("Cannot process certificate: " + ex.getMessage());
                        }
                    }
                    return x500Name.equals(x500Name2);
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof X509CRL)) {
            return false;
        }
        if (o instanceof X509CRLObject) {
            final X509CRLObject x509CRLObject = (X509CRLObject)o;
            return (!this.isHashCodeSet || !x509CRLObject.isHashCodeSet || x509CRLObject.hashCodeValue == this.hashCodeValue) && this.c.equals(x509CRLObject.c);
        }
        return super.equals(o);
    }
    
    @Override
    public int hashCode() {
        if (!this.isHashCodeSet) {
            this.isHashCodeSet = true;
            this.hashCodeValue = super.hashCode();
        }
        return this.hashCodeValue;
    }
}
