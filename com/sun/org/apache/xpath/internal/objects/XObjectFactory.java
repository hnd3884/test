package com.sun.org.apache.xpath.internal.objects;

import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.axes.OneStepIterator;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;

public class XObjectFactory
{
    public static XObject create(final Object val) {
        XObject result;
        if (val instanceof XObject) {
            result = (XObject)val;
        }
        else if (val instanceof String) {
            result = new XString((String)val);
        }
        else if (val instanceof Boolean) {
            result = new XBoolean((Boolean)val);
        }
        else if (val instanceof Double) {
            result = new XNumber((Number)val);
        }
        else {
            result = new XObject(val);
        }
        return result;
    }
    
    public static XObject create(final Object val, final XPathContext xctxt) {
        XObject result;
        if (val instanceof XObject) {
            result = (XObject)val;
        }
        else if (val instanceof String) {
            result = new XString((String)val);
        }
        else if (val instanceof Boolean) {
            result = new XBoolean((Boolean)val);
        }
        else if (val instanceof Number) {
            result = new XNumber((Number)val);
        }
        else if (val instanceof DTM) {
            final DTM dtm = (DTM)val;
            try {
                final int dtmRoot = dtm.getDocument();
                final DTMAxisIterator iter = dtm.getAxisIterator(13);
                iter.setStartNode(dtmRoot);
                final DTMIterator iterator = new OneStepIterator(iter, 13);
                iterator.setRoot(dtmRoot, xctxt);
                result = new XNodeSet(iterator);
            }
            catch (final Exception ex) {
                throw new WrappedRuntimeException(ex);
            }
        }
        else if (val instanceof DTMAxisIterator) {
            final DTMAxisIterator iter2 = (DTMAxisIterator)val;
            try {
                final DTMIterator iterator2 = new OneStepIterator(iter2, 13);
                iterator2.setRoot(iter2.getStartNode(), xctxt);
                result = new XNodeSet(iterator2);
            }
            catch (final Exception ex) {
                throw new WrappedRuntimeException(ex);
            }
        }
        else if (val instanceof DTMIterator) {
            result = new XNodeSet((DTMIterator)val);
        }
        else if (val instanceof Node) {
            result = new XNodeSetForDOM((Node)val, xctxt);
        }
        else if (val instanceof NodeList) {
            result = new XNodeSetForDOM((NodeList)val, xctxt);
        }
        else if (val instanceof NodeIterator) {
            result = new XNodeSetForDOM((NodeIterator)val, xctxt);
        }
        else {
            result = new XObject(val);
        }
        return result;
    }
}
