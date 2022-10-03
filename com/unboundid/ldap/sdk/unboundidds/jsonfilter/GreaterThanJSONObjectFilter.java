package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.HashSet;
import java.util.Arrays;
import com.unboundid.util.json.JSONException;
import java.util.Map;
import com.unboundid.util.json.JSONBoolean;
import java.util.LinkedHashMap;
import java.math.BigDecimal;
import java.util.Iterator;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONObject;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.util.json.JSONString;
import com.unboundid.util.json.JSONNumber;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.json.JSONValue;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class GreaterThanJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "greaterThan";
    public static final String FIELD_FIELD_PATH = "field";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_ALLOW_EQUALS = "allowEquals";
    public static final String FIELD_MATCH_ALL_ELEMENTS = "matchAllElements";
    public static final String FIELD_CASE_SENSITIVE = "caseSensitive";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final long serialVersionUID = -8397741931424599570L;
    private volatile boolean allowEquals;
    private volatile boolean caseSensitive;
    private volatile boolean matchAllElements;
    private volatile JSONValue value;
    private volatile List<String> field;
    
    GreaterThanJSONObjectFilter() {
        this.field = null;
        this.value = null;
        this.allowEquals = false;
        this.matchAllElements = false;
        this.caseSensitive = false;
    }
    
    private GreaterThanJSONObjectFilter(final List<String> field, final JSONValue value, final boolean allowEquals, final boolean matchAllElements, final boolean caseSensitive) {
        this.field = field;
        this.value = value;
        this.allowEquals = allowEquals;
        this.matchAllElements = matchAllElements;
        this.caseSensitive = caseSensitive;
    }
    
    public GreaterThanJSONObjectFilter(final String field, final long value) {
        this(Collections.singletonList(field), new JSONNumber(value));
    }
    
    public GreaterThanJSONObjectFilter(final String field, final double value) {
        this(Collections.singletonList(field), new JSONNumber(value));
    }
    
    public GreaterThanJSONObjectFilter(final String field, final String value) {
        this(Collections.singletonList(field), new JSONString(value));
    }
    
    public GreaterThanJSONObjectFilter(final String field, final JSONValue value) {
        this(Collections.singletonList(field), value);
    }
    
    public GreaterThanJSONObjectFilter(final List<String> field, final JSONValue value) {
        Validator.ensureNotNull(field);
        Validator.ensureFalse(field.isEmpty());
        Validator.ensureNotNull(value);
        Validator.ensureTrue(value instanceof JSONNumber || value instanceof JSONString);
        this.field = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(field));
        this.value = value;
        this.allowEquals = false;
        this.matchAllElements = false;
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
    
    public void setValue(final long value) {
        this.setValue(new JSONNumber(value));
    }
    
    public void setValue(final double value) {
        this.setValue(new JSONNumber(value));
    }
    
    public void setValue(final String value) {
        Validator.ensureNotNull(value);
        this.setValue(new JSONString(value));
    }
    
    public void setValue(final JSONValue value) {
        Validator.ensureNotNull(value);
        Validator.ensureTrue(value instanceof JSONNumber || value instanceof JSONString);
        this.value = value;
    }
    
    public boolean allowEquals() {
        return this.allowEquals;
    }
    
    public void setAllowEquals(final boolean allowEquals) {
        this.allowEquals = allowEquals;
    }
    
    public boolean matchAllElements() {
        return this.matchAllElements;
    }
    
    public void setMatchAllElements(final boolean matchAllElements) {
        this.matchAllElements = matchAllElements;
    }
    
    public boolean caseSensitive() {
        return this.caseSensitive;
    }
    
    public void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    @Override
    public String getFilterType() {
        return "greaterThan";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return GreaterThanJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return GreaterThanJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        final List<JSONValue> candidates = JSONObjectFilter.getValues(o, this.field);
        if (candidates.isEmpty()) {
            return false;
        }
        for (final JSONValue v : candidates) {
            if (v instanceof JSONArray) {
                boolean matchOne = false;
                boolean matchAll = true;
                for (final JSONValue arrayValue : ((JSONArray)v).getValues()) {
                    if (this.matches(arrayValue)) {
                        if (!this.matchAllElements) {
                            return true;
                        }
                        matchOne = true;
                    }
                    else {
                        matchAll = false;
                        if (this.matchAllElements) {
                            break;
                        }
                        continue;
                    }
                }
                if (this.matchAllElements && matchOne && matchAll) {
                    return true;
                }
                continue;
            }
            else {
                if (this.matches(v)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private boolean matches(final JSONValue v) {
        if (v instanceof JSONNumber && this.value instanceof JSONNumber) {
            final BigDecimal targetValue = ((JSONNumber)this.value).getValue();
            final BigDecimal objectValue = ((JSONNumber)v).getValue();
            if (this.allowEquals) {
                return objectValue.compareTo(targetValue) >= 0;
            }
            return objectValue.compareTo(targetValue) > 0;
        }
        else {
            if (!(v instanceof JSONString) || !(this.value instanceof JSONString)) {
                return false;
            }
            final String targetValue2 = ((JSONString)this.value).stringValue();
            final String objectValue2 = ((JSONString)v).stringValue();
            if (this.allowEquals) {
                if (this.caseSensitive) {
                    return objectValue2.compareTo(targetValue2) >= 0;
                }
                return objectValue2.compareToIgnoreCase(targetValue2) >= 0;
            }
            else {
                if (this.caseSensitive) {
                    return objectValue2.compareTo(targetValue2) > 0;
                }
                return objectValue2.compareToIgnoreCase(targetValue2) > 0;
            }
        }
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(6));
        fields.put("filterType", new JSONString("greaterThan"));
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
        if (this.allowEquals) {
            fields.put("allowEquals", JSONBoolean.TRUE);
        }
        if (this.matchAllElements) {
            fields.put("matchAllElements", JSONBoolean.TRUE);
        }
        if (this.caseSensitive) {
            fields.put("caseSensitive", JSONBoolean.TRUE);
        }
        return new JSONObject(fields);
    }
    
    @Override
    protected GreaterThanJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        final List<String> fieldPath = this.getStrings(filterObject, "field", false, null);
        final boolean isAllowEquals = this.getBoolean(filterObject, "allowEquals", false);
        final boolean isMatchAllElements = this.getBoolean(filterObject, "matchAllElements", false);
        final boolean isCaseSensitive = this.getBoolean(filterObject, "caseSensitive", false);
        return new GreaterThanJSONObjectFilter(fieldPath, filterObject.getField("value"), isAllowEquals, isMatchAllElements, isCaseSensitive);
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("field", "value")));
        OPTIONAL_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("allowEquals", "matchAllElements", "caseSensitive")));
    }
}
