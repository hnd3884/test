package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.FileNotFoundException;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.dtm.ref.EmptyIterator;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public final class LoadDocument
{
    private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
    
    public static DTMAxisIterator documentF(final Object arg1, final DTMAxisIterator arg2, final String xslURI, final AbstractTranslet translet, final DOM dom) throws TransletException {
        String baseURI = null;
        final int arg2FirstNode = arg2.next();
        if (arg2FirstNode == -1) {
            return EmptyIterator.getInstance();
        }
        baseURI = dom.getDocumentURI(arg2FirstNode);
        if (!SystemIDResolver.isAbsoluteURI(baseURI)) {
            baseURI = SystemIDResolver.getAbsoluteURIFromRelative(baseURI);
        }
        try {
            if (arg1 instanceof String) {
                if (((String)arg1).length() == 0) {
                    return document(xslURI, "", translet, dom);
                }
                return document((String)arg1, baseURI, translet, dom);
            }
            else {
                if (arg1 instanceof DTMAxisIterator) {
                    return document((DTMAxisIterator)arg1, baseURI, translet, dom);
                }
                final String err = "document(" + arg1.toString() + ")";
                throw new IllegalArgumentException(err);
            }
        }
        catch (final Exception e) {
            throw new TransletException(e);
        }
    }
    
    public static DTMAxisIterator documentF(final Object arg, String xslURI, final AbstractTranslet translet, final DOM dom) throws TransletException {
        try {
            if (arg instanceof String) {
                if (xslURI == null) {
                    xslURI = "";
                }
                String baseURI = xslURI;
                if (!SystemIDResolver.isAbsoluteURI(xslURI)) {
                    baseURI = SystemIDResolver.getAbsoluteURIFromRelative(xslURI);
                }
                String href = (String)arg;
                if (href.length() != 0) {
                    return document(href, baseURI, translet, dom);
                }
                href = "";
                final TemplatesImpl templates = (TemplatesImpl)translet.getTemplates();
                DOM sdom = null;
                if (templates != null) {
                    sdom = templates.getStylesheetDOM();
                }
                if (sdom != null) {
                    return document(sdom, translet, dom);
                }
                return document(href, baseURI, translet, dom, true);
            }
            else {
                if (arg instanceof DTMAxisIterator) {
                    return document((DTMAxisIterator)arg, null, translet, dom);
                }
                final String err = "document(" + arg.toString() + ")";
                throw new IllegalArgumentException(err);
            }
        }
        catch (final Exception e) {
            throw new TransletException(e);
        }
    }
    
    private static DTMAxisIterator document(final String uri, final String base, final AbstractTranslet translet, final DOM dom) throws Exception {
        return document(uri, base, translet, dom, false);
    }
    
    private static DTMAxisIterator document(String uri, final String base, final AbstractTranslet translet, final DOM dom, final boolean cacheDOM) throws Exception {
        try {
            final String originalUri = uri;
            final MultiDOM multiplexer = (MultiDOM)dom;
            if (base != null && !base.equals("")) {
                uri = SystemIDResolver.getAbsoluteURI(uri, base);
            }
            if (uri == null || uri.equals("")) {
                return EmptyIterator.getInstance();
            }
            int mask = multiplexer.getDocumentMask(uri);
            if (mask != -1) {
                final DOM newDom = ((DOMAdapter)multiplexer.getDOMAdapter(uri)).getDOMImpl();
                if (newDom instanceof DOMEnhancedForDTM) {
                    return new SingletonIterator(newDom.getDocument(), true);
                }
            }
            final DOMCache cache = translet.getDOMCache();
            mask = multiplexer.nextMask();
            DOM newdom;
            if (cache != null) {
                newdom = cache.retrieveDocument(base, originalUri, translet);
                if (newdom == null) {
                    final Exception e = new FileNotFoundException(originalUri);
                    throw new TransletException(e);
                }
            }
            else {
                final String accessError = SecuritySupport.checkAccess(uri, translet.getAllowedProtocols(), "all");
                if (accessError != null) {
                    final ErrorMsg msg = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(uri), accessError);
                    throw new Exception(msg.toString());
                }
                final XSLTCDTMManager dtmManager = (XSLTCDTMManager)multiplexer.getDTMManager();
                final DOMEnhancedForDTM enhancedDOM = (DOMEnhancedForDTM)(newdom = (DOMEnhancedForDTM)dtmManager.getDTM(new StreamSource(uri), false, null, true, false, translet.hasIdCall(), cacheDOM));
                if (cacheDOM) {
                    final TemplatesImpl templates = (TemplatesImpl)translet.getTemplates();
                    if (templates != null) {
                        templates.setStylesheetDOM(enhancedDOM);
                    }
                }
                translet.prepassDocument(enhancedDOM);
                enhancedDOM.setDocumentURI(uri);
            }
            final DOMAdapter domAdapter = translet.makeDOMAdapter(newdom);
            multiplexer.addDOMAdapter(domAdapter);
            translet.buildKeys(domAdapter, null, null, newdom.getDocument());
            return new SingletonIterator(newdom.getDocument(), true);
        }
        catch (final Exception e2) {
            throw e2;
        }
    }
    
    private static DTMAxisIterator document(final DTMAxisIterator arg1, String baseURI, final AbstractTranslet translet, final DOM dom) throws Exception {
        final UnionIterator union = new UnionIterator(dom);
        int node = -1;
        while ((node = arg1.next()) != -1) {
            final String uri = dom.getStringValueX(node);
            if (baseURI == null) {
                baseURI = dom.getDocumentURI(node);
                if (!SystemIDResolver.isAbsoluteURI(baseURI)) {
                    baseURI = SystemIDResolver.getAbsoluteURIFromRelative(baseURI);
                }
            }
            union.addIterator(document(uri, baseURI, translet, dom));
        }
        return union;
    }
    
    private static DTMAxisIterator document(final DOM newdom, final AbstractTranslet translet, final DOM dom) throws Exception {
        final DTMManager dtmManager = ((MultiDOM)dom).getDTMManager();
        if (dtmManager != null && newdom instanceof DTM) {
            ((DTM)newdom).migrateTo(dtmManager);
        }
        translet.prepassDocument(newdom);
        final DOMAdapter domAdapter = translet.makeDOMAdapter(newdom);
        ((MultiDOM)dom).addDOMAdapter(domAdapter);
        translet.buildKeys(domAdapter, null, null, newdom.getDocument());
        return new SingletonIterator(newdom.getDocument(), true);
    }
}
