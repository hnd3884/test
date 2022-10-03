package org.apache.tika.extractor;

import org.apache.tika.parser.DelegatingParser;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.CorruptedFileException;
import java.io.IOException;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.io.TikaInputStream;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.tika.io.TemporaryResources;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import java.io.FilenameFilter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import java.io.File;

public class ParsingEmbeddedDocumentExtractor implements EmbeddedDocumentExtractor
{
    private static final File ABSTRACT_PATH;
    private static final Parser DELEGATING_PARSER;
    private final ParseContext context;
    
    public ParsingEmbeddedDocumentExtractor(final ParseContext context) {
        this.context = context;
    }
    
    @Override
    public boolean shouldParseEmbedded(final Metadata metadata) {
        final DocumentSelector selector = this.context.get(DocumentSelector.class);
        if (selector != null) {
            return selector.select(metadata);
        }
        final FilenameFilter filter = this.context.get(FilenameFilter.class);
        if (filter != null) {
            final String name = metadata.get("resourceName");
            if (name != null) {
                return filter.accept(ParsingEmbeddedDocumentExtractor.ABSTRACT_PATH, name);
            }
        }
        return true;
    }
    
    @Override
    public void parseEmbedded(final InputStream stream, final ContentHandler handler, final Metadata metadata, final boolean outputHtml) throws SAXException, IOException {
        if (outputHtml) {
            final AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "class", "class", "CDATA", "package-entry");
            handler.startElement("http://www.w3.org/1999/xhtml", "div", "div", attributes);
        }
        final String name = metadata.get("resourceName");
        if (name != null && name.length() > 0 && outputHtml) {
            handler.startElement("http://www.w3.org/1999/xhtml", "h1", "h1", new AttributesImpl());
            final char[] chars = name.toCharArray();
            handler.characters(chars, 0, chars.length);
            handler.endElement("http://www.w3.org/1999/xhtml", "h1", "h1");
        }
        try (final TemporaryResources tmp = new TemporaryResources()) {
            final TikaInputStream newStream = TikaInputStream.get((InputStream)new CloseShieldInputStream(stream), tmp);
            if (stream instanceof TikaInputStream) {
                final Object container = ((TikaInputStream)stream).getOpenContainer();
                if (container != null) {
                    newStream.setOpenContainer(container);
                }
            }
            ParsingEmbeddedDocumentExtractor.DELEGATING_PARSER.parse((InputStream)newStream, new EmbeddedContentHandler(new BodyContentHandler(handler)), metadata, this.context);
        }
        catch (final EncryptedDocumentException ex) {}
        catch (final CorruptedFileException e) {
            throw new IOException(e);
        }
        catch (final TikaException ex2) {}
        if (outputHtml) {
            handler.endElement("http://www.w3.org/1999/xhtml", "div", "div");
        }
    }
    
    public Parser getDelegatingParser() {
        return ParsingEmbeddedDocumentExtractor.DELEGATING_PARSER;
    }
    
    static {
        ABSTRACT_PATH = new File("");
        DELEGATING_PARSER = new DelegatingParser();
    }
}
