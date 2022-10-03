package org.apache.http;

public class TruncatedChunkException extends MalformedChunkCodingException
{
    private static final long serialVersionUID = -23506263930279460L;
    
    public TruncatedChunkException(final String message) {
        super(message);
    }
    
    public TruncatedChunkException(final String format, final Object... args) {
        super(HttpException.clean(String.format(format, args)));
    }
}
