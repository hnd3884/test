package javax.xml.parsers;

import javax.xml.validation.Schema;
import org.w3c.dom.DOMImplementation;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import java.io.File;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import java.io.InputStream;

public abstract class DocumentBuilder
{
    private static final boolean DEBUG = false;
    
    protected DocumentBuilder() {
    }
    
    public void reset() {
        throw new UnsupportedOperationException("This DocumentBuilder, \"" + this.getClass().getName() + "\", does not support the reset functionality." + "  Specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\"" + " version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
    
    public Document parse(final InputStream inputStream) throws SAXException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        return this.parse(new InputSource(inputStream));
    }
    
    public Document parse(final InputStream inputStream, final String systemId) throws SAXException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        final InputSource inputSource = new InputSource(inputStream);
        inputSource.setSystemId(systemId);
        return this.parse(inputSource);
    }
    
    public Document parse(final String s) throws SAXException, IOException {
        if (s == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        return this.parse(new InputSource(s));
    }
    
    public Document parse(final File file) throws SAXException, IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        return this.parse(new InputSource(FilePathToURI.filepath2URI(file.getAbsolutePath())));
    }
    
    public abstract Document parse(final InputSource p0) throws SAXException, IOException;
    
    public abstract boolean isNamespaceAware();
    
    public abstract boolean isValidating();
    
    public abstract void setEntityResolver(final EntityResolver p0);
    
    public abstract void setErrorHandler(final ErrorHandler p0);
    
    public abstract Document newDocument();
    
    public abstract DOMImplementation getDOMImplementation();
    
    public Schema getSchema() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
    
    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
}
