package org.w3c.dom.html;

public interface HTMLInputElement extends HTMLElement
{
    String getDefaultValue();
    
    void setDefaultValue(final String p0);
    
    boolean getDefaultChecked();
    
    void setDefaultChecked(final boolean p0);
    
    HTMLFormElement getForm();
    
    String getAccept();
    
    void setAccept(final String p0);
    
    String getAccessKey();
    
    void setAccessKey(final String p0);
    
    String getAlign();
    
    void setAlign(final String p0);
    
    String getAlt();
    
    void setAlt(final String p0);
    
    boolean getChecked();
    
    void setChecked(final boolean p0);
    
    boolean getDisabled();
    
    void setDisabled(final boolean p0);
    
    int getMaxLength();
    
    void setMaxLength(final int p0);
    
    String getName();
    
    void setName(final String p0);
    
    boolean getReadOnly();
    
    void setReadOnly(final boolean p0);
    
    String getSize();
    
    void setSize(final String p0);
    
    String getSrc();
    
    void setSrc(final String p0);
    
    int getTabIndex();
    
    void setTabIndex(final int p0);
    
    String getType();
    
    String getUseMap();
    
    void setUseMap(final String p0);
    
    String getValue();
    
    void setValue(final String p0);
    
    void blur();
    
    void focus();
    
    void select();
    
    void click();
}
