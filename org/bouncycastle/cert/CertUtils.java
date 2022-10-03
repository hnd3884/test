package org.bouncycastle.cert;

import java.util.ArrayList;
import org.bouncycastle.asn1.DERNull;
import java.text.ParseException;
import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.OutputStream;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.AttributeCertificateInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.operator.ContentSigner;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.List;
import java.util.Set;

class CertUtils
{
    private static Set EMPTY_SET;
    private static List EMPTY_LIST;
    
    static ASN1Primitive parseNonEmptyASN1(final byte[] array) throws IOException {
        final ASN1Primitive fromByteArray = ASN1Primitive.fromByteArray(array);
        if (fromByteArray == null) {
            throw new IOException("no content found");
        }
        return fromByteArray;
    }
    
    static X509CertificateHolder generateFullCert(final ContentSigner contentSigner, final TBSCertificate tbsCertificate) {
        try {
            return new X509CertificateHolder(generateStructure(tbsCertificate, contentSigner.getAlgorithmIdentifier(), generateSig(contentSigner, (ASN1Encodable)tbsCertificate)));
        }
        catch (final IOException ex) {
            throw new IllegalStateException("cannot produce certificate signature");
        }
    }
    
    static X509AttributeCertificateHolder generateFullAttrCert(final ContentSigner contentSigner, final AttributeCertificateInfo attributeCertificateInfo) {
        try {
            return new X509AttributeCertificateHolder(generateAttrStructure(attributeCertificateInfo, contentSigner.getAlgorithmIdentifier(), generateSig(contentSigner, (ASN1Encodable)attributeCertificateInfo)));
        }
        catch (final IOException ex) {
            throw new IllegalStateException("cannot produce attribute certificate signature");
        }
    }
    
    static X509CRLHolder generateFullCRL(final ContentSigner contentSigner, final TBSCertList list) {
        try {
            return new X509CRLHolder(generateCRLStructure(list, contentSigner.getAlgorithmIdentifier(), generateSig(contentSigner, (ASN1Encodable)list)));
        }
        catch (final IOException ex) {
            throw new IllegalStateException("cannot produce certificate signature");
        }
    }
    
    private static byte[] generateSig(final ContentSigner contentSigner, final ASN1Encodable asn1Encodable) throws IOException {
        final OutputStream outputStream = contentSigner.getOutputStream();
        new DEROutputStream(outputStream).writeObject(asn1Encodable);
        outputStream.close();
        return contentSigner.getSignature();
    }
    
    private static Certificate generateStructure(final TBSCertificate tbsCertificate, final AlgorithmIdentifier algorithmIdentifier, final byte[] array) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)tbsCertificate);
        asn1EncodableVector.add((ASN1Encodable)algorithmIdentifier);
        asn1EncodableVector.add((ASN1Encodable)new DERBitString(array));
        return Certificate.getInstance((Object)new DERSequence(asn1EncodableVector));
    }
    
    private static AttributeCertificate generateAttrStructure(final AttributeCertificateInfo attributeCertificateInfo, final AlgorithmIdentifier algorithmIdentifier, final byte[] array) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)attributeCertificateInfo);
        asn1EncodableVector.add((ASN1Encodable)algorithmIdentifier);
        asn1EncodableVector.add((ASN1Encodable)new DERBitString(array));
        return AttributeCertificate.getInstance((Object)new DERSequence(asn1EncodableVector));
    }
    
    private static CertificateList generateCRLStructure(final TBSCertList list, final AlgorithmIdentifier algorithmIdentifier, final byte[] array) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)list);
        asn1EncodableVector.add((ASN1Encodable)algorithmIdentifier);
        asn1EncodableVector.add((ASN1Encodable)new DERBitString(array));
        return CertificateList.getInstance((Object)new DERSequence(asn1EncodableVector));
    }
    
    static Set getCriticalExtensionOIDs(final Extensions extensions) {
        if (extensions == null) {
            return CertUtils.EMPTY_SET;
        }
        return Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList(extensions.getCriticalExtensionOIDs())));
    }
    
    static Set getNonCriticalExtensionOIDs(final Extensions extensions) {
        if (extensions == null) {
            return CertUtils.EMPTY_SET;
        }
        return Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList(extensions.getNonCriticalExtensionOIDs())));
    }
    
    static List getExtensionOIDs(final Extensions extensions) {
        if (extensions == null) {
            return CertUtils.EMPTY_LIST;
        }
        return Collections.unmodifiableList((List<?>)Arrays.asList((T[])extensions.getExtensionOIDs()));
    }
    
    static void addExtension(final ExtensionsGenerator extensionsGenerator, final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws CertIOException {
        try {
            extensionsGenerator.addExtension(asn1ObjectIdentifier, b, asn1Encodable);
        }
        catch (final IOException ex) {
            throw new CertIOException("cannot encode extension: " + ex.getMessage(), ex);
        }
    }
    
    static DERBitString booleanToBitString(final boolean[] array) {
        final byte[] array2 = new byte[(array.length + 7) / 8];
        for (int i = 0; i != array.length; ++i) {
            final byte[] array3 = array2;
            final int n = i / 8;
            array3[n] |= (byte)(array[i] ? (1 << 7 - i % 8) : 0);
        }
        final int n2 = array.length % 8;
        if (n2 == 0) {
            return new DERBitString(array2);
        }
        return new DERBitString(array2, 8 - n2);
    }
    
    static boolean[] bitStringToBoolean(final DERBitString derBitString) {
        if (derBitString != null) {
            final byte[] bytes = derBitString.getBytes();
            final boolean[] array = new boolean[bytes.length * 8 - derBitString.getPadBits()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = ((bytes[i / 8] & 128 >>> i % 8) != 0x0);
            }
            return array;
        }
        return null;
    }
    
    static Date recoverDate(final ASN1GeneralizedTime asn1GeneralizedTime) {
        try {
            return asn1GeneralizedTime.getDate();
        }
        catch (final ParseException ex) {
            throw new IllegalStateException("unable to recover date: " + ex.getMessage());
        }
    }
    
    static boolean isAlgIdEqual(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2) {
        if (!algorithmIdentifier.getAlgorithm().equals((Object)algorithmIdentifier2.getAlgorithm())) {
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
    
    static {
        CertUtils.EMPTY_SET = Collections.unmodifiableSet((Set<?>)new HashSet<Object>());
        CertUtils.EMPTY_LIST = Collections.unmodifiableList((List<?>)new ArrayList<Object>());
    }
}
