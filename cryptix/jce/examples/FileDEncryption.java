package cryptix.jce.examples;

import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
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
import java.security.NoSuchProviderException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import java.security.Key;

public final class FileDEncryption
{
    private String inputFile;
    private String outputFile;
    private Key secretKey;
    private String keyfile;
    private String alg;
    private boolean running_enc;
    private String provider;
    private String mode;
    private byte[] iv;
    
    private boolean ciphering(final int way) {
        boolean ret = false;
        boolean ok = false;
        IvParameterSpec ivSpec = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(this.alg, this.provider);
            if (this.mode == "ECB") {
                cipher.init(way, this.secretKey);
            }
            else {
                ivSpec = new IvParameterSpec(this.iv);
                cipher.init(way, this.secretKey, ivSpec);
            }
            ok = true;
        }
        catch (final NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        catch (final NoSuchPaddingException nspe) {
            nspe.printStackTrace();
        }
        catch (final NoSuchProviderException nspe2) {
            nspe2.printStackTrace();
        }
        catch (final InvalidKeyException ike) {
            ike.printStackTrace();
        }
        catch (final InvalidAlgorithmParameterException iape) {
            iape.printStackTrace();
        }
        if (!ok) {
            return false;
        }
        FileInputStream fInput = null;
        FileOutputStream fOutput = null;
        CipherOutputStream cStr = null;
        try {
            if (this.running_enc) {
                fInput = new FileInputStream(this.inputFile);
                fOutput = new FileOutputStream(this.outputFile + ".encrypted");
                cStr = new CipherOutputStream(fOutput, cipher);
                final byte[] buffer = new byte[8192];
                int length = 0;
                while ((length = fInput.read(buffer)) != -1) {
                    cStr.write(buffer, 0, length);
                }
                fInput.close();
                cStr.close();
            }
            else {
                CipherInputStream ciStr = null;
                fInput = new FileInputStream(this.outputFile + ".encrypted");
                ciStr = new CipherInputStream(fInput, cipher);
                fOutput = new FileOutputStream(this.outputFile + ".decrypted");
                final byte[] buffer2 = new byte[8192];
                int length2 = 0;
                while ((length2 = ciStr.read(buffer2)) != -1) {
                    fOutput.write(buffer2, 0, length2);
                }
                ciStr.close();
                fOutput.close();
            }
            ret = true;
        }
        catch (final FileNotFoundException fnfe) {
            System.out.println("File not found! " + fnfe.getMessage());
            fnfe.printStackTrace();
        }
        catch (final IOException ioe) {
            System.out.println("IOException! " + ioe.getMessage());
            ioe.printStackTrace();
        }
        return ret;
    }
    
    private boolean getKeyWithIV() {
        boolean res = false;
        FileInputStream fInput = null;
        try {
            fInput = new FileInputStream(this.keyfile);
            final ObjectInputStream objIStream = new ObjectInputStream(fInput);
            this.secretKey = (Key)objIStream.readObject();
            this.iv = new byte[fInput.available()];
            if (this.mode != "ECB") {}
            fInput.read(this.iv);
            objIStream.close();
            System.out.println("Read key!\n");
            res = true;
        }
        catch (final StreamCorruptedException sce) {
            sce.printStackTrace();
        }
        catch (final FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        catch (final OptionalDataException ode) {
            ode.printStackTrace();
        }
        catch (final ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        return res;
    }
    
    public final synchronized boolean go() {
        this.running_enc = true;
        boolean result = this.saveKeyWithIV();
        if (result) {
            result = this.ciphering(1);
        }
        else {
            result = !result;
        }
        return result;
    }
    
    public final synchronized boolean reTurn() {
        this.running_enc = false;
        boolean result = this.getKeyWithIV();
        if (result) {
            result = this.ciphering(2);
        }
        else {
            result = !result;
        }
        return result;
    }
    
    private boolean saveKeyWithIV() {
        boolean res = false;
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(this.keyfile);
            final ObjectOutputStream objStr = new ObjectOutputStream(fOut);
            objStr.writeObject(this.secretKey);
            if (this.mode != "ECB") {
                fOut.write(this.iv);
            }
            objStr.close();
            fOut.close();
            res = true;
        }
        catch (final FileNotFoundException fnfe) {
            System.out.println("File not found!\n");
        }
        catch (final IOException ioe) {
            System.out.println("IOException!\n");
        }
        return res;
    }
    
    public FileDEncryption(final String filename, final Key key, final byte[] iVector, final String algorithm, final String amode, final String padding, final String prov) {
        this.inputFile = filename;
        this.outputFile = filename + "." + algorithm + "." + amode + "." + padding;
        this.alg = algorithm + "/" + amode + "/" + padding;
        this.mode = amode;
        this.secretKey = key;
        this.iv = iVector;
        this.keyfile = this.outputFile + "." + "key";
        this.provider = prov;
    }
}
