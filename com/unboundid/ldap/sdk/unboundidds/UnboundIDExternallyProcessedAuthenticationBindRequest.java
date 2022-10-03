package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.ToCodeHelper;
import com.unboundid.ldap.sdk.ToCodeArgHelper;
import java.util.List;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.StaticUtils;
import java.util.LinkedHashMap;
import java.util.Collections;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import java.util.Map;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SASLBindRequest;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class UnboundIDExternallyProcessedAuthenticationBindRequest extends SASLBindRequest
{
    public static final String UNBOUNDID_EXTERNALLY_PROCESSED_AUTH_MECHANISM_NAME = "UNBOUNDID-EXTERNALLY-PROCESSED-AUTHENTICATION";
    private static final byte TYPE_AUTHENTICATION_ID = Byte.MIN_VALUE;
    private static final byte TYPE_EXTERNAL_MECHANISM_NAME = -127;
    private static final byte TYPE_EXTERNAL_AUTH_WAS_SUCCESSFUL = -126;
    private static final byte TYPE_EXTERNAL_AUTH_FAILURE_REASON = -125;
    private static final byte TYPE_EXTERNAL_AUTH_WAS_PASSWORD_BASED = -124;
    private static final byte TYPE_EXTERNAL_AUTH_WAS_SECURE = -123;
    private static final byte TYPE_END_CLIENT_IP_ADDRESS = -122;
    private static final byte TYPE_ADDITIONAL_ACCESS_LOG_PROPERTIES = -89;
    private static final long serialVersionUID = -4312237491980971019L;
    private volatile ASN1OctetString encodedCredentials;
    private final boolean externalAuthWasPasswordBased;
    private final boolean externalAuthWasSecure;
    private final boolean externalAuthWasSuccessful;
    private volatile int messageID;
    private final Map<String, String> additionalAccessLogProperties;
    private final String authenticationID;
    private final String endClientIPAddress;
    private final String externalAuthFailureReason;
    private final String externalMechanismName;
    
    public UnboundIDExternallyProcessedAuthenticationBindRequest(final String authenticationID, final String externalMechanismName, final boolean externalAuthWasSuccessful, final String externalAuthFailureReason, final boolean externalAuthWasPasswordBased, final boolean externalAuthWasSecure, final String endClientIPAddress, final Map<String, String> additionalAccessLogProperties, final Control... controls) {
        super(controls);
        Validator.ensureNotNull(authenticationID);
        Validator.ensureNotNull(externalMechanismName);
        this.authenticationID = authenticationID;
        this.externalMechanismName = externalMechanismName;
        this.externalAuthWasSuccessful = externalAuthWasSuccessful;
        this.externalAuthFailureReason = externalAuthFailureReason;
        this.externalAuthWasPasswordBased = externalAuthWasPasswordBased;
        this.externalAuthWasSecure = externalAuthWasSecure;
        this.endClientIPAddress = endClientIPAddress;
        if (additionalAccessLogProperties == null) {
            this.additionalAccessLogProperties = Collections.emptyMap();
        }
        else {
            this.additionalAccessLogProperties = Collections.unmodifiableMap((Map<? extends String, ? extends String>)new LinkedHashMap<String, String>(additionalAccessLogProperties));
        }
        this.messageID = -1;
        this.encodedCredentials = null;
    }
    
    public static UnboundIDExternallyProcessedAuthenticationBindRequest decodeSASLCredentials(final ASN1OctetString saslCredentials, final Control... controls) throws LDAPException {
        Validator.ensureNotNull(saslCredentials);
        boolean passwordBased = true;
        boolean secure = false;
        Boolean successful = null;
        String failureReason = null;
        String ipAddress = null;
        String mechanism = null;
        String authID = null;
        final LinkedHashMap<String, String> logProperties = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(10));
        try {
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(saslCredentials.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        authID = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        mechanism = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -126: {
                        successful = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -125: {
                        failureReason = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -124: {
                        passwordBased = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -123: {
                        secure = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -122: {
                        ipAddress = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -89: {
                        for (final ASN1Element propertiesElement : ASN1Sequence.decodeAsSequence(e).elements()) {
                            final ASN1Element[] logPairElements = ASN1Sequence.decodeAsSequence(propertiesElement).elements();
                            final String name = ASN1OctetString.decodeAsOctetString(logPairElements[0]).stringValue();
                            final String value = ASN1OctetString.decodeAsOctetString(logPairElements[1]).stringValue();
                            logProperties.put(name, value);
                        }
                        break;
                    }
                }
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_EXTERNALLY_PROCESSED_AUTH_CANNOT_DECODE_CREDS.get("UNBOUNDID-EXTERNALLY-PROCESSED-AUTHENTICATION", StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (authID == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_EXTERNALLY_PROCESSED_AUTH_NO_AUTH_ID.get("UNBOUNDID-EXTERNALLY-PROCESSED-AUTHENTICATION"));
        }
        if (mechanism == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_EXTERNALLY_PROCESSED_AUTH_NO_MECH.get("UNBOUNDID-EXTERNALLY-PROCESSED-AUTHENTICATION"));
        }
        if (successful == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, UnboundIDDSMessages.ERR_EXTERNALLY_PROCESSED_AUTH_NO_WAS_SUCCESSFUL.get("UNBOUNDID-EXTERNALLY-PROCESSED-AUTHENTICATION"));
        }
        final UnboundIDExternallyProcessedAuthenticationBindRequest bindRequest = new UnboundIDExternallyProcessedAuthenticationBindRequest(authID, mechanism, successful, failureReason, passwordBased, secure, ipAddress, logProperties, controls);
        bindRequest.encodedCredentials = saslCredentials;
        return bindRequest;
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public String getExternalMechanismName() {
        return this.externalMechanismName;
    }
    
    public boolean externalAuthenticationWasSuccessful() {
        return this.externalAuthWasSuccessful;
    }
    
    public String getExternalAuthenticationFailureReason() {
        return this.externalAuthFailureReason;
    }
    
    public boolean externalAuthenticationWasPasswordBased() {
        return this.externalAuthWasPasswordBased;
    }
    
    public boolean externalAuthenticationWasSecure() {
        return this.externalAuthWasSecure;
    }
    
    public String getEndClientIPAddress() {
        return this.endClientIPAddress;
    }
    
    public Map<String, String> getAdditionalAccessLogProperties() {
        return this.additionalAccessLogProperties;
    }
    
    @Override
    public String getSASLMechanismName() {
        return "UNBOUNDID-EXTERNALLY-PROCESSED-AUTHENTICATION";
    }
    
    public ASN1OctetString getEncodedCredentials() {
        if (this.encodedCredentials == null) {
            final ArrayList<ASN1Element> credElements = new ArrayList<ASN1Element>(8);
            credElements.add(new ASN1OctetString((byte)(-128), this.authenticationID));
            credElements.add(new ASN1OctetString((byte)(-127), this.externalMechanismName));
            credElements.add(new ASN1Boolean((byte)(-126), this.externalAuthWasSuccessful));
            if (this.externalAuthFailureReason != null) {
                credElements.add(new ASN1OctetString((byte)(-125), this.externalAuthFailureReason));
            }
            if (!this.externalAuthWasPasswordBased) {
                credElements.add(new ASN1Boolean((byte)(-124), false));
            }
            if (this.externalAuthWasSecure) {
                credElements.add(new ASN1Boolean((byte)(-123), true));
            }
            if (this.endClientIPAddress != null) {
                credElements.add(new ASN1OctetString((byte)(-122), this.endClientIPAddress));
            }
            if (!this.additionalAccessLogProperties.isEmpty()) {
                final ArrayList<ASN1Element> logElements = new ArrayList<ASN1Element>(this.additionalAccessLogProperties.size());
                for (final Map.Entry<String, String> e : this.additionalAccessLogProperties.entrySet()) {
                    logElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(e.getKey()), new ASN1OctetString(e.getValue()) }));
                }
                credElements.add(new ASN1Sequence((byte)(-89), logElements));
            }
            final ASN1Sequence credSequence = new ASN1Sequence(credElements);
            this.encodedCredentials = new ASN1OctetString(credSequence.encode());
        }
        return this.encodedCredentials;
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        this.messageID = InternalSDKHelper.nextMessageID(connection);
        return this.sendBindRequest(connection, "", this.getEncodedCredentials(), this.getControls(), this.getResponseTimeoutMillis(connection));
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public UnboundIDExternallyProcessedAuthenticationBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public UnboundIDExternallyProcessedAuthenticationBindRequest duplicate(final Control[] controls) {
        final UnboundIDExternallyProcessedAuthenticationBindRequest bindRequest = new UnboundIDExternallyProcessedAuthenticationBindRequest(this.authenticationID, this.externalMechanismName, this.externalAuthWasSuccessful, this.externalAuthFailureReason, this.externalAuthWasPasswordBased, this.externalAuthWasSecure, this.endClientIPAddress, this.additionalAccessLogProperties, controls);
        bindRequest.encodedCredentials = this.encodedCredentials;
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public UnboundIDExternallyProcessedAuthenticationBindRequest getRebindRequest(final String host, final int port) {
        return this.duplicate();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("UnboundIDExternallyProcessedAuthenticationBindRequest(authenticationID='");
        buffer.append(this.authenticationID);
        buffer.append("', externalMechanismName='");
        buffer.append(this.externalMechanismName);
        buffer.append("', externalAuthenticationWasSuccessful=");
        buffer.append(this.externalAuthWasSuccessful);
        buffer.append('\'');
        if (this.externalAuthFailureReason != null) {
            buffer.append(", externalAuthenticationFailureReason='");
            buffer.append(this.externalAuthFailureReason);
            buffer.append('\'');
        }
        buffer.append(", externalAuthenticationWasPasswordBased=");
        buffer.append(this.externalAuthWasPasswordBased);
        buffer.append(", externalAuthenticationWasSecure=");
        buffer.append(this.externalAuthWasSecure);
        if (this.endClientIPAddress != null) {
            buffer.append(", endClientIPAddress='");
            buffer.append(this.endClientIPAddress);
            buffer.append('\'');
        }
        if (!this.additionalAccessLogProperties.isEmpty()) {
            buffer.append(", additionalAccessLogProperties={");
            final Iterator<Map.Entry<String, String>> iterator = this.additionalAccessLogProperties.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, String> e = iterator.next();
                buffer.append('\'');
                buffer.append(e.getKey());
                buffer.append("'='");
                buffer.append(e.getValue());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
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
        final ArrayList<ToCodeArgHelper> mapConstructorArgs = new ArrayList<ToCodeArgHelper>(1);
        mapConstructorArgs.add(ToCodeArgHelper.createInteger(this.additionalAccessLogProperties.size(), "Initial Capacity"));
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "LinkedHashMap<String,String>", requestID + "AdditionalAccessLogProperties", "new LinkedHashMap<String,String>", mapConstructorArgs);
        for (final Map.Entry<String, String> e : this.additionalAccessLogProperties.entrySet()) {
            final ArrayList<ToCodeArgHelper> putArgs = new ArrayList<ToCodeArgHelper>(2);
            putArgs.add(ToCodeArgHelper.createString(e.getKey(), "Log Property Key"));
            putArgs.add(ToCodeArgHelper.createString(e.getValue(), "Log Property Value"));
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "AdditionalAccessLogProperties.put", putArgs);
        }
        final ArrayList<ToCodeArgHelper> requestConstructorArgs = new ArrayList<ToCodeArgHelper>(8);
        requestConstructorArgs.add(ToCodeArgHelper.createString(this.authenticationID, "Authentication ID"));
        requestConstructorArgs.add(ToCodeArgHelper.createString(this.externalMechanismName, "External Mechanism Name"));
        requestConstructorArgs.add(ToCodeArgHelper.createBoolean(this.externalAuthWasSuccessful, "External Authentication Was Successful"));
        requestConstructorArgs.add(ToCodeArgHelper.createString(this.externalAuthFailureReason, "External Authentication Failure Reason"));
        requestConstructorArgs.add(ToCodeArgHelper.createBoolean(this.externalAuthWasPasswordBased, "External Authentication Was Password Based"));
        requestConstructorArgs.add(ToCodeArgHelper.createBoolean(this.externalAuthWasSecure, "External Authentication Was Secure"));
        requestConstructorArgs.add(ToCodeArgHelper.createString(this.endClientIPAddress, "End Client IP Address"));
        requestConstructorArgs.add(ToCodeArgHelper.createRaw(requestID + "AdditionalAccessLogProperties", "Additional AccessLogProperties"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            requestConstructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        lineList.add("");
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "UnboundIDExternallyProcessedAuthenticationBindRequest", requestID + "Request", "new UnboundIDExternallyProcessedAuthenticationBindRequest", requestConstructorArgs);
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
