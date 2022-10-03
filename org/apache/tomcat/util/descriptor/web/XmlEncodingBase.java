package org.apache.tomcat.util.descriptor.web;

import org.apache.juli.logging.LogFactory;
import java.io.UnsupportedEncodingException;
import org.apache.tomcat.util.buf.B2CConverter;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

public abstract class XmlEncodingBase
{
    private static final StringManager sm;
    private static Log log;
    private Charset charset;
    
    public XmlEncodingBase() {
        this.charset = StandardCharsets.UTF_8;
    }
    
    @Deprecated
    public void setEncoding(final String encoding) {
        try {
            this.charset = B2CConverter.getCharset(encoding);
        }
        catch (final UnsupportedEncodingException e) {
            XmlEncodingBase.log.warn((Object)XmlEncodingBase.sm.getString("xmlEncodingBase.encodingInvalid", new Object[] { encoding, this.charset.name() }), (Throwable)e);
        }
    }
    
    @Deprecated
    public String getEncoding() {
        return this.charset.name();
    }
    
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    static {
        sm = StringManager.getManager((Class)XmlEncodingBase.class);
        XmlEncodingBase.log = LogFactory.getLog((Class)XmlEncodingBase.class);
    }
}
