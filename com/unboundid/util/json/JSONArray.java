package com.unboundid.util.json;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JSONArray extends JSONValue
{
    public static final JSONArray EMPTY_ARRAY;
    private static final long serialVersionUID = -5493008945333225318L;
    private Integer hashCode;
    private final List<JSONValue> values;
    private String stringRepresentation;
    
    public JSONArray(final JSONValue... values) {
        this((values == null) ? null : Arrays.asList(values));
    }
    
    public JSONArray(final List<? extends JSONValue> values) {
        if (values == null) {
            this.values = Collections.emptyList();
        }
        else {
            this.values = Collections.unmodifiableList((List<? extends JSONValue>)new ArrayList<JSONValue>(values));
        }
        this.hashCode = null;
        this.stringRepresentation = null;
    }
    
    public List<JSONValue> getValues() {
        return this.values;
    }
    
    public boolean isEmpty() {
        return this.values.isEmpty();
    }
    
    public int size() {
        return this.values.size();
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == null) {
            int hc = 0;
            for (final JSONValue v : this.values) {
                hc = hc * 31 + v.hashCode();
            }
            this.hashCode = hc;
        }
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof JSONArray) {
            final JSONArray a = (JSONArray)o;
            return this.values.equals(a.values);
        }
        return false;
    }
    
    public boolean equals(final JSONArray array, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        if (!ignoreFieldNameCase && !ignoreValueCase && !ignoreArrayOrder) {
            return this.values.equals(array.values);
        }
        if (this.values.size() != array.values.size()) {
            return false;
        }
        if (!ignoreArrayOrder) {
            final Iterator<JSONValue> thisIterator = this.values.iterator();
            final Iterator<JSONValue> thatIterator = array.values.iterator();
            while (thisIterator.hasNext()) {
                final JSONValue thisValue = thisIterator.next();
                final JSONValue thatValue = thatIterator.next();
                if (!thisValue.equals(thatValue, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder)) {
                    return false;
                }
            }
            return true;
        }
        final ArrayList<JSONValue> thatValues = new ArrayList<JSONValue>(array.values);
        final Iterator<JSONValue> thisIterator2 = this.values.iterator();
        while (thisIterator2.hasNext()) {
            final JSONValue thisValue = thisIterator2.next();
            boolean found = false;
            final Iterator<JSONValue> thatIterator2 = thatValues.iterator();
            while (thatIterator2.hasNext()) {
                final JSONValue thatValue2 = thatIterator2.next();
                if (thisValue.equals(thatValue2, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder)) {
                    found = true;
                    thatIterator2.remove();
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final JSONValue v, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        return v instanceof JSONArray && this.equals((JSONArray)v, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder);
    }
    
    public boolean contains(final JSONValue value, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder, final boolean recursive) {
        for (final JSONValue v : this.values) {
            if (v.equals(value, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder)) {
                return true;
            }
            if (recursive && v instanceof JSONArray && ((JSONArray)v).contains(value, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder, recursive)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        if (this.stringRepresentation == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toString(buffer);
            this.stringRepresentation = buffer.toString();
        }
        return this.stringRepresentation;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        if (this.stringRepresentation != null) {
            buffer.append(this.stringRepresentation);
            return;
        }
        buffer.append("[ ");
        final Iterator<JSONValue> iterator = this.values.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(',');
            }
            buffer.append(' ');
        }
        buffer.append(']');
    }
    
    @Override
    public String toSingleLineString() {
        final StringBuilder buffer = new StringBuilder();
        this.toSingleLineString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toSingleLineString(final StringBuilder buffer) {
        buffer.append("[ ");
        final Iterator<JSONValue> iterator = this.values.iterator();
        while (iterator.hasNext()) {
            iterator.next().toSingleLineString(buffer);
            if (iterator.hasNext()) {
                buffer.append(',');
            }
            buffer.append(' ');
        }
        buffer.append(']');
    }
    
    @Override
    public String toNormalizedString() {
        final StringBuilder buffer = new StringBuilder();
        this.toNormalizedString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer) {
        this.toNormalizedString(buffer, false, true, false);
    }
    
    @Override
    public String toNormalizedString(final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        final StringBuilder buffer = new StringBuilder();
        this.toNormalizedString(buffer, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder);
        return buffer.toString();
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        final List<String> normalizedValues = new ArrayList<String>(this.values.size());
        for (final JSONValue v : this.values) {
            normalizedValues.add(v.toNormalizedString(ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder));
        }
        if (ignoreArrayOrder) {
            Collections.sort(normalizedValues);
        }
        buffer.append('[');
        final Iterator<String> iterator = normalizedValues.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append(']');
    }
    
    @Override
    public void appendToJSONBuffer(final JSONBuffer buffer) {
        buffer.beginArray();
        for (final JSONValue value : this.values) {
            value.appendToJSONBuffer(buffer);
        }
        buffer.endArray();
    }
    
    @Override
    public void appendToJSONBuffer(final String fieldName, final JSONBuffer buffer) {
        buffer.beginArray(fieldName);
        for (final JSONValue value : this.values) {
            value.appendToJSONBuffer(buffer);
        }
        buffer.endArray();
    }
    
    static {
        EMPTY_ARRAY = new JSONArray(new JSONValue[0]);
    }
}
