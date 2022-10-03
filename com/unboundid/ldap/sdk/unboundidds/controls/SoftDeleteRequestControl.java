package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.DeleteRequest;
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
public final class SoftDeleteRequestControl extends Control
{
    public static final String SOFT_DELETE_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.20";
    private static final byte TYPE_RETURN_SOFT_DELETE_RESPONSE = Byte.MIN_VALUE;
    private static final long serialVersionUID = 4068029406430690545L;
    private final boolean returnSoftDeleteResponse;
    
    public SoftDeleteRequestControl() {
        this(true, true);
    }
    
    public SoftDeleteRequestControl(final boolean isCritical, final boolean returnSoftDeleteResponse) {
        super("1.3.6.1.4.1.30221.2.5.20", isCritical, encodeValue(returnSoftDeleteResponse));
        this.returnSoftDeleteResponse = returnSoftDeleteResponse;
    }
    
    public SoftDeleteRequestControl(final Control control) throws LDAPException {
        super(control);
        boolean returnResponse = true;
        if (control.hasValue()) {
            try {
                final ASN1Sequence valueSequence = ASN1Sequence.decodeAsSequence(control.getValue().getValue());
                final ASN1Element[] arr$ = valueSequence.elements();
                final int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    final ASN1Element e = arr$[i$];
                    switch (e.getType()) {
                        case Byte.MIN_VALUE: {
                            returnResponse = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                            ++i$;
                            continue;
                        }
                        default: {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SOFT_DELETE_REQUEST_UNSUPPORTED_VALUE_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
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
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SOFT_DELETE_REQUEST_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
            }
        }
        this.returnSoftDeleteResponse = returnResponse;
    }
    
    private static ASN1OctetString encodeValue(final boolean returnSoftDeleteResponse) {
        if (returnSoftDeleteResponse) {
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(1);
        elements.add(new ASN1Boolean((byte)(-128), false));
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public boolean returnSoftDeleteResponse() {
        return this.returnSoftDeleteResponse;
    }
    
    public static DeleteRequest createSoftDeleteRequest(final String targetDN, final boolean isCritical, final boolean returnSoftDeleteResponse) {
        final Control[] controls = { new SoftDeleteRequestControl(isCritical, returnSoftDeleteResponse) };
        return new DeleteRequest(targetDN, controls);
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_SOFT_DELETE_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SoftDeleteRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", returnSoftDeleteResponse=");
        buffer.append(this.returnSoftDeleteResponse);
        buffer.append(')');
    }
}
