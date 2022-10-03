package com.google.api.client.http;

import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import com.google.api.client.util.Preconditions;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.InputStream;
import com.google.api.client.util.FieldInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import com.google.api.client.util.Types;
import com.google.api.client.util.Data;
import com.google.api.client.util.escape.CharEscapers;
import java.io.StringWriter;
import com.google.api.client.util.ArrayValueMap;
import java.util.Map;
import com.google.api.client.util.GenericData;
import java.util.Arrays;
import java.lang.reflect.Type;
import com.google.api.client.util.ClassInfo;
import java.io.IOException;
import com.google.api.client.util.Throwables;
import java.io.Reader;
import java.io.StringReader;
import com.google.api.client.util.ObjectParser;

public class UrlEncodedParser implements ObjectParser
{
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String MEDIA_TYPE;
    
    public static void parse(final String content, final Object data) {
        parse(content, data, true);
    }
    
    public static void parse(final String content, final Object data, final boolean decodeEnabled) {
        if (content == null) {
            return;
        }
        try {
            parse(new StringReader(content), data, decodeEnabled);
        }
        catch (final IOException exception) {
            throw Throwables.propagate(exception);
        }
    }
    
    public static void parse(final Reader reader, final Object data) throws IOException {
        parse(reader, data, true);
    }
    
    public static void parse(final Reader reader, final Object data, final boolean decodeEnabled) throws IOException {
        final Class<?> clazz = data.getClass();
        final ClassInfo classInfo = ClassInfo.of(clazz);
        final List<Type> context = Arrays.asList(clazz);
        final GenericData genericData = GenericData.class.isAssignableFrom(clazz) ? ((GenericData)data) : null;
        final Map<Object, Object> map = Map.class.isAssignableFrom(clazz) ? ((Map)data) : null;
        final ArrayValueMap arrayValueMap = new ArrayValueMap(data);
        StringWriter nameWriter = new StringWriter();
        StringWriter valueWriter = new StringWriter();
        boolean readingName = true;
    Block_14:
        while (true) {
            final int read = reader.read();
            switch (read) {
                case -1:
                case 38: {
                    final String name = decodeEnabled ? CharEscapers.decodeUri(nameWriter.toString()) : nameWriter.toString();
                    if (name.length() != 0) {
                        final String stringValue = decodeEnabled ? CharEscapers.decodeUri(valueWriter.toString()) : valueWriter.toString();
                        final FieldInfo fieldInfo = classInfo.getFieldInfo(name);
                        if (fieldInfo != null) {
                            final Type type = Data.resolveWildcardTypeOrTypeVariable(context, fieldInfo.getGenericType());
                            if (Types.isArray(type)) {
                                final Class<?> rawArrayComponentType = Types.getRawArrayComponentType(context, Types.getArrayComponentType(type));
                                arrayValueMap.put(fieldInfo.getField(), rawArrayComponentType, parseValue(rawArrayComponentType, context, stringValue));
                            }
                            else if (Types.isAssignableToOrFrom(Types.getRawArrayComponentType(context, type), Iterable.class)) {
                                Collection<Object> collection = (Collection<Object>)fieldInfo.getValue(data);
                                if (collection == null) {
                                    collection = Data.newCollectionInstance(type);
                                    fieldInfo.setValue(data, collection);
                                }
                                final Type subFieldType = (type == Object.class) ? null : Types.getIterableParameter(type);
                                collection.add(parseValue(subFieldType, context, stringValue));
                            }
                            else {
                                fieldInfo.setValue(data, parseValue(type, context, stringValue));
                            }
                        }
                        else if (map != null) {
                            ArrayList<String> listValue = map.get(name);
                            if (listValue == null) {
                                listValue = new ArrayList<String>();
                                if (genericData != null) {
                                    genericData.set(name, listValue);
                                }
                                else {
                                    map.put(name, listValue);
                                }
                            }
                            listValue.add(stringValue);
                        }
                    }
                    readingName = true;
                    nameWriter = new StringWriter();
                    valueWriter = new StringWriter();
                    if (read == -1) {
                        break Block_14;
                    }
                    continue;
                }
                case 61: {
                    if (readingName) {
                        readingName = false;
                        continue;
                    }
                    valueWriter.write(read);
                    continue;
                }
                default: {
                    if (readingName) {
                        nameWriter.write(read);
                        continue;
                    }
                    valueWriter.write(read);
                    continue;
                }
            }
        }
        arrayValueMap.setValues();
    }
    
    private static Object parseValue(final Type valueType, final List<Type> context, final String value) {
        final Type resolved = Data.resolveWildcardTypeOrTypeVariable(context, valueType);
        return Data.parsePrimitiveValue(resolved, value);
    }
    
    @Override
    public <T> T parseAndClose(final InputStream in, final Charset charset, final Class<T> dataClass) throws IOException {
        final InputStreamReader r = new InputStreamReader(in, charset);
        return this.parseAndClose(r, dataClass);
    }
    
    @Override
    public Object parseAndClose(final InputStream in, final Charset charset, final Type dataType) throws IOException {
        final InputStreamReader r = new InputStreamReader(in, charset);
        return this.parseAndClose(r, dataType);
    }
    
    @Override
    public <T> T parseAndClose(final Reader reader, final Class<T> dataClass) throws IOException {
        return (T)this.parseAndClose(reader, (Type)dataClass);
    }
    
    @Override
    public Object parseAndClose(final Reader reader, final Type dataType) throws IOException {
        Preconditions.checkArgument(dataType instanceof Class, (Object)"dataType has to be of type Class<?>");
        final Object newInstance = Types.newInstance((Class<Object>)dataType);
        parse(new BufferedReader(reader), newInstance);
        return newInstance;
    }
    
    static {
        MEDIA_TYPE = new HttpMediaType("application/x-www-form-urlencoded").setCharsetParameter(StandardCharsets.UTF_8).build();
    }
}
