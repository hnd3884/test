package com.unboundid.ldap.sdk.unboundidds;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.unboundidds.extensions.RegisterYubiKeyOTPDeviceExtendedRequest;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.DeregisterYubiKeyOTPDeviceExtendedRequest;
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
import java.io.Serializable;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class RegisterYubiKeyOTPDevice extends LDAPCommandLineTool implements Serializable
{
    private static final long serialVersionUID = 5705120716566064832L;
    private BooleanArgument deregister;
    private BooleanArgument promptForUserPassword;
    private FileArgument userPasswordFile;
    private StringArgument authenticationID;
    private StringArgument userPassword;
    private StringArgument otp;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final RegisterYubiKeyOTPDevice tool = new RegisterYubiKeyOTPDevice(outStream, errStream);
        return tool.runTool(args);
    }
    
    public RegisterYubiKeyOTPDevice(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.deregister = null;
        this.otp = null;
        this.promptForUserPassword = null;
        this.userPasswordFile = null;
        this.authenticationID = null;
        this.userPassword = null;
    }
    
    @Override
    public String getToolName() {
        return "register-yubikey-otp-device";
    }
    
    @Override
    public String getToolDescription() {
        return UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_TOOL_DESCRIPTION.get("UNBOUNDID-YUBIKEY-OTP");
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        (this.deregister = new BooleanArgument(null, "deregister", 1, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_DEREGISTER.get("--otp"))).addLongIdentifier("de-register", true);
        parser.addArgument(this.deregister);
        parser.addArgument(this.otp = new StringArgument(null, "otp", false, 1, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_PLACEHOLDER_OTP.get(), UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_OTP.get()));
        (this.authenticationID = new StringArgument(null, "authID", false, 1, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_PLACEHOLDER_AUTHID.get(), UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_AUTHID.get())).addLongIdentifier("authenticationID", true);
        this.authenticationID.addLongIdentifier("auth-id", true);
        this.authenticationID.addLongIdentifier("authentication-id", true);
        parser.addArgument(this.authenticationID);
        (this.userPassword = new StringArgument(null, "userPassword", false, 1, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_PLACEHOLDER_USER_PW.get(), UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_USER_PW.get(this.authenticationID.getIdentifierString()))).setSensitive(true);
        this.userPassword.addLongIdentifier("user-password", true);
        parser.addArgument(this.userPassword);
        (this.userPasswordFile = new FileArgument(null, "userPasswordFile", false, 1, null, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_USER_PW_FILE.get(this.authenticationID.getIdentifierString()), true, true, true, false)).addLongIdentifier("user-password-file", true);
        parser.addArgument(this.userPasswordFile);
        (this.promptForUserPassword = new BooleanArgument(null, "promptForUserPassword", UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_PROMPT_FOR_USER_PW.get(this.authenticationID.getIdentifierString()))).addLongIdentifier("prompt-for-user-password", true);
        parser.addArgument(this.promptForUserPassword);
        parser.addExclusiveArgumentSet(this.userPassword, this.userPasswordFile, this.promptForUserPassword);
        parser.addDependentArgumentSet(this.userPassword, this.authenticationID, new Argument[0]);
        parser.addDependentArgumentSet(this.userPasswordFile, this.authenticationID, new Argument[0]);
        parser.addDependentArgumentSet(this.promptForUserPassword, this.authenticationID, new Argument[0]);
    }
    
    @Override
    public void doExtendedNonLDAPArgumentValidation() throws ArgumentException {
        if (!this.deregister.isPresent() && !this.otp.isPresent()) {
            throw new ArgumentException(UnboundIDDSMessages.ERR_REGISTER_YUBIKEY_OTP_DEVICE_NO_OTP_TO_REGISTER.get(this.otp.getIdentifierString()));
        }
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
    protected boolean supportsOutputFile() {
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
    protected boolean logToolInvocationByDefault() {
        return true;
    }
    
    @Override
    public ResultCode doToolProcessing() {
        LDAPConnection conn;
        try {
            conn = this.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, UnboundIDDSMessages.ERR_REGISTER_YUBIKEY_OTP_DEVICE_CANNOT_CONNECT.get(StaticUtils.getExceptionMessage(le)));
            return le.getResultCode();
        }
        try {
            final String authID = this.authenticationID.getValue();
            byte[] staticPassword = null;
            Label_0268: {
                if (this.userPassword.isPresent()) {
                    staticPassword = StaticUtils.getBytes(this.userPassword.getValue());
                }
                else {
                    if (this.userPasswordFile.isPresent()) {
                        try {
                            final char[] pwChars = this.getPasswordFileReader().readPassword(this.userPasswordFile.getValue());
                            staticPassword = StaticUtils.getBytes(new String(pwChars));
                            break Label_0268;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, UnboundIDDSMessages.ERR_REGISTER_YUBIKEY_OTP_DEVICE_CANNOT_READ_PW.get(StaticUtils.getExceptionMessage(e)));
                            return ResultCode.LOCAL_ERROR;
                        }
                    }
                    if (this.promptForUserPassword.isPresent()) {
                        try {
                            this.getOut().print(UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_ENTER_PW.get(authID));
                            staticPassword = PasswordReader.readPassword();
                            break Label_0268;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, UnboundIDDSMessages.ERR_REGISTER_YUBIKEY_OTP_DEVICE_CANNOT_READ_PW.get(StaticUtils.getExceptionMessage(e)));
                            return ResultCode.LOCAL_ERROR;
                        }
                    }
                    staticPassword = null;
                }
            }
            if (this.deregister.isPresent()) {
                final DeregisterYubiKeyOTPDeviceExtendedRequest r = new DeregisterYubiKeyOTPDeviceExtendedRequest(authID, staticPassword, this.otp.getValue(), new Control[0]);
                ExtendedResult deregisterResult;
                try {
                    deregisterResult = conn.processExtendedOperation(r);
                }
                catch (final LDAPException le2) {
                    deregisterResult = new ExtendedResult(le2);
                }
                if (deregisterResult.getResultCode() == ResultCode.SUCCESS) {
                    if (this.otp.isPresent()) {
                        this.wrapOut(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_DEREGISTER_SUCCESS_ONE.get(authID));
                    }
                    else {
                        this.wrapOut(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_DEREGISTER_SUCCESS_ALL.get(authID));
                    }
                    return ResultCode.SUCCESS;
                }
                this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, UnboundIDDSMessages.ERR_REGISTER_YUBIKEY_OTP_DEVICE_DEREGISTER_FAILED.get(authID, String.valueOf(deregisterResult)));
                return deregisterResult.getResultCode();
            }
            else {
                final RegisterYubiKeyOTPDeviceExtendedRequest r2 = new RegisterYubiKeyOTPDeviceExtendedRequest(authID, staticPassword, this.otp.getValue(), new Control[0]);
                ExtendedResult registerResult;
                try {
                    registerResult = conn.processExtendedOperation(r2);
                }
                catch (final LDAPException le2) {
                    registerResult = new ExtendedResult(le2);
                }
                if (registerResult.getResultCode() == ResultCode.SUCCESS) {
                    this.wrapOut(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_REGISTER_SUCCESS.get(authID));
                    return ResultCode.SUCCESS;
                }
                this.wrapErr(0, StaticUtils.TERMINAL_WIDTH_COLUMNS, UnboundIDDSMessages.ERR_REGISTER_YUBIKEY_OTP_DEVICE_REGISTER_FAILED.get(authID, String.valueOf(registerResult)));
                return registerResult.getResultCode();
            }
        }
        finally {
            conn.close();
        }
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleMap = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "adminPassword", "--authenticationID", "u:test.user", "--userPassword", "testUserPassword", "--otp", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqr" };
        exampleMap.put(args, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_EXAMPLE_REGISTER.get());
        args = new String[] { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "adminPassword", "--deregister", "--authenticationID", "dn:uid=test.user,ou=People,dc=example,dc=com" };
        exampleMap.put(args, UnboundIDDSMessages.INFO_REGISTER_YUBIKEY_OTP_DEVICE_EXAMPLE_DEREGISTER.get());
        return exampleMap;
    }
}
