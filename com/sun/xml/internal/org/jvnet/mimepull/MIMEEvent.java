package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

abstract class MIMEEvent
{
    static final StartMessage START_MESSAGE;
    static final StartPart START_PART;
    static final EndPart END_PART;
    static final EndMessage END_MESSAGE;
    
    abstract EVENT_TYPE getEventType();
    
    static {
        START_MESSAGE = new StartMessage();
        START_PART = new StartPart();
        END_PART = new EndPart();
        END_MESSAGE = new EndMessage();
    }
    
    enum EVENT_TYPE
    {
        START_MESSAGE, 
        START_PART, 
        HEADERS, 
        CONTENT, 
        END_PART, 
        END_MESSAGE;
    }
    
    static final class StartMessage extends MIMEEvent
    {
        @Override
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.START_MESSAGE;
        }
    }
    
    static final class StartPart extends MIMEEvent
    {
        @Override
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.START_PART;
        }
    }
    
    static final class EndPart extends MIMEEvent
    {
        @Override
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.END_PART;
        }
    }
    
    static final class Headers extends MIMEEvent
    {
        InternetHeaders ih;
        
        Headers(final InternetHeaders ih) {
            this.ih = ih;
        }
        
        @Override
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.HEADERS;
        }
        
        InternetHeaders getHeaders() {
            return this.ih;
        }
    }
    
    static final class Content extends MIMEEvent
    {
        private final ByteBuffer buf;
        
        Content(final ByteBuffer buf) {
            this.buf = buf;
        }
        
        @Override
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.CONTENT;
        }
        
        ByteBuffer getData() {
            return this.buf;
        }
    }
    
    static final class EndMessage extends MIMEEvent
    {
        @Override
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.END_MESSAGE;
        }
    }
}
