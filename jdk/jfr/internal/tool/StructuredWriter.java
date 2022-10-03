package jdk.jfr.internal.tool;

import java.io.PrintWriter;

abstract class StructuredWriter
{
    private static final String LINE_SEPARATOR;
    private final PrintWriter out;
    private final StringBuilder builder;
    private char[] indentionArray;
    private int indent;
    private int column;
    private boolean first;
    
    StructuredWriter(final PrintWriter out) {
        this.builder = new StringBuilder(4000);
        this.indentionArray = new char[0];
        this.indent = 0;
        this.first = true;
        this.out = out;
    }
    
    protected final int getColumn() {
        return this.column;
    }
    
    public final void flush(final boolean b) {
        if (b) {
            this.out.print(this.builder.toString());
            this.builder.setLength(0);
            return;
        }
        if (this.first || this.builder.length() > 100000) {
            this.out.print(this.builder.toString());
            this.builder.setLength(0);
            this.first = false;
        }
    }
    
    public final void printIndent() {
        this.builder.append(this.indentionArray, 0, this.indent);
        this.column += this.indent;
    }
    
    public final void println() {
        this.builder.append(StructuredWriter.LINE_SEPARATOR);
        this.column = 0;
    }
    
    public final void print(final String... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            this.print(array[i]);
        }
    }
    
    public final void printAsString(final Object o) {
        this.print(String.valueOf(o));
    }
    
    public final void print(final String s) {
        this.builder.append(s);
        this.column += s.length();
    }
    
    public final void print(final char c) {
        this.builder.append(c);
        ++this.column;
    }
    
    public final void print(final int n) {
        this.print(String.valueOf(n));
    }
    
    public final void indent() {
        this.indent += 2;
        this.updateIndent();
    }
    
    public final void retract() {
        this.indent -= 2;
        this.updateIndent();
    }
    
    public final void println(final String s) {
        this.print(s);
        this.println();
    }
    
    private void updateIndent() {
        if (this.indent > this.indentionArray.length) {
            this.indentionArray = new char[this.indent];
            for (int i = 0; i < this.indentionArray.length; ++i) {
                this.indentionArray[i] = ' ';
            }
        }
    }
    
    static {
        LINE_SEPARATOR = String.format("%n", new Object[0]);
    }
}
