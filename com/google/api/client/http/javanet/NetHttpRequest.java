package com.google.api.client.http.javanet;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Executors;
import com.google.api.client.util.StreamingContent;
import java.util.concurrent.Callable;
import java.io.OutputStream;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.LowLevelHttpResponse;
import java.io.IOException;
import com.google.common.annotations.VisibleForTesting;
import java.net.HttpURLConnection;
import com.google.api.client.http.LowLevelHttpRequest;

final class NetHttpRequest extends LowLevelHttpRequest
{
    private final HttpURLConnection connection;
    private int writeTimeout;
    private static final OutputWriter DEFAULT_CONNECTION_WRITER;
    
    NetHttpRequest(final HttpURLConnection connection) {
        this.connection = connection;
        this.writeTimeout = 0;
        connection.setInstanceFollowRedirects(false);
    }
    
    @Override
    public void addHeader(final String name, final String value) {
        this.connection.addRequestProperty(name, value);
    }
    
    @VisibleForTesting
    String getRequestProperty(final String name) {
        return this.connection.getRequestProperty(name);
    }
    
    @Override
    public void setTimeout(final int connectTimeout, final int readTimeout) {
        this.connection.setReadTimeout(readTimeout);
        this.connection.setConnectTimeout(connectTimeout);
    }
    
    @Override
    public void setWriteTimeout(final int writeTimeout) throws IOException {
        this.writeTimeout = writeTimeout;
    }
    
    @Override
    public LowLevelHttpResponse execute() throws IOException {
        return this.execute(NetHttpRequest.DEFAULT_CONNECTION_WRITER);
    }
    
    @VisibleForTesting
    LowLevelHttpResponse execute(final OutputWriter outputWriter) throws IOException {
        final HttpURLConnection connection = this.connection;
        if (this.getStreamingContent() != null) {
            final String contentType = this.getContentType();
            if (contentType != null) {
                this.addHeader("Content-Type", contentType);
            }
            final String contentEncoding = this.getContentEncoding();
            if (contentEncoding != null) {
                this.addHeader("Content-Encoding", contentEncoding);
            }
            final long contentLength = this.getContentLength();
            if (contentLength >= 0L) {
                connection.setRequestProperty("Content-Length", Long.toString(contentLength));
            }
            final String requestMethod = connection.getRequestMethod();
            if ("POST".equals(requestMethod) || "PUT".equals(requestMethod)) {
                connection.setDoOutput(true);
                if (contentLength >= 0L && contentLength <= 2147483647L) {
                    connection.setFixedLengthStreamingMode((int)contentLength);
                }
                else {
                    connection.setChunkedStreamingMode(0);
                }
                final OutputStream out = connection.getOutputStream();
                boolean threw = true;
                try {
                    this.writeContentToOutputStream(outputWriter, out);
                    threw = false;
                }
                catch (final IOException e) {
                    if (!this.hasResponse(connection)) {
                        throw e;
                    }
                    try {
                        out.close();
                    }
                    catch (final IOException exception) {
                        if (!threw) {
                            throw exception;
                        }
                    }
                }
                finally {
                    try {
                        out.close();
                    }
                    catch (final IOException exception2) {
                        if (!threw) {
                            throw exception2;
                        }
                    }
                }
            }
            else {
                Preconditions.checkArgument(contentLength == 0L, "%s with non-zero content length is not supported", requestMethod);
            }
        }
        boolean successfulConnection = false;
        try {
            connection.connect();
            final NetHttpResponse response = new NetHttpResponse(connection);
            successfulConnection = true;
            return response;
        }
        finally {
            if (!successfulConnection) {
                connection.disconnect();
            }
        }
    }
    
    private boolean hasResponse(final HttpURLConnection connection) {
        try {
            return connection.getResponseCode() > 0;
        }
        catch (final IOException e) {
            return false;
        }
    }
    
    private void writeContentToOutputStream(final OutputWriter outputWriter, final OutputStream out) throws IOException {
        if (this.writeTimeout == 0) {
            outputWriter.write(out, this.getStreamingContent());
        }
        else {
            final StreamingContent content = this.getStreamingContent();
            final Callable<Boolean> writeContent = new Callable<Boolean>() {
                @Override
                public Boolean call() throws IOException {
                    outputWriter.write(out, content);
                    return Boolean.TRUE;
                }
            };
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<Boolean> future = executor.submit(new FutureTask<Object>(writeContent), (Boolean)null);
            executor.shutdown();
            try {
                future.get(this.writeTimeout, TimeUnit.MILLISECONDS);
            }
            catch (final InterruptedException e) {
                throw new IOException("Socket write interrupted", e);
            }
            catch (final ExecutionException e2) {
                throw new IOException("Exception in socket write", e2);
            }
            catch (final TimeoutException e3) {
                throw new IOException("Socket write timed out", e3);
            }
            if (!executor.isTerminated()) {
                executor.shutdown();
            }
        }
    }
    
    static {
        DEFAULT_CONNECTION_WRITER = new DefaultOutputWriter();
    }
    
    static class DefaultOutputWriter implements OutputWriter
    {
        @Override
        public void write(final OutputStream outputStream, final StreamingContent content) throws IOException {
            content.writeTo(outputStream);
        }
    }
    
    interface OutputWriter
    {
        void write(final OutputStream p0, final StreamingContent p1) throws IOException;
    }
}
