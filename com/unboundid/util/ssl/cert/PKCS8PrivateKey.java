package com.unboundid.util.ssl.cert;

import java.util.Iterator;
import java.util.Collections;
import com.unboundid.util.Base64;
import java.util.List;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.util.Collection;
import com.unboundid.asn1.ASN1ObjectIdentifier;
import com.unboundid.asn1.ASN1Integer;
import java.util.ArrayList;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.OID;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1BitString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PKCS8PrivateKey implements Serializable
{
    private static final byte TYPE_ATTRIBUTES = -96;
    private static final byte TYPE_PUBLIC_KEY = -127;
    private static final long serialVersionUID = -5551171525811450486L;
    private final ASN1BitString publicKey;
    private final ASN1Element attributesElement;
    private final ASN1Element privateKeyAlgorithmParameters;
    private final ASN1OctetString encodedPrivateKey;
    private final byte[] pkcs8PrivateKeyBytes;
    private final DecodedPrivateKey decodedPrivateKey;
    private final OID privateKeyAlgorithmOID;
    private final PKCS8PrivateKeyVersion version;
    private final String privateKeyAlgorithmName;
    
    PKCS8PrivateKey(final PKCS8PrivateKeyVersion version, final OID privateKeyAlgorithmOID, final ASN1Element privateKeyAlgorithmParameters, final ASN1OctetString encodedPrivateKey, final DecodedPrivateKey decodedPrivateKey, final ASN1Element attributesElement, final ASN1BitString publicKey) throws CertException {
        this.version = version;
        this.privateKeyAlgorithmOID = privateKeyAlgorithmOID;
        this.privateKeyAlgorithmParameters = privateKeyAlgorithmParameters;
        this.encodedPrivateKey = encodedPrivateKey;
        this.decodedPrivateKey = decodedPrivateKey;
        this.attributesElement = attributesElement;
        this.publicKey = publicKey;
        final PublicKeyAlgorithmIdentifier identifier = PublicKeyAlgorithmIdentifier.forOID(privateKeyAlgorithmOID);
        if (identifier == null) {
            this.privateKeyAlgorithmName = null;
        }
        else {
            this.privateKeyAlgorithmName = identifier.getName();
        }
        this.pkcs8PrivateKeyBytes = this.encode().encode();
    }
    
    public PKCS8PrivateKey(final byte[] privateKeyBytes) throws CertException {
        this.pkcs8PrivateKeyBytes = privateKeyBytes;
        ASN1Element[] privateKeyElements;
        try {
            privateKeyElements = ASN1Sequence.decodeAsSequence(privateKeyBytes).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_PRIVATE_KEY_DECODE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        if (privateKeyElements.length < 3) {
            throw new CertException(CertMessages.ERR_PRIVATE_KEY_DECODE_NOT_ENOUGH_ELEMENTS.get(privateKeyElements.length));
        }
        try {
            final int versionIntValue = privateKeyElements[0].decodeAsInteger().intValue();
            this.version = PKCS8PrivateKeyVersion.valueOf(versionIntValue);
            if (this.version == null) {
                throw new CertException(CertMessages.ERR_PRIVATE_KEY_DECODE_UNSUPPORTED_VERSION.get(versionIntValue));
            }
        }
        catch (final CertException e2) {
            Debug.debugException(e2);
            throw e2;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_PRIVATE_KEY_DECODE_CANNOT_PARSE_VERSION.get(StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            final ASN1Element[] privateKeyAlgorithmElements = privateKeyElements[1].decodeAsSequence().elements();
            this.privateKeyAlgorithmOID = privateKeyAlgorithmElements[0].decodeAsObjectIdentifier().getOID();
            if (privateKeyAlgorithmElements.length > 1) {
                this.privateKeyAlgorithmParameters = privateKeyAlgorithmElements[1];
            }
            else {
                this.privateKeyAlgorithmParameters = null;
            }
            this.encodedPrivateKey = privateKeyElements[2].decodeAsOctetString();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_PRIVATE_KEY_DECODE_CANNOT_PARSE_ALGORITHM.get(StaticUtils.getExceptionMessage(e)), e);
        }
        final PublicKeyAlgorithmIdentifier privateKeyAlgorithmIdentifier = PublicKeyAlgorithmIdentifier.forOID(this.privateKeyAlgorithmOID);
        if (privateKeyAlgorithmIdentifier == null) {
            this.privateKeyAlgorithmName = null;
            this.decodedPrivateKey = null;
        }
        else {
            this.privateKeyAlgorithmName = privateKeyAlgorithmIdentifier.getName();
            DecodedPrivateKey pk = null;
            switch (privateKeyAlgorithmIdentifier) {
                case RSA: {
                    try {
                        pk = new RSAPrivateKey(this.encodedPrivateKey);
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                    }
                    break;
                }
                case EC: {
                    try {
                        pk = new EllipticCurvePrivateKey(this.encodedPrivateKey);
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                    }
                    break;
                }
            }
            this.decodedPrivateKey = pk;
        }
        ASN1BitString pk2 = null;
        ASN1Element attrsElement = null;
        for (int i = 3; i < privateKeyElements.length; ++i) {
            final ASN1Element element = privateKeyElements[i];
            switch (element.getType()) {
                case -96: {
                    attrsElement = element;
                    break;
                }
                case -127: {
                    try {
                        pk2 = ASN1BitString.decodeAsBitString(element);
                    }
                    catch (final Exception e4) {
                        Debug.debugException(e4);
                        throw new CertException(CertMessages.ERR_PRIVATE_KEY_DECODE_CANNOT_PARSE_PUBLIC_KEY.get(StaticUtils.getExceptionMessage(e4)), e4);
                    }
                    break;
                }
            }
        }
        this.attributesElement = attrsElement;
        this.publicKey = pk2;
    }
    
    static byte[] wrapRSAPrivateKey(final byte[] rsaPrivateKeyBytes) throws CertException {
        try {
            final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(5);
            elements.add(new ASN1Integer(PKCS8PrivateKeyVersion.V1.getIntValue()));
            elements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(PublicKeyAlgorithmIdentifier.RSA.getOID()) }));
            elements.add(new ASN1OctetString(rsaPrivateKeyBytes));
            return new ASN1Sequence(elements).encode();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_PRIVATE_KEY_WRAP_RSA_KEY_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    ASN1Element encode() throws CertException {
        try {
            final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(5);
            elements.add(new ASN1Integer(this.version.getIntValue()));
            if (this.privateKeyAlgorithmParameters == null) {
                elements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.privateKeyAlgorithmOID) }));
            }
            else {
                elements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(this.privateKeyAlgorithmOID), this.privateKeyAlgorithmParameters }));
            }
            elements.add(this.encodedPrivateKey);
            if (this.attributesElement != null) {
                elements.add(new ASN1Element((byte)(-96), this.attributesElement.getValue()));
            }
            if (this.publicKey != null) {
                elements.add(new ASN1BitString((byte)(-127), this.publicKey.getBits()));
            }
            return new ASN1Sequence(elements);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_PRIVATE_KEY_ENCODE_ERROR.get(this.toString(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public byte[] getPKCS8PrivateKeyBytes() {
        return this.pkcs8PrivateKeyBytes;
    }
    
    public PKCS8PrivateKeyVersion getVersion() {
        return this.version;
    }
    
    public OID getPrivateKeyAlgorithmOID() {
        return this.privateKeyAlgorithmOID;
    }
    
    public String getPrivateKeyAlgorithmName() {
        return this.privateKeyAlgorithmName;
    }
    
    public String getPrivateKeyAlgorithmNameOrOID() {
        if (this.privateKeyAlgorithmName == null) {
            return this.privateKeyAlgorithmOID.toString();
        }
        return this.privateKeyAlgorithmName;
    }
    
    public ASN1Element getPrivateKeyAlgorithmParameters() {
        return this.privateKeyAlgorithmParameters;
    }
    
    public ASN1OctetString getEncodedPrivateKey() {
        return this.encodedPrivateKey;
    }
    
    public DecodedPrivateKey getDecodedPrivateKey() {
        return this.decodedPrivateKey;
    }
    
    public ASN1Element getAttributesElement() {
        return this.attributesElement;
    }
    
    public ASN1BitString getPublicKey() {
        return this.publicKey;
    }
    
    public PrivateKey toPrivateKey() throws GeneralSecurityException {
        final KeyFactory keyFactory = KeyFactory.getInstance(this.getPrivateKeyAlgorithmNameOrOID());
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(this.pkcs8PrivateKeyBytes));
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("PKCS8PrivateKey(version='");
        buffer.append(this.version.getName());
        buffer.append("', privateKeyAlgorithmOID=");
        buffer.append(this.privateKeyAlgorithmOID.toString());
        buffer.append('\'');
        if (this.privateKeyAlgorithmName != null) {
            buffer.append(", privateKeyAlgorithmName='");
            buffer.append(this.privateKeyAlgorithmName);
            buffer.append('\'');
        }
        if (this.decodedPrivateKey == null) {
            buffer.append(", encodedPrivateKey='");
            StaticUtils.toHex(this.encodedPrivateKey.getValue(), ":", buffer);
            buffer.append('\'');
        }
        else {
            buffer.append(", decodedPrivateKey=");
            this.decodedPrivateKey.toString(buffer);
            if (this.decodedPrivateKey instanceof EllipticCurvePrivateKey) {
                try {
                    final OID namedCurveOID = this.privateKeyAlgorithmParameters.decodeAsObjectIdentifier().getOID();
                    buffer.append(", ellipticCurvePrivateKeyParameters=namedCurve='");
                    buffer.append(NamedCurve.getNameOrOID(namedCurveOID));
                    buffer.append('\'');
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
        }
        buffer.append("')");
    }
    
    public List<String> toPEM() {
        final ArrayList<String> lines = new ArrayList<String>(10);
        lines.add("-----BEGIN PRIVATE KEY-----");
        final String keyBase64 = Base64.encode(this.pkcs8PrivateKeyBytes);
        lines.addAll(StaticUtils.wrapLine(keyBase64, 64));
        lines.add("-----END PRIVATE KEY-----");
        return Collections.unmodifiableList((List<? extends String>)lines);
    }
    
    public String toPEMString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("-----BEGIN PRIVATE KEY-----");
        buffer.append(StaticUtils.EOL);
        final String keyBase64 = Base64.encode(this.pkcs8PrivateKeyBytes);
        for (final String line : StaticUtils.wrapLine(keyBase64, 64)) {
            buffer.append(line);
            buffer.append(StaticUtils.EOL);
        }
        buffer.append("-----END PRIVATE KEY-----");
        buffer.append(StaticUtils.EOL);
        return buffer.toString();
    }
}
