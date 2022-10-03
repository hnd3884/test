package com.unboundid.ldap.sdk;

import java.util.Iterator;
import com.unboundid.util.InternalUseOnly;
import java.util.logging.Level;
import com.unboundid.util.DebugType;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.sasl.SaslClient;
import com.unboundid.util.Debug;
import java.util.Map;
import javax.security.sasl.Sasl;
import java.util.HashMap;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.util.Validator;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import javax.security.auth.callback.CallbackHandler;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class DIGESTMD5BindRequest extends SASLBindRequest implements CallbackHandler
{
    public static final String DIGESTMD5_MECHANISM_NAME = "DIGEST-MD5";
    private static final long serialVersionUID = 867592367640540593L;
    private final ASN1OctetString password;
    private int messageID;
    private final List<SASLQualityOfProtection> allowedQoP;
    private final List<String> unhandledCallbackMessages;
    private final String authenticationID;
    private final String authorizationID;
    private final String realm;
    
    public DIGESTMD5BindRequest(final String authenticationID, final String password) {
        this(authenticationID, null, new ASN1OctetString(password), null, DIGESTMD5BindRequest.NO_CONTROLS);
        Validator.ensureNotNull(password);
    }
    
    public DIGESTMD5BindRequest(final String authenticationID, final byte[] password) {
        this(authenticationID, null, new ASN1OctetString(password), null, DIGESTMD5BindRequest.NO_CONTROLS);
        Validator.ensureNotNull(password);
    }
    
    public DIGESTMD5BindRequest(final String authenticationID, final ASN1OctetString password) {
        this(authenticationID, null, password, null, DIGESTMD5BindRequest.NO_CONTROLS);
    }
    
    public DIGESTMD5BindRequest(final String authenticationID, final String authorizationID, final String password, final String realm, final Control... controls) {
        this(authenticationID, authorizationID, new ASN1OctetString(password), realm, controls);
        Validator.ensureNotNull(password);
    }
    
    public DIGESTMD5BindRequest(final String authenticationID, final String authorizationID, final byte[] password, final String realm, final Control... controls) {
        this(authenticationID, authorizationID, new ASN1OctetString(password), realm, controls);
        Validator.ensureNotNull(password);
    }
    
    public DIGESTMD5BindRequest(final String authenticationID, final String authorizationID, final ASN1OctetString password, final String realm, final Control... controls) {
        super(controls);
        this.messageID = -1;
        Validator.ensureNotNull(authenticationID, password);
        this.authenticationID = authenticationID;
        this.authorizationID = authorizationID;
        this.password = password;
        this.realm = realm;
        this.allowedQoP = Collections.singletonList(SASLQualityOfProtection.AUTH);
        this.unhandledCallbackMessages = new ArrayList<String>(5);
    }
    
    public DIGESTMD5BindRequest(final DIGESTMD5BindRequestProperties properties, final Control... controls) {
        super(controls);
        this.messageID = -1;
        Validator.ensureNotNull(properties);
        this.authenticationID = properties.getAuthenticationID();
        this.authorizationID = properties.getAuthorizationID();
        this.password = properties.getPassword();
        this.realm = properties.getRealm();
        this.allowedQoP = properties.getAllowedQoP();
        this.unhandledCallbackMessages = new ArrayList<String>(5);
    }
    
    @Override
    public String getSASLMechanismName() {
        return "DIGEST-MD5";
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    public String getPasswordString() {
        return this.password.stringValue();
    }
    
    public byte[] getPasswordBytes() {
        return this.password.getValue();
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    public List<SASLQualityOfProtection> getAllowedQoP() {
        return this.allowedQoP;
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        this.unhandledCallbackMessages.clear();
        final HashMap<String, Object> saslProperties = new HashMap<String, Object>(StaticUtils.computeMapCapacity(20));
        saslProperties.put("javax.security.sasl.qop", SASLQualityOfProtection.toString(this.allowedQoP));
        saslProperties.put("javax.security.sasl.server.authentication", "false");
        SaslClient saslClient;
        try {
            final String[] mechanisms = { "DIGEST-MD5" };
            saslClient = Sasl.createSaslClient(mechanisms, this.authorizationID, "ldap", connection.getConnectedAddress(), saslProperties, this);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_DIGESTMD5_CANNOT_CREATE_SASL_CLIENT.get(StaticUtils.getExceptionMessage(e)), e);
        }
        final SASLHelper helper = new SASLHelper(this, connection, "DIGEST-MD5", saslClient, this.getControls(), this.getResponseTimeoutMillis(connection), this.unhandledCallbackMessages);
        try {
            return helper.processSASLBind();
        }
        finally {
            this.messageID = helper.getMessageID();
        }
    }
    
    @Override
    public DIGESTMD5BindRequest getRebindRequest(final String host, final int port) {
        final DIGESTMD5BindRequestProperties properties = new DIGESTMD5BindRequestProperties(this.authenticationID, this.password);
        properties.setAuthorizationID(this.authorizationID);
        properties.setRealm(this.realm);
        properties.setAllowedQoP(this.allowedQoP);
        return new DIGESTMD5BindRequest(properties, this.getControls());
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
            else if (callback instanceof RealmCallback) {
                final RealmCallback rc = (RealmCallback)callback;
                if (this.realm == null) {
                    final String defaultRealm = rc.getDefaultText();
                    if (defaultRealm == null) {
                        this.unhandledCallbackMessages.add(LDAPMessages.ERR_DIGESTMD5_REALM_REQUIRED_BUT_NONE_PROVIDED.get(String.valueOf(rc.getPrompt())));
                    }
                    else {
                        rc.setText(defaultRealm);
                    }
                }
                else {
                    rc.setText(this.realm);
                }
            }
            else if (callback instanceof RealmChoiceCallback) {
                final RealmChoiceCallback rcc = (RealmChoiceCallback)callback;
                if (this.realm == null) {
                    final String choices = StaticUtils.concatenateStrings("{", " '", ",", "'", " }", rcc.getChoices());
                    this.unhandledCallbackMessages.add(LDAPMessages.ERR_DIGESTMD5_REALM_REQUIRED_BUT_NONE_PROVIDED.get(rcc.getPrompt(), choices));
                }
                else {
                    final String[] choices2 = rcc.getChoices();
                    for (int i = 0; i < choices2.length; ++i) {
                        if (choices2[i].equals(this.realm)) {
                            rcc.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
            else {
                if (Debug.debugEnabled(DebugType.LDAP)) {
                    Debug.debug(Level.WARNING, DebugType.LDAP, "Unexpected DIGEST-MD5 SASL callback of type " + callback.getClass().getName());
                }
                this.unhandledCallbackMessages.add(LDAPMessages.ERR_DIGESTMD5_UNEXPECTED_CALLBACK.get(callback.getClass().getName()));
            }
        }
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public DIGESTMD5BindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public DIGESTMD5BindRequest duplicate(final Control[] controls) {
        final DIGESTMD5BindRequestProperties properties = new DIGESTMD5BindRequestProperties(this.authenticationID, this.password);
        properties.setAuthorizationID(this.authorizationID);
        properties.setRealm(this.realm);
        properties.setAllowedQoP(this.allowedQoP);
        final DIGESTMD5BindRequest bindRequest = new DIGESTMD5BindRequest(properties, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DIGESTMD5BindRequest(authenticationID='");
        buffer.append(this.authenticationID);
        buffer.append('\'');
        if (this.authorizationID != null) {
            buffer.append(", authorizationID='");
            buffer.append(this.authorizationID);
            buffer.append('\'');
        }
        if (this.realm != null) {
            buffer.append(", realm='");
            buffer.append(this.realm);
            buffer.append('\'');
        }
        buffer.append(", qop='");
        buffer.append(SASLQualityOfProtection.toString(this.allowedQoP));
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
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "DIGESTMD5BindRequestProperties", requestID + "RequestProperties", "new DIGESTMD5BindRequestProperties", ToCodeArgHelper.createString(this.authenticationID, "Authentication ID"), ToCodeArgHelper.createString("---redacted-password---", "Password"));
        if (this.authorizationID != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setAuthorizationID", ToCodeArgHelper.createString(this.authorizationID, null));
        }
        if (this.realm != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setRealm", ToCodeArgHelper.createString(this.realm, null));
        }
        final ArrayList<String> qopValues = new ArrayList<String>(3);
        for (final SASLQualityOfProtection qop : this.allowedQoP) {
            qopValues.add("SASLQualityOfProtection." + qop.name());
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setAllowedQoP", ToCodeArgHelper.createRaw(qopValues, null));
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(2);
        constructorArgs.add(ToCodeArgHelper.createRaw(requestID + "RequestProperties", null));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "DIGESTMD5BindRequest", requestID + "Request", "new DIGESTMD5BindRequest", constructorArgs);
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
