package org.apache.xml.serialize;

import java.io.OutputStream;
import java.io.Writer;

public class XHTMLSerializer extends HTMLSerializer
{
    public XHTMLSerializer() {
        super(true, new OutputFormat("xhtml", null, false));
    }
    
    public XHTMLSerializer(final OutputFormat outputFormat) {
        super(true, (outputFormat != null) ? outputFormat : new OutputFormat("xhtml", null, false));
    }
    
    public XHTMLSerializer(final Writer outputCharStream, final OutputFormat outputFormat) {
        super(true, (outputFormat != null) ? outputFormat : new OutputFormat("xhtml", null, false));
        this.setOutputCharStream(outputCharStream);
    }
    
    public XHTMLSerializer(final OutputStream outputByteStream, final OutputFormat outputFormat) {
        super(true, (outputFormat != null) ? outputFormat : new OutputFormat("xhtml", null, false));
        this.setOutputByteStream(outputByteStream);
    }
    
    public void setOutputFormat(final OutputFormat outputFormat) {
        super.setOutputFormat((outputFormat != null) ? outputFormat : new OutputFormat("xhtml", null, false));
    }
}
