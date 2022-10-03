package cryptix.jce.examples;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import java.security.Key;
import javax.crypto.Mac;

public final class MACs
{
    private Mac mac;
    private Key key;
    
    public boolean run(final String algorithm, final String provider, final String filename) {
        try {
            final KeyGenerator kg = KeyGenerator.getInstance("HMAC", provider);
            kg.init(new SecureRandom());
            this.key = kg.generateKey();
            (this.mac = Mac.getInstance(algorithm, provider)).init(this.key);
        }
        catch (final NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return false;
        }
        catch (final NoSuchProviderException ex2) {
            ex2.printStackTrace();
            return false;
        }
        catch (final InvalidKeyException ike) {
            ike.printStackTrace();
            return false;
        }
        try {
            final FileInputStream fInput = new FileInputStream(filename);
            final FileOutputStream fOutput = new FileOutputStream(filename + "." + algorithm);
            final byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = fInput.read(buffer)) != -1) {
                this.mac.update(buffer);
            }
            fOutput.write(this.mac.doFinal());
            fInput.close();
            fOutput.close();
        }
        catch (final FileNotFoundException ex3) {
            ex3.printStackTrace();
            return false;
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }
    
    public MACs() {
        this.mac = null;
        this.key = null;
    }
}
