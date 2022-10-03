package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.Comment;

public class CommentEvent extends EventBase implements Comment
{
    private String _text;
    
    public CommentEvent() {
        super(5);
    }
    
    public CommentEvent(final String text) {
        this();
        this._text = text;
    }
    
    @Override
    public String toString() {
        return "<!--" + this._text + "-->";
    }
    
    @Override
    public String getText() {
        return this._text;
    }
    
    public void setText(final String text) {
        this._text = text;
    }
}
