package org.apache.tika.parser;

import org.xml.sax.Attributes;
import org.apache.commons.io.IOUtils;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.tika.utils.XMLReaderUtils;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.TaggedContentHandler;
import org.apache.tika.sax.OfflineContentHandler;
import java.net.URLConnection;
import java.net.URL;
import org.apache.commons.io.input.CloseShieldInputStream;
import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.net.Socket;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import java.util.Collections;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import java.net.URI;

public class NetworkParser extends AbstractParser
{
    private final URI uri;
    private final Set<MediaType> supportedTypes;
    
    public NetworkParser(final URI uri, final Set<MediaType> supportedTypes) {
        this.uri = uri;
        this.supportedTypes = supportedTypes;
    }
    
    public NetworkParser(final URI uri) {
        this(uri, Collections.singleton(MediaType.OCTET_STREAM));
    }
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return this.supportedTypes;
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        final TemporaryResources tmp = new TemporaryResources();
        try {
            final TikaInputStream tis = TikaInputStream.get(stream, tmp);
            this.parse(tis, handler, metadata, context);
        }
        finally {
            tmp.dispose();
        }
    }
    
    private void parse(final TikaInputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        if ("telnet".equals(this.uri.getScheme())) {
            try (final Socket socket = new Socket(this.uri.getHost(), this.uri.getPort())) {
                new ParsingTask(stream, new FilterOutputStream(socket.getOutputStream()) {
                    @Override
                    public void close() throws IOException {
                        socket.shutdownOutput();
                    }
                }).parse(socket.getInputStream(), handler, metadata, context);
            }
        }
        else {
            final URL url = this.uri.toURL();
            final URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();
            try (final InputStream input = connection.getInputStream()) {
                new ParsingTask(stream, connection.getOutputStream()).parse((InputStream)new CloseShieldInputStream(input), handler, metadata, context);
            }
        }
    }
    
    private static class ParsingTask implements Runnable
    {
        private final TikaInputStream input;
        private final OutputStream output;
        private volatile Exception exception;
        
        public ParsingTask(final TikaInputStream input, final OutputStream output) {
            this.exception = null;
            this.input = input;
            this.output = output;
        }
        
        public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
            final Thread thread = new Thread(this, "Tika network parser");
            thread.start();
            final TaggedContentHandler tagged = new TaggedContentHandler(new OfflineContentHandler(handler));
            try {
                XMLReaderUtils.parseSAX(stream, new TeeContentHandler(new ContentHandler[] { tagged, new MetaHandler(metadata) }), context);
                try {
                    thread.join(1000L);
                }
                catch (final InterruptedException e) {
                    throw new TikaException("Network parser interrupted", e);
                }
                if (this.exception != null) {
                    this.input.throwIfCauseOf((Throwable)this.exception);
                    throw new TikaException("Unexpected network parser error", this.exception);
                }
            }
            catch (final SAXException e2) {
                tagged.throwIfCauseOf(e2);
                throw new TikaException("Invalid network parser output", e2);
            }
            catch (final IOException e3) {
                throw new TikaException("Unable to read network parser output", e3);
            }
            finally {
                try {
                    thread.join(1000L);
                }
                catch (final InterruptedException e4) {
                    throw new TikaException("Network parser interrupted", e4);
                }
                if (this.exception != null) {
                    this.input.throwIfCauseOf((Throwable)this.exception);
                    throw new TikaException("Unexpected network parser error", this.exception);
                }
            }
        }
        
        @Override
        public void run() {
            try {
                try {
                    IOUtils.copy((InputStream)this.input, this.output);
                }
                finally {
                    this.output.close();
                }
            }
            catch (final Exception e) {
                this.exception = e;
            }
        }
    }
    
    private static class MetaHandler extends DefaultHandler
    {
        private final Metadata metadata;
        
        public MetaHandler(final Metadata metadata) {
            this.metadata = metadata;
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            if ("http://www.w3.org/1999/xhtml".equals(uri) && "meta".equals(localName)) {
                final String name = attributes.getValue("", "name");
                final String content = attributes.getValue("", "content");
                if (name != null && content != null) {
                    this.metadata.add(name, content);
                }
            }
        }
    }
}
