package com.zoho.framework.utils.crypto;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import com.adventnet.persistence.ConfigurationParser;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

public class EnDecryptUtil
{
    private static final Logger LOGGER;
    private static final char[] HEX;
    private static HashMap<Integer, EnDecrypt> algoMap;
    private static EncryptionHandler encryptionHandler;
    private static Integer defaultAlgorithmValue;
    private static String cryptTag;
    
    public static int getIntValueForAlgorithm(final String algoString) {
        switch (algoString) {
            case "aes256": {
                return 2;
            }
            case "aes128": {
                return 1;
            }
            case "blowfish": {
                return 3;
            }
            default: {
                throw new IllegalArgumentException("We do not have support for the given algorithm - " + algoString);
            }
        }
    }
    
    protected static String BASE16_ENCODE(final byte[] input) {
        final char[] b16 = new char[input.length * 2];
        int i = 0;
        for (final byte c : input) {
            final int low = c & 0xF;
            final int high = (c & 0xF0) >> 4;
            b16[i++] = EnDecryptUtil.HEX[high];
            b16[i++] = EnDecryptUtil.HEX[low];
        }
        return new String(b16);
    }
    
    protected static byte[] BASE16_DECODE(final String b16str) {
        final int len = b16str.length();
        final byte[] out = new byte[len / 2];
        int j = 0;
        for (int i = 0; i < len; i += 2) {
            final int c1 = INT(b16str.charAt(i));
            final int c2 = INT(b16str.charAt(i + 1));
            final int bt = c1 << 4 | c2;
            out[j++] = (byte)bt;
        }
        return out;
    }
    
    private static int INT(final char c) {
        return Integer.decode("0x" + c);
    }
    
    protected static String B2S(final byte[] bytes) {
        return new String(bytes);
    }
    
    public static void initializeEnDecryption() throws Exception {
        final String fileName = System.getProperty("server.home", ".") + File.separator + "conf" + File.separator + "Persistence" + File.separator + "persistence-configurations.xml";
        final ConfigurationParser parser = new ConfigurationParser(fileName);
        final String defaultAlgorithm = parser.getConfigurationValue("cryptAlgo");
        EnDecryptUtil.defaultAlgorithmValue = ((defaultAlgorithm == null) ? 2 : getIntValueForAlgorithm(defaultAlgorithm));
        EnDecryptUtil.cryptTag = ((parser.getConfigurationValue("CryptTag") == null) ? "MLITE_ENCRYPT_DECRYPT" : parser.getConfigurationValue("CryptTag"));
        EnDecryptUtil.algoMap = new HashMap<Integer, EnDecrypt>();
        Properties cryptClasses = new Properties();
        if (parser.getConfigurationProps("cryptClass") != null) {
            cryptClasses = parser.getConfigurationProps("cryptClass");
        }
        final Set<Object> keys = ((Hashtable<Object, V>)cryptClasses).keySet();
        for (final Object key : keys) {
            final String algoName = (String)key;
            final String cryptClass = cryptClasses.getProperty(algoName);
            final Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(cryptClass);
            final EnDecrypt cryptInstance = (EnDecrypt)c.newInstance();
            EnDecryptUtil.algoMap.put(getIntValueForAlgorithm(algoName), cryptInstance);
        }
        final String handlerClass = parser.getConfigurationValue("EncryptionHandler");
        if (handlerClass != null) {
            final Class<?> c2 = Thread.currentThread().getContextClassLoader().loadClass(handlerClass);
            EnDecryptUtil.encryptionHandler = (EncryptionHandler)c2.newInstance();
        }
    }
    
    protected static HashMap<Integer, EnDecrypt> getEnDecryptionMap() throws Exception {
        if (EnDecryptUtil.algoMap == null) {
            initializeEnDecryption();
        }
        return EnDecryptUtil.algoMap;
    }
    
    protected static String getCryptTag() throws Exception {
        if (EnDecryptUtil.cryptTag == null) {
            initializeEnDecryption();
        }
        return EnDecryptUtil.cryptTag;
    }
    
    public static void setCryptTag(final String newCryptTag) {
        EnDecryptUtil.cryptTag = newCryptTag;
    }
    
    public static EncryptionHandler getEncryptionHandler() throws Exception {
        if (EnDecryptUtil.encryptionHandler == null) {
            initializeEnDecryption();
        }
        return EnDecryptUtil.encryptionHandler;
    }
    
    public static Integer getDefaultAlgorithmValue() throws Exception {
        if (EnDecryptUtil.defaultAlgorithmValue == null) {
            initializeEnDecryption();
        }
        return EnDecryptUtil.defaultAlgorithmValue;
    }
    
    static {
        LOGGER = Logger.getLogger(EnDecryptUtil.class.getName());
        HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
