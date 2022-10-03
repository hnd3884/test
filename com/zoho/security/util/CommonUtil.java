package com.zoho.security.util;

import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.security.SecureRandom;
import java.util.logging.Logger;

public class CommonUtil
{
    private static final Logger LOGGER;
    private static final int BUFFER_SIZE = 1024;
    public static SecureRandom secureRandom;
    
    public static String convertFileToString(final File file) throws IOException {
        int length = 0;
        final StringBuilder content = new StringBuilder();
        final char[] buffer = new char[1024];
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((length = reader.read(buffer)) != -1) {
                content.append(String.valueOf(buffer, 0, length));
            }
        }
        catch (final IOException ex) {
            CommonUtil.LOGGER.log(Level.SEVERE, "Exception occured while reading File -  {0}", ex.getMessage());
            throw ex;
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException ex2) {
                CommonUtil.LOGGER.log(Level.SEVERE, "Unable to close Buffer reader");
                throw ex2;
            }
        }
        return content.toString().trim();
    }
    
    public static int getSecureRandomNumber() {
        return (CommonUtil.secureRandom != null) ? CommonUtil.secureRandom.nextInt(99999) : 0;
    }
    
    static {
        LOGGER = Logger.getLogger(CommonUtil.class.getName());
        CommonUtil.secureRandom = null;
        try {
            CommonUtil.secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        }
        catch (final NoSuchAlgorithmException | NoSuchProviderException e) {
            CommonUtil.LOGGER.log(Level.SEVERE, "Exception occurred while creating securerandom instance , exception  : ", e);
        }
    }
}
