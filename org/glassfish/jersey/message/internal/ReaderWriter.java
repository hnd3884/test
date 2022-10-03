package org.glassfish.jersey.message.internal;

import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.io.Closeable;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import javax.ws.rs.core.MediaType;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public final class ReaderWriter
{
    private static final Logger LOGGER;
    public static final Charset UTF8;
    public static final int BUFFER_SIZE;
    
    private static int getBufferSize() {
        final String value = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("jersey.config.io.bufferSize"));
        if (value != null) {
            try {
                final int i = Integer.parseInt(value);
                if (i <= 0) {
                    throw new NumberFormatException("Value not positive.");
                }
                return i;
            }
            catch (final NumberFormatException e) {
                ReaderWriter.LOGGER.log(Level.CONFIG, "Value of jersey.config.io.bufferSize property is not a valid positive integer [" + value + "]. Reverting to default [" + 8192 + "].", e);
            }
        }
        return 8192;
    }
    
    public static void writeTo(final InputStream in, final OutputStream out) throws IOException {
        final byte[] data = new byte[ReaderWriter.BUFFER_SIZE];
        int read;
        while ((read = in.read(data)) != -1) {
            out.write(data, 0, read);
        }
    }
    
    public static void writeTo(final Reader in, final Writer out) throws IOException {
        final char[] data = new char[ReaderWriter.BUFFER_SIZE];
        int read;
        while ((read = in.read(data)) != -1) {
            out.write(data, 0, read);
        }
    }
    
    public static Charset getCharset(final MediaType m) {
        final String name = (m == null) ? null : m.getParameters().get("charset");
        return (name == null) ? ReaderWriter.UTF8 : Charset.forName(name);
    }
    
    public static String readFromAsString(final InputStream in, final MediaType type) throws IOException {
        return readFromAsString(new InputStreamReader(in, getCharset(type)));
    }
    
    public static String readFromAsString(final Reader reader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final char[] c = new char[ReaderWriter.BUFFER_SIZE];
        int l;
        while ((l = reader.read(c)) != -1) {
            sb.append(c, 0, l);
        }
        return sb.toString();
    }
    
    public static void writeToAsString(final String s, final OutputStream out, final MediaType type) throws IOException {
        final Writer osw = new OutputStreamWriter(out, getCharset(type));
        osw.write(s, 0, s.length());
        osw.flush();
    }
    
    public static void safelyClose(final Closeable closeable) {
        try {
            closeable.close();
        }
        catch (final IOException ioe) {
            ReaderWriter.LOGGER.log(Level.FINE, LocalizationMessages.MESSAGE_CONTENT_INPUT_STREAM_CLOSE_FAILED(), ioe);
        }
        catch (final ProcessingException pe) {
            ReaderWriter.LOGGER.log(Level.FINE, LocalizationMessages.MESSAGE_CONTENT_INPUT_STREAM_CLOSE_FAILED(), (Throwable)pe);
        }
    }
    
    private ReaderWriter() {
    }
    
    static {
        LOGGER = Logger.getLogger(ReaderWriter.class.getName());
        UTF8 = Charset.forName("UTF-8");
        BUFFER_SIZE = getBufferSize();
    }
}
