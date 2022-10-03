package com.google.api.client.testing.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.util.IOUtils;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import com.google.api.client.http.LowLevelHttpResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.api.client.util.Beta;
import com.google.api.client.http.LowLevelHttpRequest;

@Beta
public class MockLowLevelHttpRequest extends LowLevelHttpRequest
{
    private String url;
    private final Map<String, List<String>> headersMap;
    private MockLowLevelHttpResponse response;
    
    public MockLowLevelHttpRequest() {
        this.headersMap = new HashMap<String, List<String>>();
        this.response = new MockLowLevelHttpResponse();
    }
    
    public MockLowLevelHttpRequest(final String url) {
        this.headersMap = new HashMap<String, List<String>>();
        this.response = new MockLowLevelHttpResponse();
        this.url = url;
    }
    
    @Override
    public void addHeader(String name, final String value) throws IOException {
        name = name.toLowerCase(Locale.US);
        List<String> values = this.headersMap.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            this.headersMap.put(name, values);
        }
        values.add(value);
    }
    
    @Override
    public LowLevelHttpResponse execute() throws IOException {
        return this.response;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends List<String>>)this.headersMap);
    }
    
    public String getFirstHeaderValue(final String name) {
        final List<String> values = this.headersMap.get(name.toLowerCase(Locale.US));
        return (values == null) ? null : values.get(0);
    }
    
    public List<String> getHeaderValues(final String name) {
        final List<String> values = this.headersMap.get(name.toLowerCase(Locale.US));
        return (values == null) ? Collections.emptyList() : Collections.unmodifiableList((List<? extends String>)values);
    }
    
    public MockLowLevelHttpRequest setUrl(final String url) {
        this.url = url;
        return this;
    }
    
    public String getContentAsString() throws IOException {
        if (this.getStreamingContent() == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.getStreamingContent().writeTo(out);
        final String contentEncoding = this.getContentEncoding();
        if (contentEncoding != null && contentEncoding.contains("gzip")) {
            final InputStream contentInputStream = new GZIPInputStream(new ByteArrayInputStream(out.toByteArray()));
            out = new ByteArrayOutputStream();
            IOUtils.copy(contentInputStream, out);
        }
        final String contentType = this.getContentType();
        final HttpMediaType mediaType = (contentType != null) ? new HttpMediaType(contentType) : null;
        final Charset charset = (mediaType == null || mediaType.getCharsetParameter() == null) ? StandardCharsets.ISO_8859_1 : mediaType.getCharsetParameter();
        return out.toString(charset.name());
    }
    
    public MockLowLevelHttpResponse getResponse() {
        return this.response;
    }
    
    public MockLowLevelHttpRequest setResponse(final MockLowLevelHttpResponse response) {
        this.response = response;
        return this;
    }
}
