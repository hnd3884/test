package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MatchingEntryCountRequestControl extends Control
{
    public static final String MATCHING_ENTRY_COUNT_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.36";
    private static final byte TYPE_MAX_CANDIDATES_TO_EXAMINE = Byte.MIN_VALUE;
    private static final byte TYPE_ALWAYS_EXAMINE_CANDIDATES = -127;
    private static final byte TYPE_PROCESS_SEARCH_IF_UNINDEXED = -126;
    private static final byte TYPE_INCLUDE_DEBUG_INFO = -125;
    private static final byte TYPE_SKIP_RESOLVING_EXPLODED_INDEXES = -124;
    private static final byte TYPE_FAST_SHORT_CIRCUIT_THRESHOLD = -123;
    private static final byte TYPE_SLOW_SHORT_CIRCUIT_THRESHOLD = -122;
    private static final long serialVersionUID = 7981532783303485308L;
    private final boolean alwaysExamineCandidates;
    private final boolean includeDebugInfo;
    private final boolean processSearchIfUnindexed;
    private final boolean skipResolvingExplodedIndexes;
    private final int maxCandidatesToExamine;
    private final Long slowShortCircuitThreshold;
    private final Long fastShortCircuitThreshold;
    
    public MatchingEntryCountRequestControl() {
        this(true, 0, false, false, false);
    }
    
    public MatchingEntryCountRequestControl(final boolean isCritical, final int maxCandidatesToExamine, final boolean alwaysExamineCandidates, final boolean processSearchIfUnindexed, final boolean includeDebugInfo) {
        this(isCritical, maxCandidatesToExamine, alwaysExamineCandidates, processSearchIfUnindexed, false, null, null, includeDebugInfo);
    }
    
    public MatchingEntryCountRequestControl(final boolean isCritical, final int maxCandidatesToExamine, final boolean alwaysExamineCandidates, final boolean processSearchIfUnindexed, final boolean skipResolvingExplodedIndexes, final Long fastShortCircuitThreshold, final Long slowShortCircuitThreshold, final boolean includeDebugInfo) {
        super("1.3.6.1.4.1.30221.2.5.36", isCritical, encodeValue(maxCandidatesToExamine, alwaysExamineCandidates, processSearchIfUnindexed, skipResolvingExplodedIndexes, fastShortCircuitThreshold, slowShortCircuitThreshold, includeDebugInfo));
        Validator.ensureTrue(maxCandidatesToExamine >= 0);
        this.maxCandidatesToExamine = maxCandidatesToExamine;
        this.alwaysExamineCandidates = alwaysExamineCandidates;
        this.processSearchIfUnindexed = processSearchIfUnindexed;
        this.skipResolvingExplodedIndexes = skipResolvingExplodedIndexes;
        this.includeDebugInfo = includeDebugInfo;
        if (fastShortCircuitThreshold == null) {
            this.fastShortCircuitThreshold = null;
        }
        else {
            this.fastShortCircuitThreshold = Math.max(0L, fastShortCircuitThreshold);
        }
        if (slowShortCircuitThreshold == null) {
            this.slowShortCircuitThreshold = null;
        }
        else {
            this.slowShortCircuitThreshold = Math.max(0L, slowShortCircuitThreshold);
        }
    }
    
    public MatchingEntryCountRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MATCHING_ENTRY_COUNT_REQUEST_MISSING_VALUE.get());
        }
        try {
            boolean alwaysExamine = false;
            boolean debug = false;
            boolean processUnindexed = false;
            boolean skipExploded = false;
            int maxCandidates = 0;
            Long fastSCThreshold = null;
            Long slowSCThreshold = null;
            final ASN1Element[] arr$;
            final ASN1Element[] elements = arr$ = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            for (final ASN1Element e : arr$) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        maxCandidates = ASN1Integer.decodeAsInteger(e).intValue();
                        if (maxCandidates < 0) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MATCHING_ENTRY_COUNT_REQUEST_INVALID_MAX.get());
                        }
                        break;
                    }
                    case -127: {
                        alwaysExamine = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -126: {
                        processUnindexed = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -125: {
                        debug = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -124: {
                        skipExploded = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -123: {
                        fastSCThreshold = Math.max(0L, ASN1Long.decodeAsLong(e).longValue());
                        break;
                    }
                    case -122: {
                        slowSCThreshold = Math.max(0L, ASN1Long.decodeAsLong(e).longValue());
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MATCHING_ENTRY_COUNT_REQUEST_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            this.maxCandidatesToExamine = maxCandidates;
            this.alwaysExamineCandidates = alwaysExamine;
            this.processSearchIfUnindexed = processUnindexed;
            this.includeDebugInfo = debug;
            this.skipResolvingExplodedIndexes = skipExploded;
            this.fastShortCircuitThreshold = fastSCThreshold;
            this.slowShortCircuitThreshold = slowSCThreshold;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MATCHING_ENTRY_COUNT_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final int maxCandidatesToExamine, final boolean alwaysExamineCandidates, final boolean processSearchIfUnindexed, final boolean skipResolvingExplodedIndexes, final Long fastShortCircuitThreshold, final Long slowShortCircuitThreshold, final boolean includeDebugInfo) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        if (maxCandidatesToExamine > 0) {
            elements.add(new ASN1Integer((byte)(-128), maxCandidatesToExamine));
        }
        if (alwaysExamineCandidates) {
            elements.add(new ASN1Boolean((byte)(-127), true));
        }
        if (processSearchIfUnindexed) {
            elements.add(new ASN1Boolean((byte)(-126), true));
        }
        if (includeDebugInfo) {
            elements.add(new ASN1Boolean((byte)(-125), true));
        }
        if (skipResolvingExplodedIndexes) {
            elements.add(new ASN1Boolean((byte)(-124), true));
        }
        if (fastShortCircuitThreshold != null) {
            elements.add(new ASN1Long((byte)(-123), Math.max(0L, fastShortCircuitThreshold)));
        }
        if (slowShortCircuitThreshold != null) {
            elements.add(new ASN1Long((byte)(-122), Math.max(0L, slowShortCircuitThreshold)));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public int getMaxCandidatesToExamine() {
        return this.maxCandidatesToExamine;
    }
    
    public boolean alwaysExamineCandidates() {
        return this.alwaysExamineCandidates;
    }
    
    public boolean processSearchIfUnindexed() {
        return this.processSearchIfUnindexed;
    }
    
    public boolean skipResolvingExplodedIndexes() {
        return this.skipResolvingExplodedIndexes;
    }
    
    public Long getFastShortCircuitThreshold() {
        return this.fastShortCircuitThreshold;
    }
    
    public Long getSlowShortCircuitThreshold() {
        return this.slowShortCircuitThreshold;
    }
    
    public boolean includeDebugInfo() {
        return this.includeDebugInfo;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_MATCHING_ENTRY_COUNT_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("MatchingEntryCountRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", maxCandidatesToExamine=");
        buffer.append(this.maxCandidatesToExamine);
        buffer.append(", alwaysExamineCandidates=");
        buffer.append(this.alwaysExamineCandidates);
        buffer.append(", processSearchIfUnindexed=");
        buffer.append(this.processSearchIfUnindexed);
        buffer.append(", skipResolvingExplodedIndexes=");
        buffer.append(this.skipResolvingExplodedIndexes);
        buffer.append(", fastShortCircuitThreshold=");
        buffer.append(this.fastShortCircuitThreshold);
        buffer.append(", slowShortCircuitThreshold=");
        buffer.append(this.slowShortCircuitThreshold);
        buffer.append(", includeDebugInfo=");
        buffer.append(this.includeDebugInfo);
        buffer.append(')');
    }
}
