package org.htmlparser.util;

import org.htmlparser.scanners.Scanner;
import org.htmlparser.Tag;
import org.htmlparser.Node;
import org.htmlparser.lexer.Cursor;
import org.htmlparser.lexer.Lexer;

public class IteratorImpl implements NodeIterator
{
    Lexer mLexer;
    ParserFeedback mFeedback;
    Cursor mCursor;
    
    public IteratorImpl(final Lexer lexer, final ParserFeedback fb) {
        this.mLexer = lexer;
        this.mFeedback = fb;
        this.mCursor = new Cursor(this.mLexer.getPage(), 0);
    }
    
    public boolean hasMoreNodes() throws ParserException {
        this.mCursor.setPosition(this.mLexer.getPosition());
        final boolean ret = '\uffff' != this.mLexer.getPage().getCharacter(this.mCursor);
        return ret;
    }
    
    public Node nextNode() throws ParserException {
        Node ret;
        try {
            ret = this.mLexer.nextNode();
            if (null != ret && ret instanceof Tag) {
                final Tag tag = (Tag)ret;
                if (!tag.isEndTag()) {
                    final Scanner scanner = tag.getThisScanner();
                    if (null != scanner) {
                        final NodeList stack = new NodeList();
                        ret = scanner.scan(tag, this.mLexer, stack);
                    }
                }
            }
        }
        catch (final ParserException pe) {
            throw pe;
        }
        catch (final Exception e) {
            final StringBuffer msgBuffer = new StringBuffer();
            msgBuffer.append("Unexpected Exception occurred while reading ");
            msgBuffer.append(this.mLexer.getPage().getUrl());
            msgBuffer.append(", in nextNode");
            final ParserException ex = new ParserException(msgBuffer.toString(), e);
            this.mFeedback.error(msgBuffer.toString(), ex);
            throw ex;
        }
        return ret;
    }
}
