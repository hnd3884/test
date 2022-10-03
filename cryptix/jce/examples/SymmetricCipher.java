package cryptix.jce.examples;

import javax.crypto.SecretKey;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;

public final class SymmetricCipher
{
    private Cipher cipher;
    private KeyGenerator kg;
    
    public void run(final String algorithm, final String mode, final String padding, final String provider, final String filename) {
        try {
            this.cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding, provider);
            this.kg = KeyGenerator.getInstance(algorithm, provider);
            int strength = 0;
            IvParameterSpec spec = null;
            byte[] iv = null;
            if (algorithm == "Blowfish") {
                strength = 448;
            }
            else if (algorithm == "CAST5") {
                strength = 128;
            }
            else if (algorithm == "DES") {
                strength = 56;
            }
            else if (algorithm == "TripleDES" || algorithm == "DESede") {
                strength = 192;
            }
            else if (algorithm == "Rijndael") {
                strength = 256;
            }
            else if (algorithm == "SKIPJACK") {
                strength = 80;
            }
            else {
                if (algorithm != "Square") {
                    throw new RuntimeException();
                }
                strength = 128;
            }
            System.out.println("Using keylength: " + strength + " bits.");
            System.out.println("Blocksize: " + this.cipher.getBlockSize() * 8 + " bits.");
            System.out.println();
            this.kg.init(strength, new SecureRandom());
            final SecretKey key = this.kg.generateKey();
            if (mode == "ECB") {
                this.cipher.init(1, key);
            }
            else {
                final SecureRandom sr = new SecureRandom();
                iv = new byte[this.cipher.getBlockSize()];
                sr.nextBytes(iv);
                spec = new IvParameterSpec(iv);
            }
            if (filename != null) {
                final FileDEncryption fe = new FileDEncryption(filename, key, iv, algorithm, mode, padding, provider);
                System.out.println("******** BEGIN file encryption! *******");
                System.out.println();
                fe.go();
                System.out.println("******** END file encryption! *******");
                System.out.println();
                System.out.println("******** BEGIN file decryption! *******");
                System.out.println();
                fe.reTurn();
                System.out.println("******** END file decryption! *******");
                System.out.println();
            }
            final byte[] text1 = ("text for encryption. You will not recognize it " + "after encryption.").getBytes();
            final byte[] text2 = ("!holdrio! more bytes for encryption. " + "aaaaaaaaaaaaaaaaaaEND").getBytes();
            try {
                this.cipher.init(1, key, spec);
            }
            catch (final InvalidAlgorithmParameterException iape) {
                System.out.println("cipher.init: InvalidAlgorithmParameterException.");
                iape.printStackTrace();
            }
            final int outLength = this.cipher.getOutputSize(text1.length + text2.length);
            System.out.println("Output bytes: " + outLength);
            System.out.println();
            final byte[] encr1 = this.cipher.update(text1);
            final byte[] encr2 = this.cipher.doFinal(text2);
            System.out.println("cipher: " + new String(encr1) + new String(encr2));
            System.out.println();
            try {
                final Cipher decipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding, provider);
                decipher.init(2, key, spec);
                final byte[] deciph1 = decipher.update(encr1);
                final byte[] deciph2 = decipher.doFinal(encr2);
                System.out.println(algorithm + "/" + mode + "/" + padding + " decrypted: " + new String(deciph1) + new String(deciph2) + " " + " " + decipher.getOutputSize(encr1.length + encr2.length));
            }
            catch (final InvalidAlgorithmParameterException iape2) {
                System.out.println("cipher.init: InvalidAlgorithmParameterException.");
                iape2.printStackTrace();
            }
        }
        catch (final NoSuchAlgorithmException nsae) {
            System.out.println("No such algorithm!\n");
            nsae.printStackTrace();
        }
        catch (final NoSuchPaddingException nspe) {
            System.out.println("No such padding!\n");
            nspe.printStackTrace();
        }
        catch (final NoSuchProviderException nspre) {
            System.out.println("No such provider found!\n");
            nspre.printStackTrace();
        }
        catch (final InvalidKeyException ike) {
            System.out.println("Invalidkey Exception!\n");
            ike.printStackTrace();
        }
        catch (final IllegalBlockSizeException ibse) {
            System.out.println("Illegal block size exception!\n");
            ibse.printStackTrace();
        }
        catch (final BadPaddingException bpe) {
            System.out.println("Bad padding exception!\n");
            bpe.printStackTrace();
        }
    }
    
    public SymmetricCipher() {
        this.cipher = null;
        this.kg = null;
    }
}
