package com.me.devicemanagement.framework.server.util;

import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.security.PublicKey;
import java.security.Signature;
import java.nio.file.Files;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.IOException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.nio.file.Paths;
import java.io.File;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class SecurityUtil
{
    private static Logger logger;
    private static Properties filesToChecksumValues;
    private static final String FILES_TO_CHECKSUM_VALUES_LOCK = "FILES_TO_CHECKSUM_VALUES_LOCK";
    public static final String SERVER_DMROOT_CRT = "Server_DMRootCA.crt";
    public static final String SERVER_DMROOT_KEY = "Server_DMRootCA.key";
    
    public static void addSecurityParamIfDoesNotExist(final String paramName, final String paramValue) {
        try {
            final DataObject securityParamsDO = getSecurityParamDO(paramName);
            if (securityParamsDO.getRow("SecurityParams") == null) {
                addSecurityParamsData(securityParamsDO, paramName, paramValue);
            }
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "addSecurityParameterIfDoesNotExist():- Caught exception while updating Parameter:" + paramName + " in DB. {0}", ex);
        }
    }
    
    private static DataObject getSecurityParamDO(final String paramName) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("SecurityParams", "PARAM_NAME"), (Object)paramName, 0, false);
        return DataAccess.get("SecurityParams", criteria);
    }
    
    private static void addSecurityParamsData(final DataObject securityParamsDO, final String paramName, final String paramValue) throws Exception {
        final Row securityParamRow = new Row("SecurityParams");
        securityParamRow.set("PARAM_NAME", (Object)paramName);
        securityParamRow.set("PARAM_VALUE", (Object)paramValue);
        securityParamsDO.addRow(securityParamRow);
        SecurityUtil.logger.log(Level.FINER, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
        SyMUtil.getPersistence().update(securityParamsDO);
    }
    
    public static void updateSecurityParameter(final String paramName, final String paramValue) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SecurityParams", "PARAM_NAME"), (Object)paramName, 0, false);
            final DataObject systemParamsDO = DataAccess.get("SecurityParams", criteria);
            Row systemParamRow = systemParamsDO.getRow("SecurityParams");
            if (systemParamRow == null) {
                systemParamRow = new Row("SecurityParams");
                systemParamRow.set("PARAM_NAME", (Object)paramName);
                systemParamRow.set("PARAM_VALUE", (Object)paramValue);
                systemParamsDO.addRow(systemParamRow);
                DataAccess.add(systemParamsDO);
                SecurityUtil.logger.log(Level.FINER, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                systemParamRow.set("PARAM_VALUE", (Object)paramValue);
                systemParamsDO.updateRow(systemParamRow);
                DataAccess.update(systemParamsDO);
                SecurityUtil.logger.log(Level.FINER, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "Caught exception while updating Parameter:" + paramName + " in DB. {0}", ex);
        }
    }
    
    public static String getSecurityParameter(final String paramKey) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SecurityParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            final DataObject systemParamsDO = DataAccess.get("SecurityParams", criteria);
            final Row systemParamRow = systemParamsDO.getRow("SecurityParams");
            if (systemParamRow == null) {
                return null;
            }
            final String paramValue = (String)systemParamRow.get("PARAM_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "Caught exception while retrieving Security Parameter:" + paramKey + " from DB. {0}", ex);
            return null;
        }
    }
    
    public static void deleteSecurityParameter(final String paramKey) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SecurityParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            DataAccess.delete(criteria);
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "Caught exception while deleting SyM Parameter:" + paramKey + " from DB. {0}", ex);
        }
    }
    
    public static void deleteSecurityParameters(final String[] paramKeys) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SecurityParams", "PARAM_NAME"), (Object)paramKeys, 8, false);
            SyMUtil.getPersistence().delete(criteria);
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "deleteSecurityParameters():- Caught exception while deleting SyM Parameters :" + paramKeys + " from DB. {0}", ex);
        }
    }
    
    public static boolean validateCheckSumForFile(final File file) throws SyMException {
        try {
            if (file.exists()) {
                final String fullFilePath = Paths.get(file.toURI()).toAbsolutePath().toString();
                final String serverHomePath = Paths.get(new File(System.getProperty("server.home")).getCanonicalFile().toURI()).toAbsolutePath().toString();
                if (fullFilePath.startsWith(serverHomePath)) {
                    final String filePathFromServerHome = fullFilePath.replace(serverHomePath + "\\", "").replace("\\", "/");
                    Properties checkSumAddedFilesProps = null;
                    try {
                        checkSumAddedFilesProps = getFilesToCheckSumValues();
                    }
                    catch (final Exception ex) {
                        SecurityUtil.logger.log(Level.INFO, "Checksum Base File Is Corrupted");
                        throw new SyMException(50001, "Checksum Base File Is Corrupted", ex);
                    }
                    try {
                        if (checkSumAddedFilesProps == null || checkSumAddedFilesProps.isEmpty()) {
                            SecurityUtil.logger.log(Level.INFO, "checksum base file checkSumAddedFiles.props is empty");
                            return true;
                        }
                        if (!checkSumAddedFilesProps.containsKey(filePathFromServerHome)) {
                            SecurityUtil.logger.log(Level.INFO, filePathFromServerHome + " is not present in the checkSumAddedFiles.props");
                            return true;
                        }
                        if (validateSHA256Hash(file, checkSumAddedFilesProps.getProperty(filePathFromServerHome))) {
                            return true;
                        }
                        throw new Exception(file.getCanonicalPath() + " is corrupted");
                    }
                    catch (final Exception ex) {
                        SecurityUtil.logger.log(Level.INFO, file.getCanonicalPath() + " is corrupted", ex);
                        throw new SyMException(50002, file.getCanonicalPath() + " is corrupted", ex);
                    }
                }
                final Exception ex2 = new Exception(file.getCanonicalPath() + " is not present in server location");
                SecurityUtil.logger.log(Level.INFO, ex2.getMessage(), ex2);
                throw new SyMException(50003, ex2.getMessage(), ex2);
            }
            final Exception ex3 = new Exception(file.getCanonicalPath() + " does not exist");
            SecurityUtil.logger.log(Level.INFO, ex3.getMessage(), ex3);
            throw new SyMException(50005, ex3.getMessage(), ex3);
        }
        catch (final IOException ex4) {
            SecurityUtil.logger.log(Level.INFO, "IOException in secure read", ex4);
            throw new SyMException(50004, ex4);
        }
    }
    
    private static Properties getFilesToCheckSumValues() throws Exception {
        if (SecurityUtil.filesToChecksumValues == null) {
            try {
                if (!verifyCheckSumBaseFile()) {
                    throw new Exception("Exception while getting checksum for files ");
                }
                final String checkSumAddedFilesPropsFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "checkSumAddedFiles.props";
                synchronized ("FILES_TO_CHECKSUM_VALUES_LOCK") {
                    SecurityUtil.filesToChecksumValues = FileAccessUtil.readProperties(checkSumAddedFilesPropsFilePath);
                }
            }
            catch (final Exception ex) {
                SecurityUtil.logger.log(Level.INFO, "Exception while getting checksum for files ", ex);
                throw ex;
            }
        }
        return SecurityUtil.filesToChecksumValues;
    }
    
    private static boolean verifyCheckSumBaseFile() throws Exception {
        try {
            final File signedFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "checkSumAddedFiles.sign");
            final File checkSumAddedFilesPropsFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "checkSumAddedFiles.props");
            if (signedFile.exists() && checkSumAddedFilesPropsFile.exists()) {
                final String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmm6aS0CwImC7gdLmqiHGuKCHz8sMgOo9GXQt0gmI0356CVROu1Ti8QCPmX8R26o1mOCvoUzUk0t3x8heudeyzrFJzxNtm79TUsfR1LbkE7c+2cmY0VAjmArA3K9eh58daWerPk7k/EFZd7Kq30I69NyDt8SNwntEGWeQglb9kkJys9vxPhj69kq/pkEou6aMlVyHDS5InPa1xpyvQHsZrpetquwdgVw/egUyFqVP6NVArtsiCQ9Tv21wkNgPdMY/i5Gdsl+dAoRFvSfEXd+DdvxUnlUuCraPrrgwx0eUMub04pxnGutEKcufXyQFmD+6d92BrBBC9AOoo3a1qRrvzQIDAQAB";
                final byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
                final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                final PublicKey pubKey = keyFactory.generatePublic(keySpec);
                final byte[] sigToVerify = Files.readAllBytes(Paths.get(signedFile.getCanonicalPath(), new String[0]));
                final Signature sig = Signature.getInstance("SHA256withRSA");
                sig.initVerify(pubKey);
                final List<String> lines = Files.readAllLines(Paths.get(checkSumAddedFilesPropsFile.getCanonicalPath(), new String[0]));
                for (final String line : lines) {
                    final byte[] buffer = line.trim().getBytes();
                    sig.update(buffer, 0, buffer.length);
                }
                return sig.verify(sigToVerify);
            }
            final Exception ex = new Exception("checksum base file or signed file does not exist");
            throw new SyMException(50006, ex.getMessage(), ex);
        }
        catch (final Exception ex2) {
            SecurityUtil.logger.log(Level.SEVERE, "Exception while verifying checksum base file", ex2);
            throw ex2;
        }
    }
    
    public static String getSHA256HashFromInputStream(final InputStream input) throws Exception {
        final byte[] buffer = new byte[8192];
        int read = 0;
        final StringBuilder output = new StringBuilder();
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            while ((read = input.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            final byte[] digest2;
            final byte[] md5sum = digest2 = digest.digest();
            for (final byte b : digest2) {
                output.append(Integer.toString((b & 0xFF) + 256, 16).substring(1));
            }
        }
        catch (final Exception exp) {
            SecurityUtil.logger.log(Level.SEVERE, "Got Exception in getMD5HashFromInputStream(): ", exp);
            throw exp;
        }
        return output.toString();
    }
    
    public static boolean validateSHA256Hash(final File file, final String hash) throws Exception {
        try (final InputStream inputStream = new FileInputStream(file)) {
            return getSHA256HashFromInputStream(inputStream).equals(hash);
        }
    }
    
    public static void updateServerRootCertificateToDatabase(final String rootCrtPath, final String rootKeyPath) {
        try {
            final String rootCACertString = new String(Files.readAllBytes(Paths.get(rootCrtPath, new String[0])));
            final String rootCAKeyString = new String(Files.readAllBytes(Paths.get(rootKeyPath, new String[0])));
            final String rootCACertEncrypt = ApiFactoryProvider.getCryptoAPI().encrypt(rootCACertString, null, null);
            final String rootCAKeyEncrypt = ApiFactoryProvider.getCryptoAPI().encrypt(rootCAKeyString, null, null);
            updateAdvancedSecurityDetail("Server_DMRootCA.crt", rootCACertEncrypt);
            updateAdvancedSecurityDetail("Server_DMRootCA.key", rootCAKeyEncrypt);
            SecurityUtil.logger.log(Level.INFO, "----ADDED SERVER SSL KEYPAIR INTO DB----");
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.SEVERE, "Exception in updateServerRootCertificateToDatabase", ex);
        }
    }
    
    public static void updateAdvancedSecurityDetail(final String paramName, final String paramValue) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AdvancedSecurityDetails", "PARAM_NAME"), (Object)paramName, 0, false);
            final DataObject systemParamsDO = DataAccess.get("AdvancedSecurityDetails", criteria);
            Row systemParamRow = systemParamsDO.getRow("AdvancedSecurityDetails");
            if (systemParamRow == null) {
                systemParamRow = new Row("AdvancedSecurityDetails");
                systemParamRow.set("PARAM_NAME", (Object)paramName);
                systemParamRow.set("PARAM_VALUE", (Object)paramValue);
                systemParamsDO.addRow(systemParamRow);
                DataAccess.add(systemParamsDO);
                SecurityUtil.logger.log(Level.INFO, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                systemParamRow.set("PARAM_VALUE", (Object)paramValue);
                systemParamsDO.updateRow(systemParamRow);
                DataAccess.update(systemParamsDO);
                SecurityUtil.logger.log(Level.INFO, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "Caught exception while updating Parameter:" + paramName + " in DB. {0}", ex);
        }
    }
    
    public static String getAdvancedSecurityDetail(final String paramKey) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AdvancedSecurityDetails", "PARAM_NAME"), (Object)paramKey, 0, false);
            final DataObject systemParamsDO = DataAccess.get("AdvancedSecurityDetails", criteria);
            final Row systemParamRow = systemParamsDO.getRow("AdvancedSecurityDetails");
            if (systemParamRow == null) {
                return null;
            }
            final String paramValue = (String)systemParamRow.get("PARAM_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "Caught exception while retrieving Security Parameter:" + paramKey + " from DB. {0}", ex);
            return null;
        }
    }
    
    public static void deleteAdvancedSecurityDetail(final String paramKey) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AdvancedSecurityDetails", "PARAM_NAME"), (Object)paramKey, 0, false);
            DataAccess.delete(criteria);
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "Caught exception while deleting SyM Parameter:" + paramKey + " from DB. {0}", ex);
        }
    }
    
    public static void migrateRowFromSecurityParamsToAdvanceSecurityDetails(final String paramKey) {
        try {
            final String paramValue = getSecurityParameter(paramKey);
            if (paramValue != null && !paramValue.isEmpty()) {
                updateAdvancedSecurityDetail(paramKey, paramValue);
                deleteSecurityParameter(paramKey);
            }
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "Caught exception while migrating from SecurityParams to AdvancedSecurityDetails" + paramKey + " from DB. {0}", ex);
            throw ex;
        }
    }
    
    public static void migrateRowFromAdvanceSecurityDetailsToSecurityParams(final String paramKey) {
        try {
            final String paramValue = getAdvancedSecurityDetail(paramKey);
            if (paramValue != null && !paramValue.isEmpty()) {
                updateSecurityParameter(paramKey, paramValue);
                deleteAdvancedSecurityDetail(paramKey);
            }
        }
        catch (final Exception ex) {
            SecurityUtil.logger.log(Level.WARNING, "Caught exception while migrating from AdvancedSecurityDetails to SecurityParams" + paramKey + " from DB. {0}", ex);
            throw ex;
        }
    }
    
    public static String getNormalizedRequestURI(final HttpServletRequest request) {
        String requestURI = request.getContextPath() + request.getServletPath();
        final String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            requestURI += pathInfo;
        }
        requestURI = com.adventnet.iam.security.SecurityUtil.getNormalizedURI(requestURI);
        return requestURI;
    }
    
    static {
        SecurityUtil.logger = Logger.getLogger(SecurityUtil.class.getName());
        SecurityUtil.filesToChecksumValues = null;
    }
}
