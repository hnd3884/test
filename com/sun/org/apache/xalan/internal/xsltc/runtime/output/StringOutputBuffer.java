package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

class StringOutputBuffer implements OutputBuffer
{
    private StringBuffer _buffer;
    
    public StringOutputBuffer() {
        this._buffer = new StringBuffer();
    }
    
    @Override
    public String close() {
        return this._buffer.toString();
    }
    
    @Override
    public OutputBuffer append(final String s) {
        this._buffer.append(s);
        return this;
    }
    
    @Override
    public OutputBuffer append(final char[] s, final int from, final int to) {
        this._buffer.append(s, from, to);
        return this;
    }
    
    @Override
    public OutputBuffer append(final char ch) {
        this._buffer.append(ch);
        return this;
    }
}
