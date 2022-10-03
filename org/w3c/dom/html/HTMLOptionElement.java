package org.w3c.dom.html;

public interface HTMLOptionElement extends HTMLElement
{
    HTMLFormElement getForm();
    
    boolean getDefaultSelected();
    
    void setDefaultSelected(final boolean p0);
    
    String getText();
    
    int getIndex();
    
    void setIndex(final int p0);
    
    boolean getDisabled();
    
    void setDisabled(final boolean p0);
    
    String getLabel();
    
    void setLabel(final String p0);
    
    boolean getSelected();
    
    String getValue();
    
    void setValue(final String p0);
}
