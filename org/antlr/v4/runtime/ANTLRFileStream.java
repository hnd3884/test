package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Utils;
import java.io.IOException;

public class ANTLRFileStream extends ANTLRInputStream
{
    protected String fileName;
    
    public ANTLRFileStream(final String fileName) throws IOException {
        this(fileName, null);
    }
    
    public ANTLRFileStream(final String fileName, final String encoding) throws IOException {
        this.load(this.fileName = fileName, encoding);
    }
    
    public void load(final String fileName, final String encoding) throws IOException {
        this.data = Utils.readFile(fileName, encoding);
        this.n = this.data.length;
    }
    
    @Override
    public String getSourceName() {
        return this.fileName;
    }
}
