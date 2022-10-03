package com.adventnet.persistence;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonStringDataTypeValidator implements DataTypeValidator
{
    @Override
    public void validate(final Object value) {
        try {
            if (value != null) {
                new JSONObject(value.toString());
            }
        }
        catch (final JSONException e) {
            throw new IllegalArgumentException(e.getMessage(), (Throwable)e);
        }
    }
}
