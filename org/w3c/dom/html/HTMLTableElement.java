package org.w3c.dom.html;

public interface HTMLTableElement extends HTMLElement
{
    HTMLTableCaptionElement getCaption();
    
    void setCaption(final HTMLTableCaptionElement p0);
    
    HTMLTableSectionElement getTHead();
    
    void setTHead(final HTMLTableSectionElement p0);
    
    HTMLTableSectionElement getTFoot();
    
    void setTFoot(final HTMLTableSectionElement p0);
    
    HTMLCollection getRows();
    
    HTMLCollection getTBodies();
    
    String getAlign();
    
    void setAlign(final String p0);
    
    String getBgColor();
    
    void setBgColor(final String p0);
    
    String getBorder();
    
    void setBorder(final String p0);
    
    String getCellPadding();
    
    void setCellPadding(final String p0);
    
    String getCellSpacing();
    
    void setCellSpacing(final String p0);
    
    String getFrame();
    
    void setFrame(final String p0);
    
    String getRules();
    
    void setRules(final String p0);
    
    String getSummary();
    
    void setSummary(final String p0);
    
    String getWidth();
    
    void setWidth(final String p0);
    
    HTMLElement createTHead();
    
    void deleteTHead();
    
    HTMLElement createTFoot();
    
    void deleteTFoot();
    
    HTMLElement createCaption();
    
    void deleteCaption();
    
    HTMLElement insertRow(final int p0);
    
    void deleteRow(final int p0);
}
