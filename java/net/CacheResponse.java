package java.net;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class CacheResponse
{
    public abstract Map<String, List<String>> getHeaders() throws IOException;
    
    public abstract InputStream getBody() throws IOException;
}
