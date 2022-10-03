package com.sun.xml.internal.ws.runtime.config;

import javax.xml.ws.WebServiceFeature;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.config.metro.util.ParserUtil;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.XMLEventReader;
import javax.xml.namespace.QName;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.config.metro.dev.FeatureReader;

public class TubelineFeatureReader implements FeatureReader
{
    private static final Logger LOGGER;
    private static final QName NAME_ATTRIBUTE_NAME;
    
    @Override
    public TubelineFeature parse(final XMLEventReader reader) throws WebServiceException {
        try {
            final StartElement element = reader.nextEvent().asStartElement();
            boolean attributeEnabled = true;
            final Iterator iterator = element.getAttributes();
            while (iterator.hasNext()) {
                final Attribute nextAttribute = iterator.next();
                final QName attributeName = nextAttribute.getName();
                if (TubelineFeatureReader.ENABLED_ATTRIBUTE_NAME.equals(attributeName)) {
                    attributeEnabled = ParserUtil.parseBooleanValue(nextAttribute.getValue());
                }
                else {
                    if (TubelineFeatureReader.NAME_ATTRIBUTE_NAME.equals(attributeName)) {
                        continue;
                    }
                    throw TubelineFeatureReader.LOGGER.logSevereException(new WebServiceException("Unexpected attribute"));
                }
            }
            return this.parseFactories(attributeEnabled, element, reader);
        }
        catch (final XMLStreamException e) {
            throw TubelineFeatureReader.LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", e));
        }
    }
    
    private TubelineFeature parseFactories(final boolean enabled, final StartElement element, final XMLEventReader reader) throws WebServiceException {
        int elementRead = 0;
    Label_0268:
        while (reader.hasNext()) {
            try {
                final XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {
                    case 5: {
                        continue;
                    }
                    case 4: {
                        if (event.asCharacters().isWhiteSpace()) {
                            continue;
                        }
                        throw TubelineFeatureReader.LOGGER.logSevereException(new WebServiceException("No character data allowed, was " + event.asCharacters()));
                    }
                    case 1: {
                        ++elementRead;
                        continue;
                    }
                    case 2: {
                        if (--elementRead >= 0) {
                            continue;
                        }
                        final EndElement endElement = event.asEndElement();
                        if (!element.getName().equals(endElement.getName())) {
                            throw TubelineFeatureReader.LOGGER.logSevereException(new WebServiceException("End element does not match " + endElement));
                        }
                        break Label_0268;
                    }
                    default: {
                        throw TubelineFeatureReader.LOGGER.logSevereException(new WebServiceException("Unexpected event, was " + event));
                    }
                }
                continue;
            }
            catch (final XMLStreamException e) {
                throw TubelineFeatureReader.LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", e));
            }
            break;
        }
        return new TubelineFeature(enabled);
    }
    
    static {
        LOGGER = Logger.getLogger(TubelineFeatureReader.class);
        NAME_ATTRIBUTE_NAME = new QName("name");
    }
}
