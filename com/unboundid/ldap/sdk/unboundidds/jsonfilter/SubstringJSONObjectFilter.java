package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.Arrays;
import java.util.HashSet;
import com.unboundid.util.json.JSONException;
import java.util.Map;
import com.unboundid.util.json.JSONBoolean;
import java.util.LinkedHashMap;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONString;
import com.unboundid.util.json.JSONValue;
import com.unboundid.util.json.JSONObject;
import java.util.Iterator;
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
public final class SubstringJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "substring";
    public static final String FIELD_FIELD_PATH = "field";
    public static final String FIELD_STARTS_WITH = "startsWith";
    public static final String FIELD_CONTAINS = "contains";
    public static final String FIELD_ENDS_WITH = "endsWith";
    public static final String FIELD_CASE_SENSITIVE = "caseSensitive";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final long serialVersionUID = 811514243548895420L;
    private volatile boolean caseSensitive;
    private volatile int minLength;
    private volatile List<String> contains;
    private volatile List<String> matchContains;
    private volatile List<String> field;
    private volatile String endsWith;
    private volatile String matchEndsWith;
    private volatile String matchStartsWith;
    private volatile String startsWith;
    
    SubstringJSONObjectFilter() {
        this.field = null;
        this.startsWith = null;
        this.contains = null;
        this.endsWith = null;
        this.caseSensitive = false;
        this.minLength = 0;
        this.matchStartsWith = null;
        this.matchContains = null;
        this.matchEndsWith = null;
    }
    
    private SubstringJSONObjectFilter(final List<String> field, final String startsWith, final List<String> contains, final String endsWith, final boolean caseSensitive) {
        this.field = field;
        this.caseSensitive = caseSensitive;
        this.setSubstringComponents(startsWith, contains, endsWith);
    }
    
    public SubstringJSONObjectFilter(final String field, final String startsWith, final String contains, final String endsWith) {
        this(Collections.singletonList(field), startsWith, (contains == null) ? null : Collections.singletonList(contains), endsWith);
    }
    
    public SubstringJSONObjectFilter(final List<String> field, final String startsWith, final List<String> contains, final String endsWith) {
        Validator.ensureNotNull(field);
        Validator.ensureFalse(field.isEmpty());
        this.field = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(field));
        this.caseSensitive = false;
        this.setSubstringComponents(startsWith, contains, endsWith);
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
    
    public String getStartsWith() {
        return this.startsWith;
    }
    
    public List<String> getContains() {
        return this.contains;
    }
    
    public String getEndsWith() {
        return this.endsWith;
    }
    
    public void setSubstringComponents(final String startsWith, final String contains, final String endsWith) {
        this.setSubstringComponents(startsWith, (contains == null) ? null : Collections.singletonList(contains), endsWith);
    }
    
    public void setSubstringComponents(final String startsWith, final List<String> contains, final String endsWith) {
        Validator.ensureFalse(startsWith == null && contains == null && endsWith == null);
        this.minLength = 0;
        this.startsWith = startsWith;
        if (startsWith != null) {
            this.minLength += startsWith.length();
            if (this.caseSensitive) {
                this.matchStartsWith = startsWith;
            }
            else {
                this.matchStartsWith = StaticUtils.toLowerCase(startsWith);
            }
        }
        if (contains == null) {
            this.contains = Collections.emptyList();
            this.matchContains = this.contains;
        }
        else {
            this.contains = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(contains));
            final ArrayList<String> mcList = new ArrayList<String>(contains.size());
            for (final String s : contains) {
                this.minLength += s.length();
                if (this.caseSensitive) {
                    mcList.add(s);
                }
                else {
                    mcList.add(StaticUtils.toLowerCase(s));
                }
            }
            this.matchContains = Collections.unmodifiableList((List<? extends String>)mcList);
        }
        this.endsWith = endsWith;
        if (endsWith != null) {
            this.minLength += endsWith.length();
            if (this.caseSensitive) {
                this.matchEndsWith = endsWith;
            }
            else {
                this.matchEndsWith = StaticUtils.toLowerCase(endsWith);
            }
        }
    }
    
    public boolean caseSensitive() {
        return this.caseSensitive;
    }
    
    public void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        this.setSubstringComponents(this.startsWith, this.contains, this.endsWith);
    }
    
    @Override
    public String getFilterType() {
        return "substring";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return SubstringJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return SubstringJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        final List<JSONValue> candidates = JSONObjectFilter.getValues(o, this.field);
        if (candidates.isEmpty()) {
            return false;
        }
        for (final JSONValue v : candidates) {
            if (v instanceof JSONString) {
                if (this.matchesValue(v)) {
                    return true;
                }
                continue;
            }
            else {
                if (!(v instanceof JSONArray)) {
                    continue;
                }
                for (final JSONValue arrayValue : ((JSONArray)v).getValues()) {
                    if (this.matchesValue(arrayValue)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean matchesValue(final JSONValue v) {
        return v instanceof JSONString && this.matchesString(((JSONString)v).stringValue());
    }
    
    public boolean matchesString(final String s) {
        String stringValue;
        if (this.caseSensitive) {
            stringValue = s;
        }
        else {
            stringValue = StaticUtils.toLowerCase(s);
        }
        if (stringValue.length() < this.minLength) {
            return false;
        }
        final StringBuilder buffer = new StringBuilder(stringValue);
        if (this.matchStartsWith != null) {
            if (buffer.indexOf(this.matchStartsWith) != 0) {
                return false;
            }
            buffer.delete(0, this.matchStartsWith.length());
        }
        if (this.matchEndsWith != null) {
            final int lengthMinusEndsWith = buffer.length() - this.matchEndsWith.length();
            if (buffer.lastIndexOf(this.matchEndsWith) != lengthMinusEndsWith) {
                return false;
            }
            buffer.setLength(lengthMinusEndsWith);
        }
        for (final String containsElement : this.matchContains) {
            final int index = buffer.indexOf(containsElement);
            if (index < 0) {
                return false;
            }
            buffer.delete(0, index + containsElement.length());
        }
        return true;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(6));
        fields.put("filterType", new JSONString("substring"));
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
        if (this.startsWith != null) {
            fields.put("startsWith", new JSONString(this.startsWith));
        }
        if (!this.contains.isEmpty()) {
            if (this.contains.size() == 1) {
                fields.put("contains", new JSONString(this.contains.get(0)));
            }
            else {
                final ArrayList<JSONValue> containsValues = new ArrayList<JSONValue>(this.contains.size());
                for (final String s : this.contains) {
                    containsValues.add(new JSONString(s));
                }
                fields.put("contains", new JSONArray(containsValues));
            }
        }
        if (this.endsWith != null) {
            fields.put("endsWith", new JSONString(this.endsWith));
        }
        if (this.caseSensitive) {
            fields.put("caseSensitive", JSONBoolean.TRUE);
        }
        return new JSONObject(fields);
    }
    
    @Override
    protected SubstringJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        final List<String> fieldPath = this.getStrings(filterObject, "field", false, null);
        final String subInitial = this.getString(filterObject, "startsWith", null, false);
        final List<String> subAny = this.getStrings(filterObject, "contains", true, Collections.emptyList());
        final String subFinal = this.getString(filterObject, "endsWith", null, false);
        if (subInitial == null && subFinal == null && subAny.isEmpty()) {
            throw new JSONException(JFMessages.ERR_SUBSTRING_FILTER_NO_COMPONENTS.get(String.valueOf(filterObject), "substring", "startsWith", "contains", "endsWith"));
        }
        final boolean isCaseSensitive = this.getBoolean(filterObject, "caseSensitive", false);
        return new SubstringJSONObjectFilter(fieldPath, subInitial, subAny, subFinal, isCaseSensitive);
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("field")));
        OPTIONAL_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("startsWith", "contains", "endsWith", "caseSensitive")));
    }
}
