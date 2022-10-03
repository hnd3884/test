package com.unboundid.ldap.listener;

import java.net.Socket;
import java.security.MessageDigest;
import javax.net.ssl.KeyManager;
import com.unboundid.util.ObjectPair;
import java.util.Iterator;
import java.net.InetAddress;
import javax.net.ssl.TrustManager;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import com.unboundid.util.ssl.TrustStoreTrustManager;
import com.unboundid.util.ssl.KeyStoreKeyManager;
import com.unboundid.util.ssl.cert.CertException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Formatter;
import java.util.logging.StreamHandler;
import com.unboundid.util.MinimalLogFormatter;
import java.util.EnumSet;
import com.unboundid.ldap.sdk.OperationType;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.DN;
import java.io.File;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.util.args.ArgumentException;
import java.util.Set;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import com.unboundid.util.CommandLineTool;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class InMemoryDirectoryServerTool extends CommandLineTool implements Serializable, LDAPListenerExceptionHandler
{
    private static final long serialVersionUID = 6484637038039050412L;
    private BooleanArgument accessLogToStandardOutArgument;
    private BooleanArgument dontStartArgument;
    private BooleanArgument generateSelfSignedCertificateArgument;
    private BooleanArgument ldapDebugLogToStandardOutArgument;
    private BooleanArgument useDefaultSchemaArgument;
    private BooleanArgument useSSLArgument;
    private BooleanArgument useStartTLSArgument;
    private DNArgument additionalBindDNArgument;
    private DNArgument baseDNArgument;
    private FileArgument accessLogFileArgument;
    private FileArgument codeLogFile;
    private FileArgument keyStorePathArgument;
    private FileArgument ldapDebugLogFileArgument;
    private FileArgument ldifFileArgument;
    private FileArgument trustStorePathArgument;
    private FileArgument useSchemaFileArgument;
    private InMemoryDirectoryServer directoryServer;
    private IntegerArgument maxChangeLogEntriesArgument;
    private IntegerArgument maxConcurrentConnectionsArgument;
    private IntegerArgument portArgument;
    private IntegerArgument sizeLimitArgument;
    private StringArgument additionalBindPasswordArgument;
    private StringArgument allowedOperationTypeArgument;
    private StringArgument authenticationRequiredOperationTypeArgument;
    private StringArgument defaultPasswordEncodingArgument;
    private StringArgument equalityIndexArgument;
    private StringArgument keyStorePasswordArgument;
    private StringArgument keyStoreTypeArgument;
    private StringArgument passwordAttributeArgument;
    private StringArgument trustStorePasswordArgument;
    private StringArgument trustStoreTypeArgument;
    private StringArgument vendorNameArgument;
    private StringArgument vendorVersionArgument;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final InMemoryDirectoryServerTool tool = new InMemoryDirectoryServerTool(outStream, errStream);
        return tool.runTool(args);
    }
    
    public InMemoryDirectoryServerTool(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.directoryServer = null;
        this.dontStartArgument = null;
        this.generateSelfSignedCertificateArgument = null;
        this.useDefaultSchemaArgument = null;
        this.useSSLArgument = null;
        this.useStartTLSArgument = null;
        this.additionalBindDNArgument = null;
        this.baseDNArgument = null;
        this.accessLogToStandardOutArgument = null;
        this.accessLogFileArgument = null;
        this.keyStorePathArgument = null;
        this.ldapDebugLogToStandardOutArgument = null;
        this.ldapDebugLogFileArgument = null;
        this.ldifFileArgument = null;
        this.trustStorePathArgument = null;
        this.useSchemaFileArgument = null;
        this.maxChangeLogEntriesArgument = null;
        this.maxConcurrentConnectionsArgument = null;
        this.portArgument = null;
        this.sizeLimitArgument = null;
        this.additionalBindPasswordArgument = null;
        this.allowedOperationTypeArgument = null;
        this.authenticationRequiredOperationTypeArgument = null;
        this.defaultPasswordEncodingArgument = null;
        this.equalityIndexArgument = null;
        this.keyStorePasswordArgument = null;
        this.keyStoreTypeArgument = null;
        this.passwordAttributeArgument = null;
        this.trustStorePasswordArgument = null;
        this.trustStoreTypeArgument = null;
        this.vendorNameArgument = null;
        this.vendorVersionArgument = null;
    }
    
    @Override
    public String getToolName() {
        return "in-memory-directory-server";
    }
    
    @Override
    public String getToolDescription() {
        return ListenerMessages.INFO_MEM_DS_TOOL_DESC.get(InMemoryDirectoryServer.class.getName());
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public void addToolArguments(final ArgumentParser parser) throws ArgumentException {
        (this.portArgument = new IntegerArgument('p', "port", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PORT.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_PORT.get(), 0, 65535)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        parser.addArgument(this.portArgument);
        (this.useSSLArgument = new BooleanArgument('Z', "useSSL", ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_USE_SSL.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.useSSLArgument.addLongIdentifier("use-ssl", true);
        parser.addArgument(this.useSSLArgument);
        (this.useStartTLSArgument = new BooleanArgument('q', "useStartTLS", ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_USE_START_TLS.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.useStartTLSArgument.addLongIdentifier("use-starttls", true);
        this.useStartTLSArgument.addLongIdentifier("use-start-tls", true);
        parser.addArgument(this.useStartTLSArgument);
        (this.keyStorePathArgument = new FileArgument('K', "keyStorePath", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PATH.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_KEY_STORE_PATH.get(), true, true, true, false)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.keyStorePathArgument.addLongIdentifier("key-store-path", true);
        parser.addArgument(this.keyStorePathArgument);
        (this.keyStorePasswordArgument = new StringArgument('W', "keyStorePassword", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PASSWORD.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_KEY_STORE_PW.get())).setSensitive(true);
        this.keyStorePasswordArgument.setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.keyStorePasswordArgument.addLongIdentifier("keyStorePIN", true);
        this.keyStorePasswordArgument.addLongIdentifier("key-store-password", true);
        this.keyStorePasswordArgument.addLongIdentifier("key-store-pin", true);
        parser.addArgument(this.keyStorePasswordArgument);
        (this.keyStoreTypeArgument = new StringArgument(null, "keyStoreType", false, 1, "{type}", ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_KEY_STORE_TYPE.get(), "JKS")).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.keyStoreTypeArgument.addLongIdentifier("keyStoreFormat", true);
        this.keyStoreTypeArgument.addLongIdentifier("key-store-type", true);
        this.keyStoreTypeArgument.addLongIdentifier("key-store-format", true);
        parser.addArgument(this.keyStoreTypeArgument);
        (this.generateSelfSignedCertificateArgument = new BooleanArgument(null, "generateSelfSignedCertificate", 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_SELF_SIGNED_CERT.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.generateSelfSignedCertificateArgument.addLongIdentifier("useSelfSignedCertificate", true);
        this.generateSelfSignedCertificateArgument.addLongIdentifier("selfSignedCertificate", true);
        this.generateSelfSignedCertificateArgument.addLongIdentifier("generate-self-signed-certificate", true);
        this.generateSelfSignedCertificateArgument.addLongIdentifier("use-self-signed-certificate", true);
        this.generateSelfSignedCertificateArgument.addLongIdentifier("self-signed-certificate", true);
        parser.addArgument(this.generateSelfSignedCertificateArgument);
        (this.trustStorePathArgument = new FileArgument('P', "trustStorePath", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PATH.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_TRUST_STORE_PATH.get(), true, true, true, false)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.trustStorePathArgument.addLongIdentifier("trust-store-path", true);
        parser.addArgument(this.trustStorePathArgument);
        (this.trustStorePasswordArgument = new StringArgument('T', "trustStorePassword", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PASSWORD.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_TRUST_STORE_PW.get())).setSensitive(true);
        this.trustStorePasswordArgument.setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.trustStorePasswordArgument.addLongIdentifier("trustStorePIN", true);
        this.trustStorePasswordArgument.addLongIdentifier("trust-store-password", true);
        this.trustStorePasswordArgument.addLongIdentifier("trust-store-pin", true);
        parser.addArgument(this.trustStorePasswordArgument);
        (this.trustStoreTypeArgument = new StringArgument(null, "trustStoreType", false, 1, "{type}", ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_TRUST_STORE_TYPE.get(), "JKS")).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.trustStoreTypeArgument.addLongIdentifier("trustStoreFormat", true);
        this.trustStoreTypeArgument.addLongIdentifier("trust-store-type", true);
        this.trustStoreTypeArgument.addLongIdentifier("trust-store-format", true);
        parser.addArgument(this.trustStoreTypeArgument);
        (this.maxConcurrentConnectionsArgument = new IntegerArgument(null, "maxConcurrentConnections", false, 1, null, ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_MAX_CONNECTIONS.get(), 1, Integer.MAX_VALUE, Integer.MAX_VALUE)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.maxConcurrentConnectionsArgument.addLongIdentifier("maximumConcurrentConnections", true);
        this.maxConcurrentConnectionsArgument.addLongIdentifier("maxConnections", true);
        this.maxConcurrentConnectionsArgument.addLongIdentifier("maximumConnections", true);
        this.maxConcurrentConnectionsArgument.addLongIdentifier("max-concurrent-connections", true);
        this.maxConcurrentConnectionsArgument.addLongIdentifier("maximum-concurrent-connections", true);
        this.maxConcurrentConnectionsArgument.addLongIdentifier("max-connections", true);
        this.maxConcurrentConnectionsArgument.addLongIdentifier("maximum-connections", true);
        parser.addArgument(this.maxConcurrentConnectionsArgument);
        (this.dontStartArgument = new BooleanArgument(null, "dontStart", ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_DONT_START.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_CONNECTIVITY.get());
        this.dontStartArgument.setHidden(true);
        this.dontStartArgument.addLongIdentifier("doNotStart", true);
        this.dontStartArgument.addLongIdentifier("dont-start", true);
        this.dontStartArgument.addLongIdentifier("do-not-start", true);
        parser.addArgument(this.dontStartArgument);
        (this.baseDNArgument = new DNArgument('b', "baseDN", true, 0, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_BASE_DN.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_BASE_DN.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.baseDNArgument.addLongIdentifier("base-dn", true);
        parser.addArgument(this.baseDNArgument);
        (this.ldifFileArgument = new FileArgument('l', "ldifFile", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PATH.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_LDIF_FILE.get(), true, true, true, false)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.ldifFileArgument.addLongIdentifier("ldif-file", true);
        parser.addArgument(this.ldifFileArgument);
        (this.additionalBindDNArgument = new DNArgument('D', "additionalBindDN", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_BIND_DN.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_ADDITIONAL_BIND_DN.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.additionalBindDNArgument.addLongIdentifier("additional-bind-dn", true);
        parser.addArgument(this.additionalBindDNArgument);
        (this.additionalBindPasswordArgument = new StringArgument('w', "additionalBindPassword", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PASSWORD.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_ADDITIONAL_BIND_PW.get())).setSensitive(true);
        this.additionalBindPasswordArgument.setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.additionalBindPasswordArgument.addLongIdentifier("additional-bind-password", true);
        parser.addArgument(this.additionalBindPasswordArgument);
        (this.useDefaultSchemaArgument = new BooleanArgument('s', "useDefaultSchema", ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_USE_DEFAULT_SCHEMA.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.useDefaultSchemaArgument.addLongIdentifier("use-default-schema", true);
        parser.addArgument(this.useDefaultSchemaArgument);
        (this.useSchemaFileArgument = new FileArgument('S', "useSchemaFile", false, 0, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PATH.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_USE_SCHEMA_FILE.get(), true, true, false, false)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.useSchemaFileArgument.addLongIdentifier("use-schema-file", true);
        parser.addArgument(this.useSchemaFileArgument);
        (this.equalityIndexArgument = new StringArgument('I', "equalityIndex", false, 0, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_ATTR.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_EQ_INDEX.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.equalityIndexArgument.addLongIdentifier("equality-index", true);
        parser.addArgument(this.equalityIndexArgument);
        (this.maxChangeLogEntriesArgument = new IntegerArgument('c', "maxChangeLogEntries", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_COUNT.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_MAX_CHANGELOG_ENTRIES.get(), 0, Integer.MAX_VALUE, 0)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.maxChangeLogEntriesArgument.addLongIdentifier("max-changelog-entries", true);
        this.maxChangeLogEntriesArgument.addLongIdentifier("max-change-log-entries", true);
        parser.addArgument(this.maxChangeLogEntriesArgument);
        (this.sizeLimitArgument = new IntegerArgument(null, "sizeLimit", false, 1, null, ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_SIZE_LIMIT.get(), 1, Integer.MAX_VALUE, Integer.MAX_VALUE)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.sizeLimitArgument.addLongIdentifier("searchSizeLimit", true);
        this.sizeLimitArgument.addLongIdentifier("size-limit", true);
        this.sizeLimitArgument.addLongIdentifier("search-size-limit", true);
        parser.addArgument(this.sizeLimitArgument);
        (this.passwordAttributeArgument = new StringArgument(null, "passwordAttribute", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_ATTR.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_PASSWORD_ATTRIBUTE.get(), "userPassword")).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.passwordAttributeArgument.addLongIdentifier("passwordAttributeType", true);
        this.passwordAttributeArgument.addLongIdentifier("password-attribute", true);
        this.passwordAttributeArgument.addLongIdentifier("password-attribute-type", true);
        parser.addArgument(this.passwordAttributeArgument);
        final Set<String> allowedSchemes = StaticUtils.setOf("md5", "smd5", "sha", "ssha", "sha256", "ssha256", "sha384", "ssha384", "sha512", "ssha512", "clear", "base64", "hex");
        (this.defaultPasswordEncodingArgument = new StringArgument(null, "defaultPasswordEncoding", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_SCHEME.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_DEFAULT_PASSWORD_ENCODING.get(), allowedSchemes)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.defaultPasswordEncodingArgument.addLongIdentifier("defaultPasswordEncodingScheme", true);
        this.defaultPasswordEncodingArgument.addLongIdentifier("defaultPasswordStorageScheme", true);
        this.defaultPasswordEncodingArgument.addLongIdentifier("defaultPasswordScheme", true);
        this.defaultPasswordEncodingArgument.addLongIdentifier("default-password-encoding", true);
        this.defaultPasswordEncodingArgument.addLongIdentifier("default-password-encoding-scheme", true);
        this.defaultPasswordEncodingArgument.addLongIdentifier("default-password-storage-scheme", true);
        this.defaultPasswordEncodingArgument.addLongIdentifier("default-password-scheme", true);
        parser.addArgument(this.defaultPasswordEncodingArgument);
        final Set<String> allowedOperationTypeAllowedValues = StaticUtils.setOf("add", "bind", "compare", "delete", "extended", "modify", "modify-dn", "search");
        (this.allowedOperationTypeArgument = new StringArgument(null, "allowedOperationType", false, 0, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_TYPE.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_ALLOWED_OP_TYPE.get(), allowedOperationTypeAllowedValues)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.allowedOperationTypeArgument.addLongIdentifier("allowed-operation-type", true);
        parser.addArgument(this.allowedOperationTypeArgument);
        final Set<String> authRequiredTypeAllowedValues = StaticUtils.setOf("add", "compare", "delete", "extended", "modify", "modify-dn", "search");
        (this.authenticationRequiredOperationTypeArgument = new StringArgument(null, "authenticationRequiredOperationType", false, 0, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_TYPE.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_AUTH_REQUIRED_OP_TYPE.get(), authRequiredTypeAllowedValues)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.authenticationRequiredOperationTypeArgument.addLongIdentifier("requiredAuthenticationOperationType", true);
        this.authenticationRequiredOperationTypeArgument.addLongIdentifier("requireAuthenticationOperationType", true);
        this.authenticationRequiredOperationTypeArgument.addLongIdentifier("authentication-required-operation-type", true);
        this.authenticationRequiredOperationTypeArgument.addLongIdentifier("required-authentication-operation-type", true);
        this.authenticationRequiredOperationTypeArgument.addLongIdentifier("require-authentication-operation-type", true);
        parser.addArgument(this.authenticationRequiredOperationTypeArgument);
        (this.vendorNameArgument = new StringArgument(null, "vendorName", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_VALUE.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_VENDOR_NAME.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.vendorNameArgument.addLongIdentifier("vendor-name", true);
        parser.addArgument(this.vendorNameArgument);
        (this.vendorVersionArgument = new StringArgument(null, "vendorVersion", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_VALUE.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_VENDOR_VERSION.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_DATA.get());
        this.vendorVersionArgument.addLongIdentifier("vendor-version", true);
        parser.addArgument(this.vendorVersionArgument);
        (this.accessLogToStandardOutArgument = new BooleanArgument('A', "accessLogToStandardOut", ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_ACCESS_LOG_TO_STDOUT.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_LOGGING.get());
        this.accessLogToStandardOutArgument.addLongIdentifier("access-log-to-standard-out", true);
        parser.addArgument(this.accessLogToStandardOutArgument);
        (this.accessLogFileArgument = new FileArgument('a', "accessLogFile", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PATH.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_ACCESS_LOG_FILE.get(), false, true, true, false)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_LOGGING.get());
        this.accessLogFileArgument.addLongIdentifier("access-log-format", true);
        parser.addArgument(this.accessLogFileArgument);
        (this.ldapDebugLogToStandardOutArgument = new BooleanArgument(null, "ldapDebugLogToStandardOut", ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_LDAP_DEBUG_LOG_TO_STDOUT.get())).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_LOGGING.get());
        this.ldapDebugLogToStandardOutArgument.addLongIdentifier("ldap-debug-log-to-standard-out", true);
        parser.addArgument(this.ldapDebugLogToStandardOutArgument);
        (this.ldapDebugLogFileArgument = new FileArgument('d', "ldapDebugLogFile", false, 1, ListenerMessages.INFO_MEM_DS_TOOL_ARG_PLACEHOLDER_PATH.get(), ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_LDAP_DEBUG_LOG_FILE.get(), false, true, true, false)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_LOGGING.get());
        this.ldapDebugLogFileArgument.addLongIdentifier("ldap-debug-log-file", true);
        parser.addArgument(this.ldapDebugLogFileArgument);
        (this.codeLogFile = new FileArgument('C', "codeLogFile", false, 1, "{path}", ListenerMessages.INFO_MEM_DS_TOOL_ARG_DESC_CODE_LOG_FILE.get(), false, true, true, false)).setArgumentGroupName(ListenerMessages.INFO_MEM_DS_TOOL_GROUP_LOGGING.get());
        this.codeLogFile.addLongIdentifier("code-log-file", true);
        parser.addArgument(this.codeLogFile);
        parser.addExclusiveArgumentSet(this.useDefaultSchemaArgument, this.useSchemaFileArgument, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useSSLArgument, this.useStartTLSArgument, new Argument[0]);
        parser.addExclusiveArgumentSet(this.keyStorePathArgument, this.generateSelfSignedCertificateArgument, new Argument[0]);
        parser.addExclusiveArgumentSet(this.accessLogToStandardOutArgument, this.accessLogFileArgument, new Argument[0]);
        parser.addExclusiveArgumentSet(this.ldapDebugLogToStandardOutArgument, this.ldapDebugLogFileArgument, new Argument[0]);
        parser.addDependentArgumentSet(this.additionalBindDNArgument, this.additionalBindPasswordArgument, new Argument[0]);
        parser.addDependentArgumentSet(this.additionalBindPasswordArgument, this.additionalBindDNArgument, new Argument[0]);
        parser.addDependentArgumentSet(this.useSSLArgument, this.keyStorePathArgument, this.generateSelfSignedCertificateArgument);
        parser.addDependentArgumentSet(this.keyStorePathArgument, this.keyStorePasswordArgument, new Argument[0]);
        parser.addDependentArgumentSet(this.keyStorePasswordArgument, this.keyStorePathArgument, new Argument[0]);
        parser.addDependentArgumentSet(this.keyStoreTypeArgument, this.keyStorePathArgument, new Argument[0]);
        parser.addDependentArgumentSet(this.useStartTLSArgument, this.keyStorePathArgument, this.generateSelfSignedCertificateArgument);
        parser.addDependentArgumentSet(this.keyStorePathArgument, this.useSSLArgument, this.useStartTLSArgument);
        parser.addDependentArgumentSet(this.generateSelfSignedCertificateArgument, this.useSSLArgument, this.useStartTLSArgument);
        parser.addDependentArgumentSet(this.trustStorePathArgument, this.useSSLArgument, this.useStartTLSArgument);
        parser.addDependentArgumentSet(this.trustStorePasswordArgument, this.trustStorePathArgument, new Argument[0]);
        parser.addDependentArgumentSet(this.trustStoreTypeArgument, this.trustStorePathArgument, new Argument[0]);
    }
    
    @Override
    public boolean supportsInteractiveMode() {
        return true;
    }
    
    @Override
    public boolean defaultsToInteractiveMode() {
        return true;
    }
    
    @Override
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    public ResultCode doToolProcessing() {
        InMemoryDirectoryServerConfig serverConfig;
        try {
            serverConfig = this.getConfig();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.err(ListenerMessages.ERR_MEM_DS_TOOL_ERROR_INITIALIZING_CONFIG.get(le.getMessage()));
            return le.getResultCode();
        }
        try {
            this.directoryServer = new InMemoryDirectoryServer(serverConfig);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.err(ListenerMessages.ERR_MEM_DS_TOOL_ERROR_CREATING_SERVER_INSTANCE.get(le.getMessage()));
            return le.getResultCode();
        }
        if (this.ldifFileArgument.isPresent()) {
            final File ldifFile = this.ldifFileArgument.getValue();
            try {
                final int numEntries = this.directoryServer.importFromLDIF(true, ldifFile.getAbsolutePath());
                this.out(ListenerMessages.INFO_MEM_DS_TOOL_ADDED_ENTRIES_FROM_LDIF.get(numEntries, ldifFile.getAbsolutePath()));
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                this.err(ListenerMessages.ERR_MEM_DS_TOOL_ERROR_POPULATING_SERVER_INSTANCE.get(ldifFile.getAbsolutePath(), le2.getMessage()));
                return le2.getResultCode();
            }
        }
        try {
            if (!this.dontStartArgument.isPresent()) {
                this.directoryServer.startListening();
                this.out(ListenerMessages.INFO_MEM_DS_TOOL_LISTENING.get(this.directoryServer.getListenPort()));
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.err(ListenerMessages.ERR_MEM_DS_TOOL_ERROR_STARTING_SERVER.get(StaticUtils.getExceptionMessage(e)));
            return ResultCode.LOCAL_ERROR;
        }
        return ResultCode.SUCCESS;
    }
    
    private InMemoryDirectoryServerConfig getConfig() throws LDAPException {
        final List<DN> dnList = this.baseDNArgument.getValues();
        final DN[] baseDNs = new DN[dnList.size()];
        dnList.toArray(baseDNs);
        final InMemoryDirectoryServerConfig serverConfig = new InMemoryDirectoryServerConfig(baseDNs);
        int listenPort = 0;
        if (this.portArgument.isPresent()) {
            listenPort = this.portArgument.getValue();
        }
        if (this.useDefaultSchemaArgument.isPresent()) {
            serverConfig.setSchema(Schema.getDefaultStandardSchema());
        }
        else if (this.useSchemaFileArgument.isPresent()) {
            final ArrayList<File> schemaFiles = new ArrayList<File>(10);
            for (final File f : this.useSchemaFileArgument.getValues()) {
                if (!f.exists()) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_NO_SUCH_SCHEMA_FILE.get(f.getAbsolutePath()));
                }
                if (f.isFile()) {
                    schemaFiles.add(f);
                }
                else {
                    for (final File subFile : f.listFiles()) {
                        if (subFile.isFile()) {
                            schemaFiles.add(subFile);
                        }
                    }
                }
            }
            try {
                serverConfig.setSchema(Schema.getSchema(schemaFiles));
            }
            catch (final Exception e) {
                Debug.debugException(e);
                final StringBuilder fileList = new StringBuilder();
                final Iterator<File> fileIterator = schemaFiles.iterator();
                while (fileIterator.hasNext()) {
                    fileList.append(fileIterator.next().getAbsolutePath());
                    if (fileIterator.hasNext()) {
                        fileList.append(", ");
                    }
                }
                throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_ERROR_READING_SCHEMA.get(fileList, StaticUtils.getExceptionMessage(e)), e);
            }
        }
        else {
            serverConfig.setSchema(null);
        }
        if (this.additionalBindDNArgument.isPresent()) {
            serverConfig.addAdditionalBindCredentials(this.additionalBindDNArgument.getValue().toString(), this.additionalBindPasswordArgument.getValue());
        }
        if (this.maxChangeLogEntriesArgument.isPresent()) {
            serverConfig.setMaxChangeLogEntries(this.maxChangeLogEntriesArgument.getValue());
        }
        if (this.maxConcurrentConnectionsArgument.isPresent()) {
            serverConfig.setMaxConnections(this.maxConcurrentConnectionsArgument.getValue());
        }
        if (this.sizeLimitArgument.isPresent()) {
            serverConfig.setMaxSizeLimit(this.sizeLimitArgument.getValue());
        }
        if (this.passwordAttributeArgument.isPresent()) {
            serverConfig.setPasswordAttributes(this.passwordAttributeArgument.getValues());
        }
        final LinkedHashMap<String, InMemoryPasswordEncoder> passwordEncoders = new LinkedHashMap<String, InMemoryPasswordEncoder>(10);
        addUnsaltedEncoder("MD5", "MD5", passwordEncoders);
        addUnsaltedEncoder("SHA", "SHA-1", passwordEncoders);
        addUnsaltedEncoder("SHA1", "SHA-1", passwordEncoders);
        addUnsaltedEncoder("SHA-1", "SHA-1", passwordEncoders);
        addUnsaltedEncoder("SHA256", "SHA-256", passwordEncoders);
        addUnsaltedEncoder("SHA-256", "SHA-256", passwordEncoders);
        addUnsaltedEncoder("SHA384", "SHA-384", passwordEncoders);
        addUnsaltedEncoder("SHA-384", "SHA-384", passwordEncoders);
        addUnsaltedEncoder("SHA512", "SHA-512", passwordEncoders);
        addUnsaltedEncoder("SHA-512", "SHA-512", passwordEncoders);
        addSaltedEncoder("SMD5", "MD5", passwordEncoders);
        addSaltedEncoder("SSHA", "SHA-1", passwordEncoders);
        addSaltedEncoder("SSHA1", "SHA-1", passwordEncoders);
        addSaltedEncoder("SSHA-1", "SHA-1", passwordEncoders);
        addSaltedEncoder("SSHA256", "SHA-256", passwordEncoders);
        addSaltedEncoder("SSHA-256", "SHA-256", passwordEncoders);
        addSaltedEncoder("SSHA384", "SHA-384", passwordEncoders);
        addSaltedEncoder("SSHA-384", "SHA-384", passwordEncoders);
        addSaltedEncoder("SSHA512", "SHA-512", passwordEncoders);
        addSaltedEncoder("SSHA-512", "SHA-512", passwordEncoders);
        addClearEncoder("CLEAR", null, passwordEncoders);
        addClearEncoder("BASE64", Base64PasswordEncoderOutputFormatter.getInstance(), passwordEncoders);
        addClearEncoder("HEX", HexPasswordEncoderOutputFormatter.getLowercaseInstance(), passwordEncoders);
        InMemoryPasswordEncoder primaryEncoder;
        if (this.defaultPasswordEncodingArgument.isPresent()) {
            primaryEncoder = passwordEncoders.remove(StaticUtils.toLowerCase(this.defaultPasswordEncodingArgument.getValue()));
            if (primaryEncoder == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_UNAVAILABLE_PW_ENCODING.get(this.defaultPasswordEncodingArgument.getValue(), String.valueOf(passwordEncoders.keySet())));
            }
        }
        else {
            primaryEncoder = null;
        }
        serverConfig.setPasswordEncoders(primaryEncoder, passwordEncoders.values());
        if (this.allowedOperationTypeArgument.isPresent()) {
            final EnumSet<OperationType> operationTypes = EnumSet.noneOf(OperationType.class);
            for (final String operationTypeName : this.allowedOperationTypeArgument.getValues()) {
                final OperationType name = OperationType.forName(operationTypeName);
                if (name == null) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_UNSUPPORTED_ALLOWED_OP_TYPE.get(name));
                }
                switch (name) {
                    case ADD:
                    case BIND:
                    case COMPARE:
                    case DELETE:
                    case EXTENDED:
                    case MODIFY:
                    case MODIFY_DN:
                    case SEARCH: {
                        operationTypes.add(name);
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_UNSUPPORTED_ALLOWED_OP_TYPE.get(name));
                    }
                }
            }
            serverConfig.setAllowedOperationTypes(operationTypes);
        }
        if (this.authenticationRequiredOperationTypeArgument.isPresent()) {
            final EnumSet<OperationType> operationTypes = EnumSet.noneOf(OperationType.class);
            for (final String operationTypeName : this.authenticationRequiredOperationTypeArgument.getValues()) {
                final OperationType name = OperationType.forName(operationTypeName);
                if (name == null) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_UNSUPPORTED_AUTH_REQUIRED_OP_TYPE.get(name));
                }
                switch (name) {
                    case ADD:
                    case COMPARE:
                    case DELETE:
                    case EXTENDED:
                    case MODIFY:
                    case MODIFY_DN:
                    case SEARCH: {
                        operationTypes.add(name);
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_UNSUPPORTED_AUTH_REQUIRED_OP_TYPE.get(name));
                    }
                }
            }
            serverConfig.setAuthenticationRequiredOperationTypes(operationTypes);
        }
        if (this.accessLogToStandardOutArgument.isPresent()) {
            final StreamHandler handler = new StreamHandler(System.out, new MinimalLogFormatter(null, false, false, true));
            StaticUtils.setLogHandlerLevel(handler, Level.INFO);
            serverConfig.setAccessLogHandler(handler);
        }
        else if (this.accessLogFileArgument.isPresent()) {
            final File logFile = this.accessLogFileArgument.getValue();
            try {
                final FileHandler handler2 = new FileHandler(logFile.getAbsolutePath(), true);
                StaticUtils.setLogHandlerLevel(handler2, Level.INFO);
                handler2.setFormatter(new MinimalLogFormatter(null, false, false, true));
                serverConfig.setAccessLogHandler(handler2);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_ERROR_CREATING_LOG_HANDLER.get(logFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), e2);
            }
        }
        if (this.ldapDebugLogToStandardOutArgument.isPresent()) {
            final StreamHandler handler = new StreamHandler(System.out, new MinimalLogFormatter(null, false, false, true));
            StaticUtils.setLogHandlerLevel(handler, Level.INFO);
            serverConfig.setLDAPDebugLogHandler(handler);
        }
        else if (this.ldapDebugLogFileArgument.isPresent()) {
            final File logFile = this.ldapDebugLogFileArgument.getValue();
            try {
                final FileHandler handler2 = new FileHandler(logFile.getAbsolutePath(), true);
                StaticUtils.setLogHandlerLevel(handler2, Level.INFO);
                handler2.setFormatter(new MinimalLogFormatter(null, false, false, true));
                serverConfig.setLDAPDebugLogHandler(handler2);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_ERROR_CREATING_LOG_HANDLER.get(logFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), e2);
            }
        }
        if (this.codeLogFile.isPresent()) {
            serverConfig.setCodeLogDetails(this.codeLogFile.getValue().getAbsolutePath(), true);
        }
        if (this.useSSLArgument.isPresent() || this.useStartTLSArgument.isPresent()) {
            File keyStorePath;
            char[] keyStorePIN;
            String keyStoreType;
            if (this.keyStorePathArgument.isPresent()) {
                keyStorePath = this.keyStorePathArgument.getValue();
                keyStorePIN = this.keyStorePasswordArgument.getValue().toCharArray();
                keyStoreType = this.keyStoreTypeArgument.getValue();
            }
            else {
                try {
                    keyStoreType = "JKS";
                    final ObjectPair<File, char[]> keyStoreInfo = SelfSignedCertificateGenerator.generateTemporarySelfSignedCertificate(this.getToolName(), keyStoreType);
                    keyStorePath = keyStoreInfo.getFirst();
                    keyStorePIN = keyStoreInfo.getSecond();
                }
                catch (final CertException e3) {
                    Debug.debugException(e3);
                    throw new LDAPException(ResultCode.LOCAL_ERROR, e3.getMessage(), e3);
                }
            }
            try {
                final KeyManager keyManager = new KeyStoreKeyManager(keyStorePath, keyStorePIN, keyStoreType, null);
                TrustManager trustManager;
                if (this.trustStorePathArgument.isPresent()) {
                    char[] password;
                    if (this.trustStorePasswordArgument.isPresent()) {
                        password = this.trustStorePasswordArgument.getValue().toCharArray();
                    }
                    else {
                        password = null;
                    }
                    trustManager = new TrustStoreTrustManager(this.trustStorePathArgument.getValue(), password, this.trustStoreTypeArgument.getValue(), true);
                }
                else {
                    trustManager = new TrustAllTrustManager();
                }
                final SSLUtil serverSSLUtil = new SSLUtil(keyManager, trustManager);
                if (this.useSSLArgument.isPresent()) {
                    final SSLUtil clientSSLUtil = new SSLUtil(new TrustAllTrustManager());
                    serverConfig.setListenerConfigs(InMemoryListenerConfig.createLDAPSConfig("LDAPS", null, listenPort, serverSSLUtil.createSSLServerSocketFactory(), clientSSLUtil.createSSLSocketFactory()));
                }
                else {
                    serverConfig.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("LDAP+StartTLS", null, listenPort, serverSSLUtil.createSSLSocketFactory()));
                }
            }
            catch (final Exception e4) {
                Debug.debugException(e4);
                throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_DS_TOOL_ERROR_INITIALIZING_SSL.get(StaticUtils.getExceptionMessage(e4)), e4);
            }
        }
        else {
            serverConfig.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("LDAP", listenPort));
        }
        if (this.vendorNameArgument.isPresent()) {
            serverConfig.setVendorName(this.vendorNameArgument.getValue());
        }
        if (this.vendorVersionArgument.isPresent()) {
            serverConfig.setVendorVersion(this.vendorVersionArgument.getValue());
        }
        if (this.equalityIndexArgument.isPresent()) {
            serverConfig.setEqualityIndexAttributes(this.equalityIndexArgument.getValues());
        }
        return serverConfig;
    }
    
    private static void addUnsaltedEncoder(final String schemeName, final String digestAlgorithm, final Map<String, InMemoryPasswordEncoder> encoderMap) {
        try {
            final UnsaltedMessageDigestInMemoryPasswordEncoder encoder = new UnsaltedMessageDigestInMemoryPasswordEncoder('{' + schemeName + '}', Base64PasswordEncoderOutputFormatter.getInstance(), MessageDigest.getInstance(digestAlgorithm));
            encoderMap.put(StaticUtils.toLowerCase(schemeName), encoder);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    private static void addSaltedEncoder(final String schemeName, final String digestAlgorithm, final Map<String, InMemoryPasswordEncoder> encoderMap) {
        try {
            final SaltedMessageDigestInMemoryPasswordEncoder encoder = new SaltedMessageDigestInMemoryPasswordEncoder('{' + schemeName + '}', Base64PasswordEncoderOutputFormatter.getInstance(), MessageDigest.getInstance(digestAlgorithm), 8, true, true);
            encoderMap.put(StaticUtils.toLowerCase(schemeName), encoder);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    private static void addClearEncoder(final String schemeName, final PasswordEncoderOutputFormatter outputFormatter, final Map<String, InMemoryPasswordEncoder> encoderMap) {
        final ClearInMemoryPasswordEncoder encoder = new ClearInMemoryPasswordEncoder('{' + schemeName + '}', outputFormatter);
        encoderMap.put(StaticUtils.toLowerCase(schemeName), encoder);
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleUsages = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        final String[] example1Args = { "--baseDN", "dc=example,dc=com" };
        exampleUsages.put(example1Args, ListenerMessages.INFO_MEM_DS_TOOL_EXAMPLE_1.get());
        final String[] example2Args = { "--baseDN", "dc=example,dc=com", "--port", "1389", "--ldifFile", "test.ldif", "--accessLogFile", "access.log", "--useDefaultSchema" };
        exampleUsages.put(example2Args, ListenerMessages.INFO_MEM_DS_TOOL_EXAMPLE_2.get());
        return exampleUsages;
    }
    
    public InMemoryDirectoryServer getDirectoryServer() {
        return this.directoryServer;
    }
    
    @Override
    public void connectionCreationFailure(final Socket socket, final Throwable cause) {
        this.err(ListenerMessages.ERR_MEM_DS_TOOL_ERROR_ACCEPTING_CONNECTION.get(StaticUtils.getExceptionMessage(cause)));
    }
    
    @Override
    public void connectionTerminated(final LDAPListenerClientConnection connection, final LDAPException cause) {
        this.err(ListenerMessages.ERR_MEM_DS_TOOL_CONNECTION_TERMINATED_BY_EXCEPTION.get(StaticUtils.getExceptionMessage(cause)));
    }
}
