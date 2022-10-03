package com.lowagie.text;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import com.lowagie.text.pdf.OutputStreamCounter;

public abstract class DocWriter implements DocListener
{
    public static final byte NEWLINE = 10;
    public static final byte TAB = 9;
    public static final byte LT = 60;
    public static final byte SPACE = 32;
    public static final byte EQUALS = 61;
    public static final byte QUOTE = 34;
    public static final byte GT = 62;
    public static final byte FORWARD = 47;
    protected Rectangle pageSize;
    protected Document document;
    protected OutputStreamCounter os;
    protected boolean open;
    protected boolean pause;
    protected boolean closeStream;
    
    protected DocWriter() {
        this.open = false;
        this.pause = false;
        this.closeStream = true;
    }
    
    protected DocWriter(final Document document, final OutputStream os) {
        this.open = false;
        this.pause = false;
        this.closeStream = true;
        this.document = document;
        this.os = new OutputStreamCounter(new BufferedOutputStream(os));
    }
    
    @Override
    public boolean add(final Element element) throws DocumentException {
        return false;
    }
    
    @Override
    public void open() {
        this.open = true;
    }
    
    @Override
    public boolean setPageSize(final Rectangle pageSize) {
        this.pageSize = pageSize;
        return true;
    }
    
    @Override
    public boolean setMargins(final float marginLeft, final float marginRight, final float marginTop, final float marginBottom) {
        return false;
    }
    
    @Override
    public boolean newPage() {
        return this.open;
    }
    
    @Override
    public void setHeader(final HeaderFooter header) {
    }
    
    @Override
    public void resetHeader() {
    }
    
    @Override
    public void setFooter(final HeaderFooter footer) {
    }
    
    @Override
    public void resetFooter() {
    }
    
    @Override
    public void resetPageCount() {
    }
    
    @Override
    public void setPageCount(final int pageN) {
    }
    
    @Override
    public void close() {
        this.open = false;
        try {
            this.os.flush();
            if (this.closeStream) {
                this.os.close();
            }
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }
    
    public static final byte[] getISOBytes(final String text) {
        if (text == null) {
            return null;
        }
        final int len = text.length();
        final byte[] b = new byte[len];
        for (int k = 0; k < len; ++k) {
            b[k] = (byte)text.charAt(k);
        }
        return b;
    }
    
    public void pause() {
        this.pause = true;
    }
    
    public boolean isPaused() {
        return this.pause;
    }
    
    public void resume() {
        this.pause = false;
    }
    
    public void flush() {
        try {
            this.os.flush();
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }
    
    protected void write(final String string) throws IOException {
        this.os.write(getISOBytes(string));
    }
    
    protected void addTabs(final int indent) throws IOException {
        this.os.write(10);
        for (int i = 0; i < indent; ++i) {
            this.os.write(9);
        }
    }
    
    protected void write(final String key, final String value) throws IOException {
        this.os.write(32);
        this.write(key);
        this.os.write(61);
        this.os.write(34);
        this.write(value);
        this.os.write(34);
    }
    
    protected void writeStart(final String tag) throws IOException {
        this.os.write(60);
        this.write(tag);
    }
    
    protected void writeEnd(final String tag) throws IOException {
        this.os.write(60);
        this.os.write(47);
        this.write(tag);
        this.os.write(62);
    }
    
    protected void writeEnd() throws IOException {
        this.os.write(32);
        this.os.write(47);
        this.os.write(62);
    }
    
    protected boolean writeMarkupAttributes(final Properties markup) throws IOException {
        if (markup == null) {
            return false;
        }
        final Iterator attributeIterator = ((Hashtable<Object, V>)markup).keySet().iterator();
        while (attributeIterator.hasNext()) {
            final String name = String.valueOf(attributeIterator.next());
            this.write(name, markup.getProperty(name));
        }
        markup.clear();
        return true;
    }
    
    public boolean isCloseStream() {
        return this.closeStream;
    }
    
    public void setCloseStream(final boolean closeStream) {
        this.closeStream = closeStream;
    }
    
    @Override
    public boolean setMarginMirroring(final boolean MarginMirroring) {
        return false;
    }
    
    @Override
    public boolean setMarginMirroringTopBottom(final boolean MarginMirroring) {
        return false;
    }
}
