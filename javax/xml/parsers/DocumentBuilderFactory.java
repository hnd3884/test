package javax.xml.parsers;

import javax.xml.validation.Schema;

public abstract class DocumentBuilderFactory
{
    private boolean validating;
    private boolean namespaceAware;
    private boolean whitespace;
    private boolean expandEntityRef;
    private boolean ignoreComments;
    private boolean coalescing;
    
    protected DocumentBuilderFactory() {
        this.validating = false;
        this.namespaceAware = false;
        this.whitespace = false;
        this.expandEntityRef = true;
        this.ignoreComments = false;
        this.coalescing = false;
    }
    
    public static DocumentBuilderFactory newInstance() {
        try {
            return (DocumentBuilderFactory)FactoryFinder.find("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }
    
    public static DocumentBuilderFactory newInstance(final String s, ClassLoader contextClassLoader) {
        if (s == null) {
            throw new FactoryConfigurationError("factoryClassName cannot be null.");
        }
        if (contextClassLoader == null) {
            contextClassLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (DocumentBuilderFactory)FactoryFinder.newInstance(s, contextClassLoader, false);
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }
    
    public abstract DocumentBuilder newDocumentBuilder() throws ParserConfigurationException;
    
    public void setNamespaceAware(final boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }
    
    public void setValidating(final boolean validating) {
        this.validating = validating;
    }
    
    public void setIgnoringElementContentWhitespace(final boolean whitespace) {
        this.whitespace = whitespace;
    }
    
    public void setExpandEntityReferences(final boolean expandEntityRef) {
        this.expandEntityRef = expandEntityRef;
    }
    
    public void setIgnoringComments(final boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
    }
    
    public void setCoalescing(final boolean coalescing) {
        this.coalescing = coalescing;
    }
    
    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }
    
    public boolean isValidating() {
        return this.validating;
    }
    
    public boolean isIgnoringElementContentWhitespace() {
        return this.whitespace;
    }
    
    public boolean isExpandEntityReferences() {
        return this.expandEntityRef;
    }
    
    public boolean isIgnoringComments() {
        return this.ignoreComments;
    }
    
    public boolean isCoalescing() {
        return this.coalescing;
    }
    
    public abstract void setAttribute(final String p0, final Object p1) throws IllegalArgumentException;
    
    public abstract Object getAttribute(final String p0) throws IllegalArgumentException;
    
    public abstract void setFeature(final String p0, final boolean p1) throws ParserConfigurationException;
    
    public abstract boolean getFeature(final String p0) throws ParserConfigurationException;
    
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
