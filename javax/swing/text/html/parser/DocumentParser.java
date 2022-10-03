package javax.swing.text.html.parser;

import javax.swing.text.ChangedCharSetException;
import javax.swing.text.html.HTML;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import java.io.IOException;
import java.io.Reader;
import javax.swing.text.html.HTMLEditorKit;

public class DocumentParser extends Parser
{
    private int inbody;
    private int intitle;
    private int inhead;
    private int instyle;
    private int inscript;
    private boolean seentitle;
    private HTMLEditorKit.ParserCallback callback;
    private boolean ignoreCharSet;
    private static final boolean debugFlag = false;
    
    public DocumentParser(final DTD dtd) {
        super(dtd);
        this.callback = null;
        this.ignoreCharSet = false;
    }
    
    public void parse(final Reader reader, final HTMLEditorKit.ParserCallback callback, final boolean ignoreCharSet) throws IOException {
        this.ignoreCharSet = ignoreCharSet;
        this.callback = callback;
        this.parse(reader);
        callback.handleEndOfLineString(this.getEndOfLineString());
    }
    
    @Override
    protected void handleStartTag(final TagElement tagElement) {
        final Element element = tagElement.getElement();
        if (element == this.dtd.body) {
            ++this.inbody;
        }
        else if (element != this.dtd.html) {
            if (element == this.dtd.head) {
                ++this.inhead;
            }
            else if (element == this.dtd.title) {
                ++this.intitle;
            }
            else if (element == this.dtd.style) {
                ++this.instyle;
            }
            else if (element == this.dtd.script) {
                ++this.inscript;
            }
        }
        if (tagElement.fictional()) {
            final SimpleAttributeSet set = new SimpleAttributeSet();
            set.addAttribute(HTMLEditorKit.ParserCallback.IMPLIED, Boolean.TRUE);
            this.callback.handleStartTag(tagElement.getHTMLTag(), set, this.getBlockStartPosition());
        }
        else {
            this.callback.handleStartTag(tagElement.getHTMLTag(), this.getAttributes(), this.getBlockStartPosition());
            this.flushAttributes();
        }
    }
    
    @Override
    protected void handleComment(final char[] array) {
        this.callback.handleComment(array, this.getBlockStartPosition());
    }
    
    @Override
    protected void handleEmptyTag(final TagElement tagElement) throws ChangedCharSetException {
        final Element element = tagElement.getElement();
        if (element == this.dtd.meta && !this.ignoreCharSet) {
            final SimpleAttributeSet attributes = this.getAttributes();
            if (attributes != null) {
                final String s = (String)attributes.getAttribute(HTML.Attribute.CONTENT);
                if (s != null) {
                    if ("content-type".equalsIgnoreCase((String)attributes.getAttribute(HTML.Attribute.HTTPEQUIV))) {
                        if (!s.equalsIgnoreCase("text/html") && !s.equalsIgnoreCase("text/plain")) {
                            throw new ChangedCharSetException(s, false);
                        }
                    }
                    else if ("charset".equalsIgnoreCase((String)attributes.getAttribute(HTML.Attribute.HTTPEQUIV))) {
                        throw new ChangedCharSetException(s, true);
                    }
                }
            }
        }
        if (this.inbody != 0 || element == this.dtd.meta || element == this.dtd.base || element == this.dtd.isindex || element == this.dtd.style || element == this.dtd.link) {
            if (tagElement.fictional()) {
                final SimpleAttributeSet set = new SimpleAttributeSet();
                set.addAttribute(HTMLEditorKit.ParserCallback.IMPLIED, Boolean.TRUE);
                this.callback.handleSimpleTag(tagElement.getHTMLTag(), set, this.getBlockStartPosition());
            }
            else {
                this.callback.handleSimpleTag(tagElement.getHTMLTag(), this.getAttributes(), this.getBlockStartPosition());
                this.flushAttributes();
            }
        }
    }
    
    @Override
    protected void handleEndTag(final TagElement tagElement) {
        final Element element = tagElement.getElement();
        if (element == this.dtd.body) {
            --this.inbody;
        }
        else if (element == this.dtd.title) {
            --this.intitle;
            this.seentitle = true;
        }
        else if (element == this.dtd.head) {
            --this.inhead;
        }
        else if (element == this.dtd.style) {
            --this.instyle;
        }
        else if (element == this.dtd.script) {
            --this.inscript;
        }
        this.callback.handleEndTag(tagElement.getHTMLTag(), this.getBlockStartPosition());
    }
    
    @Override
    protected void handleText(final char[] array) {
        if (array != null) {
            if (this.inscript != 0) {
                this.callback.handleComment(array, this.getBlockStartPosition());
                return;
            }
            if (this.inbody != 0 || this.instyle != 0 || (this.intitle != 0 && !this.seentitle)) {
                this.callback.handleText(array, this.getBlockStartPosition());
            }
        }
    }
    
    @Override
    protected void handleError(final int n, final String s) {
        this.callback.handleError(s, this.getCurrentPos());
    }
    
    private void debug(final String s) {
        System.out.println(s);
    }
}
