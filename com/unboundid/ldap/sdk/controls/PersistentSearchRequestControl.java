package com.unboundid.ldap.sdk.controls;

import java.util.Iterator;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collection;
import java.util.Set;
import java.util.EnumSet;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PersistentSearchRequestControl extends Control
{
    public static final String PERSISTENT_SEARCH_REQUEST_OID = "2.16.840.1.113730.3.4.3";
    private static final long serialVersionUID = 3532762682521779027L;
    private final boolean changesOnly;
    private final boolean returnECs;
    private final EnumSet<PersistentSearchChangeType> changeTypes;
    
    public PersistentSearchRequestControl(final PersistentSearchChangeType changeType, final boolean changesOnly, final boolean returnECs) {
        super("2.16.840.1.113730.3.4.3", true, encodeValue(changeType, changesOnly, returnECs));
        this.changeTypes = EnumSet.of(changeType);
        this.changesOnly = changesOnly;
        this.returnECs = returnECs;
    }
    
    public PersistentSearchRequestControl(final Set<PersistentSearchChangeType> changeTypes, final boolean changesOnly, final boolean returnECs) {
        super("2.16.840.1.113730.3.4.3", true, encodeValue(changeTypes, changesOnly, returnECs));
        this.changeTypes = EnumSet.copyOf(changeTypes);
        this.changesOnly = changesOnly;
        this.returnECs = returnECs;
    }
    
    public PersistentSearchRequestControl(final PersistentSearchChangeType changeType, final boolean changesOnly, final boolean returnECs, final boolean isCritical) {
        super("2.16.840.1.113730.3.4.3", isCritical, encodeValue(changeType, changesOnly, returnECs));
        this.changeTypes = EnumSet.of(changeType);
        this.changesOnly = changesOnly;
        this.returnECs = returnECs;
    }
    
    public PersistentSearchRequestControl(final Set<PersistentSearchChangeType> changeTypes, final boolean changesOnly, final boolean returnECs, final boolean isCritical) {
        super("2.16.840.1.113730.3.4.3", isCritical, encodeValue(changeTypes, changesOnly, returnECs));
        this.changeTypes = EnumSet.copyOf(changeTypes);
        this.changesOnly = changesOnly;
        this.returnECs = returnECs;
    }
    
    public PersistentSearchRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PSEARCH_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            this.changeTypes = EnumSet.copyOf(PersistentSearchChangeType.decodeChangeTypes(ASN1Integer.decodeAsInteger(elements[0]).intValue()));
            this.changesOnly = ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();
            this.returnECs = ASN1Boolean.decodeAsBoolean(elements[2]).booleanValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PSEARCH_CANNOT_DECODE.get(e), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final PersistentSearchChangeType changeType, final boolean changesOnly, final boolean returnECs) {
        Validator.ensureNotNull(changeType);
        final ASN1Element[] elements = { new ASN1Integer(changeType.intValue()), new ASN1Boolean(changesOnly), new ASN1Boolean(returnECs) };
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    private static ASN1OctetString encodeValue(final Set<PersistentSearchChangeType> changeTypes, final boolean changesOnly, final boolean returnECs) {
        Validator.ensureNotNull(changeTypes);
        Validator.ensureFalse(changeTypes.isEmpty(), "PersistentSearchRequestControl.changeTypes must not be empty.");
        final ASN1Element[] elements = { new ASN1Integer(PersistentSearchChangeType.encodeChangeTypes(changeTypes)), new ASN1Boolean(changesOnly), new ASN1Boolean(returnECs) };
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public Set<PersistentSearchChangeType> getChangeTypes() {
        return this.changeTypes;
    }
    
    public boolean changesOnly() {
        return this.changesOnly;
    }
    
    public boolean returnECs() {
        return this.returnECs;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PSEARCH_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PersistentSearchRequestControl(changeTypes={");
        final Iterator<PersistentSearchChangeType> iterator = this.changeTypes.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next().getName());
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, changesOnly=");
        buffer.append(this.changesOnly);
        buffer.append(", returnECs=");
        buffer.append(this.returnECs);
        buffer.append(", isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
