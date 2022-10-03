package com.steadystate.css.util;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.Writer;

public final class Output
{
    private static final String NEW_LINE;
    private Writer writer_;
    private StringBuffer currentIndent_;
    private boolean afterNewLine_;
    private final String indent_;
    
    public Output(final Writer aWriter, final String anIndent) {
        this.writer_ = new BufferedWriter(aWriter);
        this.indent_ = anIndent;
        this.currentIndent_ = new StringBuffer();
    }
    
    public Output print(final char aChar) throws IOException {
        this.writeIndentIfNeeded();
        this.writer_.write(aChar);
        return this;
    }
    
    public Output print(final String aString) throws IOException {
        if (null != aString) {
            this.writeIndentIfNeeded();
            this.writer_.write(aString);
        }
        return this;
    }
    
    public Output println(final String aString) throws IOException {
        this.writeIndentIfNeeded();
        this.writer_.write(aString);
        this.writer_.write(Output.NEW_LINE);
        this.afterNewLine_ = true;
        return this;
    }
    
    public Output println() throws IOException {
        this.writer_.write(Output.NEW_LINE);
        this.afterNewLine_ = true;
        return this;
    }
    
    public Output flush() throws IOException {
        this.writer_.flush();
        return this;
    }
    
    public Output indent() {
        this.currentIndent_.append(this.indent_);
        return this;
    }
    
    public Output unindent() {
        this.currentIndent_.setLength(Math.max(0, this.currentIndent_.length() - this.indent_.length()));
        return this;
    }
    
    private void writeIndentIfNeeded() throws IOException {
        if (this.afterNewLine_) {
            this.writer_.write(this.currentIndent_.toString());
            this.afterNewLine_ = false;
        }
    }
    
    static {
        NEW_LINE = System.getProperty("line.separator");
    }
}
