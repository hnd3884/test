package javax.xml.xpath;

public abstract class XPathFactory
{
    public static final String DEFAULT_PROPERTY_NAME = "javax.xml.xpath.XPathFactory";
    public static final String DEFAULT_OBJECT_MODEL_URI = "http://java.sun.com/jaxp/xpath/dom";
    
    protected XPathFactory() {
    }
    
    public static final XPathFactory newInstance() {
        try {
            return newInstance("http://java.sun.com/jaxp/xpath/dom");
        }
        catch (final XPathFactoryConfigurationException ex) {
            throw new RuntimeException("XPathFactory#newInstance() failed to create an XPathFactory for the default object model: http://java.sun.com/jaxp/xpath/dom with the XPathFactoryConfigurationException: " + ex.toString());
        }
    }
    
    public static final XPathFactory newInstance(final String s) throws XPathFactoryConfigurationException {
        if (s == null) {
            throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null");
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
        }
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (classLoader == null) {
            classLoader = XPathFactory.class.getClassLoader();
        }
        final XPathFactory factory = new XPathFactoryFinder(classLoader).newFactory(s);
        if (factory == null) {
            throw new XPathFactoryConfigurationException("No XPathFctory implementation found for the object model: " + s);
        }
        return factory;
    }
    
    public static XPathFactory newInstance(final String s, final String s2, ClassLoader contextClassLoader) throws XPathFactoryConfigurationException {
        if (s == null) {
            throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null");
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
        }
        if (s2 == null) {
            throw new XPathFactoryConfigurationException("factoryClassName cannot be null.");
        }
        if (contextClassLoader == null) {
            contextClassLoader = SecuritySupport.getContextClassLoader();
        }
        final XPathFactory instance = new XPathFactoryFinder(contextClassLoader).createInstance(s2);
        if (instance == null || !instance.isObjectModelSupported(s)) {
            throw new XPathFactoryConfigurationException("No XPathFctory implementation found for the object model: " + s);
        }
        return instance;
    }
    
    public abstract boolean isObjectModelSupported(final String p0);
    
    public abstract void setFeature(final String p0, final boolean p1) throws XPathFactoryConfigurationException;
    
    public abstract boolean getFeature(final String p0) throws XPathFactoryConfigurationException;
    
    public abstract void setXPathVariableResolver(final XPathVariableResolver p0);
    
    public abstract void setXPathFunctionResolver(final XPathFunctionResolver p0);
    
    public abstract XPath newXPath();
}
