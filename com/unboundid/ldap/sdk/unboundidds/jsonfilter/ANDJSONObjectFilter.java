package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.HashSet;
import com.unboundid.util.json.JSONException;
import java.util.Map;
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
public final class ANDJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "and";
    public static final String FIELD_AND_FILTERS = "andFilters";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final long serialVersionUID = 6616759665873968672L;
    private volatile List<JSONObjectFilter> andFilters;
    
    public ANDJSONObjectFilter(final JSONObjectFilter... andFilters) {
        this(StaticUtils.toList(andFilters));
    }
    
    public ANDJSONObjectFilter(final Collection<JSONObjectFilter> andFilters) {
        this.setANDFilters(andFilters);
    }
    
    public List<JSONObjectFilter> getANDFilters() {
        return this.andFilters;
    }
    
    public void setANDFilters(final JSONObjectFilter... andFilters) {
        this.setANDFilters(StaticUtils.toList(andFilters));
    }
    
    public void setANDFilters(final Collection<JSONObjectFilter> andFilters) {
        if (andFilters == null || andFilters.isEmpty()) {
            this.andFilters = Collections.emptyList();
        }
        else {
            this.andFilters = Collections.unmodifiableList((List<? extends JSONObjectFilter>)new ArrayList<JSONObjectFilter>(andFilters));
        }
    }
    
    @Override
    public String getFilterType() {
        return "and";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return ANDJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return ANDJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        for (final JSONObjectFilter f : this.andFilters) {
            if (!f.matchesJSONObject(o)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(2));
        fields.put("filterType", new JSONString("and"));
        final ArrayList<JSONValue> filterValues = new ArrayList<JSONValue>(this.andFilters.size());
        for (final JSONObjectFilter f : this.andFilters) {
            filterValues.add(f.toJSONObject());
        }
        fields.put("andFilters", new JSONArray(filterValues));
        return new JSONObject(fields);
    }
    
    @Override
    protected ANDJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        return new ANDJSONObjectFilter(this.getFilters(filterObject, "andFilters"));
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("andFilters")));
        OPTIONAL_FIELD_NAMES = Collections.emptySet();
    }
}
