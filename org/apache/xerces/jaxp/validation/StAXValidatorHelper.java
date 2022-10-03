package org.apache.xerces.jaxp.validation;

import javax.xml.stream.events.Namespace;
import java.util.Iterator;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.util.XMLSymbols;
import javax.xml.stream.events.EntityDeclaration;
import java.io.IOException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.Location;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.apache.xerces.impl.Constants;
import java.util.List;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import java.util.ArrayList;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import javax.xml.stream.events.XMLEvent;
import java.util.HashMap;
import org.apache.xerces.util.StAXLocationWrapper;
import org.apache.xerces.util.JAXPNamespaceContextWrapper;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.validation.EntityState;

final class StAXValidatorHelper implements ValidatorHelper, EntityState
{
    private static final String STRING_INTERNING = "javax.xml.stream.isInterning";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    private final XMLErrorReporter fErrorReporter;
    private final XMLSchemaValidator fSchemaValidator;
    private final SymbolTable fSymbolTable;
    private final ValidationManager fValidationManager;
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private final JAXPNamespaceContextWrapper fNamespaceContext;
    private final StAXLocationWrapper fStAXLocationWrapper;
    private final XMLStreamReaderLocation fXMLStreamReaderLocation;
    private HashMap fEntities;
    private boolean fStringsInternalized;
    private StreamHelper fStreamHelper;
    private EventHelper fEventHelper;
    private StAXDocumentHandler fStAXValidatorHandler;
    private StAXStreamResultBuilder fStAXStreamResultBuilder;
    private StAXEventResultBuilder fStAXEventResultBuilder;
    private int fDepth;
    private XMLEvent fCurrentEvent;
    final QName fElementQName;
    final QName fAttributeQName;
    final XMLAttributesImpl fAttributes;
    final ArrayList fDeclaredPrefixes;
    final XMLString fTempString;
    final XMLStringBuffer fStringBuffer;
    private final boolean fIsXSD11;
    
    public StAXValidatorHelper(final XMLSchemaValidatorComponentManager fComponentManager) {
        this.fStAXLocationWrapper = new StAXLocationWrapper();
        this.fXMLStreamReaderLocation = new XMLStreamReaderLocation();
        this.fEntities = null;
        this.fStringsInternalized = false;
        this.fDepth = 0;
        this.fCurrentEvent = null;
        this.fElementQName = new QName();
        this.fAttributeQName = new QName();
        this.fAttributes = new XMLAttributesImpl();
        this.fDeclaredPrefixes = new ArrayList();
        this.fTempString = new XMLString();
        this.fStringBuffer = new XMLStringBuffer();
        this.fComponentManager = fComponentManager;
        this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
        this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
        (this.fNamespaceContext = new JAXPNamespaceContextWrapper(this.fSymbolTable)).setDeclaredPrefixes(this.fDeclaredPrefixes);
        this.fIsXSD11 = Constants.W3C_XML_SCHEMA11_NS_URI.equals(this.fComponentManager.getProperty("http://apache.org/xml/properties/validation/schema/version"));
    }
    
    public void validate(final Source source, final Result result) throws SAXException, IOException {
        if (result instanceof StAXResult || result == null) {
            final StAXSource stAXSource = (StAXSource)source;
            final StAXResult stAXResult = (StAXResult)result;
            try {
                final XMLStreamReader xmlStreamReader = stAXSource.getXMLStreamReader();
                if (xmlStreamReader != null) {
                    if (this.fStreamHelper == null) {
                        this.fStreamHelper = new StreamHelper();
                    }
                    this.fStreamHelper.validate(xmlStreamReader, stAXResult);
                }
                else {
                    if (this.fEventHelper == null) {
                        this.fEventHelper = new EventHelper();
                    }
                    this.fEventHelper.validate(stAXSource.getXMLEventReader(), stAXResult);
                }
            }
            catch (final XMLStreamException ex) {
                throw new SAXException(ex);
            }
            catch (final XMLParseException ex2) {
                throw Util.toSAXParseException(ex2);
            }
            catch (final XNIException ex3) {
                throw Util.toSAXException(ex3);
            }
            finally {
                this.fCurrentEvent = null;
                this.fStAXLocationWrapper.setLocation(null);
                this.fXMLStreamReaderLocation.setXMLStreamReader(null);
                if (this.fStAXValidatorHandler != null) {
                    this.fStAXValidatorHandler.setStAXResult(null);
                }
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
    }
    
    public boolean isEntityDeclared(final String s) {
        return this.fEntities != null && this.fEntities.containsKey(s);
    }
    
    public boolean isEntityUnparsed(final String s) {
        if (this.fEntities != null) {
            final EntityDeclaration entityDeclaration = this.fEntities.get(s);
            if (entityDeclaration != null) {
                return entityDeclaration.getNotationName() != null;
            }
        }
        return false;
    }
    
    final EntityDeclaration getEntityDeclaration(final String s) {
        return (this.fEntities != null) ? this.fEntities.get(s) : null;
    }
    
    final XMLEvent getCurrentEvent() {
        return this.fCurrentEvent;
    }
    
    final void fillQName(final QName qName, String s, String empty_STRING, String empty_STRING2) {
        if (!this.fStringsInternalized) {
            s = ((s != null && s.length() > 0) ? this.fSymbolTable.addSymbol(s) : null);
            empty_STRING = ((empty_STRING != null) ? this.fSymbolTable.addSymbol(empty_STRING) : XMLSymbols.EMPTY_STRING);
            empty_STRING2 = ((empty_STRING2 != null && empty_STRING2.length() > 0) ? this.fSymbolTable.addSymbol(empty_STRING2) : XMLSymbols.EMPTY_STRING);
        }
        else {
            if (s != null && s.length() == 0) {
                s = null;
            }
            if (empty_STRING == null) {
                empty_STRING = XMLSymbols.EMPTY_STRING;
            }
            if (empty_STRING2 == null) {
                empty_STRING2 = XMLSymbols.EMPTY_STRING;
            }
        }
        String addSymbol = empty_STRING;
        if (empty_STRING2 != XMLSymbols.EMPTY_STRING) {
            this.fStringBuffer.clear();
            this.fStringBuffer.append(empty_STRING2);
            this.fStringBuffer.append(':');
            this.fStringBuffer.append(empty_STRING);
            addSymbol = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
        }
        qName.setValues(empty_STRING2, empty_STRING, addSymbol, s);
    }
    
    final void setup(final Location location, final StAXResult stAXResult, final boolean fStringsInternalized) {
        this.fDepth = 0;
        this.fComponentManager.reset();
        this.setupStAXResultHandler(stAXResult);
        this.fValidationManager.setEntityState(this);
        if (this.fEntities != null && !this.fEntities.isEmpty()) {
            this.fEntities.clear();
        }
        this.fStAXLocationWrapper.setLocation(location);
        this.fErrorReporter.setDocumentLocator(this.fStAXLocationWrapper);
        this.fStringsInternalized = fStringsInternalized;
    }
    
    final void processEntityDeclarations(final List list) {
        final int n = (list != null) ? list.size() : 0;
        if (n > 0) {
            if (this.fEntities == null) {
                this.fEntities = new HashMap();
            }
            for (int i = 0; i < n; ++i) {
                final EntityDeclaration entityDeclaration = list.get(i);
                this.fEntities.put(entityDeclaration.getName(), entityDeclaration);
            }
        }
    }
    
    private void setupStAXResultHandler(final StAXResult stAXResult) {
        if (stAXResult == null) {
            this.fStAXValidatorHandler = null;
            this.fSchemaValidator.setDocumentHandler(null);
            return;
        }
        if (stAXResult.getXMLStreamWriter() != null) {
            if (this.fStAXStreamResultBuilder == null) {
                this.fStAXStreamResultBuilder = new StAXStreamResultBuilder(this.fNamespaceContext);
            }
            this.fStAXValidatorHandler = this.fStAXStreamResultBuilder;
            this.fStAXStreamResultBuilder.setStAXResult(stAXResult);
        }
        else {
            if (this.fStAXEventResultBuilder == null) {
                this.fStAXEventResultBuilder = new StAXEventResultBuilder(this, this.fNamespaceContext);
            }
            this.fStAXValidatorHandler = this.fStAXEventResultBuilder;
            this.fStAXEventResultBuilder.setStAXResult(stAXResult);
        }
        this.fSchemaValidator.setDocumentHandler(this.fStAXValidatorHandler);
    }
    
    final class EventHelper
    {
        private static final int CHUNK_SIZE = 1024;
        private static final int CHUNK_MASK = 1023;
        private final char[] fCharBuffer;
        
        EventHelper() {
            this.fCharBuffer = new char[1024];
        }
        
        final void validate(final XMLEventReader xmlEventReader, final StAXResult stAXResult) throws SAXException, XMLStreamException {
            StAXValidatorHelper.this.fCurrentEvent = xmlEventReader.peek();
            if (StAXValidatorHelper.this.fCurrentEvent != null) {
                final int eventType = StAXValidatorHelper.this.fCurrentEvent.getEventType();
                if (eventType != 7 && eventType != 1) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(StAXValidatorHelper.this.fComponentManager.getLocale(), "StAXIllegalInitialState", null));
                }
                StAXValidatorHelper.this.setup(null, stAXResult, false);
                StAXValidatorHelper.this.fSchemaValidator.startDocument(StAXValidatorHelper.this.fStAXLocationWrapper, null, StAXValidatorHelper.this.fNamespaceContext, null);
            Label_1025:
                while (xmlEventReader.hasNext()) {
                    StAXValidatorHelper.this.fCurrentEvent = xmlEventReader.nextEvent();
                    switch (StAXValidatorHelper.this.fCurrentEvent.getEventType()) {
                        case 1: {
                            ++StAXValidatorHelper.this.fDepth;
                            final StartElement startElement = StAXValidatorHelper.this.fCurrentEvent.asStartElement();
                            this.fillQName(StAXValidatorHelper.this.fElementQName, startElement.getName());
                            this.fillXMLAttributes(startElement);
                            this.fillDeclaredPrefixes(startElement);
                            StAXValidatorHelper.this.fNamespaceContext.setNamespaceContext(startElement.getNamespaceContext());
                            StAXValidatorHelper.this.fStAXLocationWrapper.setLocation(startElement.getLocation());
                            StAXValidatorHelper.this.fSchemaValidator.startElement(StAXValidatorHelper.this.fElementQName, StAXValidatorHelper.this.fAttributes, null);
                            continue;
                        }
                        case 2: {
                            final EndElement endElement = StAXValidatorHelper.this.fCurrentEvent.asEndElement();
                            this.fillQName(StAXValidatorHelper.this.fElementQName, endElement.getName());
                            this.fillDeclaredPrefixes(endElement);
                            StAXValidatorHelper.this.fStAXLocationWrapper.setLocation(endElement.getLocation());
                            StAXValidatorHelper.this.fSchemaValidator.endElement(StAXValidatorHelper.this.fElementQName, null);
                            if (--StAXValidatorHelper.this.fDepth <= 0) {
                                break Label_1025;
                            }
                            continue;
                        }
                        case 4:
                        case 6: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                final Characters characters = StAXValidatorHelper.this.fCurrentEvent.asCharacters();
                                StAXValidatorHelper.this.fStAXValidatorHandler.setIgnoringCharacters(true);
                                this.sendCharactersToValidator(characters.getData());
                                StAXValidatorHelper.this.fStAXValidatorHandler.setIgnoringCharacters(false);
                                StAXValidatorHelper.this.fStAXValidatorHandler.characters(characters);
                                continue;
                            }
                            this.sendCharactersToValidator(StAXValidatorHelper.this.fCurrentEvent.asCharacters().getData());
                            continue;
                        }
                        case 12: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                final Characters characters2 = StAXValidatorHelper.this.fCurrentEvent.asCharacters();
                                StAXValidatorHelper.this.fStAXValidatorHandler.setIgnoringCharacters(true);
                                StAXValidatorHelper.this.fSchemaValidator.startCDATA(null);
                                this.sendCharactersToValidator(StAXValidatorHelper.this.fCurrentEvent.asCharacters().getData());
                                StAXValidatorHelper.this.fSchemaValidator.endCDATA(null);
                                StAXValidatorHelper.this.fStAXValidatorHandler.setIgnoringCharacters(false);
                                StAXValidatorHelper.this.fStAXValidatorHandler.cdata(characters2);
                                continue;
                            }
                            StAXValidatorHelper.this.fSchemaValidator.startCDATA(null);
                            this.sendCharactersToValidator(StAXValidatorHelper.this.fCurrentEvent.asCharacters().getData());
                            StAXValidatorHelper.this.fSchemaValidator.endCDATA(null);
                            continue;
                        }
                        case 7: {
                            ++StAXValidatorHelper.this.fDepth;
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.startDocument((StartDocument)StAXValidatorHelper.this.fCurrentEvent);
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.endDocument((EndDocument)StAXValidatorHelper.this.fCurrentEvent);
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (StAXValidatorHelper.this.fIsXSD11) {
                                final ProcessingInstruction processingInstruction = (ProcessingInstruction)StAXValidatorHelper.this.fCurrentEvent;
                                this.fillXMLString(StAXValidatorHelper.this.fTempString, processingInstruction.getData());
                                StAXValidatorHelper.this.fSchemaValidator.processingInstruction(processingInstruction.getTarget(), StAXValidatorHelper.this.fTempString, null);
                            }
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.processingInstruction((ProcessingInstruction)StAXValidatorHelper.this.fCurrentEvent);
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if (StAXValidatorHelper.this.fIsXSD11) {
                                this.fillXMLString(StAXValidatorHelper.this.fTempString, ((Comment)StAXValidatorHelper.this.fCurrentEvent).getText());
                                StAXValidatorHelper.this.fSchemaValidator.comment(StAXValidatorHelper.this.fTempString, null);
                            }
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.comment((Comment)StAXValidatorHelper.this.fCurrentEvent);
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.entityReference((EntityReference)StAXValidatorHelper.this.fCurrentEvent);
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            final DTD dtd = (DTD)StAXValidatorHelper.this.fCurrentEvent;
                            StAXValidatorHelper.this.processEntityDeclarations(dtd.getEntities());
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.doctypeDecl(dtd);
                                continue;
                            }
                            continue;
                        }
                    }
                }
                StAXValidatorHelper.this.fSchemaValidator.endDocument(null);
            }
        }
        
        private void fillQName(final QName qName, final javax.xml.namespace.QName qName2) {
            StAXValidatorHelper.this.fillQName(qName, qName2.getNamespaceURI(), qName2.getLocalPart(), qName2.getPrefix());
        }
        
        private void fillXMLAttributes(final StartElement startElement) {
            StAXValidatorHelper.this.fAttributes.removeAllAttributes();
            final Iterator attributes = startElement.getAttributes();
            while (attributes.hasNext()) {
                final Attribute attribute = attributes.next();
                this.fillQName(StAXValidatorHelper.this.fAttributeQName, attribute.getName());
                final String dtdType = attribute.getDTDType();
                final int length = StAXValidatorHelper.this.fAttributes.getLength();
                StAXValidatorHelper.this.fAttributes.addAttributeNS(StAXValidatorHelper.this.fAttributeQName, (dtdType != null) ? dtdType : XMLSymbols.fCDATASymbol, attribute.getValue());
                StAXValidatorHelper.this.fAttributes.setSpecified(length, attribute.isSpecified());
            }
        }
        
        private void fillDeclaredPrefixes(final StartElement startElement) {
            this.fillDeclaredPrefixes(startElement.getNamespaces());
        }
        
        private void fillDeclaredPrefixes(final EndElement endElement) {
            this.fillDeclaredPrefixes(endElement.getNamespaces());
        }
        
        private void fillDeclaredPrefixes(final Iterator iterator) {
            StAXValidatorHelper.this.fDeclaredPrefixes.clear();
            while (iterator.hasNext()) {
                final String prefix = iterator.next().getPrefix();
                StAXValidatorHelper.this.fDeclaredPrefixes.add((prefix != null) ? prefix : "");
            }
        }
        
        private void sendCharactersToValidator(final String s) {
            if (s != null) {
                final int length = s.length();
                final int n = length & 0x3FF;
                if (n > 0) {
                    s.getChars(0, n, this.fCharBuffer, 0);
                    StAXValidatorHelper.this.fTempString.setValues(this.fCharBuffer, 0, n);
                    StAXValidatorHelper.this.fSchemaValidator.characters(StAXValidatorHelper.this.fTempString, null);
                }
                int i = n;
                while (i < length) {
                    s.getChars(i, i += 1024, this.fCharBuffer, 0);
                    StAXValidatorHelper.this.fTempString.setValues(this.fCharBuffer, 0, 1024);
                    StAXValidatorHelper.this.fSchemaValidator.characters(StAXValidatorHelper.this.fTempString, null);
                }
            }
        }
        
        private void fillXMLString(final XMLString xmlString, final String s) {
            int length;
            char[] array;
            if (s != null) {
                length = s.length();
                if (length <= this.fCharBuffer.length) {
                    s.getChars(0, length, this.fCharBuffer, 0);
                    array = this.fCharBuffer;
                }
                else {
                    array = s.toCharArray();
                }
            }
            else {
                length = 0;
                array = new char[0];
            }
            xmlString.setValues(array, 0, length);
        }
    }
    
    final class StreamHelper
    {
        final void validate(final XMLStreamReader xmlStreamReader, final StAXResult stAXResult) throws SAXException, XMLStreamException {
            if (xmlStreamReader.hasNext()) {
                int n = xmlStreamReader.getEventType();
                if (n != 7 && n != 1) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(StAXValidatorHelper.this.fComponentManager.getLocale(), "StAXIllegalInitialState", null));
                }
                StAXValidatorHelper.this.fXMLStreamReaderLocation.setXMLStreamReader(xmlStreamReader);
                Object o = Boolean.FALSE;
                try {
                    o = xmlStreamReader.getProperty("javax.xml.stream.isInterning");
                }
                catch (final Exception ex) {}
                StAXValidatorHelper.this.setup(StAXValidatorHelper.this.fXMLStreamReaderLocation, stAXResult, Boolean.TRUE.equals(o));
                StAXValidatorHelper.this.fSchemaValidator.startDocument(StAXValidatorHelper.this.fStAXLocationWrapper, null, StAXValidatorHelper.this.fNamespaceContext, null);
                do {
                    switch (n) {
                        case 1: {
                            ++StAXValidatorHelper.this.fDepth;
                            StAXValidatorHelper.this.fillQName(StAXValidatorHelper.this.fElementQName, xmlStreamReader.getNamespaceURI(), xmlStreamReader.getLocalName(), xmlStreamReader.getPrefix());
                            this.fillXMLAttributes(xmlStreamReader);
                            this.fillDeclaredPrefixes(xmlStreamReader);
                            StAXValidatorHelper.this.fNamespaceContext.setNamespaceContext(xmlStreamReader.getNamespaceContext());
                            StAXValidatorHelper.this.fSchemaValidator.startElement(StAXValidatorHelper.this.fElementQName, StAXValidatorHelper.this.fAttributes, null);
                            break;
                        }
                        case 2: {
                            StAXValidatorHelper.this.fillQName(StAXValidatorHelper.this.fElementQName, xmlStreamReader.getNamespaceURI(), xmlStreamReader.getLocalName(), xmlStreamReader.getPrefix());
                            this.fillDeclaredPrefixes(xmlStreamReader);
                            StAXValidatorHelper.this.fNamespaceContext.setNamespaceContext(xmlStreamReader.getNamespaceContext());
                            StAXValidatorHelper.this.fSchemaValidator.endElement(StAXValidatorHelper.this.fElementQName, null);
                            --StAXValidatorHelper.this.fDepth;
                            break;
                        }
                        case 4:
                        case 6: {
                            StAXValidatorHelper.this.fTempString.setValues(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                            StAXValidatorHelper.this.fSchemaValidator.characters(StAXValidatorHelper.this.fTempString, null);
                            break;
                        }
                        case 12: {
                            StAXValidatorHelper.this.fSchemaValidator.startCDATA(null);
                            StAXValidatorHelper.this.fTempString.setValues(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                            StAXValidatorHelper.this.fSchemaValidator.characters(StAXValidatorHelper.this.fTempString, null);
                            StAXValidatorHelper.this.fSchemaValidator.endCDATA(null);
                            break;
                        }
                        case 7: {
                            ++StAXValidatorHelper.this.fDepth;
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.startDocument(xmlStreamReader);
                                break;
                            }
                            break;
                        }
                        case 3: {
                            if (StAXValidatorHelper.this.fIsXSD11) {
                                final String piData = xmlStreamReader.getPIData();
                                if (piData != null) {
                                    StAXValidatorHelper.this.fTempString.setValues(piData.toCharArray(), 0, piData.length());
                                }
                                else {
                                    StAXValidatorHelper.this.fTempString.setValues(new char[0], 0, 0);
                                }
                                StAXValidatorHelper.this.fSchemaValidator.processingInstruction(xmlStreamReader.getPITarget(), StAXValidatorHelper.this.fTempString, null);
                            }
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.processingInstruction(xmlStreamReader);
                                break;
                            }
                            break;
                        }
                        case 5: {
                            if (StAXValidatorHelper.this.fIsXSD11) {
                                StAXValidatorHelper.this.fTempString.setValues(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                                StAXValidatorHelper.this.fSchemaValidator.comment(StAXValidatorHelper.this.fTempString, null);
                            }
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.comment(xmlStreamReader);
                                break;
                            }
                            break;
                        }
                        case 9: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                StAXValidatorHelper.this.fStAXValidatorHandler.entityReference(xmlStreamReader);
                                break;
                            }
                            break;
                        }
                        case 11: {
                            StAXValidatorHelper.this.processEntityDeclarations((List)xmlStreamReader.getProperty("javax.xml.stream.entities"));
                            break;
                        }
                    }
                    n = xmlStreamReader.next();
                } while (xmlStreamReader.hasNext() && StAXValidatorHelper.this.fDepth > 0);
                StAXValidatorHelper.this.fSchemaValidator.endDocument(null);
                if (n == 8 && StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                    StAXValidatorHelper.this.fStAXValidatorHandler.endDocument(xmlStreamReader);
                }
            }
        }
        
        private void fillXMLAttributes(final XMLStreamReader xmlStreamReader) {
            StAXValidatorHelper.this.fAttributes.removeAllAttributes();
            for (int attributeCount = xmlStreamReader.getAttributeCount(), i = 0; i < attributeCount; ++i) {
                StAXValidatorHelper.this.fillQName(StAXValidatorHelper.this.fAttributeQName, xmlStreamReader.getAttributeNamespace(i), xmlStreamReader.getAttributeLocalName(i), xmlStreamReader.getAttributePrefix(i));
                final String attributeType = xmlStreamReader.getAttributeType(i);
                StAXValidatorHelper.this.fAttributes.addAttributeNS(StAXValidatorHelper.this.fAttributeQName, (attributeType != null) ? attributeType : XMLSymbols.fCDATASymbol, xmlStreamReader.getAttributeValue(i));
                StAXValidatorHelper.this.fAttributes.setSpecified(i, xmlStreamReader.isAttributeSpecified(i));
            }
        }
        
        private void fillDeclaredPrefixes(final XMLStreamReader xmlStreamReader) {
            StAXValidatorHelper.this.fDeclaredPrefixes.clear();
            for (int namespaceCount = xmlStreamReader.getNamespaceCount(), i = 0; i < namespaceCount; ++i) {
                final String namespacePrefix = xmlStreamReader.getNamespacePrefix(i);
                StAXValidatorHelper.this.fDeclaredPrefixes.add((namespacePrefix != null) ? namespacePrefix : "");
            }
        }
    }
    
    static final class XMLStreamReaderLocation implements Location
    {
        private XMLStreamReader reader;
        
        public XMLStreamReaderLocation() {
        }
        
        public int getCharacterOffset() {
            final Location location = this.getLocation();
            if (location != null) {
                return location.getCharacterOffset();
            }
            return -1;
        }
        
        public int getColumnNumber() {
            final Location location = this.getLocation();
            if (location != null) {
                return location.getColumnNumber();
            }
            return -1;
        }
        
        public int getLineNumber() {
            final Location location = this.getLocation();
            if (location != null) {
                return location.getLineNumber();
            }
            return -1;
        }
        
        public String getPublicId() {
            final Location location = this.getLocation();
            if (location != null) {
                return location.getPublicId();
            }
            return null;
        }
        
        public String getSystemId() {
            final Location location = this.getLocation();
            if (location != null) {
                return location.getSystemId();
            }
            return null;
        }
        
        public void setXMLStreamReader(final XMLStreamReader reader) {
            this.reader = reader;
        }
        
        private Location getLocation() {
            return (this.reader != null) ? this.reader.getLocation() : null;
        }
    }
}
