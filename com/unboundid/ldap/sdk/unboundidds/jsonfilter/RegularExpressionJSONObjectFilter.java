package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import com.unboundid.util.json.JSONBoolean;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.Iterator;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONString;
import com.unboundid.util.json.JSONValue;
import com.unboundid.util.json.JSONObject;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.util.json.JSONException;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class RegularExpressionJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "regularExpression";
    public static final String FIELD_FIELD_PATH = "field";
    public static final String FIELD_REGULAR_EXPRESSION = "regularExpression";
    public static final String FIELD_MATCH_ALL_ELEMENTS = "matchAllElements";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final long serialVersionUID = 7678844742777504519L;
    private volatile boolean matchAllElements;
    private volatile List<String> field;
    private volatile Pattern regularExpression;
    
    RegularExpressionJSONObjectFilter() {
        this.field = null;
        this.regularExpression = null;
        this.matchAllElements = false;
    }
    
    private RegularExpressionJSONObjectFilter(final List<String> field, final Pattern regularExpression, final boolean matchAllElements) {
        this.field = field;
        this.regularExpression = regularExpression;
        this.matchAllElements = matchAllElements;
    }
    
    public RegularExpressionJSONObjectFilter(final String field, final String regularExpression) throws JSONException {
        this(Collections.singletonList(field), regularExpression);
    }
    
    public RegularExpressionJSONObjectFilter(final String field, final Pattern regularExpression) {
        this(Collections.singletonList(field), regularExpression);
    }
    
    public RegularExpressionJSONObjectFilter(final List<String> field, final String regularExpression) throws JSONException {
        Validator.ensureNotNull(field);
        Validator.ensureFalse(field.isEmpty());
        Validator.ensureNotNull(regularExpression);
        this.field = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(field));
        try {
            this.regularExpression = Pattern.compile(regularExpression);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new JSONException(JFMessages.ERR_REGEX_FILTER_INVALID_REGEX.get(regularExpression, StaticUtils.getExceptionMessage(e)), e);
        }
        this.matchAllElements = false;
    }
    
    public RegularExpressionJSONObjectFilter(final List<String> field, final Pattern regularExpression) {
        Validator.ensureNotNull(field);
        Validator.ensureFalse(field.isEmpty());
        Validator.ensureNotNull(regularExpression);
        this.field = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(field));
        this.regularExpression = regularExpression;
        this.matchAllElements = false;
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
    
    public Pattern getRegularExpression() {
        return this.regularExpression;
    }
    
    public void setRegularExpression(final String regularExpression) throws JSONException {
        Validator.ensureNotNull(regularExpression);
        try {
            this.regularExpression = Pattern.compile(regularExpression);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new JSONException(JFMessages.ERR_REGEX_FILTER_INVALID_REGEX.get(regularExpression, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public void setRegularExpression(final Pattern regularExpression) {
        Validator.ensureNotNull(regularExpression);
        this.regularExpression = regularExpression;
    }
    
    public boolean matchAllElements() {
        return this.matchAllElements;
    }
    
    public void setMatchAllElements(final boolean matchAllElements) {
        this.matchAllElements = matchAllElements;
    }
    
    @Override
    public String getFilterType() {
        return "regularExpression";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return RegularExpressionJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return RegularExpressionJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        final List<JSONValue> candidates = JSONObjectFilter.getValues(o, this.field);
        if (candidates.isEmpty()) {
            return false;
        }
        for (final JSONValue v : candidates) {
            if (v instanceof JSONString) {
                final Matcher matcher = this.regularExpression.matcher(((JSONString)v).stringValue());
                if (matcher.matches()) {
                    return true;
                }
                continue;
            }
            else {
                if (!(v instanceof JSONArray)) {
                    continue;
                }
                boolean matchOne = false;
                boolean matchAll = true;
                for (final JSONValue arrayValue : ((JSONArray)v).getValues()) {
                    if (!(arrayValue instanceof JSONString)) {
                        matchAll = false;
                        if (this.matchAllElements) {
                            break;
                        }
                    }
                    final Matcher matcher2 = this.regularExpression.matcher(((JSONString)arrayValue).stringValue());
                    if (matcher2.matches()) {
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
                if (matchOne && matchAll) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(4));
        fields.put("filterType", new JSONString("regularExpression"));
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
        fields.put("regularExpression", new JSONString(this.regularExpression.toString()));
        if (this.matchAllElements) {
            fields.put("matchAllElements", JSONBoolean.TRUE);
        }
        return new JSONObject(fields);
    }
    
    @Override
    protected RegularExpressionJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        final List<String> fieldPath = this.getStrings(filterObject, "field", false, null);
        final String regex = this.getString(filterObject, "regularExpression", null, true);
        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new JSONException(JFMessages.ERR_REGEX_FILTER_DECODE_INVALID_REGEX.get(String.valueOf(filterObject), "regularExpression", JSONObjectFilter.fieldPathToName(fieldPath), StaticUtils.getExceptionMessage(e)), e);
        }
        final boolean matchAll = this.getBoolean(filterObject, "matchAllElements", false);
        return new RegularExpressionJSONObjectFilter(fieldPath, pattern, matchAll);
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("field", "regularExpression")));
        OPTIONAL_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("matchAllElements")));
    }
}
