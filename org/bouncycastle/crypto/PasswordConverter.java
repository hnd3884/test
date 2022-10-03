package org.bouncycastle.crypto;

public enum PasswordConverter implements CharToByteConverter
{
    ASCII {
        public String getType() {
            return "ASCII";
        }
        
        public byte[] convert(final char[] array) {
            return PBEParametersGenerator.PKCS5PasswordToBytes(array);
        }
    }, 
    UTF8 {
        public String getType() {
            return "UTF8";
        }
        
        public byte[] convert(final char[] array) {
            return PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(array);
        }
    }, 
    PKCS12 {
        public String getType() {
            return "PKCS12";
        }
        
        public byte[] convert(final char[] array) {
            return PBEParametersGenerator.PKCS12PasswordToBytes(array);
        }
    };
}
