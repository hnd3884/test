package org.apache.xml.serialize;

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
    
    public void printText(final String s) throws IOException {
        try {
            for (int length = s.length(), i = 0; i < length; ++i) {
                if (this._pos == 4096) {
                    this._writer.write(this._buffer);
                    this._pos = 0;
                }
                this._buffer[this._pos] = s.charAt(i);
                ++this._pos;
            }
        }
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
            throw exception;
        }
    }
    
    public void printText(final StringBuffer sb) throws IOException {
        try {
            for (int length = sb.length(), i = 0; i < length; ++i) {
                if (this._pos == 4096) {
                    this._writer.write(this._buffer);
                    this._pos = 0;
                }
                this._buffer[this._pos] = sb.charAt(i);
                ++this._pos;
            }
        }
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
            throw exception;
        }
    }
    
    public void printText(final char[] array, int n, int n2) throws IOException {
        try {
            while (n2-- > 0) {
                if (this._pos == 4096) {
                    this._writer.write(this._buffer);
                    this._pos = 0;
                }
                this._buffer[this._pos] = array[n];
                ++n;
                ++this._pos;
            }
        }
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
            throw exception;
        }
    }
    
    public void printText(final char c) throws IOException {
        try {
            if (this._pos == 4096) {
                this._writer.write(this._buffer);
                this._pos = 0;
            }
            this._buffer[this._pos] = c;
            ++this._pos;
        }
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
            throw exception;
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
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
            throw exception;
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
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
            throw exception;
        }
    }
    
    public void breakLine(final boolean b) throws IOException {
        this.breakLine();
    }
    
    public void flushLine(final boolean b) throws IOException {
        try {
            this._writer.write(this._buffer, 0, this._pos);
        }
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
        }
        this._pos = 0;
    }
    
    public void flush() throws IOException {
        try {
            this._writer.write(this._buffer, 0, this._pos);
            this._writer.flush();
        }
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
            throw exception;
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
    
    public void setNextIndent(final int n) {
    }
    
    public void setThisIndent(final int n) {
    }
}
