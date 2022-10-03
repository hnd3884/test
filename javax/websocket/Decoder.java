package javax.websocket;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

public interface Decoder
{
    void init(final EndpointConfig p0);
    
    void destroy();
    
    public interface TextStream<T> extends Decoder
    {
        T decode(final Reader p0) throws DecodeException, IOException;
    }
    
    public interface Text<T> extends Decoder
    {
        T decode(final String p0) throws DecodeException;
        
        boolean willDecode(final String p0);
    }
    
    public interface BinaryStream<T> extends Decoder
    {
        T decode(final InputStream p0) throws DecodeException, IOException;
    }
    
    public interface Binary<T> extends Decoder
    {
        T decode(final ByteBuffer p0) throws DecodeException;
        
        boolean willDecode(final ByteBuffer p0);
    }
}
