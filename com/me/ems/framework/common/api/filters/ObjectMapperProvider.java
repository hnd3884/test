package com.me.ems.framework.common.api.filters;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.ext.ContextResolver;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper>
{
    public ObjectMapper getContext(final Class<?> aClass) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer((Class)Long.class, (JsonSerializer)ToStringSerializer.instance);
        objectMapper.registerModule((Module)simpleModule);
        return objectMapper;
    }
}
