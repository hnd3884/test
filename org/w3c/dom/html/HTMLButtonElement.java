package org.w3c.dom.html;

public interface HTMLButtonElement extends HTMLElement
{
    HTMLFormElement getForm();
    
    String getAccessKey();
    
    void setAccessKey(final String p0);
    
    boolean getDisabled();
    
    void setDisabled(final boolean p0);
    
    String getName();
    
    void setName(final String p0);
    
    int getTabIndex();
    
    void setTabIndex(final int p0);
    
    String getType();
    
    String getValue();
    
    void setValue(final String p0);
}
