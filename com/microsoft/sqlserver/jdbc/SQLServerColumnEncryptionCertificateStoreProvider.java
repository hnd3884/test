package com.microsoft.sqlserver.jdbc;

import java.util.Locale;
import java.util.logging.Logger;

public final class SQLServerColumnEncryptionCertificateStoreProvider extends SQLServerColumnEncryptionKeyStoreProvider
{
    private static final Logger windowsCertificateStoreLogger;
    static boolean isWindows;
    String name;
    static final String localMachineDirectory = "LocalMachine";
    static final String currentUserDirectory = "CurrentUser";
    static final String myCertificateStore = "My";
    
    public SQLServerColumnEncryptionCertificateStoreProvider() {
        this.name = "MSSQL_CERTIFICATE_STORE";
        SQLServerColumnEncryptionCertificateStoreProvider.windowsCertificateStoreLogger.entering(SQLServerColumnEncryptionCertificateStoreProvider.class.getName(), "SQLServerColumnEncryptionCertificateStoreProvider");
    }
    
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public byte[] encryptColumnEncryptionKey(final String masterKeyPath, final String encryptionAlgorithm, final byte[] plainTextColumnEncryptionKey) throws SQLServerException {
        throw new SQLServerException(null, SQLServerException.getErrString("R_InvalidWindowsCertificateStoreEncryption"), null, 0, false);
    }
    
    private byte[] decryptColumnEncryptionKeyWindows(final String masterKeyPath, final String encryptionAlgorithm, final byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        try {
            return AuthenticationJNI.DecryptColumnEncryptionKey(masterKeyPath, encryptionAlgorithm, encryptedColumnEncryptionKey);
        }
        catch (final DLLException e) {
            DLLException.buildException(e.GetErrCode(), e.GetParam1(), e.GetParam2(), e.GetParam3());
            return null;
        }
    }
    
    @Override
    public byte[] decryptColumnEncryptionKey(final String masterKeyPath, final String encryptionAlgorithm, final byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        SQLServerColumnEncryptionCertificateStoreProvider.windowsCertificateStoreLogger.entering(SQLServerColumnEncryptionCertificateStoreProvider.class.getName(), "decryptColumnEncryptionKey", "Decrypting Column Encryption Key.");
        if (SQLServerColumnEncryptionCertificateStoreProvider.isWindows) {
            final byte[] plainCek = this.decryptColumnEncryptionKeyWindows(masterKeyPath, encryptionAlgorithm, encryptedColumnEncryptionKey);
            SQLServerColumnEncryptionCertificateStoreProvider.windowsCertificateStoreLogger.exiting(SQLServerColumnEncryptionCertificateStoreProvider.class.getName(), "decryptColumnEncryptionKey", "Finished decrypting Column Encryption Key.");
            return plainCek;
        }
        throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), (Throwable)null);
    }
    
    @Override
    public boolean verifyColumnMasterKeyMetadata(final String masterKeyPath, final boolean allowEnclaveComputations, final byte[] signature) throws SQLServerException {
        try {
            return AuthenticationJNI.VerifyColumnMasterKeyMetadata(masterKeyPath, allowEnclaveComputations, signature);
        }
        catch (final DLLException e) {
            DLLException.buildException(e.GetErrCode(), e.GetParam1(), e.GetParam2(), e.GetParam3());
            return false;
        }
    }
    
    static {
        windowsCertificateStoreLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionCertificateStoreProvider");
        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("windows")) {
            SQLServerColumnEncryptionCertificateStoreProvider.isWindows = true;
        }
        else {
            SQLServerColumnEncryptionCertificateStoreProvider.isWindows = false;
        }
    }
}
