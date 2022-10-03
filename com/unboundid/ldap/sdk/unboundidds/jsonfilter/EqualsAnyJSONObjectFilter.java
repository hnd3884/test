package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.HashSet;
import java.util.Arrays;
import com.unboundid.util.json.JSONException;
import java.util.Map;
import com.unboundid.util.json.JSONBoolean;
import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONObject;
import com.unboundid.util.json.JSONString;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.Collections;
import com.unboundid.util.json.JSONValue;
import java.util.List;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class EqualsAnyJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "equalsAny";
    public static final String FIELD_FIELD_PATH = "field";
    public static final String FIELD_VALUES = "values";
    public static final String FIELD_CASE_SENSITIVE = "caseSensitive";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final long serialVersionUID = -7441807169198186996L;
    private volatile boolean caseSensitive;
    private volatile List<JSONValue> values;
    private volatile List<String> field;
    
    EqualsAnyJSONObjectFilter() {
        this.field = null;
        this.values = null;
        this.caseSensitive = false;
    }
    
    private EqualsAnyJSONObjectFilter(final List<String> field, final List<JSONValue> values, final boolean caseSensitive) {
        this.field = field;
        this.values = values;
        this.caseSensitive = caseSensitive;
    }
    
    public EqualsAnyJSONObjectFilter(final String field, final String... values) {
        this(Collections.singletonList(field), toJSONValues(values));
    }
    
    public EqualsAnyJSONObjectFilter(final String field, final JSONValue... values) {
        this(Collections.singletonList(field), StaticUtils.toList(values));
    }
    
    public EqualsAnyJSONObjectFilter(final String field, final Collection<JSONValue> values) {
        this(Collections.singletonList(field), values);
    }
    
    public EqualsAnyJSONObjectFilter(final List<String> field, final Collection<JSONValue> values) {
        Validator.ensureNotNull(field);
        Validator.ensureFalse(field.isEmpty());
        Validator.ensureNotNull(values);
        Validator.ensureFalse(values.isEmpty());
        this.field = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(field));
        this.values = Collections.unmodifiableList((List<? extends JSONValue>)new ArrayList<JSONValue>(values));
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
    
    public List<JSONValue> getValues() {
        return this.values;
    }
    
    public void setValues(final String... values) {
        this.setValues(toJSONValues(values));
    }
    
    public void setValues(final JSONValue... values) {
        this.setValues(StaticUtils.toList(values));
    }
    
    public void setValues(final Collection<JSONValue> values) {
        Validator.ensureNotNull(values);
        Validator.ensureFalse(values.isEmpty());
        this.values = Collections.unmodifiableList((List<? extends JSONValue>)new ArrayList<JSONValue>(values));
    }
    
    private static List<JSONValue> toJSONValues(final String... values) {
        final ArrayList<JSONValue> valueList = new ArrayList<JSONValue>(values.length);
        for (final String s : values) {
            valueList.add(new JSONString(s));
        }
        return valueList;
    }
    
    public boolean caseSensitive() {
        return this.caseSensitive;
    }
    
    public void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    @Override
    public String getFilterType() {
        return "equalsAny";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return EqualsAnyJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return EqualsAnyJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        final List<JSONValue> candidates = JSONObjectFilter.getValues(o, this.field);
        if (candidates.isEmpty()) {
            return false;
        }
        for (final JSONValue objectValue : candidates) {
            for (final JSONValue filterValue : this.values) {
                if (filterValue.equals(objectValue, false, !this.caseSensitive, false)) {
                    return true;
                }
            }
            if (objectValue instanceof JSONArray) {
                final JSONArray a = (JSONArray)objectValue;
                for (final JSONValue filterValue2 : this.values) {
                    if (a.contains(filterValue2, false, !this.caseSensitive, false, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(4));
        fields.put("filterType", new JSONString("equalsAny"));
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
        fields.put("values", new JSONArray(this.values));
        if (this.caseSensitive) {
            fields.put("caseSensitive", JSONBoolean.TRUE);
        }
        return new JSONObject(fields);
    }
    
    @Override
    protected EqualsAnyJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        final List<String> fieldPath = this.getStrings(filterObject, "field", false, null);
        final boolean isCaseSensitive = this.getBoolean(filterObject, "caseSensitive", false);
        final JSONValue arrayValue = filterObject.getField("values");
        if (arrayValue instanceof JSONArray) {
            return new EqualsAnyJSONObjectFilter(fieldPath, ((JSONArray)arrayValue).getValues(), isCaseSensitive);
        }
        throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_ARRAY.get(String.valueOf(filterObject), "equalsAny", "values"));
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("field", "values")));
        OPTIONAL_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("caseSensitive")));
    }
}
