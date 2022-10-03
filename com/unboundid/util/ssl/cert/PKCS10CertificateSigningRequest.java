package com.unboundid.util.ssl.cert;

import com.unboundid.util.Base64;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.PrivateKey;
import com.unboundid.asn1.ASN1OctetString;
import java.security.MessageDigest;
import java.security.KeyPair;
import com.unboundid.asn1.ASN1ObjectIdentifier;
import com.unboundid.asn1.ASN1Integer;
import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.Collections;
import com.unboundid.asn1.ASN1Sequence;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.util.ObjectPair;
import java.util.List;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1BitString;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PKCS10CertificateSigningRequest implements Serializable
{
    private static final byte TYPE_ATTRIBUTES = -96;
    private static final OID ATTRIBUTE_OID_EXTENSIONS;
    private static final long serialVersionUID = -1665446530589389194L;
    private final ASN1BitString signatureValue;
    private final ASN1BitString encodedPublicKey;
    private final ASN1Element publicKeyAlgorithmParameters;
    private final ASN1Element signatureAlgorithmParameters;
    private final byte[] pkcs10CertificateSigningRequestBytes;
    private final DecodedPublicKey decodedPublicKey;
    private final DN subjectDN;
    private final List<ObjectPair<OID, ASN1Set>> requestAttributes;
    private final List<X509CertificateExtension> extensions;
    private final OID publicKeyAlgorithmOID;
    private final OID signatureAlgorithmOID;
    private final PKCS10CertificateSigningRequestVersion version;
    private final String publicKeyAlgorithmName;
    private final String signatureAlgorithmName;
    
    PKCS10CertificateSigningRequest(final PKCS10CertificateSigningRequestVersion version, final OID signatureAlgorithmOID, final ASN1Element signatureAlgorithmParameters, final ASN1BitString signatureValue, final DN subjectDN, final OID publicKeyAlgorithmOID, final ASN1Element publicKeyAlgorithmParameters, final ASN1BitString encodedPublicKey, final DecodedPublicKey decodedPublicKey, final List<ObjectPair<OID, ASN1Set>> nonExtensionAttributes, final X509CertificateExtension... extensions) throws CertException {
        this.version = version;
        this.signatureAlgorithmOID = signatureAlgorithmOID;
        this.signatureAlgorithmParameters = signatureAlgorithmParameters;
        this.signatureValue = signatureValue;
        this.subjectDN = subjectDN;
        this.publicKeyAlgorithmOID = publicKeyAlgorithmOID;
        this.publicKeyAlgorithmParameters = publicKeyAlgorithmParameters;
        this.encodedPublicKey = encodedPublicKey;
        this.decodedPublicKey = decodedPublicKey;
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
        final ArrayList<ObjectPair<OID, ASN1Set>> attrs = new ArrayList<ObjectPair<OID, ASN1Set>>(10);
        if (nonExtensionAttributes != null) {
            attrs.addAll(nonExtensionAttributes);
        }
        if (extensions.length > 0) {
            final ArrayList<ASN1Element> extensionElements = new ArrayList<ASN1Element>(extensions.length);
            for (final X509CertificateExtension e : extensions) {
                extensionElements.add(e.encode());
            }
            attrs.add(new ObjectPair<OID, ASN1Set>(PKCS10CertificateSigningRequest.ATTRIBUTE_OID_EXTENSIONS, new ASN1Set(new ASN1Element[] { new ASN1Sequence(extensionElements) })));
        }
        this.requestAttributes = Collections.unmodifiableList((List<? extends ObjectPair<OID, ASN1Set>>)attrs);
        this.pkcs10CertificateSigningRequestBytes = this.encode().encode();
    }
    
    public PKCS10CertificateSigningRequest(final byte[] encodedRequest) throws CertException {
        this.pkcs10CertificateSigningRequestBytes = encodedRequest;
        ASN1Element[] requestElements;
        try {
            requestElements = ASN1Sequence.decodeAsSequence(encodedRequest).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CSR_DECODE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        if (requestElements.length != 3) {
            throw new CertException(CertMessages.ERR_CSR_DECODE_UNEXPECTED_SEQUENCE_ELEMENT_COUNT.get(requestElements.length));
        }
        ASN1Element[] requestInfoElements;
        try {
            requestInfoElements = ASN1Sequence.decodeAsSequence(requestElements[0]).elements();
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CSR_DECODE_FIRST_ELEMENT_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        try {
            final int versionIntValue = requestInfoElements[0].decodeAsInteger().intValue();
            this.version = PKCS10CertificateSigningRequestVersion.valueOf(versionIntValue);
            if (this.version == null) {
                throw new CertException(CertMessages.ERR_CSR_DECODE_UNSUPPORTED_VERSION.get(this.version));
            }
        }
        catch (final CertException e3) {
            Debug.debugException(e3);
            throw e3;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CSR_DECODE_CANNOT_PARSE_VERSION.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        try {
            this.subjectDN = X509Certificate.decodeName(requestInfoElements[1]);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CSR_DECODE_CANNOT_PARSE_SUBJECT_DN.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        try {
            final ASN1Element[] subjectPublicKeyInfoElements = requestInfoElements[2].decodeAsSequence().elements();
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
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CSR_DECODE_CANNOT_PARSE_PUBLIC_KEY_INFO.get(StaticUtils.getExceptionMessage(e2)), e2);
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
                    catch (final Exception e4) {
                        Debug.debugException(e4);
                    }
                    break;
                }
                case EC: {
                    try {
                        pk = new EllipticCurvePublicKey(this.encodedPublicKey);
                    }
                    catch (final Exception e4) {
                        Debug.debugException(e4);
                    }
                    break;
                }
            }
            this.decodedPublicKey = pk;
        }
        final ArrayList<ObjectPair<OID, ASN1Set>> attrList = new ArrayList<ObjectPair<OID, ASN1Set>>(10);
        final ArrayList<X509CertificateExtension> extList = new ArrayList<X509CertificateExtension>(10);
        if (requestInfoElements.length > 3) {
            for (int i = 3; i < requestInfoElements.length; ++i) {
                final ASN1Element element = requestInfoElements[i];
                if (element.getType() == -96) {
                    try {
                        for (final ASN1Element attrSetElement : element.decodeAsSet().elements()) {
                            final ASN1Element[] attrElements = attrSetElement.decodeAsSequence().elements();
                            final OID attrOID = attrElements[0].decodeAsObjectIdentifier().getOID();
                            final ASN1Set attrValues = attrElements[1].decodeAsSet();
                            attrList.add(new ObjectPair<OID, ASN1Set>(attrOID, attrValues));
                        }
                    }
                    catch (final Exception e5) {
                        Debug.debugException(e5);
                        throw new CertException(CertMessages.ERR_CSR_DECODE_CANNOT_PARSE_ATTRS.get(StaticUtils.getExceptionMessage(e5)), e5);
                    }
                    for (final ObjectPair<OID, ASN1Set> p : attrList) {
                        if (p.getFirst().equals(PKCS10CertificateSigningRequest.ATTRIBUTE_OID_EXTENSIONS)) {
                            try {
                                for (final ASN1Element extElement : p.getSecond().elements()[0].decodeAsSequence().elements()) {
                                    extList.add(X509CertificateExtension.decode(extElement));
                                }
                            }
                            catch (final Exception e6) {
                                Debug.debugException(e6);
                                throw new CertException(CertMessages.ERR_CSR_DECODE_CANNOT_PARSE_EXT_ATTR.get(p.getFirst(), StaticUtils.getExceptionMessage(e6)), e6);
                            }
                        }
                    }
                }
            }
        }
        this.requestAttributes = Collections.unmodifiableList((List<? extends ObjectPair<OID, ASN1Set>>)attrList);
        this.extensions = Collections.unmodifiableList((List<? extends X509CertificateExtension>)extList);
        try {
            final ASN1Element[] signatureAlgorithmElements = requestElements[1].decodeAsSequence().elements();
            this.signatureAlgorithmOID = signatureAlgorithmElements[0].decodeAsObjectIdentifier().getOID();
            if (signatureAlgorithmElements.length > 1) {
                this.signatureAlgorithmParameters = signatureAlgorithmElements[1];
            }
            else {
                this.signatureAlgorithmParameters = null;
            }
        }
        catch (final Exception e7) {
            Debug.debugException(e7);
            throw new CertException(CertMessages.ERR_CSR_DECODE_CANNOT_PARSE_SIG_ALG.get(StaticUtils.getExceptionMessage(e7)), e7);
        }
        final SignatureAlgorithmIdentifier signatureAlgorithmIdentifier = SignatureAlgorithmIdentifier.forOID(this.signatureAlgorithmOID);
        if (signatureAlgorithmIdentifier == null) {
            this.signatureAlgorithmName = null;
        }
        else {
            this.signatureAlgorithmName = signatureAlgorithmIdentifier.getUserFriendlyName();
        }
        try {
            this.signatureValue = requestElements[2].decodeAsBitString();
        }
        catch (final Exception e8) {
            Debug.debugException(e8);
            throw new CertException(CertMessages.ERR_CSR_DECODE_CANNOT_PARSE_SIG_VALUE.get(StaticUtils.getExceptionMessage(e8)), e8);
        }
    }
    
    private ASN1Element encode() throws CertException {
        try {
            final ArrayList<ASN1Element> requestInfoElements = new ArrayList<ASN1Element>(4);
            requestInfoElements.add(new ASN1Integer(this.version.getIntValue()));
            requestInfoElements.add(X509Certificate.encodeName(this.subjectDN));
            if (this.publicKeyAlgorithmParameters == null) {
                requestInfoElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.publicKeyAlgorithmOID) }), this.encodedPublicKey }));
            }
            else {
                requestInfoElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.publicKeyAlgorithmOID), this.publicKeyAlgorithmParameters }), this.encodedPublicKey }));
            }
            final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(this.requestAttributes.size());
            for (final ObjectPair<OID, ASN1Set> attr : this.requestAttributes) {
                attrElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(attr.getFirst()), attr.getSecond() }));
            }
            requestInfoElements.add(new ASN1Set((byte)(-96), attrElements));
            final ArrayList<ASN1Element> certificationRequestElements = new ArrayList<ASN1Element>(3);
            certificationRequestElements.add(new ASN1Sequence(requestInfoElements));
            if (this.signatureAlgorithmParameters == null) {
                certificationRequestElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.signatureAlgorithmOID) }));
            }
            else {
                certificationRequestElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.signatureAlgorithmOID), this.signatureAlgorithmParameters }));
            }
            certificationRequestElements.add(this.signatureValue);
            return new ASN1Sequence(certificationRequestElements);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CSR_ENCODE_ERROR.get(this.toString(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public static PKCS10CertificateSigningRequest generateCertificateSigningRequest(final SignatureAlgorithmIdentifier signatureAlgorithm, final KeyPair keyPair, final DN subjectDN, final X509CertificateExtension... extensions) throws CertException {
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
            throw new CertException(CertMessages.ERR_CSR_GEN_CANNOT_PARSE_KEY_PAIR.get(StaticUtils.getExceptionMessage(e2)), e2);
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
        final ASN1BitString encodedSignature = generateSignature(signatureAlgorithm, keyPair.getPrivate(), subjectDN, publicKeyAlgorithmOID, publicKeyAlgorithmParameters, encodedPublicKey, allExtensions);
        return new PKCS10CertificateSigningRequest(PKCS10CertificateSigningRequestVersion.V1, signatureAlgorithm.getOID(), null, encodedSignature, subjectDN, publicKeyAlgorithmOID, publicKeyAlgorithmParameters, encodedPublicKey, decodedPublicKey, null, allExtensions);
    }
    
    private static ASN1BitString generateSignature(final SignatureAlgorithmIdentifier signatureAlgorithm, final PrivateKey privateKey, final DN subjectDN, final OID publicKeyAlgorithmOID, final ASN1Element publicKeyAlgorithmParameters, final ASN1BitString encodedPublicKey, final X509CertificateExtension... extensions) throws CertException {
        Signature signature;
        try {
            signature = Signature.getInstance(signatureAlgorithm.getJavaName());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CSR_GEN_SIGNATURE_CANNOT_GET_SIGNATURE_GENERATOR.get(signatureAlgorithm.getJavaName(), StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            signature.initSign(privateKey);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CSR_GEN_SIGNATURE_CANNOT_INIT_SIGNATURE_GENERATOR.get(signatureAlgorithm.getJavaName(), StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            final ArrayList<ASN1Element> requestInfoElements = new ArrayList<ASN1Element>(4);
            requestInfoElements.add(new ASN1Integer(PKCS10CertificateSigningRequestVersion.V1.getIntValue()));
            requestInfoElements.add(X509Certificate.encodeName(subjectDN));
            if (publicKeyAlgorithmParameters == null) {
                requestInfoElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(publicKeyAlgorithmOID) }), encodedPublicKey }));
            }
            else {
                requestInfoElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(publicKeyAlgorithmOID), publicKeyAlgorithmParameters }), encodedPublicKey }));
            }
            final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(1);
            if (extensions != null && extensions.length > 0) {
                final ArrayList<ASN1Element> extensionElements = new ArrayList<ASN1Element>(extensions.length);
                for (final X509CertificateExtension e2 : extensions) {
                    extensionElements.add(e2.encode());
                }
                attrElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(PKCS10CertificateSigningRequest.ATTRIBUTE_OID_EXTENSIONS), new ASN1Set(new ASN1Element[] { new ASN1Sequence(extensionElements) }) }));
            }
            requestInfoElements.add(new ASN1Set((byte)(-96), attrElements));
            final byte[] certificationRequestInfoBytes = new ASN1Sequence(requestInfoElements).encode();
            signature.update(certificationRequestInfoBytes);
            final byte[] signatureBytes = signature.sign();
            return new ASN1BitString(ASN1BitString.getBitsForBytes(signatureBytes));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CSR_GEN_SIGNATURE_CANNOT_COMPUTE.get(signatureAlgorithm.getJavaName(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public byte[] getPKCS10CertificateSigningRequestBytes() {
        return this.pkcs10CertificateSigningRequestBytes;
    }
    
    public PKCS10CertificateSigningRequestVersion getVersion() {
        return this.version;
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
    
    public List<ObjectPair<OID, ASN1Set>> getRequestAttributes() {
        return this.requestAttributes;
    }
    
    public List<X509CertificateExtension> getExtensions() {
        return this.extensions;
    }
    
    public ASN1BitString getSignatureValue() {
        return this.signatureValue;
    }
    
    public void verifySignature() throws CertException {
        PublicKey publicKey;
        try {
            byte[] encodedPublicKeyBytes;
            if (this.publicKeyAlgorithmParameters == null) {
                encodedPublicKeyBytes = new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.publicKeyAlgorithmOID) }), this.encodedPublicKey }).encode();
            }
            else {
                encodedPublicKeyBytes = new ASN1Sequence(new ASN1Element[] { new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.publicKeyAlgorithmOID), this.publicKeyAlgorithmParameters }), this.encodedPublicKey }).encode();
            }
            final KeyFactory keyFactory = KeyFactory.getInstance(this.getPublicKeyAlgorithmNameOrOID());
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedPublicKeyBytes));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_CSR_VERIFY_SIGNATURE_CANNOT_GET_PUBLIC_KEY.get(StaticUtils.getExceptionMessage(e)), e);
        }
        SignatureAlgorithmIdentifier signatureAlgorithm;
        Signature signature;
        try {
            signatureAlgorithm = SignatureAlgorithmIdentifier.forOID(this.signatureAlgorithmOID);
            signature = Signature.getInstance(signatureAlgorithm.getJavaName());
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CSR_VERIFY_SIGNATURE_CANNOT_GET_SIGNATURE_VERIFIER.get(this.getSignatureAlgorithmNameOrOID(), StaticUtils.getExceptionMessage(e2)), e2);
        }
        try {
            signature.initVerify(publicKey);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CSR_VERIFY_SIGNATURE_CANNOT_INIT_SIGNATURE_VERIFIER.get(signatureAlgorithm.getJavaName(), StaticUtils.getExceptionMessage(e2)), e2);
        }
        boolean signatureIsValid;
        try {
            final ASN1Element[] requestInfoElements = ASN1Sequence.decodeAsSequence(this.pkcs10CertificateSigningRequestBytes).elements();
            final byte[] requestInfoBytes = requestInfoElements[0].encode();
            signature.update(requestInfoBytes);
            signatureIsValid = signature.verify(this.signatureValue.getBytes());
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new CertException(CertMessages.ERR_CSR_VERIFY_SIGNATURE_ERROR.get(this.subjectDN, StaticUtils.getExceptionMessage(e3)), e3);
        }
        if (!signatureIsValid) {
            throw new CertException(CertMessages.ERR_CSR_VERIFY_SIGNATURE_NOT_VALID.get(this.subjectDN));
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("PKCS10CertificateSigningRequest(version='");
        buffer.append(this.version.getName());
        buffer.append("', subjectDN='");
        buffer.append(this.subjectDN);
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
        buffer.append(", signatureAlgorithmOID='");
        buffer.append(this.signatureAlgorithmOID.toString());
        buffer.append('\'');
        if (this.signatureAlgorithmName != null) {
            buffer.append(", signatureAlgorithmName='");
            buffer.append(this.signatureAlgorithmName);
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
        lines.add("-----BEGIN CERTIFICATE REQUEST-----");
        final String csrBase64 = Base64.encode(this.pkcs10CertificateSigningRequestBytes);
        lines.addAll(StaticUtils.wrapLine(csrBase64, 64));
        lines.add("-----END CERTIFICATE REQUEST-----");
        return Collections.unmodifiableList((List<? extends String>)lines);
    }
    
    public String toPEMString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("-----BEGIN CERTIFICATE REQUEST-----");
        buffer.append(StaticUtils.EOL);
        final String csrBase64 = Base64.encode(this.pkcs10CertificateSigningRequestBytes);
        for (final String line : StaticUtils.wrapLine(csrBase64, 64)) {
            buffer.append(line);
            buffer.append(StaticUtils.EOL);
        }
        buffer.append("-----END CERTIFICATE REQUEST-----");
        buffer.append(StaticUtils.EOL);
        return buffer.toString();
    }
    
    static {
        ATTRIBUTE_OID_EXTENSIONS = new OID("1.2.840.113549.1.9.14");
    }
}
