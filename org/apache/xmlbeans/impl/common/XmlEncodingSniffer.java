package org.apache.xmlbeans.impl.common;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;

public class XmlEncodingSniffer
{
    private String _xmlencoding;
    private String _javaencoding;
    private InputStream _stream;
    private Reader _reader;
    
    public XmlEncodingSniffer(final InputStream stream, final String encodingOverride) throws IOException, UnsupportedEncodingException {
        this._stream = stream;
        if (encodingOverride != null) {
            this._xmlencoding = EncodingMap.getJava2IANAMapping(encodingOverride);
        }
        if (this._xmlencoding == null) {
            this._xmlencoding = encodingOverride;
        }
        if (this._xmlencoding == null) {
            final SniffedXmlInputStream sniffed = new SniffedXmlInputStream(this._stream);
            this._xmlencoding = sniffed.getXmlEncoding();
            assert this._xmlencoding != null;
            this._stream = sniffed;
        }
        this._javaencoding = EncodingMap.getIANA2JavaMapping(this._xmlencoding);
        if (this._javaencoding == null) {
            this._javaencoding = this._xmlencoding;
        }
    }
    
    public XmlEncodingSniffer(final Reader reader, String encodingDefault) throws IOException, UnsupportedEncodingException {
        if (encodingDefault == null) {
            encodingDefault = "UTF-8";
        }
        final SniffedXmlReader sniffedReader = new SniffedXmlReader(reader);
        this._reader = sniffedReader;
        this._xmlencoding = sniffedReader.getXmlEncoding();
        if (this._xmlencoding == null) {
            this._xmlencoding = EncodingMap.getJava2IANAMapping(encodingDefault);
            if (this._xmlencoding != null) {
                this._javaencoding = encodingDefault;
            }
            else {
                this._xmlencoding = encodingDefault;
            }
        }
        if (this._xmlencoding == null) {
            this._xmlencoding = "UTF-8";
        }
        this._javaencoding = EncodingMap.getIANA2JavaMapping(this._xmlencoding);
        if (this._javaencoding == null) {
            this._javaencoding = this._xmlencoding;
        }
    }
    
    public String getXmlEncoding() {
        return this._xmlencoding;
    }
    
    public String getJavaEncoding() {
        return this._javaencoding;
    }
    
    public InputStream getStream() throws UnsupportedEncodingException {
        if (this._stream != null) {
            final InputStream is = this._stream;
            this._stream = null;
            return is;
        }
        if (this._reader != null) {
            final InputStream is = new ReaderInputStream(this._reader, this._javaencoding);
            this._reader = null;
            return is;
        }
        return null;
    }
    
    public Reader getReader() throws UnsupportedEncodingException {
        if (this._reader != null) {
            final Reader reader = this._reader;
            this._reader = null;
            return reader;
        }
        if (this._stream != null) {
            final Reader reader = new InputStreamReader(this._stream, this._javaencoding);
            this._stream = null;
            return reader;
        }
        return null;
    }
}
