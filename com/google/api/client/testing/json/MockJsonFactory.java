package com.google.api.client.testing.json;

import java.io.Writer;
import com.google.api.client.json.JsonGenerator;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.io.IOException;
import com.google.api.client.json.JsonParser;
import java.io.InputStream;
import com.google.api.client.util.Beta;
import com.google.api.client.json.JsonFactory;

@Beta
public class MockJsonFactory extends JsonFactory
{
    @Override
    public JsonParser createJsonParser(final InputStream in) throws IOException {
        return new MockJsonParser(this);
    }
    
    @Override
    public JsonParser createJsonParser(final InputStream in, final Charset charset) throws IOException {
        return new MockJsonParser(this);
    }
    
    @Override
    public JsonParser createJsonParser(final String value) throws IOException {
        return new MockJsonParser(this);
    }
    
    @Override
    public JsonParser createJsonParser(final Reader reader) throws IOException {
        return new MockJsonParser(this);
    }
    
    @Override
    public JsonGenerator createJsonGenerator(final OutputStream out, final Charset enc) throws IOException {
        return new MockJsonGenerator(this);
    }
    
    @Override
    public JsonGenerator createJsonGenerator(final Writer writer) throws IOException {
        return new MockJsonGenerator(this);
    }
}
