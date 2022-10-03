package org.apache.axiom.attachments;

import org.apache.commons.logging.LogFactory;
import java.io.OutputStream;
import org.apache.axiom.blob.OverflowableBlob;
import javax.activation.DataSource;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.james.mime4j.MimeException;
import java.io.IOException;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.om.OMException;
import java.io.InputStream;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.axiom.mime.Header;
import org.apache.axiom.om.util.DetachableInputStream;
import javax.activation.DataHandler;
import org.apache.axiom.blob.WritableBlob;
import org.apache.james.mime4j.stream.MimeTokenStream;
import java.util.List;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.commons.logging.Log;

final class PartImpl implements Part
{
    private static final int STATE_UNREAD = 0;
    private static final int STATE_BUFFERED = 1;
    private static final int STATE_STREAMING = 2;
    private static final int STATE_DISCARDED = 3;
    private static final Log log;
    private final WritableBlobFactory blobFactory;
    private List headers;
    private int state;
    private MimeTokenStream parser;
    private WritableBlob content;
    private final DataHandler dataHandler;
    private DetachableInputStream detachableInputStream;
    
    PartImpl(final WritableBlobFactory blobFactory, final List headers, final MimeTokenStream parser) {
        this.state = 0;
        this.blobFactory = blobFactory;
        this.headers = headers;
        this.parser = parser;
        this.dataHandler = new PartDataHandler(this);
    }
    
    public String getHeader(final String name) {
        String value = null;
        for (int i = 0, l = this.headers.size(); i < l; ++i) {
            final Header header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                value = header.getValue();
                break;
            }
        }
        if (PartImpl.log.isDebugEnabled()) {
            PartImpl.log.debug((Object)("getHeader name=(" + name + ") value=(" + value + ")"));
        }
        return value;
    }
    
    public String getContentID() {
        return this.getHeader("content-id");
    }
    
    public String getContentType() {
        return this.getHeader("content-type");
    }
    
    String getDataSourceContentType() {
        final String ct = this.getContentType();
        return (ct == null) ? "application/octet-stream" : ct;
    }
    
    public DataHandler getDataHandler() {
        return this.dataHandler;
    }
    
    public long getSize() {
        return this.getContent().getSize();
    }
    
    private WritableBlob getContent() {
        switch (this.state) {
            case 0: {
                this.fetch();
            }
            case 1: {
                return this.content;
            }
            default: {
                throw new IllegalStateException("The content of the MIME part has already been consumed");
            }
        }
    }
    
    private static void checkParserState(final EntityState state, final EntityState expected) throws IllegalStateException {
        if (expected != state) {
            throw new IllegalStateException("Internal error: expected parser to be in state " + expected + ", but got " + state);
        }
    }
    
    private InputStream getDecodedInputStream() {
        InputStream in;
        if ("quoted-printable".equalsIgnoreCase(this.getHeader("Content-Transfer-Encoding"))) {
            in = new QuotedPrintableInputStream(this.parser.getInputStream(), true);
        }
        else {
            in = this.parser.getDecodedInputStream();
        }
        if (PartImpl.log.isDebugEnabled()) {
            in = new DebugInputStream(in, PartImpl.log);
        }
        return in;
    }
    
    void fetch() {
        switch (this.state) {
            case 0: {
                checkParserState(this.parser.getState(), EntityState.T_BODY);
                this.content = this.blobFactory.createBlob();
                if (PartImpl.log.isDebugEnabled()) {
                    PartImpl.log.debug((Object)("Using blob of type " + this.content.getClass().getName()));
                }
                try {
                    this.content.readFrom(this.getDecodedInputStream());
                }
                catch (final StreamCopyException ex) {
                    if (ex.getOperation() == 1) {
                        throw new OMException("Failed to fetch the MIME part content", ex.getCause());
                    }
                    throw new OMException("Failed to write the MIME part content to temporary storage", ex.getCause());
                }
                this.moveToNextPart();
                this.state = 1;
                break;
            }
            case 2: {
                try {
                    this.detachableInputStream.detach();
                }
                catch (final IOException ex2) {
                    throw new OMException(ex2);
                }
                this.detachableInputStream = null;
                this.moveToNextPart();
                this.state = 3;
                break;
            }
        }
    }
    
    private void moveToNextPart() {
        try {
            checkParserState(this.parser.next(), EntityState.T_END_BODYPART);
            final EntityState state = this.parser.next();
            if (state == EntityState.T_EPILOGUE) {
                while (this.parser.next() != EntityState.T_END_MULTIPART) {}
            }
            else if (state != EntityState.T_START_BODYPART && state != EntityState.T_END_MULTIPART) {
                throw new IllegalStateException("Internal error: unexpected parser state " + state);
            }
        }
        catch (final IOException ex) {
            throw new OMException(ex);
        }
        catch (final MimeException ex2) {
            throw new OMException((Throwable)ex2);
        }
        this.parser = null;
    }
    
    InputStream getInputStream(final boolean preserve) throws IOException {
        if (!preserve && this.state == 0) {
            checkParserState(this.parser.getState(), EntityState.T_BODY);
            this.state = 2;
            return this.detachableInputStream = new DetachableInputStream(this.getDecodedInputStream());
        }
        final WritableBlob content = this.getContent();
        if (preserve) {
            return content.getInputStream();
        }
        if (content instanceof MemoryBlob) {
            return ((MemoryBlob)content).readOnce();
        }
        return new ReadOnceInputStreamWrapper(this, content.getInputStream());
    }
    
    DataSource getDataSource() {
        WritableBlob blob = this.getContent();
        if (blob instanceof OverflowableBlob) {
            final WritableBlob overflowBlob = ((OverflowableBlob)blob).getOverflowBlob();
            if (overflowBlob != null) {
                blob = overflowBlob;
            }
        }
        if (blob instanceof LegacyTempFileBlob) {
            return ((LegacyTempFileBlob)blob).getDataSource(this.getDataSourceContentType());
        }
        return null;
    }
    
    void writeTo(final OutputStream out) throws IOException {
        this.getContent().writeTo(out);
    }
    
    void releaseContent() throws IOException {
        switch (this.state) {
            case 0: {
                Label_0032: {
                    break Label_0032;
                    try {
                        EntityState state;
                        do {
                            state = this.parser.next();
                        } while (state != EntityState.T_START_BODYPART && state != EntityState.T_END_MULTIPART);
                    }
                    catch (final MimeException ex) {
                        throw new OMException((Throwable)ex);
                    }
                }
                this.state = 3;
                break;
            }
            case 1: {
                this.content.release();
                break;
            }
        }
    }
    
    static {
        log = LogFactory.getLog((Class)PartImpl.class);
    }
}
