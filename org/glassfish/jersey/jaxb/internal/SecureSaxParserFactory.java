package org.glassfish.jersey.jaxb.internal;

import org.xml.sax.XMLReader;
import org.xml.sax.Parser;
import java.io.File;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import org.xml.sax.HandlerBase;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.validation.Schema;
import java.util.logging.Level;
import org.glassfish.jersey.internal.util.SaxHelper;
import org.xml.sax.EntityResolver;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParserFactory;

public class SecureSaxParserFactory extends SAXParserFactory
{
    private static final Logger LOGGER;
    private static final EntityResolver EMPTY_ENTITY_RESOLVER;
    private static final String EXTERNAL_GENERAL_ENTITIES_FEATURE = "http://xml.org/sax/features/external-general-entities";
    private static final String EXTERNAL_PARAMETER_ENTITIES_FEATURE = "http://xml.org/sax/features/external-parameter-entities";
    private final SAXParserFactory spf;
    
    public SecureSaxParserFactory(final SAXParserFactory spf) {
        this.spf = spf;
        if (SaxHelper.isXdkParserFactory(spf)) {
            SecureSaxParserFactory.LOGGER.log(Level.WARNING, LocalizationMessages.SAX_XDK_NO_SECURITY_FEATURES());
        }
        else {
            try {
                spf.setFeature("http://xml.org/sax/features/external-general-entities", Boolean.FALSE);
            }
            catch (final Exception ex) {
                SecureSaxParserFactory.LOGGER.log(Level.CONFIG, LocalizationMessages.SAX_CANNOT_DISABLE_GENERAL_ENTITY_PROCESSING_FEATURE(spf.getClass()), ex);
            }
            try {
                spf.setFeature("http://xml.org/sax/features/external-parameter-entities", Boolean.FALSE);
            }
            catch (final Exception ex) {
                SecureSaxParserFactory.LOGGER.log(Level.CONFIG, LocalizationMessages.SAX_CANNOT_DISABLE_PARAMETER_ENTITY_PROCESSING_FEATURE(spf.getClass()), ex);
            }
            try {
                spf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
            }
            catch (final Exception ex) {
                SecureSaxParserFactory.LOGGER.log(Level.CONFIG, LocalizationMessages.SAX_CANNOT_ENABLE_SECURE_PROCESSING_FEATURE(spf.getClass()), ex);
            }
        }
    }
    
    @Override
    public void setNamespaceAware(final boolean b) {
        this.spf.setNamespaceAware(b);
    }
    
    @Override
    public void setValidating(final boolean b) {
        this.spf.setValidating(b);
    }
    
    @Override
    public boolean isNamespaceAware() {
        return this.spf.isNamespaceAware();
    }
    
    @Override
    public boolean isValidating() {
        return this.spf.isValidating();
    }
    
    @Override
    public Schema getSchema() {
        return this.spf.getSchema();
    }
    
    @Override
    public void setSchema(final Schema schema) {
        this.spf.setSchema(schema);
    }
    
    @Override
    public void setXIncludeAware(final boolean b) {
        this.spf.setXIncludeAware(b);
    }
    
    @Override
    public boolean isXIncludeAware() {
        return this.spf.isXIncludeAware();
    }
    
    @Override
    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        return new WrappingSAXParser(this.spf.newSAXParser());
    }
    
    @Override
    public void setFeature(final String s, final boolean b) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        this.spf.setFeature(s, b);
    }
    
    @Override
    public boolean getFeature(final String s) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        return this.spf.getFeature(s);
    }
    
    static {
        LOGGER = Logger.getLogger(SecureSaxParserFactory.class.getName());
        EMPTY_ENTITY_RESOLVER = new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) {
                return new InputSource(new ByteArrayInputStream(new byte[0]));
            }
        };
    }
    
    private static final class WrappingSAXParser extends SAXParser
    {
        private final SAXParser sp;
        
        protected WrappingSAXParser(final SAXParser sp) {
            this.sp = sp;
        }
        
        @Override
        public void reset() {
            this.sp.reset();
        }
        
        @Override
        public void parse(final InputStream inputStream, final HandlerBase handlerBase) throws SAXException, IOException {
            this.sp.parse(inputStream, handlerBase);
        }
        
        @Override
        public void parse(final InputStream inputStream, final HandlerBase handlerBase, final String s) throws SAXException, IOException {
            this.sp.parse(inputStream, handlerBase, s);
        }
        
        @Override
        public void parse(final InputStream inputStream, final DefaultHandler defaultHandler) throws SAXException, IOException {
            this.sp.parse(inputStream, defaultHandler);
        }
        
        @Override
        public void parse(final InputStream inputStream, final DefaultHandler defaultHandler, final String s) throws SAXException, IOException {
            this.sp.parse(inputStream, defaultHandler, s);
        }
        
        @Override
        public void parse(final String s, final HandlerBase handlerBase) throws SAXException, IOException {
            this.sp.parse(s, handlerBase);
        }
        
        @Override
        public void parse(final String s, final DefaultHandler defaultHandler) throws SAXException, IOException {
            this.sp.parse(s, defaultHandler);
        }
        
        @Override
        public void parse(final File file, final HandlerBase handlerBase) throws SAXException, IOException {
            this.sp.parse(file, handlerBase);
        }
        
        @Override
        public void parse(final File file, final DefaultHandler defaultHandler) throws SAXException, IOException {
            this.sp.parse(file, defaultHandler);
        }
        
        @Override
        public void parse(final InputSource inputSource, final HandlerBase handlerBase) throws SAXException, IOException {
            this.sp.parse(inputSource, handlerBase);
        }
        
        @Override
        public void parse(final InputSource inputSource, final DefaultHandler defaultHandler) throws SAXException, IOException {
            this.sp.parse(inputSource, defaultHandler);
        }
        
        @Override
        public Parser getParser() throws SAXException {
            return this.sp.getParser();
        }
        
        @Override
        public XMLReader getXMLReader() throws SAXException {
            final XMLReader r = this.sp.getXMLReader();
            r.setEntityResolver(SecureSaxParserFactory.EMPTY_ENTITY_RESOLVER);
            return r;
        }
        
        @Override
        public boolean isNamespaceAware() {
            return this.sp.isNamespaceAware();
        }
        
        @Override
        public boolean isValidating() {
            return this.sp.isValidating();
        }
        
        @Override
        public void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
            this.sp.setProperty(s, o);
        }
        
        @Override
        public Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
            return this.sp.getProperty(s);
        }
        
        @Override
        public Schema getSchema() {
            return this.sp.getSchema();
        }
        
        @Override
        public boolean isXIncludeAware() {
            return this.sp.isXIncludeAware();
        }
    }
}
