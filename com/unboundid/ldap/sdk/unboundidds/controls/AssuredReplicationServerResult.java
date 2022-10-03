package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Enumerated;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AssuredReplicationServerResult implements Serializable
{
    private static final byte TYPE_RESULT_CODE = Byte.MIN_VALUE;
    private static final byte TYPE_SERVER_ID = -127;
    private static final byte TYPE_REPLICA_ID = -126;
    private static final long serialVersionUID = 3015162215769386343L;
    private final AssuredReplicationServerResultCode resultCode;
    private final Short replicaID;
    private final Short replicationServerID;
    
    public AssuredReplicationServerResult(final AssuredReplicationServerResultCode resultCode, final Short replicationServerID, final Short replicaID) {
        this.resultCode = resultCode;
        this.replicationServerID = replicationServerID;
        this.replicaID = replicaID;
    }
    
    public AssuredReplicationServerResultCode getResultCode() {
        return this.resultCode;
    }
    
    public Short getReplicationServerID() {
        return this.replicationServerID;
    }
    
    public Short getReplicaID() {
        return this.replicaID;
    }
    
    ASN1Element encode() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1Enumerated((byte)(-128), this.resultCode.intValue()));
        if (this.replicationServerID != null) {
            elements.add(new ASN1Integer((byte)(-127), this.replicationServerID));
        }
        if (this.replicaID != null) {
            elements.add(new ASN1Integer((byte)(-126), this.replicaID));
        }
        return new ASN1Sequence(elements);
    }
    
    static AssuredReplicationServerResult decode(final ASN1Element element) throws LDAPException {
        AssuredReplicationServerResultCode resultCode = null;
        Short serverID = null;
        Short replicaID = null;
        try {
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(element).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        final int rcValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        resultCode = AssuredReplicationServerResultCode.valueOf(rcValue);
                        if (resultCode == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_SERVER_RESULT_INVALID_RESULT_CODE.get(rcValue));
                        }
                        break;
                    }
                    case -127: {
                        serverID = (short)ASN1Integer.decodeAsInteger(e).intValue();
                        break;
                    }
                    case -126: {
                        replicaID = (short)ASN1Integer.decodeAsInteger(e).intValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_SERVER_RESULT_UNEXPECTED_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
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
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_SERVER_RESULT_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (resultCode == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_SERVER_RESULT_NO_RESULT_CODE.get());
        }
        return new AssuredReplicationServerResult(resultCode, serverID, replicaID);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("AssuredReplicationServerResult(resultCode=");
        buffer.append(this.resultCode.name());
        if (this.replicationServerID != null) {
            buffer.append(", replicationServerID=");
            buffer.append(this.replicationServerID);
        }
        if (this.replicaID != null) {
            buffer.append(", replicaID=");
            buffer.append(this.replicaID);
        }
        buffer.append(')');
    }
}
