package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.LDAPResult;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.ldap.sdk.SASLBindRequest;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public abstract class UnboundIDTOTPBindRequest extends SASLBindRequest
{
    public static final String UNBOUNDID_TOTP_MECHANISM_NAME = "UNBOUNDID-TOTP";
    static final byte TYPE_AUTHENTICATION_ID = Byte.MIN_VALUE;
    static final byte TYPE_AUTHORIZATION_ID = -127;
    static final byte TYPE_TOTP_PASSWORD = -126;
    static final byte TYPE_STATIC_PASSWORD = -125;
    private static final long serialVersionUID = -8751931123826994145L;
    private final ASN1OctetString staticPassword;
    private volatile int messageID;
    private final String authenticationID;
    private final String authorizationID;
    
    protected UnboundIDTOTPBindRequest(final String authenticationID, final String authorizationID, final String staticPassword, final Control... controls) {
        super(controls);
        this.messageID = -1;
        Validator.ensureNotNull(authenticationID);
        this.authenticationID = authenticationID;
        this.authorizationID = authorizationID;
        if (staticPassword == null) {
            this.staticPassword = null;
        }
        else {
            this.staticPassword = new ASN1OctetString((byte)(-125), staticPassword);
        }
    }
    
    protected UnboundIDTOTPBindRequest(final String authenticationID, final String authorizationID, final byte[] staticPassword, final Control... controls) {
        super(controls);
        this.messageID = -1;
        Validator.ensureNotNull(authenticationID);
        this.authenticationID = authenticationID;
        this.authorizationID = authorizationID;
        if (staticPassword == null) {
            this.staticPassword = null;
        }
        else {
            this.staticPassword = new ASN1OctetString((byte)(-125), staticPassword);
        }
    }
    
    protected UnboundIDTOTPBindRequest(final String authenticationID, final String authorizationID, final ASN1OctetString staticPassword, final Control... controls) {
        super(controls);
        this.messageID = -1;
        Validator.ensureNotNull(authenticationID);
        if (staticPassword != null) {
            Validator.ensureTrue(staticPassword.getType() == -125);
        }
        this.authenticationID = authenticationID;
        this.authorizationID = authorizationID;
        this.staticPassword = staticPassword;
    }
    
    public final String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public final String getAuthorizationID() {
        return this.authorizationID;
    }
    
    public final ASN1OctetString getStaticPassword() {
        return this.staticPassword;
    }
    
    @Override
    public final String getSASLMechanismName() {
        return "UNBOUNDID-TOTP";
    }
    
    @Override
    protected final BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        this.messageID = InternalSDKHelper.nextMessageID(connection);
        return this.sendBindRequest(connection, "", this.getSASLCredentials(), this.getControls(), this.getResponseTimeoutMillis(connection));
    }
    
    protected abstract ASN1OctetString getSASLCredentials() throws LDAPException;
    
    public static ASN1OctetString encodeCredentials(final String authenticationID, final String authorizationID, final String totpPassword, final ASN1OctetString staticPassword) {
        Validator.ensureNotNull(authenticationID);
        Validator.ensureNotNull(totpPassword);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        elements.add(new ASN1OctetString((byte)(-128), authenticationID));
        if (authorizationID != null) {
            elements.add(new ASN1OctetString((byte)(-127), authorizationID));
        }
        elements.add(new ASN1OctetString((byte)(-126), totpPassword));
        if (staticPassword != null) {
            if (staticPassword.getType() == -125) {
                elements.add(staticPassword);
            }
            else {
                elements.add(new ASN1OctetString((byte)(-125), staticPassword.getValue()));
            }
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    @Override
    public final int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public final void toString(final StringBuilder buffer) {
        buffer.append("UnboundIDTOTPBindRequest(authID='");
        buffer.append(this.authenticationID);
        buffer.append("', ");
        if (this.authorizationID != null) {
            buffer.append("authzID='");
            buffer.append(this.authorizationID);
            buffer.append("', ");
        }
        buffer.append("includesStaticPassword=");
        buffer.append(this.staticPassword != null);
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
}
