package com.sun.crypto.provider;

import java.io.UnsupportedEncodingException;
import java.io.InvalidClassException;
import javax.crypto.SealedObject;
import java.security.AccessController;
import sun.misc.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.security.cert.CertificateFactory;
import java.io.DataInputStream;
import java.security.DigestInputStream;
import java.io.ByteArrayInputStream;
import sun.misc.IOUtils;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.MessageDigest;
import java.io.ObjectOutputStream;
import java.io.DataOutputStream;
import java.security.DigestOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.Date;
import java.security.cert.Certificate;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.Locale;
import java.security.Key;
import java.util.Hashtable;
import sun.security.util.Debug;
import java.security.KeyStoreSpi;

public final class JceKeyStore extends KeyStoreSpi
{
    private static final Debug debug;
    private static final int JCEKS_MAGIC = -825307442;
    private static final int JKS_MAGIC = -17957139;
    private static final int VERSION_1 = 1;
    private static final int VERSION_2 = 2;
    private Hashtable<String, Object> entries;
    
    public JceKeyStore() {
        this.entries = new Hashtable<String, Object>();
    }
    
    @Override
    public Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        final SecretKeyEntry value = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (!(value instanceof PrivateKeyEntry) && !(value instanceof SecretKeyEntry)) {
            return null;
        }
        final KeyProtector keyProtector = new KeyProtector(array);
        Key key;
        if (value instanceof PrivateKeyEntry) {
            final byte[] protectedKey = ((PrivateKeyEntry)value).protectedKey;
            EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
            try {
                encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(protectedKey);
            }
            catch (final IOException ex) {
                throw new UnrecoverableKeyException("Private key not stored as PKCS #8 EncryptedPrivateKeyInfo");
            }
            key = keyProtector.recover(encryptedPrivateKeyInfo);
        }
        else {
            final SecretKeyEntry secretKeyEntry = value;
            key = keyProtector.unseal(secretKeyEntry.sealedKey, secretKeyEntry.maxLength);
        }
        return key;
    }
    
    @Override
    public Certificate[] engineGetCertificateChain(final String s) {
        Certificate[] array = null;
        final PrivateKeyEntry value = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (value instanceof PrivateKeyEntry && value.chain != null) {
            array = value.chain.clone();
        }
        return array;
    }
    
    @Override
    public Certificate engineGetCertificate(final String s) {
        Certificate cert = null;
        final PrivateKeyEntry value = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (value != null) {
            if (value instanceof TrustedCertEntry) {
                cert = ((TrustedCertEntry)value).cert;
            }
            else if (value instanceof PrivateKeyEntry && value.chain != null) {
                cert = value.chain[0];
            }
        }
        return cert;
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        Date date = null;
        final SecretKeyEntry value = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (value != null) {
            if (value instanceof TrustedCertEntry) {
                date = new Date(((TrustedCertEntry)value).date.getTime());
            }
            else if (value instanceof PrivateKeyEntry) {
                date = new Date(((PrivateKeyEntry)value).date.getTime());
            }
            else {
                date = new Date(value.date.getTime());
            }
        }
        return date;
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final Key key, final char[] array, final Certificate[] array2) throws KeyStoreException {
        synchronized (this.entries) {
            try {
                final KeyProtector keyProtector = new KeyProtector(array);
                if (key instanceof PrivateKey) {
                    final PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry();
                    privateKeyEntry.date = new Date();
                    privateKeyEntry.protectedKey = keyProtector.protect((PrivateKey)key);
                    if (array2 != null && array2.length != 0) {
                        privateKeyEntry.chain = array2.clone();
                    }
                    else {
                        privateKeyEntry.chain = null;
                    }
                    this.entries.put(s.toLowerCase(Locale.ENGLISH), privateKeyEntry);
                }
                else {
                    final SecretKeyEntry secretKeyEntry = new SecretKeyEntry();
                    secretKeyEntry.date = new Date();
                    secretKeyEntry.sealedKey = keyProtector.seal(key);
                    secretKeyEntry.maxLength = Integer.MAX_VALUE;
                    this.entries.put(s.toLowerCase(Locale.ENGLISH), secretKeyEntry);
                }
            }
            catch (final Exception ex) {
                throw new KeyStoreException(ex.getMessage());
            }
        }
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        synchronized (this.entries) {
            final PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry();
            privateKeyEntry.date = new Date();
            privateKeyEntry.protectedKey = array.clone();
            if (array2 != null && array2.length != 0) {
                privateKeyEntry.chain = array2.clone();
            }
            else {
                privateKeyEntry.chain = null;
            }
            this.entries.put(s.toLowerCase(Locale.ENGLISH), privateKeyEntry);
        }
    }
    
    @Override
    public void engineSetCertificateEntry(final String s, final Certificate cert) throws KeyStoreException {
        synchronized (this.entries) {
            final Object value = this.entries.get(s.toLowerCase(Locale.ENGLISH));
            if (value != null) {
                if (value instanceof PrivateKeyEntry) {
                    throw new KeyStoreException("Cannot overwrite own certificate");
                }
                if (value instanceof SecretKeyEntry) {
                    throw new KeyStoreException("Cannot overwrite secret key");
                }
            }
            final TrustedCertEntry trustedCertEntry = new TrustedCertEntry();
            trustedCertEntry.cert = cert;
            trustedCertEntry.date = new Date();
            this.entries.put(s.toLowerCase(Locale.ENGLISH), trustedCertEntry);
        }
    }
    
    @Override
    public void engineDeleteEntry(final String s) throws KeyStoreException {
        synchronized (this.entries) {
            this.entries.remove(s.toLowerCase(Locale.ENGLISH));
        }
    }
    
    @Override
    public Enumeration<String> engineAliases() {
        return this.entries.keys();
    }
    
    @Override
    public boolean engineContainsAlias(final String s) {
        return this.entries.containsKey(s.toLowerCase(Locale.ENGLISH));
    }
    
    @Override
    public int engineSize() {
        return this.entries.size();
    }
    
    @Override
    public boolean engineIsKeyEntry(final String s) {
        boolean b = false;
        final Object value = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (value instanceof PrivateKeyEntry || value instanceof SecretKeyEntry) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean engineIsCertificateEntry(final String s) {
        boolean b = false;
        if (this.entries.get(s.toLowerCase(Locale.ENGLISH)) instanceof TrustedCertEntry) {
            b = true;
        }
        return b;
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
                if (!(value instanceof PrivateKeyEntry) || ((PrivateKeyEntry)value).chain == null) {
                    continue;
                }
                cert = ((PrivateKeyEntry)value).chain[0];
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
            ObjectOutputStream objectOutputStream = null;
            try {
                dataOutputStream.writeInt(-825307442);
                dataOutputStream.writeInt(2);
                dataOutputStream.writeInt(this.entries.size());
                final Enumeration<String> keys = this.entries.keys();
                while (keys.hasMoreElements()) {
                    final String s = keys.nextElement();
                    final Object value = this.entries.get(s);
                    if (value instanceof PrivateKeyEntry) {
                        final PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry)value;
                        dataOutputStream.writeInt(1);
                        dataOutputStream.writeUTF(s);
                        dataOutputStream.writeLong(privateKeyEntry.date.getTime());
                        dataOutputStream.writeInt(privateKeyEntry.protectedKey.length);
                        dataOutputStream.write(privateKeyEntry.protectedKey);
                        int length;
                        if (privateKeyEntry.chain == null) {
                            length = 0;
                        }
                        else {
                            length = privateKeyEntry.chain.length;
                        }
                        dataOutputStream.writeInt(length);
                        for (int i = 0; i < length; ++i) {
                            final byte[] encoded = privateKeyEntry.chain[i].getEncoded();
                            dataOutputStream.writeUTF(privateKeyEntry.chain[i].getType());
                            dataOutputStream.writeInt(encoded.length);
                            dataOutputStream.write(encoded);
                        }
                    }
                    else if (value instanceof TrustedCertEntry) {
                        dataOutputStream.writeInt(2);
                        dataOutputStream.writeUTF(s);
                        dataOutputStream.writeLong(((TrustedCertEntry)value).date.getTime());
                        final byte[] encoded2 = ((TrustedCertEntry)value).cert.getEncoded();
                        dataOutputStream.writeUTF(((TrustedCertEntry)value).cert.getType());
                        dataOutputStream.writeInt(encoded2.length);
                        dataOutputStream.write(encoded2);
                    }
                    else {
                        dataOutputStream.writeInt(3);
                        dataOutputStream.writeUTF(s);
                        dataOutputStream.writeLong(((SecretKeyEntry)value).date.getTime());
                        objectOutputStream = new ObjectOutputStream(dataOutputStream);
                        objectOutputStream.writeObject(((SecretKeyEntry)value).sealedKey);
                    }
                }
                dataOutputStream.write(preKeyedHash.digest());
                dataOutputStream.flush();
            }
            finally {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                else {
                    dataOutputStream.close();
                }
            }
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
            int n3 = 0;
            if (inputStream == null) {
                return;
            }
            final byte[] allBytes = IOUtils.readAllBytes(inputStream);
            final int length = allBytes.length;
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(allBytes);
            DataInputStream dataInputStream;
            if (array != null) {
                preKeyedHash = this.getPreKeyedHash(array);
                dataInputStream = new DataInputStream(new DigestInputStream(byteArrayInputStream, preKeyedHash));
            }
            else {
                dataInputStream = new DataInputStream(byteArrayInputStream);
            }
            ObjectInputStream objectInputStream = null;
            try {
                final int int1 = dataInputStream.readInt();
                final int int2 = dataInputStream.readInt();
                if ((int1 != -825307442 && int1 != -17957139) || (int2 != 1 && int2 != 2)) {
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
                        final PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry();
                        final String utf = dataInputStream.readUTF();
                        privateKeyEntry.date = new Date(dataInputStream.readLong());
                        privateKeyEntry.protectedKey = IOUtils.readExactlyNBytes((InputStream)dataInputStream, dataInputStream.readInt());
                        final int int5 = dataInputStream.readInt();
                        final ArrayList list = new ArrayList();
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
                            list.add(certificateFactory.generateCertificate(new ByteArrayInputStream(IOUtils.readExactlyNBytes((InputStream)dataInputStream, dataInputStream.readInt()))));
                        }
                        privateKeyEntry.chain = (Certificate[])list.toArray(new Certificate[int5]);
                        this.entries.put(utf, privateKeyEntry);
                    }
                    else if (int4 == 2) {
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
                        trustedCertEntry.cert = certificateFactory.generateCertificate(new ByteArrayInputStream(IOUtils.readExactlyNBytes((InputStream)dataInputStream, dataInputStream.readInt())));
                        this.entries.put(utf3, trustedCertEntry);
                    }
                    else {
                        if (int4 != 3) {
                            throw new IOException("Unrecognized keystore entry: " + int4);
                        }
                        ++n3;
                        final SecretKeyEntry secretKeyEntry = new SecretKeyEntry();
                        final String utf5 = dataInputStream.readUTF();
                        secretKeyEntry.date = new Date(dataInputStream.readLong());
                        try {
                            objectInputStream = new ObjectInputStream(dataInputStream);
                            AccessController.doPrivileged(() -> {
                                ObjectInputFilter.Config.setObjectInputFilter(objectInputStream2, (ObjectInputFilter)new DeserializationChecker(n4));
                                return null;
                            });
                            secretKeyEntry.sealedKey = (SealedObject)objectInputStream.readObject();
                            secretKeyEntry.maxLength = length;
                        }
                        catch (final ClassNotFoundException ex) {
                            throw new IOException(ex.getMessage());
                        }
                        catch (final InvalidClassException ex2) {
                            throw new IOException("Invalid secret key format");
                        }
                        this.entries.put(utf5, secretKeyEntry);
                    }
                }
                if (JceKeyStore.debug != null) {
                    JceKeyStore.debug.println("JceKeyStore load: private key count: " + n2 + ". trusted key count: " + n + ". secret key count: " + n3);
                }
                if (array != null) {
                    final byte[] digest = preKeyedHash.digest();
                    if (!MessageDigest.isEqual(digest, IOUtils.readExactlyNBytes((InputStream)dataInputStream, digest.length))) {
                        throw new IOException("Keystore was tampered with, or password was incorrect", new UnrecoverableKeyException("Password verification failed"));
                    }
                }
            }
            finally {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                else {
                    dataInputStream.close();
                }
            }
        }
    }
    
    private MessageDigest getPreKeyedHash(final char[] array) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest instance = MessageDigest.getInstance("SHA");
        final byte[] array2 = new byte[array.length * 2];
        int i = 0;
        int n = 0;
        while (i < array.length) {
            array2[n++] = (byte)(array[i] >> 8);
            array2[n++] = (byte)array[i];
            ++i;
        }
        instance.update(array2);
        for (int j = 0; j < array2.length; ++j) {
            array2[j] = 0;
        }
        instance.update("Mighty Aphrodite".getBytes("UTF8"));
        return instance;
    }
    
    static {
        debug = Debug.getInstance("keystore");
    }
    
    private static final class PrivateKeyEntry
    {
        Date date;
        byte[] protectedKey;
        Certificate[] chain;
    }
    
    private static final class SecretKeyEntry
    {
        Date date;
        SealedObject sealedKey;
        int maxLength;
    }
    
    private static final class TrustedCertEntry
    {
        Date date;
        Certificate cert;
    }
    
    private static class DeserializationChecker implements ObjectInputFilter
    {
        private final int fullLength;
        
        public DeserializationChecker(final int fullLength) {
            this.fullLength = fullLength;
        }
        
        public ObjectInputFilter.Status checkInput(final ObjectInputFilter.FilterInfo filterInfo) {
            if (filterInfo.arrayLength() > this.fullLength) {
                return ObjectInputFilter.Status.REJECTED;
            }
            final Class serialClass = filterInfo.serialClass();
            switch ((int)filterInfo.depth()) {
                case 1: {
                    if (serialClass != SealedObjectForKeyProtector.class) {
                        return ObjectInputFilter.Status.REJECTED;
                    }
                    break;
                }
                case 2: {
                    if (serialClass != null && serialClass != SealedObject.class && serialClass != byte[].class) {
                        return ObjectInputFilter.Status.REJECTED;
                    }
                    break;
                }
                default: {
                    if (serialClass != null && serialClass != Object.class) {
                        return ObjectInputFilter.Status.REJECTED;
                    }
                    break;
                }
            }
            final ObjectInputFilter serialFilter = ObjectInputFilter.Config.getSerialFilter();
            if (serialFilter != null) {
                return serialFilter.checkInput(filterInfo);
            }
            return ObjectInputFilter.Status.UNDECIDED;
        }
    }
}
