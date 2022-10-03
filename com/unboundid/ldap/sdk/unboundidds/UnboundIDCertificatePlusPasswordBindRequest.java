package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.ToCodeHelper;
import com.unboundid.ldap.sdk.ToCodeArgHelper;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SASLBindRequest;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class UnboundIDCertificatePlusPasswordBindRequest extends SASLBindRequest
{
    public static final String UNBOUNDID_CERT_PLUS_PW_MECHANISM_NAME = "UNBOUNDID-CERTIFICATE-PLUS-PASSWORD";
    private static final long serialVersionUID = 8863298749835036708L;
    private final ASN1OctetString password;
    private volatile int messageID;
    
    public UnboundIDCertificatePlusPasswordBindRequest(final String password, final Control... controls) {
        this(new ASN1OctetString((byte)(-93), password), controls);
    }
    
    public UnboundIDCertificatePlusPasswordBindRequest(final byte[] password, final Control... controls) {
        this(new ASN1OctetString((byte)(-93), password), controls);
    }
    
    private UnboundIDCertificatePlusPasswordBindRequest(final ASN1OctetString password, final Control... controls) {
        super(controls);
        this.messageID = -1;
        Validator.ensureFalse(password.getValueLength() == 0, "The bind password must not be empty");
        this.password = password;
    }
    
    public ASN1OctetString getPassword() {
        return this.password;
    }
    
    @Override
    public String getSASLMechanismName() {
        return "UNBOUNDID-CERTIFICATE-PLUS-PASSWORD";
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        this.messageID = InternalSDKHelper.nextMessageID(connection);
        return this.sendBindRequest(connection, "", this.password, this.getControls(), this.getResponseTimeoutMillis(connection));
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public UnboundIDCertificatePlusPasswordBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public UnboundIDCertificatePlusPasswordBindRequest duplicate(final Control[] controls) {
        final UnboundIDCertificatePlusPasswordBindRequest bindRequest = new UnboundIDCertificatePlusPasswordBindRequest(this.password, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public UnboundIDCertificatePlusPasswordBindRequest getRebindRequest(final String host, final int port) {
        return this.duplicate();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("UnboundIDCertificatePlusPasswordBindRequest(");
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append("controls={");
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
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(2);
        constructorArgs.add(ToCodeArgHelper.createString("---redacted-password---", "Bind Password"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "UnboundIDCertificatePlusPasswordBindRequest", requestID + "Request", "new UnboundIDCertificatePlusPasswordBindRequest", constructorArgs);
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
