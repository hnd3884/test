package com.unboundid.ldap.sdk;

import java.util.ArrayList;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ANONYMOUSBindRequest extends SASLBindRequest
{
    public static final String ANONYMOUS_MECHANISM_NAME = "ANONYMOUS";
    private static final long serialVersionUID = 4259102841471750866L;
    private final String traceString;
    
    public ANONYMOUSBindRequest() {
        this((String)null, ANONYMOUSBindRequest.NO_CONTROLS);
    }
    
    public ANONYMOUSBindRequest(final String traceString) {
        this(traceString, ANONYMOUSBindRequest.NO_CONTROLS);
    }
    
    public ANONYMOUSBindRequest(final Control... controls) {
        this((String)null, controls);
    }
    
    public ANONYMOUSBindRequest(final String traceString, final Control... controls) {
        super(controls);
        this.traceString = traceString;
    }
    
    @Override
    public String getSASLMechanismName() {
        return "ANONYMOUS";
    }
    
    public String getTraceString() {
        return this.traceString;
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        ASN1OctetString credentials = null;
        if (this.traceString != null && !this.traceString.isEmpty()) {
            credentials = new ASN1OctetString(this.traceString);
        }
        return this.sendBindRequest(connection, null, credentials, this.getControls(), this.getResponseTimeoutMillis(connection));
    }
    
    @Override
    public ANONYMOUSBindRequest getRebindRequest(final String host, final int port) {
        return new ANONYMOUSBindRequest(this.traceString, this.getControls());
    }
    
    @Override
    public ANONYMOUSBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public ANONYMOUSBindRequest duplicate(final Control[] controls) {
        final ANONYMOUSBindRequest bindRequest = new ANONYMOUSBindRequest(this.traceString, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ANONYMOUSBindRequest(");
        if (this.traceString != null) {
            buffer.append(", trace='");
            buffer.append(this.traceString);
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
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(2);
        constructorArgs.add(ToCodeArgHelper.createString(this.traceString, "Trace String"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "ANONYMOUSBindRequest", requestID + "Request", "new ANONYMOUSBindRequest", constructorArgs);
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
