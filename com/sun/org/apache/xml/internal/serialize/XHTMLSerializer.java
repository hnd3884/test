package com.sun.org.apache.xml.internal.serialize;

import java.io.OutputStream;
import java.io.Writer;

public class XHTMLSerializer extends HTMLSerializer
{
    public XHTMLSerializer() {
        super(true, new OutputFormat("xhtml", null, false));
    }
    
    public XHTMLSerializer(final OutputFormat format) {
        super(true, (format != null) ? format : new OutputFormat("xhtml", null, false));
    }
    
    public XHTMLSerializer(final Writer writer, final OutputFormat format) {
        super(true, (format != null) ? format : new OutputFormat("xhtml", null, false));
        this.setOutputCharStream(writer);
    }
    
    public XHTMLSerializer(final OutputStream output, final OutputFormat format) {
        super(true, (format != null) ? format : new OutputFormat("xhtml", null, false));
        this.setOutputByteStream(output);
    }
    
    @Override
    public void setOutputFormat(final OutputFormat format) {
        super.setOutputFormat((format != null) ? format : new OutputFormat("xhtml", null, false));
    }
}
