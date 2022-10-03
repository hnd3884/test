package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RouteToServerRequestControl extends Control
{
    public static final String ROUTE_TO_SERVER_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.16";
    private static final byte TYPE_SERVER_ID = Byte.MIN_VALUE;
    private static final byte TYPE_ALLOW_ALTERNATE_SERVER = -127;
    private static final byte TYPE_PREFER_LOCAL_SERVER = -126;
    private static final byte TYPE_PREFER_NON_DEGRADED_SERVER = -125;
    private static final long serialVersionUID = 2100638364623466061L;
    private final boolean allowAlternateServer;
    private final boolean preferLocalServer;
    private final boolean preferNonDegradedServer;
    private final String serverID;
    
    public RouteToServerRequestControl(final boolean isCritical, final String serverID, final boolean allowAlternateServer, final boolean preferLocalServer, final boolean preferNonDegradedServer) {
        super("1.3.6.1.4.1.30221.2.5.16", isCritical, encodeValue(serverID, allowAlternateServer, preferLocalServer, preferNonDegradedServer));
        this.serverID = serverID;
        this.allowAlternateServer = allowAlternateServer;
        this.preferLocalServer = (allowAlternateServer && preferLocalServer);
        this.preferNonDegradedServer = (allowAlternateServer && preferNonDegradedServer);
    }
    
    public RouteToServerRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_SERVER_REQUEST_MISSING_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            valueSequence = ASN1Sequence.decodeAsSequence(value.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_SERVER_REQUEST_VALUE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            final ASN1Element[] elements = valueSequence.elements();
            this.serverID = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            this.allowAlternateServer = ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();
            boolean preferLocal = this.allowAlternateServer;
            boolean preferNonDegraded = this.allowAlternateServer;
            for (int i = 2; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case -126: {
                        preferLocal = (this.allowAlternateServer && ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue());
                        break;
                    }
                    case -125: {
                        preferNonDegraded = (this.allowAlternateServer && ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue());
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_SERVER_REQUEST_INVALID_VALUE_TYPE.get(StaticUtils.toHex(elements[i].getType())));
                    }
                }
            }
            this.preferLocalServer = preferLocal;
            this.preferNonDegradedServer = preferNonDegraded;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_SERVER_REQUEST_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String serverID, final boolean allowAlternateServer, final boolean preferLocalServer, final boolean preferNonDegradedServer) {
        Validator.ensureNotNull(serverID);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        elements.add(new ASN1OctetString((byte)(-128), serverID));
        elements.add(new ASN1Boolean((byte)(-127), allowAlternateServer));
        if (allowAlternateServer && !preferLocalServer) {
            elements.add(new ASN1Boolean((byte)(-126), false));
        }
        if (allowAlternateServer && !preferNonDegradedServer) {
            elements.add(new ASN1Boolean((byte)(-125), false));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getServerID() {
        return this.serverID;
    }
    
    public boolean allowAlternateServer() {
        return this.allowAlternateServer;
    }
    
    public boolean preferLocalServer() {
        return this.preferLocalServer;
    }
    
    public boolean preferNonDegradedServer() {
        return this.preferNonDegradedServer;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_ROUTE_TO_SERVER_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("RouteToServerRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", serverID='");
        buffer.append(this.serverID);
        buffer.append("', allowAlternateServer=");
        buffer.append(this.allowAlternateServer);
        buffer.append(", preferLocalServer=");
        buffer.append(this.preferLocalServer);
        buffer.append(", preferNonDegradedServer=");
        buffer.append(this.preferNonDegradedServer);
        buffer.append(')');
    }
}
