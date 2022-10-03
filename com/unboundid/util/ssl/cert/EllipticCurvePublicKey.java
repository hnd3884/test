package com.unboundid.util.ssl.cert;

import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1BitString;
import java.math.BigInteger;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class EllipticCurvePublicKey extends DecodedPublicKey
{
    private static final long serialVersionUID = 7537378153089968013L;
    private final boolean yCoordinateIsEven;
    private final BigInteger xCoordinate;
    private final BigInteger yCoordinate;
    
    EllipticCurvePublicKey(final BigInteger xCoordinate, final BigInteger yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.yCoordinateIsEven = yCoordinate.mod(BigInteger.valueOf(2L)).equals(BigInteger.ZERO);
    }
    
    EllipticCurvePublicKey(final BigInteger xCoordinate, final boolean yCoordinateIsEven) {
        this.xCoordinate = xCoordinate;
        this.yCoordinateIsEven = yCoordinateIsEven;
        this.yCoordinate = null;
    }
    
    EllipticCurvePublicKey(final ASN1BitString subjectPublicKey) throws CertException {
        try {
            final byte[] keyBytes = subjectPublicKey.getBytes();
            if (keyBytes.length == 65) {
                if (keyBytes[0] != 4) {
                    throw new CertException(CertMessages.ERR_EC_PUBLIC_KEY_PARSE_UNEXPECTED_UNCOMPRESSED_FIRST_BYTE.get(StaticUtils.toHex(keyBytes[0])));
                }
                final byte[] xBytes = new byte[32];
                final byte[] yBytes = new byte[32];
                System.arraycopy(keyBytes, 1, xBytes, 0, 32);
                System.arraycopy(keyBytes, 33, yBytes, 0, 32);
                this.xCoordinate = new BigInteger(xBytes);
                this.yCoordinate = new BigInteger(yBytes);
                this.yCoordinateIsEven = ((keyBytes[64] & 0x1) == 0x0);
            }
            else {
                if (keyBytes.length != 33) {
                    throw new CertException(CertMessages.ERR_EC_PUBLIC_KEY_PARSE_UNEXPECTED_SIZE.get(keyBytes.length));
                }
                this.yCoordinate = null;
                if (keyBytes[0] == 2) {
                    this.yCoordinateIsEven = true;
                }
                else {
                    if (keyBytes[0] != 3) {
                        throw new CertException(CertMessages.ERR_EC_PUBLIC_KEY_PARSE_UNEXPECTED_COMPRESSED_FIRST_BYTE.get(StaticUtils.toHex(keyBytes[0])));
                    }
                    this.yCoordinateIsEven = false;
                }
                final byte[] xBytes = new byte[32];
                System.arraycopy(keyBytes, 1, xBytes, 0, 32);
                this.xCoordinate = new BigInteger(xBytes);
            }
        }
        catch (final CertException e) {
            Debug.debugException(e);
            throw e;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_EC_PUBLIC_KEY_PARSE_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    ASN1BitString encode() throws CertException {
        byte[] publicKeyBytes;
        if (this.yCoordinate == null) {
            publicKeyBytes = new byte[33];
            if (this.yCoordinateIsEven) {
                publicKeyBytes[0] = 2;
            }
            else {
                publicKeyBytes[0] = 3;
            }
        }
        else {
            publicKeyBytes = new byte[65];
            publicKeyBytes[0] = 4;
        }
        final byte[] xCoordinateBytes = this.xCoordinate.toByteArray();
        if (xCoordinateBytes.length > 32) {
            throw new CertException(CertMessages.ERR_EC_PUBLIC_KEY_ENCODE_X_TOO_LARGE.get(this.toString(), xCoordinateBytes.length));
        }
        final int xStartPos = 33 - xCoordinateBytes.length;
        System.arraycopy(xCoordinateBytes, 0, publicKeyBytes, xStartPos, xCoordinateBytes.length);
        if (this.yCoordinate != null) {
            final byte[] yCoordinateBytes = this.yCoordinate.toByteArray();
            if (yCoordinateBytes.length > 32) {
                throw new CertException(CertMessages.ERR_EC_PUBLIC_KEY_ENCODE_Y_TOO_LARGE.get(this.toString(), yCoordinateBytes.length));
            }
            final int yStartPos = 65 - yCoordinateBytes.length;
            System.arraycopy(yCoordinateBytes, 0, publicKeyBytes, yStartPos, yCoordinateBytes.length);
        }
        final boolean[] bits = ASN1BitString.getBitsForBytes(publicKeyBytes);
        return new ASN1BitString(bits);
    }
    
    public boolean usesCompressedForm() {
        return this.yCoordinate == null;
    }
    
    public BigInteger getXCoordinate() {
        return this.xCoordinate;
    }
    
    public BigInteger getYCoordinate() {
        return this.yCoordinate;
    }
    
    public boolean yCoordinateIsEven() {
        return this.yCoordinateIsEven;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EllipticCurvePublicKey(usesCompressedForm=");
        buffer.append(this.yCoordinate == null);
        buffer.append(", xCoordinate=");
        buffer.append(this.xCoordinate);
        if (this.yCoordinate == null) {
            buffer.append(", yCoordinateIsEven=");
            buffer.append(this.yCoordinateIsEven);
        }
        else {
            buffer.append(", yCoordinate=");
            buffer.append(this.yCoordinate);
        }
        buffer.append(')');
    }
}
