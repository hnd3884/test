package org.msgpack.util.json;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.msgpack.type.ValueFactory;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import org.msgpack.type.Value;
import java.io.InputStreamReader;
import org.msgpack.MessagePack;
import java.io.InputStream;
import org.json.simple.parser.JSONParser;
import java.io.Reader;
import org.msgpack.unpacker.Converter;

public class JSONUnpacker extends Converter
{
    protected Reader in;
    private JSONParser parser;
    
    public JSONUnpacker(final InputStream in) {
        this(new MessagePack(), in);
    }
    
    public JSONUnpacker(final MessagePack msgpack, final InputStream in) {
        this(msgpack, new InputStreamReader(in));
    }
    
    JSONUnpacker(final MessagePack msgpack, final Reader in) {
        super(msgpack, null);
        this.in = in;
        this.parser = new JSONParser();
    }
    
    @Override
    protected Value nextValue() throws IOException {
        try {
            final Object obj = this.parser.parse(this.in);
            return this.objectToValue(obj);
        }
        catch (final ParseException e) {
            throw new IOException((Throwable)e);
        }
        catch (final IOException e2) {
            throw new IOException(e2);
        }
    }
    
    private Value objectToValue(final Object obj) {
        if (obj instanceof String) {
            return ValueFactory.createRawValue((String)obj);
        }
        if (obj instanceof Integer) {
            return ValueFactory.createIntegerValue((int)obj);
        }
        if (obj instanceof Long) {
            return ValueFactory.createIntegerValue((long)obj);
        }
        if (obj instanceof Map) {
            return this.mapToValue((Map)obj);
        }
        if (obj instanceof List) {
            return this.listToValue((List)obj);
        }
        if (obj instanceof Boolean) {
            return ValueFactory.createBooleanValue((boolean)obj);
        }
        if (obj instanceof Double) {
            return ValueFactory.createFloatValue((double)obj);
        }
        return ValueFactory.createNilValue();
    }
    
    private Value listToValue(final List list) {
        final Value[] array = new Value[list.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.objectToValue(list.get(i));
        }
        return ValueFactory.createArrayValue(array, true);
    }
    
    private Value mapToValue(final Map map) {
        final Value[] kvs = new Value[map.size() * 2];
        final Iterator<Map.Entry> it = map.entrySet().iterator();
        for (int i = 0; i < kvs.length; i += 2) {
            final Map.Entry pair = it.next();
            kvs[i] = this.objectToValue(pair.getKey());
            kvs[i + 1] = this.objectToValue(pair.getValue());
        }
        return ValueFactory.createMapValue(kvs, true);
    }
    
    @Override
    public int getReadByteCount() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public void resetReadByteCount() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
        super.close();
    }
}
