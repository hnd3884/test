package com.sun.xml.internal.ws.streaming;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.ws.WebServiceException;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;
import java.io.Closeable;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderFilter;

public class TidyXMLStreamReader extends XMLStreamReaderFilter
{
    private final Closeable closeableSource;
    
    public TidyXMLStreamReader(@NotNull final XMLStreamReader reader, @Nullable final Closeable closeableSource) {
        super(reader);
        this.closeableSource = closeableSource;
    }
    
    @Override
    public void close() throws XMLStreamException {
        super.close();
        try {
            if (this.closeableSource != null) {
                this.closeableSource.close();
            }
        }
        catch (final IOException e) {
            throw new WebServiceException(e);
        }
    }
}
