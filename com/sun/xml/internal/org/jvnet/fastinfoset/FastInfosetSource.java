package com.sun.xml.internal.org.jvnet.fastinfoset;

import com.sun.xml.internal.fastinfoset.sax.SAXDocumentParser;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import java.io.InputStream;
import javax.xml.transform.sax.SAXSource;

public class FastInfosetSource extends SAXSource
{
    public FastInfosetSource(final InputStream inputStream) {
        super(new InputSource(inputStream));
    }
    
    @Override
    public XMLReader getXMLReader() {
        XMLReader reader = super.getXMLReader();
        if (reader == null) {
            reader = new SAXDocumentParser();
            this.setXMLReader(reader);
        }
        ((SAXDocumentParser)reader).setInputStream(this.getInputStream());
        return reader;
    }
    
    public InputStream getInputStream() {
        return this.getInputSource().getByteStream();
    }
    
    public void setInputStream(final InputStream inputStream) {
        this.setInputSource(new InputSource(inputStream));
    }
}
