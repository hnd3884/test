package com.unboundid.ldap.sdk;

import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class PLAINBindRequest extends SASLBindRequest
{
    public static final String PLAIN_MECHANISM_NAME = "PLAIN";
    private static final long serialVersionUID = -5186140710317748684L;
    private final ASN1OctetString password;
    private final String authenticationID;
    private final String authorizationID;
    
    public PLAINBindRequest(final String authenticationID, final String password) {
        this(authenticationID, null, new ASN1OctetString(password), PLAINBindRequest.NO_CONTROLS);
        Validator.ensureNotNull(password);
    }
    
    public PLAINBindRequest(final String authenticationID, final byte[] password) {
        this(authenticationID, null, new ASN1OctetString(password), PLAINBindRequest.NO_CONTROLS);
        Validator.ensureNotNull(password);
    }
    
    public PLAINBindRequest(final String authenticationID, final ASN1OctetString password) {
        this(authenticationID, null, password, PLAINBindRequest.NO_CONTROLS);
    }
    
    public PLAINBindRequest(final String authenticationID, final String authorizationID, final String password) {
        this(authenticationID, authorizationID, new ASN1OctetString(password), PLAINBindRequest.NO_CONTROLS);
        Validator.ensureNotNull(password);
    }
    
    public PLAINBindRequest(final String authenticationID, final String authorizationID, final byte[] password) {
        this(authenticationID, authorizationID, new ASN1OctetString(password), PLAINBindRequest.NO_CONTROLS);
        Validator.ensureNotNull(password);
    }
    
    public PLAINBindRequest(final String authenticationID, final String authorizationID, final ASN1OctetString password) {
        this(authenticationID, authorizationID, password, PLAINBindRequest.NO_CONTROLS);
    }
    
    public PLAINBindRequest(final String authenticationID, final String password, final Control... controls) {
        this(authenticationID, null, new ASN1OctetString(password), controls);
        Validator.ensureNotNull(password);
    }
    
    public PLAINBindRequest(final String authenticationID, final byte[] password, final Control... controls) {
        this(authenticationID, null, new ASN1OctetString(password), controls);
        Validator.ensureNotNull(password);
    }
    
    public PLAINBindRequest(final String authenticationID, final ASN1OctetString password, final Control... controls) {
        this(authenticationID, null, password, controls);
    }
    
    public PLAINBindRequest(final String authenticationID, final String authorizationID, final String password, final Control... controls) {
        this(authenticationID, authorizationID, new ASN1OctetString(password), controls);
        Validator.ensureNotNull(password);
    }
    
    public PLAINBindRequest(final String authenticationID, final String authorizationID, final byte[] password, final Control... controls) {
        this(authenticationID, authorizationID, new ASN1OctetString(password), controls);
        Validator.ensureNotNull(password);
    }
    
    public PLAINBindRequest(final String authenticationID, final String authorizationID, final ASN1OctetString password, final Control... controls) {
        super(controls);
        Validator.ensureNotNull(authenticationID, password);
        this.authenticationID = authenticationID;
        this.authorizationID = authorizationID;
        this.password = password;
    }
    
    @Override
    public String getSASLMechanismName() {
        return "PLAIN";
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
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final byte[] authZIDBytes = StaticUtils.getBytes(this.authorizationID);
        final byte[] authNIDBytes = StaticUtils.getBytes(this.authenticationID);
        final byte[] passwordBytes = this.password.getValue();
        final byte[] credBytes = new byte[2 + authZIDBytes.length + authNIDBytes.length + passwordBytes.length];
        System.arraycopy(authZIDBytes, 0, credBytes, 0, authZIDBytes.length);
        int pos = authZIDBytes.length + 1;
        System.arraycopy(authNIDBytes, 0, credBytes, pos, authNIDBytes.length);
        pos += authNIDBytes.length + 1;
        System.arraycopy(passwordBytes, 0, credBytes, pos, passwordBytes.length);
        return this.sendBindRequest(connection, "", new ASN1OctetString(credBytes), this.getControls(), this.getResponseTimeoutMillis(connection));
    }
    
    @Override
    public PLAINBindRequest getRebindRequest(final String host, final int port) {
        return new PLAINBindRequest(this.authenticationID, this.authorizationID, this.password, this.getControls());
    }
    
    @Override
    public PLAINBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public PLAINBindRequest duplicate(final Control[] controls) {
        final PLAINBindRequest bindRequest = new PLAINBindRequest(this.authenticationID, this.authorizationID, this.password, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PLAINBindRequest(authenticationID='");
        buffer.append(this.authenticationID);
        buffer.append('\'');
        if (this.authorizationID != null) {
            buffer.append(", authorizationID='");
            buffer.append(this.authorizationID);
            buffer.append('\'');
        }
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
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(4);
        constructorArgs.add(ToCodeArgHelper.createString(this.authenticationID, "Authentication ID"));
        constructorArgs.add(ToCodeArgHelper.createString(this.authorizationID, "Authorization ID"));
        constructorArgs.add(ToCodeArgHelper.createString("---redacted-password---", "Bind Password"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "PLAINBindRequest", requestID + "Request", "new PLAINBindRequest", constructorArgs);
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
