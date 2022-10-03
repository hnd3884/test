package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;

public class XWPFNum
{
    protected XWPFNumbering numbering;
    private CTNum ctNum;
    
    public XWPFNum() {
        this.ctNum = null;
        this.numbering = null;
    }
    
    public XWPFNum(final CTNum ctNum) {
        this.ctNum = ctNum;
        this.numbering = null;
    }
    
    public XWPFNum(final XWPFNumbering numbering) {
        this.ctNum = null;
        this.numbering = numbering;
    }
    
    public XWPFNum(final CTNum ctNum, final XWPFNumbering numbering) {
        this.ctNum = ctNum;
        this.numbering = numbering;
    }
    
    public XWPFNumbering getNumbering() {
        return this.numbering;
    }
    
    public void setNumbering(final XWPFNumbering numbering) {
        this.numbering = numbering;
    }
    
    public CTNum getCTNum() {
        return this.ctNum;
    }
    
    public void setCTNum(final CTNum ctNum) {
        this.ctNum = ctNum;
    }
}
