package org.w3c.dom.html;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public interface HTMLDocument extends Document
{
    String getTitle();
    
    void setTitle(final String p0);
    
    String getReferrer();
    
    String getDomain();
    
    String getURL();
    
    HTMLElement getBody();
    
    void setBody(final HTMLElement p0);
    
    HTMLCollection getImages();
    
    HTMLCollection getApplets();
    
    HTMLCollection getLinks();
    
    HTMLCollection getForms();
    
    HTMLCollection getAnchors();
    
    String getCookie();
    
    void setCookie(final String p0);
    
    void open();
    
    void close();
    
    void write(final String p0);
    
    void writeln(final String p0);
    
    Element getElementById(final String p0);
    
    NodeList getElementsByName(final String p0);
}
