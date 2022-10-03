package com.unboundid.ldap.sdk.unboundidds;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Iterator;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.DeliverOneTimePasswordExtendedResult;
import java.util.List;
import com.unboundid.ldap.sdk.unboundidds.extensions.DeliverOneTimePasswordExtendedRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ObjectPair;
import java.util.ArrayList;
import com.unboundid.util.PasswordReader;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class DeliverOneTimePassword extends LDAPCommandLineTool implements Serializable
{
    private static final long serialVersionUID = -7414730592661321416L;
    private BooleanArgument promptForBindPassword;
    private DNArgument bindDN;
    private FileArgument bindPasswordFile;
    private StringArgument compactTextAfterOTP;
    private StringArgument compactTextBeforeOTP;
    private StringArgument deliveryMechanism;
    private StringArgument fullTextAfterOTP;
    private StringArgument fullTextBeforeOTP;
    private StringArgument messageSubject;
    private StringArgument userName;
    private StringArgument bindPassword;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final DeliverOneTimePassword tool = new DeliverOneTimePassword(outStream, errStream);
        return tool.runTool(args);
    }
    
    public DeliverOneTimePassword(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.promptForBindPassword = null;
        this.bindDN = null;
        this.bindPasswordFile = null;
        this.bindPassword = null;
        this.compactTextAfterOTP = null;
        this.compactTextBeforeOTP = null;
        this.deliveryMechanism = null;
        this.fullTextAfterOTP = null;
        this.fullTextBeforeOTP = null;
        this.messageSubject = null;
        this.userName = null;
    }
    
    @Override
    public String getToolName() {
        return "deliver-one-time-password";
    }
    
    @Override
    public String getToolDescription() {
        return UnboundIDDSMessages.INFO_DELIVER_OTP_TOOL_DESCRIPTION.get();
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        (this.bindDN = new DNArgument('D', "bindDN", false, 1, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_DN.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_BIND_DN.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_ID_AND_AUTH.get());
        this.bindDN.addLongIdentifier("bind-dn", true);
        parser.addArgument(this.bindDN);
        (this.userName = new StringArgument('n', "userName", false, 1, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_USERNAME.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_USERNAME.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_ID_AND_AUTH.get());
        this.userName.addLongIdentifier("user-name", true);
        parser.addArgument(this.userName);
        (this.bindPassword = new StringArgument('w', "bindPassword", false, 1, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_PASSWORD.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_BIND_PW.get())).setSensitive(true);
        this.bindPassword.setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_ID_AND_AUTH.get());
        this.bindPassword.addLongIdentifier("bind-password", true);
        parser.addArgument(this.bindPassword);
        (this.bindPasswordFile = new FileArgument('j', "bindPasswordFile", false, 1, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_PATH.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_BIND_PW_FILE.get(), true, true, true, false)).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_ID_AND_AUTH.get());
        this.bindPasswordFile.addLongIdentifier("bind-password-file", true);
        parser.addArgument(this.bindPasswordFile);
        (this.promptForBindPassword = new BooleanArgument(null, "promptForBindPassword", 1, UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_BIND_PW_PROMPT.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_ID_AND_AUTH.get());
        this.promptForBindPassword.addLongIdentifier("prompt-for-bind-password", true);
        parser.addArgument(this.promptForBindPassword);
        (this.deliveryMechanism = new StringArgument('m', "deliveryMechanism", false, 0, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_NAME.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_MECH.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_DELIVERY_MECH.get());
        this.deliveryMechanism.addLongIdentifier("delivery-mechanism", true);
        parser.addArgument(this.deliveryMechanism);
        (this.messageSubject = new StringArgument('s', "messageSubject", false, 1, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_SUBJECT.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_SUBJECT.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_DELIVERY_MECH.get());
        this.messageSubject.addLongIdentifier("message-subject", true);
        parser.addArgument(this.messageSubject);
        (this.fullTextBeforeOTP = new StringArgument('f', "fullTextBeforeOTP", false, 1, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_FULL_BEFORE.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_FULL_BEFORE.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_DELIVERY_MECH.get());
        this.fullTextBeforeOTP.addLongIdentifier("full-text-before-otp", true);
        parser.addArgument(this.fullTextBeforeOTP);
        (this.fullTextAfterOTP = new StringArgument('F', "fullTextAfterOTP", false, 1, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_FULL_AFTER.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_FULL_AFTER.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_DELIVERY_MECH.get());
        this.fullTextAfterOTP.addLongIdentifier("full-text-after-otp", true);
        parser.addArgument(this.fullTextAfterOTP);
        (this.compactTextBeforeOTP = new StringArgument('c', "compactTextBeforeOTP", false, 1, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_COMPACT_BEFORE.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_COMPACT_BEFORE.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_DELIVERY_MECH.get());
        this.compactTextBeforeOTP.addLongIdentifier("compact-text-before-otp", true);
        parser.addArgument(this.compactTextBeforeOTP);
        (this.compactTextAfterOTP = new StringArgument('C', "compactTextAfterOTP", false, 1, UnboundIDDSMessages.INFO_DELIVER_OTP_PLACEHOLDER_COMPACT_AFTER.get(), UnboundIDDSMessages.INFO_DELIVER_OTP_DESCRIPTION_COMPACT_AFTER.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_OTP_GROUP_DELIVERY_MECH.get());
        this.compactTextAfterOTP.addLongIdentifier("compact-text-after-otp", true);
        parser.addArgument(this.compactTextAfterOTP);
        parser.addRequiredArgumentSet(this.bindDN, this.userName, new Argument[0]);
        parser.addExclusiveArgumentSet(this.bindDN, this.userName, new Argument[0]);
        parser.addExclusiveArgumentSet(this.bindPassword, this.bindPasswordFile, this.promptForBindPassword);
    }
    
    @Override
    protected boolean supportsAuthentication() {
        return false;
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
        String authID;
        if (this.bindDN.isPresent()) {
            authID = "dn:" + this.bindDN.getValue();
        }
        else {
            authID = "u:" + this.userName.getValue();
        }
        String pw = null;
        Label_0225: {
            if (this.bindPassword.isPresent()) {
                pw = this.bindPassword.getValue();
            }
            else {
                if (this.bindPasswordFile.isPresent()) {
                    try {
                        pw = new String(this.getPasswordFileReader().readPassword(this.bindPasswordFile.getValue()));
                        break Label_0225;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        this.err(UnboundIDDSMessages.ERR_DELIVER_OTP_CANNOT_READ_BIND_PW.get(StaticUtils.getExceptionMessage(e)));
                        return ResultCode.LOCAL_ERROR;
                    }
                }
                try {
                    this.getOut().print(UnboundIDDSMessages.INFO_DELIVER_OTP_ENTER_PW.get());
                    pw = StaticUtils.toUTF8String(PasswordReader.readPassword());
                    this.getOut().println();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    this.err(UnboundIDDSMessages.ERR_DELIVER_OTP_CANNOT_READ_BIND_PW.get(StaticUtils.getExceptionMessage(e)));
                    return ResultCode.LOCAL_ERROR;
                }
            }
        }
        ArrayList<ObjectPair<String, String>> preferredDeliveryMechanisms;
        if (this.deliveryMechanism.isPresent()) {
            final List<String> dmList = this.deliveryMechanism.getValues();
            preferredDeliveryMechanisms = new ArrayList<ObjectPair<String, String>>(dmList.size());
            for (final String s : dmList) {
                preferredDeliveryMechanisms.add(new ObjectPair<String, String>(s, null));
            }
        }
        else {
            preferredDeliveryMechanisms = null;
        }
        LDAPConnection conn;
        try {
            conn = this.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.err(UnboundIDDSMessages.ERR_DELIVER_OTP_CANNOT_GET_CONNECTION.get(StaticUtils.getExceptionMessage(le)));
            return le.getResultCode();
        }
        try {
            final DeliverOneTimePasswordExtendedRequest request = new DeliverOneTimePasswordExtendedRequest(authID, pw, this.messageSubject.getValue(), this.fullTextBeforeOTP.getValue(), this.fullTextAfterOTP.getValue(), this.compactTextBeforeOTP.getValue(), this.compactTextAfterOTP.getValue(), preferredDeliveryMechanisms, new Control[0]);
            DeliverOneTimePasswordExtendedResult result;
            try {
                result = (DeliverOneTimePasswordExtendedResult)conn.processExtendedOperation(request);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                this.err(UnboundIDDSMessages.ERR_DELIVER_OTP_ERROR_PROCESSING_EXTOP.get(StaticUtils.getExceptionMessage(le2)));
                return le2.getResultCode();
            }
            if (result.getResultCode() == ResultCode.SUCCESS) {
                final String mechanism = result.getDeliveryMechanism();
                final String id = result.getRecipientID();
                if (id == null) {
                    this.out(UnboundIDDSMessages.INFO_DELIVER_OTP_SUCCESS_RESULT_WITHOUT_ID.get(mechanism));
                }
                else {
                    this.out(UnboundIDDSMessages.INFO_DELIVER_OTP_SUCCESS_RESULT_WITH_ID.get(mechanism, id));
                }
                final String message = result.getDeliveryMessage();
                if (message != null) {
                    this.out(UnboundIDDSMessages.INFO_DELIVER_OTP_SUCCESS_MESSAGE.get(message));
                }
            }
            else if (result.getDiagnosticMessage() == null) {
                this.err(UnboundIDDSMessages.ERR_DELIVER_OTP_ERROR_RESULT_NO_MESSAGE.get(String.valueOf(result.getResultCode())));
            }
            else {
                this.err(UnboundIDDSMessages.ERR_DELIVER_OTP_ERROR_RESULT.get(String.valueOf(result.getResultCode()), result.getDiagnosticMessage()));
            }
            return result.getResultCode();
        }
        finally {
            conn.close();
        }
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleMap = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=test.user,ou=People,dc=example,dc=com", "--bindPassword", "password", "--messageSubject", "Your one-time password", "--fullTextBeforeOTP", "Your one-time password is '", "--fullTextAfterOTP", "'.", "--compactTextBeforeOTP", "Your OTP is '", "--compactTextAfterOTP", "'." };
        exampleMap.put(args, UnboundIDDSMessages.INFO_DELIVER_OTP_EXAMPLE_1.get());
        args = new String[] { "--hostname", "server.example.com", "--port", "389", "--userName", "test.user", "--bindPassword", "password", "--deliveryMechanism", "SMS", "--deliveryMechanism", "E-Mail", "--messageSubject", "Your one-time password", "--fullTextBeforeOTP", "Your one-time password is '", "--fullTextAfterOTP", "'.", "--compactTextBeforeOTP", "Your OTP is '", "--compactTextAfterOTP", "'." };
        exampleMap.put(args, UnboundIDDSMessages.INFO_DELIVER_OTP_EXAMPLE_2.get());
        return exampleMap;
    }
}
