package com.unboundid.ldap.sdk;

import java.util.ArrayList;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class EXTERNALBindRequest extends SASLBindRequest
{
    public static final String EXTERNAL_MECHANISM_NAME = "EXTERNAL";
    private static final long serialVersionUID = 7520760039662616663L;
    private int messageID;
    private final String authzID;
    
    public EXTERNALBindRequest() {
        this((String)null, StaticUtils.NO_CONTROLS);
    }
    
    public EXTERNALBindRequest(final String authzID) {
        this(authzID, StaticUtils.NO_CONTROLS);
    }
    
    public EXTERNALBindRequest(final Control... controls) {
        this((String)null, controls);
    }
    
    public EXTERNALBindRequest(final String authzID, final Control... controls) {
        super(controls);
        this.messageID = -1;
        this.authzID = authzID;
    }
    
    public String getAuthorizationID() {
        return this.authzID;
    }
    
    @Override
    public String getSASLMechanismName() {
        return "EXTERNAL";
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        this.messageID = connection.nextMessageID();
        ASN1OctetString creds;
        if (this.authzID == null) {
            creds = null;
        }
        else {
            creds = new ASN1OctetString(this.authzID);
        }
        return this.sendBindRequest(connection, "", creds, this.getControls(), this.getResponseTimeoutMillis(connection));
    }
    
    @Override
    public EXTERNALBindRequest getRebindRequest(final String host, final int port) {
        return new EXTERNALBindRequest(this.authzID, this.getControls());
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public EXTERNALBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public EXTERNALBindRequest duplicate(final Control[] controls) {
        final EXTERNALBindRequest bindRequest = new EXTERNALBindRequest(this.authzID, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EXTERNALBindRequest(");
        boolean added = false;
        if (this.authzID != null) {
            buffer.append("authzID='");
            buffer.append(this.authzID);
            buffer.append('\'');
            added = true;
        }
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            if (added) {
                buffer.append(", ");
            }
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
        if (this.authzID != null) {
            constructorArgs.add(ToCodeArgHelper.createString(this.authzID, "Authorization ID"));
        }
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "EXTERNALBindRequest", requestID + "Request", "new EXTERNALBindRequest", constructorArgs);
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
