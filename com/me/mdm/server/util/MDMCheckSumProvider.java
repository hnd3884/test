package com.me.mdm.server.util;

import java.util.logging.Level;
import java.security.MessageDigest;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;

public class MDMCheckSumProvider extends ChecksumProvider
{
    private static Logger logger;
    private static MDMCheckSumProvider checksumProvider;
    public static String security_algorithm_sha_256;
    
    public static MDMCheckSumProvider getInstance() {
        if (MDMCheckSumProvider.checksumProvider == null) {
            MDMCheckSumProvider.checksumProvider = new MDMCheckSumProvider();
        }
        return MDMCheckSumProvider.checksumProvider;
    }
    
    public String getSHA256HashFromString(final String inputString) throws Exception {
        byte[] buffer = new byte[1024];
        String output = "";
        buffer = inputString.getBytes();
        final int length = inputString.length();
        try {
            final MessageDigest digest = MessageDigest.getInstance(MDMCheckSumProvider.security_algorithm_sha_256);
            digest.update(buffer, 0, length);
            final byte[] sha256 = digest.digest();
            for (int i = 0; i < sha256.length; ++i) {
                output += Integer.toString((sha256[i] & 0xFF) + 256, 16).substring(1);
            }
            MDMCheckSumProvider.logger.log(Level.INFO, "SHA-256 hash of the given string generated successfully");
        }
        catch (final Exception exp) {
            MDMCheckSumProvider.logger.log(Level.SEVERE, "Got Exception in GetSHA256hashFromString(): ", exp);
            output = "--";
        }
        return output;
    }
    
    public boolean validateFileCheckSum(final String filePath, final String checksumToCompare, final Boolean isStatic) throws Exception {
        String fileCheckSum = "";
        fileCheckSum = this.GetMD5HashFromFile(filePath, (boolean)isStatic);
        if (fileCheckSum.length() <= 31) {
            return false;
        }
        if (fileCheckSum.compareTo(checksumToCompare) == 0) {
            return true;
        }
        MDMCheckSumProvider.logger.log(Level.INFO, "checkSum failed for filePath ::{0} as downloaded file''s checksum ::{1} is different from checksum in db ::{2}", new Object[] { filePath, fileCheckSum, checksumToCompare });
        return false;
    }
    
    static {
        MDMCheckSumProvider.logger = Logger.getLogger(MDMCheckSumProvider.class.getName());
        MDMCheckSumProvider.checksumProvider = null;
        MDMCheckSumProvider.security_algorithm_sha_256 = "SHA-256";
    }
}
