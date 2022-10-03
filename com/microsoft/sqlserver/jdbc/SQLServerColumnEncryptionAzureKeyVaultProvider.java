package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.ArrayList;
import com.microsoft.azure.keyvault.models.KeyBundle;
import com.microsoft.azure.keyvault.models.KeyVerifyResult;
import com.microsoft.azure.keyvault.webkey.JsonWebKeySignatureAlgorithm;
import com.microsoft.azure.keyvault.models.KeyOperationResult;
import com.microsoft.azure.keyvault.webkey.JsonWebKeyEncryptionAlgorithm;
import java.util.Iterator;
import java.net.URISyntaxException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import com.microsoft.rest.protocol.ResponseBuilder;
import com.microsoft.azure.AzureResponseBuilder;
import com.microsoft.rest.protocol.SerializerAdapter;
import com.microsoft.azure.serializer.AzureJacksonAdapter;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import com.microsoft.rest.RestClient;
import retrofit2.Retrofit;
import okhttp3.OkHttpClient;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import com.microsoft.azure.keyvault.KeyVaultClient;
import java.util.List;
import java.util.logging.Logger;

public class SQLServerColumnEncryptionAzureKeyVaultProvider extends SQLServerColumnEncryptionKeyStoreProvider
{
    private static final Logger akvLogger;
    String name;
    private final String baseUrl = "https://{vaultBaseUrl}";
    private static final String MSSQL_JDBC_PROPERTIES = "mssql-jdbc.properties";
    private static final String AKV_TRUSTED_ENDPOINTS_KEYWORD = "AKVTrustedEndpoints";
    private static final List<String> akvTrustedEndpoints;
    private final String rsaEncryptionAlgorithmWithOAEPForAKV = "RSA-OAEP";
    private final byte[] firstVersion;
    private KeyVaultClient keyVaultClient;
    private KeyVaultCredential credentials;
    
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Deprecated
    public SQLServerColumnEncryptionAzureKeyVaultProvider(final SQLServerKeyVaultAuthenticationCallback authenticationCallback, final ExecutorService executorService) throws SQLServerException {
        this(authenticationCallback);
    }
    
    public SQLServerColumnEncryptionAzureKeyVaultProvider(final SQLServerKeyVaultAuthenticationCallback authenticationCallback) throws SQLServerException {
        this.name = "AZURE_KEY_VAULT";
        this.firstVersion = new byte[] { 1 };
        if (null == authenticationCallback) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NullValue"));
            final Object[] msgArgs1 = { "SQLServerKeyVaultAuthenticationCallback" };
            throw new SQLServerException(form.format(msgArgs1), (Throwable)null);
        }
        this.credentials = new KeyVaultCredential(authenticationCallback);
        final RestClient restClient = new RestClient.Builder(new OkHttpClient.Builder(), new Retrofit.Builder()).withBaseUrl("https://{vaultBaseUrl}").withCredentials((ServiceClientCredentials)this.credentials).withSerializerAdapter((SerializerAdapter)new AzureJacksonAdapter()).withResponseBuilderFactory((ResponseBuilder.Factory)new AzureResponseBuilder.Factory()).build();
        this.keyVaultClient = new KeyVaultClient(restClient);
    }
    
    public SQLServerColumnEncryptionAzureKeyVaultProvider(final String clientId, final String clientKey) throws SQLServerException {
        this.name = "AZURE_KEY_VAULT";
        this.firstVersion = new byte[] { 1 };
        this.credentials = new KeyVaultCredential(clientId, clientKey);
        this.keyVaultClient = new KeyVaultClient((ServiceClientCredentials)this.credentials);
    }
    
    @Override
    public byte[] decryptColumnEncryptionKey(final String masterKeyPath, String encryptionAlgorithm, final byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        this.ValidateNonEmptyAKVPath(masterKeyPath);
        if (null == encryptedColumnEncryptionKey) {
            throw new SQLServerException(SQLServerException.getErrString("R_NullEncryptedColumnEncryptionKey"), (Throwable)null);
        }
        if (0 == encryptedColumnEncryptionKey.length) {
            throw new SQLServerException(SQLServerException.getErrString("R_EmptyEncryptedColumnEncryptionKey"), (Throwable)null);
        }
        encryptionAlgorithm = this.validateEncryptionAlgorithm(encryptionAlgorithm);
        final int keySizeInBytes = this.getAKVKeySize(masterKeyPath);
        if (encryptedColumnEncryptionKey[0] != this.firstVersion[0]) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidEcryptionAlgorithmVersion"));
            final Object[] msgArgs = { String.format("%02X ", encryptedColumnEncryptionKey[0]), String.format("%02X ", this.firstVersion[0]) };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        int currentIndex = this.firstVersion.length;
        final short keyPathLength = this.convertTwoBytesToShort(encryptedColumnEncryptionKey, currentIndex);
        currentIndex += 2;
        final short cipherTextLength = this.convertTwoBytesToShort(encryptedColumnEncryptionKey, currentIndex);
        currentIndex += 2;
        currentIndex += keyPathLength;
        if (cipherTextLength != keySizeInBytes) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_AKVKeyLengthError"));
            final Object[] msgArgs2 = { cipherTextLength, keySizeInBytes, masterKeyPath };
            throw new SQLServerException(this, form2.format(msgArgs2), null, 0, false);
        }
        final int signatureLength = encryptedColumnEncryptionKey.length - currentIndex - cipherTextLength;
        if (signatureLength != keySizeInBytes) {
            final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_AKVSignatureLengthError"));
            final Object[] msgArgs3 = { signatureLength, keySizeInBytes, masterKeyPath };
            throw new SQLServerException(this, form3.format(msgArgs3), null, 0, false);
        }
        final byte[] cipherText = new byte[cipherTextLength];
        System.arraycopy(encryptedColumnEncryptionKey, currentIndex, cipherText, 0, cipherTextLength);
        currentIndex += cipherTextLength;
        final byte[] signature = new byte[signatureLength];
        System.arraycopy(encryptedColumnEncryptionKey, currentIndex, signature, 0, signatureLength);
        final byte[] hash = new byte[encryptedColumnEncryptionKey.length - signature.length];
        System.arraycopy(encryptedColumnEncryptionKey, 0, hash, 0, encryptedColumnEncryptionKey.length - signature.length);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (final NoSuchAlgorithmException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
        md.update(hash);
        final byte[] dataToVerify = md.digest();
        if (null == dataToVerify) {
            throw new SQLServerException(SQLServerException.getErrString("R_HashNull"), (Throwable)null);
        }
        if (!this.AzureKeyVaultVerifySignature(dataToVerify, signature, masterKeyPath)) {
            final MessageFormat form4 = new MessageFormat(SQLServerException.getErrString("R_CEKSignatureNotMatchCMK"));
            final Object[] msgArgs4 = { masterKeyPath };
            throw new SQLServerException(this, form4.format(msgArgs4), null, 0, false);
        }
        final byte[] decryptedCEK = this.AzureKeyVaultUnWrap(masterKeyPath, encryptionAlgorithm, cipherText);
        return decryptedCEK;
    }
    
    private short convertTwoBytesToShort(final byte[] input, final int index) throws SQLServerException {
        if (index + 1 >= input.length) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_ByteToShortConversion"), null, 0, false);
        }
        final ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(input[index]);
        byteBuffer.put(input[index + 1]);
        final short shortVal = byteBuffer.getShort(0);
        return shortVal;
    }
    
    @Override
    public byte[] encryptColumnEncryptionKey(final String masterKeyPath, String encryptionAlgorithm, final byte[] columnEncryptionKey) throws SQLServerException {
        this.ValidateNonEmptyAKVPath(masterKeyPath);
        if (null == columnEncryptionKey) {
            throw new SQLServerException(SQLServerException.getErrString("R_NullColumnEncryptionKey"), (Throwable)null);
        }
        if (0 == columnEncryptionKey.length) {
            throw new SQLServerException(SQLServerException.getErrString("R_EmptyCEK"), (Throwable)null);
        }
        encryptionAlgorithm = this.validateEncryptionAlgorithm(encryptionAlgorithm);
        final int keySizeInBytes = this.getAKVKeySize(masterKeyPath);
        final byte[] version = { this.firstVersion[0] };
        final byte[] masterKeyPathBytes = masterKeyPath.toLowerCase(Locale.ENGLISH).getBytes(StandardCharsets.UTF_16LE);
        final byte[] keyPathLength = { (byte)((short)masterKeyPathBytes.length & 0xFF), (byte)((short)masterKeyPathBytes.length >> 8 & 0xFF) };
        final byte[] cipherText = this.AzureKeyVaultWrap(masterKeyPath, encryptionAlgorithm, columnEncryptionKey);
        final byte[] cipherTextLength = { (byte)((short)cipherText.length & 0xFF), (byte)((short)cipherText.length >> 8 & 0xFF) };
        if (cipherText.length != keySizeInBytes) {
            throw new SQLServerException(SQLServerException.getErrString("R_CipherTextLengthNotMatchRSASize"), (Throwable)null);
        }
        final byte[] dataToHash = new byte[version.length + keyPathLength.length + cipherTextLength.length + masterKeyPathBytes.length + cipherText.length];
        int destinationPosition = version.length;
        System.arraycopy(version, 0, dataToHash, 0, version.length);
        System.arraycopy(keyPathLength, 0, dataToHash, destinationPosition, keyPathLength.length);
        destinationPosition += keyPathLength.length;
        System.arraycopy(cipherTextLength, 0, dataToHash, destinationPosition, cipherTextLength.length);
        destinationPosition += cipherTextLength.length;
        System.arraycopy(masterKeyPathBytes, 0, dataToHash, destinationPosition, masterKeyPathBytes.length);
        destinationPosition += masterKeyPathBytes.length;
        System.arraycopy(cipherText, 0, dataToHash, destinationPosition, cipherText.length);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (final NoSuchAlgorithmException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
        md.update(dataToHash);
        final byte[] dataToSign = md.digest();
        final byte[] signedHash = this.AzureKeyVaultSignHashedData(dataToSign, masterKeyPath);
        if (signedHash.length != keySizeInBytes) {
            throw new SQLServerException(SQLServerException.getErrString("R_SignedHashLengthError"), (Throwable)null);
        }
        if (!this.AzureKeyVaultVerifySignature(dataToSign, signedHash, masterKeyPath)) {
            throw new SQLServerException(SQLServerException.getErrString("R_InvalidSignatureComputed"), (Throwable)null);
        }
        final int encryptedColumnEncryptionKeyLength = version.length + cipherTextLength.length + keyPathLength.length + cipherText.length + masterKeyPathBytes.length + signedHash.length;
        final byte[] encryptedColumnEncryptionKey = new byte[encryptedColumnEncryptionKeyLength];
        int currentIndex = 0;
        System.arraycopy(version, 0, encryptedColumnEncryptionKey, currentIndex, version.length);
        currentIndex += version.length;
        System.arraycopy(keyPathLength, 0, encryptedColumnEncryptionKey, currentIndex, keyPathLength.length);
        currentIndex += keyPathLength.length;
        System.arraycopy(cipherTextLength, 0, encryptedColumnEncryptionKey, currentIndex, cipherTextLength.length);
        currentIndex += cipherTextLength.length;
        System.arraycopy(masterKeyPathBytes, 0, encryptedColumnEncryptionKey, currentIndex, masterKeyPathBytes.length);
        currentIndex += masterKeyPathBytes.length;
        System.arraycopy(cipherText, 0, encryptedColumnEncryptionKey, currentIndex, cipherText.length);
        currentIndex += cipherText.length;
        System.arraycopy(signedHash, 0, encryptedColumnEncryptionKey, currentIndex, signedHash.length);
        return encryptedColumnEncryptionKey;
    }
    
    private String validateEncryptionAlgorithm(String encryptionAlgorithm) throws SQLServerException {
        if (null == encryptionAlgorithm) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_NullKeyEncryptionAlgorithm"), null, 0, false);
        }
        if ("RSA_OAEP".equalsIgnoreCase(encryptionAlgorithm)) {
            encryptionAlgorithm = "RSA-OAEP";
        }
        if (!"RSA-OAEP".equalsIgnoreCase(encryptionAlgorithm.trim())) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidKeyEncryptionAlgorithm"));
            final Object[] msgArgs = { encryptionAlgorithm, "RSA-OAEP" };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        return encryptionAlgorithm;
    }
    
    private void ValidateNonEmptyAKVPath(final String masterKeyPath) throws SQLServerException {
        if (null == masterKeyPath || masterKeyPath.trim().isEmpty()) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_AKVPathNull"));
            final Object[] msgArgs = { masterKeyPath };
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        URI parsedUri = null;
        try {
            parsedUri = new URI(masterKeyPath);
            String host = parsedUri.getHost();
            if (null != host) {
                host = host.toLowerCase(Locale.ENGLISH);
            }
            for (final String endpoint : SQLServerColumnEncryptionAzureKeyVaultProvider.akvTrustedEndpoints) {
                if (null != host && host.endsWith(endpoint)) {
                    return;
                }
            }
        }
        catch (final URISyntaxException e) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_AKVURLInvalid"));
            final Object[] msgArgs2 = { masterKeyPath };
            throw new SQLServerException(form2.format(msgArgs2), null, 0, e);
        }
        final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_AKVMasterKeyPathInvalid"));
        final Object[] msgArgs3 = { masterKeyPath };
        throw new SQLServerException(null, form3.format(msgArgs3), null, 0, false);
    }
    
    private byte[] AzureKeyVaultWrap(final String masterKeyPath, final String encryptionAlgorithm, final byte[] columnEncryptionKey) throws SQLServerException {
        if (null == columnEncryptionKey) {
            throw new SQLServerException(SQLServerException.getErrString("R_CEKNull"), (Throwable)null);
        }
        final JsonWebKeyEncryptionAlgorithm jsonEncryptionAlgorithm = new JsonWebKeyEncryptionAlgorithm(encryptionAlgorithm);
        final KeyOperationResult wrappedKey = this.keyVaultClient.wrapKey(masterKeyPath, jsonEncryptionAlgorithm, columnEncryptionKey);
        return wrappedKey.result();
    }
    
    private byte[] AzureKeyVaultUnWrap(final String masterKeyPath, final String encryptionAlgorithm, final byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        if (null == encryptedColumnEncryptionKey) {
            throw new SQLServerException(SQLServerException.getErrString("R_EncryptedCEKNull"), (Throwable)null);
        }
        if (0 == encryptedColumnEncryptionKey.length) {
            throw new SQLServerException(SQLServerException.getErrString("R_EmptyEncryptedCEK"), (Throwable)null);
        }
        final JsonWebKeyEncryptionAlgorithm jsonEncryptionAlgorithm = new JsonWebKeyEncryptionAlgorithm(encryptionAlgorithm);
        final KeyOperationResult unwrappedKey = this.keyVaultClient.unwrapKey(masterKeyPath, jsonEncryptionAlgorithm, encryptedColumnEncryptionKey);
        return unwrappedKey.result();
    }
    
    private byte[] AzureKeyVaultSignHashedData(final byte[] dataToSign, final String masterKeyPath) throws SQLServerException {
        assert null != dataToSign && 0 != dataToSign.length;
        final KeyOperationResult signedData = this.keyVaultClient.sign(masterKeyPath, JsonWebKeySignatureAlgorithm.RS256, dataToSign);
        return signedData.result();
    }
    
    private boolean AzureKeyVaultVerifySignature(final byte[] dataToVerify, final byte[] signature, final String masterKeyPath) throws SQLServerException {
        assert null != dataToVerify && 0 != dataToVerify.length;
        assert null != signature && 0 != signature.length;
        final KeyVerifyResult valid = this.keyVaultClient.verify(masterKeyPath, JsonWebKeySignatureAlgorithm.RS256, dataToVerify, signature);
        return valid.value();
    }
    
    private int getAKVKeySize(final String masterKeyPath) throws SQLServerException {
        final KeyBundle retrievedKey = this.keyVaultClient.getKey(masterKeyPath);
        if (null == retrievedKey) {
            final String[] keyTokens = masterKeyPath.split("/");
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_AKVKeyNotFound"));
            final Object[] msgArgs = { keyTokens[keyTokens.length - 1] };
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        if (!"RSA".equalsIgnoreCase(retrievedKey.key().kty().toString()) && !"RSA-HSM".equalsIgnoreCase(retrievedKey.key().kty().toString())) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_NonRSAKey"));
            final Object[] msgArgs2 = { retrievedKey.key().kty().toString() };
            throw new SQLServerException(null, form2.format(msgArgs2), null, 0, false);
        }
        return retrievedKey.key().n().length;
    }
    
    @Override
    public boolean verifyColumnMasterKeyMetadata(final String masterKeyPath, final boolean allowEnclaveComputations, final byte[] signature) throws SQLServerException {
        if (!allowEnclaveComputations) {
            return false;
        }
        KeyStoreProviderCommon.validateNonEmptyMasterKeyPath(masterKeyPath);
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.name.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update(masterKeyPath.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update("true".getBytes(StandardCharsets.UTF_16LE));
            final byte[] dataToVerify = md.digest();
            if (null == dataToVerify) {
                throw new SQLServerException(SQLServerException.getErrString("R_HashNull"), (Throwable)null);
            }
            final byte[] signedHash = this.AzureKeyVaultSignHashedData(dataToVerify, masterKeyPath);
            if (null == signedHash) {
                throw new SQLServerException(SQLServerException.getErrString("R_SignedHashLengthError"), (Throwable)null);
            }
            return this.AzureKeyVaultVerifySignature(dataToVerify, signature, masterKeyPath);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
    }
    
    private static List<String> getTrustedEndpoints() {
        final Properties mssqlJdbcProperties = getMssqlJdbcProperties();
        final List<String> trustedEndpoints = new ArrayList<String>();
        boolean append = true;
        if (null != mssqlJdbcProperties) {
            String endpoints = mssqlJdbcProperties.getProperty("AKVTrustedEndpoints");
            if (null != endpoints && !endpoints.isBlank()) {
                endpoints = endpoints.trim();
                if (';' != endpoints.charAt(0)) {
                    append = false;
                }
                else {
                    endpoints = endpoints.substring(1);
                }
                final String[] split;
                final String[] entries = split = endpoints.split(";");
                for (final String entry : split) {
                    if (null != entry && !entry.isBlank()) {
                        trustedEndpoints.add(entry.trim());
                    }
                }
            }
        }
        if (append) {
            trustedEndpoints.add("vault.azure.net");
            trustedEndpoints.add("vault.azure.cn");
            trustedEndpoints.add("vault.usgovcloudapi.net");
            trustedEndpoints.add("vault.microsoftazure.de");
        }
        return trustedEndpoints;
    }
    
    private static Properties getMssqlJdbcProperties() {
        Properties props = null;
        try (final FileInputStream in = new FileInputStream("mssql-jdbc.properties")) {
            props = new Properties();
            props.load(in);
        }
        catch (final IOException e) {
            if (SQLServerColumnEncryptionAzureKeyVaultProvider.akvLogger.isLoggable(Level.FINER)) {
                SQLServerColumnEncryptionAzureKeyVaultProvider.akvLogger.finer("Unable to load the mssql-jdbc.properties file: " + e);
            }
        }
        return (null != props && !props.isEmpty()) ? props : null;
    }
    
    static {
        akvLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider");
        akvTrustedEndpoints = getTrustedEndpoints();
    }
}
