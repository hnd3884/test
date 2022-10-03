package io.netty.handler.codec.http;

public class HttpResponseDecoder extends HttpObjectDecoder
{
    private static final HttpResponseStatus UNKNOWN_STATUS;
    
    public HttpResponseDecoder() {
    }
    
    public HttpResponseDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true);
    }
    
    public HttpResponseDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders);
    }
    
    public HttpResponseDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize);
    }
    
    public HttpResponseDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize, final boolean allowDuplicateContentLengths) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize, allowDuplicateContentLengths);
    }
    
    public HttpResponseDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize, final boolean allowDuplicateContentLengths, final boolean allowPartialChunks) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks);
    }
    
    @Override
    protected HttpMessage createMessage(final String[] initialLine) {
        return new DefaultHttpResponse(HttpVersion.valueOf(initialLine[0]), HttpResponseStatus.valueOf(Integer.parseInt(initialLine[1]), initialLine[2]), this.validateHeaders);
    }
    
    @Override
    protected HttpMessage createInvalidMessage() {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseDecoder.UNKNOWN_STATUS, this.validateHeaders);
    }
    
    @Override
    protected boolean isDecodingRequest() {
        return false;
    }
    
    static {
        UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");
    }
}
