package org.apache.tika;

import java.util.Properties;
import org.xml.sax.SAXException;
import org.apache.tika.exception.WriteLimitReachedException;
import org.xml.sax.ContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ParsingReader;
import org.apache.tika.parser.ParseContext;
import java.io.Reader;
import java.net.URL;
import java.io.File;
import java.nio.file.Path;
import org.apache.tika.io.TikaInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.language.translate.Translator;
import org.apache.tika.parser.Parser;
import org.apache.tika.detect.Detector;

public class Tika
{
    private final Detector detector;
    private final Parser parser;
    private final Translator translator;
    private int maxStringLength;
    
    public Tika(final Detector detector, final Parser parser) {
        this.maxStringLength = 100000;
        this.detector = detector;
        this.parser = parser;
        this.translator = TikaConfig.getDefaultConfig().getTranslator();
    }
    
    public Tika(final Detector detector, final Parser parser, final Translator translator) {
        this.maxStringLength = 100000;
        this.detector = detector;
        this.parser = parser;
        this.translator = translator;
    }
    
    public Tika(final TikaConfig config) {
        this(config.getDetector(), new AutoDetectParser(config), config.getTranslator());
    }
    
    public Tika() {
        this(TikaConfig.getDefaultConfig());
    }
    
    public Tika(final Detector detector) {
        this(detector, new AutoDetectParser(detector));
    }
    
    public String detect(final InputStream stream, final Metadata metadata) throws IOException {
        if (stream == null || stream.markSupported()) {
            return this.detector.detect(stream, metadata).toString();
        }
        return this.detector.detect(new BufferedInputStream(stream), metadata).toString();
    }
    
    public String detect(final InputStream stream, final String name) throws IOException {
        final Metadata metadata = new Metadata();
        metadata.set("resourceName", name);
        return this.detect(stream, metadata);
    }
    
    public String detect(final InputStream stream) throws IOException {
        return this.detect(stream, new Metadata());
    }
    
    public String detect(final byte[] prefix, final String name) {
        try (final InputStream stream = (InputStream)TikaInputStream.get(prefix)) {
            return this.detect(stream, name);
        }
        catch (final IOException e) {
            throw new IllegalStateException("Unexpected IOException", e);
        }
    }
    
    public String detect(final byte[] prefix) {
        try (final InputStream stream = (InputStream)TikaInputStream.get(prefix)) {
            return this.detect(stream);
        }
        catch (final IOException e) {
            throw new IllegalStateException("Unexpected IOException", e);
        }
    }
    
    public String detect(final Path path) throws IOException {
        final Metadata metadata = new Metadata();
        try (final InputStream stream = (InputStream)TikaInputStream.get(path, metadata)) {
            return this.detect(stream, metadata);
        }
    }
    
    public String detect(final File file) throws IOException {
        final Metadata metadata = new Metadata();
        try (final InputStream stream = (InputStream)TikaInputStream.get(file, metadata)) {
            return this.detect(stream, metadata);
        }
    }
    
    public String detect(final URL url) throws IOException {
        final Metadata metadata = new Metadata();
        try (final InputStream stream = (InputStream)TikaInputStream.get(url, metadata)) {
            return this.detect(stream, metadata);
        }
    }
    
    public String detect(final String name) {
        try {
            return this.detect((InputStream)null, name);
        }
        catch (final IOException e) {
            throw new IllegalStateException("Unexpected IOException", e);
        }
    }
    
    public String translate(final String text, final String sourceLanguage, final String targetLanguage) {
        try {
            return this.translator.translate(text, sourceLanguage, targetLanguage);
        }
        catch (final Exception e) {
            throw new IllegalStateException("Error translating data.", e);
        }
    }
    
    public String translate(final String text, final String targetLanguage) {
        try {
            return this.translator.translate(text, targetLanguage);
        }
        catch (final Exception e) {
            throw new IllegalStateException("Error translating data.", e);
        }
    }
    
    public Reader parse(final InputStream stream, final Metadata metadata) throws IOException {
        final ParseContext context = new ParseContext();
        context.set(Parser.class, this.parser);
        return new ParsingReader(this.parser, stream, metadata, context);
    }
    
    public Reader parse(final InputStream stream) throws IOException {
        return this.parse(stream, new Metadata());
    }
    
    public Reader parse(final Path path, final Metadata metadata) throws IOException {
        final InputStream stream = (InputStream)TikaInputStream.get(path, metadata);
        return this.parse(stream, metadata);
    }
    
    public Reader parse(final Path path) throws IOException {
        return this.parse(path, new Metadata());
    }
    
    public Reader parse(final File file, final Metadata metadata) throws IOException {
        final InputStream stream = (InputStream)TikaInputStream.get(file, metadata);
        return this.parse(stream, metadata);
    }
    
    public Reader parse(final File file) throws IOException {
        return this.parse(file, new Metadata());
    }
    
    public Reader parse(final URL url) throws IOException {
        final Metadata metadata = new Metadata();
        final InputStream stream = (InputStream)TikaInputStream.get(url, metadata);
        return this.parse(stream, metadata);
    }
    
    public String parseToString(final InputStream stream, final Metadata metadata) throws IOException, TikaException {
        return this.parseToString(stream, metadata, this.maxStringLength);
    }
    
    public String parseToString(final InputStream stream, final Metadata metadata, final int maxLength) throws IOException, TikaException {
        final WriteOutContentHandler handler = new WriteOutContentHandler(maxLength);
        try {
            final ParseContext context = new ParseContext();
            context.set(Parser.class, this.parser);
            this.parser.parse(stream, new BodyContentHandler(handler), metadata, context);
        }
        catch (final SAXException e) {
            if (!WriteLimitReachedException.isWriteLimitReached(e)) {
                throw new TikaException("Unexpected SAX processing failure", e);
            }
        }
        finally {
            stream.close();
        }
        return handler.toString();
    }
    
    public String parseToString(final InputStream stream) throws IOException, TikaException {
        return this.parseToString(stream, new Metadata());
    }
    
    public String parseToString(final Path path) throws IOException, TikaException {
        final Metadata metadata = new Metadata();
        final InputStream stream = (InputStream)TikaInputStream.get(path, metadata);
        return this.parseToString(stream, metadata);
    }
    
    public String parseToString(final File file) throws IOException, TikaException {
        final Metadata metadata = new Metadata();
        final InputStream stream = (InputStream)TikaInputStream.get(file, metadata);
        return this.parseToString(stream, metadata);
    }
    
    public String parseToString(final URL url) throws IOException, TikaException {
        final Metadata metadata = new Metadata();
        final InputStream stream = (InputStream)TikaInputStream.get(url, metadata);
        return this.parseToString(stream, metadata);
    }
    
    public int getMaxStringLength() {
        return this.maxStringLength;
    }
    
    public void setMaxStringLength(final int maxStringLength) {
        this.maxStringLength = maxStringLength;
    }
    
    public Parser getParser() {
        return this.parser;
    }
    
    public Detector getDetector() {
        return this.detector;
    }
    
    public Translator getTranslator() {
        return this.translator;
    }
    
    @Override
    public String toString() {
        String version = null;
        try (final InputStream stream = Tika.class.getResourceAsStream("/META-INF/maven/org.apache.tika/tika-core/pom.properties")) {
            if (stream != null) {
                final Properties properties = new Properties();
                properties.load(stream);
                version = properties.getProperty("version");
            }
        }
        catch (final Exception ex) {}
        if (version != null) {
            return "Apache Tika " + version;
        }
        return "Apache Tika";
    }
}
