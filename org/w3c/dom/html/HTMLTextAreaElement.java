package org.w3c.dom.html;

public interface HTMLTextAreaElement extends HTMLElement
{
    String getDefaultValue();
    
    void setDefaultValue(final String p0);
    
    HTMLFormElement getForm();
    
    String getAccessKey();
    
    void setAccessKey(final String p0);
    
    int getCols();
    
    void setCols(final int p0);
    
    boolean getDisabled();
    
    void setDisabled(final boolean p0);
    
    String getName();
    
    void setName(final String p0);
    
    boolean getReadOnly();
    
    void setReadOnly(final boolean p0);
    
    int getRows();
    
    void setRows(final int p0);
    
    int getTabIndex();
    
    void setTabIndex(final int p0);
    
    String getType();
    
    String getValue();
    
    void setValue(final String p0);
    
    void blur();
    
    void focus();
    
    void select();
}
