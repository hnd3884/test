package com.fasterxml.jackson.module.jaxb.deser;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.activation.DataSource;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import javax.activation.DataHandler;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class DataHandlerJsonDeserializer extends StdScalarDeserializer<DataHandler>
{
    private static final long serialVersionUID = 1L;
    
    public DataHandlerJsonDeserializer() {
        super((Class)DataHandler.class);
    }
    
    public DataHandler deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final byte[] value = jp.getBinaryValue();
        return new DataHandler(new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(value);
            }
            
            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new IOException();
            }
            
            @Override
            public String getContentType() {
                return "application/octet-stream";
            }
            
            @Override
            public String getName() {
                return "json-binary-data";
            }
        });
    }
}
