package com.zoho.framework.utils.crypto;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CryptoUtil
{
    private static final Logger LOGGER;
    private static EnDecrypt cryptInstance;
    
    public static void setEnDecryptInstance(final EnDecrypt instance) throws Exception {
        if (null != CryptoUtil.cryptInstance) {
            CryptoUtil.LOGGER.log(Level.INFO, "Already crypt instance has been set and hence this [{0}] is ignored", instance);
            return;
        }
        CryptoUtil.cryptInstance = instance;
    }
    
    public static String encrypt(final String plaintext) {
        if (null == plaintext || 0 == plaintext.trim().length()) {
            return plaintext;
        }
        if (CryptoUtil.cryptInstance == null) {
            CryptoUtil.LOGGER.log(Level.WARNING, "EnDecrypt instance has not been initialized. EnDeccryptInstance is [{0}]  ", CryptoUtil.cryptInstance);
            return plaintext;
        }
        return CryptoUtil.cryptInstance.encrypt(plaintext);
    }
    
    public static String encrypt(final String plaintext, final int algorithm) throws Exception {
        if (null == plaintext || 0 == plaintext.trim().length()) {
            return plaintext;
        }
        return getEnDecryptor(algorithm).encrypt(plaintext);
    }
    
    public static String encrypt(final String plaintext, final int algorithm, final String cryptTag) throws Exception {
        final String encryptionKey = (cryptTag == null) ? EnDecryptUtil.getCryptTag() : cryptTag;
        if (null == plaintext || 0 == plaintext.trim().length()) {
            return plaintext;
        }
        return getEnDecryptor(algorithm).encrypt(plaintext, encryptionKey);
    }
    
    public static String encrypt(final String plaintext, final String cryptTag) throws Exception {
        final String encryptionKey = (cryptTag == null) ? EnDecryptUtil.getCryptTag() : cryptTag;
        if (null == plaintext || 0 == plaintext.trim().length()) {
            return plaintext;
        }
        if (CryptoUtil.cryptInstance == null) {
            CryptoUtil.LOGGER.log(Level.WARNING, "EnDecrypt instance has not been initialized. EnDeccryptInstance is [{0}]  ", CryptoUtil.cryptInstance);
            return plaintext;
        }
        return CryptoUtil.cryptInstance.encrypt(plaintext, encryptionKey);
    }
    
    private static EnDecrypt getEnDecryptor(final int algo) throws Exception {
        final HashMap<Integer, EnDecrypt> algoMap = EnDecryptUtil.getEnDecryptionMap();
        EnDecrypt cryptInstance = algoMap.get(algo);
        if (cryptInstance == null) {
            cryptInstance = getDefaultAlgorithm(algo);
            if (cryptInstance == null) {
                throw new IllegalArgumentException("No implementation has been configured for the given algorithm");
            }
        }
        return cryptInstance;
    }
    
    private static EnDecrypt getDefaultAlgorithm(final int algo) {
        switch (algo) {
            case 1: {
                return new EnDecryptAES128Impl();
            }
            case 3: {
                return new EnDecryptBlowfishImpl();
            }
            case 2: {
                return new EnDecryptAES256Impl();
            }
            default: {
                return null;
            }
        }
    }
    
    public static String decrypt(final String cipherText) {
        if (null == cipherText || 0 == cipherText.trim().length()) {
            return cipherText;
        }
        if (CryptoUtil.cryptInstance == null) {
            CryptoUtil.LOGGER.log(Level.WARNING, "EnDecrypt instance has not been initialized. EnDecryptInstance is [{0}]  ", CryptoUtil.cryptInstance);
            return cipherText;
        }
        return CryptoUtil.cryptInstance.decrypt(cipherText);
    }
    
    public static String decrypt(final String cipherText, final int algorithm) throws Exception {
        if (null == cipherText || 0 == cipherText.trim().length()) {
            return cipherText;
        }
        return getEnDecryptor(algorithm).decrypt(cipherText);
    }
    
    public static String decrypt(final String cipherText, final int algorithm, final String cryptTag) throws Exception {
        final String encryptionKey = (cryptTag == null) ? EnDecryptUtil.getCryptTag() : cryptTag;
        if (null == cipherText || 0 == cipherText.trim().length()) {
            return cipherText;
        }
        return getEnDecryptor(algorithm).decrypt(cipherText, encryptionKey);
    }
    
    public static String decrypt(final String cipherText, final String cryptTag) throws Exception {
        final String encryptionKey = (cryptTag == null) ? EnDecryptUtil.getCryptTag() : cryptTag;
        if (null == cipherText || 0 == cipherText.trim().length()) {
            return cipherText;
        }
        if (CryptoUtil.cryptInstance == null) {
            CryptoUtil.LOGGER.log(Level.WARNING, "EnDecrypt instance has not been initialized. EnDecryptInstance is [{0}]  ", CryptoUtil.cryptInstance);
            return cipherText;
        }
        return CryptoUtil.cryptInstance.decrypt(cipherText, encryptionKey);
    }
    
    public static String getReEncryptedValue(final String value, final int oldAlgorithm, final int newAlgorithm, final String oldCryptTag, final String newCryptTag) throws Exception {
        if (value == null) {
            throw new NullPointerException("The value provided should not be null.");
        }
        final String oldCryptKey = (oldCryptTag == null) ? "MLITE_ENCRYPT_DECRYPT" : oldCryptTag;
        final String newCryptKey = (newCryptTag == null) ? EnDecryptUtil.getCryptTag() : newCryptTag;
        final EnDecrypt oldEnDecryptInstance = EnDecryptUtil.getEnDecryptionMap().get(oldAlgorithm);
        final EnDecrypt newEnDecryptInstance = EnDecryptUtil.getEnDecryptionMap().get(newAlgorithm);
        final String decryptedValue = oldEnDecryptInstance.decrypt(value, oldCryptKey);
        if (value.equals(decryptedValue)) {
            throw new IllegalArgumentException("Provide a properly encrypted value.");
        }
        final String encryptedValue = newEnDecryptInstance.encrypt(decryptedValue, newCryptKey);
        return encryptedValue;
    }
    
    public static String getReEncryptedValue(final String value, final int algorithm, final String oldCryptTag, final String newCryptTag) throws Exception {
        if (value == null) {
            throw new NullPointerException("The value provided should not be null.");
        }
        final String oldCryptKey = (oldCryptTag == null) ? "MLITE_ENCRYPT_DECRYPT" : oldCryptTag;
        final String newCryptKey = (newCryptTag == null) ? EnDecryptUtil.getCryptTag() : newCryptTag;
        final EnDecrypt enDecryptInstance = EnDecryptUtil.getEnDecryptionMap().get(algorithm);
        final String decryptedValue = enDecryptInstance.decrypt(value, oldCryptKey);
        if (value.equals(decryptedValue)) {
            throw new IllegalArgumentException("Provide a properly encrypted value.");
        }
        final String encryptedValue = enDecryptInstance.encrypt(decryptedValue, newCryptKey);
        return encryptedValue;
    }
    
    public static String getReEncryptedValue(final String value, final String oldCryptTag, final String newCryptTag) throws Exception {
        if (value == null) {
            throw new NullPointerException("The value provided should not be null.");
        }
        final String oldCryptKey = (oldCryptTag == null) ? "MLITE_ENCRYPT_DECRYPT" : oldCryptTag;
        final String newCryptKey = (newCryptTag == null) ? EnDecryptUtil.getCryptTag() : newCryptTag;
        final String decryptedValue = CryptoUtil.cryptInstance.decrypt(value, oldCryptKey);
        if (value.equals(decryptedValue)) {
            throw new IllegalArgumentException("Provide a properly encrypted value.");
        }
        final String encryptedValue = CryptoUtil.cryptInstance.encrypt(decryptedValue, newCryptKey);
        return encryptedValue;
    }
    
    private static boolean validateInputs(final String[] args) {
        return ((args[0].equalsIgnoreCase("-a") || args[0].equalsIgnoreCase("--algo")) && (args[1].equalsIgnoreCase("aes128") || args[1].equalsIgnoreCase("aes256") || args[1].equalsIgnoreCase("blowfish")) && (args[2].equalsIgnoreCase("-k") || args[2].equalsIgnoreCase("--key")) && (args[4].equals("-v") || args[4].equals("--values"))) || ((args[0].equalsIgnoreCase("-a") || args[0].equalsIgnoreCase("--algo") || args[0].equalsIgnoreCase("-k") || args[0].equalsIgnoreCase("--key")) && (args[2].equalsIgnoreCase("-v") || args[2].equalsIgnoreCase("--values"))) || (!args[0].equalsIgnoreCase("-k") && !args[0].equalsIgnoreCase("--key") && !args[0].equalsIgnoreCase("-a") && !args[0].equalsIgnoreCase("--algo"));
    }
    
    public static void printUsage() {
        final String format = "%-50s %-70s";
        System.console().writer().println(String.format(format, "encrypt.bat / encrypt.sh [options]", ""));
        System.console().writer().println(String.format(format, "Options::", ""));
        System.console().writer().println(String.format(format, "-a or --algo", "To specify the algorithm to be used for encryption. Available ones: aes128, aes256, blowfish"));
        System.console().writer().println(String.format(format, "-k or --key", "To specify the encryption key to be used for encrypting the value"));
        System.console().writer().println(String.format(format, "-v or --values", "To specify one or more values separated by space to be encrypted"));
        System.console().writer().println(String.format(format, "The order of arguments must be algorithm first, key next and values last when both algorithm and key are to be specified.", ""));
        System.console().writer().println(String.format(format, "The order of arguments must be algorithm or key first and values next when only either of algorithm or key is to be specified.", ""));
        System.console().writer().println(String.format(format, "Only values to be encrypted can also be specified", ""));
        System.console().writer().println(String.format(format, "If no options are specified, all the words, separated by space, following the script, will be considered as individual text to be encrypted using the AES algorithm", ""));
        System.console().writer().println(String.format(format, "Note: For using AES encryption with 256 bit key length, unlimited strength jce jars should be bundled with the jre used", ""));
    }
    
    public static void main(final String[] args) throws Exception {
        if (validateInputs(args)) {
            String algo = "aes256";
            String cryptTag = EnDecryptUtil.getCryptTag();
            int startIndex = 3;
            if (args[0].equalsIgnoreCase("-k") || args[0].equalsIgnoreCase("--key") || args[0].equalsIgnoreCase("-a") || args[0].equalsIgnoreCase("--algo")) {
                System.console().writer().println("------------------------");
                if ((args[0].equalsIgnoreCase("-a") || args[0].equalsIgnoreCase("--algo")) && (args[2].equalsIgnoreCase("-k") || args[2].equalsIgnoreCase("--key"))) {
                    cryptTag = args[3];
                    algo = args[1];
                    startIndex = 5;
                }
                else if (args[0].equals("-k") || args[0].equalsIgnoreCase("--key")) {
                    cryptTag = args[1];
                }
                else if (args[0].equals("-a") || args[0].equals("--algo")) {
                    algo = args[1];
                }
                for (int i = startIndex; i < args.length; ++i) {
                    if (algo.equalsIgnoreCase("aes256")) {
                        final String en = encrypt(args[i], 2, cryptTag);
                        final String dn = decrypt(en, 2, cryptTag);
                        System.console().writer().println(dn + " (using AES encryption) = " + en);
                    }
                    else if (algo.equalsIgnoreCase("aes128")) {
                        final String en = encrypt(args[i], 1, cryptTag);
                        final String dn = decrypt(en, 1, cryptTag);
                        System.console().writer().println(dn + " (using AES encryption) = " + en);
                    }
                    else if (algo.equalsIgnoreCase("des")) {
                        System.console().writer().println("DES algorithm is not supported as it is considered a weaker one.");
                    }
                    else if (algo.equalsIgnoreCase("blowfish")) {
                        final String en = encrypt(args[i], 3, cryptTag);
                        final String dn = decrypt(en, 3, cryptTag);
                        System.console().writer().println(dn + " (using Blowfish encryption) = " + en);
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("showUsage")) {
                printUsage();
            }
            else {
                System.console().writer().println("------------------------");
                for (final String plainText : args) {
                    System.console().writer().println(plainText + "=" + encrypt(plainText, 2));
                }
            }
        }
        else {
            printUsage();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(CryptoUtil.class.getName());
        CryptoUtil.cryptInstance = null;
    }
}
