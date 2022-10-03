package org.apache.tika.detect;

import org.xml.sax.InputSource;
import java.io.BufferedInputStream;
import java.util.Iterator;
import org.apache.tika.utils.CharsetUtils;
import org.apache.tika.mime.MediaType;
import java.util.Collections;
import org.apache.tika.exception.TikaException;
import org.apache.tika.config.LoadErrorHandler;
import java.util.List;
import org.apache.tika.metadata.Metadata;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.tika.config.ServiceLoader;
import java.io.BufferedReader;

public class AutoDetectReader extends BufferedReader
{
    private static final ServiceLoader DEFAULT_LOADER;
    private static final EncodingDetector DEFAULT_DETECTOR;
    private final Charset charset;
    
    private AutoDetectReader(final InputStream stream, final Charset charset) throws IOException {
        super(new InputStreamReader(stream, charset));
        this.charset = charset;
        this.mark(1);
        if (this.read() != 65279) {
            this.reset();
        }
    }
    
    private AutoDetectReader(final InputStream stream, final Metadata metadata, final List<EncodingDetector> detectors, final LoadErrorHandler handler) throws IOException, TikaException {
        this(stream, detect(stream, metadata, detectors, handler));
    }
    
    public AutoDetectReader(final InputStream stream, final Metadata metadata, final EncodingDetector encodingDetector) throws IOException, TikaException {
        this(getBuffered(stream), metadata, Collections.singletonList(encodingDetector), AutoDetectReader.DEFAULT_LOADER.getLoadErrorHandler());
    }
    
    public AutoDetectReader(final InputStream stream, final Metadata metadata, final ServiceLoader loader) throws IOException, TikaException {
        this(getBuffered(stream), metadata, loader.loadServiceProviders(EncodingDetector.class), loader.getLoadErrorHandler());
    }
    
    public AutoDetectReader(final InputStream stream, final Metadata metadata) throws IOException, TikaException {
        this(stream, metadata, AutoDetectReader.DEFAULT_DETECTOR);
    }
    
    public AutoDetectReader(final InputStream stream) throws IOException, TikaException {
        this(stream, new Metadata());
    }
    
    private static Charset detect(final InputStream input, final Metadata metadata, final List<EncodingDetector> detectors, final LoadErrorHandler handler) throws IOException, TikaException {
        for (final EncodingDetector detector : detectors) {
            try {
                final Charset charset = detector.detect(input, metadata);
                if (charset != null) {
                    return charset;
                }
                continue;
            }
            catch (final NoClassDefFoundError e) {
                handler.handleLoadError(detector.getClass().getName(), e);
            }
        }
        final MediaType type = MediaType.parse(metadata.get("Content-Type"));
        if (type != null) {
            final String charset2 = type.getParameters().get("charset");
            if (charset2 != null) {
                try {
                    return CharsetUtils.forName(charset2);
                }
                catch (final IllegalArgumentException ex) {}
            }
        }
        throw new TikaException("Failed to detect the character encoding of a document");
    }
    
    private static InputStream getBuffered(final InputStream stream) {
        if (stream.markSupported()) {
            return stream;
        }
        return new BufferedInputStream(stream);
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public InputSource asInputSource() {
        final InputSource source = new InputSource(this);
        source.setEncoding(this.charset.name());
        return source;
    }
    
    static {
        DEFAULT_LOADER = new ServiceLoader(AutoDetectReader.class.getClassLoader());
        DEFAULT_DETECTOR = new CompositeEncodingDetector(AutoDetectReader.DEFAULT_LOADER.loadServiceProviders(EncodingDetector.class));
    }
}
