package javax.xml.parsers;

import javax.xml.validation.Schema;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXException;

public abstract class SAXParserFactory
{
    private boolean validating;
    private boolean namespaceAware;
    
    protected SAXParserFactory() {
        this.validating = false;
        this.namespaceAware = false;
    }
    
    public static SAXParserFactory newInstance() {
        try {
            return (SAXParserFactory)FactoryFinder.find("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }
    
    public static SAXParserFactory newInstance(final String s, ClassLoader contextClassLoader) {
        if (s == null) {
            throw new FactoryConfigurationError("factoryClassName cannot be null.");
        }
        if (contextClassLoader == null) {
            contextClassLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (SAXParserFactory)FactoryFinder.newInstance(s, contextClassLoader, false);
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }
    
    public abstract SAXParser newSAXParser() throws ParserConfigurationException, SAXException;
    
    public void setNamespaceAware(final boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }
    
    public void setValidating(final boolean validating) {
        this.validating = validating;
    }
    
    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }
    
    public boolean isValidating() {
        return this.validating;
    }
    
    public abstract void setFeature(final String p0, final boolean p1) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;
    
    public abstract boolean getFeature(final String p0) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;
    
    public Schema getSchema() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
    
    public void setSchema(final Schema schema) {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
    
    public void setXIncludeAware(final boolean b) {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
    
    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
}
