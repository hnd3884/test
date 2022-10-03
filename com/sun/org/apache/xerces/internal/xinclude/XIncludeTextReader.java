package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import java.util.Iterator;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import java.util.Locale;
import java.util.Map;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import java.net.HttpURLConnection;
import java.net.URL;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import java.io.BufferedInputStream;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.Reader;

public class XIncludeTextReader
{
    private Reader fReader;
    private XIncludeHandler fHandler;
    private XMLInputSource fSource;
    private XMLErrorReporter fErrorReporter;
    private XMLString fTempString;
    
    public XIncludeTextReader(final XMLInputSource source, final XIncludeHandler handler, final int bufferSize) throws IOException {
        this.fTempString = new XMLString();
        this.fHandler = handler;
        this.fSource = source;
        this.fTempString = new XMLString(new char[bufferSize + 1], 0, 0);
    }
    
    public void setErrorReporter(final XMLErrorReporter errorReporter) {
        this.fErrorReporter = errorReporter;
    }
    
    protected Reader getReader(final XMLInputSource source) throws IOException {
        if (source.getCharacterStream() != null) {
            return source.getCharacterStream();
        }
        InputStream stream = null;
        String encoding = source.getEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        if (source.getByteStream() != null) {
            stream = source.getByteStream();
            if (!(stream instanceof BufferedInputStream)) {
                stream = new BufferedInputStream(stream, this.fTempString.ch.length);
            }
        }
        else {
            final String expandedSystemId = XMLEntityManager.expandSystemId(source.getSystemId(), source.getBaseSystemId(), false);
            final URL url = new URL(expandedSystemId);
            final URLConnection urlCon = url.openConnection();
            if (urlCon instanceof HttpURLConnection && source instanceof HTTPInputSource) {
                final HttpURLConnection urlConnection = (HttpURLConnection)urlCon;
                final HTTPInputSource httpInputSource = (HTTPInputSource)source;
                final Iterator propIter = httpInputSource.getHTTPRequestProperties();
                while (propIter.hasNext()) {
                    final Map.Entry entry = propIter.next();
                    urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
                final boolean followRedirects = httpInputSource.getFollowHTTPRedirects();
                if (!followRedirects) {
                    XMLEntityManager.setInstanceFollowRedirects(urlConnection, followRedirects);
                }
            }
            stream = new BufferedInputStream(urlCon.getInputStream());
            final String rawContentType = urlCon.getContentType();
            final int index = (rawContentType != null) ? rawContentType.indexOf(59) : -1;
            String contentType = null;
            String charset = null;
            if (index != -1) {
                contentType = rawContentType.substring(0, index).trim();
                charset = rawContentType.substring(index + 1).trim();
                if (charset.startsWith("charset=")) {
                    charset = charset.substring(8).trim();
                    if ((charset.charAt(0) == '\"' && charset.charAt(charset.length() - 1) == '\"') || (charset.charAt(0) == '\'' && charset.charAt(charset.length() - 1) == '\'')) {
                        charset = charset.substring(1, charset.length() - 1);
                    }
                }
                else {
                    charset = null;
                }
            }
            else {
                contentType = rawContentType.trim();
            }
            String detectedEncoding = null;
            if (contentType.equals("text/xml")) {
                if (charset != null) {
                    detectedEncoding = charset;
                }
                else {
                    detectedEncoding = "US-ASCII";
                }
            }
            else if (contentType.equals("application/xml")) {
                if (charset != null) {
                    detectedEncoding = charset;
                }
                else {
                    detectedEncoding = this.getEncodingName(stream);
                }
            }
            else if (contentType.endsWith("+xml")) {
                detectedEncoding = this.getEncodingName(stream);
            }
            if (detectedEncoding != null) {
                encoding = detectedEncoding;
            }
        }
        encoding = encoding.toUpperCase(Locale.ENGLISH);
        encoding = this.consumeBOM(stream, encoding);
        if (encoding.equals("UTF-8")) {
            return new UTF8Reader(stream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
        }
        final String javaEncoding = EncodingMap.getIANA2JavaMapping(encoding);
        if (javaEncoding == null) {
            final MessageFormatter aFormatter = this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210");
            final Locale aLocale = this.fErrorReporter.getLocale();
            throw new IOException(aFormatter.formatMessage(aLocale, "EncodingDeclInvalid", new Object[] { encoding }));
        }
        if (javaEncoding.equals("ASCII")) {
            return new ASCIIReader(stream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
        }
        return new InputStreamReader(stream, javaEncoding);
    }
    
    protected String getEncodingName(final InputStream stream) throws IOException {
        final byte[] b4 = new byte[4];
        String encoding = null;
        stream.mark(4);
        final int count = stream.read(b4, 0, 4);
        stream.reset();
        if (count == 4) {
            encoding = this.getEncodingName(b4);
        }
        return encoding;
    }
    
    protected String consumeBOM(final InputStream stream, final String encoding) throws IOException {
        final byte[] b = new byte[3];
        int count = 0;
        stream.mark(3);
        if (encoding.equals("UTF-8")) {
            count = stream.read(b, 0, 3);
            if (count == 3) {
                final int b2 = b[0] & 0xFF;
                final int b3 = b[1] & 0xFF;
                final int b4 = b[2] & 0xFF;
                if (b2 != 239 || b3 != 187 || b4 != 191) {
                    stream.reset();
                }
            }
            else {
                stream.reset();
            }
        }
        else if (encoding.startsWith("UTF-16")) {
            count = stream.read(b, 0, 2);
            if (count == 2) {
                final int b2 = b[0] & 0xFF;
                final int b3 = b[1] & 0xFF;
                if (b2 == 254 && b3 == 255) {
                    return "UTF-16BE";
                }
                if (b2 == 255 && b3 == 254) {
                    return "UTF-16LE";
                }
            }
            stream.reset();
        }
        return encoding;
    }
    
    protected String getEncodingName(final byte[] b4) {
        final int b5 = b4[0] & 0xFF;
        final int b6 = b4[1] & 0xFF;
        if (b5 == 254 && b6 == 255) {
            return "UTF-16BE";
        }
        if (b5 == 255 && b6 == 254) {
            return "UTF-16LE";
        }
        final int b7 = b4[2] & 0xFF;
        if (b5 == 239 && b6 == 187 && b7 == 191) {
            return "UTF-8";
        }
        final int b8 = b4[3] & 0xFF;
        if (b5 == 0 && b6 == 0 && b7 == 0 && b8 == 60) {
            return "ISO-10646-UCS-4";
        }
        if (b5 == 60 && b6 == 0 && b7 == 0 && b8 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (b5 == 0 && b6 == 0 && b7 == 60 && b8 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (b5 == 0 && b6 == 60 && b7 == 0 && b8 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (b5 == 0 && b6 == 60 && b7 == 0 && b8 == 63) {
            return "UTF-16BE";
        }
        if (b5 == 60 && b6 == 0 && b7 == 63 && b8 == 0) {
            return "UTF-16LE";
        }
        if (b5 == 76 && b6 == 111 && b7 == 167 && b8 == 148) {
            return "CP037";
        }
        return null;
    }
    
    public void parse() throws IOException {
        this.fReader = this.getReader(this.fSource);
        this.fSource = null;
        for (int readSize = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1); readSize != -1; readSize = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1)) {
            for (int i = 0; i < readSize; ++i) {
                final char ch = this.fTempString.ch[i];
                if (!this.isValid(ch)) {
                    if (XMLChar.isHighSurrogate(ch)) {
                        int ch2;
                        if (++i < readSize) {
                            ch2 = this.fTempString.ch[i];
                        }
                        else {
                            ch2 = this.fReader.read();
                            if (ch2 != -1) {
                                this.fTempString.ch[readSize++] = (char)ch2;
                            }
                        }
                        if (XMLChar.isLowSurrogate(ch2)) {
                            final int sup = XMLChar.supplemental(ch, (char)ch2);
                            if (!this.isValid(sup)) {
                                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[] { Integer.toString(sup, 16) }, (short)2);
                            }
                        }
                        else {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[] { Integer.toString(ch2, 16) }, (short)2);
                        }
                    }
                    else {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[] { Integer.toString(ch, 16) }, (short)2);
                    }
                }
            }
            if (this.fHandler != null && readSize > 0) {
                this.fTempString.offset = 0;
                this.fTempString.length = readSize;
                this.fHandler.characters(this.fTempString, this.fHandler.modifyAugmentations(null, true));
            }
        }
    }
    
    public void setInputSource(final XMLInputSource source) {
        this.fSource = source;
    }
    
    public void close() throws IOException {
        if (this.fReader != null) {
            this.fReader.close();
            this.fReader = null;
        }
    }
    
    protected boolean isValid(final int ch) {
        return XMLChar.isValid(ch);
    }
    
    protected void setBufferSize(int bufferSize) {
        if (this.fTempString.ch.length != ++bufferSize) {
            this.fTempString.ch = new char[bufferSize];
        }
    }
}
