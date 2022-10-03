package com.unboundid.util.ssl.cert;

import java.util.Collection;
import com.unboundid.asn1.ASN1ObjectIdentifier;
import com.unboundid.asn1.ASN1Integer;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.OID;
import com.unboundid.asn1.ASN1BitString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class EllipticCurvePrivateKey extends DecodedPrivateKey
{
    private static final byte TYPE_PARAMETERS = -96;
    private static final byte TYPE_PUBLIC_KEY = -127;
    private static final long serialVersionUID = -7102211426269543850L;
    private final ASN1BitString publicKey;
    private final byte[] privateKeyBytes;
    private final int version;
    private final OID namedCurveOID;
    
    EllipticCurvePrivateKey(final int version, final byte[] privateKeyBytes, final OID namedCurveOID, final ASN1BitString publicKey) {
        this.version = version;
        this.privateKeyBytes = privateKeyBytes;
        this.namedCurveOID = namedCurveOID;
        this.publicKey = publicKey;
    }
    
    EllipticCurvePrivateKey(final ASN1OctetString encodedPrivateKey) throws CertException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(encodedPrivateKey.getValue()).elements();
            this.version = elements[0].decodeAsInteger().intValue();
            if (this.version != 1) {
                throw new CertException(CertMessages.ERR_EC_PRIVATE_KEY_UNSUPPORTED_VERSION.get(this.version));
            }
            this.privateKeyBytes = elements[1].decodeAsOctetString().getValue();
            ASN1BitString pubKey = null;
            OID curveOID = null;
            for (int i = 2; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case -96: {
                        curveOID = elements[i].decodeAsObjectIdentifier().getOID();
                        break;
                    }
                    case -127: {
                        pubKey = elements[i].decodeAsBitString();
                        break;
                    }
                }
            }
            this.namedCurveOID = curveOID;
            this.publicKey = pubKey;
        }
        catch (final CertException e) {
            Debug.debugException(e);
            throw e;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_EC_PRIVATE_KEY_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    ASN1OctetString encode() throws CertException {
        try {
            final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
            elements.add(new ASN1Integer(this.version));
            elements.add(new ASN1OctetString(this.privateKeyBytes));
            if (this.namedCurveOID != null) {
                elements.add(new ASN1ObjectIdentifier((byte)(-96), this.namedCurveOID));
            }
            if (this.publicKey != null) {
                elements.add(new ASN1BitString((byte)(-127), this.publicKey.getBits()));
            }
            return new ASN1OctetString(new ASN1Sequence(elements).encode());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_EC_PRIVATE_KEY_CANNOT_ENCODE.get(this.toString(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public byte[] getPrivateKeyBytes() {
        return this.privateKeyBytes;
    }
    
    public OID getNamedCurveOID() {
        return this.namedCurveOID;
    }
    
    public ASN1BitString getPublicKey() {
        return this.publicKey;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EllipticCurvePrivateKey(version=");
        buffer.append(this.version);
        buffer.append(", privateKeyBytes=");
        StaticUtils.toHex(this.privateKeyBytes, ":", buffer);
        if (this.namedCurveOID != null) {
            buffer.append(", namedCurveOID='");
            buffer.append(this.namedCurveOID.toString());
            buffer.append('\'');
            final NamedCurve namedCurve = NamedCurve.forOID(this.namedCurveOID);
            if (namedCurve != null) {
                buffer.append(", namedCurveName='");
                buffer.append(namedCurve.getName());
                buffer.append('\'');
            }
        }
        if (this.publicKey != null) {
            try {
                final byte[] publicKeyBytes = this.publicKey.getBytes();
                buffer.append(", publicKeyBytes=");
                StaticUtils.toHex(publicKeyBytes, ":", buffer);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                buffer.append(", publicKeyBitString=");
                this.publicKey.toString(buffer);
            }
        }
        buffer.append(')');
    }
}
