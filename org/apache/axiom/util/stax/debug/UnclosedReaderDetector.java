package org.apache.axiom.util.stax.debug;

import javax.xml.stream.XMLStreamException;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;
import org.apache.commons.logging.LogFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import org.apache.commons.logging.Log;
import org.apache.axiom.util.stax.wrapper.WrappingXMLInputFactory;

public class UnclosedReaderDetector extends WrappingXMLInputFactory
{
    static final Log log;
    
    public UnclosedReaderDetector(final XMLInputFactory parent) {
        super(parent);
    }
    
    @Override
    protected XMLStreamReader wrap(final XMLStreamReader reader) {
        return new StreamReaderWrapper(reader);
    }
    
    static {
        log = LogFactory.getLog((Class)UnclosedReaderDetector.class);
    }
    
    private static class StreamReaderWrapper extends XMLStreamReaderWrapper
    {
        private final Throwable stackTrace;
        private boolean isClosed;
        
        public StreamReaderWrapper(final XMLStreamReader parent) {
            super(parent);
            this.stackTrace = new Throwable();
        }
        
        @Override
        public void close() throws XMLStreamException {
            super.close();
            this.isClosed = true;
        }
        
        @Override
        protected void finalize() throws Throwable {
            if (!this.isClosed) {
                UnclosedReaderDetector.log.warn((Object)"Detected unclosed XMLStreamReader.", this.stackTrace);
            }
        }
    }
}
