package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import com.unboundid.ldap.sdk.Filter;
import java.util.Collection;
import java.util.HashSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.json.JSONBoolean;
import java.util.Iterator;
import com.unboundid.util.json.JSONValue;
import java.util.ArrayList;
import com.unboundid.util.json.JSONArray;
import java.util.Collections;
import com.unboundid.util.json.JSONString;
import com.unboundid.util.json.JSONException;
import java.util.List;
import com.unboundid.util.json.JSONObject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class JSONObjectFilter implements Serializable
{
    public static final String JSON_OBJECT_FILTER_MATCHING_RULE_NAME = "jsonObjectFilterExtensibleMatch";
    public static final String JSON_OBJECT_FILTER_MATCHING_RULE_OID = "1.3.6.1.4.1.30221.2.4.13";
    public static final String FIELD_FILTER_TYPE = "filterType";
    private static final ConcurrentHashMap<String, JSONObjectFilter> FILTER_TYPES;
    private static final long serialVersionUID = -551616596693584562L;
    
    public abstract String getFilterType();
    
    protected abstract Set<String> getRequiredFieldNames();
    
    protected abstract Set<String> getOptionalFieldNames();
    
    public abstract boolean matchesJSONObject(final JSONObject p0);
    
    public abstract JSONObject toJSONObject();
    
    protected List<String> getStrings(final JSONObject o, final String fieldName, final boolean allowEmpty, final List<String> defaultValues) throws JSONException {
        final JSONValue v = o.getField(fieldName);
        if (v == null) {
            if (defaultValues == null) {
                throw new JSONException(JFMessages.ERR_OBJECT_FILTER_MISSING_REQUIRED_FIELD.get(String.valueOf(o), this.getFilterType(), fieldName));
            }
            return defaultValues;
        }
        else {
            if (v instanceof JSONString) {
                return Collections.singletonList(((JSONString)v).stringValue());
            }
            if (!(v instanceof JSONArray)) {
                throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_STRINGS.get(String.valueOf(o), this.getFilterType(), fieldName));
            }
            final List<JSONValue> values = ((JSONArray)v).getValues();
            if (!values.isEmpty()) {
                final ArrayList<String> valueList = new ArrayList<String>(values.size());
                for (final JSONValue av : values) {
                    if (!(av instanceof JSONString)) {
                        throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_STRINGS.get(String.valueOf(o), this.getFilterType(), fieldName));
                    }
                    valueList.add(((JSONString)av).stringValue());
                }
                return valueList;
            }
            if (allowEmpty) {
                return Collections.emptyList();
            }
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_EMPTY_ARRAY.get(String.valueOf(o), this.getFilterType(), fieldName));
        }
    }
    
    protected String getString(final JSONObject o, final String fieldName, final String defaultValue, final boolean required) throws JSONException {
        final JSONValue v = o.getField(fieldName);
        if (v == null) {
            if (required && defaultValue == null) {
                throw new JSONException(JFMessages.ERR_OBJECT_FILTER_MISSING_REQUIRED_FIELD.get(String.valueOf(o), this.getFilterType(), fieldName));
            }
            return defaultValue;
        }
        else {
            if (v instanceof JSONString) {
                return ((JSONString)v).stringValue();
            }
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_STRING.get(String.valueOf(o), this.getFilterType(), fieldName));
        }
    }
    
    protected boolean getBoolean(final JSONObject o, final String fieldName, final Boolean defaultValue) throws JSONException {
        final JSONValue v = o.getField(fieldName);
        if (v == null) {
            if (defaultValue == null) {
                throw new JSONException(JFMessages.ERR_OBJECT_FILTER_MISSING_REQUIRED_FIELD.get(String.valueOf(o), this.getFilterType(), fieldName));
            }
            return defaultValue;
        }
        else {
            if (v instanceof JSONBoolean) {
                return ((JSONBoolean)v).booleanValue();
            }
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_BOOLEAN.get(String.valueOf(o), this.getFilterType(), fieldName));
        }
    }
    
    protected List<JSONObjectFilter> getFilters(final JSONObject o, final String fieldName) throws JSONException {
        final JSONValue value = o.getField(fieldName);
        if (value == null) {
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_MISSING_REQUIRED_FIELD.get(String.valueOf(o), this.getFilterType(), fieldName));
        }
        if (!(value instanceof JSONArray)) {
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_ARRAY.get(String.valueOf(o), this.getFilterType(), fieldName));
        }
        final List<JSONValue> values = ((JSONArray)value).getValues();
        final ArrayList<JSONObjectFilter> filterList = new ArrayList<JSONObjectFilter>(values.size());
        for (final JSONValue arrayValue : values) {
            if (!(arrayValue instanceof JSONObject)) {
                throw new JSONException(JFMessages.ERR_OBJECT_FILTER_ARRAY_ELEMENT_NOT_OBJECT.get(String.valueOf(o), this.getFilterType(), fieldName));
            }
            final JSONObject filterObject = (JSONObject)arrayValue;
            try {
                filterList.add(decode(filterObject));
            }
            catch (final JSONException e) {
                Debug.debugException(e);
                throw new JSONException(JFMessages.ERR_OBJECT_FILTER_ARRAY_ELEMENT_NOT_FILTER.get(String.valueOf(o), this.getFilterType(), String.valueOf(filterObject), fieldName, e.getMessage()), e);
            }
        }
        return filterList;
    }
    
    protected static List<JSONValue> getValues(final JSONObject o, final List<String> fieldName) {
        final ArrayList<JSONValue> values = new ArrayList<JSONValue>(10);
        getValues(o, fieldName, 0, values);
        return values;
    }
    
    private static void getValues(final JSONObject o, final List<String> fieldName, final int fieldNameIndex, final List<JSONValue> values) {
        final JSONValue v = o.getField(fieldName.get(fieldNameIndex));
        if (v == null) {
            return;
        }
        final int nextIndex = fieldNameIndex + 1;
        if (nextIndex < fieldName.size()) {
            if (v instanceof JSONObject) {
                getValues((JSONObject)v, fieldName, nextIndex, values);
            }
            else if (v instanceof JSONArray) {
                getValuesFromArray((JSONArray)v, fieldName, nextIndex, values);
            }
            return;
        }
        values.add(v);
    }
    
    private static void getValuesFromArray(final JSONArray a, final List<String> fieldName, final int fieldNameIndex, final List<JSONValue> values) {
        for (final JSONValue v : a.getValues()) {
            if (v instanceof JSONObject) {
                getValues((JSONObject)v, fieldName, fieldNameIndex, values);
            }
            else {
                if (!(v instanceof JSONArray)) {
                    continue;
                }
                getValuesFromArray((JSONArray)v, fieldName, fieldNameIndex, values);
            }
        }
    }
    
    public static JSONObjectFilter decode(final JSONObject o) throws JSONException {
        final JSONValue filterTypeValue = o.getField("filterType");
        if (filterTypeValue == null) {
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_MISSING_FILTER_TYPE.get(String.valueOf(o), "filterType"));
        }
        if (!(filterTypeValue instanceof JSONString)) {
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_INVALID_FILTER_TYPE.get(String.valueOf(o), "filterType"));
        }
        final String filterType = StaticUtils.toLowerCase(((JSONString)filterTypeValue).stringValue());
        final JSONObjectFilter decoder = JSONObjectFilter.FILTER_TYPES.get(filterType);
        if (decoder == null) {
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_INVALID_FILTER_TYPE.get(String.valueOf(o), "filterType"));
        }
        final HashSet<String> objectFields = new HashSet<String>(o.getFields().keySet());
        objectFields.remove("filterType");
        for (final String requiredField : decoder.getRequiredFieldNames()) {
            if (!objectFields.remove(requiredField)) {
                throw new JSONException(JFMessages.ERR_OBJECT_FILTER_MISSING_REQUIRED_FIELD.get(String.valueOf(o), decoder.getFilterType(), requiredField));
            }
        }
        for (final String remainingField : objectFields) {
            if (!decoder.getOptionalFieldNames().contains(remainingField)) {
                throw new JSONException(JFMessages.ERR_OBJECT_FILTER_UNRECOGNIZED_FIELD.get(String.valueOf(o), decoder.getFilterType(), remainingField));
            }
        }
        return decoder.decodeFilter(o);
    }
    
    protected abstract JSONObjectFilter decodeFilter(final JSONObject p0) throws JSONException;
    
    protected static void registerFilterType(final JSONObjectFilter... impl) {
        for (final JSONObjectFilter f : impl) {
            final String filterTypeName = StaticUtils.toLowerCase(f.getFilterType());
            JSONObjectFilter.FILTER_TYPES.put(filterTypeName, f);
        }
    }
    
    public final Filter toLDAPFilter(final String attributeDescription) {
        return Filter.createExtensibleMatchFilter(attributeDescription, "jsonObjectFilterExtensibleMatch", false, this.toString());
    }
    
    static String fieldPathToName(final List<String> fieldPath) {
        if (fieldPath == null) {
            return "null";
        }
        if (fieldPath.isEmpty()) {
            return "";
        }
        if (fieldPath.size() == 1) {
            return new JSONString(fieldPath.get(0)).toString();
        }
        final StringBuilder buffer = new StringBuilder();
        for (final String pathElement : fieldPath) {
            if (buffer.length() > 0) {
                buffer.append('.');
            }
            new JSONString(pathElement).toString(buffer);
        }
        return buffer.toString();
    }
    
    @Override
    public final int hashCode() {
        return this.toJSONObject().hashCode();
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof JSONObjectFilter) {
            final JSONObjectFilter f = (JSONObjectFilter)o;
            return this.toJSONObject().equals(f.toJSONObject());
        }
        return false;
    }
    
    @Override
    public final String toString() {
        return this.toJSONObject().toString();
    }
    
    public final void toString(final StringBuilder buffer) {
        this.toJSONObject().toString(buffer);
    }
    
    static {
        FILTER_TYPES = new ConcurrentHashMap<String, JSONObjectFilter>(StaticUtils.computeMapCapacity(10));
        registerFilterType(new ContainsFieldJSONObjectFilter(), new EqualsJSONObjectFilter(), new EqualsAnyJSONObjectFilter(), new ObjectMatchesJSONObjectFilter(), new SubstringJSONObjectFilter(), new GreaterThanJSONObjectFilter(), new LessThanJSONObjectFilter(), new RegularExpressionJSONObjectFilter(), new ANDJSONObjectFilter(new JSONObjectFilter[0]), new ORJSONObjectFilter(new JSONObjectFilter[0]), new NegateJSONObjectFilter());
    }
}
