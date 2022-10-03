package org.apache.poi.poifs.crypt.temp;

import javax.crypto.CipherInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.security.Key;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.ChainingMode;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.io.IOException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.xssf.streaming.SheetDataWriter;

public class SheetDataWriterWithDecorator extends SheetDataWriter
{
    static final CipherAlgorithm cipherAlgorithm;
    SecretKeySpec skeySpec;
    byte[] ivBytes;
    
    public SheetDataWriterWithDecorator() throws IOException {
    }
    
    void init() {
        if (this.skeySpec == null) {
            final SecureRandom sr = new SecureRandom();
            this.ivBytes = new byte[16];
            final byte[] keyBytes = new byte[16];
            sr.nextBytes(this.ivBytes);
            sr.nextBytes(keyBytes);
            this.skeySpec = new SecretKeySpec(keyBytes, SheetDataWriterWithDecorator.cipherAlgorithm.jceId);
        }
    }
    
    @Override
    protected OutputStream decorateOutputStream(final FileOutputStream fos) {
        this.init();
        final Cipher ciEnc = CryptoFunctions.getCipher((Key)this.skeySpec, SheetDataWriterWithDecorator.cipherAlgorithm, ChainingMode.cbc, this.ivBytes, 1, "PKCS5Padding");
        return new CipherOutputStream(fos, ciEnc);
    }
    
    @Override
    protected InputStream decorateInputStream(final FileInputStream fis) {
        final Cipher ciDec = CryptoFunctions.getCipher((Key)this.skeySpec, SheetDataWriterWithDecorator.cipherAlgorithm, ChainingMode.cbc, this.ivBytes, 2, "PKCS5Padding");
        return new CipherInputStream(fis, ciDec);
    }
    
    static {
        cipherAlgorithm = CipherAlgorithm.aes128;
    }
}
