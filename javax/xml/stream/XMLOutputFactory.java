package javax.xml.stream;

import javax.xml.transform.Result;
import java.io.OutputStream;
import java.io.Writer;

public abstract class XMLOutputFactory
{
    public static final String IS_REPAIRING_NAMESPACES = "javax.xml.stream.isRepairingNamespaces";
    private static final String PROPERTY_NAME = "javax.xml.stream.XMLOutputFactory";
    private static final String DEFAULT_FACTORY = "com.ctc.wstx.stax.WstxOutputFactory";
    
    protected XMLOutputFactory() {
    }
    
    public static XMLOutputFactory newInstance() throws FactoryConfigurationError {
        try {
            return (XMLOutputFactory)FactoryFinder.find("javax.xml.stream.XMLOutputFactory", "com.ctc.wstx.stax.WstxOutputFactory");
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
    
    public abstract XMLStreamWriter createXMLStreamWriter(final Writer p0) throws XMLStreamException;
    
    public abstract XMLStreamWriter createXMLStreamWriter(final OutputStream p0) throws XMLStreamException;
    
    public abstract XMLStreamWriter createXMLStreamWriter(final OutputStream p0, final String p1) throws XMLStreamException;
    
    public abstract XMLStreamWriter createXMLStreamWriter(final Result p0) throws XMLStreamException;
    
    public abstract XMLEventWriter createXMLEventWriter(final Result p0) throws XMLStreamException;
    
    public abstract XMLEventWriter createXMLEventWriter(final OutputStream p0) throws XMLStreamException;
    
    public abstract XMLEventWriter createXMLEventWriter(final OutputStream p0, final String p1) throws XMLStreamException;
    
    public abstract XMLEventWriter createXMLEventWriter(final Writer p0) throws XMLStreamException;
    
    public abstract void setProperty(final String p0, final Object p1) throws IllegalArgumentException;
    
    public abstract Object getProperty(final String p0) throws IllegalArgumentException;
    
    public abstract boolean isPropertySupported(final String p0);
}
