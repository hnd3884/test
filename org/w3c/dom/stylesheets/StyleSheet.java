package org.w3c.dom.stylesheets;

import org.w3c.dom.Node;

public interface StyleSheet
{
    String getType();
    
    boolean getDisabled();
    
    void setDisabled(final boolean p0);
    
    Node getOwnerNode();
    
    StyleSheet getParentStyleSheet();
    
    String getHref();
    
    String getTitle();
    
    MediaList getMedia();
}
