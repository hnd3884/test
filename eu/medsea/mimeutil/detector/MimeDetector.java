package eu.medsea.mimeutil.detector;

import java.io.InputStream;
import java.net.URL;
import java.io.File;
import java.util.Collection;

public abstract class MimeDetector
{
    public final String getName() {
        return this.getClass().getName();
    }
    
    public final Collection getMimeTypes(final String fileName) throws UnsupportedOperationException {
        return this.getMimeTypesFileName(fileName);
    }
    
    public final Collection getMimeTypes(final File file) throws UnsupportedOperationException {
        return this.getMimeTypesFile(file);
    }
    
    public final Collection getMimeTypes(final URL url) throws UnsupportedOperationException {
        return this.getMimeTypesURL(url);
    }
    
    public final Collection getMimeTypes(final byte[] data) throws UnsupportedOperationException {
        return this.getMimeTypesByteArray(data);
    }
    
    public final Collection getMimeTypes(final InputStream in) throws UnsupportedOperationException {
        if (!in.markSupported()) {
            throw new UnsupportedOperationException("The InputStream must support the mark() and reset() methods.");
        }
        return this.getMimeTypesInputStream(in);
    }
    
    public void init() {
    }
    
    public void delete() {
    }
    
    public abstract String getDescription();
    
    protected abstract Collection getMimeTypesFileName(final String p0) throws UnsupportedOperationException;
    
    protected abstract Collection getMimeTypesFile(final File p0) throws UnsupportedOperationException;
    
    protected abstract Collection getMimeTypesURL(final URL p0) throws UnsupportedOperationException;
    
    protected abstract Collection getMimeTypesInputStream(final InputStream p0) throws UnsupportedOperationException;
    
    protected abstract Collection getMimeTypesByteArray(final byte[] p0) throws UnsupportedOperationException;
    
    protected static InputStream closeStream(final InputStream in) {
        if (in == null) {
            return null;
        }
        try {
            in.close();
        }
        catch (final Exception ex) {}
        return null;
    }
}
