package org.bouncycastle.jcajce.provider.keystore.bc;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.io.DigestOutputStream;
import javax.crypto.CipherOutputStream;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.crypto.io.DigestInputStream;
import javax.crypto.CipherInputStream;
import org.bouncycastle.util.io.TeeOutputStream;
import org.bouncycastle.crypto.io.MacOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.io.MacInputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.digests.SHA1Digest;
import java.io.OutputStream;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.security.KeyStoreException;
import java.util.Enumeration;
import javax.crypto.SecretKeyFactory;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.Cipher;
import java.security.spec.EncodedKeySpec;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.Key;
import java.security.cert.CertificateException;
import java.security.NoSuchProviderException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.security.cert.CertificateEncodingException;
import java.io.IOException;
import java.io.DataOutputStream;
import java.security.cert.Certificate;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.SecureRandom;
import java.util.Hashtable;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import java.security.KeyStoreSpi;

public class BcKeyStoreSpi extends KeyStoreSpi implements BCKeyStore
{
    private static final int STORE_VERSION = 2;
    private static final int STORE_SALT_SIZE = 20;
    private static final String STORE_CIPHER = "PBEWithSHAAndTwofish-CBC";
    private static final int KEY_SALT_SIZE = 20;
    private static final int MIN_ITERATIONS = 1024;
    private static final String KEY_CIPHER = "PBEWithSHAAnd3-KeyTripleDES-CBC";
    static final int NULL = 0;
    static final int CERTIFICATE = 1;
    static final int KEY = 2;
    static final int SECRET = 3;
    static final int SEALED = 4;
    static final int KEY_PRIVATE = 0;
    static final int KEY_PUBLIC = 1;
    static final int KEY_SECRET = 2;
    protected Hashtable table;
    protected SecureRandom random;
    protected int version;
    private final JcaJceHelper helper;
    
    public BcKeyStoreSpi(final int version) {
        this.table = new Hashtable();
        this.random = new SecureRandom();
        this.helper = new BCJcaJceHelper();
        this.version = version;
    }
    
    private void encodeCertificate(final Certificate certificate, final DataOutputStream dataOutputStream) throws IOException {
        try {
            final byte[] encoded = certificate.getEncoded();
            dataOutputStream.writeUTF(certificate.getType());
            dataOutputStream.writeInt(encoded.length);
            dataOutputStream.write(encoded);
        }
        catch (final CertificateEncodingException ex) {
            throw new IOException(ex.toString());
        }
    }
    
    private Certificate decodeCertificate(final DataInputStream dataInputStream) throws IOException {
        final String utf = dataInputStream.readUTF();
        final byte[] array = new byte[dataInputStream.readInt()];
        dataInputStream.readFully(array);
        try {
            return this.helper.createCertificateFactory(utf).generateCertificate(new ByteArrayInputStream(array));
        }
        catch (final NoSuchProviderException ex) {
            throw new IOException(ex.toString());
        }
        catch (final CertificateException ex2) {
            throw new IOException(ex2.toString());
        }
    }
    
    private void encodeKey(final Key key, final DataOutputStream dataOutputStream) throws IOException {
        final byte[] encoded = key.getEncoded();
        if (key instanceof PrivateKey) {
            dataOutputStream.write(0);
        }
        else if (key instanceof PublicKey) {
            dataOutputStream.write(1);
        }
        else {
            dataOutputStream.write(2);
        }
        dataOutputStream.writeUTF(key.getFormat());
        dataOutputStream.writeUTF(key.getAlgorithm());
        dataOutputStream.writeInt(encoded.length);
        dataOutputStream.write(encoded);
    }
    
    private Key decodeKey(final DataInputStream dataInputStream) throws IOException {
        final int read = dataInputStream.read();
        final String utf = dataInputStream.readUTF();
        final String utf2 = dataInputStream.readUTF();
        final byte[] array = new byte[dataInputStream.readInt()];
        dataInputStream.readFully(array);
        EncodedKeySpec encodedKeySpec;
        if (utf.equals("PKCS#8") || utf.equals("PKCS8")) {
            encodedKeySpec = new PKCS8EncodedKeySpec(array);
        }
        else if (utf.equals("X.509") || utf.equals("X509")) {
            encodedKeySpec = new X509EncodedKeySpec(array);
        }
        else {
            if (utf.equals("RAW")) {
                return new SecretKeySpec(array, utf2);
            }
            throw new IOException("Key format " + utf + " not recognised!");
        }
        try {
            switch (read) {
                case 0: {
                    return BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(array));
                }
                case 1: {
                    return BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(array));
                }
                case 2: {
                    return this.helper.createSecretKeyFactory(utf2).generateSecret(encodedKeySpec);
                }
                default: {
                    throw new IOException("Key type " + read + " not recognised!");
                }
            }
        }
        catch (final Exception ex) {
            throw new IOException("Exception creating key: " + ex.toString());
        }
    }
    
    protected Cipher makePBECipher(final String s, final int n, final char[] array, final byte[] array2, final int n2) throws IOException {
        try {
            final PBEKeySpec pbeKeySpec = new PBEKeySpec(array);
            final SecretKeyFactory secretKeyFactory = this.helper.createSecretKeyFactory(s);
            final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(array2, n2);
            final Cipher cipher = this.helper.createCipher(s);
            cipher.init(n, secretKeyFactory.generateSecret(pbeKeySpec), pbeParameterSpec);
            return cipher;
        }
        catch (final Exception ex) {
            throw new IOException("Error initialising store of key store: " + ex);
        }
    }
    
    public void setRandom(final SecureRandom random) {
        this.random = random;
    }
    
    @Override
    public Enumeration engineAliases() {
        return this.table.keys();
    }
    
    @Override
    public boolean engineContainsAlias(final String s) {
        return this.table.get(s) != null;
    }
    
    @Override
    public void engineDeleteEntry(final String s) throws KeyStoreException {
        if (this.table.get(s) == null) {
            return;
        }
        this.table.remove(s);
    }
    
    @Override
    public Certificate engineGetCertificate(final String s) {
        final StoreEntry storeEntry = this.table.get(s);
        if (storeEntry != null) {
            if (storeEntry.getType() == 1) {
                return (Certificate)storeEntry.getObject();
            }
            final Certificate[] certificateChain = storeEntry.getCertificateChain();
            if (certificateChain != null) {
                return certificateChain[0];
            }
        }
        return null;
    }
    
    @Override
    public String engineGetCertificateAlias(final Certificate certificate) {
        final Enumeration elements = this.table.elements();
        while (elements.hasMoreElements()) {
            final StoreEntry storeEntry = (StoreEntry)elements.nextElement();
            if (storeEntry.getObject() instanceof Certificate) {
                if (((Certificate)storeEntry.getObject()).equals(certificate)) {
                    return storeEntry.getAlias();
                }
                continue;
            }
            else {
                final Certificate[] certificateChain = storeEntry.getCertificateChain();
                if (certificateChain != null && certificateChain[0].equals(certificate)) {
                    return storeEntry.getAlias();
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public Certificate[] engineGetCertificateChain(final String s) {
        final StoreEntry storeEntry = this.table.get(s);
        if (storeEntry != null) {
            return storeEntry.getCertificateChain();
        }
        return null;
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        final StoreEntry storeEntry = this.table.get(s);
        if (storeEntry != null) {
            return storeEntry.getDate();
        }
        return null;
    }
    
    @Override
    public Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        final StoreEntry storeEntry = this.table.get(s);
        if (storeEntry == null || storeEntry.getType() == 1) {
            return null;
        }
        return (Key)storeEntry.getObject(array);
    }
    
    @Override
    public boolean engineIsCertificateEntry(final String s) {
        final StoreEntry storeEntry = this.table.get(s);
        return storeEntry != null && storeEntry.getType() == 1;
    }
    
    @Override
    public boolean engineIsKeyEntry(final String s) {
        final StoreEntry storeEntry = this.table.get(s);
        return storeEntry != null && storeEntry.getType() != 1;
    }
    
    @Override
    public void engineSetCertificateEntry(final String s, final Certificate certificate) throws KeyStoreException {
        final StoreEntry storeEntry = this.table.get(s);
        if (storeEntry != null && storeEntry.getType() != 1) {
            throw new KeyStoreException("key store already has a key entry with alias " + s);
        }
        this.table.put(s, new StoreEntry(s, certificate));
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        this.table.put(s, new StoreEntry(s, array, array2));
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final Key key, final char[] array, final Certificate[] array2) throws KeyStoreException {
        if (key instanceof PrivateKey && array2 == null) {
            throw new KeyStoreException("no certificate chain for private key");
        }
        try {
            this.table.put(s, new StoreEntry(s, key, array, array2));
        }
        catch (final Exception ex) {
            throw new KeyStoreException(ex.toString());
        }
    }
    
    @Override
    public int engineSize() {
        return this.table.size();
    }
    
    protected void loadStore(final InputStream inputStream) throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        for (int i = dataInputStream.read(); i > 0; i = dataInputStream.read()) {
            final String utf = dataInputStream.readUTF();
            final Date date = new Date(dataInputStream.readLong());
            final int int1 = dataInputStream.readInt();
            Certificate[] array = null;
            if (int1 != 0) {
                array = new Certificate[int1];
                for (int j = 0; j != int1; ++j) {
                    array[j] = this.decodeCertificate(dataInputStream);
                }
            }
            switch (i) {
                case 1: {
                    this.table.put(utf, new StoreEntry(utf, date, 1, this.decodeCertificate(dataInputStream)));
                    break;
                }
                case 2: {
                    this.table.put(utf, new StoreEntry(utf, date, 2, this.decodeKey(dataInputStream), array));
                    break;
                }
                case 3:
                case 4: {
                    final byte[] array2 = new byte[dataInputStream.readInt()];
                    dataInputStream.readFully(array2);
                    this.table.put(utf, new StoreEntry(utf, date, i, array2, array));
                    break;
                }
                default: {
                    throw new IOException("Unknown object type in store.");
                }
            }
        }
    }
    
    protected void saveStore(final OutputStream outputStream) throws IOException {
        final Enumeration elements = this.table.elements();
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        while (elements.hasMoreElements()) {
            final StoreEntry storeEntry = (StoreEntry)elements.nextElement();
            dataOutputStream.write(storeEntry.getType());
            dataOutputStream.writeUTF(storeEntry.getAlias());
            dataOutputStream.writeLong(storeEntry.getDate().getTime());
            final Certificate[] certificateChain = storeEntry.getCertificateChain();
            if (certificateChain == null) {
                dataOutputStream.writeInt(0);
            }
            else {
                dataOutputStream.writeInt(certificateChain.length);
                for (int i = 0; i != certificateChain.length; ++i) {
                    this.encodeCertificate(certificateChain[i], dataOutputStream);
                }
            }
            switch (storeEntry.getType()) {
                case 1: {
                    this.encodeCertificate((Certificate)storeEntry.getObject(), dataOutputStream);
                    continue;
                }
                case 2: {
                    this.encodeKey((Key)storeEntry.getObject(), dataOutputStream);
                    continue;
                }
                case 3:
                case 4: {
                    final byte[] array = (byte[])storeEntry.getObject();
                    dataOutputStream.writeInt(array.length);
                    dataOutputStream.write(array);
                    continue;
                }
                default: {
                    throw new IOException("Unknown object type in store.");
                }
            }
        }
        dataOutputStream.write(0);
    }
    
    @Override
    public void engineLoad(final InputStream inputStream, final char[] array) throws IOException {
        this.table.clear();
        if (inputStream == null) {
            return;
        }
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final int int1 = dataInputStream.readInt();
        if (int1 != 2 && int1 != 0 && int1 != 1) {
            throw new IOException("Wrong version of key store.");
        }
        final int int2 = dataInputStream.readInt();
        if (int2 <= 0) {
            throw new IOException("Invalid salt detected");
        }
        final byte[] array2 = new byte[int2];
        dataInputStream.readFully(array2);
        final int int3 = dataInputStream.readInt();
        final HMac hMac = new HMac(new SHA1Digest());
        if (array != null && array.length != 0) {
            final byte[] pkcs12PasswordToBytes = PBEParametersGenerator.PKCS12PasswordToBytes(array);
            final PKCS12ParametersGenerator pkcs12ParametersGenerator = new PKCS12ParametersGenerator(new SHA1Digest());
            pkcs12ParametersGenerator.init(pkcs12PasswordToBytes, array2, int3);
            CipherParameters cipherParameters;
            if (int1 != 2) {
                cipherParameters = pkcs12ParametersGenerator.generateDerivedMacParameters(hMac.getMacSize());
            }
            else {
                cipherParameters = pkcs12ParametersGenerator.generateDerivedMacParameters(hMac.getMacSize() * 8);
            }
            Arrays.fill(pkcs12PasswordToBytes, (byte)0);
            hMac.init(cipherParameters);
            this.loadStore(new MacInputStream(dataInputStream, hMac));
            final byte[] array3 = new byte[hMac.getMacSize()];
            hMac.doFinal(array3, 0);
            final byte[] array4 = new byte[hMac.getMacSize()];
            dataInputStream.readFully(array4);
            if (!Arrays.constantTimeAreEqual(array3, array4)) {
                this.table.clear();
                throw new IOException("KeyStore integrity check failed.");
            }
        }
        else {
            this.loadStore(dataInputStream);
            dataInputStream.readFully(new byte[hMac.getMacSize()]);
        }
    }
    
    @Override
    public void engineStore(final OutputStream outputStream, final char[] array) throws IOException {
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        final byte[] array2 = new byte[20];
        final int n = 1024 + (this.random.nextInt() & 0x3FF);
        this.random.nextBytes(array2);
        dataOutputStream.writeInt(this.version);
        dataOutputStream.writeInt(array2.length);
        dataOutputStream.write(array2);
        dataOutputStream.writeInt(n);
        final HMac hMac = new HMac(new SHA1Digest());
        final MacOutputStream macOutputStream = new MacOutputStream(hMac);
        final PKCS12ParametersGenerator pkcs12ParametersGenerator = new PKCS12ParametersGenerator(new SHA1Digest());
        final byte[] pkcs12PasswordToBytes = PBEParametersGenerator.PKCS12PasswordToBytes(array);
        pkcs12ParametersGenerator.init(pkcs12PasswordToBytes, array2, n);
        if (this.version < 2) {
            hMac.init(pkcs12ParametersGenerator.generateDerivedMacParameters(hMac.getMacSize()));
        }
        else {
            hMac.init(pkcs12ParametersGenerator.generateDerivedMacParameters(hMac.getMacSize() * 8));
        }
        for (int i = 0; i != pkcs12PasswordToBytes.length; ++i) {
            pkcs12PasswordToBytes[i] = 0;
        }
        this.saveStore(new TeeOutputStream(dataOutputStream, macOutputStream));
        final byte[] array3 = new byte[hMac.getMacSize()];
        hMac.doFinal(array3, 0);
        dataOutputStream.write(array3);
        dataOutputStream.close();
    }
    
    public static class BouncyCastleStore extends BcKeyStoreSpi
    {
        public BouncyCastleStore() {
            super(1);
        }
        
        @Override
        public void engineLoad(final InputStream inputStream, final char[] array) throws IOException {
            this.table.clear();
            if (inputStream == null) {
                return;
            }
            final DataInputStream dataInputStream = new DataInputStream(inputStream);
            final int int1 = dataInputStream.readInt();
            if (int1 != 2 && int1 != 0 && int1 != 1) {
                throw new IOException("Wrong version of key store.");
            }
            final byte[] array2 = new byte[dataInputStream.readInt()];
            if (array2.length != 20) {
                throw new IOException("Key store corrupted.");
            }
            dataInputStream.readFully(array2);
            final int int2 = dataInputStream.readInt();
            if (int2 < 0 || int2 > 65536) {
                throw new IOException("Key store corrupted.");
            }
            String s;
            if (int1 == 0) {
                s = "OldPBEWithSHAAndTwofish-CBC";
            }
            else {
                s = "PBEWithSHAAndTwofish-CBC";
            }
            final CipherInputStream cipherInputStream = new CipherInputStream(dataInputStream, this.makePBECipher(s, 2, array, array2, int2));
            final SHA1Digest sha1Digest = new SHA1Digest();
            this.loadStore(new DigestInputStream(cipherInputStream, sha1Digest));
            final byte[] array3 = new byte[sha1Digest.getDigestSize()];
            sha1Digest.doFinal(array3, 0);
            final byte[] array4 = new byte[sha1Digest.getDigestSize()];
            Streams.readFully(cipherInputStream, array4);
            if (!Arrays.constantTimeAreEqual(array3, array4)) {
                this.table.clear();
                throw new IOException("KeyStore integrity check failed.");
            }
        }
        
        @Override
        public void engineStore(final OutputStream outputStream, final char[] array) throws IOException {
            final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            final byte[] array2 = new byte[20];
            final int n = 1024 + (this.random.nextInt() & 0x3FF);
            this.random.nextBytes(array2);
            dataOutputStream.writeInt(this.version);
            dataOutputStream.writeInt(array2.length);
            dataOutputStream.write(array2);
            dataOutputStream.writeInt(n);
            final CipherOutputStream cipherOutputStream = new CipherOutputStream(dataOutputStream, this.makePBECipher("PBEWithSHAAndTwofish-CBC", 1, array, array2, n));
            final DigestOutputStream digestOutputStream = new DigestOutputStream(new SHA1Digest());
            this.saveStore(new TeeOutputStream(cipherOutputStream, digestOutputStream));
            cipherOutputStream.write(digestOutputStream.getDigest());
            cipherOutputStream.close();
        }
    }
    
    public static class Std extends BcKeyStoreSpi
    {
        public Std() {
            super(2);
        }
    }
    
    private class StoreEntry
    {
        int type;
        String alias;
        Object obj;
        Certificate[] certChain;
        Date date;
        
        StoreEntry(final String alias, final Certificate obj) {
            this.date = new Date();
            this.type = 1;
            this.alias = alias;
            this.obj = obj;
            this.certChain = null;
        }
        
        StoreEntry(final String alias, final byte[] obj, final Certificate[] certChain) {
            this.date = new Date();
            this.type = 3;
            this.alias = alias;
            this.obj = obj;
            this.certChain = certChain;
        }
        
        StoreEntry(final String alias, final Key key, final char[] array, final Certificate[] certChain) throws Exception {
            this.date = new Date();
            this.type = 4;
            this.alias = alias;
            this.certChain = certChain;
            final byte[] array2 = new byte[20];
            BcKeyStoreSpi.this.random.setSeed(System.currentTimeMillis());
            BcKeyStoreSpi.this.random.nextBytes(array2);
            final int n = 1024 + (BcKeyStoreSpi.this.random.nextInt() & 0x3FF);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeInt(array2.length);
            dataOutputStream.write(array2);
            dataOutputStream.writeInt(n);
            final DataOutputStream dataOutputStream2 = new DataOutputStream(new CipherOutputStream(dataOutputStream, BcKeyStoreSpi.this.makePBECipher("PBEWithSHAAnd3-KeyTripleDES-CBC", 1, array, array2, n)));
            BcKeyStoreSpi.this.encodeKey(key, dataOutputStream2);
            dataOutputStream2.close();
            this.obj = byteArrayOutputStream.toByteArray();
        }
        
        StoreEntry(final String alias, final Date date, final int type, final Object obj) {
            this.date = new Date();
            this.alias = alias;
            this.date = date;
            this.type = type;
            this.obj = obj;
        }
        
        StoreEntry(final String alias, final Date date, final int type, final Object obj, final Certificate[] certChain) {
            this.date = new Date();
            this.alias = alias;
            this.date = date;
            this.type = type;
            this.obj = obj;
            this.certChain = certChain;
        }
        
        int getType() {
            return this.type;
        }
        
        String getAlias() {
            return this.alias;
        }
        
        Object getObject() {
            return this.obj;
        }
        
        Object getObject(final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            if ((array == null || array.length == 0) && this.obj instanceof Key) {
                return this.obj;
            }
            if (this.type == 4) {
                final DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream((byte[])this.obj));
                try {
                    final byte[] array2 = new byte[dataInputStream.readInt()];
                    dataInputStream.readFully(array2);
                    final CipherInputStream cipherInputStream = new CipherInputStream(dataInputStream, BcKeyStoreSpi.this.makePBECipher("PBEWithSHAAnd3-KeyTripleDES-CBC", 2, array, array2, dataInputStream.readInt()));
                    try {
                        return BcKeyStoreSpi.this.decodeKey(new DataInputStream(cipherInputStream));
                    }
                    catch (final Exception ex) {
                        final DataInputStream dataInputStream2 = new DataInputStream(new ByteArrayInputStream((byte[])this.obj));
                        byte[] array3 = new byte[dataInputStream2.readInt()];
                        dataInputStream2.readFully(array3);
                        int n = dataInputStream2.readInt();
                        final CipherInputStream cipherInputStream2 = new CipherInputStream(dataInputStream2, BcKeyStoreSpi.this.makePBECipher("BrokenPBEWithSHAAnd3-KeyTripleDES-CBC", 2, array, array3, n));
                        Key key;
                        try {
                            key = BcKeyStoreSpi.this.decodeKey(new DataInputStream(cipherInputStream2));
                        }
                        catch (final Exception ex2) {
                            final DataInputStream dataInputStream3 = new DataInputStream(new ByteArrayInputStream((byte[])this.obj));
                            array3 = new byte[dataInputStream3.readInt()];
                            dataInputStream3.readFully(array3);
                            n = dataInputStream3.readInt();
                            key = BcKeyStoreSpi.this.decodeKey(new DataInputStream(new CipherInputStream(dataInputStream3, BcKeyStoreSpi.this.makePBECipher("OldPBEWithSHAAnd3-KeyTripleDES-CBC", 2, array, array3, n))));
                        }
                        if (key != null) {
                            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                            dataOutputStream.writeInt(array3.length);
                            dataOutputStream.write(array3);
                            dataOutputStream.writeInt(n);
                            final DataOutputStream dataOutputStream2 = new DataOutputStream(new CipherOutputStream(dataOutputStream, BcKeyStoreSpi.this.makePBECipher("PBEWithSHAAnd3-KeyTripleDES-CBC", 1, array, array3, n)));
                            BcKeyStoreSpi.this.encodeKey(key, dataOutputStream2);
                            dataOutputStream2.close();
                            this.obj = byteArrayOutputStream.toByteArray();
                            return key;
                        }
                        throw new UnrecoverableKeyException("no match");
                    }
                }
                catch (final Exception ex3) {
                    throw new UnrecoverableKeyException("no match");
                }
            }
            throw new RuntimeException("forget something!");
        }
        
        Certificate[] getCertificateChain() {
            return this.certChain;
        }
        
        Date getDate() {
            return this.date;
        }
    }
    
    public static class Version1 extends BcKeyStoreSpi
    {
        public Version1() {
            super(1);
        }
    }
}
