package com.unboundid.util;

import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.EXTERNALBindRequest;
import javax.net.ssl.KeyManager;
import java.io.PrintStream;
import java.io.InputStream;
import com.unboundid.util.ssl.PromptTrustManager;
import com.unboundid.util.ssl.JVMDefaultTrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import com.unboundid.util.ssl.TrustStoreTrustManager;
import com.unboundid.util.ssl.TrustAllTrustManager;
import com.unboundid.util.ssl.KeyStoreKeyManager;
import javax.net.SocketFactory;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.ldap.sdk.RoundRobinServerSet;
import com.unboundid.ldap.sdk.SingleServerSet;
import java.util.Collection;
import com.unboundid.ldap.sdk.AggregatePostConnectProcessor;
import com.unboundid.ldap.sdk.StartTLSPostConnectProcessor;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPConnectionPoolHealthCheck;
import com.unboundid.ldap.sdk.PostConnectProcessor;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.io.OutputStream;
import com.unboundid.util.ssl.AggregateTrustManager;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLSocketFactory;
import com.unboundid.ldap.sdk.ServerSet;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.BooleanArgument;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class LDAPCommandLineTool extends CommandLineTool
{
    private BooleanArgument helpSASL;
    private BooleanArgument enableSSLDebugging;
    private BooleanArgument promptForBindPassword;
    private BooleanArgument promptForKeyStorePassword;
    private BooleanArgument promptForTrustStorePassword;
    private BooleanArgument trustAll;
    private BooleanArgument useSASLExternal;
    private BooleanArgument useSSL;
    private BooleanArgument useStartTLS;
    private DNArgument bindDN;
    private FileArgument bindPasswordFile;
    private FileArgument keyStorePasswordFile;
    private FileArgument trustStorePasswordFile;
    private IntegerArgument port;
    private StringArgument bindPassword;
    private StringArgument certificateNickname;
    private StringArgument host;
    private StringArgument keyStoreFormat;
    private StringArgument keyStorePath;
    private StringArgument keyStorePassword;
    private StringArgument saslOption;
    private StringArgument trustStoreFormat;
    private StringArgument trustStorePath;
    private StringArgument trustStorePassword;
    private BindRequest bindRequest;
    private ServerSet serverSet;
    private SSLSocketFactory startTLSSocketFactory;
    private final AtomicReference<AggregateTrustManager> promptTrustManager;
    
    public LDAPCommandLineTool(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.helpSASL = null;
        this.enableSSLDebugging = null;
        this.promptForBindPassword = null;
        this.promptForKeyStorePassword = null;
        this.promptForTrustStorePassword = null;
        this.trustAll = null;
        this.useSASLExternal = null;
        this.useSSL = null;
        this.useStartTLS = null;
        this.bindDN = null;
        this.bindPasswordFile = null;
        this.keyStorePasswordFile = null;
        this.trustStorePasswordFile = null;
        this.port = null;
        this.bindPassword = null;
        this.certificateNickname = null;
        this.host = null;
        this.keyStoreFormat = null;
        this.keyStorePath = null;
        this.keyStorePassword = null;
        this.saslOption = null;
        this.trustStoreFormat = null;
        this.trustStorePath = null;
        this.trustStorePassword = null;
        this.bindRequest = null;
        this.serverSet = null;
        this.startTLSSocketFactory = null;
        this.promptTrustManager = new AtomicReference<AggregateTrustManager>();
    }
    
    static Set<String> getLongLDAPArgumentIdentifiers(final LDAPCommandLineTool tool) {
        final LinkedHashSet<String> ids = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(21));
        ids.add("hostname");
        ids.add("port");
        if (tool.supportsAuthentication()) {
            ids.add("bindDN");
            ids.add("bindPassword");
            ids.add("bindPasswordFile");
            ids.add("promptForBindPassword");
        }
        ids.add("useSSL");
        ids.add("useStartTLS");
        ids.add("trustAll");
        ids.add("keyStorePath");
        ids.add("keyStorePassword");
        ids.add("keyStorePasswordFile");
        ids.add("promptForKeyStorePassword");
        ids.add("keyStoreFormat");
        ids.add("trustStorePath");
        ids.add("trustStorePassword");
        ids.add("trustStorePasswordFile");
        ids.add("promptForTrustStorePassword");
        ids.add("trustStoreFormat");
        ids.add("certNickname");
        if (tool.supportsAuthentication()) {
            ids.add("saslOption");
            ids.add("useSASLExternal");
            ids.add("helpSASL");
        }
        return Collections.unmodifiableSet((Set<? extends String>)ids);
    }
    
    protected Set<Character> getSuppressedShortIdentifiers() {
        return Collections.emptySet();
    }
    
    private Character getShortIdentifierIfNotSuppressed(final Character id) {
        if (this.getSuppressedShortIdentifiers().contains(id)) {
            return null;
        }
        return id;
    }
    
    @Override
    public final void addToolArguments(final ArgumentParser parser) throws ArgumentException {
        final boolean supportsAuthentication = this.supportsAuthentication();
        String argumentGroup;
        if (supportsAuthentication) {
            argumentGroup = UtilityMessages.INFO_LDAP_TOOL_ARG_GROUP_CONNECT_AND_AUTH.get();
        }
        else {
            argumentGroup = UtilityMessages.INFO_LDAP_TOOL_ARG_GROUP_CONNECT.get();
        }
        (this.host = new StringArgument(this.getShortIdentifierIfNotSuppressed('h'), "hostname", true, this.supportsMultipleServers() ? 0 : 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_HOST.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_HOST.get(), "localhost")).setArgumentGroupName(argumentGroup);
        parser.addArgument(this.host);
        (this.port = new IntegerArgument(this.getShortIdentifierIfNotSuppressed('p'), "port", true, this.supportsMultipleServers() ? 0 : 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PORT.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_PORT.get(), 1, 65535, 389)).setArgumentGroupName(argumentGroup);
        parser.addArgument(this.port);
        if (supportsAuthentication) {
            (this.bindDN = new DNArgument(this.getShortIdentifierIfNotSuppressed('D'), "bindDN", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_DN.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_BIND_DN.get())).setArgumentGroupName(argumentGroup);
            if (this.includeAlternateLongIdentifiers()) {
                this.bindDN.addLongIdentifier("bind-dn", true);
            }
            parser.addArgument(this.bindDN);
            (this.bindPassword = new StringArgument(this.getShortIdentifierIfNotSuppressed('w'), "bindPassword", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PASSWORD.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_BIND_PW.get())).setSensitive(true);
            this.bindPassword.setArgumentGroupName(argumentGroup);
            if (this.includeAlternateLongIdentifiers()) {
                this.bindPassword.addLongIdentifier("bind-password", true);
            }
            parser.addArgument(this.bindPassword);
            (this.bindPasswordFile = new FileArgument(this.getShortIdentifierIfNotSuppressed('j'), "bindPasswordFile", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_BIND_PW_FILE.get(), true, true, true, false)).setArgumentGroupName(argumentGroup);
            if (this.includeAlternateLongIdentifiers()) {
                this.bindPasswordFile.addLongIdentifier("bind-password-file", true);
            }
            parser.addArgument(this.bindPasswordFile);
            (this.promptForBindPassword = new BooleanArgument(null, "promptForBindPassword", 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_BIND_PW_PROMPT.get())).setArgumentGroupName(argumentGroup);
            if (this.includeAlternateLongIdentifiers()) {
                this.promptForBindPassword.addLongIdentifier("prompt-for-bind-password", true);
            }
            parser.addArgument(this.promptForBindPassword);
        }
        (this.useSSL = new BooleanArgument(this.getShortIdentifierIfNotSuppressed('Z'), "useSSL", 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_USE_SSL.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.useSSL.addLongIdentifier("use-ssl", true);
        }
        parser.addArgument(this.useSSL);
        (this.useStartTLS = new BooleanArgument(this.getShortIdentifierIfNotSuppressed('q'), "useStartTLS", 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_USE_START_TLS.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.useStartTLS.addLongIdentifier("use-starttls", true);
            this.useStartTLS.addLongIdentifier("use-start-tls", true);
        }
        parser.addArgument(this.useStartTLS);
        (this.trustAll = new BooleanArgument(this.getShortIdentifierIfNotSuppressed('X'), "trustAll", 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_ALL.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.trustAll.addLongIdentifier("trustAllCertificates", true);
            this.trustAll.addLongIdentifier("trust-all", true);
            this.trustAll.addLongIdentifier("trust-all-certificates", true);
        }
        parser.addArgument(this.trustAll);
        (this.keyStorePath = new StringArgument(this.getShortIdentifierIfNotSuppressed('K'), "keyStorePath", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PATH.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.keyStorePath.addLongIdentifier("key-store-path", true);
        }
        parser.addArgument(this.keyStorePath);
        (this.keyStorePassword = new StringArgument(this.getShortIdentifierIfNotSuppressed('W'), "keyStorePassword", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PASSWORD.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PASSWORD.get())).setSensitive(true);
        this.keyStorePassword.setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.keyStorePassword.addLongIdentifier("keyStorePIN", true);
            this.keyStorePassword.addLongIdentifier("key-store-password", true);
            this.keyStorePassword.addLongIdentifier("key-store-pin", true);
        }
        parser.addArgument(this.keyStorePassword);
        (this.keyStorePasswordFile = new FileArgument(this.getShortIdentifierIfNotSuppressed('u'), "keyStorePasswordFile", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PASSWORD_FILE.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.keyStorePasswordFile.addLongIdentifier("keyStorePINFile", true);
            this.keyStorePasswordFile.addLongIdentifier("key-store-password-file", true);
            this.keyStorePasswordFile.addLongIdentifier("key-store-pin-file", true);
        }
        parser.addArgument(this.keyStorePasswordFile);
        (this.promptForKeyStorePassword = new BooleanArgument(null, "promptForKeyStorePassword", 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PASSWORD_PROMPT.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.promptForKeyStorePassword.addLongIdentifier("promptForKeyStorePIN", true);
            this.promptForKeyStorePassword.addLongIdentifier("prompt-for-key-store-password", true);
            this.promptForKeyStorePassword.addLongIdentifier("prompt-for-key-store-pin", true);
        }
        parser.addArgument(this.promptForKeyStorePassword);
        (this.keyStoreFormat = new StringArgument(null, "keyStoreFormat", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_FORMAT.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_FORMAT.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.keyStoreFormat.addLongIdentifier("keyStoreType", true);
            this.keyStoreFormat.addLongIdentifier("key-store-format", true);
            this.keyStoreFormat.addLongIdentifier("key-store-type", true);
        }
        parser.addArgument(this.keyStoreFormat);
        (this.trustStorePath = new StringArgument(this.getShortIdentifierIfNotSuppressed('P'), "trustStorePath", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PATH.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.trustStorePath.addLongIdentifier("trust-store-path", true);
        }
        parser.addArgument(this.trustStorePath);
        (this.trustStorePassword = new StringArgument(this.getShortIdentifierIfNotSuppressed('T'), "trustStorePassword", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PASSWORD.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PASSWORD.get())).setSensitive(true);
        this.trustStorePassword.setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.trustStorePassword.addLongIdentifier("trustStorePIN", true);
            this.trustStorePassword.addLongIdentifier("trust-store-password", true);
            this.trustStorePassword.addLongIdentifier("trust-store-pin", true);
        }
        parser.addArgument(this.trustStorePassword);
        (this.trustStorePasswordFile = new FileArgument(this.getShortIdentifierIfNotSuppressed('U'), "trustStorePasswordFile", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PASSWORD_FILE.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.trustStorePasswordFile.addLongIdentifier("trustStorePINFile", true);
            this.trustStorePasswordFile.addLongIdentifier("trust-store-password-file", true);
            this.trustStorePasswordFile.addLongIdentifier("trust-store-pin-file", true);
        }
        parser.addArgument(this.trustStorePasswordFile);
        (this.promptForTrustStorePassword = new BooleanArgument(null, "promptForTrustStorePassword", 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PASSWORD_PROMPT.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.promptForTrustStorePassword.addLongIdentifier("promptForTrustStorePIN", true);
            this.promptForTrustStorePassword.addLongIdentifier("prompt-for-trust-store-password", true);
            this.promptForTrustStorePassword.addLongIdentifier("prompt-for-trust-store-pin", true);
        }
        parser.addArgument(this.promptForTrustStorePassword);
        (this.trustStoreFormat = new StringArgument(null, "trustStoreFormat", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_FORMAT.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_FORMAT.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.trustStoreFormat.addLongIdentifier("trustStoreType", true);
            this.trustStoreFormat.addLongIdentifier("trust-store-format", true);
            this.trustStoreFormat.addLongIdentifier("trust-store-type", true);
        }
        parser.addArgument(this.trustStoreFormat);
        (this.certificateNickname = new StringArgument(this.getShortIdentifierIfNotSuppressed('N'), "certNickname", false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_CERT_NICKNAME.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_CERT_NICKNAME.get())).setArgumentGroupName(argumentGroup);
        if (this.includeAlternateLongIdentifiers()) {
            this.certificateNickname.addLongIdentifier("certificateNickname", true);
            this.certificateNickname.addLongIdentifier("cert-nickname", true);
            this.certificateNickname.addLongIdentifier("certificate-nickname", true);
        }
        parser.addArgument(this.certificateNickname);
        if (this.supportsSSLDebugging()) {
            (this.enableSSLDebugging = new BooleanArgument(null, "enableSSLDebugging", 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_ENABLE_SSL_DEBUGGING.get())).setArgumentGroupName(argumentGroup);
            if (this.includeAlternateLongIdentifiers()) {
                this.enableSSLDebugging.addLongIdentifier("enableTLSDebugging", true);
                this.enableSSLDebugging.addLongIdentifier("enableStartTLSDebugging", true);
                this.enableSSLDebugging.addLongIdentifier("enable-ssl-debugging", true);
                this.enableSSLDebugging.addLongIdentifier("enable-tls-debugging", true);
                this.enableSSLDebugging.addLongIdentifier("enable-starttls-debugging", true);
                this.enableSSLDebugging.addLongIdentifier("enable-start-tls-debugging", true);
            }
            parser.addArgument(this.enableSSLDebugging);
            this.addEnableSSLDebuggingArgument(this.enableSSLDebugging);
        }
        if (supportsAuthentication) {
            (this.saslOption = new StringArgument(this.getShortIdentifierIfNotSuppressed('o'), "saslOption", false, 0, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_SASL_OPTION.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_SASL_OPTION.get())).setArgumentGroupName(argumentGroup);
            if (this.includeAlternateLongIdentifiers()) {
                this.saslOption.addLongIdentifier("sasl-option", true);
            }
            parser.addArgument(this.saslOption);
            (this.useSASLExternal = new BooleanArgument(null, "useSASLExternal", 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_USE_SASL_EXTERNAL.get())).setArgumentGroupName(argumentGroup);
            if (this.includeAlternateLongIdentifiers()) {
                this.useSASLExternal.addLongIdentifier("use-sasl-external", true);
            }
            parser.addArgument(this.useSASLExternal);
            if (this.supportsSASLHelp()) {
                (this.helpSASL = new BooleanArgument(null, "helpSASL", UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_HELP_SASL.get())).setArgumentGroupName(argumentGroup);
                if (this.includeAlternateLongIdentifiers()) {
                    this.helpSASL.addLongIdentifier("help-sasl", true);
                }
                this.helpSASL.setUsageArgument(true);
                parser.addArgument(this.helpSASL);
                this.setHelpSASLArgument(this.helpSASL);
            }
        }
        parser.addExclusiveArgumentSet(this.useSSL, this.useStartTLS, new Argument[0]);
        parser.addExclusiveArgumentSet(this.keyStorePassword, this.keyStorePasswordFile, this.promptForKeyStorePassword);
        parser.addExclusiveArgumentSet(this.trustStorePassword, this.trustStorePasswordFile, this.promptForTrustStorePassword);
        parser.addExclusiveArgumentSet(this.trustAll, this.trustStorePath, new Argument[0]);
        parser.addDependentArgumentSet(this.keyStorePassword, this.keyStorePath, new Argument[0]);
        parser.addDependentArgumentSet(this.keyStorePasswordFile, this.keyStorePath, new Argument[0]);
        parser.addDependentArgumentSet(this.promptForKeyStorePassword, this.keyStorePath, new Argument[0]);
        parser.addDependentArgumentSet(this.trustStorePassword, this.trustStorePath, new Argument[0]);
        parser.addDependentArgumentSet(this.trustStorePasswordFile, this.trustStorePath, new Argument[0]);
        parser.addDependentArgumentSet(this.promptForTrustStorePassword, this.trustStorePath, new Argument[0]);
        parser.addDependentArgumentSet(this.keyStorePath, this.useSSL, this.useStartTLS);
        parser.addDependentArgumentSet(this.trustStorePath, this.useSSL, this.useStartTLS);
        parser.addDependentArgumentSet(this.trustAll, this.useSSL, this.useStartTLS);
        if (supportsAuthentication) {
            if (!this.defaultToPromptForBindPassword()) {
                parser.addDependentArgumentSet(this.bindDN, this.bindPassword, this.bindPasswordFile, this.promptForBindPassword);
            }
            parser.addExclusiveArgumentSet(this.bindDN, this.saslOption, this.useSASLExternal);
            parser.addExclusiveArgumentSet(this.bindPassword, this.bindPasswordFile, this.promptForBindPassword);
            parser.addDependentArgumentSet(this.bindPassword, this.bindDN, this.saslOption);
            parser.addDependentArgumentSet(this.bindPasswordFile, this.bindDN, this.saslOption);
            parser.addDependentArgumentSet(this.promptForBindPassword, this.bindDN, this.saslOption);
        }
        this.addNonLDAPArguments(parser);
    }
    
    public abstract void addNonLDAPArguments(final ArgumentParser p0) throws ArgumentException;
    
    @Override
    public final void doExtendedArgumentValidation() throws ArgumentException {
        if ((this.host.getValues().size() > 1 || this.port.getValues().size() > 1) && this.host.getValues().size() != this.port.getValues().size()) {
            throw new ArgumentException(UtilityMessages.ERR_LDAP_TOOL_HOST_PORT_COUNT_MISMATCH.get(this.host.getLongIdentifier(), this.port.getLongIdentifier()));
        }
        this.doExtendedNonLDAPArgumentValidation();
    }
    
    protected boolean supportsAuthentication() {
        return true;
    }
    
    protected boolean defaultToPromptForBindPassword() {
        return false;
    }
    
    protected boolean supportsSASLHelp() {
        return true;
    }
    
    protected boolean includeAlternateLongIdentifiers() {
        return false;
    }
    
    protected List<Control> getBindControls() {
        return null;
    }
    
    protected boolean supportsMultipleServers() {
        return false;
    }
    
    protected boolean supportsSSLDebugging() {
        return false;
    }
    
    public void doExtendedNonLDAPArgumentValidation() throws ArgumentException {
    }
    
    public LDAPConnectionOptions getConnectionOptions() {
        return new LDAPConnectionOptions();
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public final LDAPConnection getConnection() throws LDAPException {
        final LDAPConnection connection = this.getUnauthenticatedConnection();
        try {
            if (this.bindRequest != null) {
                connection.bind(this.bindRequest);
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            connection.close();
            throw le;
        }
        return connection;
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public final LDAPConnection getUnauthenticatedConnection() throws LDAPException {
        if (this.serverSet == null) {
            this.serverSet = this.createServerSet();
            this.bindRequest = this.createBindRequest();
        }
        final LDAPConnection connection = this.serverSet.getConnection();
        if (this.useStartTLS.isPresent()) {
            try {
                final ExtendedResult extendedResult = connection.processExtendedOperation(new StartTLSExtendedRequest(this.startTLSSocketFactory));
                if (!extendedResult.getResultCode().equals(ResultCode.SUCCESS)) {
                    throw new LDAPException(extendedResult.getResultCode(), UtilityMessages.ERR_LDAP_TOOL_START_TLS_FAILED.get(extendedResult.getDiagnosticMessage()));
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                connection.close();
                throw le;
            }
        }
        return connection;
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public final LDAPConnectionPool getConnectionPool(final int initialConnections, final int maxConnections) throws LDAPException {
        return this.getConnectionPool(initialConnections, maxConnections, 1, null, null, true, null);
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public final LDAPConnectionPool getConnectionPool(final int initialConnections, final int maxConnections, final int initialConnectThreads, final PostConnectProcessor beforeStartTLSProcessor, final PostConnectProcessor afterStartTLSProcessor, final boolean throwOnConnectFailure, final LDAPConnectionPoolHealthCheck healthCheck) throws LDAPException {
        if (this.serverSet == null) {
            this.serverSet = this.createServerSet();
            this.bindRequest = this.createBindRequest();
        }
        final ArrayList<PostConnectProcessor> pcpList = new ArrayList<PostConnectProcessor>(3);
        if (beforeStartTLSProcessor != null) {
            pcpList.add(beforeStartTLSProcessor);
        }
        if (this.useStartTLS.isPresent()) {
            pcpList.add(new StartTLSPostConnectProcessor(this.startTLSSocketFactory));
        }
        if (afterStartTLSProcessor != null) {
            pcpList.add(afterStartTLSProcessor);
        }
        PostConnectProcessor postConnectProcessor = null;
        switch (pcpList.size()) {
            case 0: {
                postConnectProcessor = null;
                break;
            }
            case 1: {
                postConnectProcessor = pcpList.get(0);
                break;
            }
            default: {
                postConnectProcessor = new AggregatePostConnectProcessor(pcpList);
                break;
            }
        }
        return new LDAPConnectionPool(this.serverSet, this.bindRequest, initialConnections, maxConnections, initialConnectThreads, postConnectProcessor, throwOnConnectFailure, healthCheck);
    }
    
    public ServerSet createServerSet() throws LDAPException {
        final SSLUtil sslUtil = this.createSSLUtil();
        SocketFactory socketFactory = null;
        Label_0114: {
            if (this.useSSL.isPresent()) {
                try {
                    socketFactory = sslUtil.createSSLSocketFactory();
                    break Label_0114;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_CREATE_SSL_SOCKET_FACTORY.get(StaticUtils.getExceptionMessage(e)), e);
                }
            }
            if (this.useStartTLS.isPresent()) {
                try {
                    this.startTLSSocketFactory = sslUtil.createSSLSocketFactory();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_CREATE_SSL_SOCKET_FACTORY.get(StaticUtils.getExceptionMessage(e)), e);
                }
            }
        }
        if (this.host.getValues().size() == 1) {
            return new SingleServerSet(this.host.getValue(), this.port.getValue(), socketFactory, this.getConnectionOptions());
        }
        final List<String> hostList = this.host.getValues();
        final List<Integer> portList = this.port.getValues();
        final String[] hosts = new String[hostList.size()];
        final int[] ports = new int[hosts.length];
        for (int i = 0; i < hosts.length; ++i) {
            hosts[i] = hostList.get(i);
            ports[i] = portList.get(i);
        }
        return new RoundRobinServerSet(hosts, ports, socketFactory, this.getConnectionOptions());
    }
    
    public SSLUtil createSSLUtil() throws LDAPException {
        return this.createSSLUtil(false);
    }
    
    public SSLUtil createSSLUtil(final boolean force) throws LDAPException {
        if (force || this.useSSL.isPresent() || this.useStartTLS.isPresent()) {
            KeyManager keyManager = null;
            if (this.keyStorePath.isPresent()) {
                char[] pw = null;
                Label_0168: {
                    if (this.keyStorePassword.isPresent()) {
                        pw = this.keyStorePassword.getValue().toCharArray();
                    }
                    else {
                        if (this.keyStorePasswordFile.isPresent()) {
                            try {
                                pw = this.getPasswordFileReader().readPassword(this.keyStorePasswordFile.getValue());
                                break Label_0168;
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_READ_KEY_STORE_PASSWORD.get(StaticUtils.getExceptionMessage(e)), e);
                            }
                        }
                        if (this.promptForKeyStorePassword.isPresent()) {
                            this.getOut().print(UtilityMessages.INFO_LDAP_TOOL_ENTER_KEY_STORE_PASSWORD.get());
                            pw = StaticUtils.toUTF8String(PasswordReader.readPassword()).toCharArray();
                            this.getOut().println();
                        }
                    }
                    try {
                        keyManager = new KeyStoreKeyManager(this.keyStorePath.getValue(), pw, this.keyStoreFormat.getValue(), this.certificateNickname.getValue());
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_CREATE_KEY_MANAGER.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                }
            }
            TrustManager tm;
            if (this.trustAll.isPresent()) {
                tm = new TrustAllTrustManager(false);
            }
            else if (this.trustStorePath.isPresent()) {
                char[] pw2 = null;
                Label_0407: {
                    if (this.trustStorePassword.isPresent()) {
                        pw2 = this.trustStorePassword.getValue().toCharArray();
                    }
                    else {
                        if (this.trustStorePasswordFile.isPresent()) {
                            try {
                                pw2 = this.getPasswordFileReader().readPassword(this.trustStorePasswordFile.getValue());
                                break Label_0407;
                            }
                            catch (final Exception e2) {
                                Debug.debugException(e2);
                                throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_READ_TRUST_STORE_PASSWORD.get(StaticUtils.getExceptionMessage(e2)), e2);
                            }
                        }
                        if (this.promptForTrustStorePassword.isPresent()) {
                            this.getOut().print(UtilityMessages.INFO_LDAP_TOOL_ENTER_TRUST_STORE_PASSWORD.get());
                            pw2 = StaticUtils.toUTF8String(PasswordReader.readPassword()).toCharArray();
                            this.getOut().println();
                        }
                    }
                }
                tm = new TrustStoreTrustManager(this.trustStorePath.getValue(), pw2, this.trustStoreFormat.getValue(), true);
            }
            else if (this.promptTrustManager.get() != null) {
                tm = this.promptTrustManager.get();
            }
            else {
                final ArrayList<String> expectedAddresses = new ArrayList<String>(5);
                if (this.useSSL.isPresent() || this.useStartTLS.isPresent()) {
                    expectedAddresses.addAll(this.host.getValues());
                }
                final AggregateTrustManager atm = new AggregateTrustManager(false, new X509TrustManager[] { JVMDefaultTrustManager.getInstance(), new PromptTrustManager(null, true, expectedAddresses, null, null) });
                if (this.promptTrustManager.compareAndSet(null, atm)) {
                    tm = atm;
                }
                else {
                    tm = this.promptTrustManager.get();
                }
            }
            return new SSLUtil(keyManager, tm);
        }
        return null;
    }
    
    public BindRequest createBindRequest() throws LDAPException {
        if (!this.supportsAuthentication()) {
            return null;
        }
        final List<Control> bindControlList = this.getBindControls();
        Control[] bindControls;
        if (bindControlList == null || bindControlList.isEmpty()) {
            bindControls = StaticUtils.NO_CONTROLS;
        }
        else {
            bindControls = new Control[bindControlList.size()];
            bindControlList.toArray(bindControls);
        }
        byte[] pw = null;
        Label_0195: {
            if (this.bindPassword.isPresent()) {
                pw = StaticUtils.getBytes(this.bindPassword.getValue());
            }
            else {
                if (this.bindPasswordFile.isPresent()) {
                    try {
                        final char[] pwChars = this.getPasswordFileReader().readPassword(this.bindPasswordFile.getValue());
                        pw = StaticUtils.getBytes(new String(pwChars));
                        break Label_0195;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_READ_BIND_PASSWORD.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                }
                if (this.promptForBindPassword.isPresent()) {
                    this.getOriginalOut().print(UtilityMessages.INFO_LDAP_TOOL_ENTER_BIND_PASSWORD.get());
                    pw = PasswordReader.readPassword();
                    this.getOriginalOut().println();
                }
                else {
                    pw = null;
                }
            }
        }
        if (this.saslOption.isPresent()) {
            String dnStr;
            if (this.bindDN.isPresent()) {
                dnStr = this.bindDN.getValue().toString();
            }
            else {
                dnStr = null;
            }
            return SASLUtils.createBindRequest(dnStr, pw, this.defaultToPromptForBindPassword(), this, null, this.saslOption.getValues(), bindControls);
        }
        if (this.useSASLExternal.isPresent()) {
            return new EXTERNALBindRequest(bindControls);
        }
        if (this.bindDN.isPresent()) {
            if (pw == null && !this.bindDN.getValue().isNullDN() && this.defaultToPromptForBindPassword()) {
                this.getOriginalOut().print(UtilityMessages.INFO_LDAP_TOOL_ENTER_BIND_PASSWORD.get());
                pw = PasswordReader.readPassword();
                this.getOriginalOut().println();
            }
            return new SimpleBindRequest(this.bindDN.getValue(), pw, bindControls);
        }
        return null;
    }
    
    public final boolean anyLDAPArgumentsProvided() {
        return isAnyPresent(this.host, this.port, this.bindDN, this.bindPassword, this.bindPasswordFile, this.promptForBindPassword, this.useSSL, this.useStartTLS, this.trustAll, this.keyStorePath, this.keyStorePassword, this.keyStorePasswordFile, this.promptForKeyStorePassword, this.keyStoreFormat, this.trustStorePath, this.trustStorePassword, this.trustStorePasswordFile, this.trustStoreFormat, this.certificateNickname, this.saslOption, this.useSASLExternal);
    }
    
    private static boolean isAnyPresent(final Argument... args) {
        for (final Argument a : args) {
            if (a != null && a.getNumOccurrences() > 0) {
                return true;
            }
        }
        return false;
    }
}
