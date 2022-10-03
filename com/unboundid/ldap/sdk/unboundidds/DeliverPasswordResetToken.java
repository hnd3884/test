package com.unboundid.ldap.sdk.unboundidds;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Iterator;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.DeliverPasswordResetTokenExtendedResult;
import java.util.List;
import com.unboundid.ldap.sdk.unboundidds.extensions.DeliverPasswordResetTokenExtendedRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.ObjectPair;
import java.util.ArrayList;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class DeliverPasswordResetToken extends LDAPCommandLineTool implements Serializable
{
    private static final long serialVersionUID = 5793619963770997266L;
    private DNArgument userDN;
    private StringArgument compactTextAfterToken;
    private StringArgument compactTextBeforeToken;
    private StringArgument deliveryMechanism;
    private StringArgument fullTextAfterToken;
    private StringArgument fullTextBeforeToken;
    private StringArgument messageSubject;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final DeliverPasswordResetToken tool = new DeliverPasswordResetToken(outStream, errStream);
        return tool.runTool(args);
    }
    
    public DeliverPasswordResetToken(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.userDN = null;
        this.compactTextAfterToken = null;
        this.compactTextBeforeToken = null;
        this.deliveryMechanism = null;
        this.fullTextAfterToken = null;
        this.fullTextBeforeToken = null;
        this.messageSubject = null;
    }
    
    @Override
    public String getToolName() {
        return "deliver-password-reset-token";
    }
    
    @Override
    public String getToolDescription() {
        return UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_TOOL_DESCRIPTION.get();
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        (this.userDN = new DNArgument('b', "userDN", true, 1, UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_DN.get(), UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_USER_DN.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_GROUP_ID.get());
        this.userDN.addLongIdentifier("user-dn", true);
        parser.addArgument(this.userDN);
        (this.deliveryMechanism = new StringArgument('m', "deliveryMechanism", false, 0, UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_NAME.get(), UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_MECH.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_GROUP_DELIVERY_MECH.get());
        this.deliveryMechanism.addLongIdentifier("delivery-mechanism", true);
        parser.addArgument(this.deliveryMechanism);
        (this.messageSubject = new StringArgument('s', "messageSubject", false, 1, UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_SUBJECT.get(), UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_SUBJECT.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_GROUP_DELIVERY_MECH.get());
        this.messageSubject.addLongIdentifier("message-subject", true);
        parser.addArgument(this.messageSubject);
        (this.fullTextBeforeToken = new StringArgument('f', "fullTextBeforeToken", false, 1, UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_FULL_BEFORE.get(), UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_FULL_BEFORE.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_GROUP_DELIVERY_MECH.get());
        this.fullTextBeforeToken.addLongIdentifier("full-text-before-token", true);
        parser.addArgument(this.fullTextBeforeToken);
        (this.fullTextAfterToken = new StringArgument('F', "fullTextAfterToken", false, 1, UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_FULL_AFTER.get(), UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_FULL_AFTER.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_GROUP_DELIVERY_MECH.get());
        this.fullTextAfterToken.addLongIdentifier("full-text-after-token", true);
        parser.addArgument(this.fullTextAfterToken);
        (this.compactTextBeforeToken = new StringArgument('c', "compactTextBeforeToken", false, 1, UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_COMPACT_BEFORE.get(), UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_COMPACT_BEFORE.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_GROUP_DELIVERY_MECH.get());
        this.compactTextBeforeToken.addLongIdentifier("compact-text-before-token", true);
        parser.addArgument(this.compactTextBeforeToken);
        (this.compactTextAfterToken = new StringArgument('C', "compactTextAfterToken", false, 1, UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_COMPACT_AFTER.get(), UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_COMPACT_AFTER.get())).setArgumentGroupName(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_GROUP_DELIVERY_MECH.get());
        this.compactTextAfterToken.addLongIdentifier("compact-text-after-token", true);
        parser.addArgument(this.compactTextAfterToken);
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
            this.err(UnboundIDDSMessages.ERR_DELIVER_PW_RESET_TOKEN_CANNOT_GET_CONNECTION.get(StaticUtils.getExceptionMessage(le)));
            return le.getResultCode();
        }
        try {
            final DeliverPasswordResetTokenExtendedRequest request = new DeliverPasswordResetTokenExtendedRequest(this.userDN.getStringValue(), this.messageSubject.getValue(), this.fullTextBeforeToken.getValue(), this.fullTextAfterToken.getValue(), this.compactTextBeforeToken.getValue(), this.compactTextAfterToken.getValue(), preferredDeliveryMechanisms, new Control[0]);
            DeliverPasswordResetTokenExtendedResult result;
            try {
                result = (DeliverPasswordResetTokenExtendedResult)conn.processExtendedOperation(request);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                this.err(UnboundIDDSMessages.ERR_DELIVER_PW_RESET_TOKEN_ERROR_PROCESSING_EXTOP.get(StaticUtils.getExceptionMessage(le2)));
                return le2.getResultCode();
            }
            if (result.getResultCode() == ResultCode.SUCCESS) {
                final String mechanism = result.getDeliveryMechanism();
                final String id = result.getRecipientID();
                if (id == null) {
                    this.out(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_SUCCESS_RESULT_WITHOUT_ID.get(mechanism));
                }
                else {
                    this.out(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_SUCCESS_RESULT_WITH_ID.get(mechanism, id));
                }
                final String message = result.getDeliveryMessage();
                if (message != null) {
                    this.out(UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_SUCCESS_MESSAGE.get(message));
                }
            }
            else if (result.getDiagnosticMessage() == null) {
                this.err(UnboundIDDSMessages.ERR_DELIVER_PW_RESET_TOKEN_ERROR_RESULT_NO_MESSAGE.get(String.valueOf(result.getResultCode())));
            }
            else {
                this.err(UnboundIDDSMessages.ERR_DELIVER_PW_RESET_TOKEN_ERROR_RESULT.get(String.valueOf(result.getResultCode()), result.getDiagnosticMessage()));
            }
            return result.getResultCode();
        }
        finally {
            conn.close();
        }
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleMap = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=password.admin,ou=People,dc=example,dc=com", "--bindPassword", "password", "--userDN", "uid=test.user,ou=People,dc=example,dc=com", "--deliveryMechanism", "SMS", "--deliveryMechanism", "E-Mail", "--messageSubject", "Your password reset token", "--fullTextBeforeToken", "Your single-use password reset token is '", "--fullTextAfterToken", "'.", "--compactTextBeforeToken", "Your single-use password reset token is '", "--compactTextAfterToken", "'." };
        exampleMap.put(args, UnboundIDDSMessages.INFO_DELIVER_PW_RESET_TOKEN_EXAMPLE.get());
        return exampleMap;
    }
}
