package org.apache.commons.lang;

import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;

class StringPrintWriter extends PrintWriter
{
    public StringPrintWriter() {
        super(new StringWriter());
    }
    
    public StringPrintWriter(final int initialSize) {
        super(new StringWriter(initialSize));
    }
    
    public String getString() {
        this.flush();
        return ((StringWriter)super.out).toString();
    }
}
