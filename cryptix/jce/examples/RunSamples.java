package cryptix.jce.examples;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.security.Provider;
import java.security.Security;
import cryptix.jce.provider.CryptixCrypto;

public final class RunSamples
{
    private static boolean addProviderCryptix() {
        final Provider cryptix_provider = new CryptixCrypto();
        final int result = Security.addProvider(cryptix_provider);
        if (result == -1) {
            System.out.println("Provider entry already in file.\n");
            return false;
        }
        System.out.println("Provider added at position: " + result);
        return true;
    }
    
    public static void main(final String[] args) {
        FileInputStream fis = null;
        String filename = null;
        if (args.length < 1) {
            System.out.println("You did not specify a file to encrypt!?");
            System.out.println("If you want one to encrypt just place a filename as first argument to the program.");
        }
        else {
            try {
                fis = new FileInputStream(args[0]);
                fis.close();
                filename = args[0];
            }
            catch (final FileNotFoundException fnfe) {
                System.out.println("Filename is not valid!");
                fnfe.printStackTrace();
                System.exit(-1);
            }
            catch (final IOException ioe) {
                System.out.println("IO Error!");
                ioe.printStackTrace();
                System.exit(-1);
            }
        }
        if (!addProviderCryptix()) {
            System.out.println("Could not add provider cryptix.\n");
            System.exit(-1);
        }
        final String provider = "CryptixCrypto";
        final String[] secret_algorithm = new String[0];
        final String[] mode = { "ECB", "CBC", "OFB" };
        final String[] padding = { "None", "NoPadding", "PKCS#5" };
        int i = 0;
        int j = 0;
        int k = 0;
        for (i = 0; i < secret_algorithm.length; ++i) {
            for (j = 0; j < mode.length; ++j) {
                for (k = 0; k < padding.length; ++k) {
                    System.out.println("***************\nNew cipher object!");
                    System.out.println("Using:" + secret_algorithm[i] + "/" + mode[j] + "/" + padding[k] + "\n");
                    final SymmetricCipher symmetricCipher = new SymmetricCipher();
                    symmetricCipher.run(secret_algorithm[i], mode[j], padding[k], provider, filename);
                }
            }
        }
        System.out.println("**************\nRunning hash functions:");
        final String[] hashfunctions = { "MD2", "MD4", "MD5", "RIPEMD128", "RIPEMD160", "SHA", "SHA0", "SHA1" };
        if (filename != null) {
            for (int ii = 0; ii < hashfunctions.length; ++ii) {
                System.out.println("***************");
                System.out.println("New hash object.");
                System.out.println("Using: " + hashfunctions[ii] + "/" + provider);
                final FileHashing fh = new FileHashing();
                fh.run(hashfunctions[ii], provider, filename);
            }
        }
        else {
            System.out.println("You did not provide any file to hash.");
        }
        System.out.println("**************");
        System.out.println();
    }
}
