package org.apache.xmlbeans;

import java.lang.ref.SoftReference;
import org.w3c.dom.DOMImplementation;
import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import java.io.Reader;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

public interface XmlFactoryHook
{
    XmlObject newInstance(final SchemaTypeLoader p0, final SchemaType p1, final XmlOptions p2);
    
    XmlObject parse(final SchemaTypeLoader p0, final String p1, final SchemaType p2, final XmlOptions p3) throws XmlException;
    
    XmlObject parse(final SchemaTypeLoader p0, final InputStream p1, final SchemaType p2, final XmlOptions p3) throws XmlException, IOException;
    
    XmlObject parse(final SchemaTypeLoader p0, final XMLStreamReader p1, final SchemaType p2, final XmlOptions p3) throws XmlException;
    
    XmlObject parse(final SchemaTypeLoader p0, final Reader p1, final SchemaType p2, final XmlOptions p3) throws XmlException, IOException;
    
    XmlObject parse(final SchemaTypeLoader p0, final Node p1, final SchemaType p2, final XmlOptions p3) throws XmlException;
    
    @Deprecated
    XmlObject parse(final SchemaTypeLoader p0, final XMLInputStream p1, final SchemaType p2, final XmlOptions p3) throws XmlException, XMLStreamException;
    
    XmlSaxHandler newXmlSaxHandler(final SchemaTypeLoader p0, final SchemaType p1, final XmlOptions p2);
    
    DOMImplementation newDomImplementation(final SchemaTypeLoader p0, final XmlOptions p1);
    
    public static final class ThreadContext
    {
        private static ThreadLocal threadHook;
        
        public static void clearThreadLocals() {
            ThreadContext.threadHook.remove();
        }
        
        public static XmlFactoryHook getHook() {
            final SoftReference softRef = ThreadContext.threadHook.get();
            return (softRef == null) ? null : softRef.get();
        }
        
        public static void setHook(final XmlFactoryHook hook) {
            ThreadContext.threadHook.set(new SoftReference(hook));
        }
        
        private ThreadContext() {
        }
        
        static {
            ThreadContext.threadHook = new ThreadLocal();
        }
    }
}
