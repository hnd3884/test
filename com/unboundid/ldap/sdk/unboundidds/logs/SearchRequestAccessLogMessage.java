package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.ldap.sdk.Filter;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.SearchScope;
import java.util.List;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class SearchRequestAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = -6751258649156129642L;
    private final Boolean typesOnly;
    private final DereferencePolicy derefPolicy;
    private final Integer sizeLimit;
    private final Integer timeLimit;
    private final List<String> requestedAttributes;
    private final SearchScope scope;
    private final String baseDN;
    private final String filter;
    
    public SearchRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public SearchRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.baseDN = this.getNamedValue("base");
        this.filter = this.getNamedValue("filter");
        this.sizeLimit = this.getNamedValueAsInteger("sizeLimit");
        this.timeLimit = this.getNamedValueAsInteger("timeLimit");
        this.typesOnly = this.getNamedValueAsBoolean("typesOnly");
        SearchScope ss = null;
        try {
            ss = SearchScope.definedValueOf(this.getNamedValueAsInteger("scope"));
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        this.scope = ss;
        DereferencePolicy deref = null;
        final String derefStr = this.getNamedValue("deref");
        if (derefStr != null) {
            for (final DereferencePolicy p : DereferencePolicy.values()) {
                if (p.getName().equalsIgnoreCase(derefStr)) {
                    deref = p;
                    break;
                }
            }
        }
        this.derefPolicy = deref;
        final String attrStr = this.getNamedValue("attrs");
        if (attrStr == null) {
            this.requestedAttributes = null;
        }
        else if (attrStr.equals("ALL")) {
            this.requestedAttributes = Collections.emptyList();
        }
        else {
            final LinkedList<String> attrs = new LinkedList<String>();
            final StringTokenizer st = new StringTokenizer(attrStr, ",", false);
            while (st.hasMoreTokens()) {
                attrs.add(st.nextToken());
            }
            this.requestedAttributes = Collections.unmodifiableList((List<? extends String>)attrs);
        }
    }
    
    public final String getBaseDN() {
        return this.baseDN;
    }
    
    public final SearchScope getScope() {
        return this.scope;
    }
    
    public final String getFilter() {
        return this.filter;
    }
    
    public final Filter getParsedFilter() {
        try {
            if (this.filter == null) {
                return null;
            }
            return Filter.create(this.filter);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public final DereferencePolicy getDereferencePolicy() {
        return this.derefPolicy;
    }
    
    public final Integer getSizeLimit() {
        return this.sizeLimit;
    }
    
    public final Integer getTimeLimit() {
        return this.timeLimit;
    }
    
    public final Boolean typesOnly() {
        return this.typesOnly;
    }
    
    public final List<String> getRequestedAttributes() {
        return this.requestedAttributes;
    }
    
    @Override
    public final AccessLogOperationType getOperationType() {
        return AccessLogOperationType.SEARCH;
    }
}
