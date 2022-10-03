package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetUserResourceLimitsResponseControl extends Control implements DecodeableControl
{
    public static final String GET_USER_RESOURCE_LIMITS_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.26";
    private static final byte TYPE_SIZE_LIMIT = Byte.MIN_VALUE;
    private static final byte TYPE_TIME_LIMIT = -127;
    private static final byte TYPE_IDLE_TIME_LIMIT = -126;
    private static final byte TYPE_LOOKTHROUGH_LIMIT = -125;
    private static final byte TYPE_EQUIVALENT_AUTHZ_USER_DN = -124;
    private static final byte TYPE_CLIENT_CONNECTION_POLICY_NAME = -123;
    private static final byte TYPE_GROUP_DNS = -90;
    private static final byte TYPE_PRIVILEGE_NAMES = -89;
    private static final byte TYPE_OTHER_ATTRIBUTES = -88;
    private static final long serialVersionUID = -5261978490319320250L;
    private final List<Attribute> otherAttributes;
    private final List<String> groupDNs;
    private final List<String> privilegeNames;
    private final Long idleTimeLimitSeconds;
    private final Long lookthroughLimit;
    private final Long sizeLimit;
    private final Long timeLimitSeconds;
    private final String clientConnectionPolicyName;
    private final String equivalentAuthzUserDN;
    
    GetUserResourceLimitsResponseControl() {
        this.otherAttributes = null;
        this.groupDNs = null;
        this.privilegeNames = null;
        this.idleTimeLimitSeconds = null;
        this.lookthroughLimit = null;
        this.sizeLimit = null;
        this.timeLimitSeconds = null;
        this.clientConnectionPolicyName = null;
        this.equivalentAuthzUserDN = null;
    }
    
    public GetUserResourceLimitsResponseControl(final Long sizeLimit, final Long timeLimitSeconds, final Long idleTimeLimitSeconds, final Long lookthroughLimit, final String equivalentAuthzUserDN, final String clientConnectionPolicyName) {
        this(sizeLimit, timeLimitSeconds, idleTimeLimitSeconds, lookthroughLimit, equivalentAuthzUserDN, clientConnectionPolicyName, null, null, null);
    }
    
    public GetUserResourceLimitsResponseControl(final Long sizeLimit, final Long timeLimitSeconds, final Long idleTimeLimitSeconds, final Long lookthroughLimit, final String equivalentAuthzUserDN, final String clientConnectionPolicyName, final List<String> groupDNs, final List<String> privilegeNames, final List<Attribute> otherAttributes) {
        super("1.3.6.1.4.1.30221.2.5.26", false, encodeValue(sizeLimit, timeLimitSeconds, idleTimeLimitSeconds, lookthroughLimit, equivalentAuthzUserDN, clientConnectionPolicyName, groupDNs, privilegeNames, otherAttributes));
        if (sizeLimit == null || sizeLimit > 0L) {
            this.sizeLimit = sizeLimit;
        }
        else {
            this.sizeLimit = -1L;
        }
        if (timeLimitSeconds == null || timeLimitSeconds > 0L) {
            this.timeLimitSeconds = timeLimitSeconds;
        }
        else {
            this.timeLimitSeconds = -1L;
        }
        if (idleTimeLimitSeconds == null || idleTimeLimitSeconds > 0L) {
            this.idleTimeLimitSeconds = idleTimeLimitSeconds;
        }
        else {
            this.idleTimeLimitSeconds = -1L;
        }
        if (lookthroughLimit == null || lookthroughLimit > 0L) {
            this.lookthroughLimit = lookthroughLimit;
        }
        else {
            this.lookthroughLimit = -1L;
        }
        this.equivalentAuthzUserDN = equivalentAuthzUserDN;
        this.clientConnectionPolicyName = clientConnectionPolicyName;
        if (groupDNs == null) {
            this.groupDNs = null;
        }
        else {
            this.groupDNs = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(groupDNs));
        }
        if (privilegeNames == null) {
            this.privilegeNames = null;
        }
        else {
            this.privilegeNames = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(privilegeNames));
        }
        if (otherAttributes == null) {
            this.otherAttributes = Collections.emptyList();
        }
        else {
            this.otherAttributes = Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(otherAttributes));
        }
    }
    
    private static ASN1OctetString encodeValue(final Long sizeLimit, final Long timeLimitSeconds, final Long idleTimeLimitSeconds, final Long lookthroughLimit, final String equivalentAuthzUserDN, final String clientConnectionPolicyName, final List<String> groupDNs, final List<String> privilegeNames, final List<Attribute> otherAttributes) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(10);
        if (sizeLimit != null) {
            if (sizeLimit > 0L) {
                elements.add(new ASN1Long((byte)(-128), sizeLimit));
            }
            else {
                elements.add(new ASN1Long((byte)(-128), -1L));
            }
        }
        if (timeLimitSeconds != null) {
            if (timeLimitSeconds > 0L) {
                elements.add(new ASN1Long((byte)(-127), timeLimitSeconds));
            }
            else {
                elements.add(new ASN1Long((byte)(-127), -1L));
            }
        }
        if (idleTimeLimitSeconds != null) {
            if (idleTimeLimitSeconds > 0L) {
                elements.add(new ASN1Long((byte)(-126), idleTimeLimitSeconds));
            }
            else {
                elements.add(new ASN1Long((byte)(-126), -1L));
            }
        }
        if (lookthroughLimit != null) {
            if (lookthroughLimit > 0L) {
                elements.add(new ASN1Long((byte)(-125), lookthroughLimit));
            }
            else {
                elements.add(new ASN1Long((byte)(-125), -1L));
            }
        }
        if (equivalentAuthzUserDN != null) {
            elements.add(new ASN1OctetString((byte)(-124), equivalentAuthzUserDN));
        }
        if (clientConnectionPolicyName != null) {
            elements.add(new ASN1OctetString((byte)(-123), clientConnectionPolicyName));
        }
        if (groupDNs != null) {
            final ArrayList<ASN1Element> dnElements = new ArrayList<ASN1Element>(groupDNs.size());
            for (final String s : groupDNs) {
                dnElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Set((byte)(-90), dnElements));
        }
        if (privilegeNames != null) {
            final ArrayList<ASN1Element> privElements = new ArrayList<ASN1Element>(privilegeNames.size());
            for (final String s : privilegeNames) {
                privElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Set((byte)(-89), privElements));
        }
        if (otherAttributes != null && !otherAttributes.isEmpty()) {
            final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(otherAttributes.size());
            for (final Attribute a : otherAttributes) {
                attrElements.add(a.encode());
            }
            elements.add(new ASN1Sequence((byte)(-88), attrElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public GetUserResourceLimitsResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_USER_RESOURCE_LIMITS_RESPONSE_MISSING_VALUE.get());
        }
        List<Attribute> oa = Collections.emptyList();
        List<String> gd = null;
        List<String> pn = null;
        Long sL = null;
        Long tL = null;
        Long iTL = null;
        Long lL = null;
        String eAUD = null;
        String cCPN = null;
        try {
            final ASN1Element[] arr$;
            final ASN1Element[] elements = arr$ = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            for (final ASN1Element e : arr$) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        sL = ASN1Long.decodeAsLong(e).longValue();
                        break;
                    }
                    case -127: {
                        tL = ASN1Long.decodeAsLong(e).longValue();
                        break;
                    }
                    case -126: {
                        iTL = ASN1Long.decodeAsLong(e).longValue();
                        break;
                    }
                    case -125: {
                        lL = ASN1Long.decodeAsLong(e).longValue();
                        break;
                    }
                    case -124: {
                        eAUD = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -123: {
                        cCPN = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -90: {
                        final ASN1Element[] groupElements = ASN1Set.decodeAsSet(e).elements();
                        gd = new ArrayList<String>(groupElements.length);
                        for (final ASN1Element pe : groupElements) {
                            gd.add(ASN1OctetString.decodeAsOctetString(pe).stringValue());
                        }
                        gd = Collections.unmodifiableList((List<? extends String>)gd);
                        break;
                    }
                    case -89: {
                        final ASN1Element[] privElements = ASN1Set.decodeAsSet(e).elements();
                        pn = new ArrayList<String>(privElements.length);
                        for (final ASN1Element pe2 : privElements) {
                            pn.add(ASN1OctetString.decodeAsOctetString(pe2).stringValue());
                        }
                        pn = Collections.unmodifiableList((List<? extends String>)pn);
                        break;
                    }
                    case -88: {
                        final ASN1Element[] attrElemnets = ASN1Sequence.decodeAsSequence(e).elements();
                        oa = new ArrayList<Attribute>(attrElemnets.length);
                        for (final ASN1Element ae : attrElemnets) {
                            oa.add(Attribute.decode(ASN1Sequence.decodeAsSequence(ae)));
                        }
                        oa = Collections.unmodifiableList((List<? extends Attribute>)oa);
                        break;
                    }
                }
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_USER_RESOURCE_LIMITS_RESPONSE_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        this.otherAttributes = oa;
        this.groupDNs = gd;
        this.privilegeNames = pn;
        this.sizeLimit = sL;
        this.timeLimitSeconds = tL;
        this.idleTimeLimitSeconds = iTL;
        this.lookthroughLimit = lL;
        this.equivalentAuthzUserDN = eAUD;
        this.clientConnectionPolicyName = cCPN;
    }
    
    @Override
    public GetUserResourceLimitsResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new GetUserResourceLimitsResponseControl(oid, isCritical, value);
    }
    
    public static GetUserResourceLimitsResponseControl get(final BindResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.26");
        if (c == null) {
            return null;
        }
        if (c instanceof GetUserResourceLimitsResponseControl) {
            return (GetUserResourceLimitsResponseControl)c;
        }
        return new GetUserResourceLimitsResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public Long getSizeLimit() {
        return this.sizeLimit;
    }
    
    public Long getTimeLimitSeconds() {
        return this.timeLimitSeconds;
    }
    
    public Long getIdleTimeLimitSeconds() {
        return this.idleTimeLimitSeconds;
    }
    
    public Long getLookthroughLimit() {
        return this.lookthroughLimit;
    }
    
    public String getEquivalentAuthzUserDN() {
        return this.equivalentAuthzUserDN;
    }
    
    public String getClientConnectionPolicyName() {
        return this.clientConnectionPolicyName;
    }
    
    public List<String> getGroupDNs() {
        return this.groupDNs;
    }
    
    public List<String> getPrivilegeNames() {
        return this.privilegeNames;
    }
    
    public List<Attribute> getOtherAttributes() {
        return this.otherAttributes;
    }
    
    public Attribute getOtherAttribute(final String name) {
        for (final Attribute a : this.otherAttributes) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GET_USER_RESOURCE_LIMITS_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetUserResourceLimitsResponseControl(");
        boolean added = false;
        if (this.sizeLimit != null) {
            buffer.append("sizeLimit=");
            buffer.append(this.sizeLimit);
            added = true;
        }
        if (this.timeLimitSeconds != null) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("timeLimitSeconds=");
            buffer.append(this.timeLimitSeconds);
            added = true;
        }
        if (this.idleTimeLimitSeconds != null) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("idleTimeLimitSeconds=");
            buffer.append(this.idleTimeLimitSeconds);
            added = true;
        }
        if (this.lookthroughLimit != null) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("lookthroughLimit=");
            buffer.append(this.lookthroughLimit);
            added = true;
        }
        if (this.equivalentAuthzUserDN != null) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("equivalentAuthzUserDN=\"");
            buffer.append(this.equivalentAuthzUserDN);
            buffer.append('\"');
            added = true;
        }
        if (this.clientConnectionPolicyName != null) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("clientConnectionPolicyName=\"");
            buffer.append(this.clientConnectionPolicyName);
            buffer.append('\"');
            added = true;
        }
        if (this.groupDNs != null) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("groupDNs={");
            final Iterator<String> dnIterator = this.groupDNs.iterator();
            while (dnIterator.hasNext()) {
                buffer.append('\"');
                buffer.append(dnIterator.next());
                buffer.append('\"');
                if (dnIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
            added = true;
        }
        if (this.privilegeNames != null) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("privilegeNames={");
            final Iterator<String> privilegeIterator = this.privilegeNames.iterator();
            while (privilegeIterator.hasNext()) {
                buffer.append('\"');
                buffer.append(privilegeIterator.next());
                buffer.append('\"');
                if (privilegeIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
            added = true;
        }
        if (!this.otherAttributes.isEmpty()) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("otherAttributes={");
            final Iterator<Attribute> attrIterator = this.otherAttributes.iterator();
            while (attrIterator.hasNext()) {
                attrIterator.next().toString(buffer);
                if (attrIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append("')");
    }
}
