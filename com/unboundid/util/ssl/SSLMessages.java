package com.unboundid.util.ssl;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum SSLMessages
{
    ERR_AGGREGATE_TRUST_MANAGER_NONE_TRUSTED("Certificate {0} was not trusted by any of the configured trust managers.  The trust manager messages were:  {1}"), 
    ERR_CERTIFICATE_REJECTED_BY_END_OF_STREAM("Certificate {0} cannot be trusted because the end of the standard input stream was reached without finding information about whether to trust the presented certificate."), 
    ERR_CERTIFICATE_REJECTED_BY_USER("The user rejected certificate {0}."), 
    ERR_HOSTNAME_NOT_FOUND("The presented certificate ''{0}'' did not contain any of the acceptable addresses in the CN subject attribute or in a subjectAltName extension."), 
    ERR_HOST_NAME_SSL_SOCKET_VERIFIER_EXCEPTION("Unable to verify an attempt to to establish a secure connection to ''{0}:{1,number,0}'' because an unexpected error was encountered during validation processing:  {2}"), 
    ERR_HOST_NAME_SSL_SOCKET_VERIFIER_HOSTNAME_NOT_FOUND("Hostname verification failed because the expected hostname ''{0}'' was not found in peer certificate ''{1}''."), 
    ERR_HOST_NAME_SSL_SOCKET_VERIFIER_NO_PEER_CERTS("Unable to verify an attempt to establish a secure connection to ''{0}:{1,number,0}'' because no peer certificates are available."), 
    ERR_HOST_NAME_SSL_SOCKET_VERIFIER_NO_SESSION("Unable to verify an attempt to establish a secure connection to ''{0}:{1,number,0}'' because no session information is available for the connection."), 
    ERR_HOST_NAME_SSL_SOCKET_VERIFIER_PEER_NOT_X509("Unable to verify an attempt to establish a secure connection to ''{0}:{1,number,0}'' because the peer certificate was not an X.509 certificate.  The reported certificate type is ''{2}''."), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_CACERTS_NOT_FOUND_NO_EXCEPTION("Unable to locate the cacerts keystore file containing the JVM's default set of trusted issuers."), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_CACERTS_NOT_FOUND_WITH_EXCEPTION("Unable to locate a valid keystore file containing the JVM's default set of trusted issuers.  One or more errors were encountered during processing."), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_CANNOT_ERROR_LOADING_KEYSTORE("Unable to parse the contents of file ''{0}'' as a keystore:  {1}"), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_CANNOT_INSTANTIATE_KEYSTORE("Unable to instantiate a keystore of type ''{0}'':  {1}"), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_CERT_EXPIRED("The presented certificate chain containing certificates {0} cannot be trusted because certificate ''{1}'' expired at {2}."), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_CERT_NOT_YET_VALID("The presented certificate chain containing certificates {0} cannot be trusted because certificate ''{1}'' will not become valid until {2}."), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_ERROR_ITERATING_THROUGH_CACERTS("An unexpected error occurred while attempting to iterate through certificates in the keystore loaded from file ''{0}'':  {1}"), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_INVALID_JAVA_HOME("System property ''{0}'' value ''{1}'' is not a valid path to a directory on the local filesystem."), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_LOAD_ERROR("While examining file ''{0}'', the error encountered was:  {1}"), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_NO_CERTS_IN_CHAIN("The certificate chain cannot be trusted because it was null or empty."), 
    ERR_JVM_DEFAULT_TRUST_MANAGER_NO_JAVA_HOME("Unable to determine the location of the Java installation because the ''{0}'' system property is not defined."), 
    ERR_JVM_DEFAULT_TRUST_MANGER_NO_TRUSTED_ISSUER_FOUND("The presented certificate chain containing certificates {0} cannot be trusted because none of the certificates in that chain were found in the JVM's default set of trusted issuers."), 
    ERR_KEYSTORE_CANNOT_GET_KEY_MANAGERS("Unable to obtain key managers for key store file ''{0}'' using format ''{1}'':  {2}"), 
    ERR_KEYSTORE_CANNOT_LOAD("Unable to load key store ''{0}'' of type ''{1}'':  {2}"), 
    ERR_KEYSTORE_NO_SUCH_FILE("Key store file ''{0}'' does not exist."), 
    ERR_NO_ENABLED_SSL_CIPHER_SUITES_AVAILABLE_FOR_SOCKET("None of the configured set of enabled SSL cipher suites could be configured for use with the SSL socket.  The currently-enabled cipher suites are:  {0}.  The SSL socket indicated its supported cipher suites are:  {1}.  You may explicitly configure the enabled protocols using the {2} system property or by calling the {3} method."), 
    ERR_NO_ENABLED_SSL_PROTOCOLS_AVAILABLE_FOR_SOCKET("None of the configured set of enabled SSL protocols could be configured for use with the SSL socket.  The currently-enabled protocols are:  {0}.  The SSL socket indicated its supported protocols are:  {1}.  You may explicitly configure the enabled protocols using the {2} system property or by calling the {3} method."), 
    ERR_PKCS11_CANNOT_ACCESS("Unable to access the PKCS#11 key store:  {0}"), 
    ERR_PKCS11_CANNOT_GET_KEY_MANAGERS("Unable to obtain key managers for the PKCS#11 key store:  {0}"), 
    ERR_SET_ENABLED_PROTOCOLS_SOCKET_SHUTDOWN_INPUT("The shutdownInput method is not supported for SSL sockets."), 
    ERR_SET_ENABLED_PROTOCOLS_SOCKET_SHUTDOWN_OUTPUT("The shutdownOutput method is not supported for SSL sockets."), 
    ERR_SET_ENABLED_PROTOCOLS_SOCKET_URGENT_DATA_NOT_SUPPORTED("Sending urgent data is not supported for SSL sockets."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_ANON_AUTH("The cipher suite uses anonymous authentication."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_EXPORT_ENCRYPTION("The cipher suite uses a weakened export-grade encryption."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_INIT_ERROR("ERROR:  An unexpected error occurred while trying to initialize the TLS cipher suite selector:  {0}"), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_LEGACY_SSL_PROTOCOL("The cipher suite uses a legacy SSL protocol."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_BE_ALG("The cipher suite uses the non-recommended {0} bulk encryption algorithm."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_DIGEST_ALG("The cipher suite uses the non-recommended {0} message digest algorithm."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_KE_ALG("The cipher suite uses the non-recommended {0} key exchange algorithm."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_UNKNOWN_BE_ALG("The cipher suite uses a non-recommended bulk encryption algorithm."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_UNKNOWN_DIGEST_ALG("The cipher suite uses a non-recommended message digest algorithm."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_UNKNOWN_KE_ALG("The cipher suite uses a non-recommended key exchange algorithm."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_NULL_COMPONENT("The cipher suite uses a NULL key exchange, authentication, bulk encryption, or message digest algorithm."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_PSK("The cipher suite relies on a pre-shared key."), 
    ERR_TLS_CIPHER_SUITE_SELECTOR_UNRECOGNIZED_PROTOCOL("The cipher suite uses an unrecognized protocol."), 
    ERR_TRUSTSTORE_CANNOT_GET_TRUST_MANAGERS("Unable to obtain trust managers for trust store file ''{0}'' using format ''{1}'':  {2}"), 
    ERR_TRUSTSTORE_CANNOT_LOAD("Unable to load trust store ''{0}'' of type ''{1}'':  {2}"), 
    ERR_TRUSTSTORE_NO_SUCH_FILE("Trust store file ''{0}'' does not exist."), 
    ERR_TRUSTSTORE_UNSUPPORTED_FORMAT("Unsupported trust store format ''{0}''."), 
    ERR_VALIDITY_TOO_EARLY("The presented certificate ''{0}'' will not be valid until {1}."), 
    ERR_VALIDITY_TOO_LATE("The presented certificate ''{0}'' expired on {1}."), 
    INFO_PROMPT_CLIENT_HEADING("The client presented the following certificate chain:"), 
    INFO_PROMPT_ISSUER_SUBJECT("Issuer {0,number,0} Subject: {1}"), 
    INFO_PROMPT_MESSAGE("Do you wish to trust this certificate?  Enter 'y' or 'n':"), 
    INFO_PROMPT_SERVER_HEADING("The server presented the following certificate chain:"), 
    INFO_PROMPT_SHA1_FINGERPRINT("SHA-1 Fingerprint: {0}"), 
    INFO_PROMPT_SHA256_FINGERPRINT("256-bit SHA-2 Fingerprint: {0}"), 
    INFO_PROMPT_SUBJECT("Subject: {0}"), 
    INFO_PROMPT_VALID_FROM("Valid From: {0}"), 
    INFO_PROMPT_VALID_TO("Valid Until: {0}"), 
    INFO_TLS_CIPHER_SUITE_SELECTOR_TOOL_DESC("Provides information about the TLS cipher suites that are supported by the JVM and selects a recommended set of suites for secure communication."), 
    WARN_PROMPT_PROCESSOR_BC_DISALLOWED_INTERMEDIATE("WARNING:  Issuer certificate ''{0}'' has a basic constraints extension that indicates there should not be any intermediate CA certificates between it and the end entity certificate, but one or more intermediate CA certificates were found ahead of it in the chain."), 
    WARN_PROMPT_PROCESSOR_BC_NOT_CA("WARNING:  Issuer certificate ''{0}'' has a basic constraints extension that indicates the certificate should not be trusted as an issuer certificate."), 
    WARN_PROMPT_PROCESSOR_BC_TOO_MANY_INTERMEDIATES("WARNING:  Issuer certificate ''{0}'' has a basic constraints extension that indicates there should be at most {1,number,0} intermediate CA certificate(s) between it and the end entity certificate, but {2,number,0} intermediate CA certificates were found."), 
    WARN_PROMPT_PROCESSOR_CERT_BAD_SIGNATURE("WARNING:  {0} certificate ''{1}'' has an invalid signature.  This may mean that the certificate has been forged or that it has been altered since it was signed."), 
    WARN_PROMPT_PROCESSOR_CERT_EXPIRED("WARNING:  {0} certificate ''{1}'' expired on {2} ({3} ago)."), 
    WARN_PROMPT_PROCESSOR_CERT_IS_SELF_SIGNED("WARNING:  The certificate is self-signed."), 
    WARN_PROMPT_PROCESSOR_CERT_NOT_YET_VALID("WARNING:  {0} certificate ''{1}'' will not be valid until {2} ({3} from now)."), 
    WARN_PROMPT_PROCESSOR_CHAIN_ISSUER_MISMATCH("WARNING:  The presented certificate chain has certificate ''{0}'' immediately following certificate ''{1}'', but the former certificate is not the issuer for the latter."), 
    WARN_PROMPT_PROCESSOR_CHAIN_NOT_COMPLETE("WARNING:  The presented certificate chain is not complete.  It ends with certificate ''{0}'', which is not self-signed."), 
    WARN_PROMPT_PROCESSOR_DATE_TIME("{0} at {1}"), 
    WARN_PROMPT_PROCESSOR_EKU_MISSING_CLIENT_AUTH("WARNING:  Client certificate ''{0}'' has an extended key usage extension that does not include the clientAuth usage.  This certificate is not intended to be used as a TLS client certificate."), 
    WARN_PROMPT_PROCESSOR_EKU_MISSING_SERVER_AUTH("WARNING:  Server certificate ''{0}'' has an extended key usage extension that does not include the serverAuth usage.  This certificate is not intended to be used as a TLS server certificate."), 
    WARN_PROMPT_PROCESSOR_KU_NO_KEY_CERT_SIGN("WARNING:  Issuer certificate ''{0}'' has a key usage extension that does not include the keyCertSign usage.  This indicates that the certificate should not be trusted as an issuer certificate."), 
    WARN_PROMPT_PROCESSOR_LABEL_CLIENT("Client"), 
    WARN_PROMPT_PROCESSOR_LABEL_ISSUER("Issuer"), 
    WARN_PROMPT_PROCESSOR_LABEL_SERVER("Server"), 
    WARN_PROMPT_PROCESSOR_MULTIPLE_ADDRESSES_NOT_MATCHED("WARNING:  Server certificate ''{0}'' appears to be intended for use in a server with one of the following addresses:  {1}.  None of these addresses matches the address used by the client."), 
    WARN_PROMPT_PROCESSOR_NO_BC_EXTENSION("WARNING:  Issuer certificate ''{0}'' does not have a basic constraints extension.  This certificate was likely not intended to be used as an issuer certificate."), 
    WARN_PROMPT_PROCESSOR_SINGLE_ADDRESS_NOT_MATCHED("WARNING:  Server certificate ''{0}'' appears to be intended for use in a server with address {1}, which does not match the address used by the client.");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<SSLMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<SSLMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private SSLMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = SSLMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (SSLMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = SSLMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                SSLMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (SSLMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = SSLMessages.MESSAGES.get(this);
        if (f == null) {
            if (SSLMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(SSLMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            SSLMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (SSLMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = SSLMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (SSLMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = SSLMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                SSLMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-ssl");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<SSLMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<SSLMessages, MessageFormat>(100);
    }
}
