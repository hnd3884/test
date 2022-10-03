package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

public class PKCS8EncryptedPrivateKeyInfoBuilder
{
    private PrivateKeyInfo privateKeyInfo;
    
    public PKCS8EncryptedPrivateKeyInfoBuilder(final byte[] array) {
        this(PrivateKeyInfo.getInstance((Object)array));
    }
    
    public PKCS8EncryptedPrivateKeyInfoBuilder(final PrivateKeyInfo privateKeyInfo) {
        this.privateKeyInfo = privateKeyInfo;
    }
    
    public PKCS8EncryptedPrivateKeyInfo build(final OutputEncryptor outputEncryptor) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final OutputStream outputStream = outputEncryptor.getOutputStream(byteArrayOutputStream);
            outputStream.write(this.privateKeyInfo.getEncoded());
            outputStream.close();
            return new PKCS8EncryptedPrivateKeyInfo(new EncryptedPrivateKeyInfo(outputEncryptor.getAlgorithmIdentifier(), byteArrayOutputStream.toByteArray()));
        }
        catch (final IOException ex) {
            throw new IllegalStateException("cannot encode privateKeyInfo");
        }
    }
}
