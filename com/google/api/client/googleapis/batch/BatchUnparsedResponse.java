package com.google.api.client.googleapis.batch;

import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.HttpTransport;
import java.io.ByteArrayInputStream;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpResponse;
import java.io.FilterInputStream;
import com.google.api.client.util.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.List;

final class BatchUnparsedResponse
{
    private final String boundary;
    private final List<BatchRequest.RequestInfo<?, ?>> requestInfos;
    private final InputStream inputStream;
    boolean hasNext;
    List<BatchRequest.RequestInfo<?, ?>> unsuccessfulRequestInfos;
    private int contentId;
    private final boolean retryAllowed;
    
    BatchUnparsedResponse(final InputStream inputStream, final String boundary, final List<BatchRequest.RequestInfo<?, ?>> requestInfos, final boolean retryAllowed) throws IOException {
        this.hasNext = true;
        this.unsuccessfulRequestInfos = new ArrayList<BatchRequest.RequestInfo<?, ?>>();
        this.contentId = 0;
        this.boundary = boundary;
        this.requestInfos = requestInfos;
        this.retryAllowed = retryAllowed;
        this.inputStream = inputStream;
        this.checkForFinalBoundary(this.readLine());
    }
    
    void parseNextResponse() throws IOException {
        ++this.contentId;
        String line;
        while ((line = this.readLine()) != null && !line.equals("")) {}
        final String statusLine = this.readLine();
        final String[] statusParts = statusLine.split(" ");
        final int statusCode = Integer.parseInt(statusParts[1]);
        final List<String> headerNames = new ArrayList<String>();
        final List<String> headerValues = new ArrayList<String>();
        long contentLength = -1L;
        while ((line = this.readLine()) != null && !line.equals("")) {
            final String[] headerParts = line.split(": ", 2);
            final String headerName = headerParts[0];
            final String headerValue = headerParts[1];
            headerNames.add(headerName);
            headerValues.add(headerValue);
            if ("Content-Length".equalsIgnoreCase(headerName.trim())) {
                contentLength = Long.parseLong(headerValue);
            }
        }
        InputStream body;
        if (contentLength == -1L) {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            while ((line = this.readRawLine()) != null && !line.startsWith(this.boundary)) {
                buffer.write(line.getBytes("ISO-8859-1"));
            }
            body = trimCrlf(buffer.toByteArray());
            line = trimCrlf(line);
        }
        else {
            body = new FilterInputStream(ByteStreams.limit(this.inputStream, contentLength)) {
                @Override
                public void close() {
                }
            };
        }
        final HttpResponse response = this.getFakeResponse(statusCode, body, headerNames, headerValues);
        this.parseAndCallback(this.requestInfos.get(this.contentId - 1), statusCode, response);
        while (true) {
            if (body.skip(contentLength) <= 0L) {
                if (body.read() != -1) {
                    continue;
                }
                break;
            }
        }
        if (contentLength != -1L) {
            line = this.readLine();
        }
        while (line != null && line.length() == 0) {
            line = this.readLine();
        }
        this.checkForFinalBoundary(line);
    }
    
    private <T, E> void parseAndCallback(final BatchRequest.RequestInfo<T, E> requestInfo, final int statusCode, final HttpResponse response) throws IOException {
        final BatchCallback<T, E> callback = requestInfo.callback;
        final HttpHeaders responseHeaders = response.getHeaders();
        final HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler = requestInfo.request.getUnsuccessfulResponseHandler();
        if (HttpStatusCodes.isSuccess(statusCode)) {
            if (callback == null) {
                return;
            }
            final T parsed = this.getParsedDataClass(requestInfo.dataClass, response, requestInfo);
            callback.onSuccess(parsed, responseHeaders);
        }
        else {
            final HttpContent content = requestInfo.request.getContent();
            final boolean retrySupported = this.retryAllowed && (content == null || content.retrySupported());
            boolean errorHandled = false;
            boolean redirectRequest = false;
            if (unsuccessfulResponseHandler != null) {
                errorHandled = unsuccessfulResponseHandler.handleResponse(requestInfo.request, response, retrySupported);
            }
            if (!errorHandled && requestInfo.request.handleRedirect(response.getStatusCode(), response.getHeaders())) {
                redirectRequest = true;
            }
            if (retrySupported && (errorHandled || redirectRequest)) {
                this.unsuccessfulRequestInfos.add(requestInfo);
            }
            else {
                if (callback == null) {
                    return;
                }
                final E parsed2 = this.getParsedDataClass(requestInfo.errorClass, response, requestInfo);
                callback.onFailure(parsed2, responseHeaders);
            }
        }
    }
    
    private <A, T, E> A getParsedDataClass(final Class<A> dataClass, final HttpResponse response, final BatchRequest.RequestInfo<T, E> requestInfo) throws IOException {
        if (dataClass == Void.class) {
            return null;
        }
        return (A)requestInfo.request.getParser().parseAndClose(response.getContent(), response.getContentCharset(), (Class)dataClass);
    }
    
    private HttpResponse getFakeResponse(final int statusCode, final InputStream partContent, final List<String> headerNames, final List<String> headerValues) throws IOException {
        final HttpRequest request = new FakeResponseHttpTransport(statusCode, partContent, headerNames, headerValues).createRequestFactory().buildPostRequest(new GenericUrl("http://google.com/"), (HttpContent)null);
        request.setLoggingEnabled(false);
        request.setThrowExceptionOnExecuteError(false);
        return request.execute();
    }
    
    private String readRawLine() throws IOException {
        int b = this.inputStream.read();
        if (b == -1) {
            return null;
        }
        final StringBuilder buffer = new StringBuilder();
        while (b != -1) {
            buffer.append((char)b);
            if (b == 10) {
                break;
            }
            b = this.inputStream.read();
        }
        return buffer.toString();
    }
    
    private String readLine() throws IOException {
        return trimCrlf(this.readRawLine());
    }
    
    private static String trimCrlf(final String input) {
        if (input.endsWith("\r\n")) {
            return input.substring(0, input.length() - 2);
        }
        if (input.endsWith("\n")) {
            return input.substring(0, input.length() - 1);
        }
        return input;
    }
    
    private static InputStream trimCrlf(final byte[] bytes) {
        int length = bytes.length;
        if (length > 0 && bytes[length - 1] == 10) {
            --length;
        }
        if (length > 0 && bytes[length - 1] == 13) {
            --length;
        }
        return new ByteArrayInputStream(bytes, 0, length);
    }
    
    private void checkForFinalBoundary(final String boundaryLine) throws IOException {
        if (boundaryLine.equals(this.boundary + "--")) {
            this.hasNext = false;
            this.inputStream.close();
        }
    }
    
    private static class FakeResponseHttpTransport extends HttpTransport
    {
        private int statusCode;
        private InputStream partContent;
        private List<String> headerNames;
        private List<String> headerValues;
        
        FakeResponseHttpTransport(final int statusCode, final InputStream partContent, final List<String> headerNames, final List<String> headerValues) {
            this.statusCode = statusCode;
            this.partContent = partContent;
            this.headerNames = headerNames;
            this.headerValues = headerValues;
        }
        
        protected LowLevelHttpRequest buildRequest(final String method, final String url) {
            return new FakeLowLevelHttpRequest(this.partContent, this.statusCode, this.headerNames, this.headerValues);
        }
    }
    
    private static class FakeLowLevelHttpRequest extends LowLevelHttpRequest
    {
        private InputStream partContent;
        private int statusCode;
        private List<String> headerNames;
        private List<String> headerValues;
        
        FakeLowLevelHttpRequest(final InputStream partContent, final int statusCode, final List<String> headerNames, final List<String> headerValues) {
            this.partContent = partContent;
            this.statusCode = statusCode;
            this.headerNames = headerNames;
            this.headerValues = headerValues;
        }
        
        public void addHeader(final String name, final String value) {
        }
        
        public LowLevelHttpResponse execute() {
            final FakeLowLevelHttpResponse response = new FakeLowLevelHttpResponse(this.partContent, this.statusCode, this.headerNames, this.headerValues);
            return response;
        }
    }
    
    private static class FakeLowLevelHttpResponse extends LowLevelHttpResponse
    {
        private InputStream partContent;
        private int statusCode;
        private List<String> headerNames;
        private List<String> headerValues;
        
        FakeLowLevelHttpResponse(final InputStream partContent, final int statusCode, final List<String> headerNames, final List<String> headerValues) {
            this.headerNames = new ArrayList<String>();
            this.headerValues = new ArrayList<String>();
            this.partContent = partContent;
            this.statusCode = statusCode;
            this.headerNames = headerNames;
            this.headerValues = headerValues;
        }
        
        public InputStream getContent() {
            return this.partContent;
        }
        
        public int getStatusCode() {
            return this.statusCode;
        }
        
        public String getContentEncoding() {
            return null;
        }
        
        public long getContentLength() {
            return 0L;
        }
        
        public String getContentType() {
            return null;
        }
        
        public String getStatusLine() {
            return null;
        }
        
        public String getReasonPhrase() {
            return null;
        }
        
        public int getHeaderCount() {
            return this.headerNames.size();
        }
        
        public String getHeaderName(final int index) {
            return this.headerNames.get(index);
        }
        
        public String getHeaderValue(final int index) {
            return this.headerValues.get(index);
        }
    }
}
