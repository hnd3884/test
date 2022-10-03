package com.unboundid.ldap.sdk;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GenericSASLBindRequest extends SASLBindRequest
{
    private static final long serialVersionUID = 7740968332104559230L;
    private final ASN1OctetString credentials;
    private final String bindDN;
    private final String mechanism;
    
    public GenericSASLBindRequest(final String bindDN, final String mechanism, final ASN1OctetString credentials, final Control... controls) {
        super(controls);
        Validator.ensureNotNull(mechanism);
        this.bindDN = bindDN;
        this.mechanism = mechanism;
        this.credentials = credentials;
    }
    
    public String getBindDN() {
        return this.bindDN;
    }
    
    @Override
    public String getSASLMechanismName() {
        return this.mechanism;
    }
    
    public ASN1OctetString getCredentials() {
        return this.credentials;
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        return this.sendBindRequest(connection, this.bindDN, this.credentials, this.getControls(), this.getResponseTimeoutMillis(connection));
    }
    
    @Override
    public GenericSASLBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GenericSASLBindRequest duplicate(final Control[] controls) {
        return new GenericSASLBindRequest(this.bindDN, this.mechanism, this.credentials, controls);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GenericSASLBindRequest(mechanism='");
        buffer.append(this.mechanism);
        buffer.append('\'');
        if (this.bindDN != null) {
            buffer.append(", bindDN='");
            buffer.append(this.bindDN);
            buffer.append('\'');
        }
        if (this.credentials != null) {
            buffer.append(", credentials=byte[");
            buffer.append(this.credentials.getValueLength());
            buffer.append(']');
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
        constructorArgs.add(ToCodeArgHelper.createString(this.bindDN, "Bind DN"));
        constructorArgs.add(ToCodeArgHelper.createString(this.mechanism, "SASL Mechanism Name"));
        constructorArgs.add(ToCodeArgHelper.createByteArray("---redacted-SASL-credentials".getBytes(StandardCharsets.UTF_8), true, "SASL Credentials"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "GenericSASLBindRequest", requestID + "Request", "new GenericSASLBindRequest", constructorArgs);
        if (includeProcessing) {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < indentSpaces; ++i) {
                buffer.append(' ');
            }
            final String indent = buffer.toString();
            lineList.add("");
            lineList.add(indent + '{');
            lineList.add(indent + "  BindResult " + requestID + "Result = connection.bind(" + requestID + "Request);");
            lineList.add(indent + "  // The bind was processed successfully.");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (SASLBindInProgressException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The SASL bind requires multiple stages.  " + "Continue it here.");
            lineList.add(indent + "  // Do not attempt to use the connection for " + "any other purpose until bind processing has completed.");
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
