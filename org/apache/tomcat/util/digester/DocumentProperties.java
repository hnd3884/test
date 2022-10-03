package org.apache.tomcat.util.digester;

import java.nio.charset.Charset;

public interface DocumentProperties
{
    public interface Charset
    {
        void setCharset(final java.nio.charset.Charset p0);
    }
    
    @Deprecated
    public interface Encoding
    {
        void setEncoding(final String p0);
    }
}
