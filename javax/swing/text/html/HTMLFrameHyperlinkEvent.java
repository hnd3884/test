package javax.swing.text.html;

import java.awt.event.InputEvent;
import javax.swing.text.Element;
import java.net.URL;
import javax.swing.event.HyperlinkEvent;

public class HTMLFrameHyperlinkEvent extends HyperlinkEvent
{
    private String targetFrame;
    
    public HTMLFrameHyperlinkEvent(final Object o, final EventType eventType, final URL url, final String targetFrame) {
        super(o, eventType, url);
        this.targetFrame = targetFrame;
    }
    
    public HTMLFrameHyperlinkEvent(final Object o, final EventType eventType, final URL url, final String s, final String targetFrame) {
        super(o, eventType, url, s);
        this.targetFrame = targetFrame;
    }
    
    public HTMLFrameHyperlinkEvent(final Object o, final EventType eventType, final URL url, final Element element, final String targetFrame) {
        super(o, eventType, url, null, element);
        this.targetFrame = targetFrame;
    }
    
    public HTMLFrameHyperlinkEvent(final Object o, final EventType eventType, final URL url, final String s, final Element element, final String targetFrame) {
        super(o, eventType, url, s, element);
        this.targetFrame = targetFrame;
    }
    
    public HTMLFrameHyperlinkEvent(final Object o, final EventType eventType, final URL url, final String s, final Element element, final InputEvent inputEvent, final String targetFrame) {
        super(o, eventType, url, s, element, inputEvent);
        this.targetFrame = targetFrame;
    }
    
    public String getTarget() {
        return this.targetFrame;
    }
}
