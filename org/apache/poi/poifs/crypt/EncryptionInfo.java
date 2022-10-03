package org.apache.poi.poifs.crypt;

import org.apache.poi.util.BitFieldFactory;
import java.util.Collections;
import org.apache.poi.util.GenericRecordUtil;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.BitField;
import org.apache.poi.common.usermodel.GenericRecord;

public class EncryptionInfo implements GenericRecord
{
    public static final BitField flagCryptoAPI;
    public static final BitField flagDocProps;
    public static final BitField flagExternal;
    public static final BitField flagAES;
    private static final int[] FLAGS_MASKS;
    private static final String[] FLAGS_NAMES;
    private final EncryptionMode encryptionMode;
    private final int versionMajor;
    private final int versionMinor;
    private final int encryptionFlags;
    private EncryptionHeader header;
    private EncryptionVerifier verifier;
    private Decryptor decryptor;
    private Encryptor encryptor;
    
    public EncryptionInfo(final POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }
    
    public EncryptionInfo(final DirectoryNode dir) throws IOException {
        this(dir.createDocumentInputStream("EncryptionInfo"), null);
    }
    
    public EncryptionInfo(final LittleEndianInput dis, final EncryptionMode preferredEncryptionMode) throws IOException {
        if (preferredEncryptionMode == EncryptionMode.xor) {
            this.versionMajor = EncryptionMode.xor.versionMajor;
            this.versionMinor = EncryptionMode.xor.versionMinor;
        }
        else {
            this.versionMajor = dis.readUShort();
            this.versionMinor = dis.readUShort();
        }
        if (this.versionMajor == EncryptionMode.xor.versionMajor && this.versionMinor == EncryptionMode.xor.versionMinor) {
            this.encryptionMode = EncryptionMode.xor;
            this.encryptionFlags = -1;
        }
        else if (this.versionMajor == EncryptionMode.binaryRC4.versionMajor && this.versionMinor == EncryptionMode.binaryRC4.versionMinor) {
            this.encryptionMode = EncryptionMode.binaryRC4;
            this.encryptionFlags = -1;
        }
        else if (2 <= this.versionMajor && this.versionMajor <= 4 && this.versionMinor == 2) {
            this.encryptionFlags = dis.readInt();
            this.encryptionMode = ((preferredEncryptionMode == EncryptionMode.cryptoAPI || !EncryptionInfo.flagAES.isSet(this.encryptionFlags)) ? EncryptionMode.cryptoAPI : EncryptionMode.standard);
        }
        else {
            if (this.versionMajor != EncryptionMode.agile.versionMajor || this.versionMinor != EncryptionMode.agile.versionMinor) {
                this.encryptionFlags = dis.readInt();
                throw new EncryptedDocumentException("Unknown encryption: version major: " + this.versionMajor + " / version minor: " + this.versionMinor + " / fCrypto: " + EncryptionInfo.flagCryptoAPI.isSet(this.encryptionFlags) + " / fExternal: " + EncryptionInfo.flagExternal.isSet(this.encryptionFlags) + " / fDocProps: " + EncryptionInfo.flagDocProps.isSet(this.encryptionFlags) + " / fAES: " + EncryptionInfo.flagAES.isSet(this.encryptionFlags));
            }
            this.encryptionMode = EncryptionMode.agile;
            this.encryptionFlags = dis.readInt();
        }
        EncryptionInfoBuilder eib;
        try {
            eib = getBuilder(this.encryptionMode);
        }
        catch (final Exception e) {
            throw new IOException(e);
        }
        eib.initialize(this, dis);
    }
    
    public EncryptionInfo(final EncryptionMode encryptionMode) {
        this(encryptionMode, null, null, -1, -1, null);
    }
    
    public EncryptionInfo(final EncryptionMode encryptionMode, final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this.encryptionMode = encryptionMode;
        this.versionMajor = encryptionMode.versionMajor;
        this.versionMinor = encryptionMode.versionMinor;
        this.encryptionFlags = encryptionMode.encryptionFlags;
        EncryptionInfoBuilder eib;
        try {
            eib = getBuilder(encryptionMode);
        }
        catch (final Exception e) {
            throw new EncryptedDocumentException(e);
        }
        eib.initialize(this, cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
    }
    
    public EncryptionInfo(final EncryptionInfo other) {
        this.encryptionMode = other.encryptionMode;
        this.versionMajor = other.versionMajor;
        this.versionMinor = other.versionMinor;
        this.encryptionFlags = other.encryptionFlags;
        this.header = ((other.header == null) ? null : other.header.copy());
        this.verifier = ((other.verifier == null) ? null : other.verifier.copy());
        if (other.decryptor != null) {
            (this.decryptor = other.decryptor.copy()).setEncryptionInfo(this);
        }
        if (other.encryptor != null) {
            (this.encryptor = other.encryptor.copy()).setEncryptionInfo(this);
        }
    }
    
    protected static EncryptionInfoBuilder getBuilder(final EncryptionMode encryptionMode) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final ClassLoader cl = EncryptionInfo.class.getClassLoader();
        final EncryptionInfoBuilder eib = (EncryptionInfoBuilder)cl.loadClass(encryptionMode.builder).newInstance();
        return eib;
    }
    
    public int getVersionMajor() {
        return this.versionMajor;
    }
    
    public int getVersionMinor() {
        return this.versionMinor;
    }
    
    public int getEncryptionFlags() {
        return this.encryptionFlags;
    }
    
    public EncryptionHeader getHeader() {
        return this.header;
    }
    
    public EncryptionVerifier getVerifier() {
        return this.verifier;
    }
    
    public Decryptor getDecryptor() {
        return this.decryptor;
    }
    
    public Encryptor getEncryptor() {
        return this.encryptor;
    }
    
    public void setHeader(final EncryptionHeader header) {
        this.header = header;
    }
    
    public void setVerifier(final EncryptionVerifier verifier) {
        this.verifier = verifier;
    }
    
    public void setDecryptor(final Decryptor decryptor) {
        this.decryptor = decryptor;
    }
    
    public void setEncryptor(final Encryptor encryptor) {
        this.encryptor = encryptor;
    }
    
    public EncryptionMode getEncryptionMode() {
        return this.encryptionMode;
    }
    
    public boolean isDocPropsEncrypted() {
        return !EncryptionInfo.flagDocProps.isSet(this.getEncryptionFlags());
    }
    
    public EncryptionInfo copy() {
        return new EncryptionInfo(this);
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        final Map<String, Supplier<?>> m = new LinkedHashMap<String, Supplier<?>>();
        m.put("encryptionMode", this::getEncryptionMode);
        m.put("versionMajor", this::getVersionMajor);
        m.put("versionMinor", this::getVersionMinor);
        m.put("encryptionFlags", GenericRecordUtil.getBitsAsString((Supplier<Number>)this::getEncryptionFlags, EncryptionInfo.FLAGS_MASKS, EncryptionInfo.FLAGS_NAMES));
        m.put("header", this::getHeader);
        m.put("verifier", this::getVerifier);
        m.put("decryptor", this::getDecryptor);
        m.put("encryptor", this::getEncryptor);
        return Collections.unmodifiableMap((Map<? extends String, ? extends Supplier<?>>)m);
    }
    
    static {
        flagCryptoAPI = BitFieldFactory.getInstance(4);
        flagDocProps = BitFieldFactory.getInstance(8);
        flagExternal = BitFieldFactory.getInstance(16);
        flagAES = BitFieldFactory.getInstance(32);
        FLAGS_MASKS = new int[] { 4, 8, 16, 32 };
        FLAGS_NAMES = new String[] { "CRYPTO_API", "DOC_PROPS", "EXTERNAL", "AES" };
    }
}
