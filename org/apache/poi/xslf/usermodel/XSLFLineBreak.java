package org.apache.poi.xslf.usermodel;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;

class XSLFLineBreak extends XSLFTextRun
{
    protected XSLFLineBreak(final CTTextLineBreak r, final XSLFTextParagraph p) {
        super((XmlObject)r, p);
    }
    
    @Override
    public void setText(final String text) {
        throw new IllegalStateException("You cannot change text of a line break, it is always '\\n'");
    }
}
