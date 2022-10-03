package com.unboundid.util;

import java.util.TreeMap;
import java.util.Iterator;
import java.util.HashMap;
import com.unboundid.ldap.sdk.unboundidds.UnboundIDYubiKeyOTPBindRequest;
import com.unboundid.ldap.sdk.unboundidds.SingleUseTOTPBindRequest;
import com.unboundid.ldap.sdk.unboundidds.UnboundIDDeliveredOTPBindRequest;
import com.unboundid.ldap.sdk.unboundidds.UnboundIDCertificatePlusPasswordBindRequest;
import com.unboundid.ldap.sdk.PLAINBindRequest;
import com.unboundid.ldap.sdk.GSSAPIBindRequestProperties;
import com.unboundid.ldap.sdk.GSSAPIBindRequest;
import com.unboundid.ldap.sdk.EXTERNALBindRequest;
import com.unboundid.ldap.sdk.SASLQualityOfProtection;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequestProperties;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequest;
import com.unboundid.ldap.sdk.CRAMMD5BindRequest;
import com.unboundid.ldap.sdk.ANONYMOUSBindRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.SASLBindRequest;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SASLUtils
{
    public static final String SASL_OPTION_AUTH_ID = "authID";
    public static final String SASL_OPTION_AUTHZ_ID = "authzID";
    public static final String SASL_OPTION_CONFIG_FILE = "configFile";
    public static final String SASL_OPTION_DEBUG = "debug";
    public static final String SASL_OPTION_KDC_ADDRESS = "kdcAddress";
    public static final String SASL_OPTION_MECHANISM = "mech";
    public static final String SASL_OPTION_OTP = "otp";
    public static final String SASL_OPTION_PROMPT_FOR_STATIC_PW = "promptForStaticPassword";
    public static final String SASL_OPTION_PROTOCOL = "protocol";
    public static final String SASL_OPTION_QOP = "qop";
    public static final String SASL_OPTION_REALM = "realm";
    public static final String SASL_OPTION_REQUIRE_CACHE = "requireCache";
    public static final String SASL_OPTION_RENEW_TGT = "renewTGT";
    public static final String SASL_OPTION_TICKET_CACHE_PATH = "ticketCache";
    public static final String SASL_OPTION_TOTP_PASSWORD = "totpPassword";
    public static final String SASL_OPTION_TRACE = "trace";
    public static final String SASL_OPTION_USE_TICKET_CACHE = "useTicketCache";
    private static final Map<String, SASLMechanismInfo> SASL_MECHANISMS;
    
    private SASLUtils() {
    }
    
    public static List<SASLMechanismInfo> getSupportedSASLMechanisms() {
        return Collections.unmodifiableList((List<? extends SASLMechanismInfo>)new ArrayList<SASLMechanismInfo>(SASLUtils.SASL_MECHANISMS.values()));
    }
    
    public static SASLMechanismInfo getSASLMechanismInfo(final String mechanism) {
        return SASLUtils.SASL_MECHANISMS.get(StaticUtils.toLowerCase(mechanism));
    }
    
    public static SASLBindRequest createBindRequest(final String bindDN, final String password, final String mechanism, final String... options) throws LDAPException {
        return createBindRequest(bindDN, (byte[])((password == null) ? null : StaticUtils.getBytes(password)), mechanism, StaticUtils.toList(options), new Control[0]);
    }
    
    public static SASLBindRequest createBindRequest(final String bindDN, final String password, final String mechanism, final List<String> options, final Control... controls) throws LDAPException {
        return createBindRequest(bindDN, (byte[])((password == null) ? null : StaticUtils.getBytes(password)), mechanism, options, controls);
    }
    
    public static SASLBindRequest createBindRequest(final String bindDN, final byte[] password, final String mechanism, final String... options) throws LDAPException {
        return createBindRequest(bindDN, password, mechanism, StaticUtils.toList(options), new Control[0]);
    }
    
    public static SASLBindRequest createBindRequest(final String bindDN, final byte[] password, final String mechanism, final List<String> options, final Control... controls) throws LDAPException {
        return createBindRequest(bindDN, password, false, null, mechanism, options, controls);
    }
    
    public static SASLBindRequest createBindRequest(final String bindDN, final byte[] password, final boolean promptForPassword, final CommandLineTool tool, final String mechanism, final List<String> options, final Control... controls) throws LDAPException {
        if (promptForPassword) {
            Validator.ensureNotNull(tool);
        }
        final Map<String, String> optionsMap = parseOptions(options);
        final String mechOption = optionsMap.remove(StaticUtils.toLowerCase("mech"));
        String mech;
        if (mechOption != null) {
            mech = mechOption;
            if (mechanism != null && !mech.equalsIgnoreCase(mechanism)) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_MECH_CONFLICT.get(mechanism, mech));
            }
        }
        else {
            mech = mechanism;
        }
        if (mech == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_NO_MECH.get());
        }
        if (mech.equalsIgnoreCase("ANONYMOUS")) {
            return createANONYMOUSBindRequest(password, optionsMap, controls);
        }
        if (mech.equalsIgnoreCase("CRAM-MD5")) {
            return createCRAMMD5BindRequest(password, promptForPassword, tool, optionsMap, controls);
        }
        if (mech.equalsIgnoreCase("DIGEST-MD5")) {
            return createDIGESTMD5BindRequest(password, promptForPassword, tool, optionsMap, controls);
        }
        if (mech.equalsIgnoreCase("EXTERNAL")) {
            return createEXTERNALBindRequest(password, optionsMap, controls);
        }
        if (mech.equalsIgnoreCase("GSSAPI")) {
            return createGSSAPIBindRequest(password, promptForPassword, tool, optionsMap, controls);
        }
        if (mech.equalsIgnoreCase("PLAIN")) {
            return createPLAINBindRequest(password, promptForPassword, tool, optionsMap, controls);
        }
        if (mech.equalsIgnoreCase("UNBOUNDID-CERTIFICATE-PLUS-PASSWORD")) {
            return createUnboundIDCertificatePlusPasswordBindRequest(password, tool, optionsMap, controls);
        }
        if (mech.equalsIgnoreCase("UNBOUNDID-DELIVERED-OTP")) {
            return createUNBOUNDIDDeliveredOTPBindRequest(password, optionsMap, controls);
        }
        if (mech.equalsIgnoreCase("UNBOUNDID-TOTP")) {
            return createUNBOUNDIDTOTPBindRequest(password, tool, optionsMap, controls);
        }
        if (mech.equalsIgnoreCase("UNBOUNDID-YUBIKEY-OTP")) {
            return createUNBOUNDIDYUBIKEYOTPBindRequest(password, tool, optionsMap, controls);
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_UNSUPPORTED_MECH.get(mech));
    }
    
    private static ANONYMOUSBindRequest createANONYMOUSBindRequest(final byte[] password, final Map<String, String> options, final Control[] controls) throws LDAPException {
        if (password != null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_MECH_DOESNT_ACCEPT_PASSWORD.get("ANONYMOUS"));
        }
        final String trace = options.remove(StaticUtils.toLowerCase("trace"));
        ensureNoUnsupportedOptions(options, "ANONYMOUS");
        return new ANONYMOUSBindRequest(trace, controls);
    }
    
    private static CRAMMD5BindRequest createCRAMMD5BindRequest(final byte[] password, final boolean promptForPassword, final CommandLineTool tool, final Map<String, String> options, final Control[] controls) throws LDAPException {
        byte[] pw;
        if (password == null) {
            if (!promptForPassword) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_MECH_REQUIRES_PASSWORD.get("CRAM-MD5"));
            }
            tool.getOriginalOut().print(UtilityMessages.INFO_LDAP_TOOL_ENTER_BIND_PASSWORD.get());
            pw = PasswordReader.readPassword();
            tool.getOriginalOut().println();
        }
        else {
            pw = password;
        }
        final String authID = options.remove(StaticUtils.toLowerCase("authID"));
        if (authID == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("authID", "CRAM-MD5"));
        }
        ensureNoUnsupportedOptions(options, "CRAM-MD5");
        return new CRAMMD5BindRequest(authID, pw, controls);
    }
    
    private static DIGESTMD5BindRequest createDIGESTMD5BindRequest(final byte[] password, final boolean promptForPassword, final CommandLineTool tool, final Map<String, String> options, final Control[] controls) throws LDAPException {
        byte[] pw;
        if (password == null) {
            if (!promptForPassword) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_MECH_REQUIRES_PASSWORD.get("CRAM-MD5"));
            }
            tool.getOriginalOut().print(UtilityMessages.INFO_LDAP_TOOL_ENTER_BIND_PASSWORD.get());
            pw = PasswordReader.readPassword();
            tool.getOriginalOut().println();
        }
        else {
            pw = password;
        }
        final String authID = options.remove(StaticUtils.toLowerCase("authID"));
        if (authID == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("authID", "CRAM-MD5"));
        }
        final DIGESTMD5BindRequestProperties properties = new DIGESTMD5BindRequestProperties(authID, pw);
        properties.setAuthorizationID(options.remove(StaticUtils.toLowerCase("authzID")));
        properties.setRealm(options.remove(StaticUtils.toLowerCase("realm")));
        final String qopString = options.remove(StaticUtils.toLowerCase("qop"));
        if (qopString != null) {
            properties.setAllowedQoP(SASLQualityOfProtection.decodeQoPList(qopString));
        }
        ensureNoUnsupportedOptions(options, "DIGEST-MD5");
        return new DIGESTMD5BindRequest(properties, controls);
    }
    
    private static EXTERNALBindRequest createEXTERNALBindRequest(final byte[] password, final Map<String, String> options, final Control[] controls) throws LDAPException {
        if (password != null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_MECH_DOESNT_ACCEPT_PASSWORD.get("EXTERNAL"));
        }
        ensureNoUnsupportedOptions(options, "EXTERNAL");
        return new EXTERNALBindRequest(controls);
    }
    
    private static GSSAPIBindRequest createGSSAPIBindRequest(final byte[] password, final boolean promptForPassword, final CommandLineTool tool, final Map<String, String> options, final Control[] controls) throws LDAPException {
        final String authID = options.remove(StaticUtils.toLowerCase("authID"));
        if (authID == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("authID", "GSSAPI"));
        }
        final GSSAPIBindRequestProperties gssapiProperties = new GSSAPIBindRequestProperties(authID, password);
        gssapiProperties.setAuthorizationID(options.remove(StaticUtils.toLowerCase("authzID")));
        gssapiProperties.setConfigFilePath(options.remove(StaticUtils.toLowerCase("configFile")));
        gssapiProperties.setEnableGSSAPIDebugging(getBooleanValue(options, "debug", false));
        gssapiProperties.setKDCAddress(options.remove(StaticUtils.toLowerCase("kdcAddress")));
        final String protocol = options.remove(StaticUtils.toLowerCase("protocol"));
        if (protocol != null) {
            gssapiProperties.setServicePrincipalProtocol(protocol);
        }
        gssapiProperties.setRealm(options.remove(StaticUtils.toLowerCase("realm")));
        final String qopString = options.remove(StaticUtils.toLowerCase("qop"));
        if (qopString != null) {
            gssapiProperties.setAllowedQoP(SASLQualityOfProtection.decodeQoPList(qopString));
        }
        gssapiProperties.setRenewTGT(getBooleanValue(options, "renewTGT", false));
        gssapiProperties.setRequireCachedCredentials(getBooleanValue(options, "requireCache", false));
        gssapiProperties.setTicketCachePath(options.remove(StaticUtils.toLowerCase("ticketCache")));
        gssapiProperties.setUseTicketCache(getBooleanValue(options, "useTicketCache", true));
        ensureNoUnsupportedOptions(options, "GSSAPI");
        if (password == null && (!gssapiProperties.useTicketCache() || !gssapiProperties.requireCachedCredentials())) {
            if (!promptForPassword) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_GSSAPI_PASSWORD_REQUIRED.get());
            }
            tool.getOriginalOut().print(UtilityMessages.INFO_LDAP_TOOL_ENTER_BIND_PASSWORD.get());
            gssapiProperties.setPassword(PasswordReader.readPassword());
            tool.getOriginalOut().println();
        }
        return new GSSAPIBindRequest(gssapiProperties, controls);
    }
    
    private static PLAINBindRequest createPLAINBindRequest(final byte[] password, final boolean promptForPassword, final CommandLineTool tool, final Map<String, String> options, final Control[] controls) throws LDAPException {
        byte[] pw;
        if (password == null) {
            if (!promptForPassword) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_MECH_REQUIRES_PASSWORD.get("CRAM-MD5"));
            }
            tool.getOriginalOut().print(UtilityMessages.INFO_LDAP_TOOL_ENTER_BIND_PASSWORD.get());
            pw = PasswordReader.readPassword();
            tool.getOriginalOut().println();
        }
        else {
            pw = password;
        }
        final String authID = options.remove(StaticUtils.toLowerCase("authID"));
        if (authID == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("authID", "PLAIN"));
        }
        final String authzID = options.remove(StaticUtils.toLowerCase("authzID"));
        ensureNoUnsupportedOptions(options, "PLAIN");
        return new PLAINBindRequest(authID, authzID, pw, controls);
    }
    
    private static UnboundIDCertificatePlusPasswordBindRequest createUnboundIDCertificatePlusPasswordBindRequest(final byte[] password, final CommandLineTool tool, final Map<String, String> options, final Control[] controls) throws LDAPException {
        byte[] pw;
        if (password == null) {
            tool.getOriginalOut().print(UtilityMessages.INFO_LDAP_TOOL_ENTER_BIND_PASSWORD.get());
            pw = PasswordReader.readPassword();
            tool.getOriginalOut().println();
        }
        else {
            pw = password;
        }
        ensureNoUnsupportedOptions(options, "UNBOUNDID-CERTIFICATE-PLUS-PASSWORD");
        return new UnboundIDCertificatePlusPasswordBindRequest(pw, controls);
    }
    
    private static UnboundIDDeliveredOTPBindRequest createUNBOUNDIDDeliveredOTPBindRequest(final byte[] password, final Map<String, String> options, final Control... controls) throws LDAPException {
        if (password != null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_MECH_DOESNT_ACCEPT_PASSWORD.get("UNBOUNDID-DELIVERED-OTP"));
        }
        final String authID = options.remove(StaticUtils.toLowerCase("authID"));
        if (authID == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("authID", "UNBOUNDID-DELIVERED-OTP"));
        }
        final String otp = options.remove(StaticUtils.toLowerCase("otp"));
        if (otp == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("otp", "UNBOUNDID-DELIVERED-OTP"));
        }
        final String authzID = options.remove(StaticUtils.toLowerCase("authzID"));
        ensureNoUnsupportedOptions(options, "UNBOUNDID-DELIVERED-OTP");
        return new UnboundIDDeliveredOTPBindRequest(authID, authzID, otp, controls);
    }
    
    private static SingleUseTOTPBindRequest createUNBOUNDIDTOTPBindRequest(final byte[] password, final CommandLineTool tool, final Map<String, String> options, final Control... controls) throws LDAPException {
        final String authID = options.remove(StaticUtils.toLowerCase("authID"));
        if (authID == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("authID", "UNBOUNDID-TOTP"));
        }
        final String totpPassword = options.remove(StaticUtils.toLowerCase("totpPassword"));
        if (totpPassword == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("totpPassword", "UNBOUNDID-TOTP"));
        }
        byte[] pwBytes = password;
        final String authzID = options.remove(StaticUtils.toLowerCase("authzID"));
        final String promptStr = options.remove(StaticUtils.toLowerCase("promptForStaticPassword"));
        if (promptStr != null) {
            if (promptStr.equalsIgnoreCase("true")) {
                if (pwBytes != null) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_PROMPT_FOR_PROVIDED_PW.get("promptForStaticPassword"));
                }
                tool.getOriginalOut().print(UtilityMessages.INFO_SASL_ENTER_STATIC_PW.get());
                pwBytes = PasswordReader.readPassword();
                tool.getOriginalOut().println();
            }
            else if (!promptStr.equalsIgnoreCase("false")) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_PROMPT_FOR_STATIC_PW_BAD_VALUE.get("promptForStaticPassword"));
            }
        }
        ensureNoUnsupportedOptions(options, "UNBOUNDID-TOTP");
        return new SingleUseTOTPBindRequest(authID, authzID, totpPassword, pwBytes, controls);
    }
    
    private static UnboundIDYubiKeyOTPBindRequest createUNBOUNDIDYUBIKEYOTPBindRequest(final byte[] password, final CommandLineTool tool, final Map<String, String> options, final Control... controls) throws LDAPException {
        final String authID = options.remove(StaticUtils.toLowerCase("authID"));
        if (authID == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("authID", "UNBOUNDID-YUBIKEY-OTP"));
        }
        final String otp = options.remove(StaticUtils.toLowerCase("otp"));
        if (otp == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_MISSING_REQUIRED_OPTION.get("otp", "UNBOUNDID-YUBIKEY-OTP"));
        }
        final String authzID = options.remove(StaticUtils.toLowerCase("authzID"));
        byte[] pwBytes = password;
        final String promptStr = options.remove(StaticUtils.toLowerCase("promptForStaticPassword"));
        if (promptStr != null) {
            if (promptStr.equalsIgnoreCase("true")) {
                if (pwBytes != null) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_PROMPT_FOR_PROVIDED_PW.get("promptForStaticPassword"));
                }
                tool.getOriginalOut().print(UtilityMessages.INFO_SASL_ENTER_STATIC_PW.get());
                pwBytes = PasswordReader.readPassword();
                tool.getOriginalOut().println();
            }
            else if (!promptStr.equalsIgnoreCase("false")) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_PROMPT_FOR_STATIC_PW_BAD_VALUE.get("promptForStaticPassword"));
            }
        }
        ensureNoUnsupportedOptions(options, "UNBOUNDID-YUBIKEY-OTP");
        return new UnboundIDYubiKeyOTPBindRequest(authID, authzID, pwBytes, otp, controls);
    }
    
    private static Map<String, String> parseOptions(final List<String> options) throws LDAPException {
        if (options == null) {
            return new HashMap<String, String>(0);
        }
        final HashMap<String, String> m = new HashMap<String, String>(StaticUtils.computeMapCapacity(options.size()));
        for (final String s : options) {
            final int equalPos = s.indexOf(61);
            if (equalPos < 0) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_MISSING_EQUAL.get(s));
            }
            if (equalPos == 0) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_STARTS_WITH_EQUAL.get(s));
            }
            final String name = s.substring(0, equalPos);
            final String value = s.substring(equalPos + 1);
            if (m.put(StaticUtils.toLowerCase(name), value) != null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_NOT_MULTI_VALUED.get(name));
            }
        }
        return m;
    }
    
    @InternalUseOnly
    public static void ensureNoUnsupportedOptions(final Map<String, String> options, final String mechanism) throws LDAPException {
        if (!options.isEmpty()) {
            final Iterator i$ = options.keySet().iterator();
            if (i$.hasNext()) {
                final String s = i$.next();
                throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_UNSUPPORTED_FOR_MECH.get(s, mechanism));
            }
        }
    }
    
    static boolean getBooleanValue(final Map<String, String> m, final String o, final boolean d) throws LDAPException {
        final String s = StaticUtils.toLowerCase(m.remove(StaticUtils.toLowerCase(o)));
        if (s == null) {
            return d;
        }
        if (s.equals("true") || s.equals("t") || s.equals("yes") || s.equals("y") || s.equals("on") || s.equals("1")) {
            return true;
        }
        if (s.equals("false") || s.equals("f") || s.equals("no") || s.equals("n") || s.equals("off") || s.equals("0")) {
            return false;
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_SASL_OPTION_MALFORMED_BOOLEAN_VALUE.get(o));
    }
    
    public static String getUsageString(final int maxWidth) {
        final StringBuilder buffer = new StringBuilder();
        for (final String line : getUsage(maxWidth)) {
            buffer.append(line);
            buffer.append(StaticUtils.EOL);
        }
        return buffer.toString();
    }
    
    public static List<String> getUsage(final int maxWidth) {
        final ArrayList<String> lines = new ArrayList<String>(100);
        boolean first = true;
        for (final SASLMechanismInfo i : getSupportedSASLMechanisms()) {
            if (first) {
                first = false;
            }
            else {
                lines.add("");
                lines.add("");
            }
            lines.addAll(StaticUtils.wrapLine(UtilityMessages.INFO_SASL_HELP_MECHANISM.get(i.getName()), maxWidth));
            lines.add("");
            for (final String line : StaticUtils.wrapLine(i.getDescription(), maxWidth - 4)) {
                lines.add("  " + line);
            }
            lines.add("");
            for (final String line : StaticUtils.wrapLine(UtilityMessages.INFO_SASL_HELP_MECHANISM_OPTIONS.get(i.getName()), maxWidth - 4)) {
                lines.add("  " + line);
            }
            if (i.acceptsPassword()) {
                lines.add("");
                if (i.requiresPassword()) {
                    for (final String line : StaticUtils.wrapLine(UtilityMessages.INFO_SASL_HELP_PASSWORD_REQUIRED.get(i.getName()), maxWidth - 4)) {
                        lines.add("  " + line);
                    }
                }
                else {
                    for (final String line : StaticUtils.wrapLine(UtilityMessages.INFO_SASL_HELP_PASSWORD_OPTIONAL.get(i.getName()), maxWidth - 4)) {
                        lines.add("  " + line);
                    }
                }
            }
            for (final SASLOption o : i.getOptions()) {
                lines.add("");
                lines.add("  * " + o.getName());
                for (final String line2 : StaticUtils.wrapLine(o.getDescription(), maxWidth - 14)) {
                    lines.add("       " + line2);
                }
            }
        }
        return lines;
    }
    
    static {
        final TreeMap<String, SASLMechanismInfo> m = new TreeMap<String, SASLMechanismInfo>();
        m.put(StaticUtils.toLowerCase("ANONYMOUS"), new SASLMechanismInfo("ANONYMOUS", UtilityMessages.INFO_SASL_ANONYMOUS_DESCRIPTION.get(), false, false, new SASLOption[] { new SASLOption("trace", UtilityMessages.INFO_SASL_ANONYMOUS_OPTION_TRACE.get(), false, false) }));
        m.put(StaticUtils.toLowerCase("CRAM-MD5"), new SASLMechanismInfo("CRAM-MD5", UtilityMessages.INFO_SASL_CRAM_MD5_DESCRIPTION.get(), true, true, new SASLOption[] { new SASLOption("authID", UtilityMessages.INFO_SASL_CRAM_MD5_OPTION_AUTH_ID.get(), true, false) }));
        m.put(StaticUtils.toLowerCase("DIGEST-MD5"), new SASLMechanismInfo("DIGEST-MD5", UtilityMessages.INFO_SASL_DIGEST_MD5_DESCRIPTION.get(), true, true, new SASLOption[] { new SASLOption("authID", UtilityMessages.INFO_SASL_DIGEST_MD5_OPTION_AUTH_ID.get(), true, false), new SASLOption("authzID", UtilityMessages.INFO_SASL_DIGEST_MD5_OPTION_AUTHZ_ID.get(), false, false), new SASLOption("realm", UtilityMessages.INFO_SASL_DIGEST_MD5_OPTION_REALM.get(), false, false), new SASLOption("qop", UtilityMessages.INFO_SASL_DIGEST_MD5_OPTION_QOP.get(), false, false) }));
        m.put(StaticUtils.toLowerCase("EXTERNAL"), new SASLMechanismInfo("EXTERNAL", UtilityMessages.INFO_SASL_EXTERNAL_DESCRIPTION.get(), false, false, new SASLOption[0]));
        m.put(StaticUtils.toLowerCase("GSSAPI"), new SASLMechanismInfo("GSSAPI", UtilityMessages.INFO_SASL_GSSAPI_DESCRIPTION.get(), true, false, new SASLOption[] { new SASLOption("authID", UtilityMessages.INFO_SASL_GSSAPI_OPTION_AUTH_ID.get(), true, false), new SASLOption("authzID", UtilityMessages.INFO_SASL_GSSAPI_OPTION_AUTHZ_ID.get(), false, false), new SASLOption("configFile", UtilityMessages.INFO_SASL_GSSAPI_OPTION_CONFIG_FILE.get(), false, false), new SASLOption("debug", UtilityMessages.INFO_SASL_GSSAPI_OPTION_DEBUG.get(), false, false), new SASLOption("kdcAddress", UtilityMessages.INFO_SASL_GSSAPI_OPTION_KDC_ADDRESS.get(), false, false), new SASLOption("protocol", UtilityMessages.INFO_SASL_GSSAPI_OPTION_PROTOCOL.get(), false, false), new SASLOption("realm", UtilityMessages.INFO_SASL_GSSAPI_OPTION_REALM.get(), false, false), new SASLOption("qop", UtilityMessages.INFO_SASL_GSSAPI_OPTION_QOP.get(), false, false), new SASLOption("renewTGT", UtilityMessages.INFO_SASL_GSSAPI_OPTION_RENEW_TGT.get(), false, false), new SASLOption("requireCache", UtilityMessages.INFO_SASL_GSSAPI_OPTION_REQUIRE_TICKET_CACHE.get(), false, false), new SASLOption("ticketCache", UtilityMessages.INFO_SASL_GSSAPI_OPTION_TICKET_CACHE.get(), false, false), new SASLOption("useTicketCache", UtilityMessages.INFO_SASL_GSSAPI_OPTION_USE_TICKET_CACHE.get(), false, false) }));
        m.put(StaticUtils.toLowerCase("PLAIN"), new SASLMechanismInfo("PLAIN", UtilityMessages.INFO_SASL_PLAIN_DESCRIPTION.get(), true, true, new SASLOption[] { new SASLOption("authID", UtilityMessages.INFO_SASL_PLAIN_OPTION_AUTH_ID.get(), true, false), new SASLOption("authzID", UtilityMessages.INFO_SASL_PLAIN_OPTION_AUTHZ_ID.get(), false, false) }));
        m.put(StaticUtils.toLowerCase("UNBOUNDID-CERTIFICATE-PLUS-PASSWORD"), new SASLMechanismInfo("UNBOUNDID-CERTIFICATE-PLUS-PASSWORD", UtilityMessages.INFO_SASL_UNBOUNDID_CERT_PLUS_PASSWORD_DESCRIPTION.get(), true, true, new SASLOption[0]));
        m.put(StaticUtils.toLowerCase("UNBOUNDID-DELIVERED-OTP"), new SASLMechanismInfo("UNBOUNDID-DELIVERED-OTP", UtilityMessages.INFO_SASL_UNBOUNDID_DELIVERED_OTP_DESCRIPTION.get(), false, false, new SASLOption[] { new SASLOption("authID", UtilityMessages.INFO_SASL_UNBOUNDID_TOTP_OPTION_AUTH_ID.get(), true, false), new SASLOption("authzID", UtilityMessages.INFO_SASL_UNBOUNDID_TOTP_OPTION_AUTHZ_ID.get(), false, false), new SASLOption("otp", UtilityMessages.INFO_SASL_UNBOUNDID_DELIVERED_OTP_OPTION_OTP.get(), true, false) }));
        m.put(StaticUtils.toLowerCase("UNBOUNDID-TOTP"), new SASLMechanismInfo("UNBOUNDID-TOTP", UtilityMessages.INFO_SASL_UNBOUNDID_TOTP_DESCRIPTION.get(), true, false, new SASLOption[] { new SASLOption("authID", UtilityMessages.INFO_SASL_UNBOUNDID_TOTP_OPTION_AUTH_ID.get(), true, false), new SASLOption("authzID", UtilityMessages.INFO_SASL_UNBOUNDID_TOTP_OPTION_AUTHZ_ID.get(), false, false), new SASLOption("promptForStaticPassword", UtilityMessages.INFO_SASL_UNBOUNDID_TOTP_OPTION_PROMPT_FOR_PW.get(), false, false), new SASLOption("totpPassword", UtilityMessages.INFO_SASL_UNBOUNDID_TOTP_OPTION_TOTP_PASSWORD.get(), true, false) }));
        m.put(StaticUtils.toLowerCase("UNBOUNDID-YUBIKEY-OTP"), new SASLMechanismInfo("UNBOUNDID-YUBIKEY-OTP", UtilityMessages.INFO_SASL_UNBOUNDID_YUBIKEY_OTP_DESCRIPTION.get(), true, false, new SASLOption[] { new SASLOption("authID", UtilityMessages.INFO_SASL_UNBOUNDID_YUBIKEY_OTP_OPTION_AUTH_ID.get(), true, false), new SASLOption("authzID", UtilityMessages.INFO_SASL_UNBOUNDID_YUBIKEY_OTP_OPTION_AUTHZ_ID.get(), false, false), new SASLOption("otp", UtilityMessages.INFO_SASL_UNBOUNDID_YUBIKEY_OTP_OPTION_OTP.get(), true, false), new SASLOption("promptForStaticPassword", UtilityMessages.INFO_SASL_UNBOUNDID_YUBIKEY_OTP_OPTION_PROMPT_FOR_PW.get(), false, false) }));
        SASL_MECHANISMS = Collections.unmodifiableMap((Map<? extends String, ? extends SASLMechanismInfo>)m);
    }
}
