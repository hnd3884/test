package com.google.api.client.googleapis.testing;

import java.util.List;
import java.util.Iterator;
import java.net.URLDecoder;
import java.io.IOException;
import com.google.common.collect.Lists;
import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.Map;

public final class TestUtils
{
    private static final String UTF_8 = "UTF-8";
    
    public static Map<String, String> parseQuery(final String query) throws IOException {
        final Map<String, String> map = new HashMap<String, String>();
        final Iterable<String> entries = Splitter.on('&').split((CharSequence)query);
        for (final String entry : entries) {
            final List<String> sides = Lists.newArrayList(Splitter.on('=').split((CharSequence)entry));
            if (sides.size() != 2) {
                throw new IOException("Invalid Query String");
            }
            final String key = URLDecoder.decode(sides.get(0), "UTF-8");
            final String value = URLDecoder.decode(sides.get(1), "UTF-8");
            map.put(key, value);
        }
        return map;
    }
    
    private TestUtils() {
    }
}
