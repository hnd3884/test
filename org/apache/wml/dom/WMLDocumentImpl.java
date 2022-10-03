package org.apache.wml.dom;

import org.w3c.dom.DocumentType;
import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.DOMException;
import java.lang.reflect.InvocationTargetException;
import org.w3c.dom.Element;
import java.util.Hashtable;
import org.apache.wml.WMLDocument;
import org.apache.xerces.dom.DocumentImpl;

public class WMLDocumentImpl extends DocumentImpl implements WMLDocument
{
    private static final long serialVersionUID = -6582904849512384104L;
    private static Hashtable _elementTypesWML;
    private static final Class[] _elemClassSigWML;
    
    public Element createElement(final String s) throws DOMException {
        final Class clazz = WMLDocumentImpl._elementTypesWML.get(s);
        if (clazz != null) {
            try {
                return (Element)clazz.getConstructor((Class[])WMLDocumentImpl._elemClassSigWML).newInstance(this, s);
            }
            catch (final Exception ex) {
                Throwable targetException;
                if (ex instanceof InvocationTargetException) {
                    targetException = ((InvocationTargetException)ex).getTargetException();
                }
                else {
                    targetException = ex;
                }
                System.out.println("Exception " + targetException.getClass().getName());
                System.out.println(targetException.getMessage());
                throw new IllegalStateException("Tag '" + s + "' associated with an Element class that failed to construct.");
            }
        }
        return new WMLElementImpl(this, s);
    }
    
    protected boolean canRenameElements(final String s, final String s2, final ElementImpl elementImpl) {
        return WMLDocumentImpl._elementTypesWML.get(s2) == WMLDocumentImpl._elementTypesWML.get(elementImpl.getTagName());
    }
    
    public WMLDocumentImpl(final DocumentType documentType) {
        super(documentType, false);
    }
    
    static {
        _elemClassSigWML = new Class[] { WMLDocumentImpl.class, String.class };
        (WMLDocumentImpl._elementTypesWML = new Hashtable()).put("b", WMLBElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("noop", WMLNoopElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("a", WMLAElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("setvar", WMLSetvarElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("access", WMLAccessElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("strong", WMLStrongElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("postfield", WMLPostfieldElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("do", WMLDoElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("wml", WMLWmlElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("tr", WMLTrElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("go", WMLGoElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("big", WMLBigElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("anchor", WMLAnchorElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("timer", WMLTimerElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("small", WMLSmallElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("optgroup", WMLOptgroupElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("head", WMLHeadElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("td", WMLTdElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("fieldset", WMLFieldsetElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("img", WMLImgElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("refresh", WMLRefreshElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("onevent", WMLOneventElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("input", WMLInputElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("prev", WMLPrevElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("table", WMLTableElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("meta", WMLMetaElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("template", WMLTemplateElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("br", WMLBrElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("option", WMLOptionElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("u", WMLUElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("p", WMLPElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("select", WMLSelectElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("em", WMLEmElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("i", WMLIElementImpl.class);
        WMLDocumentImpl._elementTypesWML.put("card", WMLCardElementImpl.class);
    }
}
