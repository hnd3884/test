package javax.xml.stream;

import javax.xml.stream.util.XMLEventAllocator;
import java.io.InputStream;
import javax.xml.transform.Source;
import java.io.Reader;

public abstract class XMLInputFactory
{
    public static final String ALLOCATOR = "javax.xml.stream.allocator";
    public static final String IS_COALESCING = "javax.xml.stream.isCoalescing";
    public static final String IS_NAMESPACE_AWARE = "javax.xml.stream.isNamespaceAware";
    public static final String IS_REPLACING_ENTITY_REFERENCES = "javax.xml.stream.isReplacingEntityReferences";
    public static final String IS_SUPPORTING_EXTERNAL_ENTITIES = "javax.xml.stream.isSupportingExternalEntities";
    public static final String IS_VALIDATING = "javax.xml.stream.isValidating";
    public static final String REPORTER = "javax.xml.stream.reporter";
    public static final String RESOLVER = "javax.xml.stream.resolver";
    public static final String SUPPORT_DTD = "javax.xml.stream.supportDTD";
    private static final String PROPERTY_NAME = "javax.xml.stream.XMLInputFactory";
    private static final String DEFAULT_FACTORY = "com.ctc.wstx.stax.WstxInputFactory";
    
    protected XMLInputFactory() {
    }
    
    public static XMLInputFactory newInstance() throws FactoryConfigurationError {
        try {
            return (XMLInputFactory)FactoryFinder.find("javax.xml.stream.XMLInputFactory", "com.ctc.wstx.stax.WstxInputFactory");
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }
    
    public static XMLInputFactory newInstance(final String s, ClassLoader contextClassLoader) throws FactoryConfigurationError {
        if (contextClassLoader == null) {
            contextClassLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (XMLInputFactory)FactoryFinder.find(s, contextClassLoader, "com.ctc.wstx.stax.WstxInputFactory");
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }
    
    public abstract XMLStreamReader createXMLStreamReader(final Reader p0) throws XMLStreamException;
    
    public abstract XMLStreamReader createXMLStreamReader(final Source p0) throws XMLStreamException;
    
    public abstract XMLStreamReader createXMLStreamReader(final InputStream p0) throws XMLStreamException;
    
    public abstract XMLStreamReader createXMLStreamReader(final InputStream p0, final String p1) throws XMLStreamException;
    
    public abstract XMLStreamReader createXMLStreamReader(final String p0, final InputStream p1) throws XMLStreamException;
    
    public abstract XMLStreamReader createXMLStreamReader(final String p0, final Reader p1) throws XMLStreamException;
    
    public abstract XMLEventReader createXMLEventReader(final Reader p0) throws XMLStreamException;
    
    public abstract XMLEventReader createXMLEventReader(final String p0, final Reader p1) throws XMLStreamException;
    
    public abstract XMLEventReader createXMLEventReader(final XMLStreamReader p0) throws XMLStreamException;
    
    public abstract XMLEventReader createXMLEventReader(final Source p0) throws XMLStreamException;
    
    public abstract XMLEventReader createXMLEventReader(final InputStream p0) throws XMLStreamException;
    
    public abstract XMLEventReader createXMLEventReader(final InputStream p0, final String p1) throws XMLStreamException;
    
    public abstract XMLEventReader createXMLEventReader(final String p0, final InputStream p1) throws XMLStreamException;
    
    public abstract XMLStreamReader createFilteredReader(final XMLStreamReader p0, final StreamFilter p1) throws XMLStreamException;
    
    public abstract XMLEventReader createFilteredReader(final XMLEventReader p0, final EventFilter p1) throws XMLStreamException;
    
    public abstract XMLResolver getXMLResolver();
    
    public abstract void setXMLResolver(final XMLResolver p0);
    
    public abstract XMLReporter getXMLReporter();
    
    public abstract void setXMLReporter(final XMLReporter p0);
    
    public abstract void setProperty(final String p0, final Object p1) throws IllegalArgumentException;
    
    public abstract Object getProperty(final String p0) throws IllegalArgumentException;
    
    public abstract boolean isPropertySupported(final String p0);
    
    public abstract void setEventAllocator(final XMLEventAllocator p0);
    
    public abstract XMLEventAllocator getEventAllocator();
}
