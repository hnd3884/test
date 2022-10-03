package com.google.api.client.util;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

@Deprecated
public final class Charsets
{
    public static final Charset UTF_8;
    public static final Charset ISO_8859_1;
    
    private Charsets() {
    }
    
    static {
        UTF_8 = StandardCharsets.UTF_8;
        ISO_8859_1 = StandardCharsets.ISO_8859_1;
    }
}
