package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AssuredReplicationRequestControl extends Control
{
    public static final String ASSURED_REPLICATION_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.28";
    private static final byte TYPE_MIN_LOCAL_LEVEL = Byte.MIN_VALUE;
    private static final byte TYPE_MAX_LOCAL_LEVEL = -127;
    private static final byte TYPE_MIN_REMOTE_LEVEL = -126;
    private static final byte TYPE_MAX_REMOTE_LEVEL = -125;
    private static final byte TYPE_SEND_RESPONSE_IMMEDIATELY = -124;
    private static final byte TYPE_TIMEOUT = -123;
    private static final long serialVersionUID = -2013933506118879241L;
    private final AssuredReplicationLocalLevel maximumLocalLevel;
    private final AssuredReplicationLocalLevel minimumLocalLevel;
    private final AssuredReplicationRemoteLevel maximumRemoteLevel;
    private final AssuredReplicationRemoteLevel minimumRemoteLevel;
    private final boolean sendResponseImmediately;
    private final Long timeoutMillis;
    
    public AssuredReplicationRequestControl(final AssuredReplicationLocalLevel minimumLocalLevel, final AssuredReplicationRemoteLevel minimumRemoteLevel, final Long timeoutMillis) {
        this(false, minimumLocalLevel, null, minimumRemoteLevel, null, timeoutMillis, false);
    }
    
    public AssuredReplicationRequestControl(final boolean isCritical, final AssuredReplicationLocalLevel minimumLocalLevel, final AssuredReplicationLocalLevel maximumLocalLevel, final AssuredReplicationRemoteLevel minimumRemoteLevel, final AssuredReplicationRemoteLevel maximumRemoteLevel, final Long timeoutMillis, final boolean sendResponseImmediately) {
        super("1.3.6.1.4.1.30221.2.5.28", isCritical, encodeValue(minimumLocalLevel, maximumLocalLevel, minimumRemoteLevel, maximumRemoteLevel, sendResponseImmediately, timeoutMillis));
        this.minimumLocalLevel = minimumLocalLevel;
        this.maximumLocalLevel = maximumLocalLevel;
        this.minimumRemoteLevel = minimumRemoteLevel;
        this.maximumRemoteLevel = maximumRemoteLevel;
        this.sendResponseImmediately = sendResponseImmediately;
        this.timeoutMillis = timeoutMillis;
    }
    
    public AssuredReplicationRequestControl(final Control c) throws LDAPException {
        super(c);
        final ASN1OctetString value = c.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_REQUEST_NO_VALUE.get());
        }
        AssuredReplicationLocalLevel maxLocalLevel = null;
        AssuredReplicationLocalLevel minLocalLevel = null;
        AssuredReplicationRemoteLevel maxRemoteLevel = null;
        AssuredReplicationRemoteLevel minRemoteLevel = null;
        boolean sendImmediately = false;
        Long timeout = null;
        try {
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        final int intValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        minLocalLevel = AssuredReplicationLocalLevel.valueOf(intValue);
                        if (minLocalLevel == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_REQUEST_INVALID_MIN_LOCAL_LEVEL.get(intValue));
                        }
                        break;
                    }
                    case -127: {
                        final int intValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        maxLocalLevel = AssuredReplicationLocalLevel.valueOf(intValue);
                        if (maxLocalLevel == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_REQUEST_INVALID_MAX_LOCAL_LEVEL.get(intValue));
                        }
                        break;
                    }
                    case -126: {
                        final int intValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        minRemoteLevel = AssuredReplicationRemoteLevel.valueOf(intValue);
                        if (minRemoteLevel == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_REQUEST_INVALID_MIN_REMOTE_LEVEL.get(intValue));
                        }
                        break;
                    }
                    case -125: {
                        final int intValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        maxRemoteLevel = AssuredReplicationRemoteLevel.valueOf(intValue);
                        if (maxRemoteLevel == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_REQUEST_INVALID_MAX_REMOTE_LEVEL.get(intValue));
                        }
                        break;
                    }
                    case -124: {
                        sendImmediately = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -123: {
                        timeout = ASN1Long.decodeAsLong(e).longValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_REQUEST_UNEXPECTED_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_REQUEST_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        this.minimumLocalLevel = minLocalLevel;
        this.maximumLocalLevel = maxLocalLevel;
        this.minimumRemoteLevel = minRemoteLevel;
        this.maximumRemoteLevel = maxRemoteLevel;
        this.sendResponseImmediately = sendImmediately;
        this.timeoutMillis = timeout;
    }
    
    private static ASN1OctetString encodeValue(final AssuredReplicationLocalLevel minimumLocalLevel, final AssuredReplicationLocalLevel maximumLocalLevel, final AssuredReplicationRemoteLevel minimumRemoteLevel, final AssuredReplicationRemoteLevel maximumRemoteLevel, final boolean sendResponseImmediately, final Long timeoutMillis) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(6);
        if (minimumLocalLevel != null) {
            elements.add(new ASN1Enumerated((byte)(-128), minimumLocalLevel.intValue()));
        }
        if (maximumLocalLevel != null) {
            elements.add(new ASN1Enumerated((byte)(-127), maximumLocalLevel.intValue()));
        }
        if (minimumRemoteLevel != null) {
            elements.add(new ASN1Enumerated((byte)(-126), minimumRemoteLevel.intValue()));
        }
        if (maximumRemoteLevel != null) {
            elements.add(new ASN1Enumerated((byte)(-125), maximumRemoteLevel.intValue()));
        }
        if (sendResponseImmediately) {
            elements.add(new ASN1Boolean((byte)(-124), true));
        }
        if (timeoutMillis != null) {
            elements.add(new ASN1Long((byte)(-123), timeoutMillis));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public AssuredReplicationLocalLevel getMinimumLocalLevel() {
        return this.minimumLocalLevel;
    }
    
    public AssuredReplicationLocalLevel getMaximumLocalLevel() {
        return this.maximumLocalLevel;
    }
    
    public AssuredReplicationRemoteLevel getMinimumRemoteLevel() {
        return this.minimumRemoteLevel;
    }
    
    public AssuredReplicationRemoteLevel getMaximumRemoteLevel() {
        return this.maximumRemoteLevel;
    }
    
    public boolean sendResponseImmediately() {
        return this.sendResponseImmediately;
    }
    
    public Long getTimeoutMillis() {
        return this.timeoutMillis;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_ASSURED_REPLICATION_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AssuredReplicationRequestControl(isCritical=");
        buffer.append(this.isCritical());
        if (this.minimumLocalLevel != null) {
            buffer.append(", minimumLocalLevel=");
            buffer.append(this.minimumLocalLevel.name());
        }
        if (this.maximumLocalLevel != null) {
            buffer.append(", maximumLocalLevel=");
            buffer.append(this.maximumLocalLevel.name());
        }
        if (this.minimumRemoteLevel != null) {
            buffer.append(", minimumRemoteLevel=");
            buffer.append(this.minimumRemoteLevel.name());
        }
        if (this.maximumRemoteLevel != null) {
            buffer.append(", maximumRemoteLevel=");
            buffer.append(this.maximumRemoteLevel.name());
        }
        buffer.append(", sendResponseImmediately=");
        buffer.append(this.sendResponseImmediately);
        if (this.timeoutMillis != null) {
            buffer.append(", timeoutMillis=");
            buffer.append(this.timeoutMillis);
        }
        buffer.append(')');
    }
}
