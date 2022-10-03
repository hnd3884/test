package javax.swing.event;

import java.awt.event.InputEvent;
import javax.swing.text.Element;
import java.net.URL;
import java.util.EventObject;

public class HyperlinkEvent extends EventObject
{
    private EventType type;
    private URL u;
    private String desc;
    private Element sourceElement;
    private InputEvent inputEvent;
    
    public HyperlinkEvent(final Object o, final EventType eventType, final URL url) {
        this(o, eventType, url, null);
    }
    
    public HyperlinkEvent(final Object o, final EventType eventType, final URL url, final String s) {
        this(o, eventType, url, s, null);
    }
    
    public HyperlinkEvent(final Object o, final EventType type, final URL u, final String desc, final Element sourceElement) {
        super(o);
        this.type = type;
        this.u = u;
        this.desc = desc;
        this.sourceElement = sourceElement;
    }
    
    public HyperlinkEvent(final Object o, final EventType type, final URL u, final String desc, final Element sourceElement, final InputEvent inputEvent) {
        super(o);
        this.type = type;
        this.u = u;
        this.desc = desc;
        this.sourceElement = sourceElement;
        this.inputEvent = inputEvent;
    }
    
    public EventType getEventType() {
        return this.type;
    }
    
    public String getDescription() {
        return this.desc;
    }
    
    public URL getURL() {
        return this.u;
    }
    
    public Element getSourceElement() {
        return this.sourceElement;
    }
    
    public InputEvent getInputEvent() {
        return this.inputEvent;
    }
    
    public static final class EventType
    {
        public static final EventType ENTERED;
        public static final EventType EXITED;
        public static final EventType ACTIVATED;
        private String typeString;
        
        private EventType(final String typeString) {
            this.typeString = typeString;
        }
        
        @Override
        public String toString() {
            return this.typeString;
        }
        
        static {
            ENTERED = new EventType("ENTERED");
            EXITED = new EventType("EXITED");
            ACTIVATED = new EventType("ACTIVATED");
        }
    }
}
