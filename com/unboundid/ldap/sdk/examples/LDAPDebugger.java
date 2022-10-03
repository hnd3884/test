package com.unboundid.ldap.sdk.examples;

import java.util.LinkedHashMap;
import com.unboundid.util.ObjectPair;
import java.util.logging.Handler;
import com.unboundid.util.Debug;
import javax.net.ServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import com.unboundid.util.ssl.KeyStoreKeyManager;
import java.io.File;
import com.unboundid.ldap.listener.SelfSignedCertificateGenerator;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.listener.LDAPListenerConfig;
import com.unboundid.ldap.listener.ToCodeRequestHandler;
import com.unboundid.ldap.listener.LDAPListenerRequestHandler;
import com.unboundid.ldap.listener.LDAPDebuggerRequestHandler;
import java.util.logging.Formatter;
import com.unboundid.util.MinimalLogFormatter;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.io.IOException;
import com.unboundid.util.StaticUtils;
import java.util.logging.FileHandler;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.listener.ProxyRequestHandler;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import com.unboundid.ldap.listener.LDAPListener;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPDebugger extends LDAPCommandLineTool implements Serializable
{
    private static final long serialVersionUID = -8942937427428190983L;
    private ArgumentParser parser;
    private BooleanArgument listenUsingSSL;
    private BooleanArgument generateSelfSignedCertificate;
    private FileArgument codeLogFile;
    private FileArgument outputFile;
    private IntegerArgument listenPort;
    private LDAPDebuggerShutdownListener shutdownListener;
    private LDAPListener listener;
    private StringArgument listenAddress;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final LDAPDebugger ldapDebugger = new LDAPDebugger(outStream, errStream);
        return ldapDebugger.runTool(args);
    }
    
    public LDAPDebugger(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
    }
    
    @Override
    public String getToolName() {
        return "ldap-debugger";
    }
    
    @Override
    public String getToolDescription() {
        return "Intercept and decode LDAP communication.";
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
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
    protected boolean defaultToPromptForBindPassword() {
        return true;
    }
    
    @Override
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    protected boolean includeAlternateLongIdentifiers() {
        return true;
    }
    
    @Override
    protected boolean supportsSSLDebugging() {
        return true;
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        this.parser = parser;
        String description = "The address on which to listen for client connections.  If this is not provided, then it will listen on all interfaces.";
        (this.listenAddress = new StringArgument('a', "listenAddress", false, 1, "{address}", description)).addLongIdentifier("listen-address", true);
        parser.addArgument(this.listenAddress);
        description = "The port on which to listen for client connections.  If no value is provided, then a free port will be automatically selected.";
        (this.listenPort = new IntegerArgument('L', "listenPort", true, 1, "{port}", description, 0, 65535, 0)).addLongIdentifier("listen-port", true);
        parser.addArgument(this.listenPort);
        description = "Use SSL when accepting client connections.  This is independent of the '--useSSL' option, which applies only to communication between the LDAP debugger and the backend server.  If this argument is provided, then either the --keyStorePath or the --generateSelfSignedCertificate argument must also be provided.";
        (this.listenUsingSSL = new BooleanArgument('S', "listenUsingSSL", 1, description)).addLongIdentifier("listen-using-ssl", true);
        parser.addArgument(this.listenUsingSSL);
        description = "Generate a self-signed certificate to present to clients when the --listenUsingSSL argument is provided.  This argument cannot be used in conjunction with the --keyStorePath argument.";
        (this.generateSelfSignedCertificate = new BooleanArgument(null, "generateSelfSignedCertificate", 1, description)).addLongIdentifier("generate-self-signed-certificate", true);
        parser.addArgument(this.generateSelfSignedCertificate);
        description = "The path to the output file to be written.  If no value is provided, then the output will be written to standard output.";
        (this.outputFile = new FileArgument('f', "outputFile", false, 1, "{path}", description, false, true, true, false)).addLongIdentifier("output-file", true);
        parser.addArgument(this.outputFile);
        description = "The path to the a code log file to be written.  If a value is provided, then the tool will generate sample code that corresponds to the requests received from clients.  If no value is provided, then no code log will be generated.";
        (this.codeLogFile = new FileArgument('c', "codeLogFile", false, 1, "{path}", description, false, true, true, false)).addLongIdentifier("code-log-file", true);
        parser.addArgument(this.codeLogFile);
        final Argument keyStorePathArgument = parser.getNamedArgument("keyStorePath");
        parser.addDependentArgumentSet(this.listenUsingSSL, keyStorePathArgument, this.generateSelfSignedCertificate);
        final Argument keyStorePasswordArgument = parser.getNamedArgument("keyStorePassword");
        final Argument keyStorePasswordFileArgument = parser.getNamedArgument("keyStorePasswordFile");
        final Argument promptForKeyStorePasswordArgument = parser.getNamedArgument("promptForKeyStorePassword");
        parser.addExclusiveArgumentSet(this.generateSelfSignedCertificate, keyStorePathArgument, new Argument[0]);
        parser.addExclusiveArgumentSet(this.generateSelfSignedCertificate, keyStorePasswordArgument, new Argument[0]);
        parser.addExclusiveArgumentSet(this.generateSelfSignedCertificate, keyStorePasswordFileArgument, new Argument[0]);
        parser.addExclusiveArgumentSet(this.generateSelfSignedCertificate, promptForKeyStorePasswordArgument, new Argument[0]);
    }
    
    @Override
    public ResultCode doToolProcessing() {
        ProxyRequestHandler proxyHandler;
        try {
            proxyHandler = new ProxyRequestHandler(this.createServerSet());
        }
        catch (final LDAPException le) {
            this.err("Unable to prepare to connect to the target server:  ", le.getMessage());
            return le.getResultCode();
        }
        Handler logHandler = null;
        Label_0105: {
            if (this.outputFile.isPresent()) {
                try {
                    logHandler = new FileHandler(this.outputFile.getValue().getAbsolutePath());
                    break Label_0105;
                }
                catch (final IOException ioe) {
                    this.err("Unable to open the output file for writing:  ", StaticUtils.getExceptionMessage(ioe));
                    return ResultCode.LOCAL_ERROR;
                }
            }
            logHandler = new ConsoleHandler();
        }
        StaticUtils.setLogHandlerLevel(logHandler, Level.INFO);
        logHandler.setFormatter(new MinimalLogFormatter("'['dd/MMM/yyyy:HH:mm:ss Z']'", false, false, true));
        LDAPListenerRequestHandler requestHandler = new LDAPDebuggerRequestHandler(logHandler, proxyHandler);
        if (this.codeLogFile.isPresent()) {
            try {
                requestHandler = new ToCodeRequestHandler(this.codeLogFile.getValue(), true, requestHandler);
            }
            catch (final Exception e) {
                this.err("Unable to open code log file '", this.codeLogFile.getValue().getAbsolutePath(), "' for writing:  ", StaticUtils.getExceptionMessage(e));
                return ResultCode.LOCAL_ERROR;
            }
        }
        final LDAPListenerConfig config = new LDAPListenerConfig(this.listenPort.getValue(), requestHandler);
        if (this.listenAddress.isPresent()) {
            try {
                config.setListenAddress(LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(this.listenAddress.getValue()));
            }
            catch (final Exception e2) {
                this.err("Unable to resolve '", this.listenAddress.getValue(), "' as a valid address:  ", StaticUtils.getExceptionMessage(e2));
                return ResultCode.PARAM_ERROR;
            }
        }
        if (this.listenUsingSSL.isPresent()) {
            try {
                SSLUtil sslUtil;
                if (this.generateSelfSignedCertificate.isPresent()) {
                    final ObjectPair<File, char[]> keyStoreInfo = SelfSignedCertificateGenerator.generateTemporarySelfSignedCertificate(this.getToolName(), "JKS");
                    sslUtil = new SSLUtil(new KeyStoreKeyManager(keyStoreInfo.getFirst(), keyStoreInfo.getSecond(), "JKS", null), new TrustAllTrustManager(false));
                }
                else {
                    sslUtil = this.createSSLUtil(true);
                }
                config.setServerSocketFactory(sslUtil.createSSLServerSocketFactory());
            }
            catch (final Exception e2) {
                this.err("Unable to create a server socket factory to accept SSL-based client connections:  ", StaticUtils.getExceptionMessage(e2));
                return ResultCode.LOCAL_ERROR;
            }
        }
        this.listener = new LDAPListener(config);
        try {
            this.listener.startListening();
        }
        catch (final Exception e2) {
            this.err("Unable to start listening for client connections:  ", StaticUtils.getExceptionMessage(e2));
            return ResultCode.LOCAL_ERROR;
        }
        int port;
        for (port = this.listener.getListenPort(); port <= 0; port = this.listener.getListenPort()) {
            try {
                Thread.sleep(1L);
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                if (e3 instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (this.listenUsingSSL.isPresent()) {
            this.out("Listening for SSL-based LDAP client connections on port ", port);
        }
        else {
            this.out("Listening for LDAP client connections on port ", port);
        }
        this.shutdownListener = new LDAPDebuggerShutdownListener(this.listener, logHandler);
        Runtime.getRuntime().addShutdownHook(this.shutdownListener);
        return ResultCode.SUCCESS;
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "--hostname", "server.example.com", "--port", "389", "--listenPort", "1389", "--outputFile", "/tmp/ldap-debugger.log" };
        final String description = "Listen for client connections on port 1389 on all interfaces and forward any traffic received to server.example.com:389.  The decoded LDAP communication will be written to the /tmp/ldap-debugger.log log file.";
        examples.put(args, "Listen for client connections on port 1389 on all interfaces and forward any traffic received to server.example.com:389.  The decoded LDAP communication will be written to the /tmp/ldap-debugger.log log file.");
        return examples;
    }
    
    public LDAPListener getListener() {
        return this.listener;
    }
    
    public void shutDown() {
        Runtime.getRuntime().removeShutdownHook(this.shutdownListener);
        this.shutdownListener.run();
    }
}
