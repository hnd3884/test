package org.w3c.dom.svg;

import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.Document;

public interface SVGDocument extends Document, DocumentEvent
{
    String getTitle();
    
    String getReferrer();
    
    String getDomain();
    
    String getURL();
    
    SVGSVGElement getRootElement();
}
