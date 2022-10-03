package org.apache.axiom.om.impl.common.serializer.pull;

import org.apache.axiom.om.OMDataSource;
import java.io.IOException;
import org.apache.axiom.om.OMException;
import javax.activation.DataHandler;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.logging.LogFactory;
import org.apache.axiom.util.stax.xop.XOPEncodingStreamReader;
import org.apache.commons.logging.Log;
import org.apache.axiom.om.impl.OMXMLStreamReaderEx;
import javax.xml.stream.util.StreamReaderDelegate;

public class OMXMLStreamReaderExAdapter extends StreamReaderDelegate implements OMXMLStreamReaderEx
{
    private static final Log log;
    private final PullSerializer serializer;
    private XOPEncodingStreamReader xopEncoder;
    
    static {
        log = LogFactory.getLog((Class)OMXMLStreamReaderExAdapter.class);
    }
    
    public OMXMLStreamReaderExAdapter(final PullSerializer serializer) {
        super((XMLStreamReader)serializer);
        this.serializer = serializer;
    }
    
    public boolean isInlineMTOM() {
        return this.xopEncoder == null;
    }
    
    public void setInlineMTOM(final boolean value) {
        if (value) {
            if (this.xopEncoder != null) {
                this.xopEncoder = null;
                this.setParent((XMLStreamReader)this.serializer);
            }
        }
        else if (this.xopEncoder == null) {
            this.setParent((XMLStreamReader)(this.xopEncoder = new XOPEncodingStreamReader((XMLStreamReader)this.serializer, ContentIDGenerator.DEFAULT, OptimizationPolicy.ALL)));
        }
    }
    
    public DataHandler getDataHandler(String contentID) {
        if (contentID.startsWith("cid:")) {
            OMXMLStreamReaderExAdapter.log.warn((Object)"Invalid usage of OMStAXWrapper#getDataHandler(String): the argument must be a content ID, not an href; see OMAttachmentAccessor.");
            contentID = contentID.substring(4);
        }
        if (this.xopEncoder == null) {
            throw new IllegalStateException("The wrapper is in inlineMTOM=true mode");
        }
        if (this.xopEncoder.getContentIDs().contains(contentID)) {
            try {
                return this.xopEncoder.getDataHandler(contentID);
            }
            catch (final IOException ex) {
                throw new OMException((Throwable)ex);
            }
        }
        return null;
    }
    
    public OMDataSource getDataSource() {
        return this.serializer.getDataSource();
    }
    
    public void enableDataSourceEvents(final boolean value) {
        this.serializer.enableDataSourceEvents(value);
    }
}
