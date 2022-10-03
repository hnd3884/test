package com.unboundid.util.json;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.util.Iterator;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.PKCS11KeyManager;
import com.unboundid.util.ssl.KeyStoreKeyManager;
import com.unboundid.util.Debug;
import com.unboundid.util.ssl.TrustStoreTrustManager;
import com.unboundid.util.ssl.TrustAllTrustManager;
import java.util.Arrays;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.StartTLSPostConnectProcessor;
import javax.net.SocketFactory;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class SecurityOptions
{
    private static final String FIELD_CLIENT_CERT_ALIAS = "client-certificate-alias";
    private static final String FIELD_KEY_STORE_FILE = "key-store-file";
    private static final String FIELD_KEY_STORE_PIN = "key-store-pin";
    private static final String FIELD_KEY_STORE_PIN_FILE = "key-store-pin-file";
    private static final String FIELD_KEY_STORE_TYPE = "key-store-type";
    private static final String FIELD_SECURITY_TYPE = "security-type";
    private static final String FIELD_TRUST_ALL_CERTS = "trust-all-certificates";
    private static final String FIELD_TRUST_EXPIRED_CERTS = "trust-expired-certificates";
    private static final String FIELD_TRUST_STORE_FILE = "trust-store-file";
    private static final String FIELD_TRUST_STORE_PIN = "trust-store-pin";
    private static final String FIELD_TRUST_STORE_PIN_FILE = "trust-store-pin-file";
    private static final String FIELD_TRUST_STORE_TYPE = "trust-store-type";
    private static final String FIELD_VERIFY_ADDRESS = "verify-address-in-certificate";
    private final boolean verifyAddressInCertificate;
    private final SocketFactory socketFactory;
    private final StartTLSPostConnectProcessor postConnectProcessor;
    
    SecurityOptions(final JSONObject connectionDetailsObject) throws LDAPException {
        boolean useSSL = false;
        boolean useStartTLS = false;
        boolean trustAll = false;
        boolean trustExpired = false;
        boolean verifyAddress = false;
        String certAlias = null;
        String keyStoreFile = null;
        String keyStorePIN = null;
        String keyStoreType = null;
        String trustStoreFile = null;
        String trustStorePIN = null;
        String trustStoreType = null;
        final JSONObject o = LDAPConnectionDetailsJSONSpecification.getObject(connectionDetailsObject, "communication-security");
        if (o != null) {
            LDAPConnectionDetailsJSONSpecification.validateAllowedFields(o, "communication-security", "client-certificate-alias", "key-store-file", "key-store-pin", "key-store-pin-file", "key-store-type", "security-type", "trust-all-certificates", "trust-expired-certificates", "trust-store-file", "trust-store-pin", "trust-store-pin-file", "trust-store-type", "verify-address-in-certificate");
            final String type = StaticUtils.toLowerCase(LDAPConnectionDetailsJSONSpecification.getString(o, "security-type", null));
            if (type == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_MISSING_SECURITY_TYPE.get("security-type"));
            }
            if (type.equals("none")) {
                if (o.getFields().size() > 1) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_INVALID_FIELD_WITH_NONE.get("security-type"));
                }
            }
            else if (type.equals("ssl") || type.equals("tls")) {
                useSSL = true;
            }
            else {
                if (!type.equals("starttls") && !type.equals("start-tls")) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_INVALID_TYPE.get("security-type"));
                }
                useStartTLS = true;
            }
            trustExpired = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "trust-expired-certificates", false);
            trustAll = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "trust-all-certificates", false);
            if (trustAll) {
                LDAPConnectionDetailsJSONSpecification.rejectConflictingFields(o, "trust-all-certificates", "trust-store-file", "trust-store-pin", "trust-store-pin-file", "trust-store-type");
            }
            else {
                trustStoreFile = LDAPConnectionDetailsJSONSpecification.getString(o, "trust-store-file", null);
                if (trustStoreFile == null) {
                    LDAPConnectionDetailsJSONSpecification.rejectUnresolvedDependency(o, "trust-store-file", "trust-store-pin", "trust-store-pin-file", "trust-store-type");
                }
                else {
                    trustStoreType = LDAPConnectionDetailsJSONSpecification.getString(o, "trust-store-type", "JKS").toUpperCase();
                    if (!trustStoreType.equals("JKS") && !trustStoreType.equals("PKCS12")) {
                        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_INVALID_TS_TYPE.get("trust-store-type", trustStoreType));
                    }
                    trustStorePIN = LDAPConnectionDetailsJSONSpecification.getString(o, "trust-store-pin", null);
                    if (trustStorePIN == null) {
                        final String trustStorePINFile = LDAPConnectionDetailsJSONSpecification.getString(o, "trust-store-pin-file", null);
                        if (trustStorePINFile != null) {
                            trustStorePIN = LDAPConnectionDetailsJSONSpecification.getStringFromFile(trustStorePINFile, "trust-store-pin-file");
                        }
                    }
                    else {
                        LDAPConnectionDetailsJSONSpecification.rejectConflictingFields(o, "trust-store-pin", "trust-store-pin-file");
                    }
                }
            }
            verifyAddress = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "verify-address-in-certificate", verifyAddress);
            boolean useKeyStore = false;
            keyStoreFile = LDAPConnectionDetailsJSONSpecification.getString(o, "key-store-file", keyStoreFile);
            if (keyStoreFile != null) {
                useKeyStore = true;
                keyStoreType = LDAPConnectionDetailsJSONSpecification.getString(o, "key-store-type", "JKS").toUpperCase();
                if (!keyStoreType.equals("JKS") && !keyStoreType.equals("PKCS12")) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_INVALID_KS_TYPE_WITH_FILE.get("key-store-type", keyStoreType));
                }
            }
            else {
                keyStoreType = LDAPConnectionDetailsJSONSpecification.getString(o, "key-store-type", null);
                if (keyStoreType != null) {
                    useKeyStore = true;
                    keyStoreType = keyStoreType.toUpperCase();
                    if (!keyStoreType.equals("PKCS11")) {
                        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_INVALID_KS_TYPE_WITHOUT_FILE.get("key-store-type", keyStoreType, "key-store-file"));
                    }
                }
            }
            if (useKeyStore) {
                certAlias = LDAPConnectionDetailsJSONSpecification.getString(o, "client-certificate-alias", null);
                keyStorePIN = LDAPConnectionDetailsJSONSpecification.getString(o, "key-store-pin", null);
                if (keyStorePIN == null) {
                    final String keyStorePINFile = LDAPConnectionDetailsJSONSpecification.getString(o, "key-store-pin-file", null);
                    if (keyStorePINFile != null) {
                        keyStorePIN = LDAPConnectionDetailsJSONSpecification.getStringFromFile(keyStorePINFile, "key-store-pin-file");
                    }
                }
                else {
                    LDAPConnectionDetailsJSONSpecification.rejectConflictingFields(o, "key-store-pin", "key-store-pin-file");
                }
            }
            else {
                for (final String fieldName : Arrays.asList("key-store-pin", "key-store-pin-file", "client-certificate-alias")) {
                    if (o.getField(fieldName) != null) {
                        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_INVALID_FIELD_WITHOUT_KS.get(fieldName));
                    }
                }
            }
        }
        this.verifyAddressInCertificate = verifyAddress;
        if (!useSSL) {
            if (!useStartTLS) {
                this.socketFactory = SocketFactory.getDefault();
                this.postConnectProcessor = null;
                return;
            }
        }
        TrustManager trustManager;
        try {
            if (trustAll) {
                trustManager = new TrustAllTrustManager(!trustExpired);
            }
            else if (trustStoreFile != null) {
                char[] trustStorePINArray;
                if (trustStorePIN == null) {
                    trustStorePINArray = null;
                }
                else {
                    trustStorePINArray = trustStorePIN.toCharArray();
                }
                trustManager = new TrustStoreTrustManager(trustStoreFile, trustStorePINArray, trustStoreType, !trustExpired);
            }
            else {
                trustManager = null;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_CANNOT_CREATE_TRUST_MANAGER.get(StaticUtils.getExceptionMessage(e)), e);
        }
        KeyManager keyManager;
        try {
            char[] keyStorePINArray;
            if (keyStorePIN == null) {
                keyStorePINArray = null;
            }
            else {
                keyStorePINArray = keyStorePIN.toCharArray();
            }
            if (keyStoreFile != null) {
                keyManager = new KeyStoreKeyManager(keyStoreFile, keyStorePINArray, keyStoreType, certAlias);
            }
            else if (keyStoreType != null && keyStoreType.equals("PKCS11")) {
                keyManager = new PKCS11KeyManager(keyStorePINArray, certAlias);
            }
            else {
                keyManager = null;
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.LOCAL_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_CANNOT_CREATE_KEY_MANAGER.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        final SSLUtil sslUtil = new SSLUtil(keyManager, trustManager);
        if (useSSL) {
            try {
                this.socketFactory = sslUtil.createSSLSocketFactory();
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new LDAPException(ResultCode.LOCAL_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_CANNOT_CREATE_SOCKET_FACTORY.get(StaticUtils.getExceptionMessage(e3)), e3);
            }
            this.postConnectProcessor = null;
        }
        else {
            this.socketFactory = SocketFactory.getDefault();
            try {
                this.postConnectProcessor = new StartTLSPostConnectProcessor(sslUtil.createSSLSocketFactory());
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new LDAPException(ResultCode.LOCAL_ERROR, JSONMessages.ERR_SECURITY_OPTIONS_CANNOT_CREATE_POST_CONNECT_PROCESSOR.get(StaticUtils.getExceptionMessage(e3)), e3);
            }
        }
    }
    
    boolean verifyAddressInCertificate() {
        return this.verifyAddressInCertificate;
    }
    
    SocketFactory getSocketFactory() {
        return this.socketFactory;
    }
    
    StartTLSPostConnectProcessor getPostConnectProcessor() {
        return this.postConnectProcessor;
    }
}
