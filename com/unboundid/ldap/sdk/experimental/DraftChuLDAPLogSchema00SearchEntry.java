package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.SearchRequest;
import java.util.Collections;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchScope;
import java.util.List;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00SearchEntry extends DraftChuLDAPLogSchema00Entry
{
    public static final String ATTR_DEREFERENCE_POLICY = "reqDerefAliases";
    public static final String ATTR_ENTRIES_RETURNED = "reqEntries";
    public static final String ATTR_FILTER = "reqFilter";
    public static final String ATTR_REQUESTED_ATTRIBUTE = "reqAttr";
    public static final String ATTR_SCOPE = "reqScope";
    public static final String ATTR_SIZE_LIMIT = "reqSizeLimit";
    public static final String ATTR_TIME_LIMIT_SECONDS = "reqTimeLimit";
    public static final String ATTR_TYPES_ONLY = "reqAttrsOnly";
    private static final long serialVersionUID = 948178493925578134L;
    private final boolean typesOnly;
    private final DereferencePolicy dereferencePolicy;
    private final Filter filter;
    private final Integer entriesReturned;
    private final Integer requestedSizeLimit;
    private final Integer requestedTimeLimitSeconds;
    private final List<String> requestedAttributes;
    private final SearchScope scope;
    
    public DraftChuLDAPLogSchema00SearchEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.SEARCH);
        final String scopeStr = entry.getAttributeValue("reqScope");
        if (scopeStr == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqScope"));
        }
        final String lowerScope = StaticUtils.toLowerCase(scopeStr);
        if (lowerScope.equals("base")) {
            this.scope = SearchScope.BASE;
        }
        else if (lowerScope.equals("one")) {
            this.scope = SearchScope.ONE;
        }
        else if (lowerScope.equals("sub")) {
            this.scope = SearchScope.SUB;
        }
        else {
            if (!lowerScope.equals("subord")) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_SEARCH_SCOPE_ERROR.get(entry.getDN(), "reqScope", scopeStr));
            }
            this.scope = SearchScope.SUBORDINATE_SUBTREE;
        }
        final String derefStr = entry.getAttributeValue("reqDerefAliases");
        if (derefStr == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqDerefAliases"));
        }
        final String lowerDeref = StaticUtils.toLowerCase(derefStr);
        if (lowerDeref.equals("never")) {
            this.dereferencePolicy = DereferencePolicy.NEVER;
        }
        else if (lowerDeref.equals("searching")) {
            this.dereferencePolicy = DereferencePolicy.SEARCHING;
        }
        else if (lowerDeref.equals("finding")) {
            this.dereferencePolicy = DereferencePolicy.FINDING;
        }
        else {
            if (!lowerDeref.equals("always")) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_SEARCH_DEREF_ERROR.get(entry.getDN(), "reqDerefAliases", derefStr));
            }
            this.dereferencePolicy = DereferencePolicy.ALWAYS;
        }
        final String typesOnlyStr = entry.getAttributeValue("reqAttrsOnly");
        if (typesOnlyStr == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqAttrsOnly"));
        }
        final String lowerTypesOnly = StaticUtils.toLowerCase(typesOnlyStr);
        if (lowerTypesOnly.equals("true")) {
            this.typesOnly = true;
        }
        else {
            if (!lowerTypesOnly.equals("false")) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_SEARCH_TYPES_ONLY_ERROR.get(entry.getDN(), "reqAttrsOnly", typesOnlyStr));
            }
            this.typesOnly = false;
        }
        final String filterStr = entry.getAttributeValue("reqFilter");
        if (filterStr == null) {
            this.filter = null;
        }
        else {
            try {
                this.filter = Filter.create(filterStr);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_SEARCH_FILTER_ERROR.get(entry.getDN(), "reqFilter", filterStr), e);
            }
        }
        final String[] requestedAttrArray = entry.getAttributeValues("reqAttr");
        if (requestedAttrArray == null || requestedAttrArray.length == 0) {
            this.requestedAttributes = Collections.emptyList();
        }
        else {
            this.requestedAttributes = Collections.unmodifiableList((List<? extends String>)StaticUtils.toList(requestedAttrArray));
        }
        final String sizeLimitStr = entry.getAttributeValue("reqSizeLimit");
        if (sizeLimitStr == null) {
            this.requestedSizeLimit = null;
        }
        else {
            try {
                this.requestedSizeLimit = Integer.parseInt(sizeLimitStr);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_SEARCH_INT_ERROR.get(entry.getDN(), "reqSizeLimit", sizeLimitStr), e2);
            }
        }
        final String timeLimitStr = entry.getAttributeValue("reqTimeLimit");
        if (timeLimitStr == null) {
            this.requestedTimeLimitSeconds = null;
        }
        else {
            try {
                this.requestedTimeLimitSeconds = Integer.parseInt(timeLimitStr);
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_SEARCH_INT_ERROR.get(entry.getDN(), "reqTimeLimit", timeLimitStr), e3);
            }
        }
        final String entriesReturnedStr = entry.getAttributeValue("reqEntries");
        if (entriesReturnedStr == null) {
            this.entriesReturned = null;
        }
        else {
            try {
                this.entriesReturned = Integer.parseInt(entriesReturnedStr);
            }
            catch (final Exception e4) {
                Debug.debugException(e4);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_SEARCH_INT_ERROR.get(entry.getDN(), "reqEntries", entriesReturnedStr), e4);
            }
        }
    }
    
    public SearchScope getScope() {
        return this.scope;
    }
    
    public DereferencePolicy getDereferencePolicy() {
        return this.dereferencePolicy;
    }
    
    public boolean typesOnly() {
        return this.typesOnly;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public Integer getRequestedSizeLimit() {
        return this.requestedSizeLimit;
    }
    
    public Integer getRequestedTimeLimitSeconds() {
        return this.requestedTimeLimitSeconds;
    }
    
    public List<String> getRequestedAttributes() {
        return this.requestedAttributes;
    }
    
    public Integer getEntriesReturned() {
        return this.entriesReturned;
    }
    
    public SearchRequest toSearchRequest() {
        final int sizeLimit = (this.requestedSizeLimit == null) ? 0 : this.requestedSizeLimit;
        final int timeLimit = (this.requestedTimeLimitSeconds == null) ? 0 : this.requestedTimeLimitSeconds;
        final Filter f = (this.filter == null) ? Filter.createPresenceFilter("objectClass") : this.filter;
        final String[] attrArray = this.requestedAttributes.toArray(StaticUtils.NO_STRINGS);
        final SearchRequest searchRequest = new SearchRequest(this.getTargetEntryDN(), this.scope, this.dereferencePolicy, sizeLimit, timeLimit, this.typesOnly, f, attrArray);
        searchRequest.setControls(this.getRequestControlArray());
        return searchRequest;
    }
}
