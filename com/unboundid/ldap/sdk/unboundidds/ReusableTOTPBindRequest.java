package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.ToCodeHelper;
import java.nio.charset.StandardCharsets;
import com.unboundid.ldap.sdk.ToCodeArgHelper;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ReusableTOTPBindRequest extends UnboundIDTOTPBindRequest
{
    private static final long serialVersionUID = -8283436883838802510L;
    private final byte[] sharedSecret;
    private final int totpIntervalDurationSeconds;
    private final int totpNumDigits;
    
    public ReusableTOTPBindRequest(final String authenticationID, final String authorizationID, final byte[] sharedSecret, final String staticPassword, final Control... controls) {
        this(authenticationID, authorizationID, sharedSecret, staticPassword, 30, 6, controls);
    }
    
    public ReusableTOTPBindRequest(final String authenticationID, final String authorizationID, final byte[] sharedSecret, final byte[] staticPassword, final Control... controls) {
        this(authenticationID, authorizationID, sharedSecret, staticPassword, 30, 6, controls);
    }
    
    public ReusableTOTPBindRequest(final String authenticationID, final String authorizationID, final byte[] sharedSecret, final String staticPassword, final int totpIntervalDurationSeconds, final int totpNumDigits, final Control... controls) {
        super(authenticationID, authorizationID, staticPassword, controls);
        Validator.ensureTrue(totpIntervalDurationSeconds > 0);
        Validator.ensureTrue(totpNumDigits >= 6 && totpNumDigits <= 8);
        this.sharedSecret = sharedSecret;
        this.totpIntervalDurationSeconds = totpIntervalDurationSeconds;
        this.totpNumDigits = totpNumDigits;
    }
    
    public ReusableTOTPBindRequest(final String authenticationID, final String authorizationID, final byte[] sharedSecret, final byte[] staticPassword, final int totpIntervalDurationSeconds, final int totpNumDigits, final Control... controls) {
        super(authenticationID, authorizationID, staticPassword, controls);
        Validator.ensureTrue(totpIntervalDurationSeconds > 0);
        Validator.ensureTrue(totpNumDigits >= 6 && totpNumDigits <= 8);
        this.sharedSecret = sharedSecret;
        this.totpIntervalDurationSeconds = totpIntervalDurationSeconds;
        this.totpNumDigits = totpNumDigits;
    }
    
    private ReusableTOTPBindRequest(final String authenticationID, final String authorizationID, final byte[] sharedSecret, final ASN1OctetString staticPassword, final int totpIntervalDurationSeconds, final int totpNumDigits, final Control... controls) {
        super(authenticationID, authorizationID, staticPassword, controls);
        this.sharedSecret = sharedSecret;
        this.totpIntervalDurationSeconds = totpIntervalDurationSeconds;
        this.totpNumDigits = totpNumDigits;
    }
    
    public byte[] getSharedSecret() {
        return this.sharedSecret;
    }
    
    public int getTOTPIntervalDurationSeconds() {
        return this.totpIntervalDurationSeconds;
    }
    
    public int getTOTPNumDigits() {
        return this.totpNumDigits;
    }
    
    @Override
    protected ASN1OctetString getSASLCredentials() throws LDAPException {
        final String totpPassword = OneTimePassword.totp(this.sharedSecret, System.currentTimeMillis(), this.totpIntervalDurationSeconds, this.totpNumDigits);
        return UnboundIDTOTPBindRequest.encodeCredentials(this.getAuthenticationID(), this.getAuthorizationID(), totpPassword, this.getStaticPassword());
    }
    
    @Override
    public ReusableTOTPBindRequest getRebindRequest(final String host, final int port) {
        return this.duplicate();
    }
    
    @Override
    public ReusableTOTPBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public ReusableTOTPBindRequest duplicate(final Control[] controls) {
        final ReusableTOTPBindRequest bindRequest = new ReusableTOTPBindRequest(this.getAuthenticationID(), this.getAuthorizationID(), this.sharedSecret, this.getStaticPassword(), this.totpIntervalDurationSeconds, this.totpNumDigits, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public void toCode(final List<String> lineList, final String requestID, final int indentSpaces, final boolean includeProcessing) {
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(7);
        constructorArgs.add(ToCodeArgHelper.createString(this.getAuthenticationID(), "Authentication ID"));
        constructorArgs.add(ToCodeArgHelper.createString(this.getAuthorizationID(), "Authorization ID"));
        constructorArgs.add(ToCodeArgHelper.createByteArray("---redacted-secret---".getBytes(StandardCharsets.UTF_8), true, "Shared Secret"));
        constructorArgs.add(ToCodeArgHelper.createString((this.getStaticPassword() == null) ? "null" : "---redacted-password---", "Static Password"));
        constructorArgs.add(ToCodeArgHelper.createInteger(this.totpIntervalDurationSeconds, "Interval Duration (seconds)"));
        constructorArgs.add(ToCodeArgHelper.createInteger(this.totpNumDigits, "Number of TOTP Digits"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "ReusableTOTPBindRequest", requestID + "Request", "new ReusableTOTPBindRequest", constructorArgs);
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
