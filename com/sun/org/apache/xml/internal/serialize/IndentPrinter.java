package com.sun.org.apache.xml.internal.serialize;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class IndentPrinter extends Printer
{
    private StringBuffer _line;
    private StringBuffer _text;
    private int _spaces;
    private int _thisIndent;
    private int _nextIndent;
    
    public IndentPrinter(final Writer writer, final OutputFormat format) {
        super(writer, format);
        this._line = new StringBuffer(80);
        this._text = new StringBuffer(20);
        this._spaces = 0;
        final int n = 0;
        this._nextIndent = n;
        this._thisIndent = n;
    }
    
    @Override
    public void enterDTD() {
        if (this._dtdWriter == null) {
            this._line.append(this._text);
            this._text = new StringBuffer(20);
            this.flushLine(false);
            this._dtdWriter = new StringWriter();
            this._docWriter = this._writer;
            this._writer = this._dtdWriter;
        }
    }
    
    @Override
    public String leaveDTD() {
        if (this._writer == this._dtdWriter) {
            this._line.append(this._text);
            this._text = new StringBuffer(20);
            this.flushLine(false);
            this._writer = this._docWriter;
            return this._dtdWriter.toString();
        }
        return null;
    }
    
    @Override
    public void printText(final String text) {
        this._text.append(text);
    }
    
    @Override
    public void printText(final StringBuffer text) {
        this._text.append(text.toString());
    }
    
    @Override
    public void printText(final char ch) {
        this._text.append(ch);
    }
    
    @Override
    public void printText(final char[] chars, final int start, final int length) {
        this._text.append(chars, start, length);
    }
    
    @Override
    public void printSpace() {
        if (this._text.length() > 0) {
            if (this._format.getLineWidth() > 0 && this._thisIndent + this._line.length() + this._spaces + this._text.length() > this._format.getLineWidth()) {
                this.flushLine(false);
                try {
                    this._writer.write(this._format.getLineSeparator());
                }
                catch (final IOException except) {
                    if (this._exception == null) {
                        this._exception = except;
                    }
                }
            }
            while (this._spaces > 0) {
                this._line.append(' ');
                --this._spaces;
            }
            this._line.append(this._text);
            this._text = new StringBuffer(20);
        }
        ++this._spaces;
    }
    
    @Override
    public void breakLine() {
        this.breakLine(false);
    }
    
    @Override
    public void breakLine(final boolean preserveSpace) {
        if (this._text.length() > 0) {
            while (this._spaces > 0) {
                this._line.append(' ');
                --this._spaces;
            }
            this._line.append(this._text);
            this._text = new StringBuffer(20);
        }
        this.flushLine(preserveSpace);
        try {
            this._writer.write(this._format.getLineSeparator());
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
        }
    }
    
    @Override
    public void flushLine(final boolean preserveSpace) {
        if (this._line.length() > 0) {
            try {
                if (this._format.getIndenting() && !preserveSpace) {
                    int indent = this._thisIndent;
                    if (2 * indent > this._format.getLineWidth() && this._format.getLineWidth() > 0) {
                        indent = this._format.getLineWidth() / 2;
                    }
                    while (indent > 0) {
                        this._writer.write(32);
                        --indent;
                    }
                }
                this._thisIndent = this._nextIndent;
                this._spaces = 0;
                this._writer.write(this._line.toString());
                this._line = new StringBuffer(40);
            }
            catch (final IOException except) {
                if (this._exception == null) {
                    this._exception = except;
                }
            }
        }
    }
    
    @Override
    public void flush() {
        if (this._line.length() > 0 || this._text.length() > 0) {
            this.breakLine();
        }
        try {
            this._writer.flush();
        }
        catch (final IOException except) {
            if (this._exception == null) {
                this._exception = except;
            }
        }
    }
    
    @Override
    public void indent() {
        this._nextIndent += this._format.getIndent();
    }
    
    @Override
    public void unindent() {
        this._nextIndent -= this._format.getIndent();
        if (this._nextIndent < 0) {
            this._nextIndent = 0;
        }
        if (this._line.length() + this._spaces + this._text.length() == 0) {
            this._thisIndent = this._nextIndent;
        }
    }
    
    @Override
    public int getNextIndent() {
        return this._nextIndent;
    }
    
    @Override
    public void setNextIndent(final int indent) {
        this._nextIndent = indent;
    }
    
    @Override
    public void setThisIndent(final int indent) {
        this._thisIndent = indent;
    }
}
