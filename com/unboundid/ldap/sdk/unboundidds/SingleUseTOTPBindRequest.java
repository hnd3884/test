package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.ToCodeHelper;
import com.unboundid.ldap.sdk.ToCodeArgHelper;
import java.util.ArrayList;
import java.util.List;
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

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SingleUseTOTPBindRequest extends UnboundIDTOTPBindRequest
{
    private static final long serialVersionUID = -4429898810534930296L;
    private final String totpPassword;
    
    public SingleUseTOTPBindRequest(final String authenticationID, final String authorizationID, final String totpPassword, final String staticPassword, final Control... controls) {
        super(authenticationID, authorizationID, staticPassword, controls);
        Validator.ensureNotNull(totpPassword);
        this.totpPassword = totpPassword;
    }
    
    public SingleUseTOTPBindRequest(final String authenticationID, final String authorizationID, final String totpPassword, final byte[] staticPassword, final Control... controls) {
        super(authenticationID, authorizationID, staticPassword, controls);
        Validator.ensureNotNull(totpPassword);
        this.totpPassword = totpPassword;
    }
    
    private SingleUseTOTPBindRequest(final String authenticationID, final String authorizationID, final String totpPassword, final ASN1OctetString staticPassword, final Control... controls) {
        super(authenticationID, authorizationID, staticPassword, controls);
        Validator.ensureNotNull(totpPassword);
        this.totpPassword = totpPassword;
    }
    
    public static SingleUseTOTPBindRequest decodeSASLCredentials(final ASN1OctetString saslCredentials, final Control... controls) throws LDAPException {
        try {
            String authenticationID = null;
            String authorizationID = null;
            String totpPassword = null;
            ASN1OctetString staticPassword = null;
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
                        totpPassword = e.decodeAsOctetString().stringValue();
                        break;
                    }
                    case -125: {
                        staticPassword = e.decodeAsOctetString();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_SINGLE_USE_TOTP_DECODE_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            if (authenticationID == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_SINGLE_USE_TOTP_DECODE_MISSING_AUTHN_ID.get());
            }
            if (totpPassword == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_SINGLE_USE_TOTP_DECODE_MISSING_TOTP_PW.get());
            }
            return new SingleUseTOTPBindRequest(authenticationID, authorizationID, totpPassword, staticPassword, controls);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_SINGLE_USE_TOTP_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public String getTOTPPassword() {
        return this.totpPassword;
    }
    
    @Override
    protected ASN1OctetString getSASLCredentials() {
        return UnboundIDTOTPBindRequest.encodeCredentials(this.getAuthenticationID(), this.getAuthorizationID(), this.totpPassword, this.getStaticPassword());
    }
    
    @Override
    public SingleUseTOTPBindRequest getRebindRequest(final String host, final int port) {
        return null;
    }
    
    @Override
    public SingleUseTOTPBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public SingleUseTOTPBindRequest duplicate(final Control[] controls) {
        final SingleUseTOTPBindRequest bindRequest = new SingleUseTOTPBindRequest(this.getAuthenticationID(), this.getAuthorizationID(), this.totpPassword, this.getStaticPassword(), controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public void toCode(final List<String> lineList, final String requestID, final int indentSpaces, final boolean includeProcessing) {
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(5);
        constructorArgs.add(ToCodeArgHelper.createString(this.getAuthenticationID(), "Authentication ID"));
        constructorArgs.add(ToCodeArgHelper.createString(this.getAuthorizationID(), "Authorization ID"));
        constructorArgs.add(ToCodeArgHelper.createString("---redacted-totp-password", "TOTP Password"));
        constructorArgs.add(ToCodeArgHelper.createString((this.getStaticPassword() == null) ? "null" : "---redacted-static-password---", "Static Password"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "SingleUseTOTPBindRequest", requestID + "Request", "new SingleUseTOTPBindRequest", constructorArgs);
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
