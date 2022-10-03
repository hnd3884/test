package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.HashSet;
import com.unboundid.util.json.JSONException;
import java.util.Map;
import com.unboundid.util.json.JSONBoolean;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONString;
import com.unboundid.util.json.JSONValue;
import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.util.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.List;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ORJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "or";
    public static final String FIELD_OR_FILTERS = "orFilters";
    public static final String FIELD_EXCLUSIVE = "exclusive";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final long serialVersionUID = -7821418213623654386L;
    private volatile boolean exclusive;
    private volatile List<JSONObjectFilter> orFilters;
    
    public ORJSONObjectFilter(final JSONObjectFilter... orFilters) {
        this(StaticUtils.toList(orFilters));
    }
    
    public ORJSONObjectFilter(final Collection<JSONObjectFilter> orFilters) {
        this.setORFilters(orFilters);
        this.exclusive = false;
    }
    
    public List<JSONObjectFilter> getORFilters() {
        return this.orFilters;
    }
    
    public void setORFilters(final JSONObjectFilter... orFilters) {
        this.setORFilters(StaticUtils.toList(orFilters));
    }
    
    public void setORFilters(final Collection<JSONObjectFilter> orFilters) {
        if (orFilters == null || orFilters.isEmpty()) {
            this.orFilters = Collections.emptyList();
        }
        else {
            this.orFilters = Collections.unmodifiableList((List<? extends JSONObjectFilter>)new ArrayList<JSONObjectFilter>(orFilters));
        }
    }
    
    public boolean exclusive() {
        return this.exclusive;
    }
    
    public void setExclusive(final boolean exclusive) {
        this.exclusive = exclusive;
    }
    
    @Override
    public String getFilterType() {
        return "or";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return ORJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return ORJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        boolean matchFound = false;
        for (final JSONObjectFilter f : this.orFilters) {
            if (f.matchesJSONObject(o)) {
                if (!this.exclusive) {
                    return true;
                }
                if (matchFound) {
                    return false;
                }
                matchFound = true;
            }
        }
        return matchFound;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(3));
        fields.put("filterType", new JSONString("or"));
        final ArrayList<JSONValue> filterValues = new ArrayList<JSONValue>(this.orFilters.size());
        for (final JSONObjectFilter f : this.orFilters) {
            filterValues.add(f.toJSONObject());
        }
        fields.put("orFilters", new JSONArray(filterValues));
        if (this.exclusive) {
            fields.put("exclusive", JSONBoolean.TRUE);
        }
        return new JSONObject(fields);
    }
    
    @Override
    protected ORJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        final ORJSONObjectFilter orFilter = new ORJSONObjectFilter(this.getFilters(filterObject, "orFilters"));
        orFilter.exclusive = this.getBoolean(filterObject, "exclusive", false);
        return orFilter;
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("orFilters")));
        OPTIONAL_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("exclusive")));
    }
}
