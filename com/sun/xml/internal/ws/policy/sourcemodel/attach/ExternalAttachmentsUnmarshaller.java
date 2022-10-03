package com.sun.xml.internal.ws.policy.sourcemodel.attach;

import java.net.URISyntaxException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelTranslator;
import javax.xml.stream.Location;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Collections;
import javax.xml.stream.events.StartElement;
import java.io.Reader;
import java.util.HashMap;
import com.sun.xml.internal.ws.policy.Policy;
import java.util.Map;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelUnmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.namespace.QName;
import java.net.URI;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class ExternalAttachmentsUnmarshaller
{
    private static final PolicyLogger LOGGER;
    public static final URI BINDING_ID;
    public static final URI BINDING_OPERATION_ID;
    public static final URI BINDING_OPERATION_INPUT_ID;
    public static final URI BINDING_OPERATION_OUTPUT_ID;
    public static final URI BINDING_OPERATION_FAULT_ID;
    private static final QName POLICY_ATTACHMENT;
    private static final QName APPLIES_TO;
    private static final QName POLICY;
    private static final QName URI;
    private static final QName POLICIES;
    private static final ContextClassloaderLocal<XMLInputFactory> XML_INPUT_FACTORY;
    private static final PolicyModelUnmarshaller POLICY_UNMARSHALLER;
    private final Map<URI, Policy> map;
    private URI currentUri;
    private Policy currentPolicy;
    
    public ExternalAttachmentsUnmarshaller() {
        this.map = new HashMap<URI, Policy>();
        this.currentUri = null;
        this.currentPolicy = null;
    }
    
    public static Map<URI, Policy> unmarshal(final Reader source) throws PolicyException {
        ExternalAttachmentsUnmarshaller.LOGGER.entering(source);
        try {
            final XMLEventReader reader = ExternalAttachmentsUnmarshaller.XML_INPUT_FACTORY.get().createXMLEventReader(source);
            final ExternalAttachmentsUnmarshaller instance = new ExternalAttachmentsUnmarshaller();
            final Map<URI, Policy> map = instance.unmarshal(reader, null);
            ExternalAttachmentsUnmarshaller.LOGGER.exiting(map);
            return Collections.unmodifiableMap((Map<? extends URI, ? extends Policy>)map);
        }
        catch (final XMLStreamException ex) {
            throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0086_FAILED_CREATE_READER(source)), ex);
        }
    }
    
    private Map<URI, Policy> unmarshal(final XMLEventReader reader, final StartElement parentElement) throws PolicyException {
        XMLEvent event = null;
        while (reader.hasNext()) {
            try {
                event = reader.peek();
                switch (event.getEventType()) {
                    case 5:
                    case 7: {
                        reader.nextEvent();
                        continue;
                    }
                    case 4: {
                        this.processCharacters(event.asCharacters(), parentElement, this.map);
                        reader.nextEvent();
                        continue;
                    }
                    case 2: {
                        this.processEndTag(event.asEndElement(), parentElement);
                        reader.nextEvent();
                        return this.map;
                    }
                    case 1: {
                        final StartElement element = event.asStartElement();
                        this.processStartTag(element, parentElement, reader, this.map);
                        continue;
                    }
                    case 8: {
                        return this.map;
                    }
                    default: {
                        throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0087_UNKNOWN_EVENT(event)));
                    }
                }
                continue;
            }
            catch (final XMLStreamException e) {
                final Location location = (event == null) ? null : event.getLocation();
                throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(location)), e);
            }
            break;
        }
        return this.map;
    }
    
    private void processStartTag(final StartElement element, final StartElement parent, final XMLEventReader reader, final Map<URI, Policy> map) throws PolicyException {
        try {
            final QName name = element.getName();
            if (parent == null) {
                if (!name.equals(ExternalAttachmentsUnmarshaller.POLICIES)) {
                    throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<Policies>", name, element.getLocation())));
                }
            }
            else {
                final QName parentName = parent.getName();
                if (parentName.equals(ExternalAttachmentsUnmarshaller.POLICIES)) {
                    if (!name.equals(ExternalAttachmentsUnmarshaller.POLICY_ATTACHMENT)) {
                        throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<PolicyAttachment>", name, element.getLocation())));
                    }
                }
                else if (parentName.equals(ExternalAttachmentsUnmarshaller.POLICY_ATTACHMENT)) {
                    if (name.equals(ExternalAttachmentsUnmarshaller.POLICY)) {
                        this.readPolicy(reader);
                        return;
                    }
                    if (!name.equals(ExternalAttachmentsUnmarshaller.APPLIES_TO)) {
                        throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<AppliesTo> or <Policy>", name, element.getLocation())));
                    }
                }
                else {
                    if (!parentName.equals(ExternalAttachmentsUnmarshaller.APPLIES_TO)) {
                        throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0090_UNEXPECTED_ELEMENT(name, element.getLocation())));
                    }
                    if (!name.equals(ExternalAttachmentsUnmarshaller.URI)) {
                        throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<URI>", name, element.getLocation())));
                    }
                }
            }
            reader.nextEvent();
            this.unmarshal(reader, element);
        }
        catch (final XMLStreamException e) {
            throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(element.getLocation()), e));
        }
    }
    
    private void readPolicy(final XMLEventReader reader) throws PolicyException {
        final PolicySourceModel policyModel = ExternalAttachmentsUnmarshaller.POLICY_UNMARSHALLER.unmarshalModel(reader);
        final PolicyModelTranslator translator = PolicyModelTranslator.getTranslator();
        final Policy policy = translator.translate(policyModel);
        if (this.currentUri != null) {
            this.map.put(this.currentUri, policy);
            this.currentUri = null;
            this.currentPolicy = null;
        }
        else {
            this.currentPolicy = policy;
        }
    }
    
    private void processEndTag(final EndElement element, final StartElement startElement) throws PolicyException {
        this.checkEndTagName(startElement.getName(), element);
    }
    
    private void checkEndTagName(final QName expectedName, final EndElement element) throws PolicyException {
        final QName actualName = element.getName();
        if (!expectedName.equals(actualName)) {
            throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0091_END_ELEMENT_NO_MATCH(expectedName, element, element.getLocation())));
        }
    }
    
    private void processCharacters(final Characters chars, final StartElement currentElement, final Map<URI, Policy> map) throws PolicyException {
        if (chars.isWhiteSpace()) {
            return;
        }
        final String data = chars.getData();
        if (currentElement != null && ExternalAttachmentsUnmarshaller.URI.equals(currentElement.getName())) {
            this.processUri(chars, map);
            return;
        }
        throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0092_CHARACTER_DATA_UNEXPECTED(currentElement, data, chars.getLocation())));
    }
    
    private void processUri(final Characters chars, final Map<URI, Policy> map) throws PolicyException {
        final String data = chars.getData().trim();
        try {
            final URI uri = new URI(data);
            if (this.currentPolicy != null) {
                map.put(uri, this.currentPolicy);
                this.currentUri = null;
                this.currentPolicy = null;
            }
            else {
                this.currentUri = uri;
            }
        }
        catch (final URISyntaxException e) {
            throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0093_INVALID_URI(data, chars.getLocation())), e);
        }
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(ExternalAttachmentsUnmarshaller.class);
        try {
            BINDING_ID = new URI("urn:uuid:c9bef600-0d7a-11de-abc1-0002a5d5c51b");
            BINDING_OPERATION_ID = new URI("urn:uuid:62e66b60-0d7b-11de-a1a2-0002a5d5c51b");
            BINDING_OPERATION_INPUT_ID = new URI("urn:uuid:730d8d20-0d7b-11de-84e9-0002a5d5c51b");
            BINDING_OPERATION_OUTPUT_ID = new URI("urn:uuid:85b0f980-0d7b-11de-8e9d-0002a5d5c51b");
            BINDING_OPERATION_FAULT_ID = new URI("urn:uuid:917cb060-0d7b-11de-9e80-0002a5d5c51b");
        }
        catch (final URISyntaxException e) {
            throw ExternalAttachmentsUnmarshaller.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0094_INVALID_URN()), e);
        }
        POLICY_ATTACHMENT = new QName("http://www.w3.org/ns/ws-policy", "PolicyAttachment");
        APPLIES_TO = new QName("http://www.w3.org/ns/ws-policy", "AppliesTo");
        POLICY = new QName("http://www.w3.org/ns/ws-policy", "Policy");
        URI = new QName("http://www.w3.org/ns/ws-policy", "URI");
        POLICIES = new QName("http://java.sun.com/xml/ns/metro/management", "Policies");
        XML_INPUT_FACTORY = new ContextClassloaderLocal<XMLInputFactory>() {
            @Override
            protected XMLInputFactory initialValue() throws Exception {
                return XMLInputFactory.newInstance();
            }
        };
        POLICY_UNMARSHALLER = PolicyModelUnmarshaller.getXmlUnmarshaller();
    }
}
