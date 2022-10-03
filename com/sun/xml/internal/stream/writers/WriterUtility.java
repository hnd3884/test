package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.io.OutputStreamWriter;
import java.nio.charset.CharsetEncoder;
import java.io.Writer;

public class WriterUtility
{
    public static final String START_COMMENT = "<!--";
    public static final String END_COMMENT = "-->";
    public static final String DEFAULT_ENCODING = " encoding=\"utf-8\"";
    public static final String DEFAULT_XMLDECL = "<?xml version=\"1.0\" ?>";
    public static final String DEFAULT_XML_VERSION = "1.0";
    public static final char CLOSE_START_TAG = '>';
    public static final char OPEN_START_TAG = '<';
    public static final String OPEN_END_TAG = "</";
    public static final char CLOSE_END_TAG = '>';
    public static final String START_CDATA = "<![CDATA[";
    public static final String END_CDATA = "]]>";
    public static final String CLOSE_EMPTY_ELEMENT = "/>";
    public static final String SPACE = " ";
    public static final String UTF_8 = "utf-8";
    static final boolean DEBUG_XML_CONTENT = false;
    boolean fEscapeCharacters;
    Writer fWriter;
    CharsetEncoder fEncoder;
    
    public WriterUtility() {
        this.fEscapeCharacters = true;
        this.fWriter = null;
        this.fEncoder = this.getDefaultEncoder();
    }
    
    public WriterUtility(final Writer writer) {
        this.fEscapeCharacters = true;
        this.fWriter = null;
        this.fWriter = writer;
        if (writer instanceof OutputStreamWriter) {
            final String charset = ((OutputStreamWriter)writer).getEncoding();
            if (charset != null) {
                this.fEncoder = Charset.forName(charset).newEncoder();
            }
        }
        else if (writer instanceof FileWriter) {
            final String charset = ((FileWriter)writer).getEncoding();
            if (charset != null) {
                this.fEncoder = Charset.forName(charset).newEncoder();
            }
        }
        else {
            this.fEncoder = this.getDefaultEncoder();
        }
    }
    
    public void setWriter(final Writer writer) {
        this.fWriter = writer;
    }
    
    public void setEscapeCharacters(final boolean escape) {
        this.fEscapeCharacters = escape;
    }
    
    public boolean getEscapeCharacters() {
        return this.fEscapeCharacters;
    }
    
    public void writeXMLContent(final char[] content, final int start, final int length) throws IOException {
        this.writeXMLContent(content, start, length, this.getEscapeCharacters());
    }
    
    private void writeXMLContent(final char[] content, final int start, final int length, final boolean escapeCharacter) throws IOException {
        final int end = start + length;
        int startWritePos = start;
        for (int index = start; index < end; ++index) {
            final char ch = content[index];
            if (this.fEncoder != null && !this.fEncoder.canEncode(ch)) {
                this.fWriter.write(content, startWritePos, index - startWritePos);
                this.fWriter.write("&#x");
                this.fWriter.write(Integer.toHexString(ch));
                this.fWriter.write(59);
                startWritePos = index + 1;
            }
            switch (ch) {
                case '<': {
                    if (escapeCharacter) {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        this.fWriter.write("&lt;");
                        startWritePos = index + 1;
                        break;
                    }
                    break;
                }
                case '&': {
                    if (escapeCharacter) {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        this.fWriter.write("&amp;");
                        startWritePos = index + 1;
                        break;
                    }
                    break;
                }
                case '>': {
                    if (escapeCharacter) {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        this.fWriter.write("&gt;");
                        startWritePos = index + 1;
                        break;
                    }
                    break;
                }
            }
        }
        this.fWriter.write(content, startWritePos, end - startWritePos);
    }
    
    public void writeXMLContent(final String content) throws IOException {
        if (content == null || content.length() == 0) {
            return;
        }
        this.writeXMLContent(content.toCharArray(), 0, content.length());
    }
    
    public void writeXMLAttributeValue(final String value) throws IOException {
        this.writeXMLContent(value.toCharArray(), 0, value.length(), true);
    }
    
    private CharsetEncoder getDefaultEncoder() {
        try {
            final String encoding = SecuritySupport.getSystemProperty("file.encoding");
            if (encoding != null) {
                return Charset.forName(encoding).newEncoder();
            }
        }
        catch (final Exception ex) {}
        return null;
    }
}
