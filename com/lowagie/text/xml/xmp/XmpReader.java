package com.lowagie.text.xml.xmp;

import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.xml.XmlDomWriter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import com.lowagie.text.ExceptionConverter;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class XmpReader
{
    private Document domDocument;
    
    public XmpReader(final byte[] bytes) throws SAXException, IOException {
        try {
            final DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            fact.setNamespaceAware(true);
            final DocumentBuilder db = fact.newDocumentBuilder();
            db.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(final String publicId, final String systemId) {
                    return new InputSource(new StringReader(""));
                }
            });
            final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            this.domDocument = db.parse(bais);
        }
        catch (final ParserConfigurationException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public boolean replace(final String namespaceURI, final String localName, final String value) {
        final NodeList nodes = this.domDocument.getElementsByTagNameNS(namespaceURI, localName);
        if (nodes.getLength() == 0) {
            return false;
        }
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            this.setNodeText(this.domDocument, node, value);
        }
        return true;
    }
    
    public boolean add(final String parent, final String namespaceURI, final String localName, final String value) {
        final NodeList nodes = this.domDocument.getElementsByTagName(parent);
        if (nodes.getLength() == 0) {
            return false;
        }
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node pNode = nodes.item(i);
            final NamedNodeMap attrs = pNode.getAttributes();
            for (int j = 0; j < attrs.getLength(); ++j) {
                Node node = attrs.item(j);
                if (namespaceURI.equals(node.getNodeValue())) {
                    node = this.domDocument.createElement(localName);
                    node.appendChild(this.domDocument.createTextNode(value));
                    pNode.appendChild(node);
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean setNodeText(final Document domDocument, final Node n, final String value) {
        if (n == null) {
            return false;
        }
        Node nc = null;
        while ((nc = n.getFirstChild()) != null) {
            n.removeChild(nc);
        }
        n.appendChild(domDocument.createTextNode(value));
        return true;
    }
    
    public byte[] serializeDoc() throws IOException {
        final XmlDomWriter xw = new XmlDomWriter();
        final ByteArrayOutputStream fout = new ByteArrayOutputStream();
        xw.setOutput(fout, null);
        fout.write("<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n".getBytes(StandardCharsets.UTF_8));
        fout.flush();
        final NodeList xmpmeta = this.domDocument.getElementsByTagName("x:xmpmeta");
        xw.write(xmpmeta.item(0));
        fout.flush();
        for (int i = 0; i < 20; ++i) {
            fout.write("                                                                                                   \n".getBytes());
        }
        fout.write("<?xpacket end=\"w\"?>".getBytes());
        fout.close();
        return fout.toByteArray();
    }
}
