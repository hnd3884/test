package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.IntermediateResponseListener;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.EnumSet;
import java.util.Collections;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ChangeType;
import java.util.Set;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetChangelogBatchExtendedRequest extends ExtendedRequest
{
    public static final String GET_CHANGELOG_BATCH_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.10";
    private static final byte TYPE_MAX_TIME = Byte.MIN_VALUE;
    private static final byte TYPE_WAIT_FOR_MAX_CHANGES = -127;
    private static final byte TYPE_INCLUDE_BASE = -94;
    private static final byte TYPE_EXCLUDE_BASE = -93;
    private static final byte TYPE_CHANGE_TYPES = -92;
    private static final byte TYPE_CONTINUE_ON_MISSING_CHANGES = -123;
    private static final byte TYPE_PARE_ENTRIES_FOR_USER_DN = -122;
    private static final byte TYPE_INCLUDE_SOFT_DELETED_ENTRY_MODS = -120;
    private static final byte TYPE_INCLUDE_SOFT_DELETED_ENTRY_DELETES = -119;
    private static final int CHANGE_TYPE_ADD = 0;
    private static final int CHANGE_TYPE_DELETE = 1;
    private static final int CHANGE_TYPE_MODIFY = 2;
    private static final int CHANGE_TYPE_MODIFY_DN = 3;
    private static final long serialVersionUID = 3270898150012821635L;
    private final boolean continueOnMissingChanges;
    private final boolean includeSoftDeletedEntryDeletes;
    private final boolean includeSoftDeletedEntryMods;
    private final boolean waitForMaxChanges;
    private final ChangelogBatchChangeSelectionCriteria changeSelectionCriteria;
    private final ChangelogBatchStartingPoint startingPoint;
    private final ChangelogEntryListener entryListener;
    private final int maxChanges;
    private final List<String> excludeBaseDNs;
    private final List<String> includeBaseDNs;
    private final long maxWaitTimeMillis;
    private final Set<ChangeType> changeTypes;
    private final String pareEntriesForUserDN;
    
    public GetChangelogBatchExtendedRequest(final ChangelogBatchStartingPoint startingPoint, final int maxChanges, final long maxWaitTimeMillis, final Control... controls) {
        this(null, startingPoint, maxChanges, maxWaitTimeMillis, false, null, null, null, false, null, null, false, false, controls);
    }
    
    public GetChangelogBatchExtendedRequest(final ChangelogEntryListener entryListener, final ChangelogBatchStartingPoint startingPoint, final int maxChanges, final long maxWaitTimeMillis, final Control... controls) {
        this(entryListener, startingPoint, maxChanges, maxWaitTimeMillis, false, null, null, null, false, null, null, false, false, controls);
    }
    
    public GetChangelogBatchExtendedRequest(final ChangelogBatchStartingPoint startingPoint, final int maxChanges, final long maxWaitTimeMillis, final boolean waitForMaxChanges, final List<String> includeBaseDNs, final List<String> excludeBaseDNs, final Set<ChangeType> changeTypes, final boolean continueOnMissingChanges, final Control... controls) {
        this(null, startingPoint, maxChanges, maxWaitTimeMillis, waitForMaxChanges, includeBaseDNs, excludeBaseDNs, changeTypes, continueOnMissingChanges, null, null, false, false, controls);
    }
    
    public GetChangelogBatchExtendedRequest(final ChangelogEntryListener entryListener, final ChangelogBatchStartingPoint startingPoint, final int maxChanges, final long maxWaitTimeMillis, final boolean waitForMaxChanges, final List<String> includeBaseDNs, final List<String> excludeBaseDNs, final Set<ChangeType> changeTypes, final boolean continueOnMissingChanges, final Control... controls) {
        this(entryListener, startingPoint, maxChanges, maxWaitTimeMillis, waitForMaxChanges, includeBaseDNs, excludeBaseDNs, changeTypes, continueOnMissingChanges, null, null, false, false, controls);
    }
    
    public GetChangelogBatchExtendedRequest(final ChangelogEntryListener entryListener, final ChangelogBatchStartingPoint startingPoint, final int maxChanges, final long maxWaitTimeMillis, final boolean waitForMaxChanges, final List<String> includeBaseDNs, final List<String> excludeBaseDNs, final Set<ChangeType> changeTypes, final boolean continueOnMissingChanges, final String pareEntriesForUserDN, final ChangelogBatchChangeSelectionCriteria changeSelectionCriteria, final Control... controls) {
        this(entryListener, startingPoint, maxChanges, maxWaitTimeMillis, waitForMaxChanges, includeBaseDNs, excludeBaseDNs, changeTypes, continueOnMissingChanges, pareEntriesForUserDN, changeSelectionCriteria, false, false, controls);
    }
    
    public GetChangelogBatchExtendedRequest(final ChangelogEntryListener entryListener, final ChangelogBatchStartingPoint startingPoint, final int maxChanges, final long maxWaitTimeMillis, final boolean waitForMaxChanges, final List<String> includeBaseDNs, final List<String> excludeBaseDNs, final Set<ChangeType> changeTypes, final boolean continueOnMissingChanges, final String pareEntriesForUserDN, final ChangelogBatchChangeSelectionCriteria changeSelectionCriteria, final boolean includeSoftDeletedEntryMods, final boolean includeSoftDeletedEntryDeletes, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.10", encodeValue(startingPoint, maxChanges, maxWaitTimeMillis, waitForMaxChanges, includeBaseDNs, excludeBaseDNs, changeTypes, continueOnMissingChanges, pareEntriesForUserDN, changeSelectionCriteria, includeSoftDeletedEntryMods, includeSoftDeletedEntryDeletes), controls);
        this.entryListener = entryListener;
        this.startingPoint = startingPoint;
        this.maxWaitTimeMillis = maxWaitTimeMillis;
        this.waitForMaxChanges = waitForMaxChanges;
        this.continueOnMissingChanges = continueOnMissingChanges;
        this.pareEntriesForUserDN = pareEntriesForUserDN;
        this.changeSelectionCriteria = changeSelectionCriteria;
        this.includeSoftDeletedEntryMods = includeSoftDeletedEntryMods;
        this.includeSoftDeletedEntryDeletes = includeSoftDeletedEntryDeletes;
        if (maxChanges <= 0) {
            this.maxChanges = 0;
        }
        else {
            this.maxChanges = maxChanges;
        }
        if (includeBaseDNs == null) {
            this.includeBaseDNs = Collections.emptyList();
        }
        else {
            this.includeBaseDNs = Collections.unmodifiableList((List<? extends String>)includeBaseDNs);
        }
        if (excludeBaseDNs == null) {
            this.excludeBaseDNs = Collections.emptyList();
        }
        else {
            this.excludeBaseDNs = Collections.unmodifiableList((List<? extends String>)excludeBaseDNs);
        }
        if (changeTypes == null || changeTypes.isEmpty()) {
            this.changeTypes = Collections.unmodifiableSet((Set<? extends ChangeType>)EnumSet.allOf(ChangeType.class));
        }
        else {
            this.changeTypes = Collections.unmodifiableSet((Set<? extends ChangeType>)changeTypes);
        }
    }
    
    public GetChangelogBatchExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest.getOID(), extendedRequest.getValue(), extendedRequest.getControls());
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_REQ_NO_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            valueSequence = ASN1Sequence.decodeAsSequence(value.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_REQ_VALUE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        final ASN1Element[] elements = valueSequence.elements();
        if (elements.length < 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_REQ_TOO_FEW_ELEMENTS.get());
        }
        try {
            this.startingPoint = ChangelogBatchStartingPoint.decode(elements[0]);
            final int mc = ASN1Integer.decodeAsInteger(elements[1]).intValue();
            if (mc > 0) {
                this.maxChanges = mc;
            }
            else {
                this.maxChanges = 0;
            }
            boolean waitForMax = false;
            long maxTime = 0L;
            List<String> includeBase = Collections.emptyList();
            List<String> excludeBase = Collections.emptyList();
            Set<ChangeType> types = Collections.unmodifiableSet((Set<? extends ChangeType>)EnumSet.allOf(ChangeType.class));
            boolean continueOnMissing = false;
            String pareForDN = null;
            ChangelogBatchChangeSelectionCriteria changeCriteria = null;
            boolean includeSDMods = false;
            boolean includeSDDeletes = false;
            for (int i = 2; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case Byte.MIN_VALUE: {
                        maxTime = ASN1Long.decodeAsLong(elements[i]).longValue();
                        if (maxTime < 0L) {
                            maxTime = 0L;
                            break;
                        }
                        break;
                    }
                    case -127: {
                        waitForMax = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -94: {
                        final ASN1Element[] includeElements = ASN1Sequence.decodeAsSequence(elements[i]).elements();
                        final ArrayList<String> includeList = new ArrayList<String>(includeElements.length);
                        for (final ASN1Element e2 : includeElements) {
                            includeList.add(ASN1OctetString.decodeAsOctetString(e2).stringValue());
                        }
                        includeBase = Collections.unmodifiableList((List<? extends String>)includeList);
                        break;
                    }
                    case -93: {
                        final ASN1Element[] excludeElements = ASN1Sequence.decodeAsSequence(elements[i]).elements();
                        final ArrayList<String> excludeList = new ArrayList<String>(excludeElements.length);
                        for (final ASN1Element e3 : excludeElements) {
                            excludeList.add(ASN1OctetString.decodeAsOctetString(e3).stringValue());
                        }
                        excludeBase = Collections.unmodifiableList((List<? extends String>)excludeList);
                        break;
                    }
                    case -92: {
                        final EnumSet<ChangeType> ctSet = EnumSet.noneOf(ChangeType.class);
                        for (final ASN1Element e4 : ASN1Set.decodeAsSet(elements[i]).elements()) {
                            final int v = ASN1Enumerated.decodeAsEnumerated(e4).intValue();
                            switch (v) {
                                case 0: {
                                    ctSet.add(ChangeType.ADD);
                                    break;
                                }
                                case 1: {
                                    ctSet.add(ChangeType.DELETE);
                                    break;
                                }
                                case 2: {
                                    ctSet.add(ChangeType.MODIFY);
                                    break;
                                }
                                case 3: {
                                    ctSet.add(ChangeType.MODIFY_DN);
                                    break;
                                }
                                default: {
                                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_REQ_VALUE_UNRECOGNIZED_CT.get(v));
                                }
                            }
                        }
                        types = Collections.unmodifiableSet((Set<? extends ChangeType>)ctSet);
                        break;
                    }
                    case -123: {
                        continueOnMissing = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -122: {
                        pareForDN = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -89: {
                        changeCriteria = ChangelogBatchChangeSelectionCriteria.decode(elements[i]);
                        break;
                    }
                    case -120: {
                        includeSDMods = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -119: {
                        includeSDDeletes = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_REQ_VALUE_UNRECOGNIZED_TYPE.get(StaticUtils.toHex(elements[i].getType())));
                    }
                }
            }
            this.entryListener = null;
            this.maxWaitTimeMillis = maxTime;
            this.waitForMaxChanges = waitForMax;
            this.includeBaseDNs = includeBase;
            this.excludeBaseDNs = excludeBase;
            this.changeTypes = types;
            this.continueOnMissingChanges = continueOnMissing;
            this.pareEntriesForUserDN = pareForDN;
            this.changeSelectionCriteria = changeCriteria;
            this.includeSoftDeletedEntryMods = includeSDMods;
            this.includeSoftDeletedEntryDeletes = includeSDDeletes;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e5) {
            Debug.debugException(e5);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_REQ_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e5)), e5);
        }
    }
    
    private static ASN1OctetString encodeValue(final ChangelogBatchStartingPoint startingPoint, final int maxChanges, final long maxWaitTimeMillis, final boolean waitForMaxChanges, final List<String> includeBaseDNs, final List<String> excludeBaseDNs, final Set<ChangeType> changeTypes, final boolean continueOnMissingChanges, final String pareEntriesForUserDN, final ChangelogBatchChangeSelectionCriteria changeSelectionCriteria, final boolean includeSoftDeletedEntryMods, final boolean includeSoftDeletedEntryDeletes) {
        Validator.ensureNotNull(startingPoint);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(12);
        elements.add(startingPoint.encode());
        if (maxChanges > 0) {
            elements.add(new ASN1Integer(maxChanges));
        }
        else {
            elements.add(new ASN1Integer(0));
        }
        if (maxWaitTimeMillis > 0L) {
            elements.add(new ASN1Long((byte)(-128), maxWaitTimeMillis));
        }
        if (waitForMaxChanges) {
            elements.add(new ASN1Boolean((byte)(-127), true));
        }
        if (includeBaseDNs != null && !includeBaseDNs.isEmpty()) {
            final ArrayList<ASN1Element> l = new ArrayList<ASN1Element>(includeBaseDNs.size());
            for (final String s : includeBaseDNs) {
                l.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-94), l));
        }
        if (excludeBaseDNs != null && !excludeBaseDNs.isEmpty()) {
            final ArrayList<ASN1Element> l = new ArrayList<ASN1Element>(excludeBaseDNs.size());
            for (final String s : excludeBaseDNs) {
                l.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-93), l));
        }
        if (changeTypes != null && !changeTypes.isEmpty() && !changeTypes.equals(EnumSet.allOf(ChangeType.class))) {
            final ArrayList<ASN1Element> l = new ArrayList<ASN1Element>(changeTypes.size());
            for (final ChangeType t : changeTypes) {
                switch (t) {
                    case ADD: {
                        l.add(new ASN1Enumerated(0));
                        continue;
                    }
                    case DELETE: {
                        l.add(new ASN1Enumerated(1));
                        continue;
                    }
                    case MODIFY: {
                        l.add(new ASN1Enumerated(2));
                        continue;
                    }
                    case MODIFY_DN: {
                        l.add(new ASN1Enumerated(3));
                        continue;
                    }
                }
            }
            elements.add(new ASN1Set((byte)(-92), l));
        }
        if (continueOnMissingChanges) {
            elements.add(new ASN1Boolean((byte)(-123), true));
        }
        if (pareEntriesForUserDN != null) {
            elements.add(new ASN1OctetString((byte)(-122), pareEntriesForUserDN));
        }
        if (changeSelectionCriteria != null) {
            elements.add(changeSelectionCriteria.encode());
        }
        if (includeSoftDeletedEntryMods) {
            elements.add(new ASN1Boolean((byte)(-120), true));
        }
        if (includeSoftDeletedEntryDeletes) {
            elements.add(new ASN1Boolean((byte)(-119), true));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public ChangelogBatchStartingPoint getStartingPoint() {
        return this.startingPoint;
    }
    
    public int getMaxChanges() {
        return this.maxChanges;
    }
    
    public long getMaxWaitTimeMillis() {
        return this.maxWaitTimeMillis;
    }
    
    public boolean waitForMaxChanges() {
        return this.waitForMaxChanges;
    }
    
    public List<String> getIncludeBaseDNs() {
        return this.includeBaseDNs;
    }
    
    public List<String> getExcludeBaseDNs() {
        return this.excludeBaseDNs;
    }
    
    public Set<ChangeType> getChangeTypes() {
        return this.changeTypes;
    }
    
    public boolean continueOnMissingChanges() {
        return this.continueOnMissingChanges;
    }
    
    public String getPareEntriesForUserDN() {
        return this.pareEntriesForUserDN;
    }
    
    public ChangelogBatchChangeSelectionCriteria getChangeSelectionCriteria() {
        return this.changeSelectionCriteria;
    }
    
    public boolean includeSoftDeletedEntryMods() {
        return this.includeSoftDeletedEntryMods;
    }
    
    public boolean includeSoftDeletedEntryDeletes() {
        return this.includeSoftDeletedEntryDeletes;
    }
    
    public ChangelogEntryListener getEntryListener() {
        return this.entryListener;
    }
    
    public GetChangelogBatchExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final IntermediateResponseListener l = this.getIntermediateResponseListener();
        if (l != null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_REQ_IR_LISTENER_NOT_ALLOWED.get());
        }
        GetChangelogBatchIntermediateResponseListener listener;
        if (this.entryListener == null) {
            listener = new GetChangelogBatchIntermediateResponseListener(new DefaultChangelogEntryListener(this));
        }
        else {
            listener = new GetChangelogBatchIntermediateResponseListener(this.entryListener);
        }
        this.setIntermediateResponseListener(listener);
        ExtendedResult r;
        try {
            r = super.process(connection, depth);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            r = new ExtendedResult(this.getLastMessageID(), le.getResultCode(), le.getDiagnosticMessage(), le.getMatchedDN(), le.getReferralURLs(), null, null, le.getResponseControls());
        }
        finally {
            this.setIntermediateResponseListener(null);
        }
        if (this.entryListener == null) {
            final DefaultChangelogEntryListener defaultEntryListener = (DefaultChangelogEntryListener)listener.getEntryListener();
            return new GetChangelogBatchExtendedResult(r, defaultEntryListener.getEntryList());
        }
        return new GetChangelogBatchExtendedResult(r, listener.getEntryCount());
    }
    
    @Override
    public GetChangelogBatchExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GetChangelogBatchExtendedRequest duplicate(final Control[] controls) {
        final GetChangelogBatchExtendedRequest r = new GetChangelogBatchExtendedRequest(this.entryListener, this.startingPoint, this.maxChanges, this.maxWaitTimeMillis, this.waitForMaxChanges, this.includeBaseDNs, this.excludeBaseDNs, this.changeTypes, this.continueOnMissingChanges, this.pareEntriesForUserDN, this.changeSelectionCriteria, this.includeSoftDeletedEntryMods, this.includeSoftDeletedEntryDeletes, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_GET_CHANGELOG_BATCH_REQ_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetChangelogBatchExtendedRequest(startingPoint=");
        this.startingPoint.toString(buffer);
        buffer.append(", maxChanges=");
        buffer.append(this.maxChanges);
        buffer.append(", maxWaitTimeMillis=");
        buffer.append(this.maxWaitTimeMillis);
        buffer.append(", waitForMaxChanges=");
        buffer.append(this.waitForMaxChanges);
        buffer.append(", includeBase={");
        final Iterator<String> includeIterator = this.includeBaseDNs.iterator();
        while (includeIterator.hasNext()) {
            buffer.append('\"');
            buffer.append(includeIterator.next());
            buffer.append('\"');
            if (includeIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, excludeBase={");
        final Iterator<String> excludeIterator = this.excludeBaseDNs.iterator();
        while (excludeIterator.hasNext()) {
            buffer.append('\"');
            buffer.append(excludeIterator.next());
            buffer.append('\"');
            if (excludeIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, changeTypes={");
        final Iterator<ChangeType> typeIterator = this.changeTypes.iterator();
        while (typeIterator.hasNext()) {
            buffer.append(typeIterator.next().getName());
            if (typeIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, continueOnMissingChanges=");
        buffer.append(this.continueOnMissingChanges);
        if (this.pareEntriesForUserDN != null) {
            buffer.append(", pareEntriesForUserDN='");
            buffer.append(this.pareEntriesForUserDN);
            buffer.append('\'');
        }
        if (this.changeSelectionCriteria != null) {
            buffer.append(", changeSelectionCriteria=");
            this.changeSelectionCriteria.toString(buffer);
        }
        buffer.append(", includeSoftDeletedEntryMods=");
        buffer.append(this.includeSoftDeletedEntryMods);
        buffer.append(", includeSoftDeletedEntryDeletes=");
        buffer.append(this.includeSoftDeletedEntryDeletes);
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
