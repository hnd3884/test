package org.apache.axiom.attachments;

import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.Field;
import java.util.ArrayList;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.axiom.om.OMException;
import org.apache.james.mime4j.stream.MimeTokenStream;

final class MultipartAttachmentStreams extends IncomingAttachmentStreams
{
    private final MimeTokenStream parser;
    
    public MultipartAttachmentStreams(final MimeTokenStream parser) throws OMException {
        this.parser = parser;
    }
    
    @Override
    public IncomingAttachmentInputStream getNextStream() throws OMException {
        if (!this.isReadyToGetNextStream()) {
            throw new IllegalStateException("nextStreamNotReady");
        }
        IncomingAttachmentInputStream stream;
        try {
            if (this.parser.getState() == EntityState.T_BODY) {
                if (this.parser.next() != EntityState.T_END_BODYPART) {
                    throw new IllegalStateException();
                }
                this.parser.next();
            }
            if (this.parser.getState() != EntityState.T_START_BODYPART) {
                return null;
            }
            if (this.parser.next() != EntityState.T_START_HEADER) {
                throw new IllegalStateException();
            }
            final List fields = new ArrayList();
            while (this.parser.next() == EntityState.T_FIELD) {
                fields.add(this.parser.getField());
            }
            if (this.parser.next() != EntityState.T_BODY) {
                throw new IllegalStateException();
            }
            stream = new IncomingAttachmentInputStream(this.parser.getInputStream(), this);
            for (final Field field : fields) {
                final String name = field.getName();
                String value = field.getBody();
                if ("content-id".equals(name) || "content-type".equals(name) || "content-location".equals(name)) {
                    value = value.trim();
                }
                stream.addHeader(name, value);
            }
        }
        catch (final MimeException ex) {
            throw new OMException((Throwable)ex);
        }
        catch (final IOException ex2) {
            throw new OMException(ex2);
        }
        this.setReadyToGetNextStream(false);
        return stream;
    }
}
