package org.bouncycastle.cms;

import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.util.HashSet;
import org.bouncycastle.util.io.TeeOutputStream;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.io.Streams;
import java.io.IOException;
import org.bouncycastle.asn1.BEROctetStringGenerator;
import java.io.OutputStream;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import java.util.Collection;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import java.util.Iterator;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Selector;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.util.Store;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Set;

class CMSUtils
{
    private static final Set<String> des;
    private static final Set mqvAlgs;
    private static final Set ecAlgs;
    private static final Set gostAlgs;
    
    static boolean isMQV(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return CMSUtils.mqvAlgs.contains(asn1ObjectIdentifier);
    }
    
    static boolean isEC(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return CMSUtils.ecAlgs.contains(asn1ObjectIdentifier);
    }
    
    static boolean isGOST(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return CMSUtils.gostAlgs.contains(asn1ObjectIdentifier);
    }
    
    static boolean isRFC2631(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.id_alg_ESDH) || asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.id_alg_SSDH);
    }
    
    static boolean isDES(final String s) {
        return CMSUtils.des.contains(Strings.toUpperCase(s));
    }
    
    static boolean isEquivalent(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2) {
        if (algorithmIdentifier == null || algorithmIdentifier2 == null) {
            return false;
        }
        if (!algorithmIdentifier.getAlgorithm().equals((Object)algorithmIdentifier2.getAlgorithm())) {
            return false;
        }
        final ASN1Encodable parameters = algorithmIdentifier.getParameters();
        final ASN1Encodable parameters2 = algorithmIdentifier2.getParameters();
        if (parameters != null) {
            return parameters.equals(parameters2) || (parameters.equals(DERNull.INSTANCE) && parameters2 == null);
        }
        return parameters2 == null || parameters2.equals(DERNull.INSTANCE);
    }
    
    static ContentInfo readContentInfo(final byte[] array) throws CMSException {
        return readContentInfo(new ASN1InputStream(array));
    }
    
    static ContentInfo readContentInfo(final InputStream inputStream) throws CMSException {
        return readContentInfo(new ASN1InputStream(inputStream));
    }
    
    static List getCertificatesFromStore(final Store store) throws CMSException {
        final ArrayList list = new ArrayList();
        try {
            final Iterator iterator = store.getMatches((Selector)null).iterator();
            while (iterator.hasNext()) {
                list.add(((X509CertificateHolder)iterator.next()).toASN1Structure());
            }
            return list;
        }
        catch (final ClassCastException ex) {
            throw new CMSException("error processing certs", ex);
        }
    }
    
    static List getAttributeCertificatesFromStore(final Store store) throws CMSException {
        final ArrayList list = new ArrayList();
        try {
            final Iterator iterator = store.getMatches((Selector)null).iterator();
            while (iterator.hasNext()) {
                list.add(new DERTaggedObject(false, 2, (ASN1Encodable)((X509AttributeCertificateHolder)iterator.next()).toASN1Structure()));
            }
            return list;
        }
        catch (final ClassCastException ex) {
            throw new CMSException("error processing certs", ex);
        }
    }
    
    static List getCRLsFromStore(final Store store) throws CMSException {
        final ArrayList list = new ArrayList();
        try {
            for (final Object next : store.getMatches((Selector)null)) {
                if (next instanceof X509CRLHolder) {
                    list.add(((X509CRLHolder)next).toASN1Structure());
                }
                else if (next instanceof OtherRevocationInfoFormat) {
                    final OtherRevocationInfoFormat instance = OtherRevocationInfoFormat.getInstance(next);
                    validateInfoFormat(instance);
                    list.add(new DERTaggedObject(false, 1, (ASN1Encodable)instance));
                }
                else {
                    if (!(next instanceof ASN1TaggedObject)) {
                        continue;
                    }
                    list.add(next);
                }
            }
            return list;
        }
        catch (final ClassCastException ex) {
            throw new CMSException("error processing certs", ex);
        }
    }
    
    private static void validateInfoFormat(final OtherRevocationInfoFormat otherRevocationInfoFormat) {
        if (CMSObjectIdentifiers.id_ri_ocsp_response.equals((Object)otherRevocationInfoFormat.getInfoFormat()) && OCSPResponse.getInstance((Object)otherRevocationInfoFormat.getInfo()).getResponseStatus().getValue().intValue() != 0) {
            throw new IllegalArgumentException("cannot add unsuccessful OCSP response to CMS SignedData");
        }
    }
    
    static Collection getOthersFromStore(final ASN1ObjectIdentifier asn1ObjectIdentifier, final Store store) {
        final ArrayList list = new ArrayList();
        final Iterator iterator = store.getMatches((Selector)null).iterator();
        while (iterator.hasNext()) {
            final OtherRevocationInfoFormat otherRevocationInfoFormat = new OtherRevocationInfoFormat(asn1ObjectIdentifier, (ASN1Encodable)iterator.next());
            validateInfoFormat(otherRevocationInfoFormat);
            list.add(new DERTaggedObject(false, 1, (ASN1Encodable)otherRevocationInfoFormat));
        }
        return list;
    }
    
    static ASN1Set createBerSetFromList(final List list) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            asn1EncodableVector.add((ASN1Encodable)iterator.next());
        }
        return (ASN1Set)new BERSet(asn1EncodableVector);
    }
    
    static ASN1Set createDerSetFromList(final List list) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            asn1EncodableVector.add((ASN1Encodable)iterator.next());
        }
        return (ASN1Set)new DERSet(asn1EncodableVector);
    }
    
    static OutputStream createBEROctetOutputStream(final OutputStream outputStream, final int n, final boolean b, final int n2) throws IOException {
        final BEROctetStringGenerator berOctetStringGenerator = new BEROctetStringGenerator(outputStream, n, b);
        if (n2 != 0) {
            return berOctetStringGenerator.getOctetOutputStream(new byte[n2]);
        }
        return berOctetStringGenerator.getOctetOutputStream();
    }
    
    private static ContentInfo readContentInfo(final ASN1InputStream asn1InputStream) throws CMSException {
        try {
            final ContentInfo instance = ContentInfo.getInstance((Object)asn1InputStream.readObject());
            if (instance == null) {
                throw new CMSException("No content found.");
            }
            return instance;
        }
        catch (final IOException ex) {
            throw new CMSException("IOException reading content.", ex);
        }
        catch (final ClassCastException ex2) {
            throw new CMSException("Malformed content.", ex2);
        }
        catch (final IllegalArgumentException ex3) {
            throw new CMSException("Malformed content.", ex3);
        }
    }
    
    public static byte[] streamToByteArray(final InputStream inputStream) throws IOException {
        return Streams.readAll(inputStream);
    }
    
    public static byte[] streamToByteArray(final InputStream inputStream, final int n) throws IOException {
        return Streams.readAllLimited(inputStream, n);
    }
    
    static InputStream attachDigestsToInputStream(final Collection collection, final InputStream inputStream) {
        Object o = inputStream;
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            o = new TeeInputStream((InputStream)o, ((DigestCalculator)iterator.next()).getOutputStream());
        }
        return (InputStream)o;
    }
    
    static OutputStream attachSignersToOutputStream(final Collection collection, final OutputStream outputStream) {
        OutputStream safeTeeOutputStream = outputStream;
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            safeTeeOutputStream = getSafeTeeOutputStream(safeTeeOutputStream, ((SignerInfoGenerator)iterator.next()).getCalculatingOutputStream());
        }
        return safeTeeOutputStream;
    }
    
    static OutputStream getSafeOutputStream(final OutputStream outputStream) {
        return (outputStream == null) ? new NullOutputStream() : outputStream;
    }
    
    static OutputStream getSafeTeeOutputStream(final OutputStream outputStream, final OutputStream outputStream2) {
        return (OutputStream)((outputStream == null) ? getSafeOutputStream(outputStream2) : ((outputStream2 == null) ? getSafeOutputStream(outputStream) : new TeeOutputStream(outputStream, outputStream2)));
    }
    
    static {
        des = new HashSet<String>();
        mqvAlgs = new HashSet();
        ecAlgs = new HashSet();
        gostAlgs = new HashSet();
        CMSUtils.des.add("DES");
        CMSUtils.des.add("DESEDE");
        CMSUtils.des.add(OIWObjectIdentifiers.desCBC.getId());
        CMSUtils.des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
        CMSUtils.des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
        CMSUtils.des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
        CMSUtils.mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        CMSUtils.mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
        CMSUtils.mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
        CMSUtils.mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
        CMSUtils.mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
        CMSUtils.ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
        CMSUtils.ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
        CMSUtils.gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        CMSUtils.gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        CMSUtils.gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
    }
}
