package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.ASN1Dump;
import com.maverick.crypto.asn1.misc.VerisignCzagExtension;
import com.maverick.crypto.asn1.misc.NetscapeRevocationURL;
import com.maverick.crypto.asn1.DERIA5String;
import com.maverick.crypto.asn1.misc.NetscapeCertType;
import com.maverick.crypto.asn1.misc.MiscObjectIdentifiers;
import com.maverick.crypto.encoders.Hex;
import com.maverick.crypto.asn1.DEREncodable;
import com.maverick.crypto.publickey.RsaPublicKey;
import com.maverick.crypto.asn1.pkcs.PKCSObjectIdentifiers;
import com.maverick.crypto.publickey.PublicKey;
import java.util.Enumeration;
import com.maverick.crypto.asn1.DERObjectIdentifier;
import com.maverick.crypto.asn1.DERInteger;
import com.maverick.crypto.asn1.DERBoolean;
import com.maverick.crypto.asn1.ASN1Sequence;
import java.io.InputStream;
import com.maverick.crypto.asn1.DERInputStream;
import java.io.ByteArrayInputStream;
import com.maverick.crypto.asn1.DERBitString;
import java.io.IOException;
import java.io.OutputStream;
import com.maverick.crypto.asn1.DEROutputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;

public class X509Certificate
{
    private X509CertificateStructure d;
    private Hashtable c;
    private Vector b;
    
    public X509Certificate(final X509CertificateStructure d) {
        this.c = new Hashtable();
        this.b = new Vector();
        this.d = d;
    }
    
    public void checkValidity() throws CertificateException {
        this.checkValidity(new Date());
    }
    
    public void checkValidity(final Date date) throws CertificateException {
        if (date.after(this.getNotAfter())) {
            throw new CertificateException(1, "Certificate expired on " + this.d.getEndDate().getTime());
        }
        if (date.before(this.getNotBefore())) {
            throw new CertificateException(2, "certificate not valid till " + this.d.getStartDate().getTime());
        }
    }
    
    public int getVersion() {
        return this.d.getVersion();
    }
    
    public BigInteger getSerialNumber() {
        return this.d.getSerialNumber().getValue();
    }
    
    public X509Name getIssuerDN() {
        return this.d.getIssuer();
    }
    
    public X509Name getSubjectDN() {
        return this.d.getSubject();
    }
    
    public Date getNotBefore() {
        return this.d.getStartDate().getDate();
    }
    
    public Date getNotAfter() {
        return this.d.getEndDate().getDate();
    }
    
    public byte[] getTBSCertificate() throws CertificateException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DEROutputStream derOutputStream = new DEROutputStream(byteArrayOutputStream);
        try {
            derOutputStream.writeObject(this.d.getTBSCertificate());
            return byteArrayOutputStream.toByteArray();
        }
        catch (final IOException ex) {
            throw new CertificateException(3, ex.toString());
        }
    }
    
    public byte[] getSignature() {
        return this.d.getSignature().getBytes();
    }
    
    public String getSigAlgOID() {
        return this.d.getSignatureAlgorithm().getObjectId().getId();
    }
    
    public String getSigAlgName() throws CertificateException {
        if (this.getSigAlgOID().equals("1.2.840.113549.1.1.4")) {
            return "MD5WithRSAEncryption";
        }
        if (this.getSigAlgOID().equals("1.2.840.113549.1.1.5")) {
            return "SHA1WithRSAEncryption";
        }
        throw new CertificateException(5, "Unsupported signature algorithm id " + this.getSigAlgOID());
    }
    
    public byte[] getSigAlgParams() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (this.d.getSignatureAlgorithm().getParameters() != null) {
            try {
                new DEROutputStream(byteArrayOutputStream).writeObject(this.d.getSignatureAlgorithm().getParameters());
            }
            catch (final Exception ex) {
                throw new RuntimeException("exception getting sig parameters " + ex);
            }
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }
    
    public boolean[] getIssuerUniqueID() {
        final DERBitString issuerUniqueId = this.d.getTBSCertificate().getIssuerUniqueId();
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
    
    public boolean[] getSubjectUniqueID() {
        final DERBitString subjectUniqueId = this.d.getTBSCertificate().getSubjectUniqueId();
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
    
    public boolean[] getKeyUsage() {
        final byte[] b = this.b("2.5.29.15");
        if (b != null) {
            byte[] bytes;
            int n;
            try {
                final DERBitString derBitString = (DERBitString)new DERInputStream(new ByteArrayInputStream(b)).readObject();
                bytes = derBitString.getBytes();
                n = bytes.length * 8 - derBitString.getPadBits();
            }
            catch (final Exception ex) {
                throw new RuntimeException("error processing key usage extension");
            }
            final boolean[] array = new boolean[(n < 9) ? 9 : n];
            for (int i = 0; i != n; ++i) {
                array[i] = ((bytes[i / 8] & 128 >>> i % 8) != 0x0);
            }
            return array;
        }
        return null;
    }
    
    public int getBasicConstraints() {
        final byte[] b = this.b("2.5.29.19");
        if (b != null) {
            try {
                final ASN1Sequence asn1Sequence = (ASN1Sequence)new DERInputStream(new ByteArrayInputStream(b)).readObject();
                if (asn1Sequence.size() == 2) {
                    if (((DERBoolean)asn1Sequence.getObjectAt(0)).isTrue()) {
                        return ((DERInteger)asn1Sequence.getObjectAt(1)).getValue().intValue();
                    }
                    return -1;
                }
                else if (asn1Sequence.size() == 1) {
                    if (!(asn1Sequence.getObjectAt(0) instanceof DERBoolean)) {
                        return -1;
                    }
                    if (((DERBoolean)asn1Sequence.getObjectAt(0)).isTrue()) {
                        return Integer.MAX_VALUE;
                    }
                    return -1;
                }
            }
            catch (final Exception ex) {
                throw new RuntimeException("error processing key usage extension");
            }
        }
        return -1;
    }
    
    public X509Extension[] getCriticalExtensionOIDs() {
        if (this.getVersion() == 3) {
            final Vector vector = new Vector();
            final X509Extensions extensions = this.d.getTBSCertificate().getExtensions();
            if (extensions != null) {
                final Enumeration oids = extensions.oids();
                while (oids.hasMoreElements()) {
                    final DERObjectIdentifier derObjectIdentifier = oids.nextElement();
                    if (extensions.getExtension(derObjectIdentifier).isCritical()) {
                        vector.addElement(derObjectIdentifier.getId());
                    }
                }
                final X509Extension[] array = new X509Extension[vector.size()];
                vector.copyInto(array);
                return array;
            }
        }
        return null;
    }
    
    private byte[] b(final String s) {
        final X509Extensions extensions = this.d.getTBSCertificate().getExtensions();
        if (extensions != null) {
            final X509Extension extension = extensions.getExtension(new DERObjectIdentifier(s));
            if (extension != null) {
                return extension.getValue().getOctets();
            }
        }
        return null;
    }
    
    public byte[] getExtensionValue(final String s) {
        final X509Extensions extensions = this.d.getTBSCertificate().getExtensions();
        if (extensions != null) {
            final X509Extension extension = extensions.getExtension(new DERObjectIdentifier(s));
            if (extension != null) {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                final DEROutputStream derOutputStream = new DEROutputStream(byteArrayOutputStream);
                try {
                    derOutputStream.writeObject(extension.getValue());
                    return byteArrayOutputStream.toByteArray();
                }
                catch (final Exception ex) {
                    throw new RuntimeException("error encoding " + ex.toString());
                }
            }
        }
        return null;
    }
    
    public X509Extension[] getNonCriticalExtensionOIDs() {
        if (this.getVersion() == 3) {
            final Vector vector = new Vector();
            final X509Extensions extensions = this.d.getTBSCertificate().getExtensions();
            if (extensions != null) {
                final Enumeration oids = extensions.oids();
                while (oids.hasMoreElements()) {
                    final DERObjectIdentifier derObjectIdentifier = oids.nextElement();
                    if (!extensions.getExtension(derObjectIdentifier).isCritical()) {
                        vector.addElement(derObjectIdentifier.getId());
                    }
                }
                final X509Extension[] array = new X509Extension[vector.size()];
                vector.copyInto(array);
                return array;
            }
        }
        return null;
    }
    
    public boolean hasUnsupportedCriticalExtension() {
        if (this.getVersion() == 3) {
            final X509Extensions extensions = this.d.getTBSCertificate().getExtensions();
            if (extensions != null) {
                final Enumeration oids = extensions.oids();
                while (oids.hasMoreElements()) {
                    final DERObjectIdentifier derObjectIdentifier = oids.nextElement();
                    if (!derObjectIdentifier.getId().equals("2.5.29.15")) {
                        if (derObjectIdentifier.getId().equals("2.5.29.19")) {
                            continue;
                        }
                        if (extensions.getExtension(derObjectIdentifier).isCritical()) {
                            return true;
                        }
                        continue;
                    }
                }
            }
        }
        return false;
    }
    
    public PublicKey getPublicKey() throws CertificateException {
        try {
            final AlgorithmIdentifier algorithmId = this.d.getSubjectPublicKeyInfo().getAlgorithmId();
            if (algorithmId.getObjectId().equals(PKCSObjectIdentifiers.rsaEncryption) || algorithmId.getObjectId().equals(X509ObjectIdentifiers.id_ea_rsa)) {
                final RSAPublicKeyStructure instance = RSAPublicKeyStructure.getInstance(this.d.getSubjectPublicKeyInfo().getPublicKey());
                return new RsaPublicKey(instance.getModulus(), instance.getPublicExponent());
            }
            throw new CertificateException(5, "Public key algorithm id " + algorithmId.getObjectId().getId() + " is not supported");
        }
        catch (final IOException ex) {
            throw new CertificateException(4, ex.getMessage());
        }
    }
    
    public byte[] getEncoded() throws CertificateException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DEROutputStream derOutputStream = new DEROutputStream(byteArrayOutputStream);
        try {
            derOutputStream.writeObject(this.d);
            return byteArrayOutputStream.toByteArray();
        }
        catch (final IOException ex) {
            throw new CertificateException(3, ex.toString());
        }
    }
    
    public void setBagAttribute(final DERObjectIdentifier derObjectIdentifier, final DEREncodable derEncodable) {
        this.c.put(derObjectIdentifier, derEncodable);
        this.b.addElement(derObjectIdentifier);
    }
    
    public DEREncodable getBagAttribute(final DERObjectIdentifier derObjectIdentifier) {
        return this.c.get(derObjectIdentifier);
    }
    
    public Enumeration getBagAttributeKeys() {
        return this.b.elements();
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String property = System.getProperty("line.separator");
        sb.append("  [0]         Version: " + this.getVersion() + property);
        sb.append("         SerialNumber: " + this.getSerialNumber() + property);
        sb.append("             IssuerDN: " + this.getIssuerDN() + property);
        sb.append("           Start Date: " + this.getNotBefore() + property);
        sb.append("           Final Date: " + this.getNotAfter() + property);
        sb.append("            SubjectDN: " + this.getSubjectDN() + property);
        try {
            sb.append("           Public Key: " + this.getPublicKey() + property);
        }
        catch (final CertificateException ex) {
            sb.append("           Public Key:  " + ex.getMessage());
        }
        try {
            sb.append("  Signature Algorithm: " + this.getSigAlgName() + property);
        }
        catch (final CertificateException ex2) {
            sb.append("  Signature Algorithm: " + ex2.getMessage());
        }
        final byte[] signature = this.getSignature();
        sb.append("            Signature: " + new String(Hex.encode(signature, 0, 20)) + property);
        for (int i = 20; i < signature.length; i += 20) {
            if (i < signature.length - 20) {
                sb.append("                       " + new String(Hex.encode(signature, i, 20)) + property);
            }
            else {
                sb.append("                       " + new String(Hex.encode(signature, i, signature.length - i)) + property);
            }
        }
        final X509Extensions extensions = this.d.getTBSCertificate().getExtensions();
        if (extensions != null) {
            final Enumeration oids = extensions.oids();
            if (oids.hasMoreElements()) {
                sb.append("       Extensions: \n");
            }
            while (oids.hasMoreElements()) {
                final DERObjectIdentifier derObjectIdentifier = oids.nextElement();
                final X509Extension extension = extensions.getExtension(derObjectIdentifier);
                if (extension.getValue() != null) {
                    final DERInputStream derInputStream = new DERInputStream(new ByteArrayInputStream(extension.getValue().getOctets()));
                    sb.append("                       critical(" + extension.isCritical() + ") ");
                    try {
                        if (derObjectIdentifier.equals(X509Extensions.BasicConstraints)) {
                            sb.append(new BasicConstraints((ASN1Sequence)derInputStream.readObject()) + property);
                        }
                        else if (derObjectIdentifier.equals(X509Extensions.KeyUsage)) {
                            sb.append(new KeyUsage((DERBitString)derInputStream.readObject()) + property);
                        }
                        else if (derObjectIdentifier.equals(MiscObjectIdentifiers.netscapeCertType)) {
                            sb.append(new NetscapeCertType((DERBitString)derInputStream.readObject()) + property);
                        }
                        else if (derObjectIdentifier.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
                            sb.append(new NetscapeRevocationURL((DERIA5String)derInputStream.readObject()) + property);
                        }
                        else if (derObjectIdentifier.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
                            sb.append(new VerisignCzagExtension((DERIA5String)derInputStream.readObject()) + property);
                        }
                        else {
                            sb.append(derObjectIdentifier.getId());
                            sb.append(" value = " + ASN1Dump.dumpAsString(derInputStream.readObject()) + property);
                        }
                    }
                    catch (final Exception ex3) {
                        sb.append(derObjectIdentifier.getId());
                        sb.append(" value = *****" + property);
                    }
                }
                else {
                    sb.append(property);
                }
            }
        }
        return sb.toString();
    }
    
    public final void verify(final PublicKey publicKey) {
    }
}
