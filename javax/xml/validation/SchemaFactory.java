package javax.xml.validation;

import java.net.URL;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import org.xml.sax.SAXException;
import javax.xml.transform.Source;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;

public abstract class SchemaFactory
{
    protected SchemaFactory() {
    }
    
    public static final SchemaFactory newInstance(final String s) {
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (classLoader == null) {
            classLoader = SchemaFactory.class.getClassLoader();
        }
        final SchemaFactory factory = new SchemaFactoryFinder(classLoader).newFactory(s);
        if (factory == null) {
            throw new IllegalArgumentException(s);
        }
        return factory;
    }
    
    public static SchemaFactory newInstance(final String s, final String s2, ClassLoader contextClassLoader) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (s2 == null) {
            throw new IllegalArgumentException("factoryClassName cannot be null.");
        }
        if (contextClassLoader == null) {
            contextClassLoader = SecuritySupport.getContextClassLoader();
        }
        final SchemaFactory instance = new SchemaFactoryFinder(contextClassLoader).createInstance(s2);
        if (instance == null || !instance.isSchemaLanguageSupported(s)) {
            throw new IllegalArgumentException(s);
        }
        return instance;
    }
    
    public abstract boolean isSchemaLanguageSupported(final String p0);
    
    public boolean getFeature(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException("the name parameter is null");
        }
        throw new SAXNotRecognizedException(s);
    }
    
    public void setFeature(final String s, final boolean b) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException("the name parameter is null");
        }
        throw new SAXNotRecognizedException(s);
    }
    
    public void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException("the name parameter is null");
        }
        throw new SAXNotRecognizedException(s);
    }
    
    public Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException("the name parameter is null");
        }
        throw new SAXNotRecognizedException(s);
    }
    
    public abstract void setErrorHandler(final ErrorHandler p0);
    
    public abstract ErrorHandler getErrorHandler();
    
    public abstract void setResourceResolver(final LSResourceResolver p0);
    
    public abstract LSResourceResolver getResourceResolver();
    
    public Schema newSchema(final Source source) throws SAXException {
        return this.newSchema(new Source[] { source });
    }
    
    public Schema newSchema(final File file) throws SAXException {
        return this.newSchema(new StreamSource(file));
    }
    
    public Schema newSchema(final URL url) throws SAXException {
        return this.newSchema(new StreamSource(url.toExternalForm()));
    }
    
    public abstract Schema newSchema(final Source[] p0) throws SAXException;
    
    public abstract Schema newSchema() throws SAXException;
}
