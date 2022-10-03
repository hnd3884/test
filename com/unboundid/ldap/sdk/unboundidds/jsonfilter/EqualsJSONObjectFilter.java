package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.HashSet;
import java.util.Arrays;
import com.unboundid.util.json.JSONException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONObject;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.util.json.JSONNumber;
import com.unboundid.util.json.JSONBoolean;
import com.unboundid.util.json.JSONString;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.json.JSONValue;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class EqualsJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "equals";
    public static final String FIELD_FIELD_PATH = "field";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_CASE_SENSITIVE = "caseSensitive";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final long serialVersionUID = 4622567662624840125L;
    private volatile boolean caseSensitive;
    private volatile JSONValue value;
    private volatile List<String> field;
    
    EqualsJSONObjectFilter() {
        this.field = null;
        this.value = null;
        this.caseSensitive = false;
    }
    
    private EqualsJSONObjectFilter(final List<String> field, final JSONValue value, final boolean caseSensitive) {
        this.field = field;
        this.value = value;
        this.caseSensitive = caseSensitive;
    }
    
    public EqualsJSONObjectFilter(final String field, final String value) {
        this(Collections.singletonList(field), new JSONString(value));
    }
    
    public EqualsJSONObjectFilter(final String field, final boolean value) {
        this(Collections.singletonList(field), value ? JSONBoolean.TRUE : JSONBoolean.FALSE);
    }
    
    public EqualsJSONObjectFilter(final String field, final long value) {
        this(Collections.singletonList(field), new JSONNumber(value));
    }
    
    public EqualsJSONObjectFilter(final String field, final double value) {
        this(Collections.singletonList(field), new JSONNumber(value));
    }
    
    public EqualsJSONObjectFilter(final String field, final JSONValue value) {
        this(Collections.singletonList(field), value);
    }
    
    public EqualsJSONObjectFilter(final List<String> field, final JSONValue value) {
        Validator.ensureNotNull(field);
        Validator.ensureFalse(field.isEmpty());
        Validator.ensureNotNull(value);
        this.field = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(field));
        this.value = value;
        this.caseSensitive = false;
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
    
    public JSONValue getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        Validator.ensureNotNull(value);
        this.value = new JSONString(value);
    }
    
    public void setValue(final boolean value) {
        this.value = (value ? JSONBoolean.TRUE : JSONBoolean.FALSE);
    }
    
    public void setValue(final long value) {
        this.value = new JSONNumber(value);
    }
    
    public void setValue(final double value) {
        this.value = new JSONNumber(value);
    }
    
    public void setValue(final JSONValue value) {
        Validator.ensureNotNull(value);
        this.value = value;
    }
    
    public boolean caseSensitive() {
        return this.caseSensitive;
    }
    
    public void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    @Override
    public String getFilterType() {
        return "equals";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return EqualsJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return EqualsJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        final List<JSONValue> candidates = JSONObjectFilter.getValues(o, this.field);
        if (candidates.isEmpty()) {
            return false;
        }
        for (final JSONValue v : candidates) {
            if (this.value.equals(v, false, !this.caseSensitive, false)) {
                return true;
            }
            if (!(v instanceof JSONArray)) {
                continue;
            }
            final JSONArray a = (JSONArray)v;
            if (a.contains(this.value, false, !this.caseSensitive, false, false)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(4));
        fields.put("filterType", new JSONString("equals"));
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
        fields.put("value", this.value);
        if (this.caseSensitive) {
            fields.put("caseSensitive", JSONBoolean.TRUE);
        }
        return new JSONObject(fields);
    }
    
    @Override
    protected EqualsJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        final List<String> fieldPath = this.getStrings(filterObject, "field", false, null);
        final boolean isCaseSensitive = this.getBoolean(filterObject, "caseSensitive", false);
        return new EqualsJSONObjectFilter(fieldPath, filterObject.getField("value"), isCaseSensitive);
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("field", "value")));
        OPTIONAL_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("caseSensitive")));
    }
}
