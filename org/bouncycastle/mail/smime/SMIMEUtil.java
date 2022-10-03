package org.bouncycastle.mail.smime;

import java.security.cert.CertificateParsingException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import java.security.cert.X509Certificate;
import java.io.File;
import org.bouncycastle.mail.smime.util.FileBackedMimeBodyPart;
import org.bouncycastle.cms.CMSTypedStream;
import java.io.ByteArrayInputStream;
import java.io.FilterOutputStream;
import java.util.Enumeration;
import org.bouncycastle.mail.smime.util.CRLFOutputStream;
import javax.mail.Multipart;
import java.io.OutputStream;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.mail.BodyPart;
import java.io.IOException;
import java.io.InputStream;
import javax.mail.internet.MimeBodyPart;
import javax.mail.MessagingException;
import org.bouncycastle.util.Strings;
import javax.mail.Part;

public class SMIMEUtil
{
    private static final String MULTIPART = "multipart";
    private static final int BUF_SIZE = 32760;
    
    public static boolean isMultipartContent(final Part part) throws MessagingException {
        return Strings.toLowerCase(part.getContentType()).startsWith("multipart");
    }
    
    static boolean isCanonicalisationRequired(final MimeBodyPart mimeBodyPart, final String s) throws MessagingException {
        final String[] header = mimeBodyPart.getHeader("Content-Transfer-Encoding");
        String s2;
        if (header == null) {
            s2 = s;
        }
        else {
            s2 = header[0];
        }
        return !s2.equalsIgnoreCase("binary");
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
    
    static void outputPostamble(final LineOutputStream lineOutputStream, final MimeBodyPart mimeBodyPart, final int n, final String s) throws MessagingException, IOException {
        InputStream rawInputStream;
        try {
            rawInputStream = mimeBodyPart.getRawInputStream();
        }
        catch (final MessagingException ex) {
            return;
        }
        int n2 = n + 1;
        String line;
        while ((line = readLine(rawInputStream)) != null && (!line.startsWith(s) || --n2 != 0)) {}
        String line2;
        while ((line2 = readLine(rawInputStream)) != null) {
            lineOutputStream.writeln(line2);
        }
        rawInputStream.close();
        if (n2 != 0) {
            throw new MessagingException("all boundaries not found for: " + s);
        }
    }
    
    static void outputPostamble(final LineOutputStream lineOutputStream, final BodyPart bodyPart, final String s, final BodyPart bodyPart2) throws MessagingException, IOException {
        InputStream rawInputStream;
        try {
            rawInputStream = ((MimeBodyPart)bodyPart).getRawInputStream();
        }
        catch (final MessagingException ex) {
            return;
        }
        final MimeMultipart mimeMultipart = (MimeMultipart)bodyPart2.getContent();
        final String string = "--" + new ContentType(mimeMultipart.getContentType()).getParameter("boundary");
        String line;
        for (int n = mimeMultipart.getCount() + 1; n != 0 && (line = readLine(rawInputStream)) != null; --n) {
            if (line.startsWith(string)) {}
        }
        String line2;
        while ((line2 = readLine(rawInputStream)) != null && !line2.startsWith(s)) {
            lineOutputStream.writeln(line2);
        }
        rawInputStream.close();
    }
    
    private static String readLine(final InputStream inputStream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        int read;
        while ((read = inputStream.read()) >= 0 && read != 10) {
            if (read != 13) {
                sb.append((char)read);
            }
        }
        if (read < 0 && sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }
    
    static void outputBodyPart(OutputStream outputStream, final boolean b, final BodyPart bodyPart, final String s) throws MessagingException, IOException {
        if (bodyPart instanceof MimeBodyPart) {
            final MimeBodyPart mimeBodyPart = (MimeBodyPart)bodyPart;
            final String[] header = mimeBodyPart.getHeader("Content-Transfer-Encoding");
            if (isMultipartContent((Part)mimeBodyPart)) {
                final Object content = bodyPart.getContent();
                Object o;
                if (content instanceof Multipart) {
                    o = content;
                }
                else {
                    o = new MimeMultipart(bodyPart.getDataHandler().getDataSource());
                }
                final String string = "--" + new ContentType(((Multipart)o).getContentType()).getParameter("boundary");
                final LineOutputStream lineOutputStream = new LineOutputStream(outputStream);
                final Enumeration allHeaderLines = mimeBodyPart.getAllHeaderLines();
                while (allHeaderLines.hasMoreElements()) {
                    lineOutputStream.writeln((String)allHeaderLines.nextElement());
                }
                lineOutputStream.writeln();
                outputPreamble(lineOutputStream, mimeBodyPart, string);
                for (int i = 0; i < ((Multipart)o).getCount(); ++i) {
                    lineOutputStream.writeln(string);
                    final BodyPart bodyPart2 = ((Multipart)o).getBodyPart(i);
                    outputBodyPart(outputStream, false, bodyPart2, s);
                    if (!isMultipartContent((Part)bodyPart2)) {
                        lineOutputStream.writeln();
                    }
                    else {
                        outputPostamble(lineOutputStream, (BodyPart)mimeBodyPart, string, bodyPart2);
                    }
                }
                lineOutputStream.writeln(string + "--");
                if (b) {
                    outputPostamble(lineOutputStream, mimeBodyPart, ((Multipart)o).getCount(), string);
                }
                return;
            }
            String s2;
            if (header == null) {
                s2 = s;
            }
            else {
                s2 = header[0];
            }
            if (!s2.equalsIgnoreCase("base64") && !s2.equalsIgnoreCase("quoted-printable")) {
                if (!s2.equalsIgnoreCase("binary")) {
                    outputStream = new CRLFOutputStream(outputStream);
                }
                bodyPart.writeTo(outputStream);
                outputStream.flush();
                return;
            }
            final boolean equalsIgnoreCase = s2.equalsIgnoreCase("base64");
            InputStream rawInputStream;
            try {
                rawInputStream = mimeBodyPart.getRawInputStream();
            }
            catch (final MessagingException ex) {
                final CRLFOutputStream crlfOutputStream = new CRLFOutputStream(outputStream);
                bodyPart.writeTo((OutputStream)crlfOutputStream);
                crlfOutputStream.flush();
                return;
            }
            final LineOutputStream lineOutputStream2 = new LineOutputStream(outputStream);
            final Enumeration allHeaderLines2 = mimeBodyPart.getAllHeaderLines();
            while (allHeaderLines2.hasMoreElements()) {
                lineOutputStream2.writeln((String)allHeaderLines2.nextElement());
            }
            lineOutputStream2.writeln();
            lineOutputStream2.flush();
            FilterOutputStream filterOutputStream;
            if (equalsIgnoreCase) {
                filterOutputStream = new Base64CRLFOutputStream(outputStream);
            }
            else {
                filterOutputStream = new CRLFOutputStream(outputStream);
            }
            final byte[] array = new byte[32760];
            int read;
            while ((read = rawInputStream.read(array, 0, array.length)) > 0) {
                filterOutputStream.write(array, 0, read);
            }
            rawInputStream.close();
            filterOutputStream.flush();
        }
        else {
            if (!s.equalsIgnoreCase("binary")) {
                outputStream = new CRLFOutputStream(outputStream);
            }
            bodyPart.writeTo(outputStream);
            outputStream.flush();
        }
    }
    
    public static MimeBodyPart toMimeBodyPart(final byte[] array) throws SMIMEException {
        return toMimeBodyPart(new ByteArrayInputStream(array));
    }
    
    public static MimeBodyPart toMimeBodyPart(final InputStream inputStream) throws SMIMEException {
        try {
            return new MimeBodyPart(inputStream);
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("exception creating body part.", (Exception)ex);
        }
    }
    
    static FileBackedMimeBodyPart toWriteOnceBodyPart(final CMSTypedStream cmsTypedStream) throws SMIMEException {
        try {
            return new WriteOnceFileBackedMimeBodyPart(cmsTypedStream.getContentStream(), File.createTempFile("bcMail", ".mime"));
        }
        catch (final IOException ex) {
            throw new SMIMEException("IOException creating tmp file:" + ex.getMessage(), ex);
        }
        catch (final MessagingException ex2) {
            throw new SMIMEException("can't create part: " + ex2, (Exception)ex2);
        }
    }
    
    public static FileBackedMimeBodyPart toMimeBodyPart(final CMSTypedStream cmsTypedStream) throws SMIMEException {
        try {
            return toMimeBodyPart(cmsTypedStream, File.createTempFile("bcMail", ".mime"));
        }
        catch (final IOException ex) {
            throw new SMIMEException("IOException creating tmp file:" + ex.getMessage(), ex);
        }
    }
    
    public static FileBackedMimeBodyPart toMimeBodyPart(final CMSTypedStream cmsTypedStream, final File file) throws SMIMEException {
        try {
            return new FileBackedMimeBodyPart(cmsTypedStream.getContentStream(), file);
        }
        catch (final IOException ex) {
            throw new SMIMEException("can't save content to file: " + ex, ex);
        }
        catch (final MessagingException ex2) {
            throw new SMIMEException("can't create part: " + ex2, (Exception)ex2);
        }
    }
    
    public static IssuerAndSerialNumber createIssuerAndSerialNumberFor(final X509Certificate x509Certificate) throws CertificateParsingException {
        try {
            return new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).getIssuer(), x509Certificate.getSerialNumber());
        }
        catch (final Exception ex) {
            throw new CertificateParsingException("exception extracting issuer and serial number: " + ex);
        }
    }
    
    static class Base64CRLFOutputStream extends FilterOutputStream
    {
        protected int lastb;
        protected static byte[] newline;
        private boolean isCrlfStream;
        
        public Base64CRLFOutputStream(final OutputStream outputStream) {
            super(outputStream);
            this.lastb = -1;
        }
        
        @Override
        public void write(final int lastb) throws IOException {
            if (lastb == 13) {
                this.out.write(Base64CRLFOutputStream.newline);
            }
            else if (lastb == 10) {
                if (this.lastb != 13) {
                    if (!this.isCrlfStream || this.lastb != 10) {
                        this.out.write(Base64CRLFOutputStream.newline);
                    }
                }
                else {
                    this.isCrlfStream = true;
                }
            }
            else {
                this.out.write(lastb);
            }
            this.lastb = lastb;
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this.write(array, 0, array.length);
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            for (int i = n; i != n + n2; ++i) {
                this.write(array[i]);
            }
        }
        
        public void writeln() throws IOException {
            super.out.write(Base64CRLFOutputStream.newline);
        }
        
        static {
            (Base64CRLFOutputStream.newline = new byte[2])[0] = 13;
            Base64CRLFOutputStream.newline[1] = 10;
        }
    }
    
    static class LineOutputStream extends FilterOutputStream
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
    
    private static class WriteOnceFileBackedMimeBodyPart extends FileBackedMimeBodyPart
    {
        public WriteOnceFileBackedMimeBodyPart(final InputStream inputStream, final File file) throws MessagingException, IOException {
            super(inputStream, file);
        }
        
        @Override
        public void writeTo(final OutputStream outputStream) throws MessagingException, IOException {
            super.writeTo(outputStream);
            this.dispose();
        }
    }
}
