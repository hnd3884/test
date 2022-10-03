package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import com.unboundid.util.Debug;
import com.unboundid.util.json.JSONException;
import java.util.Map;
import com.unboundid.util.json.JSONString;
import com.unboundid.util.json.JSONValue;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.json.JSONObject;
import com.unboundid.util.Validator;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class NegateJSONObjectFilter extends JSONObjectFilter
{
    public static final String FILTER_TYPE = "negate";
    public static final String FIELD_NEGATE_FILTER = "negateFilter";
    private static final Set<String> REQUIRED_FIELD_NAMES;
    private static final Set<String> OPTIONAL_FIELD_NAMES;
    private static final long serialVersionUID = -9067967834329526711L;
    private volatile JSONObjectFilter negateFilter;
    
    NegateJSONObjectFilter() {
        this.negateFilter = null;
    }
    
    public NegateJSONObjectFilter(final JSONObjectFilter negateFilter) {
        Validator.ensureNotNull(negateFilter);
        this.negateFilter = negateFilter;
    }
    
    public JSONObjectFilter getNegateFilter() {
        return this.negateFilter;
    }
    
    public void setNegateFilter(final JSONObjectFilter negateFilter) {
        Validator.ensureNotNull(negateFilter);
        this.negateFilter = negateFilter;
    }
    
    @Override
    public String getFilterType() {
        return "negate";
    }
    
    @Override
    protected Set<String> getRequiredFieldNames() {
        return NegateJSONObjectFilter.REQUIRED_FIELD_NAMES;
    }
    
    @Override
    protected Set<String> getOptionalFieldNames() {
        return NegateJSONObjectFilter.OPTIONAL_FIELD_NAMES;
    }
    
    @Override
    public boolean matchesJSONObject(final JSONObject o) {
        return !this.negateFilter.matchesJSONObject(o);
    }
    
    @Override
    public JSONObject toJSONObject() {
        final LinkedHashMap<String, JSONValue> fields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(2));
        fields.put("filterType", new JSONString("negate"));
        fields.put("negateFilter", this.negateFilter.toJSONObject());
        return new JSONObject(fields);
    }
    
    @Override
    protected NegateJSONObjectFilter decodeFilter(final JSONObject filterObject) throws JSONException {
        final JSONValue v = filterObject.getField("negateFilter");
        if (v == null) {
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_MISSING_REQUIRED_FIELD.get(String.valueOf(filterObject), "negate", "negateFilter"));
        }
        if (!(v instanceof JSONObject)) {
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_OBJECT.get(String.valueOf(filterObject), "negate", "negateFilter"));
        }
        try {
            return new NegateJSONObjectFilter(JSONObjectFilter.decode((JSONObject)v));
        }
        catch (final JSONException e) {
            Debug.debugException(e);
            throw new JSONException(JFMessages.ERR_OBJECT_FILTER_VALUE_NOT_FILTER.get(String.valueOf(filterObject), "negate", "negateFilter", e.getMessage()), e);
        }
    }
    
    static {
        REQUIRED_FIELD_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Collections.singletonList("negateFilter")));
        OPTIONAL_FIELD_NAMES = Collections.emptySet();
    }
}
