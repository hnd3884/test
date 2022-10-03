package eu.medsea.mimeutil;

import eu.medsea.mimeutil.handler.TextMimeHandler;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import eu.medsea.util.EncodingGuesser;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.File;
import java.util.Collection;
import eu.medsea.mimeutil.detector.MimeDetector;

public final class TextMimeDetector extends MimeDetector
{
    private static final MimeUtil2.MimeLogger log;
    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_NULL_VALUES = 1;
    private static Collection preferredEncodings;
    private static Collection handlers;
    
    private TextMimeDetector() {
    }
    
    TextMimeDetector(final int dummy) {
        this();
    }
    
    @Override
    public String getDescription() {
        return "Determine if a file or stream contains a text mime type. If so then return TextMimeType with text/plain and the best guess encoding.";
    }
    
    public Collection getMimeTypesFileName(final String fileName) throws UnsupportedOperationException {
        return this.getMimeTypesFile(new File(fileName));
    }
    
    public Collection getMimeTypesURL(final URL url) throws UnsupportedOperationException {
        InputStream in = null;
        try {
            return this.getMimeTypesInputStream(in = new BufferedInputStream(MimeUtil.getInputStreamForURL(url)));
        }
        catch (final UnsupportedOperationException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new MimeException(e2);
        }
        finally {
            try {
                in.close();
            }
            catch (final Exception ignore) {
                TextMimeDetector.log.error(ignore.getLocalizedMessage());
            }
        }
    }
    
    public Collection getMimeTypesFile(final File file) throws UnsupportedOperationException {
        if (!file.exists()) {
            throw new UnsupportedOperationException("This MimeDetector requires actual content.");
        }
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            return this.getMimeTypesInputStream(in);
        }
        catch (final UnsupportedOperationException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new MimeException(e2);
        }
        finally {
            try {
                in.close();
            }
            catch (final Exception ignore) {
                TextMimeDetector.log.error(ignore.getLocalizedMessage());
            }
        }
    }
    
    public Collection getMimeTypesInputStream(final InputStream in) throws UnsupportedOperationException {
        int offset = 0;
        final int len = 1024;
        final byte[] data = new byte[len];
        byte[] copy = null;
        in.mark(len);
        try {
            int bytesRead;
            for (int restBytesToRead = len; restBytesToRead > 0; restBytesToRead -= bytesRead) {
                bytesRead = in.read(data, offset, restBytesToRead);
                if (bytesRead < 0) {
                    break;
                }
                offset += bytesRead;
            }
            if (offset < len) {
                copy = new byte[offset];
                System.arraycopy(data, 0, copy, 0, offset);
            }
            else {
                copy = data;
            }
        }
        catch (final IOException ioe) {
            throw new MimeException(ioe);
        }
        finally {
            try {
                in.reset();
            }
            catch (final Exception e) {
                throw new MimeException(e);
            }
        }
        return this.getMimeTypesByteArray(copy);
    }
    
    public Collection getMimeTypesByteArray(final byte[] data) throws UnsupportedOperationException {
        if (EncodingGuesser.getSupportedEncodings().isEmpty() || this.isBinary(data)) {
            throw new UnsupportedOperationException();
        }
        final Collection mimeTypes = new ArrayList();
        final Collection possibleEncodings = EncodingGuesser.getPossibleEncodings(data);
        if (TextMimeDetector.log.isDebugEnabled()) {
            TextMimeDetector.log.debug("Possible encodings [" + possibleEncodings.size() + "] " + possibleEncodings);
        }
        if (possibleEncodings.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        String encoding = null;
        Iterator it = TextMimeDetector.preferredEncodings.iterator();
        while (it.hasNext()) {
            encoding = it.next();
            if (possibleEncodings.contains(encoding)) {
                mimeTypes.add(new TextMimeType("text/plain", encoding));
                break;
            }
        }
        if (mimeTypes.isEmpty() && possibleEncodings.contains(EncodingGuesser.getDefaultEncoding())) {
            encoding = EncodingGuesser.getDefaultEncoding();
            mimeTypes.add(new TextMimeType("text/plain", encoding));
        }
        if (mimeTypes.isEmpty()) {
            it = possibleEncodings.iterator();
            encoding = it.next();
            mimeTypes.add(new TextMimeType("text/plain", encoding));
        }
        if (mimeTypes.isEmpty() || TextMimeDetector.handlers.isEmpty()) {
            return mimeTypes;
        }
        try {
            final int lengthBOM = EncodingGuesser.getLengthBOM(encoding, data);
            final String content = new String(EncodingGuesser.getByteArraySubArray(data, lengthBOM, data.length - lengthBOM), encoding);
            return this.fireMimeHandlers(mimeTypes, content);
        }
        catch (final UnsupportedEncodingException ex) {
            return mimeTypes;
        }
    }
    
    public static void setPreferredEncodings(final String[] encodings) {
        TextMimeDetector.preferredEncodings = EncodingGuesser.getValidEncodings(encodings);
        if (TextMimeDetector.log.isDebugEnabled()) {
            TextMimeDetector.log.debug("Preferred Encodings set to " + TextMimeDetector.preferredEncodings);
        }
    }
    
    public static void registerTextMimeHandler(final TextMimeHandler handler) {
        TextMimeDetector.handlers.add(handler);
    }
    
    public static void unregisterTextMimeHandler(final TextMimeHandler handler) {
        TextMimeDetector.handlers.remove(handler);
    }
    
    public static Collection getRegisteredTextMimeHandlers() {
        return TextMimeDetector.handlers;
    }
    
    private Collection fireMimeHandlers(final Collection mimeTypes, final String content) {
        final TextMimeType mimeType = mimeTypes.iterator().next();
        for (final TextMimeHandler tmh : TextMimeDetector.handlers) {
            if (tmh.handle(mimeType, content)) {
                break;
            }
        }
        return mimeTypes;
    }
    
    private boolean isBinary(final byte[] data) {
        int negCount = 0;
        for (int i = 0; i < data.length; ++i) {
            if (data[i] == 0) {
                ++negCount;
            }
            else {
                negCount = 0;
            }
            if (negCount == 1) {
                return true;
            }
        }
        return false;
    }
    
    static {
        log = new MimeUtil2.MimeLogger(TextMimeDetector.class.getName());
        TextMimeDetector.preferredEncodings = new ArrayList();
        setPreferredEncodings(new String[] { "UTF-16", "UTF-8", "ISO-8859-1", "windows-1252", "US-ASCII" });
        TextMimeDetector.handlers = new ArrayList();
    }
}
