package com.sun.org.apache.xml.internal.serialize;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class Printer
{
    protected final OutputFormat _format;
    protected Writer _writer;
    protected StringWriter _dtdWriter;
    protected Writer _docWriter;
    protected IOException _exception;
    private static final int BufferSize = 4096;
    private final char[] _buffer;
    private int _pos;
    
    public Printer(final Writer writer, final OutputFormat format) {
        this._buffer = new char[4096];
        this._pos = 0;
        this._writer = writer;
        this._format = format;
        this._exception = null;
        this._dtdWriter = null;
        this._docWriter = null;
        this._pos = 0;
    }
    
    public IOException getException() {
        return this._exception;
    }
    
    public void enterDTD() throws IOException {
        if (this._dtdWriter == null) {
            this.flushLine(false);
            this._dtdWriter = new StringWriter();
            this._docWriter = this._writer;
            this._writer = this._dtdWriter;
        }
    }
    
    public String leaveDTD() throws IOException {
        if (this._writer == this._dtdWriter) {
            this.flushLine(false);
            this._writer = this._docWriter;
            return this._dtdWriter.toString();
        }
        return null;
    }
    
    public void printText(final String text) throws IOException {
        try {
            for (int length = text.length(), i = 0; i < length; ++i) {
                if (this._pos == 4096) {
                    this._writer.write(this._buffer);
                    this._pos = 0;
                }
                this._buffer[this._pos] = text.charAt(i);
                ++this._pos;
            }
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
            throw except;
        }
    }
    
    public void printText(final StringBuffer text) throws IOException {
        try {
            for (int length = text.length(), i = 0; i < length; ++i) {
                if (this._pos == 4096) {
                    this._writer.write(this._buffer);
                    this._pos = 0;
                }
                this._buffer[this._pos] = text.charAt(i);
                ++this._pos;
            }
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
            throw except;
        }
    }
    
    public void printText(final char[] chars, int start, int length) throws IOException {
        try {
            while (length-- > 0) {
                if (this._pos == 4096) {
                    this._writer.write(this._buffer);
                    this._pos = 0;
                }
                this._buffer[this._pos] = chars[start];
                ++start;
                ++this._pos;
            }
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
            throw except;
        }
    }
    
    public void printText(final char ch) throws IOException {
        try {
            if (this._pos == 4096) {
                this._writer.write(this._buffer);
                this._pos = 0;
            }
            this._buffer[this._pos] = ch;
            ++this._pos;
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
            throw except;
        }
    }
    
    public void printSpace() throws IOException {
        try {
            if (this._pos == 4096) {
                this._writer.write(this._buffer);
                this._pos = 0;
            }
            this._buffer[this._pos] = ' ';
            ++this._pos;
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
            throw except;
        }
    }
    
    public void breakLine() throws IOException {
        try {
            if (this._pos == 4096) {
                this._writer.write(this._buffer);
                this._pos = 0;
            }
            this._buffer[this._pos] = '\n';
            ++this._pos;
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
            throw except;
        }
    }
    
    public void breakLine(final boolean preserveSpace) throws IOException {
        this.breakLine();
    }
    
    public void flushLine(final boolean preserveSpace) throws IOException {
        try {
            this._writer.write(this._buffer, 0, this._pos);
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
        }
        this._pos = 0;
    }
    
    public void flush() throws IOException {
        try {
            this._writer.write(this._buffer, 0, this._pos);
            this._writer.flush();
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
            throw except;
        }
        this._pos = 0;
    }
    
    public void indent() {
    }
    
    public void unindent() {
    }
    
    public int getNextIndent() {
        return 0;
    }
    
    public void setNextIndent(final int indent) {
    }
    
    public void setThisIndent(final int indent) {
    }
}
