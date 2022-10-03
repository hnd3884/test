package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXTransformerFactory;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.xml.sax.ContentHandler;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import jdk.xml.internal.JdkXmlUtils;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import java.lang.ref.SoftReference;

final class StreamValidatorHelper implements ValidatorHelper
{
    private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private SoftReference fConfiguration;
    private XMLSchemaValidator fSchemaValidator;
    private XMLSchemaValidatorComponentManager fComponentManager;
    private ValidatorHandlerImpl handler;
    
    public StreamValidatorHelper(final XMLSchemaValidatorComponentManager componentManager) {
        this.fConfiguration = new SoftReference(null);
        this.handler = null;
        this.fComponentManager = componentManager;
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
    }
    
    @Override
    public void validate(final Source source, final Result result) throws SAXException, IOException {
        if (result == null || result instanceof StreamResult) {
            final StreamSource streamSource = (StreamSource)source;
            if (result != null) {
                TransformerHandler identityTransformerHandler;
                try {
                    final SAXTransformerFactory tf = JdkXmlUtils.getSAXTransformFactory(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"));
                    identityTransformerHandler = tf.newTransformerHandler();
                }
                catch (final TransformerConfigurationException e) {
                    throw new TransformerFactoryConfigurationError(e);
                }
                (this.handler = new ValidatorHandlerImpl(this.fComponentManager)).setContentHandler(identityTransformerHandler);
                identityTransformerHandler.setResult(result);
            }
            final XMLInputSource input = new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), null);
            input.setByteStream(streamSource.getInputStream());
            input.setCharacterStream(streamSource.getReader());
            XMLParserConfiguration config = this.fConfiguration.get();
            if (config == null) {
                config = this.initialize();
            }
            else if (this.fComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings")) {
                config.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
                config.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
            }
            this.fComponentManager.reset();
            this.fSchemaValidator.setDocumentHandler(this.handler);
            try {
                config.parse(input);
            }
            catch (final XMLParseException e2) {
                throw Util.toSAXParseException(e2);
            }
            catch (final XNIException e3) {
                throw Util.toSAXException(e3);
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
    }
    
    private XMLParserConfiguration initialize() {
        final XML11Configuration config = new XML11Configuration();
        if (this.fComponentManager.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
            config.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager());
        }
        config.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
        config.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
        final XMLErrorReporter errorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        config.setProperty("http://apache.org/xml/properties/internal/error-reporter", errorReporter);
        if (errorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            final XMLMessageFormatter xmft = new XMLMessageFormatter();
            errorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
            errorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
        }
        config.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
        config.setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager"));
        config.setDocumentHandler(this.fSchemaValidator);
        config.setDTDHandler(null);
        config.setDTDContentModelHandler(null);
        config.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"));
        config.setProperty("http://apache.org/xml/properties/security-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager"));
        this.fConfiguration = new SoftReference((T)config);
        return config;
    }
}
