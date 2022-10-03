package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.PBEParametersGenerator;

public class PKCS12Key implements PBKDFKey
{
    private final char[] password;
    private final boolean useWrongZeroLengthConversion;
    
    public PKCS12Key(final char[] array) {
        this(array, false);
    }
    
    public PKCS12Key(char[] array, final boolean useWrongZeroLengthConversion) {
        if (array == null) {
            array = new char[0];
        }
        this.password = new char[array.length];
        this.useWrongZeroLengthConversion = useWrongZeroLengthConversion;
        System.arraycopy(array, 0, this.password, 0, array.length);
    }
    
    public char[] getPassword() {
        return this.password;
    }
    
    public String getAlgorithm() {
        return "PKCS12";
    }
    
    public String getFormat() {
        return "PKCS12";
    }
    
    public byte[] getEncoded() {
        if (this.useWrongZeroLengthConversion && this.password.length == 0) {
            return new byte[2];
        }
        return PBEParametersGenerator.PKCS12PasswordToBytes(this.password);
    }
}
