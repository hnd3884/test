package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Control;
import java.util.Collection;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SetSubtreeAccessibilityExtendedRequest extends ExtendedRequest
{
    public static final String SET_SUBTREE_ACCESSIBILITY_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.19";
    private static final byte TYPE_BYPASS_USER_DN = Byte.MIN_VALUE;
    private static final byte TYPE_ADDITIONAL_SUBTREE_BASE_DNS = -95;
    private static final long serialVersionUID = -3003738735546060245L;
    private final List<String> subtreeBaseDNs;
    private final String bypassUserDN;
    private final SubtreeAccessibilityState accessibilityState;
    
    private SetSubtreeAccessibilityExtendedRequest(final Collection<String> subtreeBaseDNs, final SubtreeAccessibilityState accessibilityState, final String bypassUserDN, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.19", encodeValue(subtreeBaseDNs, accessibilityState, bypassUserDN), controls);
        this.subtreeBaseDNs = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(subtreeBaseDNs));
        this.accessibilityState = accessibilityState;
        this.bypassUserDN = bypassUserDN;
    }
    
    private static ASN1OctetString encodeValue(final Collection<String> subtreeBaseDNs, final SubtreeAccessibilityState accessibilityState, final String bypassUserDN) {
        final Iterator<String> dnIterator = subtreeBaseDNs.iterator();
        final String subtreeBaseDN = dnIterator.next();
        Validator.ensureNotNull(subtreeBaseDN);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        elements.add(new ASN1OctetString(subtreeBaseDN));
        elements.add(new ASN1Enumerated(accessibilityState.intValue()));
        if (bypassUserDN != null) {
            elements.add(new ASN1OctetString((byte)(-128), bypassUserDN));
        }
        if (dnIterator.hasNext()) {
            final ArrayList<ASN1Element> additionalDNElements = new ArrayList<ASN1Element>(subtreeBaseDNs.size() - 1);
            while (dnIterator.hasNext()) {
                final String additionalDN = dnIterator.next();
                Validator.ensureNotNull(additionalDN);
                additionalDNElements.add(new ASN1OctetString(additionalDN));
            }
            elements.add(new ASN1Sequence((byte)(-95), additionalDNElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public SetSubtreeAccessibilityExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_SUBTREE_ACCESSIBILITY_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            final List<String> baseDNs = new ArrayList<String>(10);
            baseDNs.add(ASN1OctetString.decodeAsOctetString(elements[0]).stringValue());
            final int accessibilityStateValue = ASN1Enumerated.decodeAsEnumerated(elements[1]).intValue();
            this.accessibilityState = SubtreeAccessibilityState.valueOf(accessibilityStateValue);
            if (this.accessibilityState == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_SUBTREE_ACCESSIBILITY_INVALID_ACCESSIBILITY_STATE.get(accessibilityStateValue));
            }
            String bypassDN = null;
            for (int i = 2; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case Byte.MIN_VALUE: {
                        bypassDN = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -95: {
                        for (final ASN1Element e : ASN1Sequence.decodeAsSequence(elements[i]).elements()) {
                            baseDNs.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
                        }
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_SUBTREE_ACCESSIBILITY_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(elements[i].getType())));
                    }
                }
            }
            this.bypassUserDN = bypassDN;
            this.subtreeBaseDNs = Collections.unmodifiableList((List<? extends String>)baseDNs);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_SUBTREE_ACCESSIBILITY_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (this.accessibilityState == SubtreeAccessibilityState.ACCESSIBLE && this.bypassUserDN != null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_SUBTREE_ACCESSIBILITY_UNEXPECTED_BYPASS_DN.get(this.accessibilityState.getStateName()));
        }
    }
    
    public static SetSubtreeAccessibilityExtendedRequest createSetAccessibleRequest(final String subtreeBaseDN, final Control... controls) {
        Validator.ensureNotNull(subtreeBaseDN);
        return new SetSubtreeAccessibilityExtendedRequest(Collections.singletonList(subtreeBaseDN), SubtreeAccessibilityState.ACCESSIBLE, null, controls);
    }
    
    public static SetSubtreeAccessibilityExtendedRequest createSetAccessibleRequest(final Collection<String> subtreeBaseDNs, final Control... controls) {
        Validator.ensureNotNull(subtreeBaseDNs);
        Validator.ensureFalse(subtreeBaseDNs.isEmpty());
        return new SetSubtreeAccessibilityExtendedRequest(subtreeBaseDNs, SubtreeAccessibilityState.ACCESSIBLE, null, controls);
    }
    
    public static SetSubtreeAccessibilityExtendedRequest createSetReadOnlyRequest(final String subtreeBaseDN, final boolean allowBind, final String bypassUserDN, final Control... controls) {
        Validator.ensureNotNull(subtreeBaseDN);
        if (allowBind) {
            return new SetSubtreeAccessibilityExtendedRequest(Collections.singletonList(subtreeBaseDN), SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED, bypassUserDN, controls);
        }
        return new SetSubtreeAccessibilityExtendedRequest(Collections.singletonList(subtreeBaseDN), SubtreeAccessibilityState.READ_ONLY_BIND_DENIED, bypassUserDN, controls);
    }
    
    public static SetSubtreeAccessibilityExtendedRequest createSetReadOnlyRequest(final Collection<String> subtreeBaseDNs, final boolean allowBind, final String bypassUserDN, final Control... controls) {
        Validator.ensureNotNull(subtreeBaseDNs);
        Validator.ensureFalse(subtreeBaseDNs.isEmpty());
        if (allowBind) {
            return new SetSubtreeAccessibilityExtendedRequest(subtreeBaseDNs, SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED, bypassUserDN, controls);
        }
        return new SetSubtreeAccessibilityExtendedRequest(subtreeBaseDNs, SubtreeAccessibilityState.READ_ONLY_BIND_DENIED, bypassUserDN, controls);
    }
    
    public static SetSubtreeAccessibilityExtendedRequest createSetHiddenRequest(final String subtreeBaseDN, final String bypassUserDN, final Control... controls) {
        Validator.ensureNotNull(subtreeBaseDN);
        return new SetSubtreeAccessibilityExtendedRequest(Collections.singletonList(subtreeBaseDN), SubtreeAccessibilityState.HIDDEN, bypassUserDN, controls);
    }
    
    public static SetSubtreeAccessibilityExtendedRequest createSetHiddenRequest(final Collection<String> subtreeBaseDNs, final String bypassUserDN, final Control... controls) {
        Validator.ensureNotNull(subtreeBaseDNs);
        Validator.ensureFalse(subtreeBaseDNs.isEmpty());
        return new SetSubtreeAccessibilityExtendedRequest(subtreeBaseDNs, SubtreeAccessibilityState.HIDDEN, bypassUserDN, controls);
    }
    
    public String getSubtreeBaseDN() {
        return this.subtreeBaseDNs.get(0);
    }
    
    public List<String> getSubtreeBaseDNs() {
        return this.subtreeBaseDNs;
    }
    
    public SubtreeAccessibilityState getAccessibilityState() {
        return this.accessibilityState;
    }
    
    public String getBypassUserDN() {
        return this.bypassUserDN;
    }
    
    @Override
    public SetSubtreeAccessibilityExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public SetSubtreeAccessibilityExtendedRequest duplicate(final Control[] controls) {
        return new SetSubtreeAccessibilityExtendedRequest(this.subtreeBaseDNs, this.accessibilityState, this.bypassUserDN, controls);
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_SET_SUBTREE_ACCESSIBILITY.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SetSubtreeAccessibilityExtendedRequest(baseDNs={");
        final Iterator<String> dnIterator = this.subtreeBaseDNs.iterator();
        while (dnIterator.hasNext()) {
            buffer.append('\"');
            buffer.append(dnIterator.next());
            buffer.append('\"');
            if (dnIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, accessibilityType=\"");
        buffer.append(this.accessibilityState.getStateName());
        buffer.append('\"');
        if (this.bypassUserDN != null) {
            buffer.append(", bypassUserDN=\"");
            buffer.append(this.bypassUserDN);
            buffer.append('\"');
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
}
