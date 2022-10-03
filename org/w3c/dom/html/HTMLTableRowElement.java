package org.w3c.dom.html;

public interface HTMLTableRowElement extends HTMLElement
{
    int getRowIndex();
    
    void setRowIndex(final int p0);
    
    int getSectionRowIndex();
    
    void setSectionRowIndex(final int p0);
    
    HTMLCollection getCells();
    
    void setCells(final HTMLCollection p0);
    
    String getAlign();
    
    void setAlign(final String p0);
    
    String getBgColor();
    
    void setBgColor(final String p0);
    
    String getCh();
    
    void setCh(final String p0);
    
    String getChOff();
    
    void setChOff(final String p0);
    
    String getVAlign();
    
    void setVAlign(final String p0);
    
    HTMLElement insertCell(final int p0);
    
    void deleteCell(final int p0);
}
