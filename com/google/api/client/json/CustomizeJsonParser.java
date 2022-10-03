package com.google.api.client.json;

import java.util.Collection;
import java.lang.reflect.Field;
import com.google.api.client.util.Beta;

@Beta
public class CustomizeJsonParser
{
    public boolean stopAt(final Object context, final String key) {
        return false;
    }
    
    public void handleUnrecognizedKey(final Object context, final String key) {
    }
    
    public Collection<Object> newInstanceForArray(final Object context, final Field field) {
        return null;
    }
    
    public Object newInstanceForObject(final Object context, final Class<?> fieldClass) {
        return null;
    }
}
