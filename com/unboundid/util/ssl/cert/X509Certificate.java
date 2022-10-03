package com.unboundid.util.ssl.cert;

import com.unboundid.util.Base64;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.security.PublicKey;
import java.util.Date;
import java.security.Signature;
import java.util.UUID;
import java.security.PrivateKey;
import com.unboundid.asn1.ASN1OctetString;
import java.security.MessageDigest;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import com.unboundid.util.ObjectPair;
import com.unboundid.asn1.ASN1GeneralizedTime;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1UTF8String;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1ObjectIdentifier;
import com.unboundid.asn1.ASN1BigInteger;
import com.unboundid.asn1.ASN1Exception;
import java.util.GregorianCalendar;
import com.unboundid.asn1.ASN1UTCTime;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.schema.Schema;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.OID;
import java.util.List;
import com.unboundid.ldap.sdk.DN;
import java.math.BigInteger;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1BitString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class X509Certificate implements Serializable
{
    private static final byte TYPE_EXPLICIT_VERSION = -96;
    private static final byte TYPE_IMPLICIT_ISSUER_UNIQUE_ID = -127;
    private static final byte TYPE_IMPLICIT_SUBJECT_UNIQUE_ID = -126;
    private static final byte TYPE_EXPLICIT_EXTENSIONS = -93;
    private static final long serialVersionUID = -4680448103099282243L;
    private final ASN1BitString issuerUniqueID;
    private final ASN1BitString signatureValue;
    private final ASN1BitString encodedPublicKey;
    private final ASN1BitString subjectUniqueID;
    private final ASN1Element publicKeyAlgorithmParameters;
    private final ASN1Element signatureAlgorithmParameters;
    private final BigInteger serialNumber;
    private final byte[] x509CertificateBytes;
    private final DecodedPublicKey decodedPublicKey;
    private final DN issuerDN;
    private final DN subjectDN;
    private final List<X509CertificateExtension> extensions;
    private final long notAfter;
    private final long notBefore;
    private final OID publicKeyAlgorithmOID;
    private final OID signatureAlgorithmOID;
    private final String publicKeyAlgorithmName;
    private final String signatureAlgorithmName;
    private final X509CertificateVersion version;
    
    X509Certificate(final X509CertificateVersion version, final BigInteger serialNumber, final OID signatureAlgorithmOID, final ASN1Element signatureAlgorithmParameters, final ASN1BitString signatureValue, final DN issuerDN, final long notBefore, final long notAfter, final DN subjectDN, final OID publicKeyAlgorithmOID, final ASN1Element publicKeyAlgorithmParameters, final ASN1BitString encodedPublicKey, final DecodedPublicKey decodedPublicKey, final ASN1BitString issuerUniqueID, final ASN1BitString subjectUniqueID, final X509CertificateExtension... extensions) throws CertException {
        this.version = version;
        this.serialNumber = serialNumber;
        this.signatureAlgorithmOID = signatureAlgorithmOID;
        this.signatureAlgorithmParameters = signatureAlgorithmParameters;
        this.signatureValue = signatureValue;
        this.issuerDN = issuerDN;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.subjectDN = subjectDN;
        this.publicKeyAlgorithmOID = publicKeyAlgorithmOID;
        this.publicKeyAlgorithmParameters = publicKeyAlgorithmParameters;
        this.encodedPublicKey = encodedPublicKey;
        this.decodedPublicKey = decodedPublicKey;
        this.issuerUniqueID = issuerUniqueID;
        this.subjectUniqueID = subjectUniqueID;
        this.extensions = StaticUtils.toList(extensions);
        final SignatureAlgorithmIdentifier signatureAlgorithmIdentifier = SignatureAlgorithmIdentifier.forOID(signatureAlgorithmOID);
        if (signatureAlgorithmIdentifier == null) {
            this.signatureAlgorithmName = null;
        }
        else {
            this.signatureAlgorithmName = signatureAlgorithmIdentifier.getUserFriendlyName();
        }
        final PublicKeyAlgorithmIdentifier publicKeyAlgorithmIdentifier = PublicKeyAlgorithmIdentifier.forOID(publicKeyAlgorithmOID);
        if (publicKeyAlgorithmIdentifier == null) {
            this.publicKeyAlgorithmName = null;
        }
        else {
            this.publicKeyAlgorithmName = publicKeyAlgorithmIdentifier.getName();
        }
        this.x509CertificateBytes = this.encode().encode();
    }
    
    public X509Certificate(final byte[] encodedCertificate) throws CertException {
        this.x509CertificateBytes = encodedCertificate;
        ASN1Element[] certificateElements;
        try {
            certificateElements = ASN1Sequence.decodeAsSequence(encodedCertificate).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_DECODE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        if (certificateElements.length != 3) {
            throw new CertException(CertMessages.ERR_CERT_DECODE_UNEXPECTED_SEQUENCE_ELEMENT_COUNT.get(certificateElements.length));
        }
        ASN1Element[] tbsCertificateElements;
        try {
            tbsCertificateElements = ASN1Sequence.decodeAsSequence(certificateElements[0]).elements();
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CERT_DECODE_FIRST_ELEMENT_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        int tbsCertificateElementIndex;
        try {
            if ((tbsCertificateElements[0].getType() & 0xFF) == 0xA0) {
                final int versionIntValue = ASN1Integer.decodeAsInteger(tbsCertificateElements[0].getValue()).intValue();
                this.version = X509CertificateVersion.valueOf(versionIntValue);
                if (this.version == null) {
                    throw new CertException(CertMessages.ERR_CERT_DECODE_UNSUPPORTED_VERSION.get(this.version));
                }
                tbsCertificateElementIndex = 1;
            }
            else {
                this.version = X509CertificateVersion.V1;
                tbsCertificateElementIndex = 0;
            }
        }
        catch (final CertException e3) {
            Debug.debugException(e3);
            throw e3;
        }
        catch (final Exception e4) {
            Debug.debugException(e4);
            throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_VERSION.get(StaticUtils.getExceptionMessage(e4)), e4);
        }
        try {
            this.serialNumber = tbsCertificateElements[tbsCertificateElementIndex++].decodeAsBigInteger().getBigIntegerValue();
        }
        catch (final Exception e4) {
            Debug.debugException(e4);
            throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_SERIAL_NUMBER.get(StaticUtils.getExceptionMessage(e4)), e4);
        }
        try {
            final ASN1Element[] signatureAlgorithmElements = tbsCertificateElements[tbsCertificateElementIndex++].decodeAsSequence().elements();
            this.signatureAlgorithmOID = signatureAlgorithmElements[0].decodeAsObjectIdentifier().getOID();
            if (signatureAlgorithmElements.length > 1) {
                this.signatureAlgorithmParameters = signatureAlgorithmElements[1];
            }
            else {
                this.signatureAlgorithmParameters = null;
            }
        }
        catch (final Exception e4) {
            Debug.debugException(e4);
            throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_SIG_ALG.get(StaticUtils.getExceptionMessage(e4)), e4);
        }
        final SignatureAlgorithmIdentifier signatureAlgorithmIdentifier = SignatureAlgorithmIdentifier.forOID(this.signatureAlgorithmOID);
        if (signatureAlgorithmIdentifier == null) {
            this.signatureAlgorithmName = null;
        }
        else {
            this.signatureAlgorithmName = signatureAlgorithmIdentifier.getUserFriendlyName();
        }
        try {
            this.issuerDN = decodeName(tbsCertificateElements[tbsCertificateElementIndex++]);
        }
        catch (final Exception e5) {
            Debug.debugException(e5);
            throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_ISSUER_DN.get(StaticUtils.getExceptionMessage(e5)), e5);
        }
        try {
            final ASN1Element[] validityElements = tbsCertificateElements[tbsCertificateElementIndex++].decodeAsSequence().elements();
            switch (validityElements[0].getType()) {
                case 23: {
                    this.notBefore = decodeUTCTime(validityElements[0]);
                    break;
                }
                case 24: {
                    this.notBefore = validityElements[0].decodeAsGeneralizedTime().getTime();
                    break;
                }
                default: {
                    throw new CertException(CertMessages.ERR_CERT_DECODE_NOT_BEFORE_UNEXPECTED_TYPE.get(StaticUtils.toHex(validityElements[0].getType()), StaticUtils.toHex((byte)23), StaticUtils.toHex((byte)24)));
                }
            }
            switch (validityElements[1].getType()) {
                case 23: {
                    this.notAfter = decodeUTCTime(validityElements[1]);
                    break;
                }
                case 24: {
                    this.notAfter = validityElements[1].decodeAsGeneralizedTime().getTime();
                    break;
                }
                default: {
                    throw new CertException(CertMessages.ERR_CERT_DECODE_NOT_AFTER_UNEXPECTED_TYPE.get(StaticUtils.toHex(validityElements[0].getType()), StaticUtils.toHex((byte)23), StaticUtils.toHex((byte)24)));
                }
            }
        }
        catch (final CertException e6) {
            Debug.debugException(e6);
            throw e6;
        }
        catch (final Exception e5) {
            Debug.debugException(e5);
            throw new CertException(CertMessages.ERR_CERT_DECODE_COULD_NOT_PARSE_VALIDITY.get(StaticUtils.getExceptionMessage(e5)), e5);
        }
        try {
            this.subjectDN = decodeName(tbsCertificateElements[tbsCertificateElementIndex++]);
        }
        catch (final Exception e5) {
            Debug.debugException(e5);
            throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_SUBJECT_DN.get(StaticUtils.getExceptionMessage(e5)), e5);
        }
        try {
            final ASN1Element[] subjectPublicKeyInfoElements = tbsCertificateElements[tbsCertificateElementIndex++].decodeAsSequence().elements();
            final ASN1Element[] publicKeyAlgorithmElements = subjectPublicKeyInfoElements[0].decodeAsSequence().elements();
            this.publicKeyAlgorithmOID = publicKeyAlgorithmElements[0].decodeAsObjectIdentifier().getOID();
            if (publicKeyAlgorithmElements.length > 1) {
                this.publicKeyAlgorithmParameters = publicKeyAlgorithmElements[1];
            }
            else {
                this.publicKeyAlgorithmParameters = null;
            }
            this.encodedPublicKey = subjectPublicKeyInfoElements[1].decodeAsBitString();
        }
        catch (final Exception e5) {
            Debug.debugException(e5);
            throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_PUBLIC_KEY_INFO.get(StaticUtils.getExceptionMessage(e5)), e5);
        }
        final PublicKeyAlgorithmIdentifier publicKeyAlgorithmIdentifier = PublicKeyAlgorithmIdentifier.forOID(this.publicKeyAlgorithmOID);
        if (publicKeyAlgorithmIdentifier == null) {
            this.publicKeyAlgorithmName = null;
            this.decodedPublicKey = null;
        }
        else {
            this.publicKeyAlgorithmName = publicKeyAlgorithmIdentifier.getName();
            DecodedPublicKey pk = null;
            switch (publicKeyAlgorithmIdentifier) {
                case RSA: {
                    try {
                        pk = new RSAPublicKey(this.encodedPublicKey);
                    }
                    catch (final Exception e7) {
                        Debug.debugException(e7);
                    }
                    break;
                }
                case EC: {
                    try {
                        pk = new EllipticCurvePublicKey(this.encodedPublicKey);
                    }
                    catch (final Exception e7) {
                        Debug.debugException(e7);
                    }
                    break;
                }
            }
            this.decodedPublicKey = pk;
        }
        ASN1BitString issuerID = null;
        ASN1BitString subjectID = null;
        final ArrayList<X509CertificateExtension> extList = new ArrayList<X509CertificateExtension>(10);
        while (tbsCertificateElementIndex < tbsCertificateElements.length) {
            switch (tbsCertificateElements[tbsCertificateElementIndex].getType()) {
                case -127: {
                    try {
                        issuerID = tbsCertificateElements[tbsCertificateElementIndex].decodeAsBitString();
                        break;
                    }
                    catch (final Exception e8) {
                        Debug.debugException(e8);
                        throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_ISSUER_UNIQUE_ID.get(StaticUtils.getExceptionMessage(e8)), e8);
                    }
                }
                case -126: {
                    try {
                        subjectID = tbsCertificateElements[tbsCertificateElementIndex].decodeAsBitString();
                        break;
                    }
                    catch (final Exception e8) {
                        Debug.debugException(e8);
                        throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_SUBJECT_UNIQUE_ID.get(StaticUtils.getExceptionMessage(e8)), e8);
                    }
                }
                case -93: {
                    try {
                        final ASN1Element[] arr$;
                        final ASN1Element[] extensionElements = arr$ = ASN1Sequence.decodeAsSequence(tbsCertificateElements[tbsCertificateElementIndex].getValue()).elements();
                        for (final ASN1Element extensionElement : arr$) {
                            extList.add(X509CertificateExtension.decode(extensionElement));
                        }
                    }
                    catch (final Exception e8) {
                        Debug.debugException(e8);
                        throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_EXTENSION.get(StaticUtils.getExceptionMessage(e8)), e8);
                    }
                    break;
                }
            }
            ++tbsCertificateElementIndex;
        }
        this.issuerUniqueID = issuerID;
        this.subjectUniqueID = subjectID;
        this.extensions = Collections.unmodifiableList((List<? extends X509CertificateExtension>)extList);
        try {
            final ASN1Element[] signatureAlgorithmElements2 = certificateElements[1].decodeAsSequence().elements();
            final OID oid = signatureAlgorithmElements2[0].decodeAsObjectIdentifier().getOID();
            if (!oid.equals(this.signatureAlgorithmOID)) {
                throw new CertException(CertMessages.ERR_CERT_DECODE_SIG_ALG_MISMATCH.get(this.signatureAlgorithmOID.toString(), oid.toString()));
            }
        }
        catch (final CertException e9) {
            Debug.debugException(e9);
            throw e9;
        }
        catch (final Exception e8) {
            Debug.debugException(e8);
            throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_SIG_ALG.get(StaticUtils.getExceptionMessage(e8)), e8);
        }
        try {
            this.signatureValue = certificateElements[2].decodeAsBitString();
        }
        catch (final Exception e8) {
            Debug.debugException(e8);
            throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_SIG_VALUE.get(StaticUtils.getExceptionMessage(e8)), e8);
        }
    }
    
    static DN decodeName(final ASN1Element element) throws CertException {
        Schema schema;
        try {
            schema = Schema.getDefaultStandardSchema();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            schema = null;
        }
        ASN1Element[] rdnElements;
        try {
            rdnElements = ASN1Sequence.decodeAsSequence(element).elements();
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CERT_DECODE_NAME_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        final ArrayList<RDN> rdns = new ArrayList<RDN>(rdnElements.length);
        for (int i = 0; i < rdnElements.length; ++i) {
            try {
                final ASN1Element[] attributeSetElements = rdnElements[i].decodeAsSet().elements();
                final String[] attributeNames = new String[attributeSetElements.length];
                final byte[][] attributeValues = new byte[attributeSetElements.length][];
                for (int j = 0; j < attributeSetElements.length; ++j) {
                    final ASN1Element[] attributeTypeAndValueElements = ASN1Sequence.decodeAsSequence(attributeSetElements[j]).elements();
                    final OID attributeTypeOID = attributeTypeAndValueElements[0].decodeAsObjectIdentifier().getOID();
                    final AttributeTypeDefinition attributeType = schema.getAttributeType(attributeTypeOID.toString());
                    if (attributeType == null) {
                        attributeNames[j] = attributeTypeOID.toString();
                    }
                    else {
                        attributeNames[j] = attributeType.getNameOrOID().toUpperCase();
                    }
                    attributeValues[j] = attributeTypeAndValueElements[1].decodeAsOctetString().getValue();
                }
                rdns.add(new RDN(attributeNames, attributeValues, schema));
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new CertException(CertMessages.ERR_CERT_DECODE_CANNOT_PARSE_NAME_SEQUENCE_ELEMENT.get(i, StaticUtils.getExceptionMessage(e3)), e3);
            }
        }
        Collections.reverse(rdns);
        return new DN(rdns);
    }
    
    private static long decodeUTCTime(final ASN1Element element) throws ASN1Exception {
        final long timeValue = ASN1UTCTime.decodeAsUTCTime(element).getTime();
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timeValue);
        final int year = calendar.get(1);
        if (year < 1949) {
            calendar.set(1, year + 100);
        }
        else if (year > 2050) {
            calendar.set(1, year - 100);
        }
        return calendar.getTimeInMillis();
    }
    
    ASN1Element encode() throws CertException {
        try {
            final ArrayList<ASN1Element> tbsCertificateElements = new ArrayList<ASN1Element>(10);
            if (this.version != X509CertificateVersion.V1) {
                tbsCertificateElements.add(new ASN1Element((byte)(-96), new ASN1Integer(this.version.getIntValue()).encode()));
            }
            tbsCertificateElements.add(new ASN1BigInteger(this.serialNumber));
            if (this.signatureAlgorithmParameters == null) {
                tbsCertificateElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.signatureAlgorithmOID) }));
            }
            else {
                tbsCertificateElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.signatureAlgorithmOID), this.signatureAlgorithmParameters }));
            }
            tbsCertificateElements.add(encodeName(this.issuerDN));
            tbsCertificateElements.add(encodeValiditySequence(this.notBefore, this.notAfter));
            tbsCertificateElements.add(encodeName(this.subjectDN));
            if (this.publicKeyAlgorithmParameters == null) {
                tbsCertificateElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.publicKeyAlgorithmOID) }), this.encodedPublicKey }));
            }
            else {
                tbsCertificateElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.publicKeyAlgorithmOID), this.publicKeyAlgorithmParameters }), this.encodedPublicKey }));
            }
            if (this.issuerUniqueID != null) {
                tbsCertificateElements.add(new ASN1BitString((byte)(-127), this.issuerUniqueID.getBits()));
            }
            if (this.subjectUniqueID != null) {
                tbsCertificateElements.add(new ASN1BitString((byte)(-126), this.subjectUniqueID.getBits()));
            }
            if (!this.extensions.isEmpty()) {
                final ArrayList<ASN1Element> extensionElements = new ArrayList<ASN1Element>(this.extensions.size());
                for (final X509CertificateExtension e : this.extensions) {
                    extensionElements.add(e.encode());
                }
                tbsCertificateElements.add(new ASN1Element((byte)(-93), new ASN1Sequence(extensionElements).encode()));
            }
            final ArrayList<ASN1Element> certificateElements = new ArrayList<ASN1Element>(3);
            certificateElements.add(new ASN1Sequence(tbsCertificateElements));
            if (this.signatureAlgorithmParameters == null) {
                certificateElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.signatureAlgorithmOID) }));
            }
            else {
                certificateElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.signatureAlgorithmOID), this.signatureAlgorithmParameters }));
            }
            certificateElements.add(this.signatureValue);
            return new ASN1Sequence(certificateElements);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CERT_ENCODE_ERROR.get(this.toString(), StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    static ASN1Element encodeName(final DN dn) throws CertException {
        Schema schema;
        try {
            schema = Schema.getDefaultStandardSchema();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_ENCODE_NAME_CANNOT_GET_SCHEMA.get(String.valueOf(dn), StaticUtils.getExceptionMessage(e)), e);
        }
        final RDN[] rdns = dn.getRDNs();
        final ArrayList<ASN1Element> rdnSequenceElements = new ArrayList<ASN1Element>(rdns.length);
        for (int i = rdns.length - 1; i >= 0; --i) {
            final RDN rdn = rdns[i];
            final String[] names = rdn.getAttributeNames();
            final String[] values = rdn.getAttributeValues();
            final ArrayList<ASN1Element> rdnElements = new ArrayList<ASN1Element>(names.length);
            for (int j = 0; j < names.length; ++j) {
                final AttributeTypeDefinition at = schema.getAttributeType(names[j]);
                if (at == null) {
                    throw new CertException(CertMessages.ERR_CERT_ENCODE_NAME_UNKNOWN_ATTR_TYPE.get(String.valueOf(dn), names[j]));
                }
                try {
                    rdnElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(at.getOID()), new ASN1UTF8String(values[j]) }));
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new CertException(CertMessages.ERR_CERT_ENCODE_NAME_ERROR.get(String.valueOf(dn), StaticUtils.getExceptionMessage(e2)), e2);
                }
            }
            rdnSequenceElements.add(new ASN1Set(rdnElements));
        }
        return new ASN1Sequence(rdnSequenceElements);
    }
    
    static ASN1Sequence encodeValiditySequence(final long notBefore, final long notAfter) {
        final GregorianCalendar notBeforeCalendar = new GregorianCalendar();
        notBeforeCalendar.setTimeInMillis(notBefore);
        final int notBeforeYear = notBeforeCalendar.get(1);
        final GregorianCalendar notAfterCalendar = new GregorianCalendar();
        notAfterCalendar.setTimeInMillis(notAfter);
        final int notAfterYear = notAfterCalendar.get(1);
        if (notBeforeYear >= 1950 && notBeforeYear <= 2049 && notAfterYear >= 1950 && notAfterYear <= 2049) {
            return new ASN1Sequence(new ASN1Element[] { new ASN1UTCTime(notBefore), new ASN1UTCTime(notAfter) });
        }
        return new ASN1Sequence(new ASN1Element[] { new ASN1GeneralizedTime(notBefore), new ASN1GeneralizedTime(notAfter) });
    }
    
    public static ObjectPair<X509Certificate, KeyPair> generateSelfSignedCertificate(final SignatureAlgorithmIdentifier signatureAlgorithm, final PublicKeyAlgorithmIdentifier publicKeyAlgorithm, final int keySizeBits, final DN subjectDN, final long notBefore, final long notAfter, final X509CertificateExtension... extensions) throws CertException {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(publicKeyAlgorithm.getName());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_GEN_SELF_SIGNED_CANNOT_GET_KEY_GENERATOR.get(publicKeyAlgorithm.getName(), StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            keyPairGenerator.initialize(keySizeBits);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_GEN_SELF_SIGNED_INVALID_KEY_SIZE.get(keySizeBits, publicKeyAlgorithm.getName(), StaticUtils.getExceptionMessage(e)), e);
        }
        KeyPair keyPair;
        try {
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CERT_GEN_SELF_SIGNED_CANNOT_GENERATE_KEY_PAIR.get(keySizeBits, publicKeyAlgorithm.getName(), StaticUtils.getExceptionMessage(e2)), e2);
        }
        final X509Certificate certificate = generateSelfSignedCertificate(signatureAlgorithm, keyPair, subjectDN, notBefore, notAfter, extensions);
        return new ObjectPair<X509Certificate, KeyPair>(certificate, keyPair);
    }
    
    public static X509Certificate generateSelfSignedCertificate(final SignatureAlgorithmIdentifier signatureAlgorithm, final KeyPair keyPair, final DN subjectDN, final long notBefore, final long notAfter, final X509CertificateExtension... extensions) throws CertException {
        DecodedPublicKey decodedPublicKey = null;
        OID publicKeyAlgorithmOID;
        ASN1Element publicKeyAlgorithmParameters;
        ASN1BitString encodedPublicKey;
        byte[] subjectKeyIdentifier;
        try {
            final ASN1Element[] pkElements = ASN1Sequence.decodeAsSequence(keyPair.getPublic().getEncoded()).elements();
            final ASN1Element[] pkAlgIDElements = ASN1Sequence.decodeAsSequence(pkElements[0]).elements();
            publicKeyAlgorithmOID = pkAlgIDElements[0].decodeAsObjectIdentifier().getOID();
            if (pkAlgIDElements.length == 1) {
                publicKeyAlgorithmParameters = null;
            }
            else {
                publicKeyAlgorithmParameters = pkAlgIDElements[1];
            }
            encodedPublicKey = pkElements[1].decodeAsBitString();
            try {
                if (publicKeyAlgorithmOID.equals(PublicKeyAlgorithmIdentifier.RSA.getOID())) {
                    decodedPublicKey = new RSAPublicKey(encodedPublicKey);
                }
                else if (publicKeyAlgorithmOID.equals(PublicKeyAlgorithmIdentifier.EC.getOID())) {
                    decodedPublicKey = new EllipticCurvePublicKey(encodedPublicKey);
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-1");
            subjectKeyIdentifier = sha256.digest(encodedPublicKey.getBytes());
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CERT_GEN_SELF_SIGNED_CANNOT_PARSE_KEY_PAIR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        final ArrayList<X509CertificateExtension> extensionList = new ArrayList<X509CertificateExtension>(10);
        extensionList.add(new SubjectKeyIdentifierExtension(false, new ASN1OctetString(subjectKeyIdentifier)));
        if (extensions != null) {
            for (final X509CertificateExtension e3 : extensions) {
                if (!e3.getOID().equals(SubjectKeyIdentifierExtension.SUBJECT_KEY_IDENTIFIER_OID)) {
                    extensionList.add(e3);
                }
            }
        }
        final X509CertificateExtension[] allExtensions = new X509CertificateExtension[extensionList.size()];
        extensionList.toArray(allExtensions);
        final BigInteger serialNumber = generateSerialNumber();
        final ASN1BitString encodedSignature = generateSignature(signatureAlgorithm, keyPair.getPrivate(), serialNumber, subjectDN, notBefore, notAfter, subjectDN, publicKeyAlgorithmOID, publicKeyAlgorithmParameters, encodedPublicKey, allExtensions);
        return new X509Certificate(X509CertificateVersion.V3, serialNumber, signatureAlgorithm.getOID(), null, encodedSignature, subjectDN, notBefore, notAfter, subjectDN, publicKeyAlgorithmOID, publicKeyAlgorithmParameters, encodedPublicKey, decodedPublicKey, null, null, allExtensions);
    }
    
    public static X509Certificate generateIssuerSignedCertificate(final SignatureAlgorithmIdentifier signatureAlgorithm, final X509Certificate issuerCertificate, final PrivateKey issuerPrivateKey, final OID publicKeyAlgorithmOID, final ASN1Element publicKeyAlgorithmParameters, final ASN1BitString encodedPublicKey, final DecodedPublicKey decodedPublicKey, final DN subjectDN, final long notBefore, final long notAfter, final X509CertificateExtension... extensions) throws CertException {
        byte[] subjectKeyIdentifier;
        try {
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-1");
            subjectKeyIdentifier = sha256.digest(encodedPublicKey.getBytes());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_GEN_ISSUER_SIGNED_CANNOT_GENERATE_KEY_ID.get(StaticUtils.getExceptionMessage(e)), e);
        }
        ASN1OctetString authorityKeyIdentifier = null;
        for (final X509CertificateExtension e2 : issuerCertificate.extensions) {
            if (e2 instanceof SubjectKeyIdentifierExtension) {
                authorityKeyIdentifier = ((SubjectKeyIdentifierExtension)e2).getKeyIdentifier();
            }
        }
        final ArrayList<X509CertificateExtension> extensionList = new ArrayList<X509CertificateExtension>(10);
        extensionList.add(new SubjectKeyIdentifierExtension(false, new ASN1OctetString(subjectKeyIdentifier)));
        if (authorityKeyIdentifier == null) {
            extensionList.add(new AuthorityKeyIdentifierExtension(false, null, new GeneralNamesBuilder().addDirectoryName(issuerCertificate.subjectDN).build(), issuerCertificate.serialNumber));
        }
        else {
            extensionList.add(new AuthorityKeyIdentifierExtension(false, authorityKeyIdentifier, null, null));
        }
        if (extensions != null) {
            for (final X509CertificateExtension e3 : extensions) {
                if (!e3.getOID().equals(SubjectKeyIdentifierExtension.SUBJECT_KEY_IDENTIFIER_OID)) {
                    if (!e3.getOID().equals(AuthorityKeyIdentifierExtension.AUTHORITY_KEY_IDENTIFIER_OID)) {
                        extensionList.add(e3);
                    }
                }
            }
        }
        final X509CertificateExtension[] allExtensions = new X509CertificateExtension[extensionList.size()];
        extensionList.toArray(allExtensions);
        final BigInteger serialNumber = generateSerialNumber();
        final ASN1BitString encodedSignature = generateSignature(signatureAlgorithm, issuerPrivateKey, serialNumber, issuerCertificate.subjectDN, notBefore, notAfter, subjectDN, publicKeyAlgorithmOID, publicKeyAlgorithmParameters, encodedPublicKey, allExtensions);
        return new X509Certificate(X509CertificateVersion.V3, serialNumber, signatureAlgorithm.getOID(), null, encodedSignature, issuerCertificate.subjectDN, notBefore, notAfter, subjectDN, publicKeyAlgorithmOID, publicKeyAlgorithmParameters, encodedPublicKey, decodedPublicKey, null, null, allExtensions);
    }
    
    private static BigInteger generateSerialNumber() {
        final UUID uuid = UUID.randomUUID();
        final long msb = uuid.getMostSignificantBits() & Long.MAX_VALUE;
        final long lsb = uuid.getLeastSignificantBits() & Long.MAX_VALUE;
        return BigInteger.valueOf(msb).shiftLeft(64).add(BigInteger.valueOf(lsb));
    }
    
    private static ASN1BitString generateSignature(final SignatureAlgorithmIdentifier signatureAlgorithm, final PrivateKey privateKey, final BigInteger serialNumber, final DN issuerDN, final long notBefore, final long notAfter, final DN subjectDN, final OID publicKeyAlgorithmOID, final ASN1Element publicKeyAlgorithmParameters, final ASN1BitString encodedPublicKey, final X509CertificateExtension... extensions) throws CertException {
        Signature signature;
        try {
            signature = Signature.getInstance(signatureAlgorithm.getJavaName());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_GEN_SIGNATURE_CANNOT_GET_SIGNATURE_GENERATOR.get(signatureAlgorithm.getJavaName(), StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            signature.initSign(privateKey);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_GEN_SIGNATURE_CANNOT_INIT_SIGNATURE_GENERATOR.get(signatureAlgorithm.getJavaName(), StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            final ArrayList<ASN1Element> tbsCertificateElements = new ArrayList<ASN1Element>(8);
            tbsCertificateElements.add(new ASN1Element((byte)(-96), new ASN1Integer(X509CertificateVersion.V3.getIntValue()).encode()));
            tbsCertificateElements.add(new ASN1BigInteger(serialNumber));
            tbsCertificateElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(signatureAlgorithm.getOID()) }));
            tbsCertificateElements.add(encodeName(issuerDN));
            tbsCertificateElements.add(encodeValiditySequence(notBefore, notAfter));
            tbsCertificateElements.add(encodeName(subjectDN));
            if (publicKeyAlgorithmParameters == null) {
                tbsCertificateElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(publicKeyAlgorithmOID) }), encodedPublicKey }));
            }
            else {
                tbsCertificateElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(publicKeyAlgorithmOID), publicKeyAlgorithmParameters }), encodedPublicKey }));
            }
            final ArrayList<ASN1Element> extensionElements = new ArrayList<ASN1Element>(extensions.length);
            for (final X509CertificateExtension e2 : extensions) {
                extensionElements.add(e2.encode());
            }
            tbsCertificateElements.add(new ASN1Element((byte)(-93), new ASN1Sequence(extensionElements).encode()));
            final byte[] tbsCertificateBytes = new ASN1Sequence(tbsCertificateElements).encode();
            signature.update(tbsCertificateBytes);
            final byte[] signatureBytes = signature.sign();
            return new ASN1BitString(ASN1BitString.getBitsForBytes(signatureBytes));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_GEN_SIGNATURE_CANNOT_COMPUTE.get(signatureAlgorithm.getJavaName(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public byte[] getX509CertificateBytes() {
        return this.x509CertificateBytes;
    }
    
    public X509CertificateVersion getVersion() {
        return this.version;
    }
    
    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }
    
    public OID getSignatureAlgorithmOID() {
        return this.signatureAlgorithmOID;
    }
    
    public String getSignatureAlgorithmName() {
        return this.signatureAlgorithmName;
    }
    
    public String getSignatureAlgorithmNameOrOID() {
        if (this.signatureAlgorithmName != null) {
            return this.signatureAlgorithmName;
        }
        return this.signatureAlgorithmOID.toString();
    }
    
    public ASN1Element getSignatureAlgorithmParameters() {
        return this.signatureAlgorithmParameters;
    }
    
    public DN getIssuerDN() {
        return this.issuerDN;
    }
    
    public long getNotBeforeTime() {
        return this.notBefore;
    }
    
    public Date getNotBeforeDate() {
        return new Date(this.notBefore);
    }
    
    public long getNotAfterTime() {
        return this.notAfter;
    }
    
    public Date getNotAfterDate() {
        return new Date(this.notAfter);
    }
    
    public boolean isWithinValidityWindow() {
        return this.isWithinValidityWindow(System.currentTimeMillis());
    }
    
    public boolean isWithinValidityWindow(final Date date) {
        return this.isWithinValidityWindow(date.getTime());
    }
    
    public boolean isWithinValidityWindow(final long time) {
        return time >= this.notBefore && time <= this.notAfter;
    }
    
    public DN getSubjectDN() {
        return this.subjectDN;
    }
    
    public OID getPublicKeyAlgorithmOID() {
        return this.publicKeyAlgorithmOID;
    }
    
    public String getPublicKeyAlgorithmName() {
        return this.publicKeyAlgorithmName;
    }
    
    public String getPublicKeyAlgorithmNameOrOID() {
        if (this.publicKeyAlgorithmName != null) {
            return this.publicKeyAlgorithmName;
        }
        return this.publicKeyAlgorithmOID.toString();
    }
    
    public ASN1Element getPublicKeyAlgorithmParameters() {
        return this.publicKeyAlgorithmParameters;
    }
    
    public ASN1BitString getEncodedPublicKey() {
        return this.encodedPublicKey;
    }
    
    public DecodedPublicKey getDecodedPublicKey() {
        return this.decodedPublicKey;
    }
    
    public ASN1BitString getIssuerUniqueID() {
        return this.issuerUniqueID;
    }
    
    public ASN1BitString getSubjectUniqueID() {
        return this.subjectUniqueID;
    }
    
    public List<X509CertificateExtension> getExtensions() {
        return this.extensions;
    }
    
    public ASN1BitString getSignatureValue() {
        return this.signatureValue;
    }
    
    public void verifySignature(final X509Certificate issuerCertificate) throws CertException {
        X509Certificate issuer;
        if (issuerCertificate == null) {
            if (!this.isSelfSigned()) {
                throw new CertException(CertMessages.ERR_CERT_VERIFY_SIGNATURE_ISSUER_CERT_NOT_PROVIDED.get());
            }
            issuer = this;
        }
        else {
            issuer = issuerCertificate;
        }
        PublicKey publicKey;
        try {
            publicKey = issuer.toCertificate().getPublicKey();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_VERIFY_SIGNATURE_CANNOT_GET_PUBLIC_KEY.get(StaticUtils.getExceptionMessage(e)), e);
        }
        SignatureAlgorithmIdentifier signatureAlgorithm;
        Signature signature;
        try {
            signatureAlgorithm = SignatureAlgorithmIdentifier.forOID(this.signatureAlgorithmOID);
            signature = Signature.getInstance(signatureAlgorithm.getJavaName());
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CERT_VERIFY_SIGNATURE_CANNOT_GET_SIGNATURE_VERIFIER.get(this.getSignatureAlgorithmNameOrOID(), StaticUtils.getExceptionMessage(e2)), e2);
        }
        try {
            signature.initVerify(publicKey);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CERT_VERIFY_SIGNATURE_CANNOT_INIT_SIGNATURE_VERIFIER.get(signatureAlgorithm.getJavaName(), StaticUtils.getExceptionMessage(e2)), e2);
        }
        try {
            final ASN1Element[] x509CertificateElements = ASN1Sequence.decodeAsSequence(this.x509CertificateBytes).elements();
            final byte[] tbsCertificateBytes = x509CertificateElements[0].encode();
            signature.update(tbsCertificateBytes);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CERT_GEN_SIGNATURE_CANNOT_COMPUTE.get(signatureAlgorithm.getJavaName(), StaticUtils.getExceptionMessage(e2)), e2);
        }
        try {
            if (!signature.verify(this.signatureValue.getBytes())) {
                throw new CertException(CertMessages.ERR_CERT_VERIFY_SIGNATURE_NOT_VALID.get(this.subjectDN));
            }
        }
        catch (final CertException ce) {
            Debug.debugException(ce);
            throw ce;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CERT_VERIFY_SIGNATURE_ERROR.get(this.subjectDN, StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public byte[] getSHA1Fingerprint() throws CertException {
        return this.getFingerprint("SHA-1");
    }
    
    public byte[] getSHA256Fingerprint() throws CertException {
        return this.getFingerprint("SHA-256");
    }
    
    private byte[] getFingerprint(final String digestAlgorithm) throws CertException {
        try {
            final MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);
            return digest.digest(this.x509CertificateBytes);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CERT_CANNOT_COMPUTE_FINGERPRINT.get(digestAlgorithm, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public boolean isSelfSigned() {
        AuthorityKeyIdentifierExtension akie = null;
        SubjectKeyIdentifierExtension skie = null;
        for (final X509CertificateExtension e : this.extensions) {
            if (e instanceof AuthorityKeyIdentifierExtension) {
                akie = (AuthorityKeyIdentifierExtension)e;
            }
            else {
                if (!(e instanceof SubjectKeyIdentifierExtension)) {
                    continue;
                }
                skie = (SubjectKeyIdentifierExtension)e;
            }
        }
        if (akie != null && skie != null) {
            return akie.getKeyIdentifier() != null && Arrays.equals(akie.getKeyIdentifier().getValue(), skie.getKeyIdentifier().getValue());
        }
        return this.subjectDN.equals(this.issuerDN);
    }
    
    public boolean isIssuerFor(final X509Certificate c) {
        return this.isIssuerFor(c, null);
    }
    
    public boolean isIssuerFor(final X509Certificate c, final StringBuilder nonMatchReason) {
        if (!c.issuerDN.equals(this.subjectDN)) {
            if (nonMatchReason != null) {
                nonMatchReason.append(CertMessages.INFO_CERT_IS_ISSUER_FOR_DN_MISMATCH.get(this.subjectDN, c.subjectDN, this.issuerDN));
            }
            return false;
        }
        byte[] authorityKeyIdentifier = null;
        for (final X509CertificateExtension extension : c.extensions) {
            if (extension instanceof AuthorityKeyIdentifierExtension) {
                final AuthorityKeyIdentifierExtension akie = (AuthorityKeyIdentifierExtension)extension;
                if (akie.getKeyIdentifier() != null) {
                    authorityKeyIdentifier = akie.getKeyIdentifier().getValue();
                    break;
                }
                continue;
            }
        }
        if (authorityKeyIdentifier != null) {
            boolean matchFound = false;
            for (final X509CertificateExtension extension2 : this.extensions) {
                if (extension2 instanceof SubjectKeyIdentifierExtension) {
                    final SubjectKeyIdentifierExtension skie = (SubjectKeyIdentifierExtension)extension2;
                    matchFound = Arrays.equals(authorityKeyIdentifier, skie.getKeyIdentifier().getValue());
                    break;
                }
            }
            if (!matchFound) {
                if (nonMatchReason != null) {
                    nonMatchReason.append(CertMessages.INFO_CERT_IS_ISSUER_FOR_KEY_ID_MISMATCH.get(this.subjectDN, c.subjectDN));
                }
                return false;
            }
        }
        return true;
    }
    
    public Certificate toCertificate() throws CertificateException {
        return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(this.x509CertificateBytes));
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("X509Certificate(version='");
        buffer.append(this.version.getName());
        buffer.append("', serialNumber='");
        StaticUtils.toHex(this.serialNumber.toByteArray(), ":", buffer);
        buffer.append("', signatureAlgorithmOID='");
        buffer.append(this.signatureAlgorithmOID.toString());
        buffer.append('\'');
        if (this.signatureAlgorithmName != null) {
            buffer.append(", signatureAlgorithmName='");
            buffer.append(this.signatureAlgorithmName);
            buffer.append('\'');
        }
        buffer.append(", issuerDN='");
        buffer.append(this.issuerDN.toString());
        buffer.append("', notBefore='");
        buffer.append(StaticUtils.encodeGeneralizedTime(this.notBefore));
        buffer.append("', notAfter='");
        buffer.append(StaticUtils.encodeGeneralizedTime(this.notAfter));
        buffer.append("', subjectDN='");
        buffer.append(this.subjectDN.toString());
        buffer.append("', publicKeyAlgorithmOID='");
        buffer.append(this.publicKeyAlgorithmOID.toString());
        buffer.append('\'');
        if (this.publicKeyAlgorithmName != null) {
            buffer.append(", publicKeyAlgorithmName='");
            buffer.append(this.publicKeyAlgorithmName);
            buffer.append('\'');
        }
        buffer.append(", subjectPublicKey=");
        if (this.decodedPublicKey == null) {
            buffer.append('\'');
            try {
                StaticUtils.toHex(this.encodedPublicKey.getBytes(), ":", buffer);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.encodedPublicKey.toString(buffer);
            }
            buffer.append('\'');
        }
        else {
            this.decodedPublicKey.toString(buffer);
            if (this.decodedPublicKey instanceof EllipticCurvePublicKey) {
                try {
                    final OID namedCurveOID = this.publicKeyAlgorithmParameters.decodeAsObjectIdentifier().getOID();
                    buffer.append(", ellipticCurvePublicKeyParameters=namedCurve='");
                    buffer.append(NamedCurve.getNameOrOID(namedCurveOID));
                    buffer.append('\'');
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
        }
        if (this.issuerUniqueID != null) {
            buffer.append(", issuerUniqueID='");
            buffer.append(this.issuerUniqueID.toString());
            buffer.append('\'');
        }
        if (this.subjectUniqueID != null) {
            buffer.append(", subjectUniqueID='");
            buffer.append(this.subjectUniqueID.toString());
            buffer.append('\'');
        }
        if (!this.extensions.isEmpty()) {
            buffer.append(", extensions={");
            final Iterator<X509CertificateExtension> iterator = this.extensions.iterator();
            while (iterator.hasNext()) {
                iterator.next().toString(buffer);
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(", signatureValue='");
        try {
            StaticUtils.toHex(this.signatureValue.getBytes(), ":", buffer);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            buffer.append(this.signatureValue.toString());
        }
        buffer.append("')");
    }
    
    public List<String> toPEM() {
        final ArrayList<String> lines = new ArrayList<String>(10);
        lines.add("-----BEGIN CERTIFICATE-----");
        final String certBase64 = Base64.encode(this.x509CertificateBytes);
        lines.addAll(StaticUtils.wrapLine(certBase64, 64));
        lines.add("-----END CERTIFICATE-----");
        return Collections.unmodifiableList((List<? extends String>)lines);
    }
    
    public String toPEMString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("-----BEGIN CERTIFICATE-----");
        buffer.append(StaticUtils.EOL);
        final String certBase64 = Base64.encode(this.x509CertificateBytes);
        for (final String line : StaticUtils.wrapLine(certBase64, 64)) {
            buffer.append(line);
            buffer.append(StaticUtils.EOL);
        }
        buffer.append("-----END CERTIFICATE-----");
        buffer.append(StaticUtils.EOL);
        return buffer.toString();
    }
}
