package org.apache.tika.mime;

import java.util.List;
import java.io.File;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import org.w3c.dom.Document;

public class MimeTypesFactory
{
    public static final String CUSTOM_MIMES_SYS_PROP = "tika.custom-mimetypes";
    
    public static MimeTypes create() {
        return new MimeTypes();
    }
    
    public static MimeTypes create(final Document document) throws MimeTypeException {
        final MimeTypes mimeTypes = new MimeTypes();
        new MimeTypesReader(mimeTypes).read(document);
        mimeTypes.init();
        return mimeTypes;
    }
    
    public static MimeTypes create(final InputStream... inputStreams) throws IOException, MimeTypeException {
        final MimeTypes mimeTypes = new MimeTypes();
        final MimeTypesReader reader = new MimeTypesReader(mimeTypes);
        for (final InputStream inputStream : inputStreams) {
            reader.read(inputStream);
        }
        mimeTypes.init();
        return mimeTypes;
    }
    
    public static MimeTypes create(final InputStream stream) throws IOException, MimeTypeException {
        return create(new InputStream[] { stream });
    }
    
    public static MimeTypes create(final URL... urls) throws IOException, MimeTypeException {
        final InputStream[] streams = new InputStream[urls.length];
        for (int i = 0; i < streams.length; ++i) {
            streams[i] = urls[i].openStream();
        }
        try {
            return create(streams);
        }
        finally {
            for (final InputStream stream : streams) {
                stream.close();
            }
        }
    }
    
    public static MimeTypes create(final URL url) throws IOException, MimeTypeException {
        return create(new URL[] { url });
    }
    
    public static MimeTypes create(final String filePath) throws IOException, MimeTypeException {
        return create(MimeTypesReader.class.getResource(filePath));
    }
    
    public static MimeTypes create(final String coreFilePath, final String extensionFilePath) throws IOException, MimeTypeException {
        return create(coreFilePath, extensionFilePath, null);
    }
    
    public static MimeTypes create(final String coreFilePath, final String extensionFilePath, ClassLoader classLoader) throws IOException, MimeTypeException {
        if (classLoader == null) {
            classLoader = MimeTypesReader.class.getClassLoader();
        }
        final String classPrefix = MimeTypesReader.class.getPackage().getName().replace('.', '/') + "/";
        final URL coreURL = classLoader.getResource(classPrefix + coreFilePath);
        final List<URL> extensionURLs = Collections.list(classLoader.getResources(classPrefix + extensionFilePath));
        final List<URL> urls = new ArrayList<URL>();
        urls.add(coreURL);
        urls.addAll(extensionURLs);
        final String customMimesPath = System.getProperty("tika.custom-mimetypes");
        if (customMimesPath != null) {
            final File externalFile = new File(customMimesPath);
            if (!externalFile.exists()) {
                throw new IOException("Specified custom mimetypes file not found: " + customMimesPath);
            }
            final URL externalURL = externalFile.toURI().toURL();
            urls.add(externalURL);
        }
        return create((URL[])urls.toArray(new URL[0]));
    }
}
