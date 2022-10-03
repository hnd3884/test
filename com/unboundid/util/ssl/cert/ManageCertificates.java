package com.unboundid.util.ssl.cert;

import com.unboundid.util.ssl.JVMDefaultTrustManager;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.CopyOption;
import java.text.SimpleDateFormat;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.PasswordReader;
import com.unboundid.util.Base64;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1BitString;
import java.io.FileInputStream;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.util.ObjectPair;
import java.security.PublicKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.net.InetAddress;
import java.security.KeyPair;
import com.unboundid.util.OID;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.Validator;
import java.util.Arrays;
import java.util.Collection;
import java.security.Key;
import java.security.UnrecoverableKeyException;
import java.security.PrivateKey;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import com.unboundid.ldap.sdk.DN;
import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicReference;
import java.security.cert.Certificate;
import java.util.List;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import java.util.LinkedHashSet;
import java.util.Collections;
import com.unboundid.util.args.ArgumentException;
import java.util.Set;
import com.unboundid.util.args.BooleanValueArgument;
import com.unboundid.util.args.OIDArgumentValueValidator;
import com.unboundid.util.args.ArgumentValueValidator;
import com.unboundid.util.args.IPAddressArgumentValueValidator;
import com.unboundid.util.args.TimestampArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.SubCommand;
import java.util.LinkedHashMap;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.FileArgument;
import java.io.ByteArrayInputStream;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import java.io.InputStream;
import com.unboundid.util.args.ArgumentParser;
import java.io.File;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.CommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ManageCertificates extends CommandLineTool
{
    private static final File JVM_DEFAULT_CACERTS_FILE;
    private static final String PROPERTY_DEFAULT_KEYSTORE_TYPE;
    private static final String DEFAULT_KEYSTORE_TYPE;
    private static final int WRAP_COLUMN;
    private volatile ArgumentParser globalParser;
    private volatile ArgumentParser subCommandParser;
    private final InputStream in;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.in, System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(Math.max(1, Math.min(resultCode.intValue(), 255)));
        }
    }
    
    public static ResultCode main(final InputStream in, final OutputStream out, final OutputStream err, final String... args) {
        final ManageCertificates manageCertificates = new ManageCertificates(in, out, err);
        return manageCertificates.runTool(args);
    }
    
    public ManageCertificates(final OutputStream out, final OutputStream err) {
        this(null, out, err);
    }
    
    public ManageCertificates(final InputStream in, final OutputStream out, final OutputStream err) {
        super(out, err);
        this.globalParser = null;
        this.subCommandParser = null;
        if (in == null) {
            this.in = new ByteArrayInputStream(StaticUtils.NO_BYTES);
        }
        else {
            this.in = in;
        }
    }
    
    @Override
    public String getToolName() {
        return "manage-certificates";
    }
    
    @Override
    public String getToolDescription() {
        return CertMessages.INFO_MANAGE_CERTS_TOOL_DESC.get();
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
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    protected boolean supportsOutputFile() {
        return false;
    }
    
    @Override
    protected boolean logToolInvocationByDefault() {
        return true;
    }
    
    @Override
    public void addToolArguments(final ArgumentParser parser) throws ArgumentException {
        this.globalParser = parser;
        final ArgumentParser listCertsParser = new ArgumentParser("list-certificates", CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_DESC.get());
        final FileArgument listCertsKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_ARG_KS_DESC.get(), true, true, true, false);
        listCertsKeystore.addLongIdentifier("keystore-path", true);
        listCertsKeystore.addLongIdentifier("keystorePath", true);
        listCertsKeystore.addLongIdentifier("keystore-file", true);
        listCertsKeystore.addLongIdentifier("keystoreFile", true);
        listCertsParser.addArgument(listCertsKeystore);
        final StringArgument listCertsKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_ARG_KS_PW_DESC.get());
        listCertsKeystorePassword.addLongIdentifier("keystorePassword", true);
        listCertsKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        listCertsKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        listCertsKeystorePassword.addLongIdentifier("keystore-pin", true);
        listCertsKeystorePassword.addLongIdentifier("keystorePIN", true);
        listCertsKeystorePassword.addLongIdentifier("storepass", true);
        listCertsKeystorePassword.setSensitive(true);
        listCertsParser.addArgument(listCertsKeystorePassword);
        final FileArgument listCertsKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        listCertsKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        listCertsKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        listCertsKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        listCertsKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        listCertsKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        listCertsParser.addArgument(listCertsKeystorePasswordFile);
        final BooleanArgument listCertsPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_ARG_PROMPT_FOR_KS_PW_DESC.get());
        listCertsPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        listCertsPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        listCertsPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        listCertsPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        listCertsPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        listCertsParser.addArgument(listCertsPromptForKeystorePassword);
        final StringArgument listCertsAlias = new StringArgument(null, "alias", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_ARG_ALIAS_DESC.get());
        listCertsAlias.addLongIdentifier("nickname", true);
        listCertsParser.addArgument(listCertsAlias);
        final BooleanArgument listCertsDisplayPEM = new BooleanArgument(null, "display-pem-certificate", 1, CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_ARG_DISPLAY_PEM_DESC.get());
        listCertsDisplayPEM.addLongIdentifier("displayPEMCertificate", true);
        listCertsDisplayPEM.addLongIdentifier("display-pem", true);
        listCertsDisplayPEM.addLongIdentifier("displayPEM", true);
        listCertsDisplayPEM.addLongIdentifier("show-pem-certificate", true);
        listCertsDisplayPEM.addLongIdentifier("showPEMCertificate", true);
        listCertsDisplayPEM.addLongIdentifier("show-pem", true);
        listCertsDisplayPEM.addLongIdentifier("showPEM", true);
        listCertsDisplayPEM.addLongIdentifier("pem", true);
        listCertsDisplayPEM.addLongIdentifier("rfc", true);
        listCertsParser.addArgument(listCertsDisplayPEM);
        final BooleanArgument listCertsVerbose = new BooleanArgument(null, "verbose", 1, CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_ARG_VERBOSE_DESC.get());
        listCertsParser.addArgument(listCertsVerbose);
        final BooleanArgument listCertsDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_ARG_DISPLAY_COMMAND_DESC.get());
        listCertsDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        listCertsDisplayCommand.addLongIdentifier("show-keytool-command", true);
        listCertsDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        listCertsParser.addArgument(listCertsDisplayCommand);
        listCertsParser.addExclusiveArgumentSet(listCertsKeystorePassword, listCertsKeystorePasswordFile, listCertsPromptForKeystorePassword);
        final LinkedHashMap<String[], String> listCertsExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(3));
        listCertsExamples.put(new String[] { "list-certificates", "--keystore", getPlatformSpecificPath("config", "keystore") }, CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_EXAMPLE_1.get(getPlatformSpecificPath("config", "keystore")));
        listCertsExamples.put(new String[] { "list-certificates", "--keystore", getPlatformSpecificPath("config", "keystore.p12"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--verbose", "--display-pem-certificate", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_EXAMPLE_2.get(getPlatformSpecificPath("config", "keystore.p12"), getPlatformSpecificPath("config", "keystore.pin")));
        if (ManageCertificates.JVM_DEFAULT_CACERTS_FILE != null) {
            listCertsExamples.put(new String[] { "list-certificates", "--keystore", ManageCertificates.JVM_DEFAULT_CACERTS_FILE.getAbsolutePath() }, CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_EXAMPLE_3.get());
        }
        final SubCommand listCertsSubCommand = new SubCommand("list-certificates", CertMessages.INFO_MANAGE_CERTS_SC_LIST_CERTS_DESC.get(), listCertsParser, listCertsExamples);
        listCertsSubCommand.addName("listCertificates", true);
        listCertsSubCommand.addName("list-certs", true);
        listCertsSubCommand.addName("listCerts", true);
        listCertsSubCommand.addName("list", false);
        parser.addSubCommand(listCertsSubCommand);
        final ArgumentParser exportCertParser = new ArgumentParser("export-certificate", CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_DESC.get());
        final FileArgument exportCertKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_KS_DESC.get(), true, true, true, false);
        exportCertKeystore.addLongIdentifier("keystore-path", true);
        exportCertKeystore.addLongIdentifier("keystorePath", true);
        exportCertKeystore.addLongIdentifier("keystore-file", true);
        exportCertKeystore.addLongIdentifier("keystoreFile", true);
        exportCertParser.addArgument(exportCertKeystore);
        final StringArgument exportCertKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_KS_PW_DESC.get());
        exportCertKeystorePassword.addLongIdentifier("keystorePassword", true);
        exportCertKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        exportCertKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        exportCertKeystorePassword.addLongIdentifier("keystore-pin", true);
        exportCertKeystorePassword.addLongIdentifier("keystorePIN", true);
        exportCertKeystorePassword.addLongIdentifier("storepass", true);
        exportCertKeystorePassword.setSensitive(true);
        exportCertParser.addArgument(exportCertKeystorePassword);
        final FileArgument exportCertKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        exportCertKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        exportCertKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        exportCertKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        exportCertKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        exportCertKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        exportCertParser.addArgument(exportCertKeystorePasswordFile);
        final BooleanArgument exportCertPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_PROMPT_FOR_KS_PW_DESC.get());
        exportCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        exportCertPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        exportCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        exportCertPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        exportCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        exportCertParser.addArgument(exportCertPromptForKeystorePassword);
        final StringArgument exportCertAlias = new StringArgument(null, "alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_ALIAS_DESC.get());
        exportCertAlias.addLongIdentifier("nickname", true);
        exportCertParser.addArgument(exportCertAlias);
        final BooleanArgument exportCertChain = new BooleanArgument(null, "export-certificate-chain", 1, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_CHAIN_DESC.get());
        exportCertChain.addLongIdentifier("exportCertificateChain", true);
        exportCertChain.addLongIdentifier("export-chain", true);
        exportCertChain.addLongIdentifier("exportChain", true);
        exportCertChain.addLongIdentifier("certificate-chain", true);
        exportCertChain.addLongIdentifier("certificateChain", true);
        exportCertChain.addLongIdentifier("chain", true);
        exportCertParser.addArgument(exportCertChain);
        final Set<String> exportCertOutputFormatAllowedValues = StaticUtils.setOf("PEM", "text", "txt", "RFC", "DER", "binary", "bin");
        final StringArgument exportCertOutputFormat = new StringArgument(null, "output-format", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_FORMAT.get(), CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_FORMAT_DESC.get(), exportCertOutputFormatAllowedValues, "PEM");
        exportCertOutputFormat.addLongIdentifier("outputFormat");
        exportCertParser.addArgument(exportCertOutputFormat);
        final FileArgument exportCertOutputFile = new FileArgument(null, "output-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_FILE_DESC.get(), false, true, true, false);
        exportCertOutputFile.addLongIdentifier("outputFile", true);
        exportCertOutputFile.addLongIdentifier("export-file", true);
        exportCertOutputFile.addLongIdentifier("exportFile", true);
        exportCertOutputFile.addLongIdentifier("certificate-file", true);
        exportCertOutputFile.addLongIdentifier("certificateFile", true);
        exportCertOutputFile.addLongIdentifier("file", true);
        exportCertOutputFile.addLongIdentifier("filename", true);
        exportCertParser.addArgument(exportCertOutputFile);
        final BooleanArgument exportCertSeparateFile = new BooleanArgument(null, "separate-file-per-certificate", 1, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_SEPARATE_FILE_DESC.get());
        exportCertSeparateFile.addLongIdentifier("separateFilePerCertificate", true);
        exportCertSeparateFile.addLongIdentifier("separate-files", true);
        exportCertSeparateFile.addLongIdentifier("separateFiles", true);
        exportCertParser.addArgument(exportCertSeparateFile);
        final BooleanArgument exportCertDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_ARG_DISPLAY_COMMAND_DESC.get());
        exportCertDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        exportCertDisplayCommand.addLongIdentifier("show-keytool-command", true);
        exportCertDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        exportCertParser.addArgument(exportCertDisplayCommand);
        exportCertParser.addExclusiveArgumentSet(exportCertKeystorePassword, exportCertKeystorePasswordFile, exportCertPromptForKeystorePassword);
        exportCertParser.addDependentArgumentSet(exportCertSeparateFile, exportCertChain, new Argument[0]);
        exportCertParser.addDependentArgumentSet(exportCertSeparateFile, exportCertOutputFile, new Argument[0]);
        final LinkedHashMap<String[], String> exportCertExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        exportCertExamples.put(new String[] { "export-certificate", "--keystore", getPlatformSpecificPath("config", "keystore"), "--alias", "server-cert" }, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_EXAMPLE_1.get());
        exportCertExamples.put(new String[] { "export-certificate", "--keystore", getPlatformSpecificPath("config", "keystore.p12"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--export-certificate-chain", "--output-format", "DER", "--output-file", "certificate-chain.der", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_EXAMPLE_2.get());
        final SubCommand exportCertSubCommand = new SubCommand("export-certificate", CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_DESC.get(), exportCertParser, exportCertExamples);
        exportCertSubCommand.addName("exportCertificate", true);
        exportCertSubCommand.addName("export-cert", true);
        exportCertSubCommand.addName("exportCert", true);
        exportCertSubCommand.addName("export", false);
        parser.addSubCommand(exportCertSubCommand);
        final ArgumentParser exportKeyParser = new ArgumentParser("export-private-key", CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_DESC.get());
        final FileArgument exportKeyKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_KS_DESC.get(), true, true, true, false);
        exportKeyKeystore.addLongIdentifier("keystore-path", true);
        exportKeyKeystore.addLongIdentifier("keystorePath", true);
        exportKeyKeystore.addLongIdentifier("keystore-file", true);
        exportKeyKeystore.addLongIdentifier("keystoreFile", true);
        exportKeyParser.addArgument(exportKeyKeystore);
        final StringArgument exportKeyKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_KS_PW_DESC.get());
        exportKeyKeystorePassword.addLongIdentifier("keystorePassword", true);
        exportKeyKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        exportKeyKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        exportKeyKeystorePassword.addLongIdentifier("keystore-pin", true);
        exportKeyKeystorePassword.addLongIdentifier("keystorePIN", true);
        exportKeyKeystorePassword.addLongIdentifier("storepass", true);
        exportKeyKeystorePassword.setSensitive(true);
        exportKeyParser.addArgument(exportKeyKeystorePassword);
        final FileArgument exportKeyKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        exportKeyKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        exportKeyKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        exportKeyKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        exportKeyKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        exportKeyKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        exportKeyParser.addArgument(exportKeyKeystorePasswordFile);
        final BooleanArgument exportKeyPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_PROMPT_FOR_KS_PW_DESC.get());
        exportKeyPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        exportKeyPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        exportKeyPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        exportKeyPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        exportKeyPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        exportKeyParser.addArgument(exportKeyPromptForKeystorePassword);
        final StringArgument exportKeyPKPassword = new StringArgument(null, "private-key-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_PK_PW_DESC.get());
        exportKeyPKPassword.addLongIdentifier("privateKeyPassword", true);
        exportKeyPKPassword.addLongIdentifier("private-key-passphrase", true);
        exportKeyPKPassword.addLongIdentifier("privateKeyPassphrase", true);
        exportKeyPKPassword.addLongIdentifier("private-key-pin", true);
        exportKeyPKPassword.addLongIdentifier("privateKeyPIN", true);
        exportKeyPKPassword.addLongIdentifier("key-password", true);
        exportKeyPKPassword.addLongIdentifier("keyPassword", true);
        exportKeyPKPassword.addLongIdentifier("key-passphrase", true);
        exportKeyPKPassword.addLongIdentifier("keyPassphrase", true);
        exportKeyPKPassword.addLongIdentifier("key-pin", true);
        exportKeyPKPassword.addLongIdentifier("keyPIN", true);
        exportKeyPKPassword.addLongIdentifier("keypass", true);
        exportKeyPKPassword.setSensitive(true);
        exportKeyParser.addArgument(exportKeyPKPassword);
        final FileArgument exportKeyPKPasswordFile = new FileArgument(null, "private-key-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_PK_PW_FILE_DESC.get(), true, true, true, false);
        exportKeyPKPasswordFile.addLongIdentifier("privateKeyPasswordFile", true);
        exportKeyPKPasswordFile.addLongIdentifier("private-key-passphrase-file", true);
        exportKeyPKPasswordFile.addLongIdentifier("privateKeyPassphraseFile", true);
        exportKeyPKPasswordFile.addLongIdentifier("private-key-pin-file", true);
        exportKeyPKPasswordFile.addLongIdentifier("privateKeyPINFile", true);
        exportKeyPKPasswordFile.addLongIdentifier("key-password-file", true);
        exportKeyPKPasswordFile.addLongIdentifier("keyPasswordFile", true);
        exportKeyPKPasswordFile.addLongIdentifier("key-passphrase-file", true);
        exportKeyPKPasswordFile.addLongIdentifier("keyPassphraseFile", true);
        exportKeyPKPasswordFile.addLongIdentifier("key-pin-file", true);
        exportKeyPKPasswordFile.addLongIdentifier("keyPINFile", true);
        exportKeyParser.addArgument(exportKeyPKPasswordFile);
        final BooleanArgument exportKeyPromptForPKPassword = new BooleanArgument(null, "prompt-for-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_PROMPT_FOR_PK_PW_DESC.get());
        exportKeyPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassword", true);
        exportKeyPromptForPKPassword.addLongIdentifier("prompt-for-private-key-passphrase", true);
        exportKeyPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassphrase", true);
        exportKeyPromptForPKPassword.addLongIdentifier("prompt-for-private-key-pin", true);
        exportKeyPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPIN", true);
        exportKeyPromptForPKPassword.addLongIdentifier("prompt-for-key-password", true);
        exportKeyPromptForPKPassword.addLongIdentifier("promptForKeyPassword", true);
        exportKeyPromptForPKPassword.addLongIdentifier("prompt-for-key-passphrase", true);
        exportKeyPromptForPKPassword.addLongIdentifier("promptForKeyPassphrase", true);
        exportKeyPromptForPKPassword.addLongIdentifier("prompt-for-key-pin", true);
        exportKeyPromptForPKPassword.addLongIdentifier("promptForKeyPIN", true);
        exportKeyParser.addArgument(exportKeyPromptForPKPassword);
        final StringArgument exportKeyAlias = new StringArgument(null, "alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_ALIAS_DESC.get());
        exportKeyAlias.addLongIdentifier("nickname", true);
        exportKeyParser.addArgument(exportKeyAlias);
        final Set<String> exportKeyOutputFormatAllowedValues = StaticUtils.setOf("PEM", "text", "txt", "RFC", "DER", "binary", "bin");
        final StringArgument exportKeyOutputFormat = new StringArgument(null, "output-format", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_FORMAT.get(), CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_FORMAT_DESC.get(), exportKeyOutputFormatAllowedValues, "PEM");
        exportKeyOutputFormat.addLongIdentifier("outputFormat");
        exportKeyParser.addArgument(exportKeyOutputFormat);
        final FileArgument exportKeyOutputFile = new FileArgument(null, "output-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_ARG_FILE_DESC.get(), false, true, true, false);
        exportKeyOutputFile.addLongIdentifier("outputFile", true);
        exportKeyOutputFile.addLongIdentifier("export-file", true);
        exportKeyOutputFile.addLongIdentifier("exportFile", true);
        exportKeyOutputFile.addLongIdentifier("private-key-file", true);
        exportKeyOutputFile.addLongIdentifier("privateKeyFile", true);
        exportKeyOutputFile.addLongIdentifier("key-file", true);
        exportKeyOutputFile.addLongIdentifier("keyFile", true);
        exportKeyOutputFile.addLongIdentifier("file", true);
        exportKeyOutputFile.addLongIdentifier("filename", true);
        exportKeyParser.addArgument(exportKeyOutputFile);
        exportKeyParser.addRequiredArgumentSet(exportKeyKeystorePassword, exportKeyKeystorePasswordFile, exportKeyPromptForKeystorePassword);
        exportKeyParser.addExclusiveArgumentSet(exportKeyKeystorePassword, exportKeyKeystorePasswordFile, exportKeyPromptForKeystorePassword);
        exportKeyParser.addExclusiveArgumentSet(exportKeyPKPassword, exportKeyPKPasswordFile, exportKeyPromptForPKPassword);
        final LinkedHashMap<String[], String> exportKeyExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        exportKeyExamples.put(new String[] { "export-private-key", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert" }, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_EXAMPLE_1.get());
        exportKeyExamples.put(new String[] { "export-private-key", "--keystore", getPlatformSpecificPath("config", "keystore.p12"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--private-key-password-file", getPlatformSpecificPath("config", "server-cert-key.pin"), "--alias", "server-cert", "--output-format", "DER", "--output-file", "server-cert-key.der" }, CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_KEY_EXAMPLE_2.get());
        final SubCommand exportKeySubCommand = new SubCommand("export-private-key", CertMessages.INFO_MANAGE_CERTS_SC_EXPORT_CERT_DESC.get(), exportKeyParser, exportKeyExamples);
        exportKeySubCommand.addName("exportPrivateKey", true);
        exportKeySubCommand.addName("export-key", true);
        exportKeySubCommand.addName("exportKey", true);
        parser.addSubCommand(exportKeySubCommand);
        final ArgumentParser importCertParser = new ArgumentParser("import-certificate", CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_DESC.get());
        final FileArgument importCertKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_KS_DESC.get(), false, true, true, false);
        importCertKeystore.addLongIdentifier("keystore-path", true);
        importCertKeystore.addLongIdentifier("keystorePath", true);
        importCertKeystore.addLongIdentifier("keystore-file", true);
        importCertKeystore.addLongIdentifier("keystoreFile", true);
        importCertParser.addArgument(importCertKeystore);
        final StringArgument importCertKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_KS_PW_DESC.get());
        importCertKeystorePassword.addLongIdentifier("keystorePassword", true);
        importCertKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        importCertKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        importCertKeystorePassword.addLongIdentifier("keystore-pin", true);
        importCertKeystorePassword.addLongIdentifier("keystorePIN", true);
        importCertKeystorePassword.addLongIdentifier("storepass", true);
        importCertKeystorePassword.setSensitive(true);
        importCertParser.addArgument(importCertKeystorePassword);
        final FileArgument importCertKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        importCertKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        importCertKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        importCertKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        importCertKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        importCertKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        importCertParser.addArgument(importCertKeystorePasswordFile);
        final BooleanArgument importCertPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_PROMPT_FOR_KS_PW_DESC.get());
        importCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        importCertPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        importCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        importCertPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        importCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        importCertParser.addArgument(importCertPromptForKeystorePassword);
        final Set<String> importCertKeystoreTypeAllowedValues = StaticUtils.setOf("jks", "pkcs12", "pkcs 12", "pkcs#12", "pkcs #12");
        final StringArgument importCertKeystoreType = new StringArgument(null, "keystore-type", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_TYPE.get(), CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_KS_TYPE_DESC.get(), importCertKeystoreTypeAllowedValues);
        importCertKeystoreType.addLongIdentifier("keystoreType", true);
        importCertKeystoreType.addLongIdentifier("storetype", true);
        importCertParser.addArgument(importCertKeystoreType);
        final StringArgument importCertAlias = new StringArgument(null, "alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_ALIAS_DESC.get());
        importCertAlias.addLongIdentifier("nickname", true);
        importCertParser.addArgument(importCertAlias);
        final FileArgument importCertCertificateFile = new FileArgument(null, "certificate-file", true, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_CERT_FILE_DESC.get(), true, true, true, false);
        importCertCertificateFile.addLongIdentifier("certificateFile", true);
        importCertCertificateFile.addLongIdentifier("certificate-chain-file", true);
        importCertCertificateFile.addLongIdentifier("certificateChainFile", true);
        importCertCertificateFile.addLongIdentifier("input-file", true);
        importCertCertificateFile.addLongIdentifier("inputFile", true);
        importCertCertificateFile.addLongIdentifier("import-file", true);
        importCertCertificateFile.addLongIdentifier("importFile", true);
        importCertCertificateFile.addLongIdentifier("file", true);
        importCertCertificateFile.addLongIdentifier("filename", true);
        importCertParser.addArgument(importCertCertificateFile);
        final FileArgument importCertPKFile = new FileArgument(null, "private-key-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_KEY_FILE_DESC.get(), true, true, true, false);
        importCertPKFile.addLongIdentifier("privateKeyFile", true);
        importCertPKFile.addLongIdentifier("key-file", true);
        importCertPKFile.addLongIdentifier("keyFile", true);
        importCertParser.addArgument(importCertPKFile);
        final StringArgument importCertPKPassword = new StringArgument(null, "private-key-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_PK_PW_DESC.get());
        importCertPKPassword.addLongIdentifier("privateKeyPassword", true);
        importCertPKPassword.addLongIdentifier("private-key-passphrase", true);
        importCertPKPassword.addLongIdentifier("privateKeyPassphrase", true);
        importCertPKPassword.addLongIdentifier("private-key-pin", true);
        importCertPKPassword.addLongIdentifier("privateKeyPIN", true);
        importCertPKPassword.addLongIdentifier("key-password", true);
        importCertPKPassword.addLongIdentifier("keyPassword", true);
        importCertPKPassword.addLongIdentifier("key-passphrase", true);
        importCertPKPassword.addLongIdentifier("keyPassphrase", true);
        importCertPKPassword.addLongIdentifier("key-pin", true);
        importCertPKPassword.addLongIdentifier("keyPIN", true);
        importCertPKPassword.addLongIdentifier("keypass", true);
        importCertPKPassword.setSensitive(true);
        importCertParser.addArgument(importCertPKPassword);
        final FileArgument importCertPKPasswordFile = new FileArgument(null, "private-key-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_PK_PW_FILE_DESC.get(), true, true, true, false);
        importCertPKPasswordFile.addLongIdentifier("privateKeyPasswordFile", true);
        importCertPKPasswordFile.addLongIdentifier("private-key-passphrase-file", true);
        importCertPKPasswordFile.addLongIdentifier("privateKeyPassphraseFile", true);
        importCertPKPasswordFile.addLongIdentifier("private-key-pin-file", true);
        importCertPKPasswordFile.addLongIdentifier("privateKeyPINFile", true);
        importCertPKPasswordFile.addLongIdentifier("key-password-file", true);
        importCertPKPasswordFile.addLongIdentifier("keyPasswordFile", true);
        importCertPKPasswordFile.addLongIdentifier("key-passphrase-file", true);
        importCertPKPasswordFile.addLongIdentifier("keyPassphraseFile", true);
        importCertPKPasswordFile.addLongIdentifier("key-pin-file", true);
        importCertPKPasswordFile.addLongIdentifier("keyPINFile", true);
        importCertParser.addArgument(importCertPKPasswordFile);
        final BooleanArgument importCertPromptForPKPassword = new BooleanArgument(null, "prompt-for-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_PROMPT_FOR_PK_PW_DESC.get());
        importCertPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassword", true);
        importCertPromptForPKPassword.addLongIdentifier("prompt-for-private-key-passphrase", true);
        importCertPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassphrase", true);
        importCertPromptForPKPassword.addLongIdentifier("prompt-for-private-key-pin", true);
        importCertPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPIN", true);
        importCertPromptForPKPassword.addLongIdentifier("prompt-for-key-password", true);
        importCertPromptForPKPassword.addLongIdentifier("promptForKeyPassword", true);
        importCertPromptForPKPassword.addLongIdentifier("prompt-for-key-passphrase", true);
        importCertPromptForPKPassword.addLongIdentifier("promptForKeyPassphrase", true);
        importCertPromptForPKPassword.addLongIdentifier("prompt-for-key-pin", true);
        importCertPromptForPKPassword.addLongIdentifier("promptForKeyPIN", true);
        importCertParser.addArgument(importCertPromptForPKPassword);
        final BooleanArgument importCertNoPrompt = new BooleanArgument(null, "no-prompt", 1, CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_NO_PROMPT_DESC.get());
        importCertNoPrompt.addLongIdentifier("noPrompt", true);
        importCertParser.addArgument(importCertNoPrompt);
        final BooleanArgument importCertDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_ARG_DISPLAY_COMMAND_DESC.get());
        importCertDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        importCertDisplayCommand.addLongIdentifier("show-keytool-command", true);
        importCertDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        importCertParser.addArgument(importCertDisplayCommand);
        importCertParser.addRequiredArgumentSet(importCertKeystorePassword, importCertKeystorePasswordFile, importCertPromptForKeystorePassword);
        importCertParser.addExclusiveArgumentSet(importCertKeystorePassword, importCertKeystorePasswordFile, importCertPromptForKeystorePassword);
        importCertParser.addExclusiveArgumentSet(importCertPKPassword, importCertPKPasswordFile, importCertPromptForPKPassword);
        final LinkedHashMap<String[], String> importCertExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        importCertExamples.put(new String[] { "import-certificate", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--certificate-file", "server-cert.crt" }, CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_EXAMPLE_1.get("server-cert.crt"));
        importCertExamples.put(new String[] { "import-certificate", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--certificate-file", "server-cert.crt", "--certificate-file", "server-cert-issuer.crt", "--private-key-file", "server-cert.key", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_EXAMPLE_2.get());
        final SubCommand importCertSubCommand = new SubCommand("import-certificate", CertMessages.INFO_MANAGE_CERTS_SC_IMPORT_CERT_DESC.get(), importCertParser, importCertExamples);
        importCertSubCommand.addName("importCertificate", true);
        importCertSubCommand.addName("import-certificates", true);
        importCertSubCommand.addName("importCertificates", true);
        importCertSubCommand.addName("import-cert", true);
        importCertSubCommand.addName("importCert", true);
        importCertSubCommand.addName("import-certs", true);
        importCertSubCommand.addName("importCerts", true);
        importCertSubCommand.addName("import-certificate-chain", true);
        importCertSubCommand.addName("importCertificateChain", true);
        importCertSubCommand.addName("import-chain", true);
        importCertSubCommand.addName("importChain", true);
        importCertSubCommand.addName("import", false);
        parser.addSubCommand(importCertSubCommand);
        final ArgumentParser deleteCertParser = new ArgumentParser("delete-certificate", CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_DESC.get());
        final FileArgument deleteCertKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_ARG_KS_DESC.get(), true, true, true, false);
        deleteCertKeystore.addLongIdentifier("keystore-path", true);
        deleteCertKeystore.addLongIdentifier("keystorePath", true);
        deleteCertKeystore.addLongIdentifier("keystore-file", true);
        deleteCertKeystore.addLongIdentifier("keystoreFile", true);
        deleteCertParser.addArgument(deleteCertKeystore);
        final StringArgument deleteCertKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_ARG_KS_PW_DESC.get());
        deleteCertKeystorePassword.addLongIdentifier("keystorePassword", true);
        deleteCertKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        deleteCertKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        deleteCertKeystorePassword.addLongIdentifier("keystore-pin", true);
        deleteCertKeystorePassword.addLongIdentifier("keystorePIN", true);
        deleteCertKeystorePassword.addLongIdentifier("storepass", true);
        deleteCertKeystorePassword.setSensitive(true);
        deleteCertParser.addArgument(deleteCertKeystorePassword);
        final FileArgument deleteCertKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        deleteCertKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        deleteCertKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        deleteCertKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        deleteCertKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        deleteCertKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        deleteCertParser.addArgument(deleteCertKeystorePasswordFile);
        final BooleanArgument deleteCertPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_ARG_PROMPT_FOR_KS_PW_DESC.get());
        deleteCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        deleteCertPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        deleteCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        deleteCertPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        deleteCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        deleteCertParser.addArgument(deleteCertPromptForKeystorePassword);
        final StringArgument deleteCertAlias = new StringArgument(null, "alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_ARG_ALIAS_DESC.get());
        deleteCertAlias.addLongIdentifier("nickname", true);
        deleteCertParser.addArgument(deleteCertAlias);
        final BooleanArgument deleteCertNoPrompt = new BooleanArgument(null, "no-prompt", 1, CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_ARG_NO_PROMPT_DESC.get());
        deleteCertNoPrompt.addLongIdentifier("noPrompt", true);
        deleteCertParser.addArgument(deleteCertNoPrompt);
        final BooleanArgument deleteCertDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_ARG_DISPLAY_COMMAND_DESC.get());
        deleteCertDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        deleteCertDisplayCommand.addLongIdentifier("show-keytool-command", true);
        deleteCertDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        deleteCertParser.addArgument(deleteCertDisplayCommand);
        deleteCertParser.addExclusiveArgumentSet(deleteCertKeystorePassword, deleteCertKeystorePasswordFile, deleteCertPromptForKeystorePassword);
        deleteCertParser.addRequiredArgumentSet(deleteCertKeystorePassword, deleteCertKeystorePasswordFile, deleteCertPromptForKeystorePassword);
        final LinkedHashMap<String[], String> deleteCertExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        deleteCertExamples.put(new String[] { "delete-certificate", "--keystore", getPlatformSpecificPath("config", "keystore"), "--alias", "server-cert" }, CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_EXAMPLE_1.get(getPlatformSpecificPath("config", "keystore")));
        final SubCommand deleteCertSubCommand = new SubCommand("delete-certificate", CertMessages.INFO_MANAGE_CERTS_SC_DELETE_CERT_DESC.get(), deleteCertParser, deleteCertExamples);
        deleteCertSubCommand.addName("deleteCertificate", true);
        deleteCertSubCommand.addName("remove-certificate", false);
        deleteCertSubCommand.addName("removeCertificate", true);
        deleteCertSubCommand.addName("delete", false);
        deleteCertSubCommand.addName("remove", false);
        parser.addSubCommand(deleteCertSubCommand);
        final ArgumentParser genCertParser = new ArgumentParser("generate-self-signed-certificate", CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_DESC.get());
        final FileArgument genCertKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_KS_DESC.get(), false, true, true, false);
        genCertKeystore.addLongIdentifier("keystore-path", true);
        genCertKeystore.addLongIdentifier("keystorePath", true);
        genCertKeystore.addLongIdentifier("keystore-file", true);
        genCertKeystore.addLongIdentifier("keystoreFile", true);
        genCertParser.addArgument(genCertKeystore);
        final StringArgument genCertKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_KS_PW_DESC.get());
        genCertKeystorePassword.addLongIdentifier("keystorePassword", true);
        genCertKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        genCertKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        genCertKeystorePassword.addLongIdentifier("keystore-pin", true);
        genCertKeystorePassword.addLongIdentifier("keystorePIN", true);
        genCertKeystorePassword.addLongIdentifier("storepass", true);
        genCertKeystorePassword.setSensitive(true);
        genCertParser.addArgument(genCertKeystorePassword);
        final FileArgument genCertKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        genCertKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        genCertKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        genCertKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        genCertKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        genCertKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        genCertParser.addArgument(genCertKeystorePasswordFile);
        final BooleanArgument genCertPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_PROMPT_FOR_KS_PW_DESC.get());
        genCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        genCertPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        genCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        genCertPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        genCertPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        genCertParser.addArgument(genCertPromptForKeystorePassword);
        final StringArgument genCertPKPassword = new StringArgument(null, "private-key-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_PK_PW_DESC.get());
        genCertPKPassword.addLongIdentifier("privateKeyPassword", true);
        genCertPKPassword.addLongIdentifier("private-key-passphrase", true);
        genCertPKPassword.addLongIdentifier("privateKeyPassphrase", true);
        genCertPKPassword.addLongIdentifier("private-key-pin", true);
        genCertPKPassword.addLongIdentifier("privateKeyPIN", true);
        genCertPKPassword.addLongIdentifier("key-password", true);
        genCertPKPassword.addLongIdentifier("keyPassword", true);
        genCertPKPassword.addLongIdentifier("key-passphrase", true);
        genCertPKPassword.addLongIdentifier("keyPassphrase", true);
        genCertPKPassword.addLongIdentifier("key-pin", true);
        genCertPKPassword.addLongIdentifier("keyPIN", true);
        genCertPKPassword.addLongIdentifier("keypass", true);
        genCertPKPassword.setSensitive(true);
        genCertParser.addArgument(genCertPKPassword);
        final FileArgument genCertPKPasswordFile = new FileArgument(null, "private-key-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_PK_PW_FILE_DESC.get(), true, true, true, false);
        genCertPKPasswordFile.addLongIdentifier("privateKeyPasswordFile", true);
        genCertPKPasswordFile.addLongIdentifier("private-key-passphrase-file", true);
        genCertPKPasswordFile.addLongIdentifier("privateKeyPassphraseFile", true);
        genCertPKPasswordFile.addLongIdentifier("private-key-pin-file", true);
        genCertPKPasswordFile.addLongIdentifier("privateKeyPINFile", true);
        genCertPKPasswordFile.addLongIdentifier("key-password-file", true);
        genCertPKPasswordFile.addLongIdentifier("keyPasswordFile", true);
        genCertPKPasswordFile.addLongIdentifier("key-passphrase-file", true);
        genCertPKPasswordFile.addLongIdentifier("keyPassphraseFile", true);
        genCertPKPasswordFile.addLongIdentifier("key-pin-file", true);
        genCertPKPasswordFile.addLongIdentifier("keyPINFile", true);
        genCertParser.addArgument(genCertPKPasswordFile);
        final BooleanArgument genCertPromptForPKPassword = new BooleanArgument(null, "prompt-for-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_PROMPT_FOR_PK_PW_DESC.get());
        genCertPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassword", true);
        genCertPromptForPKPassword.addLongIdentifier("prompt-for-private-key-passphrase", true);
        genCertPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassphrase", true);
        genCertPromptForPKPassword.addLongIdentifier("prompt-for-private-key-pin", true);
        genCertPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPIN", true);
        genCertPromptForPKPassword.addLongIdentifier("prompt-for-key-password", true);
        genCertPromptForPKPassword.addLongIdentifier("promptForKeyPassword", true);
        genCertPromptForPKPassword.addLongIdentifier("prompt-for-key-passphrase", true);
        genCertPromptForPKPassword.addLongIdentifier("promptForKeyPassphrase", true);
        genCertPromptForPKPassword.addLongIdentifier("prompt-for-key-pin", true);
        genCertPromptForPKPassword.addLongIdentifier("promptForKeyPIN", true);
        genCertParser.addArgument(genCertPromptForPKPassword);
        final Set<String> genCertKeystoreTypeAllowedValues = StaticUtils.setOf("jks", "pkcs12", "pkcs 12", "pkcs#12", "pkcs #12");
        final StringArgument genCertKeystoreType = new StringArgument(null, "keystore-type", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_TYPE.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_KS_TYPE_DESC.get(), genCertKeystoreTypeAllowedValues);
        genCertKeystoreType.addLongIdentifier("keystoreType", true);
        genCertKeystoreType.addLongIdentifier("storetype", true);
        genCertParser.addArgument(genCertKeystoreType);
        final StringArgument genCertAlias = new StringArgument(null, "alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_ALIAS_DESC.get());
        genCertAlias.addLongIdentifier("nickname", true);
        genCertParser.addArgument(genCertAlias);
        final BooleanArgument genCertReplace = new BooleanArgument(null, "replace-existing-certificate", 1, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_REPLACE_DESC.get());
        genCertReplace.addLongIdentifier("replaceExistingCertificate", true);
        genCertReplace.addLongIdentifier("replace-certificate", true);
        genCertReplace.addLongIdentifier("replaceCertificate", true);
        genCertReplace.addLongIdentifier("replace-existing", true);
        genCertReplace.addLongIdentifier("replaceExisting", true);
        genCertReplace.addLongIdentifier("replace", true);
        genCertReplace.addLongIdentifier("use-existing-key-pair", true);
        genCertReplace.addLongIdentifier("use-existing-keypair", true);
        genCertReplace.addLongIdentifier("useExistingKeypair", true);
        genCertParser.addArgument(genCertReplace);
        final DNArgument genCertSubjectDN = new DNArgument(null, "subject-dn", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_SUBJECT_DN_DESC.get());
        genCertSubjectDN.addLongIdentifier("subjectDN", true);
        genCertSubjectDN.addLongIdentifier("subject", true);
        genCertSubjectDN.addLongIdentifier("dname", true);
        genCertParser.addArgument(genCertSubjectDN);
        final IntegerArgument genCertDaysValid = new IntegerArgument(null, "days-valid", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_DAYS_VALID_DESC.get(), 1, Integer.MAX_VALUE);
        genCertDaysValid.addLongIdentifier("daysValid", true);
        genCertDaysValid.addLongIdentifier("validity", true);
        genCertParser.addArgument(genCertDaysValid);
        final TimestampArgument genCertNotBefore = new TimestampArgument(null, "validity-start-time", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_TIMESTAMP.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_VALIDITY_START_TIME_DESC.get("20180102123456"));
        genCertNotBefore.addLongIdentifier("validityStartTime", true);
        genCertNotBefore.addLongIdentifier("not-before", true);
        genCertNotBefore.addLongIdentifier("notBefore", true);
        genCertParser.addArgument(genCertNotBefore);
        final StringArgument genCertKeyAlgorithm = new StringArgument(null, "key-algorithm", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_KEY_ALGORITHM_DESC.get());
        genCertKeyAlgorithm.addLongIdentifier("keyAlgorithm", true);
        genCertKeyAlgorithm.addLongIdentifier("key-alg", true);
        genCertKeyAlgorithm.addLongIdentifier("keyAlg", true);
        genCertParser.addArgument(genCertKeyAlgorithm);
        final IntegerArgument genCertKeySizeBits = new IntegerArgument(null, "key-size-bits", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_BITS.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_KEY_SIZE_BITS_DESC.get(), 1, Integer.MAX_VALUE);
        genCertKeySizeBits.addLongIdentifier("keySizeBits", true);
        genCertKeySizeBits.addLongIdentifier("key-length-bits", true);
        genCertKeySizeBits.addLongIdentifier("keyLengthBits", true);
        genCertKeySizeBits.addLongIdentifier("key-size", true);
        genCertKeySizeBits.addLongIdentifier("keySize", true);
        genCertKeySizeBits.addLongIdentifier("key-length", true);
        genCertKeySizeBits.addLongIdentifier("keyLength", true);
        genCertParser.addArgument(genCertKeySizeBits);
        final StringArgument genCertSignatureAlgorithm = new StringArgument(null, "signature-algorithm", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_SIG_ALG_DESC.get());
        genCertSignatureAlgorithm.addLongIdentifier("signatureAlgorithm", true);
        genCertSignatureAlgorithm.addLongIdentifier("signature-alg", true);
        genCertSignatureAlgorithm.addLongIdentifier("signatureAlg", true);
        genCertSignatureAlgorithm.addLongIdentifier("sig-alg", true);
        genCertSignatureAlgorithm.addLongIdentifier("sigAlg", true);
        genCertParser.addArgument(genCertSignatureAlgorithm);
        final BooleanArgument genCertInheritExtensions = new BooleanArgument(null, "inherit-extensions", 1, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_INHERIT_EXT_DESC.get());
        genCertInheritExtensions.addLongIdentifier("inheritExtensions", true);
        genCertParser.addArgument(genCertInheritExtensions);
        final StringArgument genCertSubjectAltDNS = new StringArgument(null, "subject-alternative-name-dns", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_SAN_DNS_DESC.get());
        genCertSubjectAltDNS.addLongIdentifier("subjectAlternativeNameDNS", true);
        genCertSubjectAltDNS.addLongIdentifier("subject-alt-name-dns", true);
        genCertSubjectAltDNS.addLongIdentifier("subjectAltNameDNS", true);
        genCertSubjectAltDNS.addLongIdentifier("subject-alternative-dns", true);
        genCertSubjectAltDNS.addLongIdentifier("subjectAlternativeDNS", true);
        genCertSubjectAltDNS.addLongIdentifier("subject-alt-dns", true);
        genCertSubjectAltDNS.addLongIdentifier("subjectAltDNS", true);
        genCertSubjectAltDNS.addLongIdentifier("san-dns", true);
        genCertSubjectAltDNS.addLongIdentifier("sanDNS", true);
        genCertParser.addArgument(genCertSubjectAltDNS);
        final StringArgument genCertSubjectAltIP = new StringArgument(null, "subject-alternative-name-ip-address", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_SAN_IP_DESC.get());
        genCertSubjectAltIP.addLongIdentifier("subjectAlternativeNameIPAddress", true);
        genCertSubjectAltIP.addLongIdentifier("subject-alternative-name-ip", true);
        genCertSubjectAltIP.addLongIdentifier("subjectAlternativeNameIP", true);
        genCertSubjectAltIP.addLongIdentifier("subject-alt-name-ip-address", true);
        genCertSubjectAltIP.addLongIdentifier("subjectAltNameIPAddress", true);
        genCertSubjectAltIP.addLongIdentifier("subject-alt-name-ip", true);
        genCertSubjectAltIP.addLongIdentifier("subjectAltNameIP", true);
        genCertSubjectAltIP.addLongIdentifier("subject-alternative-ip-address", true);
        genCertSubjectAltIP.addLongIdentifier("subjectAlternativeIPAddress", true);
        genCertSubjectAltIP.addLongIdentifier("subject-alternative-ip", true);
        genCertSubjectAltIP.addLongIdentifier("subjectAlternativeIP", true);
        genCertSubjectAltIP.addLongIdentifier("subject-alt-ip-address", true);
        genCertSubjectAltIP.addLongIdentifier("subjectAltIPAddress", true);
        genCertSubjectAltIP.addLongIdentifier("subject-alt-ip", true);
        genCertSubjectAltIP.addLongIdentifier("subjectAltIP", true);
        genCertSubjectAltIP.addLongIdentifier("san-ip-address", true);
        genCertSubjectAltIP.addLongIdentifier("sanIPAddress", true);
        genCertSubjectAltIP.addLongIdentifier("san-ip", true);
        genCertSubjectAltIP.addLongIdentifier("sanIP", true);
        genCertSubjectAltIP.addValueValidator(new IPAddressArgumentValueValidator(true, true));
        genCertParser.addArgument(genCertSubjectAltIP);
        final StringArgument genCertSubjectAltEmail = new StringArgument(null, "subject-alternative-name-email-address", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_SAN_EMAIL_DESC.get());
        genCertSubjectAltEmail.addLongIdentifier("subjectAlternativeNameEmailAddress", true);
        genCertSubjectAltEmail.addLongIdentifier("subject-alternative-name-email", true);
        genCertSubjectAltEmail.addLongIdentifier("subjectAlternativeNameEmail", true);
        genCertSubjectAltEmail.addLongIdentifier("subject-alt-name-email-address", true);
        genCertSubjectAltEmail.addLongIdentifier("subjectAltNameEmailAddress", true);
        genCertSubjectAltEmail.addLongIdentifier("subject-alt-name-email", true);
        genCertSubjectAltEmail.addLongIdentifier("subjectAltNameEmail", true);
        genCertSubjectAltEmail.addLongIdentifier("subject-alternative-email-address", true);
        genCertSubjectAltEmail.addLongIdentifier("subjectAlternativeEmailAddress", true);
        genCertSubjectAltEmail.addLongIdentifier("subject-alternative-email", true);
        genCertSubjectAltEmail.addLongIdentifier("subjectAlternativeEmail", true);
        genCertSubjectAltEmail.addLongIdentifier("subject-alt-email-address", true);
        genCertSubjectAltEmail.addLongIdentifier("subjectAltEmailAddress", true);
        genCertSubjectAltEmail.addLongIdentifier("subject-alt-email", true);
        genCertSubjectAltEmail.addLongIdentifier("subjectAltEmail", true);
        genCertSubjectAltEmail.addLongIdentifier("san-email-address", true);
        genCertSubjectAltEmail.addLongIdentifier("sanEmailAddress", true);
        genCertSubjectAltEmail.addLongIdentifier("san-email", true);
        genCertSubjectAltEmail.addLongIdentifier("sanEmail", true);
        genCertParser.addArgument(genCertSubjectAltEmail);
        final StringArgument genCertSubjectAltURI = new StringArgument(null, "subject-alternative-name-uri", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_URI.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_SAN_URI_DESC.get());
        genCertSubjectAltURI.addLongIdentifier("subjectAlternativeNameURI", true);
        genCertSubjectAltURI.addLongIdentifier("subject-alt-name-uri", true);
        genCertSubjectAltURI.addLongIdentifier("subjectAltNameURI", true);
        genCertSubjectAltURI.addLongIdentifier("subject-alternative-uri", true);
        genCertSubjectAltURI.addLongIdentifier("subjectAlternativeURI", true);
        genCertSubjectAltURI.addLongIdentifier("subject-alt-uri", true);
        genCertSubjectAltURI.addLongIdentifier("subjectAltURI", true);
        genCertSubjectAltURI.addLongIdentifier("san-uri", true);
        genCertSubjectAltURI.addLongIdentifier("sanURI", true);
        genCertParser.addArgument(genCertSubjectAltURI);
        final StringArgument genCertSubjectAltOID = new StringArgument(null, "subject-alternative-name-oid", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_OID.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_SAN_OID_DESC.get());
        genCertSubjectAltOID.addLongIdentifier("subjectAlternativeNameOID", true);
        genCertSubjectAltOID.addLongIdentifier("subject-alt-name-oid", true);
        genCertSubjectAltOID.addLongIdentifier("subjectAltNameOID", true);
        genCertSubjectAltOID.addLongIdentifier("subject-alternative-oid", true);
        genCertSubjectAltOID.addLongIdentifier("subjectAlternativeOID", true);
        genCertSubjectAltOID.addLongIdentifier("subject-alt-oid", true);
        genCertSubjectAltOID.addLongIdentifier("subjectAltOID", true);
        genCertSubjectAltOID.addLongIdentifier("san-oid", true);
        genCertSubjectAltOID.addLongIdentifier("sanOID", true);
        genCertSubjectAltOID.addValueValidator(new OIDArgumentValueValidator(true));
        genCertParser.addArgument(genCertSubjectAltOID);
        final BooleanValueArgument genCertBasicConstraintsIsCA = new BooleanValueArgument(null, "basic-constraints-is-ca", false, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_BC_IS_CA_DESC.get());
        genCertBasicConstraintsIsCA.addLongIdentifier("basicConstraintsIsCA", true);
        genCertBasicConstraintsIsCA.addLongIdentifier("bc-is-ca", true);
        genCertBasicConstraintsIsCA.addLongIdentifier("bcIsCA", true);
        genCertParser.addArgument(genCertBasicConstraintsIsCA);
        final IntegerArgument genCertBasicConstraintsPathLength = new IntegerArgument(null, "basic-constraints-maximum-path-length", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_BC_PATH_LENGTH_DESC.get(), 0, Integer.MAX_VALUE);
        genCertBasicConstraintsPathLength.addLongIdentifier("basicConstraintsMaximumPathLength", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("basic-constraints-max-path-length", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("basicConstraintsMaxPathLength", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("basic-constraints-path-length", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("basicConstraintsPathLength", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("bc-maximum-path-length", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("bcMaximumPathLength", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("bc-max-path-length", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("bcMaxPathLength", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("bc-path-length", true);
        genCertBasicConstraintsPathLength.addLongIdentifier("bcPathLength", true);
        genCertParser.addArgument(genCertBasicConstraintsPathLength);
        final StringArgument genCertKeyUsage = new StringArgument(null, "key-usage", false, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_KU_DESC.get());
        genCertKeyUsage.addLongIdentifier("keyUsage", true);
        genCertParser.addArgument(genCertKeyUsage);
        final StringArgument genCertExtendedKeyUsage = new StringArgument(null, "extended-key-usage", false, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_EKU_DESC.get());
        genCertExtendedKeyUsage.addLongIdentifier("extendedKeyUsage", true);
        genCertParser.addArgument(genCertExtendedKeyUsage);
        final StringArgument genCertExtension = new StringArgument(null, "extension", false, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_EXT_DESC.get());
        genCertExtension.addLongIdentifier("ext", true);
        genCertParser.addArgument(genCertExtension);
        final BooleanArgument genCertDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_DISPLAY_COMMAND_DESC.get());
        genCertDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        genCertDisplayCommand.addLongIdentifier("show-keytool-command", true);
        genCertDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        genCertParser.addArgument(genCertDisplayCommand);
        genCertParser.addRequiredArgumentSet(genCertKeystorePassword, genCertKeystorePasswordFile, genCertPromptForKeystorePassword);
        genCertParser.addExclusiveArgumentSet(genCertKeystorePassword, genCertKeystorePasswordFile, genCertPromptForKeystorePassword);
        genCertParser.addExclusiveArgumentSet(genCertPKPassword, genCertPKPasswordFile, genCertPromptForPKPassword);
        genCertParser.addExclusiveArgumentSet(genCertReplace, genCertKeyAlgorithm, new Argument[0]);
        genCertParser.addExclusiveArgumentSet(genCertReplace, genCertKeySizeBits, new Argument[0]);
        genCertParser.addExclusiveArgumentSet(genCertReplace, genCertSignatureAlgorithm, new Argument[0]);
        genCertParser.addDependentArgumentSet(genCertBasicConstraintsPathLength, genCertBasicConstraintsIsCA, new Argument[0]);
        final LinkedHashMap<String[], String> genCertExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(4));
        genCertExamples.put(new String[] { "generate-self-signed-certificate", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--subject-dn", "CN=ldap.example.com,O=Example Corp,C=US" }, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_EXAMPLE_1.get());
        genCertExamples.put(new String[] { "generate-self-signed-certificate", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--replace-existing-certificate", "--inherit-extensions" }, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_EXAMPLE_2.get());
        genCertExamples.put(new String[] { "generate-self-signed-certificate", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--subject-dn", "CN=ldap.example.com,O=Example Corp,C=US", "--days-valid", "3650", "--validity-start-time", "20170101000000", "--key-algorithm", "RSA", "--key-size-bits", "4096", "--signature-algorithm", "SHA256withRSA", "--subject-alternative-name-dns", "ldap1.example.com", "--subject-alternative-name-dns", "ldap2.example.com", "--subject-alternative-name-ip-address", "1.2.3.4", "--subject-alternative-name-ip-address", "1.2.3.5", "--extended-key-usage", "server-auth", "--extended-key-usage", "client-auth", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_EXAMPLE_3.get());
        genCertExamples.put(new String[] { "generate-self-signed-certificate", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "ca-cert", "--subject-dn", "CN=Example Certification Authority,O=Example Corp,C=US", "--days-valid", "7300", "--validity-start-time", "20170101000000", "--key-algorithm", "EC", "--key-size-bits", "256", "--signature-algorithm", "SHA256withECDSA", "--basic-constraints-is-ca", "true", "--key-usage", "key-cert-sign", "--key-usage", "crl-sign", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_EXAMPLE_4.get());
        final SubCommand genCertSubCommand = new SubCommand("generate-self-signed-certificate", CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_DESC.get(), genCertParser, genCertExamples);
        genCertSubCommand.addName("generateSelfSignedCertificate", true);
        genCertSubCommand.addName("generate-certificate", false);
        genCertSubCommand.addName("generateCertificate", true);
        genCertSubCommand.addName("self-signed-certificate", true);
        genCertSubCommand.addName("selfSignedCertificate", true);
        genCertSubCommand.addName("selfcert", true);
        parser.addSubCommand(genCertSubCommand);
        final ArgumentParser genCSRParser = new ArgumentParser("generate-certificate-signing-request", CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_DESC.get());
        final Set<String> genCSROutputFormatAllowedValues = StaticUtils.setOf("PEM", "text", "txt", "RFC", "DER", "binary", "bin");
        final StringArgument genCSROutputFormat = new StringArgument(null, "output-format", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_FORMAT.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_FORMAT_DESC.get(), genCSROutputFormatAllowedValues, "PEM");
        genCSROutputFormat.addLongIdentifier("outputFormat");
        genCSRParser.addArgument(genCSROutputFormat);
        final FileArgument genCSROutputFile = new FileArgument(null, "output-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_OUTPUT_FILE_DESC.get(), false, true, true, false);
        genCSROutputFile.addLongIdentifier("outputFile", true);
        genCSROutputFile.addLongIdentifier("filename", true);
        genCSROutputFile.addLongIdentifier("file", true);
        genCSRParser.addArgument(genCSROutputFile);
        final FileArgument genCSRKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_KS_DESC.get(), false, true, true, false);
        genCSRKeystore.addLongIdentifier("keystore-path", true);
        genCSRKeystore.addLongIdentifier("keystorePath", true);
        genCSRKeystore.addLongIdentifier("keystore-file", true);
        genCSRKeystore.addLongIdentifier("keystoreFile", true);
        genCSRParser.addArgument(genCSRKeystore);
        final StringArgument genCSRKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_KS_PW_DESC.get());
        genCSRKeystorePassword.addLongIdentifier("keystorePassword", true);
        genCSRKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        genCSRKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        genCSRKeystorePassword.addLongIdentifier("keystore-pin", true);
        genCSRKeystorePassword.addLongIdentifier("keystorePIN", true);
        genCSRKeystorePassword.addLongIdentifier("storepass", true);
        genCSRKeystorePassword.setSensitive(true);
        genCSRParser.addArgument(genCSRKeystorePassword);
        final FileArgument genCSRKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        genCSRKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        genCSRKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        genCSRKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        genCSRKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        genCSRKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        genCSRParser.addArgument(genCSRKeystorePasswordFile);
        final BooleanArgument genCSRPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_PROMPT_FOR_KS_PW_DESC.get());
        genCSRPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        genCSRPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        genCSRPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        genCSRPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        genCSRPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        genCSRParser.addArgument(genCSRPromptForKeystorePassword);
        final StringArgument genCSRPKPassword = new StringArgument(null, "private-key-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_PK_PW_DESC.get());
        genCSRPKPassword.addLongIdentifier("privateKeyPassword", true);
        genCSRPKPassword.addLongIdentifier("private-key-passphrase", true);
        genCSRPKPassword.addLongIdentifier("privateKeyPassphrase", true);
        genCSRPKPassword.addLongIdentifier("private-key-pin", true);
        genCSRPKPassword.addLongIdentifier("privateKeyPIN", true);
        genCSRPKPassword.addLongIdentifier("key-password", true);
        genCSRPKPassword.addLongIdentifier("keyPassword", true);
        genCSRPKPassword.addLongIdentifier("key-passphrase", true);
        genCSRPKPassword.addLongIdentifier("keyPassphrase", true);
        genCSRPKPassword.addLongIdentifier("key-pin", true);
        genCSRPKPassword.addLongIdentifier("keyPIN", true);
        genCSRPKPassword.addLongIdentifier("keypass", true);
        genCSRPKPassword.setSensitive(true);
        genCSRParser.addArgument(genCSRPKPassword);
        final FileArgument genCSRPKPasswordFile = new FileArgument(null, "private-key-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_PK_PW_FILE_DESC.get(), true, true, true, false);
        genCSRPKPasswordFile.addLongIdentifier("privateKeyPasswordFile", true);
        genCSRPKPasswordFile.addLongIdentifier("private-key-passphrase-file", true);
        genCSRPKPasswordFile.addLongIdentifier("privateKeyPassphraseFile", true);
        genCSRPKPasswordFile.addLongIdentifier("private-key-pin-file", true);
        genCSRPKPasswordFile.addLongIdentifier("privateKeyPINFile", true);
        genCSRPKPasswordFile.addLongIdentifier("key-password-file", true);
        genCSRPKPasswordFile.addLongIdentifier("keyPasswordFile", true);
        genCSRPKPasswordFile.addLongIdentifier("key-passphrase-file", true);
        genCSRPKPasswordFile.addLongIdentifier("keyPassphraseFile", true);
        genCSRPKPasswordFile.addLongIdentifier("key-pin-file", true);
        genCSRPKPasswordFile.addLongIdentifier("keyPINFile", true);
        genCSRParser.addArgument(genCSRPKPasswordFile);
        final BooleanArgument genCSRPromptForPKPassword = new BooleanArgument(null, "prompt-for-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_PROMPT_FOR_PK_PW_DESC.get());
        genCSRPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassword", true);
        genCSRPromptForPKPassword.addLongIdentifier("prompt-for-private-key-passphrase", true);
        genCSRPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassphrase", true);
        genCSRPromptForPKPassword.addLongIdentifier("prompt-for-private-key-pin", true);
        genCSRPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPIN", true);
        genCSRPromptForPKPassword.addLongIdentifier("prompt-for-key-password", true);
        genCSRPromptForPKPassword.addLongIdentifier("promptForKeyPassword", true);
        genCSRPromptForPKPassword.addLongIdentifier("prompt-for-key-passphrase", true);
        genCSRPromptForPKPassword.addLongIdentifier("promptForKeyPassphrase", true);
        genCSRPromptForPKPassword.addLongIdentifier("prompt-for-key-pin", true);
        genCSRPromptForPKPassword.addLongIdentifier("promptForKeyPIN", true);
        genCSRParser.addArgument(genCSRPromptForPKPassword);
        final Set<String> genCSRKeystoreTypeAllowedValues = StaticUtils.setOf("jks", "pkcs12", "pkcs 12", "pkcs#12", "pkcs #12");
        final StringArgument genCSRKeystoreType = new StringArgument(null, "keystore-type", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_TYPE.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_KS_TYPE_DESC.get(), genCSRKeystoreTypeAllowedValues);
        genCSRKeystoreType.addLongIdentifier("keystoreType", true);
        genCSRKeystoreType.addLongIdentifier("storetype", true);
        genCSRParser.addArgument(genCSRKeystoreType);
        final StringArgument genCSRAlias = new StringArgument(null, "alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_ALIAS_DESC.get());
        genCSRAlias.addLongIdentifier("nickname", true);
        genCSRParser.addArgument(genCSRAlias);
        final BooleanArgument genCSRReplace = new BooleanArgument(null, "use-existing-key-pair", 1, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_REPLACE_DESC.get());
        genCSRReplace.addLongIdentifier("use-existing-keypair", true);
        genCSRReplace.addLongIdentifier("useExistingKeyPair", true);
        genCSRReplace.addLongIdentifier("replace-existing-certificate", true);
        genCSRReplace.addLongIdentifier("replaceExistingCertificate", true);
        genCSRReplace.addLongIdentifier("replace-certificate", true);
        genCSRReplace.addLongIdentifier("replaceCertificate", true);
        genCSRReplace.addLongIdentifier("replace-existing", true);
        genCSRReplace.addLongIdentifier("replaceExisting", true);
        genCSRReplace.addLongIdentifier("replace", true);
        genCSRParser.addArgument(genCSRReplace);
        final DNArgument genCSRSubjectDN = new DNArgument(null, "subject-dn", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_SUBJECT_DN_DESC.get());
        genCSRSubjectDN.addLongIdentifier("subjectDN", true);
        genCSRSubjectDN.addLongIdentifier("subject", true);
        genCSRSubjectDN.addLongIdentifier("dname", true);
        genCSRParser.addArgument(genCSRSubjectDN);
        final StringArgument genCSRKeyAlgorithm = new StringArgument(null, "key-algorithm", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_KEY_ALGORITHM_DESC.get());
        genCSRKeyAlgorithm.addLongIdentifier("keyAlgorithm", true);
        genCSRKeyAlgorithm.addLongIdentifier("key-alg", true);
        genCSRKeyAlgorithm.addLongIdentifier("keyAlg", true);
        genCSRParser.addArgument(genCSRKeyAlgorithm);
        final IntegerArgument genCSRKeySizeBits = new IntegerArgument(null, "key-size-bits", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_BITS.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_KEY_ALGORITHM_DESC.get(), 1, Integer.MAX_VALUE);
        genCSRKeySizeBits.addLongIdentifier("keySizeBits", true);
        genCSRKeySizeBits.addLongIdentifier("key-length-bits", true);
        genCSRKeySizeBits.addLongIdentifier("keyLengthBits", true);
        genCSRKeySizeBits.addLongIdentifier("key-size", true);
        genCSRKeySizeBits.addLongIdentifier("keySize", true);
        genCSRKeySizeBits.addLongIdentifier("key-length", true);
        genCSRKeySizeBits.addLongIdentifier("keyLength", true);
        genCSRParser.addArgument(genCSRKeySizeBits);
        final StringArgument genCSRSignatureAlgorithm = new StringArgument(null, "signature-algorithm", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_SIG_ALG_DESC.get());
        genCSRSignatureAlgorithm.addLongIdentifier("signatureAlgorithm", true);
        genCSRSignatureAlgorithm.addLongIdentifier("signature-alg", true);
        genCSRSignatureAlgorithm.addLongIdentifier("signatureAlg", true);
        genCSRSignatureAlgorithm.addLongIdentifier("sig-alg", true);
        genCSRSignatureAlgorithm.addLongIdentifier("sigAlg", true);
        genCSRParser.addArgument(genCSRSignatureAlgorithm);
        final BooleanArgument genCSRInheritExtensions = new BooleanArgument(null, "inherit-extensions", 1, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_INHERIT_EXT_DESC.get());
        genCSRInheritExtensions.addLongIdentifier("inheritExtensions", true);
        genCSRParser.addArgument(genCSRInheritExtensions);
        final StringArgument genCSRSubjectAltDNS = new StringArgument(null, "subject-alternative-name-dns", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_SAN_DNS_DESC.get());
        genCSRSubjectAltDNS.addLongIdentifier("subjectAlternativeNameDNS", true);
        genCSRSubjectAltDNS.addLongIdentifier("subject-alt-name-dns", true);
        genCSRSubjectAltDNS.addLongIdentifier("subjectAltNameDNS", true);
        genCSRSubjectAltDNS.addLongIdentifier("subject-alternative-dns", true);
        genCSRSubjectAltDNS.addLongIdentifier("subjectAlternativeDNS", true);
        genCSRSubjectAltDNS.addLongIdentifier("subject-alt-dns", true);
        genCSRSubjectAltDNS.addLongIdentifier("subjectAltDNS", true);
        genCSRSubjectAltDNS.addLongIdentifier("san-dns", true);
        genCSRSubjectAltDNS.addLongIdentifier("sanDNS", true);
        genCSRParser.addArgument(genCSRSubjectAltDNS);
        final StringArgument genCSRSubjectAltIP = new StringArgument(null, "subject-alternative-name-ip-address", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_SAN_IP_DESC.get());
        genCSRSubjectAltIP.addLongIdentifier("subjectAlternativeNameIPAddress", true);
        genCSRSubjectAltIP.addLongIdentifier("subject-alternative-name-ip", true);
        genCSRSubjectAltIP.addLongIdentifier("subjectAlternativeNameIP", true);
        genCSRSubjectAltIP.addLongIdentifier("subject-alt-name-ip-address", true);
        genCSRSubjectAltIP.addLongIdentifier("subjectAltNameIPAddress", true);
        genCSRSubjectAltIP.addLongIdentifier("subject-alt-name-ip", true);
        genCSRSubjectAltIP.addLongIdentifier("subjectAltNameIP", true);
        genCSRSubjectAltIP.addLongIdentifier("subject-alternative-ip-address", true);
        genCSRSubjectAltIP.addLongIdentifier("subjectAlternativeIPAddress", true);
        genCSRSubjectAltIP.addLongIdentifier("subject-alternative-ip", true);
        genCSRSubjectAltIP.addLongIdentifier("subjectAlternativeIP", true);
        genCSRSubjectAltIP.addLongIdentifier("subject-alt-ip-address", true);
        genCSRSubjectAltIP.addLongIdentifier("subjectAltIPAddress", true);
        genCSRSubjectAltIP.addLongIdentifier("subject-alt-ip", true);
        genCSRSubjectAltIP.addLongIdentifier("subjectAltIP", true);
        genCSRSubjectAltIP.addLongIdentifier("san-ip-address", true);
        genCSRSubjectAltIP.addLongIdentifier("sanIPAddress", true);
        genCSRSubjectAltIP.addLongIdentifier("san-ip", true);
        genCSRSubjectAltIP.addLongIdentifier("sanIP", true);
        genCSRSubjectAltIP.addValueValidator(new IPAddressArgumentValueValidator(true, true));
        genCSRParser.addArgument(genCSRSubjectAltIP);
        final StringArgument genCSRSubjectAltEmail = new StringArgument(null, "subject-alternative-name-email-address", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_SAN_EMAIL_DESC.get());
        genCSRSubjectAltEmail.addLongIdentifier("subjectAlternativeNameEmailAddress", true);
        genCSRSubjectAltEmail.addLongIdentifier("subject-alternative-name-email", true);
        genCSRSubjectAltEmail.addLongIdentifier("subjectAlternativeNameEmail", true);
        genCSRSubjectAltEmail.addLongIdentifier("subject-alt-name-email-address", true);
        genCSRSubjectAltEmail.addLongIdentifier("subjectAltNameEmailAddress", true);
        genCSRSubjectAltEmail.addLongIdentifier("subject-alt-name-email", true);
        genCSRSubjectAltEmail.addLongIdentifier("subjectAltNameEmail", true);
        genCSRSubjectAltEmail.addLongIdentifier("subject-alternative-email-address", true);
        genCSRSubjectAltEmail.addLongIdentifier("subjectAlternativeEmailAddress", true);
        genCSRSubjectAltEmail.addLongIdentifier("subject-alternative-email", true);
        genCSRSubjectAltEmail.addLongIdentifier("subjectAlternativeEmail", true);
        genCSRSubjectAltEmail.addLongIdentifier("subject-alt-email-address", true);
        genCSRSubjectAltEmail.addLongIdentifier("subjectAltEmailAddress", true);
        genCSRSubjectAltEmail.addLongIdentifier("subject-alt-email", true);
        genCSRSubjectAltEmail.addLongIdentifier("subjectAltEmail", true);
        genCSRSubjectAltEmail.addLongIdentifier("san-email-address", true);
        genCSRSubjectAltEmail.addLongIdentifier("sanEmailAddress", true);
        genCSRSubjectAltEmail.addLongIdentifier("san-email", true);
        genCSRSubjectAltEmail.addLongIdentifier("sanEmail", true);
        genCSRParser.addArgument(genCSRSubjectAltEmail);
        final StringArgument genCSRSubjectAltURI = new StringArgument(null, "subject-alternative-name-uri", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_URI.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_SAN_URI_DESC.get());
        genCSRSubjectAltURI.addLongIdentifier("subjectAlternativeNameURI", true);
        genCSRSubjectAltURI.addLongIdentifier("subject-alt-name-uri", true);
        genCSRSubjectAltURI.addLongIdentifier("subjectAltNameURI", true);
        genCSRSubjectAltURI.addLongIdentifier("subject-alternative-uri", true);
        genCSRSubjectAltURI.addLongIdentifier("subjectAlternativeURI", true);
        genCSRSubjectAltURI.addLongIdentifier("subject-alt-uri", true);
        genCSRSubjectAltURI.addLongIdentifier("subjectAltURI", true);
        genCSRSubjectAltURI.addLongIdentifier("san-uri", true);
        genCSRSubjectAltURI.addLongIdentifier("sanURI", true);
        genCSRParser.addArgument(genCSRSubjectAltURI);
        final StringArgument genCSRSubjectAltOID = new StringArgument(null, "subject-alternative-name-oid", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_OID.get(), CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_SAN_OID_DESC.get());
        genCSRSubjectAltOID.addLongIdentifier("subjectAlternativeNameOID", true);
        genCSRSubjectAltOID.addLongIdentifier("subject-alt-name-oid", true);
        genCSRSubjectAltOID.addLongIdentifier("subjectAltNameOID", true);
        genCSRSubjectAltOID.addLongIdentifier("subject-alternative-oid", true);
        genCSRSubjectAltOID.addLongIdentifier("subjectAlternativeOID", true);
        genCSRSubjectAltOID.addLongIdentifier("subject-alt-oid", true);
        genCSRSubjectAltOID.addLongIdentifier("subjectAltOID", true);
        genCSRSubjectAltOID.addLongIdentifier("san-oid", true);
        genCSRSubjectAltOID.addLongIdentifier("sanOID", true);
        genCSRSubjectAltOID.addValueValidator(new OIDArgumentValueValidator(true));
        genCSRParser.addArgument(genCSRSubjectAltOID);
        final BooleanValueArgument genCSRBasicConstraintsIsCA = new BooleanValueArgument(null, "basic-constraints-is-ca", false, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_BC_IS_CA_DESC.get());
        genCSRBasicConstraintsIsCA.addLongIdentifier("basicConstraintsIsCA", true);
        genCSRBasicConstraintsIsCA.addLongIdentifier("bc-is-ca", true);
        genCSRBasicConstraintsIsCA.addLongIdentifier("bcIsCA", true);
        genCSRParser.addArgument(genCSRBasicConstraintsIsCA);
        final IntegerArgument genCSRBasicConstraintsPathLength = new IntegerArgument(null, "basic-constraints-maximum-path-length", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_BC_PATH_LENGTH_DESC.get(), 0, Integer.MAX_VALUE);
        genCSRBasicConstraintsPathLength.addLongIdentifier("basicConstraintsMaximumPathLength", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("basic-constraints-max-path-length", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("basicConstraintsMaxPathLength", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("basic-constraints-path-length", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("basicConstraintsPathLength", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("bc-maximum-path-length", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("bcMaximumPathLength", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("bc-max-path-length", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("bcMaxPathLength", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("bc-path-length", true);
        genCSRBasicConstraintsPathLength.addLongIdentifier("bcPathLength", true);
        genCSRParser.addArgument(genCSRBasicConstraintsPathLength);
        final StringArgument genCSRKeyUsage = new StringArgument(null, "key-usage", false, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_KU_DESC.get());
        genCSRKeyUsage.addLongIdentifier("keyUsage", true);
        genCSRParser.addArgument(genCSRKeyUsage);
        final StringArgument genCSRExtendedKeyUsage = new StringArgument(null, "extended-key-usage", false, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_EKU_DESC.get());
        genCSRExtendedKeyUsage.addLongIdentifier("extendedKeyUsage", true);
        genCSRParser.addArgument(genCSRExtendedKeyUsage);
        final StringArgument genCSRExtension = new StringArgument(null, "extension", false, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_EXT_DESC.get());
        genCSRExtension.addLongIdentifier("ext", true);
        genCSRParser.addArgument(genCSRExtension);
        final BooleanArgument genCSRDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_ARG_DISPLAY_COMMAND_DESC.get());
        genCSRDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        genCSRDisplayCommand.addLongIdentifier("show-keytool-command", true);
        genCSRDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        genCSRParser.addArgument(genCSRDisplayCommand);
        genCSRParser.addRequiredArgumentSet(genCSRKeystorePassword, genCSRKeystorePasswordFile, genCSRPromptForKeystorePassword);
        genCSRParser.addExclusiveArgumentSet(genCSRKeystorePassword, genCSRKeystorePasswordFile, genCSRPromptForKeystorePassword);
        genCSRParser.addExclusiveArgumentSet(genCSRPKPassword, genCSRPKPasswordFile, genCSRPromptForPKPassword);
        genCSRParser.addExclusiveArgumentSet(genCSRReplace, genCSRKeyAlgorithm, new Argument[0]);
        genCSRParser.addExclusiveArgumentSet(genCSRReplace, genCSRKeySizeBits, new Argument[0]);
        genCSRParser.addExclusiveArgumentSet(genCSRReplace, genCSRSignatureAlgorithm, new Argument[0]);
        genCSRParser.addDependentArgumentSet(genCSRBasicConstraintsPathLength, genCSRBasicConstraintsIsCA, new Argument[0]);
        final LinkedHashMap<String[], String> genCSRExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(3));
        genCSRExamples.put(new String[] { "generate-certificate-signing-request", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--subject-dn", "CN=ldap.example.com,O=Example Corp,C=US" }, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_EXAMPLE_1.get());
        genCSRExamples.put(new String[] { "generate-certificate-signing-request", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--use-existing-key-pair", "--inherit-extensions", "--output-file", "server-cert.csr" }, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_EXAMPLE_2.get());
        genCSRExamples.put(new String[] { "generate-certificate-signing-request", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--subject-dn", "CN=ldap.example.com,O=Example Corp,C=US", "--key-algorithm", "EC", "--key-size-bits", "256", "--signature-algorithm", "SHA256withECDSA", "--subject-alternative-name-dns", "ldap1.example.com", "--subject-alternative-name-dns", "ldap2.example.com", "--subject-alternative-name-ip-address", "1.2.3.4", "--subject-alternative-name-ip-address", "1.2.3.5", "--extended-key-usage", "server-auth", "--extended-key-usage", "client-auth", "--output-file", "server-cert.csr", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_EXAMPLE_3.get());
        final SubCommand genCSRSubCommand = new SubCommand("generate-certificate-signing-request", CertMessages.INFO_MANAGE_CERTS_SC_GEN_CSR_DESC.get(), genCSRParser, genCSRExamples);
        genCSRSubCommand.addName("generateCertificateSigningRequest", true);
        genCSRSubCommand.addName("generate-certificate-request", false);
        genCSRSubCommand.addName("generateCertificateRequest", true);
        genCSRSubCommand.addName("generate-csr", true);
        genCSRSubCommand.addName("generateCSR", true);
        genCSRSubCommand.addName("certificate-signing-request", true);
        genCSRSubCommand.addName("certificateSigningRequest", true);
        genCSRSubCommand.addName("csr", true);
        genCSRSubCommand.addName("certreq", true);
        parser.addSubCommand(genCSRSubCommand);
        final ArgumentParser signCSRParser = new ArgumentParser("sign-certificate-signing-request", CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_DESC.get());
        final FileArgument signCSRInputFile = new FileArgument(null, "request-input-file", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_INPUT_FILE_DESC.get(), true, true, true, false);
        signCSRInputFile.addLongIdentifier("requestInputFile", true);
        signCSRInputFile.addLongIdentifier("certificate-signing-request", true);
        signCSRInputFile.addLongIdentifier("certificateSigningRequest", true);
        signCSRInputFile.addLongIdentifier("input-file", false);
        signCSRInputFile.addLongIdentifier("inputFile", true);
        signCSRInputFile.addLongIdentifier("csr", true);
        signCSRParser.addArgument(signCSRInputFile);
        final FileArgument signCSROutputFile = new FileArgument(null, "certificate-output-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_OUTPUT_FILE_DESC.get(), false, true, true, false);
        signCSROutputFile.addLongIdentifier("certificateOutputFile", true);
        signCSROutputFile.addLongIdentifier("output-file", false);
        signCSROutputFile.addLongIdentifier("outputFile", true);
        signCSROutputFile.addLongIdentifier("certificate-file", true);
        signCSROutputFile.addLongIdentifier("certificateFile", true);
        signCSRParser.addArgument(signCSROutputFile);
        final Set<String> signCSROutputFormatAllowedValues = StaticUtils.setOf("PEM", "text", "txt", "RFC", "DER", "binary", "bin");
        final StringArgument signCSROutputFormat = new StringArgument(null, "output-format", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_FORMAT.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_FORMAT_DESC.get(), signCSROutputFormatAllowedValues, "PEM");
        signCSROutputFormat.addLongIdentifier("outputFormat");
        signCSRParser.addArgument(signCSROutputFormat);
        final FileArgument signCSRKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_KS_DESC.get(), true, true, true, false);
        signCSRKeystore.addLongIdentifier("keystore-path", true);
        signCSRKeystore.addLongIdentifier("keystorePath", true);
        signCSRKeystore.addLongIdentifier("keystore-file", true);
        signCSRKeystore.addLongIdentifier("keystoreFile", true);
        signCSRParser.addArgument(signCSRKeystore);
        final StringArgument signCSRKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_KS_PW_DESC.get());
        signCSRKeystorePassword.addLongIdentifier("keystorePassword", true);
        signCSRKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        signCSRKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        signCSRKeystorePassword.addLongIdentifier("keystore-pin", true);
        signCSRKeystorePassword.addLongIdentifier("keystorePIN", true);
        signCSRKeystorePassword.addLongIdentifier("storepass", true);
        signCSRKeystorePassword.setSensitive(true);
        signCSRParser.addArgument(signCSRKeystorePassword);
        final FileArgument signCSRKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        signCSRKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        signCSRKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        signCSRKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        signCSRKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        signCSRKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        signCSRParser.addArgument(signCSRKeystorePasswordFile);
        final BooleanArgument signCSRPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_PROMPT_FOR_KS_PW_DESC.get());
        signCSRPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        signCSRPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        signCSRPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        signCSRPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        signCSRPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        signCSRParser.addArgument(signCSRPromptForKeystorePassword);
        final StringArgument signCSRPKPassword = new StringArgument(null, "private-key-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_PK_PW_DESC.get());
        signCSRPKPassword.addLongIdentifier("privateKeyPassword", true);
        signCSRPKPassword.addLongIdentifier("private-key-passphrase", true);
        signCSRPKPassword.addLongIdentifier("privateKeyPassphrase", true);
        signCSRPKPassword.addLongIdentifier("private-key-pin", true);
        signCSRPKPassword.addLongIdentifier("privateKeyPIN", true);
        signCSRPKPassword.addLongIdentifier("key-password", true);
        signCSRPKPassword.addLongIdentifier("keyPassword", true);
        signCSRPKPassword.addLongIdentifier("key-passphrase", true);
        signCSRPKPassword.addLongIdentifier("keyPassphrase", true);
        signCSRPKPassword.addLongIdentifier("key-pin", true);
        signCSRPKPassword.addLongIdentifier("keyPIN", true);
        signCSRPKPassword.addLongIdentifier("keypass", true);
        signCSRPKPassword.setSensitive(true);
        signCSRParser.addArgument(signCSRPKPassword);
        final FileArgument signCSRPKPasswordFile = new FileArgument(null, "private-key-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_PK_PW_FILE_DESC.get(), true, true, true, false);
        signCSRPKPasswordFile.addLongIdentifier("privateKeyPasswordFile", true);
        signCSRPKPasswordFile.addLongIdentifier("private-key-passphrase-file", true);
        signCSRPKPasswordFile.addLongIdentifier("privateKeyPassphraseFile", true);
        signCSRPKPasswordFile.addLongIdentifier("private-key-pin-file", true);
        signCSRPKPasswordFile.addLongIdentifier("privateKeyPINFile", true);
        signCSRPKPasswordFile.addLongIdentifier("key-password-file", true);
        signCSRPKPasswordFile.addLongIdentifier("keyPasswordFile", true);
        signCSRPKPasswordFile.addLongIdentifier("key-passphrase-file", true);
        signCSRPKPasswordFile.addLongIdentifier("keyPassphraseFile", true);
        signCSRPKPasswordFile.addLongIdentifier("key-pin-file", true);
        signCSRPKPasswordFile.addLongIdentifier("keyPINFile", true);
        signCSRParser.addArgument(signCSRPKPasswordFile);
        final BooleanArgument signCSRPromptForPKPassword = new BooleanArgument(null, "prompt-for-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_PROMPT_FOR_PK_PW_DESC.get());
        signCSRPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassword", true);
        signCSRPromptForPKPassword.addLongIdentifier("prompt-for-private-key-passphrase", true);
        signCSRPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassphrase", true);
        signCSRPromptForPKPassword.addLongIdentifier("prompt-for-private-key-pin", true);
        signCSRPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPIN", true);
        signCSRPromptForPKPassword.addLongIdentifier("prompt-for-key-password", true);
        signCSRPromptForPKPassword.addLongIdentifier("promptForKeyPassword", true);
        signCSRPromptForPKPassword.addLongIdentifier("prompt-for-key-passphrase", true);
        signCSRPromptForPKPassword.addLongIdentifier("promptForKeyPassphrase", true);
        signCSRPromptForPKPassword.addLongIdentifier("prompt-for-key-pin", true);
        signCSRPromptForPKPassword.addLongIdentifier("promptForKeyPIN", true);
        signCSRParser.addArgument(signCSRPromptForPKPassword);
        final StringArgument signCSRAlias = new StringArgument(null, "signing-certificate-alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_ALIAS_DESC.get());
        signCSRAlias.addLongIdentifier("signingCertificateAlias", true);
        signCSRAlias.addLongIdentifier("signing-certificate-nickname", true);
        signCSRAlias.addLongIdentifier("signingCertificateNickname", true);
        signCSRAlias.addLongIdentifier("alias", true);
        signCSRAlias.addLongIdentifier("nickname", true);
        signCSRParser.addArgument(signCSRAlias);
        final DNArgument signCSRSubjectDN = new DNArgument(null, "subject-dn", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_SUBJECT_DN_DESC.get());
        signCSRSubjectDN.addLongIdentifier("subjectDN", true);
        signCSRSubjectDN.addLongIdentifier("subject", true);
        signCSRSubjectDN.addLongIdentifier("dname", true);
        signCSRParser.addArgument(signCSRSubjectDN);
        final IntegerArgument signCSRDaysValid = new IntegerArgument(null, "days-valid", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_DAYS_VALID_DESC.get(), 1, Integer.MAX_VALUE);
        signCSRDaysValid.addLongIdentifier("daysValid", true);
        signCSRDaysValid.addLongIdentifier("validity", true);
        signCSRParser.addArgument(signCSRDaysValid);
        final TimestampArgument signCSRNotBefore = new TimestampArgument(null, "validity-start-time", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_TIMESTAMP.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_VALIDITY_START_TIME_DESC.get("20180102123456"));
        signCSRNotBefore.addLongIdentifier("validityStartTime", true);
        signCSRNotBefore.addLongIdentifier("not-before", true);
        signCSRNotBefore.addLongIdentifier("notBefore", true);
        signCSRParser.addArgument(signCSRNotBefore);
        final StringArgument signCSRSignatureAlgorithm = new StringArgument(null, "signature-algorithm", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_SIG_ALG_DESC.get());
        signCSRSignatureAlgorithm.addLongIdentifier("signatureAlgorithm", true);
        signCSRSignatureAlgorithm.addLongIdentifier("signature-alg", true);
        signCSRSignatureAlgorithm.addLongIdentifier("signatureAlg", true);
        signCSRSignatureAlgorithm.addLongIdentifier("sig-alg", true);
        signCSRSignatureAlgorithm.addLongIdentifier("sigAlg", true);
        signCSRParser.addArgument(signCSRSignatureAlgorithm);
        final BooleanArgument signCSRIncludeExtensions = new BooleanArgument(null, "include-requested-extensions", 1, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_INCLUDE_EXT_DESC.get());
        signCSRIncludeExtensions.addLongIdentifier("includeRequestedExtensions", true);
        signCSRParser.addArgument(signCSRIncludeExtensions);
        final StringArgument signCSRSubjectAltDNS = new StringArgument(null, "subject-alternative-name-dns", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_SAN_DNS_DESC.get());
        signCSRSubjectAltDNS.addLongIdentifier("subjectAlternativeNameDNS", true);
        signCSRSubjectAltDNS.addLongIdentifier("subject-alt-name-dns", true);
        signCSRSubjectAltDNS.addLongIdentifier("subjectAltNameDNS", true);
        signCSRSubjectAltDNS.addLongIdentifier("subject-alternative-dns", true);
        signCSRSubjectAltDNS.addLongIdentifier("subjectAlternativeDNS", true);
        signCSRSubjectAltDNS.addLongIdentifier("subject-alt-dns", true);
        signCSRSubjectAltDNS.addLongIdentifier("subjectAltDNS", true);
        signCSRSubjectAltDNS.addLongIdentifier("san-dns", true);
        signCSRSubjectAltDNS.addLongIdentifier("sanDNS", true);
        signCSRParser.addArgument(signCSRSubjectAltDNS);
        final StringArgument signCSRSubjectAltIP = new StringArgument(null, "subject-alternative-name-ip-address", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_SAN_IP_DESC.get());
        signCSRSubjectAltIP.addLongIdentifier("subjectAlternativeNameIPAddress", true);
        signCSRSubjectAltIP.addLongIdentifier("subject-alternative-name-ip", true);
        signCSRSubjectAltIP.addLongIdentifier("subjectAlternativeNameIP", true);
        signCSRSubjectAltIP.addLongIdentifier("subject-alt-name-ip-address", true);
        signCSRSubjectAltIP.addLongIdentifier("subjectAltNameIPAddress", true);
        signCSRSubjectAltIP.addLongIdentifier("subject-alt-name-ip", true);
        signCSRSubjectAltIP.addLongIdentifier("subjectAltNameIP", true);
        signCSRSubjectAltIP.addLongIdentifier("subject-alternative-ip-address", true);
        signCSRSubjectAltIP.addLongIdentifier("subjectAlternativeIPAddress", true);
        signCSRSubjectAltIP.addLongIdentifier("subject-alternative-ip", true);
        signCSRSubjectAltIP.addLongIdentifier("subjectAlternativeIP", true);
        signCSRSubjectAltIP.addLongIdentifier("subject-alt-ip-address", true);
        signCSRSubjectAltIP.addLongIdentifier("subjectAltIPAddress", true);
        signCSRSubjectAltIP.addLongIdentifier("subject-alt-ip", true);
        signCSRSubjectAltIP.addLongIdentifier("subjectAltIP", true);
        signCSRSubjectAltIP.addLongIdentifier("san-ip-address", true);
        signCSRSubjectAltIP.addLongIdentifier("sanIPAddress", true);
        signCSRSubjectAltIP.addLongIdentifier("san-ip", true);
        signCSRSubjectAltIP.addLongIdentifier("sanIP", true);
        signCSRSubjectAltIP.addValueValidator(new IPAddressArgumentValueValidator(true, true));
        signCSRParser.addArgument(signCSRSubjectAltIP);
        final StringArgument signCSRSubjectAltEmail = new StringArgument(null, "subject-alternative-name-email-address", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_SAN_EMAIL_DESC.get());
        signCSRSubjectAltEmail.addLongIdentifier("subjectAlternativeNameEmailAddress", true);
        signCSRSubjectAltEmail.addLongIdentifier("subject-alternative-name-email", true);
        signCSRSubjectAltEmail.addLongIdentifier("subjectAlternativeNameEmail", true);
        signCSRSubjectAltEmail.addLongIdentifier("subject-alt-name-email-address", true);
        signCSRSubjectAltEmail.addLongIdentifier("subjectAltNameEmailAddress", true);
        signCSRSubjectAltEmail.addLongIdentifier("subject-alt-name-email", true);
        signCSRSubjectAltEmail.addLongIdentifier("subjectAltNameEmail", true);
        signCSRSubjectAltEmail.addLongIdentifier("subject-alternative-email-address", true);
        signCSRSubjectAltEmail.addLongIdentifier("subjectAlternativeEmailAddress", true);
        signCSRSubjectAltEmail.addLongIdentifier("subject-alternative-email", true);
        signCSRSubjectAltEmail.addLongIdentifier("subjectAlternativeEmail", true);
        signCSRSubjectAltEmail.addLongIdentifier("subject-alt-email-address", true);
        signCSRSubjectAltEmail.addLongIdentifier("subjectAltEmailAddress", true);
        signCSRSubjectAltEmail.addLongIdentifier("subject-alt-email", true);
        signCSRSubjectAltEmail.addLongIdentifier("subjectAltEmail", true);
        signCSRSubjectAltEmail.addLongIdentifier("san-email-address", true);
        signCSRSubjectAltEmail.addLongIdentifier("sanEmailAddress", true);
        signCSRSubjectAltEmail.addLongIdentifier("san-email", true);
        signCSRSubjectAltEmail.addLongIdentifier("sanEmail", true);
        signCSRParser.addArgument(signCSRSubjectAltEmail);
        final StringArgument signCSRSubjectAltURI = new StringArgument(null, "subject-alternative-name-uri", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_URI.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_SAN_URI_DESC.get());
        signCSRSubjectAltURI.addLongIdentifier("subjectAlternativeNameURI", true);
        signCSRSubjectAltURI.addLongIdentifier("subject-alt-name-uri", true);
        signCSRSubjectAltURI.addLongIdentifier("subjectAltNameURI", true);
        signCSRSubjectAltURI.addLongIdentifier("subject-alternative-uri", true);
        signCSRSubjectAltURI.addLongIdentifier("subjectAlternativeURI", true);
        signCSRSubjectAltURI.addLongIdentifier("subject-alt-uri", true);
        signCSRSubjectAltURI.addLongIdentifier("subjectAltURI", true);
        signCSRSubjectAltURI.addLongIdentifier("san-uri", true);
        signCSRSubjectAltURI.addLongIdentifier("sanURI", true);
        signCSRParser.addArgument(signCSRSubjectAltURI);
        final StringArgument signCSRSubjectAltOID = new StringArgument(null, "subject-alternative-name-oid", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_OID.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_SAN_OID_DESC.get());
        signCSRSubjectAltOID.addLongIdentifier("subjectAlternativeNameOID", true);
        signCSRSubjectAltOID.addLongIdentifier("subject-alt-name-oid", true);
        signCSRSubjectAltOID.addLongIdentifier("subjectAltNameOID", true);
        signCSRSubjectAltOID.addLongIdentifier("subject-alternative-oid", true);
        signCSRSubjectAltOID.addLongIdentifier("subjectAlternativeOID", true);
        signCSRSubjectAltOID.addLongIdentifier("subject-alt-oid", true);
        signCSRSubjectAltOID.addLongIdentifier("subjectAltOID", true);
        signCSRSubjectAltOID.addLongIdentifier("san-oid", true);
        signCSRSubjectAltOID.addLongIdentifier("sanOID", true);
        signCSRSubjectAltOID.addValueValidator(new OIDArgumentValueValidator(true));
        signCSRParser.addArgument(signCSRSubjectAltOID);
        final StringArgument signCSRIssuerAltDNS = new StringArgument(null, "issuer-alternative-name-dns", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_IAN_DNS_DESC.get());
        signCSRIssuerAltDNS.addLongIdentifier("issuerAlternativeNameDNS", true);
        signCSRIssuerAltDNS.addLongIdentifier("issuer-alt-name-dns", true);
        signCSRIssuerAltDNS.addLongIdentifier("issuerAltNameDNS", true);
        signCSRIssuerAltDNS.addLongIdentifier("issuer-alternative-dns", true);
        signCSRIssuerAltDNS.addLongIdentifier("issuerAlternativeDNS", true);
        signCSRIssuerAltDNS.addLongIdentifier("issuer-alt-dns", true);
        signCSRIssuerAltDNS.addLongIdentifier("issuerAltDNS", true);
        signCSRIssuerAltDNS.addLongIdentifier("ian-dns", true);
        signCSRIssuerAltDNS.addLongIdentifier("ianDNS", true);
        signCSRParser.addArgument(signCSRIssuerAltDNS);
        final StringArgument signCSRIssuerAltIP = new StringArgument(null, "issuer-alternative-name-ip-address", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_IAN_IP_DESC.get());
        signCSRIssuerAltIP.addLongIdentifier("issuerAlternativeNameIPAddress", true);
        signCSRIssuerAltIP.addLongIdentifier("issuer-alternative-name-ip", true);
        signCSRIssuerAltIP.addLongIdentifier("issuerAlternativeNameIP", true);
        signCSRIssuerAltIP.addLongIdentifier("issuer-alt-name-ip-address", true);
        signCSRIssuerAltIP.addLongIdentifier("issuerAltNameIPAddress", true);
        signCSRIssuerAltIP.addLongIdentifier("issuer-alt-name-ip", true);
        signCSRIssuerAltIP.addLongIdentifier("issuerAltNameIP", true);
        signCSRIssuerAltIP.addLongIdentifier("issuer-alternative-ip-address", true);
        signCSRIssuerAltIP.addLongIdentifier("issuerAlternativeIPAddress", true);
        signCSRIssuerAltIP.addLongIdentifier("issuer-alternative-ip", true);
        signCSRIssuerAltIP.addLongIdentifier("issuerAlternativeIP", true);
        signCSRIssuerAltIP.addLongIdentifier("issuer-alt-ip-address", true);
        signCSRIssuerAltIP.addLongIdentifier("issuerAltIPAddress", true);
        signCSRIssuerAltIP.addLongIdentifier("issuer-alt-ip", true);
        signCSRIssuerAltIP.addLongIdentifier("issuerAltIP", true);
        signCSRIssuerAltIP.addLongIdentifier("ian-ip-address", true);
        signCSRIssuerAltIP.addLongIdentifier("ianIPAddress", true);
        signCSRIssuerAltIP.addLongIdentifier("ian-ip", true);
        signCSRIssuerAltIP.addLongIdentifier("ianIP", true);
        signCSRIssuerAltIP.addValueValidator(new IPAddressArgumentValueValidator(true, true));
        signCSRParser.addArgument(signCSRIssuerAltIP);
        final StringArgument signCSRIssuerAltEmail = new StringArgument(null, "issuer-alternative-name-email-address", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_NAME.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_IAN_EMAIL_DESC.get());
        signCSRIssuerAltEmail.addLongIdentifier("issuerAlternativeNameEmailAddress", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuer-alternative-name-email", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuerAlternativeNameEmail", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuer-alt-name-email-address", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuerAltNameEmailAddress", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuer-alt-name-email", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuerAltNameEmail", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuer-alternative-email-address", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuerAlternativeEmailAddress", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuer-alternative-email", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuerAlternativeEmail", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuer-alt-email-address", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuerAltEmailAddress", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuer-alt-email", true);
        signCSRIssuerAltEmail.addLongIdentifier("issuerAltEmail", true);
        signCSRIssuerAltEmail.addLongIdentifier("ian-email-address", true);
        signCSRIssuerAltEmail.addLongIdentifier("ianEmailAddress", true);
        signCSRIssuerAltEmail.addLongIdentifier("ian-email", true);
        signCSRIssuerAltEmail.addLongIdentifier("ianEmail", true);
        signCSRParser.addArgument(signCSRIssuerAltEmail);
        final StringArgument signCSRIssuerAltURI = new StringArgument(null, "issuer-alternative-name-uri", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_URI.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_IAN_URI_DESC.get());
        signCSRIssuerAltURI.addLongIdentifier("issuerAlternativeNameURI", true);
        signCSRIssuerAltURI.addLongIdentifier("issuer-alt-name-uri", true);
        signCSRIssuerAltURI.addLongIdentifier("issuerAltNameURI", true);
        signCSRIssuerAltURI.addLongIdentifier("issuer-alternative-uri", true);
        signCSRIssuerAltURI.addLongIdentifier("issuerAlternativeURI", true);
        signCSRIssuerAltURI.addLongIdentifier("issuer-alt-uri", true);
        signCSRIssuerAltURI.addLongIdentifier("issuerAltURI", true);
        signCSRIssuerAltURI.addLongIdentifier("ian-uri", true);
        signCSRIssuerAltURI.addLongIdentifier("ianURI", true);
        signCSRParser.addArgument(signCSRIssuerAltURI);
        final StringArgument signCSRIssuerAltOID = new StringArgument(null, "issuer-alternative-name-oid", false, 0, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_OID.get(), CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_IAN_OID_DESC.get());
        signCSRIssuerAltOID.addLongIdentifier("issuerAlternativeNameOID", true);
        signCSRIssuerAltOID.addLongIdentifier("issuer-alt-name-oid", true);
        signCSRIssuerAltOID.addLongIdentifier("issuerAltNameOID", true);
        signCSRIssuerAltOID.addLongIdentifier("issuer-alternative-oid", true);
        signCSRIssuerAltOID.addLongIdentifier("issuerAlternativeOID", true);
        signCSRIssuerAltOID.addLongIdentifier("issuer-alt-oid", true);
        signCSRIssuerAltOID.addLongIdentifier("issuerAltOID", true);
        signCSRIssuerAltOID.addLongIdentifier("ian-oid", true);
        signCSRIssuerAltOID.addLongIdentifier("ianOID", true);
        signCSRIssuerAltOID.addValueValidator(new OIDArgumentValueValidator(true));
        signCSRParser.addArgument(signCSRIssuerAltOID);
        final BooleanValueArgument signCSRBasicConstraintsIsCA = new BooleanValueArgument(null, "basic-constraints-is-ca", false, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_BC_IS_CA_DESC.get());
        signCSRBasicConstraintsIsCA.addLongIdentifier("basicConstraintsIsCA", true);
        signCSRBasicConstraintsIsCA.addLongIdentifier("bc-is-ca", true);
        signCSRBasicConstraintsIsCA.addLongIdentifier("bcIsCA", true);
        signCSRParser.addArgument(signCSRBasicConstraintsIsCA);
        final IntegerArgument signCSRBasicConstraintsPathLength = new IntegerArgument(null, "basic-constraints-maximum-path-length", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_GEN_CERT_ARG_BC_PATH_LENGTH_DESC.get(), 0, Integer.MAX_VALUE);
        signCSRBasicConstraintsPathLength.addLongIdentifier("basicConstraintsMaximumPathLength", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("basic-constraints-max-path-length", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("basicConstraintsMaxPathLength", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("basic-constraints-path-length", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("basicConstraintsPathLength", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("bc-maximum-path-length", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("bcMaximumPathLength", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("bc-max-path-length", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("bcMaxPathLength", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("bc-path-length", true);
        signCSRBasicConstraintsPathLength.addLongIdentifier("bcPathLength", true);
        signCSRParser.addArgument(signCSRBasicConstraintsPathLength);
        final StringArgument signCSRKeyUsage = new StringArgument(null, "key-usage", false, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_KU_DESC.get());
        signCSRKeyUsage.addLongIdentifier("keyUsage", true);
        signCSRParser.addArgument(signCSRKeyUsage);
        final StringArgument signCSRExtendedKeyUsage = new StringArgument(null, "extended-key-usage", false, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_EKU_DESC.get());
        signCSRExtendedKeyUsage.addLongIdentifier("extendedKeyUsage", true);
        signCSRParser.addArgument(signCSRExtendedKeyUsage);
        final StringArgument signCSRExtension = new StringArgument(null, "extension", false, 0, null, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_EXT_DESC.get());
        signCSRExtension.addLongIdentifier("ext", true);
        signCSRParser.addArgument(signCSRExtension);
        final BooleanArgument signCSRNoPrompt = new BooleanArgument(null, "no-prompt", 1, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_NO_PROMPT_DESC.get());
        signCSRNoPrompt.addLongIdentifier("noPrompt", true);
        signCSRParser.addArgument(signCSRNoPrompt);
        final BooleanArgument signCSRDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_ARG_DISPLAY_COMMAND_DESC.get());
        signCSRDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        signCSRDisplayCommand.addLongIdentifier("show-keytool-command", true);
        signCSRDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        signCSRParser.addArgument(signCSRDisplayCommand);
        signCSRParser.addRequiredArgumentSet(signCSRKeystorePassword, signCSRKeystorePasswordFile, signCSRPromptForKeystorePassword);
        signCSRParser.addExclusiveArgumentSet(signCSRKeystorePassword, signCSRKeystorePasswordFile, signCSRPromptForKeystorePassword);
        signCSRParser.addExclusiveArgumentSet(signCSRPKPassword, signCSRPKPasswordFile, signCSRPromptForPKPassword);
        signCSRParser.addDependentArgumentSet(signCSRBasicConstraintsPathLength, signCSRBasicConstraintsIsCA, new Argument[0]);
        final LinkedHashMap<String[], String> signCSRExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        signCSRExamples.put(new String[] { "sign-certificate-signing-request", "--request-input-file", "server-cert.csr", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--signing-certificate-alias", "ca-cert", "--include-requested-extensions" }, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_EXAMPLE_1.get(getPlatformSpecificPath("config", "keystore")));
        signCSRExamples.put(new String[] { "sign-certificate-signing-request", "--request-input-file", "server-cert.csr", "--certificate-output-file", "server-cert.der", "--output-format", "DER", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--signing-certificate-alias", "ca-cert", "--days-valid", "730", "--validity-start-time", "20170101000000", "--include-requested-extensions", "--issuer-alternative-name-email-address", "ca@example.com" }, CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_EXAMPLE_2.get(getPlatformSpecificPath("config", "keystore")));
        final SubCommand signCSRSubCommand = new SubCommand("sign-certificate-signing-request", CertMessages.INFO_MANAGE_CERTS_SC_SIGN_CSR_DESC.get(), signCSRParser, signCSRExamples);
        signCSRSubCommand.addName("signCertificateSigningRequest", true);
        signCSRSubCommand.addName("sign-certificate-request", false);
        signCSRSubCommand.addName("signCertificateRequest", true);
        signCSRSubCommand.addName("sign-certificate", false);
        signCSRSubCommand.addName("signCertificate", true);
        signCSRSubCommand.addName("sign-csr", true);
        signCSRSubCommand.addName("signCSR", true);
        signCSRSubCommand.addName("sign", false);
        signCSRSubCommand.addName("gencert", true);
        parser.addSubCommand(signCSRSubCommand);
        final ArgumentParser changeAliasParser = new ArgumentParser("change-certificate-alias", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_DESC.get());
        final FileArgument changeAliasKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_KS_DESC.get(), true, true, true, false);
        changeAliasKeystore.addLongIdentifier("keystore-path", true);
        changeAliasKeystore.addLongIdentifier("keystorePath", true);
        changeAliasKeystore.addLongIdentifier("keystore-file", true);
        changeAliasKeystore.addLongIdentifier("keystoreFile", true);
        changeAliasParser.addArgument(changeAliasKeystore);
        final StringArgument changeAliasKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_KS_PW_DESC.get());
        changeAliasKeystorePassword.addLongIdentifier("keystorePassword", true);
        changeAliasKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        changeAliasKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        changeAliasKeystorePassword.addLongIdentifier("keystore-pin", true);
        changeAliasKeystorePassword.addLongIdentifier("keystorePIN", true);
        changeAliasKeystorePassword.addLongIdentifier("storepass", true);
        changeAliasKeystorePassword.setSensitive(true);
        changeAliasParser.addArgument(changeAliasKeystorePassword);
        final FileArgument changeAliasKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        changeAliasKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        changeAliasKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        changeAliasKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        changeAliasKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        changeAliasKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        changeAliasParser.addArgument(changeAliasKeystorePasswordFile);
        final BooleanArgument changeAliasPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_PROMPT_FOR_KS_PW_DESC.get());
        changeAliasPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        changeAliasPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        changeAliasPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        changeAliasPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        changeAliasPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        changeAliasParser.addArgument(changeAliasPromptForKeystorePassword);
        final StringArgument changeAliasPKPassword = new StringArgument(null, "private-key-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_PK_PW_DESC.get());
        changeAliasPKPassword.addLongIdentifier("privateKeyPassword", true);
        changeAliasPKPassword.addLongIdentifier("private-key-passphrase", true);
        changeAliasPKPassword.addLongIdentifier("privateKeyPassphrase", true);
        changeAliasPKPassword.addLongIdentifier("private-key-pin", true);
        changeAliasPKPassword.addLongIdentifier("privateKeyPIN", true);
        changeAliasPKPassword.addLongIdentifier("key-password", true);
        changeAliasPKPassword.addLongIdentifier("keyPassword", true);
        changeAliasPKPassword.addLongIdentifier("key-passphrase", true);
        changeAliasPKPassword.addLongIdentifier("keyPassphrase", true);
        changeAliasPKPassword.addLongIdentifier("key-pin", true);
        changeAliasPKPassword.addLongIdentifier("keyPIN", true);
        changeAliasPKPassword.addLongIdentifier("keypass", true);
        changeAliasPKPassword.setSensitive(true);
        changeAliasParser.addArgument(changeAliasPKPassword);
        final FileArgument changeAliasPKPasswordFile = new FileArgument(null, "private-key-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_PK_PW_FILE_DESC.get(), true, true, true, false);
        changeAliasPKPasswordFile.addLongIdentifier("privateKeyPasswordFile", true);
        changeAliasPKPasswordFile.addLongIdentifier("private-key-passphrase-file", true);
        changeAliasPKPasswordFile.addLongIdentifier("privateKeyPassphraseFile", true);
        changeAliasPKPasswordFile.addLongIdentifier("private-key-pin-file", true);
        changeAliasPKPasswordFile.addLongIdentifier("privateKeyPINFile", true);
        changeAliasPKPasswordFile.addLongIdentifier("key-password-file", true);
        changeAliasPKPasswordFile.addLongIdentifier("keyPasswordFile", true);
        changeAliasPKPasswordFile.addLongIdentifier("key-passphrase-file", true);
        changeAliasPKPasswordFile.addLongIdentifier("keyPassphraseFile", true);
        changeAliasPKPasswordFile.addLongIdentifier("key-pin-file", true);
        changeAliasPKPasswordFile.addLongIdentifier("keyPINFile", true);
        changeAliasParser.addArgument(changeAliasPKPasswordFile);
        final BooleanArgument changeAliasPromptForPKPassword = new BooleanArgument(null, "prompt-for-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_PROMPT_FOR_PK_PW_DESC.get());
        changeAliasPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassword", true);
        changeAliasPromptForPKPassword.addLongIdentifier("prompt-for-private-key-passphrase", true);
        changeAliasPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPassphrase", true);
        changeAliasPromptForPKPassword.addLongIdentifier("prompt-for-private-key-pin", true);
        changeAliasPromptForPKPassword.addLongIdentifier("promptForPrivateKeyPIN", true);
        changeAliasPromptForPKPassword.addLongIdentifier("prompt-for-key-password", true);
        changeAliasPromptForPKPassword.addLongIdentifier("promptForKeyPassword", true);
        changeAliasPromptForPKPassword.addLongIdentifier("prompt-for-key-passphrase", true);
        changeAliasPromptForPKPassword.addLongIdentifier("promptForKeyPassphrase", true);
        changeAliasPromptForPKPassword.addLongIdentifier("prompt-for-key-pin", true);
        changeAliasPromptForPKPassword.addLongIdentifier("promptForKeyPIN", true);
        changeAliasParser.addArgument(changeAliasPromptForPKPassword);
        final StringArgument changeAliasCurrentAlias = new StringArgument(null, "current-alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_CURRENT_ALIAS_DESC.get());
        changeAliasCurrentAlias.addLongIdentifier("currentAlias", true);
        changeAliasCurrentAlias.addLongIdentifier("old-alias", true);
        changeAliasCurrentAlias.addLongIdentifier("oldAlias", true);
        changeAliasCurrentAlias.addLongIdentifier("source-alias", true);
        changeAliasCurrentAlias.addLongIdentifier("sourceAlias", true);
        changeAliasCurrentAlias.addLongIdentifier("alias", true);
        changeAliasCurrentAlias.addLongIdentifier("current-nickname", true);
        changeAliasCurrentAlias.addLongIdentifier("currentNickname", true);
        changeAliasCurrentAlias.addLongIdentifier("old-nickname", true);
        changeAliasCurrentAlias.addLongIdentifier("oldNickname", true);
        changeAliasCurrentAlias.addLongIdentifier("source-nickname", true);
        changeAliasCurrentAlias.addLongIdentifier("sourceNickname", true);
        changeAliasCurrentAlias.addLongIdentifier("nickname", true);
        changeAliasCurrentAlias.addLongIdentifier("from", false);
        changeAliasParser.addArgument(changeAliasCurrentAlias);
        final StringArgument changeAliasNewAlias = new StringArgument(null, "new-alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_NEW_ALIAS_DESC.get());
        changeAliasNewAlias.addLongIdentifier("newAlias", true);
        changeAliasNewAlias.addLongIdentifier("destination-alias", true);
        changeAliasNewAlias.addLongIdentifier("destinationAlias", true);
        changeAliasNewAlias.addLongIdentifier("new-nickname", true);
        changeAliasNewAlias.addLongIdentifier("newNickname", true);
        changeAliasNewAlias.addLongIdentifier("destination-nickname", true);
        changeAliasNewAlias.addLongIdentifier("destinationNickname", true);
        changeAliasNewAlias.addLongIdentifier("to", false);
        changeAliasParser.addArgument(changeAliasNewAlias);
        final BooleanArgument changeAliasDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_ARG_DISPLAY_COMMAND_DESC.get());
        changeAliasDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        changeAliasDisplayCommand.addLongIdentifier("show-keytool-command", true);
        changeAliasDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        changeAliasParser.addArgument(changeAliasDisplayCommand);
        changeAliasParser.addRequiredArgumentSet(changeAliasKeystorePassword, changeAliasKeystorePasswordFile, changeAliasPromptForKeystorePassword);
        changeAliasParser.addExclusiveArgumentSet(changeAliasKeystorePassword, changeAliasKeystorePasswordFile, changeAliasPromptForKeystorePassword);
        changeAliasParser.addExclusiveArgumentSet(changeAliasPKPassword, changeAliasPKPasswordFile, changeAliasPromptForPKPassword);
        final LinkedHashMap<String[], String> changeAliasExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        changeAliasExamples.put(new String[] { "change-certificate-alias", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--current-alias", "server-cert", "--new-alias", "server-certificate", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_EXAMPLE_1.get());
        final SubCommand changeAliasSubCommand = new SubCommand("change-certificate-alias", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_ALIAS_DESC.get(), changeAliasParser, changeAliasExamples);
        changeAliasSubCommand.addName("changeCertificateAlias", true);
        changeAliasSubCommand.addName("change-alias", false);
        changeAliasSubCommand.addName("changeAlias", true);
        changeAliasSubCommand.addName("rename-certificate", true);
        changeAliasSubCommand.addName("renameCertificate", true);
        changeAliasSubCommand.addName("rename", false);
        parser.addSubCommand(changeAliasSubCommand);
        final ArgumentParser changeKSPWParser = new ArgumentParser("change-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_DESC.get());
        final FileArgument changeKSPWKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_ARG_KS_DESC.get(), true, true, true, false);
        changeKSPWKeystore.addLongIdentifier("keystore-path", true);
        changeKSPWKeystore.addLongIdentifier("keystorePath", true);
        changeKSPWKeystore.addLongIdentifier("keystore-file", true);
        changeKSPWKeystore.addLongIdentifier("keystoreFile", true);
        changeKSPWParser.addArgument(changeKSPWKeystore);
        final StringArgument changeKSPWCurrentPassword = new StringArgument(null, "current-keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_ARG_CURRENT_PW_DESC.get());
        changeKSPWCurrentPassword.addLongIdentifier("currentKeystorePassword", true);
        changeKSPWCurrentPassword.addLongIdentifier("current-keystore-passphrase", true);
        changeKSPWCurrentPassword.addLongIdentifier("currentKeystorePassphrase", true);
        changeKSPWCurrentPassword.addLongIdentifier("current-keystore-pin", true);
        changeKSPWCurrentPassword.addLongIdentifier("currentKeystorePIN", true);
        changeKSPWCurrentPassword.addLongIdentifier("storepass", true);
        changeKSPWCurrentPassword.setSensitive(true);
        changeKSPWParser.addArgument(changeKSPWCurrentPassword);
        final FileArgument changeKSPWCurrentPasswordFile = new FileArgument(null, "current-keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_ARG_CURRENT_PW_FILE_DESC.get(), true, true, true, false);
        changeKSPWCurrentPasswordFile.addLongIdentifier("currentKeystorePasswordFile", true);
        changeKSPWCurrentPasswordFile.addLongIdentifier("current-keystore-passphrase-file", true);
        changeKSPWCurrentPasswordFile.addLongIdentifier("currentKeystorePassphraseFile", true);
        changeKSPWCurrentPasswordFile.addLongIdentifier("current-keystore-pin-file", true);
        changeKSPWCurrentPasswordFile.addLongIdentifier("currentKeystorePINFile", true);
        changeKSPWParser.addArgument(changeKSPWCurrentPasswordFile);
        final BooleanArgument changeKSPWPromptForCurrentPassword = new BooleanArgument(null, "prompt-for-current-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_ARG_PROMPT_FOR_CURRENT_PW_DESC.get());
        changeKSPWPromptForCurrentPassword.addLongIdentifier("promptForCurrentKeystorePassword", true);
        changeKSPWPromptForCurrentPassword.addLongIdentifier("prompt-for-current-keystore-passphrase", true);
        changeKSPWPromptForCurrentPassword.addLongIdentifier("promptForCurrentKeystorePassphrase", true);
        changeKSPWPromptForCurrentPassword.addLongIdentifier("prompt-for-current-keystore-pin", true);
        changeKSPWPromptForCurrentPassword.addLongIdentifier("promptForCurrentKeystorePIN", true);
        changeKSPWParser.addArgument(changeKSPWPromptForCurrentPassword);
        final StringArgument changeKSPWNewPassword = new StringArgument(null, "new-keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_ARG_NEW_PW_DESC.get());
        changeKSPWNewPassword.addLongIdentifier("newKeystorePassword", true);
        changeKSPWNewPassword.addLongIdentifier("new-keystore-passphrase", true);
        changeKSPWNewPassword.addLongIdentifier("newKeystorePassphrase", true);
        changeKSPWNewPassword.addLongIdentifier("new-keystore-pin", true);
        changeKSPWNewPassword.addLongIdentifier("newKeystorePIN", true);
        changeKSPWNewPassword.addLongIdentifier("new", true);
        changeKSPWNewPassword.setSensitive(true);
        changeKSPWParser.addArgument(changeKSPWNewPassword);
        final FileArgument changeKSPWNewPasswordFile = new FileArgument(null, "new-keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_ARG_NEW_PW_FILE_DESC.get(), true, true, true, false);
        changeKSPWNewPasswordFile.addLongIdentifier("newKeystorePasswordFile", true);
        changeKSPWNewPasswordFile.addLongIdentifier("new-keystore-passphrase-file", true);
        changeKSPWNewPasswordFile.addLongIdentifier("newKeystorePassphraseFile", true);
        changeKSPWNewPasswordFile.addLongIdentifier("new-keystore-pin-file", true);
        changeKSPWNewPasswordFile.addLongIdentifier("newKeystorePINFile", true);
        changeKSPWParser.addArgument(changeKSPWNewPasswordFile);
        final BooleanArgument changeKSPWPromptForNewPassword = new BooleanArgument(null, "prompt-for-new-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_ARG_PROMPT_FOR_NEW_PW_DESC.get());
        changeKSPWPromptForNewPassword.addLongIdentifier("promptForNewKeystorePassword", true);
        changeKSPWPromptForNewPassword.addLongIdentifier("prompt-for-new-keystore-passphrase", true);
        changeKSPWPromptForNewPassword.addLongIdentifier("promptForNewKeystorePassphrase", true);
        changeKSPWPromptForNewPassword.addLongIdentifier("prompt-for-new-keystore-pin", true);
        changeKSPWPromptForNewPassword.addLongIdentifier("promptForNewKeystorePIN", true);
        changeKSPWParser.addArgument(changeKSPWPromptForNewPassword);
        final BooleanArgument changeKSPWDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_ARG_DISPLAY_COMMAND_DESC.get());
        changeKSPWDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        changeKSPWDisplayCommand.addLongIdentifier("show-keytool-command", true);
        changeKSPWDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        changeKSPWParser.addArgument(changeKSPWDisplayCommand);
        changeKSPWParser.addRequiredArgumentSet(changeKSPWCurrentPassword, changeKSPWCurrentPasswordFile, changeKSPWPromptForCurrentPassword);
        changeKSPWParser.addExclusiveArgumentSet(changeKSPWCurrentPassword, changeKSPWCurrentPasswordFile, changeKSPWPromptForCurrentPassword);
        changeKSPWParser.addRequiredArgumentSet(changeKSPWNewPassword, changeKSPWNewPasswordFile, changeKSPWPromptForNewPassword);
        changeKSPWParser.addExclusiveArgumentSet(changeKSPWNewPassword, changeKSPWNewPasswordFile, changeKSPWPromptForNewPassword);
        final LinkedHashMap<String[], String> changeKSPWExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        changeKSPWExamples.put(new String[] { "change-keystore-password", "--keystore", getPlatformSpecificPath("config", "keystore"), "--current-keystore-password-file", getPlatformSpecificPath("config", "current.pin"), "--new-keystore-password-file", getPlatformSpecificPath("config", "new.pin"), "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_EXAMPLE_1.get(getPlatformSpecificPath("config", "keystore"), getPlatformSpecificPath("config", "current.pin"), getPlatformSpecificPath("config", "new.pin")));
        final SubCommand changeKSPWSubCommand = new SubCommand("change-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_DESC.get(), changeKSPWParser, changeKSPWExamples);
        changeKSPWSubCommand.addName("changeKeystorePassword", true);
        changeKSPWSubCommand.addName("change-keystore-passphrase", true);
        changeKSPWSubCommand.addName("changeKeystorePassphrase", true);
        changeKSPWSubCommand.addName("change-keystore-pin", true);
        changeKSPWSubCommand.addName("changeKeystorePIN", true);
        changeKSPWSubCommand.addName("storepasswd", true);
        parser.addSubCommand(changeKSPWSubCommand);
        final ArgumentParser changePKPWParser = new ArgumentParser("change-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_DESC.get());
        final FileArgument changePKPWKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_KS_DESC.get(), true, true, true, false);
        changePKPWKeystore.addLongIdentifier("keystore-path", true);
        changePKPWKeystore.addLongIdentifier("keystorePath", true);
        changePKPWKeystore.addLongIdentifier("keystore-file", true);
        changePKPWKeystore.addLongIdentifier("keystoreFile", true);
        changePKPWParser.addArgument(changePKPWKeystore);
        final StringArgument changePKPWKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_KS_PW_DESC.get());
        changePKPWKeystorePassword.addLongIdentifier("keystorePassword", true);
        changePKPWKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        changePKPWKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        changePKPWKeystorePassword.addLongIdentifier("keystore-pin", true);
        changePKPWKeystorePassword.addLongIdentifier("keystorePIN", true);
        changePKPWKeystorePassword.addLongIdentifier("storepass", true);
        changePKPWKeystorePassword.setSensitive(true);
        changePKPWParser.addArgument(changePKPWKeystorePassword);
        final FileArgument changePKPWKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        changePKPWKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        changePKPWKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        changePKPWKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        changePKPWKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        changePKPWKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        changePKPWParser.addArgument(changePKPWKeystorePasswordFile);
        final BooleanArgument changePKPWPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_PROMPT_FOR_KS_PW_DESC.get());
        changePKPWPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        changePKPWPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        changePKPWPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        changePKPWPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        changePKPWPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        changePKPWParser.addArgument(changePKPWPromptForKeystorePassword);
        final StringArgument changePKPWAlias = new StringArgument(null, "alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_ALIAS_DESC.get());
        changePKPWAlias.addLongIdentifier("nickname", true);
        changePKPWParser.addArgument(changePKPWAlias);
        final StringArgument changePKPWCurrentPassword = new StringArgument(null, "current-private-key-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_CURRENT_PW_DESC.get());
        changePKPWCurrentPassword.addLongIdentifier("currentPrivateKeyPassword", true);
        changePKPWCurrentPassword.addLongIdentifier("current-private-key-passphrase", true);
        changePKPWCurrentPassword.addLongIdentifier("currentPrivateKeyPassphrase", true);
        changePKPWCurrentPassword.addLongIdentifier("current-private-key-pin", true);
        changePKPWCurrentPassword.addLongIdentifier("currentPrivateKeyPIN", true);
        changePKPWCurrentPassword.addLongIdentifier("keypass", true);
        changePKPWCurrentPassword.setSensitive(true);
        changePKPWParser.addArgument(changePKPWCurrentPassword);
        final FileArgument changePKPWCurrentPasswordFile = new FileArgument(null, "current-private-key-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_CURRENT_PW_FILE_DESC.get(), true, true, true, false);
        changePKPWCurrentPasswordFile.addLongIdentifier("currentPrivateKeyPasswordFile", true);
        changePKPWCurrentPasswordFile.addLongIdentifier("current-private-key-passphrase-file", true);
        changePKPWCurrentPasswordFile.addLongIdentifier("currentPrivateKeyPassphraseFile", true);
        changePKPWCurrentPasswordFile.addLongIdentifier("current-private-key-pin-file", true);
        changePKPWCurrentPasswordFile.addLongIdentifier("currentPrivateKeyPINFile", true);
        changePKPWParser.addArgument(changePKPWCurrentPasswordFile);
        final BooleanArgument changePKPWPromptForCurrentPassword = new BooleanArgument(null, "prompt-for-current-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_PROMPT_FOR_CURRENT_PW_DESC.get());
        changePKPWPromptForCurrentPassword.addLongIdentifier("promptForCurrentPrivateKeyPassword", true);
        changePKPWPromptForCurrentPassword.addLongIdentifier("prompt-for-current-private-key-passphrase", true);
        changePKPWPromptForCurrentPassword.addLongIdentifier("promptForCurrentPrivateKeyPassphrase", true);
        changePKPWPromptForCurrentPassword.addLongIdentifier("prompt-for-current-private-key-pin", true);
        changePKPWPromptForCurrentPassword.addLongIdentifier("promptForCurrentPrivateKeyPIN", true);
        changePKPWParser.addArgument(changePKPWPromptForCurrentPassword);
        final StringArgument changePKPWNewPassword = new StringArgument(null, "new-private-key-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_NEW_PW_DESC.get());
        changePKPWNewPassword.addLongIdentifier("newPrivateKeyPassword", true);
        changePKPWNewPassword.addLongIdentifier("new-private-key-passphrase", true);
        changePKPWNewPassword.addLongIdentifier("newPrivateKeyPassphrase", true);
        changePKPWNewPassword.addLongIdentifier("new-private-key-pin", true);
        changePKPWNewPassword.addLongIdentifier("newPrivateKeyPIN", true);
        changePKPWNewPassword.addLongIdentifier("new", true);
        changePKPWNewPassword.setSensitive(true);
        changePKPWParser.addArgument(changePKPWNewPassword);
        final FileArgument changePKPWNewPasswordFile = new FileArgument(null, "new-private-key-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_NEW_PW_FILE_DESC.get(), true, true, true, false);
        changePKPWNewPasswordFile.addLongIdentifier("newPrivateKeyPasswordFile", true);
        changePKPWNewPasswordFile.addLongIdentifier("new-private-key-passphrase-file", true);
        changePKPWNewPasswordFile.addLongIdentifier("newPrivateKeyPassphraseFile", true);
        changePKPWNewPasswordFile.addLongIdentifier("new-private-key-pin-file", true);
        changePKPWNewPasswordFile.addLongIdentifier("newPrivateKeyPINFile", true);
        changePKPWParser.addArgument(changePKPWNewPasswordFile);
        final BooleanArgument changePKPWPromptForNewPassword = new BooleanArgument(null, "prompt-for-new-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_PROMPT_FOR_NEW_PW_DESC.get());
        changePKPWPromptForNewPassword.addLongIdentifier("promptForNewPrivateKeyPassword", true);
        changePKPWPromptForNewPassword.addLongIdentifier("prompt-for-new-private-key-passphrase", true);
        changePKPWPromptForNewPassword.addLongIdentifier("promptForNewPrivateKeyPassphrase", true);
        changePKPWPromptForNewPassword.addLongIdentifier("prompt-for-new-private-key-pin", true);
        changePKPWPromptForNewPassword.addLongIdentifier("promptForNewPrivateKeyPIN", true);
        changePKPWParser.addArgument(changePKPWPromptForNewPassword);
        final BooleanArgument changePKPWDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_ARG_DISPLAY_COMMAND_DESC.get());
        changePKPWDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        changePKPWDisplayCommand.addLongIdentifier("show-keytool-command", true);
        changePKPWDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        changePKPWParser.addArgument(changePKPWDisplayCommand);
        changePKPWParser.addRequiredArgumentSet(changePKPWKeystorePassword, changePKPWKeystorePasswordFile, changePKPWPromptForKeystorePassword);
        changePKPWParser.addExclusiveArgumentSet(changePKPWKeystorePassword, changePKPWKeystorePasswordFile, changePKPWPromptForKeystorePassword);
        changePKPWParser.addRequiredArgumentSet(changePKPWCurrentPassword, changePKPWCurrentPasswordFile, changePKPWPromptForCurrentPassword);
        changePKPWParser.addExclusiveArgumentSet(changePKPWCurrentPassword, changePKPWCurrentPasswordFile, changePKPWPromptForCurrentPassword);
        changePKPWParser.addRequiredArgumentSet(changePKPWNewPassword, changePKPWNewPasswordFile, changePKPWPromptForNewPassword);
        changePKPWParser.addExclusiveArgumentSet(changePKPWNewPassword, changePKPWNewPasswordFile, changePKPWPromptForNewPassword);
        final LinkedHashMap<String[], String> changePKPWExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        changePKPWExamples.put(new String[] { "change-private-key-password", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert", "--current-private-key-password-file", getPlatformSpecificPath("config", "current.pin"), "--new-private-key-password-file", getPlatformSpecificPath("config", "new.pin"), "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_EXAMPLE_1.get(getPlatformSpecificPath("config", "keystore"), getPlatformSpecificPath("config", "current.pin"), getPlatformSpecificPath("config", "new.pin")));
        final SubCommand changePKPWSubCommand = new SubCommand("change-private-key-password", CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_PK_PW_DESC.get(), changePKPWParser, changePKPWExamples);
        changePKPWSubCommand.addName("changePrivateKeyPassword", true);
        changePKPWSubCommand.addName("change-private-key-passphrase", true);
        changePKPWSubCommand.addName("changePrivateKeyPassphrase", true);
        changePKPWSubCommand.addName("change-private-key-pin", true);
        changePKPWSubCommand.addName("changePrivateKeyPIN", true);
        changePKPWSubCommand.addName("change-key-password", false);
        changePKPWSubCommand.addName("changeKeyPassword", true);
        changePKPWSubCommand.addName("change-key-passphrase", true);
        changePKPWSubCommand.addName("changeKeyPassphrase", true);
        changePKPWSubCommand.addName("change-key-pin", true);
        changePKPWSubCommand.addName("changeKeyPIN", true);
        changePKPWSubCommand.addName("keypasswd", true);
        parser.addSubCommand(changePKPWSubCommand);
        final ArgumentParser trustServerParser = new ArgumentParser("trust-server-certificate", CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_DESC.get());
        final StringArgument trustServerHostname = new StringArgument('h', "hostname", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_HOST.get(), CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_HOSTNAME_DESC.get());
        trustServerHostname.addLongIdentifier("server-address", true);
        trustServerHostname.addLongIdentifier("serverAddress", true);
        trustServerHostname.addLongIdentifier("address", true);
        trustServerParser.addArgument(trustServerHostname);
        final IntegerArgument trustServerPort = new IntegerArgument('p', "port", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PORT.get(), CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_PORT_DESC.get(), 1, 65535);
        trustServerPort.addLongIdentifier("server-port", true);
        trustServerPort.addLongIdentifier("serverPort", true);
        trustServerParser.addArgument(trustServerPort);
        final BooleanArgument trustServerUseStartTLS = new BooleanArgument('q', "use-ldap-start-tls", 1, CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_USE_START_TLS_DESC.get());
        trustServerUseStartTLS.addLongIdentifier("use-ldap-starttls", true);
        trustServerUseStartTLS.addLongIdentifier("useLDAPStartTLS", true);
        trustServerUseStartTLS.addLongIdentifier("use-start-tls", true);
        trustServerUseStartTLS.addLongIdentifier("use-starttls", true);
        trustServerUseStartTLS.addLongIdentifier("useStartTLS", true);
        trustServerParser.addArgument(trustServerUseStartTLS);
        final FileArgument trustServerKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_KS_DESC.get(), false, true, true, false);
        trustServerKeystore.addLongIdentifier("keystore-path", true);
        trustServerKeystore.addLongIdentifier("keystorePath", true);
        trustServerKeystore.addLongIdentifier("keystore-file", true);
        trustServerKeystore.addLongIdentifier("keystoreFile", true);
        trustServerParser.addArgument(trustServerKeystore);
        final StringArgument trustServerKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_KS_PW_DESC.get());
        trustServerKeystorePassword.addLongIdentifier("keystorePassword", true);
        trustServerKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        trustServerKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        trustServerKeystorePassword.addLongIdentifier("keystore-pin", true);
        trustServerKeystorePassword.addLongIdentifier("keystorePIN", true);
        trustServerKeystorePassword.addLongIdentifier("storepass", true);
        trustServerKeystorePassword.setSensitive(true);
        trustServerParser.addArgument(trustServerKeystorePassword);
        final FileArgument trustServerKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        trustServerKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        trustServerKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        trustServerKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        trustServerKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        trustServerKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        trustServerParser.addArgument(trustServerKeystorePasswordFile);
        final BooleanArgument trustServerPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_PROMPT_FOR_KS_PW_DESC.get());
        trustServerPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        trustServerPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        trustServerPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        trustServerPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        trustServerPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        trustServerParser.addArgument(trustServerPromptForKeystorePassword);
        final Set<String> trustServerKeystoreTypeAllowedValues = StaticUtils.setOf("jks", "pkcs12", "pkcs 12", "pkcs#12", "pkcs #12");
        final StringArgument trustServerKeystoreType = new StringArgument(null, "keystore-type", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_TYPE.get(), CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_KS_TYPE_DESC.get(), trustServerKeystoreTypeAllowedValues);
        trustServerKeystoreType.addLongIdentifier("keystoreType", true);
        trustServerKeystoreType.addLongIdentifier("storetype", true);
        trustServerParser.addArgument(trustServerKeystoreType);
        final StringArgument trustServerAlias = new StringArgument(null, "alias", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_ALIAS_DESC.get());
        trustServerAlias.addLongIdentifier("nickname", true);
        trustServerParser.addArgument(trustServerAlias);
        final BooleanArgument trustServerIssuersOnly = new BooleanArgument(null, "issuers-only", 1, CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_ISSUERS_ONLY_DESC.get());
        trustServerIssuersOnly.addLongIdentifier("issuersOnly", true);
        trustServerIssuersOnly.addLongIdentifier("issuer-certificates-only", true);
        trustServerIssuersOnly.addLongIdentifier("issuerCertificatesOnly", true);
        trustServerIssuersOnly.addLongIdentifier("only-issuers", true);
        trustServerIssuersOnly.addLongIdentifier("onlyIssuers", true);
        trustServerIssuersOnly.addLongIdentifier("only-issuer-certificates", true);
        trustServerIssuersOnly.addLongIdentifier("onlyIssuerCertificates", true);
        trustServerParser.addArgument(trustServerIssuersOnly);
        final BooleanArgument trustServerEnableSSLDebugging = new BooleanArgument(null, "enableSSLDebugging", 1, CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_ENABLE_SSL_DEBUGGING_DESC.get());
        trustServerEnableSSLDebugging.addLongIdentifier("enableTLSDebugging", true);
        trustServerEnableSSLDebugging.addLongIdentifier("enableStartTLSDebugging", true);
        trustServerEnableSSLDebugging.addLongIdentifier("enable-ssl-debugging", true);
        trustServerEnableSSLDebugging.addLongIdentifier("enable-tls-debugging", true);
        trustServerEnableSSLDebugging.addLongIdentifier("enable-starttls-debugging", true);
        trustServerEnableSSLDebugging.addLongIdentifier("enable-start-tls-debugging", true);
        trustServerParser.addArgument(trustServerEnableSSLDebugging);
        this.addEnableSSLDebuggingArgument(trustServerEnableSSLDebugging);
        final BooleanArgument trustServerVerbose = new BooleanArgument(null, "verbose", 1, CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_VERBOSE_DESC.get());
        trustServerParser.addArgument(trustServerVerbose);
        final BooleanArgument trustServerNoPrompt = new BooleanArgument(null, "no-prompt", 1, CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_ARG_NO_PROMPT_DESC.get());
        trustServerNoPrompt.addLongIdentifier("noPrompt", true);
        trustServerParser.addArgument(trustServerNoPrompt);
        trustServerParser.addRequiredArgumentSet(trustServerKeystorePassword, trustServerKeystorePasswordFile, trustServerPromptForKeystorePassword);
        trustServerParser.addExclusiveArgumentSet(trustServerKeystorePassword, trustServerKeystorePasswordFile, trustServerPromptForKeystorePassword);
        final LinkedHashMap<String[], String> trustServerExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        trustServerExamples.put(new String[] { "trust-server-certificate", "--hostname", "ds.example.com", "--port", "636", "--keystore", getPlatformSpecificPath("config", "truststore"), "--keystore-password-file", getPlatformSpecificPath("config", "truststore.pin"), "--verbose" }, CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_EXAMPLE_1.get(getPlatformSpecificPath("config", "truststore")));
        trustServerExamples.put(new String[] { "trust-server-certificate", "--hostname", "ds.example.com", "--port", "389", "--use-ldap-start-tls", "--keystore", getPlatformSpecificPath("config", "truststore"), "--keystore-password-file", getPlatformSpecificPath("config", "truststore.pin"), "--issuers-only", "--alias", "ds-start-tls-cert", "--no-prompt" }, CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_EXAMPLE_2.get(getPlatformSpecificPath("config", "truststore")));
        final SubCommand trustServerSubCommand = new SubCommand("trust-server-certificate", CertMessages.INFO_MANAGE_CERTS_SC_TRUST_SERVER_DESC.get(), trustServerParser, trustServerExamples);
        trustServerSubCommand.addName("trustServerCertificate", true);
        trustServerSubCommand.addName("trust-server", false);
        trustServerSubCommand.addName("trustServer", true);
        parser.addSubCommand(trustServerSubCommand);
        final ArgumentParser checkUsabilityParser = new ArgumentParser("check-certificate-usability", CertMessages.INFO_MANAGE_CERTS_SC_CHECK_USABILITY_DESC.get());
        final FileArgument checkUsabilityKeystore = new FileArgument(null, "keystore", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHECK_USABILITY_ARG_KS_DESC.get(), true, true, true, false);
        checkUsabilityKeystore.addLongIdentifier("keystore-path", true);
        checkUsabilityKeystore.addLongIdentifier("keystorePath", true);
        checkUsabilityKeystore.addLongIdentifier("keystore-file", true);
        checkUsabilityKeystore.addLongIdentifier("keystoreFile", true);
        checkUsabilityParser.addArgument(checkUsabilityKeystore);
        final StringArgument checkUsabilityKeystorePassword = new StringArgument(null, "keystore-password", false, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_PASSWORD.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHECK_USABILITY_ARG_KS_PW_DESC.get());
        checkUsabilityKeystorePassword.addLongIdentifier("keystorePassword", true);
        checkUsabilityKeystorePassword.addLongIdentifier("keystore-passphrase", true);
        checkUsabilityKeystorePassword.addLongIdentifier("keystorePassphrase", true);
        checkUsabilityKeystorePassword.addLongIdentifier("keystore-pin", true);
        checkUsabilityKeystorePassword.addLongIdentifier("keystorePIN", true);
        checkUsabilityKeystorePassword.addLongIdentifier("storepass", true);
        checkUsabilityKeystorePassword.setSensitive(true);
        checkUsabilityParser.addArgument(checkUsabilityKeystorePassword);
        final FileArgument checkUsabilityKeystorePasswordFile = new FileArgument(null, "keystore-password-file", false, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_CHECK_USABILITY_ARG_KS_PW_FILE_DESC.get(), true, true, true, false);
        checkUsabilityKeystorePasswordFile.addLongIdentifier("keystorePasswordFile", true);
        checkUsabilityKeystorePasswordFile.addLongIdentifier("keystore-passphrase-file", true);
        checkUsabilityKeystorePasswordFile.addLongIdentifier("keystorePassphraseFile", true);
        checkUsabilityKeystorePasswordFile.addLongIdentifier("keystore-pin-file", true);
        checkUsabilityKeystorePasswordFile.addLongIdentifier("keystorePINFile", true);
        checkUsabilityParser.addArgument(checkUsabilityKeystorePasswordFile);
        final BooleanArgument checkUsabilityPromptForKeystorePassword = new BooleanArgument(null, "prompt-for-keystore-password", CertMessages.INFO_MANAGE_CERTS_SC_CHECK_USABILITY_ARG_PROMPT_FOR_KS_PW_DESC.get());
        checkUsabilityPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassword", true);
        checkUsabilityPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-passphrase", true);
        checkUsabilityPromptForKeystorePassword.addLongIdentifier("promptForKeystorePassphrase", true);
        checkUsabilityPromptForKeystorePassword.addLongIdentifier("prompt-for-keystore-pin", true);
        checkUsabilityPromptForKeystorePassword.addLongIdentifier("promptForKeystorePIN", true);
        checkUsabilityParser.addArgument(checkUsabilityPromptForKeystorePassword);
        final StringArgument checkUsabilityAlias = new StringArgument(null, "alias", true, 1, CertMessages.INFO_MANAGE_CERTS_PLACEHOLDER_ALIAS.get(), CertMessages.INFO_MANAGE_CERTS_SC_CHECK_USABILITY_ARG_ALIAS_DESC.get());
        checkUsabilityAlias.addLongIdentifier("nickname", true);
        checkUsabilityParser.addArgument(checkUsabilityAlias);
        final BooleanArgument checkUsabilityIgnoreSHA1Signature = new BooleanArgument(null, "allow-sha-1-signature-for-issuer-certificates", 1, CertMessages.INFO_MANAGE_CERTS_SC_CHECK_USABILITY_IGNORE_SHA1_WARNING_DESC.get());
        checkUsabilityIgnoreSHA1Signature.addLongIdentifier("allow-sha1-signature-for-issuer-certificates", true);
        checkUsabilityIgnoreSHA1Signature.addLongIdentifier("allowSHA1SignatureForIssuerCertificates", true);
        checkUsabilityParser.addArgument(checkUsabilityIgnoreSHA1Signature);
        checkUsabilityParser.addRequiredArgumentSet(checkUsabilityKeystorePassword, checkUsabilityKeystorePasswordFile, checkUsabilityPromptForKeystorePassword);
        checkUsabilityParser.addExclusiveArgumentSet(checkUsabilityKeystorePassword, checkUsabilityKeystorePasswordFile, checkUsabilityPromptForKeystorePassword);
        final LinkedHashMap<String[], String> checkUsabilityExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        checkUsabilityExamples.put(new String[] { "check-certificate-usability", "--keystore", getPlatformSpecificPath("config", "keystore"), "--keystore-password-file", getPlatformSpecificPath("config", "keystore.pin"), "--alias", "server-cert" }, CertMessages.INFO_MANAGE_CERTS_SC_CHECK_USABILITY_EXAMPLE_1.get(getPlatformSpecificPath("config", "keystore")));
        final SubCommand checkUsabilitySubCommand = new SubCommand("check-certificate-usability", CertMessages.INFO_MANAGE_CERTS_SC_CHECK_USABILITY_DESC.get(), checkUsabilityParser, checkUsabilityExamples);
        checkUsabilitySubCommand.addName("checkCertificateUsability", true);
        checkUsabilitySubCommand.addName("check-usability", true);
        checkUsabilitySubCommand.addName("checkUsability", true);
        parser.addSubCommand(checkUsabilitySubCommand);
        final ArgumentParser displayCertParser = new ArgumentParser("display-certificate-file", CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CERT_DESC.get());
        final FileArgument displayCertFile = new FileArgument(null, "certificate-file", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CERT_ARG_FILE_DESC.get(), true, true, true, false);
        displayCertFile.addLongIdentifier("certificateFile", true);
        displayCertFile.addLongIdentifier("input-file", true);
        displayCertFile.addLongIdentifier("inputFile", true);
        displayCertFile.addLongIdentifier("file", true);
        displayCertFile.addLongIdentifier("filename", true);
        displayCertParser.addArgument(displayCertFile);
        final BooleanArgument displayCertVerbose = new BooleanArgument(null, "verbose", 1, CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CERT_ARG_VERBOSE_DESC.get());
        displayCertParser.addArgument(displayCertVerbose);
        final BooleanArgument displayCertDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CERT_ARG_DISPLAY_COMMAND_DESC.get());
        displayCertDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        displayCertDisplayCommand.addLongIdentifier("show-keytool-command", true);
        displayCertDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        displayCertParser.addArgument(displayCertDisplayCommand);
        final LinkedHashMap<String[], String> displayCertExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        displayCertExamples.put(new String[] { "display-certificate-file", "--certificate-file", "certificate.pem" }, CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CERT_EXAMPLE_1.get("certificate.pem"));
        displayCertExamples.put(new String[] { "display-certificate-file", "--certificate-file", "certificate.pem", "--verbose", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CERT_EXAMPLE_2.get("certificate.pem"));
        final SubCommand displayCertSubCommand = new SubCommand("display-certificate-file", CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CERT_DESC.get(), displayCertParser, displayCertExamples);
        displayCertSubCommand.addName("displayCertificateFile", true);
        displayCertSubCommand.addName("display-certificate", false);
        displayCertSubCommand.addName("displayCertificate", true);
        displayCertSubCommand.addName("display-certificates", true);
        displayCertSubCommand.addName("displayCertificates", true);
        displayCertSubCommand.addName("show-certificate", true);
        displayCertSubCommand.addName("showCertificate", true);
        displayCertSubCommand.addName("show-certificate-file", true);
        displayCertSubCommand.addName("showCertificateFile", true);
        displayCertSubCommand.addName("show-certificates", true);
        displayCertSubCommand.addName("showCertificates", true);
        displayCertSubCommand.addName("print-certificate-file", false);
        displayCertSubCommand.addName("printCertificateFile", true);
        displayCertSubCommand.addName("print-certificate", false);
        displayCertSubCommand.addName("printCertificate", true);
        displayCertSubCommand.addName("print-certificates", true);
        displayCertSubCommand.addName("printCertificates", true);
        displayCertSubCommand.addName("printcert", true);
        parser.addSubCommand(displayCertSubCommand);
        final ArgumentParser displayCSRParser = new ArgumentParser("display-certificate-signing-request-file", CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CSR_DESC.get());
        final FileArgument displayCSRFile = new FileArgument(null, "certificate-signing-request-file", true, 1, null, CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CSR_ARG_FILE_DESC.get(), true, true, true, false);
        displayCSRFile.addLongIdentifier("certificateSigningRequestFile", true);
        displayCSRFile.addLongIdentifier("request-file", false);
        displayCSRFile.addLongIdentifier("requestFile", true);
        displayCSRFile.addLongIdentifier("input-file", true);
        displayCSRFile.addLongIdentifier("inputFile", true);
        displayCSRFile.addLongIdentifier("file", true);
        displayCSRFile.addLongIdentifier("filename", true);
        displayCSRParser.addArgument(displayCSRFile);
        final BooleanArgument displayCSRVerbose = new BooleanArgument(null, "verbose", 1, CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CSR_ARG_VERBOSE_DESC.get());
        displayCSRParser.addArgument(displayCSRVerbose);
        final BooleanArgument displayCSRDisplayCommand = new BooleanArgument(null, "display-keytool-command", 1, CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CSR_ARG_DISPLAY_COMMAND_DESC.get());
        displayCSRDisplayCommand.addLongIdentifier("displayKeytoolCommand", true);
        displayCSRDisplayCommand.addLongIdentifier("show-keytool-command", true);
        displayCSRDisplayCommand.addLongIdentifier("showKeytoolCommand", true);
        displayCSRParser.addArgument(displayCSRDisplayCommand);
        final LinkedHashMap<String[], String> displayCSRExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        displayCSRExamples.put(new String[] { "display-certificate-signing-request-file", "--certificate-signing-request-file", "server-cert.csr", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CSR_EXAMPLE_1.get("server-cert.csr"));
        final SubCommand displayCSRSubCommand = new SubCommand("display-certificate-signing-request-file", CertMessages.INFO_MANAGE_CERTS_SC_DISPLAY_CSR_DESC.get(), displayCSRParser, displayCSRExamples);
        displayCSRSubCommand.addName("displayCertificateSigningRequestFile", true);
        displayCSRSubCommand.addName("display-certificate-signing-request", true);
        displayCSRSubCommand.addName("displayCertificateSigningRequest", true);
        displayCSRSubCommand.addName("display-certificate-request-file", true);
        displayCSRSubCommand.addName("displayCertificateRequestFile", true);
        displayCSRSubCommand.addName("display-certificate-request", false);
        displayCSRSubCommand.addName("displayCertificateRequest", true);
        displayCSRSubCommand.addName("display-csr-file", true);
        displayCSRSubCommand.addName("displayCSRFile", true);
        displayCSRSubCommand.addName("display-csr", true);
        displayCSRSubCommand.addName("displayCSR", true);
        displayCSRSubCommand.addName("show-certificate-signing-request-file", true);
        displayCSRSubCommand.addName("showCertificateSigningRequestFile", true);
        displayCSRSubCommand.addName("show-certificate-signing-request", true);
        displayCSRSubCommand.addName("showCertificateSigningRequest", true);
        displayCSRSubCommand.addName("show-certificate-request-file", true);
        displayCSRSubCommand.addName("showCertificateRequestFile", true);
        displayCSRSubCommand.addName("show-certificate-request", true);
        displayCSRSubCommand.addName("showCertificateRequest", true);
        displayCSRSubCommand.addName("show-csr-file", true);
        displayCSRSubCommand.addName("showCSRFile", true);
        displayCSRSubCommand.addName("show-csr", true);
        displayCSRSubCommand.addName("showCSR", true);
        displayCSRSubCommand.addName("print-certificate-signing-request-file", false);
        displayCSRSubCommand.addName("printCertificateSigningRequestFile", true);
        displayCSRSubCommand.addName("print-certificate-signing-request", true);
        displayCSRSubCommand.addName("printCertificateSigningRequest", true);
        displayCSRSubCommand.addName("print-certificate-request-file", true);
        displayCSRSubCommand.addName("printCertificateRequestFile", true);
        displayCSRSubCommand.addName("print-certificate-request", false);
        displayCSRSubCommand.addName("printCertificateRequest", true);
        displayCSRSubCommand.addName("print-csr-file", true);
        displayCSRSubCommand.addName("printCSRFile", true);
        displayCSRSubCommand.addName("print-csr", true);
        displayCSRSubCommand.addName("printCSR", true);
        displayCSRSubCommand.addName("printcertreq", true);
        parser.addSubCommand(displayCSRSubCommand);
    }
    
    private static String getPlatformSpecificPath(final String... pathElements) {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < pathElements.length; ++i) {
            if (i > 0) {
                buffer.append(File.separatorChar);
            }
            buffer.append(pathElements[i]);
        }
        return buffer.toString();
    }
    
    @Override
    public ResultCode doToolProcessing() {
        final SubCommand selectedSubCommand = this.globalParser.getSelectedSubCommand();
        if (selectedSubCommand == null) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_NO_SUBCOMMAND.get());
            return ResultCode.PARAM_ERROR;
        }
        this.subCommandParser = selectedSubCommand.getArgumentParser();
        if (selectedSubCommand.hasName("list-certificates")) {
            return this.doListCertificates();
        }
        if (selectedSubCommand.hasName("export-certificate")) {
            return this.doExportCertificate();
        }
        if (selectedSubCommand.hasName("export-private-key")) {
            return this.doExportPrivateKey();
        }
        if (selectedSubCommand.hasName("import-certificate")) {
            return this.doImportCertificate();
        }
        if (selectedSubCommand.hasName("delete-certificate")) {
            return this.doDeleteCertificate();
        }
        if (selectedSubCommand.hasName("generate-self-signed-certificate")) {
            return this.doGenerateOrSignCertificateOrCSR();
        }
        if (selectedSubCommand.hasName("generate-certificate-signing-request")) {
            return this.doGenerateOrSignCertificateOrCSR();
        }
        if (selectedSubCommand.hasName("sign-certificate-signing-request")) {
            return this.doGenerateOrSignCertificateOrCSR();
        }
        if (selectedSubCommand.hasName("change-certificate-alias")) {
            return this.doChangeCertificateAlias();
        }
        if (selectedSubCommand.hasName("change-keystore-password")) {
            return this.doChangeKeystorePassword();
        }
        if (selectedSubCommand.hasName("change-private-key-password")) {
            return this.doChangePrivateKeyPassword();
        }
        if (selectedSubCommand.hasName("trust-server-certificate")) {
            return this.doTrustServerCertificate();
        }
        if (selectedSubCommand.hasName("check-certificate-usability")) {
            return this.doCheckCertificateUsability();
        }
        if (selectedSubCommand.hasName("display-certificate-file")) {
            return this.doDisplayCertificateFile();
        }
        if (selectedSubCommand.hasName("display-certificate-signing-request-file")) {
            return this.doDisplayCertificateSigningRequestFile();
        }
        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_UNKNOWN_SUBCOMMAND.get(selectedSubCommand.getPrimaryName()));
        return ResultCode.PARAM_ERROR;
    }
    
    private ResultCode doListCertificates() {
        final BooleanArgument displayPEMArgument = this.subCommandParser.getBooleanArgument("display-pem-certificate");
        final boolean displayPEM = displayPEMArgument != null && displayPEMArgument.isPresent();
        final BooleanArgument verboseArgument = this.subCommandParser.getBooleanArgument("verbose");
        final boolean verbose = verboseArgument != null && verboseArgument.isPresent();
        final StringArgument aliasArgument = this.subCommandParser.getStringArgument("alias");
        Set<String> aliases;
        Map<String, String> missingAliases;
        if (aliasArgument == null || !aliasArgument.isPresent()) {
            aliases = Collections.emptySet();
            missingAliases = Collections.emptyMap();
        }
        else {
            final List<String> values = aliasArgument.getValues();
            aliases = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(values.size()));
            missingAliases = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(values.size()));
            for (final String alias : values) {
                final String lowerAlias = StaticUtils.toLowerCase(alias);
                aliases.add(StaticUtils.toLowerCase(lowerAlias));
                missingAliases.put(lowerAlias, alias);
            }
        }
        final File keystorePath = this.getKeystorePath();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        if (displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent()) {
            final ArrayList<String> keytoolArgs = new ArrayList<String>(10);
            keytoolArgs.add("-list");
            keytoolArgs.add("-keystore");
            keytoolArgs.add(keystorePath.getAbsolutePath());
            keytoolArgs.add("-storetype");
            keytoolArgs.add(keystoreType);
            if (keystorePassword != null) {
                keytoolArgs.add("-storepass");
                keytoolArgs.add("*****REDACTED*****");
            }
            for (final String alias2 : missingAliases.values()) {
                keytoolArgs.add("-alias");
                keytoolArgs.add(alias2);
            }
            if (displayPEM) {
                keytoolArgs.add("-rfc");
            }
            if (verbose) {
                keytoolArgs.add("-v");
            }
            this.displayKeytoolCommand(keytoolArgs);
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        Enumeration<String> aliasEnumeration;
        try {
            aliasEnumeration = keystore.aliases();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.err(new Object[0]);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_LIST_CERTS_CANNOT_GET_ALIASES.get(keystorePath.getAbsolutePath()));
            e.printStackTrace(this.getErr());
            return ResultCode.LOCAL_ERROR;
        }
        int listedCount = 0;
        ResultCode resultCode = ResultCode.SUCCESS;
        while (aliasEnumeration.hasMoreElements()) {
            final String alias3 = aliasEnumeration.nextElement();
            final String lowerAlias2 = StaticUtils.toLowerCase(alias3);
            if (!aliases.isEmpty() && missingAliases.remove(lowerAlias2) == null) {
                continue;
            }
            X509Certificate[] certificateChain;
            try {
                Certificate[] chain = keystore.getCertificateChain(alias3);
                if (chain == null || chain.length == 0) {
                    final Certificate cert = keystore.getCertificate(alias3);
                    if (cert == null) {
                        continue;
                    }
                    chain = new Certificate[] { cert };
                }
                certificateChain = new X509Certificate[chain.length];
                for (int i = 0; i < chain.length; ++i) {
                    certificateChain[i] = new X509Certificate(chain[i].getEncoded());
                }
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                this.err(new Object[0]);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_LIST_CERTS_ERROR_GETTING_CERT.get(alias3, StaticUtils.getExceptionMessage(e2)));
                resultCode = ResultCode.LOCAL_ERROR;
                continue;
            }
            ++listedCount;
            for (int j = 0; j < certificateChain.length; ++j) {
                this.out(new Object[0]);
                if (certificateChain.length == 1) {
                    this.out(CertMessages.INFO_MANAGE_CERTS_LIST_CERTS_LABEL_ALIAS_WITHOUT_CHAIN.get(alias3));
                }
                else {
                    this.out(CertMessages.INFO_MANAGE_CERTS_LIST_CERTS_LABEL_ALIAS_WITH_CHAIN.get(alias3, j + 1, certificateChain.length));
                }
                this.printCertificate(certificateChain[j], "", verbose);
                if (j == 0) {
                    if (hasKeyAlias(keystore, alias3)) {
                        this.out(CertMessages.INFO_MANAGE_CERTS_LIST_CERTS_LABEL_HAS_PK_YES.get());
                    }
                    else {
                        this.out(CertMessages.INFO_MANAGE_CERTS_LIST_CERTS_LABEL_HAS_PK_NO.get());
                    }
                }
                CertException signatureVerificationException = null;
                if (certificateChain[j].isSelfSigned()) {
                    try {
                        certificateChain[j].verifySignature(null);
                    }
                    catch (final CertException ce) {
                        Debug.debugException(ce);
                        signatureVerificationException = ce;
                    }
                }
                else {
                    X509Certificate issuerCertificate = null;
                    try {
                        final AtomicReference<KeyStore> jvmDefaultTrustStoreRef = new AtomicReference<KeyStore>();
                        final AtomicReference<DN> missingIssuerRef = new AtomicReference<DN>();
                        issuerCertificate = getIssuerCertificate(certificateChain[j], keystore, jvmDefaultTrustStoreRef, missingIssuerRef);
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                    }
                    if (issuerCertificate == null) {
                        signatureVerificationException = new CertException(CertMessages.ERR_MANAGE_CERTS_LIST_CERTS_VERIFY_SIGNATURE_NO_ISSUER.get(certificateChain[j].getIssuerDN()));
                    }
                    else {
                        try {
                            certificateChain[j].verifySignature(issuerCertificate);
                        }
                        catch (final CertException ce2) {
                            Debug.debugException(ce2);
                            signatureVerificationException = ce2;
                        }
                    }
                }
                if (signatureVerificationException == null) {
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_LIST_CERTS_SIGNATURE_VALID.get());
                }
                else {
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, signatureVerificationException.getMessage());
                }
                if (displayPEM) {
                    this.out(CertMessages.INFO_MANAGE_CERTS_LIST_CERTS_LABEL_PEM.get());
                    writePEMCertificate(this.getOut(), certificateChain[j].getX509CertificateBytes());
                }
            }
        }
        if (!missingAliases.isEmpty()) {
            this.err(new Object[0]);
            for (final String missingAlias : missingAliases.values()) {
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_LIST_CERTS_ALIAS_NOT_IN_KS.get(missingAlias, keystorePath.getAbsolutePath()));
                resultCode = ResultCode.PARAM_ERROR;
            }
        }
        else if (listedCount == 0) {
            this.out(new Object[0]);
            if (keystorePassword == null) {
                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_LIST_CERTS_NO_CERTS_OR_KEYS_WITHOUT_PW.get());
            }
            else {
                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_LIST_CERTS_NO_CERTS_OR_KEYS_WITH_PW.get());
            }
        }
        return resultCode;
    }
    
    private ResultCode doExportCertificate() {
        final StringArgument aliasArgument = this.subCommandParser.getStringArgument("alias");
        final String alias = aliasArgument.getValue();
        final BooleanArgument exportChainArgument = this.subCommandParser.getBooleanArgument("export-certificate-chain");
        final boolean exportChain = exportChainArgument != null && exportChainArgument.isPresent();
        final BooleanArgument separateFilePerCertificateArgument = this.subCommandParser.getBooleanArgument("separate-file-per-certificate");
        final boolean separateFilePerCertificate = separateFilePerCertificateArgument != null && separateFilePerCertificateArgument.isPresent();
        boolean exportPEM = true;
        final StringArgument outputFormatArgument = this.subCommandParser.getStringArgument("output-format");
        if (outputFormatArgument != null && outputFormatArgument.isPresent()) {
            final String format = outputFormatArgument.getValue().toLowerCase();
            if (format.equals("der") || format.equals("binary") || format.equals("bin")) {
                exportPEM = false;
            }
        }
        File outputFile = null;
        final FileArgument outputFileArgument = this.subCommandParser.getFileArgument("output-file");
        if (outputFileArgument != null && outputFileArgument.isPresent()) {
            outputFile = outputFileArgument.getValue();
        }
        if (outputFile == null && !exportPEM) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_CERT_NO_FILE_WITH_DER.get());
            return ResultCode.PARAM_ERROR;
        }
        final File keystorePath = this.getKeystorePath();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        if (displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent()) {
            final ArrayList<String> keytoolArgs = new ArrayList<String>(10);
            keytoolArgs.add("-list");
            keytoolArgs.add("-keystore");
            keytoolArgs.add(keystorePath.getAbsolutePath());
            keytoolArgs.add("-storetype");
            keytoolArgs.add(keystoreType);
            if (keystorePassword != null) {
                keytoolArgs.add("-storepass");
                keytoolArgs.add("*****REDACTED*****");
            }
            keytoolArgs.add("-alias");
            keytoolArgs.add(alias);
            if (exportPEM) {
                keytoolArgs.add("-rfc");
            }
            if (outputFile != null) {
                keytoolArgs.add("-file");
                keytoolArgs.add(outputFile.getAbsolutePath());
            }
            this.displayKeytoolCommand(keytoolArgs);
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        DN missingIssuerDN = null;
        X509Certificate[] certificatesToExport = null;
        Label_0696: {
            if (exportChain) {
                try {
                    final AtomicReference<DN> missingIssuerRef = new AtomicReference<DN>();
                    certificatesToExport = getCertificateChain(alias, keystore, missingIssuerRef);
                    missingIssuerDN = missingIssuerRef.get();
                    break Label_0696;
                }
                catch (final LDAPException le4) {
                    Debug.debugException(le4);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
                    return le4.getResultCode();
                }
            }
            try {
                final Certificate cert = keystore.getCertificate(alias);
                if (cert == null) {
                    certificatesToExport = new X509Certificate[0];
                }
                else {
                    certificatesToExport = new X509Certificate[] { new X509Certificate(cert.getEncoded()) };
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_CERT_ERROR_GETTING_CERT.get(alias, keystorePath.getAbsolutePath()));
                e.printStackTrace(this.getErr());
                return ResultCode.LOCAL_ERROR;
            }
        }
        if (certificatesToExport.length == 0) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_CERT_NO_CERT_WITH_ALIAS.get(alias, keystorePath.getAbsolutePath()));
            return ResultCode.PARAM_ERROR;
        }
        int fileCounter = 1;
        String filename = null;
        PrintStream printStream;
        if (outputFile == null) {
            printStream = this.getOut();
        }
        else {
            try {
                if (certificatesToExport.length > 1 && separateFilePerCertificate) {
                    filename = outputFile.getAbsolutePath() + '.' + fileCounter;
                }
                else {
                    filename = outputFile.getAbsolutePath();
                }
                printStream = new PrintStream(filename);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_CERT_ERROR_OPENING_OUTPUT.get(outputFile.getAbsolutePath()));
                e2.printStackTrace(this.getErr());
                return ResultCode.LOCAL_ERROR;
            }
        }
        try {
            for (final X509Certificate certificate : certificatesToExport) {
                try {
                    if (separateFilePerCertificate && certificatesToExport.length > 1) {
                        if (fileCounter > 1) {
                            printStream.close();
                            filename = outputFile.getAbsolutePath() + '.' + fileCounter;
                            printStream = new PrintStream(filename);
                        }
                        ++fileCounter;
                    }
                    if (exportPEM) {
                        writePEMCertificate(printStream, certificate.getX509CertificateBytes());
                    }
                    else {
                        printStream.write(certificate.getX509CertificateBytes());
                    }
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_CERT_ERROR_WRITING_CERT.get(alias, certificate.getSubjectDN()));
                    e3.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
                if (outputFile != null) {
                    this.out(new Object[0]);
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_EXPORT_CERT_EXPORT_SUCCESSFUL.get(filename));
                    this.printCertificate(certificate, "", false);
                }
            }
        }
        finally {
            printStream.flush();
            if (outputFile != null) {
                printStream.close();
            }
        }
        if (missingIssuerDN != null) {
            this.err(new Object[0]);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_EXPORT_CERT_MISSING_CERT_IN_CHAIN.get(missingIssuerDN, keystorePath.getAbsolutePath()));
            return ResultCode.NO_SUCH_OBJECT;
        }
        return ResultCode.SUCCESS;
    }
    
    private ResultCode doExportPrivateKey() {
        final StringArgument aliasArgument = this.subCommandParser.getStringArgument("alias");
        final String alias = aliasArgument.getValue();
        boolean exportPEM = true;
        final StringArgument outputFormatArgument = this.subCommandParser.getStringArgument("output-format");
        if (outputFormatArgument != null && outputFormatArgument.isPresent()) {
            final String format = outputFormatArgument.getValue().toLowerCase();
            if (format.equals("der") || format.equals("binary") || format.equals("bin")) {
                exportPEM = false;
            }
        }
        File outputFile = null;
        final FileArgument outputFileArgument = this.subCommandParser.getFileArgument("output-file");
        if (outputFileArgument != null && outputFileArgument.isPresent()) {
            outputFile = outputFileArgument.getValue();
        }
        if (outputFile == null && !exportPEM) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_KEY_NO_FILE_WITH_DER.get());
            return ResultCode.PARAM_ERROR;
        }
        final File keystorePath = this.getKeystorePath();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        char[] privateKeyPassword;
        try {
            privateKeyPassword = this.getPrivateKeyPassword(keystore, alias, keystorePassword);
        }
        catch (final LDAPException le4) {
            Debug.debugException(le4);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
            return le4.getResultCode();
        }
        PrivateKey privateKey;
        try {
            final Key key = keystore.getKey(alias, privateKeyPassword);
            if (key == null) {
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_KEY_NO_KEY_WITH_ALIAS.get(alias, keystorePath.getAbsolutePath()));
                return ResultCode.PARAM_ERROR;
            }
            privateKey = (PrivateKey)key;
        }
        catch (final UnrecoverableKeyException e) {
            Debug.debugException(e);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_KEY_WRONG_KEY_PW.get(alias, keystorePath.getAbsolutePath()));
            return ResultCode.PARAM_ERROR;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_KEY_ERROR_GETTING_KEY.get(alias, keystorePath.getAbsolutePath()));
            e2.printStackTrace(this.getErr());
            return ResultCode.LOCAL_ERROR;
        }
        Label_0592: {
            if (outputFile == null) {
                final PrintStream printStream = this.getOut();
                break Label_0592;
            }
            PrintStream printStream;
            try {
                printStream = new PrintStream(outputFile);
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_KEY_ERROR_OPENING_OUTPUT.get(outputFile.getAbsolutePath()));
                e3.printStackTrace(this.getErr());
                return ResultCode.LOCAL_ERROR;
            }
            try {
                try {
                    if (exportPEM) {
                        writePEMPrivateKey(printStream, privateKey.getEncoded());
                    }
                    else {
                        printStream.write(privateKey.getEncoded());
                    }
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_EXPORT_KEY_ERROR_WRITING_KEY.get(alias));
                    e3.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
                if (outputFile != null) {
                    this.out(new Object[0]);
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_EXPORT_KEY_EXPORT_SUCCESSFUL.get());
                }
            }
            finally {
                printStream.flush();
                if (outputFile != null) {
                    printStream.close();
                }
            }
        }
        return ResultCode.SUCCESS;
    }
    
    private ResultCode doImportCertificate() {
        final StringArgument aliasArgument = this.subCommandParser.getStringArgument("alias");
        final String alias = aliasArgument.getValue();
        final FileArgument certificateFileArgument = this.subCommandParser.getFileArgument("certificate-file");
        final List<File> certFiles = certificateFileArgument.getValues();
        final FileArgument privateKeyFileArgument = this.subCommandParser.getFileArgument("private-key-file");
        File privateKeyFile;
        if (privateKeyFileArgument != null && privateKeyFileArgument.isPresent()) {
            privateKeyFile = privateKeyFileArgument.getValue();
        }
        else {
            privateKeyFile = null;
        }
        final BooleanArgument noPromptArgument = this.subCommandParser.getBooleanArgument("no-prompt");
        final boolean noPrompt = noPromptArgument != null && noPromptArgument.isPresent();
        final File keystorePath = this.getKeystorePath();
        final boolean isNewKeystore = !keystorePath.exists();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        final ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>(5);
        for (final File certFile : certFiles) {
            try {
                final List<X509Certificate> certs = readCertificatesFromFile(certFile);
                if (certs.isEmpty()) {
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_NO_CERTS_IN_FILE.get(certFile.getAbsolutePath()));
                    return ResultCode.PARAM_ERROR;
                }
                certList.addAll(certs);
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
                return le3.getResultCode();
            }
        }
        PKCS8PrivateKey privateKey;
        if (privateKeyFile == null) {
            privateKey = null;
        }
        else {
            try {
                privateKey = readPrivateKeyFromFile(privateKeyFile);
            }
            catch (final LDAPException le4) {
                Debug.debugException(le4);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
                return le4.getResultCode();
            }
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        char[] privateKeyPassword;
        try {
            privateKeyPassword = this.getPrivateKeyPassword(keystore, alias, keystorePassword);
        }
        catch (final LDAPException le5) {
            Debug.debugException(le5);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le5.getMessage());
            return le5.getResultCode();
        }
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        if (displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent()) {
            final ArrayList<String> keytoolArgs = new ArrayList<String>(10);
            keytoolArgs.add("-import");
            keytoolArgs.add("-keystore");
            keytoolArgs.add(keystorePath.getAbsolutePath());
            keytoolArgs.add("-storetype");
            keytoolArgs.add(keystoreType);
            keytoolArgs.add("-storepass");
            keytoolArgs.add("*****REDACTED*****");
            keytoolArgs.add("-keypass");
            keytoolArgs.add("*****REDACTED*****");
            keytoolArgs.add("-alias");
            keytoolArgs.add(alias);
            keytoolArgs.add("-file");
            keytoolArgs.add(certFiles.get(0).getAbsolutePath());
            keytoolArgs.add("-trustcacerts");
            this.displayKeytoolCommand(keytoolArgs);
        }
        final Iterator<X509Certificate> certIterator = certList.iterator();
        X509Certificate subjectCert = certIterator.next();
        while (!subjectCert.isSelfSigned() || !certIterator.hasNext()) {
            if (!certIterator.hasNext()) {
                ArrayList<X509Certificate> chain;
                if (certList.get(certList.size() - 1).isSelfSigned()) {
                    chain = certList;
                }
                else {
                    chain = new ArrayList<X509Certificate>(certList.size() + 5);
                    chain.addAll(certList);
                    final AtomicReference<KeyStore> jvmDefaultTrustStoreRef = new AtomicReference<KeyStore>();
                    final AtomicReference<DN> missingIssuerRef = new AtomicReference<DN>();
                    X509Certificate c = certList.get(certList.size() - 1);
                    while (!c.isSelfSigned()) {
                        X509Certificate issuer;
                        try {
                            issuer = getIssuerCertificate(c, keystore, jvmDefaultTrustStoreRef, missingIssuerRef);
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_CANNOT_GET_ISSUER.get(c.getIssuerDN()));
                            e.printStackTrace(this.getErr());
                            return ResultCode.LOCAL_ERROR;
                        }
                        if (issuer == null) {
                            final byte[] authorityKeyIdentifier = getAuthorityKeyIdentifier(c);
                            if (privateKey != null || hasKeyAlias(keystore, alias)) {
                                if (authorityKeyIdentifier == null) {
                                    this.err(new Object[0]);
                                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_NO_ISSUER_NO_AKI.get(c.getIssuerDN()));
                                }
                                else {
                                    this.err(new Object[0]);
                                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_NO_ISSUER_WITH_AKI.get(c.getIssuerDN(), toColonDelimitedHex(authorityKeyIdentifier)));
                                }
                                return ResultCode.PARAM_ERROR;
                            }
                            if (authorityKeyIdentifier == null) {
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_IMPORT_CERT_NO_ISSUER_NO_AKI.get(c.getIssuerDN()));
                                break;
                            }
                            this.err(new Object[0]);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_IMPORT_CERT_NO_ISSUER_WITH_AKI.get(c.getIssuerDN(), toColonDelimitedHex(authorityKeyIdentifier)));
                            break;
                        }
                        else {
                            chain.add(issuer);
                            c = issuer;
                        }
                    }
                }
                if (privateKey != null) {
                    if (hasKeyAlias(keystore, alias)) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_WITH_PK_KEY_ALIAS_CONFLICT.get(alias));
                        return ResultCode.PARAM_ERROR;
                    }
                    if (hasCertificateAlias(keystore, alias)) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_WITH_PK_CERT_ALIAS_CONFLICT.get(alias));
                        return ResultCode.PARAM_ERROR;
                    }
                    PrivateKey javaPrivateKey;
                    try {
                        javaPrivateKey = privateKey.toPrivateKey();
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_ERROR_CONVERTING_KEY.get(privateKeyFile.getAbsolutePath()));
                        e2.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    final Certificate[] javaCertificateChain = new Certificate[chain.size()];
                    for (int i = 0; i < javaCertificateChain.length; ++i) {
                        final X509Certificate c2 = chain.get(i);
                        try {
                            javaCertificateChain[i] = c2.toCertificate();
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_ERROR_CONVERTING_CERT.get(c2.getSubjectDN()));
                            e.printStackTrace(this.getErr());
                            return ResultCode.LOCAL_ERROR;
                        }
                    }
                    if (!noPrompt) {
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_CONFIRM_IMPORT_CHAIN_NEW_KEY.get(alias));
                        for (final X509Certificate c2 : chain) {
                            this.out(new Object[0]);
                            this.printCertificate(c2, "", false);
                        }
                        this.out(new Object[0]);
                        try {
                            if (!this.promptForYesNo(CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_PROMPT_IMPORT_CHAIN.get())) {
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_CANCELED.get());
                                return ResultCode.USER_CANCELED;
                            }
                        }
                        catch (final LDAPException le6) {
                            Debug.debugException(le6);
                            this.err(new Object[0]);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le6.getMessage());
                            return le6.getResultCode();
                        }
                    }
                    try {
                        keystore.setKeyEntry(alias, javaPrivateKey, privateKeyPassword, javaCertificateChain);
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_ERROR_UPDATING_KS_WITH_CHAIN.get(alias));
                        e3.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    try {
                        writeKeystore(keystore, keystorePath, keystorePassword);
                    }
                    catch (final LDAPException le6) {
                        Debug.debugException(le6);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le6.getMessage());
                        return le6.getResultCode();
                    }
                    if (isNewKeystore) {
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_CREATED_KEYSTORE.get(getUserFriendlyKeystoreType(keystoreType)));
                    }
                    this.out(new Object[0]);
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_IMPORTED_CHAIN_WITH_PK.get());
                    return ResultCode.SUCCESS;
                }
                else {
                    if (hasCertificateAlias(keystore, alias)) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_WITH_CONFLICTING_CERT_ALIAS.get(alias));
                        return ResultCode.PARAM_ERROR;
                    }
                    if (!hasKeyAlias(keystore, alias)) {
                        final LinkedHashMap<String, X509Certificate> certMap = new LinkedHashMap<String, X509Certificate>(StaticUtils.computeMapCapacity(certList.size()));
                        for (int j = 0; j < certList.size(); ++j) {
                            final X509Certificate x509Certificate = certList.get(j);
                            Certificate javaCertificate;
                            try {
                                javaCertificate = x509Certificate.toCertificate();
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_ERROR_CONVERTING_CERT.get(x509Certificate.getSubjectDN()));
                                e.printStackTrace(this.getErr());
                                return ResultCode.LOCAL_ERROR;
                            }
                            String certAlias;
                            if (j == 0) {
                                certAlias = alias;
                            }
                            else if (certList.size() > 2) {
                                certAlias = alias + "-issuer-" + j;
                            }
                            else {
                                certAlias = alias + "-issuer";
                            }
                            certMap.put(certAlias, x509Certificate);
                            if (hasKeyAlias(keystore, certAlias) || hasCertificateAlias(keystore, certAlias)) {
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_WITH_CONFLICTING_ISSUER_ALIAS.get(x509Certificate.getSubjectDN(), certAlias));
                                return ResultCode.PARAM_ERROR;
                            }
                            try {
                                keystore.setCertificateEntry(certAlias, javaCertificate);
                            }
                            catch (final Exception e4) {
                                Debug.debugException(e4);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_ERROR_UPDATING_KS_WITH_CERT.get(x509Certificate.getSubjectDN(), alias));
                                e4.printStackTrace(this.getErr());
                                return ResultCode.LOCAL_ERROR;
                            }
                        }
                        if (!noPrompt) {
                            this.out(new Object[0]);
                            this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_CONFIRM_IMPORT_CHAIN_NO_KEY.get(alias));
                            for (final Map.Entry<String, X509Certificate> e5 : certMap.entrySet()) {
                                this.out(new Object[0]);
                                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_LABEL_ALIAS.get(e5.getKey()));
                                this.printCertificate(e5.getValue(), "", false);
                            }
                            this.out(new Object[0]);
                            try {
                                if (!this.promptForYesNo(CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_PROMPT_IMPORT_CHAIN.get())) {
                                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_CANCELED.get());
                                    return ResultCode.USER_CANCELED;
                                }
                            }
                            catch (final LDAPException le7) {
                                Debug.debugException(le7);
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le7.getMessage());
                                return le7.getResultCode();
                            }
                        }
                        try {
                            writeKeystore(keystore, keystorePath, keystorePassword);
                        }
                        catch (final LDAPException le7) {
                            Debug.debugException(le7);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le7.getMessage());
                            return le7.getResultCode();
                        }
                        this.out(new Object[0]);
                        if (isNewKeystore) {
                            this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_CREATED_KEYSTORE.get(getUserFriendlyKeystoreType(keystoreType)));
                        }
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_IMPORTED_CHAIN_WITHOUT_PK.get());
                        return ResultCode.SUCCESS;
                    }
                    PrivateKey existingPrivateKey;
                    X509Certificate existingEndCertificate;
                    try {
                        existingPrivateKey = (PrivateKey)keystore.getKey(alias, privateKeyPassword);
                        final Certificate[] existingChain = keystore.getCertificateChain(alias);
                        existingEndCertificate = new X509Certificate(existingChain[0].getEncoded());
                    }
                    catch (final Exception e6) {
                        Debug.debugException(e6);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_INTO_KEY_ALIAS_CANNOT_GET_KEY.get(alias));
                        e6.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    final boolean[] existingPublicKeyBits = existingEndCertificate.getEncodedPublicKey().getBits();
                    final boolean[] newPublicKeyBits = chain.get(0).getEncodedPublicKey().getBits();
                    if (!Arrays.equals(existingPublicKeyBits, newPublicKeyBits)) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_INTO_KEY_ALIAS_KEY_MISMATCH.get(alias));
                        return ResultCode.PARAM_ERROR;
                    }
                    final Certificate[] newChain = new Certificate[chain.size()];
                    for (int k = 0; k < chain.size(); ++k) {
                        final X509Certificate c3 = chain.get(k);
                        try {
                            newChain[k] = c3.toCertificate();
                        }
                        catch (final Exception e7) {
                            Debug.debugException(e7);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_ERROR_CONVERTING_CERT.get(c3.getSubjectDN()));
                            e7.printStackTrace(this.getErr());
                            return ResultCode.LOCAL_ERROR;
                        }
                    }
                    if (!noPrompt) {
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_CONFIRM_IMPORT_CHAIN_EXISTING_KEY.get(alias));
                        for (final X509Certificate c3 : chain) {
                            this.out(new Object[0]);
                            this.printCertificate(c3, "", false);
                        }
                        this.out(new Object[0]);
                        try {
                            if (!this.promptForYesNo(CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_PROMPT_IMPORT_CHAIN.get())) {
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_CANCELED.get());
                                return ResultCode.USER_CANCELED;
                            }
                        }
                        catch (final LDAPException le8) {
                            Debug.debugException(le8);
                            this.err(new Object[0]);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le8.getMessage());
                            return le8.getResultCode();
                        }
                    }
                    try {
                        keystore.setKeyEntry(alias, existingPrivateKey, privateKeyPassword, newChain);
                    }
                    catch (final Exception e8) {
                        Debug.debugException(e8);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_ERROR_UPDATING_KS_WITH_CHAIN.get(alias));
                        e8.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    try {
                        writeKeystore(keystore, keystorePath, keystorePassword);
                    }
                    catch (final LDAPException le8) {
                        Debug.debugException(le8);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le8.getMessage());
                        return le8.getResultCode();
                    }
                    this.out(new Object[0]);
                    if (isNewKeystore) {
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_CREATED_KEYSTORE.get(getUserFriendlyKeystoreType(keystoreType)));
                    }
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_IMPORT_CERT_IMPORTED_CHAIN_WITHOUT_PK.get());
                    return ResultCode.SUCCESS;
                }
            }
            else {
                final X509Certificate issuerCert = certIterator.next();
                final StringBuilder notIssuerReason = new StringBuilder();
                if (!issuerCert.isIssuerFor(subjectCert, notIssuerReason)) {
                    if (!Arrays.equals(issuerCert.getX509CertificateBytes(), subjectCert.getX509CertificateBytes())) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_NEXT_NOT_ISSUER_OF_PREV.get(notIssuerReason.toString()));
                        return ResultCode.PARAM_ERROR;
                    }
                    certIterator.remove();
                }
                subjectCert = issuerCert;
            }
        }
        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_IMPORT_CERT_SELF_SIGNED_NOT_LAST.get(subjectCert.getSubjectDN()));
        return ResultCode.PARAM_ERROR;
    }
    
    private ResultCode doDeleteCertificate() {
        final StringArgument aliasArgument = this.subCommandParser.getStringArgument("alias");
        final String alias = aliasArgument.getValue();
        final BooleanArgument noPromptArgument = this.subCommandParser.getBooleanArgument("no-prompt");
        final boolean noPrompt = noPromptArgument != null && noPromptArgument.isPresent();
        final File keystorePath = this.getKeystorePath();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        if (displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent()) {
            final ArrayList<String> keytoolArgs = new ArrayList<String>(10);
            keytoolArgs.add("-delete");
            keytoolArgs.add("-keystore");
            keytoolArgs.add(keystorePath.getAbsolutePath());
            keytoolArgs.add("-storetype");
            keytoolArgs.add(keystoreType);
            keytoolArgs.add("-storepass");
            keytoolArgs.add("*****REDACTED*****");
            keytoolArgs.add("-alias");
            keytoolArgs.add(alias);
            this.displayKeytoolCommand(keytoolArgs);
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        final ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>(5);
        boolean hasPrivateKey = false;
        Label_0553: {
            if (hasCertificateAlias(keystore, alias)) {
                try {
                    hasPrivateKey = false;
                    certList.add(new X509Certificate(keystore.getCertificate(alias).getEncoded()));
                    break Label_0553;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_DELETE_CERT_ERROR_GETTING_CERT.get(alias));
                    e.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
            }
            if (hasKeyAlias(keystore, alias)) {
                try {
                    hasPrivateKey = true;
                    for (final Certificate c : keystore.getCertificateChain(alias)) {
                        certList.add(new X509Certificate(c.getEncoded()));
                    }
                    break Label_0553;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_DELETE_CERT_ERROR_GETTING_CHAIN.get(alias));
                    e.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
            }
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_DELETE_CERT_ERROR_ALIAS_NOT_CERT_OR_KEY.get(alias));
            return ResultCode.PARAM_ERROR;
        }
        if (!noPrompt) {
            this.out(new Object[0]);
            if (!hasPrivateKey) {
                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_DELETE_CERT_CONFIRM_DELETE_CERT.get());
            }
            else {
                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_DELETE_CERT_CONFIRM_DELETE_CHAIN.get());
            }
            for (final X509Certificate c2 : certList) {
                this.out(new Object[0]);
                this.printCertificate(c2, "", false);
            }
            this.out(new Object[0]);
            try {
                if (!this.promptForYesNo(CertMessages.INFO_MANAGE_CERTS_DELETE_CERT_PROMPT_DELETE.get())) {
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_DELETE_CERT_CANCELED.get());
                    return ResultCode.USER_CANCELED;
                }
            }
            catch (final LDAPException le4) {
                Debug.debugException(le4);
                this.err(new Object[0]);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
                return le4.getResultCode();
            }
        }
        try {
            keystore.deleteEntry(alias);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_DELETE_CERT_DELETE_ERROR.get(alias));
            e.printStackTrace(this.getErr());
            return ResultCode.LOCAL_ERROR;
        }
        try {
            writeKeystore(keystore, keystorePath, keystorePassword);
        }
        catch (final LDAPException le4) {
            Debug.debugException(le4);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
            return le4.getResultCode();
        }
        if (certList.size() == 1) {
            this.out(new Object[0]);
            this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_DELETE_CERT_DELETED_CERT.get());
        }
        else {
            this.out(new Object[0]);
            this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_DELETE_CERT_DELETED_CHAIN.get());
        }
        return ResultCode.SUCCESS;
    }
    
    private ResultCode doGenerateOrSignCertificateOrCSR() {
        final SubCommand selectedSubCommand = this.globalParser.getSelectedSubCommand();
        boolean isGenerateCertificate;
        boolean isGenerateCSR;
        boolean isSignCSR;
        if (selectedSubCommand.hasName("generate-self-signed-certificate")) {
            isGenerateCertificate = true;
            isGenerateCSR = false;
            isSignCSR = false;
        }
        else if (selectedSubCommand.hasName("generate-certificate-signing-request")) {
            isGenerateCertificate = false;
            isGenerateCSR = true;
            isSignCSR = false;
        }
        else {
            Validator.ensureTrue(selectedSubCommand.hasName("sign-certificate-signing-request"));
            isGenerateCertificate = false;
            isGenerateCSR = false;
            isSignCSR = true;
        }
        final StringArgument aliasArgument = this.subCommandParser.getStringArgument("alias");
        final String alias = aliasArgument.getValue();
        final File keystorePath = this.getKeystorePath();
        final boolean isNewKeystore = !keystorePath.exists();
        DN subjectDN = null;
        final DNArgument subjectDNArgument = this.subCommandParser.getDNArgument("subject-dn");
        if (subjectDNArgument != null && subjectDNArgument.isPresent()) {
            subjectDN = subjectDNArgument.getValue();
        }
        File inputFile = null;
        final FileArgument inputFileArgument = this.subCommandParser.getFileArgument("input-file");
        if (inputFileArgument != null && inputFileArgument.isPresent()) {
            inputFile = inputFileArgument.getValue();
        }
        File outputFile = null;
        final FileArgument outputFileArgument = this.subCommandParser.getFileArgument("output-file");
        if (outputFileArgument != null && outputFileArgument.isPresent()) {
            outputFile = outputFileArgument.getValue();
        }
        boolean outputPEM = true;
        final StringArgument outputFormatArgument = this.subCommandParser.getStringArgument("output-format");
        if (outputFormatArgument != null && outputFormatArgument.isPresent()) {
            final String format = outputFormatArgument.getValue().toLowerCase();
            if (format.equals("der") || format.equals("binary") || format.equals("bin")) {
                outputPEM = false;
            }
        }
        if (!outputPEM && outputFile == null) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_NO_FILE_WITH_DER.get());
            return ResultCode.PARAM_ERROR;
        }
        final BooleanArgument replaceExistingCertificateArgument = this.subCommandParser.getBooleanArgument("replace-existing-certificate");
        final boolean replaceExistingCertificate = replaceExistingCertificateArgument != null && replaceExistingCertificateArgument.isPresent();
        if (replaceExistingCertificate && !keystorePath.exists()) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_REPLACE_WITHOUT_KS.get());
            return ResultCode.PARAM_ERROR;
        }
        final BooleanArgument inheritExtensionsArgument = this.subCommandParser.getBooleanArgument("inherit-extensions");
        final boolean inheritExtensions = inheritExtensionsArgument != null && inheritExtensionsArgument.isPresent();
        final BooleanArgument includeRequestedExtensionsArgument = this.subCommandParser.getBooleanArgument("include-requested-extensions");
        final boolean includeRequestedExtensions = includeRequestedExtensionsArgument != null && includeRequestedExtensionsArgument.isPresent();
        final BooleanArgument noPromptArgument = this.subCommandParser.getBooleanArgument("no-prompt");
        final boolean noPrompt = noPromptArgument != null && noPromptArgument.isPresent();
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        final boolean displayKeytoolCommand = displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent();
        int daysValid = 365;
        final IntegerArgument daysValidArgument = this.subCommandParser.getIntegerArgument("days-valid");
        if (daysValidArgument != null && daysValidArgument.isPresent()) {
            daysValid = daysValidArgument.getValue();
        }
        Date validityStartTime = null;
        final TimestampArgument validityStartTimeArgument = this.subCommandParser.getTimestampArgument("validity-start-time");
        if (validityStartTimeArgument != null && validityStartTimeArgument.isPresent()) {
            validityStartTime = validityStartTimeArgument.getValue();
        }
        PublicKeyAlgorithmIdentifier keyAlgorithmIdentifier = null;
        String keyAlgorithmName = null;
        final StringArgument keyAlgorithmArgument = this.subCommandParser.getStringArgument("key-algorithm");
        if (keyAlgorithmArgument != null && keyAlgorithmArgument.isPresent()) {
            final String name = keyAlgorithmArgument.getValue();
            keyAlgorithmIdentifier = PublicKeyAlgorithmIdentifier.forName(name);
            if (keyAlgorithmIdentifier == null) {
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_UNKNOWN_KEY_ALG.get(name));
                return ResultCode.PARAM_ERROR;
            }
            keyAlgorithmName = keyAlgorithmIdentifier.getName();
        }
        Integer keySizeBits = null;
        final IntegerArgument keySizeBitsArgument = this.subCommandParser.getIntegerArgument("key-size-bits");
        if (keySizeBitsArgument != null && keySizeBitsArgument.isPresent()) {
            keySizeBits = keySizeBitsArgument.getValue();
        }
        if (keyAlgorithmIdentifier != null && keyAlgorithmIdentifier != PublicKeyAlgorithmIdentifier.RSA && keySizeBits == null) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_NO_KEY_SIZE_FOR_NON_RSA_KEY.get());
            return ResultCode.PARAM_ERROR;
        }
        String signatureAlgorithmName = null;
        SignatureAlgorithmIdentifier signatureAlgorithmIdentifier = null;
        final StringArgument signatureAlgorithmArgument = this.subCommandParser.getStringArgument("signature-algorithm");
        if (signatureAlgorithmArgument != null && signatureAlgorithmArgument.isPresent()) {
            final String name2 = signatureAlgorithmArgument.getValue();
            signatureAlgorithmIdentifier = SignatureAlgorithmIdentifier.forName(name2);
            if (signatureAlgorithmIdentifier == null) {
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_UNKNOWN_SIG_ALG.get(name2));
                return ResultCode.PARAM_ERROR;
            }
            signatureAlgorithmName = signatureAlgorithmIdentifier.getJavaName();
        }
        if (keyAlgorithmIdentifier != null && keyAlgorithmIdentifier != PublicKeyAlgorithmIdentifier.RSA && signatureAlgorithmIdentifier == null) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_NO_SIG_ALG_FOR_NON_RSA_KEY.get());
            return ResultCode.PARAM_ERROR;
        }
        final ArrayList<X509CertificateExtension> extensionList = new ArrayList<X509CertificateExtension>(10);
        final GeneralNamesBuilder sanBuilder = new GeneralNamesBuilder();
        final LinkedHashSet<String> sanValues = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(10));
        final StringArgument sanDNSArgument = this.subCommandParser.getStringArgument("subject-alternative-name-dns");
        if (sanDNSArgument != null && sanDNSArgument.isPresent()) {
            for (final String value : sanDNSArgument.getValues()) {
                sanBuilder.addDNSName(value);
                sanValues.add("DNS:" + value);
            }
        }
        final StringArgument sanIPArgument = this.subCommandParser.getStringArgument("subject-alternative-name-ip-address");
        if (sanIPArgument != null && sanIPArgument.isPresent()) {
            for (final String value2 : sanIPArgument.getValues()) {
                try {
                    sanBuilder.addIPAddress(LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(value2));
                    sanValues.add("IP:" + value2);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new RuntimeException(e);
                }
            }
        }
        final StringArgument sanEmailArgument = this.subCommandParser.getStringArgument("subject-alternative-name-email-address");
        if (sanEmailArgument != null && sanEmailArgument.isPresent()) {
            for (final String value3 : sanEmailArgument.getValues()) {
                sanBuilder.addRFC822Name(value3);
                sanValues.add("EMAIL:" + value3);
            }
        }
        final StringArgument sanURIArgument = this.subCommandParser.getStringArgument("subject-alternative-name-uri");
        if (sanURIArgument != null && sanURIArgument.isPresent()) {
            for (final String value4 : sanURIArgument.getValues()) {
                sanBuilder.addUniformResourceIdentifier(value4);
                sanValues.add("URI:" + value4);
            }
        }
        final StringArgument sanOIDArgument = this.subCommandParser.getStringArgument("subject-alternative-name-oid");
        if (sanOIDArgument != null && sanOIDArgument.isPresent()) {
            for (final String value5 : sanOIDArgument.getValues()) {
                sanBuilder.addRegisteredID(new OID(value5));
                sanValues.add("OID:" + value5);
            }
        }
        if (!sanValues.isEmpty()) {
            try {
                extensionList.add(new SubjectAlternativeNameExtension(false, sanBuilder.build()));
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new RuntimeException(e2);
            }
        }
        final GeneralNamesBuilder ianBuilder = new GeneralNamesBuilder();
        final LinkedHashSet<String> ianValues = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(10));
        final StringArgument ianDNSArgument = this.subCommandParser.getStringArgument("issuer-alternative-name-dns");
        if (ianDNSArgument != null && ianDNSArgument.isPresent()) {
            for (final String value6 : ianDNSArgument.getValues()) {
                ianBuilder.addDNSName(value6);
                ianValues.add("DNS:" + value6);
            }
        }
        final StringArgument ianIPArgument = this.subCommandParser.getStringArgument("issuer-alternative-name-ip-address");
        if (ianIPArgument != null && ianIPArgument.isPresent()) {
            for (final String value7 : ianIPArgument.getValues()) {
                try {
                    ianBuilder.addIPAddress(LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(value7));
                    ianValues.add("IP:" + value7);
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    throw new RuntimeException(e3);
                }
            }
        }
        final StringArgument ianEmailArgument = this.subCommandParser.getStringArgument("issuer-alternative-name-email-address");
        if (ianEmailArgument != null && ianEmailArgument.isPresent()) {
            for (final String value8 : ianEmailArgument.getValues()) {
                ianBuilder.addRFC822Name(value8);
                ianValues.add("EMAIL:" + value8);
            }
        }
        final StringArgument ianURIArgument = this.subCommandParser.getStringArgument("issuer-alternative-name-uri");
        if (ianURIArgument != null && ianURIArgument.isPresent()) {
            for (final String value9 : ianURIArgument.getValues()) {
                ianBuilder.addUniformResourceIdentifier(value9);
                ianValues.add("URI:" + value9);
            }
        }
        final StringArgument ianOIDArgument = this.subCommandParser.getStringArgument("issuer-alternative-name-oid");
        if (ianOIDArgument != null && ianOIDArgument.isPresent()) {
            for (final String value10 : ianOIDArgument.getValues()) {
                ianBuilder.addRegisteredID(new OID(value10));
                ianValues.add("OID:" + value10);
            }
        }
        if (!ianValues.isEmpty()) {
            try {
                extensionList.add(new IssuerAlternativeNameExtension(false, ianBuilder.build()));
            }
            catch (final Exception e4) {
                Debug.debugException(e4);
                throw new RuntimeException(e4);
            }
        }
        BasicConstraintsExtension basicConstraints = null;
        final BooleanValueArgument basicConstraintsIsCAArgument = this.subCommandParser.getBooleanValueArgument("basic-constraints-is-ca");
        if (basicConstraintsIsCAArgument != null && basicConstraintsIsCAArgument.isPresent()) {
            final boolean isCA = basicConstraintsIsCAArgument.getValue();
            Integer pathLength = null;
            final IntegerArgument pathLengthArgument = this.subCommandParser.getIntegerArgument("basic-constraints-maximum-path-length");
            if (pathLengthArgument != null && pathLengthArgument.isPresent()) {
                if (!isCA) {
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_BC_PATH_LENGTH_WITHOUT_CA.get());
                    return ResultCode.PARAM_ERROR;
                }
                pathLength = pathLengthArgument.getValue();
            }
            basicConstraints = new BasicConstraintsExtension(false, isCA, pathLength);
            extensionList.add(basicConstraints);
        }
        KeyUsageExtension keyUsage = null;
        final StringArgument keyUsageArgument = this.subCommandParser.getStringArgument("key-usage");
        if (keyUsageArgument != null && keyUsageArgument.isPresent()) {
            boolean digitalSignature = false;
            boolean nonRepudiation = false;
            boolean keyEncipherment = false;
            boolean dataEncipherment = false;
            boolean keyAgreement = false;
            boolean keyCertSign = false;
            boolean crlSign = false;
            boolean encipherOnly = false;
            boolean decipherOnly = false;
            for (final String value11 : keyUsageArgument.getValues()) {
                if (value11.equalsIgnoreCase("digital-signature") || value11.equalsIgnoreCase("digitalSignature")) {
                    digitalSignature = true;
                }
                else if (value11.equalsIgnoreCase("non-repudiation") || value11.equalsIgnoreCase("nonRepudiation") || value11.equalsIgnoreCase("content-commitment") || value11.equalsIgnoreCase("contentCommitment")) {
                    nonRepudiation = true;
                }
                else if (value11.equalsIgnoreCase("key-encipherment") || value11.equalsIgnoreCase("keyEncipherment")) {
                    keyEncipherment = true;
                }
                else if (value11.equalsIgnoreCase("data-encipherment") || value11.equalsIgnoreCase("dataEncipherment")) {
                    dataEncipherment = true;
                }
                else if (value11.equalsIgnoreCase("key-agreement") || value11.equalsIgnoreCase("keyAgreement")) {
                    keyAgreement = true;
                }
                else if (value11.equalsIgnoreCase("key-cert-sign") || value11.equalsIgnoreCase("keyCertSign")) {
                    keyCertSign = true;
                }
                else if (value11.equalsIgnoreCase("crl-sign") || value11.equalsIgnoreCase("crlSign")) {
                    crlSign = true;
                }
                else if (value11.equalsIgnoreCase("encipher-only") || value11.equalsIgnoreCase("encipherOnly")) {
                    encipherOnly = true;
                }
                else {
                    if (!value11.equalsIgnoreCase("decipher-only") && !value11.equalsIgnoreCase("decipherOnly")) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_INVALID_KEY_USAGE.get(value11));
                        return ResultCode.PARAM_ERROR;
                    }
                    decipherOnly = true;
                }
            }
            keyUsage = new KeyUsageExtension(false, digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment, keyAgreement, keyCertSign, crlSign, encipherOnly, decipherOnly);
            extensionList.add(keyUsage);
        }
        ExtendedKeyUsageExtension extendedKeyUsage = null;
        final StringArgument extendedKeyUsageArgument = this.subCommandParser.getStringArgument("extended-key-usage");
        if (extendedKeyUsageArgument != null && extendedKeyUsageArgument.isPresent()) {
            final List<String> values = extendedKeyUsageArgument.getValues();
            final ArrayList<OID> keyPurposeIDs = new ArrayList<OID>(values.size());
            for (final String value12 : values) {
                if (value12.equalsIgnoreCase("server-auth") || value12.equalsIgnoreCase("serverAuth") || value12.equalsIgnoreCase("server-authentication") || value12.equalsIgnoreCase("serverAuthentication") || value12.equalsIgnoreCase("tls-server-authentication") || value12.equalsIgnoreCase("tlsServerAuthentication")) {
                    keyPurposeIDs.add(ExtendedKeyUsageID.TLS_SERVER_AUTHENTICATION.getOID());
                }
                else if (value12.equalsIgnoreCase("client-auth") || value12.equalsIgnoreCase("clientAuth") || value12.equalsIgnoreCase("client-authentication") || value12.equalsIgnoreCase("clientAuthentication") || value12.equalsIgnoreCase("tls-client-authentication") || value12.equalsIgnoreCase("tlsClientAuthentication")) {
                    keyPurposeIDs.add(ExtendedKeyUsageID.TLS_CLIENT_AUTHENTICATION.getOID());
                }
                else if (value12.equalsIgnoreCase("code-signing") || value12.equalsIgnoreCase("codeSigning")) {
                    keyPurposeIDs.add(ExtendedKeyUsageID.CODE_SIGNING.getOID());
                }
                else if (value12.equalsIgnoreCase("email-protection") || value12.equalsIgnoreCase("emailProtection")) {
                    keyPurposeIDs.add(ExtendedKeyUsageID.EMAIL_PROTECTION.getOID());
                }
                else if (value12.equalsIgnoreCase("time-stamping") || value12.equalsIgnoreCase("timeStamping")) {
                    keyPurposeIDs.add(ExtendedKeyUsageID.TIME_STAMPING.getOID());
                }
                else if (value12.equalsIgnoreCase("ocsp-signing") || value12.equalsIgnoreCase("ocspSigning")) {
                    keyPurposeIDs.add(ExtendedKeyUsageID.OCSP_SIGNING.getOID());
                }
                else {
                    if (!OID.isStrictlyValidNumericOID(value12)) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_INVALID_EXTENDED_KEY_USAGE.get(value12));
                        return ResultCode.PARAM_ERROR;
                    }
                    keyPurposeIDs.add(new OID(value12));
                }
            }
            try {
                extendedKeyUsage = new ExtendedKeyUsageExtension(false, keyPurposeIDs);
            }
            catch (final Exception e5) {
                Debug.debugException(e5);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_EXTENDED_KEY_USAGE_ERROR.get());
                e5.printStackTrace(this.getErr());
                return ResultCode.PARAM_ERROR;
            }
            extensionList.add(extendedKeyUsage);
        }
        final ArrayList<X509CertificateExtension> genericExtensions = new ArrayList<X509CertificateExtension>(5);
        final StringArgument extensionArgument = this.subCommandParser.getStringArgument("extension");
        if (extensionArgument != null && extensionArgument.isPresent()) {
            for (final String value12 : extensionArgument.getValues()) {
                try {
                    final int firstColonPos = value12.indexOf(58);
                    final int secondColonPos = value12.indexOf(58, firstColonPos + 1);
                    final OID oid = new OID(value12.substring(0, firstColonPos));
                    if (!oid.isStrictlyValidNumericOID()) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_EXT_MALFORMED_OID.get(value12, oid.toString()));
                        return ResultCode.PARAM_ERROR;
                    }
                    final String criticalityString = value12.substring(firstColonPos + 1, secondColonPos);
                    boolean criticality;
                    if (criticalityString.equalsIgnoreCase("true") || criticalityString.equalsIgnoreCase("t") || criticalityString.equalsIgnoreCase("yes") || criticalityString.equalsIgnoreCase("y") || criticalityString.equalsIgnoreCase("on") || criticalityString.equalsIgnoreCase("1")) {
                        criticality = true;
                    }
                    else {
                        if (!criticalityString.equalsIgnoreCase("false") && !criticalityString.equalsIgnoreCase("f") && !criticalityString.equalsIgnoreCase("no") && !criticalityString.equalsIgnoreCase("n") && !criticalityString.equalsIgnoreCase("off") && !criticalityString.equalsIgnoreCase("0")) {
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_EXT_INVALID_CRITICALITY.get(value12, criticalityString));
                            return ResultCode.PARAM_ERROR;
                        }
                        criticality = false;
                    }
                    byte[] valueBytes;
                    try {
                        valueBytes = StaticUtils.fromHex(value12.substring(secondColonPos + 1));
                    }
                    catch (final Exception e6) {
                        Debug.debugException(e6);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_EXT_INVALID_VALUE.get(value12));
                        return ResultCode.PARAM_ERROR;
                    }
                    final X509CertificateExtension extension = new X509CertificateExtension(oid, criticality, valueBytes);
                    genericExtensions.add(extension);
                    extensionList.add(extension);
                }
                catch (final Exception e7) {
                    Debug.debugException(e7);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_EXT_MALFORMED.get(value12));
                    return ResultCode.PARAM_ERROR;
                }
            }
        }
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        char[] privateKeyPassword;
        try {
            privateKeyPassword = this.getPrivateKeyPassword(keystore, alias, keystorePassword);
        }
        catch (final LDAPException le4) {
            Debug.debugException(le4);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
            return le4.getResultCode();
        }
        if (replaceExistingCertificate) {
            if (!hasKeyAlias(keystore, alias)) {
                if (hasCertificateAlias(keystore, alias)) {
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_REPLACE_ALIAS_IS_CERT.get(alias, keystorePath.getAbsolutePath()));
                    return ResultCode.PARAM_ERROR;
                }
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_REPLACE_NO_SUCH_ALIAS.get(alias, keystorePath.getAbsolutePath()));
                return ResultCode.PARAM_ERROR;
            }
            else {
                X509Certificate certToReplace;
                KeyPair keyPair;
                try {
                    final Certificate[] chain = keystore.getCertificateChain(alias);
                    certToReplace = new X509Certificate(chain[0].getEncoded());
                    final PublicKey publicKey = chain[0].getPublicKey();
                    final PrivateKey privateKey = (PrivateKey)keystore.getKey(alias, privateKeyPassword);
                    keyPair = new KeyPair(publicKey, privateKey);
                }
                catch (final Exception e8) {
                    Debug.debugException(e8);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_REPLACE_COULD_NOT_GET_CERT.get(alias));
                    e8.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
                signatureAlgorithmIdentifier = SignatureAlgorithmIdentifier.forOID(certToReplace.getSignatureAlgorithmOID());
                if (signatureAlgorithmIdentifier == null) {
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_UNKNOWN_SIG_ALG_IN_CERT.get(certToReplace.getSignatureAlgorithmOID()));
                    return ResultCode.PARAM_ERROR;
                }
                signatureAlgorithmName = signatureAlgorithmIdentifier.getJavaName();
                if (subjectDN == null) {
                    subjectDN = certToReplace.getSubjectDN();
                }
                if (inheritExtensions) {
                    for (final X509CertificateExtension extension2 : certToReplace.getExtensions()) {
                        if (!(extension2 instanceof AuthorityKeyIdentifierExtension)) {
                            if (extension2 instanceof IssuerAlternativeNameExtension) {
                                continue;
                            }
                            if (extension2 instanceof SubjectKeyIdentifierExtension) {
                                continue;
                            }
                            if (extension2 instanceof BasicConstraintsExtension) {
                                if (basicConstraints != null) {
                                    continue;
                                }
                                basicConstraints = (BasicConstraintsExtension)extension2;
                                extensionList.add(basicConstraints);
                            }
                            else if (extension2 instanceof ExtendedKeyUsageExtension) {
                                if (extendedKeyUsage != null) {
                                    continue;
                                }
                                extendedKeyUsage = (ExtendedKeyUsageExtension)extension2;
                                extensionList.add(extendedKeyUsage);
                            }
                            else if (extension2 instanceof KeyUsageExtension) {
                                if (keyUsage != null) {
                                    continue;
                                }
                                keyUsage = (KeyUsageExtension)extension2;
                                extensionList.add(keyUsage);
                            }
                            else if (extension2 instanceof SubjectAlternativeNameExtension) {
                                if (!sanValues.isEmpty()) {
                                    continue;
                                }
                                final SubjectAlternativeNameExtension e9 = (SubjectAlternativeNameExtension)extension2;
                                for (final String dnsName : e9.getDNSNames()) {
                                    sanValues.add("DNS:" + dnsName);
                                }
                                for (final InetAddress ipAddress : e9.getIPAddresses()) {
                                    sanValues.add("IP:" + ipAddress.getHostAddress());
                                }
                                for (final String emailAddress : e9.getRFC822Names()) {
                                    sanValues.add("EMAIL:" + emailAddress);
                                }
                                for (final String uri : e9.getUniformResourceIdentifiers()) {
                                    sanValues.add("URI:" + uri);
                                }
                                for (final OID oid2 : e9.getRegisteredIDs()) {
                                    sanValues.add("OID:" + oid2.toString());
                                }
                                extensionList.add(extension2);
                            }
                            else {
                                genericExtensions.add(extension2);
                                extensionList.add(extension2);
                            }
                        }
                    }
                }
                final X509CertificateExtension[] extensions = new X509CertificateExtension[extensionList.size()];
                extensionList.toArray(extensions);
                if (isGenerateCertificate) {
                    if (displayKeytoolCommand) {
                        final ArrayList<String> keytoolArguments = new ArrayList<String>(30);
                        keytoolArguments.add("-selfcert");
                        keytoolArguments.add("-keystore");
                        keytoolArguments.add(keystorePath.getAbsolutePath());
                        keytoolArguments.add("-storetype");
                        keytoolArguments.add(keystoreType);
                        keytoolArguments.add("-storepass");
                        keytoolArguments.add("*****REDACTED*****");
                        keytoolArguments.add("-keypass");
                        keytoolArguments.add("*****REDACTED*****");
                        keytoolArguments.add("-alias");
                        keytoolArguments.add(alias);
                        keytoolArguments.add("-dname");
                        keytoolArguments.add(subjectDN.toString());
                        keytoolArguments.add("-sigalg");
                        keytoolArguments.add(signatureAlgorithmName);
                        keytoolArguments.add("-validity");
                        keytoolArguments.add(String.valueOf(daysValid));
                        if (validityStartTime != null) {
                            keytoolArguments.add("-startdate");
                            keytoolArguments.add(formatValidityStartTime(validityStartTime));
                        }
                        addExtensionArguments(keytoolArguments, basicConstraints, keyUsage, extendedKeyUsage, sanValues, ianValues, genericExtensions);
                        this.displayKeytoolCommand(keytoolArguments);
                    }
                    long notBefore;
                    if (validityStartTime == null) {
                        notBefore = System.currentTimeMillis();
                    }
                    else {
                        notBefore = validityStartTime.getTime();
                    }
                    final long notAfter = notBefore + TimeUnit.DAYS.toMillis(daysValid);
                    X509Certificate certificate;
                    Certificate[] chain2;
                    try {
                        certificate = X509Certificate.generateSelfSignedCertificate(signatureAlgorithmIdentifier, keyPair, subjectDN, notBefore, notAfter, extensions);
                        chain2 = new Certificate[] { certificate.toCertificate() };
                    }
                    catch (final Exception e10) {
                        Debug.debugException(e10);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_GENERATING_CERT.get());
                        e10.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    try {
                        keystore.setKeyEntry(alias, keyPair.getPrivate(), privateKeyPassword, chain2);
                        writeKeystore(keystore, keystorePath, keystorePassword);
                    }
                    catch (final Exception e10) {
                        Debug.debugException(e10);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_UPDATING_KEYSTORE.get());
                        e10.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    this.out(new Object[0]);
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_GEN_CERT_SUCCESSFULLY_GENERATED_SELF_CERT.get());
                    this.printCertificate(certificate, "", false);
                    return ResultCode.SUCCESS;
                }
                Validator.ensureTrue(isGenerateCSR);
                if (displayKeytoolCommand) {
                    final ArrayList<String> keytoolArguments = new ArrayList<String>(30);
                    keytoolArguments.add("-certreq");
                    keytoolArguments.add("-keystore");
                    keytoolArguments.add(keystorePath.getAbsolutePath());
                    keytoolArguments.add("-storetype");
                    keytoolArguments.add(keystoreType);
                    keytoolArguments.add("-storepass");
                    keytoolArguments.add("*****REDACTED*****");
                    keytoolArguments.add("-keypass");
                    keytoolArguments.add("*****REDACTED*****");
                    keytoolArguments.add("-alias");
                    keytoolArguments.add(alias);
                    keytoolArguments.add("-dname");
                    keytoolArguments.add(subjectDN.toString());
                    keytoolArguments.add("-sigalg");
                    keytoolArguments.add(signatureAlgorithmName);
                    addExtensionArguments(keytoolArguments, basicConstraints, keyUsage, extendedKeyUsage, sanValues, ianValues, genericExtensions);
                    if (outputFile != null) {
                        keytoolArguments.add("-file");
                        keytoolArguments.add(outputFile.getAbsolutePath());
                    }
                    this.displayKeytoolCommand(keytoolArguments);
                }
                PKCS10CertificateSigningRequest certificateSigningRequest;
                try {
                    certificateSigningRequest = PKCS10CertificateSigningRequest.generateCertificateSigningRequest(signatureAlgorithmIdentifier, keyPair, subjectDN, extensions);
                }
                catch (final Exception e6) {
                    Debug.debugException(e6);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_GENERATING_CSR.get());
                    e6.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
                try {
                    PrintStream ps;
                    if (outputFile == null) {
                        ps = this.getOut();
                    }
                    else {
                        ps = new PrintStream(outputFile);
                    }
                    if (outputPEM) {
                        writePEMCertificateSigningRequest(ps, certificateSigningRequest.getPKCS10CertificateSigningRequestBytes());
                    }
                    else {
                        ps.write(certificateSigningRequest.getPKCS10CertificateSigningRequestBytes());
                    }
                    if (outputFile != null) {
                        ps.close();
                    }
                }
                catch (final Exception e6) {
                    Debug.debugException(e6);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_WRITING_CSR.get());
                    e6.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
                if (outputFile != null) {
                    this.out(new Object[0]);
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_GEN_CERT_SUCCESSFULLY_GENERATED_CSR.get(outputFile.getAbsolutePath()));
                }
                return ResultCode.SUCCESS;
            }
        }
        else {
            if (subjectDN == null && !isSignCSR) {
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_NO_SUBJECT_DN_WITHOUT_REPLACE.get());
                return ResultCode.PARAM_ERROR;
            }
            if (keyAlgorithmIdentifier == null) {
                keyAlgorithmIdentifier = PublicKeyAlgorithmIdentifier.RSA;
                keyAlgorithmName = keyAlgorithmIdentifier.getName();
            }
            if (keySizeBits == null) {
                keySizeBits = 2048;
            }
            if (signatureAlgorithmIdentifier == null && !isSignCSR) {
                signatureAlgorithmIdentifier = SignatureAlgorithmIdentifier.SHA_256_WITH_RSA;
                signatureAlgorithmName = signatureAlgorithmIdentifier.getJavaName();
            }
            if (isGenerateCertificate || isGenerateCSR) {
                if (hasKeyAlias(keystore, alias) || hasCertificateAlias(keystore, alias)) {
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ALIAS_EXISTS_WITHOUT_REPLACE.get(alias));
                    return ResultCode.PARAM_ERROR;
                }
                if (displayKeytoolCommand) {
                    final ArrayList<String> keytoolArguments2 = new ArrayList<String>(30);
                    keytoolArguments2.add("-genkeypair");
                    keytoolArguments2.add("-keystore");
                    keytoolArguments2.add(keystorePath.getAbsolutePath());
                    keytoolArguments2.add("-storetype");
                    keytoolArguments2.add(keystoreType);
                    keytoolArguments2.add("-storepass");
                    keytoolArguments2.add("*****REDACTED*****");
                    keytoolArguments2.add("-keypass");
                    keytoolArguments2.add("*****REDACTED*****");
                    keytoolArguments2.add("-alias");
                    keytoolArguments2.add(alias);
                    keytoolArguments2.add("-dname");
                    keytoolArguments2.add(subjectDN.toString());
                    keytoolArguments2.add("-keyalg");
                    keytoolArguments2.add(keyAlgorithmName);
                    keytoolArguments2.add("-keysize");
                    keytoolArguments2.add(String.valueOf(keySizeBits));
                    keytoolArguments2.add("-sigalg");
                    keytoolArguments2.add(signatureAlgorithmName);
                    keytoolArguments2.add("-validity");
                    keytoolArguments2.add(String.valueOf(daysValid));
                    if (validityStartTime != null) {
                        keytoolArguments2.add("-startdate");
                        keytoolArguments2.add(formatValidityStartTime(validityStartTime));
                    }
                    addExtensionArguments(keytoolArguments2, basicConstraints, keyUsage, extendedKeyUsage, sanValues, ianValues, genericExtensions);
                    this.displayKeytoolCommand(keytoolArguments2);
                }
                long notBefore2;
                if (validityStartTime == null) {
                    notBefore2 = System.currentTimeMillis();
                }
                else {
                    notBefore2 = validityStartTime.getTime();
                }
                final long notAfter2 = notBefore2 + TimeUnit.DAYS.toMillis(daysValid);
                final X509CertificateExtension[] extensions2 = new X509CertificateExtension[extensionList.size()];
                extensionList.toArray(extensions2);
                X509Certificate certificate;
                Certificate[] chain3;
                KeyPair keyPair2;
                try {
                    final ObjectPair<X509Certificate, KeyPair> p = X509Certificate.generateSelfSignedCertificate(signatureAlgorithmIdentifier, keyAlgorithmIdentifier, keySizeBits, subjectDN, notBefore2, notAfter2, extensions2);
                    certificate = p.getFirst();
                    chain3 = new Certificate[] { certificate.toCertificate() };
                    keyPair2 = p.getSecond();
                }
                catch (final Exception e11) {
                    Debug.debugException(e11);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_GENERATING_CERT.get());
                    e11.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
                try {
                    keystore.setKeyEntry(alias, keyPair2.getPrivate(), privateKeyPassword, chain3);
                    writeKeystore(keystore, keystorePath, keystorePassword);
                }
                catch (final Exception e11) {
                    Debug.debugException(e11);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_UPDATING_KEYSTORE.get());
                    e11.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
                if (isNewKeystore) {
                    this.out(new Object[0]);
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_GEN_CERT_CERT_CREATED_KEYSTORE.get(getUserFriendlyKeystoreType(keystoreType)));
                }
                if (isGenerateCertificate) {
                    this.out(new Object[0]);
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_GEN_CERT_SUCCESSFULLY_GENERATED_SELF_CERT.get());
                    this.printCertificate(certificate, "", false);
                    return ResultCode.SUCCESS;
                }
                Validator.ensureTrue(isGenerateCSR);
                this.out(new Object[0]);
                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_GEN_CERT_SUCCESSFULLY_GENERATED_KEYPAIR.get());
                if (displayKeytoolCommand) {
                    final ArrayList<String> keytoolArguments3 = new ArrayList<String>(30);
                    keytoolArguments3.add("-certreq");
                    keytoolArguments3.add("-keystore");
                    keytoolArguments3.add(keystorePath.getAbsolutePath());
                    keytoolArguments3.add("-storetype");
                    keytoolArguments3.add(keystoreType);
                    keytoolArguments3.add("-storepass");
                    keytoolArguments3.add("*****REDACTED*****");
                    keytoolArguments3.add("-keypass");
                    keytoolArguments3.add("*****REDACTED*****");
                    keytoolArguments3.add("-alias");
                    keytoolArguments3.add(alias);
                    keytoolArguments3.add("-dname");
                    keytoolArguments3.add(subjectDN.toString());
                    keytoolArguments3.add("-sigalg");
                    keytoolArguments3.add(signatureAlgorithmName);
                    addExtensionArguments(keytoolArguments3, basicConstraints, keyUsage, extendedKeyUsage, sanValues, ianValues, genericExtensions);
                    if (outputFile != null) {
                        keytoolArguments3.add("-file");
                        keytoolArguments3.add(outputFile.getAbsolutePath());
                    }
                    this.displayKeytoolCommand(keytoolArguments3);
                }
                PKCS10CertificateSigningRequest certificateSigningRequest2;
                try {
                    certificateSigningRequest2 = PKCS10CertificateSigningRequest.generateCertificateSigningRequest(signatureAlgorithmIdentifier, keyPair2, subjectDN, extensions2);
                }
                catch (final Exception e10) {
                    Debug.debugException(e10);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_GENERATING_CSR.get());
                    e10.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
                try {
                    PrintStream ps2;
                    if (outputFile == null) {
                        ps2 = this.getOut();
                    }
                    else {
                        ps2 = new PrintStream(outputFile);
                    }
                    if (outputPEM) {
                        writePEMCertificateSigningRequest(ps2, certificateSigningRequest2.getPKCS10CertificateSigningRequestBytes());
                    }
                    else {
                        ps2.write(certificateSigningRequest2.getPKCS10CertificateSigningRequestBytes());
                    }
                    if (outputFile != null) {
                        ps2.close();
                    }
                }
                catch (final Exception e10) {
                    Debug.debugException(e10);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_WRITING_CSR.get());
                    e10.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
                if (outputFile != null) {
                    this.out(new Object[0]);
                    this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_GEN_CERT_SUCCESSFULLY_GENERATED_CSR.get(outputFile.getAbsolutePath()));
                }
                return ResultCode.SUCCESS;
            }
            else {
                Validator.ensureTrue(isSignCSR);
                if (hasKeyAlias(keystore, alias)) {
                    X509Certificate issuerCertificate;
                    PrivateKey issuerPrivateKey;
                    try {
                        final Certificate[] chain = keystore.getCertificateChain(alias);
                        issuerCertificate = new X509Certificate(chain[0].getEncoded());
                        issuerPrivateKey = (PrivateKey)keystore.getKey(alias, privateKeyPassword);
                    }
                    catch (final Exception e8) {
                        Debug.debugException(e8);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_SIGN_CANNOT_GET_SIGNING_CERT.get(alias));
                        e8.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    PKCS10CertificateSigningRequest csr;
                    try {
                        csr = readCertificateSigningRequestFromFile(inputFile);
                    }
                    catch (final LDAPException le5) {
                        Debug.debugException(le5);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le5.getMessage());
                        return le5.getResultCode();
                    }
                    try {
                        csr.verifySignature();
                    }
                    catch (final CertException ce) {
                        Debug.debugException(ce);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, ce.getMessage());
                        return ResultCode.PARAM_ERROR;
                    }
                    if (!noPrompt) {
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_GEN_CERT_SIGN_CONFIRM.get());
                        this.out(new Object[0]);
                        this.printCertificateSigningRequest(csr, false, "");
                        this.out(new Object[0]);
                        try {
                            if (!this.promptForYesNo(CertMessages.INFO_MANAGE_CERTS_GEN_CERT_PROMPT_SIGN.get())) {
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_SIGN_CANCELED.get());
                                return ResultCode.USER_CANCELED;
                            }
                        }
                        catch (final LDAPException le5) {
                            Debug.debugException(le5);
                            this.err(new Object[0]);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le5.getMessage());
                            return le5.getResultCode();
                        }
                    }
                    if (subjectDN == null || signatureAlgorithmIdentifier == null || includeRequestedExtensions) {
                        if (subjectDN == null) {
                            subjectDN = csr.getSubjectDN();
                        }
                        if (signatureAlgorithmIdentifier == null) {
                            signatureAlgorithmIdentifier = SignatureAlgorithmIdentifier.forOID(csr.getSignatureAlgorithmOID());
                            if (signatureAlgorithmIdentifier == null) {
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_UNKNOWN_SIG_ALG_IN_CSR.get(csr.getSignatureAlgorithmOID()));
                                return ResultCode.PARAM_ERROR;
                            }
                            signatureAlgorithmName = signatureAlgorithmIdentifier.getJavaName();
                        }
                        if (includeRequestedExtensions) {
                            for (final X509CertificateExtension extension : csr.getExtensions()) {
                                if (!(extension instanceof AuthorityKeyIdentifierExtension)) {
                                    if (extension instanceof IssuerAlternativeNameExtension) {
                                        continue;
                                    }
                                    if (extension instanceof SubjectKeyIdentifierExtension) {
                                        continue;
                                    }
                                    if (extension instanceof BasicConstraintsExtension) {
                                        if (basicConstraints != null) {
                                            continue;
                                        }
                                        basicConstraints = (BasicConstraintsExtension)extension;
                                        extensionList.add(basicConstraints);
                                    }
                                    else if (extension instanceof ExtendedKeyUsageExtension) {
                                        if (extendedKeyUsage != null) {
                                            continue;
                                        }
                                        extendedKeyUsage = (ExtendedKeyUsageExtension)extension;
                                        extensionList.add(extendedKeyUsage);
                                    }
                                    else if (extension instanceof KeyUsageExtension) {
                                        if (keyUsage != null) {
                                            continue;
                                        }
                                        keyUsage = (KeyUsageExtension)extension;
                                        extensionList.add(keyUsage);
                                    }
                                    else if (extension instanceof SubjectAlternativeNameExtension) {
                                        if (!sanValues.isEmpty()) {
                                            continue;
                                        }
                                        final SubjectAlternativeNameExtension e12 = (SubjectAlternativeNameExtension)extension;
                                        for (final String dnsName2 : e12.getDNSNames()) {
                                            sanBuilder.addDNSName(dnsName2);
                                            sanValues.add("DNS:" + dnsName2);
                                        }
                                        for (final InetAddress ipAddress2 : e12.getIPAddresses()) {
                                            sanBuilder.addIPAddress(ipAddress2);
                                            sanValues.add("IP:" + ipAddress2.getHostAddress());
                                        }
                                        for (final String emailAddress2 : e12.getRFC822Names()) {
                                            sanBuilder.addRFC822Name(emailAddress2);
                                            sanValues.add("EMAIL:" + emailAddress2);
                                        }
                                        for (final String uri2 : e12.getUniformResourceIdentifiers()) {
                                            sanBuilder.addUniformResourceIdentifier(uri2);
                                            sanValues.add("URI:" + uri2);
                                        }
                                        for (final OID oid3 : e12.getRegisteredIDs()) {
                                            sanBuilder.addRegisteredID(oid3);
                                            sanValues.add("OID:" + oid3.toString());
                                        }
                                        try {
                                            extensionList.add(new SubjectAlternativeNameExtension(false, sanBuilder.build()));
                                        }
                                        catch (final Exception ex) {
                                            Debug.debugException(ex);
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                    else {
                                        genericExtensions.add(extension);
                                        extensionList.add(extension);
                                    }
                                }
                            }
                        }
                    }
                    final ArrayList<String> keytoolArguments = new ArrayList<String>(30);
                    keytoolArguments.add("-gencert");
                    keytoolArguments.add("-keystore");
                    keytoolArguments.add(keystorePath.getAbsolutePath());
                    keytoolArguments.add("-storetype");
                    keytoolArguments.add(keystoreType);
                    keytoolArguments.add("-storepass");
                    keytoolArguments.add("*****REDACTED*****");
                    keytoolArguments.add("-keypass");
                    keytoolArguments.add("*****REDACTED*****");
                    keytoolArguments.add("-alias");
                    keytoolArguments.add(alias);
                    keytoolArguments.add("-dname");
                    keytoolArguments.add(subjectDN.toString());
                    keytoolArguments.add("-sigalg");
                    keytoolArguments.add(signatureAlgorithmName);
                    keytoolArguments.add("-validity");
                    keytoolArguments.add(String.valueOf(daysValid));
                    if (validityStartTime != null) {
                        keytoolArguments.add("-startdate");
                        keytoolArguments.add(formatValidityStartTime(validityStartTime));
                    }
                    addExtensionArguments(keytoolArguments, basicConstraints, keyUsage, extendedKeyUsage, sanValues, ianValues, genericExtensions);
                    keytoolArguments.add("-infile");
                    keytoolArguments.add(inputFile.getAbsolutePath());
                    if (outputFile != null) {
                        keytoolArguments.add("-outfile");
                        keytoolArguments.add(outputFile.getAbsolutePath());
                    }
                    if (outputPEM) {
                        keytoolArguments.add("-rfc");
                    }
                    if (displayKeytoolCommand) {
                        this.displayKeytoolCommand(keytoolArguments);
                    }
                    long notBefore3;
                    if (validityStartTime == null) {
                        notBefore3 = System.currentTimeMillis();
                    }
                    else {
                        notBefore3 = validityStartTime.getTime();
                    }
                    final long notAfter3 = notBefore3 + TimeUnit.DAYS.toMillis(daysValid);
                    final X509CertificateExtension[] extensions3 = new X509CertificateExtension[extensionList.size()];
                    extensionList.toArray(extensions3);
                    X509Certificate signedCertificate;
                    try {
                        signedCertificate = X509Certificate.generateIssuerSignedCertificate(signatureAlgorithmIdentifier, issuerCertificate, issuerPrivateKey, csr.getPublicKeyAlgorithmOID(), csr.getPublicKeyAlgorithmParameters(), csr.getEncodedPublicKey(), csr.getDecodedPublicKey(), subjectDN, notBefore3, notAfter3, extensions3);
                    }
                    catch (final Exception e13) {
                        Debug.debugException(e13);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_SIGNING_CERT.get());
                        e13.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    try {
                        PrintStream ps3;
                        if (outputFile == null) {
                            ps3 = this.getOut();
                        }
                        else {
                            ps3 = new PrintStream(outputFile);
                        }
                        if (outputPEM) {
                            writePEMCertificate(ps3, signedCertificate.getX509CertificateBytes());
                        }
                        else {
                            ps3.write(signedCertificate.getX509CertificateBytes());
                        }
                        if (outputFile != null) {
                            ps3.close();
                        }
                    }
                    catch (final Exception e13) {
                        Debug.debugException(e13);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_ERROR_WRITING_SIGNED_CERT.get());
                        e13.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    if (outputFile != null) {
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_GEN_CERT_SUCCESSFULLY_SIGNED_CERT.get(outputFile.getAbsolutePath()));
                    }
                    return ResultCode.SUCCESS;
                }
                if (hasCertificateAlias(keystore, alias)) {
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_SIGN_ALIAS_IS_CERT.get(alias, keystorePath.getAbsolutePath()));
                    return ResultCode.PARAM_ERROR;
                }
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GEN_CERT_SIGN_NO_SUCH_ALIAS.get(alias, keystorePath.getAbsolutePath()));
                return ResultCode.PARAM_ERROR;
            }
        }
    }
    
    private ResultCode doChangeCertificateAlias() {
        final StringArgument currentAliasArgument = this.subCommandParser.getStringArgument("current-alias");
        final String currentAlias = currentAliasArgument.getValue();
        final StringArgument newAliasArgument = this.subCommandParser.getStringArgument("new-alias");
        final String newAlias = newAliasArgument.getValue();
        final File keystorePath = this.getKeystorePath();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        char[] privateKeyPassword;
        try {
            privateKeyPassword = this.getPrivateKeyPassword(keystore, currentAlias, keystorePassword);
        }
        catch (final LDAPException le4) {
            Debug.debugException(le4);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
            return le4.getResultCode();
        }
        Certificate existingCertificate;
        Certificate[] existingCertificateChain;
        PrivateKey existingPrivateKey;
        try {
            if (hasCertificateAlias(keystore, currentAlias)) {
                existingCertificate = keystore.getCertificate(currentAlias);
                existingCertificateChain = null;
                existingPrivateKey = null;
            }
            else {
                if (!hasKeyAlias(keystore, currentAlias)) {
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHANGE_ALIAS_NO_SUCH_ALIAS.get(currentAlias));
                    return ResultCode.PARAM_ERROR;
                }
                existingCertificateChain = keystore.getCertificateChain(currentAlias);
                existingPrivateKey = (PrivateKey)keystore.getKey(currentAlias, privateKeyPassword);
                existingCertificate = null;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHANGE_ALIAS_CANNOT_GET_EXISTING_ENTRY.get(currentAlias));
            e.printStackTrace(this.getErr());
            return ResultCode.LOCAL_ERROR;
        }
        if (hasCertificateAlias(keystore, newAlias) || hasKeyAlias(keystore, newAlias)) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHANGE_ALIAS_NEW_ALIAS_IN_USE.get(newAlias));
            return ResultCode.PARAM_ERROR;
        }
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        if (displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent()) {
            final ArrayList<String> keytoolArguments = new ArrayList<String>(30);
            keytoolArguments.add("-changealias");
            keytoolArguments.add("-keystore");
            keytoolArguments.add(keystorePath.getAbsolutePath());
            keytoolArguments.add("-storetype");
            keytoolArguments.add(keystoreType);
            keytoolArguments.add("-storepass");
            keytoolArguments.add("*****REDACTED*****");
            keytoolArguments.add("-keypass");
            keytoolArguments.add("*****REDACTED*****");
            keytoolArguments.add("-alias");
            keytoolArguments.add(currentAlias);
            keytoolArguments.add("-destalias");
            keytoolArguments.add(newAlias);
            this.displayKeytoolCommand(keytoolArguments);
        }
        try {
            keystore.deleteEntry(currentAlias);
            if (existingCertificate != null) {
                keystore.setCertificateEntry(newAlias, existingCertificate);
            }
            else {
                keystore.setKeyEntry(newAlias, existingPrivateKey, privateKeyPassword, existingCertificateChain);
            }
            writeKeystore(keystore, keystorePath, keystorePassword);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHANGE_ALIAS_CANNOT_UPDATE_KEYSTORE.get());
            e2.printStackTrace(this.getErr());
            return ResultCode.LOCAL_ERROR;
        }
        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHANGE_ALIAS_SUCCESSFUL.get(currentAlias, newAlias));
        return ResultCode.SUCCESS;
    }
    
    private ResultCode doChangeKeystorePassword() {
        final File keystorePath = this.getKeystorePath();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] currentKeystorePassword;
        try {
            currentKeystorePassword = this.getKeystorePassword(keystorePath, "current");
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        char[] newKeystorePassword;
        try {
            newKeystorePassword = this.getKeystorePassword(keystorePath, "new");
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, currentKeystorePassword);
        }
        catch (final LDAPException le4) {
            Debug.debugException(le4);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
            return le4.getResultCode();
        }
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        if (displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent()) {
            final ArrayList<String> keytoolArguments = new ArrayList<String>(30);
            keytoolArguments.add("-storepasswd");
            keytoolArguments.add("-keystore");
            keytoolArguments.add(keystorePath.getAbsolutePath());
            keytoolArguments.add("-storetype");
            keytoolArguments.add(keystoreType);
            keytoolArguments.add("-storepass");
            keytoolArguments.add("*****REDACTED*****");
            keytoolArguments.add("-new");
            keytoolArguments.add("*****REDACTED*****");
            this.displayKeytoolCommand(keytoolArguments);
        }
        try {
            writeKeystore(keystore, keystorePath, newKeystorePassword);
        }
        catch (final LDAPException le5) {
            Debug.debugException(le5);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le5.getMessage());
            return le5.getResultCode();
        }
        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHANGE_KS_PW_SUCCESSFUL.get(keystorePath.getAbsolutePath()));
        return ResultCode.SUCCESS;
    }
    
    private ResultCode doChangePrivateKeyPassword() {
        final StringArgument aliasArgument = this.subCommandParser.getStringArgument("alias");
        final String alias = aliasArgument.getValue();
        final File keystorePath = this.getKeystorePath();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        if (hasCertificateAlias(keystore, alias)) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHANGE_PK_PW_ALIAS_IS_CERT.get(alias));
            return ResultCode.PARAM_ERROR;
        }
        if (!hasKeyAlias(keystore, alias)) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHANGE_PK_PW_NO_SUCH_ALIAS.get(alias));
            return ResultCode.PARAM_ERROR;
        }
        char[] currentPrivateKeyPassword;
        try {
            currentPrivateKeyPassword = this.getPrivateKeyPassword(keystore, alias, "current", keystorePassword);
        }
        catch (final LDAPException le4) {
            Debug.debugException(le4);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
            return le4.getResultCode();
        }
        char[] newPrivateKeyPassword;
        try {
            newPrivateKeyPassword = this.getPrivateKeyPassword(keystore, alias, "new", keystorePassword);
        }
        catch (final LDAPException le5) {
            Debug.debugException(le5);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le5.getMessage());
            return le5.getResultCode();
        }
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        if (displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent()) {
            final ArrayList<String> keytoolArguments = new ArrayList<String>(30);
            keytoolArguments.add("-keypasswd");
            keytoolArguments.add("-keystore");
            keytoolArguments.add(keystorePath.getAbsolutePath());
            keytoolArguments.add("-storetype");
            keytoolArguments.add(keystoreType);
            keytoolArguments.add("-storepass");
            keytoolArguments.add("*****REDACTED*****");
            keytoolArguments.add("-alias");
            keytoolArguments.add(alias);
            keytoolArguments.add("-keypass");
            keytoolArguments.add("*****REDACTED*****");
            keytoolArguments.add("-new");
            keytoolArguments.add("*****REDACTED*****");
            this.displayKeytoolCommand(keytoolArguments);
        }
        Certificate[] chain;
        PrivateKey privateKey;
        try {
            chain = keystore.getCertificateChain(alias);
            privateKey = (PrivateKey)keystore.getKey(alias, currentPrivateKeyPassword);
        }
        catch (final UnrecoverableKeyException e) {
            Debug.debugException(e);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHANGE_PK_PW_WRONG_PK_PW.get(alias));
            return ResultCode.PARAM_ERROR;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHANGE_PK_PW_CANNOT_GET_PK.get(alias));
            e2.printStackTrace(this.getErr());
            return ResultCode.LOCAL_ERROR;
        }
        try {
            keystore.deleteEntry(alias);
            keystore.setKeyEntry(alias, privateKey, newPrivateKeyPassword, chain);
            writeKeystore(keystore, keystorePath, keystorePassword);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHANGE_PK_PW_CANNOT_UPDATE_KS.get());
            e2.printStackTrace(this.getErr());
            return ResultCode.LOCAL_ERROR;
        }
        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHANGE_PK_PW_SUCCESSFUL.get(alias));
        return ResultCode.SUCCESS;
    }
    
    private ResultCode doTrustServerCertificate() {
        final StringArgument hostnameArgument = this.subCommandParser.getStringArgument("hostname");
        final String hostname = hostnameArgument.getValue();
        final IntegerArgument portArgument = this.subCommandParser.getIntegerArgument("port");
        final int port = portArgument.getValue();
        final StringArgument aliasArgument = this.subCommandParser.getStringArgument("alias");
        String alias;
        if (aliasArgument != null && aliasArgument.isPresent()) {
            alias = aliasArgument.getValue();
        }
        else {
            alias = hostname + ':' + port;
        }
        final BooleanArgument useLDAPStartTLSArgument = this.subCommandParser.getBooleanArgument("use-ldap-start-tls");
        final boolean useLDAPStartTLS = useLDAPStartTLSArgument != null && useLDAPStartTLSArgument.isPresent();
        final BooleanArgument issuersOnlyArgument = this.subCommandParser.getBooleanArgument("issuers-only");
        final boolean issuersOnly = issuersOnlyArgument != null && issuersOnlyArgument.isPresent();
        final BooleanArgument noPromptArgument = this.subCommandParser.getBooleanArgument("no-prompt");
        final boolean noPrompt = noPromptArgument != null && noPromptArgument.isPresent();
        final BooleanArgument verboseArgument = this.subCommandParser.getBooleanArgument("verbose");
        final boolean verbose = verboseArgument != null && verboseArgument.isPresent();
        final File keystorePath = this.getKeystorePath();
        final boolean isNewKeystore = !keystorePath.exists();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        if (hasCertificateAlias(keystore, alias) || hasKeyAlias(keystore, alias)) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_TRUST_SERVER_ALIAS_IN_USE.get(alias));
            return ResultCode.PARAM_ERROR;
        }
        final LinkedBlockingQueue<Object> responseQueue = new LinkedBlockingQueue<Object>(10);
        final ManageCertificatesServerCertificateCollector certificateCollector = new ManageCertificatesServerCertificateCollector(this, hostname, port, useLDAPStartTLS, verbose, responseQueue);
        certificateCollector.start();
        Object responseObject = CertMessages.ERR_MANAGE_CERTS_TRUST_SERVER_NO_CERT_CHAIN_RECEIVED.get(hostname + ':' + port);
        try {
            responseObject = responseQueue.poll(90L, TimeUnit.SECONDS);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        if (responseObject instanceof X509Certificate[]) {
            final X509Certificate[] chain = (X509Certificate[])responseObject;
            if (!noPrompt) {
                this.out(new Object[0]);
                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_TRUST_SERVER_RETRIEVED_CHAIN.get(hostname + ':' + port));
                boolean isFirst = true;
                for (final X509Certificate c : chain) {
                    this.out(new Object[0]);
                    if (isFirst) {
                        isFirst = false;
                        if (issuersOnly && chain.length > 1) {
                            this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_TRUST_SERVER_NOTE_OMITTED.get());
                            this.out(new Object[0]);
                        }
                    }
                    this.printCertificate(c, "", verbose);
                }
                this.out(new Object[0]);
                try {
                    if (!this.promptForYesNo(CertMessages.INFO_MANAGE_CERTS_TRUST_SERVER_PROMPT_TRUST.get())) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_TRUST_SERVER_CHAIN_REJECTED.get());
                        return ResultCode.USER_CANCELED;
                    }
                }
                catch (final LDAPException le4) {
                    Debug.debugException(le4);
                    this.err(new Object[0]);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
                    return le4.getResultCode();
                }
            }
            final LinkedHashMap<String, X509Certificate> certsByAlias = new LinkedHashMap<String, X509Certificate>(StaticUtils.computeMapCapacity(chain.length));
            for (int i = 0; i < chain.length; ++i) {
                if (i == 0) {
                    if (!issuersOnly || chain.length <= 1) {
                        certsByAlias.put(alias, chain[i]);
                    }
                }
                else if (i == 1 && chain.length == 2) {
                    certsByAlias.put(alias + "-issuer", chain[i]);
                }
                else {
                    certsByAlias.put(alias + "-issuer-" + i, chain[i]);
                }
            }
            for (final Map.Entry<String, X509Certificate> e2 : certsByAlias.entrySet()) {
                final String certAlias = e2.getKey();
                final X509Certificate cert = e2.getValue();
                try {
                    Validator.ensureFalse(hasCertificateAlias(keystore, certAlias) || hasKeyAlias(keystore, certAlias), "ERROR:  Alias '" + certAlias + "' is already in use in the " + "keystore.");
                    keystore.setCertificateEntry(certAlias, cert.toCertificate());
                }
                catch (final Exception ex) {
                    Debug.debugException(ex);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_TRUST_SERVER_ERROR_ADDING_CERT_TO_KS.get(cert.getSubjectDN()));
                    ex.printStackTrace(this.getErr());
                    return ResultCode.LOCAL_ERROR;
                }
            }
            try {
                writeKeystore(keystore, keystorePath, keystorePassword);
            }
            catch (final LDAPException le4) {
                Debug.debugException(le4);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le4.getMessage());
                return le4.getResultCode();
            }
            if (isNewKeystore) {
                this.out(new Object[0]);
                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_TRUST_SERVER_CERT_CREATED_KEYSTORE.get(getUserFriendlyKeystoreType(keystoreType)));
            }
            this.out(new Object[0]);
            if (certsByAlias.size() == 1) {
                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_TRUST_SERVER_ADDED_CERT_TO_KS.get());
            }
            else {
                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_TRUST_SERVER_ADDED_CERTS_TO_KS.get(certsByAlias.size()));
            }
            return ResultCode.SUCCESS;
        }
        if (responseObject instanceof CertException) {
            return ResultCode.LOCAL_ERROR;
        }
        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, String.valueOf(responseObject));
        return ResultCode.LOCAL_ERROR;
    }
    
    private ResultCode doCheckCertificateUsability() {
        final StringArgument aliasArgument = this.subCommandParser.getStringArgument("alias");
        final String alias = aliasArgument.getValue();
        final File keystorePath = this.getKeystorePath();
        String keystoreType;
        try {
            keystoreType = this.inferKeystoreType(keystorePath);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        char[] keystorePassword;
        try {
            keystorePassword = this.getKeystorePassword(keystorePath);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le2.getMessage());
            return le2.getResultCode();
        }
        KeyStore keystore;
        try {
            keystore = getKeystore(keystoreType, keystorePath, keystorePassword);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le3.getMessage());
            return le3.getResultCode();
        }
        Label_0395: {
            if (hasKeyAlias(keystore, alias)) {
                X509Certificate[] chain = null;
                Label_0470: {
                    try {
                        final Certificate[] genericChain = keystore.getCertificateChain(alias);
                        Validator.ensureTrue(genericChain.length > 0, "ERROR:  The keystore has a private key entry for alias '" + alias + "', but the associated certificate chain is empty.");
                        chain = new X509Certificate[genericChain.length];
                        for (int i = 0; i < genericChain.length; ++i) {
                            chain[i] = new X509Certificate(genericChain[i].getEncoded());
                        }
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_GOT_CHAIN.get(alias));
                        for (final X509Certificate c : chain) {
                            this.out(new Object[0]);
                            this.printCertificate(c, "", false);
                        }
                        break Label_0470;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_CANNOT_GET_CHAIN.get(alias));
                        e.printStackTrace(this.getErr());
                        return ResultCode.LOCAL_ERROR;
                    }
                    break Label_0395;
                }
                int numWarnings = 0;
                int numErrors = 0;
                if (chain[0].isSelfSigned()) {
                    this.err(new Object[0]);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_CHECK_USABILITY_CERT_IS_SELF_SIGNED.get(chain[0].getSubjectDN()));
                    ++numWarnings;
                }
                else if (chain.length == 1 || !chain[chain.length - 1].isSelfSigned()) {
                    this.err(new Object[0]);
                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_END_OF_CHAIN_NOT_SELF_SIGNED.get(alias));
                    ++numErrors;
                }
                else {
                    boolean chainError = false;
                    final StringBuilder nonMatchReason = new StringBuilder();
                    for (int j = 1; j < chain.length; ++j) {
                        if (!chain[j].isIssuerFor(chain[j - 1], nonMatchReason)) {
                            this.err(new Object[0]);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_CHAIN_ISSUER_MISMATCH.get(alias, chain[j].getSubjectDN(), chain[j - 1].getSubjectDN(), nonMatchReason));
                            ++numErrors;
                            chainError = true;
                        }
                    }
                    if (!chainError) {
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_CHAIN_COMPLETE.get());
                    }
                }
                if (chain.length > 1 && chain[chain.length - 1].isSelfSigned()) {
                    final X509Certificate caCert = chain[chain.length - 1];
                    try {
                        final String jvmDefaultTrustStoreType = this.inferKeystoreType(ManageCertificates.JVM_DEFAULT_CACERTS_FILE);
                        final KeyStore jvmDefaultTrustStore = KeyStore.getInstance(jvmDefaultTrustStoreType);
                        try (final FileInputStream inputStream = new FileInputStream(ManageCertificates.JVM_DEFAULT_CACERTS_FILE)) {
                            jvmDefaultTrustStore.load(inputStream, null);
                        }
                        boolean found = false;
                        final Enumeration<String> aliases = jvmDefaultTrustStore.aliases();
                        while (aliases.hasMoreElements()) {
                            final String jvmDefaultCertAlias = aliases.nextElement();
                            if (jvmDefaultTrustStore.isCertificateEntry(jvmDefaultCertAlias)) {
                                final Certificate c2 = jvmDefaultTrustStore.getCertificate(jvmDefaultCertAlias);
                                final X509Certificate xc = new X509Certificate(c2.getEncoded());
                                if (caCert.getSubjectDN().equals(xc.getSubjectDN()) && Arrays.equals(caCert.getSignatureValue().getBits(), xc.getSignatureValue().getBits())) {
                                    found = true;
                                    break;
                                }
                                continue;
                            }
                        }
                        if (found) {
                            this.out(new Object[0]);
                            this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_CA_TRUSTED_OK.get(caCert.getSubjectDN()));
                        }
                        else {
                            this.out(new Object[0]);
                            this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.NOTE_MANAGE_CERTS_CHECK_USABILITY_CA_NOT_IN_JVM_DEFAULT_TS.get(caCert.getSubjectDN()));
                        }
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        this.err(new Object[0]);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_CHECK_USABILITY_CHECK_CA_IN_TS_ERROR.get(caCert.getSubjectDN(), StaticUtils.getExceptionMessage(e2)));
                        ++numWarnings;
                    }
                }
                for (int k = 0; k < chain.length; ++k) {
                    final X509Certificate c3 = chain[k];
                    try {
                        if (c3.isSelfSigned()) {
                            c3.verifySignature(null);
                        }
                        else if (k + 1 < chain.length) {
                            c3.verifySignature(chain[k + 1]);
                        }
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_CERT_SIGNATURE_VALID.get(c3.getSubjectDN()));
                    }
                    catch (final CertException ce) {
                        this.err(new Object[0]);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, ce.getMessage());
                        ++numErrors;
                    }
                }
                final long currentTime = System.currentTimeMillis();
                final long thirtyDaysFromNow = currentTime + 2592000000L;
                for (int l = 0; l < chain.length; ++l) {
                    final X509Certificate c4 = chain[l];
                    if (c4.getNotBeforeTime() > currentTime) {
                        this.err(new Object[0]);
                        if (l == 0) {
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_END_CERT_NOT_YET_VALID.get(c4.getSubjectDN(), formatDateAndTime(c4.getNotBeforeDate())));
                        }
                        else {
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_ISSUER_CERT_NOT_YET_VALID.get(c4.getSubjectDN(), formatDateAndTime(c4.getNotBeforeDate())));
                        }
                        ++numErrors;
                    }
                    else if (c4.getNotAfterTime() < currentTime) {
                        this.err(new Object[0]);
                        if (l == 0) {
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_END_CERT_EXPIRED.get(c4.getSubjectDN(), formatDateAndTime(c4.getNotAfterDate())));
                        }
                        else {
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_ISSUER_CERT_EXPIRED.get(c4.getSubjectDN(), formatDateAndTime(c4.getNotAfterDate())));
                        }
                        ++numErrors;
                    }
                    else if (c4.getNotAfterTime() < thirtyDaysFromNow) {
                        this.err(new Object[0]);
                        if (l == 0) {
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_CHECK_USABILITY_END_CERT_NEAR_EXPIRATION.get(c4.getSubjectDN(), formatDateAndTime(c4.getNotAfterDate())));
                        }
                        else {
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_CHECK_USABILITY_ISSUER_CERT_NEAR_EXPIRATION.get(c4.getSubjectDN(), formatDateAndTime(c4.getNotAfterDate())));
                        }
                        ++numWarnings;
                    }
                    else if (l == 0) {
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_END_CERT_VALIDITY_OK.get(c4.getSubjectDN(), formatDateAndTime(c4.getNotAfterDate())));
                    }
                    else {
                        this.out(new Object[0]);
                        this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_ISSUER_CERT_VALIDITY_OK.get(c4.getSubjectDN(), formatDateAndTime(c4.getNotAfterDate())));
                    }
                }
                for (int l = 0; l < chain.length; ++l) {
                    boolean basicConstraintsFound = false;
                    boolean extendedKeyUsageFound = false;
                    boolean keyUsageFound = false;
                    final X509Certificate c5 = chain[l];
                    for (final X509CertificateExtension extension : c5.getExtensions()) {
                        if (extension instanceof ExtendedKeyUsageExtension) {
                            extendedKeyUsageFound = true;
                            if (l != 0) {
                                continue;
                            }
                            final ExtendedKeyUsageExtension e3 = (ExtendedKeyUsageExtension)extension;
                            if (!e3.getKeyPurposeIDs().contains(ExtendedKeyUsageID.TLS_SERVER_AUTHENTICATION.getOID())) {
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_END_CERT_BAD_EKU.get(c5.getSubjectDN()));
                                ++numErrors;
                            }
                            else {
                                this.out(new Object[0]);
                                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_END_CERT_GOOD_EKU.get(c5.getSubjectDN()));
                            }
                        }
                        else if (extension instanceof BasicConstraintsExtension) {
                            basicConstraintsFound = true;
                            if (l <= 0) {
                                continue;
                            }
                            final BasicConstraintsExtension e4 = (BasicConstraintsExtension)extension;
                            if (!e4.isCA()) {
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_ISSUER_CERT_BAD_BC_CA.get(c5.getSubjectDN()));
                                ++numErrors;
                            }
                            else if (e4.getPathLengthConstraint() != null && l - 1 > e4.getPathLengthConstraint()) {
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_ISSUER_CERT_BAD_BC_LENGTH.get(c5.getSubjectDN(), e4.getPathLengthConstraint(), chain[0].getSubjectDN(), l - 1));
                                ++numErrors;
                            }
                            else {
                                this.out(new Object[0]);
                                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_ISSUER_CERT_GOOD_BC.get(c5.getSubjectDN()));
                            }
                        }
                        else {
                            if (!(extension instanceof KeyUsageExtension)) {
                                continue;
                            }
                            keyUsageFound = true;
                            if (l <= 0) {
                                continue;
                            }
                            final KeyUsageExtension e5 = (KeyUsageExtension)extension;
                            if (!e5.isKeyCertSignBitSet()) {
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_ISSUER_NO_CERT_SIGN_KU.get(c5.getSubjectDN()));
                                ++numErrors;
                            }
                            else {
                                this.out(new Object[0]);
                                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_ISSUER_GOOD_KU.get(c5.getSubjectDN()));
                            }
                        }
                    }
                    if (l == 0) {
                        if (!extendedKeyUsageFound) {
                            this.err(new Object[0]);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_CHECK_USABILITY_NO_EKU.get(c5.getSubjectDN()));
                            ++numWarnings;
                        }
                    }
                    else {
                        if (!basicConstraintsFound) {
                            this.err(new Object[0]);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_CHECK_USABILITY_NO_BC.get(c5.getSubjectDN()));
                            ++numWarnings;
                        }
                        if (!keyUsageFound) {
                            this.err(new Object[0]);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_CHECK_USABILITY_NO_KU.get(c5.getSubjectDN()));
                            ++numWarnings;
                        }
                    }
                }
                boolean isIssuer = false;
                final BooleanArgument ignoreSHA1WarningArg = this.subCommandParser.getBooleanArgument("allow-sha-1-signature-for-issuer-certificates");
                final boolean ignoreSHA1SignatureWarningForIssuerCertificates = ignoreSHA1WarningArg != null && ignoreSHA1WarningArg.isPresent();
                for (final X509Certificate c6 : chain) {
                    final OID signatureAlgorithmOID = c6.getSignatureAlgorithmOID();
                    final SignatureAlgorithmIdentifier id = SignatureAlgorithmIdentifier.forOID(signatureAlgorithmOID);
                    if (id == null) {
                        this.err(new Object[0]);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_CHECK_USABILITY_UNKNOWN_SIG_ALG.get(c6.getSubjectDN(), signatureAlgorithmOID));
                        ++numWarnings;
                    }
                    else {
                        switch (id) {
                            case MD2_WITH_RSA:
                            case MD5_WITH_RSA: {
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_WEAK_SIG_ALG.get(c6.getSubjectDN(), id.getUserFriendlyName()));
                                ++numErrors;
                                break;
                            }
                            case SHA_1_WITH_RSA:
                            case SHA_1_WITH_DSA:
                            case SHA_1_WITH_ECDSA: {
                                if (isIssuer && ignoreSHA1SignatureWarningForIssuerCertificates) {
                                    this.err(new Object[0]);
                                    this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.WARN_MANAGE_CERTS_CHECK_USABILITY_ISSUER_WITH_SHA1_SIG.get(c6.getSubjectDN(), id.getUserFriendlyName(), ignoreSHA1WarningArg.getIdentifierString()));
                                    break;
                                }
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_WEAK_SIG_ALG.get(c6.getSubjectDN(), id.getUserFriendlyName()));
                                ++numErrors;
                                break;
                            }
                            case SHA_224_WITH_RSA:
                            case SHA_224_WITH_DSA:
                            case SHA_224_WITH_ECDSA:
                            case SHA_256_WITH_RSA:
                            case SHA_256_WITH_DSA:
                            case SHA_256_WITH_ECDSA:
                            case SHA_384_WITH_RSA:
                            case SHA_384_WITH_ECDSA:
                            case SHA_512_WITH_RSA:
                            case SHA_512_WITH_ECDSA: {
                                this.out(new Object[0]);
                                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_SIG_ALG_OK.get(c6.getSubjectDN(), id.getUserFriendlyName()));
                                break;
                            }
                        }
                    }
                    isIssuer = true;
                }
                for (final X509Certificate c6 : chain) {
                    if (c6.getDecodedPublicKey() != null && c6.getDecodedPublicKey() instanceof RSAPublicKey) {
                        final RSAPublicKey rsaPublicKey = (RSAPublicKey)c6.getDecodedPublicKey();
                        final byte[] modulusBytes = rsaPublicKey.getModulus().toByteArray();
                        int modulusSizeBits = modulusBytes.length * 8;
                        if (modulusBytes.length % 2 != 0 && modulusBytes[0] == 0) {
                            modulusSizeBits -= 8;
                        }
                        if (modulusSizeBits < 2048) {
                            this.err(new Object[0]);
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_WEAK_RSA_MODULUS.get(c6.getSubjectDN(), modulusSizeBits));
                            ++numErrors;
                        }
                        else {
                            this.out(new Object[0]);
                            this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_RSA_MODULUS_OK.get(c6.getSubjectDN(), modulusSizeBits));
                        }
                    }
                }
                switch (numErrors) {
                    case 0: {
                        switch (numWarnings) {
                            case 0: {
                                this.out(new Object[0]);
                                this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CHECK_USABILITY_NO_ERRORS_OR_WARNINGS.get());
                                return ResultCode.SUCCESS;
                            }
                            case 1: {
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_ONE_WARNING.get());
                                return ResultCode.PARAM_ERROR;
                            }
                            default: {
                                this.err(new Object[0]);
                                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_MULTIPLE_WARNINGS.get(numWarnings));
                                return ResultCode.PARAM_ERROR;
                            }
                        }
                        break;
                    }
                    case 1: {
                        this.err(new Object[0]);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_ONE_ERROR.get());
                        return ResultCode.PARAM_ERROR;
                    }
                    default: {
                        this.err(new Object[0]);
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_MULTIPLE_ERRORS.get(numErrors));
                        return ResultCode.PARAM_ERROR;
                    }
                }
            }
        }
        if (hasCertificateAlias(keystore, alias)) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_NO_PRIVATE_KEY.get(alias));
            return ResultCode.PARAM_ERROR;
        }
        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_CHECK_USABILITY_NO_SUCH_ALIAS.get(alias));
        return ResultCode.PARAM_ERROR;
    }
    
    private ResultCode doDisplayCertificateFile() {
        final FileArgument certificateFileArgument = this.subCommandParser.getFileArgument("certificate-file");
        final File certificateFile = certificateFileArgument.getValue();
        final BooleanArgument verboseArgument = this.subCommandParser.getBooleanArgument("verbose");
        final boolean verbose = verboseArgument != null && verboseArgument.isPresent();
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        if (displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent()) {
            final ArrayList<String> keytoolArgs = new ArrayList<String>(10);
            keytoolArgs.add("-printcert");
            keytoolArgs.add("-file");
            keytoolArgs.add(certificateFile.getAbsolutePath());
            if (verbose) {
                keytoolArgs.add("-v");
            }
            this.displayKeytoolCommand(keytoolArgs);
        }
        List<X509Certificate> certificates;
        try {
            certificates = readCertificatesFromFile(certificateFile);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        if (certificates.isEmpty()) {
            this.wrapOut(0, ManageCertificates.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_DISPLAY_CERT_NO_CERTS.get(certificateFile.getAbsolutePath()));
        }
        else {
            for (final X509Certificate c : certificates) {
                this.out(new Object[0]);
                this.printCertificate(c, "", verbose);
            }
        }
        return ResultCode.SUCCESS;
    }
    
    private ResultCode doDisplayCertificateSigningRequestFile() {
        final FileArgument csrFileArgument = this.subCommandParser.getFileArgument("certificate-signing-request-file");
        final File csrFile = csrFileArgument.getValue();
        final BooleanArgument verboseArgument = this.subCommandParser.getBooleanArgument("verbose");
        final boolean verbose = verboseArgument != null && verboseArgument.isPresent();
        final BooleanArgument displayKeytoolCommandArgument = this.subCommandParser.getBooleanArgument("display-keytool-command");
        if (displayKeytoolCommandArgument != null && displayKeytoolCommandArgument.isPresent()) {
            final ArrayList<String> keytoolArgs = new ArrayList<String>(10);
            keytoolArgs.add("-printcertreq");
            keytoolArgs.add("-file");
            keytoolArgs.add(csrFile.getAbsolutePath());
            keytoolArgs.add("-v");
            this.displayKeytoolCommand(keytoolArgs);
        }
        PKCS10CertificateSigningRequest csr;
        try {
            csr = readCertificateSigningRequestFromFile(csrFile);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, le.getMessage());
            return le.getResultCode();
        }
        this.out(new Object[0]);
        this.printCertificateSigningRequest(csr, verbose, "");
        return ResultCode.SUCCESS;
    }
    
    private void printCertificate(final X509Certificate certificate, final String indent, final boolean verbose) {
        if (verbose) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_VERSION.get(certificate.getVersion().getName()));
        }
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_SUBJECT_DN.get(certificate.getSubjectDN()));
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_ISSUER_DN.get(certificate.getIssuerDN()));
        if (verbose) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_SERIAL_NUMBER.get(toColonDelimitedHex(certificate.getSerialNumber().toByteArray())));
        }
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_VALIDITY_START.get(formatDateAndTime(certificate.getNotBeforeDate())));
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_VALIDITY_END.get(formatDateAndTime(certificate.getNotAfterDate())));
        final long currentTime = System.currentTimeMillis();
        if (currentTime < certificate.getNotBeforeTime()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_VALIDITY_STATE_NOT_YET_VALID.get());
        }
        else if (currentTime > certificate.getNotAfterTime()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_VALIDITY_STATE_EXPIRED.get());
        }
        else {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_VALIDITY_STATE_VALID.get());
        }
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_SIG_ALG.get(certificate.getSignatureAlgorithmNameOrOID()));
        if (verbose) {
            String signatureString;
            try {
                signatureString = toColonDelimitedHex(certificate.getSignatureValue().getBytes());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                signatureString = certificate.getSignatureValue().toString();
            }
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_SIG_VALUE.get());
            for (final String line : StaticUtils.wrapLine(signatureString, 78)) {
                this.out(indent + "     " + line);
            }
        }
        final String pkSummary = getPublicKeySummary(certificate.getPublicKeyAlgorithmOID(), certificate.getDecodedPublicKey(), certificate.getPublicKeyAlgorithmParameters());
        String pkAlg;
        if (pkSummary == null) {
            pkAlg = certificate.getPublicKeyAlgorithmNameOrOID();
        }
        else {
            pkAlg = certificate.getPublicKeyAlgorithmNameOrOID() + " (" + pkSummary + ')';
        }
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_PK_ALG.get(pkAlg));
        if (verbose) {
            this.printPublicKey(certificate.getEncodedPublicKey(), certificate.getDecodedPublicKey(), certificate.getPublicKeyAlgorithmParameters(), indent);
            if (certificate.getSubjectUniqueID() != null) {
                String subjectUniqueID;
                try {
                    subjectUniqueID = toColonDelimitedHex(certificate.getSubjectUniqueID().getBytes());
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    subjectUniqueID = certificate.getSubjectUniqueID().toString();
                }
                this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_SUBJECT_UNIQUE_ID.get());
                for (final String line2 : StaticUtils.wrapLine(subjectUniqueID, 78)) {
                    this.out(indent + "     " + line2);
                }
            }
            if (certificate.getIssuerUniqueID() != null) {
                String issuerUniqueID;
                try {
                    issuerUniqueID = toColonDelimitedHex(certificate.getIssuerUniqueID().getBytes());
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    issuerUniqueID = certificate.getIssuerUniqueID().toString();
                }
                this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_ISSUER_UNIQUE_ID.get());
                for (final String line2 : StaticUtils.wrapLine(issuerUniqueID, 78)) {
                    this.out(indent + "     " + line2);
                }
            }
            this.printExtensions(certificate.getExtensions(), indent);
        }
        try {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_FINGERPRINT.get("SHA-1", toColonDelimitedHex(certificate.getSHA1Fingerprint())));
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
        }
        try {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_FINGERPRINT.get("SHA-256", toColonDelimitedHex(certificate.getSHA256Fingerprint())));
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
        }
    }
    
    private void printCertificateSigningRequest(final PKCS10CertificateSigningRequest csr, final boolean verbose, final String indent) {
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CSR_LABEL_VERSION.get(csr.getVersion().getName()));
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_SUBJECT_DN.get(csr.getSubjectDN()));
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_SIG_ALG.get(csr.getSignatureAlgorithmNameOrOID()));
        if (verbose) {
            String signatureString;
            try {
                signatureString = toColonDelimitedHex(csr.getSignatureValue().getBytes());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                signatureString = csr.getSignatureValue().toString();
            }
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_SIG_VALUE.get());
            for (final String line : StaticUtils.wrapLine(signatureString, 78)) {
                this.out(indent + "     " + line);
            }
        }
        final String pkSummary = getPublicKeySummary(csr.getPublicKeyAlgorithmOID(), csr.getDecodedPublicKey(), csr.getPublicKeyAlgorithmParameters());
        String pkAlg;
        if (pkSummary == null) {
            pkAlg = csr.getPublicKeyAlgorithmNameOrOID();
        }
        else {
            pkAlg = csr.getPublicKeyAlgorithmNameOrOID() + " (" + pkSummary + ')';
        }
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_PK_ALG.get(pkAlg));
        if (verbose) {
            this.printPublicKey(csr.getEncodedPublicKey(), csr.getDecodedPublicKey(), csr.getPublicKeyAlgorithmParameters(), indent);
            this.printExtensions(csr.getExtensions(), indent);
        }
    }
    
    private void printPublicKey(final ASN1BitString encodedPublicKey, final DecodedPublicKey decodedPublicKey, final ASN1Element parameters, final String indent) {
        if (decodedPublicKey == null) {
            String pkString;
            try {
                pkString = toColonDelimitedHex(encodedPublicKey.getBytes());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                pkString = encodedPublicKey.toString();
            }
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_ENCODED_PK.get());
            for (final String line : StaticUtils.wrapLine(pkString, 78)) {
                this.out(indent + "     " + line);
            }
            return;
        }
        if (decodedPublicKey instanceof RSAPublicKey) {
            final RSAPublicKey rsaPublicKey = (RSAPublicKey)decodedPublicKey;
            final byte[] modulusBytes = rsaPublicKey.getModulus().toByteArray();
            int modulusSizeBits = modulusBytes.length * 8;
            if (modulusBytes.length % 2 != 0 && modulusBytes[0] == 0) {
                modulusSizeBits -= 8;
            }
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_RSA_MODULUS.get(modulusSizeBits));
            final String modulusHex = toColonDelimitedHex(modulusBytes);
            for (final String line2 : StaticUtils.wrapLine(modulusHex, 78)) {
                this.out(indent + "     " + line2);
            }
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_RSA_EXPONENT.get(toColonDelimitedHex(rsaPublicKey.getPublicExponent().toByteArray())));
        }
        else if (decodedPublicKey instanceof EllipticCurvePublicKey) {
            final EllipticCurvePublicKey ecPublicKey = (EllipticCurvePublicKey)decodedPublicKey;
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EC_IS_COMPRESSED.get(String.valueOf(ecPublicKey.usesCompressedForm())));
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EC_X.get(String.valueOf(ecPublicKey.getXCoordinate())));
            if (ecPublicKey.getYCoordinate() == null) {
                this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EC_Y_IS_EVEN.get(String.valueOf(ecPublicKey.yCoordinateIsEven())));
            }
            else {
                this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EC_Y.get(String.valueOf(ecPublicKey.getYCoordinate())));
            }
        }
    }
    
    private static String getPublicKeySummary(final OID publicKeyAlgorithmOID, final DecodedPublicKey publicKey, final ASN1Element parameters) {
        if (publicKey instanceof RSAPublicKey) {
            final RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKey;
            final byte[] modulusBytes = rsaPublicKey.getModulus().toByteArray();
            int modulusSizeBits = modulusBytes.length * 8;
            if (modulusBytes.length % 2 != 0 && modulusBytes[0] == 0) {
                modulusSizeBits -= 8;
            }
            return CertMessages.INFO_MANAGE_CERTS_GET_PK_SUMMARY_RSA_MODULUS_SIZE.get(modulusSizeBits);
        }
        if (parameters != null && publicKeyAlgorithmOID.equals(PublicKeyAlgorithmIdentifier.EC.getOID())) {
            try {
                final OID namedCurveOID = parameters.decodeAsObjectIdentifier().getOID();
                return NamedCurve.getNameOrOID(namedCurveOID);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        return null;
    }
    
    void printExtensions(final List<X509CertificateExtension> extensions, final String indent) {
        if (extensions.isEmpty()) {
            return;
        }
        this.out(indent + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXTENSIONS.get());
        for (final X509CertificateExtension extension : extensions) {
            if (extension instanceof AuthorityKeyIdentifierExtension) {
                this.out(indent + "     " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_AUTH_KEY_ID_EXT.get());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_OID.get(extension.getOID().toString()));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IS_CRITICAL.get(String.valueOf(extension.isCritical())));
                final AuthorityKeyIdentifierExtension e = (AuthorityKeyIdentifierExtension)extension;
                if (e.getKeyIdentifier() != null) {
                    this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_AUTH_KEY_ID_ID.get());
                    final String idHex = toColonDelimitedHex(e.getKeyIdentifier().getValue());
                    for (final String line : StaticUtils.wrapLine(idHex, 78)) {
                        this.out(indent + "               " + line);
                    }
                }
                if (e.getAuthorityCertIssuer() != null) {
                    this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_AUTH_KEY_ID_ISSUER.get());
                    this.printGeneralNames(e.getAuthorityCertIssuer(), indent + "               ");
                }
                if (e.getAuthorityCertSerialNumber() == null) {
                    continue;
                }
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_AUTH_KEY_ID_SERIAL.get(toColonDelimitedHex(e.getAuthorityCertSerialNumber().toByteArray())));
            }
            else if (extension instanceof BasicConstraintsExtension) {
                this.out(indent + "     " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_BASIC_CONST_EXT.get());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_OID.get(extension.getOID().toString()));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IS_CRITICAL.get(String.valueOf(extension.isCritical())));
                final BasicConstraintsExtension e2 = (BasicConstraintsExtension)extension;
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_BASIC_CONST_IS_CA.get(String.valueOf(e2.isCA())));
                if (e2.getPathLengthConstraint() == null) {
                    continue;
                }
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_BASIC_CONST_LENGTH.get(e2.getPathLengthConstraint()));
            }
            else if (extension instanceof CRLDistributionPointsExtension) {
                this.out(indent + "     " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_CRL_DP_EXT.get());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_OID.get(extension.getOID().toString()));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IS_CRITICAL.get(String.valueOf(extension.isCritical())));
                final CRLDistributionPointsExtension crlDPE = (CRLDistributionPointsExtension)extension;
                for (final CRLDistributionPoint dp : crlDPE.getCRLDistributionPoints()) {
                    this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_CRL_DP_HEADER.get());
                    if (dp.getFullName() != null) {
                        this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_CRL_DP_FULL_NAME.get());
                        this.printGeneralNames(dp.getFullName(), indent + "                    ");
                    }
                    if (dp.getNameRelativeToCRLIssuer() != null) {
                        this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_CRL_DP_REL_NAME.get(dp.getNameRelativeToCRLIssuer()));
                    }
                    if (!dp.getPotentialRevocationReasons().isEmpty()) {
                        this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_CRL_DP_REASON.get());
                        for (final CRLDistributionPointRevocationReason r : dp.getPotentialRevocationReasons()) {
                            this.out(indent + "                    " + r.getName());
                        }
                    }
                    if (dp.getCRLIssuer() != null) {
                        this.out(indent + "              " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_CRL_DP_CRL_ISSUER.get());
                        this.printGeneralNames(dp.getCRLIssuer(), indent + "                    ");
                    }
                }
            }
            else if (extension instanceof ExtendedKeyUsageExtension) {
                this.out(indent + "     " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_EKU_EXT.get());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_OID.get(extension.getOID().toString()));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IS_CRITICAL.get(String.valueOf(extension.isCritical())));
                final ExtendedKeyUsageExtension e3 = (ExtendedKeyUsageExtension)extension;
                for (final OID oid : e3.getKeyPurposeIDs()) {
                    final ExtendedKeyUsageID id = ExtendedKeyUsageID.forOID(oid);
                    if (id == null) {
                        this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_EKU_ID.get(oid));
                    }
                    else {
                        this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_EKU_ID.get(id.getName()));
                    }
                }
            }
            else if (extension instanceof IssuerAlternativeNameExtension) {
                this.out(indent + "     " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IAN_EXT.get());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_OID.get(extension.getOID().toString()));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IS_CRITICAL.get(String.valueOf(extension.isCritical())));
                final IssuerAlternativeNameExtension e4 = (IssuerAlternativeNameExtension)extension;
                this.printGeneralNames(e4.getGeneralNames(), indent + "          ");
            }
            else if (extension instanceof KeyUsageExtension) {
                this.out(indent + "     " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_EXT.get());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_OID.get(extension.getOID().toString()));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IS_CRITICAL.get(String.valueOf(extension.isCritical())));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_USAGES.get());
                final KeyUsageExtension kue = (KeyUsageExtension)extension;
                if (kue.isDigitalSignatureBitSet()) {
                    this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_DS.get());
                }
                if (kue.isNonRepudiationBitSet()) {
                    this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_NR.get());
                }
                if (kue.isKeyEnciphermentBitSet()) {
                    this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_KE.get());
                }
                if (kue.isDataEnciphermentBitSet()) {
                    this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_DE.get());
                }
                if (kue.isKeyAgreementBitSet()) {
                    this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_KA.get());
                }
                if (kue.isKeyCertSignBitSet()) {
                    this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_KCS.get());
                }
                if (kue.isCRLSignBitSet()) {
                    this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_CRL_SIGN.get());
                }
                if (kue.isEncipherOnlyBitSet()) {
                    this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_EO.get());
                }
                if (!kue.isDecipherOnlyBitSet()) {
                    continue;
                }
                this.out(indent + "               " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_KU_DO.get());
            }
            else if (extension instanceof SubjectAlternativeNameExtension) {
                this.out(indent + "     " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_SAN_EXT.get());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_OID.get(extension.getOID().toString()));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IS_CRITICAL.get(String.valueOf(extension.isCritical())));
                final SubjectAlternativeNameExtension e5 = (SubjectAlternativeNameExtension)extension;
                this.printGeneralNames(e5.getGeneralNames(), indent + "          ");
            }
            else if (extension instanceof SubjectKeyIdentifierExtension) {
                this.out(indent + "     " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_SKI_EXT.get());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_OID.get(extension.getOID().toString()));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IS_CRITICAL.get(String.valueOf(extension.isCritical())));
                final SubjectKeyIdentifierExtension e6 = (SubjectKeyIdentifierExtension)extension;
                final String idHex = toColonDelimitedHex(e6.getKeyIdentifier().getValue());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_SKI_ID.get());
                for (final String line : StaticUtils.wrapLine(idHex, 78)) {
                    this.out(indent + "               " + line);
                }
            }
            else {
                this.out(indent + "     " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_GENERIC.get());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_OID.get(extension.getOID().toString()));
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_IS_CRITICAL.get(String.valueOf(extension.isCritical())));
                final String valueHex = toColonDelimitedHex(extension.getValue());
                this.out(indent + "          " + CertMessages.INFO_MANAGE_CERTS_PRINT_CERT_LABEL_EXT_VALUE.get());
                this.getOut().print(StaticUtils.toHexPlusASCII(extension.getValue(), indent.length() + 15));
            }
        }
    }
    
    private void printGeneralNames(final GeneralNames generalNames, final String indent) {
        for (final String dnsName : generalNames.getDNSNames()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_GENERAL_NAMES_LABEL_DNS.get(dnsName));
        }
        for (final InetAddress ipAddress : generalNames.getIPAddresses()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_GENERAL_NAMES_LABEL_IP.get(ipAddress.getHostAddress()));
        }
        for (final String name : generalNames.getRFC822Names()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_GENERAL_NAMES_LABEL_RFC_822_NAME.get(name));
        }
        for (final DN dn : generalNames.getDirectoryNames()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_GENERAL_NAMES_LABEL_DIRECTORY_NAME.get(String.valueOf(dn)));
        }
        for (final String uri : generalNames.getUniformResourceIdentifiers()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_GENERAL_NAMES_LABEL_URI.get(uri));
        }
        for (final OID oid : generalNames.getRegisteredIDs()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_GENERAL_NAMES_LABEL_REGISTERED_ID.get(oid.toString()));
        }
        if (!generalNames.getOtherNames().isEmpty()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_GENERAL_NAMES_LABEL_OTHER_NAME_COUNT.get(generalNames.getOtherNames().size()));
        }
        if (!generalNames.getX400Addresses().isEmpty()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_GENERAL_NAMES_LABEL_X400_ADDR_COUNT.get(generalNames.getX400Addresses().size()));
        }
        if (!generalNames.getEDIPartyNames().isEmpty()) {
            this.out(indent + CertMessages.INFO_MANAGE_CERTS_GENERAL_NAMES_LABEL_EDI_PARTY_NAME_COUNT.get(generalNames.getEDIPartyNames().size()));
        }
    }
    
    private static void writePEMCertificate(final PrintStream printStream, final byte[] encodedCertificate) {
        final String certBase64 = Base64.encode(encodedCertificate);
        printStream.println("-----BEGIN CERTIFICATE-----");
        for (final String line : StaticUtils.wrapLine(certBase64, 64)) {
            printStream.println(line);
        }
        printStream.println("-----END CERTIFICATE-----");
    }
    
    private static void writePEMCertificateSigningRequest(final PrintStream printStream, final byte[] encodedCSR) {
        final String certBase64 = Base64.encode(encodedCSR);
        printStream.println("-----BEGIN CERTIFICATE REQUEST-----");
        for (final String line : StaticUtils.wrapLine(certBase64, 64)) {
            printStream.println(line);
        }
        printStream.println("-----END CERTIFICATE REQUEST-----");
    }
    
    private static void writePEMPrivateKey(final PrintStream printStream, final byte[] encodedPrivateKey) {
        final String certBase64 = Base64.encode(encodedPrivateKey);
        printStream.println("-----BEGIN PRIVATE KEY-----");
        for (final String line : StaticUtils.wrapLine(certBase64, 64)) {
            printStream.println(line);
        }
        printStream.println("-----END PRIVATE KEY-----");
    }
    
    private void displayKeytoolCommand(final List<String> keytoolArgs) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("#      keytool");
        boolean lastWasArgName = false;
        for (final String arg : keytoolArgs) {
            if (arg.startsWith("-")) {
                buffer.append(" \\");
                buffer.append(StaticUtils.EOL);
                buffer.append("#           ");
                buffer.append(arg);
                lastWasArgName = true;
            }
            else if (lastWasArgName) {
                buffer.append(' ');
                buffer.append(StaticUtils.cleanExampleCommandLineArgument(arg));
                lastWasArgName = false;
            }
            else {
                buffer.append(" \\");
                buffer.append(StaticUtils.EOL);
                buffer.append("#           ");
                buffer.append(arg);
                lastWasArgName = false;
            }
        }
        this.out(new Object[0]);
        this.out(CertMessages.INFO_MANAGE_CERTS_APPROXIMATE_KEYTOOL_COMMAND.get());
        this.out(buffer);
        this.out(new Object[0]);
    }
    
    private File getKeystorePath() {
        final FileArgument keystoreArgument = this.subCommandParser.getFileArgument("keystore");
        if (keystoreArgument != null) {
            return keystoreArgument.getValue();
        }
        return null;
    }
    
    private char[] getKeystorePassword(final File keystoreFile) throws LDAPException {
        return this.getKeystorePassword(keystoreFile, null);
    }
    
    private char[] getKeystorePassword(final File keystoreFile, final String prefix) throws LDAPException {
        String prefixDash;
        if (prefix == null) {
            prefixDash = "";
        }
        else {
            prefixDash = prefix + '-';
        }
        final StringArgument keystorePasswordArgument = this.subCommandParser.getStringArgument(prefixDash + "keystore-password");
        if (keystorePasswordArgument != null && keystorePasswordArgument.isPresent()) {
            final char[] keystorePWChars = keystorePasswordArgument.getValue().toCharArray();
            if (!keystoreFile.exists() && keystorePWChars.length < 6) {
                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_GET_KS_PW_TOO_SHORT.get());
            }
            return keystorePWChars;
        }
        else {
            final FileArgument keystorePasswordFileArgument = this.subCommandParser.getFileArgument(prefixDash + "keystore-password-file");
            if (keystorePasswordFileArgument != null && keystorePasswordFileArgument.isPresent()) {
                final File f = keystorePasswordFileArgument.getValue();
                try {
                    final char[] passwordChars = this.getPasswordFileReader().readPassword(f);
                    if (passwordChars.length < 6) {
                        throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_GET_KS_PW_TOO_SHORT.get());
                    }
                    return passwordChars;
                }
                catch (final LDAPException e) {
                    Debug.debugException(e);
                    throw e;
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_GET_KS_PW_ERROR_READING_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), e2);
                }
            }
            final BooleanArgument promptArgument = this.subCommandParser.getBooleanArgument("prompt-for-" + prefixDash + "keystore-password");
            if (promptArgument == null || !promptArgument.isPresent()) {
                return null;
            }
            this.out(new Object[0]);
            if (!keystoreFile.exists() || "new".equals(prefix)) {
                char[] pwChars;
                char[] confirmChars;
                while (true) {
                    String prompt1;
                    if ("new".equals(prefix)) {
                        prompt1 = CertMessages.INFO_MANAGE_CERTS_KEY_KS_PW_EXISTING_NEW_PROMPT.get();
                    }
                    else {
                        prompt1 = CertMessages.INFO_MANAGE_CERTS_KEY_KS_PW_NEW_PROMPT_1.get(keystoreFile.getAbsolutePath());
                    }
                    pwChars = this.promptForPassword(prompt1, false);
                    if (pwChars.length < 6) {
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GET_KS_PW_TOO_SHORT.get());
                        this.err(new Object[0]);
                    }
                    else {
                        confirmChars = this.promptForPassword(CertMessages.INFO_MANAGE_CERTS_KEY_KS_PW_NEW_PROMPT_2.get(), true);
                        if (Arrays.equals(pwChars, confirmChars)) {
                            break;
                        }
                        this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_KEY_KS_PW_PROMPT_MISMATCH.get());
                        this.err(new Object[0]);
                    }
                }
                Arrays.fill(confirmChars, '\0');
                return pwChars;
            }
            if (prefix != null && prefix.equals("current")) {
                return this.promptForPassword(CertMessages.INFO_MANAGE_CERTS_KEY_KS_PW_EXISTING_CURRENT_PROMPT.get(keystoreFile.getAbsolutePath()), false);
            }
            return this.promptForPassword(CertMessages.INFO_MANAGE_CERTS_KEY_KS_PW_EXISTING_PROMPT.get(keystoreFile.getAbsolutePath()), false);
        }
    }
    
    private char[] promptForPassword(final String prompt, final boolean allowEmpty) throws LDAPException {
        final Iterator<String> iterator = StaticUtils.wrapLine(prompt, ManageCertificates.WRAP_COLUMN).iterator();
        while (iterator.hasNext()) {
            final String line = iterator.next();
            if (iterator.hasNext()) {
                this.out(line);
            }
            else {
                this.getOut().print(line);
            }
        }
        final char[] passwordChars = PasswordReader.readPasswordChars();
        if (passwordChars.length == 0 && !allowEmpty) {
            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_PROMPT_FOR_PW_EMPTY_PW.get());
            this.err(new Object[0]);
            return this.promptForPassword(prompt, allowEmpty);
        }
        return passwordChars;
    }
    
    private boolean promptForYesNo(final String prompt) throws LDAPException {
        while (true) {
            final List<String> lines = StaticUtils.wrapLine(prompt + ' ', ManageCertificates.WRAP_COLUMN);
            final Iterator<String> lineIterator = lines.iterator();
            while (lineIterator.hasNext()) {
                final String line = lineIterator.next();
                if (lineIterator.hasNext()) {
                    this.out(line);
                }
                else {
                    this.getOut().print(line);
                }
            }
            try {
                final String response = this.readLineFromIn();
                if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y")) {
                    return true;
                }
                if (response.equalsIgnoreCase("no") || response.equalsIgnoreCase("n")) {
                    return false;
                }
                this.err(new Object[0]);
                this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_PROMPT_FOR_YES_NO_INVALID_RESPONSE.get());
                this.err(new Object[0]);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_PROMPT_FOR_YES_NO_READ_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
    }
    
    private String readLineFromIn() throws IOException {
        final ByteStringBuffer buffer = new ByteStringBuffer();
        while (true) {
            final int byteRead = this.in.read();
            if (byteRead < 0) {
                if (buffer.isEmpty()) {
                    return null;
                }
                return buffer.toString();
            }
            else {
                if (byteRead == 10) {
                    return buffer.toString();
                }
                if (byteRead == 13) {
                    final int nextByteRead = this.in.read();
                    Validator.ensureTrue(nextByteRead < 0 || nextByteRead == 10, "ERROR:  Read a carriage return from standard input that was not followed by a new line.");
                    return buffer.toString();
                }
                buffer.append((byte)(byteRead & 0xFF));
            }
        }
    }
    
    private char[] getPrivateKeyPassword(final KeyStore keystore, final String alias, final char[] keystorePassword) throws LDAPException {
        return this.getPrivateKeyPassword(keystore, alias, null, keystorePassword);
    }
    
    private char[] getPrivateKeyPassword(final KeyStore keystore, final String alias, final String prefix, final char[] keystorePassword) throws LDAPException {
        String prefixDash;
        if (prefix == null) {
            prefixDash = "";
        }
        else {
            prefixDash = prefix + '-';
        }
        final StringArgument privateKeyPasswordArgument = this.subCommandParser.getStringArgument(prefixDash + "private-key-password");
        if (privateKeyPasswordArgument == null || !privateKeyPasswordArgument.isPresent()) {
            final FileArgument privateKeyPasswordFileArgument = this.subCommandParser.getFileArgument(prefixDash + "private-key-password-file");
            if (privateKeyPasswordFileArgument != null && privateKeyPasswordFileArgument.isPresent()) {
                final File f = privateKeyPasswordFileArgument.getValue();
                try {
                    final char[] passwordChars = this.getPasswordFileReader().readPassword(f);
                    if (passwordChars.length < 6) {
                        throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_GET_PK_PW_EMPTY_FILE.get(f.getAbsolutePath()));
                    }
                    return passwordChars;
                }
                catch (final LDAPException e) {
                    Debug.debugException(e);
                    throw e;
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_GET_PK_PW_ERROR_READING_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), e2);
                }
            }
            final BooleanArgument promptArgument = this.subCommandParser.getBooleanArgument("prompt-for-" + prefixDash + "private-key-password");
            if (promptArgument != null && promptArgument.isPresent()) {
                this.out(new Object[0]);
                try {
                    if ((hasKeyAlias(keystore, alias) || hasCertificateAlias(keystore, alias)) && !"new".equals(prefix)) {
                        String prompt;
                        if ("current".equals(prefix)) {
                            prompt = CertMessages.INFO_MANAGE_CERTS_GET_PK_PW_CURRENT_PROMPT.get(alias);
                        }
                        else {
                            prompt = CertMessages.INFO_MANAGE_CERTS_GET_PK_PW_EXISTING_PROMPT.get(alias);
                        }
                        return this.promptForPassword(prompt, false);
                    }
                    char[] pwChars;
                    char[] confirmChars;
                    while (true) {
                        String prompt;
                        if ("new".equals(prefix)) {
                            prompt = CertMessages.INFO_MANAGE_CERTS_GET_PK_PW_NEW_PROMPT.get();
                        }
                        else {
                            prompt = CertMessages.INFO_MANAGE_CERTS_GET_PK_PW_NEW_PROMPT_1.get(alias);
                        }
                        pwChars = this.promptForPassword(prompt, false);
                        if (pwChars.length < 6) {
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GET_PK_PW_TOO_SHORT.get());
                            this.err(new Object[0]);
                        }
                        else {
                            confirmChars = this.promptForPassword(CertMessages.INFO_MANAGE_CERTS_GET_PK_PW_NEW_PROMPT_2.get(), true);
                            if (Arrays.equals(pwChars, confirmChars)) {
                                break;
                            }
                            this.wrapErr(0, ManageCertificates.WRAP_COLUMN, CertMessages.ERR_MANAGE_CERTS_GET_PK_PW_PROMPT_MISMATCH.get());
                            this.err(new Object[0]);
                        }
                    }
                    Arrays.fill(confirmChars, '\0');
                    return pwChars;
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    throw le;
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_GET_PK_PW_PROMPT_ERROR.get(alias, StaticUtils.getExceptionMessage(e2)), e2);
                }
            }
            return keystorePassword;
        }
        final char[] pkPasswordChars = privateKeyPasswordArgument.getValue().toCharArray();
        if (pkPasswordChars.length < 6 && !hasCertificateAlias(keystore, alias) && !hasKeyAlias(keystore, alias)) {
            throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_GET_PK_PW_TOO_SHORT.get());
        }
        return pkPasswordChars;
    }
    
    private String inferKeystoreType(final File keystorePath) throws LDAPException {
        if (!keystorePath.exists()) {
            final StringArgument keystoreTypeArgument = this.subCommandParser.getStringArgument("keystore-type");
            if (keystoreTypeArgument == null || !keystoreTypeArgument.isPresent()) {
                return ManageCertificates.DEFAULT_KEYSTORE_TYPE;
            }
            final String ktaValue = keystoreTypeArgument.getValue();
            if (ktaValue.equalsIgnoreCase("PKCS12") || ktaValue.equalsIgnoreCase("PKCS 12") || ktaValue.equalsIgnoreCase("PKCS#12") || ktaValue.equalsIgnoreCase("PKCS #12")) {
                return "PKCS12";
            }
            return "JKS";
        }
        else {
            try (final FileInputStream inputStream = new FileInputStream(keystorePath)) {
                final int firstByte = inputStream.read();
                if (firstByte < 0) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_INFER_KS_TYPE_EMPTY_FILE.get(keystorePath.getAbsolutePath()));
                }
                if (firstByte == 48) {
                    return "PKCS12";
                }
                if (firstByte == 254) {
                    return "JKS";
                }
                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_INFER_KS_TYPE_UNEXPECTED_FIRST_BYTE.get(keystorePath.getAbsolutePath(), StaticUtils.toHex((byte)(firstByte & 0xFF))));
            }
            catch (final LDAPException e) {
                Debug.debugException(e);
                throw e;
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_INFER_KS_TYPE_ERROR_READING_FILE.get(keystorePath.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), e2);
            }
        }
    }
    
    static String getUserFriendlyKeystoreType(final String keystoreType) {
        if (keystoreType.equalsIgnoreCase("JKS")) {
            return "JKS";
        }
        if (keystoreType.equalsIgnoreCase("PKCS12") || keystoreType.equalsIgnoreCase("PKCS 12") || keystoreType.equalsIgnoreCase("PKCS#12") || keystoreType.equalsIgnoreCase("PKCS #12")) {
            return "PKCS #12";
        }
        return keystoreType;
    }
    
    static KeyStore getKeystore(final String keystoreType, final File keystorePath, final char[] keystorePassword) throws LDAPException {
        KeyStore keystore;
        try {
            keystore = KeyStore.getInstance(keystoreType);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_CANNOT_INSTANTIATE_KS_TYPE.get(keystoreType, StaticUtils.getExceptionMessage(e)), e);
        }
        InputStream inputStream;
        try {
            if (keystorePath.exists()) {
                inputStream = new FileInputStream(keystorePath);
            }
            else {
                inputStream = null;
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_CANNOT_OPEN_KS_FILE_FOR_READING.get(keystorePath.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), e2);
        }
        try {
            keystore.load(inputStream, keystorePassword);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            final Throwable cause = e2.getCause();
            if (e2 instanceof IOException && cause != null && cause instanceof UnrecoverableKeyException && keystorePassword != null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_CANNOT_LOAD_KS_WRONG_PW.get(keystorePath.getAbsolutePath()), e2);
            }
            throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_ERROR_CANNOT_LOAD_KS.get(keystorePath.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), e2);
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
            }
        }
        return keystore;
    }
    
    public static List<X509Certificate> readCertificatesFromFile(final File f) throws LDAPException {
        try (final BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(f))) {
            inputStream.mark(1);
            final int firstByte = inputStream.read();
            if (firstByte < 0) {
                return Collections.emptyList();
            }
            inputStream.reset();
            final ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>(5);
            if ((firstByte & 0xFF) == 0x30) {
                while (true) {
                    ASN1Element certElement;
                    try {
                        certElement = ASN1Element.readFrom(inputStream);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CERTS_FROM_FILE_DER_NOT_VALID_ASN1.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
                    }
                    if (certElement == null) {
                        break;
                    }
                    try {
                        certList.add(new X509Certificate(certElement.encode()));
                    }
                    catch (final CertException e2) {
                        Debug.debugException(e2);
                        throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CERTS_FROM_FILE_DER_NOT_VALID_CERT.get(f.getAbsolutePath(), e2.getMessage()), e2);
                    }
                }
                return certList;
            }
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                boolean inCert = false;
                final StringBuilder buffer = new StringBuilder();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        if (inCert) {
                            throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CERTS_FROM_FILE_EOF_WITHOUT_END.get(f.getAbsolutePath()));
                        }
                        return certList;
                    }
                    else {
                        line = line.trim();
                        if (line.isEmpty()) {
                            continue;
                        }
                        if (line.startsWith("#")) {
                            continue;
                        }
                        if (line.equals("-----BEGIN CERTIFICATE-----")) {
                            if (inCert) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CERTS_FROM_FILE_MULTIPLE_BEGIN.get(f.getAbsolutePath()));
                            }
                            inCert = true;
                        }
                        else if (line.equals("-----END CERTIFICATE-----")) {
                            if (!inCert) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CERTS_FROM_FILE_END_WITHOUT_BEGIN.get(f.getAbsolutePath()));
                            }
                            inCert = false;
                            byte[] certBytes;
                            try {
                                certBytes = Base64.decode(buffer.toString());
                            }
                            catch (final Exception e3) {
                                Debug.debugException(e3);
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CERTS_FROM_FILE_PEM_CERT_NOT_BASE64.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e3)), e3);
                            }
                            try {
                                certList.add(new X509Certificate(certBytes));
                            }
                            catch (final CertException e4) {
                                Debug.debugException(e4);
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CERTS_FROM_FILE_PEM_CERT_NOT_CERT.get(f.getAbsolutePath(), e4.getMessage()), e4);
                            }
                            buffer.setLength(0);
                        }
                        else {
                            if (!inCert) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CERTS_FROM_FILE_DATA_WITHOUT_BEGIN.get(f.getAbsolutePath()));
                            }
                            buffer.append(line);
                        }
                    }
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e5) {
            Debug.debugException(e5);
            throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CERTS_FROM_FILE_READ_ERROR.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e5)), e5);
        }
    }
    
    static PKCS8PrivateKey readPrivateKeyFromFile(final File f) throws LDAPException {
        try (final BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(f))) {
            inputStream.mark(1);
            final int firstByte = inputStream.read();
            if (firstByte < 0) {
                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_EMPTY_FILE.get(f.getAbsolutePath()));
            }
            inputStream.reset();
            PKCS8PrivateKey privateKey = null;
            if ((firstByte & 0xFF) == 0x30) {
                while (true) {
                    ASN1Element pkElement;
                    try {
                        pkElement = ASN1Element.readFrom(inputStream);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_DER_NOT_VALID_ASN1.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
                    }
                    if (pkElement == null) {
                        if (privateKey == null) {
                            throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_EMPTY_FILE.get(f.getAbsolutePath()));
                        }
                        return privateKey;
                    }
                    else {
                        if (privateKey == null) {
                            try {
                                privateKey = new PKCS8PrivateKey(pkElement.encode());
                                continue;
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_DER_NOT_VALID_PK.get(f.getAbsolutePath(), e.getMessage()), e);
                            }
                            break;
                        }
                        break;
                    }
                }
                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_MULTIPLE_KEYS.get(f.getAbsolutePath()));
            }
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                boolean inKey = false;
                boolean isRSAKey = false;
                final StringBuilder buffer = new StringBuilder();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        if (inKey) {
                            throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_EOF_WITHOUT_END.get(f.getAbsolutePath()));
                        }
                        if (privateKey == null) {
                            throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_EMPTY_FILE.get(f.getAbsolutePath()));
                        }
                        return privateKey;
                    }
                    else {
                        line = line.trim();
                        if (line.isEmpty()) {
                            continue;
                        }
                        if (line.startsWith("#")) {
                            continue;
                        }
                        if (line.equals("-----BEGIN PRIVATE KEY-----") || line.equals("-----BEGIN RSA PRIVATE KEY-----")) {
                            if (inKey) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_MULTIPLE_BEGIN.get(f.getAbsolutePath()));
                            }
                            if (privateKey != null) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_MULTIPLE_KEYS.get(f.getAbsolutePath()));
                            }
                            inKey = true;
                            if (!line.equals("-----BEGIN RSA PRIVATE KEY-----")) {
                                continue;
                            }
                            isRSAKey = true;
                        }
                        else if (line.equals("-----END PRIVATE KEY-----") || line.equals("-----END RSA PRIVATE KEY-----")) {
                            if (!inKey) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_END_WITHOUT_BEGIN.get(f.getAbsolutePath()));
                            }
                            inKey = false;
                            byte[] pkBytes;
                            try {
                                pkBytes = Base64.decode(buffer.toString());
                            }
                            catch (final Exception e2) {
                                Debug.debugException(e2);
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_PEM_PK_NOT_BASE64.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), e2);
                            }
                            if (isRSAKey) {
                                pkBytes = PKCS8PrivateKey.wrapRSAPrivateKey(pkBytes);
                            }
                            try {
                                privateKey = new PKCS8PrivateKey(pkBytes);
                            }
                            catch (final CertException e3) {
                                Debug.debugException(e3);
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_PEM_PK_NOT_PK.get(f.getAbsolutePath(), e3.getMessage()), e3);
                            }
                            buffer.setLength(0);
                        }
                        else {
                            if (!inKey) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_DATA_WITHOUT_BEGIN.get(f.getAbsolutePath()));
                            }
                            buffer.append(line);
                        }
                    }
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e4) {
            Debug.debugException(e4);
            throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_PK_FROM_FILE_READ_ERROR.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e4)), e4);
        }
    }
    
    public static PKCS10CertificateSigningRequest readCertificateSigningRequestFromFile(final File f) throws LDAPException {
        try (final BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(f))) {
            inputStream.mark(1);
            final int firstByte = inputStream.read();
            if (firstByte < 0) {
                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_EMPTY_FILE.get(f.getAbsolutePath()));
            }
            inputStream.reset();
            PKCS10CertificateSigningRequest csr = null;
            if ((firstByte & 0xFF) == 0x30) {
                while (true) {
                    ASN1Element csrElement;
                    try {
                        csrElement = ASN1Element.readFrom(inputStream);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_DER_NOT_VALID_ASN1.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
                    }
                    if (csrElement == null) {
                        if (csr == null) {
                            throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_EMPTY_FILE.get(f.getAbsolutePath()));
                        }
                        return csr;
                    }
                    else {
                        if (csr == null) {
                            try {
                                csr = new PKCS10CertificateSigningRequest(csrElement.encode());
                                continue;
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_DER_NOT_VALID_CSR.get(f.getAbsolutePath(), e.getMessage()), e);
                            }
                            break;
                        }
                        break;
                    }
                }
                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_MULTIPLE_CSRS.get(f.getAbsolutePath()));
            }
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                boolean inCSR = false;
                final StringBuilder buffer = new StringBuilder();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        if (inCSR) {
                            throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_EOF_WITHOUT_END.get(f.getAbsolutePath()));
                        }
                        if (csr == null) {
                            throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_EMPTY_FILE.get(f.getAbsolutePath()));
                        }
                        return csr;
                    }
                    else {
                        line = line.trim();
                        if (line.isEmpty()) {
                            continue;
                        }
                        if (line.startsWith("#")) {
                            continue;
                        }
                        if (line.equals("-----BEGIN CERTIFICATE REQUEST-----") || line.equals("-----BEGIN NEW CERTIFICATE REQUEST-----")) {
                            if (inCSR) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_MULTIPLE_BEGIN.get(f.getAbsolutePath()));
                            }
                            if (csr != null) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_MULTIPLE_CSRS.get(f.getAbsolutePath()));
                            }
                            inCSR = true;
                        }
                        else if (line.equals("-----END CERTIFICATE REQUEST-----") || line.equals("-----END NEW CERTIFICATE REQUEST-----")) {
                            if (!inCSR) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_END_WITHOUT_BEGIN.get(f.getAbsolutePath()));
                            }
                            inCSR = false;
                            byte[] csrBytes;
                            try {
                                csrBytes = Base64.decode(buffer.toString());
                            }
                            catch (final Exception e2) {
                                Debug.debugException(e2);
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_PEM_CSR_NOT_BASE64.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), e2);
                            }
                            try {
                                csr = new PKCS10CertificateSigningRequest(csrBytes);
                            }
                            catch (final CertException e3) {
                                Debug.debugException(e3);
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_PEM_CSR_NOT_CSR.get(f.getAbsolutePath(), e3.getMessage()), e3);
                            }
                            buffer.setLength(0);
                        }
                        else {
                            if (!inCSR) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_DATA_WITHOUT_BEGIN.get(f.getAbsolutePath()));
                            }
                            buffer.append(line);
                        }
                    }
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e4) {
            Debug.debugException(e4);
            throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_READ_CSR_FROM_FILE_READ_ERROR.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e4)), e4);
        }
    }
    
    private static String toColonDelimitedHex(final byte... bytes) {
        final StringBuilder buffer = new StringBuilder(bytes.length * 3);
        StaticUtils.toHex(bytes, ":", buffer);
        return buffer.toString();
    }
    
    private static String formatDateAndTime(final Date d) {
        final String dateFormatString = "EEEE, MMMM d, yyyy";
        final String formattedDate = new SimpleDateFormat("EEEE, MMMM d, yyyy").format(d);
        final String timeFormatString = "hh:mm:ss aa z";
        final String formattedTime = new SimpleDateFormat("hh:mm:ss aa z").format(d);
        final long providedTime = d.getTime();
        final long currentTime = System.currentTimeMillis();
        if (providedTime > currentTime) {
            final long secondsInFuture = (providedTime - currentTime) / 1000L;
            final String durationInFuture = StaticUtils.secondsToHumanReadableDuration(secondsInFuture);
            return CertMessages.INFO_MANAGE_CERTS_FORMAT_DATE_AND_TIME_IN_FUTURE.get(formattedDate, formattedTime, durationInFuture);
        }
        final long secondsInPast = (currentTime - providedTime) / 1000L;
        final String durationInPast = StaticUtils.secondsToHumanReadableDuration(secondsInPast);
        return CertMessages.INFO_MANAGE_CERTS_FORMAT_DATE_AND_TIME_IN_PAST.get(formattedDate, formattedTime, durationInPast);
    }
    
    private static String formatValidityStartTime(final Date d) {
        final String dateFormatString = "yyyy'/'MM'/'dd HH':'mm':'ss";
        return new SimpleDateFormat("yyyy'/'MM'/'dd HH':'mm':'ss").format(d);
    }
    
    private static X509Certificate[] getCertificateChain(final String alias, final KeyStore keystore, final AtomicReference<DN> missingIssuerRef) throws LDAPException {
        try {
            final Certificate[] chain = keystore.getCertificateChain(alias);
            if (chain != null && chain.length > 0) {
                final X509Certificate[] x509Chain = new X509Certificate[chain.length];
                for (int i = 0; i < chain.length; ++i) {
                    x509Chain[i] = new X509Certificate(chain[i].getEncoded());
                }
                return x509Chain;
            }
            final Certificate endCert = keystore.getCertificate(alias);
            if (endCert == null) {
                return new X509Certificate[0];
            }
            final ArrayList<X509Certificate> chainList = new ArrayList<X509Certificate>(5);
            X509Certificate certificate = new X509Certificate(endCert.getEncoded());
            chainList.add(certificate);
            final AtomicReference<KeyStore> jvmDefaultTrustStoreRef = new AtomicReference<KeyStore>();
            while (true) {
                final X509Certificate issuerCertificate = getIssuerCertificate(certificate, keystore, jvmDefaultTrustStoreRef, missingIssuerRef);
                if (issuerCertificate == null) {
                    break;
                }
                chainList.add(issuerCertificate);
                certificate = issuerCertificate;
            }
            final X509Certificate[] x509Chain2 = new X509Certificate[chainList.size()];
            return chainList.toArray(x509Chain2);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_GET_CHAIN_ERROR.get(alias, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static X509Certificate getIssuerCertificate(final X509Certificate certificate, final KeyStore keystore, final AtomicReference<KeyStore> jvmDefaultTrustStoreRef, final AtomicReference<DN> missingIssuerRef) throws Exception {
        final DN subjectDN = certificate.getSubjectDN();
        final DN issuerDN = certificate.getIssuerDN();
        if (subjectDN.equals(issuerDN)) {
            return null;
        }
        X509Certificate issuerCertificate = getIssuerCertificate(certificate, keystore);
        if (issuerCertificate != null) {
            return issuerCertificate;
        }
        KeyStore jvmDefaultTrustStore = jvmDefaultTrustStoreRef.get();
        if (jvmDefaultTrustStore == null) {
            if (ManageCertificates.JVM_DEFAULT_CACERTS_FILE == null) {
                missingIssuerRef.set(issuerDN);
                return null;
            }
            final String[] arr$ = { "JKS", "PKCS12" };
            final int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                final String keystoreType = arr$[i$];
                final KeyStore ks = KeyStore.getInstance(keystoreType);
                try (final FileInputStream inputStream = new FileInputStream(ManageCertificates.JVM_DEFAULT_CACERTS_FILE)) {
                    ks.load(inputStream, null);
                    jvmDefaultTrustStore = ks;
                    jvmDefaultTrustStoreRef.set(jvmDefaultTrustStore);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    ++i$;
                    continue;
                }
                break;
            }
        }
        if (jvmDefaultTrustStore != null) {
            issuerCertificate = getIssuerCertificate(certificate, jvmDefaultTrustStore);
        }
        if (issuerCertificate == null) {
            missingIssuerRef.set(issuerDN);
        }
        return issuerCertificate;
    }
    
    private static X509Certificate getIssuerCertificate(final X509Certificate certificate, final KeyStore keystore) throws Exception {
        final Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            Certificate[] certs = null;
            if (hasCertificateAlias(keystore, alias)) {
                final Certificate c = keystore.getCertificate(alias);
                if (c == null) {
                    continue;
                }
                certs = new Certificate[] { c };
            }
            else if (hasKeyAlias(keystore, alias)) {
                certs = keystore.getCertificateChain(alias);
            }
            if (certs != null) {
                for (final Certificate c2 : certs) {
                    final X509Certificate xc = new X509Certificate(c2.getEncoded());
                    if (xc.isIssuerFor(certificate)) {
                        return xc;
                    }
                }
            }
        }
        return null;
    }
    
    private static byte[] getAuthorityKeyIdentifier(final X509Certificate c) {
        for (final X509CertificateExtension extension : c.getExtensions()) {
            if (extension instanceof AuthorityKeyIdentifierExtension) {
                final AuthorityKeyIdentifierExtension e = (AuthorityKeyIdentifierExtension)extension;
                if (e.getKeyIdentifier() != null) {
                    return e.getKeyIdentifier().getValue();
                }
                continue;
            }
        }
        return null;
    }
    
    static void writeKeystore(final KeyStore keystore, final File keystorePath, final char[] keystorePassword) throws LDAPException {
        File copyOfExistingKeystore = null;
        final String timestamp = StaticUtils.encodeGeneralizedTime(System.currentTimeMillis());
        if (keystorePath.exists()) {
            copyOfExistingKeystore = new File(keystorePath.getAbsolutePath() + ".backup-" + timestamp);
            try {
                Files.copy(keystorePath.toPath(), copyOfExistingKeystore.toPath(), new CopyOption[0]);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_WRITE_KS_ERROR_COPYING_EXISTING_KS.get(keystorePath.getAbsolutePath(), copyOfExistingKeystore.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
            }
        }
        try (final FileOutputStream outputStream = new FileOutputStream(keystorePath)) {
            keystore.store(outputStream, keystorePassword);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (copyOfExistingKeystore == null) {
                throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_WRITE_KS_ERROR_WRITING_NEW_KS.get(keystorePath.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
            }
            throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_WRITE_KS_ERROR_OVERWRITING_KS.get(keystorePath.getAbsolutePath(), StaticUtils.getExceptionMessage(e), copyOfExistingKeystore.getAbsolutePath()), e);
        }
        if (copyOfExistingKeystore != null) {
            try {
                Files.delete(copyOfExistingKeystore.toPath());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, CertMessages.ERR_MANAGE_CERTS_WRITE_KS_ERROR_DELETING_KS_BACKUP.get(copyOfExistingKeystore.getAbsolutePath(), keystorePath.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
            }
        }
    }
    
    private static boolean hasCertificateAlias(final KeyStore keystore, final String alias) {
        try {
            return keystore.isCertificateEntry(alias);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return false;
        }
    }
    
    private static boolean hasKeyAlias(final KeyStore keystore, final String alias) {
        try {
            return keystore.isKeyEntry(alias);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return false;
        }
    }
    
    private static void addExtensionArguments(final List<String> keytoolArguments, final BasicConstraintsExtension basicConstraints, final KeyUsageExtension keyUsage, final ExtendedKeyUsageExtension extendedKeyUsage, final Set<String> sanValues, final Set<String> ianValues, final List<X509CertificateExtension> genericExtensions) {
        if (basicConstraints != null) {
            final StringBuilder basicConstraintsValue = new StringBuilder();
            basicConstraintsValue.append("ca:");
            basicConstraintsValue.append(basicConstraints.isCA());
            if (basicConstraints.getPathLengthConstraint() != null) {
                basicConstraintsValue.append(",pathlen:");
                basicConstraintsValue.append(basicConstraints.getPathLengthConstraint());
            }
            keytoolArguments.add("-ext");
            keytoolArguments.add("BasicConstraints=" + (Object)basicConstraintsValue);
        }
        if (keyUsage != null) {
            final StringBuilder keyUsageValue = new StringBuilder();
            if (keyUsage.isDigitalSignatureBitSet()) {
                commaAppend(keyUsageValue, "digitalSignature");
            }
            if (keyUsage.isNonRepudiationBitSet()) {
                commaAppend(keyUsageValue, "nonRepudiation");
            }
            if (keyUsage.isKeyEnciphermentBitSet()) {
                commaAppend(keyUsageValue, "keyEncipherment");
            }
            if (keyUsage.isDataEnciphermentBitSet()) {
                commaAppend(keyUsageValue, "dataEncipherment");
            }
            if (keyUsage.isKeyAgreementBitSet()) {
                commaAppend(keyUsageValue, "keyAgreement");
            }
            if (keyUsage.isKeyCertSignBitSet()) {
                commaAppend(keyUsageValue, "keyCertSign");
            }
            if (keyUsage.isCRLSignBitSet()) {
                commaAppend(keyUsageValue, "cRLSign");
            }
            if (keyUsage.isEncipherOnlyBitSet()) {
                commaAppend(keyUsageValue, "encipherOnly");
            }
            if (keyUsage.isEncipherOnlyBitSet()) {
                commaAppend(keyUsageValue, "decipherOnly");
            }
            keytoolArguments.add("-ext");
            keytoolArguments.add("KeyUsage=" + (Object)keyUsageValue);
        }
        if (extendedKeyUsage != null) {
            final StringBuilder extendedKeyUsageValue = new StringBuilder();
            for (final OID oid : extendedKeyUsage.getKeyPurposeIDs()) {
                final ExtendedKeyUsageID id = ExtendedKeyUsageID.forOID(oid);
                if (id == null) {
                    commaAppend(extendedKeyUsageValue, oid.toString());
                }
                else {
                    switch (id) {
                        case TLS_SERVER_AUTHENTICATION: {
                            commaAppend(extendedKeyUsageValue, "serverAuth");
                            continue;
                        }
                        case TLS_CLIENT_AUTHENTICATION: {
                            commaAppend(extendedKeyUsageValue, "clientAuth");
                            continue;
                        }
                        case CODE_SIGNING: {
                            commaAppend(extendedKeyUsageValue, "codeSigning");
                            continue;
                        }
                        case EMAIL_PROTECTION: {
                            commaAppend(extendedKeyUsageValue, "emailProtection");
                            continue;
                        }
                        case TIME_STAMPING: {
                            commaAppend(extendedKeyUsageValue, "timeStamping");
                            continue;
                        }
                        case OCSP_SIGNING: {
                            commaAppend(extendedKeyUsageValue, "OCSPSigning");
                            continue;
                        }
                        default: {
                            commaAppend(extendedKeyUsageValue, id.getOID().toString());
                            continue;
                        }
                    }
                }
            }
            keytoolArguments.add("-ext");
            keytoolArguments.add("ExtendedKeyUsage=" + (Object)extendedKeyUsageValue);
        }
        if (!sanValues.isEmpty()) {
            final StringBuilder subjectAltNameValue = new StringBuilder();
            for (final String sanValue : sanValues) {
                commaAppend(subjectAltNameValue, sanValue);
            }
            keytoolArguments.add("-ext");
            keytoolArguments.add("SAN=" + (Object)subjectAltNameValue);
        }
        if (!ianValues.isEmpty()) {
            final StringBuilder issuerAltNameValue = new StringBuilder();
            for (final String ianValue : ianValues) {
                commaAppend(issuerAltNameValue, ianValue);
            }
            keytoolArguments.add("-ext");
            keytoolArguments.add("IAN=" + (Object)issuerAltNameValue);
        }
        for (final X509CertificateExtension e : genericExtensions) {
            keytoolArguments.add("-ext");
            if (e.isCritical()) {
                keytoolArguments.add(e.getOID().toString() + ":critical=" + toColonDelimitedHex(e.getValue()));
            }
            else {
                keytoolArguments.add(e.getOID().toString() + '=' + toColonDelimitedHex(e.getValue()));
            }
        }
    }
    
    private static void commaAppend(final StringBuilder buffer, final String value) {
        if (buffer.length() > 0) {
            buffer.append(',');
        }
        buffer.append(value);
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final String keystorePath = getPlatformSpecificPath("config", "keystore");
        final String keystorePWPath = getPlatformSpecificPath("config", "keystore.pin");
        final String privateKeyPWPath = getPlatformSpecificPath("config", "server-cert-private-key.pin");
        final String exportCertOutputFile = getPlatformSpecificPath("server-cert.crt");
        final String exportKeyOutputFile = getPlatformSpecificPath("server-cert.private-key");
        final String genCSROutputFile = getPlatformSpecificPath("server-cert.csr");
        final String truststorePath = getPlatformSpecificPath("config", "truststore");
        final String truststorePWPath = getPlatformSpecificPath("config", "truststore.pin");
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(20));
        examples.put(new String[] { "list-certificates", "--keystore", keystorePath, "--keystore-password-file", keystorePWPath, "--verbose", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_LIST_1.get(keystorePath));
        examples.put(new String[] { "export-certificate", "--keystore", keystorePath, "--keystore-password-file", keystorePWPath, "--alias", "server-cert", "--output-file", exportCertOutputFile, "--output-format", "PEM", "--verbose", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_EXPORT_CERT_1.get(keystorePath, exportCertOutputFile));
        examples.put(new String[] { "export-private-key", "--keystore", keystorePath, "--keystore-password-file", keystorePWPath, "--private-key-password-file", privateKeyPWPath, "--alias", "server-cert", "--output-file", exportKeyOutputFile, "--output-format", "PEM", "--verbose", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_EXPORT_KEY_1.get(keystorePath, exportKeyOutputFile));
        examples.put(new String[] { "import-certificate", "--keystore", keystorePath, "--keystore-type", "JKS", "--keystore-password-file", keystorePWPath, "--alias", "server-cert", "--certificate-file", exportCertOutputFile, "--private-key-file", exportKeyOutputFile, "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_IMPORT_1.get(exportCertOutputFile, exportKeyOutputFile, keystorePath));
        examples.put(new String[] { "delete-certificate", "--keystore", keystorePath, "--keystore-password-file", keystorePWPath, "--alias", "server-cert" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_DELETE_1.get(keystorePath));
        examples.put(new String[] { "generate-self-signed-certificate", "--keystore", keystorePath, "--keystore-type", "PKCS12", "--keystore-password-file", keystorePWPath, "--alias", "ca-cert", "--subject-dn", "CN=Example Authority,O=Example Corporation,C=US", "--days-valid", "7300", "--validity-start-time", "20170101000000", "--key-algorithm", "RSA", "--key-size-bits", "4096", "--signature-algorithm", "SHA256withRSA", "--basic-constraints-is-ca", "true", "--key-usage", "key-cert-sign", "--key-usage", "crl-sign", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_GEN_CERT_1.get(keystorePath));
        examples.put(new String[] { "generate-certificate-signing-request", "--keystore", keystorePath, "--keystore-type", "PKCS12", "--keystore-password-file", keystorePWPath, "--output-file", genCSROutputFile, "--alias", "server-cert", "--subject-dn", "CN=ldap.example.com,O=Example Corporation,C=US", "--key-algorithm", "EC", "--key-size-bits", "256", "--signature-algorithm", "SHA256withECDSA", "--subject-alternative-name-dns", "ldap1.example.com", "--subject-alternative-name-dns", "ldap2.example.com", "--extended-key-usage", "server-auth", "--extended-key-usage", "client-auth", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_GEN_CSR_1.get(keystorePath, genCSROutputFile));
        examples.put(new String[] { "generate-certificate-signing-request", "--keystore", keystorePath, "--keystore-password-file", keystorePWPath, "--alias", "server-cert", "--use-existing-key-pair", "--inherit-extensions", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_GEN_CSR_2.get(keystorePath));
        examples.put(new String[] { "sign-certificate-signing-request", "--keystore", keystorePath, "--keystore-password-file", keystorePWPath, "--request-input-file", genCSROutputFile, "--certificate-output-file", exportCertOutputFile, "--alias", "ca-cert", "--days-valid", "730", "--include-requested-extensions", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_SIGN_CERT_1.get(keystorePath, genCSROutputFile, exportCertOutputFile));
        examples.put(new String[] { "change-certificate-alias", "--keystore", keystorePath, "--keystore-password-file", keystorePWPath, "--current-alias", "server-cert", "--new-alias", "server-certificate", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_CHANGE_ALIAS_1.get(keystorePath, genCSROutputFile, exportCertOutputFile));
        examples.put(new String[] { "change-keystore-password", "--keystore", getPlatformSpecificPath("config", "keystore"), "--current-keystore-password-file", getPlatformSpecificPath("config", "current.pin"), "--new-keystore-password-file", getPlatformSpecificPath("config", "new.pin"), "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_SC_CHANGE_KS_PW_EXAMPLE_1.get(getPlatformSpecificPath("config", "keystore"), getPlatformSpecificPath("config", "current.pin"), getPlatformSpecificPath("config", "new.pin")));
        examples.put(new String[] { "trust-server-certificate", "--hostname", "ldap.example.com", "--port", "636", "--keystore", truststorePath, "--keystore-password-file", truststorePWPath, "--alias", "ldap.example.com:636" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_TRUST_SERVER_1.get(truststorePath));
        examples.put(new String[] { "check-certificate-usability", "--keystore", keystorePath, "--keystore-password-file", keystorePWPath, "--alias", "server-cert" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_CHECK_USABILITY_1.get(keystorePath));
        examples.put(new String[] { "display-certificate-file", "--certificate-file", exportCertOutputFile, "--verbose", "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_DISPLAY_CERT_1.get(keystorePath));
        examples.put(new String[] { "display-certificate-signing-request-file", "--certificate-signing-request-file", genCSROutputFile, "--display-keytool-command" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_DISPLAY_CSR_1.get(keystorePath));
        examples.put(new String[] { "--help-subcommands" }, CertMessages.INFO_MANAGE_CERTS_EXAMPLE_HELP_SUBCOMMANDS_1.get(keystorePath));
        return examples;
    }
    
    static {
        File caCertsFile;
        try {
            caCertsFile = JVMDefaultTrustManager.getInstance().getCACertsFile();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            caCertsFile = null;
        }
        JVM_DEFAULT_CACERTS_FILE = caCertsFile;
        PROPERTY_DEFAULT_KEYSTORE_TYPE = ManageCertificates.class.getName() + ".defaultKeystoreType";
        final String propertyValue = StaticUtils.getSystemProperty(ManageCertificates.PROPERTY_DEFAULT_KEYSTORE_TYPE);
        if (propertyValue != null && (propertyValue.equalsIgnoreCase("PKCS12") || propertyValue.equalsIgnoreCase("PKCS#12") || propertyValue.equalsIgnoreCase("PKCS #12") || propertyValue.equalsIgnoreCase("PKCS 12"))) {
            DEFAULT_KEYSTORE_TYPE = "PKCS12";
        }
        else {
            DEFAULT_KEYSTORE_TYPE = "JKS";
        }
        WRAP_COLUMN = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
    }
}
