package com.me.framework.migration;

import java.security.MessageDigest;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import java.util.logging.Logger;

public class ChecksumMigrationUtil
{
    private static Logger logger;
    ChecksumProvider checksumProvider;
    
    public ChecksumMigrationUtil() {
        this.checksumProvider = new ChecksumProvider();
    }
    
    public String ConvertMD5FileToSHA256(final String filePath, final String checksumToCompare) throws Exception {
        final String checkSumGenerated = this.checksumProvider.GetMD5HashFromFile(filePath);
        if (this.validateCheckSumMD5(checkSumGenerated, checksumToCompare)) {
            final String output = this.checksumProvider.GetSHA256CheckSum(filePath);
            return output;
        }
        throw new ChecksumException("Checksum and File are not matching");
    }
    
    public String ConvertMD5StringToSHA256(final String string, final String checksumToCompare) throws Exception {
        final String checkSumGenerated = this.checksumProvider.GetMD5hashFromString(string);
        if (this.validateCheckSumMD5(checkSumGenerated, checksumToCompare)) {
            final String output = GetSHA256hashFromString(string);
            return output;
        }
        throw new ChecksumException("Checksum and File are not matching");
    }
    
    public String ConvertSHA1FileToSHA256(final String filePath, final String checksumToCompare) throws Exception {
        final String checkSumGenerated = this.checksumProvider.getSHA1CheckSum(filePath);
        if (this.validateCheckSumSHA1(checkSumGenerated, checksumToCompare)) {
            final String output = this.checksumProvider.GetSHA256CheckSum(filePath);
            return output;
        }
        throw new ChecksumException("Checksum and File are not matching");
    }
    
    public String ConvertSHA1StringToSHA256(final String string, final String checksumToCompare) throws Exception {
        final String checkSumGenerated = GetSHA1hashFromString(string);
        if (this.validateCheckSumSHA1(checkSumGenerated, checksumToCompare)) {
            final String output = GetSHA256hashFromString(string);
            return output;
        }
        throw new ChecksumException("Checksum and File are not matching");
    }
    
    private boolean validateCheckSumMD5(final String checkSumGenerated, final String checksumToCompare) {
        if (checkSumGenerated.length() <= 31) {
            return false;
        }
        if (checkSumGenerated.equalsIgnoreCase(checksumToCompare)) {
            return true;
        }
        ChecksumMigrationUtil.logger.log(Level.INFO, "checkSum failed and checksum and data  is different from checksum in db ::" + checksumToCompare);
        return false;
    }
    
    private boolean validateCheckSumSHA1(final String checkSumGenerated, final String checksumToCompare) {
        if (checkSumGenerated.length() != 40) {
            ChecksumMigrationUtil.logger.log(Level.INFO, "checkSum failed and checksum and data  is different from checksum in db ::" + checksumToCompare);
            return false;
        }
        if (checkSumGenerated.equalsIgnoreCase(checksumToCompare)) {
            return true;
        }
        ChecksumMigrationUtil.logger.log(Level.INFO, "checkSum failed and checksum and data  is different from checksum in db ::" + checksumToCompare);
        return false;
    }
    
    private static String GetSHA256hashFromString(final String inputString) throws Exception {
        byte[] buffer = new byte[1024];
        String output = "";
        buffer = inputString.getBytes();
        final int length = inputString.length();
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(buffer, 0, length);
            final byte[] md5sum = digest.digest();
            for (int i = 0; i < md5sum.length; ++i) {
                output += Integer.toString((md5sum[i] & 0xFF) + 256, 16).substring(1);
            }
            ChecksumMigrationUtil.logger.log(Level.INFO, "SHA256 hash of the given string(" + inputString + ") is:  " + output);
        }
        catch (final Exception exp) {
            ChecksumMigrationUtil.logger.log(Level.SEVERE, "Got Exception in GetSHA256hashFromString(): ", exp);
            output = "--";
        }
        return output;
    }
    
    private static String GetSHA1hashFromString(final String inputString) throws Exception {
        byte[] buffer = new byte[1024];
        String output = "";
        buffer = inputString.getBytes();
        final int length = inputString.length();
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(buffer, 0, length);
            final byte[] md5sum = digest.digest();
            for (int i = 0; i < md5sum.length; ++i) {
                output += Integer.toString((md5sum[i] & 0xFF) + 256, 16).substring(1);
            }
            ChecksumMigrationUtil.logger.log(Level.INFO, "SHA1 hash of the given string(" + inputString + ") is:  " + output);
        }
        catch (final Exception exp) {
            ChecksumMigrationUtil.logger.log(Level.SEVERE, "Got Exception in GetSHA1hashFromString(): ", exp);
            output = "--";
        }
        return output;
    }
    
    static {
        ChecksumMigrationUtil.logger = Logger.getLogger(ChecksumProvider.class.getName());
    }
    
    class ChecksumException extends Exception
    {
        String message;
        
        ChecksumException(final String msg) {
            this.message = msg;
        }
        
        @Override
        public String toString() {
            return "CheckSumMisMatchException  Occurred: " + this.message;
        }
    }
}
