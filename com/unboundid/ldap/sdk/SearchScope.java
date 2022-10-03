package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import java.util.HashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SearchScope implements Serializable
{
    public static final int BASE_INT_VALUE = 0;
    public static final SearchScope BASE;
    public static final int ONE_INT_VALUE = 1;
    public static final SearchScope ONE;
    public static final int SUB_INT_VALUE = 2;
    public static final SearchScope SUB;
    public static final int SUBORDINATE_SUBTREE_INT_VALUE = 3;
    public static final SearchScope SUBORDINATE_SUBTREE;
    private static final HashMap<Integer, SearchScope> UNDEFINED_SCOPES;
    private static final long serialVersionUID = 5381929718445793181L;
    private final int intValue;
    private final String name;
    
    private SearchScope(final int intValue) {
        this.intValue = intValue;
        this.name = String.valueOf(intValue);
    }
    
    private SearchScope(final String name, final int intValue) {
        this.name = name;
        this.intValue = intValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static SearchScope valueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return SearchScope.BASE;
            }
            case 1: {
                return SearchScope.ONE;
            }
            case 2: {
                return SearchScope.SUB;
            }
            case 3: {
                return SearchScope.SUBORDINATE_SUBTREE;
            }
            default: {
                synchronized (SearchScope.UNDEFINED_SCOPES) {
                    SearchScope s = SearchScope.UNDEFINED_SCOPES.get(intValue);
                    if (s == null) {
                        s = new SearchScope(intValue);
                        SearchScope.UNDEFINED_SCOPES.put(intValue, s);
                    }
                    return s;
                }
                break;
            }
        }
    }
    
    public static SearchScope definedValueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return SearchScope.BASE;
            }
            case 1: {
                return SearchScope.ONE;
            }
            case 2: {
                return SearchScope.SUB;
            }
            case 3: {
                return SearchScope.SUBORDINATE_SUBTREE;
            }
            default: {
                return null;
            }
        }
    }
    
    public static SearchScope[] values() {
        return new SearchScope[] { SearchScope.BASE, SearchScope.ONE, SearchScope.SUB, SearchScope.SUBORDINATE_SUBTREE };
    }
    
    @Override
    public int hashCode() {
        return this.intValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (o == this || (o instanceof SearchScope && this.intValue == ((SearchScope)o).intValue));
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static {
        BASE = new SearchScope("BASE", 0);
        ONE = new SearchScope("ONE", 1);
        SUB = new SearchScope("SUB", 2);
        SUBORDINATE_SUBTREE = new SearchScope("SUBORDINATE_SUBTREE", 3);
        UNDEFINED_SCOPES = new HashMap<Integer, SearchScope>(StaticUtils.computeMapCapacity(5));
    }
}
