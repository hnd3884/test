package com.sun.xml.internal.txw2.output;

import java.io.PrintStream;

public class DumpSerializer implements XmlSerializer
{
    private final PrintStream out;
    
    public DumpSerializer(final PrintStream out) {
        this.out = out;
    }
    
    @Override
    public void beginStartTag(final String uri, final String localName, final String prefix) {
        this.out.println('<' + prefix + ':' + localName);
    }
    
    @Override
    public void writeAttribute(final String uri, final String localName, final String prefix, final StringBuilder value) {
        this.out.println('@' + prefix + ':' + localName + '=' + (Object)value);
    }
    
    @Override
    public void writeXmlns(final String prefix, final String uri) {
        this.out.println("xmlns:" + prefix + '=' + uri);
    }
    
    @Override
    public void endStartTag(final String uri, final String localName, final String prefix) {
        this.out.println('>');
    }
    
    @Override
    public void endTag() {
        this.out.println("</  >");
    }
    
    @Override
    public void text(final StringBuilder text) {
        this.out.println(text);
    }
    
    @Override
    public void cdata(final StringBuilder text) {
        this.out.println("<![CDATA[");
        this.out.println(text);
        this.out.println("]]>");
    }
    
    @Override
    public void comment(final StringBuilder comment) {
        this.out.println("<!--");
        this.out.println(comment);
        this.out.println("-->");
    }
    
    @Override
    public void startDocument() {
        this.out.println("<?xml?>");
    }
    
    @Override
    public void endDocument() {
        this.out.println("done");
    }
    
    @Override
    public void flush() {
        this.out.println("flush");
    }
}
