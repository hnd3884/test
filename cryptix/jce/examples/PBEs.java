package cryptix.jce.examples;

import java.security.spec.KeySpec;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.crypto.CipherInputStream;
import java.io.OutputStream;
import javax.crypto.CipherOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;

public final class PBEs
{
    private String algorithm;
    private String filename;
    private Key secretKey;
    private final byte[] salt;
    private final int iterations;
    
    public boolean run(final int mode, final String filename) {
        boolean ret = false;
        final AlgorithmParameterSpec aps = new PBEParameterSpec(this.salt, 20);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(this.algorithm);
            cipher.init(mode, this.secretKey, aps);
            ret = true;
        }
        catch (final NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        catch (final NoSuchPaddingException nspe) {
            nspe.printStackTrace();
        }
        catch (final InvalidKeyException ike) {
            ike.printStackTrace();
        }
        catch (final InvalidAlgorithmParameterException iape) {
            iape.printStackTrace();
        }
        if (ret) {
            ret = false;
            CipherOutputStream cOutStream = null;
            CipherInputStream cInpStream = null;
            FileInputStream fInput = null;
            FileOutputStream fOutput = null;
            try {
                if (mode == 1) {
                    fInput = new FileInputStream(filename);
                    fOutput = new FileOutputStream(filename + ".PBEencrypted." + this.algorithm);
                    cOutStream = new CipherOutputStream(fOutput, cipher);
                    final byte[] buffer = new byte[8192];
                    int length = 0;
                    while ((length = fInput.read(buffer)) != -1) {
                        cOutStream.write(buffer, 0, length);
                    }
                    fInput.close();
                    cOutStream.close();
                    ret = true;
                }
                else {
                    fInput = new FileInputStream(filename + ".PBEencrypted." + this.algorithm);
                    cInpStream = new CipherInputStream(fInput, cipher);
                    fOutput = new FileOutputStream(filename + ".PBEdecrypted." + this.algorithm);
                    final byte[] buffer = new byte[8192];
                    int length = 0;
                    while ((length = cInpStream.read(buffer)) != -1) {
                        fOutput.write(buffer, 0, length);
                    }
                    cInpStream.close();
                    fOutput.close();
                    ret = true;
                }
            }
            catch (final FileNotFoundException ex) {
                ex.printStackTrace();
                return false;
            }
            catch (final IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
            return ret;
        }
        return ret;
    }
    
    public PBEs(final String algorithm, final String passphrase) {
        this.algorithm = null;
        this.filename = null;
        this.secretKey = null;
        this.salt = new byte[] { 32, 33, 16, 85, -125, 1, 1, -111 };
        this.iterations = 20;
        this.algorithm = algorithm;
        final char[] pw = new char[passphrase.length()];
        passphrase.getChars(0, passphrase.length(), pw, 0);
        final KeySpec ks = new PBEKeySpec(pw);
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance(algorithm);
            this.secretKey = skf.generateSecret(ks);
        }
        catch (final NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        catch (final InvalidKeySpecException ikse) {
            ikse.printStackTrace();
        }
    }
}
