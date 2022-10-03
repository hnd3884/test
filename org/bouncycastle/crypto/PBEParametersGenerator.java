package org.bouncycastle.crypto;

import org.bouncycastle.util.Strings;

public abstract class PBEParametersGenerator
{
    protected byte[] password;
    protected byte[] salt;
    protected int iterationCount;
    
    protected PBEParametersGenerator() {
    }
    
    public void init(final byte[] password, final byte[] salt, final int iterationCount) {
        this.password = password;
        this.salt = salt;
        this.iterationCount = iterationCount;
    }
    
    public byte[] getPassword() {
        return this.password;
    }
    
    public byte[] getSalt() {
        return this.salt;
    }
    
    public int getIterationCount() {
        return this.iterationCount;
    }
    
    public abstract CipherParameters generateDerivedParameters(final int p0);
    
    public abstract CipherParameters generateDerivedParameters(final int p0, final int p1);
    
    public abstract CipherParameters generateDerivedMacParameters(final int p0);
    
    public static byte[] PKCS5PasswordToBytes(final char[] array) {
        if (array != null) {
            final byte[] array2 = new byte[array.length];
            for (int i = 0; i != array2.length; ++i) {
                array2[i] = (byte)array[i];
            }
            return array2;
        }
        return new byte[0];
    }
    
    public static byte[] PKCS5PasswordToUTF8Bytes(final char[] array) {
        if (array != null) {
            return Strings.toUTF8ByteArray(array);
        }
        return new byte[0];
    }
    
    public static byte[] PKCS12PasswordToBytes(final char[] array) {
        if (array != null && array.length > 0) {
            final byte[] array2 = new byte[(array.length + 1) * 2];
            for (int i = 0; i != array.length; ++i) {
                array2[i * 2] = (byte)(array[i] >>> 8);
                array2[i * 2 + 1] = (byte)array[i];
            }
            return array2;
        }
        return new byte[0];
    }
}
