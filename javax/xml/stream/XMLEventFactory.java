package javax.xml.stream;

import javax.xml.stream.events.DTD;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.StartElement;
import java.util.Iterator;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.QName;

public abstract class XMLEventFactory
{
    private static final String PROPERTY_NAME = "javax.xml.stream.XMLEventFactory";
    private static final String DEFAULT_FACTORY = "org.apache.xerces.stax.XMLEventFactoryImpl";
    
    protected XMLEventFactory() {
    }
    
    public static XMLEventFactory newInstance() throws FactoryConfigurationError {
        try {
            return (XMLEventFactory)FactoryFinder.find("javax.xml.stream.XMLEventFactory", "org.apache.xerces.stax.XMLEventFactoryImpl");
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }
    
    public static XMLEventFactory newInstance(final String s, ClassLoader contextClassLoader) throws FactoryConfigurationError {
        if (contextClassLoader == null) {
            contextClassLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (XMLEventFactory)FactoryFinder.find(s, contextClassLoader, "org.apache.xerces.stax.XMLEventFactoryImpl");
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }
    
    public abstract void setLocation(final Location p0);
    
    public abstract Attribute createAttribute(final QName p0, final String p1);
    
    public abstract Attribute createAttribute(final String p0, final String p1);
    
    public abstract Attribute createAttribute(final String p0, final String p1, final String p2, final String p3);
    
    public abstract Namespace createNamespace(final String p0);
    
    public abstract Namespace createNamespace(final String p0, final String p1);
    
    public abstract StartElement createStartElement(final QName p0, final Iterator p1, final Iterator p2);
    
    public abstract StartElement createStartElement(final String p0, final String p1, final String p2);
    
    public abstract StartElement createStartElement(final String p0, final String p1, final String p2, final Iterator p3, final Iterator p4);
    
    public abstract StartElement createStartElement(final String p0, final String p1, final String p2, final Iterator p3, final Iterator p4, final NamespaceContext p5);
    
    public abstract EndElement createEndElement(final QName p0, final Iterator p1);
    
    public abstract EndElement createEndElement(final String p0, final String p1, final String p2);
    
    public abstract EndElement createEndElement(final String p0, final String p1, final String p2, final Iterator p3);
    
    public abstract Characters createCharacters(final String p0);
    
    public abstract Characters createCData(final String p0);
    
    public abstract Characters createSpace(final String p0);
    
    public abstract Characters createIgnorableSpace(final String p0);
    
    public abstract StartDocument createStartDocument();
    
    public abstract StartDocument createStartDocument(final String p0);
    
    public abstract StartDocument createStartDocument(final String p0, final String p1);
    
    public abstract StartDocument createStartDocument(final String p0, final String p1, final boolean p2);
    
    public abstract EndDocument createEndDocument();
    
    public abstract EntityReference createEntityReference(final String p0, final EntityDeclaration p1);
    
    public abstract Comment createComment(final String p0);
    
    public abstract ProcessingInstruction createProcessingInstruction(final String p0, final String p1);
    
    public abstract DTD createDTD(final String p0);
}
