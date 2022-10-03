package javax.swing.text.html;

import javax.swing.text.Element;
import java.net.URL;
import javax.swing.event.HyperlinkEvent;

public class FormSubmitEvent extends HTMLFrameHyperlinkEvent
{
    private MethodType method;
    private String data;
    
    FormSubmitEvent(final Object o, final EventType eventType, final URL url, final Element element, final String s, final MethodType method, final String data) {
        super(o, eventType, url, element, s);
        this.method = method;
        this.data = data;
    }
    
    public MethodType getMethod() {
        return this.method;
    }
    
    public String getData() {
        return this.data;
    }
    
    public enum MethodType
    {
        GET, 
        POST;
    }
}
