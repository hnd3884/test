package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.HashSet;
import java.util.Arrays;
import com.unboundid.util.Debug;
import com.unboundid.util.json.JSONException;
import java.util.Map;
import com.unboundid.util.json.JSONString;
import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONValue;
import com.unboundid.util.json.JSONObject;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ObjectMatchesJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "objectMatches";
    public static final String FIELD_FIELD_PATH = "field";
    public static final String FIELD_FILTER = "filter";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final long serialVersionUID = 7138078723547160420L;
    private volatile JSONObjectFilter filter;
    private volatile List<String> field;
    
    ObjectMatchesJSONObjectFilter() {
        this.field = null;
        this.filter = null;
    }
    
    public ObjectMatchesJSONObjectFilter(final String field, final JSONObjectFilter filter) {
        this(Collections.singletonList(field), filter);
    }
    
    public ObjectMatchesJSONObjectFilter(final List<String> field, final JSONObjectFilter filter) {
        Validator.ensureNotNull(field);
        Validator.ensureFalse(field.isEmpty());
        Validator.ensureNotNull(filter);
        this.field = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(field));
        this.filter = filter;
    }
    
    public List<String> getField() {
        return this.field;
    }
    
    public void setField(final String... field) {
        this.setField(StaticUtils.toList(field));
    }
    
    public void setField(final List<String> field) {
        Validator.ensureNotNull(field);
        Validator.ensureFalse(field.isEmpty());
        this.field = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(field));
    }
    
    public JSONObjectFilter getFilter() {
        return this.filter;
    }
    
    public void setFilter(final JSONObjectFilter filter) {
        Validator.ensureNotNull(filter);
        this.filter = filter;
    }
    
    @Override
    public String getFilterType() {
        return "objectMatches";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return ObjectMatchesJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return ObjectMatchesJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        final List<JSONValue> candidates = JSONObjectFilter.getValues(o, this.field);
        if (candidates.isEmpty()) {
            return false;
        }
        for (final JSONValue v : candidates) {
            if (v instanceof JSONObject) {
                if (this.filter.matchesJSONObject((JSONObject)v)) {
                    return true;
                }
                continue;
            }
            else {
                if (!(v instanceof JSONArray)) {
                    continue;
                }
                for (final JSONValue arrayValue : ((JSONArray)v).getValues()) {
                    if (arrayValue instanceof JSONObject && this.filter.matchesJSONObject((JSONObject)arrayValue)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(3));
        fields.put("filterType", new JSONString("objectMatches"));
        if (this.field.size() == 1) {
            fields.put("field", new JSONString(this.field.get(0)));
        }
        else {
            final ArrayList<JSONValue> fieldNameValues = new ArrayList<JSONValue>(this.field.size());
            for (final String s : this.field) {
                fieldNameValues.add(new JSONString(s));
            }
            fields.put("field", new JSONArray(fieldNameValues));
        }
        fields.put("filter", this.filter.toJSONObject());
        return new JSONObject(fields);
    }
    
    @Override
    protected ObjectMatchesJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        final List<String> fieldPath = this.getStrings(filterObject, "field", false, null);
        final JSONValue v = filterObject.getField("filter");
        if (v == null) {
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_MISSING_REQUIRED_FIELD.get(String.valueOf(filterObject), "objectMatches", "filter"));
        }
        if (!(v instanceof JSONObject)) {
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_OBJECT.get(String.valueOf(filterObject), "objectMatches", "filter"));
        }
        try {
            return new ObjectMatchesJSONObjectFilter(fieldPath, JSONObjectFilter.decode((JSONObject)v));
        }
        catch (final JSONException e) {
            Debug.debugException(e);
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_FILTER.get(String.valueOf(filterObject), "objectMatches", "filter", e.getMessage()), e);
        }
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("field", "filter")));
        OPTIONAL_FIELD_NAMES = Collections.emptySet();
    }
}
