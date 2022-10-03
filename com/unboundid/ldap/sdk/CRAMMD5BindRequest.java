package com.unboundid.ldap.sdk;

import com.unboundid.util.InternalUseOnly;
import java.util.logging.Level;
import com.unboundid.util.DebugType;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.sasl.SaslClient;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Map;
import javax.security.sasl.Sasl;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import javax.security.auth.callback.CallbackHandler;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class CRAMMD5BindRequest extends SASLBindRequest implements CallbackHandler
{
    public static final String CRAMMD5_MECHANISM_NAME = "CRAM-MD5";
    private static final long serialVersionUID = -4556570436768136483L;
    private final ASN1OctetString password;
    private int messageID;
    private final List<String> unhandledCallbackMessages;
    private final String authenticationID;
    
    public CRAMMD5BindRequest(final String authenticationID, final String password) {
        this(authenticationID, new ASN1OctetString(password), CRAMMD5BindRequest.NO_CONTROLS);
        Validator.ensureNotNull(password);
    }
    
    public CRAMMD5BindRequest(final String authenticationID, final byte[] password) {
        this(authenticationID, new ASN1OctetString(password), CRAMMD5BindRequest.NO_CONTROLS);
        Validator.ensureNotNull(password);
    }
    
    public CRAMMD5BindRequest(final String authenticationID, final ASN1OctetString password) {
        this(authenticationID, password, CRAMMD5BindRequest.NO_CONTROLS);
    }
    
    public CRAMMD5BindRequest(final String authenticationID, final String password, final Control... controls) {
        this(authenticationID, new ASN1OctetString(password), controls);
        Validator.ensureNotNull(password);
    }
    
    public CRAMMD5BindRequest(final String authenticationID, final byte[] password, final Control... controls) {
        this(authenticationID, new ASN1OctetString(password), controls);
        Validator.ensureNotNull(password);
    }
    
    public CRAMMD5BindRequest(final String authenticationID, final ASN1OctetString password, final Control... controls) {
        super(controls);
        this.messageID = -1;
        Validator.ensureNotNull(authenticationID, password);
        this.authenticationID = authenticationID;
        this.password = password;
        this.unhandledCallbackMessages = new ArrayList<String>(5);
    }
    
    @Override
    public String getSASLMechanismName() {
        return "CRAM-MD5";
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public String getPasswordString() {
        return this.password.stringValue();
    }
    
    public byte[] getPasswordBytes() {
        return this.password.getValue();
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        this.unhandledCallbackMessages.clear();
        SaslClient saslClient;
        try {
            final String[] mechanisms = { "CRAM-MD5" };
            saslClient = Sasl.createSaslClient(mechanisms, null, "ldap", connection.getConnectedAddress(), null, this);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_CRAMMD5_CANNOT_CREATE_SASL_CLIENT.get(StaticUtils.getExceptionMessage(e)), e);
        }
        final SASLHelper helper = new SASLHelper(this, connection, "CRAM-MD5", saslClient, this.getControls(), this.getResponseTimeoutMillis(connection), this.unhandledCallbackMessages);
        try {
            return helper.processSASLBind();
        }
        finally {
            this.messageID = helper.getMessageID();
        }
    }
    
    @Override
    public CRAMMD5BindRequest getRebindRequest(final String host, final int port) {
        return new CRAMMD5BindRequest(this.authenticationID, this.password, this.getControls());
    }
    
    @InternalUseOnly
    @Override
    public void handle(final Callback[] callbacks) {
        for (final Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback)callback).setName(this.authenticationID);
            }
            else if (callback instanceof PasswordCallback) {
                ((PasswordCallback)callback).setPassword(this.password.stringValue().toCharArray());
            }
            else {
                if (Debug.debugEnabled(DebugType.LDAP)) {
                    Debug.debug(Level.WARNING, DebugType.LDAP, "Unexpected CRAM-MD5 SASL callback of type " + callback.getClass().getName());
                }
                this.unhandledCallbackMessages.add(LDAPMessages.ERR_CRAMMD5_UNEXPECTED_CALLBACK.get(callback.getClass().getName()));
            }
        }
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public CRAMMD5BindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public CRAMMD5BindRequest duplicate(final Control[] controls) {
        final CRAMMD5BindRequest bindRequest = new CRAMMD5BindRequest(this.authenticationID, this.password, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("CRAMMD5BindRequest(authenticationID='");
        buffer.append(this.authenticationID);
        buffer.append('\'');
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
    
    @Override
    public void toCode(final List<String> lineList, final String requestID, final int indentSpaces, final boolean includeProcessing) {
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(3);
        constructorArgs.add(ToCodeArgHelper.createString(this.authenticationID, "Authentication ID"));
        constructorArgs.add(ToCodeArgHelper.createString("---redacted-password---", "Bind Password"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "CRAMMD5BindRequest", requestID + "Request", "new CRAMMD5BindRequest", constructorArgs);
        if (includeProcessing) {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < indentSpaces; ++i) {
                buffer.append(' ');
            }
            final String indent = buffer.toString();
            lineList.add("");
            lineList.add(indent + "try");
            lineList.add(indent + '{');
            lineList.add(indent + "  BindResult " + requestID + "Result = connection.bind(" + requestID + "Request);");
            lineList.add(indent + "  // The bind was processed successfully.");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (LDAPException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The bind failed.  Maybe the following will " + "help explain why.");
            lineList.add(indent + "  // Note that the connection is now likely in " + "an unauthenticated state.");
            lineList.add(indent + "  ResultCode resultCode = e.getResultCode();");
            lineList.add(indent + "  String message = e.getMessage();");
            lineList.add(indent + "  String matchedDN = e.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = e.getReferralURLs();");
            lineList.add(indent + "  Control[] responseControls = " + "e.getResponseControls();");
            lineList.add(indent + '}');
        }
    }
}
