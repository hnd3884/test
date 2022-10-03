package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Comment;

public class CommentEvent extends DummyEvent implements Comment
{
    private String fText;
    
    public CommentEvent() {
        this.init();
    }
    
    public CommentEvent(final String text) {
        this.init();
        this.fText = text;
    }
    
    protected void init() {
        this.setEventType(5);
    }
    
    @Override
    public String toString() {
        return "<!--" + this.getText() + "-->";
    }
    
    @Override
    public String getText() {
        return this.fText;
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write("<!--" + this.getText() + "-->");
    }
}
