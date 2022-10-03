package org.apache.poi.poifs.crypt.temp;

import org.apache.poi.util.POILogFactory;
import javax.crypto.CipherInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.FileOutputStream;
import java.security.Key;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.ChainingMode;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.poi.util.TempFile;
import java.security.SecureRandom;
import java.io.File;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.util.POILogger;

public class EncryptedTempData
{
    private static POILogger LOG;
    private static final CipherAlgorithm cipherAlgorithm;
    private static final String PADDING = "PKCS5Padding";
    private final SecretKeySpec skeySpec;
    private final byte[] ivBytes;
    private final File tempFile;
    
    public EncryptedTempData() throws IOException {
        final SecureRandom sr = new SecureRandom();
        this.ivBytes = new byte[16];
        final byte[] keyBytes = new byte[16];
        sr.nextBytes(this.ivBytes);
        sr.nextBytes(keyBytes);
        this.skeySpec = new SecretKeySpec(keyBytes, EncryptedTempData.cipherAlgorithm.jceId);
        this.tempFile = TempFile.createTempFile("poi-temp-data", ".tmp");
    }
    
    public OutputStream getOutputStream() throws IOException {
        final Cipher ciEnc = CryptoFunctions.getCipher((Key)this.skeySpec, EncryptedTempData.cipherAlgorithm, ChainingMode.cbc, this.ivBytes, 1, "PKCS5Padding");
        return new CipherOutputStream(new FileOutputStream(this.tempFile), ciEnc);
    }
    
    public InputStream getInputStream() throws IOException {
        final Cipher ciDec = CryptoFunctions.getCipher((Key)this.skeySpec, EncryptedTempData.cipherAlgorithm, ChainingMode.cbc, this.ivBytes, 2, "PKCS5Padding");
        return new CipherInputStream(new FileInputStream(this.tempFile), ciDec);
    }
    
    public void dispose() {
        if (!this.tempFile.delete()) {
            EncryptedTempData.LOG.log(5, new Object[] { this.tempFile.getAbsolutePath() + " can't be removed (or was already removed." });
        }
    }
    
    static {
        EncryptedTempData.LOG = POILogFactory.getLogger((Class)EncryptedTempData.class);
        cipherAlgorithm = CipherAlgorithm.aes128;
    }
}
