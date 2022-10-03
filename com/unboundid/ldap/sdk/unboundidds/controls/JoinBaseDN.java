package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Null;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JoinBaseDN implements Serializable
{
    public static final byte BASE_TYPE_SEARCH_BASE = Byte.MIN_VALUE;
    public static final byte BASE_TYPE_SOURCE_ENTRY_DN = -127;
    public static final byte BASE_TYPE_CUSTOM = -126;
    private static final JoinBaseDN USE_SEARCH_BASE_DN;
    private static final JoinBaseDN USE_SOURCE_ENTRY_DN;
    private static final long serialVersionUID = -330303461586380445L;
    private final byte type;
    private final String customBaseDN;
    
    private JoinBaseDN(final byte type, final String customBaseDN) {
        this.type = type;
        this.customBaseDN = customBaseDN;
    }
    
    public static JoinBaseDN createUseSearchBaseDN() {
        return JoinBaseDN.USE_SEARCH_BASE_DN;
    }
    
    public static JoinBaseDN createUseSourceEntryDN() {
        return JoinBaseDN.USE_SOURCE_ENTRY_DN;
    }
    
    public static JoinBaseDN createUseCustomBaseDN(final String baseDN) {
        Validator.ensureNotNull(baseDN);
        return new JoinBaseDN((byte)(-126), baseDN);
    }
    
    public byte getType() {
        return this.type;
    }
    
    public String getCustomBaseDN() {
        return this.customBaseDN;
    }
    
    ASN1Element encode() {
        switch (this.type) {
            case Byte.MIN_VALUE:
            case -127: {
                return new ASN1Null(this.type);
            }
            case -126: {
                return new ASN1OctetString(this.type, this.customBaseDN);
            }
            default: {
                return null;
            }
        }
    }
    
    static JoinBaseDN decode(final ASN1Element element) throws LDAPException {
        switch (element.getType()) {
            case Byte.MIN_VALUE: {
                return JoinBaseDN.USE_SEARCH_BASE_DN;
            }
            case -127: {
                return JoinBaseDN.USE_SOURCE_ENTRY_DN;
            }
            case -126: {
                return new JoinBaseDN(element.getType(), element.decodeAsOctetString().stringValue());
            }
            default: {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_BASE_DECODE_INVALID_TYPE.get(StaticUtils.toHex(element.getType())));
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        switch (this.type) {
            case Byte.MIN_VALUE: {
                buffer.append("useSearchBaseDN");
                break;
            }
            case -127: {
                buffer.append("useSourceEntryDN");
                break;
            }
            case -126: {
                buffer.append("useCustomBaseDN(baseDN='");
                buffer.append(this.customBaseDN);
                buffer.append("')");
                break;
            }
        }
    }
    
    static {
        USE_SEARCH_BASE_DN = new JoinBaseDN((byte)(-128), null);
        USE_SOURCE_ENTRY_DN = new JoinBaseDN((byte)(-127), null);
    }
}
