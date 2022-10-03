package org.bouncycastle.mail.smime.handlers;

import java.io.FilterOutputStream;
import java.util.Enumeration;
import javax.mail.Part;
import org.bouncycastle.mail.smime.SMIMEUtil;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.ContentType;
import javax.mail.Multipart;
import org.bouncycastle.mail.smime.SMIMEStreamingProcessor;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataSource;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;

public class multipart_signed implements DataContentHandler
{
    private static final ActivationDataFlavor ADF;
    private static final DataFlavor[] DFS;
    
    public Object getContent(final DataSource dataSource) throws IOException {
        try {
            return new MimeMultipart(dataSource);
        }
        catch (final MessagingException ex) {
            return null;
        }
    }
    
    public Object getTransferData(final DataFlavor dataFlavor, final DataSource dataSource) throws IOException {
        if (multipart_signed.ADF.equals(dataFlavor)) {
            return this.getContent(dataSource);
        }
        return null;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return multipart_signed.DFS;
    }
    
    public void writeTo(final Object o, final String s, final OutputStream outputStream) throws IOException {
        if (o instanceof MimeMultipart) {
            try {
                this.outputBodyPart(outputStream, o);
                return;
            }
            catch (final MessagingException ex) {
                throw new IOException(ex.getMessage());
            }
        }
        if (o instanceof byte[]) {
            outputStream.write((byte[])o);
        }
        else if (o instanceof InputStream) {
            InputStream inputStream = (InputStream)o;
            if (!(inputStream instanceof BufferedInputStream)) {
                inputStream = new BufferedInputStream(inputStream);
            }
            int read;
            while ((read = inputStream.read()) >= 0) {
                outputStream.write(read);
            }
            inputStream.close();
        }
        else {
            if (!(o instanceof SMIMEStreamingProcessor)) {
                throw new IOException("unknown object in writeTo " + o);
            }
            ((SMIMEStreamingProcessor)o).write(outputStream);
        }
    }
    
    private void outputBodyPart(final OutputStream outputStream, final Object o) throws MessagingException, IOException {
        if (o instanceof Multipart) {
            final Multipart multipart = (Multipart)o;
            final String string = "--" + new ContentType(multipart.getContentType()).getParameter("boundary");
            final LineOutputStream lineOutputStream = new LineOutputStream(outputStream);
            for (int i = 0; i < multipart.getCount(); ++i) {
                lineOutputStream.writeln(string);
                this.outputBodyPart(outputStream, multipart.getBodyPart(i));
                lineOutputStream.writeln();
            }
            lineOutputStream.writeln(string + "--");
            return;
        }
        final MimeBodyPart mimeBodyPart = (MimeBodyPart)o;
        if (SMIMEUtil.isMultipartContent((Part)mimeBodyPart)) {
            final Object content = mimeBodyPart.getContent();
            if (content instanceof Multipart) {
                final Multipart multipart2 = (Multipart)content;
                final String string2 = "--" + new ContentType(multipart2.getContentType()).getParameter("boundary");
                final LineOutputStream lineOutputStream2 = new LineOutputStream(outputStream);
                final Enumeration allHeaderLines = mimeBodyPart.getAllHeaderLines();
                while (allHeaderLines.hasMoreElements()) {
                    lineOutputStream2.writeln((String)allHeaderLines.nextElement());
                }
                lineOutputStream2.writeln();
                outputPreamble(lineOutputStream2, mimeBodyPart, string2);
                this.outputBodyPart(outputStream, multipart2);
                return;
            }
        }
        mimeBodyPart.writeTo(outputStream);
    }
    
    static void outputPreamble(final LineOutputStream lineOutputStream, final MimeBodyPart mimeBodyPart, final String s) throws MessagingException, IOException {
        InputStream rawInputStream;
        try {
            rawInputStream = mimeBodyPart.getRawInputStream();
        }
        catch (final MessagingException ex) {
            return;
        }
        String line;
        while ((line = readLine(rawInputStream)) != null && !line.equals(s)) {
            lineOutputStream.writeln(line);
        }
        rawInputStream.close();
        if (line == null) {
            throw new MessagingException("no boundary found");
        }
    }
    
    private static String readLine(final InputStream inputStream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        int read;
        while ((read = inputStream.read()) >= 0 && read != 10) {
            if (read != 13) {
                sb.append((char)read);
            }
        }
        if (read < 0) {
            return null;
        }
        return sb.toString();
    }
    
    static {
        ADF = new ActivationDataFlavor(MimeMultipart.class, "multipart/signed", "Multipart Signed");
        DFS = new DataFlavor[] { multipart_signed.ADF };
    }
    
    private static class LineOutputStream extends FilterOutputStream
    {
        private static byte[] newline;
        
        public LineOutputStream(final OutputStream outputStream) {
            super(outputStream);
        }
        
        public void writeln(final String s) throws MessagingException {
            try {
                super.out.write(getBytes(s));
                super.out.write(LineOutputStream.newline);
            }
            catch (final Exception ex) {
                throw new MessagingException("IOException", ex);
            }
        }
        
        public void writeln() throws MessagingException {
            try {
                super.out.write(LineOutputStream.newline);
            }
            catch (final Exception ex) {
                throw new MessagingException("IOException", ex);
            }
        }
        
        private static byte[] getBytes(final String s) {
            final char[] charArray = s.toCharArray();
            final int length = charArray.length;
            final byte[] array = new byte[length];
            for (int i = 0; i < length; array[i] = (byte)charArray[i++]) {}
            return array;
        }
        
        static {
            (LineOutputStream.newline = new byte[2])[0] = 13;
            LineOutputStream.newline[1] = 10;
        }
    }
}
