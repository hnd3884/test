package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Iterator;
import java.util.Collections;
import java.util.EnumSet;
import com.unboundid.util.Validator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SuppressOperationalAttributeUpdateRequestControl extends Control
{
    public static final String SUPPRESS_OP_ATTR_UPDATE_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.27";
    private static final byte TYPE_SUPPRESS_TYPES = Byte.MIN_VALUE;
    private static final long serialVersionUID = 4603958484615351672L;
    private final Set<SuppressType> suppressTypes;
    
    public SuppressOperationalAttributeUpdateRequestControl(final SuppressType... suppressTypes) {
        this(false, suppressTypes);
    }
    
    public SuppressOperationalAttributeUpdateRequestControl(final Collection<SuppressType> suppressTypes) {
        this(false, suppressTypes);
    }
    
    public SuppressOperationalAttributeUpdateRequestControl(final boolean isCritical, final SuppressType... suppressTypes) {
        this(isCritical, Arrays.asList(suppressTypes));
    }
    
    public SuppressOperationalAttributeUpdateRequestControl(final boolean isCritical, final Collection<SuppressType> suppressTypes) {
        super("1.3.6.1.4.1.30221.2.5.27", isCritical, encodeValue(suppressTypes));
        Validator.ensureFalse(suppressTypes.isEmpty());
        final EnumSet<SuppressType> s = EnumSet.noneOf(SuppressType.class);
        for (final SuppressType t : suppressTypes) {
            s.add(t);
        }
        this.suppressTypes = Collections.unmodifiableSet((Set<? extends SuppressType>)s);
    }
    
    public SuppressOperationalAttributeUpdateRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SUPPRESS_OP_ATTR_UPDATE_REQUEST_MISSING_VALUE.get());
        }
        try {
            final ASN1Sequence valueSequence = ASN1Sequence.decodeAsSequence(value.getValue());
            final ASN1Sequence suppressTypesSequence = ASN1Sequence.decodeAsSequence(valueSequence.elements()[0]);
            final EnumSet<SuppressType> s = EnumSet.noneOf(SuppressType.class);
            for (final ASN1Element e : suppressTypesSequence.elements()) {
                final ASN1Enumerated ae = ASN1Enumerated.decodeAsEnumerated(e);
                final SuppressType t = SuppressType.valueOf(ae.intValue());
                if (t == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SUPPRESS_OP_ATTR_UNRECOGNIZED_SUPPRESS_TYPE.get(ae.intValue()));
                }
                s.add(t);
            }
            this.suppressTypes = Collections.unmodifiableSet((Set<? extends SuppressType>)s);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SUPPRESS_OP_ATTR_UPDATE_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final Collection<SuppressType> suppressTypes) {
        final ArrayList<ASN1Element> suppressTypeElements = new ArrayList<ASN1Element>(suppressTypes.size());
        for (final SuppressType t : suppressTypes) {
            suppressTypeElements.add(new ASN1Enumerated(t.intValue()));
        }
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1Sequence((byte)(-128), suppressTypeElements) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public Set<SuppressType> getSuppressTypes() {
        return this.suppressTypes;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_SUPPRESS_OP_ATTR_UPDATE_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SuppressOperationalAttributeUpdateRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", suppressTypes={");
        final Iterator<SuppressType> iterator = this.suppressTypes.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next().name());
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append("})");
    }
}
