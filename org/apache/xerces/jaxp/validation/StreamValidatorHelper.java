package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.parsers.XML11Configuration;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.apache.xml.serialize.Serializer;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.parser.XMLInputSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.apache.xml.serialize.SerializerFactory;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
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
    private final XMLSchemaValidator fSchemaValidator;
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private SoftReference fParser;
    private SerializerFactory fSerializerFactory;
    
    public StreamValidatorHelper(final XMLSchemaValidatorComponentManager fComponentManager) {
        this.fConfiguration = new SoftReference(null);
        this.fParser = new SoftReference(null);
        this.fComponentManager = fComponentManager;
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
    }
    
    public void validate(final Source source, final Result result) throws SAXException, IOException {
        if (result instanceof StreamResult || result == null) {
            final StreamSource streamSource = (StreamSource)source;
            final StreamResult streamResult = (StreamResult)result;
            final XMLInputSource xmlInputSource = new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), null);
            xmlInputSource.setByteStream(streamSource.getInputStream());
            xmlInputSource.setCharacterStream(streamSource.getReader());
            boolean b = false;
            XMLParserConfiguration initialize = this.fConfiguration.get();
            if (initialize == null) {
                initialize = this.initialize();
                b = true;
            }
            else if (this.fComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings")) {
                initialize.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
                initialize.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
                initialize.setProperty("http://apache.org/xml/properties/security-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager"));
            }
            this.fComponentManager.reset();
            Label_0409: {
                if (streamResult != null) {
                    if (this.fSerializerFactory == null) {
                        this.fSerializerFactory = SerializerFactory.getSerializerFactory("xml");
                    }
                    Serializer serializer;
                    if (streamResult.getWriter() != null) {
                        serializer = this.fSerializerFactory.makeSerializer(streamResult.getWriter(), new OutputFormat());
                    }
                    else if (streamResult.getOutputStream() != null) {
                        serializer = this.fSerializerFactory.makeSerializer(streamResult.getOutputStream(), new OutputFormat());
                    }
                    else {
                        if (streamResult.getSystemId() == null) {
                            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "StreamResultNotInitialized", null));
                        }
                        serializer = this.fSerializerFactory.makeSerializer(XMLEntityManager.createOutputStream(streamResult.getSystemId()), new OutputFormat());
                    }
                    SAXParser documentHandler = this.fParser.get();
                    if (b || documentHandler == null) {
                        documentHandler = new SAXParser(initialize);
                        this.fParser = new SoftReference(documentHandler);
                    }
                    else {
                        documentHandler.reset();
                    }
                    initialize.setDocumentHandler(this.fSchemaValidator);
                    this.fSchemaValidator.setDocumentHandler(documentHandler);
                    documentHandler.setContentHandler(serializer.asContentHandler());
                    break Label_0409;
                }
                this.fSchemaValidator.setDocumentHandler(null);
                try {
                    initialize.parse(xmlInputSource);
                }
                catch (final XMLParseException ex) {
                    throw Util.toSAXParseException(ex);
                }
                catch (final XNIException ex2) {
                    throw Util.toSAXException(ex2);
                }
                finally {
                    this.fSchemaValidator.setDocumentHandler(null);
                }
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
    }
    
    private XMLParserConfiguration initialize() {
        final XML11Configuration xml11Configuration = new XML11Configuration();
        xml11Configuration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
        xml11Configuration.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
        final XMLErrorReporter xmlErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        xml11Configuration.setProperty("http://apache.org/xml/properties/internal/error-reporter", xmlErrorReporter);
        if (xmlErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            final XMLMessageFormatter xmlMessageFormatter = new XMLMessageFormatter();
            xmlErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmlMessageFormatter);
            xmlErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmlMessageFormatter);
        }
        xml11Configuration.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
        xml11Configuration.setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager"));
        xml11Configuration.setProperty("http://apache.org/xml/properties/security-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager"));
        xml11Configuration.setDocumentHandler(this.fSchemaValidator);
        xml11Configuration.setDTDHandler(null);
        xml11Configuration.setDTDContentModelHandler(null);
        this.fConfiguration = new SoftReference(xml11Configuration);
        return xml11Configuration;
    }
}
