package org.htmlparser.nodes;

import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.util.ParserException;
import org.htmlparser.lexer.Cursor;
import org.htmlparser.lexer.Page;
import org.htmlparser.Remark;

public class RemarkNode extends AbstractNode implements Remark
{
    protected String mText;
    
    public RemarkNode(final String text) {
        super(null, 0, 0);
        this.setText(text);
    }
    
    public RemarkNode(final Page page, final int start, final int end) {
        super(page, start, end);
        this.mText = null;
    }
    
    public String getText() {
        String ret;
        if (null == this.mText) {
            final int start = this.getStartPosition() + 4;
            final int end = this.getEndPosition() - 3;
            if (start >= end) {
                ret = "";
            }
            else {
                ret = super.mPage.getText(start, end);
            }
        }
        else {
            ret = this.mText;
        }
        return ret;
    }
    
    public void setText(final String text) {
        this.mText = text;
        if (text.startsWith("<!--") && text.endsWith("-->")) {
            this.mText = text.substring(4, text.length() - 3);
        }
        super.nodeBegin = 0;
        super.nodeEnd = this.mText.length();
    }
    
    public String toPlainTextString() {
        return "";
    }
    
    public String toHtml(final boolean verbatim) {
        String ret;
        if (null == this.mText) {
            ret = super.mPage.getText(this.getStartPosition(), this.getEndPosition());
        }
        else {
            final StringBuffer buffer = new StringBuffer(this.mText.length() + 7);
            buffer.append("<!--");
            buffer.append(this.mText);
            buffer.append("-->");
            ret = buffer.toString();
        }
        return ret;
    }
    
    public String toString() {
        final int startpos = this.getStartPosition();
        int endpos = this.getEndPosition();
        final StringBuffer ret = new StringBuffer(endpos - startpos + 20);
        if (null == this.mText) {
            final Cursor start = new Cursor(this.getPage(), startpos);
            final Cursor end = new Cursor(this.getPage(), endpos);
            ret.append("Rem (");
            ret.append(start);
            ret.append(",");
            ret.append(end);
            ret.append("): ");
            start.setPosition(startpos + 4);
            endpos -= 3;
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
            ret.append("Rem (");
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
    
    public void accept(final NodeVisitor visitor) {
        visitor.visitRemarkNode(this);
    }
}
