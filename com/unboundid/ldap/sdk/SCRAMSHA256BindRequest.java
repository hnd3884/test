package com.unboundid.ldap.sdk;

import java.util.ArrayList;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SCRAMSHA256BindRequest extends SCRAMBindRequest
{
    private static final long serialVersionUID = -4396660110665214258L;
    
    public SCRAMSHA256BindRequest(final String username, final String password, final Control... controls) {
        super(username, new ASN1OctetString(password), controls);
    }
    
    public SCRAMSHA256BindRequest(final String username, final byte[] password, final Control... controls) {
        super(username, new ASN1OctetString(password), controls);
    }
    
    @Override
    public String getSASLMechanismName() {
        return "SCRAM-SHA-256";
    }
    
    @Override
    protected String getDigestAlgorithmName() {
        return "SHA-256";
    }
    
    @Override
    protected String getMACAlgorithmName() {
        return "HmacSHA256";
    }
    
    @Override
    public SCRAMSHA256BindRequest getRebindRequest(final String host, final int port) {
        return this.duplicate();
    }
    
    @Override
    public SCRAMSHA256BindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public SCRAMSHA256BindRequest duplicate(final Control[] controls) {
        return new SCRAMSHA256BindRequest(this.getUsername(), this.getPasswordBytes(), controls);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SCRAMSHA256BindRequest(username='");
        buffer.append(this.getUsername());
        buffer.append('\'');
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
        final List<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(4);
        constructorArgs.add(ToCodeArgHelper.createString(this.getUsername(), "Username"));
        constructorArgs.add(ToCodeArgHelper.createString("---redacted-password---", "Password"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "SCRAMSHA256BindRequest", requestID + "Request", "new SCRAMSHA256BindRequest", constructorArgs);
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
