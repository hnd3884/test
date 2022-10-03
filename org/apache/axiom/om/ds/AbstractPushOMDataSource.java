package org.apache.axiom.om.ds;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import org.apache.axiom.om.util.StAXUtils;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import org.apache.axiom.om.OMOutputFormat;
import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLStreamReader;

public abstract class AbstractPushOMDataSource extends AbstractOMDataSource
{
    public final boolean isDestructiveRead() {
        return this.isDestructiveWrite();
    }
    
    public final XMLStreamReader getReader() throws XMLStreamException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.serialize(bos, new OMOutputFormat());
        return StAXUtils.createXMLStreamReader(new ByteArrayInputStream(bos.toByteArray()));
    }
}
