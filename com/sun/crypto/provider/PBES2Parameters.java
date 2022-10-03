package com.sun.crypto.provider;

import sun.security.util.DerOutputStream;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.util.ObjectIdentifier;
import java.security.AlgorithmParametersSpi;

abstract class PBES2Parameters extends AlgorithmParametersSpi
{
    private static final int[] pkcs5PBKDF2;
    private static final int[] pkcs5PBES2;
    private static final int[] hmacWithSHA1;
    private static final int[] hmacWithSHA224;
    private static final int[] hmacWithSHA256;
    private static final int[] hmacWithSHA384;
    private static final int[] hmacWithSHA512;
    private static final int[] aes128CBC;
    private static final int[] aes192CBC;
    private static final int[] aes256CBC;
    private static ObjectIdentifier pkcs5PBKDF2_OID;
    private static ObjectIdentifier pkcs5PBES2_OID;
    private static ObjectIdentifier hmacWithSHA1_OID;
    private static ObjectIdentifier hmacWithSHA224_OID;
    private static ObjectIdentifier hmacWithSHA256_OID;
    private static ObjectIdentifier hmacWithSHA384_OID;
    private static ObjectIdentifier hmacWithSHA512_OID;
    private static ObjectIdentifier aes128CBC_OID;
    private static ObjectIdentifier aes192CBC_OID;
    private static ObjectIdentifier aes256CBC_OID;
    private String pbes2AlgorithmName;
    private byte[] salt;
    private int iCount;
    private AlgorithmParameterSpec cipherParam;
    private ObjectIdentifier kdfAlgo_OID;
    private ObjectIdentifier cipherAlgo_OID;
    private int keysize;
    
    PBES2Parameters() {
        this.pbes2AlgorithmName = null;
        this.salt = null;
        this.iCount = 0;
        this.cipherParam = null;
        this.kdfAlgo_OID = PBES2Parameters.hmacWithSHA1_OID;
        this.cipherAlgo_OID = null;
        this.keysize = -1;
    }
    
    PBES2Parameters(final String pbes2AlgorithmName) throws NoSuchAlgorithmException {
        this.pbes2AlgorithmName = null;
        this.salt = null;
        this.iCount = 0;
        this.cipherParam = null;
        this.kdfAlgo_OID = PBES2Parameters.hmacWithSHA1_OID;
        this.cipherAlgo_OID = null;
        this.keysize = -1;
        this.pbes2AlgorithmName = pbes2AlgorithmName;
        final int index;
        if (!pbes2AlgorithmName.startsWith("PBEWith") || (index = pbes2AlgorithmName.indexOf("And", 8)) <= 0) {
            throw new NoSuchAlgorithmException("No crypto implementation for " + pbes2AlgorithmName);
        }
        final String substring = pbes2AlgorithmName.substring(7, index);
        String s = pbes2AlgorithmName.substring(index + 3);
        final int index2;
        if ((index2 = s.indexOf(95)) > 0) {
            final int index3;
            if ((index3 = s.indexOf(47, index2 + 1)) > 0) {
                this.keysize = Integer.parseInt(s.substring(index2 + 1, index3));
            }
            else {
                this.keysize = Integer.parseInt(s.substring(index2 + 1));
            }
            s = s.substring(0, index2);
        }
        final String s2 = substring;
        switch (s2) {
            case "HmacSHA1": {
                this.kdfAlgo_OID = PBES2Parameters.hmacWithSHA1_OID;
                break;
            }
            case "HmacSHA224": {
                this.kdfAlgo_OID = PBES2Parameters.hmacWithSHA224_OID;
                break;
            }
            case "HmacSHA256": {
                this.kdfAlgo_OID = PBES2Parameters.hmacWithSHA256_OID;
                break;
            }
            case "HmacSHA384": {
                this.kdfAlgo_OID = PBES2Parameters.hmacWithSHA384_OID;
                break;
            }
            case "HmacSHA512": {
                this.kdfAlgo_OID = PBES2Parameters.hmacWithSHA512_OID;
                break;
            }
            default: {
                throw new NoSuchAlgorithmException("No crypto implementation for " + substring);
            }
        }
        if (s.equals("AES")) {
            switch (this.keysize = this.keysize) {
                case 128: {
                    this.cipherAlgo_OID = PBES2Parameters.aes128CBC_OID;
                    break;
                }
                case 256: {
                    this.cipherAlgo_OID = PBES2Parameters.aes256CBC_OID;
                    break;
                }
                default: {
                    throw new NoSuchAlgorithmException("No Cipher implementation for " + this.keysize + "-bit " + s);
                }
            }
            return;
        }
        throw new NoSuchAlgorithmException("No Cipher implementation for " + s);
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate parameter specification");
        }
        this.salt = ((PBEParameterSpec)algorithmParameterSpec).getSalt().clone();
        this.iCount = ((PBEParameterSpec)algorithmParameterSpec).getIterationCount();
        this.cipherParam = ((PBEParameterSpec)algorithmParameterSpec).getParameterSpec();
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        DerValue derValue = new DerValue(array);
        if (derValue.tag != 48) {
            throw new IOException("PBE parameter parsing error: not an ASN.1 SEQUENCE tag");
        }
        DerValue derValue2 = derValue.data.getDerValue();
        if (derValue2.getTag() == 6) {
            derValue = derValue.data.getDerValue();
            derValue2 = derValue.data.getDerValue();
        }
        final String kdf = this.parseKDF(derValue2);
        if (derValue.tag != 48) {
            throw new IOException("PBE parameter parsing error: not an ASN.1 SEQUENCE tag");
        }
        this.pbes2AlgorithmName = "PBEWith" + kdf + "And" + this.parseES(derValue.data.getDerValue());
    }
    
    private String parseKDF(final DerValue derValue) throws IOException {
        if (!PBES2Parameters.pkcs5PBKDF2_OID.equals(derValue.data.getOID())) {
            throw new IOException("PBE parameter parsing error: expecting the object identifier for PBKDF2");
        }
        if (derValue.tag != 48) {
            throw new IOException("PBE parameter parsing error: not an ASN.1 SEQUENCE tag");
        }
        final DerValue derValue2 = derValue.data.getDerValue();
        if (derValue2.tag != 48) {
            throw new IOException("PBE parameter parsing error: not an ASN.1 SEQUENCE tag");
        }
        final DerValue derValue3 = derValue2.data.getDerValue();
        if (derValue3.tag == 4) {
            this.salt = derValue3.getOctetString();
            this.iCount = derValue2.data.getInteger();
            DerValue derValue4 = null;
            if (derValue2.data.available() > 0) {
                final DerValue derValue5 = derValue2.data.getDerValue();
                if (derValue5.tag == 2) {
                    this.keysize = derValue5.getInteger() * 8;
                }
                else {
                    derValue4 = derValue5;
                }
            }
            String s = "HmacSHA1";
            if (derValue4 == null && derValue2.data.available() > 0) {
                derValue4 = derValue2.data.getDerValue();
            }
            if (derValue4 != null) {
                this.kdfAlgo_OID = derValue4.data.getOID();
                if (PBES2Parameters.hmacWithSHA1_OID.equals(this.kdfAlgo_OID)) {
                    s = "HmacSHA1";
                }
                else if (PBES2Parameters.hmacWithSHA224_OID.equals(this.kdfAlgo_OID)) {
                    s = "HmacSHA224";
                }
                else if (PBES2Parameters.hmacWithSHA256_OID.equals(this.kdfAlgo_OID)) {
                    s = "HmacSHA256";
                }
                else if (PBES2Parameters.hmacWithSHA384_OID.equals(this.kdfAlgo_OID)) {
                    s = "HmacSHA384";
                }
                else {
                    if (!PBES2Parameters.hmacWithSHA512_OID.equals(this.kdfAlgo_OID)) {
                        throw new IOException("PBE parameter parsing error: expecting the object identifier for a HmacSHA key derivation function");
                    }
                    s = "HmacSHA512";
                }
                if (derValue4.data.available() != 0 && derValue4.data.getDerValue().tag != 5) {
                    throw new IOException("PBE parameter parsing error: not an ASN.1 NULL tag");
                }
            }
            return s;
        }
        throw new IOException("PBE parameter parsing error: not an ASN.1 OCTET STRING tag");
    }
    
    private String parseES(final DerValue derValue) throws IOException {
        this.cipherAlgo_OID = derValue.data.getOID();
        String s;
        if (PBES2Parameters.aes128CBC_OID.equals(this.cipherAlgo_OID)) {
            s = "AES_128";
            this.cipherParam = new IvParameterSpec(derValue.data.getOctetString());
            this.keysize = 128;
        }
        else {
            if (!PBES2Parameters.aes256CBC_OID.equals(this.cipherAlgo_OID)) {
                throw new IOException("PBE parameter parsing error: expecting the object identifier for AES cipher");
            }
            s = "AES_256";
            this.cipherParam = new IvParameterSpec(derValue.data.getOctetString());
            this.keysize = 256;
        }
        return s;
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        this.engineInit(array);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        if (PBEParameterSpec.class.isAssignableFrom(clazz)) {
            return clazz.cast(new PBEParameterSpec(this.salt, this.iCount, this.cipherParam));
        }
        throw new InvalidParameterSpecException("Inappropriate parameter specification");
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOID(PBES2Parameters.pkcs5PBKDF2_OID);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.putOctetString(this.salt);
        derOutputStream4.putInteger(this.iCount);
        if (this.keysize > 0) {
            derOutputStream4.putInteger(this.keysize / 8);
        }
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.putOID(this.kdfAlgo_OID);
        derOutputStream5.putNull();
        derOutputStream4.write((byte)48, derOutputStream5);
        derOutputStream3.write((byte)48, derOutputStream4);
        derOutputStream2.write((byte)48, derOutputStream3);
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.putOID(this.cipherAlgo_OID);
        if (this.cipherParam != null && this.cipherParam instanceof IvParameterSpec) {
            derOutputStream6.putOctetString(((IvParameterSpec)this.cipherParam).getIV());
            derOutputStream2.write((byte)48, derOutputStream6);
            derOutputStream.write((byte)48, derOutputStream2);
            return derOutputStream.toByteArray();
        }
        throw new IOException("Wrong parameter type: IV expected");
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) throws IOException {
        return this.engineGetEncoded();
    }
    
    @Override
    protected String engineToString() {
        return this.pbes2AlgorithmName;
    }
    
    static {
        pkcs5PBKDF2 = new int[] { 1, 2, 840, 113549, 1, 5, 12 };
        pkcs5PBES2 = new int[] { 1, 2, 840, 113549, 1, 5, 13 };
        hmacWithSHA1 = new int[] { 1, 2, 840, 113549, 2, 7 };
        hmacWithSHA224 = new int[] { 1, 2, 840, 113549, 2, 8 };
        hmacWithSHA256 = new int[] { 1, 2, 840, 113549, 2, 9 };
        hmacWithSHA384 = new int[] { 1, 2, 840, 113549, 2, 10 };
        hmacWithSHA512 = new int[] { 1, 2, 840, 113549, 2, 11 };
        aes128CBC = new int[] { 2, 16, 840, 1, 101, 3, 4, 1, 2 };
        aes192CBC = new int[] { 2, 16, 840, 1, 101, 3, 4, 1, 22 };
        aes256CBC = new int[] { 2, 16, 840, 1, 101, 3, 4, 1, 42 };
        try {
            PBES2Parameters.pkcs5PBKDF2_OID = new ObjectIdentifier(PBES2Parameters.pkcs5PBKDF2);
            PBES2Parameters.pkcs5PBES2_OID = new ObjectIdentifier(PBES2Parameters.pkcs5PBES2);
            PBES2Parameters.hmacWithSHA1_OID = new ObjectIdentifier(PBES2Parameters.hmacWithSHA1);
            PBES2Parameters.hmacWithSHA224_OID = new ObjectIdentifier(PBES2Parameters.hmacWithSHA224);
            PBES2Parameters.hmacWithSHA256_OID = new ObjectIdentifier(PBES2Parameters.hmacWithSHA256);
            PBES2Parameters.hmacWithSHA384_OID = new ObjectIdentifier(PBES2Parameters.hmacWithSHA384);
            PBES2Parameters.hmacWithSHA512_OID = new ObjectIdentifier(PBES2Parameters.hmacWithSHA512);
            PBES2Parameters.aes128CBC_OID = new ObjectIdentifier(PBES2Parameters.aes128CBC);
            PBES2Parameters.aes192CBC_OID = new ObjectIdentifier(PBES2Parameters.aes192CBC);
            PBES2Parameters.aes256CBC_OID = new ObjectIdentifier(PBES2Parameters.aes256CBC);
        }
        catch (final IOException ex) {}
    }
    
    public static final class General extends PBES2Parameters
    {
        public General() throws NoSuchAlgorithmException {
        }
    }
    
    public static final class HmacSHA1AndAES_128 extends PBES2Parameters
    {
        public HmacSHA1AndAES_128() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA1AndAES_128");
        }
    }
    
    public static final class HmacSHA224AndAES_128 extends PBES2Parameters
    {
        public HmacSHA224AndAES_128() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA224AndAES_128");
        }
    }
    
    public static final class HmacSHA256AndAES_128 extends PBES2Parameters
    {
        public HmacSHA256AndAES_128() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA256AndAES_128");
        }
    }
    
    public static final class HmacSHA384AndAES_128 extends PBES2Parameters
    {
        public HmacSHA384AndAES_128() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA384AndAES_128");
        }
    }
    
    public static final class HmacSHA512AndAES_128 extends PBES2Parameters
    {
        public HmacSHA512AndAES_128() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA512AndAES_128");
        }
    }
    
    public static final class HmacSHA1AndAES_256 extends PBES2Parameters
    {
        public HmacSHA1AndAES_256() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA1AndAES_256");
        }
    }
    
    public static final class HmacSHA224AndAES_256 extends PBES2Parameters
    {
        public HmacSHA224AndAES_256() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA224AndAES_256");
        }
    }
    
    public static final class HmacSHA256AndAES_256 extends PBES2Parameters
    {
        public HmacSHA256AndAES_256() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA256AndAES_256");
        }
    }
    
    public static final class HmacSHA384AndAES_256 extends PBES2Parameters
    {
        public HmacSHA384AndAES_256() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA384AndAES_256");
        }
    }
    
    public static final class HmacSHA512AndAES_256 extends PBES2Parameters
    {
        public HmacSHA512AndAES_256() throws NoSuchAlgorithmException {
            super("PBEWithHmacSHA512AndAES_256");
        }
    }
}
