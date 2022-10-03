package com.unboundid.util;

import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.Control;
import javax.net.ssl.KeyManager;
import com.unboundid.util.ssl.PromptTrustManager;
import com.unboundid.util.ssl.JVMDefaultTrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import com.unboundid.util.ssl.TrustStoreTrustManager;
import com.unboundid.util.ssl.TrustAllTrustManager;
import com.unboundid.util.ssl.KeyStoreKeyManager;
import javax.net.SocketFactory;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.ldap.sdk.SingleServerSet;
import com.unboundid.ldap.sdk.PostConnectProcessor;
import com.unboundid.ldap.sdk.StartTLSPostConnectProcessor;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
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
public abstract class MultiServerLDAPCommandLineTool extends CommandLineTool
{
    private final int numServers;
    private final String[] serverNamePrefixes;
    private final String[] serverNameSuffixes;
    private final BooleanArgument[] trustAll;
    private final BooleanArgument[] useSSL;
    private final BooleanArgument[] useStartTLS;
    private final DNArgument[] bindDN;
    private final FileArgument[] bindPasswordFile;
    private final FileArgument[] keyStorePasswordFile;
    private final FileArgument[] trustStorePasswordFile;
    private final IntegerArgument[] port;
    private final StringArgument[] bindPassword;
    private final StringArgument[] certificateNickname;
    private final StringArgument[] host;
    private final StringArgument[] keyStoreFormat;
    private final StringArgument[] keyStorePath;
    private final StringArgument[] keyStorePassword;
    private final StringArgument[] saslOption;
    private final StringArgument[] trustStoreFormat;
    private final StringArgument[] trustStorePath;
    private final StringArgument[] trustStorePassword;
    private final BindRequest[] bindRequest;
    private final ServerSet[] serverSet;
    private final SSLSocketFactory[] startTLSSocketFactory;
    private final AtomicReference<AggregateTrustManager> promptTrustManager;
    
    public MultiServerLDAPCommandLineTool(final OutputStream outStream, final OutputStream errStream, final String[] serverNamePrefixes, final String[] serverNameSuffixes) throws LDAPSDKUsageException {
        super(outStream, errStream);
        this.promptTrustManager = new AtomicReference<AggregateTrustManager>();
        this.serverNamePrefixes = serverNamePrefixes;
        this.serverNameSuffixes = serverNameSuffixes;
        if (serverNamePrefixes == null) {
            if (serverNameSuffixes == null) {
                throw new LDAPSDKUsageException(UtilityMessages.ERR_MULTI_LDAP_TOOL_PREFIXES_AND_SUFFIXES_NULL.get());
            }
            this.numServers = serverNameSuffixes.length;
        }
        else {
            this.numServers = serverNamePrefixes.length;
            if (serverNameSuffixes != null && serverNamePrefixes.length != serverNameSuffixes.length) {
                throw new LDAPSDKUsageException(UtilityMessages.ERR_MULTI_LDAP_TOOL_PREFIXES_AND_SUFFIXES_MISMATCH.get());
            }
        }
        if (this.numServers == 0) {
            throw new LDAPSDKUsageException(UtilityMessages.ERR_MULTI_LDAP_TOOL_PREFIXES_AND_SUFFIXES_EMPTY.get());
        }
        this.trustAll = new BooleanArgument[this.numServers];
        this.useSSL = new BooleanArgument[this.numServers];
        this.useStartTLS = new BooleanArgument[this.numServers];
        this.bindDN = new DNArgument[this.numServers];
        this.bindPasswordFile = new FileArgument[this.numServers];
        this.keyStorePasswordFile = new FileArgument[this.numServers];
        this.trustStorePasswordFile = new FileArgument[this.numServers];
        this.port = new IntegerArgument[this.numServers];
        this.bindPassword = new StringArgument[this.numServers];
        this.certificateNickname = new StringArgument[this.numServers];
        this.host = new StringArgument[this.numServers];
        this.keyStoreFormat = new StringArgument[this.numServers];
        this.keyStorePath = new StringArgument[this.numServers];
        this.keyStorePassword = new StringArgument[this.numServers];
        this.saslOption = new StringArgument[this.numServers];
        this.trustStoreFormat = new StringArgument[this.numServers];
        this.trustStorePath = new StringArgument[this.numServers];
        this.trustStorePassword = new StringArgument[this.numServers];
        this.bindRequest = new BindRequest[this.numServers];
        this.serverSet = new ServerSet[this.numServers];
        this.startTLSSocketFactory = new SSLSocketFactory[this.numServers];
    }
    
    @Override
    public final void addToolArguments(final ArgumentParser parser) throws ArgumentException {
        for (int i = 0; i < this.numServers; ++i) {
            final StringBuilder groupNameBuffer = new StringBuilder();
            if (this.serverNamePrefixes != null) {
                final String prefix = this.serverNamePrefixes[i].replace('-', ' ').trim();
                groupNameBuffer.append(StaticUtils.capitalize(prefix, true));
            }
            if (this.serverNameSuffixes != null) {
                if (groupNameBuffer.length() > 0) {
                    groupNameBuffer.append(' ');
                }
                final String suffix = this.serverNameSuffixes[i].replace('-', ' ').trim();
                groupNameBuffer.append(StaticUtils.capitalize(suffix, true));
            }
            groupNameBuffer.append(' ');
            groupNameBuffer.append(UtilityMessages.INFO_MULTI_LDAP_TOOL_GROUP_CONN_AND_AUTH.get());
            final String groupName = groupNameBuffer.toString();
            (this.host[i] = new StringArgument(null, this.genArgName(i, "hostname"), true, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_HOST.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_HOST.get(), "localhost")).setArgumentGroupName(groupName);
            parser.addArgument(this.host[i]);
            (this.port[i] = new IntegerArgument(null, this.genArgName(i, "port"), true, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PORT.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_PORT.get(), 1, 65535, 389)).setArgumentGroupName(groupName);
            parser.addArgument(this.port[i]);
            (this.bindDN[i] = new DNArgument(null, this.genArgName(i, "bindDN"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_DN.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_BIND_DN.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.bindDN[i]);
            (this.bindPassword[i] = new StringArgument(null, this.genArgName(i, "bindPassword"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PASSWORD.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_BIND_PW.get())).setSensitive(true);
            this.bindPassword[i].setArgumentGroupName(groupName);
            parser.addArgument(this.bindPassword[i]);
            (this.bindPasswordFile[i] = new FileArgument(null, this.genArgName(i, "bindPasswordFile"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_BIND_PW_FILE.get(), true, true, true, false)).setArgumentGroupName(groupName);
            parser.addArgument(this.bindPasswordFile[i]);
            (this.useSSL[i] = new BooleanArgument(null, this.genArgName(i, "useSSL"), 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_USE_SSL.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.useSSL[i]);
            (this.useStartTLS[i] = new BooleanArgument(null, this.genArgName(i, "useStartTLS"), 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_USE_START_TLS.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.useStartTLS[i]);
            (this.trustAll[i] = new BooleanArgument(null, this.genArgName(i, "trustAll"), 1, UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_ALL.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.trustAll[i]);
            (this.keyStorePath[i] = new StringArgument(null, this.genArgName(i, "keyStorePath"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PATH.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.keyStorePath[i]);
            (this.keyStorePassword[i] = new StringArgument(null, this.genArgName(i, "keyStorePassword"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PASSWORD.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PASSWORD.get())).setSensitive(true);
            this.keyStorePassword[i].setArgumentGroupName(groupName);
            parser.addArgument(this.keyStorePassword[i]);
            (this.keyStorePasswordFile[i] = new FileArgument(null, this.genArgName(i, "keyStorePasswordFile"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PASSWORD_FILE.get(), true, true, true, false)).setArgumentGroupName(groupName);
            parser.addArgument(this.keyStorePasswordFile[i]);
            (this.keyStoreFormat[i] = new StringArgument(null, this.genArgName(i, "keyStoreFormat"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_FORMAT.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_FORMAT.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.keyStoreFormat[i]);
            (this.trustStorePath[i] = new StringArgument(null, this.genArgName(i, "trustStorePath"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PATH.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.trustStorePath[i]);
            (this.trustStorePassword[i] = new StringArgument(null, this.genArgName(i, "trustStorePassword"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PASSWORD.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PASSWORD.get())).setSensitive(true);
            this.trustStorePassword[i].setArgumentGroupName(groupName);
            parser.addArgument(this.trustStorePassword[i]);
            (this.trustStorePasswordFile[i] = new FileArgument(null, this.genArgName(i, "trustStorePasswordFile"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_PATH.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PASSWORD_FILE.get(), true, true, true, false)).setArgumentGroupName(groupName);
            parser.addArgument(this.trustStorePasswordFile[i]);
            (this.trustStoreFormat[i] = new StringArgument(null, this.genArgName(i, "trustStoreFormat"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_FORMAT.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_FORMAT.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.trustStoreFormat[i]);
            (this.certificateNickname[i] = new StringArgument(null, this.genArgName(i, "certNickname"), false, 1, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_CERT_NICKNAME.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_CERT_NICKNAME.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.certificateNickname[i]);
            (this.saslOption[i] = new StringArgument(null, this.genArgName(i, "saslOption"), false, 0, UtilityMessages.INFO_LDAP_TOOL_PLACEHOLDER_SASL_OPTION.get(), UtilityMessages.INFO_LDAP_TOOL_DESCRIPTION_SASL_OPTION.get())).setArgumentGroupName(groupName);
            parser.addArgument(this.saslOption[i]);
            parser.addDependentArgumentSet(this.bindDN[i], this.bindPassword[i], this.bindPasswordFile[i]);
            parser.addExclusiveArgumentSet(this.useSSL[i], this.useStartTLS[i], new Argument[0]);
            parser.addExclusiveArgumentSet(this.bindPassword[i], this.bindPasswordFile[i], new Argument[0]);
            parser.addExclusiveArgumentSet(this.keyStorePassword[i], this.keyStorePasswordFile[i], new Argument[0]);
            parser.addExclusiveArgumentSet(this.trustStorePassword[i], this.trustStorePasswordFile[i], new Argument[0]);
            parser.addExclusiveArgumentSet(this.trustAll[i], this.trustStorePath[i], new Argument[0]);
        }
        this.addNonLDAPArguments(parser);
    }
    
    private String genArgName(final int index, final String base) {
        final StringBuilder buffer = new StringBuilder();
        if (this.serverNamePrefixes != null) {
            buffer.append(this.serverNamePrefixes[index]);
            if (base.equals("saslOption")) {
                buffer.append("SASLOption");
            }
            else {
                buffer.append(StaticUtils.capitalize(base));
            }
        }
        else {
            buffer.append(base);
        }
        if (this.serverNameSuffixes != null) {
            buffer.append(this.serverNameSuffixes[index]);
        }
        return buffer.toString();
    }
    
    public abstract void addNonLDAPArguments(final ArgumentParser p0) throws ArgumentException;
    
    @Override
    public final void doExtendedArgumentValidation() throws ArgumentException {
        this.doExtendedNonLDAPArgumentValidation();
    }
    
    public void doExtendedNonLDAPArgumentValidation() throws ArgumentException {
    }
    
    public LDAPConnectionOptions getConnectionOptions() {
        return new LDAPConnectionOptions();
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public final LDAPConnection getConnection(final int serverIndex) throws LDAPException {
        final LDAPConnection connection = this.getUnauthenticatedConnection(serverIndex);
        try {
            if (this.bindRequest[serverIndex] != null) {
                connection.bind(this.bindRequest[serverIndex]);
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
    public final LDAPConnection getUnauthenticatedConnection(final int serverIndex) throws LDAPException {
        if (this.serverSet[serverIndex] == null) {
            this.serverSet[serverIndex] = this.createServerSet(serverIndex);
            this.bindRequest[serverIndex] = this.createBindRequest(serverIndex);
        }
        final LDAPConnection connection = this.serverSet[serverIndex].getConnection();
        if (this.useStartTLS[serverIndex].isPresent()) {
            try {
                final ExtendedResult extendedResult = connection.processExtendedOperation(new StartTLSExtendedRequest(this.startTLSSocketFactory[serverIndex]));
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
    public final LDAPConnectionPool getConnectionPool(final int serverIndex, final int initialConnections, final int maxConnections) throws LDAPException {
        if (this.serverSet[serverIndex] == null) {
            this.serverSet[serverIndex] = this.createServerSet(serverIndex);
            this.bindRequest[serverIndex] = this.createBindRequest(serverIndex);
        }
        PostConnectProcessor postConnectProcessor = null;
        if (this.useStartTLS[serverIndex].isPresent()) {
            postConnectProcessor = new StartTLSPostConnectProcessor(this.startTLSSocketFactory[serverIndex]);
        }
        return new LDAPConnectionPool(this.serverSet[serverIndex], this.bindRequest[serverIndex], initialConnections, maxConnections, postConnectProcessor);
    }
    
    public final ServerSet createServerSet(final int serverIndex) throws LDAPException {
        final SSLUtil sslUtil = this.createSSLUtil(serverIndex);
        SocketFactory socketFactory = null;
        if (this.useSSL[serverIndex].isPresent()) {
            try {
                socketFactory = sslUtil.createSSLSocketFactory();
                return new SingleServerSet(this.host[serverIndex].getValue(), this.port[serverIndex].getValue(), socketFactory, this.getConnectionOptions());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_CREATE_SSL_SOCKET_FACTORY.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
        if (this.useStartTLS[serverIndex].isPresent()) {
            try {
                this.startTLSSocketFactory[serverIndex] = sslUtil.createSSLSocketFactory();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_CREATE_SSL_SOCKET_FACTORY.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
        return new SingleServerSet(this.host[serverIndex].getValue(), this.port[serverIndex].getValue(), socketFactory, this.getConnectionOptions());
    }
    
    public final SSLUtil createSSLUtil(final int serverIndex) throws LDAPException {
        if (this.useSSL[serverIndex].isPresent() || this.useStartTLS[serverIndex].isPresent()) {
            KeyManager keyManager = null;
            if (this.keyStorePath[serverIndex].isPresent()) {
                char[] pw = null;
                if (this.keyStorePassword[serverIndex].isPresent()) {
                    pw = this.keyStorePassword[serverIndex].getValue().toCharArray();
                }
                else if (this.keyStorePasswordFile[serverIndex].isPresent()) {
                    try {
                        pw = this.getPasswordFileReader().readPassword(this.keyStorePasswordFile[serverIndex].getValue());
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_READ_KEY_STORE_PASSWORD.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                }
                try {
                    keyManager = new KeyStoreKeyManager(this.keyStorePath[serverIndex].getValue(), pw, this.keyStoreFormat[serverIndex].getValue(), this.certificateNickname[serverIndex].getValue());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_CREATE_KEY_MANAGER.get(StaticUtils.getExceptionMessage(e)), e);
                }
            }
            TrustManager tm;
            if (this.trustAll[serverIndex].isPresent()) {
                tm = new TrustAllTrustManager(false);
            }
            else if (this.trustStorePath[serverIndex].isPresent()) {
                char[] pw2 = null;
                if (this.trustStorePassword[serverIndex].isPresent()) {
                    pw2 = this.trustStorePassword[serverIndex].getValue().toCharArray();
                }
                else if (this.trustStorePasswordFile[serverIndex].isPresent()) {
                    try {
                        pw2 = this.getPasswordFileReader().readPassword(this.trustStorePasswordFile[serverIndex].getValue());
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_READ_TRUST_STORE_PASSWORD.get(StaticUtils.getExceptionMessage(e2)), e2);
                    }
                }
                tm = new TrustStoreTrustManager(this.trustStorePath[serverIndex].getValue(), pw2, this.trustStoreFormat[serverIndex].getValue(), true);
            }
            else {
                tm = this.promptTrustManager.get();
                if (tm == null) {
                    final AggregateTrustManager atm = new AggregateTrustManager(false, new X509TrustManager[] { JVMDefaultTrustManager.getInstance(), new PromptTrustManager() });
                    if (this.promptTrustManager.compareAndSet(null, atm)) {
                        tm = atm;
                    }
                    else {
                        tm = this.promptTrustManager.get();
                    }
                }
            }
            return new SSLUtil(keyManager, tm);
        }
        return null;
    }
    
    public final BindRequest createBindRequest(final int serverIndex) throws LDAPException {
        String pw = null;
        Label_0100: {
            if (this.bindPassword[serverIndex].isPresent()) {
                pw = this.bindPassword[serverIndex].getValue();
            }
            else {
                if (this.bindPasswordFile[serverIndex].isPresent()) {
                    try {
                        pw = new String(this.getPasswordFileReader().readPassword(this.bindPasswordFile[serverIndex].getValue()));
                        break Label_0100;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_LDAP_TOOL_CANNOT_READ_BIND_PASSWORD.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                }
                pw = null;
            }
        }
        if (this.saslOption[serverIndex].isPresent()) {
            String dnStr;
            if (this.bindDN[serverIndex].isPresent()) {
                dnStr = this.bindDN[serverIndex].getValue().toString();
            }
            else {
                dnStr = null;
            }
            return SASLUtils.createBindRequest(dnStr, pw, null, this.saslOption[serverIndex].getValues(), new Control[0]);
        }
        if (this.bindDN[serverIndex].isPresent()) {
            return new SimpleBindRequest(this.bindDN[serverIndex].getValue(), pw);
        }
        return null;
    }
}
