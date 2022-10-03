package cryptix.jce.examples;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import sun.misc.BASE64Encoder;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class FileHashing
{
    private MessageDigest md;
    private byte[] hash_raw;
    
    public boolean run(final String hashfunction, final String provider, final String filename) {
        boolean ret = false;
        try {
            this.md = MessageDigest.getInstance(hashfunction, provider);
        }
        catch (final NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return false;
        }
        catch (final NoSuchProviderException nspe) {
            nspe.printStackTrace();
            return false;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            final byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = fis.read(buffer)) != -1) {
                this.md.update(buffer, 0, length);
            }
            this.hash_raw = this.md.digest();
        }
        catch (final FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        final BASE64Encoder enc = new BASE64Encoder();
        final String base64 = enc.encode(this.hash_raw);
        System.out.println(base64);
        try {
            final FileOutputStream fout = new FileOutputStream(filename + "." + hashfunction);
            final DataOutputStream dout = new DataOutputStream(fout);
            dout.writeBytes(base64);
            dout.close();
            ret = true;
        }
        catch (final FileNotFoundException fnfe2) {
            fnfe2.printStackTrace();
        }
        catch (final IOException ioe2) {
            ioe2.printStackTrace();
        }
        return ret;
    }
    
    public FileHashing() {
        this.md = null;
        this.hash_raw = null;
    }
}
