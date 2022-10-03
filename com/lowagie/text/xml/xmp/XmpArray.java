package com.lowagie.text.xml.xmp;

import java.util.Iterator;
import java.util.ArrayList;

public class XmpArray extends ArrayList
{
    private static final long serialVersionUID = 5722854116328732742L;
    public static final String UNORDERED = "rdf:Bag";
    public static final String ORDERED = "rdf:Seq";
    public static final String ALTERNATIVE = "rdf:Alt";
    protected String type;
    
    public XmpArray(final String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer("<");
        buf.append(this.type);
        buf.append('>');
        for (final String s : this) {
            buf.append("<rdf:li>");
            buf.append(XmpSchema.escape(s));
            buf.append("</rdf:li>");
        }
        buf.append("</");
        buf.append(this.type);
        buf.append('>');
        return buf.toString();
    }
}
