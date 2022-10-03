package com.google.api.client.json.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import com.google.api.client.json.JsonFactory;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
import com.google.api.client.json.JsonGenerator;

class GsonGenerator extends JsonGenerator
{
    private final JsonWriter writer;
    private final GsonFactory factory;
    
    GsonGenerator(final GsonFactory factory, final JsonWriter writer) {
        this.factory = factory;
        (this.writer = writer).setLenient(true);
    }
    
    public void flush() throws IOException {
        this.writer.flush();
    }
    
    public void close() throws IOException {
        this.writer.close();
    }
    
    public JsonFactory getFactory() {
        return this.factory;
    }
    
    public void writeBoolean(final boolean state) throws IOException {
        this.writer.value(state);
    }
    
    public void writeEndArray() throws IOException {
        this.writer.endArray();
    }
    
    public void writeEndObject() throws IOException {
        this.writer.endObject();
    }
    
    public void writeFieldName(final String name) throws IOException {
        this.writer.name(name);
    }
    
    public void writeNull() throws IOException {
        this.writer.nullValue();
    }
    
    public void writeNumber(final int v) throws IOException {
        this.writer.value((long)v);
    }
    
    public void writeNumber(final long v) throws IOException {
        this.writer.value(v);
    }
    
    public void writeNumber(final BigInteger v) throws IOException {
        this.writer.value((Number)v);
    }
    
    public void writeNumber(final double v) throws IOException {
        this.writer.value(v);
    }
    
    public void writeNumber(final float v) throws IOException {
        this.writer.value((double)v);
    }
    
    public void writeNumber(final BigDecimal v) throws IOException {
        this.writer.value((Number)v);
    }
    
    public void writeNumber(final String encodedValue) throws IOException {
        this.writer.value((Number)new StringNumber(encodedValue));
    }
    
    public void writeStartArray() throws IOException {
        this.writer.beginArray();
    }
    
    public void writeStartObject() throws IOException {
        this.writer.beginObject();
    }
    
    public void writeString(final String value) throws IOException {
        this.writer.value(value);
    }
    
    public void enablePrettyPrint() throws IOException {
        this.writer.setIndent("  ");
    }
    
    static final class StringNumber extends Number
    {
        private static final long serialVersionUID = 1L;
        private final String encodedValue;
        
        StringNumber(final String encodedValue) {
            this.encodedValue = encodedValue;
        }
        
        @Override
        public double doubleValue() {
            return 0.0;
        }
        
        @Override
        public float floatValue() {
            return 0.0f;
        }
        
        @Override
        public int intValue() {
            return 0;
        }
        
        @Override
        public long longValue() {
            return 0L;
        }
        
        @Override
        public String toString() {
            return this.encodedValue;
        }
    }
}
