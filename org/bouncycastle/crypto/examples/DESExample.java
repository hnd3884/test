package org.bouncycastle.crypto.examples;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

public class DESExample
{
    private boolean encrypt;
    private PaddedBufferedBlockCipher cipher;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private byte[] key;
    
    public static void main(final String[] array) {
        boolean b = true;
        if (array.length < 2) {
            System.err.println("Usage: java " + new DESExample().getClass().getName() + " infile outfile [keyfile]");
            System.exit(1);
        }
        String s = "deskey.dat";
        final String s2 = array[0];
        final String s3 = array[1];
        if (array.length > 2) {
            b = false;
            s = array[2];
        }
        new DESExample(s2, s3, s, b).process();
    }
    
    public DESExample() {
        this.encrypt = true;
        this.cipher = null;
        this.in = null;
        this.out = null;
        this.key = null;
    }
    
    public DESExample(final String s, final String s2, final String s3, final boolean encrypt) {
        this.encrypt = true;
        this.cipher = null;
        this.in = null;
        this.out = null;
        this.key = null;
        this.encrypt = encrypt;
        try {
            this.in = new BufferedInputStream(new FileInputStream(s));
        }
        catch (final FileNotFoundException ex) {
            System.err.println("Input file not found [" + s + "]");
            System.exit(1);
        }
        try {
            this.out = new BufferedOutputStream(new FileOutputStream(s2));
        }
        catch (final IOException ex2) {
            System.err.println("Output file not created [" + s2 + "]");
            System.exit(1);
        }
        if (encrypt) {
            try {
                SecureRandom secureRandom = null;
                try {
                    secureRandom = new SecureRandom();
                    secureRandom.setSeed("www.bouncycastle.org".getBytes());
                }
                catch (final Exception ex3) {
                    System.err.println("Hmmm, no SHA1PRNG, you need the Sun implementation");
                    System.exit(1);
                }
                final KeyGenerationParameters keyGenerationParameters = new KeyGenerationParameters(secureRandom, 192);
                final DESedeKeyGenerator deSedeKeyGenerator = new DESedeKeyGenerator();
                deSedeKeyGenerator.init(keyGenerationParameters);
                this.key = deSedeKeyGenerator.generateKey();
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(s3));
                final byte[] encode = Hex.encode(this.key);
                bufferedOutputStream.write(encode, 0, encode.length);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            }
            catch (final IOException ex4) {
                System.err.println("Could not decryption create key file [" + s3 + "]");
                System.exit(1);
            }
        }
        else {
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(s3));
                final int available = bufferedInputStream.available();
                final byte[] array = new byte[available];
                bufferedInputStream.read(array, 0, available);
                this.key = Hex.decode(array);
            }
            catch (final IOException ex5) {
                System.err.println("Decryption key file not found, or not valid [" + s3 + "]");
                System.exit(1);
            }
        }
    }
    
    private void process() {
        this.cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()));
        if (this.encrypt) {
            this.performEncrypt(this.key);
        }
        else {
            this.performDecrypt(this.key);
        }
        try {
            this.in.close();
            this.out.flush();
            this.out.close();
        }
        catch (final IOException ex) {
            System.err.println("exception closing resources: " + ex.getMessage());
        }
    }
    
    private void performEncrypt(final byte[] array) {
        this.cipher.init(true, new KeyParameter(array));
        final int n = 47;
        final int outputSize = this.cipher.getOutputSize(n);
        final byte[] array2 = new byte[n];
        final byte[] array3 = new byte[outputSize];
        try {
            int read;
            while ((read = this.in.read(array2, 0, n)) > 0) {
                final int processBytes = this.cipher.processBytes(array2, 0, read, array3, 0);
                if (processBytes > 0) {
                    final byte[] encode = Hex.encode(array3, 0, processBytes);
                    this.out.write(encode, 0, encode.length);
                    this.out.write(10);
                }
            }
            try {
                final int doFinal = this.cipher.doFinal(array3, 0);
                if (doFinal > 0) {
                    final byte[] encode2 = Hex.encode(array3, 0, doFinal);
                    this.out.write(encode2, 0, encode2.length);
                    this.out.write(10);
                }
            }
            catch (final CryptoException ex) {}
        }
        catch (final IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    private void performDecrypt(final byte[] array) {
        this.cipher.init(false, new KeyParameter(array));
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.in));
        try {
            byte[] array2 = null;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final byte[] decode = Hex.decode(line);
                array2 = new byte[this.cipher.getOutputSize(decode.length)];
                final int processBytes = this.cipher.processBytes(decode, 0, decode.length, array2, 0);
                if (processBytes > 0) {
                    this.out.write(array2, 0, processBytes);
                }
            }
            try {
                final int doFinal = this.cipher.doFinal(array2, 0);
                if (doFinal > 0) {
                    this.out.write(array2, 0, doFinal);
                }
            }
            catch (final CryptoException ex) {}
        }
        catch (final IOException ex2) {
            ex2.printStackTrace();
        }
    }
}
