package org.json.simple;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Collection;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class JsonObject extends HashMap<String, Object> implements Jsonable
{
    private static final long serialVersionUID = 2L;
    
    public JsonObject() {
    }
    
    public JsonObject(final Map<String, ?> map) {
        super(map);
    }
    
    public void requireKeys(final JsonKey... keys) {
        final Set<JsonKey> missing = new HashSet<JsonKey>();
        for (final JsonKey k : keys) {
            if (!this.containsKey(k.getKey())) {
                missing.add(k);
            }
        }
        if (!missing.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (final JsonKey i : missing) {
                sb.append(i.getKey()).append(", ");
            }
            sb.setLength(sb.length() - 2);
            final String s = (missing.size() > 1) ? "s" : "";
            throw new NoSuchElementException("A JsonObject is missing required key" + s + ": " + sb.toString());
        }
    }
    
    public BigDecimal getBigDecimal(final JsonKey key) {
        Object returnable = ((HashMap<K, Object>)this).get(key.getKey());
        if (!(returnable instanceof BigDecimal)) {
            if (returnable instanceof Number) {
                returnable = new BigDecimal(returnable.toString());
            }
            else if (returnable instanceof String) {
                returnable = new BigDecimal((String)returnable);
            }
        }
        return (BigDecimal)returnable;
    }
    
    @Deprecated
    public BigDecimal getBigDecimal(final String key) {
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (!(returnable instanceof BigDecimal)) {
            if (returnable instanceof Number) {
                returnable = new BigDecimal(returnable.toString());
            }
            else if (returnable instanceof String) {
                returnable = new BigDecimal((String)returnable);
            }
        }
        return (BigDecimal)returnable;
    }
    
    public BigDecimal getBigDecimalOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        if (!(returnable instanceof BigDecimal)) {
            if (returnable instanceof Number) {
                returnable = new BigDecimal(returnable.toString());
            }
            else if (returnable instanceof String) {
                returnable = new BigDecimal((String)returnable);
            }
        }
        return (BigDecimal)returnable;
    }
    
    @Deprecated
    public BigDecimal getBigDecimalOrDefault(final String key, final BigDecimal defaultValue) {
        if (this.containsKey(key)) {
            Object returnable = ((HashMap<K, Object>)this).get(key);
            if (!(returnable instanceof BigDecimal)) {
                if (returnable instanceof Number) {
                    returnable = new BigDecimal(returnable.toString());
                }
                else if (returnable instanceof String) {
                    returnable = new BigDecimal((String)returnable);
                }
            }
            return (BigDecimal)returnable;
        }
        return defaultValue;
    }
    
    public Boolean getBoolean(final JsonKey key) {
        Object returnable = ((HashMap<K, Object>)this).get(key.getKey());
        if (returnable instanceof String) {
            returnable = Boolean.valueOf((String)returnable);
        }
        return (Boolean)returnable;
    }
    
    @Deprecated
    public Boolean getBoolean(final String key) {
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable instanceof String) {
            returnable = Boolean.valueOf((String)returnable);
        }
        return (Boolean)returnable;
    }
    
    public Boolean getBooleanOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        if (returnable instanceof String) {
            returnable = Boolean.valueOf((String)returnable);
        }
        return (Boolean)returnable;
    }
    
    @Deprecated
    public Boolean getBooleanOrDefault(final String key, final boolean defaultValue) {
        if (this.containsKey(key)) {
            Object returnable = ((HashMap<K, Object>)this).get(key);
            if (returnable instanceof String) {
                returnable = Boolean.valueOf((String)returnable);
            }
            return (Boolean)returnable;
        }
        return defaultValue;
    }
    
    public Byte getByte(final JsonKey key) {
        Object returnable = ((HashMap<K, Object>)this).get(key.getKey());
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).byteValue();
    }
    
    @Deprecated
    public Byte getByte(final String key) {
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).byteValue();
    }
    
    public Byte getByteOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).byteValue();
    }
    
    @Deprecated
    public Byte getByteOrDefault(final String key, final byte defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).byteValue();
    }
    
    public <T extends Collection<?>> T getCollection(final JsonKey key) {
        return (T)this.get(key.getKey());
    }
    
    @Deprecated
    public <T extends Collection<?>> T getCollection(final String key) {
        return (T)this.get(key);
    }
    
    public <T extends Collection<?>> T getCollectionOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        return (T)returnable;
    }
    
    @Deprecated
    public <T extends Collection<?>> T getCollectionOrDefault(final String key, final T defaultValue) {
        if (this.containsKey(key)) {
            final Object returnable = ((HashMap<K, Object>)this).get(key);
            return (T)returnable;
        }
        return defaultValue;
    }
    
    public Double getDouble(final JsonKey key) {
        Object returnable = ((HashMap<K, Object>)this).get(key.getKey());
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).doubleValue();
    }
    
    @Deprecated
    public Double getDouble(final String key) {
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).doubleValue();
    }
    
    public Double getDoubleOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).doubleValue();
    }
    
    @Deprecated
    public Double getDoubleOrDefault(final String key, final double defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).doubleValue();
    }
    
    @Deprecated
    public <T extends Enum<T>> T getEnum(final JsonKey key) throws ClassNotFoundException {
        final String value = this.getString(key);
        if (value == null) {
            return null;
        }
        final String[] splitValues = value.split("\\.");
        final int numberOfSplitValues = splitValues.length;
        final StringBuilder returnTypeName = new StringBuilder();
        final StringBuilder enumName = new StringBuilder();
        for (int i = 0; i < numberOfSplitValues; ++i) {
            if (i == numberOfSplitValues - 1) {
                enumName.append(splitValues[i]);
            }
            else if (i == numberOfSplitValues - 2) {
                returnTypeName.append(splitValues[i]);
            }
            else {
                returnTypeName.append(splitValues[i]);
                returnTypeName.append(".");
            }
        }
        final Class<T> returnType = (Class<T>)Class.forName(returnTypeName.toString());
        final T returnable = Enum.valueOf(returnType, enumName.toString());
        return returnable;
    }
    
    @Deprecated
    public <T extends Enum<T>> T getEnum(final String key) throws ClassNotFoundException {
        final String value = this.getStringOrDefault(key, "");
        if (value == null) {
            return null;
        }
        final String[] splitValues = value.split("\\.");
        final int numberOfSplitValues = splitValues.length;
        final StringBuilder returnTypeName = new StringBuilder();
        final StringBuilder enumName = new StringBuilder();
        for (int i = 0; i < numberOfSplitValues; ++i) {
            if (i == numberOfSplitValues - 1) {
                enumName.append(splitValues[i]);
            }
            else if (i == numberOfSplitValues - 2) {
                returnTypeName.append(splitValues[i]);
            }
            else {
                returnTypeName.append(splitValues[i]);
                returnTypeName.append(".");
            }
        }
        final Class<T> returnType = (Class<T>)Class.forName(returnTypeName.toString());
        final T returnable = Enum.valueOf(returnType, enumName.toString());
        return returnable;
    }
    
    @Deprecated
    public <T extends Enum<T>> T getEnumOrDefault(final JsonKey key) throws ClassNotFoundException {
        T returnable;
        if (this.containsKey(key)) {
            final String value = this.getStringOrDefault(key.getKey(), "");
            if (value == null) {
                return null;
            }
            final String[] splitValues = value.split("\\.");
            final int numberOfSplitValues = splitValues.length;
            final StringBuilder returnTypeName = new StringBuilder();
            final StringBuilder enumName = new StringBuilder();
            for (int i = 0; i < numberOfSplitValues; ++i) {
                if (i == numberOfSplitValues - 1) {
                    enumName.append(splitValues[i]);
                }
                else if (i == numberOfSplitValues - 2) {
                    returnTypeName.append(splitValues[i]);
                }
                else {
                    returnTypeName.append(splitValues[i]);
                    returnTypeName.append(".");
                }
            }
            final Class<T> returnType = (Class<T>)Class.forName(returnTypeName.toString());
            returnable = Enum.valueOf(returnType, enumName.toString());
        }
        else {
            returnable = (T)key.getValue();
        }
        return returnable;
    }
    
    @Deprecated
    public <T extends Enum<T>> T getEnumOrDefault(final String key, final T defaultValue) throws ClassNotFoundException {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        final String value = this.getStringOrDefault(key, "");
        if (value == null) {
            return null;
        }
        final String[] splitValues = value.split("\\.");
        final int numberOfSplitValues = splitValues.length;
        final StringBuilder returnTypeName = new StringBuilder();
        final StringBuilder enumName = new StringBuilder();
        for (int i = 0; i < numberOfSplitValues; ++i) {
            if (i == numberOfSplitValues - 1) {
                enumName.append(splitValues[i]);
            }
            else if (i == numberOfSplitValues - 2) {
                returnTypeName.append(splitValues[i]);
            }
            else {
                returnTypeName.append(splitValues[i]);
                returnTypeName.append(".");
            }
        }
        final Class<T> returnType = (Class<T>)Class.forName(returnTypeName.toString());
        final T returnable = Enum.valueOf(returnType, enumName.toString());
        return returnable;
    }
    
    public Float getFloat(final JsonKey key) {
        Object returnable = ((HashMap<K, Object>)this).get(key.getKey());
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).floatValue();
    }
    
    @Deprecated
    public Float getFloat(final String key) {
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).floatValue();
    }
    
    public Float getFloatOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).floatValue();
    }
    
    @Deprecated
    public Float getFloatOrDefault(final String key, final float defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).floatValue();
    }
    
    public Integer getInteger(final JsonKey key) {
        Object returnable = ((HashMap<K, Object>)this).get(key.getKey());
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).intValue();
    }
    
    @Deprecated
    public Integer getInteger(final String key) {
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).intValue();
    }
    
    public Integer getIntegerOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).intValue();
    }
    
    @Deprecated
    public Integer getIntegerOrDefault(final String key, final int defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).intValue();
    }
    
    public Long getLong(final JsonKey key) {
        Object returnable = ((HashMap<K, Object>)this).get(key.getKey());
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).longValue();
    }
    
    @Deprecated
    public Long getLong(final String key) {
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).longValue();
    }
    
    public Long getLongOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).longValue();
    }
    
    @Deprecated
    public Long getLongOrDefault(final String key, final long defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).longValue();
    }
    
    public <T extends Map<?, ?>> T getMap(final JsonKey key) {
        return (T)this.get(key.getKey());
    }
    
    @Deprecated
    public <T extends Map<?, ?>> T getMap(final String key) {
        return (T)this.get(key);
    }
    
    public <T extends Map<?, ?>> T getMapOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        return (T)returnable;
    }
    
    @Deprecated
    public <T extends Map<?, ?>> T getMapOrDefault(final String key, final T defaultValue) {
        Object returnable;
        if (this.containsKey(key)) {
            returnable = ((HashMap<K, Object>)this).get(key);
        }
        else {
            returnable = defaultValue;
        }
        return (T)returnable;
    }
    
    public Short getShort(final JsonKey key) {
        Object returnable = ((HashMap<K, Object>)this).get(key.getKey());
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).shortValue();
    }
    
    @Deprecated
    public Short getShort(final String key) {
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).shortValue();
    }
    
    public Short getShortOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).shortValue();
    }
    
    @Deprecated
    public Short getShortOrDefault(final String key, final short defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable == null) {
            return null;
        }
        if (returnable instanceof String) {
            returnable = new BigDecimal((String)returnable);
        }
        return ((Number)returnable).shortValue();
    }
    
    public String getString(final JsonKey key) {
        Object returnable = ((HashMap<K, Object>)this).get(key.getKey());
        if (returnable instanceof Boolean) {
            returnable = returnable.toString();
        }
        else if (returnable instanceof Number) {
            returnable = returnable.toString();
        }
        return (String)returnable;
    }
    
    @Deprecated
    public String getString(final String key) {
        Object returnable = ((HashMap<K, Object>)this).get(key);
        if (returnable instanceof Boolean) {
            returnable = returnable.toString();
        }
        else if (returnable instanceof Number) {
            returnable = returnable.toString();
        }
        return (String)returnable;
    }
    
    public String getStringOrDefault(final JsonKey key) {
        Object returnable;
        if (this.containsKey(key.getKey())) {
            returnable = ((HashMap<K, Object>)this).get(key.getKey());
        }
        else {
            returnable = key.getValue();
        }
        if (returnable instanceof Boolean) {
            returnable = returnable.toString();
        }
        else if (returnable instanceof Number) {
            returnable = returnable.toString();
        }
        return (String)returnable;
    }
    
    @Deprecated
    public String getStringOrDefault(final String key, final String defaultValue) {
        if (this.containsKey(key)) {
            Object returnable = ((HashMap<K, Object>)this).get(key);
            if (returnable instanceof Boolean) {
                returnable = returnable.toString();
            }
            else if (returnable instanceof Number) {
                returnable = returnable.toString();
            }
            return (String)returnable;
        }
        return defaultValue;
    }
    
    @Override
    public String toJson() {
        final StringWriter writable = new StringWriter();
        try {
            this.toJson(writable);
        }
        catch (final IOException ex) {}
        return writable.toString();
    }
    
    @Override
    public void toJson(final Writer writable) throws IOException {
        boolean isFirstEntry = true;
        final Iterator<Map.Entry<String, Object>> entries = this.entrySet().iterator();
        writable.write(123);
        while (entries.hasNext()) {
            if (isFirstEntry) {
                isFirstEntry = false;
            }
            else {
                writable.write(44);
            }
            final Map.Entry<String, Object> entry = entries.next();
            writable.write(Jsoner.serialize(entry.getKey()));
            writable.write(58);
            writable.write(Jsoner.serialize(entry.getValue()));
        }
        writable.write(125);
    }
}
