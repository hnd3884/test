package org.apache.tika.mime;

import java.net.URISyntaxException;
import java.net.URI;
import java.util.Collection;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import java.io.ByteArrayInputStream;
import org.apache.tika.detect.TextDetector;
import org.apache.tika.detect.XmlRootExtractor;
import org.apache.tika.Tika;
import java.io.File;
import java.util.Locale;
import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import org.apache.tika.detect.Detector;

public final class MimeTypes implements Detector, Serializable
{
    public static final String OCTET_STREAM = "application/octet-stream";
    public static final String PLAIN_TEXT = "text/plain";
    public static final String XML = "application/xml";
    private static final long serialVersionUID = -1350863170146349036L;
    private static final Map<ClassLoader, MimeTypes> CLASSLOADER_SPECIFIC_DEFAULT_TYPES;
    private static MimeTypes DEFAULT_TYPES;
    private final MimeType rootMimeType;
    private final List<MimeType> rootMimeTypeL;
    private final MimeType textMimeType;
    private final MimeType htmlMimeType;
    private final MimeType xmlMimeType;
    private final MediaTypeRegistry registry;
    private final Map<MediaType, MimeType> types;
    private final Patterns patterns;
    private final List<Magic> magics;
    private final List<MimeType> xmls;
    
    public MimeTypes() {
        this.registry = new MediaTypeRegistry();
        this.types = new HashMap<MediaType, MimeType>();
        this.patterns = new Patterns(this.registry);
        this.magics = new ArrayList<Magic>();
        this.xmls = new ArrayList<MimeType>();
        this.rootMimeType = new MimeType(MediaType.OCTET_STREAM);
        this.textMimeType = new MimeType(MediaType.TEXT_PLAIN);
        this.htmlMimeType = new MimeType(MediaType.TEXT_HTML);
        this.xmlMimeType = new MimeType(MediaType.APPLICATION_XML);
        this.rootMimeTypeL = Collections.singletonList(this.rootMimeType);
        this.add(this.rootMimeType);
        this.add(this.textMimeType);
        this.add(this.xmlMimeType);
    }
    
    public static synchronized MimeTypes getDefaultMimeTypes() {
        return getDefaultMimeTypes(null);
    }
    
    public static synchronized MimeTypes getDefaultMimeTypes(final ClassLoader classLoader) {
        MimeTypes types = MimeTypes.DEFAULT_TYPES;
        if (classLoader != null) {
            types = MimeTypes.CLASSLOADER_SPECIFIC_DEFAULT_TYPES.get(classLoader);
        }
        if (types == null) {
            try {
                types = MimeTypesFactory.create("tika-mimetypes.xml", "custom-mimetypes.xml", classLoader);
            }
            catch (final MimeTypeException e) {
                throw new RuntimeException("Unable to parse the default media type registry", e);
            }
            catch (final IOException e2) {
                throw new RuntimeException("Unable to read the default media type registry", e2);
            }
            if (classLoader == null) {
                MimeTypes.DEFAULT_TYPES = types;
            }
            else {
                MimeTypes.CLASSLOADER_SPECIFIC_DEFAULT_TYPES.put(classLoader, types);
            }
        }
        return types;
    }
    
    @Deprecated
    public MimeType getMimeType(final String name) {
        MimeType type = this.patterns.matches(name);
        if (type != null) {
            return type;
        }
        type = this.patterns.matches(name.toLowerCase(Locale.ENGLISH));
        if (type != null) {
            return type;
        }
        return this.rootMimeType;
    }
    
    @Deprecated
    public MimeType getMimeType(final File file) throws MimeTypeException, IOException {
        return this.forName(new Tika(this).detect(file));
    }
    
    List<MimeType> getMimeType(final byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data is missing");
        }
        if (data.length == 0) {
            return this.rootMimeTypeL;
        }
        final List<MimeType> result = new ArrayList<MimeType>(1);
        int currentPriority = -1;
        for (final Magic magic : this.magics) {
            if (currentPriority > 0 && currentPriority > magic.getPriority()) {
                break;
            }
            if (!magic.eval(data)) {
                continue;
            }
            result.add(magic.getType());
            currentPriority = magic.getPriority();
        }
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); ++i) {
                final MimeType matched = result.get(i);
                if ("application/xml".equals(matched.getName()) || "text/html".equals(matched.getName())) {
                    final XmlRootExtractor extractor = new XmlRootExtractor();
                    final QName rootElement = extractor.extractRootElement(data);
                    if (rootElement != null) {
                        for (final MimeType type : this.xmls) {
                            if (type.matchesXML(rootElement.getNamespaceURI(), rootElement.getLocalPart())) {
                                result.set(i, type);
                                break;
                            }
                        }
                    }
                    else if ("application/xml".equals(matched.getName())) {
                        boolean isHTML = false;
                        for (final Magic magic2 : this.magics) {
                            if (!magic2.getType().equals(this.htmlMimeType)) {
                                continue;
                            }
                            if (magic2.eval(data)) {
                                isHTML = true;
                                break;
                            }
                        }
                        if (isHTML) {
                            result.set(i, this.htmlMimeType);
                        }
                        else {
                            result.set(i, this.textMimeType);
                        }
                    }
                }
            }
            return result;
        }
        try {
            final TextDetector detector = new TextDetector(this.getMinLength());
            final ByteArrayInputStream stream = new ByteArrayInputStream(data);
            final MimeType type2 = this.forName(detector.detect(stream, new Metadata()).toString());
            return Collections.singletonList(type2);
        }
        catch (final Exception e) {
            return this.rootMimeTypeL;
        }
    }
    
    public Map<MediaType, MimeType> getTypes() {
        return this.types;
    }
    
    byte[] readMagicHeader(final InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("InputStream is missing");
        }
        final byte[] bytes = new byte[this.getMinLength()];
        int totalRead = 0;
        for (int lastRead = stream.read(bytes); lastRead != -1; lastRead = stream.read(bytes, totalRead, bytes.length - totalRead)) {
            totalRead += lastRead;
            if (totalRead == bytes.length) {
                return bytes;
            }
        }
        final byte[] shorter = new byte[totalRead];
        System.arraycopy(bytes, 0, shorter, 0, totalRead);
        return shorter;
    }
    
    public MimeType forName(final String name) throws MimeTypeException {
        final MediaType type = MediaType.parse(name);
        if (type != null) {
            final MediaType normalisedType = this.registry.normalize(type);
            MimeType mime = this.types.get(normalisedType);
            if (mime == null) {
                synchronized (this) {
                    mime = this.types.get(normalisedType);
                    if (mime == null) {
                        mime = new MimeType(type);
                        this.add(mime);
                        this.types.put(type, mime);
                    }
                }
            }
            return mime;
        }
        throw new MimeTypeException("Invalid media type name: " + name);
    }
    
    public MimeType getRegisteredMimeType(final String name) throws MimeTypeException {
        final MediaType type = MediaType.parse(name);
        if (type == null) {
            throw new MimeTypeException("Invalid media type name: " + name);
        }
        final MediaType normalisedType = this.registry.normalize(type);
        final MimeType candidate = this.types.get(normalisedType);
        if (candidate != null) {
            return candidate;
        }
        if (normalisedType.hasParameters()) {
            return this.types.get(normalisedType.getBaseType());
        }
        return null;
    }
    
    public synchronized void setSuperType(final MimeType type, final MediaType parent) {
        this.registry.addSuperType(type.getType(), parent);
    }
    
    synchronized void addAlias(final MimeType type, final MediaType alias) {
        this.registry.addAlias(type.getType(), alias);
    }
    
    public void addPattern(final MimeType type, final String pattern) throws MimeTypeException {
        this.addPattern(type, pattern, false);
    }
    
    public void addPattern(final MimeType type, final String pattern, final boolean isRegex) throws MimeTypeException {
        this.patterns.add(pattern, isRegex, type);
    }
    
    public MediaTypeRegistry getMediaTypeRegistry() {
        return this.registry;
    }
    
    public int getMinLength() {
        return 65536;
    }
    
    void add(final MimeType type) {
        this.registry.addType(type.getType());
        this.types.put(type.getType(), type);
        if (type.hasMagic()) {
            this.magics.addAll(type.getMagics());
        }
        if (type.hasRootXML()) {
            this.xmls.add(type);
        }
    }
    
    void init() {
        for (final MimeType type : this.types.values()) {
            this.magics.addAll(type.getMagics());
            if (type.hasRootXML()) {
                this.xmls.add(type);
            }
        }
        Collections.sort(this.magics);
        Collections.sort(this.xmls);
    }
    
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) throws IOException {
        List<MimeType> possibleTypes = null;
        if (input != null) {
            input.mark(this.getMinLength());
            try {
                final byte[] prefix = this.readMagicHeader(input);
                possibleTypes = this.getMimeType(prefix);
            }
            finally {
                input.reset();
            }
        }
        final String resourceName = metadata.get("resourceName");
        if (resourceName != null) {
            String name = null;
            boolean isHttp = false;
            try {
                final URI uri = new URI(resourceName);
                final String scheme = uri.getScheme();
                isHttp = (scheme != null && scheme.startsWith("http"));
                final String path = uri.getPath();
                if (path != null) {
                    final int slash = path.lastIndexOf(47);
                    if (slash + 1 < path.length()) {
                        name = path.substring(slash + 1);
                    }
                }
            }
            catch (final URISyntaxException e) {
                name = resourceName;
            }
            if (name != null) {
                final MimeType hint = this.getMimeType(name);
                if (!isHttp || !hint.isInterpreted()) {
                    possibleTypes = this.applyHint(possibleTypes, hint);
                }
            }
        }
        final String typeName = metadata.get("Content-Type");
        if (typeName != null) {
            try {
                final MimeType hint2 = this.forName(typeName);
                possibleTypes = this.applyHint(possibleTypes, hint2);
            }
            catch (final MimeTypeException ex) {}
        }
        if (possibleTypes == null || possibleTypes.isEmpty()) {
            return MediaType.OCTET_STREAM;
        }
        return possibleTypes.get(0).getType();
    }
    
    private List<MimeType> applyHint(final List<MimeType> possibleTypes, final MimeType hint) {
        if (possibleTypes == null || possibleTypes.isEmpty()) {
            return Collections.singletonList(hint);
        }
        for (final MimeType type : possibleTypes) {
            if (this.registry.isSpecializationOf(hint, type, this.types)) {
                return Collections.singletonList(hint);
            }
        }
        return possibleTypes;
    }
    
    static {
        CLASSLOADER_SPECIFIC_DEFAULT_TYPES = new HashMap<ClassLoader, MimeTypes>();
        MimeTypes.DEFAULT_TYPES = null;
    }
}
