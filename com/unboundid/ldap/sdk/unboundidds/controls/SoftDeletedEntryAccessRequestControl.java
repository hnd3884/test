package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SoftDeletedEntryAccessRequestControl extends Control
{
    public static final String SOFT_DELETED_ENTRY_ACCESS_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.24";
    private static final byte TYPE_INCLUDE_NON_SOFT_DELETED_ENTRIES = Byte.MIN_VALUE;
    private static final byte TYPE_RETURN_ENTRIES_IN_UNDELETED_FORM = -127;
    private static final long serialVersionUID = -3633807543861389512L;
    private final boolean includeNonSoftDeletedEntries;
    private final boolean returnEntriesInUndeletedForm;
    
    public SoftDeletedEntryAccessRequestControl() {
        this(false, true, false);
    }
    
    public SoftDeletedEntryAccessRequestControl(final boolean isCritical, final boolean includeNonSoftDeletedEntries, final boolean returnEntriesInUndeletedForm) {
        super("1.3.6.1.4.1.30221.2.5.24", isCritical, encodeValue(includeNonSoftDeletedEntries, returnEntriesInUndeletedForm));
        this.includeNonSoftDeletedEntries = includeNonSoftDeletedEntries;
        this.returnEntriesInUndeletedForm = returnEntriesInUndeletedForm;
    }
    
    public SoftDeletedEntryAccessRequestControl(final Control control) throws LDAPException {
        super(control);
        boolean includeNonSoftDeleted = true;
        boolean returnAsUndeleted = false;
        if (control.hasValue()) {
            try {
                final ASN1Sequence valueSequence = ASN1Sequence.decodeAsSequence(control.getValue().getValue());
                for (final ASN1Element e : valueSequence.elements()) {
                    switch (e.getType()) {
                        case Byte.MIN_VALUE: {
                            includeNonSoftDeleted = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                            break;
                        }
                        case -127: {
                            returnAsUndeleted = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                            break;
                        }
                        default: {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SOFT_DELETED_ACCESS_REQUEST_UNSUPPORTED_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
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
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SOFT_DELETED_ACCESS_REQUEST_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
            }
        }
        this.includeNonSoftDeletedEntries = includeNonSoftDeleted;
        this.returnEntriesInUndeletedForm = returnAsUndeleted;
    }
    
    private static ASN1OctetString encodeValue(final boolean includeNonSoftDeletedEntries, final boolean returnEntriesInUndeletedForm) {
        if (includeNonSoftDeletedEntries && !returnEntriesInUndeletedForm) {
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        if (!includeNonSoftDeletedEntries) {
            elements.add(new ASN1Boolean((byte)(-128), false));
        }
        if (returnEntriesInUndeletedForm) {
            elements.add(new ASN1Boolean((byte)(-127), true));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public boolean includeNonSoftDeletedEntries() {
        return this.includeNonSoftDeletedEntries;
    }
    
    public boolean returnEntriesInUndeletedForm() {
        return this.returnEntriesInUndeletedForm;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_SOFT_DELETED_ACCESS_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SoftDeletedEntryAccessRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", includeNonSoftDeletedEntries=");
        buffer.append(this.includeNonSoftDeletedEntries);
        buffer.append(", returnEntriesInUndeletedForm=");
        buffer.append(this.returnEntriesInUndeletedForm);
        buffer.append(')');
    }
}
