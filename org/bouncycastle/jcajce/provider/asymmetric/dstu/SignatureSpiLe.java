package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.SignatureException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1OctetString;

public class SignatureSpiLe extends SignatureSpi
{
    void reverseBytes(final byte[] array) {
        for (int i = 0; i < array.length / 2; ++i) {
            final byte b = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = b;
        }
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        final byte[] octets = ASN1OctetString.getInstance(super.engineSign()).getOctets();
        this.reverseBytes(octets);
        try {
            return new DEROctetString(octets).getEncoded();
        }
        catch (final Exception ex) {
            throw new SignatureException(ex.toString());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        byte[] octets;
        try {
            octets = ((ASN1OctetString)ASN1Primitive.fromByteArray(array)).getOctets();
        }
        catch (final IOException ex) {
            throw new SignatureException("error decoding signature bytes.");
        }
        this.reverseBytes(octets);
        try {
            return super.engineVerify(new DEROctetString(octets).getEncoded());
        }
        catch (final SignatureException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new SignatureException(ex3.toString());
        }
    }
}
