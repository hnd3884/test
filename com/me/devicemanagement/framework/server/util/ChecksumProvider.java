package com.me.devicemanagement.framework.server.util;

import java.util.Arrays;
import java.util.stream.Stream;
import org.json.JSONException;
import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.security.MessageDigest;
import java.util.logging.Logger;

public class ChecksumProvider
{
    private static Logger logger;
    private static String encryptionKey;
    public static final String DEFAULT_ALGORITHM = "SHA-256";
    private static String algorithm1;
    private static String algorithm2;
    private static String security_algorithm1;
    private static String security_algorithm2;
    private static ChecksumProvider checksumProvider;
    
    public static String getSecurityAlgorithm1() {
        return ChecksumProvider.security_algorithm1;
    }
    
    public static String getSecurityAlgorithm2() {
        return ChecksumProvider.security_algorithm2;
    }
    
    public static synchronized ChecksumProvider getInstance() {
        if (ChecksumProvider.checksumProvider == null) {
            ChecksumProvider.checksumProvider = new ChecksumProvider();
        }
        return ChecksumProvider.checksumProvider;
    }
    
    public String GetMD5hashFromString(final String inputString) throws Exception {
        byte[] buffer = new byte[1024];
        String output = "";
        buffer = inputString.getBytes();
        final int length = inputString.length();
        try {
            final MessageDigest digest = MessageDigest.getInstance(ChecksumProvider.security_algorithm1);
            digest.update(buffer, 0, length);
            final byte[] md5sum = digest.digest();
            for (int i = 0; i < md5sum.length; ++i) {
                output += Integer.toString((md5sum[i] & 0xFF) + 256, 16).substring(1);
            }
            ChecksumProvider.logger.log(Level.INFO, "MD5 hash of the given string(" + inputString + ") is:  " + output);
        }
        catch (final Exception exp) {
            ChecksumProvider.logger.log(Level.SEVERE, "Got Exception in GetMD5hashFromString(): ", exp);
            output = "--";
        }
        return output;
    }
    
    public String GetMD5HashFromFile(final String filePath) throws Exception {
        return this.GetMD5HashFromFile(filePath, false);
    }
    
    public String GetMD5HashFromFile(final String filePath, final boolean isStaticFile) throws Exception {
        String output = "";
        InputStream input = null;
        try {
            if (isStaticFile) {
                input = new FileInputStream(filePath);
            }
            else {
                input = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
            }
            output = this.getMD5HashFromInputStream(input);
            ChecksumProvider.logger.log(Level.INFO, "MD5 hash of the given file(" + filePath + ") is:  " + output);
        }
        catch (final Exception exp) {
            ChecksumProvider.logger.log(Level.SEVERE, "Got Exception in GetMD5HashFromFile(): ", exp);
            output = "--";
            throw exp;
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (final Exception ex) {
                throw ex;
            }
        }
        return output;
    }
    
    public String getMD5HashFromInputStream(final InputStream input) throws Exception {
        final byte[] buffer = new byte[8192];
        int read = 0;
        String output = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance(ChecksumProvider.security_algorithm1);
            while ((read = input.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            final byte[] md5sum = digest.digest();
            for (int i = 0; i < md5sum.length; ++i) {
                output += Integer.toString((md5sum[i] & 0xFF) + 256, 16).substring(1);
            }
        }
        catch (final Exception exp) {
            ChecksumProvider.logger.log(Level.SEVERE, "Got Exception in getMD5HashFromInputStream(): ", exp);
            output = "--";
            throw exp;
        }
        return output;
    }
    
    public boolean ValidateFileCheckSum(final String filePath, final String checksumToCompare) throws Exception {
        return this.ValidateFileCheckSum(filePath, checksumToCompare, Boolean.FALSE);
    }
    
    public boolean ValidateFileCheckSum(final String filePath, final String checksumToCompare, final boolean isLocal) throws Exception {
        String fileCheckSum = "";
        fileCheckSum = this.GetMD5HashFromFile(filePath, isLocal);
        if (fileCheckSum.length() <= 31) {
            return false;
        }
        if (fileCheckSum.compareTo(checksumToCompare) == 0) {
            return true;
        }
        ChecksumProvider.logger.log(Level.INFO, "checkSum failed for filePath ::" + filePath + " as downloaded file's checksum ::" + fileCheckSum + " is different from checksum in db ::" + checksumToCompare);
        return false;
    }
    
    public boolean ValidateSHA256CheckSum(final String filePath, final String sha256CheckSumToCompare) {
        return this.ValidateSHA256CheckSum(filePath, sha256CheckSumToCompare, Boolean.FALSE);
    }
    
    public boolean ValidateSHA256CheckSum(final String filePath, final String sha256CheckSumToCompare, final boolean isLocal) {
        String fileCheckSum = "";
        Boolean isValid = Boolean.FALSE;
        fileCheckSum = this.GetSHA256CheckSum(filePath, isLocal);
        if (fileCheckSum.length() == 64) {
            if (fileCheckSum.equalsIgnoreCase(sha256CheckSumToCompare)) {
                isValid = Boolean.TRUE;
            }
            else {
                ChecksumProvider.logger.log(Level.INFO, "SHA 256 checkSum failed for FilePath ::" + filePath + " as the downloaded file's checksum ::" + fileCheckSum + " is different from checksum in db ::" + sha256CheckSumToCompare);
            }
        }
        else {
            ChecksumProvider.logger.log(Level.INFO, "SHA 256 checkSum failed for FilePath ::" + filePath + " as the downloaded file's checksum ::" + fileCheckSum + " has greater or lesser than 64 digits");
        }
        return isValid;
    }
    
    public String GetSHA256CheckSum(final String filePath) {
        return this.GetSHA256CheckSum(filePath, false);
    }
    
    public String GetSHA256CheckSum(final String filePath, final Boolean isStaticFile) {
        String Checksum = "";
        InputStream fis = null;
        try {
            final StringBuilder sb = new StringBuilder();
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            if (isStaticFile) {
                fis = new FileInputStream(filePath);
            }
            else {
                fis = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
            }
            final byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            final byte[] mdbytes = md.digest();
            for (int i = 0; i < mdbytes.length; ++i) {
                sb.append(Integer.toString((mdbytes[i] & 0xFF) + 256, 16).substring(1));
            }
            Checksum = sb.toString();
        }
        catch (final Exception ex) {
            ChecksumProvider.logger.log(Level.SEVERE, null, ex);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex) {
                ChecksumProvider.logger.log(Level.SEVERE, null, ex);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {
                ChecksumProvider.logger.log(Level.SEVERE, null, ex2);
            }
        }
        return Checksum;
    }
    
    public boolean validateSHA1CheckSum(final String filePath, final String sha1CheckSumToCompare) {
        return this.validateSHA1CheckSum(filePath, sha1CheckSumToCompare, Boolean.FALSE);
    }
    
    public boolean validateSHA1CheckSum(final String filePath, final String sha1CheckSumToCompare, final boolean isLocal) {
        String fileCheckSum = "";
        Boolean isValid = Boolean.FALSE;
        fileCheckSum = this.getSHA1CheckSum(filePath);
        if (fileCheckSum.length() == 40) {
            if (fileCheckSum.equalsIgnoreCase(sha1CheckSumToCompare)) {
                isValid = Boolean.TRUE;
            }
            else {
                ChecksumProvider.logger.log(Level.INFO, "SHA 1 checkSum failed for FilePath ::" + filePath + " as the downloaded file's checksum ::" + fileCheckSum + " is different from checksum in db ::" + sha1CheckSumToCompare);
            }
        }
        else {
            ChecksumProvider.logger.log(Level.INFO, "SHA 1 checkSum failed for FilePath ::" + filePath + " as the downloaded file's checksum ::" + fileCheckSum + " has greater or lesser than 40 digits");
        }
        return isValid;
    }
    
    public String getSHA1CheckSum(final String filePath) {
        return this.getSHA1CheckSum(filePath, Boolean.FALSE);
    }
    
    public String getSHA1CheckSum(final String filePath, final boolean isStaticFile) {
        String Checksum = "";
        InputStream fis = null;
        try {
            final StringBuilder sb = new StringBuilder();
            final MessageDigest md = MessageDigest.getInstance(ChecksumProvider.security_algorithm2);
            if (isStaticFile) {
                fis = new FileInputStream(filePath);
            }
            else {
                fis = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
            }
            final byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            final byte[] mdbytes = md.digest();
            for (int i = 0; i < mdbytes.length; ++i) {
                sb.append(Integer.toString((mdbytes[i] & 0xFF) + 256, 16).substring(1));
            }
            Checksum = sb.toString();
        }
        catch (final Exception ex) {
            ChecksumProvider.logger.log(Level.SEVERE, null, ex);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex) {
                ChecksumProvider.logger.log(Level.SEVERE, null, ex);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {
                ChecksumProvider.logger.log(Level.SEVERE, null, ex2);
            }
        }
        return Checksum;
    }
    
    public boolean ValidateFileCheckSum(final String filePath, final String checksumToCompare, final String checkSumType) throws Exception {
        return this.ValidateFileCheckSum(filePath, checksumToCompare, checkSumType, Boolean.FALSE);
    }
    
    public boolean ValidateFileCheckSum(final String filePath, final String checksumToCompare, final String checkSumType, final boolean isLocal) throws Exception {
        boolean isCheckSumValidationSuccess = false;
        if (checkSumType.equalsIgnoreCase(ChecksumProvider.security_algorithm1)) {
            isCheckSumValidationSuccess = this.ValidateFileCheckSum(filePath, checksumToCompare, isLocal);
        }
        else if (checkSumType.equalsIgnoreCase("SHA256")) {
            isCheckSumValidationSuccess = this.ValidateSHA256CheckSum(filePath, checksumToCompare, isLocal);
        }
        else if (checkSumType.equalsIgnoreCase(ChecksumProvider.security_algorithm2.replaceAll("-", ""))) {
            isCheckSumValidationSuccess = this.validateSHA1CheckSum(filePath, checksumToCompare, isLocal);
        }
        return isCheckSumValidationSuccess;
    }
    
    static {
        ChecksumProvider.logger = Logger.getLogger(ChecksumProvider.class.getName());
        ChecksumProvider.encryptionKey = "encryption_algorithms";
        ChecksumProvider.algorithm1 = "security_algorithm1";
        ChecksumProvider.algorithm2 = "security_algorithm2";
        ChecksumProvider.checksumProvider = null;
        try {
            final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
            if (frameworkConfigurations.has(ChecksumProvider.encryptionKey)) {
                final JSONObject innerObject = frameworkConfigurations.getJSONObject(ChecksumProvider.encryptionKey);
                if (innerObject.has(ChecksumProvider.algorithm1) && innerObject.get(ChecksumProvider.algorithm1) != null && !innerObject.get(ChecksumProvider.algorithm1).toString().isEmpty()) {
                    ChecksumProvider.security_algorithm1 = String.valueOf(innerObject.get(ChecksumProvider.algorithm1));
                }
                else {
                    ChecksumProvider.security_algorithm1 = "SHA-256";
                }
                if (innerObject.has(ChecksumProvider.algorithm2) && innerObject.get(ChecksumProvider.algorithm2) != null && !innerObject.get(ChecksumProvider.algorithm2).toString().isEmpty()) {
                    ChecksumProvider.security_algorithm2 = String.valueOf(((JSONObject)frameworkConfigurations.get(ChecksumProvider.encryptionKey)).get(ChecksumProvider.algorithm2));
                }
                else {
                    ChecksumProvider.security_algorithm2 = "SHA-256";
                }
            }
            else {
                ChecksumProvider.security_algorithm1 = "SHA-256";
                ChecksumProvider.security_algorithm2 = "SHA-256";
            }
        }
        catch (final JSONException jsonExcep) {
            ChecksumProvider.logger.log(Level.INFO, "Exception while retrieving data from framework configuration", (Throwable)jsonExcep);
        }
    }
    
    public enum ChecksumType
    {
        SHA_256("SHA256"), 
        MD5("MD5"), 
        SHA1("SHA1");
        
        private String value;
        
        private ChecksumType(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public static ChecksumType getChecksumType(final String checksumType) {
            return Arrays.stream(values()).parallel().filter(type -> type.value.equals(s)).findFirst().orElse(ChecksumType.SHA_256);
        }
    }
}
