package org.apache.xmlbeans.impl.common;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import java.io.InputStream;

public class LoadSaveUtils
{
    public static Document xmlText2GenericDom(final InputStream is, final Document emptyDoc) throws SAXException, ParserConfigurationException, IOException {
        final SAXParser parser = SAXHelper.saxFactory().newSAXParser();
        final Sax2Dom handler = new Sax2Dom(emptyDoc);
        parser.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        parser.parse(is, handler);
        return (Document)handler.getDOM();
    }
    
    public static void xmlStreamReader2XmlText(final XMLStreamReader xsr, final OutputStream os) throws XMLStreamException {
        final XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(os);
        while (xsr.hasNext()) {
            switch (xsr.getEventType()) {
                case 10: {
                    xsw.writeAttribute(xsr.getPrefix(), xsr.getNamespaceURI(), xsr.getLocalName(), xsr.getText());
                    break;
                }
                case 12: {
                    xsw.writeCData(xsr.getText());
                    break;
                }
                case 4: {
                    xsw.writeCharacters(xsr.getText());
                    break;
                }
                case 5: {
                    xsw.writeComment(xsr.getText());
                    break;
                }
                case 11: {
                    xsw.writeDTD(xsr.getText());
                    break;
                }
                case 8: {
                    xsw.writeEndDocument();
                    break;
                }
                case 2: {
                    xsw.writeEndElement();
                }
                case 9: {
                    xsw.writeEntityRef(xsr.getText());
                    break;
                }
                case 13: {
                    xsw.writeNamespace(xsr.getPrefix(), xsr.getNamespaceURI());
                }
                case 3: {
                    xsw.writeProcessingInstruction(xsr.getPITarget(), xsr.getPIData());
                    break;
                }
                case 6: {
                    xsw.writeCharacters(xsr.getText());
                    break;
                }
                case 7: {
                    xsw.writeStartDocument();
                    break;
                }
                case 1: {
                    xsw.writeStartElement((xsr.getPrefix() == null) ? "" : xsr.getPrefix(), xsr.getLocalName(), xsr.getNamespaceURI());
                    final int attrs = xsr.getAttributeCount();
                    for (int i = attrs - 1; i >= 0; --i) {
                        xsw.writeAttribute((xsr.getAttributePrefix(i) == null) ? "" : xsr.getAttributePrefix(i), xsr.getAttributeNamespace(i), xsr.getAttributeLocalName(i), xsr.getAttributeValue(i));
                    }
                    for (int nses = xsr.getNamespaceCount(), j = 0; j < nses; ++j) {
                        xsw.writeNamespace(xsr.getNamespacePrefix(j), xsr.getNamespaceURI(j));
                    }
                    break;
                }
            }
            xsr.next();
        }
        xsw.flush();
    }
}
