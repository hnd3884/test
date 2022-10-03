package org.bouncycastle.mail.smime.util;

import java.util.Enumeration;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.mail.internet.InternetHeaders;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.InputStream;
import java.io.File;
import javax.mail.internet.MimeBodyPart;

public class FileBackedMimeBodyPart extends MimeBodyPart
{
    private static final int BUF_SIZE = 32760;
    private final File _file;
    
    public FileBackedMimeBodyPart(final File file) throws MessagingException, IOException {
        super((InputStream)new SharedFileInputStream(file));
        this._file = file;
    }
    
    public FileBackedMimeBodyPart(final InputStream inputStream, final File file) throws MessagingException, IOException {
        this(saveStreamToFile(inputStream, file));
    }
    
    public FileBackedMimeBodyPart(final InternetHeaders internetHeaders, final InputStream inputStream, final File file) throws MessagingException, IOException {
        this(saveStreamToFile(internetHeaders, inputStream, file));
    }
    
    public void writeTo(final OutputStream outputStream) throws IOException, MessagingException {
        if (!this._file.exists()) {
            throw new IOException("file " + this._file.getCanonicalPath() + " no longer exists.");
        }
        super.writeTo(outputStream);
    }
    
    public void dispose() throws IOException {
        ((SharedFileInputStream)this.contentStream).getRoot().dispose();
        if (this._file.exists() && !this._file.delete()) {
            throw new IOException("deletion of underlying file <" + this._file.getCanonicalPath() + "> failed.");
        }
    }
    
    private static File saveStreamToFile(final InputStream inputStream, final File file) throws IOException {
        saveContentToStream(new FileOutputStream(file), inputStream);
        return file;
    }
    
    private static File saveStreamToFile(final InternetHeaders internetHeaders, final InputStream inputStream, final File file) throws IOException {
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        final Enumeration allHeaderLines = internetHeaders.getAllHeaderLines();
        while (allHeaderLines.hasMoreElements()) {
            writeHeader(fileOutputStream, (String)allHeaderLines.nextElement());
        }
        writeSeperator(fileOutputStream);
        saveContentToStream(fileOutputStream, inputStream);
        return file;
    }
    
    private static void writeHeader(final OutputStream outputStream, final String s) throws IOException {
        for (int i = 0; i != s.length(); ++i) {
            outputStream.write(s.charAt(i));
        }
        writeSeperator(outputStream);
    }
    
    private static void writeSeperator(final OutputStream outputStream) throws IOException {
        outputStream.write(13);
        outputStream.write(10);
    }
    
    private static void saveContentToStream(final OutputStream outputStream, final InputStream inputStream) throws IOException {
        final byte[] array = new byte[32760];
        int read;
        while ((read = inputStream.read(array, 0, array.length)) > 0) {
            outputStream.write(array, 0, read);
        }
        outputStream.close();
        inputStream.close();
    }
}
