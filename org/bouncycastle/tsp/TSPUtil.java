package org.bouncycastle.tsp;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.util.Integers;
import java.util.HashMap;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import java.util.Collections;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import java.io.OutputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.cms.SignerInformation;
import java.util.Map;
import java.util.List;

public class TSPUtil
{
    private static List EMPTY_LIST;
    private static final Map digestLengths;
    private static final Map digestNames;
    
    public static Collection getSignatureTimestamps(final SignerInformation signerInformation, final DigestCalculatorProvider digestCalculatorProvider) throws TSPValidationException {
        final ArrayList list = new ArrayList();
        final AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        if (unsignedAttributes != null) {
            final ASN1EncodableVector all = unsignedAttributes.getAll(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
            for (int i = 0; i < all.size(); ++i) {
                final ASN1Set attrValues = ((Attribute)all.get(i)).getAttrValues();
                for (int j = 0; j < attrValues.size(); ++j) {
                    try {
                        final TimeStampToken timeStampToken = new TimeStampToken(ContentInfo.getInstance((Object)attrValues.getObjectAt(j)));
                        final TimeStampTokenInfo timeStampInfo = timeStampToken.getTimeStampInfo();
                        final DigestCalculator value = digestCalculatorProvider.get(timeStampInfo.getHashAlgorithm());
                        final OutputStream outputStream = value.getOutputStream();
                        outputStream.write(signerInformation.getSignature());
                        outputStream.close();
                        if (!Arrays.constantTimeAreEqual(value.getDigest(), timeStampInfo.getMessageImprintDigest())) {
                            throw new TSPValidationException("Incorrect digest in message imprint");
                        }
                        list.add(timeStampToken);
                    }
                    catch (final OperatorCreationException ex) {
                        throw new TSPValidationException("Unknown hash algorithm specified in timestamp");
                    }
                    catch (final Exception ex2) {
                        throw new TSPValidationException("Timestamp could not be parsed");
                    }
                }
            }
        }
        return list;
    }
    
    public static void validateCertificate(final X509CertificateHolder x509CertificateHolder) throws TSPValidationException {
        if (x509CertificateHolder.toASN1Structure().getVersionNumber() != 3) {
            throw new IllegalArgumentException("Certificate must have an ExtendedKeyUsage extension.");
        }
        final Extension extension = x509CertificateHolder.getExtension(Extension.extendedKeyUsage);
        if (extension == null) {
            throw new TSPValidationException("Certificate must have an ExtendedKeyUsage extension.");
        }
        if (!extension.isCritical()) {
            throw new TSPValidationException("Certificate must have an ExtendedKeyUsage extension marked as critical.");
        }
        final ExtendedKeyUsage instance = ExtendedKeyUsage.getInstance((Object)extension.getParsedValue());
        if (!instance.hasKeyPurposeId(KeyPurposeId.id_kp_timeStamping) || instance.size() != 1) {
            throw new TSPValidationException("ExtendedKeyUsage not solely time stamping.");
        }
    }
    
    static int getDigestLength(final String s) throws TSPException {
        final Integer n = TSPUtil.digestLengths.get(s);
        if (n != null) {
            return n;
        }
        throw new TSPException("digest algorithm cannot be found.");
    }
    
    static List getExtensionOIDs(final Extensions extensions) {
        if (extensions == null) {
            return TSPUtil.EMPTY_LIST;
        }
        return Collections.unmodifiableList((List<?>)java.util.Arrays.asList((T[])extensions.getExtensionOIDs()));
    }
    
    static void addExtension(final ExtensionsGenerator extensionsGenerator, final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws TSPIOException {
        try {
            extensionsGenerator.addExtension(asn1ObjectIdentifier, b, asn1Encodable);
        }
        catch (final IOException ex) {
            throw new TSPIOException("cannot encode extension: " + ex.getMessage(), ex);
        }
    }
    
    static {
        TSPUtil.EMPTY_LIST = Collections.unmodifiableList((List<?>)new ArrayList<Object>());
        digestLengths = new HashMap();
        digestNames = new HashMap();
        TSPUtil.digestLengths.put(PKCSObjectIdentifiers.md5.getId(), Integers.valueOf(16));
        TSPUtil.digestLengths.put(OIWObjectIdentifiers.idSHA1.getId(), Integers.valueOf(20));
        TSPUtil.digestLengths.put(NISTObjectIdentifiers.id_sha224.getId(), Integers.valueOf(28));
        TSPUtil.digestLengths.put(NISTObjectIdentifiers.id_sha256.getId(), Integers.valueOf(32));
        TSPUtil.digestLengths.put(NISTObjectIdentifiers.id_sha384.getId(), Integers.valueOf(48));
        TSPUtil.digestLengths.put(NISTObjectIdentifiers.id_sha512.getId(), Integers.valueOf(64));
        TSPUtil.digestLengths.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), Integers.valueOf(16));
        TSPUtil.digestLengths.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), Integers.valueOf(20));
        TSPUtil.digestLengths.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), Integers.valueOf(32));
        TSPUtil.digestLengths.put(CryptoProObjectIdentifiers.gostR3411.getId(), Integers.valueOf(32));
        TSPUtil.digestLengths.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256.getId(), Integers.valueOf(32));
        TSPUtil.digestLengths.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512.getId(), Integers.valueOf(64));
        TSPUtil.digestLengths.put(GMObjectIdentifiers.sm3.getId(), Integers.valueOf(32));
        TSPUtil.digestNames.put(PKCSObjectIdentifiers.md5.getId(), "MD5");
        TSPUtil.digestNames.put(OIWObjectIdentifiers.idSHA1.getId(), "SHA1");
        TSPUtil.digestNames.put(NISTObjectIdentifiers.id_sha224.getId(), "SHA224");
        TSPUtil.digestNames.put(NISTObjectIdentifiers.id_sha256.getId(), "SHA256");
        TSPUtil.digestNames.put(NISTObjectIdentifiers.id_sha384.getId(), "SHA384");
        TSPUtil.digestNames.put(NISTObjectIdentifiers.id_sha512.getId(), "SHA512");
        TSPUtil.digestNames.put(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId(), "SHA1");
        TSPUtil.digestNames.put(PKCSObjectIdentifiers.sha224WithRSAEncryption.getId(), "SHA224");
        TSPUtil.digestNames.put(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId(), "SHA256");
        TSPUtil.digestNames.put(PKCSObjectIdentifiers.sha384WithRSAEncryption.getId(), "SHA384");
        TSPUtil.digestNames.put(PKCSObjectIdentifiers.sha512WithRSAEncryption.getId(), "SHA512");
        TSPUtil.digestNames.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), "RIPEMD128");
        TSPUtil.digestNames.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), "RIPEMD160");
        TSPUtil.digestNames.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), "RIPEMD256");
        TSPUtil.digestNames.put(CryptoProObjectIdentifiers.gostR3411.getId(), "GOST3411");
        TSPUtil.digestNames.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256.getId(), "GOST3411-2012-256");
        TSPUtil.digestNames.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512.getId(), "GOST3411-2012-512");
        TSPUtil.digestNames.put(GMObjectIdentifiers.sm3.getId(), "SM3");
    }
}
