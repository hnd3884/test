package org.w3c.dom.html;

public interface HTMLSelectElement extends HTMLElement
{
    String getType();
    
    int getSelectedIndex();
    
    void setSelectedIndex(final int p0);
    
    String getValue();
    
    void setValue(final String p0);
    
    int getLength();
    
    HTMLFormElement getForm();
    
    HTMLCollection getOptions();
    
    boolean getDisabled();
    
    void setDisabled(final boolean p0);
    
    boolean getMultiple();
    
    void setMultiple(final boolean p0);
    
    String getName();
    
    void setName(final String p0);
    
    int getSize();
    
    void setSize(final int p0);
    
    int getTabIndex();
    
    void setTabIndex(final int p0);
    
    void add(final HTMLElement p0, final HTMLElement p1);
    
    void remove(final int p0);
    
    void blur();
    
    void focus();
}
