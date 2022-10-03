package com.unboundid.util;

import java.util.Arrays;
import java.util.Date;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.args.TimestampArgument;
import com.unboundid.util.args.ScopeArgument;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DurationArgument;
import com.unboundid.util.args.ControlArgument;
import com.unboundid.util.args.BooleanValueArgument;
import com.unboundid.util.args.ArgumentListArgument;
import java.util.Set;
import com.unboundid.util.args.ArgumentException;
import java.util.Collections;
import com.unboundid.ldap.sdk.ExtendedResult;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.IntegerArgument;
import java.io.File;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import javax.net.SocketFactory;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustStoreTrustManager;
import com.unboundid.util.ssl.AggregateTrustManager;
import com.unboundid.util.ssl.PromptTrustManager;
import com.unboundid.util.ssl.JVMDefaultTrustManager;
import javax.net.ssl.X509TrustManager;
import com.unboundid.util.ssl.TrustAllTrustManager;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ssl.KeyStoreKeyManager;
import com.unboundid.ldap.sdk.PLAINBindRequest;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.CRAMMD5BindRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.EXTERNALBindRequest;
import com.unboundid.util.args.Argument;
import java.util.Iterator;
import java.util.TreeMap;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.args.SubCommand;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.args.ArgumentHelper;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import com.unboundid.util.args.ArgumentParser;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class CommandLineToolInteractiveModeProcessor
{
    private static volatile boolean IN_UNIT_TEST;
    private final ArgumentParser parser;
    private final BufferedReader systemInReader;
    private final CommandLineTool tool;
    private final int wrapColumn;
    
    CommandLineToolInteractiveModeProcessor(final CommandLineTool tool, final ArgumentParser parser) {
        this.tool = tool;
        this.parser = parser;
        this.systemInReader = new BufferedReader(new InputStreamReader(System.in));
        this.wrapColumn = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
        ArgumentHelper.reset(parser.getNamedArgument("interactive"));
    }
    
    void doInteractiveModeProcessing() throws LDAPException {
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_LAUNCHING.get(this.tool.getToolName()));
        final List<String> allArgs = new ArrayList<String>(10);
        final List<SubCommand> subCommands = this.parser.getSubCommands();
        if (!subCommands.isEmpty()) {
            final SubCommand subcommand = this.promptForSubCommand();
            ArgumentHelper.setSelectedSubCommand(this.parser, subcommand);
            allArgs.add(subcommand.getPrimaryName());
        }
        final List<String> ldapArgs = new ArrayList<String>(10);
        if (this.tool instanceof LDAPCommandLineTool) {
            this.promptForLDAPArguments(ldapArgs, true);
        }
        else if (this.tool instanceof MultiServerLDAPCommandLineTool) {
            this.promptForMultiServerLDAPArguments(ldapArgs, true);
        }
        allArgs.addAll(ldapArgs);
        final List<String> toolArgs = this.displayInteractiveMenu(ldapArgs);
        allArgs.addAll(toolArgs);
        this.tool.out(new Object[0]);
        if (allArgs.isEmpty()) {
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_RUNNING_WITH_NO_ARGS.get(this.tool.getToolName()));
        }
        else {
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_RUNNING_WITH_ARGS.get());
            this.printArgs(allArgs);
        }
        this.tool.out(new Object[0]);
    }
    
    private void printArgs(final List<String> args) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("     ");
        buffer.append(this.tool.getToolName());
        if (!args.isEmpty()) {
            buffer.append(" \\");
        }
        this.tool.out(buffer);
        for (int i = 0; i < args.size(); ++i) {
            buffer.setLength(0);
            buffer.append("          ");
            final String arg = args.get(i);
            buffer.append(ExampleCommandLineArgument.getCleanArgument(arg).getLocalForm());
            if (arg.startsWith("-") && i + 1 < args.size()) {
                final String nextArg = args.get(i + 1);
                if (!nextArg.startsWith("-")) {
                    buffer.append(' ');
                    buffer.append(ExampleCommandLineArgument.getCleanArgument(nextArg).getLocalForm());
                    ++i;
                }
            }
            if (i < args.size() - 1) {
                buffer.append(" \\");
            }
            this.tool.out(buffer);
        }
    }
    
    private SubCommand promptForSubCommand() throws LDAPException {
        final List<SubCommand> subCommands = this.parser.getSubCommands();
        final TreeMap<String, SubCommand> subCommandsByName = new TreeMap<String, SubCommand>();
        for (final SubCommand sc : subCommands) {
            subCommandsByName.put(sc.getPrimaryName(), sc);
        }
        int index = 0;
        final String[] subCommandNames = new String[subCommandsByName.size()];
        for (final SubCommand sc2 : subCommandsByName.values()) {
            subCommandNames[index++] = sc2.getPrimaryName();
        }
        final int selectedSubCommandNumber = this.getNumberedMenuChoice(UtilityMessages.INFO_INTERACTIVE_SUBCOMMAND_PROMPT.get(), false, null, subCommandNames);
        return this.parser.getSubCommand(subCommandNames[selectedSubCommandNumber]);
    }
    
    private void promptForLDAPArguments(final List<String> argList, final boolean test) throws LDAPException {
        final LDAPCommandLineTool ldapTool = (LDAPCommandLineTool)this.tool;
        argList.clear();
        final BooleanArgument useSSLArgument = this.parser.getBooleanArgument("useSSL");
        final BooleanArgument useStartTLSArgument = this.parser.getBooleanArgument("useStartTLS");
        String defaultSecurityChoice;
        if (useSSLArgument.isPresent()) {
            defaultSecurityChoice = "2";
        }
        else if (useStartTLSArgument.isPresent()) {
            defaultSecurityChoice = "3";
        }
        else {
            defaultSecurityChoice = "1";
        }
        ArgumentHelper.reset(useSSLArgument);
        ArgumentHelper.reset(useStartTLSArgument);
        final int securityType = this.getNumberedMenuChoice(UtilityMessages.INFO_INTERACTIVE_LDAP_SECURITY_PROMPT.get(), false, defaultSecurityChoice, UtilityMessages.INFO_INTERACTIVE_LDAP_SECURITY_OPTION_NONE.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_SECURITY_OPTION_SSL.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_SECURITY_OPTION_START_TLS.get());
        boolean useSSL = false;
        boolean useStartTLS = false;
        switch (securityType) {
            case 1: {
                useSSL = true;
                useStartTLS = false;
                argList.add("--useSSL");
                ArgumentHelper.incrementOccurrencesSuppressException(useSSLArgument);
                break;
            }
            case 2: {
                useSSL = false;
                useStartTLS = true;
                argList.add("--useStartTLS");
                ArgumentHelper.incrementOccurrencesSuppressException(useStartTLSArgument);
                break;
            }
            default: {
                useSSL = false;
                useStartTLS = false;
                break;
            }
        }
        final StringArgument keyStorePasswordArgument = this.parser.getStringArgument("keyStorePassword");
        final StringArgument trustStorePasswordArgument = this.parser.getStringArgument("trustStorePassword");
        final StringArgument saslOptionArgument = this.parser.getStringArgument("saslOption");
        ArgumentHelper.reset(keyStorePasswordArgument);
        ArgumentHelper.reset(trustStorePasswordArgument);
        ArgumentHelper.reset(this.parser.getNamedArgument("keyStorePasswordFile"));
        ArgumentHelper.reset(this.parser.getNamedArgument("promptForKeyStorePassword"));
        ArgumentHelper.reset(this.parser.getNamedArgument("trustStorePasswordFile"));
        ArgumentHelper.reset(this.parser.getNamedArgument("promptForTrustStorePassword"));
        BindRequest bindRequest = null;
        boolean trustAll = false;
        byte[] keyStorePIN = null;
        byte[] trustStorePIN = null;
        File keyStorePath = null;
        File trustStorePath = null;
        String certificateNickname = null;
        String keyStoreFormat = null;
        String trustStoreFormat = null;
        if (useSSL || useStartTLS) {
            final StringArgument keyStorePathArgument = this.parser.getStringArgument("keyStorePath");
            final StringArgument keyStoreFormatArgument = this.parser.getStringArgument("keyStoreFormat");
            String defaultStoreTypeChoice;
            if (keyStoreFormatArgument.isPresent()) {
                final String format = keyStoreFormatArgument.getValue();
                if (format.equalsIgnoreCase("PKCS12")) {
                    defaultStoreTypeChoice = "3";
                }
                else {
                    defaultStoreTypeChoice = "2";
                }
            }
            else if (keyStorePathArgument.isPresent()) {
                defaultStoreTypeChoice = "2";
            }
            else {
                defaultStoreTypeChoice = "1";
            }
            String defaultKeyStorePath;
            if (keyStorePathArgument.isPresent()) {
                defaultKeyStorePath = keyStorePathArgument.getValue();
            }
            else {
                defaultKeyStorePath = null;
            }
            final StringArgument certNicknameArgument = this.parser.getStringArgument("certNickname");
            String defaultCertNickname;
            if (certNicknameArgument.isPresent()) {
                defaultCertNickname = certNicknameArgument.getValue();
            }
            else {
                defaultCertNickname = null;
            }
            ArgumentHelper.reset(keyStorePathArgument);
            ArgumentHelper.reset(keyStoreFormatArgument);
            ArgumentHelper.reset(certNicknameArgument);
            final int keystoreType = this.getNumberedMenuChoice(UtilityMessages.INFO_INTERACTIVE_LDAP_CLIENT_CERT_PROMPT.get(), false, defaultStoreTypeChoice, UtilityMessages.INFO_INTERACTIVE_LDAP_CLIENT_CERT_OPTION_NO.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_CLIENT_CERT_OPTION_JKS.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_CLIENT_CERT_OPTION_PKCS12.get());
            ArgumentHelper.reset(keyStoreFormatArgument);
            switch (keystoreType) {
                case 1: {
                    keyStoreFormat = "JKS";
                    break;
                }
                case 2: {
                    keyStoreFormat = "PKCS12";
                    break;
                }
            }
            if (keyStoreFormat != null) {
                ArgumentHelper.addValueSuppressException(keyStoreFormatArgument, keyStoreFormat);
                keyStorePath = this.promptForPath(UtilityMessages.INFO_INTERACTIVE_LDAP_KEYSTORE_PATH_PROMPT.get(), defaultKeyStorePath, true, true, true, true, false);
                argList.add("--keyStorePath");
                argList.add(keyStorePath.getAbsolutePath());
                ArgumentHelper.addValueSuppressException(keyStorePathArgument, keyStorePath.getAbsolutePath());
                keyStorePIN = this.promptForPassword(UtilityMessages.INFO_INTERACTIVE_LDAP_KEYSTORE_PIN_PROMPT.get(), UtilityMessages.INFO_INTERACTIVE_PIN_CONFIRM_PROMPT.get(), false);
                if (keyStorePIN != null) {
                    argList.add("--keyStorePassword");
                    argList.add("***REDACTED***");
                    ArgumentHelper.addValueSuppressException(keyStorePasswordArgument, StaticUtils.toUTF8String(keyStorePIN));
                }
                argList.add("--keyStoreFormat");
                argList.add(keyStoreFormat);
                certificateNickname = this.promptForString(UtilityMessages.INFO_INTERACTIVE_LDAP_CERT_NICKNAME_PROMPT.get(), defaultCertNickname, false);
                if (certificateNickname != null) {
                    argList.add("--certNickname");
                    argList.add(certificateNickname);
                    ArgumentHelper.addValueSuppressException(certNicknameArgument, certificateNickname);
                }
                if (ldapTool.supportsAuthentication() && this.promptForBoolean(UtilityMessages.INFO_INTERACTIVE_LDAP_CERT_AUTH_PROMPT.get(), false, true)) {
                    bindRequest = new EXTERNALBindRequest();
                    argList.add("--saslOption");
                    argList.add("mech=EXTERNAL");
                    ArgumentHelper.reset(saslOptionArgument);
                    ArgumentHelper.addValueSuppressException(saslOptionArgument, "mech=EXTERNAL");
                }
            }
            final BooleanArgument trustAllArgument = this.parser.getBooleanArgument("trustAll");
            final StringArgument trustStorePathArgument = this.parser.getStringArgument("trustStorePath");
            final StringArgument trustStoreFormatArgument = this.parser.getStringArgument("trustStoreFormat");
            String defaultTrustTypeChoice;
            if (trustAllArgument.isPresent()) {
                defaultTrustTypeChoice = "4";
            }
            else if (trustStoreFormatArgument.isPresent()) {
                final String format2 = trustStoreFormatArgument.getValue();
                if (format2.equalsIgnoreCase("PKCS12")) {
                    defaultTrustTypeChoice = "3";
                }
                else {
                    defaultTrustTypeChoice = "2";
                }
            }
            else if (trustStorePathArgument.isPresent()) {
                defaultTrustTypeChoice = "2";
            }
            else {
                defaultTrustTypeChoice = "1";
            }
            String defaultTrustStorePath;
            if (trustStorePathArgument.isPresent()) {
                defaultTrustStorePath = trustStorePathArgument.getValue();
            }
            else {
                defaultTrustStorePath = null;
            }
            ArgumentHelper.reset(trustAllArgument);
            ArgumentHelper.reset(trustStorePathArgument);
            ArgumentHelper.reset(trustStoreFormatArgument);
            final int trustType = this.getNumberedMenuChoice(UtilityMessages.INFO_INTERACTIVE_LDAP_TRUST_PROMPT.get(), false, defaultTrustTypeChoice, UtilityMessages.INFO_INTERACTIVE_LDAP_TRUST_OPTION_PROMPT.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_TRUST_OPTION_JKS.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_TRUST_OPTION_PKCS12.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_TRUST_OPTION_BLIND.get());
            switch (trustType) {
                case 1: {
                    trustStoreFormat = "JKS";
                    break;
                }
                case 2: {
                    trustStoreFormat = "PKCS12";
                    break;
                }
                case 3: {
                    trustAll = true;
                    argList.add("--trustAll");
                    ArgumentHelper.incrementOccurrencesSuppressException(trustAllArgument);
                    break;
                }
                default: {
                    ArgumentHelper.incrementOccurrencesSuppressException(trustAllArgument);
                    break;
                }
            }
            if (trustStoreFormat != null) {
                ArgumentHelper.addValueSuppressException(trustStoreFormatArgument, trustStoreFormat);
                trustStorePath = this.promptForPath(UtilityMessages.INFO_INTERACTIVE_LDAP_TRUSTSTORE_PATH_PROMPT.get(), defaultTrustStorePath, true, true, true, true, false);
                argList.add("--trustStorePath");
                argList.add(trustStorePath.getAbsolutePath());
                ArgumentHelper.addValueSuppressException(trustStorePathArgument, trustStorePath.getAbsolutePath());
                trustStorePIN = this.promptForPassword(UtilityMessages.INFO_INTERACTIVE_LDAP_TRUSTSTORE_PIN_PROMPT.get(), UtilityMessages.INFO_INTERACTIVE_PIN_CONFIRM_PROMPT.get(), false);
                if (trustStorePIN != null) {
                    argList.add("--trustStorePassword");
                    argList.add("***REDACTED***");
                    ArgumentHelper.addValueSuppressException(trustStorePasswordArgument, StaticUtils.toUTF8String(trustStorePIN));
                }
                argList.add("--trustStoreFormat");
                argList.add(trustStoreFormat);
            }
        }
        else {
            ArgumentHelper.reset(this.parser.getNamedArgument("keyStorePath"));
            ArgumentHelper.reset(this.parser.getNamedArgument("keyStoreFormat"));
            ArgumentHelper.reset(this.parser.getNamedArgument("trustStorePath"));
            ArgumentHelper.reset(this.parser.getNamedArgument("trustStoreFormat"));
            ArgumentHelper.reset(this.parser.getNamedArgument("certNickname"));
        }
        final StringArgument hostnameArgument = this.parser.getStringArgument("hostname");
        String defaultHostname;
        if (hostnameArgument.isPresent()) {
            defaultHostname = hostnameArgument.getValue();
        }
        else {
            defaultHostname = "localhost";
        }
        final IntegerArgument portArgument = this.parser.getIntegerArgument("port");
        int defaultPort;
        if (portArgument.getNumOccurrences() > 0) {
            defaultPort = portArgument.getValue();
        }
        else if (useSSL) {
            defaultPort = 636;
        }
        else {
            defaultPort = 389;
        }
        ArgumentHelper.reset(hostnameArgument);
        ArgumentHelper.reset(portArgument);
        final String hostname = this.promptForString(UtilityMessages.INFO_INTERACTIVE_LDAP_PROMPT_HOST.get(), defaultHostname, true);
        ArgumentHelper.addValueSuppressException(hostnameArgument, hostname);
        final int port = this.promptForInteger(UtilityMessages.INFO_INTERACTIVE_LDAP_PROMPT_PORT.get(), defaultPort, 1, 65535, true);
        argList.add("--hostname");
        argList.add(hostname);
        argList.add("--port");
        argList.add(String.valueOf(port));
        ArgumentHelper.addValueSuppressException(portArgument, String.valueOf(port));
        if (ldapTool.supportsAuthentication()) {
            final DNArgument bindDNArgument = this.parser.getDNArgument("bindDN");
            final StringArgument bindPasswordArgument = this.parser.getStringArgument("bindPassword");
            ArgumentHelper.reset(bindPasswordArgument);
            ArgumentHelper.reset(this.parser.getNamedArgument("bindPasswordFile"));
            ArgumentHelper.reset(this.parser.getNamedArgument("promptForBindPassword"));
            if (bindRequest == null) {
                String defaultAuthTypeChoice;
                String defaultBindDN;
                if (bindDNArgument.isPresent()) {
                    defaultAuthTypeChoice = "2";
                    defaultBindDN = bindDNArgument.getValue().toString();
                }
                else if (saslOptionArgument.isPresent()) {
                    defaultAuthTypeChoice = "3";
                    defaultBindDN = null;
                }
                else {
                    defaultAuthTypeChoice = "1";
                    defaultBindDN = null;
                }
                ArgumentHelper.reset(bindDNArgument);
                boolean useSimpleAuth = false;
                boolean useSASLAuth = false;
                final int authMethod = this.getNumberedMenuChoice(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_PROMPT.get(), false, defaultAuthTypeChoice, UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_OPTION_NONE.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_OPTION_SIMPLE.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_OPTION_SASL.get());
                switch (authMethod) {
                    case 1: {
                        useSimpleAuth = true;
                        break;
                    }
                    case 2: {
                        useSASLAuth = true;
                        break;
                    }
                }
                if (useSimpleAuth) {
                    ArgumentHelper.reset(saslOptionArgument);
                    final DN bindDN = this.promptForDN(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_BIND_DN_PROMPT.get(), defaultBindDN, true);
                    if (bindDN.isNullDN()) {
                        bindRequest = new SimpleBindRequest();
                        argList.add("--bindDN");
                        argList.add("");
                        argList.add("--bindPassword");
                        argList.add("");
                        ArgumentHelper.addValueSuppressException(bindDNArgument, "");
                        ArgumentHelper.addValueSuppressException(bindPasswordArgument, "");
                    }
                    else {
                        final byte[] bindPassword = this.promptForPassword(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_PW_PROMPT.get(), UtilityMessages.INFO_INTERACTIVE_PW_CONFIRM_PROMPT.get(), true);
                        bindRequest = new SimpleBindRequest(bindDN, bindPassword);
                        argList.add("--bindDN");
                        argList.add(bindDN.toString());
                        argList.add("--bindPassword");
                        argList.add("***REDACTED***");
                        ArgumentHelper.addValueSuppressException(bindDNArgument, bindDN.toString());
                        ArgumentHelper.addValueSuppressException(bindPasswordArgument, StaticUtils.toUTF8String(bindPassword));
                    }
                }
                else if (useSASLAuth) {
                    String defaultMechChoice = null;
                    String defaultAuthID = null;
                    String defaultAuthzID = null;
                    String defaultRealm = null;
                    if (saslOptionArgument.isPresent()) {
                        for (final String saslOption : saslOptionArgument.getValues()) {
                            final String lowerOption = StaticUtils.toLowerCase(saslOption);
                            if (lowerOption.equals("mech=cram-md5")) {
                                defaultMechChoice = "1";
                            }
                            else if (lowerOption.equals("mech=digest-md5")) {
                                defaultMechChoice = "2";
                            }
                            else if (lowerOption.equals("mech=plain")) {
                                defaultMechChoice = "3";
                            }
                            else if (lowerOption.startsWith("authid=")) {
                                defaultAuthID = saslOption.substring(7);
                            }
                            else if (lowerOption.startsWith("authzid=")) {
                                defaultAuthzID = saslOption.substring(8);
                            }
                            else {
                                if (!lowerOption.startsWith("realm=")) {
                                    continue;
                                }
                                defaultRealm = saslOption.substring(6);
                            }
                        }
                    }
                    ArgumentHelper.reset(saslOptionArgument);
                    final int mech = this.getNumberedMenuChoice(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_SASL_PROMPT.get(), false, defaultMechChoice, UtilityMessages.INFO_INTERACTIVE_LDAP_SASL_OPTION_CRAM_MD5.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_SASL_OPTION_DIGEST_MD5.get(), UtilityMessages.INFO_INTERACTIVE_LDAP_SASL_OPTION_PLAIN.get());
                    switch (mech) {
                        case 0: {
                            final String authID = this.promptForString(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_AUTHID_PROMPT.get(), defaultAuthID, true);
                            final byte[] pw = this.promptForPassword(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_PW_PROMPT.get(), UtilityMessages.INFO_INTERACTIVE_PW_CONFIRM_PROMPT.get(), true);
                            bindRequest = new CRAMMD5BindRequest(authID, pw);
                            argList.add("--saslOption");
                            argList.add("mech=CRAM-MD5");
                            argList.add("--saslOption");
                            argList.add("authID=" + authID);
                            argList.add("--bindPassword");
                            argList.add("***REDACTED***");
                            ArgumentHelper.addValueSuppressException(saslOptionArgument, "mech=CRAM-MD5");
                            ArgumentHelper.addValueSuppressException(saslOptionArgument, "authID=" + authID);
                            ArgumentHelper.addValueSuppressException(bindPasswordArgument, StaticUtils.toUTF8String(pw));
                            break;
                        }
                        case 1: {
                            final String authID = this.promptForString(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_AUTHID_PROMPT.get(), defaultAuthID, true);
                            final String authzID = this.promptForString(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_AUTHZID_PROMPT.get(), defaultAuthzID, false);
                            final String realm = this.promptForString(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_REALM_PROMPT.get(), defaultRealm, false);
                            final byte[] pw = this.promptForPassword(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_PW_PROMPT.get(), UtilityMessages.INFO_INTERACTIVE_PW_CONFIRM_PROMPT.get(), true);
                            bindRequest = new DIGESTMD5BindRequest(authID, authzID, pw, realm, new Control[0]);
                            argList.add("--saslOption");
                            argList.add("mech=DIGEST-MD5");
                            argList.add("--saslOption");
                            argList.add("authID=" + authID);
                            ArgumentHelper.addValueSuppressException(saslOptionArgument, "mech=DIGEST-MD5");
                            ArgumentHelper.addValueSuppressException(saslOptionArgument, "authID=" + authID);
                            if (authzID != null) {
                                argList.add("--saslOption");
                                argList.add("authzID=" + authzID);
                                ArgumentHelper.addValueSuppressException(saslOptionArgument, "authzID=" + authzID);
                            }
                            if (realm != null) {
                                argList.add("--saslOption");
                                argList.add("realm=" + realm);
                                ArgumentHelper.addValueSuppressException(saslOptionArgument, "realm=" + realm);
                            }
                            argList.add("--bindPassword");
                            argList.add("***REDACTED***");
                            ArgumentHelper.addValueSuppressException(bindPasswordArgument, StaticUtils.toUTF8String(pw));
                            break;
                        }
                        case 2: {
                            final String authID = this.promptForString(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_AUTHID_PROMPT.get(), defaultAuthID, true);
                            final String authzID = this.promptForString(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_AUTHZID_PROMPT.get(), defaultAuthzID, false);
                            final byte[] pw = this.promptForPassword(UtilityMessages.INFO_INTERACTIVE_LDAP_AUTH_PW_PROMPT.get(), UtilityMessages.INFO_INTERACTIVE_PW_CONFIRM_PROMPT.get(), true);
                            bindRequest = new PLAINBindRequest(authID, authzID, pw);
                            argList.add("--saslOption");
                            argList.add("mech=PLAIN");
                            argList.add("--saslOption");
                            argList.add("authID=" + authID);
                            ArgumentHelper.addValueSuppressException(saslOptionArgument, "mech=PLAIN");
                            ArgumentHelper.addValueSuppressException(saslOptionArgument, "authID=" + authID);
                            if (authzID != null) {
                                argList.add("--saslOption");
                                argList.add("authzID=" + authzID);
                                ArgumentHelper.addValueSuppressException(saslOptionArgument, "authzID=" + authzID);
                            }
                            argList.add("--bindPassword");
                            argList.add("***REDACTED***");
                            ArgumentHelper.addValueSuppressException(bindPasswordArgument, StaticUtils.toUTF8String(pw));
                            break;
                        }
                    }
                }
            }
            else {
                ArgumentHelper.reset(bindDNArgument);
            }
        }
        if (test) {
            SSLUtil sslUtil;
            if (useSSL || useStartTLS) {
                KeyManager keyManager;
                if (keyStorePath == null) {
                    keyManager = null;
                }
                else {
                    char[] pinChars;
                    if (keyStorePIN == null) {
                        pinChars = null;
                    }
                    else {
                        final String pinString = StaticUtils.toUTF8String(keyStorePIN);
                        pinChars = pinString.toCharArray();
                    }
                    try {
                        keyManager = new KeyStoreKeyManager(keyStorePath, pinChars, keyStoreFormat, certificateNickname);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_LDAP_CANNOT_CREATE_KEY_MANAGER.get(StaticUtils.getExceptionMessage(e)));
                        if (this.promptForBoolean(UtilityMessages.INFO_INTERACTIVE_LDAP_RETRY_PROMPT.get(), true, true)) {
                            this.promptForLDAPArguments(argList, test);
                            return;
                        }
                        throw new LDAPException(ResultCode.LOCAL_ERROR, "", e);
                    }
                }
                TrustManager trustManager;
                if (trustAll) {
                    trustManager = new TrustAllTrustManager();
                }
                else if (trustStorePath == null) {
                    trustManager = new AggregateTrustManager(false, new X509TrustManager[] { JVMDefaultTrustManager.getInstance(), new PromptTrustManager() });
                }
                else {
                    char[] pinChars2;
                    if (trustStorePIN == null) {
                        pinChars2 = null;
                    }
                    else {
                        final String pinString2 = StaticUtils.toUTF8String(trustStorePIN);
                        pinChars2 = pinString2.toCharArray();
                    }
                    try {
                        trustManager = new TrustStoreTrustManager(trustStorePath, pinChars2, trustStoreFormat, true);
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_LDAP_CANNOT_CREATE_TRUST_MANAGER.get(StaticUtils.getExceptionMessage(e2)));
                        if (this.promptForBoolean(UtilityMessages.INFO_INTERACTIVE_LDAP_RETRY_PROMPT.get(), true, true)) {
                            this.promptForLDAPArguments(argList, test);
                            return;
                        }
                        throw new LDAPException(ResultCode.LOCAL_ERROR, "", e2);
                    }
                }
                sslUtil = new SSLUtil(keyManager, trustManager);
            }
            else {
                sslUtil = null;
            }
            Label_3379: {
                if (useSSL) {
                    try {
                        final LDAPConnection conn = new LDAPConnection(sslUtil.createSSLSocketFactory());
                        break Label_3379;
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                        this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_LDAP_CANNOT_CREATE_SOCKET_FACTORY.get(StaticUtils.getExceptionMessage(e3)), e3);
                        if (this.promptForBoolean(UtilityMessages.INFO_INTERACTIVE_LDAP_RETRY_PROMPT.get(), true, true)) {
                            this.promptForLDAPArguments(argList, test);
                            return;
                        }
                        throw new LDAPException(ResultCode.LOCAL_ERROR, "", e3);
                    }
                }
                final LDAPConnection conn = new LDAPConnection();
                try {
                    try {
                        conn.connect(hostname, port);
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_LDAP_CANNOT_CONNECT.get(hostname, port, le.getResultString()));
                        if (this.promptForBoolean(UtilityMessages.INFO_INTERACTIVE_LDAP_RETRY_PROMPT.get(), true, true)) {
                            this.promptForLDAPArguments(argList, test);
                            return;
                        }
                        throw new LDAPException(le.getResultCode(), "", le);
                    }
                    if (useStartTLS) {
                        try {
                            final ExtendedResult startTLSResult = conn.processExtendedOperation(new StartTLSExtendedRequest(sslUtil.createSSLContext()));
                            if (startTLSResult.getResultCode() != ResultCode.SUCCESS) {
                                throw new LDAPException(startTLSResult);
                            }
                        }
                        catch (final Exception e3) {
                            Debug.debugException(e3);
                            String msg;
                            if (e3 instanceof LDAPException) {
                                msg = ((LDAPException)e3).getResultString();
                            }
                            else {
                                msg = StaticUtils.getExceptionMessage(e3);
                            }
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_LDAP_CANNOT_PERFORM_STARTTLS.get(msg));
                            if (this.promptForBoolean(UtilityMessages.INFO_INTERACTIVE_LDAP_RETRY_PROMPT.get(), true, true)) {
                                this.promptForLDAPArguments(argList, test);
                                return;
                            }
                            throw new LDAPException(ResultCode.LOCAL_ERROR, "", e3);
                        }
                    }
                    if (bindRequest != null) {
                        try {
                            conn.bind(bindRequest);
                        }
                        catch (final LDAPException le) {
                            Debug.debugException(le);
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_LDAP_CANNOT_AUTHENTICATE.get(le.getResultString()));
                            if (this.promptForBoolean(UtilityMessages.INFO_INTERACTIVE_LDAP_RETRY_PROMPT.get(), true, true)) {
                                this.promptForLDAPArguments(argList, test);
                                return;
                            }
                            throw new LDAPException(le.getResultCode(), "", le);
                        }
                    }
                }
                finally {
                    conn.close();
                }
            }
        }
    }
    
    private void promptForMultiServerLDAPArguments(final List<String> argList, final boolean test) throws LDAPException {
        throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_MULTI_SERVER_LDAP_NOT_SUPPORTED.get());
    }
    
    private List<String> displayInteractiveMenu(final List<String> ldapArgs) throws LDAPException {
        final ArrayList<Argument> args = new ArrayList<Argument>(this.parser.getNamedArguments());
        if (this.parser.getSelectedSubCommand() != null) {
            args.addAll(this.parser.getSelectedSubCommand().getArgumentParser().getNamedArguments());
        }
        final Set<String> usageArguments = CommandLineTool.getUsageArgumentIdentifiers(this.tool);
        Set<String> ldapArguments;
        if (this.tool instanceof LDAPCommandLineTool) {
            ldapArguments = LDAPCommandLineTool.getLongLDAPArgumentIdentifiers((LDAPCommandLineTool)this.tool);
        }
        else {
            ldapArguments = Collections.emptySet();
        }
        int maxIdentifierLength = 0;
        final String trailingArgsIdentifier = UtilityMessages.INFO_INTERACTIVE_MENU_TRAILING_ARGS_IDENTIFIER.get();
        if (this.parser.allowsTrailingArguments()) {
            maxIdentifierLength = trailingArgsIdentifier.length();
        }
        final Iterator<Argument> argIterator = args.iterator();
        while (argIterator.hasNext()) {
            final Argument a = argIterator.next();
            final String longID = a.getLongIdentifier();
            if (usageArguments.contains(longID) || ldapArguments.contains(longID)) {
                argIterator.remove();
            }
            else {
                maxIdentifierLength = Math.max(maxIdentifierLength, a.getIdentifierString().length());
            }
        }
        if (args.isEmpty() && !this.parser.allowsTrailingArguments()) {
            return Collections.emptyList();
        }
        for (final Argument arg : args) {
            if (!arg.isRequired()) {
                continue;
            }
            final List<String> valueStrings = arg.getValueStringRepresentations(true);
            if (!valueStrings.isEmpty()) {
                continue;
            }
            this.promptForArgument(arg);
        }
        if (this.parser.requiresTrailingArguments()) {
            this.promptForTrailingArguments();
        }
    Label_2329:
        while (true) {
            final int maxNumberLength = String.valueOf(args.size()).length();
            final int subsequentIndent = maxNumberLength + maxIdentifierLength + 4;
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_MENU_PROMPT.get());
            int optionNumber = 1;
            for (final Argument arg2 : args) {
                List<String> valueStrings2 = arg2.getValueStringRepresentations(true);
                if (arg2.isSensitive()) {
                    final int size = valueStrings2.size();
                    switch (size) {
                        case 0: {
                            break;
                        }
                        case 1: {
                            valueStrings2 = Collections.singletonList("***REDACTED***");
                            break;
                        }
                        default: {
                            valueStrings2 = new ArrayList<String>(size);
                            for (int i = 0; i <= size; ++i) {
                                valueStrings2.add("***REDACTED" + i + "***");
                            }
                            break;
                        }
                    }
                }
                switch (valueStrings2.size()) {
                    case 0: {
                        this.tool.wrapStandardOut(0, subsequentIndent, this.wrapColumn, true, rightAlign(String.valueOf(optionNumber), maxNumberLength), ' ', leftAlign(arg2.getIdentifierString(), maxIdentifierLength), " -");
                        break;
                    }
                    case 1: {
                        this.tool.wrapStandardOut(0, subsequentIndent, this.wrapColumn, true, rightAlign(String.valueOf(optionNumber), maxNumberLength), ' ', leftAlign(arg2.getIdentifierString(), maxIdentifierLength), " - ", valueStrings2.get(0));
                        break;
                    }
                    default: {
                        this.tool.wrapStandardOut(0, subsequentIndent, this.wrapColumn, true, rightAlign(String.valueOf(optionNumber), maxNumberLength), ' ', leftAlign(arg2.getIdentifierString(), maxIdentifierLength), " - ", valueStrings2.get(0));
                        for (int j = 1; j < valueStrings2.size(); ++j) {
                            this.tool.wrapStandardOut(0, subsequentIndent, this.wrapColumn, true, rightAlign("", maxNumberLength), ' ', leftAlign("", maxIdentifierLength), " - ", valueStrings2.get(j));
                        }
                        break;
                    }
                }
                ++optionNumber;
            }
            if (this.parser.allowsTrailingArguments()) {
                final List<String> trailingArgs = this.parser.getTrailingArguments();
                switch (trailingArgs.size()) {
                    case 0: {
                        this.tool.wrapStandardOut(0, subsequentIndent, this.wrapColumn, true, rightAlign("t", maxNumberLength), ' ', leftAlign(trailingArgsIdentifier, maxIdentifierLength), " -");
                        break;
                    }
                    case 1: {
                        this.tool.wrapStandardOut(0, subsequentIndent, this.wrapColumn, true, rightAlign("t", maxNumberLength), ' ', leftAlign(trailingArgsIdentifier, maxIdentifierLength), " - ", trailingArgs.get(0));
                        break;
                    }
                    default: {
                        this.tool.wrapStandardOut(0, subsequentIndent, this.wrapColumn, true, rightAlign("t", maxNumberLength), ' ', leftAlign(trailingArgsIdentifier, maxIdentifierLength), " - ", trailingArgs.get(0));
                        for (int k = 1; k < trailingArgs.size(); ++k) {
                            this.tool.wrapStandardOut(0, subsequentIndent, this.wrapColumn, true, rightAlign("", maxNumberLength), ' ', leftAlign("", maxIdentifierLength), " - ", trailingArgs.get(k));
                        }
                        break;
                    }
                }
            }
            this.tool.out(new Object[0]);
            if (this.tool instanceof LDAPCommandLineTool) {
                final LDAPCommandLineTool ldapTool = (LDAPCommandLineTool)this.tool;
                if (ldapTool.supportsAuthentication()) {
                    this.tool.wrapStandardOut(maxNumberLength - 1, subsequentIndent, this.wrapColumn, true, "l - ", UtilityMessages.INFO_INTERACTIVE_MENU_OPTION_REPROMPT_FOR_CONN_AUTH_ARGS.get());
                }
                else {
                    this.tool.wrapStandardOut(maxNumberLength - 1, subsequentIndent, this.wrapColumn, true, "l - ", UtilityMessages.INFO_INTERACTIVE_MENU_OPTION_REPROMPT_FOR_CONN_ARGS.get());
                }
            }
            else if (this.tool instanceof MultiServerLDAPCommandLineTool) {
                this.tool.wrapStandardOut(maxNumberLength - 1, subsequentIndent, this.wrapColumn, true, "l - ", UtilityMessages.INFO_INTERACTIVE_MENU_OPTION_REPROMPT_FOR_CONN_AUTH_ARGS.get());
            }
            this.tool.wrapStandardOut(maxNumberLength - 1, subsequentIndent, this.wrapColumn, true, "d - ", UtilityMessages.INFO_INTERACTIVE_MENU_OPTION_DISPLAY_ARGS.get(this.tool.getToolName()));
            this.tool.wrapStandardOut(maxNumberLength - 1, subsequentIndent, this.wrapColumn, true, "r - ", UtilityMessages.INFO_INTERACTIVE_MENU_OPTION_RUN.get(this.tool.getToolName()));
            this.tool.wrapStandardOut(maxNumberLength - 1, subsequentIndent, this.wrapColumn, true, "q - ", UtilityMessages.INFO_INTERACTIVE_MENU_OPTION_QUIT.get());
            this.tool.out(new Object[0]);
            this.tool.getOut().print(UtilityMessages.INFO_INTERACTIVE_MENU_ENTER_CHOICE_WITHOUT_DEFAULT.get());
            Argument selectedArg;
            try {
                while (true) {
                    final String line = this.systemInReader.readLine().trim();
                    if (line.equalsIgnoreCase("t") && this.tool.getMaxTrailingArguments() != 0) {
                        this.promptForTrailingArguments();
                        continue Label_2329;
                    }
                    if (line.equalsIgnoreCase("l")) {
                        if (this.tool instanceof LDAPCommandLineTool) {
                            this.promptForLDAPArguments(ldapArgs, true);
                        }
                        else if (this.tool instanceof MultiServerLDAPCommandLineTool) {
                            this.promptForMultiServerLDAPArguments(ldapArgs, true);
                        }
                        else {
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_ARG_MENU_INVALID_CHOICE.get());
                            this.tool.getOut().print(UtilityMessages.INFO_INTERACTIVE_MENU_ENTER_CHOICE_WITHOUT_DEFAULT.get());
                        }
                        continue Label_2329;
                    }
                    if (line.equalsIgnoreCase("d")) {
                        try {
                            this.validateRequiredExclusiveAndDependentArgumentSets();
                            this.tool.doExtendedArgumentValidation();
                            final ArrayList<String> argStrings = new ArrayList<String>(2 * args.size());
                            final SubCommand subcommand = this.parser.getSelectedSubCommand();
                            if (subcommand != null) {
                                argStrings.add(subcommand.getPrimaryName());
                            }
                            argStrings.addAll(ldapArgs);
                            for (final Argument a2 : args) {
                                ArgumentHelper.addToCommandLine(a2, argStrings);
                            }
                            argStrings.addAll(this.parser.getTrailingArguments());
                            if (argStrings.isEmpty()) {
                                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_MENU_NO_CURRENT_ARGS.get(this.tool.getToolName()));
                            }
                            else {
                                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_MENU_CURRENT_ARGS_HEADER.get(this.tool.getToolName()));
                                this.printArgs(argStrings);
                            }
                            this.tool.out(new Object[0]);
                            this.promptForString(UtilityMessages.INFO_INTERACTIVE_MENU_PROMPT_PRESS_ENTER_TO_CONTINUE.get(), null, false);
                        }
                        catch (final ArgumentException ae) {
                            Debug.debugException(ae);
                            this.tool.err(new Object[0]);
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_MENU_EXTENDED_VALIDATION_ERRORS.get(ae.getMessage()));
                            this.tool.err(new Object[0]);
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_MENU_CORRECT_VALIDATION_ERRORS.get());
                            this.tool.err(new Object[0]);
                            this.promptForString(UtilityMessages.INFO_INTERACTIVE_MENU_PROMPT_PRESS_ENTER_TO_CONTINUE.get(), null, false);
                        }
                        continue Label_2329;
                    }
                    if (line.equalsIgnoreCase("r")) {
                        try {
                            this.validateRequiredExclusiveAndDependentArgumentSets();
                            this.tool.doExtendedArgumentValidation();
                            break Label_2329;
                        }
                        catch (final ArgumentException ae) {
                            Debug.debugException(ae);
                            this.tool.err(new Object[0]);
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_MENU_EXTENDED_VALIDATION_ERRORS.get(ae.getMessage()));
                            this.tool.err(new Object[0]);
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_MENU_CORRECT_VALIDATION_ERRORS.get());
                            this.tool.err(new Object[0]);
                            this.promptForString(UtilityMessages.INFO_INTERACTIVE_MENU_PROMPT_PRESS_ENTER_TO_CONTINUE.get(), null, false);
                            continue Label_2329;
                        }
                    }
                    if (line.equalsIgnoreCase("q")) {
                        throw new LDAPException(ResultCode.SUCCESS, "");
                    }
                    int selectedValue = -1;
                    try {
                        selectedValue = Integer.parseInt(line);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                    }
                    if (selectedValue >= 1 && selectedValue <= args.size()) {
                        selectedArg = args.get(selectedValue - 1);
                        break;
                    }
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_ARG_MENU_INVALID_CHOICE.get());
                    this.tool.getOut().print(UtilityMessages.INFO_INTERACTIVE_MENU_ENTER_CHOICE_WITHOUT_DEFAULT.get());
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw le;
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_MENU_CANNOT_READ_CHOICE.get(StaticUtils.getExceptionMessage(e2)), e2);
            }
            this.promptForArgument(selectedArg);
        }
        final ArrayList<String> argStrings2 = new ArrayList<String>(2 * args.size());
        for (final Argument a3 : args) {
            ArgumentHelper.addToCommandLine(a3, argStrings2);
        }
        argStrings2.addAll(this.parser.getTrailingArguments());
        return argStrings2;
    }
    
    private void promptForTrailingArguments() throws LDAPException {
        this.tool.out(new Object[0]);
        ArgumentHelper.resetTrailingArguments(this.parser);
        if (this.parser.getMaxTrailingArguments() == 1) {
            if (this.parser.requiresTrailingArguments()) {
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_TRAILING_DESC_SINGLE_REQUIRED.get(this.tool.getToolName()));
            }
            else {
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_TRAILING_DESC_SINGLE_OPTIONAL.get(this.tool.getToolName()));
            }
            this.tool.out("     ", this.tool.getTrailingArgumentsPlaceholder());
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_TRAILING_PROMPT_SINGLE.get());
            while (true) {
                final String trailingArgValue = this.promptForString(UtilityMessages.INFO_INTERACTIVE_TRAILING_ARG_PROMPT.get(), null, false);
                if (trailingArgValue == null) {
                    return;
                }
                try {
                    ArgumentHelper.addTrailingArgument(this.parser, trailingArgValue);
                    return;
                }
                catch (final ArgumentException ae) {
                    Debug.debugException(ae);
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_TRAILING_VALUE_INVALID.get(ae.getMessage()));
                    continue;
                }
                break;
            }
        }
        if (this.parser.requiresTrailingArguments()) {
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_TRAILING_DESC_MULTIPLE_REQUIRED.get(this.tool.getToolName(), this.parser.getMinTrailingArguments()));
        }
        else {
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_TRAILING_DESC_MULTIPLE_OPTIONAL.get(this.tool.getToolName()));
        }
        this.tool.out("     ", this.tool.getTrailingArgumentsPlaceholder());
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_TRAILING_PROMPT_MULTIPLE.get());
        while (true) {
            final String trailingArgValue = this.promptForString(UtilityMessages.INFO_INTERACTIVE_TRAILING_ARG_PROMPT.get(), null, false);
            if (trailingArgValue == null) {
                break;
            }
            try {
                ArgumentHelper.addTrailingArgument(this.parser, trailingArgValue);
            }
            catch (final ArgumentException ae) {
                Debug.debugException(ae);
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_TRAILING_VALUE_INVALID.get(ae.getMessage()));
            }
        }
    }
    
    private void promptForArgument(final Argument a) throws LDAPException {
        this.tool.out(new Object[0]);
        final int maxValues = a.getMaxOccurrences();
        if (maxValues == 1) {
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_SPECIFY_SINGLE_VALUE.get(a.getIdentifierString()));
        }
        else {
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_SPECIFY_MULTIPLE_VALUES.get(a.getIdentifierString()));
        }
        final String description = a.getDescription();
        if (description != null && !description.isEmpty()) {
            this.tool.out(new Object[0]);
            final String prompt = UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_DESCRIPTION.get();
            this.tool.wrapStandardOut(0, prompt.length(), this.wrapColumn, true, prompt, description);
        }
        final String constraints = a.getValueConstraints();
        if (constraints != null && !constraints.isEmpty()) {
            this.tool.out(new Object[0]);
            final String prompt2 = UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_CONSTRAINTS.get();
            this.tool.wrapStandardOut(0, prompt2.length(), this.wrapColumn, true, prompt2, constraints);
            if (a.isRequired()) {
                if (maxValues == 1) {
                    this.tool.wrapStandardOut(prompt2.length(), prompt2.length(), this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_SINGLE_REQUIRED.get());
                }
                else {
                    this.tool.wrapStandardOut(prompt2.length(), prompt2.length(), this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_AT_LEAST_ONE_REQUIRED.get());
                }
            }
        }
        else if (a.isRequired()) {
            this.tool.out(new Object[0]);
            final String prompt2 = UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_CONSTRAINTS.get();
            if (maxValues == 1) {
                this.tool.wrapStandardOut(0, prompt2.length(), this.wrapColumn, true, prompt2, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_SINGLE_REQUIRED.get());
            }
            else {
                this.tool.wrapStandardOut(0, prompt2.length(), this.wrapColumn, true, prompt2, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_AT_LEAST_ONE_REQUIRED.get());
            }
        }
        if (a instanceof ArgumentListArgument) {
            this.promptForArgumentList((ArgumentListArgument)a);
        }
        else if (a instanceof BooleanArgument) {
            this.promptForBoolean((BooleanArgument)a);
        }
        else if (a instanceof BooleanValueArgument) {
            this.promptForBoolean((BooleanValueArgument)a);
        }
        else if (a instanceof ControlArgument) {
            this.promptForControl((ControlArgument)a);
        }
        else if (a instanceof DNArgument) {
            this.promptForDN((DNArgument)a);
        }
        else if (a instanceof DurationArgument) {
            this.promptForDuration((DurationArgument)a);
        }
        else if (a instanceof FileArgument) {
            this.promptForFile((FileArgument)a);
        }
        else if (a instanceof FilterArgument) {
            this.promptForFilter((FilterArgument)a);
        }
        else if (a instanceof IntegerArgument) {
            this.promptForInteger((IntegerArgument)a);
        }
        else if (a instanceof ScopeArgument) {
            this.promptForScope((ScopeArgument)a);
        }
        else if (a instanceof StringArgument) {
            this.promptForString((StringArgument)a);
        }
        else {
            if (!(a instanceof TimestampArgument)) {
                throw new AssertionError((Object)("Unexpected argument type " + a.getClass().getName()));
            }
            this.promptForTimestamp((TimestampArgument)a);
        }
    }
    
    private void promptForArgumentList(final ArgumentListArgument a) throws LDAPException {
        final List<String> values = a.getValueStrings();
        ArgumentHelper.reset(a);
        if (a.getMaxOccurrences() == 1) {
            if (!values.isEmpty()) {
                this.tool.out(new Object[0]);
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, values.get(0));
            }
            while (true) {
                final String newValue = this.promptForString(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, a.isRequired());
                try {
                    if (newValue != null) {
                        ArgumentHelper.addValue(a, newValue);
                    }
                    return;
                }
                catch (final ArgumentException ae) {
                    Debug.debugException(ae);
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_ARG_PROMPT_INVALID_VALUE.get(ae.getMessage()));
                    continue;
                }
                break;
            }
        }
        if (!values.isEmpty()) {
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUES.get());
            for (final String s : values) {
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, s);
            }
        }
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUES.get());
        boolean first = true;
        while (true) {
            final String s = this.promptForString(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, first && a.isRequired());
            if (s == null) {
                break;
            }
            try {
                ArgumentHelper.addValue(a, s);
                first = false;
            }
            catch (final ArgumentException ae2) {
                Debug.debugException(ae2);
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_ARG_PROMPT_INVALID_VALUE.get(ae2.getMessage()));
            }
        }
    }
    
    private void promptForBoolean(final BooleanArgument a) throws LDAPException {
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
        this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, a.isPresent());
        ArgumentHelper.reset(a);
        if (this.promptForBoolean(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, true)) {
            ArgumentHelper.incrementOccurrencesSuppressException(a);
        }
    }
    
    private void promptForBoolean(final BooleanValueArgument a) throws LDAPException {
        final Boolean value = a.getValue();
        ArgumentHelper.reset(a);
        final Boolean b = this.promptForBoolean(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, a.isRequired());
        if (b != null) {
            ArgumentHelper.addValueSuppressException(a, String.valueOf(b));
        }
    }
    
    private void promptForControl(final ControlArgument a) throws LDAPException {
        final List<Control> values = a.getValues();
        ArgumentHelper.reset(a);
        if (a.getMaxOccurrences() == 1) {
            if (!values.isEmpty()) {
                this.tool.out(new Object[0]);
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, values.get(0));
            }
            while (true) {
                final String newValue = this.promptForString(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, false);
                try {
                    if (newValue == null) {
                        ArgumentHelper.addValue(a, "");
                    }
                    else {
                        ArgumentHelper.addValue(a, newValue);
                    }
                    return;
                }
                catch (final ArgumentException ae) {
                    Debug.debugException(ae);
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_ARG_PROMPT_INVALID_VALUE.get(ae.getMessage()));
                    continue;
                }
                break;
            }
        }
        if (!values.isEmpty()) {
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUES.get());
            for (final Control c : values) {
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, c);
            }
        }
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUES.get());
        boolean first = true;
        while (true) {
            final String s = this.promptForString(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, first && a.isRequired());
            if (s == null) {
                break;
            }
            try {
                ArgumentHelper.addValue(a, s);
                first = false;
            }
            catch (final ArgumentException ae2) {
                Debug.debugException(ae2);
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_ARG_PROMPT_INVALID_VALUE.get(ae2.getMessage()));
            }
        }
    }
    
    private void promptForDN(final DNArgument a) throws LDAPException {
        final List<DN> values = a.getValues();
        ArgumentHelper.reset(a);
        if (a.getMaxOccurrences() == 1) {
            if (!values.isEmpty()) {
                this.tool.out(new Object[0]);
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, values.get(0));
            }
            final DN dnValue = this.promptForDN(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, true);
            ArgumentHelper.addValueSuppressException(a, String.valueOf(dnValue));
            return;
        }
        if (!values.isEmpty()) {
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUES.get());
            for (final DN dn : values) {
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, dn);
            }
        }
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUES.get());
        boolean first = true;
        while (true) {
            boolean allowNullDN;
            if (first) {
                first = false;
                allowNullDN = !a.isRequired();
            }
            else {
                allowNullDN = true;
            }
            final DN dnValue2 = this.promptForDN(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, allowNullDN);
            if (dnValue2.isNullDN()) {
                break;
            }
            ArgumentHelper.addValueSuppressException(a, String.valueOf(dnValue2));
        }
    }
    
    private void promptForDuration(final DurationArgument a) throws LDAPException {
        final List<String> values = a.getValueStringRepresentations(true);
        if (!values.isEmpty()) {
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
            this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, values.get(0));
        }
        while (true) {
            final String newValue = this.promptForString(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, a.isRequired());
            try {
                if (newValue != null) {
                    ArgumentHelper.addValue(a, newValue);
                }
            }
            catch (final ArgumentException ae) {
                Debug.debugException(ae);
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_ARG_PROMPT_INVALID_VALUE.get(ae.getMessage()));
            }
        }
    }
    
    private void promptForFile(final FileArgument a) throws LDAPException {
        final List<File> values = a.getValues();
        ArgumentHelper.reset(a);
        if (a.getMaxOccurrences() == 1) {
            if (!values.isEmpty()) {
                this.tool.out(new Object[0]);
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, values.get(0));
            }
            final File fileValue = this.promptForPath(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, a.isRequired(), a.fileMustExist(), a.parentMustExist(), a.mustBeFile(), a.mustBeDirectory());
            if (fileValue != null) {
                ArgumentHelper.addValueSuppressException(a, fileValue.getPath());
            }
            return;
        }
        if (!values.isEmpty()) {
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUES.get());
            for (final File f : values) {
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, f.getPath());
            }
        }
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUES.get());
        boolean first = true;
        while (true) {
            boolean isRequired;
            if (first) {
                first = false;
                isRequired = a.isRequired();
            }
            else {
                isRequired = false;
            }
            final File fileValue2 = this.promptForPath(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, isRequired, a.fileMustExist(), a.parentMustExist(), a.mustBeFile(), a.mustBeDirectory());
            if (fileValue2 == null) {
                break;
            }
            ArgumentHelper.addValueSuppressException(a, fileValue2.getPath());
        }
    }
    
    private void promptForFilter(final FilterArgument a) throws LDAPException {
        final List<Filter> values = a.getValues();
        ArgumentHelper.reset(a);
        if (a.getMaxOccurrences() == 1) {
            if (!values.isEmpty()) {
                this.tool.out(new Object[0]);
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, values.get(0));
            }
            final Filter filterValue = this.promptForFilter(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, a.isRequired());
            if (filterValue != null) {
                ArgumentHelper.addValueSuppressException(a, filterValue.toString());
            }
            return;
        }
        if (!values.isEmpty()) {
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUES.get());
            for (final Filter f : values) {
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, String.valueOf(f));
            }
        }
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUES.get());
        boolean first = true;
        while (true) {
            boolean isRequired;
            if (first) {
                first = false;
                isRequired = a.isRequired();
            }
            else {
                isRequired = false;
            }
            final Filter filterValue2 = this.promptForFilter(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, isRequired);
            if (filterValue2 == null) {
                break;
            }
            ArgumentHelper.addValueSuppressException(a, String.valueOf(filterValue2));
        }
    }
    
    private void promptForInteger(final IntegerArgument a) throws LDAPException {
        final List<Integer> values = a.getValues();
        ArgumentHelper.reset(a);
        if (a.getMaxOccurrences() == 1) {
            if (!values.isEmpty()) {
                this.tool.out(new Object[0]);
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, values.get(0));
            }
            final Integer intValue = this.promptForInteger(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, a.getLowerBound(), a.getUpperBound(), a.isRequired());
            if (intValue != null) {
                ArgumentHelper.addValueSuppressException(a, String.valueOf(intValue));
            }
            return;
        }
        if (!values.isEmpty()) {
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUES.get());
            for (final Integer i : values) {
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, i);
            }
        }
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUES.get());
        boolean first = true;
        while (true) {
            boolean isRequired;
            if (first) {
                first = false;
                isRequired = a.isRequired();
            }
            else {
                isRequired = false;
            }
            final Integer intValue2 = this.promptForInteger(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, a.getLowerBound(), a.getUpperBound(), isRequired);
            if (intValue2 == null) {
                break;
            }
            ArgumentHelper.addValueSuppressException(a, String.valueOf(intValue2));
        }
    }
    
    private void promptForScope(final ScopeArgument a) throws LDAPException {
        final SearchScope value = a.getValue();
        ArgumentHelper.reset(a);
        final String[] scopeValues = { "base", "one", "sub", "subordinates" };
        this.tool.out(new Object[0]);
        if (value != null && value.intValue() < scopeValues.length) {
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
            this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, scopeValues[value.intValue()]);
        }
        final int newIntValue = this.getNumberedMenuChoice(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), !a.isRequired(), null, scopeValues);
        if (newIntValue >= 0) {
            ArgumentHelper.addValueSuppressException(a, scopeValues[newIntValue]);
        }
    }
    
    private void promptForString(final StringArgument a) throws LDAPException {
        if (a.getAllowedValues() != null && !a.getAllowedValues().isEmpty() && a.getAllowedValues().size() <= 20) {
            this.promptForStringWithMenu(a);
            return;
        }
        final List<String> values = a.getValues();
        ArgumentHelper.reset(a);
        if (a.getMaxOccurrences() == 1) {
            if (!values.isEmpty()) {
                this.tool.out(new Object[0]);
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, values.get(0));
            }
            while (true) {
                String newValue;
                if (a.isSensitive()) {
                    final byte[] newValueBytes = this.promptForPassword(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_VALUE_CONFIRM.get(), a.isRequired());
                    if (newValueBytes == null) {
                        newValue = null;
                    }
                    else {
                        newValue = StaticUtils.toUTF8String(newValueBytes);
                    }
                }
                else {
                    newValue = this.promptForString(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, a.isRequired());
                }
                try {
                    if (newValue != null) {
                        ArgumentHelper.addValue(a, newValue);
                    }
                    return;
                }
                catch (final ArgumentException ae) {
                    Debug.debugException(ae);
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_ARG_PROMPT_INVALID_VALUE.get(ae.getMessage()));
                    continue;
                }
                break;
            }
        }
        if (!values.isEmpty()) {
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUES.get());
            for (final String s : values) {
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, s);
            }
        }
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUES.get());
        boolean first = true;
        while (true) {
            final String s = this.promptForString(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, first && a.isRequired());
            if (s == null) {
                break;
            }
            try {
                ArgumentHelper.addValue(a, s);
                first = false;
            }
            catch (final ArgumentException ae2) {
                Debug.debugException(ae2);
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_ARG_PROMPT_INVALID_VALUE.get(ae2.getMessage()));
            }
        }
    }
    
    private void promptForStringWithMenu(final StringArgument a) throws LDAPException {
        final List<String> values = a.getValues();
        ArgumentHelper.reset(a);
        final String[] allowedValueArray = new String[a.getAllowedValues().size()];
        a.getAllowedValues().toArray(allowedValueArray);
        if (!values.isEmpty()) {
            this.tool.out(new Object[0]);
            if (values.size() == 1) {
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, values.get(0));
            }
            else {
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUES.get());
                for (final String s : values) {
                    this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, s);
                }
            }
        }
        String message;
        if (a.getMaxOccurrences() > 1) {
            message = UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUES.get();
        }
        else {
            message = UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get();
        }
        final int firstChoice = this.getNumberedMenuChoice(message, !a.isRequired(), null, allowedValueArray);
        if (firstChoice < 0) {
            return;
        }
        ArgumentHelper.addValueSuppressException(a, allowedValueArray[firstChoice]);
        if (a.getMaxOccurrences() <= 1) {
            return;
        }
        while (true) {
            final String stringValue = this.promptForString(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, false);
            if (stringValue == null) {
                return;
            }
            if (stringValue.equalsIgnoreCase("q")) {
                throw new LDAPException(ResultCode.SUCCESS, "");
            }
            int selectedValue = -1;
            try {
                selectedValue = Integer.parseInt(stringValue);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            if (selectedValue < 1 || selectedValue > allowedValueArray.length) {
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_MENU_INVALID_CHOICE.get());
            }
            else {
                ArgumentHelper.addValueSuppressException(a, allowedValueArray[selectedValue - 1]);
            }
        }
    }
    
    private void promptForTimestamp(final TimestampArgument a) throws LDAPException {
        final List<String> stringValues = a.getValueStringRepresentations(true);
        ArgumentHelper.reset(a);
        if (a.getMaxOccurrences() == 1) {
            if (!stringValues.isEmpty()) {
                this.tool.out(new Object[0]);
                this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, stringValues.get(0));
            }
            final ObjectPair<Date, String> p = this.promptForTimestamp(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, a.isRequired());
            if (p != null) {
                ArgumentHelper.addValueSuppressException(a, p.getSecond());
            }
            return;
        }
        if (!stringValues.isEmpty()) {
            this.tool.out(new Object[0]);
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUES.get());
            for (final String s : stringValues) {
                this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, String.valueOf(s));
            }
        }
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUES.get());
        boolean first = true;
        while (true) {
            boolean isRequired;
            if (first) {
                first = false;
                isRequired = a.isRequired();
            }
            else {
                isRequired = false;
            }
            final Filter filterValue = this.promptForFilter(UtilityMessages.INFO_INTERACTIVE_ARG_PROMPT_NEW_VALUE.get(), null, isRequired);
            if (filterValue == null) {
                break;
            }
            ArgumentHelper.addValueSuppressException(a, String.valueOf(filterValue));
        }
    }
    
    private int getNumberedMenuChoice(final String prompt, final boolean allowUndefined, final String defaultOptionString, final String... options) throws LDAPException {
        final int maxNumberLength = String.valueOf(options.length).length();
        final int subsequentIndent = maxNumberLength + 3;
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, prompt);
        int optionNumber = 1;
        for (final String option : options) {
            this.tool.wrapStandardOut(0, subsequentIndent, this.wrapColumn, true, rightAlign(String.valueOf(optionNumber), maxNumberLength), " - ", option);
            ++optionNumber;
        }
        this.tool.out(new Object[0]);
        if (allowUndefined) {
            this.tool.wrapStandardOut(maxNumberLength - 1, subsequentIndent, this.wrapColumn, true, "u - ", UtilityMessages.INFO_INTERACTIVE_MENU_OPTION_UNDEFINED.get());
        }
        this.tool.wrapStandardOut(maxNumberLength - 1, subsequentIndent, this.wrapColumn, true, "q - ", UtilityMessages.INFO_INTERACTIVE_MENU_OPTION_QUIT.get());
        Label_0251: {
            if (defaultOptionString == null) {
                final String message = UtilityMessages.INFO_INTERACTIVE_MENU_ENTER_CHOICE_WITHOUT_DEFAULT.get();
                break Label_0251;
            }
            final String message = UtilityMessages.INFO_INTERACTIVE_MENU_ENTER_CHOICE_WITH_DEFAULT.get(defaultOptionString);
            try {
                while (true) {
                    this.tool.out(new Object[0]);
                    this.tool.wrapStandardOut(0, 0, this.wrapColumn, false, message);
                    String line = this.systemInReader.readLine().trim();
                    if (line.equalsIgnoreCase("q")) {
                        throw new LDAPException(ResultCode.SUCCESS, "");
                    }
                    if (allowUndefined && line.equalsIgnoreCase("u")) {
                        return -1;
                    }
                    if (line.isEmpty() && defaultOptionString != null) {
                        line = defaultOptionString;
                    }
                    int selectedValue = -1;
                    try {
                        selectedValue = Integer.parseInt(line);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                    }
                    if (selectedValue >= 1 && selectedValue <= options.length) {
                        return selectedValue - 1;
                    }
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_MENU_INVALID_CHOICE.get());
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw le;
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_MENU_CANNOT_READ_CHOICE.get(StaticUtils.getExceptionMessage(e2)), e2);
            }
        }
    }
    
    private String promptForString(final String prompt, final String defaultValue, final boolean requireValue) throws LDAPException {
        this.tool.out(new Object[0]);
        String promptStr;
        if (defaultValue == null) {
            promptStr = prompt + ": ";
        }
        else {
            promptStr = prompt + " [" + defaultValue + "]: ";
        }
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, false, promptStr);
        try {
            String line = this.systemInReader.readLine().trim();
            if (line.isEmpty() && defaultValue != null) {
                line = defaultValue;
            }
            if (!line.isEmpty()) {
                return line;
            }
            if (requireValue) {
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_VALUE_REQUIRED.get());
                return this.promptForString(prompt, defaultValue, requireValue);
            }
            return null;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_PROMPT_ERROR_READING_RESPONSE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private Boolean promptForBoolean(final String prompt, final Boolean defaultValue, final boolean requireValue) throws LDAPException {
        final String[] choices = { "true", "false" };
        this.tool.out(new Object[0]);
        if (defaultValue != null) {
            this.tool.wrapStandardOut(0, 0, this.wrapColumn, true, UtilityMessages.INFO_INTERACTIVE_ARG_DESC_CURRENT_VALUE.get());
            this.tool.wrapStandardOut(5, 10, this.wrapColumn, true, String.valueOf(defaultValue));
        }
        final int newIntValue = this.getNumberedMenuChoice(prompt, !requireValue, null, choices);
        switch (newIntValue) {
            case 0: {
                return Boolean.TRUE;
            }
            case 1: {
                return Boolean.FALSE;
            }
            default: {
                return null;
            }
        }
    }
    
    private DN promptForDN(final String prompt, final String defaultValue, final boolean nullDNAllowed) throws LDAPException {
        this.tool.out(new Object[0]);
        String promptStr;
        if (defaultValue == null) {
            promptStr = prompt + ": ";
        }
        else {
            promptStr = prompt + " [" + defaultValue + "]: ";
        }
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, false, promptStr);
        try {
            String line = this.systemInReader.readLine().trim();
            if (line.isEmpty()) {
                if (defaultValue != null) {
                    line = defaultValue;
                }
                if (line.isEmpty()) {
                    if (nullDNAllowed) {
                        return DN.NULL_DN;
                    }
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_NULL_DN_NOT_ALLOWED.get());
                    return this.promptForDN(prompt, defaultValue, nullDNAllowed);
                }
            }
            try {
                return new DN(line);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_INVALID_DN.get());
                return this.promptForDN(prompt, defaultValue, nullDNAllowed);
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_PROMPT_ERROR_READING_RESPONSE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private Filter promptForFilter(final String prompt, final Filter defaultValue, final boolean requireValue) throws LDAPException {
        this.tool.out(new Object[0]);
        String promptStr;
        if (defaultValue == null) {
            promptStr = prompt + ": ";
        }
        else {
            promptStr = prompt + " [" + defaultValue + "]: ";
        }
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, false, promptStr);
        try {
            String line = this.systemInReader.readLine().trim();
            if (line.isEmpty() && defaultValue != null) {
                line = String.valueOf(defaultValue);
            }
            if (line.isEmpty()) {
                if (requireValue) {
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_VALUE_REQUIRED.get());
                    return this.promptForFilter(prompt, defaultValue, requireValue);
                }
                return null;
            }
            else {
                try {
                    return Filter.create(line);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_INVALID_FILTER.get());
                    return this.promptForFilter(prompt, defaultValue, requireValue);
                }
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_PROMPT_ERROR_READING_RESPONSE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private Integer promptForInteger(final String prompt, final Integer defaultValue, final Integer lowerBound, final Integer upperBound, final boolean requireValue) throws LDAPException {
        this.tool.out(new Object[0]);
        int max;
        if (upperBound == null) {
            max = Integer.MAX_VALUE;
        }
        else {
            max = upperBound;
        }
        int min;
        if (lowerBound == null) {
            min = Integer.MIN_VALUE;
        }
        else {
            min = lowerBound;
        }
        String promptStr;
        if (defaultValue == null) {
            promptStr = prompt + ": ";
        }
        else {
            promptStr = prompt + " [" + defaultValue + "]: ";
        }
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, false, promptStr);
        try {
            String line = this.systemInReader.readLine().trim();
            if (line.isEmpty() && defaultValue != null) {
                line = String.valueOf(defaultValue);
            }
            if (line.isEmpty()) {
                if (requireValue) {
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_VALUE_REQUIRED.get());
                    return this.promptForInteger(prompt, defaultValue, lowerBound, upperBound, requireValue);
                }
                return null;
            }
            else {
                int intValue;
                try {
                    intValue = Integer.parseInt(line);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_INVALID_INTEGER_WITH_RANGE.get(min, max));
                    return this.promptForInteger(prompt, defaultValue, lowerBound, upperBound, requireValue);
                }
                if (intValue > max || intValue < min) {
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_INVALID_INTEGER_WITH_RANGE.get(min, max));
                    return this.promptForInteger(prompt, defaultValue, lowerBound, upperBound, requireValue);
                }
                return intValue;
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_PROMPT_ERROR_READING_RESPONSE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private File promptForPath(final String prompt, final String defaultValue, final boolean requireValue, final boolean fileMustExist, final boolean parentMustExist, final boolean mustBeFile, final boolean mustBeDirectory) throws LDAPException {
        this.tool.out(new Object[0]);
        String promptStr;
        if (defaultValue == null) {
            promptStr = prompt + ": ";
        }
        else {
            promptStr = prompt + " [" + defaultValue + "]: ";
        }
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, false, promptStr);
        try {
            String line = this.systemInReader.readLine().trim();
            if (line.isEmpty() && defaultValue != null) {
                line = String.valueOf(defaultValue);
            }
            if (line.isEmpty()) {
                if (requireValue) {
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_VALUE_REQUIRED.get());
                    return this.promptForPath(prompt, defaultValue, requireValue, fileMustExist, parentMustExist, mustBeFile, mustBeDirectory);
                }
                return null;
            }
            else {
                final File f = new File(line).getAbsoluteFile();
                if (f.exists()) {
                    if (f.isDirectory()) {
                        if (mustBeFile) {
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_PATH_MUST_BE_FILE.get(f.getAbsolutePath()));
                            return this.promptForPath(prompt, defaultValue, requireValue, fileMustExist, parentMustExist, mustBeFile, mustBeDirectory);
                        }
                    }
                    else if (mustBeDirectory) {
                        this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_PATH_MUST_BE_DIR.get(f.getAbsolutePath()));
                        return this.promptForPath(prompt, defaultValue, requireValue, fileMustExist, parentMustExist, mustBeFile, mustBeDirectory);
                    }
                    return f;
                }
                if (!fileMustExist) {
                    if (parentMustExist) {
                        final File parent = f.getParentFile();
                        if (parent == null || !parent.exists()) {
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_PARENT_DOES_NOT_EXIST.get(f.getAbsolutePath()));
                            return this.promptForPath(prompt, defaultValue, requireValue, fileMustExist, parentMustExist, mustBeFile, mustBeDirectory);
                        }
                    }
                    return f;
                }
                if (mustBeDirectory) {
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_DIR_DOES_NOT_EXIST.get(f.getAbsolutePath()));
                    return this.promptForPath(prompt, defaultValue, requireValue, fileMustExist, parentMustExist, mustBeFile, mustBeDirectory);
                }
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_FILE_DOES_NOT_EXIST.get(f.getAbsolutePath()));
                return this.promptForPath(prompt, defaultValue, requireValue, fileMustExist, parentMustExist, mustBeFile, mustBeDirectory);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_PROMPT_ERROR_READING_RESPONSE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private byte[] promptForPassword(final String prompt, final String confirmPrompt, final boolean requireValue) throws LDAPException {
        this.tool.out(new Object[0]);
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, false, prompt, ": ");
        try {
            byte[] pwBytes;
            try {
                if (CommandLineToolInteractiveModeProcessor.IN_UNIT_TEST) {
                    PasswordReader.setTestReader(this.systemInReader);
                }
                pwBytes = PasswordReader.readPassword();
            }
            finally {
                PasswordReader.setTestReader(null);
            }
            if (pwBytes != null && pwBytes.length != 0) {
                if (confirmPrompt != null) {
                    try {
                        if (CommandLineToolInteractiveModeProcessor.IN_UNIT_TEST) {
                            PasswordReader.setTestReader(this.systemInReader);
                        }
                        this.tool.wrapStandardOut(0, 0, this.wrapColumn, false, confirmPrompt, ": ");
                        final byte[] confirmedPWBytes = PasswordReader.readPassword();
                        if (!Arrays.equals(pwBytes, confirmedPWBytes)) {
                            this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_CONFIRM_MISMATCH.get());
                            return this.promptForPassword(prompt, confirmPrompt, requireValue);
                        }
                    }
                    finally {
                        PasswordReader.setTestReader(null);
                    }
                }
                return pwBytes;
            }
            if (requireValue) {
                this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_VALUE_REQUIRED.get());
                return this.promptForPassword(prompt, confirmPrompt, requireValue);
            }
            return null;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_PROMPT_ERROR_READING_RESPONSE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private ObjectPair<Date, String> promptForTimestamp(final String prompt, final Date defaultValue, final boolean requireValue) throws LDAPException {
        this.tool.out(new Object[0]);
        String promptStr;
        if (defaultValue == null) {
            promptStr = prompt + ": ";
        }
        else {
            promptStr = prompt + " [" + defaultValue + "]: ";
        }
        this.tool.wrapStandardOut(0, 0, this.wrapColumn, false, promptStr);
        try {
            String line = this.systemInReader.readLine().trim();
            if (line.isEmpty() && defaultValue != null) {
                line = String.valueOf(defaultValue);
            }
            if (line.isEmpty()) {
                if (requireValue) {
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_VALUE_REQUIRED.get());
                    return this.promptForTimestamp(prompt, defaultValue, requireValue);
                }
                return null;
            }
            else {
                try {
                    return new ObjectPair<Date, String>(TimestampArgument.parseTimestamp(line), line);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    this.tool.wrapErr(0, this.wrapColumn, UtilityMessages.ERR_INTERACTIVE_PROMPT_INVALID_TIMESTAMP.get());
                    return this.promptForTimestamp(prompt, defaultValue, requireValue);
                }
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_INTERACTIVE_PROMPT_ERROR_READING_RESPONSE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static String rightAlign(final String s, final int w) {
        final int l = s.length();
        if (l >= w) {
            return s;
        }
        final StringBuilder buffer = new StringBuilder(w);
        for (int i = 0; i < w - l; ++i) {
            buffer.append(' ');
        }
        buffer.append(s);
        return buffer.toString();
    }
    
    private static String leftAlign(final String s, final int w) {
        final int l = s.length();
        if (l >= w) {
            return s;
        }
        final StringBuilder buffer = new StringBuilder(w);
        buffer.append(s);
        while (buffer.length() < w) {
            buffer.append(' ');
        }
        return buffer.toString();
    }
    
    private void validateRequiredExclusiveAndDependentArgumentSets() throws ArgumentException {
        for (final Set<Argument> requiredArgumentsSet : this.parser.getRequiredArgumentSets()) {
            boolean found = false;
            for (final Argument a : requiredArgumentsSet) {
                if (a.getNumOccurrences() > 0) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                final StringBuilder buffer = new StringBuilder();
                for (final Argument a2 : requiredArgumentsSet) {
                    if (buffer.length() > 0) {
                        buffer.append(", ");
                    }
                    buffer.append(a2.getIdentifierString());
                }
                throw new ArgumentException(UtilityMessages.ERR_INTERACTIVE_REQUIRED_ARG_SET_CONFLICT.get(buffer.toString()));
            }
        }
        for (final Set<Argument> exclusiveArgumentsSet : this.parser.getExclusiveArgumentSets()) {
            boolean found = false;
            for (final Argument a : exclusiveArgumentsSet) {
                if (a.getNumOccurrences() > 0) {
                    if (found) {
                        final StringBuilder buffer2 = new StringBuilder();
                        for (final Argument exclusiveArg : exclusiveArgumentsSet) {
                            if (buffer2.length() > 0) {
                                buffer2.append(", ");
                            }
                            buffer2.append(exclusiveArg.getIdentifierString());
                        }
                        throw new ArgumentException(UtilityMessages.ERR_INTERACTIVE_EXCLUSIVE_ARG_SET_CONFLICT.get(buffer2.toString()));
                    }
                    found = true;
                }
            }
        }
        for (final ObjectPair<Argument, Set<Argument>> p : this.parser.getDependentArgumentSets()) {
            if (p.getFirst().getNumOccurrences() > 0) {
                boolean found = false;
                for (final Argument a : p.getSecond()) {
                    if (a.isPresent()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    final StringBuilder buffer = new StringBuilder();
                    for (final Argument arg : p.getSecond()) {
                        if (buffer.length() > 0) {
                            buffer.append(", ");
                        }
                        buffer.append(arg.getIdentifierString());
                    }
                    throw new ArgumentException(UtilityMessages.ERR_INTERACTIVE_DEPENDENT_ARG_SET_CONFLICT.get(p.getFirst().getIdentifierString(), buffer.toString()));
                }
                continue;
            }
        }
    }
    
    static void setInUnitTest(final boolean inUnitTest) {
        CommandLineToolInteractiveModeProcessor.IN_UNIT_TEST = inUnitTest;
    }
    
    static {
        CommandLineToolInteractiveModeProcessor.IN_UNIT_TEST = false;
    }
}
