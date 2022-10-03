package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RegisterYubiKeyOTPDeviceExtendedRequest extends ExtendedRequest
{
    public static final String REGISTER_YUBIKEY_OTP_DEVICE_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.54";
    private static final byte TYPE_AUTHENTICATION_ID = Byte.MIN_VALUE;
    private static final byte TYPE_STATIC_PASSWORD = -127;
    private static final byte TYPE_YUBIKEY_OTP = -126;
    private static final long serialVersionUID = 4833523148133015294L;
    private final ASN1OctetString staticPassword;
    private final String authenticationID;
    private final String yubiKeyOTP;
    
    public RegisterYubiKeyOTPDeviceExtendedRequest(final String yubiKeyOTP, final Control... controls) {
        this(null, (ASN1OctetString)null, yubiKeyOTP, controls);
    }
    
    public RegisterYubiKeyOTPDeviceExtendedRequest(final String authenticationID, final String staticPassword, final String yubiKeyOTP, final Control... controls) {
        this(authenticationID, encodePassword(staticPassword), yubiKeyOTP, controls);
    }
    
    public RegisterYubiKeyOTPDeviceExtendedRequest(final String authenticationID, final byte[] staticPassword, final String yubiKeyOTP, final Control... controls) {
        this(authenticationID, encodePassword(staticPassword), yubiKeyOTP, controls);
    }
    
    private RegisterYubiKeyOTPDeviceExtendedRequest(final String authenticationID, final ASN1OctetString staticPassword, final String yubiKeyOTP, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.54", encodeValue(authenticationID, staticPassword, yubiKeyOTP), controls);
        this.authenticationID = authenticationID;
        this.staticPassword = staticPassword;
        this.yubiKeyOTP = yubiKeyOTP;
    }
    
    public RegisterYubiKeyOTPDeviceExtendedRequest(final ExtendedRequest request) throws LDAPException {
        super(request);
        final ASN1OctetString value = request.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_REGISTER_YUBIKEY_OTP_REQUEST_NO_VALUE.get());
        }
        try {
            String authID = null;
            ASN1OctetString staticPW = null;
            String otp = null;
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        authID = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        staticPW = ASN1OctetString.decodeAsOctetString(e);
                        break;
                    }
                    case -126: {
                        otp = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_REGISTER_YUBIKEY_OTP_REQUEST_UNRECOGNIZED_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            if (otp == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_REGISTER_YUBIKEY_OTP_REQUEST_MISSING_OTP.get());
            }
            this.authenticationID = authID;
            this.staticPassword = staticPW;
            this.yubiKeyOTP = otp;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_REGISTER_YUBIKEY_OTP_REQUEST_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    static ASN1OctetString encodePassword(final Object password) {
        if (password == null) {
            return null;
        }
        if (password instanceof byte[]) {
            return new ASN1OctetString((byte)(-127), (byte[])password);
        }
        return new ASN1OctetString((byte)(-127), String.valueOf(password));
    }
    
    private static ASN1OctetString encodeValue(final String authenticationID, final ASN1OctetString staticPassword, final String yubiKeyOTP) {
        Validator.ensureNotNull(yubiKeyOTP);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        if (authenticationID != null) {
            elements.add(new ASN1OctetString((byte)(-128), authenticationID));
        }
        if (staticPassword != null) {
            elements.add(staticPassword);
        }
        elements.add(new ASN1OctetString((byte)(-126), yubiKeyOTP));
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
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
    public RegisterYubiKeyOTPDeviceExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public RegisterYubiKeyOTPDeviceExtendedRequest duplicate(final Control[] controls) {
        final RegisterYubiKeyOTPDeviceExtendedRequest r = new RegisterYubiKeyOTPDeviceExtendedRequest(this.authenticationID, this.staticPassword, this.yubiKeyOTP, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_REGISTER_YUBIKEY_OTP_REQUEST_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("RegisterYubiKeyOTPDeviceExtendedRequest(");
        if (this.authenticationID != null) {
            buffer.append("authenticationID='");
            buffer.append(this.authenticationID);
            buffer.append("', ");
        }
        buffer.append("staticPasswordProvided=");
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
