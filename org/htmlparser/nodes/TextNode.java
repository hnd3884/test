package org.htmlparser.nodes;

import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.util.ParserException;
import org.htmlparser.lexer.Cursor;
import org.htmlparser.lexer.Page;
import org.htmlparser.Text;

public class TextNode extends AbstractNode implements Text
{
    protected String mText;
    
    public TextNode(final String text) {
        super(null, 0, 0);
        this.setText(text);
    }
    
    public TextNode(final Page page, final int start, final int end) {
        super(page, start, end);
        this.mText = null;
    }
    
    public String getText() {
        return this.toHtml();
    }
    
    public void setText(final String text) {
        this.mText = text;
        super.nodeBegin = 0;
        super.nodeEnd = this.mText.length();
    }
    
    public String toPlainTextString() {
        return this.toHtml();
    }
    
    public String toHtml(final boolean verbatim) {
        String ret = this.mText;
        if (null == ret) {
            ret = super.mPage.getText(this.getStartPosition(), this.getEndPosition());
        }
        return ret;
    }
    
    public String toString() {
        final int startpos = this.getStartPosition();
        final int endpos = this.getEndPosition();
        final StringBuffer ret = new StringBuffer(endpos - startpos + 20);
        if (null == this.mText) {
            final Cursor start = new Cursor(this.getPage(), startpos);
            final Cursor end = new Cursor(this.getPage(), endpos);
            ret.append("Txt (");
            ret.append(start);
            ret.append(",");
            ret.append(end);
            ret.append("): ");
            while (start.getPosition() < endpos) {
                try {
                    final char c = super.mPage.getCharacter(start);
                    switch (c) {
                        case '\t': {
                            ret.append("\\t");
                            break;
                        }
                        case '\n': {
                            ret.append("\\n");
                            break;
                        }
                        case '\r': {
                            ret.append("\\r");
                            break;
                        }
                        default: {
                            ret.append(c);
                            break;
                        }
                    }
                }
                catch (final ParserException ex) {}
                if (77 <= ret.length()) {
                    ret.append("...");
                    break;
                }
            }
        }
        else {
            ret.append("Txt (");
            ret.append(startpos);
            ret.append(",");
            ret.append(endpos);
            ret.append("): ");
            for (int i = 0; i < this.mText.length(); ++i) {
                final char c = this.mText.charAt(i);
                switch (c) {
                    case '\t': {
                        ret.append("\\t");
                        break;
                    }
                    case '\n': {
                        ret.append("\\n");
                        break;
                    }
                    case '\r': {
                        ret.append("\\r");
                        break;
                    }
                    default: {
                        ret.append(c);
                        break;
                    }
                }
                if (77 <= ret.length()) {
                    ret.append("...");
                    break;
                }
            }
        }
        return ret.toString();
    }
    
    public boolean isWhiteSpace() {
        return this.mText == null || this.mText.trim().equals("");
    }
    
    public void accept(final NodeVisitor visitor) {
        visitor.visitStringNode(this);
    }
}
