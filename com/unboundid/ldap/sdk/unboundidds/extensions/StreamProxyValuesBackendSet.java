package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class StreamProxyValuesBackendSet implements Serializable
{
    private static final long serialVersionUID = -5437145469462592611L;
    private final ASN1OctetString backendSetID;
    private final int[] ports;
    private final String[] hosts;
    
    public StreamProxyValuesBackendSet(final ASN1OctetString backendSetID, final String[] hosts, final int[] ports) {
        Validator.ensureNotNull(backendSetID, hosts, ports);
        Validator.ensureTrue(hosts.length > 0);
        Validator.ensureTrue(hosts.length == ports.length);
        this.backendSetID = backendSetID;
        this.hosts = hosts;
        this.ports = ports;
    }
    
    public ASN1OctetString getBackendSetID() {
        return this.backendSetID;
    }
    
    public String[] getHosts() {
        return this.hosts;
    }
    
    public int[] getPorts() {
        return this.ports;
    }
    
    public ASN1Element encode() {
        final ASN1Element[] hostPortElements = new ASN1Element[this.hosts.length];
        for (int i = 0; i < this.hosts.length; ++i) {
            hostPortElements[i] = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(this.hosts[i]), new ASN1Integer(this.ports[i]) });
        }
        return new ASN1Sequence(new ASN1Element[] { this.backendSetID, new ASN1Sequence(hostPortElements) });
    }
    
    public static StreamProxyValuesBackendSet decode(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final ASN1OctetString backendSetID = ASN1OctetString.decodeAsOctetString(elements[0]);
            final ASN1Element[] hostPortElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
            final String[] hosts = new String[hostPortElements.length];
            final int[] ports = new int[hostPortElements.length];
            for (int i = 0; i < hostPortElements.length; ++i) {
                final ASN1Element[] hpElements = ASN1Sequence.decodeAsSequence(hostPortElements[i]).elements();
                hosts[i] = ASN1OctetString.decodeAsOctetString(hpElements[0]).stringValue();
                ports[i] = ASN1Integer.decodeAsInteger(hpElements[1]).intValue();
            }
            return new StreamProxyValuesBackendSet(backendSetID, hosts, ports);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_PROXY_VALUES_BACKEND_SET_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("StreamProxyValuesBackendSet(id=");
        this.backendSetID.toString(buffer);
        buffer.append(", servers={");
        for (int i = 0; i < this.hosts.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(this.hosts[i]);
            buffer.append(':');
            buffer.append(this.ports[i]);
        }
        buffer.append("})");
    }
}
