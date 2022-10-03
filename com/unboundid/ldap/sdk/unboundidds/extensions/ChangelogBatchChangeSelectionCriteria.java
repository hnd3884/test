package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class ChangelogBatchChangeSelectionCriteria
{
    static final byte TYPE_SELECTION_CRITERIA = -89;
    
    public final ASN1Element encode() {
        return new ASN1Element((byte)(-89), this.encodeInnerElement().encode());
    }
    
    protected abstract ASN1Element encodeInnerElement();
    
    public static ChangelogBatchChangeSelectionCriteria decode(final ASN1Element element) throws LDAPException {
        Validator.ensureNotNull(element);
        ASN1Element innerElement;
        try {
            innerElement = ASN1Element.decode(element.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CLBATCH_CHANGE_SELECTION_CRITERIA_DECODE_INNER_FAILURE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        switch (innerElement.getType()) {
            case -95: {
                return AnyAttributesChangeSelectionCriteria.decodeInnerElement(innerElement);
            }
            case -94: {
                return AllAttributesChangeSelectionCriteria.decodeInnerElement(innerElement);
            }
            case -93: {
                return IgnoreAttributesChangeSelectionCriteria.decodeInnerElement(innerElement);
            }
            case -124: {
                return NotificationDestinationChangeSelectionCriteria.decodeInnerElement(innerElement);
            }
            default: {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CLBATCH_CHANGE_SELECTION_CRITERIA_UNKNOWN_TYPE.get(StaticUtils.toHex(innerElement.getType())));
            }
        }
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public abstract void toString(final StringBuilder p0);
}
