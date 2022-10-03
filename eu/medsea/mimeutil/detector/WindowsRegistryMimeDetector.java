package eu.medsea.mimeutil.detector;

import java.io.IOException;
import java.io.StringWriter;
import java.io.InputStream;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import java.util.ArrayList;
import java.net.URL;
import eu.medsea.mimeutil.MimeException;
import java.util.Collection;
import java.io.File;

public class WindowsRegistryMimeDetector extends MimeDetector
{
    private static final String REG_QUERY = "reg query ";
    private static final String CONTENT_TYPE = "\"Content Type\"";
    private static final boolean isWindows;
    
    static {
        isWindows = System.getProperty("os.name").startsWith("Windows");
    }
    
    public String getDescription() {
        return "Get the MIME types of file extensions from the Windows Registry. Will be inafective on non-Windows machines.";
    }
    
    public Collection getMimeTypesFile(final File file) throws UnsupportedOperationException {
        try {
            return this.getMimeTypesURL(file.toURI().toURL());
        }
        catch (final Exception e) {
            throw new MimeException(e);
        }
    }
    
    public Collection getMimeTypesFileName(final String fileName) throws UnsupportedOperationException {
        return this.getMimeTypesFile(new File(fileName));
    }
    
    public Collection getMimeTypesURL(final URL url) throws UnsupportedOperationException {
        final Collection mimeTypes = new ArrayList();
        if (!WindowsRegistryMimeDetector.isWindows) {
            return mimeTypes;
        }
        final String contentType = this.getContentType(MimeUtil.getExtension(url.getPath()));
        if (contentType != null && contentType.length() > 0) {
            mimeTypes.add(new MimeType(contentType));
        }
        return mimeTypes;
    }
    
    public Collection getMimeTypesByteArray(final byte[] data) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("WindowsRegistryMimeDetector does not support detection from byte arrays.");
    }
    
    public Collection getMimeTypesInputStream(final InputStream in) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("WindowsRegistryMimeDetector does not support detection from InputStreams.");
    }
    
    private String getContentType(final String extension) {
        if (extension == null || extension.length() < 1) {
            return null;
        }
        try {
            final String query = "reg query \"HKEY_CLASSES_ROOT\\." + extension + "\" /v " + "\"Content Type\"";
            final Process process = Runtime.getRuntime().exec(query);
            final StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            final String result = reader.getResult();
            final int p = result.indexOf("REG_SZ");
            if (p == -1) {
                return null;
            }
            return result.substring(p + "REG_SZ".length()).trim();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    static class StreamReader extends Thread
    {
        private InputStream is;
        private StringWriter sw;
        
        StreamReader(final InputStream is) {
            this.is = is;
            this.sw = new StringWriter();
        }
        
        public void run() {
            try {
                int c;
                while ((c = this.is.read()) != -1) {
                    this.sw.write(c);
                }
            }
            catch (final IOException ex) {}
        }
        
        String getResult() {
            return this.sw.toString();
        }
    }
}
