package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xpath.internal.NodeSet;
import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.axes.RTFIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;

public class ExsltCommon
{
    public static String objectType(final Object obj) {
        if (obj instanceof String) {
            return "string";
        }
        if (obj instanceof Boolean) {
            return "boolean";
        }
        if (obj instanceof Number) {
            return "number";
        }
        if (!(obj instanceof DTMNodeIterator)) {
            return "unknown";
        }
        final DTMIterator dtmI = ((DTMNodeIterator)obj).getDTMIterator();
        if (dtmI instanceof RTFIterator) {
            return "RTF";
        }
        return "node-set";
    }
    
    public static NodeSet nodeSet(final ExpressionContext myProcessor, final Object rtf) {
        return Extensions.nodeset(myProcessor, rtf);
    }
}
