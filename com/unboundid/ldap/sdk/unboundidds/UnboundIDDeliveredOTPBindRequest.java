package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.ToCodeHelper;
import com.unboundid.ldap.sdk.ToCodeArgHelper;
import java.util.List;
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
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.SASLBindRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class UnboundIDDeliveredOTPBindRequest extends SASLBindRequest
{
    public static final String UNBOUNDID_DELIVERED_OTP_MECHANISM_NAME = "UNBOUNDID-DELIVERED-OTP";
    private static final byte TYPE_AUTHENTICATION_ID = Byte.MIN_VALUE;
    private static final byte TYPE_AUTHORIZATION_ID = -127;
    private static final byte TYPE_OTP = -126;
    private static final long serialVersionUID = 8148101285676071058L;
    private volatile int messageID;
    private final String authenticationID;
    private final String authorizationID;
    private final String oneTimePassword;
    
    public UnboundIDDeliveredOTPBindRequest(final String authenticationID, final String authorizationID, final String oneTimePassword, final Control... controls) {
        super(controls);
        this.messageID = -1;
        Validator.ensureNotNull(authenticationID);
        Validator.ensureNotNull(oneTimePassword);
        this.authenticationID = authenticationID;
        this.authorizationID = authorizationID;
        this.oneTimePassword = oneTimePassword;
    }
    
    public static UnboundIDDeliveredOTPBindRequest decodeSASLCredentials(final ASN1OctetString saslCredentials, final Control... controls) throws LDAPException {
        String authenticationID = null;
        String authorizationID = null;
        String oneTimePassword = null;
        try {
            final ASN1Sequence s = ASN1Sequence.decodeAsSequence(saslCredentials.getValue());
            for (final ASN1Element e : s.elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        authenticationID = e.decodeAsOctetString().stringValue();
                        break;
                    }
                    case -127: {
                        authorizationID = e.decodeAsOctetString().stringValue();
                        break;
                    }
                    case -126: {
                        oneTimePassword = e.decodeAsOctetString().stringValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_DOTP_DECODE_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_DOTP_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (authenticationID == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_DOTP_DECODE_MISSING_AUTHN_ID.get());
        }
        if (oneTimePassword == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_DOTP_DECODE_MISSING_OTP.get());
        }
        return new UnboundIDDeliveredOTPBindRequest(authenticationID, authorizationID, oneTimePassword, controls);
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    public String getOneTimePassword() {
        return this.oneTimePassword;
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        this.messageID = InternalSDKHelper.nextMessageID(connection);
        return this.sendBindRequest(connection, "", encodeCredentials(this.authenticationID, this.authorizationID, this.oneTimePassword), this.getControls(), this.getResponseTimeoutMillis(connection));
    }
    
    public static ASN1OctetString encodeCredentials(final String authenticationID, final String authorizationID, final String oneTimePassword) {
        Validator.ensureNotNull(authenticationID);
        Validator.ensureNotNull(oneTimePassword);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1OctetString((byte)(-128), authenticationID));
        if (authorizationID != null) {
            elements.add(new ASN1OctetString((byte)(-127), authorizationID));
        }
        elements.add(new ASN1OctetString((byte)(-126), oneTimePassword));
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    @Override
    public UnboundIDDeliveredOTPBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public UnboundIDDeliveredOTPBindRequest duplicate(final Control[] controls) {
        final UnboundIDDeliveredOTPBindRequest bindRequest = new UnboundIDDeliveredOTPBindRequest(this.authenticationID, this.authorizationID, this.oneTimePassword, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public String getSASLMechanismName() {
        return "UNBOUNDID-DELIVERED-OTP";
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("UnboundDeliveredOTPBindRequest(authID='");
        buffer.append(this.authenticationID);
        buffer.append("', ");
        if (this.authorizationID != null) {
            buffer.append("authzID='");
            buffer.append(this.authorizationID);
            buffer.append("', ");
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
        constructorArgs.add(ToCodeArgHelper.createString("---redacted-otp---", "One-Time Password"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "UnboundIDDeliveredOTPBindRequest", requestID + "Request", "new UnboundIDDeliveredOTPBindRequest", constructorArgs);
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
