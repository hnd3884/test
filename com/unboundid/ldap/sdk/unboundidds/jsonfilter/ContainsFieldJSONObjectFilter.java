package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.HashSet;
import com.unboundid.util.json.JSONException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.util.json.JSONString;
import com.unboundid.util.json.JSONNumber;
import com.unboundid.util.json.JSONNull;
import com.unboundid.util.json.JSONBoolean;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONValue;
import com.unboundid.util.json.JSONObject;
import java.util.EnumSet;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.util.StaticUtils;
import java.util.List;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ContainsFieldJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "containsField";
    public static final String FIELD_FIELD_PATH = "field";
    public static final String FIELD_EXPECTED_TYPE = "expectedType";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final Set<ExpectedValueType> ALL_EXPECTED_VALUE_TYPES;
    private static final long serialVersionUID = -2922149221350606755L;
    private volatile List<String> field;
    private volatile Set<ExpectedValueType> expectedValueTypes;
    
    ContainsFieldJSONObjectFilter() {
        this.field = null;
        this.expectedValueTypes = null;
    }
    
    private ContainsFieldJSONObjectFilter(final List<String> field, final Set<ExpectedValueType> expectedValueTypes) {
        this.field = field;
        this.expectedValueTypes = expectedValueTypes;
    }
    
    public ContainsFieldJSONObjectFilter(final String... field) {
        this(StaticUtils.toList(field));
    }
    
    public ContainsFieldJSONObjectFilter(final List<String> field) {
        Validator.ensureNotNull(field);
        Validator.ensureFalse(field.isEmpty());
        this.field = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(field));
        this.expectedValueTypes = ContainsFieldJSONObjectFilter.ALL_EXPECTED_VALUE_TYPES;
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
    
    public Set<ExpectedValueType> getExpectedType() {
        return this.expectedValueTypes;
    }
    
    public void setExpectedType(final ExpectedValueType... expectedTypes) {
        this.setExpectedType(StaticUtils.toList(expectedTypes));
    }
    
    public void setExpectedType(final Collection<ExpectedValueType> expectedTypes) {
        if (expectedTypes == null || expectedTypes.isEmpty()) {
            this.expectedValueTypes = ContainsFieldJSONObjectFilter.ALL_EXPECTED_VALUE_TYPES;
        }
        else {
            final EnumSet<ExpectedValueType> s = EnumSet.noneOf(ExpectedValueType.class);
            s.addAll((Collection<?>)expectedTypes);
            this.expectedValueTypes = Collections.unmodifiableSet((Set<? extends ExpectedValueType>)s);
        }
    }
    
    @Override
    public String getFilterType() {
        return "containsField";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return ContainsFieldJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return ContainsFieldJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        final List<JSONValue> candidates = JSONObjectFilter.getValues(o, this.field);
        if (candidates.isEmpty()) {
            return false;
        }
        for (final JSONValue v : candidates) {
            if (v instanceof JSONArray) {
                final JSONArray a = (JSONArray)v;
                if (a.isEmpty()) {
                    if (this.expectedValueTypes.contains(ExpectedValueType.EMPTY_ARRAY)) {
                        return true;
                    }
                    continue;
                }
                else {
                    if (this.expectedValueTypes.contains(ExpectedValueType.NON_EMPTY_ARRAY)) {
                        return true;
                    }
                    continue;
                }
            }
            else if (v instanceof JSONBoolean) {
                if (this.expectedValueTypes.contains(ExpectedValueType.BOOLEAN)) {
                    return true;
                }
                continue;
            }
            else if (v instanceof JSONNull) {
                if (this.expectedValueTypes.contains(ExpectedValueType.NULL)) {
                    return true;
                }
                continue;
            }
            else if (v instanceof JSONNumber) {
                if (this.expectedValueTypes.contains(ExpectedValueType.NUMBER)) {
                    return true;
                }
                continue;
            }
            else if (v instanceof JSONObject) {
                if (this.expectedValueTypes.contains(ExpectedValueType.OBJECT)) {
                    return true;
                }
                continue;
            }
            else {
                if (v instanceof JSONString && this.expectedValueTypes.contains(ExpectedValueType.STRING)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(3));
        fields.put("filterType", new JSONString("containsField"));
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
        if (!this.expectedValueTypes.equals(ContainsFieldJSONObjectFilter.ALL_EXPECTED_VALUE_TYPES)) {
            if (this.expectedValueTypes.size() == 1) {
                fields.put("expectedType", new JSONString(this.expectedValueTypes.iterator().next().toString()));
            }
            else {
                final ArrayList<JSONValue> expectedTypeValues = new ArrayList<JSONValue>(this.expectedValueTypes.size());
                for (final ExpectedValueType t : this.expectedValueTypes) {
                    expectedTypeValues.add(new JSONString(t.toString()));
                }
                fields.put("expectedType", new JSONArray(expectedTypeValues));
            }
        }
        return new JSONObject(fields);
    }
    
    @Override
    protected ContainsFieldJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        final List<String> fieldPath = this.getStrings(filterObject, "field", false, null);
        final List<String> valueTypeNames = this.getStrings(filterObject, "expectedType", false, Collections.emptyList());
        Set<ExpectedValueType> expectedTypes;
        if (valueTypeNames.isEmpty()) {
            expectedTypes = ContainsFieldJSONObjectFilter.ALL_EXPECTED_VALUE_TYPES;
        }
        else {
            final EnumSet<ExpectedValueType> valueTypes = EnumSet.noneOf(ExpectedValueType.class);
            for (final String s : valueTypeNames) {
                final ExpectedValueType t = ExpectedValueType.forName(s);
                if (t == null) {
                    throw new JSONException(JFMessages.ERR_CONTAINS_FIELD_FILTER_UNRECOGNIZED_EXPECTED_TYPE.get(String.valueOf(filterObject), "containsField", s, "expectedType"));
                }
                valueTypes.add(t);
            }
            expectedTypes = valueTypes;
        }
        return new ContainsFieldJSONObjectFilter(fieldPath, expectedTypes);
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("field")));
        OPTIONAL_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("expectedType")));
        ALL_EXPECTED_VALUE_TYPES = Collections.unmodifiableSet((Set<? extends ExpectedValueType>)EnumSet.allOf(ExpectedValueType.class));
    }
}
