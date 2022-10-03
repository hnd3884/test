package com.unboundid.ldap.sdk.unboundidds.tools;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.unboundidds.extensions.GenerateTOTPSharedSecretExtendedResult;
import com.unboundid.ldap.sdk.unboundidds.extensions.GenerateTOTPSharedSecretExtendedRequest;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.RevokeTOTPSharedSecretExtendedRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.PasswordReader;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class GenerateTOTPSharedSecret extends LDAPCommandLineTool
{
    private BooleanArgument promptForUserPassword;
    private BooleanArgument revokeAll;
    private FileArgument userPasswordFile;
    private StringArgument authenticationID;
    private StringArgument revoke;
    private StringArgument userPassword;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final OutputStream out, final OutputStream err, final String... args) {
        final GenerateTOTPSharedSecret tool = new GenerateTOTPSharedSecret(out, err);
        return tool.runTool(args);
    }
    
    public GenerateTOTPSharedSecret(final OutputStream out, final OutputStream err) {
        super(out, err);
        this.promptForUserPassword = null;
        this.revokeAll = null;
        this.userPasswordFile = null;
        this.authenticationID = null;
        this.revoke = null;
        this.userPassword = null;
    }
    
    @Override
    public String getToolName() {
        return "generate-totp-shared-secret";
    }
    
    @Override
    public String getToolDescription() {
        return ToolMessages.INFO_GEN_TOTP_SECRET_TOOL_DESC.get();
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
        return true;
    }
    
    @Override
    protected boolean supportsAuthentication() {
        return true;
    }
    
    @Override
    protected boolean defaultToPromptForBindPassword() {
        return true;
    }
    
    @Override
    protected boolean supportsSASLHelp() {
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
    protected boolean logToolInvocationByDefault() {
        return true;
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        (this.authenticationID = new StringArgument(null, "authID", true, 1, ToolMessages.INFO_GEN_TOTP_SECRET_PLACEHOLDER_AUTH_ID.get(), ToolMessages.INFO_GEN_TOTP_SECRET_DESCRIPTION_AUTH_ID.get())).addLongIdentifier("authenticationID", true);
        this.authenticationID.addLongIdentifier("auth-id", true);
        this.authenticationID.addLongIdentifier("authentication-id", true);
        parser.addArgument(this.authenticationID);
        (this.userPassword = new StringArgument(null, "userPassword", false, 1, ToolMessages.INFO_GEN_TOTP_SECRET_PLACEHOLDER_USER_PW.get(), ToolMessages.INFO_GEN_TOTP_SECRET_DESCRIPTION_USER_PW.get(this.authenticationID.getIdentifierString()))).setSensitive(true);
        this.userPassword.addLongIdentifier("user-password", true);
        parser.addArgument(this.userPassword);
        (this.userPasswordFile = new FileArgument(null, "userPasswordFile", false, 1, null, ToolMessages.INFO_GEN_TOTP_SECRET_DESCRIPTION_USER_PW_FILE.get(this.authenticationID.getIdentifierString()), true, true, true, false)).addLongIdentifier("user-password-file", true);
        parser.addArgument(this.userPasswordFile);
        (this.promptForUserPassword = new BooleanArgument(null, "promptForUserPassword", ToolMessages.INFO_GEN_TOTP_SECRET_DESCRIPTION_PROMPT_FOR_USER_PW.get(this.authenticationID.getIdentifierString()))).addLongIdentifier("prompt-for-user-password", true);
        parser.addArgument(this.promptForUserPassword);
        parser.addArgument(this.revoke = new StringArgument(null, "revoke", false, 1, ToolMessages.INFO_GEN_TOTP_SECRET_PLACEHOLDER_SECRET.get(), ToolMessages.INFO_GEN_TOTP_SECRET_DESCRIPTION_REVOKE.get()));
        (this.revokeAll = new BooleanArgument(null, "revokeAll", 1, ToolMessages.INFO_GEN_TOTP_SECRET_DESCRIPTION_REVOKE_ALL.get())).addLongIdentifier("revoke-all", true);
        parser.addArgument(this.revokeAll);
        parser.addExclusiveArgumentSet(this.userPassword, this.userPasswordFile, this.promptForUserPassword);
        parser.addDependentArgumentSet(this.userPassword, this.authenticationID, new Argument[0]);
        parser.addDependentArgumentSet(this.userPasswordFile, this.authenticationID, new Argument[0]);
        parser.addDependentArgumentSet(this.promptForUserPassword, this.authenticationID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.revoke, this.revokeAll, new Argument[0]);
    }
    
    @Override
    public ResultCode doToolProcessing() {
        LDAPConnection conn;
        try {
            conn = this.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_CANNOT_CONNECT.get(StaticUtils.getExceptionMessage(le)));
            return le.getResultCode();
        }
        try {
            final String authID = this.authenticationID.getValue();
            byte[] staticPassword = null;
            Label_0281: {
                if (this.userPassword.isPresent()) {
                    staticPassword = StaticUtils.getBytes(this.userPassword.getValue());
                }
                else {
                    if (this.userPasswordFile.isPresent()) {
                        try {
                            final char[] pwChars = this.getPasswordFileReader().readPassword(this.userPasswordFile.getValue());
                            staticPassword = StaticUtils.getBytes(new String(pwChars));
                            break Label_0281;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_CANNOT_READ_PW_FROM_FILE.get(this.userPasswordFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e)));
                            return ResultCode.LOCAL_ERROR;
                        }
                    }
                    if (this.promptForUserPassword.isPresent()) {
                        try {
                            this.getOut().print(ToolMessages.INFO_GEN_TOTP_SECRET_ENTER_PW.get(authID));
                            staticPassword = PasswordReader.readPassword();
                            break Label_0281;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_CANNOT_READ_PW_FROM_STDIN.get(StaticUtils.getExceptionMessage(e)));
                            return ResultCode.LOCAL_ERROR;
                        }
                    }
                    staticPassword = null;
                }
            }
            ExtendedResult result;
            if (this.revoke.isPresent()) {
                final RevokeTOTPSharedSecretExtendedRequest request = new RevokeTOTPSharedSecretExtendedRequest(authID, staticPassword, this.revoke.getValue(), new Control[0]);
                try {
                    result = conn.processExtendedOperation(request);
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    result = new ExtendedResult(le2);
                }
                if (result.getResultCode() == ResultCode.SUCCESS) {
                    this.wrapOut(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.INFO_GEN_TOTP_SECRET_REVOKE_SUCCESS.get(this.revoke.getValue()));
                }
                else {
                    this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_REVOKE_FAILURE.get(this.revoke.getValue()));
                }
            }
            else if (this.revokeAll.isPresent()) {
                final RevokeTOTPSharedSecretExtendedRequest request = new RevokeTOTPSharedSecretExtendedRequest(authID, staticPassword, null, new Control[0]);
                try {
                    result = conn.processExtendedOperation(request);
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    result = new ExtendedResult(le2);
                }
                if (result.getResultCode() == ResultCode.SUCCESS) {
                    this.wrapOut(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.INFO_GEN_TOTP_SECRET_REVOKE_ALL_SUCCESS.get());
                }
                else {
                    this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_REVOKE_ALL_FAILURE.get());
                }
            }
            else {
                final GenerateTOTPSharedSecretExtendedRequest request2 = new GenerateTOTPSharedSecretExtendedRequest(authID, staticPassword, new Control[0]);
                try {
                    result = conn.processExtendedOperation(request2);
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    result = new ExtendedResult(le2);
                }
                if (result.getResultCode() == ResultCode.SUCCESS) {
                    this.wrapOut(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.INFO_GEN_TOTP_SECRET_GEN_SUCCESS.get(((GenerateTOTPSharedSecretExtendedResult)result).getTOTPSharedSecret()));
                }
                else {
                    this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_GEN_FAILURE.get());
                }
            }
            if (result.getResultCode() != ResultCode.SUCCESS) {
                this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_RESULT_CODE.get(String.valueOf(result.getResultCode())));
                final String diagnosticMessage = result.getDiagnosticMessage();
                if (diagnosticMessage != null) {
                    this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_DIAGNOSTIC_MESSAGE.get(diagnosticMessage));
                }
                final String matchedDN = result.getMatchedDN();
                if (matchedDN != null) {
                    this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_MATCHED_DN.get(matchedDN));
                }
                for (final String referralURL : result.getReferralURLs()) {
                    this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, ToolMessages.ERR_GEN_TOTP_SECRET_REFERRAL_URL.get(referralURL));
                }
            }
            return result.getResultCode();
        }
        finally {
            conn.close();
        }
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        examples.put(new String[] { "--hostname", "ds.example.com", "--port", "389", "--authID", "u:john.doe", "--promptForUserPassword" }, ToolMessages.INFO_GEN_TOTP_SECRET_GEN_EXAMPLE.get());
        examples.put(new String[] { "--hostname", "ds.example.com", "--port", "389", "--authID", "u:john.doe", "--userPasswordFile", "password.txt", "--revokeAll" }, ToolMessages.INFO_GEN_TOTP_SECRET_REVOKE_ALL_EXAMPLE.get());
        return examples;
    }
}
