package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class HardDeleteRequestControl extends Control
{
    public static final String HARD_DELETE_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.22";
    private static final long serialVersionUID = 1169625153021056712L;
    
    public HardDeleteRequestControl() {
        this(false);
    }
    
    public HardDeleteRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.22", isCritical, null);
    }
    
    public HardDeleteRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            try {
                final ASN1Sequence valueSequence = ASN1Sequence.decodeAsSequence(control.getValue().getValue());
                final ASN1Element[] elements = valueSequence.elements();
                if (elements.length > 0) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_HARD_DELETE_REQUEST_UNSUPPORTED_VALUE_ELEMENT_TYPE.get(StaticUtils.toHex(elements[0].getType())));
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw le;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_HARD_DELETE_REQUEST_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
    }
    
    public static DeleteRequest createHardDeleteRequest(final String targetDN, final boolean isCritical) {
        final Control[] controls = { new HardDeleteRequestControl(isCritical) };
        return new DeleteRequest(targetDN, controls);
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_HARD_DELETE_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("HardDeleteRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
