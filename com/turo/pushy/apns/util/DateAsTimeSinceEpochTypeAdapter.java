package com.turo.pushy.apns.util;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import com.google.gson.JsonDeserializer;
import java.util.Date;
import com.google.gson.JsonSerializer;

public class DateAsTimeSinceEpochTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date>
{
    private final TimeUnit timeUnit;
    
    public DateAsTimeSinceEpochTypeAdapter(final TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit);
        this.timeUnit = timeUnit;
    }
    
    public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        Date date;
        if (json.isJsonPrimitive()) {
            date = new Date(this.timeUnit.toMillis(json.getAsLong()));
        }
        else {
            if (!json.isJsonNull()) {
                throw new JsonParseException("Dates represented as time since the epoch must either be numbers or null.");
            }
            date = null;
        }
        return date;
    }
    
    public JsonElement serialize(final Date src, final Type typeOfSrc, final JsonSerializationContext context) {
        JsonElement element;
        if (src != null) {
            element = (JsonElement)new JsonPrimitive((Number)this.timeUnit.convert(src.getTime(), TimeUnit.MILLISECONDS));
        }
        else {
            element = (JsonElement)JsonNull.INSTANCE;
        }
        return element;
    }
}
