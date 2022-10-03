package org.cyberneko.html.xercesbridge;

import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.NamespaceContext;

public abstract class XercesBridge
{
    private static final XercesBridge instance;
    
    public static XercesBridge getInstance() {
        return XercesBridge.instance;
    }
    
    private static XercesBridge makeInstance() {
        final String[] classNames = { "org.cyberneko.html.xercesbridge.XercesBridge_2_3", "org.cyberneko.html.xercesbridge.XercesBridge_2_2", "org.cyberneko.html.xercesbridge.XercesBridge_2_1", "org.cyberneko.html.xercesbridge.XercesBridge_2_0" };
        for (int i = 0; i != classNames.length; ++i) {
            final String className = classNames[i];
            final XercesBridge bridge = newInstanceOrNull(className);
            if (bridge != null) {
                return bridge;
            }
        }
        throw new IllegalStateException("Failed to create XercesBridge instance");
    }
    
    private static XercesBridge newInstanceOrNull(final String className) {
        try {
            return (XercesBridge)Class.forName(className).newInstance();
        }
        catch (final ClassNotFoundException ex) {}
        catch (final SecurityException ex2) {}
        catch (final LinkageError ex3) {}
        catch (final IllegalArgumentException e) {}
        catch (final IllegalAccessException e2) {}
        catch (final InstantiationException ex4) {}
        return null;
    }
    
    public void NamespaceContext_declarePrefix(final NamespaceContext namespaceContext, final String ns, final String avalue) {
    }
    
    public abstract String getVersion();
    
    public abstract void XMLDocumentHandler_startDocument(final XMLDocumentHandler p0, final XMLLocator p1, final String p2, final NamespaceContext p3, final Augmentations p4);
    
    public void XMLDocumentHandler_startPrefixMapping(final XMLDocumentHandler documentHandler, final String prefix, final String uri, final Augmentations augs) {
    }
    
    public void XMLDocumentHandler_endPrefixMapping(final XMLDocumentHandler documentHandler, final String prefix, final Augmentations augs) {
    }
    
    public void XMLDocumentFilter_setDocumentSource(final XMLDocumentFilter filter, final XMLDocumentSource lastSource) {
    }
    
    static {
        instance = makeInstance();
    }
}
