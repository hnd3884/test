package com.lowagie.text.xml.xmp;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;

public abstract class XmpSchema extends Properties
{
    private static final long serialVersionUID = -176374295948945272L;
    protected String xmlns;
    
    public XmpSchema(final String xmlns) {
        this.xmlns = xmlns;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        final Enumeration e = this.propertyNames();
        while (e.hasMoreElements()) {
            this.process(buf, e.nextElement());
        }
        return buf.toString();
    }
    
    protected void process(final StringBuffer buf, final Object p) {
        buf.append('<');
        buf.append(p);
        buf.append('>');
        buf.append(((Hashtable<K, Object>)this).get(p));
        buf.append("</");
        buf.append(p);
        buf.append('>');
    }
    
    public String getXmlns() {
        return this.xmlns;
    }
    
    public Object addProperty(final String key, final String value) {
        return this.setProperty(key, value);
    }
    
    @Override
    public Object setProperty(final String key, final String value) {
        return super.setProperty(key, escape(value));
    }
    
    public Object setProperty(final String key, final XmpArray value) {
        return super.setProperty(key, value.toString());
    }
    
    public Object setProperty(final String key, final LangAlt value) {
        return super.setProperty(key, value.toString());
    }
    
    public static String escape(final String content) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < content.length(); ++i) {
            switch (content.charAt(i)) {
                case '<': {
                    buf.append("&lt;");
                    break;
                }
                case '>': {
                    buf.append("&gt;");
                    break;
                }
                case '\'': {
                    buf.append("&apos;");
                    break;
                }
                case '\"': {
                    buf.append("&quot;");
                    break;
                }
                case '&': {
                    buf.append("&amp;");
                    break;
                }
                default: {
                    buf.append(content.charAt(i));
                    break;
                }
            }
        }
        return buf.toString();
    }
}
