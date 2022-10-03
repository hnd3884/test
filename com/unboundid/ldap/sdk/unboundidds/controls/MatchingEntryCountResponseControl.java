package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.SearchResult;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Null;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MatchingEntryCountResponseControl extends Control implements DecodeableControl
{
    public static final String MATCHING_ENTRY_COUNT_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.37";
    private static final byte TYPE_DEBUG_INFO = -96;
    private static final byte TYPE_SEARCH_INDEXED = -127;
    private static final long serialVersionUID = -5488025806310455564L;
    private final boolean searchIndexed;
    private final int countValue;
    private final List<String> debugInfo;
    private final MatchingEntryCountType countType;
    
    MatchingEntryCountResponseControl() {
        this.searchIndexed = false;
        this.countType = null;
        this.countValue = -1;
        this.debugInfo = null;
    }
    
    private MatchingEntryCountResponseControl(final MatchingEntryCountType countType, final int countValue, final boolean searchIndexed, final Collection<String> debugInfo) {
        super("1.3.6.1.4.1.30221.2.5.37", false, encodeValue(countType, countValue, searchIndexed, debugInfo));
        this.countType = countType;
        this.countValue = countValue;
        this.searchIndexed = searchIndexed;
        if (debugInfo == null) {
            this.debugInfo = Collections.emptyList();
        }
        else {
            this.debugInfo = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(debugInfo));
        }
    }
    
    public MatchingEntryCountResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MATCHING_ENTRY_COUNT_RESPONSE_MISSING_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.countType = MatchingEntryCountType.valueOf(elements[0].getType());
            if (this.countType == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MATCHING_ENTRY_COUNT_RESPONSE_INVALID_COUNT_TYPE.get(StaticUtils.toHex(elements[0].getType())));
            }
            switch (this.countType) {
                case EXAMINED_COUNT:
                case UNEXAMINED_COUNT: {
                    this.countValue = ASN1Integer.decodeAsInteger(elements[0]).intValue();
                    if (this.countValue < 0) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MATCHING_ENTRY_COUNT_RESPONSE_NEGATIVE_EXACT_COUNT.get());
                    }
                    break;
                }
                case UPPER_BOUND: {
                    this.countValue = ASN1Integer.decodeAsInteger(elements[0]).intValue();
                    if (this.countValue <= 0) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MATCHING_ENTRY_COUNT_RESPONSE_NON_POSITIVE_UPPER_BOUND.get());
                    }
                    break;
                }
                default: {
                    this.countValue = -1;
                    break;
                }
            }
            boolean isIndexed = this.countType != MatchingEntryCountType.UNKNOWN;
            List<String> debugMessages = Collections.emptyList();
            for (int i = 1; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case -96: {
                        final ASN1Element[] debugElements = ASN1Sequence.decodeAsSequence(elements[i]).elements();
                        debugMessages = new ArrayList<String>(debugElements.length);
                        for (final ASN1Element e : debugElements) {
                            debugMessages.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
                        }
                        break;
                    }
                    case -127: {
                        isIndexed = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MATCHING_ENTRY_COUNT_RESPONSE_UNKNOWN_ELEMENT_TYPE.get(StaticUtils.toHex(elements[i].getType())));
                    }
                }
            }
            this.searchIndexed = isIndexed;
            this.debugInfo = Collections.unmodifiableList((List<? extends String>)debugMessages);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_BACKEND_SET_ID_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public static MatchingEntryCountResponseControl createExactCountResponse(final int count, final boolean examined, final Collection<String> debugInfo) {
        return createExactCountResponse(count, examined, true, debugInfo);
    }
    
    public static MatchingEntryCountResponseControl createExactCountResponse(final int count, final boolean examined, final boolean searchIndexed, final Collection<String> debugInfo) {
        Validator.ensureTrue(count >= 0);
        MatchingEntryCountType countType;
        if (examined) {
            countType = MatchingEntryCountType.EXAMINED_COUNT;
        }
        else {
            countType = MatchingEntryCountType.UNEXAMINED_COUNT;
        }
        return new MatchingEntryCountResponseControl(countType, count, searchIndexed, debugInfo);
    }
    
    public static MatchingEntryCountResponseControl createUpperBoundResponse(final int upperBound, final Collection<String> debugInfo) {
        return createUpperBoundResponse(upperBound, true, debugInfo);
    }
    
    public static MatchingEntryCountResponseControl createUpperBoundResponse(final int upperBound, final boolean searchIndexed, final Collection<String> debugInfo) {
        Validator.ensureTrue(upperBound > 0);
        return new MatchingEntryCountResponseControl(MatchingEntryCountType.UPPER_BOUND, upperBound, searchIndexed, debugInfo);
    }
    
    public static MatchingEntryCountResponseControl createUnknownCountResponse(final Collection<String> debugInfo) {
        return new MatchingEntryCountResponseControl(MatchingEntryCountType.UNKNOWN, -1, false, debugInfo);
    }
    
    private static ASN1OctetString encodeValue(final MatchingEntryCountType countType, final int countValue, final boolean searchIndexed, final Collection<String> debugInfo) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        switch (countType) {
            case EXAMINED_COUNT:
            case UNEXAMINED_COUNT:
            case UPPER_BOUND: {
                elements.add(new ASN1Integer(countType.getBERType(), countValue));
                break;
            }
            case UNKNOWN: {
                elements.add(new ASN1Null(countType.getBERType()));
                break;
            }
        }
        if (debugInfo != null) {
            final ArrayList<ASN1Element> debugElements = new ArrayList<ASN1Element>(debugInfo.size());
            for (final String s : debugInfo) {
                debugElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-96), debugElements));
        }
        if (!searchIndexed) {
            elements.add(new ASN1Boolean((byte)(-127), searchIndexed));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public MatchingEntryCountType getCountType() {
        return this.countType;
    }
    
    public int getCountValue() {
        return this.countValue;
    }
    
    public boolean searchIndexed() {
        return this.searchIndexed;
    }
    
    public List<String> getDebugInfo() {
        return this.debugInfo;
    }
    
    @Override
    public MatchingEntryCountResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new MatchingEntryCountResponseControl(oid, isCritical, value);
    }
    
    public static MatchingEntryCountResponseControl get(final SearchResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.37");
        if (c == null) {
            return null;
        }
        if (c instanceof MatchingEntryCountResponseControl) {
            return (MatchingEntryCountResponseControl)c;
        }
        return new MatchingEntryCountResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_MATCHING_ENTRY_COUNT_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("MatchingEntryCountResponseControl(countType='");
        buffer.append(this.countType.name());
        buffer.append('\'');
        switch (this.countType) {
            case EXAMINED_COUNT:
            case UNEXAMINED_COUNT: {
                buffer.append(", count=");
                buffer.append(this.countValue);
                break;
            }
            case UPPER_BOUND: {
                buffer.append(", upperBound=");
                buffer.append(this.countValue);
                break;
            }
        }
        buffer.append(", searchIndexed=");
        buffer.append(this.searchIndexed);
        if (!this.debugInfo.isEmpty()) {
            buffer.append(", debugInfo={");
            final Iterator<String> iterator = this.debugInfo.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
