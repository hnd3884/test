package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import org.apache.tomcat.util.collections.ConcurrentCache;

public class MediaTypeCache
{
    private final ConcurrentCache<String, String[]> cache;
    
    public MediaTypeCache(final int size) {
        this.cache = (ConcurrentCache<String, String[]>)new ConcurrentCache(size);
    }
    
    public String[] parse(final String input) {
        String[] result = (String[])this.cache.get((Object)input);
        if (result != null) {
            return result;
        }
        MediaType m = null;
        try {
            m = MediaType.parseMediaType(new StringReader(input));
        }
        catch (final IOException ex) {}
        if (m != null) {
            result = new String[] { m.toStringNoCharset(), m.getCharset() };
            this.cache.put((Object)input, (Object)result);
        }
        return result;
    }
}
