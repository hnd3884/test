package org.apache.xmlbeans.impl.common;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;

public final class XmlReaderToWriter
{
    private XmlReaderToWriter() {
    }
    
    public static void writeAll(final XMLStreamReader xmlr, final XMLStreamWriter writer) throws XMLStreamException {
        while (xmlr.hasNext()) {
            write(xmlr, writer);
            xmlr.next();
        }
        write(xmlr, writer);
        writer.flush();
    }
    
    public static void write(final XMLStreamReader xmlr, final XMLStreamWriter writer) throws XMLStreamException {
        switch (xmlr.getEventType()) {
            case 1: {
                final String localName = xmlr.getLocalName();
                final String namespaceURI = xmlr.getNamespaceURI();
                if (namespaceURI != null && namespaceURI.length() > 0) {
                    final String prefix = xmlr.getPrefix();
                    if (prefix != null) {
                        writer.writeStartElement(prefix, localName, namespaceURI);
                    }
                    else {
                        writer.writeStartElement(namespaceURI, localName);
                    }
                }
                else {
                    writer.writeStartElement(localName);
                }
                for (int i = 0, len = xmlr.getNamespaceCount(); i < len; ++i) {
                    writer.writeNamespace(xmlr.getNamespacePrefix(i), xmlr.getNamespaceURI(i));
                }
                for (int i = 0, len = xmlr.getAttributeCount(); i < len; ++i) {
                    final String attUri = xmlr.getAttributeNamespace(i);
                    if (attUri != null) {
                        writer.writeAttribute(attUri, xmlr.getAttributeLocalName(i), xmlr.getAttributeValue(i));
                    }
                    else {
                        writer.writeAttribute(xmlr.getAttributeLocalName(i), xmlr.getAttributeValue(i));
                    }
                }
                break;
            }
            case 2: {
                writer.writeEndElement();
                break;
            }
            case 4:
            case 6: {
                writer.writeCharacters(xmlr.getTextCharacters(), xmlr.getTextStart(), xmlr.getTextLength());
                break;
            }
            case 3: {
                writer.writeProcessingInstruction(xmlr.getPITarget(), xmlr.getPIData());
                break;
            }
            case 12: {
                writer.writeCData(xmlr.getText());
                break;
            }
            case 5: {
                writer.writeComment(xmlr.getText());
                break;
            }
            case 9: {
                writer.writeEntityRef(xmlr.getLocalName());
                break;
            }
            case 7: {
                final String encoding = xmlr.getCharacterEncodingScheme();
                final String version = xmlr.getVersion();
                if (encoding != null && version != null) {
                    writer.writeStartDocument(encoding, version);
                    break;
                }
                if (version != null) {
                    writer.writeStartDocument(xmlr.getVersion());
                    break;
                }
                break;
            }
            case 8: {
                writer.writeEndDocument();
                break;
            }
            case 11: {
                writer.writeDTD(xmlr.getText());
                break;
            }
        }
    }
}
