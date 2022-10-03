package org.apache.axiom.om.ds.custombuilder;

import org.apache.axiom.om.OMNamespace;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import java.io.OutputStream;
import org.apache.axiom.om.util.StAXUtils;
import java.io.ByteArrayOutputStream;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.impl.builder.CustomBuilder;

public class ByteArrayCustomBuilder implements CustomBuilder
{
    private String encoding;
    
    public ByteArrayCustomBuilder(final String encoding) {
        this.encoding = null;
        this.encoding = ((encoding == null) ? "utf-8" : encoding);
    }
    
    public OMElement create(final String namespace, final String localPart, final OMContainer parent, final XMLStreamReader reader, final OMFactory factory) throws OMException {
        try {
            String prefix = reader.getPrefix();
            if (prefix == null) {
                prefix = "";
            }
            final StreamingOMSerializer ser = new StreamingOMSerializer();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(baos, this.encoding);
            ser.serialize(reader, writer, false);
            writer.flush();
            final byte[] bytes = baos.toByteArray();
            final String text = new String(bytes, "utf-8");
            final ByteArrayDataSource ds = new ByteArrayDataSource(bytes, this.encoding);
            final OMNamespace ns = factory.createOMNamespace(namespace, prefix);
            OMElement om = null;
            if (parent instanceof SOAPHeader && factory instanceof SOAPFactory) {
                om = ((SOAPFactory)factory).createSOAPHeaderBlock(localPart, ns, ds);
            }
            else {
                om = factory.createOMElement(ds, localPart, ns);
            }
            parent.addChild(om);
            return om;
        }
        catch (final XMLStreamException e) {
            throw new OMException(e);
        }
        catch (final OMException e2) {
            throw e2;
        }
        catch (final Throwable t) {
            throw new OMException(t);
        }
    }
}
