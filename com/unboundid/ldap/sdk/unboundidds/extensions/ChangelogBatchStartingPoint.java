package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Date;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class ChangelogBatchStartingPoint implements Serializable
{
    private static final long serialVersionUID = -1580168275337643812L;
    
    public abstract ASN1Element encode();
    
    public static ChangelogBatchStartingPoint decode(final ASN1Element element) throws LDAPException {
        Validator.ensureNotNull(element);
        switch (element.getType()) {
            case Byte.MIN_VALUE: {
                return new ResumeWithTokenStartingPoint(ASN1OctetString.decodeAsOctetString(element));
            }
            case -127: {
                return new ResumeWithCSNStartingPoint(ASN1OctetString.decodeAsOctetString(element).stringValue());
            }
            case -126: {
                if (element.getValueLength() != 0) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_BEGINNING_OF_CHANGELOG_STARTING_POINT_HAS_VALUE.get());
                }
                return new BeginningOfChangelogStartingPoint();
            }
            case -125: {
                if (element.getValueLength() != 0) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_OF_CHANGELOG_STARTING_POINT_HAS_VALUE.get());
                }
                return new EndOfChangelogStartingPoint();
            }
            case -124: {
                Date time;
                try {
                    time = StaticUtils.decodeGeneralizedTime(ASN1OctetString.decodeAsOctetString(element).stringValue());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CHANGE_TIME_STARTING_POINT_MALFORMED_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
                }
                return new ChangeTimeStartingPoint(time.getTime());
            }
            default: {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_UNKNOWN_CHANGELOG_BATCH_STARTING_POINT_TYPE.get(StaticUtils.toHex(element.getType())));
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
