package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPResult;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AssuredReplicationResponseControl extends Control implements DecodeableControl
{
    public static final String ASSURED_REPLICATION_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.29";
    private static final byte TYPE_LOCAL_LEVEL = Byte.MIN_VALUE;
    private static final byte TYPE_LOCAL_SATISFIED = -127;
    private static final byte TYPE_LOCAL_MESSAGE = -126;
    private static final byte TYPE_REMOTE_LEVEL = -125;
    private static final byte TYPE_REMOTE_SATISFIED = -124;
    private static final byte TYPE_REMOTE_MESSAGE = -123;
    private static final byte TYPE_CSN = -122;
    private static final byte TYPE_SERVER_RESULTS = -89;
    private static final long serialVersionUID = -4521456074629871607L;
    private final AssuredReplicationLocalLevel localLevel;
    private final AssuredReplicationRemoteLevel remoteLevel;
    private final boolean localAssuranceSatisfied;
    private final boolean remoteAssuranceSatisfied;
    private final List<AssuredReplicationServerResult> serverResults;
    private final String csn;
    private final String localAssuranceMessage;
    private final String remoteAssuranceMessage;
    
    AssuredReplicationResponseControl() {
        this.localLevel = null;
        this.localAssuranceSatisfied = false;
        this.localAssuranceMessage = null;
        this.remoteLevel = null;
        this.remoteAssuranceSatisfied = false;
        this.remoteAssuranceMessage = null;
        this.csn = null;
        this.serverResults = null;
    }
    
    public AssuredReplicationResponseControl(final AssuredReplicationLocalLevel localLevel, final boolean localAssuranceSatisfied, final String localAssuranceMessage, final AssuredReplicationRemoteLevel remoteLevel, final boolean remoteAssuranceSatisfied, final String remoteAssuranceMessage, final String csn, final Collection<AssuredReplicationServerResult> serverResults) {
        super("1.3.6.1.4.1.30221.2.5.29", false, encodeValue(localLevel, localAssuranceSatisfied, localAssuranceMessage, remoteLevel, remoteAssuranceSatisfied, remoteAssuranceMessage, csn, serverResults));
        this.localLevel = localLevel;
        this.localAssuranceSatisfied = localAssuranceSatisfied;
        this.localAssuranceMessage = localAssuranceMessage;
        this.remoteLevel = remoteLevel;
        this.remoteAssuranceSatisfied = remoteAssuranceSatisfied;
        this.remoteAssuranceMessage = remoteAssuranceMessage;
        this.csn = csn;
        if (serverResults == null) {
            this.serverResults = Collections.emptyList();
        }
        else {
            this.serverResults = Collections.unmodifiableList((List<? extends AssuredReplicationServerResult>)new ArrayList<AssuredReplicationServerResult>(serverResults));
        }
    }
    
    public AssuredReplicationResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_RESPONSE_NO_VALUE.get());
        }
        AssuredReplicationLocalLevel lLevel = null;
        Boolean lSatisfied = null;
        String lMessage = null;
        AssuredReplicationRemoteLevel rLevel = null;
        Boolean rSatisfied = null;
        String rMessage = null;
        String seqNum = null;
        List<AssuredReplicationServerResult> sResults = Collections.emptyList();
        try {
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        final int intValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        lLevel = AssuredReplicationLocalLevel.valueOf(intValue);
                        if (lLevel == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_RESPONSE_INVALID_LOCAL_LEVEL.get(intValue));
                        }
                        break;
                    }
                    case -127: {
                        lSatisfied = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -126: {
                        lMessage = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -125: {
                        final int intValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        rLevel = AssuredReplicationRemoteLevel.valueOf(intValue);
                        if (lLevel == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_RESPONSE_INVALID_REMOTE_LEVEL.get(intValue));
                        }
                        break;
                    }
                    case -124: {
                        rSatisfied = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -123: {
                        rMessage = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -122: {
                        seqNum = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -89: {
                        final ASN1Element[] srElements = ASN1Sequence.decodeAsSequence(e).elements();
                        final ArrayList<AssuredReplicationServerResult> srList = new ArrayList<AssuredReplicationServerResult>(srElements.length);
                        for (final ASN1Element srElement : srElements) {
                            try {
                                srList.add(AssuredReplicationServerResult.decode(srElement));
                            }
                            catch (final Exception ex) {
                                Debug.debugException(ex);
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_RESPONSE_ERROR_DECODING_SR.get(StaticUtils.getExceptionMessage(ex)), ex);
                            }
                        }
                        sResults = Collections.unmodifiableList((List<? extends AssuredReplicationServerResult>)srList);
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_RESPONSE_UNEXPECTED_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
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
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_RESPONSE_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (lSatisfied == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_RESPONSE_NO_LOCAL_SATISFIED.get());
        }
        if (rSatisfied == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSURED_REPLICATION_RESPONSE_NO_REMOTE_SATISFIED.get());
        }
        this.localLevel = lLevel;
        this.localAssuranceSatisfied = lSatisfied;
        this.localAssuranceMessage = lMessage;
        this.remoteLevel = rLevel;
        this.remoteAssuranceSatisfied = rSatisfied;
        this.remoteAssuranceMessage = rMessage;
        this.csn = seqNum;
        this.serverResults = sResults;
    }
    
    private static ASN1OctetString encodeValue(final AssuredReplicationLocalLevel localLevel, final boolean localAssuranceSatisfied, final String localAssuranceMessage, final AssuredReplicationRemoteLevel remoteLevel, final boolean remoteAssuranceSatisfied, final String remoteAssuranceMessage, final String csn, final Collection<AssuredReplicationServerResult> serverResults) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(8);
        if (localLevel != null) {
            elements.add(new ASN1Enumerated((byte)(-128), localLevel.intValue()));
        }
        elements.add(new ASN1Boolean((byte)(-127), localAssuranceSatisfied));
        if (localAssuranceMessage != null) {
            elements.add(new ASN1OctetString((byte)(-126), localAssuranceMessage));
        }
        if (remoteLevel != null) {
            elements.add(new ASN1Enumerated((byte)(-125), remoteLevel.intValue()));
        }
        elements.add(new ASN1Boolean((byte)(-124), remoteAssuranceSatisfied));
        if (remoteAssuranceMessage != null) {
            elements.add(new ASN1OctetString((byte)(-123), remoteAssuranceMessage));
        }
        if (csn != null) {
            elements.add(new ASN1OctetString((byte)(-122), csn));
        }
        if (serverResults != null && !serverResults.isEmpty()) {
            final ArrayList<ASN1Element> srElements = new ArrayList<ASN1Element>(serverResults.size());
            for (final AssuredReplicationServerResult r : serverResults) {
                srElements.add(r.encode());
            }
            elements.add(new ASN1Sequence((byte)(-89), srElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    @Override
    public AssuredReplicationResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new AssuredReplicationResponseControl(oid, isCritical, value);
    }
    
    public static AssuredReplicationResponseControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.29");
        if (c == null) {
            return null;
        }
        if (c instanceof AssuredReplicationResponseControl) {
            return (AssuredReplicationResponseControl)c;
        }
        return new AssuredReplicationResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public static List<AssuredReplicationResponseControl> getAll(final LDAPResult result) throws LDAPException {
        final Control[] controls = result.getResponseControls();
        final ArrayList<AssuredReplicationResponseControl> decodedControls = new ArrayList<AssuredReplicationResponseControl>(controls.length);
        for (final Control c : controls) {
            if (c.getOID().equals("1.3.6.1.4.1.30221.2.5.29")) {
                if (c instanceof AssuredReplicationResponseControl) {
                    decodedControls.add((AssuredReplicationResponseControl)c);
                }
                else {
                    decodedControls.add(new AssuredReplicationResponseControl(c.getOID(), c.isCritical(), c.getValue()));
                }
            }
        }
        return Collections.unmodifiableList((List<? extends AssuredReplicationResponseControl>)decodedControls);
    }
    
    public AssuredReplicationLocalLevel getLocalLevel() {
        return this.localLevel;
    }
    
    public boolean localAssuranceSatisfied() {
        return this.localAssuranceSatisfied;
    }
    
    public String getLocalAssuranceMessage() {
        return this.localAssuranceMessage;
    }
    
    public AssuredReplicationRemoteLevel getRemoteLevel() {
        return this.remoteLevel;
    }
    
    public boolean remoteAssuranceSatisfied() {
        return this.remoteAssuranceSatisfied;
    }
    
    public String getRemoteAssuranceMessage() {
        return this.remoteAssuranceMessage;
    }
    
    public String getCSN() {
        return this.csn;
    }
    
    public List<AssuredReplicationServerResult> getServerResults() {
        return this.serverResults;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_ASSURED_REPLICATION_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AssuredReplicationResponseControl(isCritical=");
        buffer.append(this.isCritical());
        if (this.localLevel != null) {
            buffer.append(", localLevel=");
            buffer.append(this.localLevel.name());
        }
        buffer.append(", localAssuranceSatisfied=");
        buffer.append(this.localAssuranceSatisfied);
        if (this.localAssuranceMessage != null) {
            buffer.append(", localMessage='");
            buffer.append(this.localAssuranceMessage);
            buffer.append('\'');
        }
        if (this.remoteLevel != null) {
            buffer.append(", remoteLevel=");
            buffer.append(this.remoteLevel.name());
        }
        buffer.append(", remoteAssuranceSatisfied=");
        buffer.append(this.remoteAssuranceSatisfied);
        if (this.remoteAssuranceMessage != null) {
            buffer.append(", remoteMessage='");
            buffer.append(this.remoteAssuranceMessage);
            buffer.append('\'');
        }
        if (this.csn != null) {
            buffer.append(", csn='");
            buffer.append(this.csn);
            buffer.append('\'');
        }
        if (this.serverResults != null && !this.serverResults.isEmpty()) {
            buffer.append(", serverResults={");
            final Iterator<AssuredReplicationServerResult> iterator = this.serverResults.iterator();
            while (iterator.hasNext()) {
                if (iterator.hasNext()) {
                    iterator.next().toString(buffer);
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
