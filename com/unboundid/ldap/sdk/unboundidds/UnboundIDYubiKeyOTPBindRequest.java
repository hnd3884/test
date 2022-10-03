package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.BindRequest;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.SASLBindRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UnboundIDYubiKeyOTPBindRequest extends SASLBindRequest
{
    public static final String UNBOUNDID_YUBIKEY_OTP_MECHANISM_NAME = "UNBOUNDID-YUBIKEY-OTP";
    private static final byte TYPE_AUTHENTICATION_ID = Byte.MIN_VALUE;
    private static final byte TYPE_AUTHORIZATION_ID = -127;
    private static final byte TYPE_STATIC_PASSWORD = -126;
    private static final byte TYPE_YUBIKEY_OTP = -125;
    private static final long serialVersionUID = -6124016046606933247L;
    private final ASN1OctetString staticPassword;
    private volatile int messageID;
    private final String authenticationID;
    private final String authorizationID;
    private final String yubiKeyOTP;
    
    public UnboundIDYubiKeyOTPBindRequest(final String authenticationID, final String authorizationID, final String staticPassword, final String yubiKeyOTP, final Control... controls) {
        this(authenticationID, authorizationID, toASN1OctetString(staticPassword), yubiKeyOTP, controls);
    }
    
    public UnboundIDYubiKeyOTPBindRequest(final String authenticationID, final String authorizationID, final byte[] staticPassword, final String yubiKeyOTP, final Control... controls) {
        this(authenticationID, authorizationID, toASN1OctetString(staticPassword), yubiKeyOTP, controls);
    }
    
    private UnboundIDYubiKeyOTPBindRequest(final String authenticationID, final String authorizationID, final ASN1OctetString staticPassword, final String yubiKeyOTP, final Control... controls) {
        super(controls);
        this.messageID = -1;
        Validator.ensureNotNull(authenticationID);
        Validator.ensureNotNull(yubiKeyOTP);
        this.authenticationID = authenticationID;
        this.authorizationID = authorizationID;
        this.staticPassword = staticPassword;
        this.yubiKeyOTP = yubiKeyOTP;
    }
    
    private static ASN1OctetString toASN1OctetString(final Object password) {
        if (password == null) {
            return null;
        }
        if (password instanceof byte[]) {
            return new ASN1OctetString((byte)(-126), (byte[])password);
        }
        return new ASN1OctetString((byte)(-126), String.valueOf(password));
    }
    
    public static UnboundIDYubiKeyOTPBindRequest decodeCredentials(final ASN1OctetString saslCredentials, final Control... controls) throws LDAPException {
        try {
            ASN1OctetString staticPassword = null;
            String authenticationID = null;
            String authorizationID = null;
            String yubiKeyOTP = null;
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(saslCredentials.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        authenticationID = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        authorizationID = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -126: {
                        staticPassword = ASN1OctetString.decodeAsOctetString(e);
                        break;
                    }
                    case -125: {
                        yubiKeyOTP = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_YUBIKEY_OTP_DECODE_UNRECOGNIZED_CRED_ELEMENT.get("UNBOUNDID-YUBIKEY-OTP", StaticUtils.toHex(e.getType())));
                    }
                }
            }
            if (authenticationID == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_YUBIKEY_OTP_DECODE_NO_AUTH_ID.get("UNBOUNDID-YUBIKEY-OTP"));
            }
            if (yubiKeyOTP == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_YUBIKEY_OTP_NO_OTP.get("UNBOUNDID-YUBIKEY-OTP"));
            }
            return new UnboundIDYubiKeyOTPBindRequest(authenticationID, authorizationID, staticPassword, yubiKeyOTP, controls);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_YUBIKEY_OTP_DECODE_ERROR.get("UNBOUNDID-YUBIKEY-OTP", StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    public String getStaticPasswordString() {
        if (this.staticPassword == null) {
            return null;
        }
        return this.staticPassword.stringValue();
    }
    
    public byte[] getStaticPasswordBytes() {
        if (this.staticPassword == null) {
            return null;
        }
        return this.staticPassword.getValue();
    }
    
    public String getYubiKeyOTP() {
        return this.yubiKeyOTP;
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        this.messageID = InternalSDKHelper.nextMessageID(connection);
        return this.sendBindRequest(connection, "", this.encodeCredentials(), this.getControls(), this.getResponseTimeoutMillis(connection));
    }
    
    public ASN1OctetString encodeCredentials() {
        return encodeCredentials(this.authenticationID, this.authorizationID, this.staticPassword, this.yubiKeyOTP);
    }
    
    public static ASN1OctetString encodeCredentials(final String authenticationID, final String authorizationID, final ASN1OctetString staticPassword, final String yubiKeyOTP) {
        Validator.ensureNotNull(authenticationID);
        Validator.ensureNotNull(yubiKeyOTP);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        elements.add(new ASN1OctetString((byte)(-128), authenticationID));
        if (authorizationID != null) {
            elements.add(new ASN1OctetString((byte)(-127), authorizationID));
        }
        if (staticPassword != null) {
            elements.add(new ASN1OctetString((byte)(-126), staticPassword.getValue()));
        }
        elements.add(new ASN1OctetString((byte)(-125), yubiKeyOTP));
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    @Override
    public UnboundIDYubiKeyOTPBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public UnboundIDYubiKeyOTPBindRequest duplicate(final Control[] controls) {
        final UnboundIDYubiKeyOTPBindRequest bindRequest = new UnboundIDYubiKeyOTPBindRequest(this.authenticationID, this.authorizationID, this.staticPassword, this.yubiKeyOTP, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public String getSASLMechanismName() {
        return "UNBOUNDID-YUBIKEY-OTP";
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("UnboundYubiKeyOTPBindRequest(authenticationID='");
        buffer.append(this.authenticationID);
        if (this.authorizationID != null) {
            buffer.append("', authorizationID='");
            buffer.append(this.authorizationID);
        }
        buffer.append("', staticPasswordProvided=");
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
