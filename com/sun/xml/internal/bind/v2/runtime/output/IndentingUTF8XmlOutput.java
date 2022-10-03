package com.sun.xml.internal.bind.v2.runtime.output;

import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import java.io.IOException;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import java.io.OutputStream;

public final class IndentingUTF8XmlOutput extends UTF8XmlOutput
{
    private final Encoded indent8;
    private final int unitLen;
    private int depth;
    private boolean seenText;
    
    public IndentingUTF8XmlOutput(final OutputStream out, final String indentStr, final Encoded[] localNames, final CharacterEscapeHandler escapeHandler) {
        super(out, localNames, escapeHandler);
        this.depth = 0;
        this.seenText = false;
        if (indentStr != null) {
            final Encoded e = new Encoded(indentStr);
            (this.indent8 = new Encoded()).ensureSize(e.len * 8);
            this.unitLen = e.len;
            for (int i = 0; i < 8; ++i) {
                System.arraycopy(e.buf, 0, this.indent8.buf, this.unitLen * i, this.unitLen);
            }
        }
        else {
            this.indent8 = null;
            this.unitLen = 0;
        }
    }
    
    @Override
    public void beginStartTag(final int prefix, final String localName) throws IOException {
        this.indentStartTag();
        super.beginStartTag(prefix, localName);
    }
    
    @Override
    public void beginStartTag(final Name name) throws IOException {
        this.indentStartTag();
        super.beginStartTag(name);
    }
    
    private void indentStartTag() throws IOException {
        this.closeStartTag();
        if (!this.seenText) {
            this.printIndent();
        }
        ++this.depth;
        this.seenText = false;
    }
    
    @Override
    public void endTag(final Name name) throws IOException {
        this.indentEndTag();
        super.endTag(name);
    }
    
    @Override
    public void endTag(final int prefix, final String localName) throws IOException {
        this.indentEndTag();
        super.endTag(prefix, localName);
    }
    
    private void indentEndTag() throws IOException {
        --this.depth;
        if (!this.closeStartTagPending && !this.seenText) {
            this.printIndent();
        }
        this.seenText = false;
    }
    
    private void printIndent() throws IOException {
        this.write(10);
        int i = this.depth % 8;
        this.write(this.indent8.buf, 0, i * this.unitLen);
        for (i >>= 3; i > 0; --i) {
            this.indent8.write(this);
        }
    }
    
    @Override
    public void text(final String value, final boolean needSP) throws IOException {
        this.seenText = true;
        super.text(value, needSP);
    }
    
    @Override
    public void text(final Pcdata value, final boolean needSP) throws IOException {
        this.seenText = true;
        super.text(value, needSP);
    }
    
    @Override
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        this.write(10);
        super.endDocument(fragment);
    }
}
