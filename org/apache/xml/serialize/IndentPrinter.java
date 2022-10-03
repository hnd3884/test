package org.apache.xml.serialize;

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
    
    public IndentPrinter(final Writer writer, final OutputFormat outputFormat) {
        super(writer, outputFormat);
        this._line = new StringBuffer(80);
        this._text = new StringBuffer(20);
        this._spaces = 0;
        final int n = 0;
        this._nextIndent = n;
        this._thisIndent = n;
    }
    
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
    
    public void printText(final String s) {
        this._text.append(s);
    }
    
    public void printText(final StringBuffer sb) {
        this._text.append(sb.toString());
    }
    
    public void printText(final char c) {
        this._text.append(c);
    }
    
    public void printText(final char[] array, final int n, final int n2) {
        this._text.append(array, n, n2);
    }
    
    public void printSpace() {
        if (this._text.length() > 0) {
            if (this._format.getLineWidth() > 0 && this._thisIndent + this._line.length() + this._spaces + this._text.length() > this._format.getLineWidth()) {
                this.flushLine(false);
                try {
                    this._writer.write(this._format.getLineSeparator());
                }
                catch (final IOException exception) {
                    if (this._exception == null) {
                        this._exception = exception;
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
    
    public void breakLine() {
        this.breakLine(false);
    }
    
    public void breakLine(final boolean b) {
        if (this._text.length() > 0) {
            while (this._spaces > 0) {
                this._line.append(' ');
                --this._spaces;
            }
            this._line.append(this._text);
            this._text = new StringBuffer(20);
        }
        this.flushLine(b);
        try {
            this._writer.write(this._format.getLineSeparator());
        }
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
        }
    }
    
    public void flushLine(final boolean b) {
        if (this._line.length() > 0) {
            try {
                if (this._format.getIndenting() && !b) {
                    int i = this._thisIndent;
                    if (2 * i > this._format.getLineWidth() && this._format.getLineWidth() > 0) {
                        i = this._format.getLineWidth() / 2;
                    }
                    while (i > 0) {
                        this._writer.write(32);
                        --i;
                    }
                }
                this._thisIndent = this._nextIndent;
                this._spaces = 0;
                this._writer.write(this._line.toString());
                this._line = new StringBuffer(40);
            }
            catch (final IOException exception) {
                if (this._exception == null) {
                    this._exception = exception;
                }
            }
        }
    }
    
    public void flush() {
        if (this._line.length() > 0 || this._text.length() > 0) {
            this.breakLine();
        }
        try {
            this._writer.flush();
        }
        catch (final IOException exception) {
            if (this._exception == null) {
                this._exception = exception;
            }
        }
    }
    
    public void indent() {
        this._nextIndent += this._format.getIndent();
    }
    
    public void unindent() {
        this._nextIndent -= this._format.getIndent();
        if (this._nextIndent < 0) {
            this._nextIndent = 0;
        }
        if (this._line.length() + this._spaces + this._text.length() == 0) {
            this._thisIndent = this._nextIndent;
        }
    }
    
    public int getNextIndent() {
        return this._nextIndent;
    }
    
    public void setNextIndent(final int nextIndent) {
        this._nextIndent = nextIndent;
    }
    
    public void setThisIndent(final int thisIndent) {
        this._thisIndent = thisIndent;
    }
}
