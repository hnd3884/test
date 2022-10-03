package com.lowagie.text.xml.xmp;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;

public class LangAlt extends Properties
{
    private static final long serialVersionUID = 4396971487200843099L;
    public static final String DEFAULT = "x-default";
    
    public LangAlt(final String defaultValue) {
        this.addLanguage("x-default", defaultValue);
    }
    
    public LangAlt() {
    }
    
    public void addLanguage(final String language, final String value) {
        this.setProperty(language, XmpSchema.escape(value));
    }
    
    protected void process(final StringBuffer buf, final Object lang) {
        buf.append("<rdf:li xml:lang=\"");
        buf.append(lang);
        buf.append("\" >");
        buf.append(((Hashtable<K, Object>)this).get(lang));
        buf.append("</rdf:li>");
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<rdf:Alt>");
        final Enumeration e = this.propertyNames();
        while (e.hasMoreElements()) {
            this.process(sb, e.nextElement());
        }
        sb.append("</rdf:Alt>");
        return sb.toString();
    }
}
