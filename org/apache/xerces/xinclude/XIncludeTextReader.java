package org.apache.xerces.xinclude;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.io.Latin1Reader;
import org.apache.xerces.impl.io.ASCIIReader;
import org.apache.xerces.impl.io.UTF16Reader;
import org.apache.xerces.impl.io.UTF8Reader;
import java.util.Iterator;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.xerces.util.EncodingMap;
import java.util.Locale;
import java.util.Map;
import org.apache.xerces.util.HTTPInputSource;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.xerces.impl.XMLEntityManager;
import java.io.BufferedInputStream;
import java.io.IOException;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xni.parser.XMLInputSource;
import java.io.Reader;

public class XIncludeTextReader
{
    private Reader fReader;
    private final XIncludeHandler fHandler;
    private XMLInputSource fSource;
    private XMLErrorReporter fErrorReporter;
    private XMLString fTempString;
    
    public XIncludeTextReader(final XMLInputSource fSource, final XIncludeHandler fHandler, final int n) throws IOException {
        this.fTempString = new XMLString();
        this.fHandler = fHandler;
        this.fSource = fSource;
        this.fTempString = new XMLString(new char[n + 1], 0, 0);
    }
    
    public void setErrorReporter(final XMLErrorReporter fErrorReporter) {
        this.fErrorReporter = fErrorReporter;
    }
    
    protected Reader getReader(final XMLInputSource xmlInputSource) throws IOException {
        if (xmlInputSource.getCharacterStream() != null) {
            return xmlInputSource.getCharacterStream();
        }
        String encoding = xmlInputSource.getEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        InputStream byteStream;
        if (xmlInputSource.getByteStream() != null) {
            byteStream = xmlInputSource.getByteStream();
            if (!(byteStream instanceof BufferedInputStream)) {
                byteStream = new BufferedInputStream(byteStream, this.fTempString.ch.length);
            }
        }
        else {
            final URLConnection openConnection = new URL(XMLEntityManager.expandSystemId(xmlInputSource.getSystemId(), xmlInputSource.getBaseSystemId(), false)).openConnection();
            if (openConnection instanceof HttpURLConnection && xmlInputSource instanceof HTTPInputSource) {
                final HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection;
                final HTTPInputSource httpInputSource = (HTTPInputSource)xmlInputSource;
                final Iterator httpRequestProperties = httpInputSource.getHTTPRequestProperties();
                while (httpRequestProperties.hasNext()) {
                    final Map.Entry entry = httpRequestProperties.next();
                    httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                }
                final boolean followHTTPRedirects = httpInputSource.getFollowHTTPRedirects();
                if (!followHTTPRedirects) {
                    httpURLConnection.setInstanceFollowRedirects(followHTTPRedirects);
                }
            }
            byteStream = new BufferedInputStream(openConnection.getInputStream());
            final String contentType = openConnection.getContentType();
            final int n = (contentType != null) ? contentType.indexOf(59) : -1;
            String s = null;
            String trim;
            if (n != -1) {
                trim = contentType.substring(0, n).trim();
                final String trim2 = contentType.substring(n + 1).trim();
                if (trim2.startsWith("charset=")) {
                    s = trim2.substring(8).trim();
                    if ((s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"') || (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'')) {
                        s = s.substring(1, s.length() - 1);
                    }
                }
                else {
                    s = null;
                }
            }
            else {
                trim = ((contentType != null) ? contentType.trim() : "");
            }
            String s2 = null;
            if (trim.equals("text/xml")) {
                if (s != null) {
                    s2 = s;
                }
                else {
                    s2 = "US-ASCII";
                }
            }
            else if (trim.equals("application/xml")) {
                if (s != null) {
                    s2 = s;
                }
                else {
                    s2 = this.getEncodingName(byteStream);
                }
            }
            else if (trim.endsWith("+xml")) {
                s2 = this.getEncodingName(byteStream);
            }
            if (s2 != null) {
                encoding = s2;
            }
        }
        final String consumeBOM = this.consumeBOM(byteStream, encoding.toUpperCase(Locale.ENGLISH));
        if (consumeBOM.equals("UTF-8")) {
            return this.createUTF8Reader(byteStream);
        }
        if (consumeBOM.equals("UTF-16BE")) {
            return this.createUTF16Reader(byteStream, true);
        }
        if (consumeBOM.equals("UTF-16LE")) {
            return this.createUTF16Reader(byteStream, false);
        }
        final String iana2JavaMapping = EncodingMap.getIANA2JavaMapping(consumeBOM);
        if (iana2JavaMapping == null) {
            throw new IOException(this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210").formatMessage(this.fErrorReporter.getLocale(), "EncodingDeclInvalid", new Object[] { consumeBOM }));
        }
        if (iana2JavaMapping.equals("ASCII")) {
            return this.createASCIIReader(byteStream);
        }
        if (iana2JavaMapping.equals("ISO8859_1")) {
            return this.createLatin1Reader(byteStream);
        }
        return new InputStreamReader(byteStream, iana2JavaMapping);
    }
    
    private Reader createUTF8Reader(final InputStream inputStream) {
        return new UTF8Reader(inputStream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }
    
    private Reader createUTF16Reader(final InputStream inputStream, final boolean b) {
        return new UTF16Reader(inputStream, this.fTempString.ch.length << 1, b, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }
    
    private Reader createASCIIReader(final InputStream inputStream) {
        return new ASCIIReader(inputStream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }
    
    private Reader createLatin1Reader(final InputStream inputStream) {
        return new Latin1Reader(inputStream, this.fTempString.ch.length);
    }
    
    protected String getEncodingName(final InputStream inputStream) throws IOException {
        final byte[] array = new byte[4];
        String encodingName = null;
        inputStream.mark(4);
        final int read = inputStream.read(array, 0, 4);
        inputStream.reset();
        if (read == 4) {
            encodingName = this.getEncodingName(array);
        }
        return encodingName;
    }
    
    protected String consumeBOM(final InputStream inputStream, final String s) throws IOException {
        final byte[] array = new byte[3];
        inputStream.mark(3);
        if (s.equals("UTF-8")) {
            if (inputStream.read(array, 0, 3) == 3) {
                final int n = array[0] & 0xFF;
                final int n2 = array[1] & 0xFF;
                final int n3 = array[2] & 0xFF;
                if (n != 239 || n2 != 187 || n3 != 191) {
                    inputStream.reset();
                }
            }
            else {
                inputStream.reset();
            }
        }
        else if (s.startsWith("UTF-16")) {
            if (inputStream.read(array, 0, 2) == 2) {
                final int n4 = array[0] & 0xFF;
                final int n5 = array[1] & 0xFF;
                if (n4 == 254 && n5 == 255) {
                    return "UTF-16BE";
                }
                if (n4 == 255 && n5 == 254) {
                    return "UTF-16LE";
                }
            }
            inputStream.reset();
        }
        return s;
    }
    
    protected String getEncodingName(final byte[] array) {
        final int n = array[0] & 0xFF;
        final int n2 = array[1] & 0xFF;
        if (n == 254 && n2 == 255) {
            return "UTF-16BE";
        }
        if (n == 255 && n2 == 254) {
            return "UTF-16LE";
        }
        final int n3 = array[2] & 0xFF;
        if (n == 239 && n2 == 187 && n3 == 191) {
            return "UTF-8";
        }
        final int n4 = array[3] & 0xFF;
        if (n == 0 && n2 == 0 && n3 == 0 && n4 == 60) {
            return "ISO-10646-UCS-4";
        }
        if (n == 60 && n2 == 0 && n3 == 0 && n4 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (n == 0 && n2 == 0 && n3 == 60 && n4 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (n == 0 && n2 == 60 && n3 == 0 && n4 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (n == 0 && n2 == 60 && n3 == 0 && n4 == 63) {
            return "UTF-16BE";
        }
        if (n == 60 && n2 == 0 && n3 == 63 && n4 == 0) {
            return "UTF-16LE";
        }
        if (n == 76 && n2 == 111 && n3 == 167 && n4 == 148) {
            return "CP037";
        }
        return null;
    }
    
    public void parse() throws IOException {
        this.fReader = this.getReader(this.fSource);
        this.fSource = null;
        int i = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1);
        this.fHandler.fHasIncludeReportedContent = true;
        while (i != -1) {
            for (int j = 0; j < i; ++j) {
                final char c = this.fTempString.ch[j];
                if (!this.isValid(c)) {
                    if (XMLChar.isHighSurrogate(c)) {
                        int read;
                        if (++j < i) {
                            read = this.fTempString.ch[j];
                        }
                        else {
                            read = this.fReader.read();
                            if (read != -1) {
                                this.fTempString.ch[i++] = (char)read;
                            }
                        }
                        if (XMLChar.isLowSurrogate(read)) {
                            final int supplemental = XMLChar.supplemental(c, (char)read);
                            if (!this.isValid(supplemental)) {
                                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[] { Integer.toString(supplemental, 16) }, (short)2);
                            }
                        }
                        else {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[] { Integer.toString(read, 16) }, (short)2);
                        }
                    }
                    else {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[] { Integer.toString(c, 16) }, (short)2);
                    }
                }
            }
            if (this.fHandler != null && i > 0) {
                this.fTempString.offset = 0;
                this.fTempString.length = i;
                this.fHandler.characters(this.fTempString, this.fHandler.modifyAugmentations(null, true));
            }
            i = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1);
        }
    }
    
    public void setInputSource(final XMLInputSource fSource) {
        this.fSource = fSource;
    }
    
    public void close() throws IOException {
        if (this.fReader != null) {
            this.fReader.close();
            this.fReader = null;
        }
    }
    
    protected boolean isValid(final int n) {
        return XMLChar.isValid(n);
    }
    
    protected void setBufferSize(int n) {
        if (this.fTempString.ch.length != ++n) {
            this.fTempString.ch = new char[n];
        }
    }
}
