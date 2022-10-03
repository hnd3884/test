package org.bouncycastle.pkcs;

import org.bouncycastle.util.io.Streams;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.operator.InputDecryptorProvider;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;

public class PKCS8EncryptedPrivateKeyInfo
{
    private EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
    
    private static EncryptedPrivateKeyInfo parseBytes(final byte[] array) throws IOException {
        try {
            return EncryptedPrivateKeyInfo.getInstance((Object)ASN1Primitive.fromByteArray(array));
        }
        catch (final ClassCastException ex) {
            throw new PKCSIOException("malformed data: " + ex.getMessage(), ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new PKCSIOException("malformed data: " + ex2.getMessage(), ex2);
        }
    }
    
    public PKCS8EncryptedPrivateKeyInfo(final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo) {
        this.encryptedPrivateKeyInfo = encryptedPrivateKeyInfo;
    }
    
    public PKCS8EncryptedPrivateKeyInfo(final byte[] array) throws IOException {
        this(parseBytes(array));
    }
    
    public EncryptedPrivateKeyInfo toASN1Structure() {
        return this.encryptedPrivateKeyInfo;
    }
    
    public byte[] getEncoded() throws IOException {
        return this.encryptedPrivateKeyInfo.getEncoded();
    }
    
    public PrivateKeyInfo decryptPrivateKeyInfo(final InputDecryptorProvider inputDecryptorProvider) throws PKCSException {
        try {
            return PrivateKeyInfo.getInstance((Object)Streams.readAll(inputDecryptorProvider.get(this.encryptedPrivateKeyInfo.getEncryptionAlgorithm()).getInputStream(new ByteArrayInputStream(this.encryptedPrivateKeyInfo.getEncryptedData()))));
        }
        catch (final Exception ex) {
            throw new PKCSException("unable to read encrypted data: " + ex.getMessage(), ex);
        }
    }
}
