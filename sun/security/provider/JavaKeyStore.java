package sun.security.provider;

import java.security.UnrecoverableEntryException;
import java.security.KeyStore;
import sun.security.pkcs12.PKCS12KeyStore;
import java.util.Locale;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import sun.misc.IOUtils;
import java.security.cert.CertificateFactory;
import java.io.DataInputStream;
import java.security.DigestInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.MessageDigest;
import java.io.DataOutputStream;
import java.security.DigestOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.Date;
import java.security.cert.Certificate;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.io.IOException;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import java.security.UnrecoverableKeyException;
import java.security.Key;
import java.util.Hashtable;
import sun.security.util.Debug;
import java.security.KeyStoreSpi;

abstract class JavaKeyStore extends KeyStoreSpi
{
    private static final Debug debug;
    private static final int MAGIC = -17957139;
    private static final int VERSION_1 = 1;
    private static final int VERSION_2 = 2;
    private final Hashtable<String, Object> entries;
    
    JavaKeyStore() {
        this.entries = new Hashtable<String, Object>();
    }
    
    abstract String convertAlias(final String p0);
    
    @Override
    public Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        final KeyEntry value = this.entries.get(this.convertAlias(s));
        if (value == null || !(value instanceof KeyEntry)) {
            return null;
        }
        if (array == null) {
            throw new UnrecoverableKeyException("Password must not be null");
        }
        final byte[] convertToBytes = this.convertToBytes(array);
        final KeyProtector keyProtector = new KeyProtector(convertToBytes);
        final byte[] protectedPrivKey = value.protectedPrivKey;
        try {
            return keyProtector.recover(new EncryptedPrivateKeyInfo(protectedPrivKey));
        }
        catch (final IOException ex) {
            throw new UnrecoverableKeyException("Private key not stored as PKCS #8 EncryptedPrivateKeyInfo");
        }
        finally {
            Arrays.fill(convertToBytes, (byte)0);
        }
    }
    
    @Override
    public Certificate[] engineGetCertificateChain(final String s) {
        final KeyEntry value = this.entries.get(this.convertAlias(s));
        if (value == null || !(value instanceof KeyEntry)) {
            return null;
        }
        if (value.chain == null) {
            return null;
        }
        return value.chain.clone();
    }
    
    @Override
    public Certificate engineGetCertificate(final String s) {
        final KeyEntry value = this.entries.get(this.convertAlias(s));
        if (value == null) {
            return null;
        }
        if (value instanceof TrustedCertEntry) {
            return ((TrustedCertEntry)value).cert;
        }
        if (value.chain == null) {
            return null;
        }
        return value.chain[0];
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        final KeyEntry value = this.entries.get(this.convertAlias(s));
        if (value == null) {
            return null;
        }
        if (value instanceof TrustedCertEntry) {
            return new Date(((TrustedCertEntry)value).date.getTime());
        }
        return new Date(value.date.getTime());
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final Key key, final char[] array, final Certificate[] array2) throws KeyStoreException {
        byte[] convertToBytes = null;
        if (!(key instanceof PrivateKey)) {
            throw new KeyStoreException("Cannot store non-PrivateKeys");
        }
        try {
            synchronized (this.entries) {
                final KeyEntry keyEntry = new KeyEntry();
                keyEntry.date = new Date();
                convertToBytes = this.convertToBytes(array);
                keyEntry.protectedPrivKey = new KeyProtector(convertToBytes).protect(key);
                if (array2 != null && array2.length != 0) {
                    keyEntry.chain = array2.clone();
                }
                else {
                    keyEntry.chain = null;
                }
                this.entries.put(this.convertAlias(s), keyEntry);
            }
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new KeyStoreException("Key protection algorithm not found");
        }
        finally {
            if (convertToBytes != null) {
                Arrays.fill(convertToBytes, (byte)0);
            }
        }
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        synchronized (this.entries) {
            try {
                new EncryptedPrivateKeyInfo(array);
            }
            catch (final IOException ex) {
                throw new KeyStoreException("key is not encoded as EncryptedPrivateKeyInfo");
            }
            final KeyEntry keyEntry = new KeyEntry();
            keyEntry.date = new Date();
            keyEntry.protectedPrivKey = array.clone();
            if (array2 != null && array2.length != 0) {
                keyEntry.chain = array2.clone();
            }
            else {
                keyEntry.chain = null;
            }
            this.entries.put(this.convertAlias(s), keyEntry);
        }
    }
    
    @Override
    public void engineSetCertificateEntry(final String s, final Certificate cert) throws KeyStoreException {
        synchronized (this.entries) {
            final Object value = this.entries.get(this.convertAlias(s));
            if (value != null && value instanceof KeyEntry) {
                throw new KeyStoreException("Cannot overwrite own certificate");
            }
            final TrustedCertEntry trustedCertEntry = new TrustedCertEntry();
            trustedCertEntry.cert = cert;
            trustedCertEntry.date = new Date();
            this.entries.put(this.convertAlias(s), trustedCertEntry);
        }
    }
    
    @Override
    public void engineDeleteEntry(final String s) throws KeyStoreException {
        synchronized (this.entries) {
            this.entries.remove(this.convertAlias(s));
        }
    }
    
    @Override
    public Enumeration<String> engineAliases() {
        return this.entries.keys();
    }
    
    @Override
    public boolean engineContainsAlias(final String s) {
        return this.entries.containsKey(this.convertAlias(s));
    }
    
    @Override
    public int engineSize() {
        return this.entries.size();
    }
    
    @Override
    public boolean engineIsKeyEntry(final String s) {
        final Object value = this.entries.get(this.convertAlias(s));
        return value != null && value instanceof KeyEntry;
    }
    
    @Override
    public boolean engineIsCertificateEntry(final String s) {
        final Object value = this.entries.get(this.convertAlias(s));
        return value != null && value instanceof TrustedCertEntry;
    }
    
    @Override
    public String engineGetCertificateAlias(final Certificate certificate) {
        final Enumeration<String> keys = this.entries.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            final Object value = this.entries.get(s);
            Certificate cert;
            if (value instanceof TrustedCertEntry) {
                cert = ((TrustedCertEntry)value).cert;
            }
            else {
                if (((KeyEntry)value).chain == null) {
                    continue;
                }
                cert = ((KeyEntry)value).chain[0];
            }
            if (cert.equals(certificate)) {
                return s;
            }
        }
        return null;
    }
    
    @Override
    public void engineStore(final OutputStream outputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        synchronized (this.entries) {
            if (array == null) {
                throw new IllegalArgumentException("password can't be null");
            }
            final MessageDigest preKeyedHash = this.getPreKeyedHash(array);
            final DataOutputStream dataOutputStream = new DataOutputStream(new DigestOutputStream(outputStream, preKeyedHash));
            dataOutputStream.writeInt(-17957139);
            dataOutputStream.writeInt(2);
            dataOutputStream.writeInt(this.entries.size());
            final Enumeration<String> keys = this.entries.keys();
            while (keys.hasMoreElements()) {
                final String s = keys.nextElement();
                final Object value = this.entries.get(s);
                if (value instanceof KeyEntry) {
                    dataOutputStream.writeInt(1);
                    dataOutputStream.writeUTF(s);
                    dataOutputStream.writeLong(((KeyEntry)value).date.getTime());
                    dataOutputStream.writeInt(((KeyEntry)value).protectedPrivKey.length);
                    dataOutputStream.write(((KeyEntry)value).protectedPrivKey);
                    int length;
                    if (((KeyEntry)value).chain == null) {
                        length = 0;
                    }
                    else {
                        length = ((KeyEntry)value).chain.length;
                    }
                    dataOutputStream.writeInt(length);
                    for (int i = 0; i < length; ++i) {
                        final byte[] encoded = ((KeyEntry)value).chain[i].getEncoded();
                        dataOutputStream.writeUTF(((KeyEntry)value).chain[i].getType());
                        dataOutputStream.writeInt(encoded.length);
                        dataOutputStream.write(encoded);
                    }
                }
                else {
                    dataOutputStream.writeInt(2);
                    dataOutputStream.writeUTF(s);
                    dataOutputStream.writeLong(((TrustedCertEntry)value).date.getTime());
                    final byte[] encoded2 = ((TrustedCertEntry)value).cert.getEncoded();
                    dataOutputStream.writeUTF(((TrustedCertEntry)value).cert.getType());
                    dataOutputStream.writeInt(encoded2.length);
                    dataOutputStream.write(encoded2);
                }
            }
            dataOutputStream.write(preKeyedHash.digest());
            dataOutputStream.flush();
        }
    }
    
    @Override
    public void engineLoad(final InputStream inputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        synchronized (this.entries) {
            MessageDigest preKeyedHash = null;
            CertificateFactory certificateFactory = null;
            Hashtable hashtable = null;
            int n = 0;
            int n2 = 0;
            if (inputStream == null) {
                return;
            }
            DataInputStream dataInputStream;
            if (array != null) {
                preKeyedHash = this.getPreKeyedHash(array);
                dataInputStream = new DataInputStream(new DigestInputStream(inputStream, preKeyedHash));
            }
            else {
                dataInputStream = new DataInputStream(inputStream);
            }
            final int int1 = dataInputStream.readInt();
            final int int2 = dataInputStream.readInt();
            if (int1 != -17957139 || (int2 != 1 && int2 != 2)) {
                throw new IOException("Invalid keystore format");
            }
            if (int2 == 1) {
                certificateFactory = CertificateFactory.getInstance("X509");
            }
            else {
                hashtable = new Hashtable(3);
            }
            this.entries.clear();
            for (int int3 = dataInputStream.readInt(), i = 0; i < int3; ++i) {
                final int int4 = dataInputStream.readInt();
                if (int4 == 1) {
                    ++n2;
                    final KeyEntry keyEntry = new KeyEntry();
                    final String utf = dataInputStream.readUTF();
                    keyEntry.date = new Date(dataInputStream.readLong());
                    keyEntry.protectedPrivKey = IOUtils.readExactlyNBytes(dataInputStream, dataInputStream.readInt());
                    final int int5 = dataInputStream.readInt();
                    if (int5 > 0) {
                        final ArrayList list = new ArrayList((int5 > 10) ? 10 : int5);
                        for (int j = 0; j < int5; ++j) {
                            if (int2 == 2) {
                                final String utf2 = dataInputStream.readUTF();
                                if (hashtable.containsKey(utf2)) {
                                    certificateFactory = (CertificateFactory)hashtable.get(utf2);
                                }
                                else {
                                    certificateFactory = CertificateFactory.getInstance(utf2);
                                    hashtable.put(utf2, certificateFactory);
                                }
                            }
                            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.readExactlyNBytes(dataInputStream, dataInputStream.readInt()));
                            list.add((Object)certificateFactory.generateCertificate(byteArrayInputStream));
                            byteArrayInputStream.close();
                        }
                        keyEntry.chain = (Certificate[])list.toArray((Object[])new Certificate[int5]);
                    }
                    this.entries.put(utf, keyEntry);
                }
                else {
                    if (int4 != 2) {
                        throw new IOException("Unrecognized keystore entry: " + int4);
                    }
                    ++n;
                    final TrustedCertEntry trustedCertEntry = new TrustedCertEntry();
                    final String utf3 = dataInputStream.readUTF();
                    trustedCertEntry.date = new Date(dataInputStream.readLong());
                    if (int2 == 2) {
                        final String utf4 = dataInputStream.readUTF();
                        if (hashtable.containsKey(utf4)) {
                            certificateFactory = (CertificateFactory)hashtable.get(utf4);
                        }
                        else {
                            certificateFactory = CertificateFactory.getInstance(utf4);
                            hashtable.put(utf4, certificateFactory);
                        }
                    }
                    final ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(IOUtils.readExactlyNBytes(dataInputStream, dataInputStream.readInt()));
                    trustedCertEntry.cert = certificateFactory.generateCertificate(byteArrayInputStream2);
                    byteArrayInputStream2.close();
                    this.entries.put(utf3, trustedCertEntry);
                }
            }
            if (JavaKeyStore.debug != null) {
                JavaKeyStore.debug.println("JavaKeyStore load: private key count: " + n2 + ". trusted key count: " + n);
            }
            if (array != null) {
                final byte[] digest = preKeyedHash.digest();
                if (!MessageDigest.isEqual(digest, IOUtils.readExactlyNBytes(dataInputStream, digest.length))) {
                    throw (IOException)new IOException("Keystore was tampered with, or password was incorrect").initCause(new UnrecoverableKeyException("Password verification failed"));
                }
            }
        }
    }
    
    private MessageDigest getPreKeyedHash(final char[] array) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest instance = MessageDigest.getInstance("SHA");
        final byte[] convertToBytes = this.convertToBytes(array);
        instance.update(convertToBytes);
        Arrays.fill(convertToBytes, (byte)0);
        instance.update("Mighty Aphrodite".getBytes("UTF8"));
        return instance;
    }
    
    private byte[] convertToBytes(final char[] array) {
        final byte[] array2 = new byte[array.length * 2];
        int i = 0;
        int n = 0;
        while (i < array.length) {
            array2[n++] = (byte)(array[i] >> 8);
            array2[n++] = (byte)array[i];
            ++i;
        }
        return array2;
    }
    
    static {
        debug = Debug.getInstance("keystore");
    }
    
    public static final class JKS extends JavaKeyStore
    {
        @Override
        String convertAlias(final String s) {
            return s.toLowerCase(Locale.ENGLISH);
        }
    }
    
    public static final class CaseExactJKS extends JavaKeyStore
    {
        @Override
        String convertAlias(final String s) {
            return s;
        }
    }
    
    public static final class DualFormatJKS extends KeyStoreDelegator
    {
        public DualFormatJKS() {
            super("JKS", JKS.class, "PKCS12", PKCS12KeyStore.class);
        }
    }
    
    private static class KeyEntry
    {
        Date date;
        byte[] protectedPrivKey;
        Certificate[] chain;
    }
    
    private static class TrustedCertEntry
    {
        Date date;
        Certificate cert;
    }
}
