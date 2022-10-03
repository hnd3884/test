package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;

public class XWPFAbstractNum
{
    protected XWPFNumbering numbering;
    private CTAbstractNum ctAbstractNum;
    
    protected XWPFAbstractNum() {
        this.ctAbstractNum = null;
        this.numbering = null;
    }
    
    public XWPFAbstractNum(final CTAbstractNum abstractNum) {
        this.ctAbstractNum = abstractNum;
    }
    
    public XWPFAbstractNum(final CTAbstractNum ctAbstractNum, final XWPFNumbering numbering) {
        this.ctAbstractNum = ctAbstractNum;
        this.numbering = numbering;
    }
    
    public CTAbstractNum getAbstractNum() {
        return this.ctAbstractNum;
    }
    
    public XWPFNumbering getNumbering() {
        return this.numbering;
    }
    
    public void setNumbering(final XWPFNumbering numbering) {
        this.numbering = numbering;
    }
    
    public CTAbstractNum getCTAbstractNum() {
        return this.ctAbstractNum;
    }
}
