package org.eclipse.jdt.internal.compiler.parser;

public class NLSTag
{
    public int start;
    public int end;
    public int lineNumber;
    public int index;
    
    public NLSTag(final int start, final int end, final int lineNumber, final int index) {
        this.start = start;
        this.end = end;
        this.lineNumber = lineNumber;
        this.index = index;
    }
    
    @Override
    public String toString() {
        return "NLSTag(" + this.start + "," + this.end + "," + this.lineNumber + ")";
    }
}
