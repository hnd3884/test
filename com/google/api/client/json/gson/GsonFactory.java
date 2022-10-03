package com.google.api.client.json.gson;

import com.google.gson.stream.JsonWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;
import com.google.api.client.json.JsonGenerator;
import java.io.OutputStream;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.api.client.json.JsonParser;
import java.io.InputStream;
import com.google.api.client.util.Beta;
import com.google.api.client.json.JsonFactory;

public class GsonFactory extends JsonFactory
{
    @Beta
    public static GsonFactory getDefaultInstance() {
        return InstanceHolder.INSTANCE;
    }
    
    public JsonParser createJsonParser(final InputStream in) {
        return this.createJsonParser(new InputStreamReader(in, StandardCharsets.UTF_8));
    }
    
    public JsonParser createJsonParser(final InputStream in, final Charset charset) {
        if (charset == null) {
            return this.createJsonParser(in);
        }
        return this.createJsonParser(new InputStreamReader(in, charset));
    }
    
    public JsonParser createJsonParser(final String value) {
        return this.createJsonParser(new StringReader(value));
    }
    
    public JsonParser createJsonParser(final Reader reader) {
        return new GsonParser(this, new JsonReader(reader));
    }
    
    public JsonGenerator createJsonGenerator(final OutputStream out, final Charset enc) {
        return this.createJsonGenerator(new OutputStreamWriter(out, enc));
    }
    
    public JsonGenerator createJsonGenerator(final Writer writer) {
        return new GsonGenerator(this, new JsonWriter(writer));
    }
    
    @Beta
    static class InstanceHolder
    {
        static final GsonFactory INSTANCE;
        
        static {
            INSTANCE = new GsonFactory();
        }
    }
}
